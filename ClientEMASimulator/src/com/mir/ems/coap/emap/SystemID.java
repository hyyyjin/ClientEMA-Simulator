package com.mir.ems.coap.emap;

import org.eclipse.californium.core.CoapResource;

import com.mir.smartgrid.simulator.global.Global;

public class SystemID extends CoapResource {

	private String parentPath;

	public SystemID(String name, String parentPath) {
		// TODO Auto-generated constructor stub
		super(name);

		setParentPath(parentPath);

		if (getParentPath().contains("EMAP")) {
			add(new Version(Global.version));
		} else if (getParentPath().contains("OpenADR")) {
			add(new Version(Global.openADRVersion));
		}

	}

	public String getParentPath() {
		return parentPath;
	}

	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

}
