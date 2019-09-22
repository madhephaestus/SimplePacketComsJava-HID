package hapticDevice.edu.wpi;

import static org.junit.Assert.*;

import org.junit.Test;

public class newArmTest {
	boolean failed=false;
	@Test
	public void test() throws Exception {
		RBE3001Robot bot = new RBE3001Robot(0x16C0, 0x0486, 3);
		Runnable event  = new Runnable() {
			@Override
			public void run() {
				failed=true;
			}
		};
		bot.addTimeout(1910, event  );
		bot.addTimeout(1857, event  );
		Thread.sleep(5000);
		if(failed)
			fail();
		bot.setPidSetpoint(1000, 1, 0, bot.getPidSetpoint(0)+360);
		Thread.sleep(1000);
		bot.disconnect();
		Thread.sleep(1000);
	}

}
