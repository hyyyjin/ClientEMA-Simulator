package com.mir.smartgrid.simulator.ven;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

/*
 * Copyright (c) 2018, Hyunjin Park, Mobile Intelligence and Routing Lab
 * All rights reserved
 * 
 * 2018.07.05(Thu)
 * Edited by Hyunjin Park
 * Hanyang University
 * 
 */
public interface VEN2b {

	public String QueryRegistration(String requestID) throws ParserConfigurationException, SAXException, IOException,
			TransformerConfigurationException, TransformerException;

	// profileType ?��?��?�� ?��?�� 만들?��?�� oadrProfileType Object�? �?�?
	// transportType ?��?��?�� ?��?�� 만들?��?�� oadrTransportType Object�? �?�?
	public String CreatePartyRegistration(String VEN_NAME, String profileName, String transportName, String transportAddress,
			boolean reportOnly, boolean xmlSignature, boolean httpPullModel, String requestID)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	// response ?��?��?�� ?��?�� 만들?��?�� responses Object�? �?�?, ?��벤트?�� 참여?���? optIn, optOut ?���?�?
	// 결정?��?�� ?��?��?���? ?��?��?��?��.
	public String CreatedEvent(String HASHED_VEN_NAME, String responseCode, String responseDescription, String eventID,
			String modificationNumber, String requestID)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	// UpdateReport?? 공유?��?�� ?��?�� oadrReport ?��?��?��?��?���? ?��?��만들�? 그걸 구현?��?��?�� ?��기다�? ?��?�� ?��?���?
	// public String RegisterReport(String oadrReport, String requestID)
	// throws ParserConfigurationException, SAXException, IOException,
	// TransformerException;

	public String RegisteredReport(String HASHED_VEN_NAME, String requestID, String responseCode, String responseDescription)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	// dtStart?? createdDateTime?? Time?��?? Date Date Type?���? �?경해주어?�� ?��?��.
	public String UpdateReport(String rID, double value, String dtStart, String createdDateTime, String requestID)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	public String UpdateReport(String HASHED_VEN_NAME) throws ParserConfigurationException, SAXException, IOException, TransformerException;

	// optSchedule ?��?��?�� ?��?�� 만들?��?�� optSchedule Object�? �?�?
	public String CreateOptSchedule(String HASHED_VEN_NAME,String optReason, String optType, String dtStart, String optID)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	public void CancelOptSchedule(String optID, String requestID);

	public String RequestEvent(String HASHED_VEN_NAME) throws ParserConfigurationException, SAXException, IOException, TransformerException;

	public String Poll(String HASHED_VEN_NAME) throws ParserConfigurationException, SAXException, IOException, TransformerException;

	public String RegisterReport(String rID, String resourceID, String oadrMinPeriod, String oadrMaxPeriod,
			String oadrOnChange, String marketContext, String reportRequestID, String reportSpecifierID,
			String reportName, String createdDateTime, String venID)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	public String RegisterReport(String createdDateTime, String venID)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

}
