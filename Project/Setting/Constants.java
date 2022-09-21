package EdgeEPOS.Setting;

import java.io.FileReader;
import java.io.LineNumberReader;

import org.cloudbus.cloudsim.NetworkTopology;
import org.fog.vmmobile.constants.Policies;

import com.google.common.primitives.UnsignedLong;

import EdgeEPOS.City.City;
import EdgeEPOS.Utility.ArrayFiller;
import EdgeEPOS.Utility.Factorial;
import EdgeEPOS.Utility.RandomGenerator;

//1 290538 0 0 10 11 0 0 0 61
/**
 * @author rooyesh
 * This is a holder class which contains some parameters of the simulation
 */
public class Constants {
	
//CityMap public static final String Cities_BASE_ADDRESS = "G:\\MobFogSim-master\\MobFogSim-master\\src\\EdgeEPOS\\CityMaps\\RoutersCities.csv";
	
	public static int PositionApPolicy;
	public static int PoitionSCPolicy;
	public static int PolicyReplicaVM;
	
	public static final int EPOS_NUM_ITERATION = 40; 
	public static final int EPOS_NUM_RUN = 10;
	public static final int  EPOS_NUM_PLANS = 2;
	public static final String EPOS_Output_Path = "";
	
	public static double alpha = 0.8;//the percentage of capacity that can be allocated by each agent
	public static int hopLevel = 3;
	public static double lambda;

	public static int numCloudServers; 
    public static int numFogNodes; 
    public static int numServices; 
    
    public static int numSmartThings; 
    public static String base_Path;
    public static int EDGE_ROUTERS;
    public static int BACKBONE_ROUTERS;
    public static double area;
   
	public static final int MAX_ST_IN_AP = 500;
	public static final int MIN_ST_IN_AP = 1;
	public static final int FIXED_AP_LOCATION = 0;
	public static final int RANDOM_AP_LOCATION = 1;
	public static final int FIXED_SC_LOCATION = 0;
	public static final int RANDOM_SC_LOCATION = 1;
	
	public static int MAX_X;//6500 for test,
	public static int MAX_Y;//8700,
	public static int MIN_X;//493,
	public static int MIN_Y;//2060,

	//mobile-to-base station (uplink) frequencies are lower than base station-to-mobile(downlink) frequencies.
	public static long[] rIFC_Down; // average downink transmission rate 
    public static long[] rIFC_Up;   // average uplink transmission rate  
    public static int MAX_BANDWIDTH = 100 * 1024 * 1024; //100 Mb
 	public static int MIN_BANDWIDTH = 54 * 1024 * 1024 ; //54 Mb: 56623104 // average transmission rate from IoT nodes to fog node j
 	public static long MAX_BANDWIDTH_CLOUD = 100L * 1024L * 1024L * 1024L; //100 GB: 107374182400
 	public static long MAX_BANDWIDTH_CORE = 100L * 1024L * 1024L * 1024L ; //100 Gb: 10737418240  
 	public static long MIN_BANDWIDTH_CORE = 10L * 1024L * 1024L * 1024L ; //10 Gb: 10737418240  
 	public static long UP_MAX_BANDWIDTH = 10 * 1024 * 1024; //10 Mbps;
 	public static long UP_MIN_BANDWIDTH_CORE = 1L * 1024L * 1024L * 1024L ;//1 Gbps;
 	
 	
 	//Propagation time = Distance / propagation speed
	public final static double CopperVelFact = 2*(10^8);//Copper velocity factor in meter per second
	public final static double AirVelFact = 3*(10^8);
	
