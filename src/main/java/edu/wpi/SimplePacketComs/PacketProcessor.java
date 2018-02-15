package edu.wpi.SimplePacketComs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class PacketProcessor {
	ByteOrder be = ByteOrder.LITTLE_ENDIAN;
	int packetSize = 64;
	int numFloats = (packetSize / 4) - 1;

	int getId(byte[] bytes) {
		return ByteBuffer.wrap(bytes).order(be).getInt(0);
	}

	float[] parse(byte[] bytes) {
		float[] returnValues = new float[numFloats];

		// println "Parsing packet"
		for (int i = 0; i < numFloats; i++) {
			int baseIndex = (4 * i) + 4;
			returnValues[i] = ByteBuffer.wrap(bytes).order(be).getFloat(baseIndex);
		}

		return returnValues;
	}

	byte[] command(int idOfCommand, float[] values) {
		byte[] message = new byte[packetSize];
		ByteBuffer.wrap(message).order(be).putInt(0, idOfCommand).array();
		for (int i = 0; i < numFloats && i < values.length; i++) {
			int baseIndex = (4 * i) + 4;
			ByteBuffer.wrap(message).order(be).putFloat(baseIndex, values[i]).array();
		}
		return message;
	}

}
