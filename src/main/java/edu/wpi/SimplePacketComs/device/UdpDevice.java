package edu.wpi.SimplePacketComs.device;

import java.net.InetAddress;

import edu.wpi.SimplePacketComs.PacketType;
import edu.wpi.SimplePacketComs.bytepacket.BytePacketType;
import edu.wpi.SimplePacketComs.phy.UDPSimplePacketComs;

public abstract class UdpDevice implements Device{
	private PacketType getName = new BytePacketType(1776, 64);
	private InetAddress address;
	protected UDPSimplePacketComs udpdevice;	
	private byte[] name = new byte[60];

	public 	UdpDevice(InetAddress add) throws Exception {
		this.address=add;
		udpdevice = new UDPSimplePacketComs(address);
		getName.downstream[0]=(byte)'*';// read name
		
		udpdevice.addEvent(getName.idOfCommand, () -> {
			udpdevice.readBytes(getName.idOfCommand, name);// read name
		});			
		udpdevice.addPollingPacket(getName);
		getName.oneShotMode();

	}
	/**
	 * This method tells the connection object to disconnect its pipes and close out
	 * the connection. Once this is called, it is safe to remove your device.
	 */

	public void disconnect() {
		udpdevice.disconnect();
	}

	/**
	 * Connect device imp.
	 *
	 * @return true, if successful
	 */
	public boolean connect() {
		return udpdevice.connect();
	}
	public InetAddress getAddress() {
		return address;
	}

	public String getName() {
		return (getAddress().getHostAddress()+"-"+new String(name)).trim();
	}
}