	//processing capacity (maximum service rate) of fog node j, in MIPS
    public final static double KP_min = 800d;
    public final static double KP_max = 1300d;
	public static double FP[]; // processing capacity (service rate) of fog/cloud nodes
	public static int FM[]; // memory capacity of fog/cloud nodes, in bytes
    public static long FS[]; // storage capacity of fog/cloud nodes, in bytes
 	    
 	 
 	//public static double LatencyBetweenCloudlets = 2ms;
	//the speed light travels through optical fiber : about 200,000 km/s
    //Light in air : 299,702,547 meters per second.
    //More than 90% vehicles driving at 100 km/h have only 7.6 milliseconds RTT in LTE network
 	public static double dIF[]; // average propagation delay from IoT nodes to access point or edge node j 
    public static double dFFC[][]; // propagation delay from fog/cloud node j to fog/cloud node k 
    public static double dAF[]; // propagation delay from access point a to corresponding fog/cloud node f 
    public static double MIN_dFF = 2d;
    public static double MIN_dFC = 15d;//ms
    public static double MAX_dFC = 35d;//ms
 	//System.out.println("Delay between " + sc.getName() + " and " + sc1.getName() + ": "
	//		+ NetworkTopology.getDelay(sc.getId(), sc1.getId()));
	//	System.out.println(sc.getName() + ": " + sc.getDownlinkBandwidth());
    
    public static double TRAFFIC_NORM_FACTOR;
    public static double[] ServiceTrafficPercentage; // the percentage of the traffic rate that is associated with a service
    public static double globalTraffic1[]; // this is a static version of traffic, which must remain the same for all methods

    public static int[] l_rq; // average request length of service a U(10; 26) KB
    public static int[] l_rp; // average response length of service a U(10; 20) B

    public final static double L_P_min = 50d;
    public final static double L_P_max = 200d;
    public static double[] L_P; // amount of required processing for service a per unit traffic, U(50; 200) MI per req
   
    public static int[] L_S; // size of service (i.e. container) a in bytes U(50; 500) MB
    public static int [] L_M; // required amount of memory for service a, in bytes U(2; 400) MB

    //energy model factors for fog nodes
    public static double a = 1;
    public static double b = 1;
    public static double c = 0;
    public static double p_j = 2;
    
    //energy model factors for fog nodes
    public static double A = 1;
    public static double B = 2;
    public static double p_k = 2.5;
    
    public static final int numFogCore = 4;
    public static final int numCloudCore = 8;
	
    public static double th[]; // threshold
    public static double q[]; // quality of service for service a
    public static double penalty[];
    
	public static int TAU = 60; // time interval between run of the method (sec)
	public static int ProfilePeriod = 15;//minute
	public static int SAMPLENUMBERS = 3;//num of points in between each time interval 
    public static int CONTAINER_INIT_DELAY = 50; // 50 ms
	 
    public static int MAX_DISTANCE_TO_HANDOFF = 200; //It cannot be less than Max_SPEED?? half of coverage range
    
	
    
	public static final int NumberOfColumninTrace = 10;
	public static final int NumberOfRowPerTrace = 240;

	public static int MigPointPolicy;
    public static int MigStrategyPolicy;

	//public static int MaxBandwidth;
	public static double IMG_TRANSMISSION_TIME = 10;
	
	public static int TravelPredicTimeForST = 0; // in seconds
	public static int MobilityPrecitionError = 0;// in meters
	public static final int LOWEST_LATENCY = 0;
	public static final int LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET = 1;
	public static final int LOWEST_DIST_BW_SMARTTING_AP = 2;
	public static final int ILP = 3;
	public static final int FIXED_MIGRATION_POINT = 0;
	public static final int SPEED_MIGRATION_POINT = 1;

	public static final int MIGRATION_COMPLETE_VM = 0;
	public static final int MIGRATION_CONTAINER_VM = 1;
	public static final int LIVE_MIGRATION = 2;
	public static final int ADD = 0;
	public static final int REMOVE = 1;
	
	public static int AP_COVERAGE = 500;;
    public static int CLOUDLET_COVERAGE = 500; //Max cloudlet distance - It should modify
	public static final int MIG_POINT = (int) (MAX_DISTANCE_TO_HANDOFF*1.3);// 0; //Distance from boundary - it should modify
	public static final int LIVE_MIG_POINT = 200;//It can be based on the Network's Bandwidth
	public static final int MAX_HANDOFF_TIME = 1200;
	public static final int MIN_HANDOFF_TIME = 700;
	public static final int MAX_AP_DEVICE = 15;
	
	public static final int MAX_SMART_THING = 7;
	public static final int MAX_SERVER_CLOUDLET = 10;

