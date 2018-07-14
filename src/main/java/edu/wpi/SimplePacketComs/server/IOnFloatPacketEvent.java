package edu.wpi.SimplePacketComs.server;

public interface IOnFloatPacketEvent extends IOnPacketEvent{
	default public boolean event(Number[] packet) {
		if(Float.class.isInstance(packet[0])) {
			return event((Float[])packet);
		}
		return false;
	}
	public boolean event(Float[] packet);
}
