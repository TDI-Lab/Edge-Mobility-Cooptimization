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
import org.fog.entities.MobileActuator;
import org.fog.entities.MobileDevice;
import org.fog.entities.MobileSensor;
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
public class Network {

	//be aware of  MaxAndMin.AP_COVERAGE and cloud node
	private static int seed;
	private static Random rand;
	
	private List<MobileDevice> smartThings = new ArrayList<MobileDevice>();
	private List<FogDevice> serverCloudlets = new ArrayList<>();
	private List<FogBroker> brokerList = new ArrayList<>();
	private List<ApDevice> apDevices = new ArrayList<>();
	private List <Agent> agents = new ArrayList<>();
	private Coordinate coordDevices;
	
	public class CPoint {
	    public int x;
	    public int y;
	}
	CPoint[]  NodesLoc = new CPoint[Constants.BACKBONE_ROUTERS+Constants.EDGE_ROUTERS];//473
	
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	private void createNetwork() throws Exception {
		
		System.out.println("--------------------------------");
		System.out.println("Network Configuration");
		createNetNodes();
		System.out.println("Number of Network nodes (APs + core routers + cloud center) configured: "+apDevices.size());
		
		createServersAndAgents();
		
		System.out.println("Number of Server Cloudlets configured: "+serverCloudlets.size());
		
		//for (int i = 0; i<serverCloudlets.size(); i++)
		//System.out.println("cs i "+i+" myid "+serverCloudlets.get(i).getMyId());
		
		System.out.println("Cloudlet network configuration...");
		//createServerCloudletsNetwork();
		
		System.out.println("Adding vehicles to the city...");
		addSmartThing();//check for application ???Constants.numSmartThings
		System.out.println("Number of Smart Things configured: "+smartThings.size());
		
		readMobilityData();
		
		System.out.println("Connection creation...");
		createConnections();
		System.out.println("Connection creation finished");
		
		System.out.println("Broker creation...");
		createBrokers();
		System.out.println("Number of Brokers configured: "+brokerList.size());
		
		PrintNetCapacity();
	}