	public static final int MAX_SPEED = 120;
	public static final int MAX_DIRECTION = 9;
	public static final int MAX_SERVICES = 500;//3
	public static final float MAX_VALUE_SERVICE = 1.1f;
	public static final float MAX_VALUE_AGREE = 70f;
	public static final int MAX_SIMULATION_TIME = 1000*60*2500; //30 minutes
	public static final int MAX_VM_SIZE = 128; //200MB
	public static final int MIN_VM_SIZE = 128; //100MB
	public static final int DELAY_PROCESS = 500;
	public static final double SIZE_CONTAINER = 0.6;
	public static final double PROCESS_CONTAINER = 1.3;

	 
	public static boolean MEASURING_RUNNING_TIME = false; // a boolean that is used for measuring the runtime of the greedy algorithms
    public static int TRAFFIC_CHANGE_INTERVAL; // time interval between run of the method (s)
    public static double globalTraffic[][]; // this is a static version of traffic, which must remain the same 
     public static Double rFContr[]; // transmission rate from fog node j to the fog service controller
    //private static Cost cost;
	public static String APFile;
	public static String CRFile;
	public static String LatFile;
    public static String ARLoadFile = "G:\\MobFogSim-master\\MobFogSim-master\\output\\ARLoad\\Services-";
    public static String NetOutFile = "G:\\MobFogSim-master\\MobFogSim-master\\output\\AP\\";
	
