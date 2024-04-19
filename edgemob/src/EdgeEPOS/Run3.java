package EdgeEPOS;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.fog.localization.Coordinate;

import EdgeEPOS.City.City;
import EdgeEPOS.CostComponents.RunCost;
import EdgeEPOS.PlacementMethods.Method;
import EdgeEPOS.PlacementMethods.ServiceDeploymentMethod;
import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.Constraint;
import EdgeEPOS.Setting.Network;
import EdgeEPOS.TrafficTraces.AggregatedTraceReader;
import EdgeEPOS.Utility.RandomGenerator;
import EdgeEPOS.Utility.Utility;
import fog.entities.ApDevice;
import fog.entities.FogDevice;
import fog.entities.MobileDevice;

/**
 * @author znb_n
 * Renewable TARGET signal
 * "NUM_SMART_VEHICLES":[10112,9788,10112,9788,10112,9788],
 *
 */
public class Run3 {

	private static List<MobileDevice> smartThings = new ArrayList<MobileDevice>();
	private static List<FogDevice> serverCloudlets = new ArrayList<>();
	private static List<ApDevice> apDevices = new ArrayList<>();
	
	
	private static int maxSmartThings;
	private static Coordinate coordDevices;
	private static int seed;
	private static Random rand;
    private static int index = 129;//increasing profiles

    
    public static void main(String[] args) throws FileNotFoundException {
    	System.out.println("Placement methods with target started at "+System.currentTimeMillis());
    	
    	setSeed(290538);
    	
		int[] NUM_SMART_THINGS = new int [6];
    	String city = "Munich_109_default";	
		String cityconfigfile;
		
		if(Constants.win)
			cityconfigfile = "C:\\Users\\znb_n\\eclipse-workspace\\VehEdgCld\\src\\EdgeEPOS\\Setting\\input\\CityConfig.json";
		else
			cityconfigfile = "/home/zeinab/sumo-manos/VehEdgCld/src/EdgeEPOS/Setting/input/CityConfig.json";
		
		
		if (!City.initializeCity(city, cityconfigfile, NUM_SMART_THINGS))
	    	System.exit(0);
		
		City.makeCityPolygon();
		Constants.initialize(NUM_SMART_THINGS);
		
		Network net = new Network(seed);		//the last core point (cloud node) is located in its correct position which should be amended and located inside the area, then you need to modify only the latency.
        										//either modify the max/min accordingly as all the entities are already in the area, you might not need to edit its location as it does not connect nodes directly like access points
		
		
		Constraint cons = new Constraint(Constants.numNodes, Constants.onoff, Constants.target, Constants.capacityfactor);
		cons.setAgentsConstraintAdaptively();
		
		ArrayList<Double> PROFILES = AggregatedTraceReader.readTrafficFromFile(); 
        Double avgTrafficPerNodePerService;
        int run = 0;							//run number
        int timestamp = 0;						//current time based on the mobility of vehicles from 0 to 3600
        RunCost [][] costs = new RunCost [Constants.PROFILE_NUM * Constants.EposRunPerWorkload][Constants.numPlacementMethod];
        
          System.out.println("------------------------------------");
		  ServiceDeploymentMethod dropScheme = new ServiceDeploymentMethod(ServiceDeploymentMethod.OPTIMAL_SERVICE_DEFAULT_ROUTE, ServiceDeploymentMethod.DEFAULT_MOBILITY, run, NUM_SMART_THINGS, costs); 
    	  Method OptimalPlacementDefaultRoute = new Method(dropScheme);
		  dropScheme.addAgents(net.getServerCloudlets(), net.getApDevices());
		  
		  System.out.println("------------------------------------");
		  ServiceDeploymentMethod oropScheme = new ServiceDeploymentMethod(ServiceDeploymentMethod.OPTIMAL_SERVICE_OPTIMIZED_ROUTE, ServiceDeploymentMethod.OPTIMIZED_MOBILITY, run, NUM_SMART_THINGS, costs); 
		  Method OptimalPlacementOptimizedRoute = new Method(oropScheme);
		  oropScheme.addAgents(net.getServerCloudlets(), net.getApDevices());
		  
		  System.out.println("------------------------------------");
		  ServiceDeploymentMethod drbpScheme = new ServiceDeploymentMethod(ServiceDeploymentMethod.BASE_SERVICE_DEFAULT_ROUTE, ServiceDeploymentMethod.DEFAULT_MOBILITY, run, NUM_SMART_THINGS, costs); 
		  Method BasePlacementDefaultRoute = new Method(drbpScheme); 
		  drbpScheme.addAgents(net.getServerCloudlets(), net.getApDevices());
		  
		  System.out.println("------------------------------------");
		  ServiceDeploymentMethod OrbpScheme = new ServiceDeploymentMethod(ServiceDeploymentMethod.BASE_SERVICE_OPTIMIZED_ROUTE, ServiceDeploymentMethod.OPTIMIZED_MOBILITY, run, NUM_SMART_THINGS, costs); 
		  Method BasePlacementOptimizedRoute = new Method(OrbpScheme); 
		  OrbpScheme.addAgents(net.getServerCloudlets(), net.getApDevices());
		  
		  System.out.println("------------------------------------");
		  ServiceDeploymentMethod drfpScheme = new ServiceDeploymentMethod(ServiceDeploymentMethod.FF_SERVICE_DEFAULT_ROUTE, ServiceDeploymentMethod.DEFAULT_MOBILITY, run, NUM_SMART_THINGS, costs); 
		  Method FirFitPlacementDefaultRoute = new Method(drfpScheme); 
		  drfpScheme.addAgents(net.getServerCloudlets(), net.getApDevices());
		 
		  System.out.println("------------------------------------");
		  ServiceDeploymentMethod orfpScheme = new ServiceDeploymentMethod(ServiceDeploymentMethod.FF_SERVICE_OPTIMIZED_ROUTE, ServiceDeploymentMethod.OPTIMIZED_MOBILITY, run, NUM_SMART_THINGS, costs); 
		  Method FirFitPlacementOptimizedRoute = new Method(orfpScheme); 
		  orfpScheme.addAgents(net.getServerCloudlets(), net.getApDevices());
			 
		  
        System.out.println("------------------------------------");
        System.out.println(Constants.PROFILE_NUM +" profiles selected from "+PROFILES.size()+" profiles with the distribution method "+ Constants.TRAFFIC_DIST_METHOD_NAME);
        System.out.println("TRAFFIC_CHANGE_INTERVAL: "+ Constants.TRAFFIC_CHANGE_INTERVAL+ " min, EPOS_RUN_INTERVAL: "+Constants.EPOS_RUN_INTERVAL+" min");
        
        
        
        System.out.println("\n------------------------------------Running Experiments----------------------------------");
        
        for (int profile = 0 ; profile < Constants.PROFILE_NUM ; profile++) {//interval for IoT PROFILE update 
        	avgTrafficPerNodePerService = nextRate(PROFILES);
            System.out.println("\n\n****************************Profile "+profile+": "+avgTrafficPerNodePerService+"****************************");
            
              dropScheme.TrafficDist(avgTrafficPerNodePerService, profile);
			  oropScheme.TrafficDist(avgTrafficPerNodePerService, profile);
			  
			  drbpScheme.TrafficDist(avgTrafficPerNodePerService, profile);
			  OrbpScheme.TrafficDist(avgTrafficPerNodePerService, profile);
			  
			  drfpScheme.TrafficDist(avgTrafficPerNodePerService, profile);
			  orfpScheme.TrafficDist(avgTrafficPerNodePerService, profile);
			 
              for (int j = 0; j < Constants.EposRunPerWorkload; j++) {//vehicles' update (epos run) interval
		    	
			    	System.out.println("-----------------------------------Run "+run+"-----------------------------------");
			    	
			    	timestamp = ((profile * Constants.TRAFFIC_CHANGE_INTERVAL) + (j * Constants.EPOS_RUN_INTERVAL))*60;// + 300;
			    	System.out.println("Profile "+ profile+ ", EposRun "+j+ ", current timestamp "+timestamp);
		    	    
			    	dropScheme.CreateLoad(timestamp, run);
				    oropScheme.CreateLoad(timestamp, run);
				  
				    drbpScheme.CreateLoad(timestamp, run); 
				    OrbpScheme.CreateLoad(timestamp, run);
				  
				    drfpScheme.CreateLoad(timestamp, run);
					orfpScheme.CreateLoad(timestamp, run); 
					  
					OptimalPlacementDefaultRoute.run(args, run, timestamp, net.getServerCloudlets());
					OptimalPlacementOptimizedRoute.run(args, run, timestamp, net.getServerCloudlets());
					  
					BasePlacementDefaultRoute.run(args, run, timestamp, net.getServerCloudlets()); 
					BasePlacementOptimizedRoute.run(args, run, timestamp, net.getServerCloudlets());
							  
					FirFitPlacementDefaultRoute.run(args, run, timestamp, net.getServerCloudlets());
					FirFitPlacementOptimizedRoute.run(args, run, timestamp, net.getServerCloudlets()); 
					  
		    	if (run == 0)
	            	Utility.writeUnitCosts();
	            
	            run++;
	        	System.out.println();
              }
       
        }
        Utility.WriteMethodsCosts(costs);
        Utility.dataPrep();
        System.out.println("\nRuns are completed");
    }

    
    

