package com.mir.smartgrid.simulator.global;

import javax.xml.parsers.DocumentBuilder;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.mir.smartgrid.simulator.ven.TimeFormat;

public class InitParser {

	public static String queryRegistration;
	public static String createPartyRegistration;
	public static String createdEvent;
	public static String registerReport;
	public static String registeredReport;
	public static String requestEvent;
	public static String updateReport;
	public static String createOptSchedule;
	public static String poll;

	public InitParser(){
		try {
			QueryRegistration();
			CreatePartyRegistration();
			CreatedEvent();
			RegisterReport();
			RegisteredReport();
			RequestEvent();
			UpdateReport() ;
			Poll();
			CreateOptSchedule();
		} catch (ParserConfigurationException | SAXException | IOException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void QueryRegistration()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new File("OpenADR_VEN_XML/oadrQueryRegistration.xml"));

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		StringWriter sw = new StringWriter();
		transformer.transform(source, new StreamResult(sw));

		queryRegistration = sw.toString();

	}

	public void CreatePartyRegistration()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new File("OpenADR_VEN_XML/oadrCreatePartyRegistration.xml"));

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		StringWriter sw = new StringWriter();
		transformer.transform(source, new StreamResult(sw));

		createPartyRegistration = sw.toString();

	}

	public void CreatedEvent() throws ParserConfigurationException, SAXException, IOException, TransformerException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new File("OpenADR_VEN_XML/oadrCreatedEvent_1.xml"));

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		StringWriter sw = new StringWriter();
		transformer.transform(source, new StreamResult(sw));

		createdEvent = sw.toString();
	}

	public void RegisterReport() throws ParserConfigurationException, SAXException, IOException, TransformerException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new File("OpenADR_VEN_XML/oadrRegisterReport_1.xml"));

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		StringWriter sw = new StringWriter();
		transformer.transform(source, new StreamResult(sw));

		registerReport = sw.toString();
	}

	public void RegisteredReport()
			throws ParserConfigurationException, SAXException, IOException, TransformerException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new File("OpenADR_VEN_XML/oadrRegisteredReport.xml"));

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		StringWriter sw = new StringWriter();
		transformer.transform(source, new StreamResult(sw));

		registeredReport = sw.toString();
	}

	public void RequestEvent() throws ParserConfigurationException, SAXException, IOException, TransformerException {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new File("OpenADR_VEN_XML/oadrRequestEvent.xml"));

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		StringWriter sw = new StringWriter();
		transformer.transform(source, new StreamResult(sw));

		requestEvent = sw.toString();
	}

	public void UpdateReport() throws ParserConfigurationException, TransformerException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		dbf.setNamespaceAware(true);

		dbf.setIgnoringElementContentWhitespace(true);

		dbf.setValidating(false);

		DocumentBuilder db = dbf.newDocumentBuilder();

		Document doc = db.newDocument();

		Element Envelope = doc.createElement("p1:oadrPayload");
		Envelope.setAttribute("xmlns:p1", "http://openadr.org/oadr-2.0b/2012/07");

		Element Header = createElement("p1:oadrSignedObject", Envelope, doc);

		Element OadrUpdateReport = createElement("p1:oadrUpdateReport", Header, doc);
		OadrUpdateReport.setAttribute("xmlns:p3", "http://docs.oasis-open.org/ns/energyinterop/201110");
		OadrUpdateReport.setAttribute("p3:schemaVersion", "2.0b");
		OadrUpdateReport.setAttribute("xmlns:p2", "http://docs.oasis-open.org/ns/energyinterop/201110/payloads");

		createElement("p2:requestID", OadrUpdateReport, doc, "MIR_REQ_ID");
		createElement("p3:venID", OadrUpdateReport, doc, "AA");

		// Iterator<String> it = global.emaProtocolCoAP.keySet().iterator();

		double power = 0;

		// while (it.hasNext()) {
		// String key = it.next();
		// power += global.emaProtocolCoAP.get(key).getPower();
		// }

		Element oadrReport = createElement("p1:oadrReport", OadrUpdateReport, doc);
		oadrReport.setAttribute("xmlns:p3", "urn:ietf:params:xml:ns:icalendar-2.0:stream");
		oadrReport.setAttribute("xmlns:p4", "http://docs.oasis-open.org/ns/energyinterop/201110");

		createElement("p4:reportRequestID", oadrReport, doc, "MIR_REPORT_REQ_ID");
		createElement("p4:reportSpecifierID", oadrReport, doc, "MIR_REPORT_SPEC_ID");
		createElement("p4:reportName", oadrReport, doc, "TELEMETRY_USAGE");
		createElement("p4:createdDateTime", oadrReport, doc, new TimeFormat().getCurrentTime());

		Element intervals = createElement("p3:intervals", oadrReport, doc);
		intervals.setAttribute("xmlns:p4", "http://docs.oasis-open.org/ns/energyinterop/201110");

		Element interval = createElement("p4:interval", intervals, doc);
		interval.setAttribute("xmlns:p5", "urn:ietf:params:xml:ns:icalendar-2.0");

		Element dtstart = createElement("p5:dtstart", interval, doc);
		createElement("p5:date-time", dtstart, doc, new TimeFormat().getCurrentTime());

		Element duration = createElement("p5:duration", interval, doc);
		createElement("p5:duration", duration, doc, "PT1S");

		Element oadrReportPayload = createElement("p1:oadrReportPayload", interval, doc);
		createElement("p4:rID", oadrReportPayload, doc, "AA");
		createElement("p4:confidence", oadrReportPayload, doc, "100");
		createElement("p4:accuracy", oadrReportPayload, doc, "100");

		Element payloadFloat = createElement("p1:payloadFloat", oadrReportPayload, doc);
		createElement("p4:value", payloadFloat, doc, power + "");
		createElement("p1:oadrDataQuality", oadrReportPayload, doc, "Quality Good - Non Specific");
		//
		// if (global.reportType.equals("Explicit")) {
		//
		// it = global.emaProtocolCoAP.keySet().iterator();
		//
		// while (it.hasNext()) {
		//
		// String key = it.next();
		//
		// oadrReport = createElement("p1:oadrReport", OadrUpdateReport, doc);
		// oadrReport.setAttribute("xmlns:p3",
		// "urn:ietf:params:xml:ns:icalendar-2.0:stream");
		// oadrReport.setAttribute("xmlns:p4",
		// "http://docs.oasis-open.org/ns/energyinterop/201110");
		//
		// createElement("p4:reportRequestID", oadrReport, doc,
		// "MIR_REPORT_REQ_ID");
		// createElement("p4:reportSpecifierID", oadrReport, doc,
		// "MIR_REPORT_SPEC_ID");
		// createElement("p4:reportName", oadrReport, doc, "TELEMETRY_USAGE");
		// createElement("p4:createdDateTime", oadrReport, doc, new
		// TimeFormat().getCurrentTime());
		//
		// intervals = createElement("p3:intervals", oadrReport, doc);
		// intervals.setAttribute("xmlns:p4",
		// "http://docs.oasis-open.org/ns/energyinterop/201110");
		//
		// interval = createElement("p4:interval", intervals, doc);
		// interval.setAttribute("xmlns:p5",
		// "urn:ietf:params:xml:ns:icalendar-2.0");
		//
		// dtstart = createElement("p5:dtstart", interval, doc);
		// createElement("p5:date-time", dtstart, doc, new
		// TimeFormat().getCurrentTime());
		//
		// duration = createElement("p5:duration", interval, doc);
		// createElement("p5:duration", duration, doc, global.duration);
		//
		// oadrReportPayload = createElement("p1:oadrReportPayload", interval,
		// doc);
		// createElement("p4:rID", oadrReportPayload, doc, key);
		// createElement("p4:confidence", oadrReportPayload, doc, "100");
		// createElement("p4:accuracy", oadrReportPayload, doc, "100");
		//
		// payloadFloat = createElement("p1:payloadFloat", oadrReportPayload,
		// doc);
		// createElement("p4:value", payloadFloat, doc,
		// global.emaProtocolCoAP.get(key).getPower() + "");
		// createElement("p1:oadrDataQuality", oadrReportPayload, doc, "Quality
		// Good - Non Specific");
		//
		// }
		// }

		StringWriter sw = new StringWriter();

		StreamResult result = new StreamResult(sw);

		DOMSource source = new DOMSource(Envelope);

		TransformerFactory tf = TransformerFactory.newInstance();

		Transformer transformer = tf.newTransformer();

		transformer.transform(source, result);

		String xmlString = sw.toString();

		updateReport = xmlString;
	}

	public void CreateOptSchedule() throws ParserConfigurationException, SAXException, IOException,
			TransformerException, DOMException, TransformerFactoryConfigurationError {

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new File("OpenADR_VEN_XML/oadrCreateOpt.xml"));

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		StringWriter sw = new StringWriter();
		transformer.transform(source, new StreamResult(sw));

		createOptSchedule = sw.toString();

	}

	public void CancelOptSchedule(String optID, String requestID) {

	}

	public void Poll() throws ParserConfigurationException, SAXException, IOException, TransformerException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new File("OpenADR_VEN_XML/oadrPoll.xml"));

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		StringWriter sw = new StringWriter();
		transformer.transform(source, new StreamResult(sw));

		poll = sw.toString();
	}

	private Element createElement(String name, Element el, Document doc) {

		Element element = doc.createElement(name);

		el.appendChild(element);

		return element;

	}

	private void createElement(String name, Element el, Document doc, String value) {

		Element element = doc.createElement(name);

		el.appendChild(element);

		if (!value.equals("")) {

			element.setTextContent(value);

		}

	}
	
	public static void main(String[]args) throws ParserConfigurationException, SAXException, IOException{
		
		
		new InitParser();

		System.out.println(InitParser.queryRegistration);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	    InputSource is = new InputSource(new StringReader(InitParser.queryRegistration));
		Document doc = dBuilder.parse(is);

		NodeList nodes = doc.getDocumentElement().getElementsByTagNameNS("*", "*");

		for (int i = 0; i < nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if (node.getNodeName().contains("requestID")) {
				node.setTextContent("aa");
			}
		}

		
	}

}
