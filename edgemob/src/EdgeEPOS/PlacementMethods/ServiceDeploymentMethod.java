package EdgeEPOS.PlacementMethods;

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.fog.localization.Coordinate;

import EdgeEPOS.CostComponents.RunCost;
import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.Constraint;
import EdgeEPOS.Setting.MobileARservice;
import EdgeEPOS.Setting.Workload;
import EdgeEPOS.Utility.ArrayFiller;
import fog.entities.ApDevice;
import fog.entities.FogDevice;
import fog.entities.MobileDevice;

/**
 *
 * @author rooyesh
 *
 * This class contains variables that keep the type of the service deployment
 * method and related function
 */
public class ServiceDeploymentMethod {

    public static final int OPTIMAL_SERVICE_DEFAULT_ROUTE = 0;			//EPOS optimized placement for default routes of SUMO
    public static final int OPTIMAL_SERVICE_OPTIMIZED_ROUTE = 1; 		//EPOS optimized placement for optimized routes of SUMO
    public static final int BASE_SERVICE_DEFAULT_ROUTE = 2;				//Baseline placement for default routes of SUMO
    public static final int BASE_SERVICE_OPTIMIZED_ROUTE = 3;				//Baseline placement for optimized routes of SUMO
	public static final int FF_SERVICE_DEFAULT_ROUTE = 4;
	public static final int FF_SERVICE_OPTIMIZED_ROUTE = 5;
	public static final int DEFAULT_MOBILITY = 0;
	public static final int OPTIMIZED_MOBILITY = 1;
	
	
    public int methodType; //type of the method (e.g. optimal vs baseline)
    public int mobilityType;
    public int run;
    public double optBeta;
	public int optNumPlan;
    public int optHopLevel;
    public RunCost[][] mastercosts;
	
    private int timestamp;

    public static boolean GP = true;
    
    public Plan GPlan;
    public Workload workload;
    public List<Agent> agents = new ArrayList<>();
	public List<MobileDevice> smartThings = new ArrayList<MobileDevice>();
	public int numst;
 	
	//************************************************************
	//	Service requests
	//************************************************************
	
	/**
	 * traffic ratio reach every vehicle/end-device during every workload profile
	 */
	public double[] globalTraffic; 
    public double TRAFFIC_NORM_FACTOR;
    /**
     * percentage of the traffic rate for every vehicle/end-device during every workload profile
     */
    public double[] ServiceTrafficPercentage; 
       
    public int currentVehiclesInAreaToPlan = 0;//number of vehicles moving (service requests need planning) at the moment in the area
	/**
	 * records the connected APs to each smart thing at every time-stamp		
	 */
	public short[][] VehiclesToAP;
	
	

	
    public ServiceDeploymentMethod(int meType, int moType, int run, int[] nUM_SMART_THINGS, RunCost[][] costs) {
    	System.out.println("Placement method "+meType+", initialization step...");
		
        this.methodType = meType;
        this.mobilityType = moType;
        this.mastercosts = costs;
        this.run = run;
       
        //global response:
        GPlan = new Plan(nUM_SMART_THINGS[meType], GP);
        numst = nUM_SMART_THINGS[meType];
    	
        
        /* initially all services are deployed on cloud server: GPlan.x will be as follows
         * 0 0 0 0 1
         * 0 0 0 0 1
         * 0 0 0 0 1
         * 0 0 0 0 1
         */
        if (this.run==0) {
            short value0 = 0, value1 = 1;
        	ArrayFiller.fill2DArrayWithConstantNumber(GPlan.x, 0, nUM_SMART_THINGS[meType], 0, Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS, value0);
            //ArrayFiller.fill2DArrayWithConstantNumber(GPlan.x, 0, nUM_SMART_THINGS[meType], Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS - 1, Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS, value1);
            ArrayFiller.fill1DArrayWithConstantNumber(GPlan.deployPlan, 0, Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS, false);//false for all the edge/core nodes
            ArrayFiller.fill1DArrayWithConstantNumber(GPlan.deployPlan, Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS -1, Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS, true);//true for cloud
        }
        
        
        globalTraffic = new double[numst]; 
        
        ServiceTrafficPercentage = new double[numst];
        
        generateRandomPercentageForServiceTraffic();

        
        //VehiclesToAP = new short [numst][Constants.numTimeStamps];
        //short s = -1;
        //ArrayFiller.fill2DArrayWithConstantNumber(VehiclesToAP, s);//initialization with -1

        workload = new Workload(meType, nUM_SMART_THINGS[meType]); 
        
        System.out.println("Adding smart vehicles to the city...");
		addSmartThing();
		System.out.println("Number of configured smart vehicles : "+smartThings.size());
		
		if(mobilityType == 0)
			VehiclesToAP = Constants.VehiclesToAPDefault;
		else
			VehiclesToAP = Constants.VehiclesToAPOptimized;
		
		//readMobilityData();

		printPresenceInArea();
        
         
    	
    	
	}

