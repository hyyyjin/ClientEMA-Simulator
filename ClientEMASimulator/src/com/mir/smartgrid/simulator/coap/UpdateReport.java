package com.mir.smartgrid.simulator.coap;

import java.sql.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONObject;

//import com.mir.smartgrid.simulator.coap.CoAPClient.UpdateTask;
import com.mir.smartgrid.simulator.controller.Controller;
import com.mir.smartgrid.simulator.global.Global;
import com.mir.smartgrid.simulator.profile.emap.v2.PowerAttributes;

public class UpdateReport extends Thread {
	Controller controller;

	String pathSet = "coap://" + Global.coapServerIP + ":" + Global.coapServerPort + "/EMAP/"
			+ Global.getParentnNodeID() + "/" + Global.version + "/";

	String openADRpathSet = "coap://" + Global.coapServerIP + ":" + Global.coapServerPort + "/OpenADR/"
			+ Global.parentnNodeID + "/" + Global.openADRVersion + "/";

	JSONObject json;
	CoapClient client;
	public UpdateReport(Controller controller) {
		this.controller = controller;
		client = new CoapClient();

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(), 0, Global.reportinterval);
	}

	private class UpdateTask extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			if (controller.getProfileType().equals("EMAP1.0b")) {
				double generate = 0, storage = 0, power = 0;

				com.mir.smartgrid.simulator.profile.emap.v2.PowerAttributes pa = new com.mir.smartgrid.simulator.profile.emap.v2.PowerAttributes();
				pa.addPowerAttributesParams(200.1, 300.1, 400.1);

				com.mir.smartgrid.simulator.profile.emap.v2.ReportDescription rd = new com.mir.smartgrid.simulator.profile.emap.v2.ReportDescription();

				// Explicit 일 경우
				if (Global.reportType.equals("Explicit")) {

					Iterator<String> it = Global.devProfile.keySet().iterator();

					double minValue = 0, maxValue = 0, avgValue = 0;

					while (it.hasNext()) {

						String key = it.next();

						if (Global.devProfile.get(key).getEmaID().equals(controller.getEmaID())) {

							avgValue += Global.devProfile.get(key).getAvgValue();
							minValue += Global.devProfile.get(key).getMinValue();
							maxValue += Global.devProfile.get(key).getMaxValue();
							power += Global.devProfile.get(key).getPower();
							generate += Global.devProfile.get(key).getGenerate();
							storage += Global.devProfile.get(key).getStorage();
						}
					}

					controller.setAvgValue(avgValue);
					controller.setMinValue(minValue);
					controller.setMaxValue(maxValue);
					controller.setGenerate(generate);
					controller.setStorage(storage);
					controller.setCurrentPower(power);

					// (1) 가장 상위 노드 정보를 전달한다.
					rd.addReportDescriptionParams(controller.getEmaID(), controller.getEmaID(), "EMA",
							controller.getReportType(), "itemUnits", "siScaleCode", "marketContext", "minPeriod",
							"maxPeriod", false, "itemDescription", pa.getPowerAttributesParams(), "state",
							"Controllable", controller.getCurrentPower(), -1, 0, controller.getGenerate(),
							controller.getStorage(), controller.getMaxValue(), controller.getMinValue(),
							controller.getAvgValue(), new Date(System.currentTimeMillis()).toString(),
							new Date(System.currentTimeMillis()).toString(),
							new Date(System.currentTimeMillis()).toString(), 9);

					// (2) 하위 노드에 대한 각 정보를 전달한다.

					if (!Global.CLIENTOPTION) {
						it = Global.devProfile.keySet().iterator();

						while (it.hasNext()) {

							String key = it.next();

							if (Global.devProfile.get(key).getEmaID().equals(controller.getEmaID())) {

								avgValue = Global.devProfile.get(key).getAvgValue();
								minValue = Global.devProfile.get(key).getMinValue();
								maxValue = Global.devProfile.get(key).getMaxValue();
								power = Global.devProfile.get(key).getPower();
								generate = Global.devProfile.get(key).getGenerate();
								storage = Global.devProfile.get(key).getStorage();

								rd.addReportDescriptionParams(key, key, "LED", controller.getReportType(), "itemUnits",
										"siScaleCode", "marketContext", "minPeriod", "maxPeriod", false,
										"itemDescription", pa.getPowerAttributesParams(), "state", "Controllable",
										Global.devProfile.get(key).getPower(), Global.devProfile.get(key).getDimming(),
										0, Global.devProfile.get(key).getGenerate(),
										Global.devProfile.get(key).getStorage(),
										Global.devProfile.get(key).getMaxValue(),
										Global.devProfile.get(key).getMinValue(),
										Global.devProfile.get(key).getAvgValue(),
										new Date(System.currentTimeMillis()).toString(),
										new Date(System.currentTimeMillis()).toString(),
										new Date(System.currentTimeMillis()).toString(), 9);
							}
						}
					} else {
						it = Global.emaProtocolCoAP.keySet().iterator();

						
						while (it.hasNext()) {

							String key = it.next();

							avgValue = Global.emaProtocolCoAP.get(key).getAvgValue();
							minValue = Global.emaProtocolCoAP.get(key).getMinValue();
							maxValue = Global.emaProtocolCoAP.get(key).getMaxValue();
							power = Global.emaProtocolCoAP.get(key).getPower();
							generate = Global.emaProtocolCoAP.get(key).getGenerate();
							storage = Global.emaProtocolCoAP.get(key).getStorage();

							rd.addReportDescriptionParams(key, key, "LED", controller.getReportType(), "itemUnits",
									"siScaleCode", "marketContext", "minPeriod", "maxPeriod", false, "itemDescription",
									pa.getPowerAttributesParams(), "state", "Controllable",
									Global.emaProtocolCoAP.get(key).getPower(),
									Global.emaProtocolCoAP.get(key).getDimming(), 0,
									Global.emaProtocolCoAP.get(key).getGenerate(),
									Global.emaProtocolCoAP.get(key).getStorage(),
									Global.emaProtocolCoAP.get(key).getMaxValue(),
									Global.emaProtocolCoAP.get(key).getMinValue(),
									Global.emaProtocolCoAP.get(key).getAvgValue(),
									new Date(System.currentTimeMillis()).toString(),
									new Date(System.currentTimeMillis()).toString(),
									new Date(System.currentTimeMillis()).toString(), 9);

						}
					}
				}

				// Implicit 일 경우
				else if (Global.reportType.equals("Implicit")) {

					Iterator<String> it = Global.devProfile.keySet().iterator();

					double minValue = 0, maxValue = 0, avgValue = 0;

					while (it.hasNext()) {

						String key = it.next();
						if (Global.devProfile.get(key).getEmaID().equals(controller.getEmaID())) {

							avgValue += Global.devProfile.get(key).getAvgValue();
							minValue += Global.devProfile.get(key).getMinValue();
							maxValue += Global.devProfile.get(key).getMaxValue();
							power += Global.devProfile.get(key).getPower();
							generate += Global.devProfile.get(key).getGenerate();
							storage += Global.devProfile.get(key).getStorage();

						}
					}
					controller.setAvgValue(avgValue);
					controller.setMinValue(minValue);
					controller.setMaxValue(maxValue);
					controller.setGenerate(generate);
					controller.setStorage(storage);
					controller.setCurrentPower(power);

					rd.addReportDescriptionParams(controller.getEmaID(), controller.getEmaID(), "EMA",
							controller.getReportType(), "itemUnits", "siScaleCode", "marketContext", "minPeriod",
							"maxPeriod", false, "itemDescription", pa.getPowerAttributesParams(), "state",
							"Controllable", controller.getCurrentPower(), -1, 0, controller.getGenerate(),
							controller.getStorage(), controller.getMaxValue(), controller.getMinValue(),
							controller.getAvgValue(), new Date(System.currentTimeMillis()).toString(),
							new Date(System.currentTimeMillis()).toString(),
							new Date(System.currentTimeMillis()).toString(), 9);

				}

				com.mir.smartgrid.simulator.profile.emap.v2.Report report = new com.mir.smartgrid.simulator.profile.emap.v2.Report();
				report.addReportParams("duration", rd.getReportDescriptionParams(), "reportRequestID",
						"reportSpecifierID", "reportName", "createdDateTime");

				com.mir.smartgrid.simulator.profile.emap.v2.RegisterReport rt = new com.mir.smartgrid.simulator.profile.emap.v2.RegisterReport();
				rt.setDestEMA(Global.getParentnNodeID());
				rt.setReport(report.getReportParams());
				rt.setRequestID("requestID");
				rt.setService("UpdateReport");
				rt.setSrcEMA(controller.getEmaID());
				rt.setType(Global.reportType);
				rt.setTime(controller.getCurrentTime(System.currentTimeMillis()));

				String uri = pathSet + "Report";

//				client = new CoapClient();
				client.setURI(uri);

				client.put(new CoapHandler() {
					@Override
					public void onLoad(CoapResponse response) {
						 String content = response.getResponseText();
						// System.out.println(content);
					}

					@Override
					public void onError() {
						// TODO Auto-generated method stub

					}
				}, rt.toString(), MediaTypeRegistry.APPLICATION_JSON);

				// CoapResponse resp = client.put(rt.toString(),
				// MediaTypeRegistry.APPLICATION_JSON);

				// try {
				//
				// System.out.println("첫번쨰");
				// System.out.println("머야");
				// System.out.println(resp.getResponseText().toString());
				//
				// json = new JSONObject(resp.getResponseText().toString());
				// String responseDescription = json.getString("service");
				//
				// if (responseDescription.matches("UpdatedReport")) {
				// if (!controller.isPullModel()) {
				// Thread.sleep(Global.interval);
				//// UpdateReport();
				// }
				// }
				//
				// } catch (Exception e) {
				// e.printStackTrace();
				// // UpdateReport();
				// }

			} else if (controller.getProfileType().equals("OpenADR2.0b")) {
				double generate = 0, storage = 0, power = 0;

				com.mir.smartgrid.simulator.profile.openadr.ReportDescription rd = new com.mir.smartgrid.simulator.profile.openadr.ReportDescription();

				// Explicit 일 경우
				if (Global.reportType.equals("Explicit")) {

					Iterator<String> it = Global.devProfile.keySet().iterator();

					double minValue = 0, maxValue = 0, avgValue = 0;

					while (it.hasNext()) {

						String key = it.next();

						if (Global.devProfile.get(key).getEmaID().equals(controller.getEmaID())) {

							avgValue += Global.devProfile.get(key).getAvgValue();
							minValue += Global.devProfile.get(key).getMinValue();
							maxValue += Global.devProfile.get(key).getMaxValue();
							power += Global.devProfile.get(key).getPower();
							generate += Global.devProfile.get(key).getGenerate();
							storage += Global.devProfile.get(key).getStorage();

						}
					}
					controller.setAvgValue(avgValue);
					controller.setMinValue(minValue);
					controller.setMaxValue(maxValue);
					controller.setGenerate(generate);
					controller.setStorage(storage);
					controller.setCurrentPower(power);

					// (1) 가장 상위 노드 정보를 전달한다.
					rd.addReportDescriptionParams(controller.getEmaID(), controller.getEmaID(), Global.reportType,
							"itemUnits", "siScaleCode", "marketContext", "minPeriod", "maxPeriod", false,
							"itemDescription",
							new PowerAttributes().addPowerAttributesParams(0, power, 0).getPowerAttributesParams());

					// (2) 하위 노드에 대한 각 정보를 전달한다.

					it = Global.devProfile.keySet().iterator();

					while (it.hasNext()) {

						String key = it.next();

						avgValue = Global.devProfile.get(key).getAvgValue();
						minValue = Global.devProfile.get(key).getMinValue();
						maxValue = Global.devProfile.get(key).getMaxValue();
						power = Global.devProfile.get(key).getPower();
						generate = Global.devProfile.get(key).getGenerate();
						storage = Global.devProfile.get(key).getStorage();

						rd.addReportDescriptionParams(key, key, Global.reportType, "itemUnits", "siScaleCode",
								"marketContext", "minPeriod", "maxPeriod", false, "itemDescription",
								new PowerAttributes().addPowerAttributesParams(0, power, 0).getPowerAttributesParams());

					}

				}

				// Implicit 일 경우
				else if (Global.reportType.equals("Implicit")) {

					Iterator<String> it = Global.devProfile.keySet().iterator();

					double minValue = 0, maxValue = 0, avgValue = 0;

					while (it.hasNext()) {

						String key = it.next();

						avgValue += Global.devProfile.get(key).getAvgValue();
						minValue += Global.devProfile.get(key).getMinValue();
						maxValue += Global.devProfile.get(key).getMaxValue();
						power += Global.devProfile.get(key).getPower();
						generate += Global.devProfile.get(key).getGenerate();
						storage += Global.devProfile.get(key).getStorage();

					}
					controller.setAvgValue(avgValue);
					controller.setMinValue(minValue);
					controller.setMaxValue(maxValue);
					controller.setGenerate(generate);
					controller.setStorage(storage);
					controller.setCurrentPower(power);

					rd.addReportDescriptionParams(controller.getEmaID(), controller.getEmaID(), Global.reportType,
							"itemUnits", "siScaleCode", "marketContext", "minPeriod", "maxPeriod", false,
							"itemDescription",
							new PowerAttributes().addPowerAttributesParams(0, power, 0).getPowerAttributesParams());

				}

				com.mir.smartgrid.simulator.profile.openadr.Report report = new com.mir.smartgrid.simulator.profile.openadr.Report();
				report.addReportParams("duration", rd.getReportDescriptionParams(), "reportRequestID",
						"reportSpecifierID", "reportName", "createdDateTime");

				com.mir.smartgrid.simulator.profile.openadr.RegisterReport rt = new com.mir.smartgrid.simulator.profile.openadr.RegisterReport();
				rt.setSrcEMA(controller.getEmaID());
				rt.setReport(report.getReportParams());
				rt.setRequestID("requestID");
				rt.setService("oadrUpdateReport");

				String uri = openADRpathSet + "EiReport";
				CoapClient client = new CoapClient();

				client.setURI(uri);
				CoapResponse resp = client.put(rt.toString(), MediaTypeRegistry.APPLICATION_JSON);

				try {

					json = new JSONObject(resp.getResponseText().toString());
					String responseDescription = json.getString("service");

					if (responseDescription.matches("oadrUpdatedReport")) {

					}

				} catch (Exception e) {
					e.printStackTrace();
					// UpdateReport();
				}

			}

		}
	}

}
