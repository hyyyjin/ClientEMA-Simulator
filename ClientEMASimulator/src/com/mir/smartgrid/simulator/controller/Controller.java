package com.mir.smartgrid.simulator.controller;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Random;

import javax.net.ssl.SSLException;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;

import com.mir.smartgrid.simulator.autoRDR.RdrRequest;
import com.mir.smartgrid.simulator.devProfile.VirtualDeviceManager;
import com.mir.smartgrid.simulator.global.Global;
import com.mir.smartgrid.simulator.mqtt.Mqtt;
import com.mir.smartgrid.simulator.mqtt.Publishing;
import com.mir.smartgrid.simulator.ven.Httpclient;

import io.netty.example.http.snoop.HttpSnoopClient;

public class Controller implements Runnable {

	enum ProtocolType {
		COAP, MQTT, HTTP
	}

	enum InitialProcedure {
		ConnectRegistration, QueryRegistration
	};

	private String emaID, protocol, profileType, reportType, registrationID;
	private MqttClient client;
	private double currentPower, maxValue, minValue, avgValue, threshold, generate, storage;
	private String maxTime, minTime;
	private boolean pullModel;
	// VEN ¿‘¿Â
	private int venRegisterSeqNumber = 0;
	private int reportCnt = 0;
	private String HASHED_VEN_NAME = "";
	private boolean RegistrationFlag = false;

	// protocol value default : MQTT
	public Controller(String emaID) {
		setEmaID(emaID);
		setProtocol("MQTT");
		setProfileType("EMAP");
	}

	public Controller(String emaID, String protocol) {
		setEmaID(emaID);
		setProtocol(protocol);
		setProfileType("EMAP");
	}

	public Controller(String emaID, String protocol, String profileType) {
		setEmaID(emaID);
		setProtocol(protocol);
		setProfileType(profileType);
	}

	public Controller(String emaID, String protocol, String profileType, String reportType) {
		setEmaID(emaID);
		setProtocol(protocol);
		setProfileType(profileType);
		setReportType(reportType);
	}

	public void run() {

		
		// START COAP SERVER - 2018-12-27
		
		// END COAP SERVER
		
//		new VirtualDeviceManager(getEmaID(), this).start();

		if (Global.autoRDR && !Global.CLIENTOPTION) {
			new RdrRequest(getEmaID(), this).start();
		}
		this.setPullModel(Global.pullModel);

		Random rand = new Random();

		int randomNum = rand.nextInt((100 - 20) + 1) + 20;

		System.out.println(getProtocol());

		ProtocolType protocol = ProtocolType.valueOf(getProtocol());
		switch (protocol) {

		case MQTT:

			try {

				String mqtt = "tcp://" + Global.getBrokerIP() + ":" + "1883";
				Mqtt mqttclient = new Mqtt(mqtt, getEmaID() + randomNum, false, false, null, null, this);

				setClient(mqttclient.getMqttClient());
				// this.client = mqttclient.getMqttClient();

				/*
				 * EMAP : Start to send "ConnecRegistration
				 */
				if (getProfileType().equals("EMAP1.0b")) {

					mqttclient.subscribe("/EMAP/" + this.getEmaID() + "/#", 0);

					initialSessionSetup(InitialProcedure.ConnectRegistration.toString());

				}

				/*
				 * Openadr2.0b : Start to send "ConnecRegistration
				 */

				else if (getProfileType().equals("OpenADR2.0b")) {

					mqttclient.subscribe("/OpenADR/" + this.getEmaID() + "/#", 0);

					initialSessionSetup(InitialProcedure.ConnectRegistration.toString());
				}

				// Connect to OpenFMB Server
				// String mqttOpenFMB = "tcp://" + "192.168.0.19" + ":" +
				// "1883";
				// Mqtt mqttclientFMB = new Mqtt(mqttOpenFMB, getEmaID() + "aa",
				// false, false, null, null, this);
				// mqttclientFMB.subscribe("openfmb/#", 0);

			} catch (MqttException e1) {

			}
			break;
		case COAP:

			new com.mir.smartgrid.simulator.coap.CoAPClient(this).start();
//			coapClient.start();

			break;

		case HTTP:

			new Httpclient(this).start();
//			try {
//				new HttpSnoopClient(getEmaID()).start();
//			} catch (SSLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (URISyntaxException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

			break;

		default:
			System.err.println("Wrong Protocol Type, Should input \"COAP OR MQTT\"");
			break;
		}

	}

