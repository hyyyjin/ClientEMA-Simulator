package com.mir.ems.coap;

import java.net.InetAddress;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.config.NetworkConfig;
import com.mir.ems.coap.emap.Emap;
import com.mir.ems.coap.emap.OpenADR;

public class CoAPServer {

	public String gw;
	public String venID;
	public String vtnID = "MIR_VTN";
	public int requestID;
	public int version;
	public String Path;
	public String Text;

	public static int ven;
	public static int poll_seq = 0;
	public static InetAddress client_ip;

	public CoAPServer() {
		// TODO Auto-generated constructor stub
		CoapServer server = new CoapServer();

		// Profile
		server.add(new Emap("EMAP"));
		server.add(new OpenADR("OpenADR"));

		// Observer
		server.add(new CoAPObserver("OpenADR2.0b"));
		server.add(new CoAPObserver("EMAP1.0b"));

		setNetworkConfiguration();
		server.addEndpoint(new CoapEndpoint(5683, setNetworkConfiguration()));

		server.start();

	}

	private NetworkConfig setNetworkConfiguration() {

		return NetworkConfig.createStandardWithoutFile()
				.setString(NetworkConfig.Keys.DEDUPLICATOR, NetworkConfig.Keys.NO_DEDUPLICATOR)
				.setInt(NetworkConfig.Keys.PREFERRED_BLOCK_SIZE, 60000)
				.setInt(NetworkConfig.Keys.UDP_CONNECTOR_DATAGRAM_SIZE, 60000)
				.setInt(NetworkConfig.Keys.UDP_CONNECTOR_SEND_BUFFER, 60000)
				.setInt(NetworkConfig.Keys.UDP_CONNECTOR_RECEIVE_BUFFER, 60000)
				.setInt(NetworkConfig.Keys.NETWORK_STAGE_RECEIVER_THREAD_COUNT, 8)
				.setInt(NetworkConfig.Keys.MAX_MESSAGE_SIZE, 60000).setInt(NetworkConfig.Keys.EXCHANGE_LIFETIME, 1500);
	}

}
