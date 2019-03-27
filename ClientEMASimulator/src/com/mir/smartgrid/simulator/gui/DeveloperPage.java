package com.mir.smartgrid.simulator.gui;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class DeveloperPage extends JFrame{

	public DeveloperPage(){
		
		java.net.URL url = DeveloperPage.class.getResource("/IMAGE/Developer.jpg");

		
		setTitle("About Developer");
		setSize(300, 412);
		setResizable(false);
		setLocation(800, 450);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		JLabel lblNewLabel = new JLabel("HYUNJIN PARK");
		lblNewLabel.setBounds(12, 236, 127, 15);
		getContentPane().add(lblNewLabel);
		
		JLabel lblMsCandidate = new JLabel("M.S Candidate");
		lblMsCandidate.setBounds(12, 251, 113, 15);
		getContentPane().add(lblMsCandidate);
		
		JLabel lblMobileIntelligence = new JLabel("Mobile Intelligence & Routing LAB");
		lblMobileIntelligence.setBounds(12, 276, 213, 15);
		getContentPane().add(lblMobileIntelligence);
		
		JLabel lblDepartmentOfComputer = new JLabel("Department of Computer Software");
		lblDepartmentOfComputer.setBounds(12, 288, 213, 15);
		getContentPane().add(lblDepartmentOfComputer);
		
		JLabel lblHanyangUniversity = new JLabel("Hanyang University");
		lblHanyangUniversity.setBounds(12, 301, 175, 15);
		getContentPane().add(lblHanyangUniversity);
		
		JLabel lblEmailMyhouenavercom = new JLabel("E-mail : myhoue3372@naver.com");
		lblEmailMyhouenavercom.setBounds(12, 326, 235, 15);
		getContentPane().add(lblEmailMyhouenavercom);
		
		JLabel lblCopyright = new JLabel("Copyright \u00A9 2018. MIR Lab All rights reserved");
		lblCopyright.setBounds(25, 368, 257, 15);
		getContentPane().add(lblCopyright);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setBounds(72, 10, 147, 201);
		lblNewLabel_1.setIcon(new ImageIcon(url));

		
		getContentPane().add(lblNewLabel_1);
		
		JLabel lblMobile = new JLabel("Mobile : +82-10-8558-4631");
		lblMobile.setBounds(12, 343, 235, 15);
		getContentPane().add(lblMobile);
		
		setVisible(true);
		
	}
}
