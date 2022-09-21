package EdgeEPOS.Setting;

import java.util.HashSet;

import org.fog.vmmobile.constants.Policies;

//import Components.Cost;
import EdgeEPOS.Utility.ArrayFiller;
import EdgeEPOS.Utility.Factorial;
import EdgeEPOS.Utility.RandomGenerator;
//import Utilities.ReverseMap;

//1 290538 0 0 10 11 0 0 0 61
/**
 * @author rooyesh
 * This is a holder class which contains some parameters of the simulation
 */
public class Constants_ann {
	
//CityMap	
	public static final String Cities_BASE_ADDRESS = "G:\\MobFogSim-master\\MobFogSim-master\\src\\EdgeEPOS\\CityMaps\\RoutersCities.csv";
	
	public static int PositionApPolicy;
	public static int PoitionSCPolicy;
	public static int PolicyReplicaVM;
	
	public static final int EPOS_num_Iteration = 40; 
	public static final int EPOS_num_Run = 10;
	public static final String EPOS_Output_Path = "";
	public static final String base_Path = "G:\\MobFogSim-master\\MobFogSim-master\\src\\EdgeEPOS\\";
	
	
	public static final int CoreRouterDistanceModifierAnnapolis = 120; 
	public static final int EdgeRouterDistanceModifierAnnapolis = 9; 
	
	public static final int File_FIXED_AP_LOCATION = 0;
	
	public static final int NumberOfColumninTrace = 10;
	public static final int NumberOfRowPerTrace = 240;

	public static int numSmartThings; // opt 2. DTMC 50 (last:100). threshold 20
    public static int MigPointPolicy;
    public static int MigStrategyPolicy;

	public static int MaxBandwidth;
	public static double LatencyBetweenCloudlets;
	public static double EEG_TRANSMISSION_TIME;
	public static final int EdgeRouters = 5332;
	public static final int CoreRouters = 45;
	public static int TravelPredicTimeForST = 0; // in seconds
	public static int MobilityPrecitionError = 0;// in meters
	public static int NumOfPlans = 20;
	public static double alpha = 0.9;
	public static double Tau = 60; //time interval in second
    public static final int LOWEST_LATENCY = 0;
	public static final int LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET = 1;
	public static final int LOWEST_DIST_BW_SMARTTING_AP = 2;
	public static final int ILP = 3;
	public static final int FIXED_MIGRATION_POINT = 0;
	public static final int SPEED_MIGRATION_POINT = 1;
	public static final int FIXED_AP_LOCATION = 0;
	public static final int RANDOM_AP_LOCATION = 1;
	public static final int MIGRATION_COMPLETE_VM = 0;
	public static final int MIGRATION_CONTAINER_VM = 1;
	public static final int LIVE_MIGRATION = 2;
	public static final int ADD = 0;
	public static final int REMOVE = 1;
	public static final int FIXED_SC_LOCATION = 0;
	public static final int RANDOM_SC_LOCATION = 1;
	
	public static final int AP_COVERAGE = 68;
   // public static final int AP_COVERAGE = 68;//450; for annapolis with about 5700 routers;heavy process then set to 450 with 143 routers currently //Max Ap coverage distance - It should modify
	public static final int CLOUDLET_COVERAGE = 450; //Max cloudlet distance - It should modify
	public static final int MAX_DISTANCE_TO_HANDOFF = 40; //It cannot be less than Max_SPEED
	public static final int MIG_POINT = (int) (MAX_DISTANCE_TO_HANDOFF*1.3);// 0; //Distance from boundary - it should modify
	public static final int LIVE_MIG_POINT = 200;//It can be based on the Network's Bandwidth
	public static final int MAX_HANDOFF_TIME = 1200;
	public static final int MIN_HANDOFF_TIME = 700;
	public static final int MAX_AP_DEVICE = 15;
	public static final int MAX_SMART_THING = 7;
	public static final int MAX_SERVER_CLOUDLET = 10;
	public static final int MAX_X = 6400;
	public static final int MAX_Y = 8600;
	public static final int MIN_X = 493;
	public static final int MIN_Y = 2060;
	public static final int MAX_SPEED = 120;
	public static final int MAX_DIRECTION = 9;
	public static final int MAX_SERVICES = 500;//3
	public static final float MAX_VALUE_SERVICE = 1.1f;
	public static final float MAX_VALUE_AGREE = 70f;
	public static final int MAX_ST_IN_AP = 500;
	public static final int MIN_ST_IN_AP = 0;
	public static final int MAX_SIMULATION_TIME = 1000*60*2500; //30 minutes
	public static final int MAX_VM_SIZE = 128; //200MB
	public static final int MIN_VM_SIZE = 128; //100MB
	public static final int MAX_BANDWIDTH = 10 * 1024;//10Gb 15 * 1024 * 1024;
	public static final int MIN_BANDWIDTH = 54 ;//54mb 5 * 1024 * 1024;
	public static final int DELAY_PROCESS = 500;
	public static final double SIZE_CONTAINER = 0.6;
	public static final double PROCESS_CONTAINER = 1.3;
	 
