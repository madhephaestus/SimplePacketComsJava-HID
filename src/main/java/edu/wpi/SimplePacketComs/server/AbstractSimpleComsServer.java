package edu.wpi.SimplePacketComs.server;

import java.util.HashMap;

import edu.wpi.SimplePacketComs.BytePacketType;
import edu.wpi.SimplePacketComs.FloatPacketType;
import edu.wpi.SimplePacketComs.IPhysicalLayer;
import edu.wpi.SimplePacketComs.PacketType;
import edu.wpi.SimplePacketComs.device.Device;

public abstract class AbstractSimpleComsServer implements Device,IPhysicalLayer {
	private HashMap<Integer,IOnPacketEvent> servers = new HashMap<>();
	private HashMap<IOnPacketEvent,PacketType> packets = new HashMap<>();
	boolean connected = false;
	private byte[] data=null;
	
	public void addServer(int i, IOnPacketEvent iOnBytePacketEvent) {
		servers.put(i,iOnBytePacketEvent);
		if(IOnBytePacketEvent.class.isInstance(iOnBytePacketEvent)) {
			packets.put(iOnBytePacketEvent, new BytePacketType(i, getPacketSize()));
		}
		if(IOnFloatPacketEvent.class.isInstance(iOnBytePacketEvent)) {
			packets.put(iOnBytePacketEvent, new FloatPacketType(i, getPacketSize()));
		}
	}
	@Override
	public boolean connect() {
		connected=connectDeviceImp();
		new Thread(()->{
			while(connected) {
				int readAmount = read(getData(),2);
				if(readAmount>0) {
					int ID = PacketType.getId(getData());
					IOnPacketEvent event = servers.get(ID);
					if(event!=null) {
						PacketType packet = packets.get(event);
						if(packet!=null) {
							Number[] dataValues = packet.parse(getData());
							if(event.event(dataValues)) {
								byte[] backData = packet.command(ID, dataValues);
								write(backData, getPacketSize(), 2);
							}else {
								// flagged for no return
							}
						}
					}
				}
			}
			disconnectDeviceImp();
			System.out.println("Disconnect AbstractSimpleComsServer");
		}).start();
		return connected;
	}

	@Override
	public void disconnect() {
		connected=false;
	}
	private byte[] getData() {
		if(data==null)
			data = new byte[getPacketSize()];
		return data;
	}

	public abstract int read(byte[] message, int howLongToWaitBeforeTimeout);

	public abstract int write(byte[] message, int length, int howLongToWaitBeforeTimeout);
	public abstract int getPacketSize();
	
	


}
