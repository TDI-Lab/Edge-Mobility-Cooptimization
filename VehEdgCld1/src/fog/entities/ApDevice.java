package fog.entities;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fog.localization.*;
import org.fog.vmmobile.constants.Policies;

import EdgeEPOS.Setting.Constants;

public class ApDevice {

	private FogDevice serverCloudlet;
	private int maxSmartThing;
	
	protected int myId;
	private String name;
	protected double uplinkBandwidth;
	protected double downlinkBandwidth;
	protected double uplinkLatency;
	protected Coordinate coord;
	protected double energyConsumption;
	private int level;

	protected Set<MobileDevice> smartThings;

	
	
	public ApDevice(String name, int coordX, int coordY,
			int id, double downLink, double energyCons,
			int maxst, double upLinkBand, double upLinkLat) {
			
			this.name = name;
			this.myId = id;
			
			this.coord = new Coordinate();
			this.setCoord(coordX, coordY);
			
			smartThings = new HashSet<>();
			setServerCloudlet(null);
			
			setDownlinkBandwidth(downLink);
			setEnergyConsumption(energyCons);
			setLevel(2);// 0 - Cloud, 1 - ServerCloudlet, 2 - AccessPoint, 3 - SmartThing
			setMaxSmartThing(maxst);
			
			setUplinkBandwidth(upLinkBand);
			setUplinkLatency(upLinkLat);

		}
	
	/**
	 * @return Mbps
	 */
	public double getUplinkBandwidth() {
		return uplinkBandwidth;
	}

	/**
	 * @param uplinkBandwidth
	 */
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

	
	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	/**
	 * @return nj per bit
	 */
	public double getEnergyConsumption() {
		return energyConsumption;
	}

	public void setEnergyConsumption(double energyConsumption) {
		this.energyConsumption = energyConsumption;
	}
	/**
	 * @return Mbps
	 */
	public double getDownlinkBandwidth() {
		return downlinkBandwidth;
	}

	/**
	 * @param downlinkBandwidth
	 */
	public void setDownlinkBandwidth(double downlinkBandwidth) {
		this.downlinkBandwidth = downlinkBandwidth;
	}
	
	public int getMyId() {
		return myId;
	}

	public String getName() {
			return name;
	}

	
	public void desconnectApSmartThing(MobileDevice st) {
		setSmartThings(st, Policies.REMOVE);
		st.setSourceAp(null);
		setUplinkLatency(getUplinkLatency() - 0.002);

	}
	public void setSmartThings(MobileDevice st, int action) {
		if (action == Policies.ADD) {
			this.smartThings.add(st);
		}
		else {
			this.smartThings.remove(st);
		}
	}

	/**
	 * @param apDevices
	 * @param st
	 * @param delay
	 * @return
	 * connects a smart thing to an access point based on their distance
	 * adds links between them with the delay calculated using the distance * Constants.AirVelFact
	 */
	public static boolean connectApSmartThing(List<ApDevice> apDevices, MobileDevice st, double delay) {
		int index = Distances.theClosestAp(apDevices, st);
		//System.out.println("ap index: "+index+" st "+st.getId());
		if (index >= 0) {
			// it checks the accessPoint limit
			if (apDevices.get(index).getMaxSmartThing() > apDevices.get(index).getSmartThings().size()) {
				st.setSourceAp(apDevices.get(index));
				apDevices.get(index).setSmartThings(st, Policies.ADD);
				delay = Distances.checkDistance(st.getCoord(), apDevices.get(index).getCoord()) * Constants.AirVelFact;//second???
				apDevices.get(index).setUplinkLatency(apDevices.get(index).getUplinkLatency() + delay);
				return true;
			}
			else {// Ap is full
				System.out.println("ap full st id: "+st.getMyId());
				return false;
			}
		}
		else {
			// The next Ap is far way
			System.out.println("The next Ap is far way"+st.getMyId());
			return false;
		}

	}


	public Set<MobileDevice> getSmartThings() {
		return smartThings;
	}

	
	public Coordinate getCoord() {
		return coord;
	}

	public void setCoord(int coordX, int coordY) {
		this.coord.setCoordX(coordX);
		this.coord.setCoordY(coordY);
	}
	

	public FogDevice getServerCloudlet() {
		return serverCloudlet;
	}

	public void setServerCloudlet(FogDevice serverCloudlet) {
		this.serverCloudlet = serverCloudlet;
	}

	public int getMaxSmartThing() {
		return maxSmartThing;
	}

	public void setMaxSmartThing(int maxSmartThing) {
		this.maxSmartThing = maxSmartThing;
	}


}
