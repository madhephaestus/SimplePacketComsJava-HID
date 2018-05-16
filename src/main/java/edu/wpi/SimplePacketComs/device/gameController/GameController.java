package edu.wpi.SimplePacketComs.device.gameController;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.wpi.SimplePacketComs.BytePacketType;
import edu.wpi.SimplePacketComs.PacketType;
import edu.wpi.SimplePacketComs.device.UdpDevice;
import edu.wpi.SimplePacketComs.phy.UDPSimplePacketComs;

public class GameController extends UdpDevice{
	private PacketType gamestate = new BytePacketType(1970, 64);
	private final byte[] status = new byte[60];
	private final byte[] data = new byte[20];

	private GameController(InetAddress add) throws Exception {
		super(add);
		udpdevice.addPollingPacket(gamestate);
		udpdevice.addEvent(gamestate.idOfCommand, () -> {
			udpdevice.readBytes(gamestate.idOfCommand, getData());
			udpdevice.writeBytes(gamestate.idOfCommand, getStatus());
		});
	}
	public static List<GameController> get(String name) throws Exception {
		HashSet<InetAddress> addresses = UDPSimplePacketComs.getAllAddresses(name);
		ArrayList<GameController> robots = new ArrayList<>();
		if (addresses.size() < 1) {
			System.out.println("No GameControllers found named "+name);
			return robots;
		}
		for (InetAddress add : addresses) {
			System.out.println("Got " + add.getHostAddress());
			GameController e = new GameController(add);
			e.connect();
			robots.add(e);
		}
		return robots;
	}
	public static List<GameController> get() throws Exception {
		return get("*");
	}
	public byte[] getStatus() {
		return status;
	}
	public byte[] getData() {
		return data;
	}
	public byte getControllerIndex() {
		return data[0];
	}
}
