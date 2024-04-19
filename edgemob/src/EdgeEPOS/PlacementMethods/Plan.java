package EdgeEPOS.PlacementMethods;


import java.util.Arrays;
import java.util.List;

import EdgeEPOS.CostComponents.ComProcessDepStorCost;
import EdgeEPOS.CostComponents.Cost;
import EdgeEPOS.CostComponents.EnergyCost;
import EdgeEPOS.CostComponents.ServiceDelay;
import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.MobileARservice;
import fog.entities.FogDevice;


/**
 * @author rooyesh
 *
 */
public class Plan {
	
	public int gp = 0;
	int edgeNode;//associated node
    int agIndex;//index of the associated cloudlet in the cloudlet list 
    public short planIndex;//excessive
    public int ServiceSize;
	public int[] y;		//host nodes for services
    public short[][] x; //Binary Matrix including requests to hosts mapping
    public boolean empty_node = false;//no service request to deploy
    /**
     * plan for calculation of energy cost for hosts
     */
    public int[] energyPlan;
    /**
     * total mips load on hosts
     */
    public double[] utilPlan;// = new double[numOfNodes*2];//CPU and Mem
    /**
     * total "mi per request" load on hosts
     */
    public double[] wlPlan;// = new double[numOfNodes*2];//CPU : mi per request and Mem
    
    /**
     * true for the deployed services of previous runs
     */
    public boolean[] deployPlan;//true or false for deployment cost
    public boolean selected = false;
    
    int assTasks;
    int assToCloud;//costs[8]
    int unassTasks;
    
    double dlViolCost;//costs[0]
    double procCost;//costs[1]
    double storCost;//costs[2]
    double memCost;//costs[3]
    double commCost;//costs[4]
    double deplCost;//costs[5]
    double energyCost;//costs[6]
    double co2EmitCost;//costs[7]
    public double [] encomp;
    
    public double [] lcosts;
	double totalLocalCost;//costs[9]
    double globalCost;
	
    private boolean FF = false;
    
    double[] cpuLoad;
    double[] memLoad;
    double[] stoLoad;
    public double[] origUtilPlan;// = new double[numOfNodes*2];//CPU and Mem
    /**
     * total "mi per request" load on hosts
     */
    public double[] origWlPlan;// = new double[numOfNodes*2];//CPU : mi per request and Mem
    

    public boolean isFF() {
		return FF;
	}

