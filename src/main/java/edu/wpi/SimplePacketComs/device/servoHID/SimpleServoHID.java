package edu.wpi.SimplePacketComs.device.servoHID;

import edu.wpi.SimplePacketComs.PacketType;
import edu.wpi.SimplePacketComs.phy.HIDSimplePacketComs;

public class SimpleServoHID extends HIDSimplePacketComs {
	private PacketType servos = new edu.wpi.SimplePacketComs.BytePacketType(1962, 64);
	private PacketType imuData = new edu.wpi.SimplePacketComs.FloatPacketType(1804, 64);
	private final double[] status = new double[12];
	private final byte[] data = new byte[16];
	
	public SimpleServoHID(int vidIn, int pidIn) {
		super(vidIn, pidIn);
		addPollingPacket(servos);
		addPollingPacket(imuData);
		addEvent(1962, () -> {
			writeBytes(1962, data);
		});
		addEvent(1804, () -> {
			readFloats(1804,status);
		});
	}
	public double[] getImuData() {
		return status;
	}
	public byte[] getData() {
		return data;
	}
}
