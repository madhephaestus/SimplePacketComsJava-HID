package edu.wpi.SimplePacketComs;

public interface IPhysicalLayer {
	
	public abstract boolean disconnectDeviceImp();
	public abstract boolean connectDeviceImp();
	
	public abstract int read(byte[] message, int howLongToWaitBeforeTimeout);

	public abstract int write(byte[] message, int length, int howLongToWaitBeforeTimeout);

}
