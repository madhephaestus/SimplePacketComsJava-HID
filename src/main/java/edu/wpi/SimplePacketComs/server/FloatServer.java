package edu.wpi.SimplePacketComs.server;

import edu.wpi.SimplePacketComs.FloatPacketType;
import edu.wpi.SimplePacketComs.PacketType;

public abstract class FloatServer implements IServerImplementation {
	private FloatPacketType packet;
	private float [] data;
	public FloatServer(int id) {
		this(id,64);
	}
	public FloatServer(int id, int size) {
		packet= new FloatPacketType(id, size);
		data=new float[packet.getDownstream().length];
	}
	@Override
	public PacketType  getPacket() {
		return packet;
	}
	@Override
	public boolean event(Number[] packet) {
		for(int i=0;i<data.length;i++)
			data[i]=packet[i].floatValue();
		boolean ret = event(data);
		for(int i=0;i<data.length;i++)
			packet[i]=data[i];
		return ret;
	}

	public abstract boolean event(float[]data);
}
