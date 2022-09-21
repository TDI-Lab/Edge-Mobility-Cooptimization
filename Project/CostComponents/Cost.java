package EdgeEPOS.CostComponents;

import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.splitmap.AbstractIterableGetMapDecorator;
import org.apache.commons.math3.analysis.function.Constant;
import org.cloudbus.cloudsim.NetworkTopology;
//import org.cloudbus.cloudsim.core.CloudSim;
import org.fog.entities.ApDevice;
import org.fog.entities.FogDevice;
import org.fog.entities.MobileDevice;
import org.fog.localization.Coordinate;
import org.fog.localization.Distances;

import EdgeEPOS.PlacementMethods.Plan;
import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.MobileARservice;



/**
 *
 * @author rooyesh
 *
 * This class contains all the functions and parameters that are related to the
 * cost
 */
public class Cost {

	public Plan agentPlan;
	private FogDevice fogServer;
	public List <MobileARservice> serviceList;
	public List<FogDevice> serverCloudlets;
	public List<ApDevice> apDevices;
	
	//public Set<MobileDevice> connectedVehicules;
	private MobileDevice[] connectedVehicleArray;
	int [][] onThePathAccessPoints;
	private int periodDu;//tau??
	private int periodNum;
	private Violation v;
	private ProcessingCost pc;
	private ServiceDelay sd;
    private static int samplingInterval = (int) (Constants.TAU / (Constants.SAMPLENUMBERS+1)); //tau = 60 sec --> samplingInterval = 15sec
    
    
    /**
     * Constructor of the cost class. The input parameters are number of cloud
     * servers, fog nodes, and services. This constructor will initializes the
     * unit cost parameters.
     * @param costComProcDep 
     * @param sD 
     *
     * @param NUM_CLOUD_SERVERS
     * @param NUM_FOG_NODES
     */
    public Cost(int period, FogDevice fogserver, List<ApDevice> apdevices, List<FogDevice> servercloudlets, List<MobileARservice> servicelist, Set<MobileDevice> connectedVehicules, ProcessingCost costComProcDep, ServiceDelay sD) {
    	
    	connectedVehicleArray = connectedVehicules.toArray(new MobileDevice[connectedVehicules.size()]);
  		pc = costComProcDep;  
  		sd = sD;
    	//this.connectedVehicules = set;
    	 // this.periodDu = periodDu;
    	  this.periodNum = period;
	      this.fogServer = fogserver;
	  	  this.serverCloudlets = servercloudlets;
	  	  this.serviceList = servicelist;
	  	  this.apDevices = apdevices;
        
       }

    
    public double calcLocalCost(Plan p) {
    	 double dlCost = deadlineViolationCost(p);
    	 double procAndCommCost = pc.procCommMonetaryCost(p, onThePathAccessPoints, samplingInterval);
    	 double energyCost = energy(p);
    	 System.out.println("cost estimation....dl "+dlCost + " pro "+procAndCommCost);
    	 return dlCost + procAndCommCost + energyCost;
    }
  
    public double energy(Plan p) {
    	
    	if (hostIndex < Constants.numFogNodes)
        	utilPlan[hostIndex+Constants.numCloudServers+Constants.numFogNodes] = 
        		Constants.a * (Math.pow(wlPlan[hostIndex] , Constants.p_j))
        		+ Constants.b * (wlPlan[hostIndex])
        		+ Constants.c;
        else //??num of machines
        	utilPlan[hostIndex+Constants.numCloudServers+Constants.numFogNodes] = 
    		Constants.A * (Math.pow(wlPlan[hostIndex] , Constants.p_k))
    		+ Constants.B;
    		
    }
  
//transmission delay vs. propagation delay:
//Transmission delay is the time needed to put the entire packet on the link and is dependent on the length of 
//the packet, while the propagation delay is needed time for one bit to reach to the other end of the link.
  /**compute deadline-violation cost
 * @param p
 * @return
 */
	private double deadlineViolationCost(Plan p) {
	  double violCost=1;
	  
	  List <Double[]> propagDelays = new ArrayList<Double[]>();
	  List <Double[]> transRats = new ArrayList<Double[]>();//bandwidth
	  List <Double[]> waitingTimes = new ArrayList<Double[]>();// queuing delay + processing delay
	  
	  expectedLinkDelayAndTRRate(p, propagDelays, transRats);
	  waitingTime(p, propagDelays, transRats, waitingTimes);
	  
	  v = new Violation();
	  violCost = v.costViol(waitingTimes, serviceList);
	  
	return violCost;
}


