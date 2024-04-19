package EdgeEPOS.CostComponents;

import java.util.List;

import fog.entities.FogDevice;


import EdgeEPOS.Setting.MobileARservice;
import EdgeEPOS.PlacementMethods.Plan;
import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Utility.Factorial;


/**
 *
 * @author rooyesh
 * Checks Equations 5-10
 * This class contains all the functions and Constants that are related to the
 * delay
 */
public class ServiceDelay {

    private List <MobileARservice> servicelist;
    private List<FogDevice> serverCloudlets;
    private Plan p;
   
    /**
     *
     * @param method
     */
    public ServiceDelay(List <MobileARservice> servicelist, List<FogDevice> serverCloudlets) {
        
    	this.servicelist = servicelist;
    	this.serverCloudlets = serverCloudlets;  
        
    }

    /**
     * Initializes the class with the input plan
     */
    public void initialize(Plan p) {
    	this.p = p;
    	
    }

    
    /**
     * Calculates f (mi per req/mi per req) (the fraction of service rate that service a obtains at node j) 
     *
     * @param a the index of the service
     * @param j the index of the host
     */
    private double calcServiceFractionFog(int a, int j, double load) {
    	  
       return servicelist.get(a).getCpuDemand() / load;
        
    }

    /**
     * Calculates f (mi per req/mi per req) (the fraction of service rate that service a obtains at node j) 
     *
     * @param a the index of the service
     * @param j the index of the host
     */
    private double calcServiceFractionFogPriori(int a, int j) {
    	  return servicelist.get(a).getCpuDemand() / (p.wlPlan[j] + servicelist.get(a).getCpuDemand());
        
    }


    /**
     * @param a
     * @param j
     * @param f_aj
     * @return service rate of node j (host) for service a (mips/mips*f_aj)
     */
    private double calcRho(int a, int j, double f_aj) {
        double rho_aj;
    	
        rho_aj = servicelist.get(a).getCpuDemand1() / (f_aj * Constants.FP[j]);
        return rho_aj;
    }

    /**
     * calculates P^0_aj (Erlang's C) for a service on a (either cloud or fog) node
     *
     * @param a the index of the service
     * @param j the index of the host node
     *  
     */
    private double calcP0Fog(int a, int j, double rho_aj) {
        
        double sum = 0;
        double d1, d2;
        double P0_aj;
        
            for (int c = 0; c <= Constants.nCore[j] - 1; c++) {
                sum = sum + ((Math.pow(Constants.nCore[j] * rho_aj, c)) / Factorial.fact[c]);
            }
            d1 = ((Math.pow(Constants.nCore[j] * rho_aj, Constants.nCore[j])) / Factorial.fact[Constants.nCore[j]]);
            d2 = 1 / (1 - rho_aj);
            P0_aj = 1 / (sum + d1 * d2);
        
        return P0_aj;
    }

    /**
     * calculates P^Q_aj (Erlang's C) for service a on a node j (either cloud or fog)
     *
     * @param a the index of the service
     * @param j the index of the node
     * @param p0 
     * @param 
     */
    private double calcPQ(int a, int j, double P0_aj, double rho_aj) {
        double d1, d2;
        double PQ_aj;
        
            d1 = (Math.pow(Constants.nCore[j] * rho_aj, Constants.nCore[j])) / Factorial.fact[Constants.nCore[j]];
            d2 = P0_aj / (1 - rho_aj);
            PQ_aj = d1 * d2;
        return PQ_aj;
    }
    /**
     * Calculates service delay for one service on one host in millisecond
     * The delay is calculated considering the propagation delay for every access point located on the route
     * It checks if the node does not have an instance of the service and updates the deployment time accordingly
     * @param a the index of the service
     * @param j the index of the host node
     * @param _backup_deploy 
     * @param intermediate Access Points 
     */
    public void calcServiceDelay(int a, int j, MetricsForVehisFromIntermediateAP intermediateAP, boolean[] _backup_deploy) {
        //System.out.println("	start calculating service delay for service: "+a+" on host: "+j+" num of on the route ap: "+intermediateAP.ConnectedAPs.size()+" waitingTimePerAp size"+intermediateAP.waitingTimePerAp.size());
        double proc_time = 0, intermProcTime = 0;
        
        double f_aj = calcServiceFractionFog(a, j, p.wlPlan[j]);
        double rho = calcRho(a, j, f_aj);
        double p0 = calcP0Fog(a, j, rho);
        double pq = calcPQ(a, j, p0, rho);
         /*
         * second- queueing delay of service a on host node j
         */
        proc_time = calcProcTimeMMC(a, j, f_aj, pq);
        
        for (int k = 0; k < intermediateAP.ConnectedAPs.size(); k++) {
        	
        	if(j < Constants.NUM_EDGE_ROUTERS){//service is deployed on edge
        		
        		intermProcTime = 2 * intermediateAP.propagTime.get(k) + proc_time + 
        			 (servicelist.get(a).getRequestSize()/(Constants.rIFC_Up[k] * 1000000d))+
        			 (servicelist.get(a).getResponseSize()/(intermediateAP.transRatePerAP.get(k) * 1000000d));//in second and bps
        		
        	}
        	else {//service is deployed beyond the edge
        		
        		intermProcTime = 2 * intermediateAP.propagTime.get(k) + proc_time + 
           			 (servicelist.get(a).getRequestSize()/(Constants.rIFC_Up[k] * 1000000d))+
           			 (servicelist.get(a).getResponseSize()/(intermediateAP.transRatePerAP.get(k) * 1000000d))
           			 +
           			 (servicelist.get(a).getRequestSize()/(Constants.rIFC_Up[j] * 1000000d))+
          			 (servicelist.get(a).getResponseSize()/(Constants.rIFC_Down[j] * 1000000d));
          	}
        	
        	
        	/* for global plan:
        	 * if the service is not deployed on this node so far
        	 */
        	if (p.gp == 1)
        		if (!p.deployPlan[j]) {//initially from previous run---> here updated based on this plan
	           		 intermediateAP.setDeployTime(calcDeployDelay(a, j));
	           		 p.deployPlan[j] = true;
           	}
        	else {
		        	if (!_backup_deploy[j]) {//initially from previous run---> here updated based on this plan
		        		 intermediateAP.setDeployTime(calcDeployDelay(a, j));
		        		 _backup_deploy[j] = true;
		        	}
        	}
        	intermediateAP.waitingTimePerAp.add(k, intermProcTime);
			
			
        }
      
    }

    

