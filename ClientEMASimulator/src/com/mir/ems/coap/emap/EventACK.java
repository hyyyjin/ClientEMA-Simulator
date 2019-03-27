package com.mir.ems.coap.emap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONException;
import org.json.JSONObject;

import com.mir.smartgrid.simulator.global.Global;


public class EventACK extends CoapResource {

	public EventACK(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleGET(CoapExchange exchange) {
		exchange.respond(ResponseCode.FORBIDDEN, "Wrong Access");
	}

	@Override
	public void handlePOST(CoapExchange exchange) {
		exchange.respond(ResponseCode.FORBIDDEN, "Wrong Access");
	}

	@Override
	public void handleDELETE(CoapExchange exchange) {
		exchange.respond(ResponseCode.FORBIDDEN, "Wrong Access");
	}

	@Override
	public void handlePUT(CoapExchange exchange) {

		String text = exchange.getRequestText();
		
		String eventID = "";
		try {

			JSONObject jsonParse = new JSONObject(text);
			eventID = jsonParse.getString("eventID");

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
		
		if (Global.eventFromServer) {
			
			if (Global.CLIENTOPTION) {

				String pathSet = "coap://" + Global.coapServerIP + ":" + Global.coapServerPort + "/EMAP/"
						+ Global.getParentnNodeID() + "/" + Global.version + "/";

				String uri = pathSet + "EventACK";
				CoapClient client = new CoapClient();

				client = new CoapClient();
				client.setURI(uri);
				client.useNONs();

				JSONObject json = new JSONObject();
				try {

					json.put("SrcEMA", Global.SYSTEMID);
					json.put("DestEMA", Global.parentnNodeID);
					json.put("service", "eventACK");
					json.put("eventID", eventID);

					client.put(new CoapHandler() {

						@Override
						public void onLoad(CoapResponse response) {
						}

						@Override
						public void onError() {
						}

					}, json.toString(), MediaTypeRegistry.APPLICATION_JSON);

					

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}



	}

}
