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

	// profileType ?΄??€ ?? λ§λ€?΄? oadrProfileType Objectλ‘? λ³?κ²?
	// transportType ?΄??€ ?? λ§λ€?΄? oadrTransportType Objectλ‘? λ³?κ²?
	public String CreatePartyRegistration(String VEN_NAME, String profileName, String transportName, String transportAddress,
			boolean reportOnly, boolean xmlSignature, boolean httpPullModel, String requestID)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	// response ?΄??€ ?? λ§λ€?΄? responses Objectλ‘? λ³?κ²?, ?΄λ²€νΈ? μ°Έμ¬? μ§? optIn, optOut ?¬λΆ?λ₯?
	// κ²°μ ?? ?΄??€λ‘? ??±??€.
	public String CreatedEvent(String HASHED_VEN_NAME, String responseCode, String responseDescription, String eventID,
			String modificationNumber, String requestID)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	// UpdateReport?? κ³΅μ ? ? ?? oadrReport ?Έ?°??΄?€λ₯? ??λ§λ€κ³? κ·Έκ±Έ κ΅¬ν??€? ?¬κΈ°λ€κ°? ?£? ??λ‘?
	// public String RegisterReport(String oadrReport, String requestID)
	// throws ParserConfigurationException, SAXException, IOException,
	// TransformerException;

	public String RegisteredReport(String HASHED_VEN_NAME, String requestID, String responseCode, String responseDescription)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	// dtStart?? createdDateTime?? Time?Ή?? Date Date Type?Όλ‘? λ³?κ²½ν΄μ£Όμ΄?Ό ??€.
	public String UpdateReport(String rID, double value, String dtStart, String createdDateTime, String requestID)
			throws ParserConfigurationException, SAXException, IOException, TransformerException;

	public String UpdateReport(String HASHED_VEN_NAME) throws ParserConfigurationException, SAXException, IOException, TransformerException;

	// optSchedule ?΄??€ ?? λ§λ€?΄? optSchedule Objectλ‘? λ³?κ²?
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
