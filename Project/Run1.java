package EdgeEPOS;

import EdgeEPOS.City.City;
import EdgeEPOS.City.CityMap;
import EdgeEPOS.PlacementMethods.Agent;
import EdgeEPOS.PlacementMethods.DeployedServices;
import EdgeEPOS.PlacementMethods.Method;
import EdgeEPOS.PlacementMethods.Plan;
import EdgeEPOS.PlacementMethods.ServiceDeploymentMethod;
import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.Workload;
import EdgeEPOS.Setting.Network;
import EdgeEPOS.TrafficTraces.*;
import experiment.IEPOSExperiment;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.fog.entities.FogBroker;
import org.fog.entities.MobileDevice;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.util.Pair;
import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.NetworkTopology;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.application.selectivity.SelectivityModel;
import org.fog.entities.ApDevice;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.MobileActuator;
import org.fog.entities.MobileDevice;
import org.fog.entities.MobileSensor;
import org.fog.entities.Tuple;
import org.fog.localization.Coordinate;
import org.fog.localization.Distances;
import org.fog.placement.MobileController;
import org.fog.placement.ModuleMapping;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.scheduler.TupleScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.TimeKeeper;
import org.fog.utils.distribution.DeterministicDistribution;
import org.fog.vmmigration.BeforeMigration;
import org.fog.vmmigration.CompleteVM;
import org.fog.vmmigration.ContainerVM;
import org.fog.vmmigration.DecisionMigration;
import org.fog.vmmigration.LiveMigration;
import org.fog.vmmigration.LowestDistBwSmartThingAP;
import org.fog.vmmigration.LowestDistBwSmartThingServerCloudlet;
import org.fog.vmmigration.LowestLatency;
import org.fog.vmmigration.MyStatistics;
import org.fog.vmmigration.PrepareCompleteVM;
import org.fog.vmmigration.PrepareContainerVM;
import org.fog.vmmigration.PrepareLiveMigration;
import org.fog.vmmigration.Service;
import org.fog.vmmigration.VmMigrationTechnique;
import org.fog.vmmobile.constants.MaxAndMin;
import org.fog.vmmobile.constants.Policies;
import org.fog.vmmobile.constants.Services;


/**
 * @author rooyesh
 *
 */
public class Run1 {

	private static int stepPolicy; // Quantity of steps in the nextStep Function
	private static List<MobileDevice> smartThings = new ArrayList<MobileDevice>();
	private static List<FogDevice> serverCloudlets = new ArrayList<>();
	private static List<ApDevice> apDevices = new ArrayList<>();
	private static List<FogBroker> brokerList = new ArrayList<>();
	private static List<String> appIdList = new ArrayList<>();
	private static List<Application> applicationList = new ArrayList<>();
	static Map<Integer, Map<String, Double>> deadlineInfo = new HashMap<Integer, Map<String, Double>>();
	private List <Agent> agents = new ArrayList<>();
	
	private static boolean migrationAble;

	private static int migPointPolicy;
	private static int migStrategyPolicy;
	private static int positionApPolicy;
	private static int positionScPolicy;
	private static int policyReplicaVM;
	private static int travelPredicTimeForST; // in seconds
	private static int mobilityPrecitionError;// in meters
	private static double latencyBetweenCloudlets;
	private static int maxBandwidth;
	private static int maxSmartThings;
	private static Coordinate coordDevices;
	private static int seed;
	private static Random rand;
	static final boolean CLOUD = true;

	static final int numOfDepts = 1;
	static final int numOfMobilesPerDept = 4;
	static final double EEG_TRANSMISSION_TIME = 10;

