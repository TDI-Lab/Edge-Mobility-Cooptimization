package EdgeEPOS.Setting;

import java.util.Random;

/**
 * @author rooyesh
 *
 */
public class MobileARservice {//implements Comparable {
	 
	/**
	 * vehicleId which is equal to the index in whole vehicles/services
	 */
	private int serviceIndex;//appId; real vehicle and service id in 
	 
    private String appName;
     
     /**
     * (50,200) mi per request
     */
    private double lp_i; 	
     /**
     * U(2, 400)MB 	-->MegaByte
     */
    private int lm_i;		
     /**
     * U(50,500)MB - size vm 	-->MegaByte
     */
    private long ls_i;			
     /**
     * req per second
     */
    private double z_i;		
     /**
     * U(10,26)KB 	--> bit
     */
    private int rq_i;			
	 /**
	 * U(10,20)B 	--> bit
	 */
	private int rp_i;			
	 
	 /**
	 * Megabits/s
	 */
	private long sBandwidth;	 
	
	 //private double sWaitTime;
	 
	private double sDeadline;//milisecond
	 /**
	 * cloudlet edge index
	 */
	private int edgeId = -1;
	 /**
	 * cloudlet host id
	 */
	private int hostId;
	 /**
	 * smart thing index
	 */
	private int sourceId;
	
	private double QoS;
	
	private double penalty;
	 
	private boolean valid = false;//for check the presence of the vehicle in the area and mig cost; if previous time interval the vehicle was in the area and its service has been schedules
	
	
	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}


	public MobileARservice(int id, String name, double RequestPerSec, double l_P, int l_M, int l_S, int l_rq, int l_rp, double th) {
		this.serviceIndex = id;
		this.appName = name;
		this.z_i = RequestPerSec;
		setValuesforARServices(l_P, l_M, l_S, l_rq, l_rp, th);
		setValid(false);
		
	}
	
	private void setValuesforARServices(double l_P, int l_M, int l_S, int l_rq, int l_rp, double th) {
		
		lp_i = l_P;//processing power in mi per sec
		lm_i = l_M;//memory in MegaByte
		ls_i = l_S;//size in MegaByte
		rq_i = l_rq;//request length in bit
		rp_i = l_rp;//response length in bit
		setEdgeId(-1);//initial host
		setSourceId(-1);//source vehicle
		setBandwidth();//MByte per second
		sDeadline = th;
		
	}

	
	public int getSourceId() {
		return sourceId;//st.getMyId
	}

	//if its source node is connected to an access point this set the id of the source node for this service
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public int getServicId() {
		return serviceIndex;
	}

	public void setServicId(int srvId) {
		this.serviceIndex = srvId;
	}
	/**
	 * @return mips
	 */
	public double getCpuDemand1() {
		return lp_i*z_i;
	}
	
	/**
	 * @return mi per request
	 */
	public double getCpuDemand() {
		return lp_i;
	}

	/**
	 * @return req per second
	 */
	public double getRequestPerSec() {
			return z_i;
	}

	/**
	 * @return MegaByte
	 */
	public int getMemDemand() {
		return lm_i;
	}


	/**
	 * @return MegaByte
	 */
	public long getStorageDemand() {
		return ls_i;
	}

	/**
	 * @return second
	 */
	public double getDeadline() {
		return sDeadline/1000;
	}


	/**
	 * @return Mbytes/sec
	 */
	public long getBandwidth() {
		return sBandwidth;
	}

	
	public void setBandwidth() {//bits/s ----> Mbytes/sec:
							
		sBandwidth = (long) (z_i * getRequestSize() * 0.125 * 0.000001);
	}
	
	/**
	 * @return bit
	 */
	public int getRequestSize() {
		return rq_i;
	}
	
	public int getEdgeId() {
		return edgeId;
	}

	public void setEdgeId(int edgeId) {
		this.edgeId = edgeId;
	}

	public int getHostId() {
		return hostId;
	}

	public void setHostId(int hostId) {
		this.hostId = hostId;
	}
	
	/**
	 * @return bit
	 */
	public int getResponseSize() {
		return rp_i;
	}

	//not used now
	//waiting time to be scheduled
	public int getsWaitTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	

	
	
}
