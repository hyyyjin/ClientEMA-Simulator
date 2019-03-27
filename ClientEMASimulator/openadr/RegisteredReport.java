package com.mir.smartgrid.simulator.profile.openadr;

public class RegisteredReport {

	int emaNum, requestID, version;
	String venID;
	
	public RegisteredReport(int emaNum, String venID, int requestID, int version){
		setEmaNum(emaNum);
		setRequestID(requestID);
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

	public String getVenID() {
		return venID;
	}

	public void setVenID(String venID) {
		this.venID = venID;
	}
	
	@Override
	public String toString() {
		return "{\"GW\":\"" + getEmaNum() + "\","
				+ "\"VENID\":\"" + getVenID()+ "\","
				+ "\"RequestID\":" + getRequestID() + ","
				+ "\"Version\":" + getVersion()+"\""
				+ "}";
	}
}