	  /** finds different edge nodes connecting source vehicle to its candidate host
	   * then calculates the average of propagation delay for the connecting links between them 
	 * @param p plan
	 * @param avgPropagDelay average propagation delay for the link (j,j') or (j,k)
	 */
	public void expectedLinkDelayAndTRRate(Plan p, List <Double[]> PropagDelays, List <Double[]> TransRates) {
 	   	
		ApDevice ap;
 	    int i, j;
	    int hostIndex, srcIndex, currAPIndex;
	    int newAPIndex = -1;
 	    onThePathAccessPoints = new int [serviceList.size()][Constants.SAMPLENUMBERS+2];
	    
 	    for (i = 0 ; i<serviceList.size() ; i++) {
 	    	
 	    	j = 0;
 	    	Double[] propagDelayPerService = new Double[Constants.SAMPLENUMBERS+2];
 	    	Double[] transRatePerService = new Double[Constants.SAMPLENUMBERS+2];
 	 	    List<Coordinate> sampleCoordinates = new ArrayList <Coordinate> ();
   			
 	 	    hostIndex = p.y[i];//host node
   			srcIndex = serviceList.get(i).getSourceId();//vehicle srcIndex = st.getMyId() in mobilitydataset: 100.srcIndex
   			MobileDevice sourceThing = getVehicleById(srcIndex, connectedVehicleArray);
   			currAPIndex = sourceThing.getSourceAp().getMyId();
   			
   			System.out.println(" i "+i+" host "+hostIndex+" srcIndex "+srcIndex+" currAP "+sourceThing.getSourceAp().getMyId());
   			
   			CoordinateAuxiliary cA = new CoordinateAuxiliary(periodNum, samplingInterval, sourceThing);
   		    cA.getSampleCoords(sampleCoordinates);
   			
   			while (j < sampleCoordinates.size()){
				
				double dis = Distances.checkDistance(sampleCoordinates.get(j), apDevices.get(currAPIndex).getCoord());
				System.out.println(" j "+j+" x " +sampleCoordinates.get(j).getCoordX()+" y "+sampleCoordinates.get(j).getCoordY()+" dis "+dis);
				
				if (Distances.checkDistance(sampleCoordinates.get(j), apDevices.get(currAPIndex).getCoord()) >= Constants.MAX_DISTANCE_TO_HANDOFF) 	
					newAPIndex = Distances.theClosestAp(apDevices, sampleCoordinates.get(j));
					
				if(newAPIndex != -1) 
					 currAPIndex = newAPIndex;
						
							onThePathAccessPoints[i][j] =  currAPIndex;
							ap = apDevices.get(currAPIndex);
						
							//connection delay for (I,j)+(j,j') or (I,j)+(j,k) + delay of Access points:
							propagDelayPerService[j] = NetworkTopology.getDelay(ap.getServerCloudlet().getId(), ap.getId())+
									NetworkTopology.getDelay(ap.getServerCloudlet().getId(), serverCloudlets.get(hostIndex).getId())+
									NetworkTopology.getDelay(apDevices.get(hostIndex).getId(), serverCloudlets.get(hostIndex).getId());
							
							//System.out.println(" propagdelay: i "+i+" j "+j+" host "+hostIndex+" apindex " +currAPIndex);
							
							  System.out.println(" propagdelay: i "+i+" j "+j+" host "
							  +hostIndex+" apindex " +currAPIndex+" "+
							  NetworkTopology.getDelay(ap.getServerCloudlet().getId(), ap.getId())+" "+
							  NetworkTopology.getDelay(ap.getServerCloudlet().getId(),
							  serverCloudlets.get(hostIndex).getId())+" "+//between cloudlets
							  NetworkTopology.getDelay(apDevices.get(hostIndex).getId(),
							  serverCloudlets.get(hostIndex).getId()));
							 
							if (ap.getDownlinkBandwidth() == 0)
								System.out.println("bw: 0 error");
							transRatePerService[j] = ap.getDownlinkBandwidth();
							/*
							 * System.out.println("bw: "+apIndex+" "+hostIndex+" "+ap.getDownlinkBandwidth()
							 * +" "+NetworkTopology.getBW(ap.getServerCloudlet().getId(), ap.getId())+" "+
							 * NetworkTopology.getBW(ap.getServerCloudlet().getId(),
							 * serverCloudlets.get(hostIndex).getId())+" "+
							 * NetworkTopology.getBW(apDevices.get(hostIndex).getId(),
							 * serverCloudlets.get(hostIndex).getId()));
							 */
						
					
				j++; 
				
		    }
 			PropagDelays.add(propagDelayPerService);
 			TransRates.add(transRatePerService);
 			
 	   }
			
  	}
	
	 /**compute processing time plus waiting time for each node regarding to the queuing theory
	 * @param p
	 * @param propagDelay
	 * @param waitingTime
	 */
	private void waitingTime (Plan p, List <Double[]> propagDelay, List <Double[]> transRate, List <Double[]> waitingTime) {
			sd.initialize(p);
		  for (int i = 0; i<serviceList.size(); i++) {
			   //System.out.println("service "+i);
	 		   waitingTime.add(sd.calcServiceDelay(i, p.y[i], transRate.get(i), propagDelay.get(i)));
	 		
		  }
	 	    	
 	}
 	     
  	private MobileDevice getVehicleById(int srcId, MobileDevice[] vehicles) {
		int i = 0 ;
  		while (i < vehicles.length) {
  			if (vehicles[i].getMyId() == srcId){
  				//System.out.println("vehicle index: " + i);
  				return vehicles[i];
  			}
  			i++;
  		}
  
  		/*MobileDevice md = new MobileDevice();
  		Iterator<MobileDevice> iter = connectedVehicules.iterator();
  	    
  		int i = 0;
  		while (iter.hasNext()) {
  			if (iter.next().getId() == srcId){//search for the source of service
  		    
 				return k;
  			}
  		}
		*/
  		return null;
	}


	private ApDevice getAccessPointById(int apId) {
		// TODO Auto-generated method stub
		
		for (ApDevice ap : apDevices) {
			if (ap.getMyId() == apId)
				return ap;
 		}
		System.out.println("null0");
		
		return null;
	}

	private int getServerCloudletIndexById(int scId) {
		// TODO Auto-generated method stub
		for (int fd = 0; fd<serverCloudlets.size(); fd++) {
			if (serverCloudlets.get(fd).getMyId() == scId) {
				System.out.println("apidIndex "+fd);
				return fd;
			}
 		}
		System.out.println("null1");
		
		return -1;
	}
 }
