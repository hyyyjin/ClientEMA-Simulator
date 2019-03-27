package com.mir.smartgrid.simulator.mqtt;

import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import com.mir.smartgrid.simulator.controller.Controller;
import com.mir.smartgrid.simulator.devProfile.DeviceProfile;
import com.mir.smartgrid.simulator.global.Global;
import com.mir.smartgrid.simulator.profile.emap.v2.PowerAttributes;
import com.mir.smartgrid.simulator.profile.emap.v2.ReportDescription;

public class Mqtt implements MqttCallback {

	enum Services {
		SessionSetup, Poll, Report, Opt, Event, Summary
	}

	enum SessionSetup {
		ConnectedRegistration, CreatedPartyRegistration, RegisteredReport, RegisterReport, Response, DistributeEvent, CanceledRegistration
	}

	enum Poll {
		Response, DistributeEvent
	}

	enum Report {
		UpdatedReport
	}

	enum Opt {
		CreatedOpt, CanceledOpt
	}

	// Private instance variables
	MqttClient client;
	public String brokerUrl;
	private boolean quietMode;
	public MqttConnectOptions conOpt;
	private boolean clean;
	private String password;
	private String userName;
	public int state;
	public String clientId;

	private String msgPayload;

	public Controller controller;

	public Mqtt(String brokerUrl, String clientId, boolean cleanSession, boolean quietMode, String userName,
			String password) throws MqttException {
		this.brokerUrl = brokerUrl;
		this.quietMode = quietMode;
		this.clean = cleanSession;
		this.password = password;
		this.userName = userName;
		this.state = 0;
		this.clientId = clientId;

		String tmpDir = System.getProperty("java.io.tmpdir");
		MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

		try {

			conOpt = new MqttConnectOptions();
			conOpt.setCleanSession(clean);
			if (password != null) {
				conOpt.setPassword(this.password.toCharArray());
			}
			if (userName != null) {
				conOpt.setUserName(this.userName);
			}

			client = new MqttClient(this.brokerUrl, clientId, dataStore);
			client.setCallback(this);

		} catch (MqttException e) {
			e.printStackTrace();
			log("Unable to set up client: " + e.toString());
			System.exit(1);
		}
	}

	public Mqtt(String brokerUrl, String clientId, boolean cleanSession, boolean quietMode, String userName,
			String password, Controller controller) throws MqttException {
		this.brokerUrl = brokerUrl;
		this.quietMode = quietMode;
		this.clean = cleanSession;
		this.password = password;
		this.userName = userName;
		this.state = 0;
		this.clientId = clientId;

		this.controller = controller;

		String tmpDir = System.getProperty("java.io.tmpdir");
		MqttDefaultFilePersistence dataStore = new MqttDefaultFilePersistence(tmpDir);

		try {

			conOpt = new MqttConnectOptions();
			conOpt.setCleanSession(clean);
			if (password != null) {
				conOpt.setPassword(this.password.toCharArray());
			}
			if (userName != null) {
				conOpt.setUserName(this.userName);
			}

			client = new MqttClient(this.brokerUrl, clientId, dataStore);
			client.setCallback(this);

		} catch (MqttException e) {
			e.printStackTrace();
			log("Unable to set up client: " + e.toString());
			System.exit(1);
		}
	}

	public void subscribe(String topicName, int qos) throws MqttException {
		client.connect(conOpt);

		client.subscribe(topicName, qos);

	}

	private void log(String message) {
		if (!quietMode) {
			// System.out.println(message);
		}
	}

	public void connectionLost(Throwable cause) {

		log("Connection to " + brokerUrl + " lost! " + cause);
		System.exit(1);
	}

	public void deliveryComplete(IMqttDeliveryToken token) {

	}

