package hapticDevice;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.util.HashSet;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.wpi.SimplePacketComs.bytepacket.BytePacketType;
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
		HashSet<InetAddress> addresses = UDPSimplePacketComs.getAllAddresses();
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
				for (int i = 0; i < 5; i++) {
					System.out.print(" " + gameController.upstream[i] + " ");

				}
			});
			device.connect();
			Thread.sleep(500000);
			//device.disconnect();
		}

	}

}
