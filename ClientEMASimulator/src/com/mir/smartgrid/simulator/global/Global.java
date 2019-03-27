package com.mir.smartgrid.simulator.global;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.mir.ems.coap.CoAPObserverSubPath;
import com.mir.smartgrid.simulator.devProfile.DeviceProfile;
import com.mir.smartgrid.simulator.devProfile.EMAP_CoAP_EMA_DR;
import com.mir.smartgrid.simulator.devProfile.Emap_Cema_Profile;

public class Global {
	public static boolean CLIENTOPTION = false;
	public static int autoDRCNT = 0;
	public static int autoDRTOTAL = 60;
	public static int RDRInterval = 1000;
	public static boolean autoDR = false;

	public static String optID = ""; 
	public static ConcurrentHashMap<String, String> optIDMap = new ConcurrentHashMap<String, String>();
	public static boolean eventFromServer = false;

	public static boolean summaryObs = false;
	public static boolean autoRDR = false;
	public static int TERMINATE_CNT = 0;
	public static int RDRRequestNum = 1;
	public static int reportinterval = 1000;
	public static int pollinginterval = 1000;
	public static int summaryInterval = 9000;
	
	public static int virtualAgentNum = 1;
	public static String profile = "EMAP1.0b";
	public static String eventURL = "http://166.104.28.51:12345/EventObserve";
	public static String vtnURL = "http://166.104.28.51:8080/OpenADR2/Simple/2.0b/";
	public static String SYSTEMID = "SERVER_EMA1";
	public static String parentnNodeID = "SERVER_EMA1";
	public static String brokerIP = "166.104.28.51";
	public static String brokerPort = "1883";
	public static String coapServerIP = "166.104.28.51";
	public static String coapServerPort = "5683";
	public static String profileType = "EMAP";
	public static String version = "1.0b";
	public static String openADRVersion = "2.0b";
	public static boolean pullModel = true;
	public static int startNum = 0;
	public static int endNum = 0;

	// Default Explicit
	// Option = 'Explicit', 'Implicit'
	public static String reportType = "Implicit";

	// VEN ¿‘¿Â
	public static int venRegisterSeqNumber = 0;
	
	public static ConcurrentHashMap<Integer, String> eventID= new ConcurrentHashMap<Integer, String>();
	public static ConcurrentHashMap<Integer, String> rdrID= new ConcurrentHashMap<Integer, String>();
	public static ConcurrentHashMap<String, String> emaConfig = new ConcurrentHashMap<String, String>();
	public static ConcurrentHashMap<String, String> cemaReportType = new ConcurrentHashMap<String, String>();
	public static ConcurrentHashMap<String, DeviceProfile> devProfile = new ConcurrentHashMap<String, DeviceProfile>();

	public static ConcurrentHashMap<String, Emap_Cema_Profile> emaProtocolCoAP = new ConcurrentHashMap<String, Emap_Cema_Profile>();
	public static HashMap<String, EMAP_CoAP_EMA_DR> obs_emaProtocolCoAP_EventFlag = new HashMap<String, EMAP_CoAP_EMA_DR>();
	public static ConcurrentHashMap<String, CoAPObserverSubPath> observeManager = new ConcurrentHashMap<>();
	public static HashMap<String, EMAP_CoAP_EMA_DR> emaProtocolCoAP_EventFlag = new HashMap<String, EMAP_CoAP_EMA_DR>();

	
	public static final String[] MICROGRIDPROFILETYPE = new String[] { "ReadingProfile", "EventProfile" };
	public static final String[] MICROGRIDPROFILE = new String[] { "SolarReadingProfile", "BatteryReadingProfile",
			"RecloserReadingProfile" };
	public static final String[] MICROGRIDEVENT = new String[] { "BatteryEventProfile", "RecloserEventProfile" };

	public static final int BATTERCAPACITY = 250;

	public static String getProfileType() {
		return profileType;
	}

	public static void setProfileType(String profileType) {
		Global.profileType = profileType;
	}

	public static String getParentnNodeID() {
		return parentnNodeID;
	}

	public static void setParentnNodeID(String parentnNodeID) {
		Global.parentnNodeID = parentnNodeID;
	}

	public static String getBrokerIP() {
		return brokerIP;
	}

	public static void setBrokerIP(String brokerIP) {
		Global.brokerIP = brokerIP;
	}

	public static String getCoapServerIP() {
		return coapServerIP;
	}

	public static void setCoapServerIP(String coapServerIP) {
		Global.coapServerIP = coapServerIP;
	}

	public static int getVirtualAgentNum() {
		return virtualAgentNum;
	}

	public static void setVirtualAgentNum(int virtualAgentNum) {
		Global.virtualAgentNum = virtualAgentNum;
	}

	public static int getStartNum() {
		return startNum;
	}

	public static void setStartNum(int startNum) {
		Global.startNum = startNum;
	}

	public static int getEndNum() {
		return endNum;
	}

	public static void setEndNum(int endNum) {
		Global.endNum = endNum;
	}
	
	
	

}
