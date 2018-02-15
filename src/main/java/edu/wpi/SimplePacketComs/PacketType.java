package edu.wpi.SimplePacketComs;

public class PacketType{
	int idOfCommand=0;
	float [] downstream = new float[15];
	float [] upstream = new float[15];
	boolean done=false;
	boolean started = false;
	public PacketType(int id){
		idOfCommand=id;
	}
	
}