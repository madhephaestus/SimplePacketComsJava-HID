package edu.wpi.SimplePacketComs.device.hephaestus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.wpi.SimplePacketComs.*;
import edu.wpi.SimplePacketComs.phy.HIDSimplePacketComs;

public class HephaestusArm extends HIDSimplePacketComs{
	PacketType pollingPacket = new FloatPacketType(37,64);
	PacketType pidPacket = new FloatPacketType(65,64);
	PacketType PDVelPacket = new FloatPacketType(48,64);
	PacketType SetVelocity = new FloatPacketType(42,64);
	public HephaestusArm(int vidIn, int pidIn) {
		super(vidIn, pidIn);
		pidPacket.oneShotMode();
		pidPacket.sendOk();
		PDVelPacket.oneShotMode();		
		PDVelPacket.sendOk();
		SetVelocity.oneShotMode();
		SetVelocity.sendOk();
		for (PacketType pt : Arrays.asList(pollingPacket, pidPacket, PDVelPacket, SetVelocity)) {
			addPollingPacket(pt);
		}
	}
	public void addPollingPacketEvent(Runnable event) {
		addEvent(pollingPacket.idOfCommand, event);
	}
	public void setValuesevent(int index,float position, float velocity, float force){
		pollingPacket.downstream[(index*3)+0] = position;
		pollingPacket.downstream[(index*3)+1] = velocity;
		pollingPacket.downstream[(index*3)+2] = force;
		//println "Setting Downstream "+downstream
	}
	public void setPIDGains(int index,float kp, float ki, float kd){
		
		pidPacket.downstream[(index*3)+0] = kp;
		pidPacket.downstream[(index*3)+1] = ki;
		pidPacket.downstream[(index*3)+2] = kd;
		//println "Setting Downstream "+downstream
	}
	public void pushPIDGains(){
		pidPacket.oneShotMode();
	}
	public void setPDVelGains(int index,float kp, float kd){
		
		PDVelPacket.downstream[(index*2)+0] = kp;
		PDVelPacket.downstream[(index*2)+1] = kd;
		//println "Setting Downstream "+downstream
	}
	public void pushPDVelGains(){
		PDVelPacket.oneShotMode();
	}
	public void setVelocity(int index,float TPS){
		SetVelocity.downstream[index] = TPS;
		//println "Setting Downstream "+downstream
	}
	public void pushVelocity(){
		SetVelocity.oneShotMode();
	}
	public List<Double> getValues(int index){
		List<Double> back= new ArrayList<>();
	
		back.add(pollingPacket.upstream[(index*3)+0].doubleValue()) ;
		back.add( pollingPacket.upstream[(index*3)+1].doubleValue());
		back.add(pollingPacket.upstream[(index*3)+2].doubleValue());
		
		return back;
	}
	public double getPosition(int index) {
		return pollingPacket.upstream[(index*3)+0].doubleValue();
	}
	
	public Number[] getRawValues(){
		return pollingPacket.upstream;
	}
	public void setRawValues(Number[] set){
		for(int i=0;i<set.length&&i<pollingPacket.downstream.length;i++) {
			pollingPacket.downstream[i]=set[i];
		}
	}
}
