package EdgeEPOS.CostComponents;

import java.util.List;

import org.cloudbus.cloudsim.NetworkTopology;
import org.fog.entities.ApDevice;
import org.fog.entities.FogDevice;
import org.fog.localization.Distances;

import EdgeEPOS.PlacementMethods.Plan;
import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.MobileARservice;
import EdgeEPOS.Utility.ArrayFiller;


/**
 * @author rooyesh
 *
 */
public class ProcessingCost {
	
	private static double[] UNIT_PROC_COST;
    private static double[] UNIT_STOR_COST;
    private static double[][] COMM_UNIT_COST;
    
    List <MobileARservice> serviceList;
    List<FogDevice> serverCloudlets;
    List<ApDevice> apDevices;
	
	public ProcessingCost(List <MobileARservice> serviceList, List<FogDevice> serverCloudlets, List<ApDevice> apDevices){

		this.serviceList = serviceList;
	    this.serverCloudlets = serverCloudlets;
	    this.apDevices = apDevices;
		
	    UNIT_PROC_COST = new double[Constants.EDGE_ROUTERS + Constants.BACKBONE_ROUTERS];
	    ArrayFiller.fill1DArrayRandomlyInRange(UNIT_PROC_COST, 0, Constants.numFogNodes-1, 0.002d, 0.002d);
		ArrayFiller.fill1DArrayWithConstantNumber(UNIT_PROC_COST, Constants.numFogNodes, Constants.numFogNodes+Constants.numCloudServers, 0.002d);
		
	    UNIT_STOR_COST = new double[Constants.EDGE_ROUTERS + Constants.BACKBONE_ROUTERS];
	    ArrayFiller.fill1DArrayRandomlyInRange(UNIT_STOR_COST, 0, Constants.numFogNodes-1, 0.000000000004d, 0.000000000004d);
		ArrayFiller.fill1DArrayWithConstantNumber(UNIT_STOR_COST, Constants.numFogNodes, Constants.numFogNodes+Constants.numCloudServers, 0.000000000004d);
		
		COMM_UNIT_COST = new double[Constants.EDGE_ROUTERS + Constants.BACKBONE_ROUTERS][Constants.EDGE_ROUTERS + Constants.BACKBONE_ROUTERS];
		ArrayFiller.fill2DArrayWithConstantNumber(COMM_UNIT_COST, 0, Constants.EDGE_ROUTERS, 0, Constants.EDGE_ROUTERS, 0.0000000002d);
		ArrayFiller.fill2DArrayWithConstantNumber(COMM_UNIT_COST, Constants.EDGE_ROUTERS, Constants.EDGE_ROUTERS+Constants.BACKBONE_ROUTERS,
				Constants.EDGE_ROUTERS, Constants.EDGE_ROUTERS+Constants.BACKBONE_ROUTERS, 0.0000000002d);
	    //diameter must be zero???
		for(int i = 0; i<Constants.EDGE_ROUTERS + Constants.BACKBONE_ROUTERS; i++)
			COMM_UNIT_COST[i][i] = 0;
			
		
	}
    
    
	
	
	/**
     * Calculates average the average cost (processing + storage + communication
     * + deployment + violation) for cloud servers and fog nodes for the
     * duration of `time`
     * @param serviceList 
     * @param serverCloudlets 
     * @param p 
	 * @param samplingInterval 
     * @param fogServer 
     *
     * @param time is in minutes
     * @param x the fog service allocation matrix
     * @param xp the cloud service allocation matrix
     * @param x_backup the backup of x
     * @param lambda_in incoming traffic rate from IoT nodes to fog node j for
     * service a (request/second)
     * @param lambdap_in incoming traffic rate to cloud server `k' for service
     * `a` (request/second)
     * @param lambda_out rate of dispatched traffic for service a from fog node
     * j to the associated cloud server (request/second)
     * @param L_P required amount of processing for service `a` per request, (in
     * million instructions per request)
     * @param L_S storage size of service a, in bytes
     * @param h index of the cloud server to which the traffic for service a is
     * routed from fog node j
     */
	public double procCommMonetaryCost(Plan p, int [][] onThePathAccessPoints, int samplingInterval) {
		
		Double totalCost = 0d;
	  
		double time = Constants.TAU;// in second
		int NUM_SERVICES= serviceList.size();
		int NUM_FOG_NODE= Constants.numFogNodes;
		int NUM_CLOUD_NODE= Constants.numCloudServers;
    	double costP, costS, costC, costDep;

        // cost of processing in fog and cloud
        costP = 0;
            for (int a = 0; a < NUM_SERVICES; a++) {
                    costP += costP(time, p.y[a], serviceList.get(a).getCpuDemand(), serviceList.get(a).getRequestPerSec());
                }
            
        costP = costP / NUM_SERVICES;//average cost
        
        // cost of storage in fog and cloud
        costS = 0;
            for (int a = 0; a < NUM_SERVICES; a++) {
                    costS += costS(time, p.y[a], serviceList.get(a).getStorageDemand());
                }
            
        costS = costS / NUM_SERVICES;

        // cost of communication between fog nodes, and fog and cloud nodes
        costC = 0;
            for (int a = 0; a < NUM_SERVICES; a++) {
            	costC += costC(time/samplingInterval, p.y[a], serviceList.get(a).getRequestPerSec(), 
                		serviceList.get(a).getRequestSize(), serviceList.get(a).getResponseSize(),
                		onThePathAccessPoints[a]);
            }
        
        costC = costC / NUM_SERVICES;

        
        // cost of container deployment
        costDep = 0;
        for (int a = 0; a < NUM_SERVICES; a++) {
                if (p.x_backup[a][p.y[a]] == 0) {//previous one??? id service ?? global response ??which cloud server
                    costDep += costDep(p.y[a], serviceList.get(a).getStorageDemand());
                }
        }
        costDep = costDep / NUM_SERVICES;

        
        Double c = (costP + costS + costC + costDep);
        totalCost += c;
        return c;
    }


    
    /**
     * Calculates the cost of processing in a particular cloud server for a
     * particular service for the duration of `time`
     *
     * @param time duration for which the cost of processing in the cloud is
     * being calculated
     * @param j the index of fog node
     * @param a the index of service
     * @param L_P required amount of processing for service `a` per request, (in
     * million instructions per request)
     * @param lambda_in incoming traffic rate to fog node j for service `a`
     * (request/second)
     * @return returns the cost of processing in the cloud for the duration of
     * `time`
     */
    public double costP(double time, int j, double L_P, double lambda_in) {
        return UNIT_PROC_COST[j] * L_P * lambda_in * time;
    }

   
    /**
     * Calculates the cost of storage in a particular cloud server for a
     * particular service for the duration of `time`
     *
     * @param time duration of time
     * @param k the index of cloud server
     * @param a the index of service
     * @param L_S storage size of service a, in bytes
     */
    private double costS(double time, int k, double L_S) {
        return UNIT_STOR_COST[k] * L_S * time;
    }

