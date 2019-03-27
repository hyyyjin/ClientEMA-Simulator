package com.mir.smartgrid.simulator.main;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.mir.smartgrid.simulator.devProfile.DeviceProfile;
import com.mir.smartgrid.simulator.global.Global;
import com.mir.smartgrid.simulator.mqtt.Mqtt;
import com.mir.smartgrid.simulator.mqtt.Publishing;

public class DomParserDemo {

	// private long currentTimeMillis;
	public DomParserDemo() {

	}

	public String setTimezoneFormat(long currentTimeMillis) {

		SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat hms = new SimpleDateFormat("hh:mm:ss.SSS");

		String total = ymd.format(new Date(currentTimeMillis)) + "T" + hms.format(new Date(currentTimeMillis))
				+ "+09:00";

		return total;
	}

	public static void main(String[] args) {

//		long current = System.currentTimeMillis();
//
//		String time = new DomParserDemo().setTimezoneFormat(current);
//
//		System.out.println(time);

		// String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
		// "<ns1:schema xmlns:ns1=\"http://example.com\"
		// xmlns:ns2=\"http://example2.com\">"
		// + "<ns1:tag1>" + "<ns2:tag2>value</ns2:tag2>" + "</ns1:tag1>" +
		// "</ns1:schema>";
		//
		// DocumentBuilderFactory docBuilderFactory =
		// DocumentBuilderFactory.newInstance();
		// docBuilderFactory.setNamespaceAware(true);
		// DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		// Document doc = docBuilder.parse(new InputSource(new
		// StringReader(xml)));
		//
		// NodeList nl = doc.getElementsByTagNameNS("http://example2.com",
		// "tag2");
		//
		//
		// System.out.println(nl.getLength());
		//
		// String a = nl.item(0).getTextContent();
		// System.out.println(a);

		try {
			File inputFile = new File("BatteryReadingProfile.xml");

			String inputFile1 = "";

			StringBuilder contentBuilder = new StringBuilder();

			try (Stream<String> stream = Files.lines(Paths.get("Resource.xml"), StandardCharsets.UTF_8)) {
				stream.forEach(s -> contentBuilder.append(s).append("\n"));
			} catch (IOException e) {
				e.printStackTrace();
			}

			// System.out.println(contentBuilder.toString());

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(contentBuilder.toString())));
			Document doc1 = dBuilder.parse(new InputSource(new StringReader(contentBuilder.toString())));
			
			// System.out.println(doc.getDocumentElement().getNodeName());
			// doc.getDocumentElement().normalize();
			// System.out.println("Root element :" +
			// doc.getDocumentElement().getNodeName());

			// NodeList ss = doc.getElementsByTagName("logicalDeviceID");
			NodeList ss = doc.getElementsByTagName("logicalDeviceID");

			System.out.println("@");

			doc.getElementsByTagName("logicalDeviceID").item(0).setTextContent("MIR.DEVICE.LED1");
			doc.getElementsByTagName("mRID").item(0).setTextContent("MIR.DEVICE.LED1");
			doc.getElementsByTagName("mRID").item(1).setTextContent("MIR.DEVICE.LED1");

			doc1.getElementsByTagName("logicalDeviceID").item(0).setTextContent("MIR.DEVICE.LED2");
			doc1.getElementsByTagName("mRID").item(0).setTextContent("MIR.DEVICE.LED2");
			doc1.getElementsByTagName("mRID").item(1).setTextContent("MIR.DEVICE.LED2");

			
			
			// Connect to OpenFMB Server
			String mqttOpenFMB = "tcp://" + "192.168.1.172" + ":" + "1883";

			Mqtt mqttclientFMB = new Mqtt(mqttOpenFMB, "teseses" + "aa", false, false, null, null);
			System.out.println("Connected to MQTT Broker for OPENFMB" + mqttOpenFMB);
			mqttclientFMB.subscribe("openfmb/resourcemodule/#", 0);

			MqttClient aa = mqttclientFMB.getMqttClient();
			String topicName = "openfmb/resourcemodule/ResourceReadingProfile/MIR.DEVICE.LED1";
			String topicName1 = "openfmb/resourcemodule/ResourceReadingProfile/MIR.DEVICE.LED2";
			
			
			while (true) {
				
				long current = System.currentTimeMillis();

				String time = new DomParserDemo().setTimezoneFormat(current);

				System.out.println(time);
				
				Thread.sleep(5000);
				doc.getElementsByTagName("timestamp").item(0).setTextContent(time);
				doc.getElementsByTagName("start").item(0).setTextContent(time);
				doc.getElementsByTagName("value").item(0).setTextContent("34.471153");

				System.out.println(ss.item(0).getTextContent());
				System.out.println("@");

				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);

