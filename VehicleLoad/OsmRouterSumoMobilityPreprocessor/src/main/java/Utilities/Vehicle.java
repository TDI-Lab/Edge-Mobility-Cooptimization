package Utilities;



public class Vehicle {
    
	public String id;
	public double x;
    public double y;
    public int ConnectedAP;
    public int disToAp;
    
	public Vehicle(String vId, double x2, double y2, int [] ap) {
		// TODO Auto-generated constructor stub
		id = vId;
		x = x2;
		y = y2;
		ConnectedAP = ap[0];
		disToAp = ap[1];
		
		
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public int getConnectedAP() {
		return ConnectedAP;
	}
	public void setConnectedAP(int connectedAP) {
		ConnectedAP = connectedAP;
	}
	
}

