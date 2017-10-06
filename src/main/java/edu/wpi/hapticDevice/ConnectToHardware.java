package edu.wpi.hapticDevice;

import java.util.concurrent.TimeUnit;

import com.neuronrobotics.bowlerstudio.BowlerStudio;
import com.neuronrobotics.sdk.addons.kinematics.LinkConfiguration;
import com.neuronrobotics.sdk.addons.kinematics.LinkFactory;
import com.neuronrobotics.sdk.addons.kinematics.MobileBase;
import com.neuronrobotics.sdk.common.BowlerAbstractDevice;
import com.neuronrobotics.sdk.common.DeviceManager;

class ConnectToHardware {

	public static void main(String[] args) 
	{

		HIDSimpleComsDevice dev = (HIDSimpleComsDevice) DeviceManager.getSpecificDevice("hidbowler", () -> {
			// If the device does not exist, prompt for the connection

			HIDSimpleComsDevice d = new HIDSimpleComsDevice(0x3742, 0x7);
			d.connect(); // Connect to it.
			LinkFactory.addLinkProvider("hidsimple", (LinkConfiguration conf) -> {
				// println "Loading link "
				return new HIDRotoryLink(d, conf);
			});
			// println "Connecting new device: "+d
			return (BowlerAbstractDevice) d;
		});
		MobileBase base = (MobileBase) DeviceManager.getSpecificDevice("HephaestusArm", () -> {
			// If the device does not exist, prompt for the connection

			MobileBase m;

			try {
				m = BowlerStudio.loadMobileBaseFromGit(
						"https://github.com/StrokeRehabilitationRobot/SeriesElasticActuator.git", "HIDarm.xml");
				if (m == null)
					throw new RuntimeException("Arm failed to assemble itself");
				// println "Connecting new device robot arm "+m
				return m;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;

		});

		// command index
		base.getAllDHChains().get(0).getCurrentJointSpaceVector();
		long sum;
		int min = 100000;
		int max = 0;
		float sinWaveInc = 200;
		float seconds = (float) 0.01;
		float range = -256;
		for (int i = 1; i < sinWaveInc; i++) {
			for (int j = 0; j < 3; j++) {
				dev.setValues((int) j, (float) (Math.sin(((float) i) / sinWaveInc * Math.PI * 2) * range) - range,
						(float) ((Math.cos(((float) i) / sinWaveInc * Math.PI * 2) * range) / seconds), (float) 0);
				float[] returnValues = dev.getValues(j);
				System.out.println("Return data " + j + " " + returnValues[0]);
			}
			try {
				Thread.sleep((long) (1000.0 * seconds));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("disconnect");
		base.disconnect();
		dev.disconnect();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("done");
		//System.exit(0);

		
	}

}
