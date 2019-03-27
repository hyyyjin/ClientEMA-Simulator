package com.mir.ems.coap.emap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONException;
import org.json.JSONObject;

import com.mir.smartgrid.simulator.devProfile.Emap_Cema_Profile;
import com.mir.smartgrid.simulator.global.Global;

public class DemandResponseEvent extends CoapResource {

	enum Type {
		POLL, CREATEDEVENT, REQUESTEVENT
	}

	public DemandResponseEvent(String name) {
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

				new EventType(getName(), service, exchange, version).start();

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
				service = service.replaceAll("Oadr", "");

				new EventType(getName(), service, exchange, version).start();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		else {
			new EventType(getName(), exchange).start();

		}

	}

	class EventType extends Thread {

		Emap_Cema_Profile emaProfile;
		// Emap_Device_Profile deviceProfile;
		private JSONObject jsonObj;
		CoapExchange exchange;
		String incomingType, requestText, setPayload;
		private String version, service;

		EventType(String incomingType, String service, CoapExchange exchange, String version) {
			this.exchange = exchange;
			this.incomingType = incomingType;
			this.requestText = exchange.getRequestText();
			this.version = version;
			this.service = service;
		}

		EventType(String incomingType, CoapExchange exchange) {
			this.exchange = exchange;
			this.incomingType = incomingType;
			this.requestText = exchange.getRequestText();
		}

		@Override
		public void run() {

			// Type type = Type.valueOf(incomingType.toUpperCase());

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

			case REQUESTEVENT:
				this.setPayload = acknowledgeREQUESTEVENT(requestText);
				break;

			case POLL:
				this.setPayload = acknowledgePOLL(this.requestText);
				break;
			case CREATEDEVENT:
				this.setPayload = acknowledgeCREATEDEVENT(this.requestText);
				break;
			default:
				this.setPayload = "TYPE WRONG";
			}

			if (this.setPayload.equals("TYPE WRONG"))
				this.exchange.respond(ResponseCode.FORBIDDEN, "Wrong Access");
			else if (this.setPayload.equals("NORESPONSE")) {

			} else
				this.exchange.respond(ResponseCode.CONTENT, this.setPayload, MediaTypeRegistry.APPLICATION_JSON);

		}

