package hapticDevice.edu.wpi;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.wpi.SimplePacketComs.device.hephaestus.HephaestusArm;

public class HIDTest {

	@Test
	public void test() throws InterruptedException {
		HephaestusArm arm = new HephaestusArm(0x3742,0x7);
		arm.connect();
		Thread.sleep(100);

		for(int i=0;i<3;i++) {
			System.out.println("Data Index "+i+" "+arm.getValues(i));
		}
		Thread.sleep(100);
		arm.disconnect();
		Thread.sleep(100);
	}

}
