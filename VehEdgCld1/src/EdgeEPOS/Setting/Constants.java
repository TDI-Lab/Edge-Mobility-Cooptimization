package EdgeEPOS.Setting;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

import org.fog.localization.Coordinate;

import EdgeEPOS.Utility.ArrayFiller;
import EdgeEPOS.Utility.Factorial;
import EdgeEPOS.Utility.RandomGenerator;
import EdgeEPOS.Utility.Utility;
import fog.entities.MobileDevice;

//import org.fog.vmmobile.constants.Policies;



//1 290538 0 0 10 11 0 0 0 61
/**
 * @author rooyesh
 * This is a holder class which contains some parameters of the simulation
 */
public class Constants {
	
	public static boolean win = false;
	
	//public static String OUT_BASEADDRESS_WIN = "C:\\Users\\znb_n\\eclipse-workspace\\VehEdgCld1\\PlansAndCosts\\Settings\\";
	//public static String IN_BASEADDRESS_WIN = "C:\\Users\\znb_n\\eclipse-workspace\\VehEdgCld1\\src\\EdgeEPOS\\Setting\\input\\";
	//public static String EPOS_WIN =  "C:\\Users\\znb_n\\eclipse-workspace\\VehEdgCld1\\PlansAndCosts\\";
			
	//public static String IN_BASEADDRESS_LIN = "/home/zeinab/sumo-manos/VehEdgCld1/src/EdgeEPOS/Setting/input/";
	//public static String OUT_BASEADDRESS_LIN = "/home/zeinab/sumo-manos/VehEdgCld1/PlansAndCosts/Settings/";
	//public static String EPOS_LIN = "/home/zeinab/sumo-manos/VehEdgCld1/PlansAndCosts/";
	
	static String projectRoot = System.getProperty("user.dir") + File.separator;
	static String OUT_BASEADDRESS = projectRoot + "PlansAndCosts" + File.separator + "Settings" + File.separator;
	static String IN_BASEADDRESS = projectRoot + "src" + File.separator + "EdgeEPOS" + File.separator + "Setting" + File.separator + "input" + File.separator;
	static String EPOS = projectRoot + "PlansAndCosts" + File.separator;

	public static String EPOS_DatasetAddress, APFile, CRFile, LatFile, ARLoadFile, NetOutFile, cityconfigfile, CoreRouterLocation;
	public static String WorkloadFile, PlanBinaryDataset, PlanUtilDataset, EPOSAnswer, ReqRes, BetaConfTempPath, BetaConfBasePath;
	public static String methodOutput, EPOSAnswerOverall, mobilityDatasetAddress;
	public static String [] mobilityDataset;

//************************************************************
//					from JSON
//************************************************************
	public static int MAX_X;//15577 
	public static int MAX_Y;//15725,
	public static int MIN_X;//13409,
	public static int MIN_Y;//14284,
	public static int NUM_EDGE_ROUTERS; 
    public static int NUM_BACKBONE_ROUTERS; //5
    public static double AREA;
	
//************************************************************
//					Simulation Configuration parameters
//************************************************************
    
    public static int numTimeStamps = 10800;
    /**
     * number of components are counted in local cost, currently 9 components: 
     */
    public static int[] APPLIEDCOST;
    
    public static int numNodes;
    public static int TAU = 300; 						//5 min time interval between successive run of our method/EPOS (in sec), number of vehicles change but the workload is fixed
   	public static int TRAFFIC_CHANGE_INTERVAL = 15; 	//time interval between run of the method (min)
    public static int EPOS_RUN_INTERVAL = 5;			//min
    public static int runNum = 35;
    public static int numPlacementMethod = 6;
    public static int PROFILE_NUM = 12;					//for 3 hours  ts = 10800 (15 min periods)
    public static int EposRunPerWorkload = 3; 			//Constants.totalEPOSRun = EposRunPerWorkload * PROFILES.size();
    
    public static short[][] VehiclesToAPDefault;
    public static short[][] VehiclesToAPOptimized;
    public static int mobilProf = 2;
	
    public static int TRAFFIC_DIST_METHOD = 0;			//0= random distribution, 1 = beta
    public static String TRAFFIC_DIST_METHOD_NAME = "random";
    
    //public static double cpuUtilRatio = 0.05;			//the percentage of capacity that can be allocated by each agent
    //public static double memUtilRatio = 0.05;
    //public static double stoUtilRatio = 0.05;
    
    public static double cpuUtilRatio = 1;			//0.9;the percentage of capacity that can be allocated by each agent
    public static double memUtilRatio = 1;
    public static double stoUtilRatio = 1;
    public static int hopLevel = 3;  					//the permitted distance between access point and a host to become a candidate for deploying a service 
    
//************************************************************
//	EPOS Config params
//************************************************************
    public static String gcFunc ="VAR";
    public static boolean target = false;
    //not used now:
	public static boolean onoff = false;
	public static boolean capacityfactor = false;

	//public static String constraint ="HARD_PLANS";

    public static int EPOS_NUM_SIMULATION = 10;//50;
	public static int EPOS_NUM_ITERATION = 40;//40
	public static int EPOS_NUM_AGENT = 109;
	public static int EPOS_PLAN_DIM = 228; 
	
