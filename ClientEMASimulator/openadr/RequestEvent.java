package com.mir.smartgrid.simulator.profile.openadr;

public class RequestEvent {

	int emaNum, requestID, replyLimit;
	String venID;

	public RequestEvent(int emaNum, String venID, int requestID, int replyLimit) {
		setEmaNum(emaNum);
		setRequestID(requestID);
		setVenID(venID);
		setReplyLimit(replyLimit);
	}

	public String getEmaNum() {
		return "gw\\/" + emaNum;
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

	public int getReplyLimit() {
		return replyLimit;
	}

	public void setReplyLimit(int replyLimit) {
		this.replyLimit = replyLimit;
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
				+ "\"replylimit\":" + getReplyLimit()+"\""
				+ "}";
	}
	
}
