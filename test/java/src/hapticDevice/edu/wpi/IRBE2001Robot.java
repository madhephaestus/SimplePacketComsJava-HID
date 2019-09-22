package hapticDevice.edu.wpi;

import java.util.Arrays;

import edu.wpi.SimplePacketComs.BytePacketType;
import edu.wpi.SimplePacketComs.FloatPacketType;
import edu.wpi.SimplePacketComs.PacketType;

public interface IRBE2001Robot {
	PacketType estop = new BytePacketType(1989, 64);
	PacketType getStatus = new BytePacketType(2012, 64);
	PacketType clearFaults = new BytePacketType(1871, 64);
	PacketType pickOrder = new FloatPacketType(1936, 64);
	PacketType approve = new BytePacketType(1994, 64);
	byte[] status = new byte[1];

	double[] pickOrderData = new double[3];
	double[] driveStatus = new double[1];

	default public void add2001() {
		pickOrder.waitToSendMode();
		clearFaults.waitToSendMode();
		estop.waitToSendMode();
		approve.waitToSendMode();
		for (PacketType pt : Arrays.asList(clearFaults, pickOrder, getStatus, approve, estop)) {
			addPollingPacket(pt);
		}
		addEvent(getStatus.idOfCommand, () -> {
			readBytes(getStatus.idOfCommand, status);
		});
	}

	default public void estop() {
		estop.oneShotMode();
	}

	default public double getDriveStatus() {
		return driveStatus[0];
	}

	default public void pickOrder(double material, double angle, double dropLocation) {
		pickOrderData[0] = material;
		pickOrderData[1] = angle;
		pickOrderData[2] = dropLocation;
		writeFloats(pickOrder.idOfCommand, pickOrderData);
		pickOrder.oneShotMode();

	}

	default public WarehouseRobotStatus getStatus() {
		return WarehouseRobotStatus.fromValue(status[0]);
	}

	default public void clearFaults() {
		clearFaults.oneShotMode();

	}

	default public void approve() {
		approve.oneShotMode();
	}

	void addPollingPacket(PacketType packet);

	public void addEvent(Integer id, Runnable event);

	void readBytes(int id, byte[] values);

	void writeFloats(int idOfCommand, double[] pickorderdata2);
}