    private static int PROFILE_NUM;//=8 for two hours (15 min periods)
    private static int index = 0;
    public static boolean printCost = false;
    private static int TAU = 15 * 60; // time interval between run of the method (15min)
    private static int TRAFFIC_CHANGE_INTERVAL = 15 * 60; // time interval between run of the method (sec)
    private static int TRAFFIC_DIST_METHOD = 0;//random distribution
    
    
    public static void main(String[] args) throws FileNotFoundException {
       
    	Log.disable();
		int numUser = 1; // number of cloud users
		Calendar calendar = Calendar.getInstance();
		boolean traceFlag = false; // mean trace events
		CloudSim.init(numUser, calendar, traceFlag);
		
		/*  Example parameters
		 *  
		 *  1 290538 0 0 1 11 0 0 0 61
		 *  
		 *  First parameter: 0/1 -> migrations are denied or allowed
		 *  Second parameter: Positive Integer -> seed to be used in the random numbers generation
		 *  Third parameter: 0/1 -> Migration point approach is fixed (0) or based on the user speed (1)
		 *  Fourth parameter: 0/1/2 -> Migration strategy approach is based on the lowest latency (0), lowest distance between the user and cloudlet (1), or lowest distance between user and Access Point (2)
		 *  Fifth parameter: Positive Integer -> Number of users
		 *  Sixth parameter: Positive Integer -> Base Network Bandwidth between cloudlets
		 *  Seventh parameter: 0/1/2 -> Migration policy based on Complete VM/Cold migration (0), Complete Container migration (1), or Container Live Migration (3)
		 *  Eighth parameter: Non Negative Integer -> User Mobility prediction, in seconds
		 *  Ninth parameter: Non Negative Integer -> User Mobility prediction inaccuracy, in meters
		 *  Tenth parameter: Positive negative Integer -> Base Network Latency between cloudlets
		 */
		setMigrationAble(false);
		setSeed(290538);
		setStepPolicy(1);
		setMigPointPolicy(0);
		// LOWEST_LATENCY = 0;
		// LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET = 1;
		// LOWEST_DIST_BW_SMARTTING_AP = 2;
		setMigStrategyPolicy(0);
		setMaxSmartThings(1);///?????
		setMaxBandwidth(11);//Network Bandwidth between cloudlets
		// MIGRATION_COMPLETE_VM = 0;
		// MIGRATION_CONTAINER_VM = 1;
		// LIVE_MIGRATION = 2;
		setPolicyReplicaVM(0);
		setTravelPredicTimeForST(0);
		setMobilityPredictionError(0);
		setLatencyBetweenCloudlets(61);
		
		int currentVehiclesInArea = 0;//number of services running
		//modify CityMap to get Max and Min (set constants) from border points file
		
		//ifogsim coverage range of APs and Cloudlet : 2000
		//https://www.techtarget.com/searchnetworking/feature/Macrocell-vs-small-cell-vs-femtocell-A-5G-introduction
		//https://community.opencellid.org/t/answered-how-to-understand-interpret-use-opencellid-database/452
		//usually 4G macro sites
		//In macro-cellular networks cell sizes usually range from 1 to 20 km. Typically, micro-cellular networks have cell sizes of 400 metres to 2 km, and pico-cellular nets have cell sizes of 4 to 200 metres.
		//What is cell radius in LTE?
		//Large cells radius is from 1 km and normally it go beyond 3 km. Small cells radius is from 0.3 km to 1 km and Microcells radius generally in the range of 200 m to 300 m.
		//papers 150-200 meters for eua: https://github.com/swinedge/eua-dataset
		String city = "Munich";
		String cityconfigfile = "src/EdgeEPOS/City/CityConfig.json";
		if (!City.initializeCity(city, cityconfigfile))
	    	System.exit(0);
		City.makeCityPolygon();
		
		/*
		Constants.numCloudServers = City.BACKBONE_ROUTERS;//5
        Constants.numFogNodes = City.EDGE_ROUTERS;//10
        Constants.numSmartThings=30;
        Constants.numServices = 30;
        Constants.base_Path = new File("").getAbsolutePath();//G:\MobFogSim-master\MobFogSim-master
        //Constants.TAU = TAU;
        Constants.TRAFFIC_CHANGE_INTERVAL = TRAFFIC_CHANGE_INTERVAL;
        int q = Constants.TAU / Constants.TRAFFIC_CHANGE_INTERVAL; // the number of times that traffic changes between each run of the method. HERE 1
        */
        Constants.initialize();
        
        Network net = new Network(seed);
      
        Workload.TRAFFIC_ENLARGE_FACTOR = 1;
        ArrayList<Double> PROFILES = AggregatedTraceReader.readTrafficFromFile(); // read the traffic 192 items, the incoming traffic sample to one
        // service on one node
        PROFILE_NUM = PROFILES.size();//4;
        
        Double trafficPerNodePerService;
        Workload w = new Workload(); 
       
        Plan DeployPlanOptOpt = null;
     
       //how to advance time???
        int i = 0;//run number
        int Ts = i*5;//EPOS running interval = 5 minute
        currentVehiclesInArea = getNumberOfServices(Ts);
        
        Method optimalRouteOptimalPlacement = new Method(new ServiceDeploymentMethod(ServiceDeploymentMethod.OPTIMAL_SERVICE_OPTIMAL_ROUTE, i, currentVehiclesInArea));
        Method baseRouteOptimPlacement = new Method(new ServiceDeploymentMethod(ServiceDeploymentMethod.OPTIMAL_SERVICE_BASE_ROUTE, i, currentVehiclesInArea));
    
        //Method baseRouteBasePlacement = new Method(new ServiceDeploymentMethod(ServiceDeploymentMethod.BASE_SERVICE_BASE_ROUTE), i);
      //Method optimRouteBasePlacement = new Method(new ServiceDeploymentMethod(ServiceDeploymentMethod.BASE_SERVICE_OPTIMAL_ROUTE, i));

        
        System.out.println("Profile numbers "+PROFILE_NUM);
        
      for (i = 0; i < PROFILE_NUM; i++) {
    	  System.out.println("Reading profiles number: "+i);
            trafficPerNodePerService = nextRate(PROFILES); // get the next PROFILE
            w.distributeTraffic(trafficPerNodePerService, TRAFFIC_DIST_METHOD, i);
            applicationList = w.createApplication(net.getSmartThings(), net.getBrokerList());
            
            //System.out.println("total traffic: "+trafficPerNodePerService * Constants.numSmartThings+ " applist size: "+applicationList.size());
      
            
        for (Agent a: net.getAgents())
        	a.arrivalRatesOfInstructionsToFogNode(w.getServiceListById(a.getAgIndex()));//update vehicles???
           
            //Traffic.setTrafficToGlobalTraffic(optimRouteOptimPlacement);
        //must filter service list based on the timestamp of vehicle:
        	DeployPlanOptOpt = optimalRouteOptimalPlacement.run(i, net.getAgents(), w.getServiceList(), net.getServerCloudlets());
            //runCloudsim(DeployPlanOptOpt);
            //delayFogStatic = optimRouteOptimPlacement.getAvgServiceDelay();
            //costFogStatic = optimRouteOptimPlacement.getAvgCost(Parameters.TRAFFIC_CHANGE_INTERVAL);
            //sviolFogStatic = Violation.getViolationPercentage(optimRouteOptimPlacement);
            
       
        
          // System.out.println("run "+i+ " end-loop: "+trafficPerNodePerService * Constants.numServices * Constants.numFogNodes);//fogplan:10*40
      
        }
       
       
    }

