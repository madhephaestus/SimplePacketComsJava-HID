package edu.wpi.SimplePacketComs;

import java.nio.ByteBuffer;

public class FloatPacketType extends PacketType {
	private final Number[] returnValues;
	private final byte[] message;

	public FloatPacketType(int id, int size) {
		super(id);
		packetSize = size;
		numberOfBytesPerValue = 4;
		numValues = (packetSize / numberOfBytesPerValue) - (4 / numberOfBytesPerValue);
		returnValues = new Number[numValues];
		message = new byte[packetSize];
		downstream = new Float[numValues];
		upstream = new Float[numValues];
		for (int i = 0; i < numValues; i++) {
			downstream[i] = (float) 0;
			upstream[i] = (float) 0;
		}
	}

	@Override
	public Number[] parse(byte[] bytes) {

		// println "Parsing packet"
		for (int i = 0; i < numValues; i++) {
			int baseIndex = (4 * i) + 4;
			returnValues[i] = ByteBuffer.wrap(bytes).order(be).getFloat(baseIndex);
		}

		return returnValues;
	}

	@Override
	public byte[] command(int idOfCommand, Number[] values) {

		writeId(idOfCommand, message);
		for (int i = 0; i < numValues && i < values.length; i++) {
			int baseIndex = (4 * i) + 4;
			ByteBuffer.wrap(message).order(be).putFloat(baseIndex, (float) values[i]).array();
		}
		return message;
	}
}