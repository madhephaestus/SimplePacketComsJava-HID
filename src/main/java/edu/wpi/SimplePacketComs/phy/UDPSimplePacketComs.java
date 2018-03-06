package edu.wpi.SimplePacketComs.phy;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;

import edu.wpi.SimplePacketComs.AbstractSimpleComsDevice;

public class UDPSimplePacketComs extends AbstractSimpleComsDevice {

	private InetAddress address;
	private static InetAddress broadcast;
	private static final HashSet<InetAddress> addrs = new HashSet<>();
	private DatagramSocket udpSock;
	private byte[] receiveData = new byte[64];
	private static final int port = 1865;
	private DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

	public UDPSimplePacketComs(InetAddress address) throws Exception {
		this.address = address;

	}

	public static HashSet<InetAddress> getAllAddresses() throws Exception {
		broadcast = InetAddress.getByAddress(new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 255 });
		addrs.clear();
		UDPSimplePacketComs pinger = new UDPSimplePacketComs(broadcast);
		pinger.connect();
		byte[] data =new byte[64];
		pinger.write(data, data.length, 1000) ;
		for(int i=0;i<100;i++) {
			pinger.read(data,2);// Allow all possible packets to be processed
			Thread.sleep(2);			
		}
		pinger.disconnect();
		return addrs;
	}

	@Override
	public int read(byte[] message, int howLongToWaitBeforeTimeout) {

		try {
			udpSock.setSoTimeout(1);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Timeout the socket after 1 ms
		try {
			udpSock.receive(receivePacket);
			
		} catch (SocketTimeoutException ste) {
			return 0;
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
		addrs.add(receivePacket.getAddress());
		int len = receivePacket.getLength();
		byte [] data = receivePacket.getData();
		for(int i=0;i<len;i++) {
			message[i]=data[i];
		}
		
		return len;
	}

	@Override
	public int write(byte[] message, int length, int howLongToWaitBeforeTimeout) {

		DatagramPacket sendPacket = new DatagramPacket(message, length, address, port);
		// Log.info("Sending UDP packet: "+sendPacket);
		try {
			udpSock.send(sendPacket);
			return length;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public boolean disconnectDeviceImp() {
		udpSock.disconnect();
		return true ;
	}

	@Override
	public boolean connectDeviceImp() {
		try {
			udpSock = new DatagramSocket();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