    /**
     * Calculates processing time for service a on fog/cloud node j according to an
     * M/M/c model 
     *
     * @param a the index of the service
     * @param j the index of the fog/cloud node
     * @param pq_aj 
     * @param f_aj 
     * @return in second the processing time of service a hosted on fog/cloud node j, if the
     * queue is stable. Otherwise, it will return a big number.
     */
    private double calcProcTimeMMC(int a, int j, double f_aj, double pq_aj) {
        
    	if (f_aj == 0) { // if service a is implemented on fog node j, but f_aj = 0, there should be a bug! 
            System.out.println("Debug Please!  service not implemented on the host node"); // this is for debug
            return Double.MAX_VALUE; // a big number
        }
    	
    	if (f_aj * Constants.FP[j] < servicelist.get(a).getCpuDemand1()) {// Checks if stability constraint is satisfied
            return 20; // (ms) a big number
        }
    	
        return 1 / (f_aj * (Constants.FP[j] / Constants.nCore[j])) + 
        		pq_aj/((f_aj * Constants.FP[j]) - servicelist.get(a).getCpuDemand1());
    }

     
     
    /**
     * @param a
     * @param j
     * @param transRate (mbps)
     * Calculates the delay of deployment of a service on a fog node.
     * Deploy delay consists of container download from cloud centre and container startup time.
     * @return second
     */
    private double calcDeployDelay(int a, int j) {
    	
        return (servicelist.get(a).getStorageDemand()*8 / Constants.rIFC_Down[Constants.NUM_BACKBONE_ROUTERS+Constants.NUM_EDGE_ROUTERS-1]) + 
        		(Constants.CONTAINER_INIT_DELAY/1000);//(mbps/mbit) +second
    }

    public void calcServiceDelayPriori(int a, int j, MetricsForVehisFromIntermediateAP intermediateAP, boolean deployed) {
        //System.out.println("	start calculating service delay for service: "+a+" on host: "+j+" num of on the route ap: "+intermediateAP.ConnectedAPs.size()+" waitingTimePerAp size"+intermediateAP.waitingTimePerAp.size());
        double proc_time = 0, intermProcTime = 0;
        
        double f_aj = calcServiceFractionFogPriori(a, j);
        double rho = calcRho(a, j, f_aj);
        double p0 = calcP0Fog(a, j, rho);
        double pq = calcPQ(a, j, p0, rho);
         /*
         * queueing time in second of service a on host node j
         */
        proc_time = calcProcTimeMMC(a, j, f_aj, pq);
        
        for (int k = 0; k < intermediateAP.ConnectedAPs.size(); k++) {
        	
        	if(j < Constants.NUM_EDGE_ROUTERS){//service is deployed on the edge network
        		
        		intermProcTime = 2 * intermediateAP.propagTime.get(k) + proc_time + 
        			 (servicelist.get(a).getRequestSize()/(Constants.rIFC_Up[k] * 1000000d))+
        			 (servicelist.get(a).getResponseSize()/(intermediateAP.transRatePerAP.get(k) * 1000000d));//in second and bps
        		
        	}
        	else {//service is deployed beyond the edge network
        		
        		intermProcTime = 2 * intermediateAP.propagTime.get(k) + proc_time + 
           			 (servicelist.get(a).getRequestSize()/(Constants.rIFC_Up[k] * 1000000d))+
           			 (servicelist.get(a).getResponseSize()/(intermediateAP.transRatePerAP.get(k) * 1000000d))
           			 +
           			 (servicelist.get(a).getRequestSize()/(Constants.rIFC_Up[j] * 1000000d))+
          			 (servicelist.get(a).getResponseSize()/(Constants.rIFC_Down[j] * 1000000d));
          		
    			
        	}
        	
        	
        	/*
        	 * if the service is not deployed on this node so far
        	 */
        	if (!deployed) {//initially from previous run---> here updated based on this plan
        		 intermediateAP.setDeployTime(calcDeployDelay(a, j));
        		 //p.deployPlan[j] = true;check if we need this for gp or other methods???
        	}
        	else
        	{
        		intermediateAP.setDeployTime(0);
        	}
        	
        	 intermediateAP.waitingTimePerAp.add(k, intermProcTime);
        	 //System.out.println("	proptime: "+intermediateAP.propagTime.get(k)+", wt: "+intermediateAP.waitingTimePerAp.get(k) +",pro:"+ proc_time);
             	 
        }
       // System.out.println("	finish calculating service delay for service: "+a+" on host: "+j+" num of on the route ap: "+intermediateAP.ConnectedAPs.size()+" waitingTimePerAp size"+intermediateAP.waitingTimePerAp.size());
  
    }

}