    /** Constructor for first fit plans where arrays are present
     * @param type
     * @param agindex
     * @param planindex
     * @param serviceSize
     * @param backup_deployment
     */
    public Plan(boolean type, int agindex, short planindex, int serviceSize, boolean[] backup_deployment) {


    	this.FF = true;
        this.cpuLoad = new double [Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
        this.memLoad = new double [Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
        this.stoLoad = new double [Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
        origUtilPlan = new double[2*(Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS)];
        origWlPlan = new double[2*(Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS)];
    	
        planIndex = planindex;
    	agIndex = agindex;
    	ServiceSize = serviceSize;
    	unassTasks = 0;
        assTasks = 0;
        assToCloud = 0;
        
        if (serviceSize != 0) {
        	x = new short[serviceSize][Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
	        y = new int[serviceSize];
	        energyPlan = new int [Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
	    }
        
        lcosts = new double[Constants.APPLIEDCOST.length];
        
        //if service list is empty --> plan contains zero as workload and utilization
		utilPlan = new double[2*(Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS)];
		wlPlan = new double[2*(Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS)];
        deployPlan = new boolean [Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
        initializeDeployments(backup_deployment);
        setCostsToZero();//zero for costs
        
        if (serviceSize == 0 ) {
        	empty_node = true;
        }
    }
    

    
   	/**Global response
     * The constructor of the class. Initializes the arrays 
     * @param numServices the number of services
     * @param numFogNodes the number of fog nodes
     * @param numCloudServers the number of cloud servers
     */
     public Plan(int numServices, boolean GP) {
    	
    	this.FF = false;
        gp = 1;
    	ServiceSize = numServices;
    	unassTasks = 0;
        assTasks= 0;
        assToCloud= 0;
        lcosts = new double[Constants.APPLIEDCOST.length];
		
        if (numServices != 0) {
        	x = new short[numServices][Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];//all the services (== total num smart things) and network nodes
	        y = new int[numServices];
	        energyPlan = new int[Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
	    }
        else {
      	  empty_node = true;
        }
        utilPlan = new double [2*(Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS)]; 	//CPU and energy
        wlPlan = new double [2*(Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS)];		//CPU and energy
        deployPlan = new boolean [Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];	//deployment of services on nodes (true; if deployed, false; not deployed)
        //energyPlan = new int [Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
        initializewl();
        setCostsToZero();
        
    }
    
     

    
     /**
     * Candidate plan planindex for the agent agindex, note that the size of arrays are based on the services received by this agent not total
     * @param agindex
     * @param planindex
     * @param serviceSize
     * @param backup_deployment
     */
    public Plan(int agindex, short planindex, int serviceSize, boolean[] backup_deployment) {
        
    	    this.FF = false;
        	planIndex = planindex;
        	agIndex = agindex;
        	ServiceSize = serviceSize;
        	unassTasks = 0;
	        assTasks = 0;
	        assToCloud = 0;
	        
	        if (serviceSize != 0) {
	        	x = new short[serviceSize][Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
		        y = new int[serviceSize];
		        energyPlan = new int[Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
		    }
	        
	        lcosts = new double[Constants.APPLIEDCOST.length];
	        //if service list is empty --> plan contains zero as workload and utilization
    		utilPlan = new double[2*(Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS)];//CPU and energy
            wlPlan = new double[2*(Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS)];//CPU and energy
            deployPlan = new boolean [Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
            
            initializeDeployments(backup_deployment);
            setCostsToZero();//zero for costs
            
            if (serviceSize == 0 ) {
            	empty_node = true;
            }
    }
    
    private void setCostsToZero() {
    	this.totalLocalCost = 0;
    	this.assTasks = 0;
    	this.assToCloud = 0;//costs[7]
    	this.unassTasks = 0;
        
    	this.dlViolCost = 0;//costs[0]
    	
    	this.procCost = 0;//costs[1]
    	this.storCost = 0;//costs[2]
    	this.memCost = 0;
    	this.deplCost = 0;//costs[4]
    	this.commCost = 0;//costs[5]
    	this.energyCost = 0;//costs[1]
    	this.co2EmitCost = 0;
		
    	
	}

	/**
	 * do the initialization for global plan
	 */
	private void initializewl() {
  		
		for(int i = 0; i<wlPlan.length ; i++) {
			wlPlan[i] = 0;
		}
		for(int i = 0; i<deployPlan.length ; i++) {
			deployPlan[i] = false;
		}
		
	}
	
	public void swapUtil(double[] utilPlan2, double[] wlPlan2) {
		double temp1, temp2;
		for(int i = 0; i<wlPlan.length ; i++) {
			temp1 = wlPlan[i];
			temp2 = utilPlan[i];
			
			wlPlan[i] = wlPlan2[i];
			utilPlan[i] = utilPlan2[i];
			
			wlPlan2[i] = temp1;
			utilPlan2[i] = temp2;
		}
		
	}

	public void updateNetUtilStatus(double[] utilPlan2, double[] wlPlan2) {
		
		for(int i = 0; i<wlPlan.length ; i++) {
			wlPlan[i] = wlPlan2[i];
		}
		
		for(int i = 0; i<utilPlan.length ; i++) {
			utilPlan[i] = utilPlan2[i];
		}
		
	}
	
  	public void updatePlan() {
 		for (int i =0; i<utilPlan.length; i++) 
 			utilPlan[i] = 0;
  	}
 	
	public void updatePlan(int hostIndex, int serviceIndex, MobileARservice ms, double mipsCapacity, double memCapacity, double sC_maxPow, double sC_IdlePow) {
		
 		x[serviceIndex][hostIndex] = 1;
 		y[serviceIndex] = hostIndex;
 		
 		//CPU demand: be careful with utilPlan:
 		double utilTemp = utilPlan[hostIndex];
        utilPlan[hostIndex] = (ms.getCpuDemand1() + utilTemp*mipsCapacity) / (mipsCapacity);
        //mi per request:
        wlPlan[hostIndex] += ms.getCpuDemand();
        //deployPlan[hostIndex] = true;
		
		// memory part:
		utilPlan[hostIndex+Constants.numNodes] = (ms.getMemDemand() + wlPlan[hostIndex+Constants.numNodes]) / (memCapacity); 
		wlPlan[hostIndex+Constants.numNodes] += ms.getMemDemand();
	        
		  
   }
 	
	public void updatePlan(int hostIndex, int serviceIndex, double mipsCapacity, double memCapacity, double sC_maxPow, double sC_IdlePow, MobileARservice ms) {
		
 		x[serviceIndex][hostIndex] = 1;
 		y[serviceIndex] = hostIndex;
 		
 		//CPU part
 		utilPlan[hostIndex] = cpuLoad[hostIndex] / mipsCapacity;
        
		//memory part
		utilPlan[hostIndex+Constants.numNodes] = memLoad[hostIndex] / memCapacity; 
		    
		if (FF) {
				double utilTemp = origUtilPlan[hostIndex];
				origUtilPlan[hostIndex] = (ms.getCpuDemand1() + utilTemp * mipsCapacity) / (mipsCapacity);
		        origWlPlan[hostIndex] += ms.getCpuDemand();
		        
		        origUtilPlan[hostIndex+Constants.numNodes] = (ms.getMemDemand() + origWlPlan[hostIndex+Constants.numNodes]) / (memCapacity); 
				origWlPlan[hostIndex+Constants.numNodes] += ms.getMemDemand();
		}
		
	      
   }
 	
	public int getAgIndex() {
		return agIndex;
	}

	public void setAgIndex(int agIndex) {
		this.agIndex = agIndex;
	}
	
	public int getEdgeNode() {
		return edgeNode;
	}

	public void setEdgeNode(int edgeNode) {
		this.edgeNode = edgeNode;
	}
	
	
	
	
	/**
	 * set local cost with the summation of all costs  
	 */
	public void setTotalLocalCost() {
		
		this.totalLocalCost = dlViolCost + procCost + storCost + memCost + commCost + deplCost + energyCost + co2EmitCost;
		
		if (Constants.APPLIEDCOST[8] == 1)
			this.totalLocalCost += assToCloud;
		
		lcosts[9] = this.totalLocalCost;
	}
	
	public double getCost(int i) {
		return lcosts[i];
	} 
	
	
	public void setGlobalCost(double globalCost) {
		this.globalCost = globalCost;
	}
	
	public double getGlobalCost() {
		return globalCost;
	}

    public double getTotalLocalCost() {
		return totalLocalCost;
	}


    
    
    
	public int getUnassTasks() {
		return unassTasks;
	}
	public void setUnassTasks(int unassTasks) {
		this.unassTasks = unassTasks;
	
	}

	public void incUnassTasks() {
		this.unassTasks++;
	}
	public int getAssTasks() {
		return assTasks;
	}

	public void setAssTasks(int assTasks) {
		this.assTasks = assTasks;
	}

	public int getAssToCloud() {
		return assToCloud;
	}

	public void setAssToCloud(int assToCloud) {
		this.assToCloud = assToCloud;
		lcosts[8] = assToCloud;
	}

	
	
	

	public double getDlViolCost() {
		return dlViolCost;
	}
	
    public void setDlViolCost(double dlViolCost) {
    	this.dlViolCost = dlViolCost;
		lcosts[0] = dlViolCost;
	}

	
	public double getProcCost() {
		return procCost;
	}

	public void setProcCost(double procCost) {
		this.procCost = procCost;
		lcosts[1] = procCost;
	}

	public double getStorCost() {
		return storCost;
	}

	public void setStorCost(double storCost) {
		this.storCost = storCost;
		lcosts[2] = storCost;
	}

	public double getMemCost() {
		return this.memCost;
		
	}

	public void setMemCost(double mc) {
		this.memCost = mc;
		lcosts[3] = mc;
		
	}
	
	public double getCommCost() {
		return commCost;
	}

	public void setCommCost(double commCost) {
		this.commCost = commCost;
		lcosts[4] = commCost;
	}
	
	public double getDeplCost() {
		return deplCost;
	}

	public void setDeplCost(double deplCost) {
		this.deplCost = deplCost;
		lcosts[5] =deplCost;
	}
	
	public double getEnergyCost() {
		return energyCost;
		
	}
	public void setEnergyCost(double energyCost) {
		this.energyCost = energyCost;
		lcosts[6] = energyCost;
	}

	 public double getCo2EmitCost() {
			return co2EmitCost;
		}

	public void setCo2EmitCost(double co2EmitCost) {
		this.co2EmitCost = co2EmitCost;
		lcosts[7] = co2EmitCost;
	}


	 @Override
	 public String toString() {
			return "[edgeNode=" + edgeNode +  ", agIndex=" + agIndex +", planIndex=" + planIndex + ", empty_node="
					+ empty_node + ", ServiceSize=" + ServiceSize + ", assTasks=" + assTasks + ", assToCloud=" + assToCloud
					+ ", unassTasks=" + unassTasks + ", dlViolCost=" + dlViolCost + ", energyCost=" + energyCost
					+ ", procCost=" + procCost + ", storCost=" + storCost + ", memCost=" + memCost + ", deplCost="
					+ deplCost + ", commCost=" + commCost + ", co2EmitCost=" + co2EmitCost + ", costs="
					+ Arrays.toString(lcosts) + ", totalLocalCost=" + totalLocalCost + ", globalCost=" + globalCost + "]";
		}



	/**updates the load on every node in the network for first fit method
	 * @param cpuLoad2
	 * @param memLoad2
	 * @param stoLoad2
	 */
	public void updateNetLoadStatus(double[] cpuLoad2, double[] memLoad2, double[] stoLoad2) {

		for(int i = 0; i<cpuLoad.length ; i++) {
			cpuLoad[i] = cpuLoad2[i];
		}
		
		for(int i = 0; i<memLoad.length ; i++) {
			memLoad[i] = memLoad2[i];
		}
		
		for(int i = 0; i<stoLoad.length ; i++) {
			stoLoad[i] = stoLoad2[i];
		}
		
	}

	/**sets the initial workload to zero and initializes the deploy plan with what we had from previous run
  	 * @param _backup_deployment
  	 *  
  	 */
  	private void initializeDeployments(boolean[] _backup_deployment) {
  		
		//for(int i = 0; i<wlPlan.length ; i++) {
		//	wlPlan[i] = 0;
		//}
		for(int i = 0; i<deployPlan.length ; i++) {
			if (_backup_deployment[i] == true)
				deployPlan[i] = true;
			else
				deployPlan[i] = false;
		}
		
	}

  	/**calculates the global cost: variance/RMSE for the utilization plan
  	 * @return
  	 */
  	public double calculateGlobalFunc() {
        int n; 
        double gc = 0;
  		if (Constants.gcFunc == "VAR") {
	  		n = utilPlan.length;
	
	        //Calculate the mean
	        double sum = 0;
	        for (double value : utilPlan) {
	            sum += value;
	        }
	        double mean = sum / n;
	
	        //Calculate the sum of squared differences from the mean
	        double sumSquaredDifferences = 0;
	        for (double value : utilPlan) {
	            double difference = value - mean;
	            sumSquaredDifferences += difference * difference;
	        }
	
	        gc = sumSquaredDifferences / n;
	       // return variance;
  		}
  		
  		else if (Constants.gcFunc == "RMSE") {
	  		
  			n = Constants.SC_RenewablePortion.length;
	        double sumSquaredErrors = 0.0;
	
	        for (int i = 0; i < n; i++) {
	            double error = Constants.SC_RenewablePortion[i] - utilPlan[i];
	            sumSquaredErrors += error * error;
	        }
	
	        double meanSquaredError = sumSquaredErrors / n;
	    
	        gc = Math.sqrt(meanSquaredError);
  		}
  		return gc;
    
  	}

	/*
	 * public void initializaWorkload(double[] tempCPULoad, double[] tempMemLoad,
	 * double[] tempStorageLoad) {
	 * 
	 * for(int i = 0; i<cpuLoad.length ; i++) { tempCPULoad[i] = cpuLoad[i]; }
	 * 
	 * for(int i = 0; i<memLoad.length ; i++) { tempMemLoad[i] = memLoad[i]; }
	 * 
	 * for(int i = 0; i<stoLoad.length ; i++) { tempStorageLoad[i] = stoLoad[i]; } }
	 */


	public void updateNetDeploymentStatus(boolean[] dep) {
		
		for(int i = 0; i<deployPlan.length ; i++) {
			if (dep[i] == true)
				deployPlan[i] = true;
			else
				deployPlan[i] = false;
		}
		
	}

	public void setENCosts(double[] eNCostParts) {
		//encomp = new double[3];
		encomp = eNCostParts;
		
	}
	public double[] getENCosts() {
		return encomp;
	}





	

		
}
