package EdgeEPOS.PlacementMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;

import fog.entities.*;
import org.fog.localization.Distances;

import EdgeEPOS.CostComponents.Cost;
import EdgeEPOS.CostComponents.EnergyCost;
import EdgeEPOS.CostComponents.MetricsForVehisFromIntermediateAP;
import EdgeEPOS.CostComponents.ComProcessDepStorCost;
import EdgeEPOS.CostComponents.ComboCosts;
import EdgeEPOS.CostComponents.ServiceDelay;
import EdgeEPOS.CostComponents.LocalCostUnits;
import EdgeEPOS.CostComponents.Violation;
import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.MobileARservice;
import EdgeEPOS.Setting.Workload;
import EdgeEPOS.Utility.RandomGenerator;
import EdgeEPOS.Utility.Utility;


/**
 * @author rooyesh
 * This class contains all the functions and parameters that are related to the agents
 */
public class Agent {
	
	private int agIndex;
	private int serviceSize;
	private FogDevice fogServer;
	private int currentTime;
	private List<FogDevice> serverCloudlets;
	private List<ApDevice> apdevices;
	private Set<MobileDevice> connectedVehicles;//st.setVmMobileDevice(vmSmartThingTest);
	//connectedVehicles = fogServer.getSmartThings();// equals to serviceList
	public List <MobileARservice> serviceList;//agent's service list
	
	Cost lcost;
	ComProcessDepStorCost costOfComProcDep;
	ServiceDelay sD;
	EnergyCost eC;
	public ArrayList <MetricsForVehisFromIntermediateAP> vehicleConnectedAPsPerRunAgent;
	
    public Plan [] Plans;
    public Plan p;
	double[] workload;// = new double[numOfNodes*2];//CPU and energy
    double[] utilization;
	private short[][] veh2AP;
   
    
	/**
	 * @param sc
	 * @param networkCloudlets excessive param
	 * @param apdevices excessive param
	 * @param vehiclesToAP 
	 */
	public Agent(FogDevice sc, List<FogDevice> networkCloudlets, List<ApDevice> apdevices, short[][] vehiclesToAP){
		agIndex = sc.getMyId();
		this.fogServer = sc;
		this.apdevices = apdevices;
		this.serverCloudlets = networkCloudlets;
		this.veh2AP = vehiclesToAP;
		workload = new double[2*(Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS)];//CPU and energy
		utilization = new double[2*(Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS)];//CPU and energy
		initializeWorkload();
 
	}
	
	
	/**
	 * initializes two temporary arrays of workload and utilization with zero
	 */
	private void initializeWorkload() {
		for (int i=0; i<Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS; i++) {
			workload[i] = 0;
			workload[i+Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS] = 0;
			utilization[i] = 0 ;
			utilization[i+Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS] = 0;
			
		}
	}
	
	
    /**
     * @param ServiceList
     * assigns received requests to each fog node to its co-located agent
     */
    public void arrivalServicesToFogNode(List<MobileARservice> ServiceList) {
    	
    	serviceList = ServiceList; 
    	serviceSize = ServiceList.size();
    	//System.out.println("Service size agent "+agIndex+": "+ ServiceList.size());
    	
    }
    
    private void generateCostComponents() {
    	
    	costOfComProcDep = new ComProcessDepStorCost(serviceList, serverCloudlets);
 		sD = new ServiceDelay(serviceList, serverCloudlets);
 		eC = new EnergyCost(serviceList);
        lcost = new Cost (fogServer, apdevices, serverCloudlets, serviceList, costOfComProcDep, sD, eC);
        
	}
	
