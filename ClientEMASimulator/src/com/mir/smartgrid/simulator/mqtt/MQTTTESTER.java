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

public class MQTTTESTER implements MqttCallback {

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

	public MQTTTESTER(String brokerUrl, String clientId, boolean cleanSession, boolean quietMode, String userName,
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

	public MQTTTESTER(String brokerUrl, String clientId, boolean cleanSession, boolean quietMode, String userName,
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

		// XML
		if (msgPayload.startsWith("<") && msgPayload.endsWith(">") || msgPayload.contains("xmlns")) {

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

	public MqttClient getMqttClient() {
		return client;
	}

	public void setMinPower() {

	}

	public void setMaxPower() {

	}

}