    private static int getNumberOfServices(int ts) {
		// TODO Auto-generated method stub
    	// read from file
		return 0;
	}

	private static void runCloudsim(Plan deployPlanOptOpt) throws FileNotFoundException {
    
    /**
	 * STEP 6: CREATE MAPPING, CONTROLLER, AND SUBMIT APPLICATION
	 **/

	MobileController mobileController = null;
	// initializing a module mapping
	ModuleMapping moduleMapping = ModuleMapping.createModuleMapping();

	for (Application app : getApplicationList()) {
		app.setPlacementStrategy("Mapping");
	}
	int i = 0;
	for (FogDevice sc : getServerCloudlets()) {
		i = 0;
		for (MobileDevice st : getSmartThings()) {
			if (st.getApDevices() != null) {
				if (sc.equals(st.getSourceServerCloudlet())) {//(moduleName, deviceName, 1);
					moduleMapping.addModuleToDevice(((AppModule) st.getVmMobileDevice()).getName(),
						sc.getName(), 1);
					moduleMapping.addModuleToDevice("client" + st.getMyId(),st.getName(), 1);
					
				}
			}
			i++;
		}
	}

	mobileController = new MobileController("MobileController",
		getServerCloudlets(), getApDevices(), getSmartThings(),
		getBrokerList(), moduleMapping, getMigPointPolicy(),
		getMigStrategyPolicy(), getStepPolicy(), getCoordDevices(),
		getSeed(), isMigrationAble());
	i = 0;
	for (Application app : applicationList) {
		mobileController.submitApplication(app, 1);
	}
	TimeKeeper.getInstance().setSimulationStartTime(
		Calendar.getInstance().getTimeInMillis());
	MyStatistics.getInstance().setSeed(getSeed());
	for (MobileDevice st : getSmartThings()) {
		if (getMigPointPolicy() == Policies.FIXED_MIGRATION_POINT) {
			if (getMigStrategyPolicy() == Policies.LOWEST_LATENCY) {

				MyStatistics.getInstance().setFileMap("./outputLatencies/" + st.getMyId()
					+ "/latencies_FIXED_MIGRATION_POINT_with_LOWEST_LATENCY_seed_"
					+ getSeed() + "_st_" + st.getMyId() + ".txt", st.getMyId());
				MyStatistics.getInstance().putLantencyFileName(
					"FIXED_MIGRATION_POINT_with_LOWEST_LATENCY_seed_"
						+ getSeed() + "_st_" + st.getMyId(), st.getMyId());
				MyStatistics.getInstance().setToPrint(
					"FIXED_MIGRATION_POINT_with_LOWEST_LATENCY");
			} else if (getMigStrategyPolicy() == Policies.LOWEST_DIST_BW_SMARTTING_AP) {
				MyStatistics.getInstance().setFileMap("./outputLatencies/" + st.getMyId()
					+ "/latencies_FIXED_MIGRATION_POINT_with_LOWEST_DIST_BW_SMARTTING_AP_seed_"
					+ getSeed() + "_st_" + st.getMyId()+ ".txt", st.getMyId());
				MyStatistics.getInstance().putLantencyFileName(
					"FIXED_MIGRATION_POINT_with_LOWEST_DIST_BW_SMARTTING_AP_seed_"
					+ getSeed() + "_st_" + st.getMyId(), st.getMyId());
				MyStatistics.getInstance().setToPrint(
					"FIXED_MIGRATION_POINT_with_LOWEST_DIST_BW_SMARTTING_AP");

			} else if (getMigStrategyPolicy() == Policies.LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET) {
				MyStatistics.getInstance().setFileMap("./outputLatencies/"+ st.getMyId()
					+ "/latencies_FIXED_MIGRATION_POINT_with_LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET_seed_"
					+ getSeed() + "_st_" + st.getMyId() + ".txt", st.getMyId());
				MyStatistics.getInstance().putLantencyFileName(
					"FIXED_MIGRATION_POINT_with_LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET_seed_"
					+ getSeed() + "_st_" + st.getMyId(), st.getMyId());
				MyStatistics.getInstance().setToPrint(
					"FIXED_MIGRATION_POINT_with_LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET");
			}
		} else if (getMigPointPolicy() == Policies.SPEED_MIGRATION_POINT) {
			if (getMigStrategyPolicy() == Policies.LOWEST_LATENCY) {
				MyStatistics.getInstance().setFileMap("./outputLatencies/" + st.getMyId()
					+ "/latencies_SPEED_MIGRATION_POINT_with_LOWEST_LATENCY_seed_"
					+ getSeed() + "_st_" + st.getMyId()+ ".txt", st.getMyId());
				MyStatistics.getInstance().putLantencyFileName(
					"SPEED_MIGRATION_POINT_with_LOWEST_LATENCY_seed_"
					+ getSeed() + "_st_" + st.getMyId(), st.getMyId());
				MyStatistics.getInstance().setToPrint(
					"SPEED_MIGRATION_POINT_with_LOWEST_LATENCY");

			} else if (getMigStrategyPolicy() == Policies.LOWEST_DIST_BW_SMARTTING_AP) {
				MyStatistics.getInstance().setFileMap("./outputLatencies/"+ st.getMyId()
					+ "/latencies_SPEED_MIGRATION_POINT_with_LOWEST_DIST_BW_SMARTTING_AP_seed_"
					+ getSeed() + "_st_" + st.getMyId()+ ".txt", st.getMyId());
				MyStatistics.getInstance().putLantencyFileName(
					"SPEED_MIGRATION_POINT_with_LOWEST_DIST_BW_SMARTTING_AP_seed_"
					+ getSeed() + "_st_" + st.getMyId(),st.getMyId());
				MyStatistics.getInstance().setToPrint(
					"SPEED_MIGRATION_POINT_with_LOWEST_DIST_BW_SMARTTING_AP");

			} else if (getMigStrategyPolicy() == Policies.LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET) {
				MyStatistics.getInstance().setFileMap("./outputLatencies/"+ st.getMyId()
					+ "/latencies_SPEED_MIGRATION_POINT_with_LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET_seed_"
					+ getSeed() + "_st_" + st.getMyId()+ ".txt", st.getMyId());
				MyStatistics.getInstance().putLantencyFileName(
					"SPEED_MIGRATION_POINT_with_LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET_seed_"
					+ getSeed() + "_st_" + st.getMyId(),st.getMyId());
				MyStatistics.getInstance().setToPrint(
					"SPEED_MIGRATION_POINT_with_LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET");
			}
		}
		MyStatistics.getInstance().putLantencyFileName("Time-latency", st.getMyId());
		MyStatistics.getInstance().getMyCount().put(st.getMyId(), 0);
	}

	int myCount = 0;

	for (MobileDevice st : getSmartThings()) {
		if (st.getSourceAp() != null) {
			System.out.println("Distance between " + st.getName() + " and "
				+ st.getSourceAp().getName() + ": "
				+ Distances.checkDistance(st.getCoord(),
					st.getSourceAp().getCoord()));
		}
	}
	for (MobileDevice st : getSmartThings()) {
		System.out.println(
			st.getName() + "- X: " + st.getCoord().getCoordX() + " Y: "
				+ st.getCoord().getCoordY() + " Direction: "
				+ st.getDirection() + " Speed: " + st.getSpeed()
				+ " VmSize: " + st.getVmMobileDevice().getSize());
	}
	System.out
		.println("_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_");
	for (FogDevice sc : getServerCloudlets()) {
		System.out.println(sc.getName() + "- X: " + sc.getCoord().getCoordX()
			+ " Y: " + sc.getCoord().getCoordY()
			+ " UpLinkLatency: " + sc.getUplinkLatency());
	}
	System.out
		.println("_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_+_");
	for (ApDevice ap : getApDevices()) {
		System.out.println(ap.getName() + "- X: " + ap.getCoord().getCoordX() + " Y: "
			+ ap.getCoord().getCoordY() + " connected to "
			+ ap.getServerCloudlet().getName());

	}
	
	System.setOut(new PrintStream("out.txt"));
	System.out.println("Inicio: " + Calendar.getInstance().getTime());
	CloudSim.startSimulation();
	//out.close();
	System.out.println("Simulation over");
	//PrintStream console = System.out;
	//System.setOut(console);
	//System.out.println("Simulation over");
	CloudSim.stopSimulation();
}
    
