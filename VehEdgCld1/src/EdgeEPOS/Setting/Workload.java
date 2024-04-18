package EdgeEPOS.Setting;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.math3.distribution.BetaDistribution;
import org.apache.commons.math3.util.Pair;
import fog.entities.*;

import EdgeEPOS.PlacementMethods.Agent;
import EdgeEPOS.Utility.ArrayFiller;
import EdgeEPOS.Utility.RandomGenerator;



/**
 * @author rooyesh
 *
 */
public class Workload {

	//public static int TRAFFIC_ENLARGE_FACTOR = 1; // since the traffic is read from the trace files, its value might be small. This factor will enlarge the vlaue of the traffic
	
	public final int random = 0; 
    public final int beta = 1;
    public int methodType;
    public int smarting_with_no_srv = 0;
	private List<MobileARservice>  serviceList;
    private int servicesInArea;
    public double totalCPU = 0;
    public double totalMem = 0;
    public double totalStorage = 0;
	
    
    
 // Augmented reality service requests:
 	/**
 	* service processing demand in MIPS
 	*/
 	public double L_P_min = 50d;
 	public double L_P_max = 200d;	
 	/**
 	* mips- required amount of processing (50,200) for service a per request (in MI per req)
 	*/
 	public double[] L_P; 
 	
 	/**
 	* MByte- size of service (i.e. container) a in megabytes U(50; 500)
 	*/
 	public int[] L_S;  
 	/**
 	* MByte- required amount of memory for service a, in megabytes U(2; 400) 
 	*/
 	public int [] L_M; 
 	/**
 	* bit -  average request length of service a U(10; 26) KB
 	*/
 	public int[] l_rq;
 	/**
 	* bit - average response length of service a U(10; 20) B
 	*/
 	public int[] l_rp; 
 	/**
     * service threshold; 10 ms
     */
 	public double[] th;
 	
 	
	

	public Workload(int method, int numst) {
	      
		this.methodType = method;
	      
        L_P = new double[numst];
        ArrayFiller.fill1DArrayRandomlyInRange(L_P, L_P_min, L_P_max); 
        
        L_S = new int[numst];
        ArrayFiller.fill1DArrayRandomlyInRange(L_S, 50 , 500);  
       
        L_M = new int[numst];
        ArrayFiller.fill1DArrayRandomlyInRange(L_M, 2 , 400);  
      
        l_rq = new int[numst];
        ArrayFiller.fill1DArrayRandomlyInRange(l_rq, 10 * 1000 * 8, 26 * 1000 * 8);  
        
        l_rp = new int[numst];
        ArrayFiller.fill1DArrayRandomlyInRange(l_rp, 10 * 8, 20 * 8);

        th = new double[numst];
        setThresholds(10);//fixed to 10 ms

	        	        
	 }
   
	
    public void distributeTraffic(double trafficPerNodePerService, int type, double[] globalTraffic, int profileNum) {
    	distributeTrafficToSmartThings(trafficPerNodePerService, globalTraffic, type, profileNum);
    	
        
    }
    
    /**distributes input traffic over all smart things/vehicles according to the distribution method
     * @param trafficPerNodePerService input traffic (request per second) per node per service
     * @param targetTraffic assigned traffic to each smart thing
     * @param type distribution method
     * @param profileNum 
     */
    private void distributeTrafficToSmartThings(double trafficPerNodePerService, double[] targetTrafficPerVehicle, int type, int profileNum) {
        
    	int numST = targetTrafficPerVehicle.length;
        System.out.println("Distributing input traffic over "+numST+" smart things.... ");
    	double totalTraffic = trafficPerNodePerService * numST;
        //System.out.println("Total traffic for all services: "+ totalTraffic);
        double[] serviceTrafficPercentage = new double[numST];
        
        if (type == beta) {
            		RandomGenerator.fillRandomBetaInArray(serviceTrafficPercentage);//beta distribution
    	}
        else {
        			ArrayFiller.fillRandomPDFInArray(serviceTrafficPercentage);//uniform distribution
        }	
        
        for (int j = 0; j < numST; j++) {
		            targetTrafficPerVehicle[j] = totalTraffic * serviceTrafficPercentage[j];
		            //System.out.println("targetTrafficPerVehicle "+ targetTrafficPerVehicle[j]);
        }
        
            buildServices(targetTrafficPerVehicle, profileNum);
            System.out.println("Number of generated services: "+serviceList.size());
    }
    

