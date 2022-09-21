package EdgeEPOS.CostComponents;

import java.util.List;

import org.fog.entities.FogDevice;

import EdgeEPOS.PlacementMethods.Method;
import EdgeEPOS.Setting.MobileARservice;
import EdgeEPOS.PlacementMethods.Plan;
import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Utility.Factorial;




/**
 *
 * @author rooyesh
 *
 * This class contains all the functions and Constants that are related to the
 * delay
 */
public class ServiceDelay {

    private double[][] rho; // rho_aj
    
    private double[][] f; // f_aj
    
    private int[] n; // n_j: processing units of fog/cloud node j
    
    private double P0[][]; // P^0 in M/M/c queueing model
    private double PQ[][]; // P^Q in M/M/c queueing model

    public static Double transRate[]; // average transmission rate from IoT nodes to fog node j (*can be measured and shown in paper by trace*)

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
    	
    	f = new double[servicelist.size()][Constants.numFogNodes + Constants.numCloudServers];
        n = new int[Constants.numFogNodes + Constants.numCloudServers];
        rho = new double[servicelist.size()][Constants.numFogNodes + Constants.numCloudServers];
        
        P0 = new double[servicelist.size()][Constants.numFogNodes + Constants.numCloudServers];
        PQ = new double[servicelist.size()][Constants.numFogNodes + Constants.numCloudServers];
        
