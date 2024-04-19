package fog.entities;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


import org.fog.localization.Coordinate;
import org.fog.vmmobile.constants.Policies;

public class FogDevice {
	
	protected ArrayList<String[]> path;
	protected int myId;
	private String name;
	protected double uplinkBandwidth;
	protected double downlinkBandwidth;
	protected double uplinkLatency;

	protected double energyCost;
	protected double procCost;
	protected double storCost;
	protected double ramCost;
	protected double cOCost;
	protected double depCost;
	
	
	public double calCost(){
		
		return 0;
	}
	public double getEnergyCost() {
		return energyCost;
	}

	public void setEnergyCost(double energyCost) {
		this.energyCost = energyCost;
	}

	public double getProcCost() {
		return procCost;
	}

	public void setProcCost(double procCost) {
		this.procCost = procCost;
	}

	public double getStorCost() {
		return storCost;
	}

	public void setStorCost(double storCost) {
		this.storCost = storCost;
	}

	public double getRamCost() {
		return ramCost;
	}

	public void setRamCost(double ramCost) {
		this.ramCost = ramCost;
	}

	public double getcOCost() {
		return cOCost;
	}

	public void setcOCost(double cOCost) {
		this.cOCost = cOCost;
	}

	public double getDepCost() {
		return depCost;
	}

	public void setDepCost(double depCost) {
		this.depCost = depCost;
	}

	private int level;
	private Host ho;
	protected double ratePerMips;

	protected double totalCost;

	protected Coordinate coord;
	protected Set<ApDevice> apDevices;
	protected Set<MobileDevice> smartThings;

	protected Set<FogDevice> serverCloudlets;


	protected Set<MobileDevice> smartThingsWithVm;
	
	public Set<MobileDevice> getSmartThingsWithVm() {
		return smartThingsWithVm;
	}

	public void setSmartThingsWithVm(MobileDevice st, int action) {// myiFogSim
		if (action == Policies.ADD) {
			this.smartThingsWithVm.add(st);
		}
		else {
			this.smartThingsWithVm.remove(st);
		}
	}


	public Host getHost() {
		return ho;
	}
	
	public FogDevice(String name, Host host,
			long uplinkBandwidth, long downlinkBandwidth, double uplinkLatency, double ratePerMips
			, int coordX, int coordY, int id) {

			
			this.coord = new Coordinate();
			this.setCoord(coordX, coordY);
			this.setMyId(id);
			this.ho = host;
			
			smartThings = new HashSet<>();
			apDevices = new HashSet<>();
			

			setUplinkBandwidth(uplinkBandwidth);
			setDownlinkBandwidth(downlinkBandwidth);
			setUplinkLatency(uplinkLatency);
			setRatePerMips(ratePerMips);

			
		}


	public String getName() {
				return name;
	}
	

	public int getMyId() {
		return myId;
	}

	public void setMyId(int myId) {
		this.myId = myId;
	}

	public Set<MobileDevice> getSmartThings() {
		return smartThings;
	}

	public void setSmartThings(MobileDevice st, int action) {
		if (action == Policies.ADD) {
			this.smartThings.add(st);
		}
		else {
			this.smartThings.remove(st);
		}
	}

	public Set<ApDevice> getApDevices() {
		return apDevices;
	}

	public void setApDevices(ApDevice ap, int action) {
		if (action == Policies.ADD) {
			this.apDevices.add(ap);
		}
		else {
			this.apDevices.remove(ap);
		}
	}

	public Coordinate getCoord() {
		return coord;
	}

	public void setCoord(int coordX, int coordY) {
		this.coord.setCoordX(coordX);
		this.coord.setCoordY(coordY);
	}

	/**
	 * @return Mbps
	 */
	public double getUplinkBandwidth() {
		return uplinkBandwidth;
	}

	public void setUplinkBandwidth(double uplinkBandwidth) {
		this.uplinkBandwidth = uplinkBandwidth;
	}

	/**
	 * @return second
	 */
	public double getUplinkLatency() {
		return uplinkLatency;
	}

	public void setUplinkLatency(double uplinkLatency) {
		this.uplinkLatency = uplinkLatency;
	}

	
	public ArrayList<String[]> getPath() {
		return path;
	}

	public void setPath(ArrayList<String[]> path) {
		this.path = path;
	}

	
	/**
	 * @return Mbps
	 */
	public double getDownlinkBandwidth() {
		return downlinkBandwidth;
	}

	public void setDownlinkBandwidth(double downlinkBandwidth) {
		this.downlinkBandwidth = downlinkBandwidth;
	}

	public double getEnergyConsumption() {
		return energyCost;
	}

	public void setEnergyConsumption(double energyConsumption) {
		this.energyCost = energyConsumption;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public double getRatePerMips() {
		return ratePerMips;
	}

	public void setRatePerMips(double ratePerMips) {
		this.ratePerMips = ratePerMips;
	}

	
	public Set<FogDevice> getServerCloudlets() {
		return serverCloudlets;
	}

	public void setServerCloudlets(FogDevice sc, int action) {// myiFogSim
		if (action == Policies.ADD) {
			this.serverCloudlets.add(sc);
		}
		else {
			this.serverCloudlets.remove(sc);
		}
	}

	
}
