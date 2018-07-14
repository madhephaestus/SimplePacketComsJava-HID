package edu.wpi.SimplePacketComs.device;

public interface Device {
	
	public boolean connect();
	public void disconnect();
	public String getName();
	
}
