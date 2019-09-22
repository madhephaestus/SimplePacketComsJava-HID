package hapticDevice.edu.wpi;

import java.util.HashMap;
import java.util.Map;

public enum WarehouseRobotStatus {
	Ready_for_new_task ( (byte)0),
	Heading_to_pickup ( (byte)1),
	Waiting_for_approval_to_pickup( (byte)2),
	Picking_up ( (byte)3),
	Heading_to_Dropoff ((byte) 4),
	Waiting_for_approval_to_dropoff( (byte)5),
	Dropping_off ( (byte)6),
	Heading_to_safe_zone ((byte) 7),
	Fault_failed_pickup ( (byte)8),
	Fault_failed_dropoff ((byte) 9),
	Fault_excessive_load ((byte) 10),
	Fault_obstructed_path ((byte) 11),
	Fault_E_Stop_pressed ( (byte)12);
	private static final Map<Byte, WarehouseRobotStatus> lookup = new HashMap<>();

    static {
        for (WarehouseRobotStatus d : WarehouseRobotStatus.values()) {
            lookup.put(d.getValue(), d);
        }
    }

	private byte value;
	
	public static WarehouseRobotStatus fromValue(byte b) {
		return lookup.get(b);
	}

	WarehouseRobotStatus(byte value){
		this.value = value;
		
	}

	public byte getValue() {
		return value;
	}

	public void setValue(byte value) {
		this.value = value;
	}
	public boolean isFault() {
		return value>6;
	}
	
}