    /** Performs candidate hosts selection and then generates candidate plans, measures local cost and write the plans to files 
     * @param curr_ts 
     * @param ma2 
     * @param hopLevel
     * @param lambda
     * @param lambda2 
     * @return an array of plans along with its local_cost
     */
    public Plan[] generatePlans(boolean[] deploy_backup_, int curr_ts) {
    	//System.out.println("--------------------------------------");
    	System.out.print(getAgIndex()+"="+serviceList.size()+", ");
 		
    	currentTime = curr_ts;
    	generateCostComponents();
 		
        Plans = new Plan [Constants.EPOS_NUM_PLANS];//array of candidate plans for this agent
         
        /**
         * List of lists of connected aps/propagation-delay/trans-rate/waiting-time/viol-cost... for all vehicles/services of this agent
         */
        vehicleConnectedAPsPerRunAgent = new ArrayList <MetricsForVehisFromIntermediateAP> ();//creates a list of the APs located on the route of service requests (vehicle) received by this agent
 		
    	
    	if (serviceList.size() == 0) {
     		Plans = emptyPlanGeneration(deploy_backup_);
     	}
        else {
        	for (int i = 0; i<serviceList.size(); i++) {//Creates one list of connecting aps for each vehicle/service. Note that connecting aps are fixed 
     			MetricsForVehisFromIntermediateAP iap = new MetricsForVehisFromIntermediateAP(serviceList.get(i).getSourceId(), currentTime, veh2AP);
     			vehicleConnectedAPsPerRunAgent.add(i, iap);
     		}
        
        	for(short planIndex=0 ; planIndex < Constants.EPOS_NUM_PLANS ; planIndex++){
	        		System.out.println("Plan "+planIndex);
	        		//selectedHosts = selectHosts(serviceList.size()); 
	        		Plans[planIndex] = makePlan(planIndex, selectHosts(serviceList.size()), deploy_backup_);
	        }
        }
         
    	 //System.out.println("End of plan generation...........");
    	 Utility.writePlans(Plans, agIndex);
         return Plans;
 	}
 	
	
    /**Baseline plan generation
     * @param deploy_backup_
     * @param curr_ts
     * @return one plan for deploying received requests on itself
     */
    public Plan generateLOPlan(boolean[] deploy_backup_, int curr_ts, int mtype) {
    	//System.out.println("--------------------------------------");
    	System.out.println(getAgIndex()+"="+serviceList.size()+", ");
    	short pin = 0;
 		currentTime = curr_ts;
    	generateCostComponents();
 		
        /**
         * List of lists of connected aps/propagation-delay/trans-rate/waiting-time/viol-cost... for all vehicles/services of this agent
         */
        vehicleConnectedAPsPerRunAgent = new ArrayList <MetricsForVehisFromIntermediateAP> ();//creates a list of the APs located on the route of service requests (vehicle) received by this agent
 		
    	
    	if (serviceList.size() == 0) {
    		p = new Plan(agIndex, pin, 0, deploy_backup_);  
     	}
        else {
        	
        	for (int i = 0; i<serviceList.size(); i++) {//Creates one list of connecting aps for each vehicle/service. Note that connecting aps are fixed 
     			MetricsForVehisFromIntermediateAP iap = new MetricsForVehisFromIntermediateAP(serviceList.get(i).getSourceId(), currentTime, veh2AP);
     			vehicleConnectedAPsPerRunAgent.add(i, iap);
     		}
        
        	
    		int [] selectedHosts = new int[serviceList.size()]; 
        	
    		Arrays.fill(selectedHosts, agIndex);
    	  	
    		p = makePlan(pin, selectedHosts, deploy_backup_);
    	  	p.selected = true;//marks this plan as the selected for the agent 
        	
		}
        
         //System.out.println("End of plan generation...........");
    	 Utility.writePlan(p, agIndex);
         return p;
 	}
 	
