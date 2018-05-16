package hapticDevice.edu.wpi;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.util.HashSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.wpi.SimplePacketComs.BytePacketType;
import edu.wpi.SimplePacketComs.phy.UDPSimplePacketComs;

public class UDPTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void test() throws Exception {
		HashSet<InetAddress> addresses = UDPSimplePacketComs.getAllAddresses("GameController_*");
		if (addresses.size() < 1) {
			fail("No devices found");
		}
		for (InetAddress add : addresses) {
			System.out.println("Got " + add);
			UDPSimplePacketComs device = new UDPSimplePacketComs(add);
			BytePacketType gameController = new BytePacketType(1970, 64);
			device.addPollingPacket(gameController);
			device.addEvent(1970, () -> {
				System.out.print("\r\n\r\nPacket updated: ");
				for (int i = 0; i < gameController.upstream.length; i++) {
					System.out.print("Got: " + gameController.upstream[i] + " ");

				}
			});
			device.connect();
			Thread.sleep(100);
			for (int i = 0; i < gameController.downstream.length; i++) {
				gameController.downstream[i]=new Byte((byte) 42);
				System.out.print("Send "+i+" " + gameController.downstream[i] + " ");

			}

			Thread.sleep(100);
			for (int i = 0; i < gameController.downstream.length; i++) {
				gameController.downstream[i]=new Byte((byte) 61);
				System.out.print("Send "+i+" " + gameController.downstream[i] + " ");

			}
			Thread.sleep(10000);

			device.disconnect();
		}

	}
	@Test
	public void swarmTest() throws Exception {
//		HashSet<InetAddress> addresses = UDPSimplePacketComs.getAllAddresses();
//		if (addresses.size() < 1) {
//			fail("No devices found");
//		}
//		for (InetAddress add : addresses) {
//			System.out.println("Got " + add);
//			UDPSimplePacketComs device = new UDPSimplePacketComs(add);
//			FloatPacketType packetOne = new FloatPacketType(1871, 64);
//			FloatPacketType packetTwo = new FloatPacketType(1936, 64);
//			BytePacketType packetThree = new BytePacketType(2012, 64);
//			device.addPollingPacket(packetOne);
//			device.addPollingPacket(packetTwo);
//			device.addPollingPacket(packetThree);
//			device.addEvent(1871, ()->{
//				//System.out.print("\r\n\r\n1871 Packet updated: ");
//				for (int i = 0; i < packetOne.upstream.length; i++) {
//					//System.out.println(" " + packetOne.upstream[i] + " ");
//					if(Math.abs((Float)packetOne.upstream[i] - 27.0) > 0.0001) {
//						fail(27+" expected Wrong response "+packetOne.upstream[i]);
//					}
//				}
//			});
//			device.addEvent(1936,()-> {
//				//System.out.print("\r\n\r\n1936 Packet updated: ");
//				for (int i = 0; i < packetTwo.upstream.length; i++) {
//					//System.out.println(" " + packetTwo.upstream[i] + " ");
//					if(Math.abs((Float)packetTwo.upstream[i] - 17.0)> 0.0001) {
//						fail(17+" expected Wrong response "+packetTwo.upstream[i]);
//					}
//				}
//			});
//			device.addEvent(2012,()-> {
//				System.out.print("\r\n\r\n2012 Packet updated: ");
//				for (int i = 0; i < packetThree.upstream.length; i++) {
//					System.out.println(" " + packetThree.upstream[i] + " ");
//					if(Math.abs((Byte )packetThree.upstream[i] - 37) > 0.0001) {
//						fail(37+" expected Wrong response "+packetThree.upstream[i]);
//					}
//
//				}
//			});
//			device.connect();
//			for (int i = 0; i < packetTwo.downstream.length; i++) {
//				packetTwo.downstream[i]=(float)42;
//				System.out.print("Send "+i+" " + packetTwo.downstream[i] + " ");
//
//			}
//
//			Thread.sleep(10000);
//			for (int i = 0; i < packetTwo.downstream.length; i++) {
//				packetTwo.downstream[i]=(float)61;
//				System.out.print("Send "+i+" " + packetTwo.downstream[i] + " ");
//
//			}
//			Thread.sleep(10000);
//
//			device.disconnect();
//		}

	}
}
