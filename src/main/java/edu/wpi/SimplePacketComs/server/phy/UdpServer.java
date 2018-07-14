package edu.wpi.SimplePacketComs.server.phy;

import edu.wpi.SimplePacketComs.BytePacketType;
import edu.wpi.SimplePacketComs.PacketType;
import edu.wpi.SimplePacketComs.phy.UDPSimplePacketComs;
import edu.wpi.SimplePacketComs.server.AbstractSimpleComsServer;

public class UdpServer extends AbstractSimpleComsServer{
	private UDPSimplePacketComs device=new UDPSimplePacketComs();
	private String name;
	
	public UdpServer(String name) {
		this.name=name;
		addServer(new BytePacketType(1776, 64),packet -> {
			byte[] data = name.getBytes();
			for(int i=0;i<data.length;i++) {
				if(packet[i].byteValue() =='*') {
					for( i=0;i<data.length;i++) {
						// Copy in our name completly
						packet[i]=data[i];
					}
					return true;
				}
				if(packet[i].byteValue()!=data[i]) {
					byte[] dataforString = new byte[60];
					for(int j=0;j<data.length;j++) {
						if(packet[j].byteValue()==0)
							break;
						dataforString[j]=packet[j].byteValue();
					}
					System.out.println("Failed name check, got: "+new String(dataforString).trim()+" expected "+name);
					return false;// failed the name check
				}
			}
			// pass the name check
			return true;
		});
		
	}

	@Override
	public int read(byte[] message, int howLongToWaitBeforeTimeout) {
		// TODO Auto-generated method stub
		return device.read(message, howLongToWaitBeforeTimeout);
	}

	@Override
	public int write(byte[] message, int length, int howLongToWaitBeforeTimeout) {
		// TODO Auto-generated method stub
		return device.write(message, length, howLongToWaitBeforeTimeout);
	}

	@Override
	public boolean disconnectDeviceImp() {
		// TODO Auto-generated method stub
		return device.disconnectDeviceImp();
	}

	@Override
	public boolean connectDeviceImp() {
		// TODO Auto-generated method stub
		return device.connectDeviceImp();
	}
	public String getName() {
		return name;
	}

	@Override
	public int getPacketSize() {
		return 64;
	}


}
