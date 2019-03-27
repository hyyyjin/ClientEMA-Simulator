package com.mir.smartgrid.simulator.profile.openadr;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class RegisterReport {

	int emaNum, requestID, version, duration, rID, resourceID, reportType, siSclecode, readingType, oadrMinPeriod,
			oadrMaxPeriod, hertz, voltage, ac, reportName;
	String venID, oadrReport, itemUnit, marketContext;
	long currentTime;

	public RegisterReport(int emaNum, String venID, int requestID, int version, String oadrReport, int duration,
			int rID, int resourceID, int reportType, String itemUnit, int siSclecode, int readingType,
			String marketContext, int oadrMinPeriod, int oadrMaxPeriod, int hertz, int voltage, int ac, int reportName,
			long currentTime) {

		setAc(ac);
		setCurrentTime(currentTime);
		setDuration(duration);
		setEmaNum(emaNum);
		setHertz(hertz);
		setItemUnit(itemUnit);
		setMarketContext(marketContext);
		setOadrMaxPeriod(oadrMaxPeriod);
		setOadrMinPeriod(oadrMinPeriod);
		setOadrReport(oadrReport);
		setReadingType(readingType);
		setReportName(reportName);
		setReportType(reportType);
		setRequestID(requestID);
		setResourceID(resourceID);
		setrID(rID);
		setSiSclecode(siSclecode);
		setVenID(venID);
		setVersion(version);
		setVoltage(voltage);

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

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getrID() {
		return rID;
	}

	public void setrID(int rID) {
		this.rID = rID;
	}

	public int getResourceID() {
		return resourceID;
	}

	public void setResourceID(int resourceID) {
		this.resourceID = resourceID;
	}

	public int getReportType() {
		return reportType;
	}

	public void setReportType(int reportType) {
		this.reportType = reportType;
	}

	public String getItemUnit() {
		return itemUnit;
	}

	public void setItemUnit(String itemUnit) {
		this.itemUnit = itemUnit;
	}

	public int getSiSclecode() {
		return siSclecode;
	}

	public void setSiSclecode(int siSclecode) {
		this.siSclecode = siSclecode;
	}

	public int getReadingType() {
		return readingType;
	}

	public void setReadingType(int readingType) {
		this.readingType = readingType;
	}

	public int getOadrMinPeriod() {
		return oadrMinPeriod;
	}

	public void setOadrMinPeriod(int oadrMinPeriod) {
		this.oadrMinPeriod = oadrMinPeriod;
	}

	public int getOadrMaxPeriod() {
		return oadrMaxPeriod;
	}

	public void setOadrMaxPeriod(int oadrMaxPeriod) {
		this.oadrMaxPeriod = oadrMaxPeriod;
	}

	public int getHertz() {
		return hertz;
	}

	public void setHertz(int hertz) {
		this.hertz = hertz;
	}

	public int getVoltage() {
		return voltage;
	}

	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}

	public int getAc() {
		return ac;
	}

	public void setAc(int ac) {
		this.ac = ac;
	}

	public int getReportName() {
		return reportName;
	}

	public void setReportName(int reportName) {
		this.reportName = reportName;
	}

	public String getVenID() {
		return venID;
	}

	public void setVenID(String venID) {
		this.venID = venID;
	}

	public String getOadrReport() {
		return oadrReport;
	}

	public void setOadrReport(String oadrReport) {
		this.oadrReport = oadrReport;
	}

	public String getMarketContext() {
		return marketContext;
	}

	public void setMarketContext(String marketContext) {
		this.marketContext = marketContext;
	}

	public long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(long currentTime) {
		this.currentTime = currentTime;
	}

	public String setTimezoneFormat(long currentTimeMillis) {

		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat hms = new SimpleDateFormat("hh:mm:ss");

		String total = ymd.format(new Date(currentTimeMillis)) + "T" + hms.format(new Date(currentTimeMillis)) + "Z";

		return total;
	}

	@Override
	public String toString() {
		return "{\"GW\":\"" + getEmaNum() + "\"," + "\"VENID\":\"" + getVenID() + "\"," + "\"RequestID\":"
				+ getRequestID() + "," + "\"oadrReport\":" + getOadrReport() + "," + "\"Duration\":" + getDuration()
				+ "," + "\"rID\":" + getrID() + "," + "\"ResourceID\":" + getResourceID() + "," + "\"RePortType\":"
				+ getReportName() + "," + "\"ItemUnit\":" + getItemUnit() + "," + "\"siSclecode\":" + getSiSclecode()
				+ "," + "\"ReadingType\":" + getReadingType() + "," + "\"MarketContext\":" + getMarketContext() + ","
				+ "\"oadrMinPeriod\":" + getOadrMinPeriod() + "," + "\"oadrMAXPeriod\":" + getOadrMaxPeriod() + ","
				+ "\"Hertz\":" + getHertz() + "," + "\"Voltage\":" + getVoltage() + "," + "\"AC\":" + getAc() + ","
				+ "\"ReportName\":" + getReportName() + "," + "\"CreateDataTime\":\""
				+ setTimezoneFormat(getCurrentTime()) + "\"" + "}";
	}

}
