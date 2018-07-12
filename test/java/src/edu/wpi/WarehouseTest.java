package edu.wpi;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.wpi.SimplePacketComs.device.warehouse.WarehouseRobot;

public class WarehouseTest {
	WarehouseRobot robot=null;
	@Before
	public void setUp() throws Exception {
		robot=WarehouseRobot.get().get(0);
		robot.connect();
		robot.setReadTimeout(1000);
	}

	@After
	public void tearDown() throws Exception {
		robot.disconnect();
	}

	@Test
	public void test() throws Exception {
		long start = System.currentTimeMillis();
		for(int i=0;i<10000;i++) {
			Thread.sleep(100);
			long now =System.currentTimeMillis()-start;
			double sec = ((double)now)/1000.0;
			System.out.println("Elapsed "+sec);
			if(robot.isTimedOut())
				fail("Timeout! after"+sec);
		}
	}

}
