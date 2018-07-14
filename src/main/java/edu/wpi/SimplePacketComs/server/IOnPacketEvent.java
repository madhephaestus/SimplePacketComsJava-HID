package edu.wpi.SimplePacketComs.server;

import edu.wpi.SimplePacketComs.PacketType;

public interface IOnPacketEvent {
	public boolean event(Number[] packet);
}
