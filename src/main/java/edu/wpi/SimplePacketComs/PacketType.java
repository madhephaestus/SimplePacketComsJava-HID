package edu.wpi.SimplePacketComs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class PacketType{
	public int idOfCommand=0;
	public Number [] downstream;
	public Number [] upstream;
	public boolean done=false;
	public boolean started = false;

	protected ByteOrder be = ByteOrder.LITTLE_ENDIAN;
	protected int packetSize = 64;
	protected int numberOfBytesPerValue = 4;
	public int numValues = (packetSize / 4) - 1;
	public PacketType(int id){
		idOfCommand=id;
	}
	public void init() {
		downstream=new Number[numValues];
		upstream=new Number[numValues];
		for(int i=0;i<numValues;i++) {
			downstream[i]=0;
			upstream[i]=0;

		}
	}

	int getId(byte[] bytes) {
		return ByteBuffer.wrap(bytes).order(be).getInt(0);
	}

	public abstract Number[] parse(byte[] bytes);

	public abstract byte[] command(int idOfCommand, Number[] values);
	
}