    /**
     * Distributes the given traffic on all the smart devices and creates corresponding augmented reality services
     * the valid field will be false indicating that the vehicle is not still in the area
     * @param perNodeTraffic the target distribution array
     * @param profileNum 
     * creates services for all the numSmartThings
     */
    public void buildServices(double[] perNodeTraffic, int profileNum) {
    	System.out.println("Generating Mobile_AR services.....");
    	serviceList = new ArrayList<>();
    	   
    	int i=0;
    	for (i = 0 ; i<perNodeTraffic.length; i++) {
            serviceList.add(new MobileARservice(i, "Mobile_AR_"+i, perNodeTraffic[i], L_P[i], L_M[i], L_S[i], l_rq[i], l_rp[i], th[i]));
            
        }
    	
    }

    /**
     * 
     * @param vehiclesToAP 
     * @param currTime 
     * @param run 
     * Creates service requests only for the numSmartThings currently running in the selected area and connected to APs
     */
    public void createApplication(List<MobileDevice> SmartThings, short[][] vehiclesToAP, int currTime, int run) {
    	System.out.println("---------------------------------------------");
    	System.out.println("Method "+methodType+": generating services...");
    	
    	servicesInArea = createContainers(SmartThings, vehiclesToAP, currTime, run);
	    //System.out.println("size smart things "+ SmartThings.size()+" size broker list "+BrokerList.size());
	    System.out.println("Services in area "+servicesInArea);
	
   }
   
    /** creates one Container for each smartThing 
     * @param SmartThings
     * @param VehiclesToAP 
     * @param BrokerList
     * @param currTS 
     * @param run 
     */
    public int createContainers(List<MobileDevice> SmartThings, short[][] VehiclesToAP, int currTS, int run) {
	
    	smarting_with_no_srv = 0;
		int i = 0;
		int j = 0;
		MobileDevice st;
		
		
		for (i = 0 ; i<SmartThings.size(); i++) {
		
			st  = SmartThings.get(i);
			int currentConnectedAP = VehiclesToAP[st.getMyId()][currTS];
			
			if (currentConnectedAP != -1) {
					
				serviceList.get(i).setValid(true);					//in this ts the vehicle is in the area
				serviceList.get(i).setEdgeId(currentConnectedAP);	//??? I changed it: st.getSourceServerCloudlet().getMyId() edge node
				serviceList.get(i).setSourceId(st.getMyId()); 		//iot end-device
				j++;
				
			}
			else {
				serviceList.get(i).setValid(false);
				smarting_with_no_srv++;
				//System.out.println("st_id "+i+" myid "+st.getMyId()+" with no running service");
				
			}
				
			
	}
	
	
	PrintLoad(run);
	return j;
    
    }
	
  
    private void PrintLoad(int runNum) {
    	
    	totalCPU = 0;
    	totalMem = 0;
    	totalStorage = 0;
    		
    	String CSV_SEPARATOR = ",";
    	
    	try {
	    	BufferedWriter csvWriter = new BufferedWriter(new FileWriter(Constants.ARLoadFile+runNum+"-"+methodType+".csv"));
	    	
	    	csvWriter.append("service id").append(CSV_SEPARATOR)
	    	.append("Cpu-mips").append(CSV_SEPARATOR)
	    	.append("CPU-MiPerReq").append(CSV_SEPARATOR)
	    	.append("CPU-NumPerReq").append(CSV_SEPARATOR)
	    	.append("Mem-MByte").append(CSV_SEPARATOR)
	    	.append("Storage-MByte").append(CSV_SEPARATOR)
	    	.append("Bandwidth-MegaBytes/s").append(CSV_SEPARATOR)
	    	.append("ReqPerSec").append(CSV_SEPARATOR)
	    	.append("RequestSize-bit").append(CSV_SEPARATOR)
	    	.append("ResponseSize-bit").append(CSV_SEPARATOR)
	    	//.append("QoS").append(CSV_SEPARATOR)
			//.append("Penalty").append(CSV_SEPARATOR)
	    	.append(System.lineSeparator());
	    	
	    	for (int i= 0 ; i<serviceList.size(); i++) {
	    		if (serviceList.get(i).isValid()) {
					csvWriter.append(i+"").append(CSV_SEPARATOR)	
					.append(serviceList.get(i).getCpuDemand1()+"").append(CSV_SEPARATOR)//lp_i*z_i
					.append(serviceList.get(i).getCpuDemand()+"").append(CSV_SEPARATOR)//lp_i
					.append(serviceList.get(i).getRequestPerSec()+"").append(CSV_SEPARATOR)//z_i
					.append(serviceList.get(i).getMemDemand()+"").append(CSV_SEPARATOR)				
					.append(serviceList.get(i).getStorageDemand()+"").append(CSV_SEPARATOR)				
					.append(serviceList.get(i).getBandwidth()+"").append(CSV_SEPARATOR)
					.append(serviceList.get(i).getRequestPerSec()+"").append(CSV_SEPARATOR)			
					.append(serviceList.get(i).getRequestSize()+"").append(CSV_SEPARATOR)			
					.append(serviceList.get(i).getResponseSize()+"").append(CSV_SEPARATOR)
					//.append(serviceList.get(i).getQoS()+"").append(CSV_SEPARATOR)
					//.append(serviceList.get(i).getPenalty()+"").append(CSV_SEPARATOR)
					.append(System.lineSeparator());
					
					totalCPU += serviceList.get(i).getCpuDemand1();
					totalMem += serviceList.get(i).getMemDemand();
					totalStorage += serviceList.get(i).getStorageDemand();
	    		}
	    		else{
	    			csvWriter.append(i+"").append(CSV_SEPARATOR)	
					.append(0+"").append(CSV_SEPARATOR)//lp_i*z_i
					.append(0+"").append(CSV_SEPARATOR)//lp_i
					.append(0+"").append(CSV_SEPARATOR)	
					.append(0+"").append(CSV_SEPARATOR)	
					.append(0+"").append(CSV_SEPARATOR)				
					.append(0+"").append(CSV_SEPARATOR)
					.append(0+"").append(CSV_SEPARATOR)			
					.append(0+"").append(CSV_SEPARATOR)			
					.append(0+"").append(CSV_SEPARATOR)
					.append(System.lineSeparator());
					
	    		}
	    	}
    	
    	csvWriter.close();
    	}
		catch (Exception e) {
	        System.out.println("Error in FileReader !!!");
	        e.printStackTrace();
	    }
    	System.out.println("Total resource demand: CPU "+totalCPU+" Mips, Memory "+totalMem+" Megabyte, Storage "+totalStorage+" Megabyte");
		writeLoad(runNum, totalCPU, totalMem, totalStorage);
    	//System.out.println("CPU: "+totalCPU+" Memory: "+totalMem+" Storage: "+totalStorage);
	}
      
  		
  		