	/**
	 * 0.025...
	 */
	public static double [] BetaConfig = {0.0,0.05,0.1,0.15,0.2,0.25,0.3,0.35,0.4,0.45,0.5,0.55,0.6,0.65,0.7,0.75,0.8,0.85,0.9,0.95,1.0};//beta values as input for I-EPOS in the range [0,1] 
    public static int betaConfigSize = BetaConfig.length;//later will be set by the length of betaConfig
    public static int EPOS_NUM_PLANS = 20;//vary, for hard constraint 20
	public static int [] PlanConfig = {20};//,0.2,0.3,0.4,0.5,0.6,0.7,0.8,0.9,1.0};
    public static int PlanConfigSize = PlanConfig.length;//later will be set by the length of betaConfig
	
//************************************************************

    /**
   	 * 50 Millisecond
   	 */
   	public static int CONTAINER_INIT_DELAY = 50;
   	public static int MAX_ST_IN_AP = 500;
	public static int MIN_ST_IN_AP = 0;	//***
	public static int FIXED_AP_LOCATION = 0;
	public static int RANDOM_AP_LOCATION = 1;
	public static int FIXED_SC_LOCATION = 0;
	public static int RANDOM_SC_LOCATION = 1;
	
	public static int PositionApPolicy;
	public static int PoitionSCPolicy;
	public static int PolicyReplicaVM;
	public static int MAX_DISTANCE_TO_HANDOFF = 200; //It cannot be less than Max_SPEED?? half of coverage range
    public static int MAX_SPEED = 120;
	//ARVE: Augmented Reality Applications in Vehicle to Edge Networks: base stations have a coverage radius larger than 3000m, comparable to macrocell. 
	//ifogsim coverage range of APs and Cloudlet : 2000
	//https://community.opencellid.org/t/answered-how-to-understand-interpret-use-opencellid-database/452
	//opencellid: Macrocell. in macro-cellular networks cell sizes usually range from 1 to 20 km. 
	//papers 150-200 meters for eua: https://github.com/swinedge/eua-dataset
			
	public static int AP_COVERAGE ;//check 500-3000
    public static int CLOUDLET_COVERAGE ; //Max cloudlet distance - It should modify
	public static int MIG_POINT = (int) (MAX_DISTANCE_TO_HANDOFF*1.3);// 0; //Distance from boundary - it should modify
	public static int LIVE_MIG_POINT = 200;//It can be based on the Network's Bandwidth
	
		
//************************************************************
//					BandWidth and propagation delay
//************************************************************
	// https://aws.amazon.com/ec2/pricing/on-demand/
	// https://www.lrz.de/services/netz/mwn-ueberblick_en/
	//Downlink is a telecommunication term pertaining to data which is sent out or downwards 
	//from a higher level or portion of a network.
	//The uplink frequency is higher than that of the down.
    //mobile-to-base station (up-link) frequencies are lower than base station-to-mobile (down-link) frequencies.
	//transmission delay vs. propagation delay:
	//Transmission delay is the time needed to put the entire packet on the link and is dependent on the length of 
	//the packet, while the propagation delay is needed time for one bit to reach to the other end of the link.

	/**
	 * Mbps-average down-link transmission rate: [72 Mbps, 10/100 Gbps, 100 Gbps]
	 */
	public static long[] rIFC_Down; 
	
	/**
	 * Mbps-average up-link transmission rate: [12 Mbps, 1/10 Gbps, 100 Gbps] 
	 */
	public static long[] rIFC_Up; 
	
    /**
     * 72Mbps
     */
    public static int DL_LTE_BANDWIDTH = 72; 
 	/**
 	 * 12 Mbps
 	 */
 	public static int UL_LTE_BANDWIDTH = 12 ; //56623104 
 	/**
 	 * Mbit, 100 Gbps: 10737418240  
 	 */
 	public static long MAX_DL_BANDWIDTH_CORE = 100L * 1000L; 
 	/**
 	 * Mbit, 10 Gbps:10L * 1024L * 1024L * 1024L * 8; 10737418240
 	 */
 	public static long MIN_DL_BANDWIDTH_CORE = 10L * 1000L;  
 	/**
 	 * 1 Gbps
 	 */
 	public static long MIN_UL_BANDWIDTH_CORE = 1L * 1000L;//Mbit
 	/**
 	 * 10 Gbps
 	 */
 	public static long UL_BANDWIDTH_CLOUD = 10L * 1000L; //Mbit
 	/**
 	 * 100 Gbps
 	 */
 	public static long DL_BANDWIDTH_CLOUD = 100L * 1000L; //Mbit, 100 Gbps: 107374182400
 	
 	/**
 	 * microsecond-delay between IoT nodes to access points, measured based on the distance and light speed.
 	 * uplink transmission delay or uplink round-trip time (RTT), refers to the time it takes for data to travel from a user's device (e.g., a mobile phone) to the base station and back again.
 	 */
 	public static double dIF[]; // average propagation delay from IoT nodes to access points in microsecond U(0.1d, 0.6d)
    /**
     * microsecond-delay between ap and connected cloudlet
     */
    public static double dAF[]; // propagation delay between access points and associated fog nodes in microsecond U(0d, 1d)
    //Propagation time = Distance / propagation speed. Light in a vacuum travels at 299,792,458 meters per second, which equates to a latency of 3.33 microseconds per kilometer (km) of path length.
	/**
	 * microseconds per km for fiber medium
	 */
	public final static double disToLatFiberFactor = 3.33 ;
	public final static double CopperVelFact = 2*(100000000);//Copper velocity factor in meter per second
	public final static double AirVelFact = 3*(100000000);
	public final static double microToSec = 1000000;
	
