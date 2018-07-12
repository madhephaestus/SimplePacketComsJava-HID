package edu.wpi.SimplePacketComs.device.warehouse;
import javafx.scene.transform.Affine;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import edu.wpi.SimplePacketComs.BytePacketType;
import edu.wpi.SimplePacketComs.FloatPacketType;
import edu.wpi.SimplePacketComs.PacketType;
import edu.wpi.SimplePacketComs.device.UdpDevice;
import edu.wpi.SimplePacketComs.phy.UDPSimplePacketComs;

public class WarehouseRobot extends UdpDevice  {
	private PacketType estop = new BytePacketType(1989, 64);
	private PacketType getStatus = new BytePacketType(2012, 64);
	private PacketType clearFaults = new BytePacketType(1871, 64);
	private PacketType pickOrder = new FloatPacketType(1936, 64);
	private PacketType getLocation = new FloatPacketType(1994, 64);
	private PacketType directDrive = new FloatPacketType(1786, 64);
	private byte[] status = new byte[1];
	private double[] pickOrderData = new double[3];
	private double[] locationData = new double[8];
	private double[] driveData = new double[8];
	private double[] driveStatus = new double[1];
	private Affine locationAffine = new Affine();
	private WarehouseRobot(InetAddress add) throws Exception {
		super(add);
		
		for (PacketType pt : Arrays.asList(clearFaults, pickOrder, getStatus, directDrive, getLocation,estop)) {
			addPollingPacket(pt);
		}

		addEvent(getStatus.idOfCommand, () -> {
			readBytes(getStatus.idOfCommand, status);
		});
		addEvent(directDrive.idOfCommand, () -> {
			readFloats(directDrive.idOfCommand, driveStatus);
		});
		addEvent(getLocation.idOfCommand, () -> {
			readFloats(getLocation.idOfCommand, locationData);
			double rotationAngleRadians = Math.PI / 180 * locationData[3];// azimuth	

			getLocationAffine().setMxx(Math.cos(rotationAngleRadians));
			getLocationAffine().setMxy(Math.sin(rotationAngleRadians));
			getLocationAffine().setMxz(0);
			getLocationAffine().setMyx(-Math.sin(rotationAngleRadians));
			getLocationAffine().setMyy(Math.cos(rotationAngleRadians));
			getLocationAffine().setMyz(0);
			getLocationAffine().setMzx(0);
			getLocationAffine().setMzy(0);
			getLocationAffine().setMzz(1);
			getLocationAffine().setTx(locationData[0]);
			getLocationAffine().setTy(locationData[1]);
			getLocationAffine().setTz(locationData[2]);
			
		});
		pickOrder.waitToSendMode();
		clearFaults.waitToSendMode();
		estop.waitToSendMode();
		
		
	}
	public static List<WarehouseRobot> get(String name) throws Exception {
		HashSet<InetAddress> addresses = UDPSimplePacketComs.getAllAddresses(name);
		ArrayList<WarehouseRobot> robots = new ArrayList<>();
		if (addresses.size() < 1) {
			System.out.println("No WarehouseRobot found named "+name);
			return robots;
		}
		for (InetAddress add : addresses) {
			System.out.println("Got " + add.getHostAddress());
			WarehouseRobot e = new WarehouseRobot(add);
			e.connect();
			robots.add(e);
		}
		return robots;
	}
	
	public static List<WarehouseRobot> get() throws Exception {
		return get("Warehouse*");
	}

	@Override
	public String toString() {
		return getName();
	}


	public List<Double> getLocationData() {
		return DoubleStream.of(locationData).boxed().collect(Collectors.toList());

	}

	public void estop() {
		estop.oneShotMode();
	}
	public double getDriveStatus() {
		return driveStatus[0];
	}
	
	public void pickOrder(double material, double angle,double dropLocation) {
		pickOrderData[0]=material;
		pickOrderData[1]=angle;
		pickOrderData[2]=dropLocation;
		writeFloats(pickOrder.idOfCommand, pickOrderData);
		pickOrder.oneShotMode();

	}

	public void directDrive(double deltaX, double deltaY,double deltaZ,double deltaAzimuth, double deltaElevation,double deltaTilt,double milisecondsTransition) throws Exception {
		if(getDriveStatus()<0.9999) {
			//throw new Exception("The robot is not done with pervious command");
		}
		driveData[0]=deltaX;
		driveData[1]=deltaY;
		driveData[2]=deltaZ;
		driveData[3]=deltaAzimuth;
		driveData[4]=deltaElevation;
		driveData[5]=deltaTilt;
		driveData[6]=milisecondsTransition;
		driveData[7]=(double)(Math.round(Math.random()*100000.0));// random session value do demarkate delta motion sessions

		writeFloats(directDrive.idOfCommand, driveData);
		
	}
	
	public WarehouseRobotStatus getStatus() {
		return WarehouseRobotStatus.fromValue(status[0]);
	}
	
	public void clearFaults() {
		clearFaults.oneShotMode();

	}


	public Affine getLocationAffine() {
		return locationAffine;
	}


	public void setLocationAffine(Affine locationAffine) {
		this.locationAffine = locationAffine;
	}



}