	public void messageArrived(String topic, MqttMessage message) throws MqttException, InterruptedException,
			ParseException, ParserConfigurationException, SAXException, IOException, JSONException {

//		 String time = new Timestamp(System.currentTimeMillis()).toString();
//		 System.out.println("Time:\t" + time + " Topic:\t" + topic +
//		 "Message:\t" + new String(message.getPayload())
//		 + " QoS:\t" + message.getQos());
		msgPayload = new String(message.getPayload());
		String[] topicParse = topic.split("/");

		// JSON
		if (msgPayload.startsWith("{") && msgPayload.endsWith("}")) {

			JSONObject msg_json = new JSONObject(msgPayload);

			if (topicParse[1].equals("EMAP") && topicParse[2].equals(this.controller.getEmaID())) {

				String profileVersion = "EMAP1.0b";

				if (msg_json.getString("DestEMA").equals(this.controller.getEmaID())) {

					String procedure = msg_json.getString("service");

					Services services = Services.valueOf(topicParse[4]);
					switch (services) {
					case SessionSetup:
						try {
							sessionSetup(procedure, profileVersion);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case Poll:
						try {
							poll(procedure, profileVersion);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case Report:
						report(procedure, profileVersion);
						break;
					case Opt:
						opt(procedure, profileVersion);
						break;
					case Event:
						poll(procedure, profileVersion);
						break;
					case Summary:
						summaryReport(procedure, profileVersion);
						break;
					
					}
				}

			}

			else if (topicParse[1].equals("OpenADR") && topicParse[2].equals(this.controller.getEmaID())) {

				String profileVersion = "OpenADR2.0b";

				String procedure = msg_json.getString("service");

				procedure = procedure.replaceAll("oadr", "");

				if (procedure
						.matches("CreatedPartyRegistration|RegisteredReport|RegisterReport|Response|DistributeEvent")) {

					topicParse[4] = "SessionSetup";

					if (procedure.equals("CreatedPartyRegistration")) {

						if (msg_json.getString("registrationID").length() < 3) {
							procedure = "ConnectedRegistration";
						} else {
							procedure = "CreatedPartyRegistration";
						}

					}

					if (procedure.equals("Response")) {

						if (this.controller.getVenRegisterSeqNumber() == 0) {

							topicParse[4] = "SessionSetup";
						} else {
							topicParse[4] = "Poll";
						}

					}

					if (procedure.equals("DistributeEvent")) {

						String eventFlag = "";
						JSONArray jsonArr = new JSONArray(msg_json.getString("event"));

						for (int i = 0; i < jsonArr.length(); i++) {

							JSONObject subJson = new JSONObject(jsonArr.get(i).toString());
							eventFlag = subJson.getString("vtnComment");

						}

						if (eventFlag.equals("SessionSetup")) {

							topicParse[4] = "SessionSetup";

						} else {

							topicParse[4] = "Event";

						}

					}

				} else if (procedure.matches("UpdatedReport")) {

					topicParse[4] = "Report";

				} else if (procedure.matches("CreatedOpt")) {

					topicParse[4] = "Opt";

				}

				Services services = Services.valueOf(topicParse[4]);
				switch (services) {
				case SessionSetup:
					try {
						sessionSetup(procedure, profileVersion);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case Poll:
					try {
						poll(procedure, profileVersion);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case Report:
					report(procedure, profileVersion);
					break;
				case Opt:
					opt(procedure, profileVersion);
					break;
				case Event:
					poll(procedure, profileVersion);
					break;
				}

			}

		}

		// XML
		else if (msgPayload.startsWith("<") && msgPayload.endsWith(">") || msgPayload.contains("xmlns")) {

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(msgPayload)));

			String profileType = null, deviceID = null,
					microgridType = doc.getDocumentElement().getNodeName().split(Pattern.quote(":"))[1];
			int nameSpaceNum = 0, tagSize = 0;

			nameSpaceNum = getNameSpaceNum(doc);

			if (nameSpaceNum > 0) {
				deviceID = this.controller.getEmaID()
						+ doc.getElementsByTagName("ns" + nameSpaceNum + ":logicalDeviceID").item(0).getTextContent()
								.split(Pattern.quote("."))[2];
			}

			if (Arrays.stream(Global.MICROGRIDPROFILE).anyMatch(microgridType::equals)) {

				profileType = Global.MICROGRIDPROFILETYPE[0];
				tagSize = doc.getElementsByTagName("ns" + nameSpaceNum + ":unit").getLength();

				classifier(profileType, microgridType, deviceID, nameSpaceNum, tagSize, doc);

			} else if (Arrays.stream(Global.MICROGRIDEVENT).anyMatch(microgridType::equals)) {

				profileType = Global.MICROGRIDPROFILETYPE[1];
				classifier(profileType, microgridType, deviceID, nameSpaceNum, tagSize, doc);

			}

		}
	}

	public void classifier(String profileType, String microgridType, String deviceID, int nsNum, int tagSize,
			Document doc) {

		DeviceProfile deviceProfile = null;

		if (profileType.equals(Global.MICROGRIDPROFILETYPE[0])) {
			double[] values = new double[tagSize];

			for (int i = 0; i < tagSize; i++) {
				values[i] = Double
						.parseDouble(doc.getElementsByTagName("ns" + nsNum + ":value").item(i).getTextContent());
			}
			// Re-closer & Battery Reading Profile
			if (microgridType.matches(Global.MICROGRIDPROFILE[1] + "|" + Global.MICROGRIDPROFILE[2])) {

				microgridType = microgridType.replaceAll("ReadingProfile", "");

				deviceProfile = new DeviceProfile(this.controller.getEmaID(), deviceID, microgridType, null, 0, 0, 0,
						values[0], 0, values[1], values[2], 0, 0, new Date(System.currentTimeMillis()));
			}
			// Solar Reading Profile
			else if (microgridType.equals(Global.MICROGRIDPROFILE[0])) {

				microgridType = microgridType.replaceAll("ReadingProfile", "");

				deviceProfile = new DeviceProfile(this.controller.getEmaID(), deviceID, microgridType, "null", 0, 0, 0,
						values[0], 0, 0, 0, 0, 0, new Date(System.currentTimeMillis()));
			}

		}

		else if (profileType.equals(Global.MICROGRIDPROFILETYPE[1])) {

			// Battery Event Profile
			if (microgridType.equals(Global.MICROGRIDEVENT[0])) {
				int eventNameSpaceNum = getBatteryEventNameSpaceNum(doc);
				double soc;
				String state;

				soc = Double.parseDouble(
						doc.getElementsByTagName("ns" + eventNameSpaceNum + ":stateOfCharge").item(0).getTextContent());
				state = doc.getElementsByTagName("ns" + eventNameSpaceNum + ":isCharging").item(0).getTextContent();

				double power = 0, volt = 0, hz = 0;
				if (Global.devProfile.containsKey(deviceID)) {
					power = Global.devProfile.get(deviceID).getPower();
					volt = Global.devProfile.get(deviceID).getVolt();
					hz = Global.devProfile.get(deviceID).getHz();
				}

				// mode : 0, 1, 2 0: Maintain Minimum Battery SoC, 1: islanded
				// 2: what?

				microgridType = microgridType.replaceAll("EventProfile", "");

				deviceProfile = new DeviceProfile(this.controller.getEmaID(), deviceID, microgridType, state, 0, 0, 0,
						power, Global.BATTERCAPACITY, volt, hz, (Global.BATTERCAPACITY * soc) / 100, soc,
						new Date(System.currentTimeMillis()));
			}

			// Re-closer Event Profile
			else if (microgridType.equals(Global.MICROGRIDEVENT[1])) {
				int eventNameSpaceNum = getRecloserEventNameSpaceNum(doc);
				String swtichState;
				swtichState = doc.getElementsByTagName("ns" + eventNameSpaceNum + ":switchStatus").item(0)
						.getTextContent();

				double power = 0, volt = 0, hz = 0;
				if (Global.devProfile.containsKey(deviceID)) {
					power = Global.devProfile.get(deviceID).getPower();
					volt = Global.devProfile.get(deviceID).getVolt();
					hz = Global.devProfile.get(deviceID).getHz();
				}

				microgridType = microgridType.replaceAll("EventProfile", "");

				deviceProfile = new DeviceProfile(this.controller.getEmaID(), deviceID, microgridType, swtichState, 0,
						0, 0, power, 0, volt, hz, 0, 0, new Date(System.currentTimeMillis()));
			}
		}

		if (Global.devProfile.containsKey(deviceID))
			Global.devProfile.replace(deviceID, deviceProfile);
		else
			Global.devProfile.put(deviceID, deviceProfile);

	}

	public int getNameSpaceNum(Document doc) {

		int nameSpaceNum = -1, maxNameSpaceNum = 20;

		for (int i = 0; i <= maxNameSpaceNum; i++) {
			try {
				doc.getElementsByTagName("ns" + i + ":logicalDeviceID").item(0).getTextContent();
				return nameSpaceNum = i;
			} catch (Exception e) {
			}

		}
		return nameSpaceNum;
	}

	public int getRecloserEventNameSpaceNum(Document doc) {

		int nameSpaceNum = -1, maxNameSpaceNum = 20;
		for (int i = 0; i <= maxNameSpaceNum; i++) {
			try {
				doc.getElementsByTagName("ns" + i + ":switchStatus").item(0).getTextContent();
				return nameSpaceNum = i;
			} catch (Exception e) {
			}
		}
		return nameSpaceNum;
	}

	public int getBatteryEventNameSpaceNum(Document doc) {

		int nameSpaceNum = -1, maxNameSpaceNum = 20;
		for (int i = 0; i <= maxNameSpaceNum; i++) {
			try {
				doc.getElementsByTagName("ns" + i + ":isCharging").item(0).getTextContent();
				return nameSpaceNum = i;
			} catch (Exception e) {
			}
		}
		return nameSpaceNum;
	}

	public void sessionSetup(String procedure, String profileVersion) throws JSONException, InterruptedException {
		String setPayload = "";
		SessionSetup sessionSetup = SessionSetup.valueOf(procedure);
		JSONObject jsonParse = new JSONObject(msgPayload);
		double generate = 0, storage = 0, power = 0;

		switch (sessionSetup) {
		case ConnectedRegistration:

			if (profileVersion.equals("EMAP1.0b")) {

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

				String topic = "/EMAP/" + Global.getParentnNodeID() + "/1.0b/SessionSetup";
				setPayload = cp.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

			}

			else if (profileVersion.equals("OpenADR2.0b")) {

				com.mir.smartgrid.simulator.profile.openadr.CreatePartyRegistration cp = new com.mir.smartgrid.simulator.profile.openadr.CreatePartyRegistration();

				cp.setHttpPullModel(this.controller.isPullModel());

				cp.setProfileName("OpenADR2.0b");
				cp.setReportOnly(false);
				cp.setRequestID("requestID");
				cp.setService("oadrCreatePartyRegistration");
				cp.setSrcEMA(this.controller.getEmaID());
				// cp.setTime(this.connection.getCurrentTime(System.currentTimeMillis()));
				cp.setTransportName("MQTT");
				cp.setXmlSignature(true);

				String topic = "/OpenADR/" + Global.getParentnNodeID() + "/2.0b/EiRegisterParty";
				setPayload = cp.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

			}

			break;

		case CreatedPartyRegistration:

			if (profileVersion.equals("EMAP1.0b")) {

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
							this.controller.getReportType(), "Wh", "siScaleCode", "marketContext", "minPeriod",
							"maxPeriod", false, "itemDescription", pa.getPowerAttributesParams(), "state",
							"Controllable", this.controller.getCurrentPower(), -1, 0, this.controller.getGenerate(),
							this.controller.getStorage(), this.controller.getMaxValue(), this.controller.getMinValue(),
							this.controller.getAvgValue(), this.controller.getCurrentTime(System.currentTimeMillis()),
							this.controller.getCurrentTime(System.currentTimeMillis()),
							this.controller.getCurrentTime(System.currentTimeMillis()), 9);

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

							rd.addReportDescriptionParams(key, key, "LED", this.controller.getReportType(), "Wh",
									"siScaleCode", "marketContext", "minPeriod", "maxPeriod", false, "itemDescription",
									pa.getPowerAttributesParams(), "state", "Controllable",
									Global.devProfile.get(key).getPower(), Global.devProfile.get(key).getDimming(), 0,
									Global.devProfile.get(key).getGenerate(), Global.devProfile.get(key).getStorage(),
									Global.devProfile.get(key).getMaxValue(), Global.devProfile.get(key).getMinValue(),
									Global.devProfile.get(key).getAvgValue(),
									this.controller.getCurrentTime(System.currentTimeMillis()),
									this.controller.getCurrentTime(System.currentTimeMillis()),
									this.controller.getCurrentTime(System.currentTimeMillis()), 9);
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
							this.controller.getReportType(), "Wh", "siScaleCode", "marketContext", "minPeriod",
							"maxPeriod", false, "itemDescription", pa.getPowerAttributesParams(), "state",
							"Controllable", this.controller.getCurrentPower(), -1, 0, this.controller.getGenerate(),
							this.controller.getStorage(), this.controller.getMaxValue(), this.controller.getMinValue(),
							this.controller.getAvgValue(), this.controller.getCurrentTime(System.currentTimeMillis()),
							this.controller.getCurrentTime(System.currentTimeMillis()),
							this.controller.getCurrentTime(System.currentTimeMillis()), 9);

				}

				com.mir.smartgrid.simulator.profile.emap.v2.Report report = new com.mir.smartgrid.simulator.profile.emap.v2.Report();
				report.addReportParams("duration", rd.getReportDescriptionParams(), "reportRequestID",
						"reportSpecifierID", "reportName", this.controller.getCurrentTime(System.currentTimeMillis()));

				com.mir.smartgrid.simulator.profile.emap.v2.RegisterReport rt = new com.mir.smartgrid.simulator.profile.emap.v2.RegisterReport();
				rt.setDestEMA(Global.getParentnNodeID());
				rt.setReport(report.getReportParams());
				rt.setRequestID("requestID");
				rt.setService("RegisterReport");
				rt.setSrcEMA(this.controller.getEmaID());
				rt.setType(Global.reportType);
				rt.setTime(this.controller.getCurrentTime(System.currentTimeMillis()));

				String topic = "/EMAP/" + Global.getParentnNodeID() + "/1.0b/SessionSetup";
				setPayload = rt.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

			}

			else if (profileVersion.equals("OpenADR2.0b")) {

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
					rd.addReportDescriptionParams(this.controller.getEmaID(), this.controller.getEmaID(),
							Global.reportType, "Wh", "siScaleCode", "marketContext", "minPeriod", "maxPeriod",
							false, "itemDescription",
							new PowerAttributes().addPowerAttributesParams(0, power, 0).getPowerAttributesParams());

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

							rd.addReportDescriptionParams(key, key, Global.reportType, "Wh", "siScaleCode",
									"marketContext", "minPeriod", "maxPeriod", false, "itemDescription",
									new PowerAttributes().addPowerAttributesParams(0, power, 0)
											.getPowerAttributesParams());
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

					rd.addReportDescriptionParams(this.controller.getEmaID(), this.controller.getEmaID(),
							Global.reportType, "Wh", "siScaleCode", "marketContext", "minPeriod", "maxPeriod",
							false, "itemDescription",
							new PowerAttributes().addPowerAttributesParams(0, power, 0).getPowerAttributesParams());

				}

				com.mir.smartgrid.simulator.profile.openadr.Report report = new com.mir.smartgrid.simulator.profile.openadr.Report();
				report.addReportParams("duration", rd.getReportDescriptionParams(), "reportRequestID",
						"reportSpecifierID", "reportName", "createdDateTime");

				com.mir.smartgrid.simulator.profile.openadr.RegisterReport rt = new com.mir.smartgrid.simulator.profile.openadr.RegisterReport();
				rt.setSrcEMA(this.controller.getEmaID());
				rt.setReport(report.getReportParams());
				rt.setRequestID("requestID");
				rt.setService("oadrRegisterReport");

				String topic = "/OpenADR/" + Global.getParentnNodeID() + "/2.0b/EiReport";
				setPayload = rt.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

			}

