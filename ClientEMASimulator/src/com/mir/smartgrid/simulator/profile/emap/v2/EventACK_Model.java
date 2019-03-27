package com.mir.smartgrid.simulator.profile.emap.v2;

import org.json.JSONException;
import org.json.JSONObject;

public class EventACK_Model {

	private String SrcEMA, DestEMA, service, eventID;

	public EventACK_Model() {

	}

	public String getSrcEMA() {
		return SrcEMA;
	}

	public void setSrcEMA(String srcEMA) {
		SrcEMA = srcEMA;
	}

	public String getDestEMA() {
		return DestEMA;
	}

	public void setDestEMA(String destEMA) {
		DestEMA = destEMA;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getEventID() {
		return eventID;
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub

		JSONObject json = new JSONObject();
		try {
			json.put("SrcEMA", getSrcEMA());
			json.put("DestEMA", getDestEMA());
			json.put("service", "eventACK");
			json.put("eventID", getEventID());

			return json.toString();

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "WRONG";

		// return super.toString();
	}
}