	/**
     * Gets the next traffic rate from the trace
     * @param traceList the trace
     * @return returns the next traffic rate from the trace
     */
    private static Double nextRate(ArrayList<Double> traceList) {
    	return traceList.get(index++);
    }

	public static List<MobileDevice> getSmartThings() {
		return smartThings;
	}

	public static void setSmartThings(List<MobileDevice> smartThings) {
		Run3.smartThings = smartThings;
	}

	public static List<FogDevice> getServerCloudlets() {
		return serverCloudlets;
	}

	public static void setServerCloudlets(List<FogDevice> serverCloudlets) {
		Run3.serverCloudlets = serverCloudlets;
	}

	public static List<ApDevice> getApDevices() {
		return apDevices;
	}

	public static void setApDevices(List<ApDevice> apDevices) {
		Run3.apDevices = apDevices;
	}

	
	public static Coordinate getCoordDevices() {
		return coordDevices;
	}

	public static void setCoordDevices(Coordinate coordDevices) {
		Run3.coordDevices = coordDevices;
	}

	public static int getSeed() {
		return seed;
	}

	public static void setSeed(int seed) {
		Run3.seed = seed;
	}

	
	public static int getMaxSmartThings() {
		return maxSmartThings;
	}

	public static void setMaxSmartThings(int maxSmartThings) {
		Run3.maxSmartThings = maxSmartThings;
	}

	public static Random getRand() {
		return rand;
	}

	public static void setRand(Random rand) {
		Run3.rand = rand;
	}


	
}