			break;
		case RegisteredReport:

			if (profileVersion.equals("EMAP1.0b")) {

				com.mir.smartgrid.simulator.profile.emap.v2.Poll poll = new com.mir.smartgrid.simulator.profile.emap.v2.Poll();
				poll.setDestEMA(Global.getParentnNodeID());
				poll.setService("Poll");
				poll.setSrcEMA(this.controller.getEmaID());
				poll.setTime(this.controller.getCurrentTime(System.currentTimeMillis()));

				String topic = "/EMAP/" + Global.getParentnNodeID() + "/1.0b/SessionSetup";
				setPayload = poll.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

			}

			else if (profileVersion.equals("OpenADR2.0b")) {

				com.mir.smartgrid.simulator.profile.openadr.Poll poll = new com.mir.smartgrid.simulator.profile.openadr.Poll();

				poll.setService("oadrPoll");
				poll.setVenID(this.controller.getEmaID());

				String topic = "/OpenADR/" + Global.getParentnNodeID() + "/2.0b/OadrPoll";
				setPayload = poll.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

			}

			break;
		case RegisterReport:

			if (profileVersion.equals("EMAP1.0b")) {

				com.mir.smartgrid.simulator.profile.emap.v2.RegisteredReport rdt = new com.mir.smartgrid.simulator.profile.emap.v2.RegisteredReport();
				rdt.setDestEMA(Global.getParentnNodeID());
				rdt.setRequestID("requestID");
				rdt.setResponseCode(200);
				rdt.setResponseDescription("OK");
				rdt.setService("RegisteredReport");
				rdt.setSrcEMA(this.controller.getEmaID());
				rdt.setTime(this.controller.getCurrentTime(System.currentTimeMillis()));

				String topic = "/EMAP/" + Global.getParentnNodeID() + "/1.0b/SessionSetup";
				setPayload = rdt.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

			}

