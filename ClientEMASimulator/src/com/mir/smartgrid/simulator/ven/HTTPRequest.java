package com.mir.smartgrid.simulator.ven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.mir.smartgrid.simulator.controller.Controller;
import com.mir.smartgrid.simulator.global.Global;

public class HTTPRequest {

	private String subPath;
	private StringBuilder httpResponse = new StringBuilder();
	private StringBuilder httpResponse_updateR = new StringBuilder();
	private StringBuilder httpResponse_event = new StringBuilder();
	private Controller controller;

	public HTTPRequest() {

	}

	public HTTPRequest(Controller controller, String subPath)
			throws ParserConfigurationException, SAXException, TransformerException {

		this.controller = controller;
		setSubPath(subPath);

		HttpClientBuilder hcb = HttpClientBuilder.create();
		HttpClient client = hcb.build();
		HttpPost post = new HttpPost(Global.vtnURL + getSubPath());
		HttpClientBuilder hcb_updateReport = HttpClientBuilder.create();
		HttpClient client_updateReport = hcb_updateReport.build();
		HttpPost post_updateReport = new HttpPost(Global.vtnURL + getSubPath());

		System.out.println(Global.vtnURL + getSubPath());
		
		try {

			post.setEntity(new StringEntity(new VENImpl().QueryRegistration("ba4d8f5c0a")));
			// POST
			HttpResponse response = client.execute(post);
			HttpResponse updateReport_response;
			// HttpResponse eventResponse;
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				// line = line.replace(" ", "");
				setHttpResponse(line);
			}

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(getHttpResponse().toString())));
			NodeList nodes = doc.getDocumentElement().getElementsByTagNameNS("*", "*");

			String requestID = "";

			while (true) {

				if (getHttpResponse().toString().contains("oadrCreatedPartyRegistration")) {

					for (int i = 0; i < nodes.getLength(); i++) {
						Node node = nodes.item(i);
						if (node.getNodeName().contains("requestID"))
							requestID = node.getTextContent();
					}

					post.setEntity(
							new StringEntity(new VENImpl().CreatePartyRegistration(this.controller.getEmaID(),
									"2.0b", "simpleHttp", null, false, false, Global.pullModel, requestID)));
					// POST
					response = client.execute(post);
					rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

					line = "";

					httpResponse = new StringBuilder();
					while ((line = rd.readLine()) != null) {
						// line = line.replace(" ", "");
						setHttpResponse(line);
					}

				}

				if (getHttpResponse().toString().length() > 1) {
					doc = dBuilder.parse(new InputSource(new StringReader(getHttpResponse().toString())));
					nodes = doc.getDocumentElement().getElementsByTagNameNS("*", "*");
				}
				if (getHttpResponse().toString().contains("oadrCreatedPartyRegistration")) {

					post = new HttpPost(Global.vtnURL + "EiReport");
					// String venID = "";
					for (int i = 0; i < nodes.getLength(); i++) {
						Node node = nodes.item(i);
						if (node.getNodeName().contains("requestID"))
							requestID = node.getTextContent();

						if (node.getNodeName().contains("venID")) {
							this.controller.setHASHED_VEN_NAME(node.getTextContent());
						}

					}

					post.setEntity(new StringEntity(new VENImpl().RegisterReport(new TimeFormat().getCurrentTime(),
							this.controller.getHASHED_VEN_NAME())));

					// POST
					response = client.execute(post);
					rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

					line = "";
					httpResponse = new StringBuilder();

					while ((line = rd.readLine()) != null) {
						// line = line.replace(" ", "");
						setHttpResponse(line);
					}

				}

				if (getHttpResponse().toString().contains("oadrRegisteredReport")) {

					post = new HttpPost(Global.vtnURL + "OadrPoll");

					post.setEntity(new StringEntity(new VENImpl().Poll(this.controller.getHASHED_VEN_NAME())));

					// POST
					response = client.execute(post);
					rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

					line = "";
					httpResponse = new StringBuilder();
					while ((line = rd.readLine()) != null) {
						// line = line.replace(" ", "");
						setHttpResponse(line);
					}

				}

				if (getHttpResponse().toString().contains("oadrRegisterReport")) {

					post = new HttpPost(Global.vtnURL + "EiReport");

					for (int i = 0; i < nodes.getLength(); i++) {
						Node node = nodes.item(i);
						if (node.getNodeName().contains("requestID"))
							requestID = node.getTextContent();

					}

					post.setEntity(new StringEntity(new VENImpl().RegisteredReport(this.controller.getHASHED_VEN_NAME(),
							requestID, "200", "OK")));

					// POST
					response = client.execute(post);
					rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

					line = "";
					httpResponse = new StringBuilder();
					while ((line = rd.readLine()) != null) {
						// line = line.replace(" ", "");
						setHttpResponse(line);
					}

				}

				if (getHttpResponse().toString().contains("oadrResponse")) {

					if (!this.controller.isRegistrationFlag()) {
						post = new HttpPost(Global.vtnURL + "EiEvent");

						for (int i = 0; i < nodes.getLength(); i++) {
							Node node = nodes.item(i);
							if (node.getNodeName().contains("requestID"))
								requestID = node.getTextContent();

						}

						post.setEntity(
								new StringEntity(new VENImpl().RequestEvent(this.controller.getHASHED_VEN_NAME())));

						// POST
						response = client.execute(post);
						rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

						line = "";
						httpResponse = new StringBuilder();
						while ((line = rd.readLine()) != null) {
							// line = line.replace(" ", "");
							setHttpResponse(line);
						}
					} else if (this.controller.isRegistrationFlag()) {

						try {
							Thread.sleep(Global.pollinginterval);

							post = new HttpPost(Global.vtnURL + "OadrPoll");

							post.setEntity(new StringEntity(new VENImpl().Poll(this.controller.getHASHED_VEN_NAME())));

							// POST
							response = client.execute(post);
							rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

							line = "";
							httpResponse = new StringBuilder();
							while ((line = rd.readLine()) != null) {
								// line = line.replace(" ", "");
								setHttpResponse(line);
							}

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}

				}

				if (getHttpUPDResponse().toString().contains("oadrUpdatedReport")) {

					new UpdateReport(this.controller).start();
					httpResponse_updateR = new StringBuilder();

				}

				if (getHttpResponse().toString().contains("oadrDistributeEvent")) {

//					System.out.println(getHttpResponse().toString());

					if (!this.controller.isRegistrationFlag()) {
						httpResponse = new StringBuilder();

						if (Global.pullModel) {
							post = new HttpPost(Global.vtnURL + "OadrPoll");
							post.setEntity(new StringEntity(new VENImpl().Poll(this.controller.getHASHED_VEN_NAME())));

							// POST
							response = client.execute(post);
							rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

							line = "";
							httpResponse = new StringBuilder();
							while ((line = rd.readLine()) != null) {
								setHttpResponse(line);
							}
						}
						post_updateReport = new HttpPost(Global.vtnURL + "EiReport");
						post_updateReport.setEntity(
								new StringEntity(new VENImpl().UpdateReport(this.controller.getHASHED_VEN_NAME())));

						// POST
						updateReport_response = client_updateReport.execute(post_updateReport);
						BufferedReader upd_rd = new BufferedReader(
								new InputStreamReader(updateReport_response.getEntity().getContent()));

						String upd_line = "";
						httpResponse_updateR = new StringBuilder();
						while ((upd_line = upd_rd.readLine()) != null) {
							setHttpUPDResponse(upd_line);
						}

						this.controller.setRegistrationFlag(true);

						if (!Global.pullModel) {

							new SendGet_EventPush(this.controller).start();

						}

					}

					else if (this.controller.isRegistrationFlag()) {

						DocumentBuilderFactory dbFactory_event = DocumentBuilderFactory.newInstance();
						DocumentBuilder dBuilder_event = dbFactory_event.newDocumentBuilder();
						Document doc_event = dBuilder_event
								.parse(new InputSource(new StringReader(getHttpResponse().toString())));
						NodeList nodes_event = doc_event.getDocumentElement().getElementsByTagNameNS("*", "*");

						double value = 0;
						String modificationNumber = "", eventID = "";
						for (int i = 0; i < nodes_event.getLength(); i++) {
							Node node = nodes_event.item(i);
							if (node.getNodeName().contains("value"))
								value = Double.parseDouble(node.getTextContent());
							if (node.getNodeName().contains("modificationNumber"))
								modificationNumber = node.getTextContent();
							if (node.getNodeName().contains("requestID"))
								requestID = node.getTextContent();
							if (node.getNodeName().contains("eventID")) {
								eventID = node.getTextContent();
							}
						}

						currentThresholdSet(value);

						
						post = new HttpPost(Global.vtnURL + "EiEvent");
						post.setEntity(new StringEntity(new VENImpl().CreatedEvent(this.controller.getHASHED_VEN_NAME(),
								"200", "OK", eventID, modificationNumber, requestID)));

						// POST
						response = client.execute(post);
						rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

						line = "";
						httpResponse = new StringBuilder();
						while ((line = rd.readLine()) != null) {
							setHttpResponse(line);
						}

						// double disVal = Global.currentVal /
						// Global.emaProtocolCoAP.keySet().size();
						//
						// Iterator<String> keys =
						// Global.emaProtocolCoAP.keySet().iterator();
						//
						// while (keys.hasNext()) {
						// String key = keys.next();
						//
						// Calendar now = Calendar.getInstance();
						//
						// int sYear = now.get(Calendar.YEAR);
						// int sMonth = now.get(Calendar.MONTH) + 1;
						// int sDate = now.get(Calendar.DATE);
						// String strYMD = sYear + "" + sMonth + "" + sDate;
						// int sHour = now.get(Calendar.HOUR_OF_DAY);
						// int sMin = now.get(Calendar.MINUTE);
						//
						// String sTime = sHour + "" + sMin + "" + "11";
						// String eTime = (sHour + 1) + "" + sMin + "" + "11";
						//
						//// // PULL MODEL
						//// if (Global.emaProtocolCoAP.get(key).isPullModel())
						// {
						//// Global.emaProtocolCoAP_EventFlag.get(key).setEventFlag(true)
						//// .setStartYMD(Integer.parseInt(strYMD)).setStartTime(Integer.parseInt(sTime))
						//// .setEndYMD(Integer.parseInt(strYMD)).setEndTime(Integer.parseInt(eTime))
						//// .setThreshold(disVal);
						//// }
						////
						//// // PUSH MODEL
						//// else if
						// (!Global.emaProtocolCoAP.get(key).isPullModel()) {
						////
						//// if
						// (Global.emaProtocolCoAP.get(key).getProtocol().equals("MQTT"))
						// {
						//// Global.initiater.eventOccur(key, 1,
						// Integer.parseInt(strYMD),
						//// Integer.parseInt(sTime), Integer.parseInt(strYMD),
						// Integer.parseInt(eTime),
						//// disVal);
						//// } else {
						////
						//// Global.obs_emaProtocolCoAP_EventFlag.get(key).setEventFlag(true)
						//// .setStartYMD(Integer.parseInt(strYMD))
						//// .setStartTime(Integer.parseInt(sTime + "11"))
						//// .setEndYMD(Integer.parseInt(strYMD))
						//// .setEndTime(Integer.parseInt(eTime +
						// "11")).setThreshold(disVal);
						////
						//// }
						//// }
						//
						// }

					}

				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// public boolean RDRrequest(String optID) {
	//
	// setSubPath("EiOpt");
	// HttpClientBuilder hcb = HttpClientBuilder.create();
	// HttpClient client = hcb.build();
	// HttpPost post = new HttpPost(Global.vtnURL + getSubPath());
	//
	// try {
	//
	// post.setEntity(new StringEntity(
	// new VENImpl().CreateOptSchedule("Emergency", "RdrRequest", new
	// TimeFormat().getCurrentTime(), optID)));
	// // POST
	// HttpResponse response = client.execute(post);
	//
	// // HttpResponse eventResponse;
	// BufferedReader rd = new BufferedReader(new
	// InputStreamReader(response.getEntity().getContent()));
	//
	// String line = "";
	// while ((line = rd.readLine()) != null) {
	// // line = line.replace(" ", "");
	// setHttpResponse(line);
	// }
	//
	// return true;
	//
	// } catch (Exception e) {
	//
	// return false;
	//
	// }
	//
	// }

	public void currentThresholdSet(double value) {

		System.out.println("GET Threshold Value" + value);

		// if (value < 80)
		// Global.currentVal = 0;
		// else if (value < 160 && value >= 80)
		// Global.currentVal = 80;
		// else if (value < 240 && value >= 160)
		// Global.currentVal = 160;
		// else if (value < 320 && value >= 240)
		// Global.currentVal = 240;
		// else if (value < 400 && value >= 320)
		// Global.currentVal = 320;
		// else if (value >= 400)
		// Global.currentVal = 400;

		new CurrentThresholdTimer(30).start();

	}

	public StringBuilder getHttpUPDResponse() {
		return httpResponse_updateR;
	}

	public HTTPRequest setHttpUPDResponse(String httpResponse) {
		this.httpResponse_updateR = this.httpResponse_updateR.append(httpResponse);
		return this;
	}

	public StringBuilder getHttpResponse() {
		return httpResponse;
	}

	public HTTPRequest setHttpResponse(String httpResponse) {
		this.httpResponse = this.httpResponse.append(httpResponse);
		return this;
	}

	public String getSubPath() {
		return subPath;
	}

	public HTTPRequest setSubPath(String subPath) {
		this.subPath = subPath;
		return this;
	}

	public StringBuilder getHttpResponse_event() {
		return httpResponse_event;
	}

	public void setHttpResponse_event(String httpResponse_event) {
		this.httpResponse_event = this.httpResponse_event.append(httpResponse_event);
	}

}
