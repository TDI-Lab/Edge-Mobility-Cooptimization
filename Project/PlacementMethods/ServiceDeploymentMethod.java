package EdgeEPOS.PlacementMethods;

import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Utility.ArrayFiller;

/**
 *
 * @author rooyesh
 *
 * This class contains variables that keep the type of the service deployment
 * method and related function
 */
public class ServiceDeploymentMethod {

    public static final int OPTIMAL_SERVICE_OPTIMAL_ROUTE = 1;
    public static final int OPTIMAL_SERVICE_BASE_ROUTE = 2; 
    public static final int BASE_SERVICE_BASE_ROUTE = 3;
    public static final int BASE_SERVICE_OPTIMAL_ROUTE = 4;
    
    public int type; // type of the method (E.g. optimal vs base)
    public int run;
    public Plan GPlan;

    public double averageRateOfTraffic = 0d;
    public Double[][] averageRateOfTrafficPerFogNodePerService; // average rate of traffic that is incoming to a fog node for a service
    public Double[] averageRateOfAggregatedServiceTrafficPerFogNode; // average rate of traffic that is incoming to a fog node for all services

    /**
     * Initializes the Constants in the class
     *
     * @param type type of the method (E.g. optimal vs. Fog Static etc.)
     */
    
    
    
    public ServiceDeploymentMethod(int type, int run, int numServices) {
        this.type = type;
        this.run = run;
        //global response:
        GPlan = new Plan(numServices, Constants.EDGE_ROUTERS, Constants.BACKBONE_ROUTERS);
        //averageRateOfTrafficPerFogNodePerService = new Double[numServices][Constants.numFogNodes];

        
        // initially all services are deployed on cloud servers
        if (run==0) {
            
        	ArrayFiller.fill2DArrayWithConstantNumber(GPlan.x, 0, numServices, 0, Constants.EDGE_ROUTERS + Constants.BACKBONE_ROUTERS,  0);
            //ArrayFiller.fill2DArrayWithConstantNumber(GPlan.x, 0, numServices, Constants.numFogNodes, Constants.numFogNodes+Constants.numCloudServers, 1);
            
        }
        
        
    }

   

}
