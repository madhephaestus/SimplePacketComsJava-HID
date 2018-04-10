package edu.wpi.SimplePacketComs;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractSimpleComsDevice {
	HashMap<Integer, ArrayList<Runnable>> events = new HashMap<>();

	// HidServices hidServices = null;
	// int vid =0 ;
	// int pid =0;
	// HidDevice hidDevice=null;
	//public PacketProcessor processor = new PacketProcessor();
	boolean connected = false;
	// PacketType pollingPacket = new PacketType(37);
	// PacketType pidPacket = new PacketType(65);
	// PacketType PDVelPacket = new PacketType(48);
	// PacketType SetVelocity = new PacketType(42);

	ArrayList<PacketType> processQueue = new ArrayList<PacketType>();
	ArrayList<PacketType> pollingQueue = new ArrayList<PacketType>();

	private boolean virtual = false;

	public void addPollingPacket(PacketType packet) {
		for (PacketType q : pollingQueue) {
			if (q.idOfCommand == packet.idOfCommand) {
				throw new RuntimeException(
						"Only one packet of a given ID is allowed to poll. Add an event to recive data");
			}
		}
		pollingQueue.add(packet);
	}

	public void pushPacket(PacketType packet) {
		packet.done = false;
		packet.started = false;
		processQueue.add(packet);
		while (packet.done == false) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void removeEvent(Integer id, Runnable event) {
		if (events.get(id) == null) {
			events.put(id, new ArrayList<>());
		}
		events.get(id).remove(event);
	}

	public void addEvent(Integer id, Runnable event) {
		if (events.get(id) == null) {
			events.put(id, new ArrayList<>());
		}
		events.get(id).add(event);
	}

	private void process(PacketType packet) {
		packet.started = true;
		try {
			if (!isVirtual()) {
				// println "Writing packet"
				try {
					byte[] message = packet.command(packet.idOfCommand, packet.downstream);
					// println "Writing: "+ message
					int val = write(message, message.length, 1);
					if (val > 0) {
						int read = read(message, 1000);
						if (read > 0) {
							// println "Parsing packet"
							// println "read: "+ message
							int ID = PacketType.getId(message);
							if(ID == packet.idOfCommand) {
								Number[] up = packet.parse(message);
								for (int i = 0; i < packet.upstream.length; i++) {
									packet.upstream[i] = up[i];
								}
							}else {
								System.out.println( "Crosstalk packet!!");
							}
						} else {
							System.out.println( "Read failed");
						}

					}else
						return;
				} catch (Throwable t) {
					t.printStackTrace(System.out);
					disconnect();
				}
			} else {
				// println "Simulation"
				for (int j = 0; j < packet.downstream.length && j < packet.upstream.length; j++) {
					packet.upstream[j] = packet.downstream[j];
				}

			}
			// println "updaing "+upstream+" downstream "+downstream

			if (events.get(packet.idOfCommand) != null) {
				for (Runnable e : events.get(packet.idOfCommand)) {
					if (e != null) {
						try {
							e.run();
						} catch (Throwable t) {
							t.printStackTrace(System.out);
						}
					}
				}
			}
		} catch (Throwable t) {
			t.printStackTrace(System.out);
		}
		packet.done = true;
	}

	public boolean connect() {
		if (connectDeviceImp()) {
			setVirtual(false);
		} else {
			setVirtual(true);
		}
		
		connected = true;
		new Thread() {
			public void run() {
				// println "Starting HID Thread"
				while (connected) {
					
					// println "loop"
					try {
						for(PacketType pollingPacket:pollingQueue) {
							process(pollingPacket);
						}
						while (processQueue.size() > 0) {
							try {
								PacketType temPack = processQueue.remove(0);
								if (temPack != null) {
									// println "Processing "+temPack
									process(temPack);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						connected=false;
					}
				}
				disconnect();
			}
		}.start();
		// throw new RuntimeException("No HID device found")
		return true;
	}

	public void disconnect() {
		connected=false;
		disconnectDeviceImp();
	}
	private boolean isVirtual() {
		// TODO Auto-generated method stub
		return virtual;
	}
	
	public abstract int read(byte[] message, int howLongToWaitBeforeTimeout);


	public abstract int write(byte[] message, int length, int howLongToWaitBeforeTimeout);

	public abstract boolean disconnectDeviceImp();

	public abstract boolean connectDeviceImp();

	/*
	 * { if(hidServices==null) hidServices = HidManager.getHidServices(); // Provide
	 * a list of attached devices hidDevice=null; for (HidDevice h :
	 * hidServices.getAttachedHidDevices()) { if(h.isVidPidSerial(vid, pid, null)){
	 * hidDevice=h;
	 * 
	 * hidDevice.open(); System.out.println("Found! "+hidDevice);
	 * 
	 * } } HIDconnected=true; new Thread(){ public void run(){ //println
	 * "Starting HID Thread" while(HIDconnected){ //println "loop" try{
	 * Thread.sleep(1); if(pollingPacket!=null){ pollingPacket.done=false;
	 * pollingPacket.started = false; process(pollingPacket); }
	 * while(processQueue.size()>0){ try{ PacketType temPack =
	 * processQueue.remove(0); if(temPack!=null){ //println "Processing "+temPack
	 * process(temPack); } }catch(Exception e){ e.printStackTrace(); }
	 * 
	 * } }catch(Exception e){ e.printStackTrace(); }
	 * 
	 * 
	 * } if(hidDevice !=null){ hidDevice.close(); } if(hidServices!=null){ // Clean
	 * shutdown hidServices.shutdown(); } //println "HID device clean shutdown" }
	 * }.start(); //throw new RuntimeException("No HID device found") return true; }
	 * void setValues(int index,float position, float velocity, float force){
	 * pollingPacket.downstream[(index*3)+0] = position;
	 * pollingPacket.downstream[(index*3)+1] = velocity;
	 * pollingPacket.downstream[(index*3)+2] = force; //println
	 * "Setting Downstream "+downstream } void setPIDGains(int index,float kp, float
	 * ki, float kd){
	 * 
	 * pidPacket.downstream[(index*3)+0] = kp; pidPacket.downstream[(index*3)+1] =
	 * ki; pidPacket.downstream[(index*3)+2] = kd; //println
	 * "Setting Downstream "+downstream } void pushPIDGains(){
	 * pushPacket(pidPacket); } void setPDVelGains(int index,float kp, float kd){
	 * 
	 * PDVelPacket.downstream[(index*2)+0] = kp; PDVelPacket.downstream[(index*2)+1]
	 * = kd; //println "Setting Downstream "+downstream } void pushPDVelGains(){
	 * pushPacket(PDVelPacket); } void setVelocity(int index,float TPS){
	 * SetVelocity.downstream[index] = TPS; //println
	 * "Setting Downstream "+downstream } void pushVelocity(){
	 * pushPacket(SetVelocity); } float [] getValues(int index){ float [] back = new
	 * float [3];
	 * 
	 * back[0]=pollingPacket.upstream[(index*3)+0];
	 * back[1]=pollingPacket.upstream[(index*3)+1];
	 * back[2]=pollingPacket.upstream[(index*3)+2];
	 * 
	 * return back; }
	 */
	public void setVirtual(boolean virtual) {
		this.virtual = virtual;
	}

}
