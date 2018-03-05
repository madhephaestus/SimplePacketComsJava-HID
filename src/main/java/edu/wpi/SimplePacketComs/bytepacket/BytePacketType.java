package edu.wpi.SimplePacketComs.bytepacket;

import edu.wpi.SimplePacketComs.PacketProcessor;
import edu.wpi.SimplePacketComs.PacketType;

public  class BytePacketType extends PacketType{
	public BytePacketType(int id, int size){
		super(id);
		processor=new BytePacketProcessor(size);
		init();

	}
	
}