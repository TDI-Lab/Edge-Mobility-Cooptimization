package EdgeEPOS.PlacementMethods;

import java.util.ArrayList;
import java.util.List;

import org.cloudbus.cloudsim.NetworkTopology;
import org.cloudbus.cloudsim.Vm;

import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.MobileARservice;


/**
 * @author rooyesh
 *
 */
public class Plan {

	public boolean empty_node = false;
    
	public int[][] x; // x_aj
    public int [] y;
    public int[][] x_backup; // backup of x_aj
    //public int[][] xp; // x'_ak
    
    public int[][] v; // v_aj
    public double d[][]; // stores d_aj
    public double Vper[]; // V^%_a

    public double cost;
    int edgeNode;//associated node
    int planIndex;//excessive
    int agIndex;//index of the associated cloudlet in the cloudlet list 
    int unassTasks;
    int assTasks;
    int assToCloud;
    int dlViol;
    double serviceDealy;
    
    public double[] utilPlan;// = new double[numOfNodes*2];//CPU and Mem
    double[] wlPlan;// = new double[numOfNodes*2];//CPU and Mem
    
    //int serviceSize;
	//List<MobileARservice> serviceList;

    		
    /**Global response
     * The constructor of the class. Initializes the arrays 
     * @param numServices the number of services
     * @param numFogNodes the number of fog nodes
     * @param numCloudServers the number of cloud servers
     */
     public Plan(int numServices, int numFogNodes, int numCloudServers) {
        x = new int[numServices][numFogNodes+numCloudServers];
        //xp = new int[numServices][numCloudServers];
        
        x_backup = new int[numServices][numFogNodes+numCloudServers];
        v = new int[numServices][numFogNodes];
        d = new double[numServices][numFogNodes];
        Vper = new double[numServices];
    }
    
    //service list is empty --> plan contains zero as workload and utilization
    public Plan(int agindex, int planindex, int serviceSize, int [][] backPlacement) {
        
        	planIndex = planindex;
        	agIndex = agindex;
        	//this.serviceSize = serviceSize;
	        cost = 0.0;
	        unassTasks = 0;
	        assTasks= 0;
	        assToCloud= 0;
	        dlViol= 0;
	        
        	x = new int[serviceSize][Constants.numFogNodes+Constants.numCloudServers];
        	y = new int[serviceSize];
        	x_backup = backPlacement;//?? check id services
	       
    		utilPlan = new double[2*(Constants.numFogNodes+Constants.numCloudServers)];//CPU and energy
            wlPlan = new double[2*(Constants.numFogNodes+Constants.numCloudServers)];//CPU and energy
    	    
            initializewl();
            
          if (serviceSize == 0 ) 
        	  empty_node = true;
    }
    
    public Plan() {
		// TODO Auto-generated constructor stub
	}

  	private void initializewl() {
		for(int i = 0; i<wlPlan.length ; i++) {
			wlPlan[i] = 0;
		}
		
	}

	
	public void updatePlan() {
 		for (int i =0; i<utilPlan.length; i++) 
 			utilPlan[i] = 0;
 		
   }
 	/*
		 * //Memory utilPlan[hostIndex+Constants.numCloudServers+Constants.numFogNodes]
		 * = (ms.getMemDemand() +
		 * wlPlan[hostIndex+Constants.numCloudServers+Constants.numFogNodes]) /
		 * (ramCapacity); wlPlan[hostIndex+Constants.numCloudServers+Constants.numFogNodes] += ms.getMemDemand();
        */
	public void updatePlan(int hostIndex, int serviceIndex, MobileARservice ms, double mipsCapacity, double ramCapacity, double e) {
		
 		x[serviceIndex][hostIndex] = 1;
 		y[serviceIndex] = hostIndex;
 		
 		//CPU
        utilPlan[hostIndex] = (ms.getCpuDemand() * ms.getRequestPerSec() + wlPlan[hostIndex]) / (mipsCapacity);
        wlPlan[hostIndex] += ms.getCpuDemand() * ms.getRequestPerSec();
        
		
        //energy
        //serverCloudlets.get(host).getHost().getNumberOfFreePes()
        if (hostIndex < Constants.numFogNodes)//only reqpersec??
        	utilPlan[hostIndex+Constants.numCloudServers+Constants.numFogNodes] = 
        		Constants.a * (Math.pow(wlPlan[hostIndex] , Constants.p_j))
        		+ Constants.b * (wlPlan[hostIndex])
        		+ Constants.c;
        else //??num of machines
        	utilPlan[hostIndex+Constants.numCloudServers+Constants.numFogNodes] = //machine cycle per sec??
    		Constants.A * (Math.pow(wlPlan[hostIndex] , Constants.p_k))
    		+ Constants.B;
    		
        //wlPlan[hostIndex+Constants.numCloudServers+Constants.numFogNodes] += ms.getMemDemand();
        
   }
 	
       
	public double getServiceDealy() {
		return serviceDealy;
	}

	public void setServiceDealy(double serviceDealy) {
		this.serviceDealy = serviceDealy;
	}
	public double getCost() {
		return cost;
	}

	public int getUnassTasks() {
		return unassTasks;
	}

	public int getAgIndex() {
		return agIndex;
	}

	public void setAgIndex(int agIndex) {
		this.agIndex = agIndex;
	}

	public void setUnassTasks(int unassTasks) {
		this.unassTasks = unassTasks;
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
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public int getEdgeNode() {
		return edgeNode;
	}

	public void setEdgeNode(int edgeNode) {
		this.edgeNode = edgeNode;
	}


}