	private void PrintNetCapacity() {
		// TODO Auto-generated method stub
		String CSV_SEPARATOR = ",";
    	int i = 0;
    	double totalCPU = 0, totalMem = 0, totalStorage = 0;
    	
				try {
			    	BufferedWriter csvWriter = new BufferedWriter(new FileWriter(Constants.NetOutFile+"AP_SC.csv"));
			    	
			    	csvWriter.append("APid").append(CSV_SEPARATOR)
			    	.append("DownBW-Byte/s").append(CSV_SEPARATOR)
			    	.append("UPBW-Byte/s").append(CSV_SEPARATOR)
			    	.append("PropagDelay-second").append(CSV_SEPARATOR)
			    	.append("Power").append(CSV_SEPARATOR)
			    	.append("SCid").append(CSV_SEPARATOR)
			    	.append("ProcPower-mips").append(CSV_SEPARATOR)
			    	.append("Ram-GB").append(CSV_SEPARATOR)
			    	.append("Storage-Byte").append(CSV_SEPARATOR)
			    	.append("DownBW-Byte/s").append(CSV_SEPARATOR)
			    	.append("UPBW-Byte/s").append(CSV_SEPARATOR)
			    	.append("PropagDelay-second").append(CSV_SEPARATOR)
			    	.append("MaxPower").append(CSV_SEPARATOR)
			    	.append("Power").append(CSV_SEPARATOR)
			    	.append(System.lineSeparator());
			    	
			    	for (i = 0; i<apDevices.size() ; i++) {
						csvWriter.append(apDevices.get(i).getMyId()+"").append(CSV_SEPARATOR)
						.append(apDevices.get(i).getDownlinkBandwidth()+"").append(CSV_SEPARATOR)
						.append(apDevices.get(i).getUplinkBandwidth()+"").append(CSV_SEPARATOR)				
						.append(apDevices.get(i).getUplinkLatency()+"").append(CSV_SEPARATOR)				
						.append(apDevices.get(i).getEnergyConsumption()+"").append(CSV_SEPARATOR)
						.append(serverCloudlets.get(i).getMyId()+"").append(CSV_SEPARATOR)	
						.append(serverCloudlets.get(i).getHost().getAvailableMips()+"").append(CSV_SEPARATOR)
						.append(serverCloudlets.get(i).getHost().getRam()+"").append(CSV_SEPARATOR)				
						.append(serverCloudlets.get(i).getHost().getStorage()+"").append(CSV_SEPARATOR)				
						.append(serverCloudlets.get(i).getDownlinkBandwidth()+"").append(CSV_SEPARATOR)
						.append(serverCloudlets.get(i).getUplinkBandwidth()+"").append(CSV_SEPARATOR)				
						.append(serverCloudlets.get(i).getUplinkLatency()+"").append(CSV_SEPARATOR)				
						.append(serverCloudlets.get(i).getHost().getMaxPower()+"").append(CSV_SEPARATOR)
						.append(serverCloudlets.get(i).getHost().getPower()+"").append(CSV_SEPARATOR)
						.append(System.lineSeparator());
						
						totalCPU += serverCloudlets.get(i).getHost().getAvailableMips();
						totalMem += serverCloudlets.get(i).getHost().getRam();
						totalStorage += serverCloudlets.get(i).getHost().getStorage();
			    		
			    	}
		    	
		    	csvWriter.close();
		    	}
				catch (Exception e) {
			        System.out.println("Error in FileReader !!!");
			        e.printStackTrace();
			    }
		    	
			System.out.println("Total resources available: ");
			System.out.printf("CPU: %.2f",totalCPU);
			System.out.printf(" Memory: %.2f",totalMem);
			System.out.printf(" Storage: %.2f\n",totalStorage);
			System.out.println("-------------------------------------------------");
			
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
	 * three types of devices are added: edge routers (access points), core routers, and one cloud node.  
	 * 
	 * A general rule of thumb in home networking says that Wi-Fi routers operating on the 
	 * traditional 2.4 GHz band reach up to 150 feet (46 m) indoors and 300 feet (92 m) outdoors. 
	 * Older 802.11a routers that ran on 5 GHz bands reached approximately one-third of these distances.
	 * 
	 */
	public void addFixedDevices() {
		System.out.println("Creating fixed access points...");
		addFixedEdgeRouters();
		System.out.println("Creating fixed core points...");
		addFixedCoreRouters();
		//System.out.println("NodesLoc "+NodesLoc.length);
		
	}
	
	
	/**
	 * OpenCellID: 468 total edge routers, 4 core routers and one cloud node for the selected area of Munich
	 * 218 m: is the max disance between vehicles to their closest AP
	 * We assume a network of LTE access points with the coverage range of 500m. 
	 *
	 */
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
						, 200// initial engergyConsumption
						, Constants.MAX_ST_IN_AP// maxSmartThing: set to 500 
						, Constants.rIFC_Up[i]//100 * 1024 * 1024 upLinkBandwidth - 100Mbits
						, Constants.dIF[i]/1000000//4 upLinkLatency + delay for smart things --> second
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
						System.out.println("creating cloud node....");
					}
					
