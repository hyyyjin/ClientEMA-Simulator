package com.mir.ems.coap;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.Response;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.mir.smartgrid.simulator.devProfile.EMAP_CoAP_EMA_DR;
import com.mir.smartgrid.simulator.devProfile.Emap_Cema_Profile;
import com.mir.smartgrid.simulator.global.Global;

public class CoAPObserverSubPath extends CoapResource {

	private String name;
	private String parentPath;

	public CoAPObserverSubPath(String name, String parentPath) {
		super(name);
		this.name = name;
		setObservable(true);
		setObserveType(Type.NON);
		getAttributes().setObservable();
		setParentPath(parentPath);
	}

	public void handleGET(CoapExchange exchange) {

		Response response = new Response(ResponseCode.CONTENT);

		if (Global.obs_emaProtocolCoAP_EventFlag.containsKey(name)
				&& !Global.obs_emaProtocolCoAP_EventFlag.get(name).isEventFlag()) {

			clearObserveRelations();
			Global.observeManager.replace(name, this);
			response.setPayload("Heartbeat_Response");
			exchange.respond(response);
		}

		else if (!Global.obs_emaProtocolCoAP_EventFlag.containsKey(name)) {

			Global.obs_emaProtocolCoAP_EventFlag.put(name, new EMAP_CoAP_EMA_DR());
			Global.observeManager.put(name, this);
			response.setPayload("Initial_Success");
			exchange.respond(response);

		} else if (Global.obs_emaProtocolCoAP_EventFlag.get(name).isEventFlag()) {

			if (getParentPath().contains("OpenADR")) {

				new Thread(new Runnable() {
					public void run() {
						Global.obs_emaProtocolCoAP_EventFlag.replace(name, new EMAP_CoAP_EMA_DR().setEventFlag(false));
					}
				}).start();

				Emap_Cema_Profile emaProfile = new Emap_Cema_Profile();

				emaProfile.setEmaID(name);

				double threshold = Global.obs_emaProtocolCoAP_EventFlag.get(name).getThreshold();
				int strTime = Global.obs_emaProtocolCoAP_EventFlag.get(name).getStartTime();
				int strYMD = Global.obs_emaProtocolCoAP_EventFlag.get(name).getStartYMD();
				int endTime = Global.obs_emaProtocolCoAP_EventFlag.get(name).getEndTime();
				int endYMD = Global.obs_emaProtocolCoAP_EventFlag.get(name).getEndYMD();

				String strTime_str = strTime + "";
				String endTime_str = endTime + "";

				strTime_str = strTime_str.length() < 6 ? "0" + strTime_str : strTime_str;
				endTime_str = endTime_str.length() < 6 ? "0" + endTime_str : endTime_str;

				String eventDuration = ISO8601(strYMD, strTime, endYMD, endTime);
				String timeForm = (strYMD + "") + strTime_str;
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

				try {

					// Date createdDateTime = sdf.parse(timeForm);
					com.mir.smartgrid.simulator.profile.openadr.Intervals interval = new com.mir.smartgrid.simulator.profile.openadr.Intervals();
					interval.addIntervalsParams("PT20S", "uid", threshold);

					com.mir.smartgrid.simulator.profile.openadr.Event event = new com.mir.smartgrid.simulator.profile.openadr.Event();

					event.addEventParams("eventID",
							new com.mir.smartgrid.simulator.profile.openadr.EventSignals()
									.addEventSignalsParams(interval.getIntervalsParams(), "signalName", "Control Event",
											"signalID", 0)
									.getEventSignalsParams(),
							1, "modificationReason", -1, "mirLab", new Date(System.currentTimeMillis()).toString(),
							"eventStatus", false, "Event", "properties", "components", emaProfile.getEmaID(),
							new Date(System.currentTimeMillis()).toString(), eventDuration, "tolerance", "notification",
							"rampUp", "recovery");

					com.mir.smartgrid.simulator.profile.openadr.EventResponse er = new com.mir.smartgrid.simulator.profile.openadr.EventResponse();
					er.setRequestID(emaProfile.getRequestID());
					er.setResponseCode(200);
					er.setResponseDescription("OK");

					com.mir.smartgrid.simulator.profile.openadr.DistributeEvent drE = new com.mir.smartgrid.simulator.profile.openadr.DistributeEvent();

					drE.setSrcEMA(Global.SYSTEMID);
					drE.setEvent(event.getEventParams());
					drE.setRequestID(emaProfile.getRequestID());
					drE.setResponse(er.toString());
					drE.setService("oadrDistributeEvent");
					drE.setResponseRequired("Always");

					String payload = drE.toString();
					exchange.respond(ResponseCode.CONTENT, payload, MediaTypeRegistry.APPLICATION_JSON);

				} catch (Exception e) {
					e.printStackTrace();
				}

			} else if (getParentPath().contains("EMAP")) {

				new Thread(new Runnable() {
					public void run() {
						Global.obs_emaProtocolCoAP_EventFlag.replace(name, new EMAP_CoAP_EMA_DR().setEventFlag(false));
					}
				}).start();

				double threshold = Global.obs_emaProtocolCoAP_EventFlag.get(name).getThreshold();
				int strTime = Global.obs_emaProtocolCoAP_EventFlag.get(name).getStartTime();
				int strYMD = Global.obs_emaProtocolCoAP_EventFlag.get(name).getStartYMD();
				int endTime = Global.obs_emaProtocolCoAP_EventFlag.get(name).getEndTime();
				int endYMD = Global.obs_emaProtocolCoAP_EventFlag.get(name).getEndYMD();

				String strTime_str = strTime + "";
				String endTime_str = endTime + "";

				strTime_str = strTime_str.length() < 6 ? "0" + strTime_str : strTime_str;
				endTime_str = endTime_str.length() < 6 ? "0" + endTime_str : endTime_str;

				String eventDuration = ISO8601(strYMD, strTime, endYMD, endTime);
				String timeForm = (strYMD + "") + strTime_str;

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

				Emap_Cema_Profile emaProfile = new Emap_Cema_Profile();

				emaProfile.setEmaID(name);

				try {
					
					com.mir.smartgrid.simulator.profile.emap.v2.Intervals interval = new com.mir.smartgrid.simulator.profile.emap.v2.Intervals();
					interval.addIntervalsParams("PT20S", "uid", 0.0);

					com.mir.smartgrid.simulator.profile.emap.v2.EventSignals es = new com.mir.smartgrid.simulator.profile.emap.v2.EventSignals();
					es.addEventSignalsParams(interval.getIntervalsParams(), "Distribute", "Event", "signalID", 0,
							threshold, 0, 0, "KW");

					com.mir.smartgrid.simulator.profile.emap.v2.Event event = new com.mir.smartgrid.simulator.profile.emap.v2.Event();

					String eventID = Global.obs_emaProtocolCoAP_EventFlag.get(name).getEventID();

					event.addEventParams(eventID, es.getEventSignalsParams(), 1, "None", 2, "marketContext",
							new Date(System.currentTimeMillis()).toString(), "Event", false, "Event", "None", "None",
							"None", new Date(System.currentTimeMillis()).toString(), eventDuration, "None", "None",
							"None", "None");

					com.mir.smartgrid.simulator.profile.emap.v2.EventResponse er = new com.mir.smartgrid.simulator.profile.emap.v2.EventResponse();
					er.setRequestID(emaProfile.getRequestID());
					er.setResponseCode(200);
					er.setResponseDescription("OK");

					com.mir.smartgrid.simulator.profile.emap.v2.DistributeEvent drE = new com.mir.smartgrid.simulator.profile.emap.v2.DistributeEvent();

					drE.setDestEMA(emaProfile.getEmaID());
					drE.setEvent(event.getEventParams());
					drE.setRequestID(emaProfile.getRequestID());
					drE.setResponse(er.toString());
					drE.setService("DistributeEvent");
					drE.setSrcEMA(Global.SYSTEMID);
					drE.setTime(new Date(System.currentTimeMillis()).toString());
					drE.setResponseRequired("Always");

					String payload = drE.toString();

					exchange.respond(ResponseCode.CONTENT, payload, MediaTypeRegistry.APPLICATION_JSON);

					Global.obs_emaProtocolCoAP_EventFlag.get(name).setEventFlag(false);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

	public static String ISO8601(int startYMD, int startTime, int endYMD, int endTime) {

		int calTime = endTime - startTime;

		if (calTime > 0) {

			if (calTime < 60) {

				return "PT" + calTime + "S";

			} else if (calTime > 60 && calTime < 6000) {

				int stemp = calTime % 100;
				int mTemp = ((calTime - stemp) % 10000) / 100;

				if (stemp >= 60) {
					stemp -= 60;
					mTemp += 1;
				}

				return stemp == 0 ? "PT" + mTemp + "M" : "PT" + mTemp + "M" + stemp + "S";

			} else {

				int stemp = calTime % 100;
				int mTemp = ((calTime - stemp) % 10000) / 100;
				int hTemp = (calTime) / 10000;

				if (stemp >= 60) {
					stemp -= 60;
					mTemp += 1;
				}

				if (mTemp >= 60) {
					mTemp -= 60;
					hTemp += 1;
				}

				if (stemp == 0 && mTemp != 0) {

					return "PT" + hTemp + "H" + mTemp + "M";

				}
				if (mTemp == 0 && stemp != 0) {

					return "PT" + hTemp + "H" + stemp + "S";
				}
				if (mTemp == 0 && stemp == 0) {

					return "PT" + hTemp + "H";
				}

				return "PT" + hTemp + "H" + mTemp + "M" + stemp + "S";

			}
		}

		return "WRONG";

	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

}
