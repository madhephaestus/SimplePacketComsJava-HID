package edu.wpi.SimplePacketComs.phy;

public class HIDfactory {
	public static HIDSimplePacketComs get(int vidIn, int pidIn) {
		return new HIDSimplePacketComs(vidIn, pidIn);
	}
	public static HIDSimplePacketComs get() {
		return new HIDSimplePacketComs();
	}
}
