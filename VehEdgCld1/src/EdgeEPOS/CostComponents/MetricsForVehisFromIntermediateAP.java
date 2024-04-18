package EdgeEPOS.CostComponents;

import java.util.ArrayList;
import java.util.List;

import fog.entities.ApDevice;
import fog.entities.FogDevice;

import EdgeEPOS.Setting.Constants;

public class MetricsForVehisFromIntermediateAP {
		
		int vehicle;
		int curTS;
		
		public List<Short> ConnectedAPs;
		
	    /**
	     * second
	     */
		public List<Double> ConnectionTime;
	    
	    /**
	     * second - propagation delay (only between ap and host)
	     */
		public List<Double> propagTime; 
	    
	    /**
	     * Mbps - transmission rate
	     */
		public List<Double> transRatePerAP;
	    
	    /**
	     * second - waiting time including processing and transmission time
	     */
		public List<Double> waitingTimePerAp;
	    
	    /**
	     * violation percentage per service per access point
	     */
		public List<Double> violPercPerAp;
		
	    /**
	     * deployment delay of a container for the first time on a node
	     */
	    public double deployTime = 0;
		
		
		public MetricsForVehisFromIntermediateAP(int vehicle, int time, short[][] veh2ap) {

			this.vehicle = vehicle;
			this.curTS = time;
			ConnectedAPs = new ArrayList <Short> ();
		    /*
		     * connection time of mobile device and access point in second
		     */
			ConnectionTime = new ArrayList<Double>();
		    /*
		     * propagation delay in second from iot node to the host
		     */
		    propagTime = new ArrayList<Double>(); 
		    
		    /*
		     * downlink transmission bandwidth for every node
		     */
		    transRatePerAP= new ArrayList<Double>();//extra??
		    /*
		     * execution time in second including propagation, queueing, and processing
		     */
		    waitingTimePerAp = new ArrayList<Double>();
		    violPercPerAp = new ArrayList<Double>();
		    //System.out.println(" transRatePerAP: " +transRatePerAP.size()+" propagTime "+propagTime.size());
			
			findAP(veh2ap);
		}
	    
	    
	    /**
	     * extracts the connecting access points for one vehicle during curTS + Constants.TAU
	     * @param veh2ap 
	     */
	    private void findAP(short[][] veh2ap) {
			// TODO Auto-generated method stub
			 //int curTS = periodNum*Constants.TAU;
			 short nextAP;
			 short firstAP;
			 double connectedTimeInterval;
			 double lastTS = curTS + Constants.TAU;
			 boolean flag = false;
			 double startTime = curTS;
			 firstAP = veh2ap[vehicle][curTS];
			 
			 
			 for(int ts = curTS+1; ts<lastTS; ts++) {
				nextAP = veh2ap[vehicle][ts];
				flag = false;
					if (nextAP != firstAP) {
						connectedTimeInterval = ts - startTime;
						ConnectedAPs.add(firstAP);
						ConnectionTime.add(connectedTimeInterval);
						flag = true;
						if (nextAP == -1)
							break;
						firstAP = nextAP;
						startTime = ts;
						
					}
			}
			 if ((flag == false)&&(firstAP != -1)) {
				 ConnectedAPs.add(firstAP);
				 ConnectionTime.add(lastTS - startTime);
				}
	     
	   
}
	 /**
	 * calculates the propagation delay in second (between ap and host) and transmission rate for each service running on its candid host regarding different aps on the route
	 * @param apDevices
	 * @param serverCloudlets
	 * @param host
	 */
	public void PropagDelayTransRate(int host){	  
		 int j = 0;
		 Short APIndex;
		 double prop_delay = 0;
		 double trans_rate = 0;
			
		 while (j < ConnectedAPs.size()){
				
				APIndex = ConnectedAPs.get(j);
			
				
				/**
		         * in second
		         * Measures the delay between vehicles and their service host: connection delay for (I,j)+(j,j') or (I,j)+(j,c) + delay of Access points and servers
		         */
				prop_delay = (Constants.dIF[APIndex] + Constants.dAF[APIndex] + Constants.dAF[host])/Constants.microToSec + Constants.dFFC[APIndex][host] ;
				//if (host ==113)
					//System.out.println("del: "+prop_delay);
				propagTime.add(j, prop_delay);//might not need to the last one
				
				trans_rate = Constants.rIFC_Down[APIndex];//we consider the min transmission rate, which belongs to the aps, on the path from host to end device
				transRatePerAP.add(j, trans_rate);
					
			j++; 
			
		 }
	
	}
	
	/**
	 * @return second
	 */
	public double getDeployTime() {
		return deployTime;
	}


	public void setDeployTime(double deployTime) {
		this.deployTime = deployTime;
	}

	
	 
}
