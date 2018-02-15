package edu.wpi.SimplePacketComs;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class BytePacketProcessor extends PacketProcessor{
	
	public BytePacketProcessor(int size){
		 packetSize = size;
		 numberOfBytesPerValue = 1;
		 numValues = (packetSize / numberOfBytesPerValue) - (4/numberOfBytesPerValue);
	}
	Number[] parse(byte[] bytes) {
		Number[] returnValues = new Number[numValues];

		// println "Parsing packet"
		for (int i = 0; i < numValues; i++) {
			int baseIndex = (numberOfBytesPerValue * i) + 4;
			returnValues[i] = bytes[baseIndex];
		}

		return returnValues;
	}

	byte[] command(int idOfCommand, Number[] values) {
		byte[] message = new byte[packetSize];
		ByteBuffer.wrap(message).order(be).putInt(0, idOfCommand).array();
		for (int i = 0; i < numValues && i < values.length; i++) {
			int baseIndex = (numberOfBytesPerValue * i) + 4;
			message[baseIndex]=(byte) values[i];
		}
		return message;
	}

}