					   ApDevice ap = new ApDevice("AccessPoint" + Integer.toString(i), // name
						coordX, coordY, index// id
						, Constants.rIFC_Down[index]//100 * 1024 * 1024 downLinkBandwidth - 100Mbits
						, 200// engergyConsumption
						, smartthingsPerAP//?? minSmartThing: set to zero thus can not connect to smart things
						, Constants.rIFC_Up[index]//100 * 1024 * 1024 upLinkBandwidth - 100Mbits
						, 0//4 upLinkLatency   for core router set to zero
					);
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
		System.out.println("Error in FileReader !!!"); e.printStackTrace(); 
		}
		
		System.out.println("Number of core routers created: "+i );
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
    	System.out.println("Number of Agents configured: "+agents.size());
				
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
		        
			//if(City.isInside(point)) {
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

					FogLinearPowerModel powerModel = new FogLinearPowerModel(107.339d, 83.433d);//change for cloud???

					//fog servers have 4 pElements.
					procCapacity = Constants.FP[i]/4;//mips
					List<Pe> peList = new ArrayList<Pe>();
					peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
					peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
					peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
					peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
					
                  
					int hostId = FogUtils.generateEntityId();
					long storage = Constants.FS[i];//16 * 1024 * 1024;// host storage MB --->bit
					long bw = Constants.rIFC_Down[i];//1000 * 1024 * 1024;//Megabits/s
					int ram = Constants.FM[i];//1024; host memory (MB)  -->bit
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
					sc = new FogDevice("ServerCloudlet" + Integer.toString(i) // name
					, characteristics, vmAllocationPolicy// vmAllocationPolicy
						, storageList, 10// schedulingInterval
						, Constants.rIFC_Up[i]// uplinkBandwidth
						, Constants.rIFC_Down[i]// downlinkBandwidth
						, 4//uplinkLatency (1,2) (15,35)???
						, 0.01// rate per mips????
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
				
			//}
			
		}
		
		//LogMobile.debug("EdgeEpos.java", "Total of serverCloudlets: " + i);
		return i;
	}
	//create Core servers
	/**
	 * @param serverCloudlets
	 * @param coordDevices
	 */
	public int addServerCloudletCoreCenters(List<FogDevice> serverCloudlets, Coordinate coordDevices) {
		
		
		int index = serverCloudlets.size();
		int i = index; int j = 0; 
		int coordX, coordY;
		Point point;
		//int cloudcenters = 0;
		//double area = Constants.area;
		List<Pe> peList;
		double procCapacity = 0 ;
		
		while (j<Constants.BACKBONE_ROUTERS) {
			coordX = NodesLoc[i].x;
			coordY = NodesLoc[i++].y;
			point = new Point(coordX, coordY);
			//System.out.println(" index "+index+" i "+i);
				
			//if(City.isInside(point)) {
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
					//exactly the model we now use in the paper
					FogLinearPowerModel powerModel = new FogLinearPowerModel(107.339d, 83.433d);

					//cloud servers have 8 pElements.
					if (index ==  (Constants.BACKBONE_ROUTERS - 1)) {
						
						procCapacity = Constants.FP[index]/8;
						peList = new ArrayList<Pe>();
							peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
							peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
							peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
							peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
							peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
							peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
							peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
							peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
					}
					else {
						procCapacity = Constants.FP[index]/4;//mips
						peList = new ArrayList<Pe>();
						peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
						peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
						peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
						peList.add(new Pe(0, new PeProvisionerOverbooking(procCapacity)));
						
					}
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
						, Constants.rIFC_Up[index]// uplinkBandwidth
						, Constants.rIFC_Down[index]// downlinkBandwidth
						, 4//uplinkLatency???
						, 0.01// mipsPer..????
						, coordX, coordY, index, serviceOffer,
						migrationStrategy, Constants.PolicyReplicaVM,
						beforeMigration);
					serverCloudlets.add(index++, sc);
					sc.setParentId(-1);
					//System.out.println("core cloudlet added"+ index);
					
					j++;
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
		}
		
		LogMobile.debug("EdgeEpos.java", "Total of serverCloudlets: " + index);
		return j;
	}


	/** It makes a full undirectional graph between serverCloudlets
	 *  please note that the links are created similar to a triangular matrix 
	 */
	public void createServerCloudletsNetwork() {
		// for no full graph, use -1 to link
		
		//FogDevice sc, sc1;
		HashMap<FogDevice, Double> net = new HashMap<>();
		//calcDelayBetweenCloudlets();
		System.out.println("Reading finished");
		System.out.println("Creating cloudlet network with "+serverCloudlets.size()*(serverCloudlets.size()-1)/2+" undirected edges between "+serverCloudlets.size()+" cloudlets");
		int l = 0, m = 0;
		//for (int l = 0; l<serverCloudlets.size(); l++) {
		
		for (FogDevice sc : serverCloudlets) {
			
			//sc = serverCloudlets.get(l);
			m= 0;
			for (FogDevice sc1 : serverCloudlets) {
			//for (int m = 0; m<serverCloudlets.size(); m++) {
				//sc1 = serverCloudlets.get(m);
				
				//System.out.println("m "+m);
				if (sc.equals(sc1)) {
					//System.out.println("break");
					break;
				}

				if (sc.getUplinkBandwidth() < sc1.getDownlinkBandwidth()) {
					net.put(sc1, sc.getUplinkBandwidth());
					NetworkTopology.addLink(sc.getId(), sc1.getId(), sc.getUplinkBandwidth(), Constants.dFFC[l][m]);
					//System.out.printf("l "+l+" "+m+" Bandwidth:%.0f \n",sc.getUplinkBandwidth());
				} else {
					net.put(sc1, sc1.getDownlinkBandwidth());
					NetworkTopology.addLink(sc.getId(), sc1.getId(), sc1.getDownlinkBandwidth(), Constants.dFFC[l][m]);
					//System.out.printf("l"+l+" "+m+" Bandwidth: %.0f\n",sc.getUplinkBandwidth());
					
				}
			m++;
			}
		l++;
		sc.setNetServerCloudlets(net);
			
		}
		
		System.out.println("Cloudlet network configuration finished");
		System.out.println("l value "+l+", m value "+m);
		/*
		for (int l = 0; l<serverCloudlets.size(); l++) {
			sc = serverCloudlets.get(l);
			for (int m  = 0; m<serverCloudlets.size(); m++) {
				sc1 = serverCloudlets.get(m);
				if (sc.equals(sc1)) {
					System.out.print("break ");
					break;
				}
				System.out.println("Delay between " + sc.getName() + " and " + sc1.getName() + ": ");
				System.out.printf("%.0f\n", NetworkTopology.getDelay(sc.getId(), sc1.getId()));
				}
				/*
				System.out.print("" +sc.getName());
				System.out.printf(" Downlink BW: %f\n" , sc.getDownlinkBandwidth());
				 */
			//}
		
		
	}
	
		
	/**
	 * read the smart things/users mobility dataset in the Mobility_Dataset directory
	 */
	private void readMobilityData() {

		//5946
		
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
		System.out.println("Reading mobility dataset...");
		File folder = new File("Mobility_Dataset2");
		File[] listOfFiles = folder.listFiles();
		Arrays.sort(listOfFiles);
		//???? be careful about the naming method: change it
		for (int i = 0; i < getSmartThings().size(); i++) {
			//System.out.println("st:"+ i+" file: "+listOfFiles[i].getName());
			readDevicePath(getSmartThings().get(i), "Mobility_Dataset2/" + listOfFiles[i].getName());
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
		String csvSplitBy = ",";

		try (BufferedReader br = new BufferedReader(new FileReader(filename))) {

			while ((line = br.readLine()) != null) {
				String[] position = line.split(csvSplitBy);
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
	 * it makes connection between SmartThings and closest AccessPoints
	 * and then connection between every AccessPoint and its closest ServerCloudlet
	 * and for each connected cloudlet creates symbolic link between smartThing and the cloudlet
	 * 
	 */
	public void createConnections(){
		int index, index1;// Auxiliary
		int myCount = 0;

		System.out.println("Creating connection between Smart Things and Access Points...");
		for (MobileDevice st : getSmartThings()) {
			
			if (!ApDevice.connectApSmartThing(getApDevices(), st, getRand().nextDouble())) {//???? dAF
				myCount++;
				LogMobile.debug("EdgeEpos.java",st.getName() + " isn't connected");
				System.out.println(st.getName()+" id: "+st.getId()+" not connected to APs "+ "coord_x: "+st.getCoord().getCoordX()+" coord_y: "+st.getCoord().getCoordY());
				}
			else {
				//System.out.println("connected to APs, name: "+st.getName()+" id: "+st.getId()+" coord_x: "+st.getCoord().getCoordX()+" coord_y: "+st.getCoord().getCoordY());
		    }
		}
		LogMobile.debug("AppExample.java", "total no connection: " + myCount);
		
		
		
		
		
		System.out.println("Creating connection between Cloudlets, Access Points, and Smart Things...");
		for (ApDevice ap : getApDevices()) {
			index = ap.getMyId();
			System.out.println("myId : "+index);
			//index = Distances.theClosestServerCloudletToAp(getServerCloudlets(), ap);//id
			//System.out.println("index : "+index);
			  ap.setServerCloudlet(getServerCloudlets().get(index));//??imp
			  ap.setParentId(getServerCloudlets().get(index).getId());//useful
			  getServerCloudlets().get(index).setApDevices(ap, Policies.ADD);//??imp
			 
			 
			NetworkTopology.addLink(serverCloudlets.get(index).getId(), ap.getId(), ap.getDownlinkBandwidth(), Constants.dAF[index]);//???
			//System.out.println("ap "+ap.getMyId()+" cloudlet "+ index);
			//System.out.println(" sc "+serverCloudlets.get(index).getCoord().getCoordX()+serverCloudlets.get(index).getCoord().getCoordY()+" ap "+ ap.getCoord().getCoordX()+ap.getCoord().getCoordY());
			
			//NetworkTopology.addLink(ap.getId(), serverCloudlets.get(index).getId(), ap.getDownlinkBandwidth(), Constants.dAF[index]);
			//System.out.printf(" down bw %.0f\n",ap.getDownlinkBandwidth());
			//System.out.println("ap delay id"+index);

			//System.out.println("Creating connection between Smart Things and Cloudlets...");
			for (MobileDevice st : ap.getSmartThings()) {
				getServerCloudlets().get(index).connectServerCloudletSmartThing(st);
				getServerCloudlets().get(index).setSmartThingsWithVm(st, Policies.ADD);
				//getAgents().get(index).connectAgentwithSmartThing(st);
				System.out.println("ap id: "+ap.getMyId()+" st id: "+st.getMyId()+" cloudlet id: "+index);
				
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
		System.out.println("st num "+Constants.numSmartThings);
		for (int i = 0; i < Constants.numSmartThings; i++) {
			//System.out.println("st: "+i);
					
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
			//????
			DeterministicDistribution distribution0 = new DeterministicDistribution(
					Constants.IMG_TRANSMISSION_TIME);// +(i*getRand().nextDouble()));

				Set<MobileSensor> sensors = new HashSet<>();

				MobileSensor sensor = new MobileSensor("Sensor" + i // Tuple's name
				, "IMG" + i // Tuple's type
				, i // User Id
				, "MyApp_AR" + i // app's name
				, distribution0);
				sensors.add(sensor);

				/*************** End set of Mobile Sensors ****************/
				/*
				if(EEG_TRANSMISSION_TIME==10)
					application.addAppEdge("EEG", "client", 2000, 500, "EEG", Tuple.UP, AppEdge.SENSOR); // adding edge from EEG (sensor) to Client module carrying tuples of type EEG
				else
					application.addAppEdge("EEG", "client", 3000, 500, "EEG", Tuple.UP, AppEdge.SENSOR);
				*/
				/**
				 * CPU length (in MIPS) of tuples carried by the application edge
				 */
				//private double tupleCpuLength;
				/**
				 * Network length (in bytes) of tuples carried by the application edge
				 */
				//private double tupleNwLength;
				
				/*************** Start set of Mobile Actuators ****************/

				MobileActuator actuator = new MobileActuator("Actuator" + i, i,
					"MyApp_AR" + i, "DISPLAY" + i);

				Set<MobileActuator> actuators = new HashSet<>();
				actuators.add(actuator);

		/*************** Start MobileDevice Configurations ****************/

		FogLinearPowerModel powerModel = new FogLinearPowerModel(87.53d,82.44d);//10 //maxPower

		List<Pe> peList = new ArrayList<>();
		int mips = 46533;
		peList.add(new Pe(0, new PeProvisionerOverbooking(mips)));

		int hostId = FogUtils.generateEntityId();
		long storage = 512 * 1024;
		// host storage
		int bw = 54 * 1024 * 1024;//??
		int ram = 1024 * 16;
		// To the hardware's characteristics (MobileDevice) - to CloudSim
		PowerHost host = new PowerHost(hostId, new RamProvisionerSimple(ram),
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
			st.setSensors(sensors);
			st.setActuators(actuators);
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
