package EdgeEPOS.Setting;

import java.util.Random;

/**
 * @author rooyesh
 *
 */
public class MobileARservice {//implements Comparable {
	 private int serviceIndex;//appId;
	 
     private String appName;
     private double lp_i; //(50,200) mi per request
     private int lm_i;//U(2, 400)MB 	-->bits
     private long ls_i; //U(50,500)MB - size vm 	-->bits
     private double z_i;//req per second
     private int rq_i;//U(10,26)KB 	--> bit
	 private int rp_i;//U(10,20)B 	--> bit
	 
	 private long sBandwidth;//bw = 1000; bw-Megabits/s. 
	
	 //private double sWaitTime;
	 
	 private int sDeadline = 10;//milisecond
	 private int edgeId;//cloudlet index
	 private int hostId;//cloudlet id
	 private int sourceId;//smart thing index
	
	 private double QoS;
	 private double penalty;
	 
	//6 sec
	public MobileARservice(int id, String name, double RequestPerSec) {
		this.serviceIndex = id;
		this.appName = name;
		this.z_i = RequestPerSec;
		setValuesforARServices();
		
		
	}
	
	private void setValuesforARServices() {
		//cpudemand= MiPerSec*z_i;
		
		lp_i = Constants.L_P[serviceIndex];//processing power in mi per sec
		lm_i = Constants.L_M[serviceIndex];//memory in Megabytes
		ls_i = Constants.L_S[serviceIndex];//size in Megabytes
		rq_i = Constants.l_rq[serviceIndex];//request length 
		rp_i = Constants.l_rp[serviceIndex];//response length 
		setEdgeId(-1);//initial host
		setSourceId(-1);//source vehicle
		setBandwidth();//
		setSLA(serviceIndex);
		
		
	}
	
	private void PrintProfile() {
		// TODO Auto-generated method stub
		
		
	}

	private void setSLA(int serviceIndex2) {
		setQoS(serviceIndex2);
		setPenalty(serviceIndex2);//?????
		
	}
	public double getQoS() {
		return QoS;
	}

	public void setQoS(int qoSIndex) {
		QoS = Constants.q[serviceIndex];;
	}

	public double getPenalty() {
		return penalty;
	}

	public void setPenalty(int penaltyIndex) {
		this.penalty = Constants.penalty[serviceIndex];;
	}


	public int getSourceId() {
		return sourceId;//st.getMyId
	}

	//if its source node is connected to an access point this set the id of the source node for this service
	public void setSourceId(int sourceId) {
		this.sourceId = sourceId;
	}

	public int getAppId() {
		return serviceIndex;
	}

	public void setAppId(int appId) {
		this.serviceIndex = appId;
	}

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

	public int getMemDemand() {
		return lm_i;
	}


	public long getStorageDemand() {
		return ls_i;
	}

	public int getsDeadline() {
		return sDeadline;
	}


	public long getBandwidth() {
		return sBandwidth;
	}

	public void setBandwidth() {//KiloByts/s ----> must convert to Mbytes/sec ???
		sBandwidth = (long) (z_i * getRequestSize() * 0.000125);
	}

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
	
	public int getResponseSize() {
		return rp_i;
	}

	//not used now
	//waiting time to be scheduled
	public int getsWaitTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 @Override
	 public int compareTo(MobileARservice comparestu) {
		 	int compareage=((MobileARservice)comparestu).getsDeadline();
	        // For Ascending order
	        return this.sDeadline-compareage;

	        // For Descending order do like this 
	        //return compareage-this.studentage;
	    }
		
	*/

	
	
}
