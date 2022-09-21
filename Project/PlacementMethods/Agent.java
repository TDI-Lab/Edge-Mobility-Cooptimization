package EdgeEPOS.PlacementMethods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.cloudbus.cloudsim.NetworkTopology;
import org.cloudbus.cloudsim.Vm;
import org.fog.application.Application;
import org.fog.entities.ApDevice;
import org.fog.entities.FogDevice;
import org.fog.entities.MobileDevice;
import org.fog.localization.Distances;

import EdgeEPOS.CostComponents.Cost;
import EdgeEPOS.CostComponents.ProcessingCost;
import EdgeEPOS.CostComponents.ServiceDelay;
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
	
	//private int agentId;
	private int agIndex;
	private FogDevice fogServer;
	
	private List<FogDevice> serverCloudlets;
	private List<ApDevice> apdevices;
	private Set<MobileDevice> connectedVehicles;//st.setVmMobileDevice(vmSmartThingTest);
	////connectedVehicles = fogServer.getSmartThings();// equals to serviceList
	public List <MobileARservice> serviceList;//agent's service list
	Cost lcost;
	ProcessingCost costComProcDep;
	ServiceDelay sD;
	protected double lambda_in[]; // lambda'^in_ak
    
    public Plan [] Plans;
	double[] workload;// = new double[numOfNodes*2];//CPU and energy
    double[] utilization;
    //int timePeriodDuration; 

    
	/**
	 * @param sc
	 * @param networkCloudlets excessive param
	 * @param apdevices excessive param???
	 */
	public Agent(FogDevice sc, List<FogDevice> networkCloudlets, List<ApDevice> apdevices){
		//timePeriodDuration??
		//this.agentId = sc.getId();
		agIndex = sc.getMyId();
		this.fogServer = sc;
		this.apdevices = apdevices;
		this.serverCloudlets = networkCloudlets;
		
		workload = new double[2*(Constants.numFogNodes+Constants.numCloudServers)];//CPU and energy
		utilization = new double[2*(Constants.numFogNodes+Constants.numCloudServers)];//CPU and energy
		initializeWorkload();
		
    
	}
	
	//all with zero??? yes
	private void initializeWorkload() {
		for (int i=0; i<Constants.numFogNodes; i++) {
			workload[i] = 0;
			workload[i+Constants.EDGE_ROUTERS+Constants.BACKBONE_ROUTERS] = 0;
			utilization[i] = 0 ;
			utilization[i+Constants.EDGE_ROUTERS+Constants.BACKBONE_ROUTERS] = 0;
			//utilization[i] = workload[i]/serverCloudlets.get(i).getHost().getTotalMips();
			//utilization[i+Constants.numCloudServers+Constants.numFogNodes] = workload[i+Constants.numCloudServers+Constants.numFogNodes]/
			//		serverCloudlets.get(i).getHost().getRam();
		}
	}
	
	// Calculates the arrival rate of the instructions to the fog nodes 
    public void arrivalRatesOfInstructionsToFogNode(List<MobileARservice> ServiceList) {
    	serviceList = ServiceList; 
    	/*
    	serviceList = new ArrayList <MobileARservice> ();
		
    	for (MobileARservice m: globalServiceList) {
    		if (m.getEdgeId() ==  this.agIndex)
    			serviceList.add(m);
    	}
    	*/
    	//System.out.println ("agent index: "+agIndex+" number of received service requests: "+serviceList.size());
    	//fogServer.getSmartThingsWithVm();
		lambda_in = new double[serviceList.size()];
		int inTotalTrafficToThis = 0;
	 	for (int i = 0; i<serviceList.size(); i++) {
	 		lambda_in[i] = serviceList.get(i).getRequestPerSec();
			inTotalTrafficToThis += lambda_in[i];
			//System.out.println ("service "+i+" lambda "+lambda_in[i]);
			
	   	}
}
	
	
    /** makes candidate host selection and then call to generate a plan accordingly 
     * @param hopLevel
     * @param lambda
     * @param lambda2 
     * @return an array of plans along with its local_cost
     */
    public Plan[] generatePlans(int period, int [][] backPlacement) {
		
 		System.out.println("Agent id: "+ getAgIndex()+" Number Of Received Service Requests: "+serviceList.size());
 		
 		costComProcDep = new ProcessingCost(serviceList, serverCloudlets, apdevices);
 		sD = new ServiceDelay(serviceList, serverCloudlets);
        lcost = new Cost (period, fogServer, apdevices, serverCloudlets, serviceList, fogServer.getSmartThingsWithVm(),costComProcDep, sD);
         
         int[] selectedHosts = new int [serviceList.size()];
         Plans = new Plan [Constants.EPOS_NUM_PLANS];
         
     	 if (serviceList.size() == 0) {
        	 emptyPlanGeneration(backPlacement);
         }
         else {
        	
        	 for(int planIndex=0 ; planIndex < Constants.EPOS_NUM_PLANS ; planIndex++){  
        		 System.out.println("plan "+planIndex);
	      		 selectedHosts = selectHosts(serviceList.size()); 
	             Plans[planIndex] = makePlan(planIndex, selectedHosts, backPlacement);
	         }
         }
         
    	 System.out.println("End of plan generation...........");
    	 Utility.writePlans(Plans, agIndex);
         return Plans;
 	}
 	
	
 	
    /** generate one plan then calculate its local_cost
     * @param index index of this plan
     * @param candidateHosts an array of candidate hosts for the placement of services 
     * @return
     */
    public Plan makePlan(int planindex, int[] candidateHosts, int [][] prePlacement) {
    	
    	int host;
    	int assToCloud = 0;
    	MobileARservice service;
    	
    	//the temporary load that is going to be assigned to the nodes as a result of this plan:
    	double [] tempCPULoad = new double [serverCloudlets.size()];//initialize to zero
    	double [] tempMemLoad = new double [serverCloudlets.size()];//initialize to zero
    	double [] tempStorageLoad = new double [serverCloudlets.size()];//initialize to zero
    	
    	Plan p = new Plan(agIndex, planindex, serviceList.size(), prePlacement);
        
    	
    	for (int i = 0 ; i<serviceList.size() ; i++){
        	service = serviceList.get(i);
            host = candidateHosts[i]; //index in the list of serverCloudlets
             
            if (enoughCapacity(serverCloudlets.get(host), service, tempCPULoad[host], tempMemLoad[host], tempStorageLoad[host])) {
       
            	    	
            	p.updatePlan(host, i, service, serverCloudlets.get(host).getHost().getTotalMips(), 
            			serverCloudlets.get(host).getHost().getRam(),serverCloudlets.get(host).getHost().getMaxPower());
            	
            	tempCPULoad[host] += service.getCpuDemand() * service.getRequestPerSec();
            	tempMemLoad[host] += service.getMemDemand();
            	tempStorageLoad[host] += service.getStorageDemand();
            	
            }
            else {
            	host = assignToCloud();
            	p.updatePlan(host, i, service, serverCloudlets.get(host).getHost().getTotalMips(),serverCloudlets.get(host).getHost().getRam(),serverCloudlets.get(host).getHost().getMaxPower());
            	
            	tempCPULoad[host] += service.getCpuDemand() * service.getRequestPerSec();
            	tempMemLoad[host] += service.getMemDemand();
            	tempStorageLoad[host] += service.getStorageDemand();
            	
            	assToCloud++;
            }
            
        }
    	//System.out.println("plan num of assigned services to Cloud "+assToCloud);
    	p.setAssToCloud(assToCloud);
    	p.setCost(lcost.calcLocalCost(p));
       return p;
       
    }
    
    
    /** Determines if a fog node still has storage and memory available
     * Checks if Equation 15 is satisfied cpu here maybe is not needed??
     * equations 16, 17, and 18 are already implemented in calcServiceDelay().
     * @param cs
     * @param service
     * @param pre_cpu
     * @param pre_mem
     * @param pre_storage
     * @return
     */
    public boolean enoughCapacity(FogDevice cs, MobileARservice service, double pre_cpu, double pre_mem, double pre_storage) {
 		//must be sure: is this stability? no
 		if ((service.getCpuDemand()*service.getRequestPerSec() + pre_cpu > Constants.alpha * cs.getHost().getTotalMips()) ||
 				(service.getMemDemand() + pre_mem > Constants.alpha * cs.getHost().getRam()) || 
 				(service.getStorageDemand() + pre_storage > Constants.alpha * cs.getHost().getStorage()))
 				
            return false;
 		else 
 			return true;
 	}
    
    
    /**
     * @return array of empty plans for the agent without any received requests 
     */
    private boolean emptyPlanGeneration(int [][] prePlacement) {
 		
 		for(int j=0 ; j<Constants.EPOS_NUM_PLANS ; j++){    
 			Plan p = new Plan(agIndex, j, 0, prePlacement);  
            Plans[j] = p;
        }
 		return true;

 	}

 	
 	/**
 	 * @return id of a cloud server in random
 	 */
 	public int assignToCloud() {
 		
 		return RandomGenerator.getHost(Constants.numFogNodes, Constants.numFogNodes+Constants.numCloudServers);
	}
    /*
    public void updateDecisionVariablesAccordingToEPOS(long combination) {
    	 
    	ArrayList<Integer> selectedPlansIndex = new ArrayList<Integer>();// to save the output of I-EPOS; index of selected plans
        getInputFromEpos(selectedPlansIndex, sRequest_Distribution, hopConstLevel, config, profile);
       
        for (nodeIndex = 0; nodeIndex<numOfNodes; nodeIndex++){
            ArrayList<BinaryPlan> biplan = new ArrayList<BinaryPlan>(); 
            selectedPlan = plans[nodeIndex][selectedPIndex.get(nodeIndex)];
            biplan.addAll(selectedPlan.biPlans);
            for (i = 0 ; i<biplan.size() ; i++){  
                try
                {
	            t = (Task)biplan.get(i).T.clone();
                }
	        catch(CloneNotSupportedException c){}  
	
	       
	        host = biplan.get(i).hostNodeId;
	        if(biplan.get(i).value == 1){ //if task will be running on some nodes then update its host capacity
	   
	        }
            }
        }
  }
*/
   
 
  
  	/**select adequate number of candidate hosts which meet the delay (num of links) limit
  	 * @param numOfServices
  	 * @param level hop constraint
  	 * @return
  	 */
  	private int[] selectHosts(int numOfServices){
  	        int maxid = serverCloudlets.size();
  	        int minid = 0;
  	    	int i = 0, temp; double m,n;
  	        int[] candidateFogIndex = new int[numOfServices];
  	        
  	        while (i < numOfServices){
  	        	
  	            temp = RandomGenerator.genUniformRandomBetween(minid, maxid-1);
  	            //if cloud then host = cloud
  	            System.out.println("candid host index: "+temp+" delay "+NetworkTopology.getDelay(serverCloudlets.get(temp).getId(), fogServer.getId()));
  	            if (NetworkTopology.getDelay(serverCloudlets.get(temp).getId(), fogServer.getId()) <= Constants.hopLevel * Constants.MIN_dFF){//delay?
  	            	candidateFogIndex[i] = temp;
  	            	System.out.println("service "+i+ " host "+temp);
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

	

	//not used now
	private  ArrayList<MobileARservice> orderServices(List<MobileARservice> serviceList2){
		MobileARservice temp;
	    ArrayList<MobileARservice> tempServices = new ArrayList<MobileARservice>();
	    
	    for (int i=0; i<serviceList2.size()-1; i++){ 
	         for (int j=i+1; j<serviceList2.size(); j++){ 
	        	 
	            if((serviceList2.get(i).getsDeadline()-serviceList2.get(i).getsWaitTime())>(serviceList2.get(j).getsDeadline()-serviceList2.get(j).getsWaitTime())){
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

	
	/*
	public int getHost(int max, int min) {
		
		Random r = new Random();
		return r.nextInt((max - min) + 1) + min;
        
	}
	*/
	
	  }
