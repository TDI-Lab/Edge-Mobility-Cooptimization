package EdgeEPOS.PlacementMethods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fog.entities.FogDevice;

import EdgeEPOS.CostComponents.Cost;
import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.MobileARservice;
import EdgeEPOS.TrafficTraces.Traffic;
import EdgeEPOS.Utility.ArrayFiller;
import EdgeEPOS.Utility.Utility;

import experiment.IEPOSExperiment;
import EdgeEPOS.CostComponents.ServiceDelay;

/**
 *
 * @author rooyesh
 *
 * This is the main class of the simulation, that contains different methods
 * that are proposed in the paper (e.g. optimization, greedy algorithms, all
 * cloud)
 */
public class Method {

    protected double[][] backup_lambda_in;

    private DeployedServices fogStaticDeployedContainers; // the number of containers deployed when using Static Fog

    private boolean firstTimeRunDone = false; // a boolean that is used in Static Fog to keep track of the first time that the algorithm should run

    private int numFogNodes;
    private int numServices;
    private int numCloudServers;

    protected int[][] x; // x_aj
    protected int[][] x_backup; // backup of x
    
    public Traffic traffic; // instance of traffic class
    protected ServiceDelay delay; // instance of delay class

    private int type; // type of the method 
    protected ServiceDeploymentMethod scheme;
   // private int hopLevel;
   // private double lambda;
    public int run;

	private int period;
    
    /**
     * Constructor of this class.
     *
     * @param scheme
     * @param numFogNodes
     * @param numServices
     * @param numCloudServers
     */
    public Method(ServiceDeploymentMethod scheme) {

    	//this.hopLevel = Constants.hopLevel;
    	//this.lambda = Constants.lambda;
        this.run = scheme.run;
        
    	traffic = new Traffic();
        
        this.scheme = scheme;
        type = scheme.type;
        x = scheme.GPlan.x;//global response??
        x_backup = scheme.GPlan.x;//??
        
/*
        this.numFogNodes = numFogNodes;
        this.numServices = numServices;
        this.numCloudServers = numCloudServers;

        backup_lambda_in = new double[numServices][numFogNodes];
*/
    }

    /**
     * Runs the corresponding method
     * @param i 
     *
     * @param traceType the type of the trace that is used (refer to Traffic
     * class)
     * @param deployPlanOptOpt 
     * @param isMinViol boolean showing if Min-Viol is running
     * @return returns the number of deployed fog and cloud services
     */
    public Plan run(int i, List <Agent> agents, List<MobileARservice> services, List<FogDevice> serverCloudlets) {
        backupAllPlacements();
        
        if (type == ServiceDeploymentMethod.OPTIMAL_SERVICE_OPTIMAL_ROUTE) {
            //return new DeployedServices(0, numCloudServers * numServices);
        	 return runOptimalOptimal(i, agents, services, serverCloudlets);
        }
        else if (type == ServiceDeploymentMethod.OPTIMAL_SERVICE_BASE_ROUTE) {
            return runOptimalBase();
        } 
        else if (type == ServiceDeploymentMethod.BASE_SERVICE_BASE_ROUTE) { 
        	return runOptimalBase();
        } 
        else { // BASE_SERVICE_OPTIMAL_ROUTE
        	return runOptimalBase();
        }
       
    }

    /**
	* Backs up previous placement at each run
	*
	*/
	private void backupAllPlacements() {
	  for (int a = 0; a < numServices; a++) {
		  for (int j = 0; j < numFogNodes+numCloudServers; j++) {
		      x_backup[a][j] = x[a][j];
		  }
	  }
	}
    /**
     * gets the average cost for a specific time duration
     *
     * @param timeDuration the duration of the time
     */
	/*
    public double getAvgCost(double timeDuration) {
        delay.initialize();
        updateDelayAndViolation();
        return Cost.calcAverageCost(timeDuration, x, xp, x_backup, Vper, Parameters.q, traffic.lambda_in, traffic.lambdap_in, traffic.lambda_out, Parameters.L_P, Parameters.L_S, Parameters.h);
    }
*/
    
