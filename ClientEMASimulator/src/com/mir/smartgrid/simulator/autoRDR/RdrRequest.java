package com.mir.smartgrid.simulator.autoRDR;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import com.mir.smartgrid.simulator.controller.Controller;
import com.mir.smartgrid.simulator.global.Global;
import com.mir.smartgrid.simulator.mqtt.Publishing;

public class RdrRequest extends Thread {

	private String emaID;
	private Controller controller;
	private int rdrExperiment = 59;
	private int rdrExperimentCnt = 0;
	private int rdrRequestCnt = Global.RDRRequestNum;
	CoapClient client;
	CoapResponse resp;

	String pathSet = "coap://" + Global.coapServerIP + ":" + Global.coapServerPort + "/EMAP/"
			+ Global.getParentnNodeID() + "/" + Global.version + "/";

	public RdrRequest(String emaID, Controller controller) {
		// TODO Auto-generated constructor stub
		setEmaID(emaID);
		setController(controller);

		if (this.controller.getProtocol().equals("COAP")) {
			client = new CoapClient();
		}

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		super.run();
		Timer timer = new Timer();
		timer.schedule(new UpdateTask(), 5000, Global.RDRInterval);

	}

	private class UpdateTask extends TimerTask {

		public void run() {

			if (rdrExperiment == rdrExperimentCnt) {
				System.out.println(getEmaID() + ":" + rdrExperimentCnt + "회 완료 스레드 종료");
				Global.TERMINATE_CNT += 1;
				if (Global.TERMINATE_CNT == Global.virtualAgentNum) {
					System.out.println("프로그램 종료");
					System.exit(0);
				}
				cancel();

			} else {
				// System.out.println(rdrExperimentCnt+"회");

			}
			// System.out.println(rdrExperimentCnt);
			String optID = Global.optIDMap.get(rdrExperimentCnt + "").toString();

			if (controller.getProtocol().equals("MQTT")) {
				for (int i = 0; i < rdrRequestCnt; i++) {
					com.mir.smartgrid.simulator.profile.emap.v2.Available available = new com.mir.smartgrid.simulator.profile.emap.v2.Available();
					available.addEventParams(controller.getCurrentTime(System.currentTimeMillis()), "PT10S", 90.0,
							20181127, 165520, 20181127, 165520);

					com.mir.smartgrid.simulator.profile.emap.v2.CreateOpt createOpt = new com.mir.smartgrid.simulator.profile.emap.v2.CreateOpt();
					createOpt.setAvailable(available.getEventParams());
					createOpt.setCreatedDateTime(controller.getCurrentTime(System.currentTimeMillis()));
					createOpt.setDestEMA(Global.parentnNodeID);
					createOpt.setMarketContext("marketContext");
					createOpt.setOptID(optID);
					createOpt.setOptReason("Emergency");
					createOpt.setOptType("RdrRequest");
					createOpt.setRequestID("requestID");
					createOpt.setService("CreateOpt");
					createOpt.setSrcEMA(getEmaID());

					String topic = "/EMAP/" + Global.getParentnNodeID() + "/1.0b/Opt";
					String setPayload = createOpt.toString();
					new Publishing().publishThread(controller.getClient(), topic, 0, setPayload.getBytes());
				}
			} else if (controller.getProtocol().equals("COAP")) {

				for (int i = 0; i < rdrRequestCnt; i++) {

					com.mir.smartgrid.simulator.profile.emap.v2.Available available = new com.mir.smartgrid.simulator.profile.emap.v2.Available();
					available.addEventParams(controller.getCurrentTime(System.currentTimeMillis()), "PT10S", 90.0,
							20181127, 165520, 20181127, 165520);

					com.mir.smartgrid.simulator.profile.emap.v2.CreateOpt createOpt = new com.mir.smartgrid.simulator.profile.emap.v2.CreateOpt();
					createOpt.setAvailable(available.getEventParams());
					createOpt.setCreatedDateTime(controller.getCurrentTime(System.currentTimeMillis()));
					createOpt.setDestEMA(Global.parentnNodeID);
					createOpt.setMarketContext("marketContext");
					createOpt.setOptID(optID);
					createOpt.setOptReason("Emergency");
					createOpt.setOptType("RdrRequest");
					createOpt.setRequestID("requestID");
					createOpt.setService("CreateOpt");
					createOpt.setSrcEMA(getEmaID());

					String uri = pathSet + "Opt";

					client.setURI(uri);
					resp = client.put(createOpt.toString(), MediaTypeRegistry.APPLICATION_JSON);
				}
				// System.out.println("==");
				// System.out.println(rdrExperimentCnt);
				// System.out.println(resp.getResponseText().toString());
			}

			rdrExperimentCnt += 1;
		}
	}

	public String getEmaID() {
		return emaID;
	}

	public void setEmaID(String emaID) {
		this.emaID = emaID;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

}
