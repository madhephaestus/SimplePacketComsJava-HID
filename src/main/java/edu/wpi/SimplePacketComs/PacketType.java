package edu.wpi.SimplePacketComs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class PacketType{
	public int idOfCommand=0;
	public Number [] downstream;
	public Number [] upstream;
	public boolean done=false;
	public boolean started = false;

	protected static ByteOrder be = ByteOrder.LITTLE_ENDIAN;
	public static int packetSize = 64;
	protected int numberOfBytesPerValue = 4;
	public int numValues = (packetSize / 4) - 1;
	public PacketType(int id){
		idOfCommand=id;
	}

	public static int getId(byte[] bytes) {
		return ByteBuffer.wrap(bytes).order(be).getInt(0);
	}
	public static void writeId(int idOfCommand,byte[] bytes) {
		bytes[3]=(byte)(idOfCommand >> 24);
		bytes[2]=(byte)(idOfCommand >> 16);
		bytes[1]=(byte)(idOfCommand >> 8);
		bytes[0]=(byte)(idOfCommand );
		//byte[] b = ByteBuffer.allocate(4).putInt(idOfCommand).array();
		//for(int i=0;i<4;i++) {
		//	int num = b[3-i];
		//	if(num<0)
		//		num+=256;
		//	bytes[i]=(byte)num;
		//}
	}
	public  byte[] command() {
		return command( idOfCommand, downstream);
	}

	public abstract Number[] parse(byte[] bytes);

	public abstract byte[] command(int idOfCommand, Number[] values);
	
}