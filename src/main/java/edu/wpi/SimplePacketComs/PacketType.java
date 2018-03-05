package edu.wpi.SimplePacketComs;

public abstract class PacketType{
	public int idOfCommand=0;
	public Number [] downstream;
	public Number [] upstream;
	public boolean done=false;
	public boolean started = false;
	public PacketProcessor processor;
	public PacketType(int id){
		idOfCommand=id;
	}
	public void init() {
		downstream=new Number[processor.numValues];
		upstream=new Number[processor.numValues];
		for(int i=0;i<processor.numValues;i++) {
			downstream[i]=0;
			upstream[i]=0;

		}
	}
	
}