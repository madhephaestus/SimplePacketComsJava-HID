package edu.wpi.SimplePacketComs.floatpacket;

import edu.wpi.SimplePacketComs.PacketProcessor;
import edu.wpi.SimplePacketComs.PacketType;

public  class FloatPacketType extends PacketType{
	public FloatPacketType(int id,int size){
		super(id);
		processor=new FloatPacketProcessor(size);
		init();
	}
	
}