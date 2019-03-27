package com.mir.smartgrid.simulator.gui;

import javax.swing.JFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import com.mir.smartgrid.simulator.global.Global;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class Configuration extends JFrame{
	private JTextField openfmbText;
	private JComboBox<String> emaComboBox;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	
	public Configuration() {
		setTitle("MIR_Simulator Configuration");
		setSize(890, 546);
		setResizable(false);
		setLocation(800, 450);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		emaComboBox = new JComboBox<String>();
		emaComboBox.setBounds(26, 29, 186, 21);
		getContentPane().add(emaComboBox);
		
		
		Iterator<String> keys = Global.emaConfig.keySet().iterator();
		
		while(keys.hasNext()){
			
			String key = keys.next();
			
			System.out.println(key);
			System.out.println(Global.emaConfig.get(key));
			emaComboBox.addItem(key);

		}
		
		emaComboBox.setSelectedIndex(0);

		
		JButton btnNewButton = new JButton("SET");
		btnNewButton.setBounds(775, 484, 97, 23);
		getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("START");
		btnNewButton_1.setBounds(666, 484, 97, 23);
		getContentPane().add(btnNewButton_1);
		
		JButton btnNewButton_2 = new JButton("CLOSE");
		btnNewButton_2.setBounds(557, 484, 97, 23);
		getContentPane().add(btnNewButton_2);


		openfmbText = new JTextField();
		openfmbText.setText(Global.emaConfig.get(emaComboBox.getSelectedItem().toString()).toString());
		openfmbText.setBounds(376, 29, 116, 21);
		getContentPane().add(openfmbText);
		openfmbText.setColumns(10);
		
		emaComboBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openfmbText.setText(Global.emaConfig.get(emaComboBox.getSelectedItem().toString()).toString());
			}
		});
		
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("OpenFMB");
		chckbxNewCheckBox.setBounds(240, 28, 115, 23);
		chckbxNewCheckBox.setSelected(true);
		getContentPane().add(chckbxNewCheckBox);
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setBounds(0, 0, 884, 21);
		getContentPane().add(menuBar);
		
		JMenu mnNewMenu = new JMenu("  File  ");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Import");
		mnNewMenu.add(mntmNewMenuItem_1);
		
		JMenuItem mntmExport = new JMenuItem("Export");
		mnNewMenu.add(mntmExport);
		
		JMenu mnNewMenu_1 = new JMenu("Help");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("About Developer");
		mnNewMenu_1.add(mntmNewMenuItem);
		
		textField = new JTextField();
		textField.setBounds(12, 187, 116, 21);
		getContentPane().add(textField);
		textField.setColumns(10);
		
		textField_1 = new JTextField();
		textField_1.setColumns(10);
		textField_1.setBounds(292, 187, 116, 21);
		getContentPane().add(textField_1);
		
		textField_2 = new JTextField();
		textField_2.setColumns(10);
		textField_2.setBounds(552, 187, 116, 21);
		getContentPane().add(textField_2);
		
		JLabel lblSolar = new JLabel("Solar");
		lblSolar.setBounds(44, 162, 57, 15);
		getContentPane().add(lblSolar);
		
		JLabel lblBattery = new JLabel("Battery");
		lblBattery.setBounds(322, 162, 57, 15);
		getContentPane().add(lblBattery);
		
		JLabel lblRecloser = new JLabel("Recloser");
		lblRecloser.setBounds(582, 162, 57, 15);
		getContentPane().add(lblRecloser);
		
		chckbxNewCheckBox.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				if(chckbxNewCheckBox.isSelected()){
					openfmbText.setText(Global.emaConfig.get(emaComboBox.getSelectedItem().toString()).toString());
					openfmbText.setVisible(true);	
				}
				else{
					openfmbText.setText(null);					
					openfmbText.setVisible(false);	
				}
			}

		});
		
		
		mntmNewMenuItem_1.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
				jfc.setDialogTitle("Select an configuration file");
				jfc.setAcceptAllFileFilterUsed(false);
				FileNameExtensionFilter filter = new FileNameExtensionFilter(".cfg files", "cfg", "CFG");
				jfc.addChoosableFileFilter(filter);

				int returnValue = jfc.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					System.out.println(jfc.getSelectedFile().getPath());
				}
				
			}

		});
		
		mntmNewMenuItem.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				new DeveloperPage();
				

			}

		});
		
		
		
		setVisible(true);

		
	}
	
	public static void main(String[] args) throws NumberFormatException, IOException, InterruptedException{

		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		
		int emaNum = Integer.parseInt(br.readLine());
		
		for(int i=1; i<=emaNum; i++){
			Global.emaConfig.put("ClientEMA"+i, "MIR"+(i*100));
		}
		
		Thread.sleep(2000);
		
		new Configuration();
	}
}
