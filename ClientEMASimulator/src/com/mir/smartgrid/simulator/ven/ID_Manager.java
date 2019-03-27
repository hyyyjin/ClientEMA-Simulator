package com.mir.smartgrid.simulator.ven;

import org.eclipse.paho.client.mqttv3.MqttClient;

public class ID_Manager {
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
	public String getProfileType() {
		return profileType;
	}
	public void setProfileType(String profileType) {
		this.profileType = profileType;
	}
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String reportType) {
		this.reportType = reportType;
	}
	public String getRegistrationID() {
		return registrationID;
	}
	public void setRegistrationID(String registrationID) {
		this.registrationID = registrationID;
	}
	public MqttClient getClient() {
		return client;
	}
	public void setClient(MqttClient client) {
		this.client = client;
	}
	public double getCurrentPower() {
		return currentPower;
	}
	public void setCurrentPower(double currentPower) {
		this.currentPower = currentPower;
	}
	public double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	public double getMinValue() {
		return minValue;
	}
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}
	public double getAvgValue() {
		return avgValue;
	}
	public void setAvgValue(double avgValue) {
		this.avgValue = avgValue;
	}
	public double getThreshold() {
		return threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
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
	public String getMaxTime() {
		return maxTime;
	}
	public void setMaxTime(String maxTime) {
		this.maxTime = maxTime;
	}
	public String getMinTime() {
		return minTime;
	}
	public void setMinTime(String minTime) {
		this.minTime = minTime;
	}
	public boolean isPullModel() {
		return pullModel;
	}
	public void setPullModel(boolean pullModel) {
		this.pullModel = pullModel;
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
