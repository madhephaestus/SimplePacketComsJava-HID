package edu.wpi.SimplePacketComs.phy;

import java.util.ArrayList;
import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;
import edu.wpi.SimplePacketComs.AbstractSimpleComsDevice;

public class HIDSimplePacketComs extends AbstractSimpleComsDevice {
	private int vid = 0;
	private int pid = 0;
	private static HidServices hidServices = null;
	private static ArrayList<HIDSimplePacketComs> connected = new ArrayList<>();

	private HidDevice hidDevice = null;
	public HIDSimplePacketComs(int vidIn, int pidIn) {
		// constructor
		setVid(vidIn);
		setPid(pidIn);
	}
	public HIDSimplePacketComs() {
	}


	@Override
	public int read(byte[] message, int howLongToWaitBeforeTimeout) {
		return hidDevice.read(message, howLongToWaitBeforeTimeout);
	}

	@Override
	public int write(byte[] message, int length, int howLongToWaitBeforeTimeout) {
		return hidDevice.write(message, length, (byte) 0);
	}

	@Override
	public boolean disconnectDeviceImp() {
		// TODO Auto-generated method stub
		if (hidDevice != null) {
			hidDevice.close();
		}
		if(connected.contains(this))
			connected.remove(this);
		if(connected.size()==0)
			if (hidServices != null) {
				// Clean shutdown
				hidServices.shutdown();
				hidServices = null;
			}
		System.out.println("HID device clean shutdown");
		return false;
	}

	@Override
	public boolean connectDeviceImp() {
		// TODO Auto-generated method stub
		if (hidServices == null)
			hidServices = HidManager.getHidServices();
		// Provide a list of attached devices
		hidDevice = null;
		int foundInterface=-1;
		for (HidDevice h : hidServices.getAttachedHidDevices()) {
			if (h.isVidPidSerial(getVid(), getPid(), null)) {
				System.out.println(hidDevice);
				if (hidDevice == null &&h.getInterfaceNumber()> foundInterface) {
					hidDevice = h;
					foundInterface=h.getInterfaceNumber();
					System.out.println("Found! " +foundInterface+" "+ hidDevice);
				} else {
					System.out.println("Also Found this matches too.. " + h);
				}

			}
		}
		if(hidDevice == null)
			return false;
		hidDevice.open();
		connected.add(this);
		return true;
	}
	public int getVid() {
		return vid;
	}
	public void setVid(int vid) {
		this.vid = vid;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}

}