  	private void writeLoad(int runNum, double totalCPU, double totalMem, double totalStorage) {
  		
  		String file_path = Constants.ReqRes + "inLoad-"+methodType+".csv";
        FileWriter fileWriter = null;
        String COMMA_DELIMITER = ",";
        String NEW_LINE_SEPARATOR = "\n";
        try 
        {    
        	if (runNum == 0) {
        		fileWriter = new FileWriter(file_path, false);
                
	        	fileWriter.append("Run").append(COMMA_DELIMITER)
		    	.append("CPU").append(COMMA_DELIMITER)
		    	.append("Memory").append(COMMA_DELIMITER)
		    	.append("Storage").append(COMMA_DELIMITER)
		    	.append(System.lineSeparator());
            }
        	else {
	        		fileWriter = new FileWriter(file_path, true);//append to the file
	        	}
	            fileWriter.append(String.valueOf(runNum));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.2f",totalCPU));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.2f",totalMem));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.2f",totalStorage));
	            fileWriter.append(NEW_LINE_SEPARATOR);
	            
	            //System.out.println("CSV file was created successfully !!!");
	            fileWriter.flush();
	            fileWriter.close();  
	            
	        } 
        catch (Exception e) 
	        {
	            System.out.println("Error in CsvFileWriter !!!");
	            e.printStackTrace();
	        } 
	}


		/*
		 * public static MobileARservice getServiceById(int sourceId) { for
		 * (MobileARservice service: getTempServiceList()) if (service.getSourceId() ==
		 * sourceId) return service; return null;
		 * 
		 * }
		 */
		
  		/**
  		 * @return all the services created with the input traffic profile in this run
  		 * the services are either valid (the vehicle is in the area), or invalid (the vehicle is not in the area) in current run
  		 */
  		public List<MobileARservice> getServiceList() {
			return serviceList;
		}

  		/**
  		 * @return number of vehicles/services in the area currently
  		 */
  		public int getServicesInArea() {
  			return servicesInArea;
  		}
  		
  		/**
  	     * Set services deadline threshold
  	     * for all services the threshold is set to a fixed specific value
  	     * we focus on delay-sensitive fog services, you might set the penalty of violating the delay threshold to a random number in U(10; 20) per % per sec
  	     * penalty = new double[numSmartThings];
  	     * ArrayFiller.fill1DArrayRandomlyInRange(penalty, 10d, 20d);
  	     * @param threshold
  	     */
  	    public void setThresholds(double threshold) {
  	   	 
  		    ArrayFiller.fill1DArrayRandomlyInRange(th, 10d, 10d); 
  		         /*
  		   	  for (int a = 0; a < servicelist.size(); a++) {
  		            th[a] = threshold;
  		        }
  		        */
  	    }
  	    
}
