package com.mir.smartgrid.simulator.mqtt;

import java.sql.Timestamp;
import java.util.regex.Pattern;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class Publishing{

	
	public void publishThread(MqttClient client, String topicName, int qos, byte[] payload) {

		new Thread(new Runnable() {

			public void run() {

//				System.out.println(payload.toString());
//				
//				String aa = new String(payload);
//				
//				aa = aa.replaceAll(Pattern.quote("\\"), "");
				
				MqttMessage message = new MqttMessage(payload);
//				MqttMessage message = new MqttMessage(aa.getBytes());
				
				message.setQos(qos);
//				message.setRetained(true);
				try {
					String time1 = new Timestamp(System.currentTimeMillis()).toString();
					
//					System.out.println("Publishing *** Time:\t" + time1 + "Topic:\t" + topicName + " Message:\t"
//							+ new String(message.getPayload()) + " QoS:\t" + message.getQos());

					client.publish(topicName, message);
				} catch (MqttException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
		}).start();
	}
}
