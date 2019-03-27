package com.mir.smartgrid.simulator.profile.openadr;

import java.util.ArrayList;

public class ReportPayload {

	String rID, dataQuality;
	int confidence, accuracy;
	double value;
	ArrayList<String> param = new ArrayList<String>();
	
	public ReportPayload(){

		
	}

	public String getrID() {
		return rID;
	}

	public void setrID(String rID) {
		this.rID = rID;
	}

	public String getDataQuality() {
		return dataQuality;
	}

	public void setDataQuality(String dataQuality) {
		this.dataQuality = dataQuality;
	}

	public int getConfidence() {
		return confidence;
	}

	public void setConfidence(int confidence) {
		this.confidence = confidence;
	}

	public int getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(int accuracy) {
		this.accuracy = accuracy;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "{\"rID\":\"" + getrID() + "\","
				+ "\"confidence\":" + getConfidence()+ ","
				+ "\"accuracy\":" + getAccuracy() + ","
				+ "\"dataQuality\":\"" + getDataQuality() + "\","
				+ "\"value\":" + getValue()
				+ "}";
	}

	public void addParams(String rID, int confidence, int accuracy, double value, String dataQuality){
		
		setAccuracy(accuracy);
		setConfidence(confidence);
		setDataQuality(dataQuality);
		setrID(rID);
		setValue(value);
		
		this.param.add(toString());
	}
	
	public String getParams(){
		
		return this.param.toString();
	}
	
	public static void main(String[] args){
		
		
		ArrayList<String> aa = new ArrayList<String>();
		
		aa.add("HYUINJIN1");
		aa.add("HYUINJIN2");
		
		System.out.println(aa);
//		System.out.println(new ReportPayload("HYUNJIN", 100,100, 100.1, "Quality Good - Non Specific").toString());
		
		
		ReportPayload bb = new ReportPayload();
		
		
		bb.addParams("HYUNJIN", 100,100, 100.1, "Quality Good - Non Specific");
		System.out.println(bb.getParams());

		
		for(int i=0; i<5; i++){
			bb.addParams("HYUNJIN"+i, 100,100, 100.1, "Quality Good - Non Specific");
		}
		
		UpdateReport cc =new UpdateReport(1, "ven1", 1, System.currentTimeMillis(), "TEST", "TESTID1", "REQEST", System.currentTimeMillis(), 10, bb.getParams());
		
		
		System.out.println(cc.toString());
		
	}
	
	
}
