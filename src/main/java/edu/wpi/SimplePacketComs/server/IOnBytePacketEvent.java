package edu.wpi.SimplePacketComs.server;

public interface IOnBytePacketEvent extends IOnPacketEvent{
	
	default public boolean event(Number[] packet) {
		if(Byte.class.isInstance(packet[0])) {
			return event((Byte[])packet);
		}
		return false;
	}
	public boolean event(Byte[] packet);

}
