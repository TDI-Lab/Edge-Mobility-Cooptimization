package Utilities;

import java.util.ArrayList;
import java.util.List;

public class TimeStamp {
	boolean valid;
	public double time;
	List<Vehicle> ts;
	int [] AP;//num of vehicles per AP
	
	public TimeStamp(double time2, Vehicle vehicle) {
		// TODO Auto-generated constructor stub
		valid = true;
		time = time2;
		AP = new int [468];
		ts = new ArrayList<>();//468
		addVehicle(vehicle);
		
	}
	public TimeStamp(double time2) {
		time = time2;
		valid = false;
	}
	public List<Vehicle> getTs() {
		return ts;
	}
	public List<Vehicle> getTs(double t) {
		return ts;
	}
	public void setTs(List<Vehicle> ts) {
		this.ts = ts;
	}

	public void addVehicle(Vehicle v) {
		this.ts.add(v);
		AP[v.ConnectedAP]++;
	}
	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

}
