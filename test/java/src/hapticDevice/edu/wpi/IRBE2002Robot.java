package hapticDevice.edu.wpi;

import edu.wpi.SimplePacketComs.FloatPacketType;
import edu.wpi.SimplePacketComs.PacketType;

public interface IRBE2002Robot {
	FloatPacketType IMU = new FloatPacketType(1804, 64);

	FloatPacketType getIR = new FloatPacketType(1590, 64);

	default public void addIMU() {
		addPollingPacket(IMU);
	}

	default void addIR() {
		addPollingPacket(getIR);
	}

	void addPollingPacket(PacketType packet);
}