			else if (profileVersion.equals("OpenADR2.0b")) {

				com.mir.smartgrid.simulator.profile.openadr.RegisteredReport rdt = new com.mir.smartgrid.simulator.profile.openadr.RegisteredReport();
				rdt.setRequestID("requestID");
				rdt.setResponseCode(200);
				rdt.setResponseDescription("OK");
				rdt.setService("oadrRegisteredReport");
				rdt.setVenID(this.controller.getEmaID());

				String topic = "/OpenADR/" + Global.getParentnNodeID() + "/2.0b/EiReport";
				setPayload = rdt.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

			}

			break;
		case Response:

			if (profileVersion.equals("EMAP1.0b")) {

				com.mir.smartgrid.simulator.profile.emap.v2.RequestEvent requestEvent = new com.mir.smartgrid.simulator.profile.emap.v2.RequestEvent();
				requestEvent.setDestEMA(Global.getParentnNodeID());
				requestEvent.setRequestID("requestID");
				requestEvent.setService("RequestEvent");
				requestEvent.setSrcEMA(this.controller.getEmaID());
				requestEvent.setTime(this.controller.getCurrentTime(System.currentTimeMillis()));

				String topic = "/EMAP/" + Global.getParentnNodeID() + "/1.0b/SessionSetup";
				setPayload = requestEvent.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

			}

