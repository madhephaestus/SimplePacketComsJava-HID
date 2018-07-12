package edu.wpi.SimplePacketComs;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractSimpleComsDevice {
	HashMap<Integer, ArrayList<Runnable>> events = new HashMap<>();
	boolean connected = false;

	ArrayList<PacketType> pollingQueue = new ArrayList<PacketType>();

	private boolean virtual = false;
	

	public abstract int read(byte[] message, int howLongToWaitBeforeTimeout);

	public abstract int write(byte[] message, int length, int howLongToWaitBeforeTimeout);

	public abstract boolean disconnectDeviceImp();
	private String name = "SimpleComsDevice";
	public abstract boolean connectDeviceImp();
	private int readTimeout = 100;
	private boolean isTimedOut  =false;
	public void addPollingPacket(PacketType packet) {
		if(getPacket(  packet.idOfCommand)!=null)
				throw new RuntimeException(
						"Only one packet of a given ID is allowed to poll. Add an event to recive data");
		
		pollingQueue.add(packet);
	}
	private PacketType getPacket( int ID) {
		for (PacketType q : pollingQueue) {
			if (q.idOfCommand == ID) {
				return q;
			}
		}
		return null;
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

	public ArrayList<Integer> getIDs() {
		ArrayList<Integer> ids = new ArrayList<>();
		for (PacketType pt : pollingQueue) {
			ids.add(pt.idOfCommand);
		}
		return ids;
	}

	public void writeFloats(int id, double[] values) {
		for (PacketType pt : pollingQueue) {
			if (FloatPacketType.class.isInstance(pt))
				if (pt.idOfCommand == id) {
					for (int i = 0; i < pt.downstream.length && i<values.length; i++) {
						pt.downstream[i] = (float) values[i];
					}
					return;
				}
		}
	}

	public void writeBytes(int id, byte[] values) {
		for (PacketType pt : pollingQueue) {
			if (BytePacketType.class.isInstance(pt))

				if (pt.idOfCommand == id) {
					for (int i = 0; i < pt.downstream.length && i<values.length; i++) {
						pt.downstream[i] = (byte) values[i];
					}
					return;
				}
		}
	}

	public void readFloats(int id, double[] values) {
		for (PacketType pt : pollingQueue) {
			if (FloatPacketType.class.isInstance(pt))

				if (pt.idOfCommand == id) {
					for (int i = 0; i < pt.upstream.length && i<values.length; i++) {
						float d =  (float) pt.upstream[i];
						values[i] = d;
					}
					return;
				}
		}	
	}

	public void readBytes(int id, byte[] values) {
		for (PacketType pt : pollingQueue) {
			if (BytePacketType.class.isInstance(pt))
				if (pt.idOfCommand == id) {
					for (int i = 0; i < pt.upstream.length && i<values.length; i++) {
						values[i] = (byte) pt.upstream[i];
					}
					return;
				}
		}
	}

	private void process(PacketType packet) {
		packet.started = true;
		try {
			if (!isVirtual()) {
				try {
					byte[] message = packet.command(packet.idOfCommand, packet.downstream);
					// println "Writing: "+ message
					int val = write(message, message.length, 1);
					if (val > 0) {
						int read = read(message, getReadTimeout());
						if (read >= packet.upstream.length) {
							// println "Parsing packet"
							// println "read: "+ message
							int ID = PacketType.getId(message);
							if (ID == packet.idOfCommand) {
								if(isTimedOut) {
									System.out.println("Timout resolved "+ID);
								}
								isTimedOut=false;
								Number[] up = packet.parse(message);
								for (int i = 0; i < packet.upstream.length; i++) {
									packet.upstream[i] = up[i];
								}
								// System.out.println("Took "+(System.currentTimeMillis()-start));
							} else {
								//readTimeout=readTimeout+(readTimeout/2);

								//System.out.print("\r\nCross Talk expected " + packet.idOfCommand + " Got: " + ID+" waiting "+readTimeout);
								
								for (int i = 0; i < 3; i++) {
									read(message, getReadTimeout());// clear any possible stuck messages

								}
								System.out.println(" ");
								isTimedOut=true;
								return;
							}
						} else {
							//System.out.println("Read failed");
							//readTimeout=readTimeout+(readTimeout/2);
							isTimedOut=true;
							return;
						}

					} else
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
			//t.printStackTrace(System.out);
		}
		packet.done = true;
	}

	private int getReadTimeout() {
		
		return readTimeout;
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
						for (PacketType pollingPacket : pollingQueue) {
							if(pollingPacket.sendOk())
								process(pollingPacket);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						Thread.sleep(1);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						connected = false;
					}
				}
				disconnectDeviceImp();
				System.out.println("SimplePacketComs disconnect");
			}
		}.start();
		// throw new RuntimeException("No HID device found")
		return true;
	}

	public void disconnect() {
		connected = false;
	}

	public boolean isVirtual() {
		// TODO Auto-generated method stub
		return virtual;
	}
	
	public void setVirtual(boolean virtual) {
		this.virtual = virtual;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isTimedOut() {
		return isTimedOut;
	}

	

}