	/**
     * Creates one agent per access point
     * @param list2 
     * @param list 
     */
    public void addAgents(List<FogDevice> serverCloudlets, List<ApDevice> apDevices) {
    	
    	int i;
    	for (i = 0; i< Constants.NUM_EDGE_ROUTERS; i++) {
    		Agent a = new Agent(serverCloudlets.get(i), serverCloudlets, apDevices, VehiclesToAP);
    		agents.add(i, a);
		}
    	System.out.println("Number of configured Agents : "+agents.size());
	
    	
	}

    /**
	 * @param id
	 * @param currTS
	 * @return assigns the service requests to the agents based on the access point (agent) id, checks whether the vehicle is present in the area 
	 * 		
	 */
	public List<MobileARservice> getServiceListById(int id, int currTS){
			List<MobileARservice>  agentServiceList = new ArrayList<MobileARservice>() ;
			for (MobileARservice m: workload.getServiceList()) {
	    		if ((m.getEdgeId() == id)&&(VehiclesToAP[m.getServicId()][currTS] != -1))
	    			agentServiceList.add(m);
	    	}
			
			//if (agentServiceList.size() != 0)
				//System.out.println("Service size for agent "+id+": "+ agentServiceList.size());
			
			return agentServiceList;
		}
	

	/**
	 * creates Constants.NUM_SMART_THINGS[type] number of smart things (e.g., vehicles) in the network
	 * @param smartThing
	 * @param coordDevices 
	 * @param j: number of smart things
	 */
	public void addSmartThing() {
		
		for (int i = 0; i < numst; i++) {
					
			int coordX = 0, coordY = 0;
			int direction, speed;
			direction = getivalue(0,Constants.MAX_DIRECTION - 1) + 1;
			speed = getivalue(0,Constants.MAX_SPEED - 1) + 1;
			
			MobileDevice st = null;

			try {
				st = new MobileDevice("SmartThing" + Integer.toString(i)
					, coordX, coordY, i// id = index in smartThings
					, direction, speed);
			
				getSmartThings().add(i, st);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}//end for

	
	}
	
	/**
	 * reads the smart things/users mobility dataset in the Mobility_Dataset directory
	 */
	private void readMobilityData() {

		String pathToMobProfs = Constants.mobilityDataset[methodType];
		File folder = new File(pathToMobProfs);
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles);
		System.out.println("Reading mobility profiles for "+getSmartThings().size()+" smart vehicles...");
		
		for (int i = 0; i < getSmartThings().size(); i++) {
			//System.out.println("st:"+ i+" mobility profile: "+listOfFiles[i].getName()+" size "+getSmartThings().size());
			readDevicePath(getSmartThings().get(i), pathToMobProfs + listOfFiles[i].getName());
			
			
		}
	}

	
	
