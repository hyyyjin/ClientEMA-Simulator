package com.mir.smartgrid.simulator.coap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import com.mir.smartgrid.simulator.global.Global;

public class SummaryObs extends Thread {

	String emaID = "";

	public SummaryObs(String emaID) {
		// TODO Auto-generated constructor stub
		setEmaID(emaID);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		
		String uri = "coap://" + Global.coapServerIP + ":" + Global.coapServerPort + "/EMAP/"+Global.parentnNodeID+"/1.0b/Summary/" + getEmaID();

		CoapClient observeClient = new CoapClient();
		observeClient.setURI(uri);
		CoapObserveRelation relation = observeClient.observe(new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				String content = response.getResponseText();
//				System.out.println("Summary" + content);
				if (content.contains("SummaryReport")) {

					String uri = "coap://" + Global.coapServerIP + ":" + Global.coapServerPort + "/EMAP/"+Global.parentnNodeID+"/1.0b/SummaryACK";

					com.mir.smartgrid.simulator.profile.emap.v2.SummaryReported sr = new com.mir.smartgrid.simulator.profile.emap.v2.SummaryReported();

					
					CoapClient client = new CoapClient();
					client.useNONs();
					client.setURI(uri);
					
					sr.setDestEMA(Global.parentnNodeID);
					sr.setReportID("reportID");
					sr.setService("SummaryReported");
					sr.setSrcEMA(getEmaID());
//					observeClient.setURI(uri);
					client.put(sr.toString(), MediaTypeRegistry.APPLICATION_JSON);

				}
			}

			@Override
			public void onError() {
				System.err.println("OBSERVING FAILED (press enter to exit)");
			}

		});

	}

	public String getEmaID() {
		return emaID;
	}

	public void setEmaID(String emaID) {
		this.emaID = emaID;
	}

}