		public String acknowledgePOLL(String requestText) {
			JSONObject drmsg = new JSONObject();

			String srcEMA = null;

			if (version.equals("EMAP1.0b")) {

				try {
					jsonObj = new JSONObject(requestText.toUpperCase());

					emaProfile = new Emap_Cema_Profile();

					String emaID = jsonObj.getString("SRCEMA");

					emaProfile.setEmaID(emaID);

					boolean flag = false;
					if (Global.emaProtocolCoAP_EventFlag.containsKey(emaID)) {
						flag = Global.emaProtocolCoAP_EventFlag.get(emaID).isEventFlag();

					}

					if (!flag) {

						com.mir.smartgrid.simulator.profile.emap.v2.Response response = new com.mir.smartgrid.simulator.profile.emap.v2.Response();
						response.setDestEMA(emaProfile.getEmaID());
						response.setRequestID("");
						response.setResponseCode(200);
						response.setResponseDescription("OK");
						response.setService("Response");
						response.setSrcEMA(Global.SYSTEMID);
						response.setTime(new Date(System.currentTimeMillis()).toString());

						String payload = response.toString();

						this.exchange.respond(ResponseCode.CONTENT, payload, MediaTypeRegistry.APPLICATION_JSON);

						return "NORESPONSE";

					} else {

						double threshold = Global.emaProtocolCoAP_EventFlag.get(emaID).getThreshold();
						int strTime = Global.emaProtocolCoAP_EventFlag.get(emaID).getStartTime();
						int strYMD = Global.emaProtocolCoAP_EventFlag.get(emaID).getStartYMD();
						int endTime = Global.emaProtocolCoAP_EventFlag.get(emaID).getEndTime();
						int endYMD = Global.emaProtocolCoAP_EventFlag.get(emaID).getEndYMD();

						String strTime_str = strTime + "";
						String endTime_str = endTime + "";

						strTime_str = strTime_str.length() < 6 ? "0" + strTime_str : strTime_str;
						endTime_str = endTime_str.length() < 6 ? "0" + endTime_str : endTime_str;

						String eventDuration = ISO8601(strYMD, strTime, endYMD, endTime);
						String timeForm = (strYMD + "") + strTime_str;
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

						try {

							// Date createdDateTime = sdf.parse(timeForm);
							com.mir.smartgrid.simulator.profile.emap.v2.Intervals interval = new com.mir.smartgrid.simulator.profile.emap.v2.Intervals();
							interval.addIntervalsParams("PT2S", "uid", 0.0);

							com.mir.smartgrid.simulator.profile.emap.v2.EventSignals es = new com.mir.smartgrid.simulator.profile.emap.v2.EventSignals();
							es.addEventSignalsParams(interval.getIntervalsParams(), "Distribute", "Event", "signalID",
									0, threshold, 0, 0, "KW");

							String eventID = Global.emaProtocolCoAP_EventFlag.get(emaID).getEventID();

							com.mir.smartgrid.simulator.profile.emap.v2.Event event = new com.mir.smartgrid.simulator.profile.emap.v2.Event();
							event.addEventParams(eventID, es.getEventSignalsParams(), 1, "None", 2, "marketContext",
									new Date(System.currentTimeMillis()).toString(), "Event", false, "Event", "None",
									"None", "None", new Date(System.currentTimeMillis()).toString(), eventDuration,
									"None", "None", "None", "None");

							com.mir.smartgrid.simulator.profile.emap.v2.EventResponse er = new com.mir.smartgrid.simulator.profile.emap.v2.EventResponse();
							er.setRequestID(emaProfile.getRequestID());
							er.setResponseCode(200);
							er.setResponseDescription("OK");

							com.mir.smartgrid.simulator.profile.emap.v2.DistributeEvent drE = new com.mir.smartgrid.simulator.profile.emap.v2.DistributeEvent();

							drE.setDestEMA(emaProfile.getEmaID());
							drE.setEvent(event.getEventParams());
							drE.setRequestID(emaProfile.getRequestID());
							drE.setResponse(er.toString());
							drE.setService("DistributeEvent");
							drE.setSrcEMA(Global.SYSTEMID);
							drE.setTime(new Date(System.currentTimeMillis()).toString());
							drE.setResponseRequired("Always");

							String payload = drE.toString();

							this.exchange.respond(ResponseCode.CONTENT, payload, MediaTypeRegistry.APPLICATION_JSON);

							if (!Global.eventFromServer) {
								if (Global.autoDR) {
									if (Global.autoDRCNT < Global.autoDRTOTAL) {
										Global.autoDRCNT += 1;
										eventID = Global.eventID.get(Global.autoDRCNT);
										Global.emaProtocolCoAP_EventFlag.get(emaProfile.getEmaID()).setEventID(eventID);
										System.out.println(Global.autoDRCNT + "===>");
									} else {
										Global.emaProtocolCoAP_EventFlag.get(emaID).setEventFlag(false);
									}
								}

								else {
									Global.emaProtocolCoAP_EventFlag.get(emaID).setEventFlag(false);
								}
							} else {
								Global.emaProtocolCoAP_EventFlag.get(emaID).setEventFlag(false);
							}
							// global.emaProtocolCoAP_EventFlag.get(emaID).setEventFlag(false);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return "NORESPONSE";
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "NORESPONSE";

			}
			return "NORESPONSE";
			//
			// else if (version.equals("OpenADR2.0b")) {
			//
			// try {
			// jsonObj = new JSONObject(requestText.toUpperCase());
			//
			// emaProfile = new Emap_Cema_Profile();
			//
			// String emaID = jsonObj.getString("VENID");
			//
			// emaProfile.setEmaID(emaID);
			//
			// boolean flag = false;
			// if (Global.emaProtocolCoAP_EventFlag.containsKey(emaID)) {
			// flag = global.emaProtocolCoAP_EventFlag.get(emaID).isEventFlag();
			//
			// }
			//
			// if (global.venRegisterFlag.get(emaID).intValue() == 0) {
			//
			// global.venRegisterFlag.replace(emaID, 1);
			//
			// com.mir.ems.profile.openadr.recent.RegisterReport rt2 = new
			// com.mir.ems.profile.openadr.recent.RegisterReport();
			// rt2.setDestEMA(emaProfile.getEmaID());
			// rt2.setRequestID("REQUESTID");
			// rt2.setService("oadrRegisterReport");
			//
			// String payload = rt2.toString();
			// this.exchange.respond(ResponseCode.CONTENT, payload,
			// MediaTypeRegistry.APPLICATION_JSON);
			//
			// return "NORESPONSE";
			//
			// }
			//
			// if (global.venRegisterFlag.get(emaID).intValue() > 0 && !flag &&
			// !global.tableHasChanged) {
			//
			// com.mir.ems.profile.openadr.recent.Response response = new
			// com.mir.ems.profile.openadr.recent.Response();
			// response.setDestEMA(emaProfile.getEmaID());
			// response.setRequestID("");
			// response.setResponseCode(200);
			// response.setResponseDescription("OK");
			// response.setService("oadrResponse");
			//
			// String payload = response.toString();
			// this.exchange.respond(ResponseCode.CONTENT, payload,
			// MediaTypeRegistry.APPLICATION_JSON);
			//
			// return "NORESPONSE";
			//
			// } else if (!global.tableHasChanged) {
			//
			// double threshold =
			// global.emaProtocolCoAP_EventFlag.get(emaID).getThreshold();
			// int strTime =
			// global.emaProtocolCoAP_EventFlag.get(emaID).getStartTime();
			// int strYMD =
			// global.emaProtocolCoAP_EventFlag.get(emaID).getStartYMD();
			// int endTime =
			// global.emaProtocolCoAP_EventFlag.get(emaID).getEndTime();
			// int endYMD =
			// global.emaProtocolCoAP_EventFlag.get(emaID).getEndYMD();
			//
			// String strTime_str = strTime + "";
			// String endTime_str = endTime + "";
			//
			// strTime_str = strTime_str.length() < 6 ? "0" + strTime_str :
			// strTime_str;
			// endTime_str = endTime_str.length() < 6 ? "0" + endTime_str :
			// endTime_str;
			//
			// String eventDuration = ISO8601(strYMD, strTime, endYMD, endTime);
			// String timeForm = (strYMD + "") + strTime_str;
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			//
			// try {
			//
			// // Date createdDateTime = sdf.parse(timeForm);
			//
			// Intervals interval = new Intervals();
			// interval.addIntervalsParams(global.duration, "uid", threshold);
			//
			// com.mir.ems.profile.openadr.recent.Event event = new
			// com.mir.ems.profile.openadr.recent.Event();
			// // Threshold �젙蹂� �쟾�떖
			//
			// event.addEventParams("eventID",
			// new com.mir.ems.profile.openadr.recent.EventSignals()
			// .addEventSignalsParams(interval.getIntervalsParams(),
			// "signalName",
			// "Control Event", "signalID", 0)
			// .getEventSignalsParams(),
			// 1, "modificationReason", -1, "mirLab",
			// new Date(System.currentTimeMillis()).toString(), "eventStatus",
			// false, "Event",
			// "properties", "components", emaProfile.getEmaID(),
			// new Date(System.currentTimeMillis()).toString(), eventDuration,
			// "tolerance",
			// "notification", "rampUp", "recovery");
			//
			// com.mir.ems.profile.openadr.recent.EventResponse er = new
			// com.mir.ems.profile.openadr.recent.EventResponse();
			// er.setRequestID(emaProfile.getRequestID());
			// er.setResponseCode(200);
			// er.setResponseDescription("OK");
			//
			// com.mir.ems.profile.openadr.recent.DistributeEvent drE = new
			// com.mir.ems.profile.openadr.recent.DistributeEvent();
			//
			// drE.setSrcEMA(global.SYSTEM_ID);
			// drE.setEvent(event.getEventParams());
			// drE.setRequestID(emaProfile.getRequestID());
			// drE.setResponse(er.toString());
			// drE.setService("oadrDistributeEvent");
			// drE.setResponseRequired("Always");
			//
			// String payload = drE.toString();
			// this.exchange.respond(ResponseCode.CONTENT, payload,
			// MediaTypeRegistry.APPLICATION_JSON);
			//
			// global.emaProtocolCoAP_EventFlag.get(emaID).setEventFlag(false);
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// return "NORESPONSE";
			// }
			//
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// return "NORESPONSE";
			//
			// }

		}

		public String acknowledgeCREATEDEVENT(String requestText) {
			JSONObject drmsg = new JSONObject();
			String srcEMA = null;

			if (version.equals("EMAP1.0b")) {

				// System.out.println("諛쏆븮�땲?");

				try {
					jsonObj = new JSONObject(requestText.toUpperCase());
					String emaID = jsonObj.getString("SRCEMA");

					com.mir.smartgrid.simulator.profile.emap.v2.Response response = new com.mir.smartgrid.simulator.profile.emap.v2.Response();
					response.setDestEMA(emaID);
					response.setRequestID("");
					response.setResponseCode(200);
					response.setResponseDescription("OK");
					response.setService("Response");
					response.setSrcEMA(Global.SYSTEMID);
					response.setTime(new Date(System.currentTimeMillis()).toString());

					String payload = response.toString();

					// System.out.println(payload.toString());

					this.exchange.respond(ResponseCode.CONTENT, payload, MediaTypeRegistry.APPLICATION_JSON);

					return "NORESPONSE";

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			//
			// else if (version.equals("OpenADR2.0b")) {
			//
			// try {
			// jsonObj = new JSONObject(requestText.toUpperCase());
			// String emaID = jsonObj.getString("VENID");
			//
			// com.mir.ems.profile.openadr.recent.Response response = new
			// com.mir.ems.profile.openadr.recent.Response();
			// response.setDestEMA(emaID);
			// response.setRequestID("");
			// response.setResponseCode(200);
			// response.setResponseDescription("OK");
			// response.setService("oadrResponse");
			//
			// String payload = response.toString();
			// this.exchange.respond(ResponseCode.CONTENT, payload,
			// MediaTypeRegistry.APPLICATION_JSON);
			//
			// return "NORESPONSE";
			//
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// } else {
			// try {
			//
			// emaProfile = new Emap_Cema_Profile();
			//
			// jsonObj = new JSONObject(requestText.toUpperCase());
			//
			// //
			// emaProfile.setEmaID(jsonObj.getString("SRCEMA")).setType(jsonObj.getString("OPTTYPE"))
			// // .setRequestID(jsonObj.getInt("REQUESTID"));
			// emaProfile.setEmaID(jsonObj.getString("SRCEMA")).setRequestID(jsonObj.getString("REQUESTID"));
			//
			// // opt in & out check
			// drmsg.put("SrcEMA", global.SYSTEM_ID);
			// drmsg.put("DestEMA", emaProfile.getEmaID());
			// drmsg.put("responseCode", 200);
			// drmsg.put("version", emaProfile.getVersion());
			// drmsg.put("responseDescription", "OK");
			// drmsg.put("requestID", emaProfile.getRequestID());
			// drmsg.put("service", "Response");
			// drmsg.put("time", new Date(System.currentTimeMillis()));
			//
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
			return drmsg.toString();
		}

		public String acknowledgeREQUESTEVENT(String requestText) {
			JSONObject drmsg = new JSONObject();
			String srcEMA = null;

			if (version.equals("EMAP1.0b")) {

				try {
					jsonObj = new JSONObject(requestText.toUpperCase());

					emaProfile = new Emap_Cema_Profile();

					emaProfile.setEmaID(jsonObj.getString("SRCEMA")).setRequestID(jsonObj.getString("REQUESTID"));

					com.mir.smartgrid.simulator.profile.emap.v2.Intervals interval = new com.mir.smartgrid.simulator.profile.emap.v2.Intervals();

					interval.addIntervalsParams("PT20S", "uid", 0.0);

					com.mir.smartgrid.simulator.profile.emap.v2.EventSignals es = new com.mir.smartgrid.simulator.profile.emap.v2.EventSignals();
					com.mir.smartgrid.simulator.profile.emap.v2.Event event = new com.mir.smartgrid.simulator.profile.emap.v2.Event();

					// Threshold �젙蹂� �쟾�떖

					event.addEventParams("eventID",
							new com.mir.smartgrid.simulator.profile.emap.v2.EventSignals()
									.addEventSignalsParams(interval.getIntervalsParams(), "signalName", "Control Event",
											"signalID", 0, 100, 0, 0, "KW/WON")
									.getEventSignalsParams(),
							1, "modificationReason", -1, "mirLab", new Date(System.currentTimeMillis()).toString(),
							"eventStatus", false, "SessionSetup", "properties", "components", emaProfile.getEmaID(),
							new Date(System.currentTimeMillis()).toString(), "PT1H", "tolerance", "notification",
							"rampUp", "recovery");

					/*-----------------�뿬湲곌퉴吏�媛� Active Mode --------------*/

					com.mir.smartgrid.simulator.profile.emap.v2.EventResponse er = new com.mir.smartgrid.simulator.profile.emap.v2.EventResponse();
					er.setRequestID(emaProfile.getRequestID());
					er.setResponseCode(200);
					er.setResponseDescription("OK");

					com.mir.smartgrid.simulator.profile.emap.v2.DistributeEvent drE = new com.mir.smartgrid.simulator.profile.emap.v2.DistributeEvent();

					drE.setDestEMA(emaProfile.getEmaID());
					drE.setEvent(event.getEventParams());
					drE.setRequestID(emaProfile.getRequestID());
					drE.setResponse(er.toString());
					drE.setService("DistributeEvent");
					drE.setSrcEMA(Global.SYSTEMID);
					drE.setTime(new Date(System.currentTimeMillis()).toString());
					drE.setResponseRequired("Always");

					String payload = drE.toString();

					this.exchange.respond(ResponseCode.CONTENT, payload, MediaTypeRegistry.APPLICATION_JSON);

					return "NORESPONSE";

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return "NORESPONSE";

			}
			//
			// else if (version.equals("OpenADR2.0b")) {
			//
			// // Threshold 遺꾨같 �븣怨좊━利� �떆�옉, Thread �븘�떂, 諛섎뱶�떆 �씠寃� �걹�굹怨�
			// // 遺꾨같�빐以섏빞 �븯湲� �븣臾�
			// new Greedy().start();
			//
			// try {
			// jsonObj = new JSONObject(requestText.toUpperCase());
			//
			// emaProfile = new Emap_Cema_Profile();
			//
			// emaProfile.setEmaID(jsonObj.getString("VENID")).setRequestID(jsonObj.getString("REQUESTID"));
			//
			// Intervals interval = new Intervals();
			// interval.addIntervalsParams(global.duration, "uid", 1234.0);
			//
			// com.mir.ems.profile.openadr.recent.Event event = new
			// com.mir.ems.profile.openadr.recent.Event();
			// // Threshold �젙蹂� �쟾�떖
			//
			// event.addEventParams("eventID",
			// new com.mir.ems.profile.openadr.recent.EventSignals()
			// .addEventSignalsParams(interval.getIntervalsParams(),
			// "signalName", "Control Event",
			// "signalID", 0)
			// .getEventSignalsParams(),
			// 1, "modificationReason", -1, "mirLab", new
			// Date(System.currentTimeMillis()).toString(),
			// "eventStatus", false, "SessionSetup", "properties", "components",
			// emaProfile.getEmaID(),
			// new Date(System.currentTimeMillis()).toString(), "PT1H",
			// "tolerance", "notification",
			// "rampUp", "recovery");
			//
			// com.mir.ems.profile.openadr.recent.EventResponse er = new
			// com.mir.ems.profile.openadr.recent.EventResponse();
			// er.setRequestID(emaProfile.getRequestID());
			// er.setResponseCode(200);
			// er.setResponseDescription("OK");
			//
			// com.mir.ems.profile.openadr.recent.DistributeEvent drE = new
			// com.mir.ems.profile.openadr.recent.DistributeEvent();
			//
			// drE.setSrcEMA(global.SYSTEM_ID);
			// drE.setEvent(event.getEventParams());
			// drE.setRequestID(emaProfile.getRequestID());
			// drE.setResponse(er.toString());
			// drE.setService("oadrDistributeEvent");
			// drE.setResponseRequired("Always");
			//
			// String payload = drE.toString();
			// this.exchange.respond(ResponseCode.CONTENT, payload,
			// MediaTypeRegistry.APPLICATION_JSON);
			//
			// return "NORESPONSE";
			//
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// return "NORESPONSE";
			//
			// }
			//
			// else {
			// try {
			//
			// emaProfile = new Emap_Cema_Profile();
			// jsonObj = new JSONObject(requestText.toUpperCase());
			//
			// emaProfile.setEmaID(jsonObj.getString("SRCEMA")).setRequestID(jsonObj.getString("REQUESTID"));
			//
			// drmsg.put("SrcEMA", global.SYSTEM_ID);
			// drmsg.put("DestEMA", emaProfile.getEmaID());
			// drmsg.put("requestID", emaProfile.getRequestID());
			// drmsg.put("responseCode", 200);
			// drmsg.put("responseDescription", "OK");
			// drmsg.put("time", new Date(System.currentTimeMillis()));
			//
			// Calendar calendar = Calendar.getInstance();
			// String startYMD = "" + calendar.get(Calendar.YEAR) + "" +
			// (calendar.get(Calendar.MONTH) + 1) + ""
			// + calendar.get(Calendar.DATE);
			// String startTime = "" + calendar.get(Calendar.HOUR_OF_DAY) + "" +
			// calendar.get(Calendar.MINUTE) + ""
			// + calendar.get(Calendar.SECOND);
			//
			// JSONArray eventInfo = new JSONArray();
			// JSONObject childFormat = new JSONObject();
			// childFormat.put("testEvent", false);
			// childFormat.put("marketContext", 1);
			// childFormat.put("eventStatus", "Registration");
			// childFormat.put("priority", 0);
			// childFormat.put("eventID", 0);
			// childFormat.put("startYMD", Integer.parseInt(startYMD));
			// childFormat.put("startTime", Integer.parseInt(startTime));
			// childFormat.put("endYMD", Integer.parseInt(startYMD));
			// childFormat.put("endTime", Integer.parseInt(startTime));
			// childFormat.put("duration", 1000);
			// childFormat.put("uid", 0);
			// double currentValue =
			// global.emaProtocolCoAP.get(jsonObj.getString("SRCEMA")).getPower();
			// double threshold =
			// global.emaProtocolCoAP.get(jsonObj.getString("SRCEMA")).getMargin();
			// childFormat.put("currentValue", currentValue);
			// childFormat.put("signalName", "Initial");
			// childFormat.put("signalType", "DistributeEvent");
			// childFormat.put("signalID", 1);
			// childFormat.put("threshold", threshold);
			// childFormat.put("capacity", threshold - currentValue);
			// eventInfo.put(childFormat);
			// drmsg.put("EMADREventInformation", eventInfo);
			//
			// eventInfo = new JSONArray();
			// childFormat = new JSONObject();
			// childFormat.put("price", 1000);
			// childFormat.put("unit", "KW");
			// eventInfo.put(childFormat);
			// drmsg.put("EMADRPriceInformation", eventInfo);
			//
			// drmsg.put("service", "DistributeEvent");
			//
			// } catch (JSONException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// }
			return drmsg.toString();
		}

	}

	public static String ISO8601(int startYMD, int startTime, int endYMD, int endTime) {

		int calTime = endTime - startTime;

		if (calTime > 0) {

			if (calTime < 60) {

				return "PT" + calTime + "S";

			} else if (calTime > 60 && calTime < 6000) {

				int stemp = calTime % 100;
				int mTemp = ((calTime - stemp) % 10000) / 100;

				if (stemp >= 60) {
					stemp -= 60;
					mTemp += 1;
				}

				return stemp == 0 ? "PT" + mTemp + "M" : "PT" + mTemp + "M" + stemp + "S";

			} else {

				int stemp = calTime % 100;
				int mTemp = ((calTime - stemp) % 10000) / 100;
				int hTemp = (calTime) / 10000;

				if (stemp >= 60) {
					stemp -= 60;
					mTemp += 1;
				}

				if (mTemp >= 60) {
					mTemp -= 60;
					hTemp += 1;
				}

				if (stemp == 0 && mTemp != 0) {

					return "PT" + hTemp + "H" + mTemp + "M";

				}
				if (mTemp == 0 && stemp != 0) {

					return "PT" + hTemp + "H" + stemp + "S";
				}
				if (mTemp == 0 && stemp == 0) {

					return "PT" + hTemp + "H";
				}

				return "PT" + hTemp + "H" + mTemp + "M" + stemp + "S";

			}
		}

		return "WRONG";

	}

}
