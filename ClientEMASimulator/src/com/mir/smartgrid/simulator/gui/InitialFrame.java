package com.mir.smartgrid.simulator.gui;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import com.mir.smartgrid.simulator.global.Global;
import com.mir.smartgrid.simulator.main.Main;
import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class InitialFrame extends JFrame {
	private Main main;
	private JTextField agentNumText;
	private JTextField brokerIPText;
	private JTextField coapServerIpText;
	private JTextField parentNodeIDText;
	private JTextField startNumText;
	private JTextField endNumText;
	private JTextField rdrRequestNumText;
	public JCheckBox rdrCheckBox;
	public InitialFrame() {
		setTitle("MIR_Simulator");
		setSize(308, 439);
		setResizable(false);
		setLocation(800, 450);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);

		agentNumText = new JTextField();
		agentNumText.setHorizontalAlignment(SwingConstants.CENTER);
		agentNumText.setText("1");
		agentNumText.setBounds(171, 101, 116, 21);
		getContentPane().add(agentNumText);
		agentNumText.setColumns(10);

		brokerIPText = new JTextField();
		brokerIPText.setHorizontalAlignment(SwingConstants.CENTER);
		brokerIPText.setText("166.104.28.131");
		brokerIPText.setBounds(171, 145, 116, 21);
		getContentPane().add(brokerIPText);
		brokerIPText.setColumns(10);

		JLabel agentNumLbl = new JLabel("Agent Number");
		agentNumLbl.setFont(new Font("Times New Roman", Font.BOLD, 12));
		agentNumLbl.setBounds(12, 101, 147, 15);
		getContentPane().add(agentNumLbl);

		JLabel brokerIPLbl = new JLabel("MQTT Broker IP");
		brokerIPLbl.setFont(new Font("Times New Roman", Font.BOLD, 12));
		brokerIPLbl.setBounds(12, 145, 147, 15);
		getContentPane().add(brokerIPLbl);

		JLabel coapServerLbl = new JLabel("CoAP Server IP");
		coapServerLbl.setFont(new Font("Times New Roman", Font.BOLD, 12));
		coapServerLbl.setBounds(12, 189, 147, 15);
		getContentPane().add(coapServerLbl);

		coapServerIpText = new JTextField();
		coapServerIpText.setText("NULL");
		coapServerIpText.setHorizontalAlignment(SwingConstants.CENTER);
		coapServerIpText.setBounds(171, 189, 116, 21);
		getContentPane().add(coapServerIpText);
		coapServerIpText.setColumns(10);

		JLabel profileLbl = new JLabel("Profile Type");
		profileLbl.setFont(new Font("Times New Roman", Font.BOLD, 12));
		profileLbl.setBounds(12, 233, 147, 15);
		getContentPane().add(profileLbl);

		final JComboBox<String> profileComboBox = new JComboBox<String>();
		profileComboBox.setBounds(171, 229, 116, 21);
		getContentPane().add(profileComboBox);
		
		profileComboBox.addItem("OpenADR2.0b");
		profileComboBox.addItem("EMAP1.0b");
//		profileComboBox.addItem("EMAP");
//		profileComboBox.addItem("OpenADR2.0b");
		profileComboBox.setSelectedIndex(1);

		JLabel mirIcon = new JLabel("");
		mirIcon.setBounds(210, 6, 77, 19);
		getContentPane().add(mirIcon);

		JButton okButton = new JButton("OK");
		okButton.setBounds(102, 377, 97, 23);
		getContentPane().add(okButton);
		
		JLabel lblParentNodeId = new JLabel("Parent Node ID");
		lblParentNodeId.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblParentNodeId.setBounds(12, 57, 147, 15);
		getContentPane().add(lblParentNodeId);
		
		parentNodeIDText = new JTextField();
		parentNodeIDText.setText("SERVER_EMA2");
		parentNodeIDText.setHorizontalAlignment(SwingConstants.CENTER);
		parentNodeIDText.setColumns(10);
		parentNodeIDText.setBounds(171, 57, 116, 21);
		getContentPane().add(parentNodeIDText);
		
		JLabel lblStartnumber = new JLabel("StartNumber");
		lblStartnumber.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblStartnumber.setBounds(12, 269, 147, 15);
		getContentPane().add(lblStartnumber);
		
		startNumText = new JTextField();
		startNumText.setText("1");
		startNumText.setHorizontalAlignment(SwingConstants.CENTER);
		startNumText.setColumns(10);
		startNumText.setBounds(171, 269, 116, 21);
		getContentPane().add(startNumText);
		
		JLabel lblEndnumber = new JLabel("EndNumber");
		lblEndnumber.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblEndnumber.setBounds(12, 310, 147, 15);
		getContentPane().add(lblEndnumber);
		
		endNumText = new JTextField();
		endNumText.setText("1");
		endNumText.setHorizontalAlignment(SwingConstants.CENTER);
		endNumText.setColumns(10);
		endNumText.setBounds(171, 310, 116, 21);
		getContentPane().add(endNumText);
		
		JLabel lblRdrrequestnum = new JLabel("RDR_RequestNum");
		lblRdrrequestnum.setFont(new Font("Times New Roman", Font.BOLD, 12));
		lblRdrrequestnum.setBounds(12, 346, 147, 15);
		getContentPane().add(lblRdrrequestnum);
		
		rdrRequestNumText = new JTextField();
		rdrRequestNumText.setText("1");
		rdrRequestNumText.setHorizontalAlignment(SwingConstants.CENTER);
		rdrRequestNumText.setColumns(10);
		rdrRequestNumText.setBounds(171, 346, 116, 21);
		getContentPane().add(rdrRequestNumText);
		
		rdrCheckBox = new JCheckBox("AutoRDR");
		rdrCheckBox.setBounds(203, 6, 83, 23);
		getContentPane().add(rdrCheckBox);

		
		
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				int virtualAgentNum = Integer.parseInt(agentNumText.getText());
				int startNum = Integer.parseInt(startNumText.getText());
				int endNum = Integer.parseInt(endNumText.getText());
				int rdrRequestNum = Integer.parseInt(rdrRequestNumText.getText());
				String parentNodeID = parentNodeIDText.getText(), brokerIP = brokerIPText.getText(), coapServerIP = coapServerIpText.getText(), profile = profileComboBox.getSelectedItem().toString();

				isInitialCheck(parentNodeID, virtualAgentNum, brokerIP, coapServerIP, profile, startNum, endNum, rdrRequestNum);
			}
		});
		
		setVisible(true);

	}
	
	public void isInitialCheck(String parentNodeID, int virtualAgentNum, String brokerIP, String coapServerIP, String profile, int startNum, int endNum, int rdrRequestNum) {

		// SET MQTT BROKER IP & COAP SERVER IP
		Global.setParentnNodeID(parentNodeID);
		Global.setVirtualAgentNum(virtualAgentNum);
		Global.setBrokerIP(brokerIP);
		Global.setCoapServerIP(coapServerIP);
		Global.setProfileType(profile);
		Global.setStartNum(startNum);
		Global.setEndNum(endNum);
		Global.RDRRequestNum = rdrRequestNum;
		
		if(rdrCheckBox.isSelected()){
			Global.autoRDR = true;
		}
		
		main.showFrame();

	}

	public void setMain(Main main) {
		this.main = main;
	}
}
