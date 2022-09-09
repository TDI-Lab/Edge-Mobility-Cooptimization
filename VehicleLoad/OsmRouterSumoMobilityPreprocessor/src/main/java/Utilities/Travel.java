package Utilities;

public class Travel {
	int StartTime;
	int FinishTime;
	String VehicleId;
	
	
	public int getFinishTime() {
		return FinishTime;
	}


	public void setFinishTime(int finishTime) {
		FinishTime = finishTime;
	}


	public int getStartTime() {
		return StartTime;
	}


	public String getVehicleId() {
		return VehicleId;
	}


	public Travel(String vehicle, int startTime) {
	super();
	StartTime = startTime;
	VehicleId = vehicle;
	}
		
}