    /**
     * Gets the next traffic rate from the trace
     * @param traceList the trace
     * @return returns the next traffic rate from the trace
     */
    private static Double nextRate(ArrayList<Double> traceList) {
    	return traceList.get(index++);
    }

    public static int getPolicyReplicaVM() {
		return policyReplicaVM;
	}

	public static void setPolicyReplicaVM(int policyReplicaVM) {
		Run1.policyReplicaVM = policyReplicaVM;
	}

	public static int getTravelPredicTimeForST() {
		return travelPredicTimeForST;
	}

	public static void setTravelPredicTimeForST(int travelPredicTimeForST) {
		Run1.travelPredicTimeForST = travelPredicTimeForST;
	}

	public static int getMobilityPrecitionError() {
		return mobilityPrecitionError;
	}

	public static void setMobilityPredictionError(int mobilityPrecitionError) {
		Run1.mobilityPrecitionError = mobilityPrecitionError;
	}

	public static double getLatencyBetweenCloudlets() {
		return latencyBetweenCloudlets;
	}

	public static void setLatencyBetweenCloudlets(double latencyBetweenCloudlets) {
		Run1.latencyBetweenCloudlets = latencyBetweenCloudlets;
	}

	public static int getStepPolicy() {
		return stepPolicy;
	}

