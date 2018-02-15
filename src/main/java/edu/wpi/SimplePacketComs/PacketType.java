package edu.wpi.SimplePacketComs;

public abstract class PacketType{
	int idOfCommand=0;
	Number [] downstream;
	Number [] upstream;
	boolean done=false;
	boolean started = false;
	public PacketProcessor processor;
	public PacketType(int id){
		idOfCommand=id;
	}
	
}