     /**
     * Calculates the cost of communication between fog nodes
     *
     * @param time duration of time
     * @param j the index of fog node
     * @param a the index of service a
     * @param lambda_out rate of dispatched traffic for service a from fog node
     * j to the host fog server (request/second)
     * @param h index of the fog server to which the traffic for service a is
     * routed from fog node j
     */
    

    public double costC(double time, int j, double lambda, int l_rq, int l_rp, int[]h) {
    	 int k = 0, serverCloudletIndex;
         double cost = 0;
         int mediateApId;
         while(k < h.length) {
         	
         	mediateApId = h[k];//connecting access points to the host node
         	serverCloudletIndex = Distances.theClosestServerCloudletToAp(serverCloudlets, apDevices.get(mediateApId));
     		
         	cost += COMM_UNIT_COST[j][serverCloudletIndex] * lambda * (l_rp + l_rq) * time;
     				k++;
     	}
     	return cost/h.length;
     	
    }

	/**
     * Calculates the cost of deployment of a service on a particular fog node
     *
     * @param j the index of the fog node
     * @param a the index of the service
     * @param L_S storage size of service a, in bytes
     */
    //return L_S[a] * FOG_CONTROLLER_COMM_UNIT_COST[j];
    public double costDep(int j, long storage) {
           	double deployCost = 0;
       		deployCost += storage * COMM_UNIT_COST[j][serverCloudlets.size()-1];//???last index = cloud an assumption
        
       	return deployCost;
       }
    
    /**
     * Calculates the delay of deployment of a service on a fog node.
     * Deploy delay consists of container download from Fog Service Controller,
     * and container startup time Everything is in ms.
     * 
     * @param a the index of the service
     * @param j the index of the fog node
     */
    private void calcDeployDelay(int a, int j) {
        //return Parameters.L_S[a] / Parameters.rFContr[j] * 1000 + Parameters.CONTAINER_INIT_DELAY;
    }
    
    
    }