	public static void setStepPolicy(int stepPolicy) {
		Run1.stepPolicy = stepPolicy;
	}

	public static List<MobileDevice> getSmartThings() {
		return smartThings;
	}

	public static void setSmartThings(List<MobileDevice> smartThings) {
		Run1.smartThings = smartThings;
	}

	public static List<FogDevice> getServerCloudlets() {
		return serverCloudlets;
	}

	public static void setServerCloudlets(List<FogDevice> serverCloudlets) {
		Run1.serverCloudlets = serverCloudlets;
	}

	public static List<ApDevice> getApDevices() {
		return apDevices;
	}

	public static void setApDevices(List<ApDevice> apDevices) {
		Run1.apDevices = apDevices;
	}

	public static int getMigPointPolicy() {
		return migPointPolicy;
	}

	public static void setMigPointPolicy(int migPointPolicy) {
		Run1.migPointPolicy = migPointPolicy;
	}

	public static int getMigStrategyPolicy() {
		return migStrategyPolicy;
	}

	public static void setMigStrategyPolicy(int migStrategyPolicy) {
		Run1.migStrategyPolicy = migStrategyPolicy;
	}

	public static int getPositionApPolicy() {
		return positionApPolicy;
	}

	public static void setPositionApPolicy(int positionApPolicy) {
		Run1.positionApPolicy = positionApPolicy;
	}

