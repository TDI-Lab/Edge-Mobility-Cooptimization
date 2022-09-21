package EdgeEPOS.Setting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.util.Pair;
import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;
import org.fog.application.AppEdge;
import org.fog.application.AppLoop;
import org.fog.application.AppModule;
import org.fog.application.Application;
import org.fog.application.selectivity.FractionalSelectivity;
import org.fog.application.selectivity.SelectivityModel;
import org.fog.entities.FogBroker;
import org.fog.entities.MobileDevice;
import org.fog.entities.Tuple;
import org.fog.scheduler.TupleScheduler;

import EdgeEPOS.PlacementMethods.Agent;
import EdgeEPOS.Utility.ArrayFiller;
import EdgeEPOS.Utility.RandomGenerator;



/**
 * @author rooyesh
 *
 */
public class Workload2 {

	private Map<Integer, Map<String, Double>> deadlineInfo = new HashMap<Integer, Map<String, Double>>();
	
	/*
	 * protected double lambda_in[][]; // lambda^in_aj protected double
	 * lambdap_in[][]; // lambda'^in_ak protected double lambda_out[][]; //
	 * lambda^out_aj protected double[][] arrivalInstructionsCloud; // LAMBDA_ak
	 * protected double[][] arrivalInstructionsFog; // LAMBDA_aj
	 */
    public static final int random = 0; 
    public static final int beta = 1;

    public static int AGGREGATED = 1;
    public static int COMBINED_APP = 2;
    public static int NOT_COMBINED = 3;

    public static int TRAFFIC_ENLARGE_FACTOR = 1; // since the traffic is read from the trace files, its value might be small. This factor will enlarge the vlaue of the traffic
	    
    
    //private List<Application> applicationList;
    private List<MobileARservice>  serviceList;
   
	 public Workload2() {
	      /*
		lambda_in = new double[Constants.numServices][Constants.numFogNodes];
        lambdap_in = new double[Constants.numServices][Constants.numCloudServers];
        lambda_out = new double[Constants.numServices][Constants.numFogNodes];
         */  
		 //arrivalInstructionsCloud = new double[Constants.numServices][Constants.numCloudServers];
	     //arrivalInstructionsFog = new double[Constants.numServices][Constants.numFogNodes];
	    
	 }
   
	
    public void distributeTraffic(double trafficPerNodePerService, int type) {
    	System.out.println("Distributing input traffic to smart things.... ");
    	distributeTrafficToSmartThings(trafficPerNodePerService, Constants.globalTraffic1, type);
    	
        
    }
    
    /**distributes input traffic over smart things/vehicles according to distribution method
     * @param trafficPerNodePerService input traffic (request per second) per node per service
     * @param targetTraffic assigned traffic to each smart thing
     * @param type distribution method
     */
    private void distributeTrafficToSmartThings(double trafficPerNodePerService, double[] targetTrafficPerVehicle, int type) {
        
    	double totalTraffic = trafficPerNodePerService * Constants.numSmartThings * Constants.numFogNodes;
        System.out.println("input traffic: "+trafficPerNodePerService+" totalTraffic "+ totalTraffic);
        
        double[] nodeTrafficPercentage = new double[Constants.numSmartThings];
        
        if (type == beta) {
    		RandomGenerator.fillRandomBetaInArray(nodeTrafficPercentage);
    	}
        else {
        	ArrayFiller.fillRandomPDFInArray(nodeTrafficPercentage);//based on random distribution
        }	
        
	for (int j = 0; j < Constants.numSmartThings; j++) {
            targetTrafficPerVehicle[j] = totalTraffic * nodeTrafficPercentage[j];
            //System.out.println("pernode "+nodeTrafficPercentage[j]+" tt "+ targetTraffic[j]);
        }
        
            buildServices(targetTrafficPerVehicle);
            System.out.println("service list size: "+serviceList.size());
    }
    

    /**
     * Distributes the given traffic on network smart devices and creates corresponding augmented reality services
     * @param totalInputTraffic the incoming traffic sample to the network
     * @param perNodeTraffic the target distribution array
     * @param type
     */
    public void buildServices(double[] perNodeTraffic) {
    	System.out.println("Building Mobile_AR services.....");
    	serviceList = new ArrayList<>();
    	   
    	int i=0;
    	for (i = 0 ; i<Constants.numSmartThings; i++) {
            serviceList.add(new MobileARservice(i, "Mobile_AR_"+i, perNodeTraffic[i]));
        }
    	
    }

    /**
     * Initialize Service Traffic Percentage based on Beta or Uniform distribution 
     * @param totalTraffic input traffic per profile
     * @param perNodeTraffic input traffic per smart thing
     * @param type
     */
  
     
    public List<Application> createApplication(List<MobileDevice> SmartThings, List<FogBroker> BrokerList) {
    	List<String> appIdList = new ArrayList<String>();
    	List<Application> applicationList = new ArrayList<Application>();
        
	   createVMs(SmartThings, BrokerList);
	   //System.out.println("size smart things "+ SmartThings.size()+" size broker list "+BrokerList.size());
	   int i = 0;
	   for (FogBroker br : BrokerList) {
		   	appIdList.add("MyApp_vr_game" + Integer.toString(i));
			//System.out.println("size appidlist : "+appIdList.size());
			Application myApp = createApplications(appIdList.get(i), br.getId(), i,(AppModule) SmartThings.get(i).getVmMobileDevice());
			applicationList.add(myApp);
			i++;
		}
	   
	   /**
		 * STEP 5.1: IT LINKS SENSORS AND ACTUATORS FOR EACH BROKER -> example
		 * from: CloudSim and iFogSim
		 **/
	   
	return applicationList;    
   }
   
