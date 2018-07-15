package edu.wpi.SimplePacketComs.server.device;

import edu.wpi.SimplePacketComs.server.ByteServer;
import edu.wpi.SimplePacketComs.server.phy.UdpServer;

public class GameControllerServer extends UdpServer {

	private int index;
	private int[] controllerData = new int[20];

	public GameControllerServer(String name, int index) {
		super(name);
		// TODO Auto-generated constructor stub
		this.index = index;
		getControllerData()[0] = index;
		for (int i = 1; i < getControllerData().length; i++) {
			getControllerData()[i] = 128;
		}
		// addServer(new BytePacketType(1970, 64), packet ->{
		// for(int i=0;i<getControllerData().length;i++) {
		// packet[i]=(byte)getControllerData()[i];
		// }
		// //System.out.println("Data");
		// return true;
		// });
		addServer(new ByteServer(1970) {
			public boolean event(byte[] packet) {
				for (int i = 0; i < getControllerData().length; i++) {
					packet[i] = (byte) getControllerData()[i];
				}
				// System.out.println("Data");
				return true;
			}
		});
	}

	public int[] getControllerData() {
		return controllerData;
	}

}
