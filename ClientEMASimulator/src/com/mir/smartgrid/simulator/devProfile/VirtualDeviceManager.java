package com.mir.smartgrid.simulator.devProfile;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.mir.smartgrid.simulator.controller.Controller;
import com.mir.smartgrid.simulator.global.Global;

public class VirtualDeviceManager extends Thread {

	private String emaID;
	private Controller controller;

	public VirtualDeviceManager(String emaID, Controller controller) {
		setEmaID(emaID);
		setController(controller);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		DeviceProfile devProfile;
		for (int i = 1; i <= 5; i++) {

			String temp = getEmaID().replaceAll("CLIENT_EMA", "");
			devProfile = new DeviceProfile(getEmaID(), temp + "0" + i, "LED", "ON", 9, 1, i, 0, 0, 0, 0, 0, 0,
					new Date(System.currentTimeMillis()));
			Global.devProfile.put(temp + "0" + i, devProfile);

		}

		Timer timer = new Timer();
		timer.schedule(new UpdateTask(), 0, 2000);

	}

	private class UpdateTask extends TimerTask {

		public void run() {

			double total = 0;

			double threshold = controller.getThreshold();

			if (controller.getReportCnt() >= 1) {
				for (int i = 1; i <= 5; i++) {

					String temp = getEmaID().replaceAll("CLIENT_EMA", "");

					double val = controller.getThreshold() / 5 - 10;
					double randomVal = Math.random() * val + 1;

//					Global.devProfile.replace(temp + "0" + i, new DeviceProfile(getEmaID(), temp + "0" + i, "LED", "ON",
//							9, 1, i, randomVal, 0, 0, 0, 0, 0, new Date(System.currentTimeMillis())));
//					Global.devProfile.get(temp + "0" + i).setMaxValue(randomVal);
//					Global.devProfile.get(temp + "0" + i).setMinValue(randomVal);

					if (randomVal > 10) {

						Global.devProfile.replace(temp + "0" + i, new DeviceProfile(getEmaID(), temp + "0" + i, "LED",
								"ON", 9, 1, i, randomVal, 0, 0, 0, 0, 0, new Date(System.currentTimeMillis())));
						Global.devProfile.get(temp + "0" + i).setMaxValue(randomVal);
						Global.devProfile.get(temp + "0" + i).setMinValue(randomVal);
					} else {
						Global.devProfile.replace(temp + "0" + i, new DeviceProfile(getEmaID(), temp + "0" + i, "LED",
								"OFF", 9, 1, i, randomVal, 0, 0, 0, 0, 0, new Date(System.currentTimeMillis())));
						Global.devProfile.get(temp + "0" + i).setMaxValue(randomVal);
						Global.devProfile.get(temp + "0" + i).setMinValue(randomVal);
					}
					
				}
				controller.setReportCnt(controller.getReportCnt() - 1);
			}

			else {
				for (int i = 1; i <= 5; i++) {

					String temp = getEmaID().replaceAll("CLIENT_EMA", "");

					double randomVal = Math.random() * 79 + 1;

					if (randomVal > 10) {

						Global.devProfile.replace(temp + "0" + i, new DeviceProfile(getEmaID(), temp + "0" + i, "LED",
								"ON", 9, 1, i, randomVal, 0, 0, 0, 0, 0, new Date(System.currentTimeMillis())));
						Global.devProfile.get(temp + "0" + i).setMaxValue(randomVal);
						Global.devProfile.get(temp + "0" + i).setMinValue(randomVal);
					} else {
						Global.devProfile.replace(temp + "0" + i, new DeviceProfile(getEmaID(), temp + "0" + i, "LED",
								"OFF", 9, 1, i, randomVal, 0, 0, 0, 0, 0, new Date(System.currentTimeMillis())));
						Global.devProfile.get(temp + "0" + i).setMaxValue(randomVal);
						Global.devProfile.get(temp + "0" + i).setMinValue(randomVal);
					}
					// System.out.println(temp+"0"+i+": "+randomVal);
					total += randomVal;
				}
			}

			// System.out.println("total: "+ total);

		}
	}

	public String getEmaID() {
		return emaID;
	}

	public void setEmaID(String emaID) {
		this.emaID = emaID;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

}
