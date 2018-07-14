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
		pollingPacket.getDownstream()[(index*3)+0] = position;
		pollingPacket.getDownstream()[(index*3)+1] = velocity;
		pollingPacket.getDownstream()[(index*3)+2] = force;
		//println "Setting Downstream "+downstream
	}
	public void setPIDGains(int index,float kp, float ki, float kd){
		
		pidPacket.getDownstream()[(index*3)+0] = kp;
		pidPacket.getDownstream()[(index*3)+1] = ki;
		pidPacket.getDownstream()[(index*3)+2] = kd;
		//println "Setting Downstream "+downstream
	}
	public void pushPIDGains(){
		pidPacket.oneShotMode();
	}
	public void setPDVelGains(int index,float kp, float kd){
		
		PDVelPacket.getDownstream()[(index*2)+0] = kp;
		PDVelPacket.getDownstream()[(index*2)+1] = kd;
		//println "Setting Downstream "+downstream
	}
	public void pushPDVelGains(){
		PDVelPacket.oneShotMode();
	}
	public void setVelocity(int index,float TPS){
		SetVelocity.getDownstream()[index] = TPS;
		//println "Setting Downstream "+downstream
	}
	public void pushVelocity(){
		SetVelocity.oneShotMode();
	}
	public List<Double> getValues(int index){
		List<Double> back= new ArrayList<>();
	
		back.add(pollingPacket.getUpstream()[(index*3)+0].doubleValue()) ;
		back.add( pollingPacket.getUpstream()[(index*3)+1].doubleValue());
		back.add(pollingPacket.getUpstream()[(index*3)+2].doubleValue());
		
		return back;
	}
	public double getPosition(int index) {
		return pollingPacket.getUpstream()[(index*3)+0].doubleValue();
	}
	
	public Number[] getRawValues(){
		return pollingPacket.getUpstream();
	}
	public void setRawValues(Number[] set){
		for(int i=0;i<set.length&&i<pollingPacket.getDownstream().length;i++) {
			pollingPacket.getDownstream()[i]=set[i];
		}
	}
}