    private Plan runOptimalBase() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
     * Runs the optimal placement method, which will update x_aj and xp_ak
     *
     * @return returns the number of deployed fog and cloud services
     */
    private Plan runOptimalOptimal(int period, List <Agent> agents, List<MobileARservice> services, List<FogDevice> serverCloudlets) {

    	Plan [][] plans = new Plan [Constants.EDGE_ROUTERS + Constants.BACKBONE_ROUTERS][Constants.EPOS_NUM_PLANS]; //??cloudnodes??
    	ArrayList<Integer> selectedPlansIndex = new ArrayList<Integer>();// index of selected plans coming from EPOS
    	System.out.println("plan generation............");
        for (int i = 0; i < agents.size(); i++) {
        	plans [i] = agents.get(i).generatePlans(period, x_backup);
        	
        }
                 
    	//runEPOS();
    	EPOSAnswer EPOSa = Utility.getInputFromEpos(selectedPlansIndex, run);
    	return EPOSa.deployEposAnswerToMapping(selectedPlansIndex, plans, agents, services, serverCloudlets);//based on outplans from this period we can easily drive assignments...

        //return DeployedServices.countDeployedServices(numServices, numFogNodes, numCloudServers, x, xp);
    }

 	private static void runEPOS() {
		// TODO Auto-generated method stub
 		
 	    IEPOSExperiment iepos = new IEPOSExperiment();
        iepos.main(null);
    	
		
	}

	
	

//    
//    /**
//     * Gets the average service delay
//     *
//     * @return
//     */
//    public double getAvgServiceDelay() {
//        double sumNum = 0;
//        double sumDenum = 0;
//        double d;
//        for (int a = 0; a < Parameters.numServices; a++) {
//            for (int j = 0; j < Parameters.numFogNodes; j++) {
//                d = delay.calcServiceDelay(a, j) * traffic.lambda_in[a][j];
//                sumNum += d;
//                sumDenum += traffic.lambda_in[a][j];
//            }
//        }
//        return sumNum / sumDenum;
//    }
//
//    /**
//     * Sets the firstTimeRunDone boolean variable false
//     */
//    public void unsetFirstTimeBoolean() {
//        firstTimeRunDone = false;
//    }
//
//    /**
//     * gets the average cost for a specific time duration
//     *
//     * @param timeDuration the duration of the time
//     */
//    public double getAvgCost(double timeDuration) {
//        delay.initialize();
//        updateDelayAndViolation();
//        return Cost.calcAverageCost(timeDuration, x, xp, x_backup, Vper, Parameters.q, traffic.lambda_in, traffic.lambdap_in, traffic.lambda_out, Parameters.L_P, Parameters.L_S, Parameters.h);
//    }
//
//    /**
//     * Prints the current service allocation among fog nodes and cloud servers
//     */
//    public void printAllocation() {
//        System.out.print("Fog");
//        for (int j = 0; j < numFogNodes; j++) {
//            System.out.print("  ");
//        }
//        System.out.println("Cloud");
//        for (int a = 0; a < numServices; a++) {
//            for (int j = 0; j < numFogNodes; j++) {
//                System.out.print(x[a][j] + " ");
//            }
//            System.out.print("   ");
//            for (int k = 0; k < numCloudServers; k++) {
//                System.out.print(xp[a][k] + " ");
//            }
//            System.out.println("");
//        }
//    }
//
// 
//
//    /**
//     * Updates arrays x_aj and xp_ak according to the combination. (we divide
//     * the combination number (its bit string) into 'numServices' chunk (e.g. if
//     * numServices=5, we divide the bit string into 5 chunks). Each chunk
//     * further is divided into 2 parts: form left to right, first part of the
//     * bit string represents the j ('numFogNodes' bits) and the second part
//     * represents the k ('numCloudServers' bits)).
//     *
//     * @param combination
//     */
//    public void updateDecisionVariablesAccordingToCombination(long combination) {
//        long mask = 1;
//        long temp;
//        for (int a = 0; a < numServices; a++) {
//            for (int k = 0; k < numCloudServers; k++) {
//                temp = combination & mask;
//                if (temp == 0) {
//                    xp[a][k] = 0;
//                } else {
//                    xp[a][k] = 1;
//                }
//                mask = mask << 1;
//            }
//
//            for (int j = 0; j < numFogNodes; j++) {
//                temp = combination & mask;
//                if (temp == 0) {
//                    x[a][j] = 0;
//                } else {
//                    x[a][j] = 1;
//                }
//                mask = mask << 1;
//            }
//        }
//    }
//
  


//    /**
//     * Updates average service delay, and violation as well
//     */
//    private void updateDelayAndViolation() {
//        for (int a = 0; a < numServices; a++) {
//            Violation.calcViolation(a, this);
//        }
//    }
//}
}