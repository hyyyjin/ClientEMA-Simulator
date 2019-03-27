package com.mir.ems.coap;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.CoAP.Type;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;

import com.mir.smartgrid.simulator.global.Global;
import com.mir.smartgrid.simulator.profile.emap.v2.Summary;
import com.mir.smartgrid.simulator.profile.emap.v2.SummaryReport;

public class SummaryObserverSubPath extends CoapResource {

	private String name;
	private String parentPath;

	public SummaryObserverSubPath(String name, String parentPath) {
		super(name);
		this.name = name;
		setObservable(true);
		setObserveType(Type.NON);
		getAttributes().setObservable();

		setParentPath(parentPath);

		Timer timer = new Timer();
		timer.schedule(new UpdateTask(), 0, Global.summaryInterval);
	}

	private class UpdateTask extends TimerTask {
		public void run() {

			changed();

		}
	}

	public void handleGET(CoapExchange exchange) {

		Iterator<String> it = Global.emaProtocolCoAP.keySet().iterator();
		Summary sm = new Summary();

		while (it.hasNext()) {

			String key = it.next();

			sm.addsummaryParam(key, Global.emaProtocolCoAP.get(key).getMargin(),
					Global.emaProtocolCoAP.get(key).getAvgValue(), Global.emaProtocolCoAP.get(key).getMaxValue(),
					Global.emaProtocolCoAP.get(key).getGenerate(), Global.emaProtocolCoAP.get(key).getStorage(),
					Global.emaProtocolCoAP.get(key).getPower());

		}

		SummaryReport sr = new SummaryReport();
		sr.setDestEMA(name);
		sr.setRequestID("requestID");
		sr.setService("SummaryReport");
		sr.setSrcEMA(Global.SYSTEMID);
		sr.setSummary(sm.getEventParams());
		sr.setSummaryType("SummaryReport");

		exchange.respond(ResponseCode.CONTENT, sr.toString(), MediaTypeRegistry.APPLICATION_JSON);

	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

}
