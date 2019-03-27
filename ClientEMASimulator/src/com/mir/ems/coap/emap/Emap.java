package com.mir.ems.coap.emap;

import org.eclipse.californium.core.CoapResource;

import com.mir.smartgrid.simulator.global.Global;

public class Emap extends CoapResource {

	public Emap(String name) {
		// TODO Auto-generated constructor stub
		super(name);

		add(new SystemID(Global.SYSTEMID, name));

	}
}
