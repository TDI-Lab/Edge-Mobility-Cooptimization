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
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.NetworkTopology;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;
import org.cloudbus.cloudsim.sdn.overbooking.BwProvisionerOverbooking;
import org.cloudbus.cloudsim.sdn.overbooking.PeProvisionerOverbooking;
import org.fog.entities.ApDevice;
import org.fog.entities.FogBroker;
import org.fog.entities.FogDevice;
import org.fog.entities.FogDeviceCharacteristics;
import org.fog.entities.MobileDevice;
import org.fog.localization.Coordinate;
import org.fog.localization.Distances;
import org.fog.policy.AppModuleAllocationPolicy;
import org.fog.scheduler.StreamOperatorScheduler;
import org.fog.utils.FogLinearPowerModel;
import org.fog.utils.FogUtils;
import org.fog.utils.distribution.DeterministicDistribution;
import org.fog.vmmigration.BeforeMigration;
import org.fog.vmmigration.CompleteVM;
import org.fog.vmmigration.ContainerVM;
import org.fog.vmmigration.DecisionMigration;
import org.fog.vmmigration.LiveMigration;
import org.fog.vmmigration.LowestDistBwSmartThingAP;
import org.fog.vmmigration.LowestDistBwSmartThingServerCloudlet;
import org.fog.vmmigration.LowestLatency;
import org.fog.vmmigration.PrepareCompleteVM;
import org.fog.vmmigration.PrepareContainerVM;
import org.fog.vmmigration.PrepareLiveMigration;
import org.fog.vmmigration.Service;
import org.fog.vmmigration.VmMigrationTechnique;
import org.fog.vmmobile.LogMobile;
import org.fog.vmmobile.constants.MaxAndMin;
import org.fog.vmmobile.constants.Policies;
import org.fog.vmmobile.constants.Services;

import com.snatik.polygon.Point;

import EdgeEPOS.AppExample2;
import EdgeEPOS.City.City;
import EdgeEPOS.City.CityMap;
import EdgeEPOS.PlacementMethods.Agent;
import EdgeEPOS.Utility.RandomGenerator;



/**
 * @author rooyesh
 *This file creates and all components of a network, 
 *including access points, routers, servers, smart things, brokers, and agents,
 *and then connect them together
 */

/**
 * @author rooyesh
 *
 */
public class Network2 {

	private static int seed;
	private static Random rand;
	
	private List<MobileDevice> smartThings = new ArrayList<MobileDevice>();
	private List<FogDevice> serverCloudlets = new ArrayList<>();
	private List<FogBroker> brokerList = new ArrayList<>();
	private List<ApDevice> apDevices = new ArrayList<>();
	private Coordinate coordDevices;
	private List <Agent> agents = new ArrayList<>();
	
	public class CPoint {
	    public int x;
	    public int y;
	}
	CPoint[]  NodesLoc = new CPoint[500];
	
