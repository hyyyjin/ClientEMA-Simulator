package com.mir.smartgrid.simulator.main;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.mir.smartgrid.simulator.mqtt.Mqtt;
import com.mir.smartgrid.simulator.mqtt.Publishing;

/**
 * This example demonstrates the use of {@link HttpGet} request method.
 */
public class HttpGetRequestMethodExample {

	public static void main(String[] args) throws MqttException, InterruptedException, ParserConfigurationException,
			SAXException, IOException, TransformerException {
		
		String[] a = {"1","2","3","4"};
		
		
		final String[] MICROGRIDPROFILE1 = new String[] {"SolarReadingProfile", "BatteryReadingProfile", "RecloserReadingProfile"};

		
		String aa = "SolarReadingProfile";
		
		boolean contains = Stream.of(MICROGRIDPROFILE1).anyMatch(x -> x == aa);
		
		
		System.out.println(Stream.of(MICROGRIDPROFILE1).anyMatch(x -> x == aa));
		
		if(Stream.of(MICROGRIDPROFILE1).anyMatch(x -> x == aa)){
			System.out.println("aa");
		}
		
		
		
//
//		StringBuilder contentBuilder = new StringBuilder();
//
//		try (Stream<String> stream = Files.lines(Paths.get("control-1.xml"), StandardCharsets.UTF_8)) {
//			stream.forEach(s -> contentBuilder.append(s).append("\n"));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//
//		// Connect to OpenFMB Server
//		String mqttOpenFMB = "tcp://" + "192.168.25.42" + ":" + "1883";
//
//		Mqtt mqttclientFMB = new Mqtt(mqttOpenFMB, "teseses" + "aa", false, false, null, null);
//		System.out.println("Connected to MQTT Broker for OPENFMB" + mqttOpenFMB);
//		mqttclientFMB.subscribe("openfmb/resourcemodule/#", 0);
//
//		MqttClient aa = mqttclientFMB.getMqttClient();
//
//		String topicName = "openfmb/reclosermodule/RecloserControlProfile/DEMO.MGRID.RECLOSER.1";
//
//		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//		Document doc = dBuilder.parse(new InputSource(new StringReader(contentBuilder.toString())));
//
//		System.out.println(doc.getDocumentElement().getNodeName());
//		// doc.getDocumentElement().normalize();
//		// System.out.println("Root element :" +
//		// doc.getDocumentElement().getNodeName());
//
//		NodeList nList = doc.getElementsByTagName("ns9:action");
//
//		int i = 0;
//		while (true) {
//			Thread.sleep(1500);
//
//			if (i % 2 == 0) {
//				nList.item(0).setTextContent("close");
//			}
//
//			else {
//				nList.item(0).setTextContent("trip");
//
//			}
//
//			TransformerFactory transformerFactory = TransformerFactory.newInstance();
//			Transformer transformer = transformerFactory.newTransformer();
//			DOMSource source = new DOMSource(doc);
//
//			StringWriter sw = new StringWriter();
//			transformer.transform(source, new StreamResult(sw));
//
//			new Publishing().publishThread(aa, topicName, 0, sw.toString().getBytes());
//
//			i++;
//		}
	}
}