	/**
     * This is the main function of this class. This function will initialize
     * the parameters of the simulation. For the specific values of the
     * parameters please refer to our paper.
     */
    public static void initialize() {
    	
    	PositionApPolicy = Policies.FIXED_AP_LOCATION;
    	PoitionSCPolicy = Policies.FIXED_SC_LOCATION;
    	PolicyReplicaVM = Policies.MIGRATION_COMPLETE_VM;
    	
    	 // for fog node is U(800; 1300) MIPS, for cloud server is 20 times that of the fog nodes. 
		 FP = new double[EDGE_ROUTERS + BACKBONE_ROUTERS];
         ArrayFiller.fill1DArrayRandomlyInRange(FP, 0, EDGE_ROUTERS+BACKBONE_ROUTERS-2, KP_min, KP_max);
         ArrayFiller.fill1DArrayRandomlyInRange(FP, EDGE_ROUTERS+BACKBONE_ROUTERS-1, EDGE_ROUTERS+BACKBONE_ROUTERS-1, 16000d, 26000d); // in MIPS
         // cloud nodes are selected to be 20 times faster

         //for fog nodes and the cloud servers is 8 GB (8d* 1000d * 1000d * 1000d * 8d) and 32 GB (32d * 1000d * 1000d * 1000d * 8d), respectively. 
	     FM = new int[EDGE_ROUTERS+BACKBONE_ROUTERS];
         ArrayFiller.fill1DArrayRandomlyInRange(FM, 0, EDGE_ROUTERS+BACKBONE_ROUTERS-2, 8d , 8d); // 8GB
         ArrayFiller.fill1DArrayRandomlyInRange(FM, EDGE_ROUTERS+BACKBONE_ROUTERS-1, EDGE_ROUTERS+BACKBONE_ROUTERS-1, 32d, 32d); // 32GB
         
         //for fog nodes more than 25 GB to host at least 50 services, for cloud servers is 10 times that of the fog nodes.
         FS = new long[EDGE_ROUTERS+BACKBONE_ROUTERS];
         ArrayFiller.fill1DArrayRandomlyInRange(FS, 0, EDGE_ROUTERS+BACKBONE_ROUTERS-2, 25d * 1000d * 1000d * 1000d * 8d, 25d * 1000d * 1000d * 1000d * 8d); // 25GB
         ArrayFiller.fill1DArrayRandomlyInRange(FS, EDGE_ROUTERS+BACKBONE_ROUTERS-1, EDGE_ROUTERS+BACKBONE_ROUTERS-1, 250d * 1000d * 1000d * 1000d * 8d, 250d * 1000d * 1000d * 1000d * 8d); // 250GB

         //transmission rate from/to smart things is 54Mb 
         //https://www.verizon.com/articles/4g-lte-speeds-vs-your-home-network/
         //up 2-5 Mbps, down 5-50 Mbps
         //LTE has a theoretical net bit rate capacity of up to 100 Mbit/s in the downlink and 50 Mbit/s in the uplink if a 20 MHz channel is used
        
         rIFC_Down = new long[BACKBONE_ROUTERS + EDGE_ROUTERS]; //Downlink BW
         if (RandomGenerator.genUniformRandom() < 0.5) {//??is not definite
             ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Down, 0, EDGE_ROUTERS, MIN_BANDWIDTH); // 54 Mbps
         } else {
             ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Down, 0, EDGE_ROUTERS, MAX_BANDWIDTH); // 100 Mbps 
         }
         
         if (RandomGenerator.genUniformRandom() < 0.5) {//??is not definite
             ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Down, EDGE_ROUTERS, BACKBONE_ROUTERS + EDGE_ROUTERS-1, MAX_BANDWIDTH_CORE); // 100 Gbps
         } else {
             ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Down, EDGE_ROUTERS, BACKBONE_ROUTERS + EDGE_ROUTERS-1, MIN_BANDWIDTH_CORE); // 10 Gbps
         }
         ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Down, BACKBONE_ROUTERS+EDGE_ROUTERS-1,BACKBONE_ROUTERS+EDGE_ROUTERS , MAX_BANDWIDTH_CLOUD);
         
         //Uplink BW
         rIFC_Up = new long[BACKBONE_ROUTERS + EDGE_ROUTERS];
         if (RandomGenerator.genUniformRandom() < 0.5) {//??is not definite
             ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Up, 0, EDGE_ROUTERS, MIN_BANDWIDTH); // 54 Mbps
         } else {
             ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Up, 0, EDGE_ROUTERS, UP_MAX_BANDWIDTH); // 10 Mbps 
         }
         
         if (RandomGenerator.genUniformRandom() < 0.5) {//??is not definite
             ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Up, EDGE_ROUTERS, BACKBONE_ROUTERS + EDGE_ROUTERS-1, MIN_BANDWIDTH_CORE); // 10 Gbps
         } else {
             ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Up, EDGE_ROUTERS, BACKBONE_ROUTERS + EDGE_ROUTERS-1, UP_MIN_BANDWIDTH_CORE); // 1 Gbps
         }
         ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Up, BACKBONE_ROUTERS+EDGE_ROUTERS-1,BACKBONE_ROUTERS+EDGE_ROUTERS , MAX_BANDWIDTH_CLOUD);
         
         
         dIF = new double[EDGE_ROUTERS];//delay between access point and end-devices in microsecond
         ArrayFiller.fill1DArrayRandomlyInRange(dIF, 0.1d, 0.6d);

         dAF = new double[BACKBONE_ROUTERS+EDGE_ROUTERS];//delay between fog nodes and associated access points
         ArrayFiller.fill1DArrayRandomlyInRange(dAF, 0d, 1d);

         
         dFFC = new double[BACKBONE_ROUTERS+EDGE_ROUTERS][BACKBONE_ROUTERS+EDGE_ROUTERS];//delay between 
         
         calcDelayBetweenCloudlets();
         //ArrayFiller.fill2DArrayRandomlyInRange(dFFC, 0, numFogNodes, 0, numFogNodes, 2d, 15d);
         //ArrayFiller.fill2DArrayRandomlyInRange(dFFC, numFogNodes, numFogNodes+numCloudServers, 0, numFogNodes+numCloudServers, 15d, 35d);
         //ArrayFiller.fill2DArrayRandomlyInRange(dFFC, 0, numFogNodes, numFogNodes, numFogNodes+numCloudServers, MIN_dFC, MAX_dFC);
         
         globalTraffic1 = new double[numSmartThings];
         ServiceTrafficPercentage = new double[numSmartThings];
         generateRandomPercentageForServiceTraffic(); // ServiceTrafficPercentage is initialized

        // Augmented reality services
        //required amount of processing for service a per request (in million instructions per request)
        L_P = new double[numSmartThings];
        ArrayFiller.fill1DArrayRandomlyInRange(L_P, L_P_min, L_P_max); 

        // size of a service is 50-500 MBytes
        L_S = new int[numSmartThings];
        ArrayFiller.fill1DArrayRandomlyInRange(L_S, 50 , 500 ); 
        
        // required amount of memory for a service is 2-400 MBytes
        L_M = new int[numSmartThings];
        ArrayFiller.fill1DArrayRandomlyInRange(L_M, 2 , 400 ); 
       
        // the request size 10KB-26KB 
        l_rq = new int[numSmartThings];
        ArrayFiller.fill1DArrayRandomlyInRange(l_rq, 10 * 1000 * 8, 26 * 1000 * 8); 
        // the request size 10B-20B
        l_rp = new int[numSmartThings];
        ArrayFiller.fill1DArrayRandomlyInRange(l_rp, 10 * 8, 20 * 8);

        //TRAFFIC_NORM_FACTOR = calcTrafficNormFactor();

        //The level of quality of service of different services is assumed to be a uniform random number between loose= 90% and strict= 99:999%.
        q = new double[numSmartThings];
        ArrayFiller.fill1DArrayRandomlyInRange(q, 0.9, 0.99999); // high QoS requirements

        th = new double[numSmartThings];
        ArrayFiller.fill1DArrayRandomlyInRange(th, 10d, 10d); // 10 ms is the threshold 
       
        //we focus on delay-sensitive fog services, we set the penalty of violating the delay threshold to a random number in U(10; 20) per % per sec
        penalty = new double[numSmartThings];
        ArrayFiller.fill1DArrayRandomlyInRange(penalty, 10d, 20d);
    
        
    	//cost = new Cost(numCloudServers, numFogNodes, numServices);
        Factorial f = new Factorial();
        globalTraffic = new double[numSmartThings][numFogNodes];
         
                
        
    	
    }

    /**
     * Generates random percentages for traffic rates of the services
     */
    private static void generateRandomPercentageForServiceTraffic() {
        ArrayFiller.fillRandomPDFInArray(ServiceTrafficPercentage);
    }

    
    /**A simple test to measure latency is to run a ping. 
	 * This is a network diagnostic tool primarily used to test connectivity between two servers or devices.
	 * To ping a destination server, an Internet Control Message Protocol (ICMP) echo request packet is sent to that server. 
	 * If a connection is available, the destination node responds with an echo reply. Ping calculates the round-trip time of
	 * the data packet's route from its origin to its destination, and vice versa
	 * @param l
	 * @param m
	 */
	private static void calcDelayBetweenCloudlets() {
		String line;
		String CSV_SEPARATOR = ","; // it could be a comma or a semicolon
		double NodesDis;
		int i = 0;
		int j;
		System.out.println("Reading Latencies from file....");
		try {
			
			  LineNumberReader csvReader = new LineNumberReader(new FileReader(LatFile)); 
			  while (((line =  csvReader.readLine()) != null)&&(i < (BACKBONE_ROUTERS+EDGE_ROUTERS - 1))) {
				  String[] data = line.split(CSV_SEPARATOR);
		  		  
		  		  for (j = 0; j<(BACKBONE_ROUTERS+EDGE_ROUTERS - 1); j++){
		  			 	NodesDis = Math.floor(Double.parseDouble(data[j]) * 100) / 100;
						dFFC[i][j] = NodesDis/CopperVelFact;
						dFFC[j][i] = dFFC[i][j];
		  		  }
		  		  i++;
			  }
		  	csvReader.close();
				  
			}
			catch (Exception e) {
			System.out.println("Error in FileReader !!!"); e.printStackTrace(); 
			}
		
		i = BACKBONE_ROUTERS+EDGE_ROUTERS - 1;
		 for (j = 0; j<(BACKBONE_ROUTERS+EDGE_ROUTERS); j++){
			 if (i == j)
				 continue;
			 dFFC[j][i] = RandomGenerator.genUniformRandomBetween(MIN_dFC, MAX_dFC);//15-35
			 dFFC[i][j] = dFFC[j][i];
			}
			
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
/*    
  		private static double calcTrafficNormFactor() {
        double sum = 0;
        for (int a = 0; a < numServices; a++) {
            sum += L_P[a];
        }
        double f_min = L_P_min / sum;
        return f_min * (KP_min / L_P_max);
    }
*/
}
