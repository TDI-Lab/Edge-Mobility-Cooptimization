package fog.entities;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;


import org.fog.localization.*;

public class MobileDevice {

	protected String name;
	protected int myId;
	protected ArrayList<String[]> path;
	protected Coordinate coord;
	private int direction; // NONE, NORTH, SOUTH, ...
	private int speed; // in m/s
	protected int startTravelTime;
	protected int travelTimeId;
	
	protected Coordinate futureCoord;// = new Coordinate();//myiFogSim
	private FogDevice sourceServerCloudlet;
	private FogDevice destinationServerCloudlet;
	private FogDevice vmLocalServerCloudlet;
	private ApDevice sourceAp;
	private ApDevice destinationAp;
	
	private boolean status;
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MobileDevice other = (MobileDevice) obj;
		if (this.getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!this.getName().equals(other.getName()))
			return false;
		return true;
	}

	
	public MobileDevice(String name, int coordX, int coordY, int id, int dir, int sp) {

		this.coord = new Coordinate();
		this.setCoord(coordX, coordY);
		this.setMyId(id);
		
		setDirection(dir);
		setSpeed(sp);
		
		setPath(new ArrayList<String[]>());
		setStatus(true);

		this.futureCoord = new Coordinate();
		setFutureCoord(-1, -1);
		
		setSourceServerCloudlet(null);
		setDestinationServerCloudlet(null);
		setVmLocalServerCloudlet(null);
		setSourceAp(null);
		setDestinationAp(null);

	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public ArrayList<String[]> getPath() {
		return path;
	}

	public void setPath(ArrayList<String[]> path) {
		this.path = path;
	}
	
	public int getMyId() {
		return myId;
	}

	public void setMyId(int myId) {
		this.myId = myId;
	}
	public Coordinate getCoord() {
		return coord;
	}

	public void setCoord(int coordX, int coordY) {
		this.coord.setCoordX(coordX);
		this.coord.setCoordY(coordY);
	}

	public int getStartTravelTime() {
		return startTravelTime;
	}

	public void setStartTravelTime(int startTravelTime) {
		this.startTravelTime = startTravelTime;
	}

	public int getTravelTimeId() {
		return travelTimeId;
	}

	public void setTravelTimeId(int travelTimeId) {
		this.travelTimeId = travelTimeId;
	}

	@Override
	public String toString() {
		return this.getName() + "[coordX=" + this.getCoord().getCoordX() + ", coordY="
			+ this.getCoord().getCoordY() + ", direction=" + direction + ", speed=" + speed
			+ ", sourceCloudletServer=" + sourceServerCloudlet + ", destinationCloudletServer="
			+ destinationServerCloudlet + ", sourceAp=" + sourceAp + ", destinationAp="
			+ destinationAp + "]";
	}

	
	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public Coordinate getFutureCoord() {
		return futureCoord;
	}

	public void setFutureCoord(int coordX, int coordY) {
		this.futureCoord.setCoordX(coordX);
		this.futureCoord.setCoordY(coordY);
	}

	public FogDevice getSourceServerCloudlet() {
		return sourceServerCloudlet;
	}

	public void setSourceServerCloudlet(FogDevice sourceServerCloudlet) {
		this.sourceServerCloudlet = sourceServerCloudlet;
	}

	public FogDevice getDestinationServerCloudlet() {
		return destinationServerCloudlet;
	}

	public void setDestinationServerCloudlet(FogDevice destinationServerCloudlet) {
		this.destinationServerCloudlet = destinationServerCloudlet;
	}

	public ApDevice getSourceAp() {
		return sourceAp;
	}

	public void setSourceAp(ApDevice sourceAp) {
		this.sourceAp = sourceAp;
	}

	public ApDevice getDestinationAp() {
		return destinationAp;
	}

	public void setDestinationAp(ApDevice destinationAp) {
		this.destinationAp = destinationAp;
	}

	public FogDevice getVmLocalServerCloudlet() {
		return vmLocalServerCloudlet;
	}

	public void setVmLocalServerCloudlet(FogDevice vmLocalServerCloudlet) {
		this.vmLocalServerCloudlet = vmLocalServerCloudlet;
	}

	public boolean isStatus() {
		return status;
	}

	public void setStatus(boolean status) {
		this.status = status;
	}

	
	public void setNextServerClouletId(int i) {
	}

	public int getNextServerClouletId() {
		return 1;
	}
}