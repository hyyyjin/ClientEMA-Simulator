package com.mir.smartgrid.simulator.profile.openadr;

public class CreatePartyRegistration {

	int emaNum, requestID, version, reportOnly, lastPollPushGet;
	String venID, transportName;
	public CreatePartyRegistration(int emaNum, String venID, int requestID, int version, String transportName, int reportOnly, int lastPollPushGet){
		
		setEmaNum(emaNum);
		setLastPollPushGet(lastPollPushGet);
		setReportOnly(reportOnly);
		setRequestID(requestID);
		setTransportName(transportName);
		setVenID(venID);
		setVersion(version);
	}
	public String getEmaNum() {
		return "gw\\/"+emaNum;
	}
	public void setEmaNum(int emaNum) {
		this.emaNum = emaNum;
	}
	public int getRequestID() {
		return requestID;
	}
	public void setRequestID(int requestID) {
		this.requestID = requestID;
	}
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	public int getReportOnly() {
		return reportOnly;
	}
	public void setReportOnly(int reportOnly) {
		this.reportOnly = reportOnly;
	}
	public int getLastPollPushGet() {
		return lastPollPushGet;
	}
	public void setLastPollPushGet(int lastPollPushGet) {
		this.lastPollPushGet = lastPollPushGet;
	}
	public String getVenID() {
		return venID;
	}
	public void setVenID(String venID) {
		this.venID = venID;
	}
	public String getTransportName() {
		return transportName;
	}
	public void setTransportName(String transportName) {
		this.transportName = transportName;
	}
	
	@Override
	public String toString() {
		return "{\"GW\":\"" + getEmaNum() + "\","
				+ "\"VENID\":\"" + getVenID()+ "\","
				+ "\"RequestID\":" + getRequestID() + ","
				+ "\"TransportName\":" + getRequestID() + ","
				+ "\"ReportOnly\":" + getRequestID() + ","
				+ "\"LastPollPushGet\":" + getRequestID() + ","
				+ "\"Version\":" + getVersion()+"\""
				+ "}";
	}
}
