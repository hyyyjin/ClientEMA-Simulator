package com.mir.smartgrid.simulator.coap;

import java.sql.Date;
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
import com.mir.smartgrid.simulator.profile.emap.v2.EventACK_Model;

public class Poll_Periodic extends Thread {
	Controller controller;
	CoapClient client ;
	JSONObject json;

	String pathSet = "coap://" + Global.coapServerIP + ":" + Global.coapServerPort + "/EMAP/"
			+ Global.getParentnNodeID() + "/" + Global.version + "/";

	String openADRpathSet = "coap://" + Global.coapServerIP + ":" + Global.coapServerPort + "/OpenADR/"
			+ Global.parentnNodeID + "/" + Global.openADRVersion + "/";

	boolean eventFlag = true;

	public Poll_Periodic(Controller controller) {
		this.controller = controller;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		client = new CoapClient();
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(), 0, Global.pollinginterval);
	}

	private class UpdateTask extends TimerTask {

		@Override
		public void run() {

			if (eventFlag) {
				if (controller.getProfileType().equals("EMAP1.0b")) {
					if (controller.isPullModel()) {

						com.mir.smartgrid.simulator.profile.emap.v2.Poll poll = new com.mir.smartgrid.simulator.profile.emap.v2.Poll();
						poll.setDestEMA(Global.getParentnNodeID());
						poll.setService("Poll");
						poll.setSrcEMA(controller.getEmaID());
						poll.setTime(controller.getCurrentTime(System.currentTimeMillis()));

						String uri = pathSet + "Poll";

						client.setURI(uri);
						client.useCONs();

						client.put(new CoapHandler() {

							@Override
							public void onLoad(CoapResponse response) {
								// TODO Auto-generated method stub
								String content = response.getResponseText();

								try {
									json = new JSONObject(content);

									String responseDescription = json.getString("service");

									if (responseDescription.matches("Response")) {

									} else if (responseDescription.matches("DistributeEvent")) {

										double threshold = 0;
										String eventID = "";

										JSONArray jsonArr = new JSONArray(json.getString("event"));

										for (int i = 0; i < jsonArr.length(); i++) {

											JSONObject subJson = new JSONObject(jsonArr.get(i).toString());
											eventID = subJson.getString("eventID");

											JSONArray subJsonArr = new JSONArray(subJson.getString("eventSignals"));

											for (int j = 0; j < subJsonArr.length(); j++) {

												JSONObject subJson2 = new JSONObject(subJsonArr.get(i).toString());

												threshold = subJson2.getDouble("threshold");
											}

										}

										controller.setThreshold(threshold);

										System.out.println("==================================");
										System.out.println("EMA ID" + controller.getEmaID());
										System.out.println("EVENT RECV" + threshold);
										System.out.println("CURRENT VAL" + controller.getCurrentPower());
										System.out.println("EVENT ID" + eventID);
										System.out.println("==================================");

										controller.setReportCnt(4);
										eventFlag = false;

										CreatedEvent(eventID);
//										EventACK(eventID);
									}

								} catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

							@Override
							public void onError() {
								// TODO Auto-generated method stub
							}

						}, poll.toString(), MediaTypeRegistry.APPLICATION_JSON);

					}

				} else if (controller.getProfileType().equals("OpenADR2.0b")) {

					if (controller.isPullModel()) {

						com.mir.smartgrid.simulator.profile.openadr.Poll poll = new com.mir.smartgrid.simulator.profile.openadr.Poll();

						poll.setService("OadrPoll");
						poll.setVenID(controller.getEmaID());

						String uri = openADRpathSet + "OadrPoll";
//						CoapClient client = new CoapClient();

						client.setURI(uri);
						CoapResponse resp = client.put(poll.toString(), MediaTypeRegistry.APPLICATION_JSON);

						try {

							json = new JSONObject(resp.getResponseText().toString());
							String responseDescription = json.getString("service");

							if (responseDescription.matches("oadrResponse")) {
								Thread.sleep(Global.pollinginterval);
								// UpdateReport();
								// Poll_Periodical();
							} else {

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

								controller.setThreshold(threshold);

								// System.out.println("==================================");
								// System.out.println("EMA ID"+
								// controller.getEmaID());
								// System.out.println("EVENT RECV"+ threshold);
								// System.out.println("CURRENT VAL"+
								// controller.getCurrentPower());
								// System.out.println("==================================");

								System.out.println(threshold);
//								System.out.println("¿©±â?");
//								controller.setReportCnt(4);

								CreatedEvent("eventID");
								// EventACK(eventID);
							}

						} catch (Exception e) {
							e.printStackTrace();
							// Poll_Periodical();
						}

					}

					// else if (observeFlag == 0) {
					//
					// CoapClient client = new CoapClient();
					//
					// String uri = "coap://" + Global.coapServerIP + ":" +
					// Global.coapServerPort + "/OpenADR2.0b/"
					// + controller.getEmaID();
					// client.setURI(uri);
					// client.observe(new CoapHandler() {
					//
					// @Override
					// public void onLoad(CoapResponse response) {
					// String content = response.getResponseText();
					//
					// if (content.contains("DistributeEvent")) {
					//
					// double threshold = 0;
					//
					// JSONArray jsonArr;
					// try {
					// json = new JSONObject(content);
					//
					// jsonArr = new JSONArray(json.getString("event"));
					//
					// for (int i = 0; i < jsonArr.length(); i++) {
					//
					// JSONObject subJson = new
					// JSONObject(jsonArr.get(i).toString());
					//
					// JSONArray subJsonArr = new
					// JSONArray(subJson.getString("eventSignals"));
					//
					// for (int j = 0; j < subJsonArr.length(); j++) {
					//
					// JSONObject subJson2 = new
					// JSONObject(subJsonArr.get(i).toString());
					//
					// JSONArray subJsonArr2 = new
					// JSONArray(subJson2.getString("intervals"));
					//
					// for (int k = 0; k < subJsonArr2.length(); k++) {
					//
					// JSONObject subJson3 = new
					// JSONObject(subJsonArr2.get(i).toString());
					//
					// threshold = subJson3.getDouble("value");
					//
					// }
					// }
					//
					// }
					//
					// controller.setThreshold(threshold);
					// //
					// System.out.println("==================================");
					// // System.out.println("EMA ID"+
					// // controller.getEmaID());
					// // System.out.println("EVENT RECV"+ threshold);
					// // System.out.println("CURRENT VAL"+
					// // controller.getCurrentPower());
					// //
					// System.out.println("==================================");
					// controller.setReportCnt(4);
					// CreatedEvent();
					//
					// System.err.println("threshold" + threshold);
					// } catch (JSONException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					//
					// observeFlag = 1;
					// }
					// }
					//
					// @Override
					// public void onError() {
					// System.err.println("OBSERVING FAILED (press enter to
					// exit)");
					// }
					//
					// });
					//
					// }

				}

			}
		}
	}

	public void CreatedEvent(String eventID) {
		System.out.println(this.controller.getProfileType());
		if (this.controller.getProfileType().equals("EMAP1.0b")) {

			com.mir.smartgrid.simulator.profile.emap.v2.CreatedEvent cde = new com.mir.smartgrid.simulator.profile.emap.v2.CreatedEvent();

			cde.setDestEMA(Global.getParentnNodeID());
			cde.setEventID(eventID);
			cde.setModificationNumber(1);
			cde.setOptType("optIn");
			cde.setRequestID("requestID");
			cde.setResponseCode(200);
			cde.setResponseDescription("OK");
			cde.setService("CreatedEvent");
			cde.setSrcEMA(this.controller.getEmaID());
			cde.setTime(new Date(System.currentTimeMillis()).toString());

			String uri = pathSet + "Event";
//			CoapClient client = new CoapClient();

			client.setURI(uri);
			client.useCONs();

			client.put(new CoapHandler() {
				@Override
				public void onLoad(CoapResponse response) {
					// TODO Auto-generated method stub
					try {

						json = new JSONObject(response.getResponseText().toString());

						String responseDescription = json.getString("service");

						if (responseDescription.matches("Response")) {
							// Poll_Periodical();
							EventACK(eventID);
							eventFlag = true;
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				public void onError() {
				};
			}, cde.toString(), MediaTypeRegistry.APPLICATION_JSON);

		} else if (this.controller.getProfileType().equals("OpenADR2.0b")) {

			
			com.mir.smartgrid.simulator.profile.openadr.CreatedEvent cde = new com.mir.smartgrid.simulator.profile.openadr.CreatedEvent();

			cde.setVtnID(Global.getParentnNodeID());
			cde.setEventID("eventID");
			cde.setModificationNumber(1);
			cde.setOptType("optIn");
			cde.setRequestID("requestID");
			cde.setResponseCode(200);
			cde.setResponseDescription("OK");
			cde.setService("oadrCreatedEvent");
			cde.setVenID(this.controller.getEmaID());

			String uri = openADRpathSet + "EiEvent";
//			CoapClient client = new CoapClient();

			client.setURI(uri);
			CoapResponse resp = client.put(cde.toString(), MediaTypeRegistry.APPLICATION_JSON);

			try {

				json = new JSONObject(resp.getResponseText().toString());

				String responseDescription = json.getString("service");

				if (responseDescription.matches("oadrResponse")) {
					eventFlag = true;
					// Poll_Periodical();
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	public void EventACK(String eventID) {

		String uri = pathSet + "EventACK";
		CoapClient client = new CoapClient();

		client.setURI(uri);

		EventACK_Model eventACK = new EventACK_Model();
		eventACK.setDestEMA(Global.getParentnNodeID());
		eventACK.setSrcEMA(this.controller.getEmaID());
		eventACK.setEventID(eventID);

		client.useNONs();
		
		client.put(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				// TODO Auto-generated method stub
			}

			public void onError() {
			};
		}, eventACK.toString(), MediaTypeRegistry.APPLICATION_JSON);

	}

}