	/** reads time-stamped travel path of each smart thing and saves the path
	 * @param st smart thing
	 * @param filename input travel path file which is a .csv file formatted as: 
	 * time-stamp,degree/angle,x,y,speed,id,connected access point,distance: 10440.00,201.86,14873.40,15714.60,12.01,4608,21,300
	 * be careful about the naming method: as the files are sorted by their name, hence their name must have a correct order. 
	 * we used numbers started from 100000 for the file names
	 */
	private void readDevicePath(MobileDevice st, String filename) {
		int id = st.getMyId();
		String line = "";
		String csvSplitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			while ((line = br.readLine()) != null) {
				String[] position = line.split(csvSplitBy);
				st.getPath().add(position);
				VehiclesToAP [id][(int)Double.parseDouble(position[0])] = Short.parseShort(position[6]);
			}

			Coordinate coordinate = new Coordinate();
			coordinate.setInitialCoordinate(st);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


    /**
	 * writes the mapping of smart things to access points during the experiment using AP_Veh.csv
	 */
	private void printPresenceInArea() {
		
		String CSV_SEPARATOR = ",";
    	
		System.out.println("Writing vehicles to APs mapping to file....");
				
		try {
	    	BufferedWriter csvWriter = new BufferedWriter(new FileWriter(Constants.NetOutFile+"AP_Veh"+methodType+".csv"));
	    	csvWriter.append("vehicle/timestamp").append(CSV_SEPARATOR);
	    	for(int j =0; j<Constants.numTimeStamps; j++) {
				csvWriter.append(j+"").append(CSV_SEPARATOR);//time-stamp
	    	}
				csvWriter.append(System.lineSeparator());
				
	    	for (int i = 0; i< numst; i++){
	    		csvWriter.append(i+"").append(CSV_SEPARATOR);//vehicle id
	    		for(int j =0; j<Constants.numTimeStamps; j++) {
					csvWriter.append(VehiclesToAP[i][j]+"").append(CSV_SEPARATOR);
						
		    	}
	    		
				csvWriter.append(System.lineSeparator());
	    	}
    	csvWriter.close();
    	}
		catch (Exception e) {
	        System.out.println("Error in FileWriter !!!");
	        e.printStackTrace();
	    }
    	
	}
    /**distributes the incoming traffic per node over the entire network
     * @param trafficPerNodePerService
     * @param profile
     */
    public void TrafficDist(double trafficPerNodePerService, int profile){
    	
    	workload.distributeTraffic(trafficPerNodePerService, Constants.TRAFFIC_DIST_METHOD, globalTraffic, profile); //creates services with the fixed traffic rate based on 15-minute profiles
        
    }
    
    /**distributes input traffic over the available smart vehicles
     * set on/off constraints based on the workload
     * @param timestamp
     * @param run2
     */
    public void CreateLoad(int timestamp, int run2) {
    	this.timestamp = timestamp;
    	workload.createApplication(getSmartThings(), VehiclesToAP, timestamp, run2);//distributes traffic based on current time (number of vehicles in the area):
	    currentVehiclesInAreaToPlan = workload.getServicesInArea();
	    //Constants.currentVehiclesInAreaToPlan[type] = currentVehiclesInAreaToPlan; 
	    System.out.println("Total number of smartthings: " + numst); 
        System.out.println("#smart things without request: "+workload.smarting_with_no_srv);
        System.out.println("#smart things with requests: "+currentVehiclesInAreaToPlan);
        
        //Constraint.setTrficDemand(run2, workload.totalCPU, workload.totalMem, workload.totalStorage);
		
        for (Agent a: agents)			//assigns currently available vehicles/services to their connected/closest access point (agent)
	    	a.arrivalServicesToFogNode(getServiceListById(a.getAgIndex(), timestamp));
    	
       }
    
    

	/**ServiceTrafficPercentage initialization
     * Generates random percentages for traffic rates of the services
     * weight[a] / sum;
     * the probabilities of a probability density function (PDF)
     */
    private void generateRandomPercentageForServiceTraffic() {
        ArrayFiller.fillRandomPDFInArray(ServiceTrafficPercentage);
    }

    public List<MobileDevice> getSmartThings() {
		return smartThings;
	}

	public void setSmartThings(List<MobileDevice> smartThings) {
		this.smartThings = smartThings;
	}

	private static int getivalue(int min, int max)//change to random generator
	{
		Random r = new Random();
		int randomValue = min + r.nextInt()%(max - min);
		return randomValue;
	}
	
	public List<Agent> getAgents() {
		return agents;
	}

	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}
}
