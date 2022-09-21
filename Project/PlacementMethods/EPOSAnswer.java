package EdgeEPOS.PlacementMethods;

import java.util.ArrayList;
import java.util.List;

import org.fog.entities.FogDevice;

import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.MobileARservice;
import EdgeEPOS.Utility.RandomGenerator;


/**
 * @author rooyesh
 *
 */
public class EPOSAnswer {

	private double local_cost;
	private double global_cost;
	private int hop_level;
	private double lambda;
	private int period;
	private Plan globalPlan;
	

	public EPOSAnswer(double local_cost, double global_cost, int period) {
		this.period = period;
		this.local_cost = local_cost;
		this.global_cost = global_cost;
		this.hop_level = Constants.hopLevel;
		this.lambda = Constants.lambda;
	}
	
	
	 /**
	     * Determines if a fog node still has storage and memory available
	     *
	     * @param j the index of the fog ndoe
	     */
	   
    /** Checks if Equation 15 is satisfied cpu here maybe is not needed??
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
 		if ((service.getCpuDemand()*service.getRequestPerSec() + pre_cpu < Constants.alpha * cs.getHost().getTotalMips()) &&
 				(service.getMemDemand() + pre_mem < Constants.alpha * cs.getHost().getRam()) && 
 				(service.getStorageDemand() + pre_storage < Constants.alpha * cs.getHost().getStorage()))
 				
            return true;
 		else 
 			return false;
 	}
    
    /**
 	 * @return id of a cloud server in random
 	 */
 	public int assignToCloud() {
 		
 		return RandomGenerator.getHost(Constants.numFogNodes, Constants.numFogNodes+Constants.numCloudServers);
	}
 	
 	
    /**applying selected plans, allocating resources and executing the services:
     *I-EPOS plan indexes coming form input; one plan (index) per node. Based on that we can identify the selected plan and apply it to the network nodes
     * @param selectedPIndex
     * @param plans
     * @param agents
     * @param services
     * @param serverCloudlets
     * @return
     */
    public Plan deployEposAnswerToMapping(ArrayList<Integer> selectedPIndex, Plan[][] plans, List<Agent> agents, List<MobileARservice> services, List<FogDevice> serverCloudlets){//gets binary plans and workload plans and then updates the nodes' capacity and deployed services 
    	int host;
    	MobileARservice service;
    	int assToCloud = 0;
    	System.out.println("applying generated plans: assigning tasks to resources....");
        
    	//the temporary load that is going to be assigned to the nodes as a result of this plan:
    	double [] tempCPULoad = new double [serverCloudlets.size()];//initialize to zero
    	double [] tempMemLoad = new double [serverCloudlets.size()];//initialize to zero
    	double [] tempStorageLoad = new double [serverCloudlets.size()];//initialize to zero
    	
        int agentIndex;
        
        
        for (agentIndex = 0; agentIndex < agents.size(); agentIndex++){//all nodes have to apply their selected plans:
            
        	Plan ptemp = plans[agentIndex][selectedPIndex.get(agentIndex)];
            for (int i = 0 ; i<agents.get(agentIndex).serviceList.size() ; i++){ //one node applies its selected plan; 
                
                host = ptemp.y[i];//id or index???
                if (enoughCapacity(serverCloudlets.get(host), agents.get(agentIndex).serviceList.get(i), tempCPULoad[host], tempMemLoad[host], tempStorageLoad[host])) {
                   
                	tempCPULoad[host] += agents.get(agentIndex).serviceList.get(i).getCpuDemand() * agents.get(agentIndex).serviceList.get(i).getRequestPerSec();
                	tempMemLoad[host] += agents.get(agentIndex).serviceList.get(i).getMemDemand();
                	tempStorageLoad[host] += agents.get(agentIndex).serviceList.get(i).getStorageDemand();
                	globalPlan.x[agents.get(agentIndex).serviceList.get(i).getAppId()][host] = 1 ;
                	globalPlan.y[agents.get(agentIndex).serviceList.get(i).getAppId()] = host;
                }
                else {
                	host = assignToCloud();
                	
                	tempCPULoad[host] += agents.get(agentIndex).serviceList.get(i).getCpuDemand() * agents.get(agentIndex).serviceList.get(i).getRequestPerSec();
                	tempMemLoad[host] += agents.get(agentIndex).serviceList.get(i).getMemDemand();
                	tempStorageLoad[host] += agents.get(agentIndex).serviceList.get(i).getStorageDemand();
                	
                	globalPlan.x[agents.get(agentIndex).serviceList.get(i).getAppId()][host] = 1 ;
                	globalPlan.y[agents.get(agentIndex).serviceList.get(i).getAppId()] = host;
                
                	assToCloud++;
                }
            }
        
        }//end of applying all plans to the network nodes
        globalPlan.setAssToCloud(assToCloud);
            
        return globalPlan;
    } 
    
	public int getPeriod() {
		return period;
	}

	public void setPeriod(int period) {
		this.period = period;
	}
	public double getLocal_cost() {
		return local_cost;
	}

	public double getGlobal_cost() {
		return global_cost;
	}

	public int getHop_level() {
		return hop_level;
	}

	public double getLambda() {
		return lambda;
	}

}