	public Network2(int seed) {
		
		setSeed(seed);

		if (getSeed() < 1) {
			System.out.println("Seed cannot be less than 1");
			System.exit(0);
		}
		setRand(new Random(getSeed() * Integer.MAX_VALUE));

		try {
			createNetwork();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	private void createNetwork() throws Exception {
		
		System.out.println("--------------------------------");
		System.out.println("Network Configuration");
		createAP();
		System.out.println("Number of network nodes configured: "+apDevices.size());
		
		//for (int i = 0; i<apDevices.size(); i++)
			//System.out.println("ap i "+i+" myid "+apDevices.get(i).getMyId());
			
		createServersAndAgents();
		
		System.out.println("number of Server Cloudlets configured: "+serverCloudlets.size());
		
		//for (int i = 0; i<serverCloudlets.size(); i++)
			//System.out.println("cs i "+i+" myid "+serverCloudlets.get(i).getMyId());
		
		createServerCloudletsNetwork();
		System.out.println("Server Cloudlet Network configured");
		
		addSmartThing();
		System.out.println("Number of Smart Things configured: "+smartThings.size());
		
		readMoblityData();
		
		CreateConnections();
		
		createBrokers();
		System.out.println("number of Brokers configured: "+brokerList.size());
		
		
	}
	
	
	public void createAP() {
		if (Constants.PositionApPolicy == Constants.FIXED_AP_LOCATION) 
			addApDevicesFixed();
		else 
			addApDevicesRandom();
		
	}
	
	/**
	 * Creates Access Points. It creates Access Points according positionApPolicy 
	 * A general rule of thumb in home networking says that Wi-Fi routers operating on the 
	 * traditional 2.4 GHz band reach up to 150 feet (46 m) indoors and 300 feet (92 m) outdoors. 
	 * Older 802.11a routers that ran on 5 GHz bands reached approximately one-third of these distances.
	 * LTE coverage range.....
	 */
	public void addApDevicesFixed() {
		System.out.println("Creating fixed access points...");
						
		addFixedEdgeRouters();
		addFixedCoreRouters();
		
		System.out.println("NodesLoc "+NodesLoc.length);
		
	}
	
	
	public void addFixedEdgeRouters(){
		
		int i = 0;
		Point point;
		//double area = Constants.area;
		//472 total routers for selected area of Munich
		
	 	int coordX = 0;
	 	int coordY = 0;
	 	String line;
		String CSV_SEPARATOR = ","; // it could be a comma or a semi colon
		try {
			
			  LineNumberReader csvReader = new LineNumberReader(new FileReader(Constants.APFile)); 
			  while (((line =  csvReader.readLine()) != null)&&(i < Constants.EDGE_ROUTERS)) {
				  String[] data = line.split(CSV_SEPARATOR);
		  		  
		  		  if (data.length<1) {
		  			System.out.println("unknown AP location in input file");
		  			  continue;
		  		  }
			    
		  		point = new Point(Double.parseDouble(data[0]),Double.parseDouble(data[1]));
		  		coordX = Integer. parseInt(data[0]);coordY = Integer. parseInt(data[1]);
		  		NodesLoc[i] = new CPoint(); 
		  		NodesLoc[i].x = coordX;
		  		NodesLoc[i].y = coordY;
		  		
		  		if(City.isInside(point)) { 
					   ApDevice ap = new ApDevice("AccessPoint" + Integer.toString(i), // name
						coordX, coordY, i//id
						, Constants.rIFC_Down[i]//100 * 1024 * 1024 downLinkBandwidth - 100Mbits
						, 200// engergyConsumption
						, Constants.MAX_ST_IN_AP// maxSmartThing: set to 500 
						, Constants.rIFC_Down[i]//100 * 1024 * 1024 upLinkBandwidth - 100Mbits
						, 4// upLinkLatency + delay for smart things
					);
					apDevices.add(i, ap);
					i++;
				}
		  		
		  		  else {
		  		 
		  			System.out.println("Edge router "+i+" "+coordX+" "+coordY+" is outside the selected area");
		  			
		  		}
		  		 
			  }
			  csvReader.close();
			  
		}
		  catch (Exception e) {
		  System.out.println("Error in FileReader !!!"); e.printStackTrace(); 
		  }
		 	
	System.out.println("Number of access points (edge routers) created: "+i);
	LogMobile.debug("EdgeEpos.java", "Total of accessPoints: " + i);

	}

	//Downlink is a telecommunication term pertaining to data which is sent out or downwards 
	//from a higher level or portion of a network.
	//The uplink frequency is higher than that of the down.
	
	public void addFixedCoreRouters(){
	
		int i = 0;
		int index = apDevices.size();
		Point point;
		//double CoreRouterDistance = 2*Math.sqrt((area/Constants.BACKBONE_ROUTERS)/Math.PI)-City.CoreRouterDistanceModifier;
	 	int coordX = 0, coordY = 0;
		int smartthingsPerAP = 0;//Constants.MAX_ST_IN_AP;
		String line;
		String CSV_SEPARATOR = ","; // it could be a comma or a semi colon
		try {
			
			  LineNumberReader csvReader = new LineNumberReader(new FileReader(Constants.CRFile)); 
			  while (((line =  csvReader.readLine()) != null)&&(i < Constants.BACKBONE_ROUTERS)) {
				  String[] data = line.split(CSV_SEPARATOR);
		  		  
		  		  if (data.length<1) {
		  			System.out.println("unknown core router location in input file");
		  			  continue;
		  		  }
			    
		  		point = new Point(Double.parseDouble(data[0]),Double.parseDouble(data[1]));
		  		coordX = Integer. parseInt(data[0]);coordY = Integer. parseInt(data[1]);
		  		NodesLoc[index] = new CPoint(); 
		  		NodesLoc[index].x = coordX;
		  		NodesLoc[index].y = coordY;
		  		
				//if(City.isInside(point)) { 
					//core ap is a cloud center and does not allow to be connected to	
					if (i ==  (Constants.BACKBONE_ROUTERS - 1)) {
						System.out.println("Cloud node creation");
					}
					
					   ApDevice ap = new ApDevice("AccessPoint" + Integer.toString(i), // name
						coordX, coordY, index// id
						, Constants.rIFC_Down[index]//100 * 1024 * 1024 downLinkBandwidth - 100Mbits
						, 200// engergyConsumption
						, smartthingsPerAP//?? minSmartThing: set to zero thus can not connect to smart things
						, Constants.rIFC_Down[index]//100 * 1024 * 1024 upLinkBandwidth - 100Mbits
						, 4// upLinkLatency
					);
					  // System.out.println("core router "+index+" "+coordX+" "+coordY);
					  // System.out.println("ap index: "+index);
						
					apDevices.add(index, ap);
					//System.out.println("ap i: "+i+" "+apDevices.get(index).getMyId());
					
					index++;
					i++;
				}
				
			//}
			
		 
		  csvReader.close();
		  
		}
		catch (Exception e) {
		System.out.println("Error in FileReader !!!"); e.printStackTrace(); 
		}
		
		System.out.println("Number of core routers created: "+i );
		LogMobile.debug("EdgeEpos.java", "Total of accessPoints: " + i);
	}

	/**
	 * evenly distributes edge routers that can be used as access points
	 */
	public void addEdgeRouters(){
		
		int i = 0;
		Point point;
		double area = Constants.area;
		//5332 total routers of annapolis
		double edgeRouterDistance = 2*Math.sqrt((area/Constants.EDGE_ROUTERS)/Math.PI)-City.EdgeRouterDistanceModifier;
		
	 	int coordY = 0;
		for (int coordX = Constants.MIN_X; coordX < Constants.MAX_X && i<Constants.EDGE_ROUTERS; coordX += edgeRouterDistance) { 
			
			for (coordY = Constants.MIN_Y; coordY < Constants.MAX_Y && i<Constants.EDGE_ROUTERS; coordY += edgeRouterDistance) {
					
				point = new Point(coordX,coordY);
		        
				if(City.isInside(point)) { 
					   ApDevice ap = new ApDevice("AccessPoint" + Integer.toString(i), // name
						coordX, coordY, i//id
						, Constants.rIFC_Down[i]//100 * 1024 * 1024 downLinkBandwidth - 100Mbits
						, 200// engergyConsumption
						, Constants.MAX_ST_IN_AP// maxSmartThing: set to 500 
						, Constants.rIFC_Down[i]//100 * 1024 * 1024 upLinkBandwidth - 100Mbits
						, 4// upLinkLatency + delay for smart things
					);
					apDevices.add(i, ap);
					System.out.println("edge router "+i+" "+coordX+" "+coordY);
					  
					i++;
				}
				
			}
			
			
		}
		System.out.println(i+" number of edge router/access point is created with distance: "+edgeRouterDistance);
		LogMobile.debug("EdgeEpos.java", "Total of accessPoints: " + i);


	}

	
	/**
	 * evenly distributes core routers which can not be used as access points
	 */
	public void addCoreRouters(){
		
		int i = 0;
		int index = apDevices.size();
		Point point;
		double area = Constants.area;
		double CoreRouterDistance = 2*Math.sqrt((area/Constants.BACKBONE_ROUTERS)/Math.PI)-City.CoreRouterDistanceModifier;
	 	int stPerAP;
		int coordY = 0;
		
		for (int coordX = Constants.MIN_X; coordX < Constants.MAX_X && i<Constants.BACKBONE_ROUTERS; coordX += CoreRouterDistance) { /* evenly distributed */
			
			for (coordY = Constants.MIN_Y; coordY < Constants.MAX_Y && i<Constants.BACKBONE_ROUTERS; coordY += CoreRouterDistance) {
				
				point = new Point(coordX,coordY);
		        
				if(City.isInside(point)) { 
					//last ap is a cloud center and does not allow to be connected to	
					if (i ==  (Constants.BACKBONE_ROUTERS - 1)) {
							stPerAP = Constants.MAX_ST_IN_AP;//???
							//System.out.println("here core cloud");
					}
						else {
							stPerAP = Constants.MAX_ST_IN_AP;
							//System.out.println("here core ap");
						}
					
					   ApDevice ap = new ApDevice("AccessPoint" + Integer.toString(i), // name
						coordX, coordY, index// id
						, Constants.rIFC_Down[index]//100 * 1024 * 1024 downLinkBandwidth - 100Mbits
						, 200// engergyConsumption
						, stPerAP//?? minSmartThing: set to zero thus can not connect to smart things
						, Constants.rIFC_Down[index]//100 * 1024 * 1024 upLinkBandwidth - 100Mbits
						, 4// upLinkLatency
					);
					   System.out.println("core router "+index+" "+coordX+" "+coordY);
					  // System.out.println("ap index: "+index);
						
					apDevices.add(index, ap);
					//System.out.println("ap i: "+i+" "+apDevices.get(index).getMyId());
					
					index++;
					i++;
				}
				
			}
			
		}
		
		System.out.println(i+ " core routers are created "+" with distance: "+CoreRouterDistance);
		LogMobile.debug("EdgeEpos.java", "Total of accessPoints: " + i);


	}

	
	/**
	 * creates server_cloudlets/fog_devices located close to the access points
	 */
	public void createServersAndAgents() {
		if (Constants.PoitionSCPolicy == Policies.FIXED_SC_LOCATION) {
			addFixedServerCloudlet(serverCloudlets, coordDevices);
			addAgents();
		} 
		else {
			
			for (int i = 0; i < MaxAndMin.MAX_SERVER_CLOUDLET; i++) {
				addRandomServerCloudlet(i);
				addAgents();
			}
		}
		
		
	}
	
    /**
     * creates one agent per serverCloudlet
     */
    private void addAgents() {
    	int i = 0;
    	for (FogDevice sc : getServerCloudlets()) {
    		Agent a = new Agent(sc, serverCloudlets, apDevices);
    		agents.add(i, a);
			i++;
			
		}
    	System.out.println("number of Agents configured: "+agents.size());
				
	}

	
	/**create edge/fog servers
	 * @param serverCloudlets totalserverCloudlets = CoreserverCloudlets + EdgeserverCloudlets;
	 * @param coordDevices
	 */
	public void addFixedServerCloudlet(List<FogDevice> serverCloudlets, Coordinate coordDevices) {
		System.out.println("Creating Server cloudlets........");
		
		System.out.println("Number of edge server cloudlets created: " + addServerCloudletEdgeServers(serverCloudlets, coordDevices));
		
		System.out.println("Number of core server cloudlets created: " + addServerCloudletCoreCenters(serverCloudlets, coordDevices));
		
	}
	
	/**
	 * add fog/edge servers at the edge network evenly distributed 
	 * @param serverCloudlets
	 * @param coordDevices
	 * @param m
	 */
	public int addServerCloudletEdgeServers(List<FogDevice> serverCloudlets, Coordinate coordDevices) { 
		int i = 0;
		int coordX, coordY;
		Point point;
		//double area = Constants.area;
		double procCapacity = 0;
		//double edgeRouterDistance = 2*Math.sqrt((area/Constants.EDGE_ROUTERS)/Math.PI)-City.EdgeRouterDistanceModifier;
		
		//for (coordX = Constants.MIN_X; coordX < Constants.MAX_X && i<Constants.EDGE_ROUTERS; coordX += edgeRouterDistance) { 
		//	for (coordY = Constants.MIN_Y; coordY < Constants.MAX_Y && i<Constants.EDGE_ROUTERS; coordY += edgeRouterDistance) { 
		while (i<Constants.EDGE_ROUTERS) {
			coordX = NodesLoc[i].x;
			coordY = NodesLoc[i].y;
			point = new Point(coordX, coordY);
		        
			if(City.isInside(point)) {
						DecisionMigration migrationStrategy;
					if (Constants.MigStrategyPolicy == Policies.LOWEST_LATENCY) {
						migrationStrategy = new LowestLatency(getServerCloudlets(),	getApDevices(), Constants.MigPointPolicy, Constants.PolicyReplicaVM);
					} else if (Constants.MigStrategyPolicy == Policies.LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET) {
						migrationStrategy = new LowestDistBwSmartThingServerCloudlet(getServerCloudlets(), getApDevices(),
								Constants.MigPointPolicy, Constants.PolicyReplicaVM);
					} else { // LOWEST_DIST_BW_SMARTTING_AP
						migrationStrategy = new LowestDistBwSmartThingAP(getServerCloudlets(), getApDevices(),
								Constants.MigPointPolicy, Constants.PolicyReplicaVM);
					}

					BeforeMigration beforeMigration = null;
					if (Constants.PolicyReplicaVM == Policies.MIGRATION_COMPLETE_VM) {
						beforeMigration = new PrepareCompleteVM();
					} else if (Constants.PolicyReplicaVM == Policies.MIGRATION_CONTAINER_VM) {
						beforeMigration = new PrepareContainerVM();
					} else if (Constants.PolicyReplicaVM == Policies.LIVE_MIGRATION) {
						beforeMigration = new PrepareLiveMigration();
					}

					FogLinearPowerModel powerModel = new FogLinearPowerModel(107.339d, 83.433d);

					//fog servers have 4 pElements.
					procCapacity = Constants.FP[i]/4;
					List<Pe> peList = new ArrayList<Pe>();
					peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
					peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
					peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
					peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
					
                  
					int hostId = FogUtils.generateEntityId();
					long storage = Constants.FS[i];//16 * 1024 * 1024;// host storage MB
					long bw = Constants.rIFC_Down[i];//1000 * 1024 * 1024;//Megabits/s
					int ram = Constants.FM[i];//1024; host memory (MB)
					// To the hardware's characteristics (MobileDevice) - to CloudSim
					PowerHost host = new PowerHost(hostId, new RamProvisionerSimple(ram),
						new BwProvisionerOverbooking(bw), storage, peList,
						new StreamOperatorScheduler(peList), powerModel);

				List<Host> hostList = new ArrayList<Host>();
				hostList.add(host);

				String arch = "x86"; // system architecture
				String os = "Linux"; // operating system
				String vmm = "Empty";// Empty
				double time_zone = 10.0; // time zone this resource located
				double cost = 3.0; // the cost of using processing in this resource
				double costPerMem = 0.05; // the cost of using memory in this resource
				double costPerStorage = 0.001; // the cost of using storage in this resource
				double costPerBw = 0.0; // the cost of using bw in this resource
				// we are not adding SAN devices by now
				LinkedList<Storage> storageList = new LinkedList<Storage>();
				FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
					arch, os, vmm, host, time_zone, cost, costPerMem, costPerStorage, costPerBw);

				AppModuleAllocationPolicy vmAllocationPolicy = new AppModuleAllocationPolicy(hostList);
				FogDevice sc = null;
				Service serviceOffer = new Service();
				serviceOffer.setType(getivalue(1,10000) % MaxAndMin.MAX_SERVICES);//???
				if (serviceOffer.getType() == Services.HIBRID || serviceOffer.getType() == Services.PUBLIC) {
					serviceOffer.setValue(getfvalue(0,1) * 10);
				} else {
					serviceOffer.setValue(0);
				}
				try {
					//double maxBandwidth = Constants.MAX_BANDWIDTH;
					//double minBandwidth = Constants.MIN_BANDWIDTH;
					//double upLinkRandom = minBandwidth + (maxBandwidth - minBandwidth)* getdvalue(0,1);//???
					//double downLinkRandom = minBandwidth + (maxBandwidth - minBandwidth) * getdvalue(0,1);
					sc = new FogDevice("ServerCloudlet" + Integer.toString(i) // name
					, characteristics, vmAllocationPolicy// vmAllocationPolicy
						, storageList, 10// schedulingInterval
						, Constants.rIFC_Down[i]// uplinkBandwidth
						, Constants.rIFC_Down[i]// downlinkBandwidth
						, 4//uplinkLatency (1,2) (15,35)
						, 0.01// rate per mips
						, coordX, coordY, i, serviceOffer,
						migrationStrategy, Constants.PolicyReplicaVM,
						beforeMigration);
					serverCloudlets.add(i, sc);
					sc.setParentId(-1);
					i++;
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		LogMobile.debug("EdgeEpos.java", "Total of serverCloudlets: " + i);
		return i;
	}
	//create Core servers
	/**
	 * @param serverCloudlets
	 * @param coordDevices
	 */
	public int addServerCloudletCoreCenters(List<FogDevice> serverCloudlets, Coordinate coordDevices) {
		
		int i = 0;
		int index = serverCloudlets.size();
		int coordX, coordY;
		Point point;
		//int cloudcenters = 0;
		//double area = Constants.area;
		System.out.println(" index "+index+" i "+i);
		double procCapacity = 0 ;
		
		while (i<Constants.BACKBONE_ROUTERS) {
			coordX = NodesLoc[index].x;
			coordY = NodesLoc[index].y;
			point = new Point(coordX, coordY);
		            
				if(City.isInside(point)) {
						DecisionMigration migrationStrategy;
					if (Constants.MigStrategyPolicy == Policies.LOWEST_LATENCY) {
						migrationStrategy = new LowestLatency(getServerCloudlets(),	getApDevices(), Constants.MigPointPolicy, Constants.PolicyReplicaVM);
					} else if (Constants.MigStrategyPolicy == Policies.LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET) {
						migrationStrategy = new LowestDistBwSmartThingServerCloudlet(getServerCloudlets(), getApDevices(),
								Constants.MigPointPolicy, Constants.PolicyReplicaVM);
					} else { // LOWEST_DIST_BW_SMARTTING_AP
						migrationStrategy = new LowestDistBwSmartThingAP(getServerCloudlets(), getApDevices(),
								Constants.MigPointPolicy, Constants.PolicyReplicaVM);
					}

					BeforeMigration beforeMigration = null;
					if (Constants.PolicyReplicaVM == Policies.MIGRATION_COMPLETE_VM) {
						beforeMigration = new PrepareCompleteVM();
					} else if (Constants.PolicyReplicaVM == Policies.MIGRATION_CONTAINER_VM) {
						beforeMigration = new PrepareContainerVM();
					} else if (Constants.PolicyReplicaVM == Policies.LIVE_MIGRATION) {
						beforeMigration = new PrepareLiveMigration();
					}

					FogLinearPowerModel powerModel = new FogLinearPowerModel(107.339d, 83.433d);

					//cloud servers have 8 pElements.
					procCapacity = Constants.FP[index]/8;
					List<Pe> peList = new ArrayList<Pe>();
						peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
						peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
						peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
						peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
						peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
						peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
						peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
						peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
                  
					int hostId = FogUtils.generateEntityId();
					long storage = Constants.FS[index];//16 * 1024 * 1024;// host storage MB
					long bw = Constants.rIFC_Down[index];//1000 * 1024 * 1024;//Megabits/s
					int ram = Constants.FM[index];//1024; host memory (MB)
					// To the hardware's characteristics (MobileDevice) - to CloudSim
					PowerHost host = new PowerHost(hostId, new RamProvisionerSimple(ram),
						new BwProvisionerOverbooking(bw), storage, peList,
						new StreamOperatorScheduler(peList), powerModel);

				List<Host> hostList = new ArrayList<Host>();
				hostList.add(host);

				String arch = "x86"; // system architecture
				String os = "Linux"; // operating system
				String vmm = "Empty";// Empty
				double time_zone = 10.0; // time zone this resource located
				double cost = 3.0; // the cost of using processing in this resource
				double costPerMem = 0.05; // the cost of using memory in this resource
				double costPerStorage = 0.001; // the cost of using storage in this resource
				double costPerBw = 0.0; // the cost of using bw in this resource
				// we are not adding SAN devices by now
				LinkedList<Storage> storageList = new LinkedList<Storage>();
				FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
					arch, os, vmm, host, time_zone, cost, costPerMem, costPerStorage, costPerBw);

				AppModuleAllocationPolicy vmAllocationPolicy = new AppModuleAllocationPolicy(hostList);
				FogDevice sc = null;
				Service serviceOffer = new Service();
				serviceOffer.setType(getivalue(1,10000) % MaxAndMin.MAX_SERVICES);
				//serviceOffer.setType(getRand().nextInt(10000) % MaxAndMin.MAX_SERVICES);
				if (serviceOffer.getType() == Services.HIBRID || serviceOffer.getType() == Services.PUBLIC) {
					serviceOffer.setValue(getfvalue(0,1) * 10);
				} else {
					serviceOffer.setValue(0);
				}
				try {
					sc = new FogDevice("ServerCloudlet" + Integer.toString(i) // id must change ??name
					, characteristics, vmAllocationPolicy// vmAllocationPolicy
						, storageList, 10// schedulingInterval
						, Constants.rIFC_Down[index]// uplinkBandwidth
						, Constants.rIFC_Down[index]// downlinkBandwidth
						, 4//uplinkLatency
						, 0.01// mipsPer..
						, coordX, coordY, index, serviceOffer,
						migrationStrategy, Constants.PolicyReplicaVM,
						beforeMigration);
					serverCloudlets.add(index, sc);
					sc.setParentId(-1);
					//System.out.println("cloudlet "+coordX+" "+coordY);
					
					index++;
					i++;
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			
		}
		
		LogMobile.debug("EdgeEpos.java", "Total of serverCloudlets: " + i);
		return i;
	}


	/** It makes a full graph between serverCloudlets
	 * 
	 */
	public void createServerCloudletsNetwork() {
		// for no full graph, use -1 to link
		int k = 1;
		FogDevice sc, sc1;
		HashMap<FogDevice, Double> net = new HashMap<>();
		int i = 0, j = 0, linha=0, coluna=0;
		for (int l = 0; l<serverCloudlets.size(); l++) {
			sc = serverCloudlets.get(l);
			j = 0;
			for (int m = 0; m<serverCloudlets.size(); m++) {
				sc1 = serverCloudlets.get(m);
				
				if (sc.equals(sc1)) {
					break;
				}

				linha = ((int) (j / 12) - (int) (i / 12));
				if (linha < 0)
					linha *= -1;
				coluna = ((int) (j % 12) - (int) (i % 12));
				if (coluna < 0)
					coluna *= -1;
				//System.out.println(l+" "+m +" l "+linha+" c "+coluna+" max "+(Math.max(linha, coluna)));
				
				calcDelayBetweenCloudlets(l, m, linha, coluna);
				//System.out.print("k "+k++);
				if (sc.getUplinkBandwidth() < sc1.getDownlinkBandwidth()) {
					net.put(sc1, sc.getUplinkBandwidth());
					NetworkTopology.addLink(sc.getId(), sc1.getId(), sc.getUplinkBandwidth(), Constants.dFFC[l][m]);
					//System.out.printf("l "+l+" "+m+" Bandwidth:%.0f \n",sc.getUplinkBandwidth());
				} else {
					net.put(sc1, sc1.getDownlinkBandwidth());
					NetworkTopology.addLink(sc.getId(), sc1.getId(), sc1.getDownlinkBandwidth(), Constants.dFFC[l][m]);
					//System.out.printf("l"+l+" "+m+" Bandwidth: %.0f\n",sc.getUplinkBandwidth());
					
				}
				j++;
				
			}
			i++;
			sc.setNetServerCloudlets(net);
			
		}
		for (int l = 0; l<serverCloudlets.size(); l++) {
			sc = serverCloudlets.get(l);
			for (int m  = 0; m<serverCloudlets.size(); m++) {
				sc1 = serverCloudlets.get(m);
				if (sc.equals(sc1)) {
					break;
				}
				/*
				System.out.println("Delay between " + sc.getName() + " and " + sc1.getName() + ": ");
				System.out.printf("%.0f\n", NetworkTopology.getDelay(sc.getId(), sc1.getId()));
				System.out.print("" +sc.getName());
				System.out.printf(" Downlink BW: %f\n" , sc.getDownlinkBandwidth());
			*/
			}
		}
		
	}
	
	
	private void calcDelayBetweenCloudlets(int l, int m, int linha, int coluna) {

		//System.out.println(" l "+l+" m "+m+ " min "+Math.min(linha, coluna)+" mAx "+Math.max(linha, coluna));
		if (Constants.dFFC[l][m] == 0) {
			if((l == (Constants.EDGE_ROUTERS+ Constants.BACKBONE_ROUTERS - 1)) || (m == (Constants.EDGE_ROUTERS + Constants.BACKBONE_ROUTERS - 1))) {
				Constants.dFFC[l][m] = RandomGenerator.genUniformRandomBetween(Constants.MIN_dFC, Constants.MAX_dFC);//15-35
				Constants.dFFC[m][l] = Constants.dFFC[l][m];
			}
				
			else if((l >= Constants.EDGE_ROUTERS)||(m >= Constants.EDGE_ROUTERS)) {
				Constants.dFFC[l][m] = RandomGenerator.genUniformRandomBetween(Constants.MIN_dFF, Constants.MIN_dFC);//8-16
				Constants.dFFC[m][l] = Constants.dFFC[l][m];
			}
			else {
				Constants.dFFC[l][m] = (Math.max(linha, coluna)) * Constants.MIN_dFF;//2-16
				Constants.dFFC[m][l] = Constants.dFFC[l][m];
			}
		}
		//return 
		
	}

	/**
	 * read the smart things/users mobility dataset in the Mobility_Dataset directory
	 */
	private void readMoblityData() {

		/*
		File folder = new File("input");
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles);
		
		// find last file in the directory that consists the order/index of input mobility files: input\ordem_entrada.csv
		//int[] ordem = readDevicePathOrder(listOfFiles[listOfFiles.length - 1]);
		
		for (int i = 0; i < getSmartThings().size(); i++) {
			System.out.println(" st :"+ i+" file :"+listOfFiles[ordem[i]].getName());
			readDevicePath(getSmartThings().get(i), "input/" + listOfFiles[ordem[i]].getName());
			
		}*/
		
		File folder = new File("Mobility_Dataset");
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles);
		
		for (int i = 0; i < getSmartThings().size(); i++) {
			System.out.print("st:"+ i+" file: "+listOfFiles[i].getName());
			readDevicePath(getSmartThings().get(i), "Mobility_Dataset/" + listOfFiles[i].getName());
			//readDevicePath(getSmartThings().get(i), "inputi/veh"+i+".csv");
			
		}
	}

	
	/**not used at present 
	 * find the order/name of path files for network mobile smart things
	 * @param filename input file that contains the path file names/orders
	 * @return an array of the index of path files (e.g., 1 to 29 + 69 for 30 st)
	 */
	private int[] readDevicePathOrder(File filename) {

		String line = "";
		String cvsSplitBy = "\t";

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			int i = 1;
			while (((line = br.readLine()) != null)) {
				if (i == 1) {
					break;
				}
				i++;
			}
			
			String[] position = line.split(cvsSplitBy);
			int order[] = new int[getSmartThings().size()];
			for (int j = 0; j < getSmartThings().size(); j++) {
				order[j] = Integer.valueOf(position[j]);
				//System.out.println("order: "+order[j]);
				
			}
			Arrays.sort(order);
			return order;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	
	/** read time-stamped path of each smart thing and save the path
	 * @param st smart thing
	 * @param filename input path file which is a csv file formatted as: time-stamp, angle, x, y, speed
	 */
	private static void readDevicePath(MobileDevice st, String filename) {

		String line = "";
		String cvsSplitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			while ((line = br.readLine()) != null) {
				String[] position = line.split(cvsSplitBy);
				st.getPath().add(position);
			}

			Coordinate coordinate = new Coordinate();
			coordinate.setInitialCoordinate(st);
			saveMobility(st);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void saveMobility(MobileDevice st) {

		try (FileWriter fw1 = new FileWriter(st.getMyId() + "out.txt", true);
			BufferedWriter bw1 = new BufferedWriter(fw1);
			PrintWriter out1 = new PrintWriter(bw1))
		{
			out1.println(st.getMyId() + " Position: " + st.getCoord().getCoordX() + ", "
				+ st.getCoord().getCoordY() + " Direction: " + st.getDirection() + " Speed: "
				+ st.getSpeed());
			out1.println("Source AP: " + st.getSourceAp() + " Dest AP: " + st.getDestinationAp()
				+ " Host: " + st.getHost().getId());
			out1.println("Local server: null  Apps null Map null");
			if (st.getDestinationServerCloudlet() == null) {
				out1.println("Dest server: null Apps: null Map: null");
			}
			else {
				out1.println("Dest server: " + st.getDestinationServerCloudlet().getName()
					+ " Apps: " + st.getDestinationServerCloudlet().getActiveApplications()
					+ " Map " + st.getDestinationServerCloudlet().getApplicationMap());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try (FileWriter fw = new FileWriter(st.getMyId() + "route.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter out = new PrintWriter(bw))
		{
			out.println(st.getMyId() + "\t" + st.getCoord().getCoordX() + "\t"
				+ st.getCoord().getCoordY() + "\t" + st.getDirection() + "\t" + st.getSpeed()
				+ "\t" + CloudSim.clock());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * it makes the connection between SmartThing and the closest AccessPoint
	 * and the connection between AccessPoint and the closest ServerCloudlet
	 * and the symbolic link between smartThing and ServerCloudlet (agent)
	 * 
	 */
	public void CreateConnections(){
		int index, index1;// Auxiliary
		int myCount = 0;
		//makes the connection between SmartThing and the closest AccessPoint
		for (MobileDevice st : getSmartThings()) {
			
			if (!ApDevice.connectApSmartThing(getApDevices(), st, getRand().nextDouble())) {//dAF
				myCount++;
				LogMobile.debug("EdgeEpos.java",st.getName() + " isn't connected");
				System.out.println(st.getName()+" id: "+st.getId()+" not connected to APs "+ "coord_x: "+st.getCoord().getCoordX()+" coord_y: "+st.getCoord().getCoordY());
				}
			else {
				//System.out.println("connected to APs, name: "+st.getName()+" id: "+st.getId()+" coord_x: "+st.getCoord().getCoordX()+" coord_y: "+st.getCoord().getCoordY());
		    }
		}
		LogMobile.debug("AppExample.java", "total no connection: " + myCount);
		
		//makes the connection between AccessPoint and the closest ServerCloudlet
		//System.out.println(" APs size "+getApDevices().size());
		for (ApDevice ap : getApDevices()) {
			index = Distances.theClosestServerCloudletToAp(getServerCloudlets(), ap);//id
			
			  ap.setServerCloudlet(getServerCloudlets().get(index));//??imp
			  ap.setParentId(getServerCloudlets().get(index).getId());//useful
			  getServerCloudlets().get(index).setApDevices(ap, Policies.ADD);//??imp
			 
			 
			NetworkTopology.addLink(serverCloudlets.get(index).getId(), ap.getId(), ap.getDownlinkBandwidth(), Constants.dAF[index]);
			//NetworkTopology.addLink(ap.getId(), serverCloudlets.get(index).getId(), ap.getDownlinkBandwidth(), Constants.dAF[index]);
			//System.out.printf(" down bw %.0f\n",ap.getDownlinkBandwidth());
			//System.out.println("ap delay id"+index);
			//makes the symbolic link between smartThing and ServerCloudlet (agent)
			for (MobileDevice st : ap.getSmartThings()) {
				getServerCloudlets().get(index).connectServerCloudletSmartThing(st);
				getServerCloudlets().get(index).setSmartThingsWithVm(st, Policies.ADD);
				//getAgents().get(index).connectAgentwithSmartThing(st);
				
			}
		}
			
	}

	private int getindex(FogDevice fd) {
		System.out.println("size "+serverCloudlets.size());
		for (int f = 0; f<serverCloudlets.size(); f++) {
			if (serverCloudlets.get(f).getId() == fd.getId()) {
				System.out.println("apidIndex "+f);
				return f;
			}
 		}
		System.out.println("null");
		return -1;
	}

	/** CREATE one BROKER per smart thing.
	 * @throws Exception
	 */
	public void createBrokers() throws Exception{
	
		for (MobileDevice st : getSmartThings()) {
			getBrokerList().add(new FogBroker("My_broker" + Integer.toString(st.getMyId())));
		}
		
	}
	
	
	
	/**
	 * create a number of smart things (e.g., vehicles) in the network
	 * @param smartThing
	 * @param coordDevices 
	 * @param j : number of smart things
	 */
	public void addSmartThing() {
	
		for (int i = 0; i < Constants.numSmartThings; i++) {
			int coordX = 0, coordY = 0;
			int direction, speed;
			direction = getivalue(0,Constants.MAX_DIRECTION - 1) + 1;
			speed = getivalue(0,Constants.MAX_SPEED - 1) + 1;
			/*************** Start set of Mobile Sensors ****************/
			VmMigrationTechnique migrationTechnique = null;
	
			if (Constants.PolicyReplicaVM == Constants.MIGRATION_COMPLETE_VM) {
				migrationTechnique = new CompleteVM(Constants.MigPointPolicy);
			} else if (Constants.PolicyReplicaVM == Constants.MIGRATION_CONTAINER_VM) {
				migrationTechnique = new ContainerVM(Constants.MigPointPolicy);
			} else if (Constants.PolicyReplicaVM == Constants.LIVE_MIGRATION) {
				migrationTechnique = new LiveMigration(Constants.MigPointPolicy);
			}

		
		/*************** Start MobileDevice Configurations ****************/

		FogLinearPowerModel powerModel = new FogLinearPowerModel(87.53d,82.44d);// 10//maxPower

		List<Pe> peList = new ArrayList<>();
		int mips = 46533;
		peList.add(new Pe(0, new PeProvisionerOverbooking(mips)));

		int hostId = FogUtils.generateEntityId();
		long storage = 512 * 1024;
		// host storage
		int bw = 54 * 1024 * 1024;//??
		int ram = 1024 * 16;
		// To the hardware's characteristics (MobileDevice) - to CloudSim
		PowerHost host = new PowerHost(
			hostId, new RamProvisionerSimple(ram),
			new BwProvisionerOverbooking(bw), storage, peList,
			new StreamOperatorScheduler(peList), powerModel);

		List<Host> hostList = new ArrayList<Host>();
		hostList.add(host);

		String arch = "x86"; // system architecture
		String os = "Android"; // operating system
		String vmm = "empty";// Empty
		double vmSize = 4;
		double time_zone = 10.0; // time zone this resource located
		double cost = 1.0; // the cost of using processing in this resource
		double costPerMem = 0.005; // the cost of using memory in this resource
		double costPerStorage = 0.0001; // the cost of using storage in this resource
		double costPerBw = 0.001; // the cost of using bw in this resource
		// we are not adding SAN devices by now
		LinkedList<Storage> storageList = new LinkedList<Storage>();

		FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
			arch, os, vmm, host, time_zone, cost, costPerMem,
			costPerStorage, costPerBw);

		AppModuleAllocationPolicy vmAllocationPolicy = new AppModuleAllocationPolicy(hostList);

		MobileDevice st = null;

		float maxServiceValue = getfvalue(0,1) * 100;
		try {
			st = new MobileDevice("SmartThing" + Integer.toString(i),
				characteristics, vmAllocationPolicy
				, storageList, 2// schedulingInterval
				, 54 * 1024 * 1024// uplinkBandwidth - 54 Mbit??
				, 54 * 1024 * 1024// downlinkBandwidth - 54 Mbits???
				, 2// uplinkLatency
				, 0.01// mipsPer..
				, coordX, coordY, i// id = index in smartThings
				, direction, speed, maxServiceValue, vmSize,
				migrationTechnique);
			st.setTempSimulation(0);
			st.setTimeFinishDeliveryVm(-1);
			st.setTimeFinishHandoff(0);
			//st.setSensors(sensors);
			//st.setActuators(actuators);
			st.setTravelPredicTime(Constants.TravelPredicTimeForST);
			st.setMobilityPredictionError(Constants.MobilityPrecitionError);
			getSmartThings().add(i, st);
		} catch (Exception e) {
			e.printStackTrace();
		}
		}//end for

	/* initial position of smart things:
		for (int j = 0 ;j <getSmartThings().size(); j++)
		System.out.println("st: "+getSmartThings().get(j).getName()+" id: "+getSmartThings().get(j).getId()+
				" coord_x: "+getSmartThings().get(j).getCoord().getCoordX()+" coord_y: "+getSmartThings().get(j).getCoord().getCoordY());
	*/
	}

	
	
	private static int getivalue(int min, int max)//change to randomgenerator
	{
		Random r = new Random();
		int randomValue = min + r.nextInt()%(max - min);
		return randomValue;
	}
	
	private static float getfvalue(float min, float max)
	{
		Random r = new Random();
		float randomValue = min + r.nextFloat()%(max - min);
		return randomValue;
	}
	
	
	public List<MobileDevice> getSmartThings() {
		return smartThings;
	}

	public void setSmartThings(List<MobileDevice> smartThings) {
		this.smartThings = smartThings;
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

	public List<Agent> getAgents() {
		return agents;
	}

	public void setAgents(List<Agent> agents) {
		this.agents = agents;
	}
		
	public List<FogBroker> getBrokerList() {
		return brokerList;
	}

	public void setBrokerList(List<FogBroker> brokerList) {
		this.brokerList = brokerList;
	}
	
	public static Random getRand() {
		return rand;
	}
	public static void setRand(Random rand) {
		Network2.rand = rand;
	}

	public static int getSeed() {
		return seed;
	}

	public static void setSeed(int seed) {
		Network2.seed = seed;
	}
	/**
	 * currently not used
	 */
	private void addApDevicesRandom() {
		int coordX, coordY;
		int i = 0;
		while (i < Constants.EDGE_ROUTERS){
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
		while (j < Constants.BACKBONE_ROUTERS){
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

	/** distribute server cloudlets at random positions
	 * currently not used
	 * @param i
	 */
	public void addRandomServerCloudlet( int i) {

		int coordX, coordY;
		DecisionMigration migrationStrategy;
		if (Constants.MigStrategyPolicy == Policies.LOWEST_LATENCY) {
			migrationStrategy = new LowestLatency(getServerCloudlets(),
				getApDevices(), Constants.MigPointPolicy, Constants.PolicyReplicaVM);
		} else if (Constants.MigStrategyPolicy == Policies.LOWEST_DIST_BW_SMARTTING_SERVERCLOUDLET) {
			migrationStrategy = new LowestDistBwSmartThingServerCloudlet(
				getServerCloudlets(), getApDevices(), Constants.MigPointPolicy,
				Constants.PolicyReplicaVM);
		} else { // Policies.LOWEST_DIST_BW_SMARTTING_AP
			migrationStrategy = new LowestDistBwSmartThingAP(
				getServerCloudlets(), getApDevices(), Constants.MigPointPolicy,
				Constants.PolicyReplicaVM);
		}

		BeforeMigration beforeMigration = null;
		if (Constants.PolicyReplicaVM == Policies.MIGRATION_COMPLETE_VM) {
			beforeMigration = new PrepareCompleteVM();
		} else if (Constants.PolicyReplicaVM == Policies.MIGRATION_CONTAINER_VM) {
			beforeMigration = new PrepareContainerVM();
		} else if (Constants.PolicyReplicaVM == Policies.LIVE_MIGRATION) {
			beforeMigration = new PrepareLiveMigration();
		}

		FogLinearPowerModel powerModel = new FogLinearPowerModel(107.339d, 83.433d);

		// CloudSim Pe (Processing Element) class represents CPU unit, defined in terms of Millions Instructions Per Second (MIPS) rating
		List<Pe> peList = new ArrayList<>();
		int mips = 3234;
		// 3. Create PEs and add these into a list.
		// need to store Pe id and MIPS Rating - to CloudSim
		peList.add(new Pe(0, new PeProvisionerOverbooking(mips)));

		int hostId = FogUtils.generateEntityId();
		long storage = 16 * 1024 * 1024;// host storage
		int bw = 1000 * 1024 * 1024;
		int ram = 1024;// host memory (MB)
		// To the hardware's characteristics (MobileDevice) - to CloudSim
		PowerHost host = new PowerHost(hostId, new RamProvisionerSimple(ram),
			new BwProvisionerOverbooking(bw), storage, peList,
			new StreamOperatorScheduler(peList), powerModel);

		List<Host> hostList = new ArrayList<Host>();
		hostList.add(host);

		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Empty";// Empty
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		// we are not adding SAN devices by now
		LinkedList<Storage> storageList = new LinkedList<Storage>();
		FogDeviceCharacteristics characteristics = new FogDeviceCharacteristics(
			arch, os, vmm, host, time_zone, cost, costPerMem, costPerStorage, costPerBw);

		AppModuleAllocationPolicy vmAllocationPolicy = new AppModuleAllocationPolicy(hostList);
		FogDevice sc = null;
		Service serviceOffer = new Service();
		serviceOffer.setType(getivalue(0,10000) % MaxAndMin.MAX_SERVICES);
		if (serviceOffer.getType() == Services.HIBRID
			|| serviceOffer.getType() == Services.PUBLIC) {
			serviceOffer.setValue(getfvalue(0,1) * 10);
		} else {
			serviceOffer.setValue(0);
		}
		try {
			coordX = getivalue(0,MaxAndMin.MAX_X);
			coordY = getivalue(0,MaxAndMin.MAX_X);
			
			sc = new FogDevice("ServerCloudlet" + Integer.toString(i) // name
			, characteristics, vmAllocationPolicy// vmAllocationPolicy
				, storageList, 10// schedulingInterval
				, Constants.MAX_BANDWIDTH// uplinkBandwidth
				, Constants.MIN_BANDWIDTH// downlinkBandwidth
				, 4// rand.nextDouble()//uplinkLatency
				, 0.01// mipsPer..
				, coordX, coordY, i, serviceOffer, migrationStrategy,
				Constants.PolicyReplicaVM, beforeMigration);
			serverCloudlets.add(i, sc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**currently not used
	 * place AP and Routers using reading their coordinations from a file
	 * @param srcAP input file
	 * @param m city map
	 */
	@SuppressWarnings("unused")
	private void addApDevicesFromFile(String srcAP, CityMap m) {
		int i = 0;
		int j = 0;
		boolean control = true;
		int coordX = 0, coordY = 0;
		String line;
		String[] strArgs = new String[3];
		
		try {
			LineNumberReader br = new LineNumberReader(new FileReader(srcAP));
	           
	            while((line = br.readLine())!= null) {
	            	if (line.startsWith("#")) 
		        		continue;
	            	
	            	strArgs = line.split("\\s+");
	            	j = strArgs.length;
	            	if (j > 1) {
	            		coordX = Integer.parseInt(strArgs[0]); coordY = Integer.parseInt(strArgs[1]);

	        			if ((coordX < MaxAndMin.MAX_X) && (coordY < MaxAndMin.MAX_Y)){
	        					
	        					System.out.println("Creating Ap devices");
	            						ApDevice ap = new ApDevice("AccessPoint" + Integer.toString(i), // name
	        							coordX, coordY, i// ap.set//id
	        							, 100 * 1024 * 1024// downLinkBandwidth - 100Mbits
	        							, 200// engergyConsumption
	        							, MaxAndMin.MAX_ST_IN_AP// maxSmartThing
	        							, 100 * 1024 * 1024// upLinkBandwidth - 100Mbits???
	        							, 4// upLinkLatency
	        						);
	        						System.out.println("x: "+coordX+" y: "+coordY+" i: "+i);
	        						apDevices.add(i, ap);
	        						i ++;
	        			}
	        		}		            		
	                else {
	                //skip invalid line		
	                }
	            	System.out.println(--i+" access points are configured");
					   
	            }
	    br.close(); 
	               
		}
		catch (Exception e) {
	        System.out.println("Error in FileReader !!!");
	        e.printStackTrace();
	    }
		
		LogMobile.debug("EdgeEpos.java", "Total of accessPoints: " + i);

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
