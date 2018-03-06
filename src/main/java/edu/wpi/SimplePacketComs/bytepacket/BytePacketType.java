package edu.wpi.SimplePacketComs.bytepacket;

import java.nio.ByteBuffer;
import edu.wpi.SimplePacketComs.PacketType;

public  class BytePacketType extends PacketType{
	public BytePacketType(int id, int size){
		super(id);
		 packetSize = size;
		 numberOfBytesPerValue = 1;
		 numValues = (packetSize / numberOfBytesPerValue) - (4/numberOfBytesPerValue);
		init();

	}
	public Number[] parse(byte[] bytes) {
		Number[] returnValues = new Number[numValues];

		// println "Parsing packet"
		for (int i = 0; i < numValues; i++) {
			int baseIndex = (numberOfBytesPerValue * i) + 4;
			returnValues[i] = bytes[baseIndex];
		}

		return returnValues;
	}

	public byte[] command(int idOfCommand, Number[] values) {
		byte[] message = new byte[packetSize];
		ByteBuffer.wrap(message).order(be).putInt(0, idOfCommand).array();
		for (int i = 0; i < numValues && i < values.length; i++) {
			int baseIndex = (numberOfBytesPerValue * i) + 4;
			message[baseIndex]=values[i].byteValue();
		}
		return message;
	}
	
}