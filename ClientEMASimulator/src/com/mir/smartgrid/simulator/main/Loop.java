package com.mir.smartgrid.simulator.main;

public class Loop {

	
	public static void main(String[] args) throws InterruptedException{
		
		int i=0;
		while(true){
			Thread.sleep(1000);
			System.out.println("Hello"+i);
			i++;
		}
	}
}
