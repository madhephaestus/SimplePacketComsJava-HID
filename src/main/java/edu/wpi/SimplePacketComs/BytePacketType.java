package edu.wpi.SimplePacketComs;

public class BytePacketType extends PacketType {
	private final byte[] message;
	private final Number[] returnValues;

	public BytePacketType(int id, int size) {
		super(id);
		packetSize = size;
		message = new byte[packetSize];
		numberOfBytesPerValue = 1;
		numValues = (packetSize / numberOfBytesPerValue) - (4 / numberOfBytesPerValue);
		returnValues = new Number[numValues];
		downstream = new Byte[numValues];
		upstream = new Byte[numValues];
		for (int i = 0; i < numValues; i++) {
			downstream[i] = (byte) 0;
			upstream[i] = (byte) 0;
		}

	}

	public Number[] parse(byte[] bytes) {
		// println "Parsing packet"
		for (int i = 0; i < numValues; i++) {
			int baseIndex = (numberOfBytesPerValue * i) + 4;
			returnValues[i] = bytes[baseIndex];
		}

		return returnValues;
	}

	public byte[] command(int idOfCommand, Number[] values) {

		writeId(idOfCommand, message);
		for (int i = 0; i < numValues && i < values.length; i++) {
			int baseIndex = (numberOfBytesPerValue * i) + 4;
			message[baseIndex] = values[i].byteValue();
		}
		return message;
	}

}