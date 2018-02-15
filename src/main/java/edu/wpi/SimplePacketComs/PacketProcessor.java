package edu.wpi.SimplePacketComs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

abstract class  PacketProcessor {
	ByteOrder be = ByteOrder.LITTLE_ENDIAN;
	int packetSize = 64;
	int numberOfBytesPerValue = 4;
	int numValues = (packetSize / 4) - 1;

	int getId(byte[] bytes) {
		return ByteBuffer.wrap(bytes).order(be).getInt(0);
	}

	abstract Number[] parse(byte[] bytes);

	abstract byte[] command(int idOfCommand, Number[] values);

}
