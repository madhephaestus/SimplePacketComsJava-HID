package hapticDevice.edu.wpi;

import java.util.Arrays;

import edu.wpi.SimplePacketComs.FloatPacketType;
import edu.wpi.SimplePacketComs.PacketType;

public interface ISimplePIDRobot {
	FloatPacketType setSetpoint = new FloatPacketType(1848, 64);
	FloatPacketType pidStatus = new FloatPacketType(1910, 64);
	FloatPacketType getConfig = new FloatPacketType(1857, 64);
	FloatPacketType setConfig = new FloatPacketType(1900, 64);

	PacketType SetPIDVelocity = new FloatPacketType(1811, 64);
	PacketType SetPDVelocityConstants = new FloatPacketType(1810, 64);
	PacketType GetPIDVelocity = new FloatPacketType(1822, 64);
	PacketType GetPDVelocityConstants = new FloatPacketType(1825, 64);

	double[] numPID = new double[1];
	double[] pidConfigData = new double[15];
	double[] pidVelConfigData = new double[15];

	double[] piddata = new double[15];
	double[] veldata = new double[15];
	NumberOfPID myNum = new NumberOfPID();

	default void setupPidCommands(int numPID) {
		//new Exception().printStackTrace();
		myNum.setMyNum(numPID);
		SetPIDVelocity.waitToSendMode();
		SetPDVelocityConstants.waitToSendMode();
		GetPIDVelocity.pollingMode();
		GetPDVelocityConstants.oneShotMode();

		getConfig.oneShotMode();
		setConfig.waitToSendMode();
		setSetpoint.waitToSendMode();

		for (PacketType pt : Arrays.asList(pidStatus, getConfig, setConfig, setSetpoint, SetPIDVelocity,
				SetPDVelocityConstants, GetPIDVelocity, GetPDVelocityConstants)) {
			addPollingPacket(pt);
		}

		addEvent(GetPDVelocityConstants.idOfCommand, () -> {
			try {
				readFloats(GetPDVelocityConstants.idOfCommand, pidVelConfigData);
				for (int i = 0; i < 3; i++) {
					System.out.print("\n vp " + getVKp(i));
					System.out.print(" vd " + getVKd(i));
					System.out.println("");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		addEvent(getConfig.idOfCommand, () -> {
			try {
				readFloats(getConfig.idOfCommand, pidConfigData);
				for (int i = 0; i < 3; i++) {
					System.out.print("\n p " + getKp(i));
					System.out.print(" i " + getKi(i));
					System.out.print(" d " + getKd(i));
					System.out.println("");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});

		addEvent(pidStatus.idOfCommand, () -> {
			try {
				if (piddata == null) {
					// piddata = new double[15];
					readFloats(pidStatus.idOfCommand, piddata);
				}
				readFloats(pidStatus.idOfCommand, piddata);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		addEvent(GetPIDVelocity.idOfCommand, () -> {
			try {
				if (veldata == null) {
					// veldata = new double[15];
					readFloats(GetPIDVelocity.idOfCommand, veldata);
				}
				readFloats(GetPIDVelocity.idOfCommand, veldata);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	default public double getNumPid() {
		return myNum.getMyNum();
	}

	default public double getPidSetpoint(int index) {

		return pidStatus.getUpstream()[1 + index * 2 + 0].doubleValue();
	}

	default public double getPidPosition(int index) {
		return pidStatus.getUpstream()[1 + index * 2 + 1].doubleValue();
	}

	/**
	 * Velocity domain values
	 * 
	 * @param index
	 * @return
	 */
	default public double getHardwareOutput(int index) {
		return GetPIDVelocity.getUpstream()[1 + index * 3 + 2].doubleValue();
	}

	default public double getVelocity(int index) {
		return GetPIDVelocity.getUpstream()[1 + index * 3 + 1].doubleValue();
	}

	default public double getVelSetpoint(int index) {

		return GetPIDVelocity.getUpstream()[1 + index * 3 + 0].doubleValue();
	}

	default public void updatConfig() {
		getConfig.oneShotMode();
		GetPDVelocityConstants.oneShotMode();
	}

	default public void setPidGains(int index, double kp, double ki, double kd) {
		pidConfigData[3 * index + 0] = kp;
		pidConfigData[3 * index + 1] = ki;
		pidConfigData[3 * index + 2] = kd;
		writeFloats(setConfig.idOfCommand, pidConfigData);
		setConfig.oneShotMode();

	}

	default public double getKp(int index) {
		readFloats(getConfig.idOfCommand, pidConfigData);
		return pidConfigData[(3 * index) + 0];
	}

	default public double getKi(int index) {
		readFloats(getConfig.idOfCommand, pidConfigData);
		return pidConfigData[(3 * index) + 1];
	}

	default public double getKd(int index) {
		readFloats(getConfig.idOfCommand, pidConfigData);
		return pidConfigData[(3 * index) + 2];
	}

	default public double getVKp(int index) {
		readFloats(GetPDVelocityConstants.idOfCommand, pidVelConfigData);
		return pidVelConfigData[(3 * index) + 0];
	}

	default public double getVKd(int index) {
		readFloats(GetPDVelocityConstants.idOfCommand, pidVelConfigData);
		return pidVelConfigData[(3 * index) + 2];
	}

	default public void setVelocityGains(int index, double kp, double kd) {
		pidVelConfigData[3 * index + 0] = kp;
		pidVelConfigData[3 * index + 1] = 0;
		pidVelConfigData[3 * index + 2] = kd;
		writeFloats(SetPDVelocityConstants.idOfCommand, pidVelConfigData);
		SetPDVelocityConstants.oneShotMode();
	}

	default public void setPidSetpoints(int msTransition, int mode, double[] data) {
		double down[] = new double[2 + getMyNumPid()];
		down[0] = msTransition;
		down[1] = mode;
		for (int i = 0; i < getMyNumPid(); i++) {
			down[2 + i] = data[i];
		}
		writeFloats(setSetpoint.idOfCommand, down);
		setSetpoint.oneShotMode();

	}

	default public void setPidSetpoint(int msTransition, int mode, int index, double data) {
		double[] cur = new double[getMyNumPid()];
		for (int i = 0; i < getMyNumPid(); i++) {
			if (i == index)
				cur[index] = data;
			else
				cur[i] = getPidSetpoint(i);
		}
		cur[index] = data;
		setPidSetpoints(msTransition, mode, cur);

	}

	default public void setVelocity(int index, double data) {
		double[] cur = new double[getMyNumPid()];
		for (int i = 0; i < getMyNumPid(); i++) {
			if (i == index)
				cur[index] = data;
			else
				cur[i] = getVelSetpoint(i);
		}
		cur[index] = data;
		setVelocity(cur);

	}

	default public void setVelocity(double[] data) {
		writeFloats(SetPIDVelocity.idOfCommand, data);
		SetPIDVelocity.oneShotMode();

	}

	default public int getMyNumPid() {
		return myNum.getMyNum();
	}

	default public void setMyNumPid(int myNumPid) {
		if (myNumPid > 0)
			myNum.setMyNum(myNumPid);
		throw new RuntimeException("Can not have 0 PID");
	}

	default public void stop(int currentIndex) {
		setPidSetpoint(0, 0, currentIndex, getPidPosition(currentIndex));
	}

	// Device API requirements
	public String getName();

	public void disconnect();

	public void readFloats(int id, double[] values);

	public void addEvent(Integer id, Runnable event);

	void addPollingPacket(PacketType packet);

	void writeFloats(int id, double[] values);
}