	/**
	 * second- currently fibre (propagation delay from fog/cloud node j to fog/cloud node k through wired/fibre based on physical distance)
	 */
	public static double dFFC[][];
	
    /**
     * second - up link latency to every fog server in the network + cloud center
     * upload latency, in a network refers to the delay or the amount of time it takes for data to travel from a user's device to the destination server
     */
    public static double upLinkLat[];
     
    /**
   	 * second - unit of hop latency 
   	 */
   	public static double BaseHopDelay;
       
    /**
     * ms, propagation delay between cloud and other cloudlets
     */
    public static double MIN_dFC = 15d;//ms for cloud node: second- 15-35 [fogplan]
    public static double MAX_dFC = 35d;//ms
 	
   
//************************************************************
//					energy/co2 factor costs
//************************************************************
    public static double kwh2ws = 3600000;
    
    /**
     * ws, 317.42 USD per MWh
     */
    public static final double RNCOST = 0.317/kwh2ws;
    /**
	 * 0.905 USD per kWh
	 */
	public static double NRNElectricityCost = 0.905/kwh2ws;// 25*10^-8--->300 -->
	
   	public static final double PUE = 1.2;// energy efficiency factor for amazon, google: 1.1
	/**
	 * USD per kg of co2 emitted
	 * 18.64$ per tonne
	 */
	public static final double Cco2 = (18.64)/1000;
	/**
	 * 0.380 kg CO2eq/kWh --> ws
	 * The carbon intensity of Germany's power sector increased by 5.5 percent in 2022, to 385 grams of carbon dioxide per kilowatt-hour (gCO₂/KWh) of electricity generated.
	 */
	public static final double R = 0.380/(3600000);
	
	/**
	* USD cost of EC2 instance SLA violation per second
	*/
	public static double SLAPenaltyCredit = 0.0058/3600;//EC2 instance: t2.nano	On-Demand hourly credit: $0.0058	vCPU: 1	 Memory: 0.5 GiB
	
//************************************************************
//					Machines and Network equipments
//************************************************************ 
    
    /**
     * returns total mips cap
     * processing capacity (service rate) (in MIPS) for a fog/cloud node 
     */
    public static double FP[];
    public static double FP_back[];
    public static short[] nCore;
	/**
     * returns MegaByte
       memory capacity of fog/cloud nodes, in bytes: for fog nodes and the cloud servers is 8 GB (8d* 1000d * 1000d * 1000d * 8d) and 32 GB (32d * 1000d * 1000d * 1000d * 8d), respectively. 
     */
    public static int FM[]; 
    public static int FM_back[]; 
    
    /**
     * returns MegaByte
     * storage capacity of fog/cloud nodes, in bytes: for fog nodes more than 25 GB to host at least 50 services, for cloud servers is 10 times that of the fog nodes.
     */
    public static long FS[];
    public static long FS_back[];
    public static long nj2j = 1000000000L;
    /**
     * power in nj/bit, edge router + access point
     */
    public static double ul_access_net_energycons = 12437; //nj--->j;12400+37	
    public static double dl_access_net_energycons = 82857; //nj--->j;82820+37	
    
    /**
     * nj/bit, only core router
     */
    public static double core_net_energycons = 12.6; 
    
    /**
     * nj/bit, edge router + switch
     */
    public static double cloud_net_energycons =(37+31.7);		
	
    /**
     * active power in watt
     */
    public static double SC_MaxPow[]; 
 	/**
 	 * idle power in watt
 	 */
 	public static double SC_IdlePow[];
 	
 	public static double SC_RenewablePortion[];//Random (0, 1)
 	//private static double AP_Eg_StorFor = 14 /(1000000000); // store and forward energy consumption joul per byte 
   	//private static double AP_Eg_Proc = 1300 /(1000000000);  // process energy consumption in joul per packet
   	
 	/**
   	 * uplink energy in nj/bit: [LTE+edge router/core router/switch+edge router]
   	 */
   	public static double Net_Pow_ul[]; 
 	
   	/**
   	 * downlink energy in nj/bit: [LTE+edge router/core router/switch+edge router]
   	 */
   	public static double Net_Pow_dl[]; 
 	
   	/**
	 * per 1 million requests
	 */
	public static double[] UNIT_PROC_COST;
	
    /**
     * Cost storage per GB-month
     */
    public static double[] UNIT_STOR_COST;
    
    /**
     * per MB per 1ms
     */
    public static double [] UNIT_MEM_COST;
    
