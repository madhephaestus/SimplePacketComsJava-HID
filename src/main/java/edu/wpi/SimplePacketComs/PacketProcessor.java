package edu.wpi.SimplePacketComs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public abstract class  PacketProcessor {
	protected ByteOrder be = ByteOrder.LITTLE_ENDIAN;
	protected int packetSize = 64;
	protected int numberOfBytesPerValue = 4;
	public int numValues = (packetSize / 4) - 1;

	int getId(byte[] bytes) {
		return ByteBuffer.wrap(bytes).order(be).getInt(0);
	}

	public abstract Number[] parse(byte[] bytes);

	public abstract byte[] command(int idOfCommand, Number[] values);

}
