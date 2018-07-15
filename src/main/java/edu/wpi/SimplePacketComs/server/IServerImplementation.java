package edu.wpi.SimplePacketComs.server;

import edu.wpi.SimplePacketComs.PacketType;

public interface IServerImplementation extends IOnPacketEvent {

	public PacketType getPacket();
}
