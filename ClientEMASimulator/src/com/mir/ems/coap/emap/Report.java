package com.mir.ems.coap.emap;

import java.util.Calendar;
import java.util.Date;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mir.smartgrid.simulator.devProfile.DeviceProfile;
import com.mir.smartgrid.simulator.devProfile.Emap_Cema_Profile;
import com.mir.smartgrid.simulator.global.Global;

public class Report extends CoapResource {

	enum Type {
		REGISTERREPORT, UPDATEREPORT
	}

	public Report(String name) {
		super(name);
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.FORBIDDEN, "Wrong Access");
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		exchange.respond(ResponseCode.FORBIDDEN, "Wrong Access");
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		exchange.respond(ResponseCode.FORBIDDEN, "Wrong Access");
	}

	@Override
	public void handlePUT(CoapExchange exchange) {

		if (getPath().contains(Global.version)) {
			try {
				String version = "EMAP" + getPath().split("/")[3];

				JSONObject json = new JSONObject(exchange.getRequestText().toString());
				String service = json.getString("service");

				new ReportType(getName(), service, exchange, version).start();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (getPath().contains(Global.openADRVersion)) {

			try {

				String version = "OpenADR" + getPath().split("/")[3];

				JSONObject json = new JSONObject(exchange.getRequestText().toString());
				String service = json.getString("service");
				service = service.replaceAll("oadr", "");

				new ReportType(getName(), service, exchange, version).start();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else {
			new ReportType(getName(), exchange).start();

		}

	}

	class ReportType extends Thread {

		CoapExchange exchange;
		String incomingType, requestText, setPayload;

		Emap_Cema_Profile emaProfile;
		private JSONObject jsonObj;
		// private JSONObject sub1JsonObj;
		// private JSONObject sub2JsonObj;

		private String version, service;

		ReportType(String incomingType, String service, CoapExchange exchange, String version) {
			this.exchange = exchange;
			this.incomingType = incomingType;
			this.requestText = exchange.getRequestText();
			this.version = version;
			this.service = service;
		}

		ReportType(String incomingType, CoapExchange exchange) {
			this.exchange = exchange;
			this.incomingType = incomingType;
			this.requestText = exchange.getRequestText();
		}

		@Override
		public void run() {

			Type type;

			if (version.equals("EMAP1.0b")) {
				type = Type.valueOf(service.toUpperCase());

			}

			else if (version.equals("OpenADR2.0b")) {
				type = Type.valueOf(service.toUpperCase());

			}

			else {
				type = Type.valueOf(incomingType.toUpperCase());

			}

			switch (type) {

			case UPDATEREPORT:
				this.setPayload = acknowledgeUPDATEREPORT(requestText);
				break;
			default:
				this.setPayload = "TYPE WRONG";
				break;
			}

			if (this.setPayload.equals("TYPE WRONG"))
				this.exchange.respond(ResponseCode.FORBIDDEN, "Wrong Access");
			else if (this.setPayload.toUpperCase().equals("NORESPONSE")) {

			} else
				this.exchange.respond(ResponseCode.CONTENT, this.setPayload, MediaTypeRegistry.APPLICATION_JSON);

		}

		public String acknowledgeUPDATEREPORT(String requestText) {
			JSONObject drmsg = new JSONObject();
			// String srcEMA = null;

			if (version.equals("EMAP1.0b")) {
				try {

					// System.out.println(requestText);
					jsonObj = new JSONObject(requestText);
					emaProfile = new Emap_Cema_Profile();

					emaProfile.setEmaID(jsonObj.getString("SrcEMA")).setRequestID(jsonObj.getString("requestID"));

					JSONArray reportArr = new JSONArray(jsonObj.getString("report"));

					for (int i = 0; i < reportArr.length(); i++) {

						JSONArray decrArr = new JSONArray(
								new JSONObject(reportArr.get(i).toString()).getString("reportDescription"));

						if (jsonObj.getString("type").equals("Implicit") || 1 == decrArr.length()) {

							JSONObject decr = new JSONObject(decrArr.get(0).toString());

							String qos, registrationID, state, minTime, maxTime;
							double margin, minValue, maxValue, avgValue, power, generate, storage;
							int priority, dimming;
							boolean pullModel;

							qos = decr.getString("qos");
							registrationID = Global.emaProtocolCoAP.get(jsonObj.get("SrcEMA")).getRegistrationID();
							// margin = decr.getDouble("margin");
							minValue = decr.getDouble("minValue");
							maxValue = decr.getDouble("maxValue");
							avgValue = decr.getDouble("avgValue");
							minTime = decr.getString("minTime");
							maxTime = decr.getString("maxTime");
							power = decr.getDouble("power");
							generate = decr.getDouble("generate");
							storage = decr.getDouble("storage");
							state = decr.getString("state");
							priority = decr.getInt("priority");
							dimming = decr.getInt("dimming");
							pullModel = Global.emaProtocolCoAP.get(jsonObj.get("SrcEMA")).isPullModel();

							boolean realTimetable = Global.emaProtocolCoAP.get(jsonObj.get("SrcEMA"))
									.isRealTimetableChanged();
							boolean timeTable = Global.emaProtocolCoAP.get(jsonObj.get("SrcEMA")).isTableChanged();

							// Threshold 湲곗� 80%�씠�긽�쓣 �궗�슜�븷 寃쎌슦 �씠踰ㅽ듃 �떆洹몃꼸

							margin = Global.emaProtocolCoAP.get(jsonObj.get("SrcEMA")).getMargin();

							Emap_Cema_Profile profile = new Emap_Cema_Profile("COAP", jsonObj.getString("SrcEMA"),
									registrationID, qos, state, power, dimming, margin, generate, storage, maxValue,
									minValue, avgValue, maxTime, minTime, priority, pullModel, timeTable, realTimetable,
									"CONNECT");

							Global.emaProtocolCoAP.replace(jsonObj.getString("SrcEMA"), profile);

						}

						else if (jsonObj.getString("type").equals("Explicit") || 1 < decrArr.length()) {

							for (int j = 0; j < decrArr.length(); j++) {

								JSONObject decr = new JSONObject(decrArr.get(j).toString());

								String qos, registrationID, state, minTime, maxTime, rID;
								double margin, minValue, maxValue, avgValue, power, generate, storage;
								int priority, dimming;
								boolean pullModel;

								if (decr.getString("deviceType").contains("EMA")) {
									qos = decr.getString("qos");
									registrationID = Global.emaProtocolCoAP.get(jsonObj.get("SrcEMA"))
											.getRegistrationID();
									// margin = decr.getDouble("margin");
									minValue = decr.getDouble("minValue");
									maxValue = decr.getDouble("maxValue");
									avgValue = decr.getDouble("avgValue");
									minTime = decr.getString("minTime");
									maxTime = decr.getString("maxTime");
									power = decr.getDouble("power");
									generate = decr.getDouble("generate");
									storage = decr.getDouble("storage");
									state = decr.getString("state");
									priority = decr.getInt("priority");
									dimming = decr.getInt("dimming");
									pullModel = Global.emaProtocolCoAP.get(jsonObj.get("SrcEMA")).isPullModel();

									boolean realTimetable = Global.emaProtocolCoAP.get(jsonObj.get("SrcEMA"))
											.isRealTimetableChanged();
									boolean timeTable = Global.emaProtocolCoAP.get(jsonObj.get("SrcEMA"))
											.isTableChanged();

									// Threshold 湲곗� 80%�씠�긽�쓣 �궗�슜�븷 寃쎌슦 �씠踰ㅽ듃
									// �떆洹몃꼸

									margin = Global.emaProtocolCoAP.get(jsonObj.get("SrcEMA")).getMargin();
									//
									// if (global.EXPERIMENTAUTODR) {
									// if (power >= margin *
									// global.EXPERIMENTPERCENT) {
									//
									// setEvent(jsonObj.getString("SrcEMA"),
									// margin, power);
									//
									// }
									// }

									Emap_Cema_Profile profile = new Emap_Cema_Profile("COAP",
											jsonObj.getString("SrcEMA"), registrationID, qos, state, power, dimming,
											margin, generate, storage, maxValue, minValue, avgValue, maxTime, minTime,
											priority, pullModel, timeTable, realTimetable, "CONNECT");

									Global.emaProtocolCoAP.replace(jsonObj.getString("SrcEMA"), profile);

								}

								else {

									rID = decr.getString("rID");
									qos = decr.getString("qos");
									margin = decr.getDouble("margin");
									minValue = decr.getDouble("minValue");
									maxValue = decr.getDouble("maxValue");
									avgValue = decr.getDouble("avgValue");
									minTime = decr.getString("minTime");
									maxTime = decr.getString("maxTime");
									power = decr.getDouble("power");
									generate = decr.getDouble("generate");
									storage = decr.getDouble("storage");
									state = decr.getString("state");
									priority = decr.getInt("priority");
									dimming = decr.getInt("dimming");

//									deviceProfile = new Emap_Device_Profile(jsonObj.getString("SrcEMA"), rID,
//											decr.getString("deviceType"), qos, state, power, dimming, margin, generate,
//											storage, maxValue, minValue, avgValue, maxTime, minTime, priority);

									DeviceProfile devProfile = new DeviceProfile(jsonObj.getString("SrcEMA"), rID,
											"LED", state, dimming, 1, priority, power, margin, 0, 0, generate, 0,
											new Date(System.currentTimeMillis()));
									Global.devProfile.replace(rID, devProfile);

								}

								JSONArray powerAtts = new JSONArray(decr.getString("powerAttributes"));

								for (int k = 0; k < powerAtts.length(); k++) {
									// JSONObject powerAtt = new
									// JSONObject(powerAtts.get(k).toString());
								}

							}

						}

					}

					com.mir.smartgrid.simulator.profile.emap.v2.UpdatedReport udt = new com.mir.smartgrid.simulator.profile.emap.v2.UpdatedReport();
					udt.setDestEMA(emaProfile.getEmaID());
					udt.setRequestID(emaProfile.getRequestID());
					udt.setResponseCode(200);
					udt.setResponseDescription("OK");
					udt.setService("UpdatedReport");
					udt.setSrcEMA(Global.SYSTEMID);
					udt.setTime(new Date(System.currentTimeMillis()).toString());
					udt.setType(Global.reportType);

					String payload = udt.toString();

					this.exchange.respond(ResponseCode.CONTENT, payload, MediaTypeRegistry.APPLICATION_JSON);

					return "NORESPONSE";

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

			return drmsg.toString();
		}

	}

	public void setEvent(String srcEMA, double margin, double power) {
		Calendar now = Calendar.getInstance();

		int sYear = now.get(Calendar.YEAR);
		int sMonth = now.get(Calendar.MONTH) + 1;
		int sDate = now.get(Calendar.DATE);
		String strYMD = sYear + "" + sMonth + "" + sDate;
		int sHour = now.get(Calendar.HOUR_OF_DAY);
		int sMin = now.get(Calendar.MINUTE);

		String sTime = sHour + "" + sMin + "" + "11";
		String eTime = (sHour + 1) + "" + sMin + "" + "11";

		double threshold = 100;

		// PULL MODEL
		if (Global.emaProtocolCoAP.get(srcEMA).isPullModel()) {
			Global.emaProtocolCoAP_EventFlag.get(srcEMA).setEventFlag(true).setStartYMD(Integer.parseInt(strYMD))
					.setStartTime(Integer.parseInt(sTime)).setEndYMD(Integer.parseInt(strYMD))
					.setEndTime(Integer.parseInt(eTime)).setThreshold(threshold);
		}

	}

}