    /**
     * cost of comm per GB in each direction, Symmetric matrix
     */
    public static double[][] UNIT_COMM_COST;
    
	
//************************************************************
//						Extra??
//************************************************************
    public static int MAX_DIRECTION = 9;
	/*
	 * public static final int LOWEST_LATENCY = 0; public static final int
	 * LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET = 1; public static final int
	 * LOWEST_DIST_BW_SMARTTING_AP = 2; public static final int ILP = 3; public
	 * static final int FIXED_MIGRATION_POINT = 0; public static final int
	 * SPEED_MIGRATION_POINT = 1; public static final int MIGRATION_COMPLETE_VM = 0;
	 * public static final int MIGRATION_CONTAINER_VM = 1; public static final int
	 * LIVE_MIGRATION = 2; public static final int ADD = 0; public static final int
	 * REMOVE = 1;
	 * 
	 * 
	 * public static final int MAX_DIRECTION = 9; public static final int
	 * MAX_SERVICES = 500;//3 public static final float MAX_VALUE_SERVICE = 1.1f;
	 * public static final float MAX_VALUE_AGREE = 70f; public static final int
	 * MAX_SIMULATION_TIME = 1000*60*2500; //30 minutes public static final int
	 * MAX_VM_SIZE = 128; //200MB public static final int MIN_VM_SIZE = 128; //100MB
	 * public static final int DELAY_PROCESS = 500; public static final double
	 * SIZE_CONTAINER = 0.6; public static final double PROCESS_CONTAINER = 1.3;
	 */

	
	  