	public static int numCloudCore = 8;
	public static int numFogCore = 4;
	public static int CONTAINER_INIT_DELAY = 50; // 50 ms
	
	
	
	public static boolean MEASURING_RUNNING_TIME = false; // a boolean that is used for measuring the runtime of the greedy algorithms
    public static int TAU; // time interval between run of the method (s)
    public static int TRAFFIC_CHANGE_INTERVAL; // time interval between run of the method (s)

    public static int numCloudServers; // opt 3. DTMC 25. threshold 3
    public static int numFogNodes; // opt 10. DTMC 100 (last:200). threshold 10
    public static int numServices; // opt 2. DTMC 50 (last:100). threshold 20

    public static double[] ServiceTrafficPercentage; // the percentage of the traffic rate that is associated with a service

    public static double th[]; // threshold
    public static double q[]; // quality of service for service a

    public static double dIF[]; // average propagation delay from IoT nodes to fog node j (*can be measured and shown in paper by trace*)
    public static Double rIF[]; // average transmission rate from IoT nodes to fog node j (*can be measured and shown in paper by trace*)

    public static double dFC[][]; // propagation delay from fog node j to cloud node k (*can be measured and shown in paper by trace*)
    public static double rFC[][]; // average transmission rate from fog node j to cloud node k (*can be measured and shown in paper by trace*)

    public static double l_rq[]; // average request length of service a
    public static double l_rp[]; // average response length of service a

    public static double KP[]; // processing capacity (service rate) of fog node j
    public static double KpP[]; // processing capacity (service rate) of cloud server k

    public static double KM[]; // memory capacity of fog node j, in bytes
    public static double KpM[]; // memory capacity of cloud server k, in bytes

    public static double KS[]; // storage capacity of fog node j, in bytes
    public static double KpS[]; // storage capacity of cloud server k, in bytes

    // (in MIPS) reference: "A Cooperative Fog Approach for Effective Workload Balancing": Each fog network contains three hosts with 1256 (ARM Cortex v5), 1536 (ARM v7) and 847 (ARM11 family) MIPS respectively
    //processing capacity (maximum service rate) of fog node j, in MIPS
    public final static double KP_min = 800d;
    public final static double KP_max = 1300d;

    // reference: "Towards QoS-aware Fog Service Placement" (in the reference simulation, it is can be found 50,100 and 200 for L_P)
    public final static double L_P_max = 200d;
    public final static double L_P_min = 50d;

    
    public static int[][] h; //index of the cloud server to which the traffic for service a is routed from fog node j
    //public static ReverseMap[][] H_inverse; // set of indices of all fog nodes that route the traffic for service a to cloud server k.

    public static double[] L_P; // amount of required processing for service a per unit traffic, in MIPS
    public static double[] L_S; // size of service (i.e. container) a,
    public static double[] L_M; // required amount of memory for service a, in bytes

    public static double globalTraffic[][]; // this is a static version of traffic, which must remain the same
    public static double globalTraffic1[]; // this is a static version of traffic, which must remain the same

    public static Double rFContr[]; // transmission rate from fog node j to the fog service controller

    //private static Cost cost;
    public static double TRAFFIC_NORM_FACTOR;
    
