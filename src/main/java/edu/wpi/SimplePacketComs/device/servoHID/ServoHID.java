package edu.wpi.SimplePacketComs.device.servoHID;

import edu.wpi.SimplePacketComs.BytePacketType;
import edu.wpi.SimplePacketComs.PacketType;
import edu.wpi.SimplePacketComs.phy.HIDSimplePacketComs;

public class ServoHID extends HIDSimplePacketComs {
	private PacketType gamestate = new BytePacketType(1804, 64);
	private final byte[] status = new byte[60];
	private final byte[] data = new byte[20];
	
	public ServoHID(int vidIn, int pidIn) {
		super(vidIn, pidIn);
		addPollingPacket(gamestate);
		addEvent(gamestate.idOfCommand, () -> {
			readBytes(gamestate.idOfCommand, getData());
			writeBytes(gamestate.idOfCommand, getStatus());
		});
	}
	public byte[] getStatus() {
		return status;
	}
	public byte[] getData() {
		return data;
	}
}