    /** creates one virtual machine for each smartThing (BROKER/USER)  ->  example from: CloudSim - example5.java
     * @param SmartThings
     * @param BrokerList
     */
    public void createVMs(List<MobileDevice> SmartThings, List<FogBroker> BrokerList) {
	
	int i = 0;
	MobileDevice st;
	int lastIndex = SmartThings.size();
	for (i = 0 ; i<lastIndex; i++) {
	
		st  = SmartThings.get(i);
		if (st.getSourceAp() != null) {
			//st.getCoord().
			CloudletScheduler cloudletScheduler = new TupleScheduler(500, 1); 
			//long sizeVm = 128;
			AppModule vmSmartThingTest = new AppModule(st.getMyId() // index=id=i
				, "AppModuleVm_" + st.getName() // name
				, "Mobile_AR" + st.getMyId() // appId
				, BrokerList.get(st.getMyId()).getId() // userId
				, serviceList.get(i).getCpuDemand()// 281 mips
				, serviceList.get(i).getMemDemand() //128 ram-MB
				, serviceList.get(i).getBandwidth() //1000 bw-Megabits/s 
				, serviceList.get(i).getStorageDemand()//128 sizeVm MB 
				, "Vm_" + st.getName() 
				, cloudletScheduler,
				new HashMap<Pair<String, String>, SelectivityModel>());

			st.setVmMobileDevice(vmSmartThingTest);
			st.getSourceServerCloudlet().getHost().vmCreate(vmSmartThingTest);
			st.setVmLocalServerCloudlet(st.getSourceServerCloudlet());
			
			serviceList.get(i).setEdgeId(st.getSourceServerCloudlet().getMyId());// edge node or gateway
			serviceList.get(i).setSourceId(st.getMyId()); //end iot device
			
			/*
			
			System.out.println("st_id "+st.getMyId() + " Position: "
				+ st.getCoord().getCoordX() + ", "
				+ st.getCoord().getCoordY() + " Direction: "
				+ st.getDirection() + " Speed: " + st.getSpeed());
			System.out.println("Source AP: " + st.getSourceAp()
				+ " Dest AP: " + st.getDestinationAp() + " Host: "
				+ st.getHost().getId());
			System.out.println("Local server: "
				+ st.getVmLocalServerCloudlet().getName() + " Apps "
				+ st.getVmLocalServerCloudlet().getActiveApplications() + " Map "
				+ st.getVmLocalServerCloudlet().getApplicationMap());
			if (st.getDestinationServerCloudlet() == null) {
				System.out.println("Dest server: null Apps: null Map: null");
			} else {
				System.out.println("Dest server: "
					+ st.getDestinationServerCloudlet().getName() + " Apps: " 
					+ st.getDestinationServerCloudlet().getActiveApplications()
					+ " Map " + st.getDestinationServerCloudlet().getApplicationMap());
			}
			*/
		}
		else {
			System.out.println("st_id "+st.getMyId()+" not ap....");
		}
		
	}
	
	submitVMs(SmartThings, BrokerList);
    
    }
	
  
    /**Each broker receives one smartThing's VM
     * @param SmartThings
     * @param BrokerList
     */
    public void submitVMs(List<MobileDevice> SmartThings, List<FogBroker> BrokerList) {
    	int i = 0;
    	
    	for (FogBroker br : BrokerList) {
    		List<Vm> tempVmList = new ArrayList<>();
    		tempVmList.add(SmartThings.get(i++).getVmMobileDevice());
    		br.submitVmList(tempVmList);
    	}
    }
    
    private Application createApplications(String appId, int userId, int myId, AppModule userVm) {
			Application application = Application.createApplication(appId, userId);
			application.addAppModule(userVm);//module 0
		  	
			Map<String,Double>moduleDeadline = new HashMap<String,Double>();
			moduleDeadline.put("mainModule", 10.0);
			Map<String,Integer>moduleAddMips = new HashMap<String,Integer>();
			moduleAddMips.put("mainModule", getvalue(0, 500));
			deadlineInfo.put(myId, moduleDeadline);
		
			application.setDeadlineInfo(deadlineInfo);
		
		return application;
	}
	
    /**
     * Calculates the arrival rates of the instructions to a fog node for a given
     * service
     *
     * @param method the method that is using the traffic
     * @param a the index of the service
     * @param j the index of the fog node
     */
    /*
    public static void calcArrivalRatesOfInstructionsFogNode(List<MobileDevice> SmartThings) {
    	Workload.arrivalInstructionsFog = new double[Constants.numServices][Constants.numFogNodes];
    	for (MobileDevice st : SmartThings) {
    		if (st.getSourceAp() != null) {
    	
    			arrivalInstructionsFog[st.getId()][st.getSourceAp().getServerCloudlet().getId()] = st.getVmMobileDevice().getMips();
    		}
    	}
 }
  		*/
    
  		private static int getvalue(int min, int max)
  		{
  			Random r = new Random();
  			int randomValue = min + r.nextInt()%(max - min);
  			return randomValue;
  		}
  		
  		public List<MobileARservice> getServiceList() {
			return serviceList;
		}

		/*
		 * public static MobileARservice getServiceById(int sourceId) { for
		 * (MobileARservice service: getTempServiceList()) if (service.getSourceId() ==
		 * sourceId) return service; return null;
		 * 
		 * }
		 */
		

}
