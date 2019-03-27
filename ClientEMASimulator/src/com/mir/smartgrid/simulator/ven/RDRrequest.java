package com.mir.smartgrid.simulator.ven;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.mir.smartgrid.simulator.controller.Controller;
import com.mir.smartgrid.simulator.global.Global;

public class RDRrequest implements Callable<Boolean> {

	private String subPath, optID;
	private StringBuilder httpResponse = new StringBuilder();
	static HttpClientBuilder hcb = HttpClientBuilder.create();
	static HttpClient client = hcb.build();
	private Controller controller;
	public RDRrequest(Controller conntroller, String optID) {
		setSubPath("EiOpt");
		setOptID(optID);
		
	}

	@Override
	public Boolean call() {
		HttpPost post = new HttpPost(Global.vtnURL + getSubPath());

		try {

			post.setEntity(new StringEntity(new VENImpl().CreateOptSchedule(this.controller.getHASHED_VEN_NAME(), "Emergency", "RdrRequest",
					new TimeFormat().getCurrentTime(), optID)));
			// POST
			HttpResponse response = client.execute(post);

			// HttpResponse eventResponse;
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

			String line = "";
			while ((line = rd.readLine()) != null) {
				// line = line.replace(" ", "");
				setHttpResponse(line);
			}

			System.out.println("RDRº¸³¿");
			
			return true;

		} catch (Exception e) {

			e.printStackTrace();
			return false;

		}
	}

	public void setHttpResponse(String httpResponse) {
		this.httpResponse = this.httpResponse.append(httpResponse);
	}

	public String getSubPath() {
		return subPath;
	}

	public void setSubPath(String subPath) {
		this.subPath = subPath;
	}

	public String getOptID() {
		return optID;
	}

	public void setOptID(String optID) {
		this.optID = optID;
	}

}
