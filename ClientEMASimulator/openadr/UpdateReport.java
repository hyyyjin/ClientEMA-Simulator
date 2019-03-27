package com.mir.smartgrid.simulator.profile.openadr;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class UpdateReport {

	int emaNum, requestID, duration;
	long createdDataTime, dateTime;
	String venID, reportName, reportSpecifierID, reportRequestID, reportPayload;
	
	public UpdateReport(int emaNum, String venID, int requestID, long createdDataTime, String reportName,
			String reportSpecifierID, String reportRequestID, long dateTime, int duration, String reportPayload) {

		setCreatedDataTime(createdDataTime);
		setDateTime(dateTime);
		setDuration(duration);
		setEmaNum(emaNum);
		setReportName(reportName);
		setReportPayload(reportPayload);
		setReportRequestID(reportRequestID);
		setReportSpecifierID(reportSpecifierID);
		setRequestID(requestID);
		setVenID(venID);
		
	}

	public String getVenID() {
		return venID;
	}

	public void setVenID(String venID) {
		this.venID = venID;
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

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public long getCreatedDataTime() {
		return createdDataTime;
	}

	public void setCreatedDataTime(long createdDataTime) {
		this.createdDataTime = createdDataTime;
	}

	public long getDateTime() {
		return dateTime;
	}

	public void setDateTime(long dateTime) {
		this.dateTime = dateTime;
	}

	public String getReportName() {
		return reportName;
	}

	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

	public String getReportSpecifierID() {
		return reportSpecifierID;
	}

	public void setReportSpecifierID(String reportSpecifierID) {
		this.reportSpecifierID = reportSpecifierID;
	}

	public String getReportRequestID() {
		return reportRequestID;
	}

	public void setReportRequestID(String reportRequestID) {
		this.reportRequestID = reportRequestID;
	}

	public String getReportPayload() {
		return reportPayload;
	}

	public void setReportPayload(String reportPayload) {
		this.reportPayload = reportPayload;
	}
	
	public String setTimezoneFormat(long currentTimeMillis) {

		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat hms = new SimpleDateFormat("hh:mm:ss");

		String total = ymd.format(new Date(currentTimeMillis)) + "T" + hms.format(new Date(currentTimeMillis)) + "Z";

		return total;
	}
	
	@Override
	public String toString() {
		return "{\"GW\":\"" + getEmaNum() + "\","
				+ "\"VENID\":\"" + getVenID()+ "\","
				+ "\"RequestID\":" + getRequestID() + ","
				+ "\"createdDataTime\":\"" + setTimezoneFormat(getCreatedDataTime()) + "\","
				+ "\"reportName\":\"" + getReportName() + "\","
				+ "\"reportSpecifierID\":\"" + getReportSpecifierID() + "\","
				+ "\"reportRequestID\":\"" + getReportRequestID() + "\","
				+ "\"date-time\":\"" + setTimezoneFormat(getDateTime()) + "\","
				+ "\"duration\":" + getDuration() + ","
				+ "\"reportPayload\":" + getReportPayload()
				+ "}";
	}
	
	
}
