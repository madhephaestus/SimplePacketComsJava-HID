package hapticDevice.edu.wpi;

import edu.wpi.SimplePacketComs.phy.HIDSimplePacketComs;

public class RBE3001Robot extends HIDSimplePacketComs implements ISimplePIDRobot {


	public RBE3001Robot(int vidIn, int pidIn,int numPID) throws Exception {
		super(vidIn,  pidIn);
		setupPidCommands(numPID);
		connect();
		if(isVirtual())
			throw new RuntimeException("Device is virtual!");
	}

	@Override
	public String toString() {
		return getName();
	}




}
