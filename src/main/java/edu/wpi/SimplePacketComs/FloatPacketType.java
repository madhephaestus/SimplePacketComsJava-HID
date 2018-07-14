package edu.wpi.SimplePacketComs;

//import java.nio.ByteBuffer;

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
		setDownstream(new Float[numValues]);
		setUpstream(new Float[numValues]);
		for (int i = 0; i < numValues; i++) {
			getDownstream()[i] = (float) 0;
			getUpstream()[i] = (float) 0;
		}
	}

	@Override
	public Number[] parse(byte[] bytes) {

		
		for (int i = 0; i < numValues; i++) {
			int baseIndex = (4 * i) + 4;
			
			int bits = toInt (bytes[0+baseIndex])|
					toInt (bytes[1+baseIndex])<<8|
					toInt (bytes[2+baseIndex])<<16|
					toInt (bytes[3+baseIndex])<<24
					 ;
			
			returnValues[i] =  Float.intBitsToFloat(bits);
			//System.out.println(" Got "+returnValues[i]+" from "+bits+" Bytes = "+bytes[0+baseIndex]+" "+bytes[1+baseIndex]+" "+bytes[2+baseIndex]+" "+bytes[2+baseIndex]);
			//returnValues[i] = ByteBuffer.wrap(bytes).order(be).getFloat(baseIndex);
		}

		return returnValues;
	}
	
	int toInt (byte byteValue ) {
		int val =byteValue;
		if(val<0) {
			val+=256;
		}
		return val;		
	}

	@Override
	public byte[] command(int idOfCommand, Number[] values) {

		writeId(idOfCommand, message);
		for (int i = 0; i < numValues && i < values.length; i++) {
			int baseIndex = (4 * i) + 4;
			// println "Parsing packet"
			int bits = Float.floatToIntBits( (float) values[i]);

			message[0+baseIndex] = (byte)(bits & 0xff);
			message[1+baseIndex] = (byte)((bits >> 8) & 0xff);
			message[2+baseIndex] = (byte)((bits >> 16) & 0xff);
			message[3+baseIndex] = (byte)((bits >> 24) & 0xff);
			//ByteBuffer.wrap(message).order(be).putFloat(baseIndex, (float) values[i]).array();
		}
		return message;
	}
}