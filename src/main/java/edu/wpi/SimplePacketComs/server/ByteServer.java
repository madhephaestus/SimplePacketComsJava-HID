package edu.wpi.SimplePacketComs.server;

import edu.wpi.SimplePacketComs.BytePacketType;
import edu.wpi.SimplePacketComs.PacketType;

public abstract class ByteServer implements IServerImplementation {
	
	private BytePacketType packet;
	private byte[]data;
	
	public ByteServer(int id) {
		this(id,64);
	}
	public ByteServer(int id, int size) {
		packet= new BytePacketType(id, size);
		data=new byte[packet.getDownstream().length];
	}
	@Override
	public PacketType  getPacket() {
		return packet;
	}
	@Override
	public boolean event(Number[] packet) {
		for(int i=0;i<data.length;i++)
			data[i]=packet[i].byteValue();
		boolean ret = event(data);
		for(int i=0;i<data.length;i++)
			packet[i]=data[i];
		return ret;
	}

	public abstract boolean event(byte[]data);
}