	/**
     * This is the main function of this class. This function will initialize
     * the parameters of the simulation. For the specific values of the
     * parameters please refer to our paper.
     */
    public static void initialize() {
    	
    	PositionApPolicy = Policies.FIXED_AP_LOCATION;
    	PoitionSCPolicy = Policies.FIXED_SC_LOCATION;
    	PolicyReplicaVM = Policies.MIGRATION_COMPLETE_VM;
    	
    	
    	//cost = new Cost(numCloudServers, numFogNodes, numServices);
        Factorial f = new Factorial();
        globalTraffic = new double[numServices][numFogNodes];
        globalTraffic1 = new double[numSmartThings];
        q = new double[numServices];
        ArrayFiller.fill1DArrayRandomlyInRange(q, 0.9, 0.99999); // high QoS requirements

        th = new double[numServices];
        ArrayFiller.fill1DArrayRandomlyInRange(th, 10d, 10d); // 10 ms is the threshold (architectural imperatives is my reference for this)

        dIF = new double[numFogNodes];
        ArrayFiller.fill1DArrayRandomlyInRange(dIF, 1d, 2d); // refer to the paper

        rIF = new Double[numFogNodes];
        if (RandomGenerator.genUniformRandom() < 0.5) {
            ArrayFiller.fill1DArrayWithConstantNumber(rIF, 54d * 1000d * 1000d); // 54 Mbps
        } else {
            ArrayFiller.fill1DArrayWithConstantNumber(rIF, 51.233d * 1000d * 1000d); // 51.233 Mbps (is the "mixed" rate of one 54Mbps and a 1Gbps link)
        }

        dFC = new double[numFogNodes][numCloudServers];
        ArrayFiller.fill2DArrayRandomlyInRange(dFC, 15d, 35d);

        // We assume there are between 6-10 hops lies between fog and cloud. And the links could be 10 Gbps or 100Gbps (up to 2). 
        // 1 is basically the lower bound, when all 10 links are 10Gbps, and that is the "mixed" rate of 10 10Gbps links. 2.38Gbps is also a mixed rate of 4 10Gbps links and 2 100Gbps.
        rFC = new double[numFogNodes][numCloudServers];
        ArrayFiller.fill2DArrayRandomlyInRange(rFC, 1d * 1000d * 1000d * 1000d, 2.38d * 1000d * 1000d * 1000d);

        rFContr = new Double[numFogNodes];
        ArrayFiller.fill1DArrayWithConstantNumber(rFContr, 10d * 1000d * 1000d * 1000d); // transmission rate of fog nodes to Fog Service Controller is 10Gbps.

        //required amount of processing for service a per request (in million instructions per request)
        L_P = new double[numServices];
        ArrayFiller.fill1DArrayRandomlyInRange(L_P, L_P_min, L_P_max); // values explained above

        L_S = new double[numServices];
        ArrayFiller.fill1DArrayRandomlyInRange(L_S, 50d * 1000d * 1000d * 8d, 500d * 1000d * 1000d * 8d); // size of a service is 50-500 MBytes

        L_M = new double[numServices];
        ArrayFiller.fill1DArrayRandomlyInRange(L_M, 2d * 1000d * 1000d * 8d, 400d * 1000d * 1000d * 8d); // required amount of memory for service is 2-400 MBytes

        KP = new double[numFogNodes];
        ArrayFiller.fill1DArrayRandomlyInRange(KP, KP_min, KP_max);
        KpP = new double[numCloudServers];
        ArrayFiller.fill1DArrayRandomlyInRange(KpP, 16000d, 26000d); // in MIPS
        // cloud nodes are selected to be 20 times faster

        KM = new double[numFogNodes];
        ArrayFiller.fill1DArrayRandomlyInRange(KM, 8d * 1000d * 1000d * 1000d * 8d, 8d * 1000d * 1000d * 1000d * 8d); // 8GB

        KpM = new double[numCloudServers];
        ArrayFiller.fill1DArrayRandomlyInRange(KpM, 32d * 1000d * 1000d * 1000d * 8d, 32d * 1000d * 1000d * 1000d * 8d); // 32GB

        KS = new double[numFogNodes];
        ArrayFiller.fill1DArrayRandomlyInRange(KS, 25d * 1000d * 1000d * 1000d * 8d, 25d * 1000d * 1000d * 1000d * 8d); // 25GB

        KpS = new double[numCloudServers];
        ArrayFiller.fill1DArrayRandomlyInRange(KpS, 250d * 1000d * 1000d * 1000d * 8d, 250d * 1000d * 1000d * 1000d * 8d); // 250GB

        // Reference: "The Impact of Mobile Multimedia Applications on Data Center Consolidation"
        // Augmented reality applications
        l_rq = new double[numServices];
        ArrayFiller.fill1DArrayRandomlyInRange(l_rq, 10d * 1000d * 8d, 26d * 1000d * 8d); // the request size 10KB-26KB 

        l_rp = new double[numServices];
        ArrayFiller.fill1DArrayRandomlyInRange(l_rp, 10d * 8d, 20d * 8d); // the request size 10B-20B

        h = new int[numServices][numFogNodes];
        for (int a = 0; a < numServices; a++) {
            for (int j = 0; j < numFogNodes; j++) {
                h[a][j] = (int) (RandomGenerator.genUniformRandom() * numCloudServers);
            }
        }
      /*
        H_inverse = new ReverseMap[numServices][numCloudServers];
        for (int k = 0; k < numCloudServers; k++) {
            for (int a = 0; a < numServices; a++) {
                HashSet<Integer> single_h_reverse = new HashSet<>();
                for (int j = 0; j < numFogNodes; j++) {
                    if (h[a][j] == k) {
                        single_h_reverse.add(j);
                    }
                }
                H_inverse[a][k] = new ReverseMap(single_h_reverse);
            }
        }
*/
        ServiceTrafficPercentage = new double[numServices];
        generateRandomPercentageForServiceTraffic(); // ServiceTrafficPercentage is initialized

        TRAFFIC_NORM_FACTOR = calcTrafficNormFactor();
        
    	
    }

    /**
     * Generates random percentages for traffic rates of the services
     */
    private static void generateRandomPercentageForServiceTraffic() {
        ArrayFiller.fillRandomPDFInArray(ServiceTrafficPercentage);
    }

    /**
     * This method calculates the normalization factor for the incoming traffic
     * to fog nodes. Note that since the number of services and number of fog
     * nodes varies in each experiment, without normalizing the incoming
     * traffic, the traffic may be small or big.
     *
     * @return returns the normalization factor for the incoming traffic to fog
     * nodes.
     */
    private static double calcTrafficNormFactor() {
        double sum = 0;
        for (int a = 0; a < numServices; a++) {
            sum += L_P[a];
        }
        double f_min = L_P_min / sum;
        return f_min * (KP_min / L_P_max);
    }
}
