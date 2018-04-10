package edu.wpi.SimplePacketComs.floatpacket;

import java.nio.ByteBuffer;
import edu.wpi.SimplePacketComs.PacketType;

public  class FloatPacketType extends PacketType{
	public FloatPacketType(int id,int size){
		super(id);
		 packetSize = size;
		 numberOfBytesPerValue = 4;
		 numValues = (packetSize / numberOfBytesPerValue) - (4/numberOfBytesPerValue);
		 
		downstream=new Float[numValues];
		upstream=new Float[numValues];
		for(int i=0;i<numValues;i++) {
			downstream[i]=(float)0;
			upstream[i]=(float)0;
		}
	}
	@Override
	public Number[] parse(byte[] bytes) {
		Number[] returnValues = new Number[numValues];

		// println "Parsing packet"
		for (int i = 0; i < numValues; i++) {
			int baseIndex = (4 * i) + 4;
			returnValues[i] = ByteBuffer.wrap(bytes).order(be).getFloat(baseIndex);
		}

		return returnValues;
	}
	@Override 
	public byte[] command(int idOfCommand, Number[] values) {
		
		byte[] message = new byte[packetSize];
		ByteBuffer.wrap(message).order(be).putInt(0, idOfCommand).array();
		for (int i = 0; i < numValues && i < values.length; i++) {
			int baseIndex = (4 * i) + 4;
			ByteBuffer.wrap(message).order(be).putFloat(baseIndex,(float)values[i]).array();
		}
		return message;
	}
}