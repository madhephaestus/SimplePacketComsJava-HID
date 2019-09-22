package hapticDevice.edu.wpi;

import edu.wpi.SimplePacketComs.device.UdpDevice;

public class RBE2001Robot extends UdpDevice  implements ISimplePIDRobot,IRBE2001Robot,IRBE2002Robot{



	public RBE2001Robot(String add, int numPID) throws Exception {
		super(add);
		setupPidCommands(numPID);
		connect();
	}

	@Override
	public String toString() {
		return getName();
	}


}
