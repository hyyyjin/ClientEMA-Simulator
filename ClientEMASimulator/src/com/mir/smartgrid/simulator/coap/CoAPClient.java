package com.mir.smartgrid.simulator.coap;

import java.sql.Date;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mir.smartgrid.simulator.controller.Controller;
import com.mir.smartgrid.simulator.global.Global;
import com.mir.smartgrid.simulator.profile.emap.v2.PowerAttributes;
import com.mir.smartgrid.simulator.profile.openadr.Event;

public class CoAPClient extends Thread {

	public int observeFlag = 0;
	public int summaryFlag = 0;

	// CoapClient client = new CoapClient();
	// CoapResponse resp;
	JSONObject json;
	CoapClient client;

	String pathSet = "coap://" + Global.coapServerIP + ":" + Global.coapServerPort + "/EMAP/"
			+ Global.getParentnNodeID() + "/" + Global.version + "/";

	String openADRpathSet = "coap://" + Global.coapServerIP + ":" + Global.coapServerPort + "/OpenADR/"
			+ Global.parentnNodeID + "/" + Global.openADRVersion + "/";

	Controller controller;

	public CoAPClient(Controller controller) {

		this.controller = controller;
		client = new CoapClient();

//		Timer timer = new Timer();
//		timer.schedule(new UpdateTask(), 0, 50);

	}

//	private class UpdateTask extends TimerTask {
//		public void run() {
//
//			if (observeFlag == 1) {
//				// CreatedEvent();
//				observeFlag = 2;
//			}
//
//		}
//	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();

