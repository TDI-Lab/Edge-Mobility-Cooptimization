package EdgeEPOS.Setting;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import fog.entities.Host;
import fog.entities.Pe;
import fog.entities.ApDevice;

import fog.entities.FogDevice;
import fog.entities.MobileDevice;
import org.fog.localization.Coordinate;
import org.fog.localization.Distances;

import org.fog.vmmobile.constants.MaxAndMin;
import org.fog.vmmobile.constants.Policies;
import org.fog.vmmobile.constants.Services;

import com.snatik.polygon.Point;

import EdgeEPOS.City.City;
import EdgeEPOS.City.CityMap;
import EdgeEPOS.PlacementMethods.Agent;
import EdgeEPOS.Utility.RandomGenerator;
import EdgeEPOS.Utility.Utility;



/**
 * @author znb_n
 * this class manages network infrastructure including: access points, cloudlets, agents, and network links
 * 
 */
public class Network {

	private static int seed;
	private static Random rand;

	//private List<MobileDevice> smartThings = new ArrayList<MobileDevice>();
	private List<FogDevice> serverCloudlets = new ArrayList<>();
	private List<ApDevice> apDevices = new ArrayList<>();
	private Coordinate coordDevices;
	
	public class CPoint {
	    public int x;
	    public int y;
	}
	CPoint[]  NodesLoc = new CPoint[Constants.numNodes];
	
