package com.mir.smartgrid.simulator.profile.openadr;

public class CreatedEvent {

	int emaNum, requestID, version, response, value;
	String venID;
	
	public CreatedEvent(int emaNum, String venID, int requestID, int version, int response, int value){
		setEmaNum(emaNum);
		setResponse(response);
		setRequestID(requestID);
		setValue(value);
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

	public int getResponse() {
		return response;
	}

	public void setResponse(int response) {
		this.response = response;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
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
				+ "\"Version\":" + getVersion()+","
				+ "\"Response\":" + getResponse()+","
				+ "\"Value\":" + getValue()+"\""					
				+ "}";
	}
	
}
