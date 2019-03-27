package com.mir.smartgrid.simulator.main;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLException;

import com.mir.ems.coap.CoAPServer;
import com.mir.smartgrid.simulator.controller.Controller;
import com.mir.smartgrid.simulator.global.Global;
import com.mir.smartgrid.simulator.gui.InitialFrame;

import io.netty.example.http.snoop.HttpSnoopClient;

public class Main {

	public InitialFrame initialFrame;
	static String id = "";
	static String protocol = "";

	public static void main(String[] args) throws IOException, InterruptedException {

		while (true) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("GUI MODE ON?(Y/N)");

			String flag = br.readLine();

			if (flag.matches("Y|y")) {
				Main main = new Main();
				main.initialFrame = new InitialFrame();
				main.initialFrame.setMain(main);
				break;
			} else if (flag.matches("N|n")) {

				parseCFG();

				if (Global.CLIENTOPTION) {

					new CoAPServer();
					ExecutorService executorService = Executors.newFixedThreadPool(2);
					executorService.execute(new Controller(id, protocol, Global.getProfileType()));

				} else {
					
					for (int i = Global.startNum; i <= Global.endNum; i++) {
						ExecutorService executorService = Executors
								.newFixedThreadPool(Global.endNum - Global.startNum + 2);
						Thread.sleep(100);
						executorService.execute(new Controller("DEVICE" + i, protocol, Global.getProfileType()));
					}
					
				}
				
				break;
			}

		}

	}

	public void showFrame() {
		initialFrame.dispose();

		EventQueue.invokeLater(new Runnable() {
			public void run() {

				// UI가 추가되어야 한다면 이곳에서 추가해야한다. Virtual Agent에 대한 Setting
				System.out.println(Global.getVirtualAgentNum());
				System.out.println(Global.getBrokerIP());
				System.out.println(Global.getParentnNodeID());
				System.out.println(Global.getCoapServerIP());
				System.out.println(Global.getProfileType());
				///////////////////////////////////////////////////////////////
				if (Global.autoRDR) {
					rdrIDFileIO();
				}
				// Thread Poll Start, Using Fixed Thread
				int virualAgentNum = Global.getVirtualAgentNum();
				ExecutorService executorService = Executors.newFixedThreadPool(virualAgentNum);

				String id = "CLIENT_EMA";

				if (Global.parentnNodeID.contains("CLIENT")) {
					id = "DEVICE";
					Global.reportType = "Implicit";
				} else {
					Global.reportType = "Explicit";
				}

				if (Global.getProfileType().equals("EMAP")) {
					for (int i = 0; i < virualAgentNum; i++) {
						executorService.execute(new Controller("DEVICE" + (i + 1)));
					}
				}

				else if (Global.getProfileType().equals("OpenADR2.0b")) {

					// for (int i = Global.startNum; i < Global.endNum; i++) {
					//
					// System.out.println("CLIENTEMA" + (i + 1));
					// try {
					// if (i == 0)
					// Thread.sleep(500);
					// else
					// Thread.sleep(50);
					//
					// executorService
					// .execute(new Controller(id + (i + 1), "COAP",
					// Global.getProfileType()));
					// } catch (InterruptedException e) {
					// // TODO Auto-generated catch block
					// e.printStackTrace();
					// }
					// }

					try {
						new HttpSnoopClient(Global.startNum, Global.endNum).start();
					} catch (SSLException | URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				else if (Global.getProfileType().equals("EMAP1.0b")) {

					for (int i = Global.startNum; i <= Global.endNum; i++) {

						System.out.println(id + i);

						try {
							Thread.sleep(1);

							executorService.execute(new Controller(id + i, "COAP", Global.getProfileType()));

						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}

			}
		});
	}

	static void rdrIDFileIO() {

		FileReader fileReader;
		try {
			fileReader = new FileReader("RDRID.txt");

			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String type = null;

			while ((type = bufferedReader.readLine()) != null) {

				int seq = Integer.parseInt(type.split("=>")[0]);
				String eventID = type.split("/")[1];

				Global.optIDMap.put(seq + "", eventID);

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("RDRIDFILE이 없습니다.");
			e.printStackTrace();
		}

	}

	static void optIDbuild() {

		String flag = Global.parentnNodeID;
		flag = flag.replaceAll("SERVER_EM", "");

		for (int i = 0; i < 60; i++) {

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String optID = System.currentTimeMillis() + "";

			Global.optIDMap.put(i + "", flag + optID);

		}

		FileWriter fw;

		String rdr = Global.autoRDR ? "rdr" : "";
		String rdrCnt = Global.RDRRequestNum + "";
		String optionName = "_" + rdr + rdrCnt;
		String fileName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-SS").format(System.currentTimeMillis());

		try {

			fw = new FileWriter(new File(Global.parentnNodeID + "_" + fileName + optionName + ".txt"));

			for (int i = 0; i < 60; i++) {
				String key = i + "";
				fw.write(String.format("%s=>ID:/%s", key, Global.optIDMap.get(key).toString()));
				fw.write(System.lineSeparator());
			}

			fw.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	static void parseCFG() {

		try {
			FileReader fileReader = new FileReader("sim.cfg");

			BufferedReader bufferedReader = new BufferedReader(fileReader);

			id = bufferedReader.readLine().split(":")[1];
			Global.SYSTEMID = id;
			protocol = bufferedReader.readLine().split(":")[1];

			if (protocol.equals("MQTT")) {
				Global.brokerIP = bufferedReader.readLine().split(":")[1];
				Global.brokerPort = bufferedReader.readLine().split(":")[1];
			} else if (protocol.equals("COAP")) {
				Global.coapServerIP = bufferedReader.readLine().split(":")[1];
				Global.coapServerPort = bufferedReader.readLine().split(":")[1];
			} else {
				Global.brokerIP = bufferedReader.readLine().split(":")[1];
				Global.brokerPort = bufferedReader.readLine().split(":")[1];
			}

			Global.parentnNodeID = bufferedReader.readLine().split(":")[1];
			Global.autoRDR = Boolean.parseBoolean(bufferedReader.readLine().split(":")[1]);
			Global.RDRInterval = Integer.parseInt(bufferedReader.readLine().split(":")[1]);
			Global.RDRRequestNum = Integer.parseInt(bufferedReader.readLine().split(":")[1]);
			Global.pullModel = Boolean.parseBoolean(bufferedReader.readLine().split(":")[1]);
			Global.profileType = bufferedReader.readLine().split(":")[1];
			Global.reportType = bufferedReader.readLine().split(":")[1];
			Global.pollinginterval = Integer.parseInt(bufferedReader.readLine().split(":")[1]);
			Global.reportinterval = Integer.parseInt(bufferedReader.readLine().split(":")[1]);
			Global.summaryObs = Boolean.parseBoolean(bufferedReader.readLine().split(":")[1]);
			Global.vtnURL = bufferedReader.readLine().split("=")[1];
			Global.eventURL = bufferedReader.readLine().split("=")[1];
			Global.CLIENTOPTION = Boolean.parseBoolean(bufferedReader.readLine().split(":")[1]);
			Global.startNum = Integer.parseInt(bufferedReader.readLine().split(":")[1]);
			Global.endNum = Integer.parseInt(bufferedReader.readLine().split(":")[1]);

			System.out.println("ID\t\t\t\t" + id);
			System.out.println("PROTOCOL\t\t\t" + protocol);
			if (protocol.equals("MQTT")) {
				System.out.println("BROKER IP\t\t\t" + Global.brokerIP);
				System.out.println("BROKER PORT\t\t\t" + Global.brokerPort);
			} else if (protocol.equals("COAP")) {
				System.out.println("COAP SERVER IP\t\t\t" + Global.coapServerIP);
				System.out.println("COAP SERVER PORT\t\t" + Global.coapServerPort);
			}

			System.out.println("SERVER ID\t\t\t" + Global.parentnNodeID);
			System.out.println("AUTO RDR\t\t\t" + Global.autoRDR);
			System.out.println("AUTO RDR num \t\t\t" + Global.RDRRequestNum);
			System.out.println("Pull MODEL\t\t\t" + Global.pullModel);
			System.out.println("ProfileType\t\t\t" + Global.profileType);
			System.out.println("REPORT TYPE\t\t\t" + Global.reportType);
			System.out.println("Polling Interval\t\t\t" + Global.pollinginterval);
			System.out.println("Report Interval\t\t\t" + Global.reportinterval);
			System.out.println("SummaryObs\t\t\t" + Global.summaryObs);
			System.out.println("vtnURL\t\t\t" + Global.vtnURL);
			System.out.println("eventURL\t\t\t" + Global.eventURL);
			bufferedReader.close();

			if (Global.autoRDR) {
				rdrIDFileIO();
			}

		} catch (Exception e) {
			System.out.println("CAN NOT FIND CONFIG FILE(sim.cfg), Check and Restart");
			System.exit(-1);
		}

	}

}