	/**
	 * sets random variable
	 * @param seed
	 */
	public Network(int seed) {
		
		setSeed(seed);

		if (getSeed() < 1) {
			System.out.println("Seed cannot be less than 1");
			System.exit(0);
		}
		setRand(new Random(getSeed() * Integer.MAX_VALUE));

		try {
			createNetwork();
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}
	
	/**creates network devices and agents, them connects them according to their distance and coverage range
	 * Finally prints the network capacity
	 * @throws Exception
	 */
	private void createNetwork() throws Exception {
		
		System.out.println("\n--------------------------------Network Configuration--------------------------------");
		
		createNetNodes();
		System.out.println("Number of configured network nodes (access points + core routers + cloud center) : "+apDevices.size());
		
		createServers();
		
		System.out.println("Number of configured fog servers : "+serverCloudlets.size());
		
		Utility.printNetCapacity(apDevices, serverCloudlets);
		
	}

	
	
		/**
	 * Creates createNetNodes based on the parameter PositionApPolicy.
	 * if its equal to FIXED_AP_LOCATION, nodes are created using fixed location (file)
	 * otherwise they will be created with random locations.
	 */
	public void createNetNodes() {
		if (Constants.PositionApPolicy == Constants.FIXED_AP_LOCATION) 
			addFixedDevices();
		else 
			addApDevicesRandom();
		
	}
	
	/**
	 * Creates network Devices according to positionApPolicy.
	 * three types of devices are added: edge routers (co-located with access points), core routers, and one cloud node.  
	 */
	public void addFixedDevices() {
		System.out.println("Creating fixed access points...");
		addFixedEdgeRouters();
		System.out.println("Creating fixed core routers and cloud center...");
		addFixedCoreRouters();
		
	}
	
	
	/**
	 * Adds Constants.NUM_EDGE_ROUTERS edge routers to the network using OpenCellID dataset
	 * We assume a network of LTE access points with the coverage range of Constants.AP_COVERAGE. 
	 *
	 */
	public void addFixedEdgeRouters(){
		
		int i = 0;
		Point point;//to double check if the access points (Cartesian points) are located in the city
		int coordX = 0;
	 	int coordY = 0;
	 	String line;
		String CSV_SEPARATOR = ",";
		
		try {
			
			  LineNumberReader csvReader = new LineNumberReader(new FileReader(Constants.APFile)); 
			  while (((line =  csvReader.readLine()) != null)&&(i < Constants.NUM_EDGE_ROUTERS)) {
				  String[] data = line.split(CSV_SEPARATOR);
		  		  
		  		  if (data.length<1) {
		  			System.out.println("unknown AP location in input file");
		  			  continue;
		  		  }
			    
		  		  point = new Point(Double.parseDouble(data[0]),Double.parseDouble(data[1]));
		  		  coordX = Integer. parseInt(data[0]);
		  		  coordY = Integer. parseInt(data[1]);
		  		  NodesLoc[i] = new CPoint(); 
		  		  NodesLoc[i].x = coordX;
		  		  NodesLoc[i].y = coordY;
		  		
		  		  if(City.isInside(point)) { 
					   ApDevice ap = new ApDevice("AccessPoint" + Integer.toString(i),  //name
						coordX, coordY, i												//id
						, Constants.rIFC_Down[i]										//Mbits, 100 * 1024 * 1024 downLinkBandwidth : 100Mbits
						, Constants.Net_Pow_ul[i]							//initial engergyConsumption
						, Constants.MAX_ST_IN_AP										// maxSmartThing: set to 500 
						, Constants.rIFC_Up[i] 											//Mbits, 100 * 1024 * 1024 upLinkBandwidth : 100Mbits
						, Constants.upLinkLat[i]										//upLinkLatency + delay for smart things --> second
					);
					apDevices.add(i, ap);
					i++;
				}
		  		else {
		  		 
		  			System.out.println("Edge router "+i+" "+coordX+" "+coordY+" is out of the selected area, debug!");
		  			
		  		}
		  		 
			  }
			  csvReader.close();
			  
		}
		  catch (Exception e) {
		  System.out.println("Error in FileReader !!!"); 
		  e.printStackTrace(); 
		  }
		 	
	System.out.println("Number of access points (edge routers): "+i);
	

	}

	/**
	 * adds Constants.NUM_BACKBONE_ROUTERS (core routers + one cloud node) located based on Munich scientific network
	 * note that no smart thing can connect to the core routers or cloud nodes directly
	 */
	public void addFixedCoreRouters(){
	
		int i = 0;
		int index = apDevices.size();
		int coordX = 0, coordY = 0;
		String line;
		ApDevice ap;
		String CSV_SEPARATOR = ","; 
		try {
			
			  LineNumberReader csvReader = new LineNumberReader(new FileReader(Constants.CRFile)); 
			  while (((line =  csvReader.readLine()) != null)&&(i < Constants.NUM_BACKBONE_ROUTERS)) {
				  
				  String[] data = line.split(CSV_SEPARATOR);
		  		  
		  		  if (data.length<1) {
		  			System.out.println("unknown core router location in input file");
		  			  continue;
		  		  }
			    
			  		coordX = Integer. parseInt(data[0]);coordY = Integer. parseInt(data[1]);
			  		NodesLoc[index] = new CPoint(); 
			  		NodesLoc[index].x = coordX;
			  		NodesLoc[index].y = coordY;
			  		
					//if(City.isInside(point)) { cloud centre is out of the city
					if (i ==  (Constants.NUM_BACKBONE_ROUTERS - 1)) {
						System.out.println("Creating cloud center!");
						
					    ap = new ApDevice("AccessPoint" + Integer.toString(i),  //cloud
						coordX, coordY, index 									//id
						, Constants.rIFC_Down[index]							//Mbps, 100 * 1024 * 1024 downLinkBandwidth - 100Mbits
						, Constants.Net_Pow_ul[index]				//engergyConsumption
						, Constants.MIN_ST_IN_AP								//set to zero thus can not connect to smart things
						, Constants.rIFC_Up[index]								//Mbps, 100 * 1024 * 1024 upLinkBandwidth - 100Mbits
						, Constants.upLinkLat[index]							//upLinkLatency for core router 
					);
					}
					else {
						 System.out.println("Creating core router "+i);
						 ap = new ApDevice("AccessPoint" + Integer.toString(i), //core router
									coordX, coordY, index 						//id
									, Constants.rIFC_Down[index]				//Mbps, 100 * 1024 * 1024 downLinkBandwidth - 100Mbits
									, Constants.Net_Pow_ul[index]	//engergyConsumption
									, Constants.MIN_ST_IN_AP					//set to zero thus can not connect to smart things
									, Constants.rIFC_Up[index]					//Mbps, 100 * 1024 * 1024 upLinkBandwidth - 100Mbits
									, Constants.upLinkLat[index]				//upLinkLatency
								);
					}
				  // System.out.println("core router "+index+" "+coordX+" "+coordY);
				  // System.out.println("ap index: "+index);
					
				apDevices.add(index, ap);
				//System.out.println("ap i: "+i+" "+apDevices.get(index).getMyId());
				
				index++;
				i++;
			}
			
			csvReader.close();
		  
		}
		catch (Exception e) {
		System.out.println("Error in FileReader !!!"); 
		e.printStackTrace(); 
		}
		
		System.out.println("Number of core routers and cloud centers: "+i );
		
	}



	
	/**
	 * creates server_cloudlets/fog_devices co-located with access points
	 */
	public void createServers() {
		if (Constants.PoitionSCPolicy == Constants.FIXED_SC_LOCATION) {
			addFixedServerCloudlet(serverCloudlets, coordDevices);
			} 
		
	}
	
    
	
	/**creates fog servers
	 * @param serverCloudlets totalserverCloudlets = CoreserverCloudlets + EdgeserverCloudlets;
	 * @param coordDevices
	 */
	public void addFixedServerCloudlet(List<FogDevice> serverCloudlets, Coordinate coordDevices) {
		System.out.println("Creating fog servers (cloudlets)...");
		
		System.out.println("Number of fog server (cloudlets) at edges created: " + addServerCloudletEdgeServers(serverCloudlets, coordDevices));
		
		System.out.println("Number of fog server (cloudlets) at core created: " + addServerCloudletCoreCenters(serverCloudlets, coordDevices));
		
	}
	
	/**
	 * adds fog servers at the edge (access points) network
	 * @param serverCloudlets
	 * @param coordDevices
	 * @param m
	 */
	public int addServerCloudletEdgeServers(List<FogDevice> serverCloudlets, Coordinate coordDevices) { 
		int i = 0;
		int coordX, coordY;
		double procCapacity = 0;
		
		while (i<Constants.NUM_EDGE_ROUTERS) {
			coordX = NodesLoc[i].x;
			coordY = NodesLoc[i].y;
			
			int coreCount = Constants.nCore[i];			//number of processing cores
			procCapacity = Constants.FP[i]/coreCount;	//mips per core
			List<Pe> peList = new ArrayList<Pe>();
			int counter = 0;
			
			while (counter < coreCount) {
				peList.add(new Pe(counter, procCapacity));
				counter ++;
			}
				
				int hostId = i;
				long storage = Constants.FS[i]  ;				//Mbit, host storage MB --->Mbit
				long bw = Constants.rIFC_Down[i];				//bps, 1000 * 1024 * 1024;//Megabits/s
				int ram = Constants.FM[i]  ;					//Mbit, host memory (MB)  -->Mbit
				
				Host host = new Host(hostId, ram, bw, storage, peList, Constants.SC_MaxPow[i], Constants.SC_IdlePow[i]);
	

				FogDevice sc = null;
				
				try {
					sc = new FogDevice("ServerCloudlet" + Integer.toString(i) 	//name
						, host
						, Constants.rIFC_Up[i]									//Mbps, uplinkBandwidth
						, Constants.rIFC_Down[i]								//Mbps, downlinkBandwidth
						, Constants.upLinkLat[i]								//second 4, uplinkLatency: refers to the time it takes for data to travel from the fog server to another point in the network, typically to a higher-level server or data center. (1,2) (15,35)???
						, 0.01													//rate per mips
						, coordX, coordY, i
						);
					
					serverCloudlets.add(i, sc);
					i++;
				
				} 
				catch (Exception e) {
					e.printStackTrace();
				}

			
		}
		
		return i;
	}
	
	/**
	 * creates fog servers for core routers and cloud
	 * @param serverCloudlets
	 * @param coordDevices
	 */
	public int addServerCloudletCoreCenters(List<FogDevice> serverCloudlets, Coordinate coordDevices) {
		
		
		int index = serverCloudlets.size();
		int i = index; int j = 0; 
		int coordX, coordY;

		List<Pe> peList;
		double procCapacity = 0 ;
		
		while (j<Constants.NUM_BACKBONE_ROUTERS) {//we do not check whether the location is inside the area or not; the cloud is not absolutely
				coordX = NodesLoc[i].x;
				coordY = NodesLoc[i].y;
			
				int coreCount = Constants.nCore[index];
				procCapacity = Constants.FP[index]/coreCount;//mips
				peList = new ArrayList<Pe>();
				int counter = 0;
				while (counter < coreCount) {
					peList.add(new Pe(counter, procCapacity));
					counter ++;
				}
					
				int hostId = index;
				long storage = Constants.FS[index] ;			//host storage MBit
				long bw = Constants.rIFC_Down[index];			//Mbps, 1000 * 1024 * 1024;//Megabits/s
				int ram = Constants.FM[index] ;					//host memory (MBit)
				
				Host host = new Host(hostId, ram, bw, storage, peList, Constants.SC_MaxPow[i], Constants.SC_IdlePow[i]);
				FogDevice sc = null;
			
				try {
					sc = new FogDevice("ServerCloudlet" + Integer.toString(i) // name
						, host
						, Constants.rIFC_Up[index]					//Mbps, uplinkBandwidth
						, Constants.rIFC_Down[index]				//Mbps, downlinkBandwidth
						, Constants.upLinkLat[i]					//4, uplinkLatency
						, 0.01										// mipsPer..
						, coordX, coordY, index);
					
					serverCloudlets.add(index++, sc);
			
					
					j++;
					
				} catch (Exception e) 
				{
					e.printStackTrace();
				}
				i++;		
		}
		
		
		return j;
	}
		
		
	/**
	 * it makes connection between SmartThings and closest AccessPoints
	 * and then connection between every AccessPoint and its closest ServerCloudlet
	 * and for each connected cloudlet creates symbolic link between smartThing and the cloudlet
	 * 
	 */
	public void createConnections(){
		int index, index1;// Auxiliary
		int myCount = 0;

		System.out.println("Create connection between Smart Things and Access Points...");
//		for (MobileDevice st : getSmartThings()) {
//			
//			if (!ApDevice.connectApSmartThing(getApDevices(), st, getRand().nextDouble())) {//delay not important, the delay calculated using the distance * Constants.AirVelFact?? dAF
//				myCount++;
//				System.out.println(st.getName()+" id: "+st.getMyId()+" not connected to APs "+ "coord_x: "+st.getCoord().getCoordX()+" coord_y: "+st.getCoord().getCoordY());
//				}
//			else {
//				//System.out.println("connected to APs, name: "+st.getName()+" id: "+st.getId()+" coord_x: "+st.getCoord().getCoordX()+" coord_y: "+st.getCoord().getCoordY());
//		    }
//		}
//		
		
		
		System.out.println("Create connection between Cloudlets and Access Points...");
		for (ApDevice ap : getApDevices()) {
			index = ap.getMyId();
			System.out.println("ap Id : "+index);
			//index = Distances.theClosestServerCloudletToAp(getServerCloudlets(), ap);//id
			//System.out.println("index : "+index);
			  ap.setServerCloudlet(getServerCloudlets().get(index));//??imp
			  getServerCloudlets().get(index).setApDevices(ap, Policies.ADD);//??imp
			 
			 //System.out.println("Creating connection between Smart Things and Cloudlets...");
				/*
				 * for (MobileDevice st : ap.getSmartThings()) {
				 * getServerCloudlets().get(index).connectServerCloudletSmartThing(st);
				 * getServerCloudlets().get(index).setSmartThingsWithVm(st, Policies.ADD);
				 * //getAgents().get(index).connectAgentwithSmartThing(st);
				 * System.out.println("ap id: "+ap.getMyId()+" st id: "+st.getMyId()
				 * +" cloudlet id: "+index);
				 * 
				 * }
				 */
		}
			
	}

	private int getindex(FogDevice fd) {
		System.out.println("size "+serverCloudlets.size());
		for (int f = 0; f<serverCloudlets.size(); f++) {
			if (serverCloudlets.get(f).getMyId() == fd.getMyId()) {
				System.out.println("apidIndex "+f);
				return f;
			}
 		}
		System.out.println("null");
		return -1;
	}



	private static int getivalue(int min, int max)//change to random generator
	{
		Random r = new Random();
		int randomValue = min + r.nextInt()%(max - min);
		return randomValue;
	}
	
	
	public List<FogDevice> getServerCloudlets() {
		return serverCloudlets;
	}

	public void setServerCloudlets(List<FogDevice> serverCloudlets) {
		this.serverCloudlets = serverCloudlets;
	}
	
	public List<ApDevice> getApDevices() {
		return apDevices;
	}

	public void setApDevices(List<ApDevice> apDevices) {
		this.apDevices = apDevices;
	}

	public static Random getRand() {
		return rand;
	}
	public static void setRand(Random rand) {
		Network.rand = rand;
	}

	public static int getSeed() {
		return seed;
	}

	public static void setSeed(int seed) {
		Network.seed = seed;
	}
	/**
	 * currently not used
	 */
	private void addApDevicesRandom() {
		int coordX, coordY;
		int i = 0;
		while (i < Constants.NUM_EDGE_ROUTERS){
			coordX = getivalue(Constants.MIN_X,Constants.MAX_X);
			coordY = getivalue(Constants.MIN_Y, Constants.MAX_Y);
			Point point = new Point(coordX,coordY);
	        
			if(City.isInside(point)) { 
				ApDevice ap = new ApDevice("AccessPoint" + Integer.toString(i), // name
				coordX, coordY, i// id
				, 100 * 1024 * 1024// downLinkBandwidth - 100 Mbits
				, 200// engergyConsumption
				, Constants.MAX_ST_IN_AP// maxSmartThing
				, 100 * 1024 * 1024// upLinkBandwidth 100 Mbits
				, 4// upLinkLatency
			);
				apDevices.add(i, ap);
				i++;
			}
		}
		
		int j = 0;
		while (j < Constants.NUM_BACKBONE_ROUTERS){
			coordX = getivalue(Constants.MIN_X,Constants.MAX_X);
			coordY = getivalue(Constants.MIN_Y, Constants.MAX_Y);
			Point point = new Point(coordX,coordY);
	        
			if(City.isInside(point)) { 
				ApDevice ap = new ApDevice("AccessPoint" + Integer.toString(i), // name
				coordX, coordY, i// id
				, 100 * 1024 * 1024// downLinkBandwidth - 100 Mbits
				, 200// engergyConsumption
				, Constants.MIN_ST_IN_AP// maxSmartThing
				, 100 * 1024 * 1024// upLinkBandwidth 100 Mbits
				, 4// upLinkLatency
			);
			
			//System.out.println("x: "+coordX+" y: "+coordY+" i: "+i);
		apDevices.add(i, ap);
		j++;
		i++;
		}
	}
	}

	
	private FogDevice getServerCloudletById(int scId) {
		// TODO Auto-generated method stub
		for (FogDevice fd : serverCloudlets) {
			if (fd.getMyId() == scId)
				return fd;
 		}
		
		System.out.println("null");
		
		return null;
	}
}