			else if (profileVersion.equals("OpenADR2.0b")) {

				com.mir.smartgrid.simulator.profile.openadr.RequestEvent requestEvent = new com.mir.smartgrid.simulator.profile.openadr.RequestEvent();
				requestEvent.setVenID(this.controller.getEmaID());
				requestEvent.setRequestID("requestID");
				requestEvent.setService("oadrRequestEvent");

				String topic = "/OpenADR/" + Global.getParentnNodeID() + "/2.0b/EiEvent";
				setPayload = requestEvent.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());
				this.controller.setVenRegisterSeqNumber(1);

			}

			break;
		case DistributeEvent:

			// Wait for 1sec
			Thread.sleep(1000);

			if (profileVersion.equals("EMAP1.0b")) {

				double threshold = 0;

				JSONArray jsonArr = new JSONArray(jsonParse.getString("event"));

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

				String topic = "/EMAP/" + Global.getParentnNodeID() + "/1.0b/Poll";

				if (this.controller.isPullModel()) {
					com.mir.smartgrid.simulator.profile.emap.v2.Poll poll = new com.mir.smartgrid.simulator.profile.emap.v2.Poll();
					poll.setDestEMA(Global.getParentnNodeID());
					poll.setService("Poll");
					poll.setSrcEMA(this.controller.getEmaID());
					poll.setTime(this.controller.getCurrentTime(System.currentTimeMillis()));

					setPayload = poll.toString();
					new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

				}

				PowerAttributes pa = new PowerAttributes();
				pa.addPowerAttributesParams(200.1, 300.1, 400.1);

				ReportDescription rd = new ReportDescription();

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
							this.controller.getReportType(), "Wh", "siScaleCode", "marketContext", "minPeriod",
							"maxPeriod", false, "itemDescription", pa.getPowerAttributesParams(), "state",
							"Controllable", this.controller.getCurrentPower(), -1, this.controller.getThreshold(),
							this.controller.getGenerate(), this.controller.getStorage(), this.controller.getMaxValue(),
							this.controller.getMinValue(), this.controller.getAvgValue(),
							this.controller.getCurrentTime(System.currentTimeMillis()),
							this.controller.getCurrentTime(System.currentTimeMillis()),
							this.controller.getCurrentTime(System.currentTimeMillis()), 9);

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

							rd.addReportDescriptionParams(key, key, "LED", this.controller.getReportType(), "Wh",
									"siScaleCode", "marketContext", "minPeriod", "maxPeriod", false, "itemDescription",
									pa.getPowerAttributesParams(), "state", "Controllable",
									Global.devProfile.get(key).getPower(), Global.devProfile.get(key).getDimming(), 0,
									Global.devProfile.get(key).getGenerate(), Global.devProfile.get(key).getStorage(),
									Global.devProfile.get(key).getMaxValue(), Global.devProfile.get(key).getMinValue(),
									Global.devProfile.get(key).getAvgValue(),
									this.controller.getCurrentTime(System.currentTimeMillis()),
									this.controller.getCurrentTime(System.currentTimeMillis()),
									this.controller.getCurrentTime(System.currentTimeMillis()), 9);
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
							this.controller.getReportType(), "Wh", "siScaleCode", "marketContext", "minPeriod",
							"maxPeriod", false, "itemDescription", pa.getPowerAttributesParams(), "state",
							"Controllable", this.controller.getCurrentPower(), -1, this.controller.getThreshold(),
							this.controller.getGenerate(), this.controller.getStorage(), this.controller.getMaxValue(),
							this.controller.getMinValue(), this.controller.getAvgValue(),
							this.controller.getCurrentTime(System.currentTimeMillis()),
							this.controller.getCurrentTime(System.currentTimeMillis()),
							this.controller.getCurrentTime(System.currentTimeMillis()), 9);

				}

				com.mir.smartgrid.simulator.profile.emap.v2.Report report = new com.mir.smartgrid.simulator.profile.emap.v2.Report();
				report.addReportParams("duration", rd.getReportDescriptionParams(), "reportRequestID",
						"reportSpecifierID", "reportName", this.controller.getCurrentTime(System.currentTimeMillis()));

				com.mir.smartgrid.simulator.profile.emap.v2.RegisterReport rt = new com.mir.smartgrid.simulator.profile.emap.v2.RegisterReport();
				rt.setDestEMA(Global.getParentnNodeID());
				rt.setReport(report.getReportParams());
				rt.setRequestID("requestID");
				rt.setService("UpdateReport");
				rt.setSrcEMA(this.controller.getEmaID());
				rt.setType(Global.reportType);
				rt.setTime(this.controller.getCurrentTime(System.currentTimeMillis()));

				topic = "/EMAP/" + Global.getParentnNodeID() + "/1.0b/Report";
				setPayload = rt.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

			}

			else if (profileVersion.equals("OpenADR2.0b")) {

				double threshold = 0;

				JSONArray jsonArr = new JSONArray(jsonParse.getString("event"));

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

				System.out.println("SET THRESHOLD" + threshold);
				this.controller.setThreshold(threshold);

				String topic = "";
				if (this.controller.isPullModel()) {

					com.mir.smartgrid.simulator.profile.openadr.Poll poll = new com.mir.smartgrid.simulator.profile.openadr.Poll();

					poll.setService("oadrPoll");
					poll.setVenID(this.controller.getEmaID());

					topic = "/OpenADR/" + Global.getParentnNodeID() + "/2.0b/OadrPoll";
					setPayload = poll.toString();
					new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());
				}

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
					rd.addReportDescriptionParams(this.controller.getEmaID(), this.controller.getEmaID(),
							Global.reportType, "Wh", "siScaleCode", "marketContext", "minPeriod", "maxPeriod",
							false, "itemDescription",
							new PowerAttributes().addPowerAttributesParams(0, power, 0).getPowerAttributesParams());

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

							rd.addReportDescriptionParams(key, key, Global.reportType, "Wh", "siScaleCode",
									"marketContext", "minPeriod", "maxPeriod", false, "itemDescription",
									new PowerAttributes().addPowerAttributesParams(0, power, 0)
											.getPowerAttributesParams());
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

					rd.addReportDescriptionParams(this.controller.getEmaID(), this.controller.getEmaID(),
							Global.reportType, "Wh", "siScaleCode", "marketContext", "minPeriod", "maxPeriod",
							false, "itemDescription",
							new PowerAttributes().addPowerAttributesParams(0, power, 0).getPowerAttributesParams());

				}

				com.mir.smartgrid.simulator.profile.openadr.Report report = new com.mir.smartgrid.simulator.profile.openadr.Report();
				report.addReportParams("duration", rd.getReportDescriptionParams(), "reportRequestID",
						"reportSpecifierID", "reportName", this.controller.getCurrentTime(System.currentTimeMillis()));

				com.mir.smartgrid.simulator.profile.openadr.RegisterReport rt = new com.mir.smartgrid.simulator.profile.openadr.RegisterReport();
				rt.setSrcEMA(this.controller.getEmaID());
				rt.setReport(report.getReportParams());
				rt.setRequestID("requestID");
				rt.setService("oadrUpdateReport");

				topic = "/OpenADR/" + Global.getParentnNodeID() + "/2.0b/EiReport";
				setPayload = rt.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

			}

			break;
		case CanceledRegistration:
			// break;
		}
	}

	public void poll(String procedure, String profileVersion) throws JSONException, InterruptedException {

		JSONObject jsonParse = new JSONObject(msgPayload);
		String setPayload = null;
		Poll poll = Poll.valueOf(procedure);
		switch (poll) {
		case Response:

			if (profileVersion.equals("EMAP1.0b")) {

				if (this.controller.isPullModel()) {

					Thread.sleep(Global.pollinginterval);

					com.mir.smartgrid.simulator.profile.emap.v2.Poll pollMsg = new com.mir.smartgrid.simulator.profile.emap.v2.Poll();
					pollMsg.setDestEMA(Global.getParentnNodeID());
					pollMsg.setService("Poll");
					pollMsg.setSrcEMA(this.controller.getEmaID());
					pollMsg.setTime(this.controller.getCurrentTime(System.currentTimeMillis()));

					String topic = "/EMAP/" + Global.getParentnNodeID() + "/1.0b/Poll";

					setPayload = pollMsg.toString();
					new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());
				}
			}

			if (profileVersion.equals("OpenADR2.0b")) {

				if (this.controller.isPullModel()) {

					Thread.sleep(Global.pollinginterval);

					com.mir.smartgrid.simulator.profile.openadr.Poll pollMsg = new com.mir.smartgrid.simulator.profile.openadr.Poll();
					pollMsg.setService("oadrPoll");
					pollMsg.setVenID(this.controller.getEmaID());

					String topic = "/OpenADR/" + Global.getParentnNodeID() + "/2.0b/OadrPoll";
					setPayload = pollMsg.toString();
					new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

				}
			}

			break;
		case DistributeEvent:

			if (profileVersion.equals("EMAP1.0b")) {

				double threshold = 0;

				JSONArray jsonArr = new JSONArray(jsonParse.getString("event"));

				for (int i = 0; i < jsonArr.length(); i++) {

					JSONObject subJson = new JSONObject(jsonArr.get(i).toString());

					JSONArray subJsonArr = new JSONArray(subJson.getString("eventSignals"));

					for (int j = 0; j < subJsonArr.length(); j++) {

						JSONObject subJson2 = new JSONObject(subJsonArr.get(i).toString());

						threshold = subJson2.getDouble("threshold");
					}

				}

				this.controller.setThreshold(threshold);

				System.out.println("==================================");
				System.out.println("EMA ID" + this.controller.getEmaID());
				System.out.println("EVENT RECV" + threshold);
				System.out.println("CURRENT VAL" + this.controller.getCurrentPower());
				System.out.println("==================================");

				this.controller.setReportCnt(4);

				if (!this.controller.isPullModel()) {
					com.mir.smartgrid.simulator.profile.emap.v2.Response res = new com.mir.smartgrid.simulator.profile.emap.v2.Response();

					res.setDestEMA(Global.getParentnNodeID());
					res.setRequestID("requestID");
					res.setResponseCode(200);
					res.setResponseDescription("OK");
					res.setService("Response");
					res.setSrcEMA(this.controller.getEmaID());
					res.setTime(new Date(System.currentTimeMillis()).toString());

					String topic = "/EMAP/" + Global.getParentnNodeID() + "/1.0b/Event";
					setPayload = res.toString();
					new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

				}

				com.mir.smartgrid.simulator.profile.emap.v2.CreatedEvent cde = new com.mir.smartgrid.simulator.profile.emap.v2.CreatedEvent();

				cde.setDestEMA(Global.getParentnNodeID());
				cde.setEventID("eventID");
				cde.setModificationNumber(1);
				cde.setOptType("optIn");
				cde.setRequestID("requestID");
				cde.setResponseCode(200);
				cde.setResponseDescription("OK");
				cde.setService("CreatedEvent");
				cde.setSrcEMA(this.controller.getEmaID());
				cde.setTime(new Date(System.currentTimeMillis()).toString());

				String topic = "/EMAP/" + Global.getParentnNodeID() + "/1.0b/Event";
				setPayload = cde.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());
			}

			else if (profileVersion.equals("OpenADR2.0b")) {

				double threshold = 0;

				JSONArray jsonArr = new JSONArray(jsonParse.getString("event"));

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

				System.out.println("==================================");
				System.out.println("EMA ID" + this.controller.getEmaID());
				System.out.println("EVENT RECV" + threshold);
				System.out.println("CURRENT VAL" + this.controller.getCurrentPower());
				System.out.println("==================================");
				this.controller.setReportCnt(4);

				if (!this.controller.isPullModel()) {
					com.mir.smartgrid.simulator.profile.openadr.Response res = new com.mir.smartgrid.simulator.profile.openadr.Response();

					res.setDestEMA(this.controller.getEmaID());
					res.setRequestID("requestID");
					res.setResponseCode(200);
					res.setResponseDescription("OK");
					res.setService("oadrResponse");

					String topic = "/OpenADR/" + Global.getParentnNodeID() + "/2.0b/EiEvent";
					setPayload = res.toString();
					new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());
				}

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

				String topic = "/OpenADR/" + Global.getParentnNodeID() + "/2.0b/EiEvent";
				setPayload = cde.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());
			}

			break;
		}

	}

	public void report(String procedure, String profileVersion) throws InterruptedException, JSONException {

		JSONObject jsonParse = new JSONObject(msgPayload);
		String setPayload = null;
		Report report = Report.valueOf(procedure);
		double generate = 0, storage = 0, power = 0;

		switch (report) {
		case UpdatedReport:
			if (profileVersion.equals("EMAP1.0b")) {

				Thread.sleep(Global.reportinterval);

				// Global.reportType = jsonParse.getString("type");

				// Explicit 일 경우

				PowerAttributes pa = new PowerAttributes();
				pa.addPowerAttributesParams(200.1, 300.1, 400.1);

				ReportDescription rd = new ReportDescription();

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
							this.controller.getReportType(), "Wh", "siScaleCode", "marketContext", "minPeriod",
							"maxPeriod", false, "itemDescription", pa.getPowerAttributesParams(), "state",
							"Controllable", this.controller.getCurrentPower(), -1, this.controller.getThreshold(),
							this.controller.getGenerate(), this.controller.getStorage(), this.controller.getMaxValue(),
							this.controller.getMinValue(), this.controller.getAvgValue(),
							this.controller.getCurrentTime(System.currentTimeMillis()),
							this.controller.getCurrentTime(System.currentTimeMillis()),
							this.controller.getCurrentTime(System.currentTimeMillis()), 9);

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
							
//							System.out.println(Global.devProfile.get(key).getState());
							
							rd.addReportDescriptionParams(key, key, "LED", this.controller.getReportType(), "Wh",
									"siScaleCode", "marketContext", "minPeriod", "maxPeriod", false, "itemDescription",
									pa.getPowerAttributesParams(), Global.devProfile.get(key).getState(), "Controllable",
									Global.devProfile.get(key).getPower(), Global.devProfile.get(key).getDimming(), 0,
									Global.devProfile.get(key).getGenerate(), Global.devProfile.get(key).getStorage(),
									Global.devProfile.get(key).getMaxValue(), Global.devProfile.get(key).getMinValue(),
									Global.devProfile.get(key).getAvgValue(),
									this.controller.getCurrentTime(System.currentTimeMillis()),
									this.controller.getCurrentTime(System.currentTimeMillis()),
									this.controller.getCurrentTime(System.currentTimeMillis()), 9);
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
							this.controller.getReportType(), "Wh", "siScaleCode", "marketContext", "minPeriod",
							"maxPeriod", false, "itemDescription", pa.getPowerAttributesParams(), "state",
							"Controllable", this.controller.getCurrentPower(), -1, this.controller.getThreshold(),
							this.controller.getGenerate(), this.controller.getStorage(), this.controller.getMaxValue(),
							this.controller.getMinValue(), this.controller.getAvgValue(),
							this.controller.getCurrentTime(System.currentTimeMillis()),
							this.controller.getCurrentTime(System.currentTimeMillis()),
							this.controller.getCurrentTime(System.currentTimeMillis()), 9);

				}

				com.mir.smartgrid.simulator.profile.emap.v2.Report rport = new com.mir.smartgrid.simulator.profile.emap.v2.Report();
				rport.addReportParams("duration", rd.getReportDescriptionParams(), "reportRequestID",
						"reportSpecifierID", "reportName", this.controller.getCurrentTime(System.currentTimeMillis()));

				com.mir.smartgrid.simulator.profile.emap.v2.RegisterReport rt = new com.mir.smartgrid.simulator.profile.emap.v2.RegisterReport();
				rt.setDestEMA(Global.getParentnNodeID());
				rt.setReport(rport.getReportParams());
				rt.setRequestID("requestID");
				rt.setService("UpdateReport");
				rt.setSrcEMA(this.controller.getEmaID());
				rt.setType(Global.reportType);
				rt.setTime(this.controller.getCurrentTime(System.currentTimeMillis()));

				String topic = "/EMAP/" + Global.getParentnNodeID() + "/1.0b/Report";
				setPayload = rt.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

			}

			else if (profileVersion.equals("OpenADR2.0b")) {

				Thread.sleep(Global.reportinterval);

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
					rd.addReportDescriptionParams(this.controller.getEmaID(), this.controller.getEmaID(),
							Global.reportType, "Wh", "siScaleCode", "marketContext", "minPeriod", "maxPeriod",
							false, "itemDescription",
							new PowerAttributes().addPowerAttributesParams(0, power, 0).getPowerAttributesParams());

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

							rd.addReportDescriptionParams(key, key, Global.reportType, "Wh", "siScaleCode",
									"marketContext", "minPeriod", "maxPeriod", false, "itemDescription",
									new PowerAttributes().addPowerAttributesParams(0, power, 0)
											.getPowerAttributesParams());
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

					rd.addReportDescriptionParams(this.controller.getEmaID(), this.controller.getEmaID(),
							Global.reportType, "Wh", "siScaleCode", "marketContext", "minPeriod", "maxPeriod",
							false, "itemDescription",
							new PowerAttributes().addPowerAttributesParams(0, power, 0).getPowerAttributesParams());

				}

				com.mir.smartgrid.simulator.profile.openadr.Report report2 = new com.mir.smartgrid.simulator.profile.openadr.Report();
				report2.addReportParams("duration", rd.getReportDescriptionParams(), "reportRequestID",
						"reportSpecifierID", "reportName", "createdDateTime");

				com.mir.smartgrid.simulator.profile.openadr.RegisterReport rt = new com.mir.smartgrid.simulator.profile.openadr.RegisterReport();
				rt.setSrcEMA(this.controller.getEmaID());
				rt.setReport(report2.getReportParams());
				rt.setRequestID("requestID");
				rt.setService("oadrUpdateReport");

				String topic = "/OpenADR/" + Global.getParentnNodeID() + "/2.0b/EiReport";
				setPayload = rt.toString();
				new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

			}

			break;
		}
	}

	public void opt(String procedure, String profileVersion) {
		Opt opt = Opt.valueOf(procedure);
		switch (opt) {
		case CreatedOpt:
			break;

		case CanceledOpt:
			break;
		}
	}
	
	public void summaryReport(String procedure, String profileVersion) {

		
		if (profileVersion.equals("EMAP1.0b")) {

			com.mir.smartgrid.simulator.profile.emap.v2.SummaryReported sr = new com.mir.smartgrid.simulator.profile.emap.v2.SummaryReported();

			sr.setDestEMA(Global.parentnNodeID);
			sr.setReportID("reportID");
			sr.setService("SummaryReported");
			sr.setSrcEMA(this.controller.getEmaID());
			
			
			String topic = "/EMAP/" + Global.getParentnNodeID() + "/1.0b/Summary";
			String setPayload = sr.toString();
			new Publishing().publishThread(this.client, topic, 0, setPayload.getBytes());

			
		}
		
		
	}

	public MqttClient getMqttClient() {
		return client;
	}

	public void setMinPower() {

	}

	public void setMaxPower() {

	}

}