    public Plan generateFFPlan(int curr_ts, int mtype, Plan p, boolean[] preRunDeployPlan) {
    	//System.out.println("--------------------------------------");
    	//System.out.println(getAgIndex()+"="+serviceList.size()+", ");
    	this.p = p;
 		currentTime = curr_ts;
    	generateCostComponents();
    	
    	/* from previous run
    	 * for the calculation of local cost of this agent's plan
    	 */
    	boolean[] deploy_local = new boolean [Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
        for (int j = 0; j < Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS; j++) 
    		deploy_local[j] = preRunDeployPlan[j];
		   
        /**????????????????????????????????????
         * List of lists of connected aps/propagation-delay/trans-rate/waiting-time/viol-cost... for all vehicles/services of this agent for total LC of plan
         */
		 vehicleConnectedAPsPerRunAgent = new ArrayList  <MetricsForVehisFromIntermediateAP> ();
		 
        if (serviceList.size() == 0) {
    		//p = new Plan(agIndex, pin, 0, deploy_backup_);  
     	}
        else {
        	
        	  //creates a list of the APs located on the route of service requests (vehicle) received by this agent for later to calculate the global cost
			  for (int i = 0; i<serviceList.size(); i++) {//Creates one list of connecting aps for each vehicle/service for the plan cost calculation. Note that connecting aps are fixed 
				  MetricsForVehisFromIntermediateAP iap = new  MetricsForVehisFromIntermediateAP(serviceList.get(i).getSourceId(), currentTime, veh2AP); 
				  vehicleConnectedAPsPerRunAgent.add(i, iap); 
			  }
			  makePlan(deploy_local);
        	  p.selected = true;//marks this plan as the selected for the agent 
        	}
        	
		 
    	 Utility.writePlan(p, agIndex);//System.out.println("End of plan generation...........");
         return p;
 	}
 	
    /**First Fit placement method
     * @param _backup_deployment
     * @param deployLocal 
     * @param p
     * @return
     */
    public void makePlan(boolean[] deployLocal) {
    	
    	int host;
    	int assToCloud = 0;
    	int j = 0;
    	boolean flag = false;
    	
    	/**
         * List of lists of connected access points+[propagation-delay/trans-rate/waiting-time] for all vehicles/services received by this agent
         */
    	List <MetricsForVehisFromIntermediateAP> vehicleConnectedAPsPerRun = new ArrayList <MetricsForVehisFromIntermediateAP> ();//creates a list of the APs located on the route of service requests (vehicle) received by this agent
 		
    	for (int i = 0; i<serviceList.size(); i++) {//Creates one list of connecting aps for each vehicle/service for calculation of plan cost. Note that connecting aps are fixed 
 			MetricsForVehisFromIntermediateAP iap = new MetricsForVehisFromIntermediateAP(serviceList.get(i).getSourceId(), currentTime, veh2AP);
			vehicleConnectedAPsPerRun.add(i, iap);
 		}
    	
 		for (int i = 0 ; i<serviceList.size() ; i++){ //System.out.println("capacity checking:");
    		
    		List<LocalCostUnits> selectHosts = sortNeighbors(i, serviceList.get(i)); //dep from previous agent 
    		
    		flag = false;
    		j = 0;
    		
    		while (!flag) {
    			
    			host = selectHosts.get(j).getIndex(); //index in the list of serverCloudlets
        		
	    		if (enoughCapacity(host, serviceList.get(i), p.cpuLoad[host], p.memLoad[host], p.stoLoad[host])) {//not needed??
	            	    	 
	    			//utilization, workload, load from previous agent:
	    			p.cpuLoad[host] += serviceList.get(i).getCpuDemand1();
	            	p.memLoad[host] += serviceList.get(i).getMemDemand();
	            	p.stoLoad[host] += serviceList.get(i).getStorageDemand();
	            	
	            	p.updatePlan(host, i, Constants.FP[host], 
	            			Constants.FM[host], Constants.SC_MaxPow[host], Constants.SC_IdlePow[host], serviceList.get(i));
	            	
	            	if (host == (Constants.numNodes - 1))
	            		assToCloud++;
	            	
	            	flag = true;
	            	p.deployPlan[host] = true;//p.deployPlan that is from previous agent is updated here for next agent
	            	
	    		}
	    		else {
	    			j++;
                }
    		}
    	}
            
 		if (!flag)
 			System.out.println("service without host in first fit! Debug");
 		
    	p.setAssToCloud(assToCloud);
    	
    	p.updateNetUtilStatus(p.origUtilPlan, p.origWlPlan);//Swap util/wl values so that only local view is available for the cost calculation
    	
    	lcost.calcLocalCost(p, currentTime, vehicleConnectedAPsPerRun, deployLocal);
    	
    }
    
 	
 	/**Baseline placement method 
 	 * generate one plan then calculate its local_cost
     * @param index index of this plan
     * @param candidateHosts an array of candidate hosts for the placement of services 
     * @return
     */
    public Plan makePlan(short planindex, int[] candidateHosts, boolean[] _backup_deployment) {
    	
    	int host;
    	int assToCloud = 0;
    	
    	/**
         * List of lists of connected aps/propagation-delay/trans-rate/waiting-time/viol-cost... for all vehicles/services of this agent
         */
    	List <MetricsForVehisFromIntermediateAP> vehicleConnectedAPsPerRun = new ArrayList <MetricsForVehisFromIntermediateAP> ();//creates a list of the APs located on the route of service requests (vehicle) received by this agent
 		
    	
    	for (int i = 0; i<serviceList.size(); i++) {//Creates one list of connecting aps for each vehicle/service. Note that connecting aps are fixed 
 			MetricsForVehisFromIntermediateAP iap = new MetricsForVehisFromIntermediateAP(serviceList.get(i).getSourceId(), currentTime, veh2AP);
			vehicleConnectedAPsPerRun.add(i, iap);
 		}
 		//System.out.println("vehicleConnectedAPsPerRun size"+vehicleConnectedAPsPerRun.size());
 		
    	/*
    	 * the temporary load arrays that are going to be assigned to the nodes as a result of this plan
    	 */
    	double [] tempCPULoad = new double [serverCloudlets.size()];//initialize to zero
    	double [] tempMemLoad = new double [serverCloudlets.size()];//initialize to zero
    	double [] tempStorageLoad = new double [serverCloudlets.size()];//initialize to zero
    	
    	Plan p = new Plan(agIndex, planindex, serviceList.size(), _backup_deployment);
        
    	
    	for (int i = 0 ; i<serviceList.size() ; i++){//System.out.println("capacity checking:");
    		
    		host = candidateHosts[i]; //index in the list of serverCloudlets
            //System.out.println("service "+i);
            if (enoughCapacity(host, serviceList.get(i), tempCPULoad[host], tempMemLoad[host], tempStorageLoad[host])) {
            	    	 
            	p.updatePlan(host, i, serviceList.get(i), Constants.FP[host], 
            			Constants.FM[host], Constants.SC_MaxPow[host], Constants.SC_IdlePow[host]);
            	
            	tempCPULoad[host] += serviceList.get(i).getCpuDemand1();
            	tempMemLoad[host] += serviceList.get(i).getMemDemand();
            	tempStorageLoad[host] += serviceList.get(i).getStorageDemand();
            	
            }
            else {
            	host = assignToCloud();
            	//System.out.println("Assigining to cloud due to resource scarcity "+host);
            	if (enoughCapacity(host, serviceList.get(i), tempCPULoad[host], tempMemLoad[host], tempStorageLoad[host])) {
            	    
            		p.updatePlan(host, i, serviceList.get(i), Constants.FP[host], 
                			Constants.FM[host], Constants.SC_MaxPow[host], Constants.SC_IdlePow[host]);
                	
	            	tempCPULoad[host] += serviceList.get(i).getCpuDemand1();
	            	tempMemLoad[host] += serviceList.get(i).getMemDemand();
	            	tempStorageLoad[host] += serviceList.get(i).getStorageDemand();
	            	
	            	assToCloud++;
            	}
            	else {
            		System.out.println("Cloud node run out of space, Debug!");
            		p.incUnassTasks();
            	}
            }
            
        }
    	//System.out.println("plan num of assigned services to Cloud "+assToCloud);
    	p.setAssToCloud(assToCloud);
    	
    	lcost.calcLocalCost(p, currentTime, vehicleConnectedAPsPerRun, _backup_deployment);
    	//p.setTotalLocalCost();   	//System.out.println("Plan "+planindex+" finished");
        
    	return p;
       
    }
    
    //extra method, merge it with the one in method class    
    /** Checks if Equations 18-20 are satisfied
     * @param cs
     * @param service
     * @param pre_cpu
     * @param pre_mem
     * @param pre_storage
     * @return
     */
    public boolean enoughCapacity(int FogDevId, MobileARservice service, double pre_cpu, double pre_mem, double pre_storage) {
 		
 		if ((service.getCpuDemand1() + pre_cpu > Constants.cpuUtilRatio * Constants.FP[FogDevId]) ||
 				(service.getMemDemand() + pre_mem > Constants.memUtilRatio * Constants.FM[FogDevId]) || 
 				(service.getStorageDemand() + pre_storage > Constants.stoUtilRatio * Constants.FS[FogDevId])){
 			return false;
 		}
 		else { 
 			   
 			return true;
 	    }
    }
    
    
    /**
     * @param x_backup 
     * @return array of empty plans for the agent without any received requests 
     */
    private Plan[] emptyPlanGeneration(boolean[] x_backup) {
 		
 		for(short j=0 ; j<Constants.EPOS_NUM_PLANS ; j++){    
 			Plan p = new Plan(agIndex, j, 0, x_backup);  
            Plans[j] = p;
        }
 		return Plans;

 	}

 	
 	/**
 	 * @return id of a cloud server in random
 	 */
 	public int assignToCloud() {
 		return serverCloudlets.size()-1;//last element
 		//return RandomGenerator.getHost(Constants.numFogNodes, Constants.numFogNodes+Constants.numCloudServers);
	}
   
 		
  
  	/**Calculates the local cost components for deployment of service mobileARservice on the servers with enough capacity in the network
  	 * Sorts the servers according to their cost
  	 * @param a
  	 * @param mobileARservice
  	 * @return
  	 */
  	private List<LocalCostUnits> sortNeighbors(int a, MobileARservice mobileARservice) {//dep must be from previous agent updated after deployment by myself and is not updated here

  		int maxid = serverCloudlets.size();
	    ComboCosts cc = new ComboCosts(a, agIndex);
	    int i = 0;
	    for (int j = 0 ; j < maxid ; j++) {
	    	
	    	MetricsForVehisFromIntermediateAP veh_ap = new MetricsForVehisFromIntermediateAP(mobileARservice.getSourceId(), currentTime, veh2AP);
	    	
	    	
	    	if (enoughCapacity(j, mobileARservice, p.cpuLoad[j], p.memLoad[j], p.stoLoad[j])) {
	    		cc.neighbors.add(i, new LocalCostUnits(j, 0));//Double.MAX_VALUE
		        lcost.calcUnitCost(mobileARservice, a, j, p, currentTime, veh_ap, cc.neighbors.get(i));//this does not change p.deployPlan
		    	i++;
	    	}
		    else
		    	continue;
		}
	    
	    cc.sort();
	    return cc.neighbors;
	}

  	 
  	 
	/**select adequate number of candidate hosts which meet the delay (num of links) limit
  	 * @param numOfServices
  	 * @param level hop constraint
  	 * @return
  	 */
  	private int[] selectHosts(int numOfServices){
  	        
  		int minid = 0;
	    int maxid = serverCloudlets.size();
        int i = 0, candid;
        int[] candidateFogIndex = new int[numOfServices];
        
        while (i < numOfServices){
        		//System.out.println("Service "+i+" :");
  	            candid = RandomGenerator.genUniformRandomBetween(minid, maxid);
  	            
  	            
  	            if (candid == serverCloudlets.size()-1)
  	            	System.out.println("	candidate host cloud: "+candid);
  	            //should change:
  	            //= (Constants.dAF[APIndex] + Constants.dAF[host])/Constants.microToSec+ Constants.dFFC[APIndex][host] ;
  	            double delay = Constants.dFFC[candid][fogServer.getMyId()];
  	            if (delay <= Constants.hopLevel * Constants.BaseHopDelay){//both in second
	  	            	candidateFogIndex[i] = candid;
	  	            	//System.out.println("	delay satisfied: "+delay);
						/*
						 * System.out.print("service "+i+ " bw("+temp+","+agIndex+"): ");
						 * System.out.printf("%.0f",NetworkTopology.getBW(serverCloudlets.get(temp).
						 * getId(), fogServer.getId()));
						 * System.out.println(" delay: "+NetworkTopology.getDelay(serverCloudlets.get(
						 * temp).getId(), fogServer.getId()));
						 */
	  	            	i++;
  	            }
  	            
  	        }
  	        /*
  	         //sort candidate hosts according to their distance to this agent in descending order
  	        for (i= 0 ;i<candidateFogIndex.length-1 ; i++){
  	            for (int k=i+1 ; k<candidateFogIndex.length ; k++){
  	                m = NetworkTopology.getDelay(serverCloudlets.get(candidateFogIndex[i]).getId(), fogServer.getId());
  	                n = NetworkTopology.getDelay(serverCloudlets.get(candidateFogIndex[k]).getId(), fogServer.getId());
  	                if(m>n){
  	                    temp = candidateFogIndex[k];
  	                    candidateFogIndex[k] = candidateFogIndex[i];
  	                    candidateFogIndex[i] = temp;
  	                }
  	            }
  	        }
  	        */
  	      return candidateFogIndex;
    }
  	
    public boolean submitService(MobileARservice m) {
    	 this.serviceList.add(m);
    	 return true;
    }
	public boolean connectAgentwithSmartThing(MobileDevice st) {
		
		connectedVehicles.add(st);
		return true;
		
	}
	public Plan[] getPlans() {
		return Plans;
	}

	public void setPlans(Plan[] plans) {
		this.Plans = plans;
	}

	public FogDevice getFogServer() {
		return fogServer;
	}

	public void setFogServer(FogDevice fogServer) {
		this.fogServer = fogServer;
	}

	
	private  ArrayList<MobileARservice> orderServices(List<MobileARservice> serviceList2){
		MobileARservice temp;
	    ArrayList<MobileARservice> tempServices = new ArrayList<MobileARservice>();
	    
	    for (int i=0; i<serviceList2.size()-1; i++){ 
	         for (int j=i+1; j<serviceList2.size(); j++){ 
	        	 
	            if((serviceList2.get(i).getDeadline()-serviceList2.get(i).getsWaitTime())>(serviceList2.get(j).getDeadline()-serviceList2.get(j).getsWaitTime())){
	                temp = serviceList2.get(i);
	                tempServices.set(i, serviceList2.get(j));
	                tempServices.set(j, temp);
	            }
	        }
	    }
	    return tempServices;
	}
	public int getAgIndex() {
		return agIndex;
	}


	public void setAgIndex(int agIndex) {
		this.agIndex = agIndex;
	}

	public int getServiceSize() {
		return serviceSize;
	}

	public void setServiceSize(int serviceSize) {
		this.serviceSize = serviceSize;
	}

	/*
	public int getHost(int max, int min) {
		
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
        
	}
	*/
	
	  }
