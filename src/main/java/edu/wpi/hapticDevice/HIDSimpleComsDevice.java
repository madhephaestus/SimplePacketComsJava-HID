package edu.wpi.hapticDevice;

import java.util.ArrayList;
import java.util.HashMap;

import org.hid4java.HidDevice;
import org.hid4java.HidManager;
import org.hid4java.HidServices;

import com.kenai.jffi.Closure;
import com.neuronrobotics.sdk.common.NonBowlerDevice;

public class HIDSimpleComsDevice extends NonBowlerDevice{
	HashMap<Integer,ArrayList<Closure>> events = new HashMap<>();
	HidServices hidServices = null;
	int vid =0 ;
	int pid =0;
	HidDevice hidDevice=null;
	public PacketProcessor processor= new PacketProcessor();
	float [] downstream = new float[15];
	float [] upstream = new float[15];
	boolean HIDconnected = false;
	int idOfCommand=37;
	public HIDSimpleComsDevice(int vidIn, int pidIn){
		// constructor
		vid=vidIn;
		pid=pidIn;
		setScriptingName("hidbowler");
	}
	
	void addEvent(Integer id, Closure event){
		if(events.get(id)==null){
			events.put(id,[]);
		}
		events.get(id).add(event);
	}
	@Override
	public  void disconnectDeviceImp(){		
		HIDconnected=false;
		//println "HID device Termination signel shutdown"
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
		Thread.start{
			//println "Starting HID Thread"
			while(HIDconnected){
				//println "loop"
				for(int i=0;i<10;i++){
					Thread.sleep(1);
					if(hidDevice!=null){
						//println "Writing packet"
						try{
							byte[] message = processor.command(idOfCommand,downstream);
							//println "Writing: "+ message
							int val = hidDevice.write(message, message.length, (byte) 0);
							if(val>0){
								int read = hidDevice.read(message, 1000);
								if(read>0){
									//println "Parsing packet"
									//println "read: "+ message
									upstream=processor.parse(message);
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
						for(int j=0;j<downstream.length&&j<upstream.length;j++){
							upstream[j]=downstream[j];
						}
						
					}
				}
				//println "updaing "+upstream+" downstream "+downstream
				try{
					if(events.get(idOfCommand)!=null){
						for(Closure e:events.get(idOfCommand)){
								e.call();
							
						}
					}
				}catch (Throwable t){
							t.printStackTrace(System.err);
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
		//throw new RuntimeException("No HID device found")
	}
	void setValues(int index,float position, float velocity, float force){
		downstream[(index*3)+0] = position;
		downstream[(index*3)+1] = velocity;
		downstream[(index*3)+2] = force;
		//println "Setting Downstream "+downstream
	}
	float [] getValues(int index){
		float [] back = new float [3];
	
		back[0]=upstream[(index*3)+0];
		back[1]=upstream[(index*3)+1];
		back[2]=upstream[(index*3)+2];
		
		return back;
	}
	@Override
	public  ArrayList<String>  getNamespacesImp(){
		// no namespaces on dummy
		return null;
	}
	
	
}

