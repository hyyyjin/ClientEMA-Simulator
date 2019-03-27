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

	// profileType ?Å¥?ûò?ä§ ?ïò?Çò ÎßåÎì§?ñ¥?Ñú oadrProfileType ObjectÎ°? Î≥?Í≤?
	// transportType ?Å¥?ûò?ä§ ?ïò?Çò ÎßåÎì§?ñ¥?Ñú oadrTransportType ObjectÎ°? Î≥?Í≤?
	public String CreatePartyRegistration(String VEN_NAME, String profileName, String transportName, String transportAddress,
			boolean reportOnly, boolean xmlSignature, boolean httpPullModel, String requestID)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	// response ?Å¥?ûò?ä§ ?ïò?Çò ÎßåÎì§?ñ¥?Ñú responses ObjectÎ°? Î≥?Í≤?, ?ù¥Î≤§Ìä∏?óê Ï∞∏Ïó¨?ï†Ïß? optIn, optOut ?ó¨Î∂?Î•?
	// Í≤∞Ï†ï?ïò?äî ?Å¥?ûò?ä§Î°? ?Éù?Ñ±?ïú?ã§.
	public String CreatedEvent(String HASHED_VEN_NAME, String responseCode, String responseDescription, String eventID,
			String modificationNumber, String requestID)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	// UpdateReport?? Í≥µÏú†?ï†?àò ?ûà?äî oadrReport ?ù∏?Ñ∞?éò?ù¥?ä§Î•? ?ïò?ÇòÎßåÎì§Í≥? Í∑∏Í±∏ Íµ¨ÌòÑ?ïú?í§?óê ?ó¨Í∏∞Îã§Í∞? ?Ñ£?äî ?òï?ÉúÎ°?
	// public String RegisterReport(String oadrReport, String requestID)
	// throws ParserConfigurationException, SAXException, IOException,
	// TransformerException;

	public String RegisteredReport(String HASHED_VEN_NAME, String requestID, String responseCode, String responseDescription)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	// dtStart?? createdDateTime?? Time?òπ?? Date Date Type?úºÎ°? Î≥?Í≤ΩÌï¥Ï£ºÏñ¥?ïº ?ïú?ã§.
	public String UpdateReport(String rID, double value, String dtStart, String createdDateTime, String requestID)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	public String UpdateReport(String HASHED_VEN_NAME) throws ParserConfigurationException, SAXException, IOException, TransformerException;

	// optSchedule ?Å¥?ûò?ä§ ?ïò?Çò ÎßåÎì§?ñ¥?Ñú optSchedule ObjectÎ°? Î≥?Í≤?
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