        for (int j = 0; j < Constants.numFogNodes; j++) {
            n[j] = Constants.numFogCore; // number of processors in a fog node
        }
        for (int k = Constants.numFogNodes-1; k < Constants.numCloudServers + Constants.numFogNodes; k++) {
            n[k] = Constants.numCloudCore; // number of processors in a cloud server
        }
        
        
        
    }

    /**
     * Initializes the queuing delay Constants for all services
     */
    public void initialize(Plan p) {
    	this.p = p;
        for (int a = 0; a < servicelist.size(); a++) {
	         for (int j = 0; j < Constants.numFogNodes + Constants.numCloudServers; j++) {
	            calcServiceFractionFog(a, j);
		        calcRhoFog(a, j);
		        calcP0Fog(a, j);
		        calcPQFog(a, j);
		    }
        }
    }

    
    /**
     * Calculates f (the fraction of service rate that service a obtains at fog/cloud
     * node j) for a service on a fog/cloud node
     *
     * @param a the index of the service
     * @param j the index of the fog/cloud node
     */
    private void calcServiceFractionFog(int a, int j) {
        
        double sum = 0;
        for (int s = 0; s < servicelist.size(); s++) {
        	//note that in this case x must cover cloud nodes too
            sum += (p.x[s][j] * servicelist.get(s).getCpuDemand());//total service rate (sigma (MI per request)) reach the node j
        }
        if (sum == 0) {
        	 f[a][j] = 0;
        } else {
        	 f[a][j] = (p.x[a][j] * servicelist.get(a).getCpuDemand()) / sum;
        }
        
    }

    
    /**
     * Calculates rho for a service on a node (either cloud or fog)
     *
     * @param a index of the service
     * @param j index of the node
     */
    
    private void calcRhoFog(int a, int j) {
        if (p.x[a][j] != 0) {
            rho[a][j] = servicelist.get(a).getRequestPerSec() / (f[a][j] * serverCloudlets.get(j).getHost().getTotalMips());
            //RequestPerSec/(fraction * processing capacity (maximum service rate) of fog node j, in MIPS (800,1300))
            //System.out.print("a "+a+" "+j);
            //System.out.printf(" rho %.5f\n",rho[a][j]);
        } else {
        	rho[a][j] = Double.POSITIVE_INFINITY;
        	//System.out.println("a "+a+" "+j+" pos big "+rho[a][j]);
            
        }
    }

       
    /**
     * calculates PQ (Erlang's C) for a service on a node (either cloud or fog)
     *
     * @param a the index of the service
     * @param j the index of the node
     * @param numServers number of servers
     */
    private void calcPQFog(int a, int j) {
        double d1, d2;
        if (rho[a][j] == Double.POSITIVE_INFINITY) { // this is when f[a][j] = 0 (service is not implemented)
            return;
        } else {
            d1 = (Math.pow(n[j] * rho[a][j], n[j])) / Factorial.fact[n[j]];
            d2 = P0[a][j] / (1 - rho[a][j]);
            PQ[a][j] = d1 * d2;
        }
    }

   

    /**
     * calculates P^0 (Erlang's C) for a service on a (either cloud or fog)
     *
     * @param a the index of the service
     * @param j the index of the node
     * @param numServers number of servers
     */
    private void calcP0Fog(int a, int j) {
        
        double sum = 0;
        double d1, d2;
        if (rho[a][j] == Double.POSITIVE_INFINITY) { // this is when f[a][j] = 0 (service is not implemented)
            return;
        } else {
            for (int c = 0; c <= n[j] - 1; c++) {
                sum = sum + ((Math.pow(n[j] * rho[a][j], c)) / Factorial.fact[c]);
            }
            d1 = ((Math.pow(n[j] * rho[a][j], n[j])) / Factorial.fact[n[j]]);
            d2 = 1 / (1 - rho[a][j]);
            P0[a][j] = 1 / (sum + d1 * d2);
        }
    }

   
    /**
     * Calculates service delay in millisecond.
     *
     * @param a the index of the service
     * @param j the index of the fog node
     */
    public Double[] calcServiceDelay(int a, int j, Double[] transRate, Double[] propagDelay) {
        
    	double proc_time;
        Double[] waitingTimePerService = new Double[transRate.length];
	 	
        proc_time = calcProcTimeMMC(a, j); // MMC
        for (int k = 0; k < transRate.length; k++) {
        	  waitingTimePerService[k] = (2 * propagDelay[k]) + (proc_time) + ((servicelist.get(a).getRequestSize()+servicelist.get(a).getResponseSize())
            		/ transRate[k] * 1000d); // in ms
        	  
				/*
				 * System.out.print("k "+k); System.out.printf(" proc time:%.5f  ",proc_time);
				 * System.out.printf("prop time:%.5f ",propagDelay[k]);
				 * System.out.printf("trans rate:%.5f ",transRate[k]);
				 * System.out.printf("wait time:%.5f\n",waitingTimePerService[k]);
				 */
				 
        }
        return waitingTimePerService;
    }

    

    /**
     * Calculates processing time of a service in a fog/cloud node according to an
     * M/M/c model 
     *
     * @param a the index of the service
     * @param j the index of the fog/cloud node
     * @return returns the processing time of service a in fog/cloud node j, if the
     * queue is stable. Otherwise, it will return a big number.
     */
    private double calcProcTimeMMC(int a, int j) {
        // Checks if Equation 15 is satisfied: check stability constraint 
        if (f[a][j] * serverCloudlets.get(j).getHost().getTotalMips() < servicelist.get(a).getCpuDemand() * servicelist.get(a).getRequestPerSec()) {
            return 20; // (ms) a big number
        }
        return 1 / ((f[a][j] * serverCloudlets.get(j).getHost().getTotalMips()) / n[j]) + 
        		PQ[a][j] / (f[a][j] * serverCloudlets.get(j).getHost().getTotalMips() - 
        				servicelist.get(a).getCpuDemand() * servicelist.get(a).getRequestPerSec());
    }

  
    /**
     * Calculates processing time of a job if the underlying model of the fog
     * node is M/M/1
     *
     * @param arrivalRate total arrival rate of a node
     * @param serviceRate total service rate of a node
     * @return returns the processing time of the job, if arrivalRate is less
     * than serviceRate, otherwise, it will return a big number
     */
    private static double calcProcTimeMM1(double arrivalRate, double serviceRate) {
        double proc_time;
        if (arrivalRate > serviceRate) {
            proc_time = 2000d; //stability constraint
            System.out.println("too much load");
        } else {
            proc_time = 1 / (serviceRate - arrivalRate) * 1000d; // so that it is in ms
        }
        return proc_time;
    }

    /**
     * Set all threshold to a specific value
     *
     * @param threshold
     */
    public void setThresholds(double threshold) {
        for (int a = 0; a < servicelist.size(); a++) {
            Constants.th[a] = threshold;
        }
    }
/*
    
    
     /**
     * Calculates the delay of deployment of a service on a fog node.
     * Deploy delay consists of container download from Fog Service Controller,
     * and container startup time Everything is in ms.
     * 
     * @param a the index of the service
     * @param j the index of the fog node
     */
    /*
    private double calcDeployDelay(int a, int j) {
    	double transRate = 1d * 1000d * 1000d * 1000d;

        return serviceList.get(a).getStorageDemand() / transRate * 1000 + Constants.CONTAINER_INIT_DELAY;
    }
*/
}