		ConnectRegistration();

	}

	public void ConnectRegistration() {

		if (this.controller.getProfileType().equals("EMAP1.0b")) {
			com.mir.smartgrid.simulator.profile.emap.v2.ConnectRegistration connectR = new com.mir.smartgrid.simulator.profile.emap.v2.ConnectRegistration();
			connectR.setDestEMA(Global.getParentnNodeID());
			connectR.setRequestID("requestID");
			connectR.setService("ConnectRegistration");
			connectR.setSrcEMA(controller.getEmaID());
			connectR.setTime(new Date(System.currentTimeMillis()).toString());
			connectR.setVersion("1.0b");

			String uri = pathSet + "SessionSetup";
			client.setURI(uri);
			CoapResponse resp = client.put(connectR.toString(), MediaTypeRegistry.APPLICATION_JSON);

			try {
				json = new JSONObject(resp.getResponseText().toString());
				String responseDescription = json.getString("responseDescription");

				// if (responseDescription.matches("OK|Ok|ok|oK"))
				CreatePartyRegistration();

			} catch (JSONException e) {
				// TODO Auto-generated catch block

				e.printStackTrace();
				ConnectRegistration();
			}

		} else if (this.controller.getProfileType().equals("OpenADR2.0b")) {

			com.mir.smartgrid.simulator.profile.openadr.ConnectRegistration connectR = new com.mir.smartgrid.simulator.profile.openadr.ConnectRegistration();
			connectR.setRequestID("requestID");
			connectR.setService("oadrQueryRegistration");
			connectR.setSrcEMA(controller.getEmaID());

			String uri = openADRpathSet + "EiRegisterParty";
//			CoapClient client = new CoapClient();
			client.setURI(uri);
			CoapResponse resp = client.put(connectR.toString(), MediaTypeRegistry.APPLICATION_JSON);

			try {
				json = new JSONObject(resp.getResponseText().toString());
				String responseDescription = json.getString("responseDescription");

				// if (responseDescription.matches("OK|Ok|ok|oK"))
				CreatePartyRegistration();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
//				ConnectRegistration();
			}

		}

	}

	public void CreatePartyRegistration() {

		if (this.controller.getProfileType().equals("EMAP1.0b")) {

			com.mir.smartgrid.simulator.profile.emap.v2.CreatePartyRegistration cp = new com.mir.smartgrid.simulator.profile.emap.v2.CreatePartyRegistration();

			cp.setDestEMA(Global.getParentnNodeID());
			cp.setHttpPullModel(this.controller.isPullModel());
			cp.setProfileName("EMAP1.0b");
			cp.setReportOnly(false);
			cp.setRequestID("requestID");
			cp.setService("CreatePartyRegistration");
			cp.setSrcEMA(this.controller.getEmaID());
			cp.setTime(this.controller.getCurrentTime(System.currentTimeMillis()));
			cp.setTransportName("MQTT");
			cp.setXmlSignature(true);

			String uri = pathSet + "SessionSetup";
//			CoapClient client = new CoapClient();
			client.setURI(uri);
			CoapResponse resp = client.put(cp.toString(), MediaTypeRegistry.APPLICATION_JSON);

			try {
				json = new JSONObject(resp.getResponseText().toString());
				// String responseDescription =
				// json.getString("responseDescription");
				//
				// if (responseDescription.matches("OK|Ok|ok|oK"))
				RegisterReport();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
//				CreatePartyRegistration();
			}

		} else if (this.controller.getProfileType().equals("OpenADR2.0b")) {

			com.mir.smartgrid.simulator.profile.openadr.CreatePartyRegistration cp = new com.mir.smartgrid.simulator.profile.openadr.CreatePartyRegistration();

			cp.setHttpPullModel(this.controller.isPullModel());
			cp.setProfileName("OpenADR2.0b");
			cp.setReportOnly(false);
			cp.setRequestID("requestID");
			cp.setService("oadrCreatePartyRegistration");
			cp.setSrcEMA(this.controller.getEmaID());
			cp.setTransportName("MQTT");
			cp.setXmlSignature(true);

			String uri = openADRpathSet + "EiRegisterParty";
//			CoapClient client = new CoapClient();
			client.setURI(uri);
			CoapResponse resp = client.put(cp.toString(), MediaTypeRegistry.APPLICATION_JSON);

			try {
				json = new JSONObject(resp.getResponseText().toString());
				// String responseDescription =
				// json.getString("responseDescription");
				//
				// if (responseDescription.matches("OK|Ok|ok|oK"))
				RegisterReport();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				CreatePartyRegistration();
			}

		}
	}

	public void RegisterReport() {
		if (this.controller.getProfileType().equals("EMAP1.0b")) {
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

					if (Global.devProfile.get(key).getEmaID().equals(this.controller.getEmaID())) {

						avgValue += Global.devProfile.get(key).getAvgValue();
						minValue += Global.devProfile.get(key).getMinValue();
						maxValue += Global.devProfile.get(key).getMaxValue();
						power += Global.devProfile.get(key).getPower();
						generate += Global.devProfile.get(key).getGenerate();
						storage += Global.devProfile.get(key).getStorage();
					}
				}

				this.controller.setAvgValue(avgValue);
				this.controller.setMinValue(minValue);
				this.controller.setMaxValue(maxValue);
				this.controller.setGenerate(generate);
				this.controller.setStorage(storage);
				this.controller.setCurrentPower(power);

				// (1) 가장 상위 노드 정보를 전달한다.
				rd.addReportDescriptionParams(this.controller.getEmaID(), this.controller.getEmaID(), "EMA",
						this.controller.getReportType(), "itemUnits", "siScaleCode", "marketContext", "minPeriod",
						"maxPeriod", false, "itemDescription", pa.getPowerAttributesParams(), "state", "Controllable",
						this.controller.getCurrentPower(), -1, 0, this.controller.getGenerate(),
						this.controller.getStorage(), this.controller.getMaxValue(), this.controller.getMinValue(),
						this.controller.getAvgValue(), new Date(System.currentTimeMillis()).toString(),
						new Date(System.currentTimeMillis()).toString(),
						new Date(System.currentTimeMillis()).toString(), 9);

				// (2) 하위 노드에 대한 각 정보를 전달한다.

				it = Global.devProfile.keySet().iterator();

				while (it.hasNext()) {

					String key = it.next();

					if (Global.devProfile.get(key).getEmaID().equals(this.controller.getEmaID())) {

						avgValue = Global.devProfile.get(key).getAvgValue();
						minValue = Global.devProfile.get(key).getMinValue();
						maxValue = Global.devProfile.get(key).getMaxValue();
						power = Global.devProfile.get(key).getPower();
						generate = Global.devProfile.get(key).getGenerate();
						storage = Global.devProfile.get(key).getStorage();

						rd.addReportDescriptionParams(key, key, "LED", this.controller.getReportType(), "itemUnits",
								"siScaleCode", "marketContext", "minPeriod", "maxPeriod", false, "itemDescription",
								pa.getPowerAttributesParams(), "state", "Controllable",
								Global.devProfile.get(key).getPower(), Global.devProfile.get(key).getDimming(), 0,
								Global.devProfile.get(key).getGenerate(), Global.devProfile.get(key).getStorage(),
								Global.devProfile.get(key).getMaxValue(), Global.devProfile.get(key).getMinValue(),
								Global.devProfile.get(key).getAvgValue(),
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
					if (Global.devProfile.get(key).getEmaID().equals(this.controller.getEmaID())) {

						avgValue += Global.devProfile.get(key).getAvgValue();
						minValue += Global.devProfile.get(key).getMinValue();
						maxValue += Global.devProfile.get(key).getMaxValue();
						power += Global.devProfile.get(key).getPower();
						generate += Global.devProfile.get(key).getGenerate();
						storage += Global.devProfile.get(key).getStorage();

					}
				}
				this.controller.setAvgValue(avgValue);
				this.controller.setMinValue(minValue);
				this.controller.setMaxValue(maxValue);
				this.controller.setGenerate(generate);
				this.controller.setStorage(storage);
				this.controller.setCurrentPower(power);

				rd.addReportDescriptionParams(this.controller.getEmaID(), this.controller.getEmaID(), "EMA",
						this.controller.getReportType(), "itemUnits", "siScaleCode", "marketContext", "minPeriod",
						"maxPeriod", false, "itemDescription", pa.getPowerAttributesParams(), "state", "Controllable",
						this.controller.getCurrentPower(), -1, 0, this.controller.getGenerate(),
						this.controller.getStorage(), this.controller.getMaxValue(), this.controller.getMinValue(),
						this.controller.getAvgValue(), new Date(System.currentTimeMillis()).toString(),
						new Date(System.currentTimeMillis()).toString(),
						new Date(System.currentTimeMillis()).toString(), 9);

			}

			com.mir.smartgrid.simulator.profile.emap.v2.Report report = new com.mir.smartgrid.simulator.profile.emap.v2.Report();
			report.addReportParams("duration", rd.getReportDescriptionParams(), "reportRequestID", "reportSpecifierID",
					"reportName", "createdDateTime");

			com.mir.smartgrid.simulator.profile.emap.v2.RegisterReport rt = new com.mir.smartgrid.simulator.profile.emap.v2.RegisterReport();
			rt.setDestEMA(Global.getParentnNodeID());
			rt.setReport(report.getReportParams());
			rt.setRequestID("requestID");
			rt.setService("RegisterReport");
			rt.setSrcEMA(this.controller.getEmaID());
			rt.setType(Global.reportType);
			rt.setTime(this.controller.getCurrentTime(System.currentTimeMillis()));

			String uri = pathSet + "SessionSetup";
//			CoapClient client = new CoapClient();
			client.setURI(uri);
			CoapResponse resp = client.put(rt.toString(), MediaTypeRegistry.APPLICATION_JSON);

			try {
				json = new JSONObject(resp.getResponseText().toString());
				String responseDescription = json.getString("responseDescription");

				if (responseDescription.matches("OK|Ok|ok|oK"))
					Poll();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// RegisterReport();
			}

		} else if (this.controller.getProfileType().equals("OpenADR2.0b")) {

			double generate = 0, storage = 0, power = 0;

			com.mir.smartgrid.simulator.profile.openadr.PowerAttributes pa = new com.mir.smartgrid.simulator.profile.openadr.PowerAttributes();
			pa.addPowerAttributesParams(200.1, 300.1, 400.1);

			com.mir.smartgrid.simulator.profile.openadr.ReportDescription rd = new com.mir.smartgrid.simulator.profile.openadr.ReportDescription();

			// Explicit 일 경우
			if (Global.reportType.equals("Explicit")) {

				Iterator<String> it = Global.devProfile.keySet().iterator();

				double minValue = 0, maxValue = 0, avgValue = 0;

				while (it.hasNext()) {

					String key = it.next();

					if (Global.devProfile.get(key).getEmaID().equals(this.controller.getEmaID())) {

						avgValue += Global.devProfile.get(key).getAvgValue();
						minValue += Global.devProfile.get(key).getMinValue();
						maxValue += Global.devProfile.get(key).getMaxValue();
						power += Global.devProfile.get(key).getPower();
						generate += Global.devProfile.get(key).getGenerate();
						storage += Global.devProfile.get(key).getStorage();
					}
				}
				this.controller.setAvgValue(avgValue);
				this.controller.setMinValue(minValue);
				this.controller.setMaxValue(maxValue);
				this.controller.setGenerate(generate);
				this.controller.setStorage(storage);
				this.controller.setCurrentPower(power);

				// (1) 가장 상위 노드 정보를 전달한다.
				rd.addReportDescriptionParams(this.controller.getEmaID(), this.controller.getEmaID(), Global.reportType,
						"itemUnits", "siScaleCode", "marketContext", "minPeriod", "maxPeriod", false, "itemDescription",
						pa.getPowerAttributesParams());

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
							pa.getPowerAttributesParams());

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
				this.controller.setAvgValue(avgValue);
				this.controller.setMinValue(minValue);
				this.controller.setMaxValue(maxValue);
				this.controller.setGenerate(generate);
				this.controller.setStorage(storage);
				this.controller.setCurrentPower(power);

				rd.addReportDescriptionParams(this.controller.getEmaID(), this.controller.getEmaID(), Global.reportType,
						"itemUnits", "siScaleCode", "marketContext", "minPeriod", "maxPeriod", false, "itemDescription",
						pa.getPowerAttributesParams());

			}

			com.mir.smartgrid.simulator.profile.openadr.Report report = new com.mir.smartgrid.simulator.profile.openadr.Report();
			report.addReportParams("duration", rd.getReportDescriptionParams(), "reportRequestID", "reportSpecifierID",
					"reportName", "createdDateTime");

			com.mir.smartgrid.simulator.profile.openadr.RegisterReport rt = new com.mir.smartgrid.simulator.profile.openadr.RegisterReport();
			rt.setSrcEMA(this.controller.getEmaID());
			rt.setReport(report.getReportParams());
			rt.setRequestID("requestID");
			rt.setService("oadrRegisterReport");

			String uri = openADRpathSet + "EiReport";
//			CoapClient client = new CoapClient();
			client.setURI(uri);
			CoapResponse resp = client.put(rt.toString(), MediaTypeRegistry.APPLICATION_JSON);

			try {
				json = new JSONObject(resp.getResponseText().toString());
				String responseDescription = json.getString("responseDescription");

				if (responseDescription.matches("OK|Ok|ok|oK"))
					Poll();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				// RegisterReport();
			}

		}
	}

	public void Poll() {
		if (this.controller.getProfileType().equals("EMAP1.0b")) {
			com.mir.smartgrid.simulator.profile.emap.v2.Poll poll = new com.mir.smartgrid.simulator.profile.emap.v2.Poll();
			poll.setDestEMA(Global.getParentnNodeID());
			poll.setService("Poll");
			poll.setSrcEMA(this.controller.getEmaID());
			poll.setTime(this.controller.getCurrentTime(System.currentTimeMillis()));

			String uri = pathSet + "SessionSetup";
//			CoapClient client = new CoapClient();

			client.setURI(uri);
			CoapResponse resp = client.put(poll.toString(), MediaTypeRegistry.APPLICATION_JSON);

			try {
				json = new JSONObject(resp.getResponseText().toString());
				String responseDescription = json.getString("service");

				if (responseDescription.matches("RegisterReport"))
					RegisteredReport();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Poll();
			}

		} else if (this.controller.getProfileType().equals("OpenADR2.0b")) {

			com.mir.smartgrid.simulator.profile.openadr.Poll poll = new com.mir.smartgrid.simulator.profile.openadr.Poll();

			poll.setService("OadrPoll");
			poll.setVenID(this.controller.getEmaID());

			String uri = openADRpathSet + "OadrPoll";
//			CoapClient client = new CoapClient();

			client.setURI(uri);
			CoapResponse resp = client.put(poll.toString(), MediaTypeRegistry.APPLICATION_JSON);

			try {
				json = new JSONObject(resp.getResponseText().toString());
				String responseDescription = json.getString("service");

				if (responseDescription.matches("oadrRegisterReport"))
					RegisteredReport();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Poll();
			}
		}

	}

	public void RegisteredReport() {
		if (this.controller.getProfileType().equals("EMAP1.0b")) {
			com.mir.smartgrid.simulator.profile.emap.v2.RegisteredReport rdt = new com.mir.smartgrid.simulator.profile.emap.v2.RegisteredReport();
			rdt.setDestEMA(Global.getParentnNodeID());
			rdt.setRequestID("requestID");
			rdt.setResponseCode(200);
			rdt.setResponseDescription("OK");
			rdt.setService("RegisteredReport");
			rdt.setSrcEMA(this.controller.getEmaID());
			rdt.setTime(this.controller.getCurrentTime(System.currentTimeMillis()));

			String uri = pathSet + "SessionSetup";
//			CoapClient client = new CoapClient();

			client.setURI(uri);
			CoapResponse resp = client.put(rdt.toString(), MediaTypeRegistry.APPLICATION_JSON);

			try {
				json = new JSONObject(resp.getResponseText().toString());
				String responseDescription = json.getString("responseDescription");

				if (responseDescription.matches("OK|Ok|ok|oK"))
					RequestEvent();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				RegisteredReport();
			}
		} else if (this.controller.getProfileType().equals("OpenADR2.0b")) {

			com.mir.smartgrid.simulator.profile.openadr.RegisteredReport rdt = new com.mir.smartgrid.simulator.profile.openadr.RegisteredReport();
			rdt.setRequestID("requestID");
			rdt.setResponseCode(200);
			rdt.setResponseDescription("OK");
			rdt.setService("oadrRegisteredReport");
			rdt.setVenID(this.controller.getEmaID());
			String uri = openADRpathSet + "EiReport";
//			CoapClient client = new CoapClient();

			client.setURI(uri);
			CoapResponse resp = client.put(rdt.toString(), MediaTypeRegistry.APPLICATION_JSON);

			try {
				json = new JSONObject(resp.getResponseText().toString());
				String responseDescription = json.getString("responseDescription");

				if (responseDescription.matches("OK|Ok|ok|oK"))
					RequestEvent();

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
//				RegisteredReport();
			}

		}
	}

	public void RequestEvent() {
		if (this.controller.getProfileType().equals("EMAP1.0b")) {
			com.mir.smartgrid.simulator.profile.emap.v2.RequestEvent requestEvent = new com.mir.smartgrid.simulator.profile.emap.v2.RequestEvent();
			requestEvent.setDestEMA(Global.getParentnNodeID());
			requestEvent.setRequestID("requestID");
			requestEvent.setService("RequestEvent");
			requestEvent.setSrcEMA(this.controller.getEmaID());
			requestEvent.setTime(this.controller.getCurrentTime(System.currentTimeMillis()));

			String uri = pathSet + "SessionSetup";
//			CoapClient client = new CoapClient();

			client.setURI(uri);
			CoapResponse resp = client.put(requestEvent.toString(), MediaTypeRegistry.APPLICATION_JSON);
			// summary();

			try {
				json = new JSONObject(resp.getResponseText().toString());
				String responseDescription = json.getString("service");

				if (responseDescription.matches("DistributeEvent")) {

					double threshold = 0;

					JSONArray jsonArr = new JSONArray(json.getString("event"));

					for (int i = 0; i < jsonArr.length(); i++) {

						JSONObject subJson = new JSONObject(jsonArr.get(i).toString());
						JSONArray subJsonArr = new JSONArray(subJson.getString("eventSignals"));

						for (int j = 0; j < subJsonArr.length(); j++) {
							JSONObject subJson2 = new JSONObject(subJsonArr.get(i).toString());
							threshold = subJson2.getDouble("threshold");
						}
					}

					System.out.println("SET THRESHOLD" + threshold);
					this.controller.setThreshold(threshold);
					
					if(Global.summaryObs){
						new SummaryObs(this.controller.getEmaID()).start();
					}
					
					if (this.controller.isPullModel()) {
						new Poll_Periodic(this.controller).start();
					} else {
						new EventObserve(this.controller.getEmaID(), this.controller).start();
					}
					new UpdateReport(this.controller).start();
				}

			} catch (JSONException e) {
				e.printStackTrace();
//				RequestEvent();
			}
		} else if (this.controller.getProfileType().equals("OpenADR2.0b")) {

			com.mir.smartgrid.simulator.profile.openadr.RequestEvent requestEvent = new com.mir.smartgrid.simulator.profile.openadr.RequestEvent();
			requestEvent.setVenID(this.controller.getEmaID());
			requestEvent.setRequestID("requestID");
			requestEvent.setService("OadrRequestEvent");

			String uri = openADRpathSet + "EiEvent";
//			CoapClient client = new CoapClient();

			client.setURI(uri);
			CoapResponse resp = client.put(requestEvent.toString(), MediaTypeRegistry.APPLICATION_JSON);

			try {
				json = new JSONObject(resp.getResponseText().toString());
				String responseDescription = json.getString("service");

				if (responseDescription.matches("oadrDistributeEvent")) {

					double threshold = 0;
					JSONArray jsonArr = new JSONArray(json.getString("event"));

					for (int i = 0; i < jsonArr.length(); i++) {

						JSONObject subJson = new JSONObject(jsonArr.get(i).toString());
						JSONArray subJsonArr = new JSONArray(subJson.getString("eventSignals"));

						for (int j = 0; j < subJsonArr.length(); j++) {

							JSONObject subJson2 = new JSONObject(subJsonArr.get(i).toString());
							JSONArray subJsonArr2 = new JSONArray(subJson2.getString("intervals"));

							for (int k = 0; k < subJsonArr2.length(); k++) {
								JSONObject subJson3 = new JSONObject(subJsonArr2.get(i).toString());
								threshold = subJson3.getDouble("value");
							}
						}

					}

					this.controller.setThreshold(threshold);

					
					if (this.controller.isPullModel()) {
						new Poll_Periodic(this.controller).start();
					} else {
						new EventObserve(this.controller.getEmaID(), this.controller).start();
					}
					
					new UpdateReport(this.controller).start();
				}

			} catch (JSONException e) {
				e.printStackTrace();
//				RequestEvent();
			}

		}
	}

}
