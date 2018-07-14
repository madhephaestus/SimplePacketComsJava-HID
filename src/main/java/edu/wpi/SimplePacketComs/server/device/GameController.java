package edu.wpi.SimplePacketComs.server.device;

import edu.wpi.SimplePacketComs.server.phy.UdpServer;

public class GameController extends UdpServer {

	private int index;

	public GameController(String name, int index) {
		super(name);
		// TODO Auto-generated constructor stub
		this.index = index;
	}

}
