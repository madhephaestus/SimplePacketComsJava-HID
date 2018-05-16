package edu.wpi.SimplePacketComs.phy;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;

import edu.wpi.SimplePacketComs.AbstractSimpleComsDevice;

public class HIDSimplePacketComs extends AbstractSimpleComsDevice {
	private int vid = 0;
	private int pid = 0;
	private HidServices hidServices = null;
	private HidDevice hidDevice = null;

	public HIDSimplePacketComs(int vidIn, int pidIn) {
		// constructor
		vid = vidIn;
		pid = pidIn;
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
		if (hidServices != null) {
			// Clean shutdown
			hidServices.shutdown();
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
		for (HidDevice h : hidServices.getAttachedHidDevices()) {
			if (h.isVidPidSerial(vid, pid, null)) {
				if (hidDevice == null) {
					hidDevice = h;

					hidDevice.open();
					System.out.println("Found! " + hidDevice);
					return true;
				} else {
					System.out.println("Already opened! this matches too.. " + h);
				}

			}
		}

		return false;
	}

}