	public void initialSessionSetup(String inputProcedure) {

		InitialProcedure procedure = InitialProcedure.valueOf(inputProcedure);
		String setPayload = "";
		switch (procedure) {
		case ConnectRegistration:

			if (getProfileType().equals("EMAP1.0b")) {

				com.mir.smartgrid.simulator.profile.emap.v2.ConnectRegistration connectRegistration = new com.mir.smartgrid.simulator.profile.emap.v2.ConnectRegistration();
				connectRegistration.setDestEMA(Global.getParentnNodeID());
				connectRegistration.setRequestID("REQUESTID");
				connectRegistration.setService(procedure.toString());
				connectRegistration.setSrcEMA(getEmaID());
				connectRegistration.setTime(getCurrentTime(System.currentTimeMillis()));
				connectRegistration.setVersion(getProfileType());
				setPayload = connectRegistration.toString();

				new Publishing().publishThread(getClient(), "/EMAP/" + Global.getParentnNodeID() + "/1.0b/SessionSetup",
						0, setPayload.getBytes());

			}

			else if (getProfileType().equals("OpenADR2.0b")) {

				com.mir.smartgrid.simulator.profile.openadr.ConnectRegistration connectR = new com.mir.smartgrid.simulator.profile.openadr.ConnectRegistration();
				connectR.setRequestID("requestID");
				connectR.setService("oadrQueryRegistration");
				connectR.setSrcEMA(getEmaID());

				setPayload = connectR.toString();

				new Publishing().publishThread(getClient(),
						"/OpenADR/" + Global.getParentnNodeID() + "/2.0b/EiRegisterParty", 0, setPayload.getBytes());

			}

			break;

		case QueryRegistration:

			com.mir.smartgrid.simulator.profile.openadr.ConnectRegistration connectR = new com.mir.smartgrid.simulator.profile.openadr.ConnectRegistration();
			connectR.setRequestID("requestID");
			connectR.setService("oadrQueryRegistration");
			connectR.setSrcEMA(getEmaID());

			setPayload = connectR.toString();

			new Publishing().publishThread(getClient(),
					"/OpenADR/" + Global.getParentnNodeID() + "/2.0b/EiRegisterParty", 0, setPayload.getBytes());

			break;
		}
	}

	public String getCurrentTime(long currentTime) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentTime);
	}

	/*
	 * =========================================================================
	 * =======
	 * 
	 * GETTER & SETTER
	 * 
	 * =========================================================================
	 * =======
	 */

	public String getEmaID() {
		return emaID;
	}

	public void setEmaID(String emaID) {
		this.emaID = emaID;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public double getCurrentPower() {
		return currentPower;
	}

	public void setCurrentPower(double currentPower) {
		this.currentPower = currentPower;
	}

	public double getAvgValue() {
		return avgValue;
	}

	public Controller setAvgValue(double avgValue) {

		if (this.avgValue <= 0) {
			this.avgValue = avgValue;
		} else {
			this.avgValue = (this.avgValue + avgValue) / 2;
		}
		return this;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public Controller setMaxValue(double maxValue) {

		if (this.maxValue < maxValue) {
			this.maxValue = maxValue;
			setMaxTime(System.currentTimeMillis());
		}

		return this;
	}

	public double getMinValue() {
		return minValue;
	}

	public Controller setMinValue(double minValue) {

		if (this.minValue > minValue) {
			this.minValue = minValue;
			setMinTime(System.currentTimeMillis());
		}

		return this;
	}

	public String getMaxTime() {
		return maxTime;
	}

	public Controller setMaxTime(long maxTime) {

		this.maxTime = getCurrentTime(maxTime);
		return this;
	}

	public String getMinTime() {
		return minTime;
	}

	public Controller setMinTime(long minTime) {

		this.minTime = getCurrentTime(minTime);

		return this;
	}

	public String getRegistrationID() {
		return registrationID;
	}

	public void setRegistrationID(String registrationID) {
		this.registrationID = registrationID;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	public String getProfileType() {
		return profileType;
	}

	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}

	public boolean isPullModel() {
		return pullModel;
	}

	public void setPullModel(boolean pullModel) {
		this.pullModel = pullModel;
	}

	public double getGenerate() {
		return generate;
	}

	public void setGenerate(double generate) {
		this.generate = generate;
	}

	public double getStorage() {
		return storage;
	}

	public void setStorage(double storage) {
		this.storage = storage;
	}

	public int getVenRegisterSeqNumber() {
		return venRegisterSeqNumber;
	}

	public void setVenRegisterSeqNumber(int venRegisterSeqNumber) {
		this.venRegisterSeqNumber = venRegisterSeqNumber;
	}

	public int getReportCnt() {
		return reportCnt;
	}

	public void setReportCnt(int reportCnt) {
		this.reportCnt = reportCnt;
	}

	public MqttClient getClient() {
		return client;
	}

	public void setClient(MqttClient client) {
		this.client = client;
	}

	public String getHASHED_VEN_NAME() {
		return HASHED_VEN_NAME;
	}

	public void setHASHED_VEN_NAME(String hASHED_VEN_NAME) {
		HASHED_VEN_NAME = hASHED_VEN_NAME;
	}

	public boolean isRegistrationFlag() {
		return RegistrationFlag;
	}

	public void setRegistrationFlag(boolean registrationFlag) {
		RegistrationFlag = registrationFlag;
	}

}