package edu.wpi.SimplePacketComs.floatpacket;

import edu.wpi.SimplePacketComs.PacketProcessor;
import edu.wpi.SimplePacketComs.PacketType;

public  class FloatPacketType extends PacketType{
	int idOfCommand=0;
	Number [] downstream;
	Number [] upstream;
	boolean done=false;
	boolean started = false;
	public PacketProcessor processor;
	public FloatPacketType(int id,int size){
		super(id);
		processor=new FloatPacketProcessor(size);
		downstream=new Number[processor.numValues];
		upstream=new Number[processor.numValues];
	}
	
}