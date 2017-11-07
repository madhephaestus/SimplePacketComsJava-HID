package edu.wpi.hapticDevice;

import java.util.ArrayList;
import java.util.HashMap;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;

import com.neuronrobotics.sdk.common.NonBowlerDevice;

import groovy.lang.Closure;

public class HIDSimpleComsDevice extends NonBowlerDevice{
	HashMap<Integer, ArrayList<Runnable>> events = new HashMap<>();

	HidServices hidServices = null;
	int vid =0 ;
	int pid =0;
	HidDevice hidDevice=null;
	public PacketProcessor processor= new PacketProcessor();
	boolean HIDconnected = false;
	PacketType pollingPacket = new PacketType(37);
	PacketType pidPacket = new PacketType(65);
	PacketType PDVelPacket = new PacketType(48);
	PacketType SetVelocity = new PacketType(42);
	
	ArrayList<PacketType> processQueue = new ArrayList<PacketType>();
	
	public HIDSimpleComsDevice(int vidIn, int pidIn){
		// constructor
		vid=vidIn;
		pid=pidIn;
		setScriptingName("hidbowler");
	}

	public void pushPacket(PacketType packet){
		packet.done=false;
		packet.started = false;
		processQueue.add(packet);
		while(packet.done==false){
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	void removeEvent(Integer id, Runnable event){
		if(events.get(id)==null){
			events.put(id, new ArrayList<>());
		}
		events.get(id).remove(event);
	}
	void addEvent(Integer id, Runnable event){
		if(events.get(id)==null){
			events.put(id, new ArrayList<>());
		}
		events.get(id).add(event);
	}
	@Override
	public  void disconnectDeviceImp(){		
		HIDconnected=false;
		//println "HID device Termination signel shutdown"
	}
	private void process(PacketType packet){
		packet.started=true;
		try{
			if(hidDevice!=null){
				//println "Writing packet"
				try{
					byte[] message = processor.command(packet.idOfCommand,packet.downstream);
					//println "Writing: "+ message
					int val = hidDevice.write(message, message.length, (byte) 0);
					if(val>0){
						int read = hidDevice.read(message, 1000);
						if(read>0){
							//println "Parsing packet"
							//println "read: "+ message
							float[] up = processor.parse(message);
							for(int i=0;i<packet.upstream.length;i++){
								packet.upstream[i]=up[i];
							}
						}else{
							//println "Read failed"	
						}
						
					}
				}catch (Throwable t){
					t.printStackTrace(System.out);
					disconnect();
				}
			}else{
				//println "Simulation"
				for(int j=0;j<packet.downstream.length&&j<packet.upstream.length;j++){
					packet.upstream[j]=packet.downstream[j];
			}
				
			}
			//println "updaing "+upstream+" downstream "+downstream
		
			if(events.get(packet.idOfCommand)!=null){
				for(Runnable e:events.get(packet.idOfCommand)){
					if(e!=null){
						try{
							e.run();
						}catch (Throwable t){
							t.printStackTrace(System.out);						
						}
					}
				}
			}
		}catch (Throwable t){
					t.printStackTrace(System.out);
		}
		packet.done=true;
	}
	
	@Override
	public  boolean connectDeviceImp(){
		if(hidServices==null)
			hidServices = HidManager.getHidServices();
		// Provide a list of attached devices
		hidDevice=null;
		for (HidDevice h : hidServices.getAttachedHidDevices()) {
		  if(h.isVidPidSerial(vid, pid, null)){
		  	  hidDevice=h;
			 
			  hidDevice.open();
			  System.out.println("Found! "+hidDevice);
			 
		  }
		}
		HIDconnected=true;
		new Thread(){
		public void run(){
			//println "Starting HID Thread"
			while(HIDconnected){
				//println "loop"
				try{
					Thread.sleep(1);
					if(pollingPacket!=null){
						pollingPacket.done=false;
						pollingPacket.started = false;
						process(pollingPacket);
					}
					while(processQueue.size()>0){
						try{
							PacketType temPack = processQueue.remove(0);
							if(temPack!=null){
								//println "Processing "+temPack
								process(temPack);
							}
						}catch(Exception e){
							e.printStackTrace();
						}
						
					}
				}catch(Exception e){
					e.printStackTrace();
				}

				
			}
			if(hidDevice !=null){
				hidDevice.close();
			}
			if(hidServices!=null){
				// Clean shutdown
				hidServices.shutdown();
			}
			//println "HID device clean shutdown"
		 }
		}.start();
		//throw new RuntimeException("No HID device found")
		return true;
	}
	void setValues(int index,float position, float velocity, float force){
		pollingPacket.downstream[(index*3)+0] = position;
		pollingPacket.downstream[(index*3)+1] = velocity;
		pollingPacket.downstream[(index*3)+2] = force;
		//println "Setting Downstream "+downstream
	}
	void setPIDGains(int index,float kp, float ki, float kd){
		
		pidPacket.downstream[(index*3)+0] = kp;
		pidPacket.downstream[(index*3)+1] = ki;
		pidPacket.downstream[(index*3)+2] = kd;
		//println "Setting Downstream "+downstream
	}
	void pushPIDGains(){
		pushPacket(pidPacket);
	}
	void setPDVelGains(int index,float kp, float kd){
		
		PDVelPacket.downstream[(index*2)+0] = kp;
		PDVelPacket.downstream[(index*2)+1] = kd;
		//println "Setting Downstream "+downstream
	}
	void pushPDVelGains(){
		pushPacket(PDVelPacket);
	}
	void setVelocity(int index,float TPS){
		SetVelocity.downstream[index] = TPS;
		//println "Setting Downstream "+downstream
	}
	void pushVelocity(){
		pushPacket(SetVelocity);
	}
	float [] getValues(int index){
		float [] back = new float [3];
	
		back[0]=pollingPacket.upstream[(index*3)+0];
		back[1]=pollingPacket.upstream[(index*3)+1];
		back[2]=pollingPacket.upstream[(index*3)+2];
		
		return back;
	}
	@Override
	public  ArrayList<String>  getNamespacesImp(){
		// no namespaces on dummy
		return null;
	}
	
	
}