	public static Coordinate getCoordDevices() {
		return coordDevices;
	}

	public static void setCoordDevices(Coordinate coordDevices) {
		Run1.coordDevices = coordDevices;
	}

	public static List<FogBroker> getBrokerList() {
		return brokerList;
	}

	public static void setBrokerList(List<FogBroker> brokerList) {
		Run1.brokerList = brokerList;
	}

	public static List<String> getAppIdList() {
		return appIdList;
	}

	public static void setAppIdList(List<String> appIdList) {
		Run1.appIdList = appIdList;
	}

	public static List<Application> getApplicationList() {
		return applicationList;
	}

	public static void setApplicationList(List<Application> applicationList) {
		Run1.applicationList = applicationList;
	}

	public static int getSeed() {
		return seed;
	}

	public static void setSeed(int seed) {
		Run1.seed = seed;
	}

	
	public static int getMaxSmartThings() {
		return maxSmartThings;
	}

	public static void setMaxSmartThings(int maxSmartThings) {
		Run1.maxSmartThings = maxSmartThings;
	}

	public static Random getRand() {
		return rand;
	}

	public static void setRand(Random rand) {
		Run1.rand = rand;
	}

	public static int getMaxBandwidth() {
		return maxBandwidth;
	}

	public static void setMaxBandwidth(int maxBandwidth) {
		Run1.maxBandwidth = maxBandwidth;
	}

	public static boolean isMigrationAble() {
		return migrationAble;
	}

	public static void setMigrationAble(boolean migrationAble) {
		Run1.migrationAble = migrationAble;
	}

	
}
