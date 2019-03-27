package com.mir.smartgrid.simulator.ven;

import java.io.BufferedReader;
import java.io.FileReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import com.mir.smartgrid.simulator.controller.Controller;
import com.mir.smartgrid.simulator.global.InitParser;

public class Httpclient extends Thread {

	private Controller controller;
	public Httpclient(Controller controller){
		
		this.controller = controller;
	}
	
	@Override
	public void run() {
		try {
			// Thread.sleep(1000);
			new InitParser();
			new HTTPRequest(controller, "EiRegisterParty").getHttpResponse();
		} catch (ParserConfigurationException | SAXException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