    /**
     * This is the main function of this class which initializes
     * the parameters of the simulation. For the specific values of the
     * parameters please refer to our paper.
     *
     *
     * @param nUM_SMART_THINGS 
     * 
     */
    public static void initialize(int[] nUM_SMART_THINGS) {
    	
    	
    	mobilityDataset = new String[nUM_SMART_THINGS.length];
    	String[] mobilityDataset = new String[2];
    	
    	//mobilityDatasetAddress = projectRoot + "mobilityprofiles" + File.separator;
    	mobilityDataset[0] = "/home/zeinab/sumo-manos/10800/Default/700/output/vehiclesWithAP/";
        mobilityDataset[1] = "/home/zeinab/sumo-manos/10800/Analytics/700/output/vehiclesWithAP/";
        
    	EPOS_DatasetAddress = projectRoot + "datasets" + File.separator;
    	BetaConfBasePath = projectRoot + "conf" + File.separator;
    	BetaConfTempPath = BetaConfBasePath + "ConfFiles109" + File.separator;
    	OUT_BASEADDRESS = projectRoot + "PlansAndCosts" + File.separator + "Settings" + File.separator;
    	EPOS = projectRoot + "PlansAndCosts" + File.separator;
    	methodOutput = EPOS + "Methods" + File.separator;
    	
    	PlanBinaryDataset = EPOS_DatasetAddress + "Binary" + File.separator;
    	PlanUtilDataset = EPOS_DatasetAddress + "Utilization" + File.separator;

    	NetOutFile = OUT_BASEADDRESS + "AP-Server" + File.separator;
    	ARLoadFile = OUT_BASEADDRESS + "ARLoad" + File.separator + "Services-";
    	EPOSAnswer = EPOS + "EPOSAnswer" + File.separator;
    	ReqRes = EPOS + "ReqRes" + File.separator;
    	EPOSAnswerOverall = EPOSAnswer + "overall" + File.separator;
    	
		
    	
    	/*
    	 * number of components contributed to the local cost, currently 9 components + 1 total
    	 */
    	APPLIEDCOST = new int[10]; 
    	Arrays.fill(APPLIEDCOST, 1);  	//DlViolCost, ProcCost, StorCost, MemoryCost, CommCost, DeplCost, EnergyCost, Co2EmitCost, assToCloud [8], TotalLocalCost [9].	
    	//APPLIEDCOST[0] = 2;
    	APPLIEDCOST[8] = 0;				//asstocloud
    	//APPLIEDCOST[5] = 0;  			//deploy cost
    	
    	readMobilityProfiles(nUM_SMART_THINGS);
    	
    	EPOS_PLAN_DIM = 2 * (NUM_EDGE_ROUTERS + NUM_BACKBONE_ROUTERS);
    	PositionApPolicy = FIXED_AP_LOCATION;
    	PoitionSCPolicy = FIXED_SC_LOCATION;
    	
    	/*
    	 * %MIPS=millions of instructions per second FLOPS= floating point operations per second
			%If we assume one instruction requires 10 floating point operation, 1 MIPS ~ 10 million FLOPS
			%1<number of ins per clock cycle per core <4
			%performance in GFlops = (CPU speed in GHz) x (number of cores per CPU chips) x (CPU instruction per cycle) x (number of CPUs per node)
			%MIPS = (Processor clock speed in hz * Num Instructions executed per cycle)*cores/(10^6)
     	 * 
    	 * https://aws.amazon.com/blogs/aws/now-available-new-c5-instance-sizes-and-bare-metal-instances/, https://medium.com/teads-engineering/estimating-aws-ec2-instances-power-consumption-c9745e347959, 
			https://www.intel.co.uk/content/www/uk/en/support/articles/000005755/processors.html, 
			https://www.intel.com/content/www/us/en/develop/documentation/vtune-help/top/reference/cpu-metrics-reference.html
    	 * 
    	 * https://www.spec.org/power_ssj2008/results/res2018q3/power_ssj2008-20180630-00825.html
    	 * https://www.spec.org/power_ssj2008/results/res2020q3/power_ssj2008-20200828-01043.html
    	 * https://www.spec.org/power_ssj2008/results/res2018q4/power_ssj2008-20181009-00863.html
    	 * https://www.spec.org/power_ssj2008/results/res2019q2/power_ssj2008-20190312-00899.html
    	 * https://www.spec.org/power_ssj2008/results/res2020q4/power_ssj2008-20201103-01057.html
    	 * https://ark.intel.com/content/www/us/en/ark/products/75277/intelxeon-processor-e5-2680-v2-25m-cache-2-80-ghz.html
    	 */
    	
    	/*
    	Machine model&Memory (GB)&Performance (GFLOPS)&Storage&Idle power (Watt)&Max power (Watt)&Inc Power (watt per Mips)\\
    	Xeon Gold 6140 2chips 36cores 2.3GHz&192&864&120&52.4&343&-
    	864 = 36*2.6 * 9.2(ipc)
    	861000 mips= 2.6*9.2*36*10^3
    	18 cores/chip
    	
    	Xeon Gold 6136 2chips 24cores 3.0GHz&196&806.4&292&131&432&-
    	806.4 = 24*3  11.2
    	806000 mips = 3.0*11.2*24*10^3
    	
    	Xeon Platinum 8180 2chips 56cores 2.5GHz&192&1523.2&480&48&385&-
    	1523.2 = 2*56*2.5  5.44
    	761000 mips = 2.5*5.44*56
    	
    	Xeon Platinum 8280 2chips 56cores 2.7GHz&192&1612.8&240&64.2&435&-
    	1612.8 = 2*56*2.7   5.2 
    	786000 mips = 2.7*5.2*56
    	
    	Xeon Platinum 8380HL 4chips 112cores 2.9GHz&384&1792&480&44.6&502&
    	1792 = 2*112*2.9    2.72
    	883000 mips = 2.9*2.72*112
    	 */

    	double[] idlePow = {52.4,131,48,64.2,44.6};//power in watt
    	int[] maxPow = {343,432,385,435,502};//power in watt
    	int[] P = {861000,806000,761000,786000,883000};//total processing power in mips
    	short[] NP = {36, 24, 56, 56, 112};//total cpu cores
    	int[] M = {192000,196000,192000,192000,384000};//memory in MB
    	int[] S = {120000,292000,480000,240000,480000};//storage in MB

    	FP = new double[NUM_EDGE_ROUTERS + NUM_BACKBONE_ROUTERS];
    	
    	nCore = new short [NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS];
    	
    	FM = new int[NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS];
        
    	FS = new long[NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS];
         
        SC_MaxPow  = new double[NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS];
        SC_IdlePow = new double[NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS];

        //randomly (uniformly) selects from edge nodes characteristics:
     	ArrayFiller.fill1DArrayWithArrays(FP, FM, FS, nCore, SC_MaxPow, SC_IdlePow, 0, NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS-2, P, M, S, NP, idlePow, maxPow);
        
        //Cloud center: Xeon E5-2680 10cores 2.80 GHz&768&112000MIPS&unlimited&57&115&0.000518
    	FP[NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS-1] = 700000;//real:112000; //max_total_load per run = 605986.33
    	FM[NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS-1] = 768000; //max_total_load per run = 221121	
    	FS[NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS-1] = 500000;//max_total_load per run = 306413	Theoretically unlimited
    	nCore[NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS-1] = 10;
    	SC_MaxPow[NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS-1] = 115;
    	SC_IdlePow[NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS-1] = 57;
    	//Fugaku had a peak processing power (Rpeak) of over 442 petaflops (quadrillions of floating-point operations per second).
    	
    	/*	
    	   Networking Device type&Idle power (Watt)&Max power (Watt)&Max Traffic capacity (Gbps) DL&Max Traffic capacity (Gbps) UL&Energy inc (nJ/bit) DL&Energy inc (nJ/bit) UL\\
    	   3-sector 2×2 MIMO LTE base station&333&528&0.072,0.012&82820&12400\\
    	   Cisco edge router 7609&4095&4550&560&560&37&37\\
    	   Cisco core router CRS-3&11070&12300&4480&4480&12.6&12.6\\
    	   Cisco ethernet switch Catalyst 6509&1589&1766&256&256&31.7&31.7\\
    	 */
    	
    	Net_Pow_ul = new double[NUM_EDGE_ROUTERS + NUM_BACKBONE_ROUTERS];
     	//ArrayFiller.fill1DArrayWithConstantNumber(Switch_Pow_Proc, 0, EDGE_ROUTERS+BACKBONE_ROUTERS, AP_Eg_Proc); 
    	ArrayFiller.fill1DArrayWithConstantNumber(Net_Pow_ul, 0, NUM_EDGE_ROUTERS, ul_access_net_energycons); 
     	ArrayFiller.fill1DArrayWithConstantNumber(Net_Pow_ul, NUM_EDGE_ROUTERS, NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS-1, core_net_energycons); 
     	ArrayFiller.fill1DArrayWithConstantNumber(Net_Pow_ul, NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS-1, NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS, cloud_net_energycons); 
    	 
     	Net_Pow_dl = new double[NUM_EDGE_ROUTERS + NUM_BACKBONE_ROUTERS];
     	//ArrayFiller.fill1DArrayWithConstantNumber(Switch_Pow_Proc, 0, EDGE_ROUTERS+BACKBONE_ROUTERS, AP_Eg_Proc); 
    	ArrayFiller.fill1DArrayWithConstantNumber(Net_Pow_dl, 0, NUM_EDGE_ROUTERS, dl_access_net_energycons); 
     	ArrayFiller.fill1DArrayWithConstantNumber(Net_Pow_dl, NUM_EDGE_ROUTERS, NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS-1, core_net_energycons); 
     	ArrayFiller.fill1DArrayWithConstantNumber(Net_Pow_dl, NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS-1, NUM_EDGE_ROUTERS+NUM_BACKBONE_ROUTERS, cloud_net_energycons); 
    	
     	
     	//transmission rate from/to: 
        //https://en.wikipedia.org/wiki/4G: LTE has a theoretical net bit rate capacity of up to 100 Mbit/s in the downlink and 50 Mbit/s in the uplink if a 20 MHz channel is used
     	//The first LTE devices support up to 100 Mbps while the network capability is up to 150 Mbps. The average data rates in the commercial networks range between 20 and 40 Mbps in downlink and 10–20 Mbps in uplink with 20 MHz bandwidth.
         
     	 //Mbit Downlink BW:[72 Mbps, 10/100 Gbps, 100 Gbps]
         rIFC_Down = new long[NUM_BACKBONE_ROUTERS + NUM_EDGE_ROUTERS]; 
             ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Down, 0, NUM_EDGE_ROUTERS, DL_LTE_BANDWIDTH);  
         if (RandomGenerator.genUniformRandom() < 0.5) {
             ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Down, NUM_EDGE_ROUTERS, NUM_BACKBONE_ROUTERS + NUM_EDGE_ROUTERS-1, MAX_DL_BANDWIDTH_CORE); // 100 Gbps--> bit per sec
         } 
         else {
             ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Down, NUM_EDGE_ROUTERS, NUM_BACKBONE_ROUTERS + NUM_EDGE_ROUTERS-1, MIN_DL_BANDWIDTH_CORE); // 10 Gbps--> bit per sec
         }
         ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Down, NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS-1,NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS , DL_BANDWIDTH_CLOUD);//--> bit per sec
         
         
         
         //Mbit Uplink BW:[12 Mbps, 1/10 Gbps, 10 Gbps]
         rIFC_Up = new long[NUM_BACKBONE_ROUTERS + NUM_EDGE_ROUTERS];
         ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Up, 0, NUM_EDGE_ROUTERS, UL_LTE_BANDWIDTH);
         if (RandomGenerator.genUniformRandom() < 0.5) {
             ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Up, NUM_EDGE_ROUTERS, NUM_BACKBONE_ROUTERS + NUM_EDGE_ROUTERS-1, MIN_DL_BANDWIDTH_CORE); // 10 Gbps--> bit per sec
         } else {
             ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Up, NUM_EDGE_ROUTERS, NUM_BACKBONE_ROUTERS + NUM_EDGE_ROUTERS-1, MIN_UL_BANDWIDTH_CORE); // 1 Gbps--> bit per sec
         }
         ArrayFiller.fill1DArrayWithConstantNumber(rIFC_Up, NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS-1,NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS , UL_BANDWIDTH_CLOUD);//--> bit per sec
         
        
         dIF = new double[NUM_EDGE_ROUTERS];
         ArrayFiller.fill1DArrayRandomlyInRange(dIF, 0.1d, 0.6d);

         dAF = new double[NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS];
         ArrayFiller.fill1DArrayRandomlyInRange(dAF, 0d, 0.1d);

         //for a server and its associated AP is the same, e.g., 0.5 ap(1) == 0.5 fs(1)
         SC_RenewablePortion = new double[2*(NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS)];
         //ArrayFiller.fill1DArrayRandomlyInRange(SC_RenewablePortion, 0.4, 1);
      	 RandomGenerator.fillRandomBetaInArrayInRange(SC_RenewablePortion, numNodes);
      	 for (int rn = 0; rn<numNodes; rn++)
      		SC_RenewablePortion[rn+numNodes] = SC_RenewablePortion[rn];
      	 //renewable energy supply rate for the cloud node
      	 SC_RenewablePortion[NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS-1] = 0.85;
      	 SC_RenewablePortion[2*(NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS)-1] = 0.85;
     	 
      	 dFFC = new double[NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS][NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS];
      	 upLinkLat = new double [NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS];
         calcDelayBetweenCloudlets();
       
         
         
         
         UNIT_PROC_COST = new double[Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS];
 	    ArrayFiller.fill1DArrayWithConstantNumber(UNIT_PROC_COST, 0, Constants.NUM_EDGE_ROUTERS, 0.0000006d);
 		ArrayFiller.fill1DArrayWithConstantNumber(UNIT_PROC_COST, Constants.NUM_EDGE_ROUTERS, Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS, 0.0000002d);
 		
 		UNIT_STOR_COST = new double[Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS];
 	    ArrayFiller.fill1DArrayRandomlyInRange(UNIT_STOR_COST, 0, Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS-1, 0.021d, 0.023d);//only $0.023 per GB is better
 		
 	    UNIT_MEM_COST = new double[Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS];
 	    ArrayFiller.fill1DArrayWithConstantNumber(UNIT_MEM_COST, 0, Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS, 0.0000000021/128);//per MB per millisecond
 		
 		UNIT_COMM_COST = new double[Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS][Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS];
 		//edge communication
 		ArrayFiller.fill2DArrayRandomlyInRangeDiagonal(UNIT_COMM_COST, 0, Constants.NUM_EDGE_ROUTERS, 0, Constants.NUM_EDGE_ROUTERS, 0.01d, 0.03d);
 		//core communication
 		ArrayFiller.fill2DArrayRandomlyInRangeDiagonal(UNIT_COMM_COST, Constants.NUM_EDGE_ROUTERS, Constants.NUM_EDGE_ROUTERS-1 +Constants.NUM_BACKBONE_ROUTERS,
 				Constants.NUM_EDGE_ROUTERS, Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS-1, 0.03d, 0.06d);
 		//core-edge communication
 		ArrayFiller.fill2DArrayRandomlyInRangeDoubled(UNIT_COMM_COST, Constants.NUM_EDGE_ROUTERS, Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS-1,
 				0, Constants.NUM_EDGE_ROUTERS, 0.03d, 0.06d);
 		//cloud communication
 		ArrayFiller.fill2DArrayRandomlyInRangeDoubled(UNIT_COMM_COST, 0, Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS,
 				Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS-1,Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS, 0.06d, 0.09d);
 	    
 		//diameter must be zero
 		for(int i = 0; i<Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS; i++)
 			UNIT_COMM_COST[i][i] = 0;
 			
 		Factorial f = new Factorial();
         
    }
    

    

	private static void readMobilityProfiles(int[] nUM_SMART_THINGS) {

		short s = -1;
        
		VehiclesToAPDefault = new short [nUM_SMART_THINGS[0]][Constants.numTimeStamps];
		VehiclesToAPOptimized = new short [nUM_SMART_THINGS[1]][Constants.numTimeStamps];
		
		ArrayFiller.fill2DArrayWithConstantNumber(VehiclesToAPDefault, s);//initialization with -1
        ArrayFiller.fill2DArrayWithConstantNumber(VehiclesToAPOptimized, s);//initialization with -1
        
        
        for (int j=0; j<mobilProf; j++) {

			String pathToMobProfs = Constants.mobilityDataset[j];
			File folder = new File(pathToMobProfs);
			File[] listOfFiles = folder.listFiles();
			Arrays.sort(listOfFiles);
			
			for (int i = 0; i < nUM_SMART_THINGS[j]; i++) {
				readDevicePath(i, pathToMobProfs + listOfFiles[i].getName(), j);
				
			}
		}
        
	}

	
	
	/** reads time-stamped travel path of each smart thing and saves the path
	 * @param st smart thing
	 * @param filename input travel path file which is a .csv file formatted as: 
	 * time-stamp,degree/angle,x,y,speed,id,connected access point,distance: 10440.00,201.86,14873.40,15714.60,12.01,4608,21,300
	 * be careful about the naming method: as the files are sorted by their name, hence their name must have a correct order. 
	 * we used numbers started from 100000 for the file names
	 */
	private static void readDevicePath(int i, String filename, int j) {
		
		String line = "";
		String csvSplitBy = ",";

		if (j == 0)
			try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
		
					while ((line = br.readLine()) != null) {
						String[] position = line.split(csvSplitBy);//connected ap
						VehiclesToAPDefault [i][(int)Double.parseDouble(position[0])] = Short.parseShort(position[6]);
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			
		
		else {
			try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
				
				while ((line = br.readLine()) != null) {
					String[] position = line.split(csvSplitBy);//connected ap
					VehiclesToAPOptimized [i][(int)Double.parseDouble(position[0])] = Short.parseShort(position[6]);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	
		}
	
	}


	/**
	 * calculates propagation delay and uplink latency between different network devices according to their distances 
	 * it is assumed that the devices are connected using fibre media and disToLatFiberFactor is used for the conversion
	 * returns in second
	 * A simple test to measure latency is to run a ping. 
	 * This is a network diagnostic tool primarily used to test connectivity between two servers or devices.
	 * To ping a destination server, an Internet Control Message Protocol (ICMP) echo request packet is sent to that server. 
	 * If a connection is available, the destination node responds with an echo reply. Ping calculates the round-trip time of
	 * the data packet's route from its origin to its destination, and vice versa
	 */
	private static void calcDelayBetweenCloudlets() {
		String line;
		String CSV_SEPARATOR = ","; 
		double NodesDis;
		int i = 0;
		int j;
		double avgCloudLat = 0 ;
		System.out.println("Initializing the distance/delay between fog servers");// using file "+LatFile);
		
		try {
			
			  LineNumberReader csvReader = new LineNumberReader(new FileReader(LatFile)); 
			  while (((line =  csvReader.readLine()) != null)&&(i < (NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS - 1))) {
				  String[] data = line.split(CSV_SEPARATOR);
		  		
		  		  for (j = 0; j<(NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS - 1); j++){		//in meter from index 0 to 112 without cloud
		  			 	NodesDis = Math.floor(Double.parseDouble(data[j]) * 100) / 100;	//round values
						dFFC[i][j] = (NodesDis/1000)*disToLatFiberFactor/1000000; 		//in second
						dFFC[j][i] = dFFC[i][j];
						
		  		  }
		  		  i++;
			  }
		  	csvReader.close();
				  
			}
			catch (Exception e) {
			System.out.println("Error in FileReader !!!"); e.printStackTrace(); 
			}
		
		//latency between cloud node and other cloudlets:
		 i = NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS - 1;
		 for (j = 0; j<(NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS); j++){
			 if (i == j)
				 continue;
			 dFFC[j][i] = (RandomGenerator.genUniformRandomBetween(MIN_dFC, MAX_dFC))/1000;//second- 15-35 ms from fogplan or 100 ms rtt from vehicle to cloud from amazon cloud for instance 
			 dFFC[i][j] = dFFC[j][i];
			 
			 avgCloudLat  += dFFC[i][j];
			 
		}
		
		avgCloudLat /= (NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS);
		calUpLkLat(avgCloudLat);
		Utility.writeCommDelay();	//Writes delay matrix
		
	}


	/**
	 * calculated uplink latency for fog servers and core routers in second
	 * @param avergCloudLat
	 */
	private static void calUpLkLat(double avergCloudLat) {
		
		double avgHopLat = 0;
		double min = 50;//a random big delay in ms
		
		
		System.out.println("avgCloudCenterUpLinkLatency: "+avergCloudLat+ " second");
		
		for (int i = 0; i < NUM_EDGE_ROUTERS; i++){//for edge routers
			upLinkLat[i] = Constants.dIF[i]/1000000;
			avgHopLat += upLinkLat[i];
		}
		for (int i = NUM_EDGE_ROUTERS; i < (NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS - 1) ; i++){//for core routers
			min = 50;
			for (int j = 0; j < NUM_EDGE_ROUTERS - 1; j++){
	 				if ((dFFC[j][i]+upLinkLat[j]) < min)
		 			 		min = (dFFC[j][i]+upLinkLat[j]);
		 	}
		 		upLinkLat[i] = min;	
				avgHopLat += upLinkLat[i];
			
		}
		//Uplink latency for cloud services refers to the time it takes for data to travel from a client device or network to a cloud server or data center. 
		upLinkLat[(NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS - 1)] = avergCloudLat;
		avgHopLat += avergCloudLat;
		avgHopLat /= (NUM_BACKBONE_ROUTERS+NUM_EDGE_ROUTERS - 1);
		
		setBaseHopDelay(avgHopLat);
	}


	/**
	 * sets the base 1-hop latency in the network
	 * @param avgHopLat
	 */
	private static void setBaseHopDelay(double avgHopLat) {
		Constants.BaseHopDelay = avgHopLat;
		System.out.println("Average hop latency "+avgHopLat+" second");
	}

	/**
	 * saves a backup of servers capacity for later adjustments according to the input workload
	 */
	public static void backupNetwork() {
		FP_back = new double[NUM_EDGE_ROUTERS + NUM_BACKBONE_ROUTERS];
		FM_back = new int[NUM_EDGE_ROUTERS + NUM_BACKBONE_ROUTERS];
		FS_back = new long[NUM_EDGE_ROUTERS + NUM_BACKBONE_ROUTERS];
    	
		for(int i = 0; i< numNodes; i++) {
			Constants.FP_back[i] = Constants.FP[i];
			Constants.FM_back[i] = Constants.FM[i];
			Constants.FS_back[i] = Constants.FS[i];

	}
	}
	
	public static void setPaths(String subPath){

	    String projectRoot = System.getProperty("user.dir") + File.separator;
	    String OUT_BASEADDRESS = "";
	    String EPOS = "";
	    String NetOutFile = "";
	    String ARLoadFile = "";
	    String EPOSAnswer = "";
	    String ReqRes = "";
	    String EPOSAnswerOverall = "";
	    String methodOutput = "";

        CreateDirectory("PlansAndCosts" + File.separator + subPath);

        OUT_BASEADDRESS = projectRoot + subPath + File.separator + "PlansAndCosts" + File.separator + "Settings" + File.separator;
        EPOS = projectRoot + subPath + File.separator + "PlansAndCosts" + File.separator;

        NetOutFile = OUT_BASEADDRESS + "AP-Server" + File.separator;
        ARLoadFile = OUT_BASEADDRESS + "ARLoad" + File.separator + "Services-";
        EPOSAnswer = EPOS + "EPOSAnswer" + File.separator;
        ReqRes = EPOS + "ReqRes" + File.separator;
        EPOSAnswerOverall = Constants.EPOSAnswer + "overall" + File.separator;
        methodOutput = EPOS + "Methods" + File.separator;
	    	
	}
	
	public static void CreateDirectory(String sub) {
		String baseDirectory = System.getProperty("user.dir") + File.separator;
	    
		String subPath = sub+File.separator+"PlansAndCosts"; // Replace with the desired subpath
        String sourceDirectory = baseDirectory+File.separator+"PlansAndCosts";
        
        // Create the target directory path
        String targetDirectoryPath = baseDirectory + subPath;

        // Create a File object representing the target directory
        File targetDirectory = new File(targetDirectoryPath);

        // Create the directory if it doesn't exist
        if (!targetDirectory.exists()) {
            boolean success = targetDirectory.mkdirs();

            if (success) {
                System.out.println("Directory created successfully.");

                // Copy the contents of the source directory to the target directory
                try {
                    Path sourcePath = Paths.get(sourceDirectory);
                    Path targetPath = Paths.get(targetDirectoryPath);

                    // Copy the directory and its contents
                    Files.walk(sourcePath, FileVisitOption.FOLLOW_LINKS)
                            .forEach(source -> {
                                try {
                                    Path destination = targetPath.resolve(sourcePath.relativize(source));
                                    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });

                    System.out.println("Directory contents copied successfully.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("Failed to create directory.");
            }
        } else {
            System.out.println("Directory already exists.");
        }
        
       // System.exit(0);
   	
   	
	}
}