				StringWriter sw = new StringWriter();
				transformer.transform(source, new StreamResult(sw));

				System.out.println(sw);
				new Publishing().publishThread(aa, topicName, 0, sw.toString().getBytes());

				
				
				DOMSource source1 = new DOMSource(doc1);
				StringWriter sw1 = new StringWriter();

				transformer.transform(source1, new StreamResult(sw1));

				System.out.println(sw1);

				new Publishing().publishThread(aa, topicName1, 0, sw1.toString().getBytes());

				
			}

			// String deviceID =
			// doc.get("://openfmb.org/xsd/2015/12/openfmb/commonmodule",
			// "*").item(0).getTextContent();

			// String[] dd = deviceID.split(Pattern.quote("."));
			// System.out.println(deviceID.split(Pattern.quote("."))[2]);
			//

			// NodeList nList =
			// doc.getElementsByTagName("ns12:value").item(0).getTextContent();

			// String nList =
			// doc.getElementsByTagName("ns12:value").item(0).getTextContent();
			//
			// for (int i = 0; i <= 20; i++) {
			//// System.out.println("ivalue" + i);
			//
			// String idV = "ns" + i + ":value";
			// String idU = "ns" + i + ":unit";
			//// System.out.println(idV);
			//
			// try {
			// doc.getElementsByTagName(idV).item(0).getTextContent();
			//
			//
			// double[] values = new
			// double[doc.getElementsByTagName(idV).getLength()];
			// String[] units = new
			// String[doc.getElementsByTagName(idV).getLength()];
			//
			// for(int j=0; j<doc.getElementsByTagName(idV).getLength(); j++){
			// values[j] =
			// Double.parseDouble(doc.getElementsByTagName(idV).item(j).getTextContent());
			// units[j] =
			// doc.getElementsByTagName(idU).item(j).getTextContent();
			// }
			//
			// System.out.println(Arrays.toString(values));
			// System.out.println(Arrays.toString(units));
			//
			//
			//
			//
			//// System.out.println(doc.getElementsByTagName(idV).getLength());
			//// for(int j=0; j<doc.getElementsByTagName(idV).getLength(); j++){
			//// System.out.println("aa");
			//// System.out.println(doc.getElementsByTagName(idV).item(j).getTextContent());
			//// }
			//// System.out.println("aa");
			// break;
			// } catch (Exception e) {
			//// System.out.println("bb");
			// }
			// //
			// if(doc.getElementsByTagName(idV).item(0).getTextContent()==null){
			// // System.out.println("aa");
			// // }else{
			// // System.out.println("bb");
			// // }
			// }

			// System.out.println(nList);

			// System.out.println(nList.item(0).getTextContent());
			//
			// nList.item(0).setTextContent("1000.1");
			//
			// write the content on console
			// @@@@@@@@@@
			// TransformerFactory transformerFactory =
			// TransformerFactory.newInstance();
			// Transformer transformer = transformerFactory.newTransformer();
			// DOMSource source = new DOMSource(doc);
			// System.out.println("-----------Modified File-----------");
			// // StreamResult consoleResult = new StreamResult(System.out);
			// // transformer.transform(source, consoleResult);
			//
			// StringWriter sw = new StringWriter();
			// transformer.transform(source, new StreamResult(sw));
			//
			// String aa = sw.toString();
			// System.out.println(aa);

			// NodeList nList = doc.getElementsByTagNameNS("n2",
			// localName)("student");
			//

			// System.out.println("----------------------------");
			//
			// for (int temp = 0; temp < nList.getLength(); temp++) {
			// Node nNode = nList.item(temp);
			// System.out.println("\nCurrent Element :" + nNode.getNodeName());
			//
			// if (nNode.getNodeType() == Node.ELEMENT_NODE) {
			// Element eElement = (Element) nNode;
			// System.out.println("Student roll no : " +
			// eElement.getAttribute("rollno"));
			// System.out.println(
			// "First Name : " +
			// eElement.getElementsByTagName("firstname").item(0).getTextContent());
			// System.out.println(
			// "Last Name : " +
			// eElement.getElementsByTagName("lastname").item(0).getTextContent());
			// System.out.println(
			// "Nick Name : " +
			// eElement.getElementsByTagName("nickname").item(0).getTextContent());
			// System.out.println("Marks : " +
			// eElement.getElementsByTagName("marks").item(0).getTextContent());
			// }
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
