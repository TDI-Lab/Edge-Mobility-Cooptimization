package EdgeEPOS.Utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import EdgeEPOS.CostComponents.ComboCosts;
import EdgeEPOS.CostComponents.RunCost;
import EdgeEPOS.PlacementMethods.Agent;
import EdgeEPOS.PlacementMethods.Plan;
import EdgeEPOS.PlacementMethods.ServiceDeploymentMethod;
import EdgeEPOS.Setting.Constants;
import fog.entities.ApDevice;
import fog.entities.FogDevice;
/**
 * @author rooyesh
 *
 */
public class Utility {
	
	
    //static String planDatasetPath = Constants.base_Path; 
    static String line = "";
    static String cvsSplitBy = ",";
    static int iteration;
    private static short[] globalLoadedServices;
	private static double[] globalCPULoad; 
	private static double[]  globalMemLoad;
	private static double[] globalMemLoadFromUtil;
	private static double[] globalCPULoadFromUtil;
	
	private static double lmin;
	private static double gmin;
	private static double lmax;
	private static double gmax;
	
	public static void initialize() {
		
	}
	
	/**
	 * writes the information about the network capacity in file
	 * @param apDevices 
	 * @param serverCloudlets 
	 */
	public static void printNetCapacity(List<ApDevice> apDevices, List<FogDevice> serverCloudlets) {
		
		String CSV_SEPARATOR = ",";
    	int i = 0;
    	double totalCPU = 0, totalMem = 0, totalStorage = 0;
    	
				try {
			    	//@SuppressWarnings("resource")
					BufferedWriter csvWriter = new BufferedWriter(new FileWriter(Constants.NetOutFile+"AP_SC.csv"));
			    	
			    	csvWriter.append("APid").append(CSV_SEPARATOR)
			    	.append("DownBW-Mbps").append(CSV_SEPARATOR)
			    	.append("UPBW-Mbps").append(CSV_SEPARATOR)
			    	.append("PropagDelay-second").append(CSV_SEPARATOR)
			    	.append("Power-nj/bit").append(CSV_SEPARATOR)
			    	.append("SCid").append(CSV_SEPARATOR)
			    	.append("ProcPower-totalmips").append(CSV_SEPARATOR)
			    	.append("Ram-MB").append(CSV_SEPARATOR)
			    	.append("Storage-MB").append(CSV_SEPARATOR)
			    	.append("DownBW-Mbps").append(CSV_SEPARATOR)
			    	.append("UPBW-Mbps").append(CSV_SEPARATOR)
			    	.append("PropagDelay-second").append(CSV_SEPARATOR)
			    	.append("MaxPower-watt").append(CSV_SEPARATOR)
			    	.append("IdlePower-watt").append(CSV_SEPARATOR)
			    	.append(System.lineSeparator());
			    	
			    	for (i = 0; i<apDevices.size() ; i++) {
						csvWriter.append(apDevices.get(i).getMyId()+"").append(CSV_SEPARATOR)
						.append(apDevices.get(i).getDownlinkBandwidth()+"").append(CSV_SEPARATOR)
						.append(apDevices.get(i).getUplinkBandwidth()+"").append(CSV_SEPARATOR)				
						.append(apDevices.get(i).getUplinkLatency()+"").append(CSV_SEPARATOR)				
						.append(apDevices.get(i).getEnergyConsumption()+"").append(CSV_SEPARATOR)
						.append(serverCloudlets.get(i).getMyId()+"").append(CSV_SEPARATOR)	
						.append(serverCloudlets.get(i).getHost().getTotalMips()+"").append(CSV_SEPARATOR)
						.append(serverCloudlets.get(i).getHost().getRam()+"").append(CSV_SEPARATOR)				
						.append(serverCloudlets.get(i).getHost().getStorage()+"").append(CSV_SEPARATOR)				
						.append(serverCloudlets.get(i).getDownlinkBandwidth()+"").append(CSV_SEPARATOR)
						.append(serverCloudlets.get(i).getUplinkBandwidth()+"").append(CSV_SEPARATOR)				
						.append(serverCloudlets.get(i).getUplinkLatency()+"").append(CSV_SEPARATOR)				
						.append(serverCloudlets.get(i).getHost().getMaxPower()+"").append(CSV_SEPARATOR)
						.append(serverCloudlets.get(i).getHost().getIdlePower()+"").append(CSV_SEPARATOR)
						.append(System.lineSeparator());
						
						totalCPU += serverCloudlets.get(i).getHost().getTotalMips();//at the beginning they are the same; not total???
						totalMem += serverCloudlets.get(i).getHost().getRam();
						totalStorage += serverCloudlets.get(i).getHost().getStorage();
			    		
			    	}
		    	
		    	csvWriter.close();
		    	}
				catch (Exception e) {
			        System.out.println("Error in FileWriter !!!");
			        e.printStackTrace();
			    }
		    	
				printTotCap(totalCPU, totalMem, totalStorage, -1);
			
			
	}

	public static void printTotCap(double totalCPU, double totalMem, double totalSto, int index) {
		
		String CSV_SEPARATOR = ",";
		FileWriter csvWriter;
		System.out.print("Total resources available in the network: ");
		System.out.printf("	CPU (mips): %.2f",totalCPU);
		System.out.printf(", Memory (MB): %.2f",totalMem);
		System.out.printf(", Storage (MB): %.2f\n",totalSto);
		
		try {
			
			if (index == -1) {
    			csvWriter = new FileWriter (Constants.ReqRes + "totNetCap.csv", false);
        		csvWriter.append("Run").append(CSV_SEPARATOR)
		    	.append("CPU-Mips").append(CSV_SEPARATOR)
		    	.append("Memory-MB").append(CSV_SEPARATOR)
		    	.append("Storage-MB").append(CSV_SEPARATOR)
		    	.append("CPU-cloud").append(CSV_SEPARATOR)
		    	.append("Memory-cloud").append(CSV_SEPARATOR)
		    	.append("Storage-cloud").append(CSV_SEPARATOR)
		    	.append(System.lineSeparator());
        		
        		csvWriter.append(index+"").append(CSV_SEPARATOR)
		    	.append(totalCPU+"").append(CSV_SEPARATOR)
				.append(totalMem+"").append(CSV_SEPARATOR)				
				.append(totalSto+"").append(CSV_SEPARATOR)				
				.append(Constants.FP[Constants.numNodes-1]+"").append(CSV_SEPARATOR)				
				.append(Constants.FM[Constants.numNodes-1]+"").append(CSV_SEPARATOR)				
				.append(Constants.FS[Constants.numNodes-1]+"").append(CSV_SEPARATOR)				
				.append(System.lineSeparator());
    		}
    		else {
    			csvWriter = new FileWriter (Constants.ReqRes + "totNetCap.csv", true);
            	csvWriter.append(index+"").append(CSV_SEPARATOR)
		    	.append(totalCPU+"").append(CSV_SEPARATOR)
				.append(totalMem+"").append(CSV_SEPARATOR)				
				.append(totalSto+"").append(CSV_SEPARATOR)				
				.append(Constants.FP[Constants.numNodes-1]+"").append(CSV_SEPARATOR)				
				.append(Constants.FM[Constants.numNodes-1]+"").append(CSV_SEPARATOR)				
				.append(Constants.FS[Constants.numNodes-1]+"").append(CSV_SEPARATOR)				
				.append(System.lineSeparator());
				
	    	}
    		csvWriter.close();
    		
		}
    		
		catch (Exception e) 
        {
        System.out.println("Error in CsvFileWriter !!!");
        }
	
		
	}
	
	public static void writePlan(Plan p, int agIndex) {
		int numNodes = Constants.numNodes;
		globalLoadedServices = new short[numNodes];
		globalCPULoad = new double[numNodes];
		globalMemLoad = new double[numNodes];
		globalMemLoadFromUtil = new double[numNodes];
		globalCPULoadFromUtil = new double[numNodes];
		
	    //desSort(agentPlans);
		writeUtilizationToFile(p, agIndex);
        writeBinaryToFile(p, agIndex);
        
	}

	public static void writePlans(Plan[] agentPlans, int agIndex){
		
		int numNodes = Constants.numNodes;
		globalLoadedServices = new short[numNodes];
		globalCPULoad = new double[numNodes];
		globalMemLoad = new double[numNodes];
		globalMemLoadFromUtil = new double[numNodes];
		globalCPULoadFromUtil = new double[numNodes];
		
	    //desSort(agentPlans);
		writeUtilizationToFile(agentPlans, agIndex);
        writeBinaryToFile(agentPlans, agIndex);
        
	}
 
	public static void extractCosts(int run, int type, int betaindex, int numPlans, List<Agent> agents, Plan[][] plans, ArrayList<Integer> selectedplans, String fileAddress, ComboCosts ePOSCosts) {
		
		int i = 0;
		String line = "";
        String cvsSplitBy = ",";
       
        int minSimulation = 0;
        BufferedReader br = null, br1 = null, br2 = null;
        double loc_cost=0.0; double glob_cost = 0.0 ;
        
		int numSimulations = Constants.EPOS_NUM_SIMULATION;
    	int numIterations = Constants.EPOS_NUM_ITERATION;
        double[] lastItrCosts= new double[numSimulations];
        
    	File dir = new File(fileAddress);
        String global_cost_File = dir+"/global-cost.csv";
        String local_cost_File = dir+"/local-cost.csv";
        String selected_plans_File =dir+"/selected-plans.csv";
        

        //extracts the iteration and run number with the minimum global-cost
        try {
        
        	//****************Global-cost
        	br = new BufferedReader(new FileReader(global_cost_File));
            for (i = 0; i < numIterations; i++)//go to the last iteration // br.readHeaders();
                br.readLine();
            
            line = br.readLine();//last iteration
            String[] input = line.split(cvsSplitBy);
            
            for (i = 0; i < numSimulations; i++)//extracts the costs for every simulation of last iteration
                lastItrCosts[i] = Double.parseDouble(input[3+i]);

            minSimulation = findMinRun(lastItrCosts, numSimulations);//the index of min(global-cost) from the costs corresponding to the last iteration
            glob_cost = lastItrCosts[minSimulation];

            //****************Local-cost
            br1 = new BufferedReader(new FileReader(local_cost_File));
            for (i = 0; i < numIterations; i++)
                br1.readLine();
            
            line = br1.readLine();
            String[] input1 = line.split(cvsSplitBy);
            loc_cost = Double.parseDouble(input1[3 + minSimulation]);//reads the corresponding local-costs for the minSimulation
            
            
            //****************Selected plans
            br2 = new BufferedReader(new FileReader(selected_plans_File));
            // br.readHeaders();
            int index = numIterations * minSimulation + numIterations;//index of selected plans
            for (i = 0; i < index; i++)
                br2.readLine();
            line = br2.readLine();//index+1
            String[] input2 = line.split(cvsSplitBy);
            
            for (i = 0; i < Constants.EPOS_NUM_AGENT; i++){
            	selectedplans.add(i, Integer.parseInt(input2[2+i]));//selected plans for all agents
            	//System.out.println(" planIndex "+planIndex.get(i));
            }
            
            
            System.out.println(", lc: "+ loc_cost+", gc: "+glob_cost);
            ePOSCosts.Runs.add(betaindex, new RunCost(betaindex, Constants.BetaConfig[betaindex], numPlans, loc_cost, glob_cost, index));
          
         
        }
        
        catch (IOException e) 
        {
            e.printStackTrace();
        
		} 
        finally {
	        try {
	            br.close();
                br1.close();
                br2.close();
	            
	        } 
	        catch(IOException e)
	        {
	            System.out.println(e);
	         }
		}
        
        writeOverallGC(type, glob_cost, loc_cost, run, betaindex, numPlans); 
        writeOverallSelectedPlans(type, plans, selectedplans, run, betaindex, numPlans);
        //writeOverallPlansAndCosts(plans, selectedplans, run, betaindex, numPlans);
        writeOverallOutputLoad(type, agents, plans, selectedplans, run, betaindex, numPlans);
        
	}


	/**
     * @param plans
     * @param planIndex
     * @param runNum
     * @param betaIndex
     * @param numPlans
     * writes selected plan index for every agent
     */
    private static void writeOverallSelectedPlans(int type, Plan[][] plans, ArrayList<Integer> planIndex, int runNum, int betaIndex, int numPlans) {
		
    	//boolean append_value = true;
    	String file_path = Constants.EPOSAnswerOverall+type+"SelectedPlans.csv";
        FileWriter fileWriter = null;
        String COMMA_DELIMITER = ",";
        String NEW_LINE_SEPARATOR = "\n";
        try 
        {    
        	if ((runNum == 0)&&(betaIndex == 0)) {
        		fileWriter = new FileWriter(file_path, false);
            	
	        	fileWriter.append("Run").append(COMMA_DELIMITER);
	        	fileWriter.append("BetaValue").append(COMMA_DELIMITER);
	        	fileWriter.append("PlanNum").append(COMMA_DELIMITER);
	        	
	        	for (int i = 0; i< Constants.EPOS_NUM_AGENT; i++) 
	        		fileWriter.append("agent-"+i).append(COMMA_DELIMITER);
	        		
	        	fileWriter.append(System.lineSeparator());
        	}
        	else 
        	{
        		fileWriter = new FileWriter(file_path, true);
            	
        	}
        	fileWriter.append(String.valueOf(runNum)).append(COMMA_DELIMITER);
        	fileWriter.append(String.valueOf(Constants.BetaConfig[betaIndex])).append(COMMA_DELIMITER);
        	fileWriter.append(String.valueOf(numPlans)).append(COMMA_DELIMITER);
        	for (int i = 0; i< Constants.EPOS_NUM_AGENT; i++) {
        		fileWriter.append(String.valueOf(planIndex.get(i))).append(COMMA_DELIMITER);
        		//plans[i][planIndex.get(i)].selected = true;//check if corrects
        		//selectedPlanIndex[i] = planIndex.get(i);
        	}
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
    
    
   
    /**
     * @param agents
     * @param plans
     * @param selPlanIndex
     * @param runNum
     * @param betaindex
     * @param numPlans
     * writes the overall load on every node as a result of selected plans - calculated, not from gplan.util
     */
    private static void writeOverallOutputLoad(int type, List<Agent> agents, Plan[][] plans, ArrayList<Integer> selPlanIndex, int runNum, int betaindex, int numPlans) {

    	int cloudletIndex;
    	boolean append_value = true; String COMMA_DELIMITER = ",";
    	//String file_path = Constants.PlansAndCosts + "SelectedPlans.csv";
        //FileWriter file_Writer = null;
       
        FileWriter file_Writer_Srvload;
		FileWriter file_Writer_Cpuload, file_Writer_Memload;
		FileWriter file_Writer_CpuUtil, file_Writer_MemUtil;
		
		short[] numServiceLoad = new short[Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
		double[] CPULoad = new double[Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
		double[] MemLoad = new double[Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
		
        try 
        {    
        	
    		String file_path_Srvload = Constants.EPOSAnswerOverall+type+"SrvRes.csv";
    		String file_path_Cpuload = Constants.EPOSAnswerOverall+type+"CPULoadRes.csv";
    		String file_path_Memload = Constants.EPOSAnswerOverall+type+"MemLoadRes.csv";
    		String file_path_CpuUtilRes = Constants.EPOSAnswerOverall+type+"CPUUtilRes.csv";
    		String file_path_MemUtilRes = Constants.EPOSAnswerOverall+type+"MemUtilRes.csv";
    		
    		if ((runNum == 0)&&(betaindex == 0)) {
    			file_Writer_Srvload = new FileWriter (file_path_Srvload, false);
        		file_Writer_Cpuload = new FileWriter (file_path_Cpuload, false);
        		file_Writer_Memload = new FileWriter (file_path_Memload, false);
        		file_Writer_CpuUtil = new FileWriter (file_path_CpuUtilRes, false);
        		file_Writer_MemUtil = new FileWriter (file_path_MemUtilRes, false);
        		
	    		file_Writer_Srvload.append("run").append(COMMA_DELIMITER);
	    		file_Writer_Srvload.append("beta").append(COMMA_DELIMITER);
	    		file_Writer_Srvload.append("numPlans").append(COMMA_DELIMITER);
	    		
	    		file_Writer_Cpuload.append("run").append(COMMA_DELIMITER);
	    		file_Writer_Cpuload.append("beta").append(COMMA_DELIMITER);
	    		file_Writer_Cpuload.append("numPlans").append(COMMA_DELIMITER);
	    		
	    		file_Writer_Memload.append("run").append(COMMA_DELIMITER);
	    		file_Writer_Memload.append("beta").append(COMMA_DELIMITER);
	    		file_Writer_Memload.append("numPlans").append(COMMA_DELIMITER);
	    		
	    		file_Writer_MemUtil.append("run").append(COMMA_DELIMITER);
	    		file_Writer_MemUtil.append("beta").append(COMMA_DELIMITER);
	    		file_Writer_MemUtil.append("numPlans").append(COMMA_DELIMITER);
	    		
	    		file_Writer_CpuUtil.append("run").append(COMMA_DELIMITER);
	    		file_Writer_CpuUtil.append("beta").append(COMMA_DELIMITER);
	    		file_Writer_CpuUtil.append("numPlans").append(COMMA_DELIMITER);
	    		
	    		
	    		for(int i = 0; i < globalLoadedServices.length; i++){
	    			file_Writer_Srvload.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_Cpuload.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_CpuUtil.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_Memload.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_MemUtil.append("cloudlet-"+i).append(COMMA_DELIMITER);
		          
		          }
	    		file_Writer_Srvload.append(System.lineSeparator());
	    		file_Writer_Cpuload.append(System.lineSeparator());
	    		file_Writer_CpuUtil.append(System.lineSeparator());
	    		file_Writer_Memload.append(System.lineSeparator());
	    		file_Writer_MemUtil.append(System.lineSeparator());
    		}
    		else{
    			file_Writer_Srvload = new FileWriter (file_path_Srvload, append_value);
        		file_Writer_Cpuload = new FileWriter (file_path_Cpuload, append_value);
        		file_Writer_CpuUtil = new FileWriter (file_path_CpuUtilRes, append_value);
        		file_Writer_Memload = new FileWriter (file_path_Memload, append_value);
        		file_Writer_MemUtil = new FileWriter (file_path_MemUtilRes, append_value);
        		}
	         
    		
    		for(int i = 0; i < Constants.EPOS_NUM_AGENT; i++){//calculates the service load over all nodes: number of services and cpu load
    			if (plans[i][selPlanIndex.get(i)].empty_node) 
    				continue;
    			for(int col = 0; col < plans[i][selPlanIndex.get(i)].y.length; col++){
        			cloudletIndex = plans[i][selPlanIndex.get(i)].y[col];
        		 	numServiceLoad[cloudletIndex] += 1;
        		 	CPULoad[cloudletIndex] += agents.get(i).serviceList.get(col).getCpuDemand1();
        		 	MemLoad[cloudletIndex] += agents.get(i).serviceList.get(col).getMemDemand();
        		 }
	        	 
	         }
	         
	        file_Writer_Srvload.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(Constants.BetaConfig[betaindex]))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numPlans))).append(COMMA_DELIMITER);
	        
	        file_Writer_Cpuload.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(Constants.BetaConfig[betaindex]))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numPlans))).append(COMMA_DELIMITER);
    		
	        file_Writer_CpuUtil.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(Constants.BetaConfig[betaindex]))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numPlans))).append(COMMA_DELIMITER);
	        
	        file_Writer_Memload.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(Constants.BetaConfig[betaindex]))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numPlans))).append(COMMA_DELIMITER);
    		
	        file_Writer_MemUtil.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(Constants.BetaConfig[betaindex]))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numPlans))).append(COMMA_DELIMITER);
    		
	        for(int i = 0; i < numServiceLoad.length; i++){
	        	file_Writer_Srvload.append(String.format(String.valueOf(numServiceLoad[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	
	        	file_Writer_Cpuload.append(String.format(String.valueOf(CPULoad[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	file_Writer_Memload.append(String.format(String.valueOf(MemLoad[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	
	        	if(Constants.FM[i] != 0) {
		        	file_Writer_CpuUtil.append(String.format(String.valueOf(CPULoad[i]/Constants.FP[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
		        	file_Writer_MemUtil.append(String.format(String.valueOf(MemLoad[i]/Constants.FM[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	}
	        	else {
		        	file_Writer_CpuUtil.append(String.format(String.valueOf(CPULoad[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        		file_Writer_MemUtil.append(String.format(String.valueOf(MemLoad[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	}
	        }
	        file_Writer_Srvload.append(System.lineSeparator());        
	        file_Writer_Cpuload.append(System.lineSeparator());    
	        file_Writer_CpuUtil.append(System.lineSeparator()); 
	        file_Writer_Memload.append(System.lineSeparator());    
	        file_Writer_MemUtil.append(System.lineSeparator()); 
	        
	        file_Writer_Srvload.flush();
	        file_Writer_Srvload.close();
	        file_Writer_Cpuload.flush();
	        file_Writer_Cpuload.close();  
	        file_Writer_CpuUtil.flush();
	        file_Writer_CpuUtil.close();
	        file_Writer_Memload.flush();
	        file_Writer_Memload.close();  
	        file_Writer_MemUtil.flush();
	        file_Writer_MemUtil.close();
             
        } 
    	catch (Exception e) 
        {
        System.out.println("Error in CsvFileWriter !!!");
        e.printStackTrace();
        } 
    }
    
    
    
   
    /**
     * @param optimalRunDir
     * @param agents
     * @param plans
     * @param selPlanIndex
     * @param runNum
     * @param beta
     * @param numberOfPlans
     * @param nLocalCost 
     * @param nGlobalCost 
     * @return the metrics for the Pareto optimal run (optimalRun) of EPOS
     */
    public static double getSelInputFromEpos(int optIndex, double lc, double gc, String optimalRunDir, List<Agent> agents, int type, Plan[][] plans, ArrayList<Integer> selPlanIndex, int runNum, double beta, int numberOfPlans, double nGlobalCost, double nLocalCost){
    	
    		System.out.println("Reading optimal output directory from EPOS......");
    	
	    	int numSimulations = Constants.EPOS_NUM_SIMULATION;
	    	int numIterations = Constants.EPOS_NUM_ITERATION;
	    	BufferedReader br2 = null;
	    	
	        File dir = new File("output/"+optimalRunDir);
	        String selected_plans_File =dir+"/selected-plans.csv";
	        String line = "";
	        String cvsSplitBy = ",";
	        
	        int i = 1;
	        int minSimulation = 0;
	        int index=0;
	        //double loc_cost=0.0;double gc = 0.0;
	        double[] lastItrCosts= new double[numSimulations];
	       
	        try {
	        
				/*
				 * br = new BufferedReader(new FileReader(global_cost_File)); for (i = 0; i <
				 * numIterations; i++) br.readLine();
				 * 
				 * line = br.readLine();//last iteration String[] input =
				 * line.split(cvsSplitBy); for (i = 0; i < numSimulations; i++) lastItrCosts[i]
				 * = Double.parseDouble(input[3+i]);//costs for every simulation of last
				 * iteration
				 * 
				 * minSimulation = findMinRun(lastItrCosts, numSimulations);//the index of
				 * min(global-cost) from costs gc = lastItrCosts[minSimulation];
				 * 
				 * 
				 * br1 = new BufferedReader(new FileReader(local_cost_File)); for (i = 0; i <
				 * numIterations; i++) br1.readLine();
				 * 
				 * line = br1.readLine(); String[] input1 = line.split(cvsSplitBy); loc_cost =
				 * Double.parseDouble(input1[3 + minSimulation]);//read the local cost for the
				 * minSimulation
				 */	
	        
	        
	            //index = numIterations * minSimulation + numIterations;
	        	br2 = new BufferedReader(new FileReader(selected_plans_File));
	            for (i = 0; i < optIndex; i++)
	                br2.readLine();
	            line = br2.readLine();//index+1
	            String[] input2 = line.split(cvsSplitBy);
	            for (i = 0; i < Constants.EPOS_NUM_AGENT; i++){
	            	selPlanIndex.add(i, Integer.parseInt(input2[2+i]));
	            	
	            }
	           
	            
	        }
	        catch (IOException e) 
	        {
	            e.printStackTrace();
	        }
     
	        finally {
		        try {
		            br2.close();
		            
		        } 
		        catch(IOException e)
		        {
		            System.out.println(e);
		         }
	        }
	    writeGC(type, gc, lc, runNum, beta, numberOfPlans, nGlobalCost, nLocalCost); 
	    writeSelectedPlans(type, plans, selPlanIndex, runNum, beta, numberOfPlans);
	    writePlansAndCosts(type, plans, selPlanIndex, runNum);
	    writeOutputLoad(type, agents, plans, selPlanIndex, runNum, beta, numberOfPlans);
	    writeRequests(type, agents, runNum);
	    
	    return gc;
	 }
    
    /**
     * @param agents
     * @param run_Num
     * writes the input workload (Num. service and cpuload) over every cloudlet
     */
    public static void writeRequests(int type, List<Agent> agents, int run_Num) {
		
    	int [] globalServReq = new int[Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
	 	double [] globalCPUReqLoad = new double [Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
	 	double[] globalMemReqLoad = new double [Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
		 
		
    	boolean append_value = true;
    	String COMMA_DELIMITER = ",";
        
    	FileWriter file_Writer_SrvReq; 
    	FileWriter file_Writer_CPULoad, file_Writer_MemLoad;
    	FileWriter file_Writer_CPUUtil, file_Writer_MemUtil;
        
    	try 
        {    
        	String file_path_SrvReq = Constants.ReqRes + type + "SrvReq.csv";
        	String file_path_CPULoadReq = Constants.ReqRes + type + "CPULoadReq.csv";
        	String file_path_MemLoadReq = Constants.ReqRes + type + "MemLoadReq.csv";
    		String file_path_CPUUtilReq = Constants.ReqRes + type + "CPUUtilReq.csv";
    		String file_path_MemUtilReq = Constants.ReqRes + type + "MemUtilReq.csv";
    		
        	
        	
    		if (run_Num == 0) {
    			
    			file_Writer_SrvReq = new FileWriter (file_path_SrvReq, false);
        		file_Writer_CPULoad = new FileWriter (file_path_CPULoadReq, false);
        		file_Writer_MemLoad = new FileWriter (file_path_MemLoadReq, false);
        		file_Writer_CPUUtil = new FileWriter (file_path_CPUUtilReq, false);
        		file_Writer_MemUtil = new FileWriter (file_path_MemUtilReq, false);
        		
	    		file_Writer_SrvReq.append("run").append(COMMA_DELIMITER);
	    		file_Writer_CPULoad.append("run").append(COMMA_DELIMITER);
	    		file_Writer_MemLoad.append("run").append(COMMA_DELIMITER);
	    		file_Writer_CPUUtil.append("run").append(COMMA_DELIMITER);
	    		file_Writer_MemUtil.append("run").append(COMMA_DELIMITER);
	    		
	    		for(int i = 0; i < Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS; i++){
	    			file_Writer_SrvReq.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_CPULoad.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_MemLoad.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_CPUUtil.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_MemUtil.append("cloudlet-"+i).append(COMMA_DELIMITER);
		          }
	    		
	    		file_Writer_SrvReq.append(System.lineSeparator());
	    		file_Writer_CPULoad.append(System.lineSeparator());
	    		file_Writer_MemLoad.append(System.lineSeparator());
	    		file_Writer_CPUUtil.append(System.lineSeparator());
	    		file_Writer_MemUtil.append(System.lineSeparator());
    		}
    		else 
    		{
    			
    			file_Writer_SrvReq = new FileWriter (file_path_SrvReq, append_value);
        		file_Writer_CPULoad = new FileWriter (file_path_CPULoadReq, append_value);
        		file_Writer_MemLoad = new FileWriter (file_path_MemLoadReq, append_value);
        		file_Writer_CPUUtil = new FileWriter (file_path_CPUUtilReq, append_value);
        		file_Writer_MemUtil = new FileWriter (file_path_MemUtilReq, append_value);
    		
    		}
	         
    		for(int i = 0; i < Constants.EPOS_NUM_AGENT; i++){
    			globalServReq [i] = agents.get(i).getServiceSize(); 
    			for(int col = 0; col < agents.get(i).getServiceSize(); col++){
        			globalCPUReqLoad[i] += agents.get(i).serviceList.get(col).getCpuDemand1();
        			globalMemReqLoad[i] += agents.get(i).serviceList.get(col).getMemDemand();
        			
        		 }
	        	 
	         }
	         
    		file_Writer_SrvReq.append(String.format(String.valueOf(run_Num))).append(COMMA_DELIMITER);
	        file_Writer_CPULoad.append(String.format(String.valueOf(run_Num))).append(COMMA_DELIMITER);
	        file_Writer_MemLoad.append(String.format(String.valueOf(run_Num))).append(COMMA_DELIMITER);
	        file_Writer_CPUUtil.append(String.format(String.valueOf(run_Num))).append(COMMA_DELIMITER);
	        file_Writer_MemUtil.append(String.format(String.valueOf(run_Num))).append(COMMA_DELIMITER);
	        
	        for(int i = 0; i < globalCPUReqLoad.length; i++){
	        	file_Writer_SrvReq.append(String.format(String.valueOf(globalServReq[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	file_Writer_CPULoad.append(String.format(String.valueOf(globalCPUReqLoad[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	file_Writer_MemLoad.append(String.format(String.valueOf(globalMemReqLoad[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	//String.format("%.8f",
	        	if (Constants.FP[i] != 0) {
	        		file_Writer_CPUUtil.append(String.format(String.valueOf(globalCPUReqLoad[i]/Constants.FP[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        		file_Writer_MemUtil.append(String.format(String.valueOf(globalMemReqLoad[i]/Constants.FM[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	}
	        	else {
	        		file_Writer_CPUUtil.append(String.format(String.valueOf(globalCPUReqLoad[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        		file_Writer_MemUtil.append(String.format(String.valueOf(globalMemReqLoad[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
		        }
	        }
	        file_Writer_SrvReq.append(System.lineSeparator());        
	        file_Writer_CPULoad.append(System.lineSeparator());    
	        file_Writer_MemLoad.append(System.lineSeparator());    
	        file_Writer_CPUUtil.append(System.lineSeparator()); 
	        file_Writer_MemUtil.append(System.lineSeparator());
	        
	        file_Writer_SrvReq.flush();
	        file_Writer_SrvReq.close();
	        file_Writer_CPULoad.flush();
	        file_Writer_CPULoad.close();
	        file_Writer_MemLoad.flush();
	        file_Writer_MemLoad.close();
	        file_Writer_CPUUtil.flush();
	        file_Writer_CPUUtil.close();
	        file_Writer_MemUtil.flush();
	        file_Writer_MemUtil.close();
             
        } 
    	catch (Exception e) 
        {
	        System.out.println("Error in CsvFileWriter !!!");
	        e.printStackTrace();
        } 
		
	}
    
	/**
	 * @param agents
	 * @param plans
	 * @param planIndex selected plans indices
	 * @param runNum
	 * @param beta
	 * @param numberOfPlans
	 * writes the total load on cloudlets from global plan including cpu and pow load from utilization vectors and Num. services and cpu load from service profiles
	 * ----> util > 1
	 */
	private static void writeOutputLoad(int type, List<Agent> agents, Plan[][] plans, ArrayList<Integer> planIndex, int runNum, double beta, int numberOfPlans) {

    	int cloudletIndex;
    	boolean append_value = true;

        String COMMA_DELIMITER = ",";
        
        FileWriter file_Writer_Srvload;
		FileWriter file_Writer_CPUload, file_Writer_Memload;
		FileWriter file_Writer_CPUUtil, file_Writer_MemUtil;
		
		int nodes = Constants.numNodes;
		
        try 
        {    
        	
    		String file_path_srvloadrespo = Constants.ReqRes +type+"SrvRespo.csv";
    		String file_path_cpuloadrespo = Constants.ReqRes +type+"CPULoadRespo.csv";
    		String file_path_memloadrespo = Constants.ReqRes +type+"MemLoadRespo.csv";
    		String file_path_mem_utilrespo = Constants.ReqRes +type+"MemUtilRespo.csv";
    		String file_path_cpu_utilrespo = Constants.ReqRes +type+"CPUUtilRespo.csv";
    		
    		if (runNum == 0) {
    			file_Writer_Srvload = new FileWriter (file_path_srvloadrespo, false);
        		file_Writer_CPUload = new FileWriter (file_path_cpuloadrespo, false);
        		file_Writer_Memload = new FileWriter (file_path_memloadrespo, false);
        		file_Writer_CPUUtil = new FileWriter (file_path_cpu_utilrespo, false);
        		file_Writer_MemUtil = new FileWriter (file_path_mem_utilrespo, false);
        		
	    		file_Writer_Srvload.append("run").append(COMMA_DELIMITER)
	    		.append("beta").append(COMMA_DELIMITER)
	    		.append("planNum").append(COMMA_DELIMITER);
	    		
	    		file_Writer_CPUload.append("run").append(COMMA_DELIMITER)
	    		.append("beta").append(COMMA_DELIMITER)
	    		.append("planNum").append(COMMA_DELIMITER);
	    		
	    		file_Writer_Memload.append("run").append(COMMA_DELIMITER)
	    		.append("beta").append(COMMA_DELIMITER)
	    		.append("planNum").append(COMMA_DELIMITER);
	    		
	    		file_Writer_CPUUtil.append("run").append(COMMA_DELIMITER)
	    		.append("beta").append(COMMA_DELIMITER)
	    		.append("planNum").append(COMMA_DELIMITER);
	    		
	    		file_Writer_MemUtil.append("run").append(COMMA_DELIMITER)
	    		.append("beta").append(COMMA_DELIMITER)
	    		.append("planNum").append(COMMA_DELIMITER);
	    		//System.out.println("globalLoadedServices "+globalLoadedServices.length);
	    		
	    		for(int i = 0; i < globalLoadedServices.length; i++){
	    			file_Writer_Srvload.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_CPUload.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_Memload.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_CPUUtil.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_MemUtil.append("cloudlet-"+i).append(COMMA_DELIMITER);
		          }
	    		file_Writer_Srvload.append(System.lineSeparator());
	    		file_Writer_CPUload.append(System.lineSeparator());
	    		file_Writer_Memload.append(System.lineSeparator());
	    		file_Writer_CPUUtil.append(System.lineSeparator());
	    		file_Writer_MemUtil.append(System.lineSeparator());
    		}
    		else{
    			file_Writer_Srvload = new FileWriter (file_path_srvloadrespo, append_value);
        		file_Writer_CPUload = new FileWriter (file_path_cpuloadrespo, append_value);
        		file_Writer_Memload = new FileWriter (file_path_memloadrespo, append_value);
        		file_Writer_CPUUtil = new FileWriter (file_path_cpu_utilrespo, append_value);
        		file_Writer_MemUtil = new FileWriter (file_path_mem_utilrespo, append_value);
        	}
	         
    		for(int i = 0; i < Constants.EPOS_NUM_AGENT; i++){
    			if (plans[i][planIndex.get(i)].empty_node) 
    				continue;
    			for(int col = 0; col < plans[i][planIndex.get(i)].y.length; col++){
        			cloudletIndex = plans[i][planIndex.get(i)].y[col];							//selected host
        		 	globalLoadedServices[cloudletIndex] += 1;
        		 	globalCPULoad[cloudletIndex] += agents.get(i).serviceList.get(col).getCpuDemand1();
        		 	globalMemLoad[cloudletIndex] += agents.get(i).serviceList.get(col).getMemDemand();
        		 }
    			for(int col = 0; col<nodes; col++) {
    				globalCPULoadFromUtil[col] += plans[i][planIndex.get(i)].utilPlan[col];
    				globalMemLoadFromUtil[col] += plans[i][planIndex.get(i)].utilPlan[col+nodes];
    			}
	         }
	         
	        file_Writer_Srvload.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(beta))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numberOfPlans))).append(COMMA_DELIMITER);
	        
	        file_Writer_CPUload.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(beta))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numberOfPlans))).append(COMMA_DELIMITER);
	        
	        file_Writer_Memload.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(beta))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numberOfPlans))).append(COMMA_DELIMITER);
	        
	        file_Writer_CPUUtil.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(beta))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numberOfPlans))).append(COMMA_DELIMITER);
	        
	        file_Writer_MemUtil.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(beta))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numberOfPlans))).append(COMMA_DELIMITER);
	        
	       
	        for(int i = 0; i < globalLoadedServices.length; i++){
	        	file_Writer_Srvload.append(String.format(String.valueOf(globalLoadedServices[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	file_Writer_CPUload.append(String.format(String.valueOf(globalCPULoad[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	file_Writer_Memload.append(String.format(String.valueOf(globalMemLoad[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	file_Writer_CPUUtil.append(String.format(String.valueOf(globalCPULoadFromUtil[i]))).append(COMMA_DELIMITER);
	        	file_Writer_MemUtil.append(String.format(String.valueOf(globalMemLoadFromUtil[i]))).append(COMMA_DELIMITER);
	        }
	        file_Writer_Srvload.append(System.lineSeparator());        
	        file_Writer_CPUload.append(System.lineSeparator());    
	        file_Writer_Memload.append(System.lineSeparator());    
	        file_Writer_CPUUtil.append(System.lineSeparator()); 
	        file_Writer_MemUtil.append(System.lineSeparator()); 
	        
	        file_Writer_Srvload.flush();
	        file_Writer_Srvload.close();
	        file_Writer_CPUload.flush();
	        file_Writer_CPUload.close();
	        file_Writer_Memload.flush();
	        file_Writer_Memload.close();
	        file_Writer_CPUUtil.flush();
	        file_Writer_CPUUtil.close(); 
	        file_Writer_MemUtil.flush();
	        file_Writer_MemUtil.close(); 
	        
             
        } 
    	catch (Exception e) 
        {
        System.out.println("Error in CsvFileWriter !!!");
        e.printStackTrace();
        } 
    }

	
    
    /**
     * @param plans
     * @param planIndex
     * @param runNum
     * writes the components of local-cost for all the generated plans of all agents
     * writes the value of different cost components for every agent's selected plan in every run
     */
    private static void writePlansAndCosts(int type, Plan[][] plans, ArrayList<Integer> planIndex, int runNum) {
		
    	
    	boolean append_value = false;
    	//String file_path = Constants.PlansAndCosts + "SelectedPlans.csv";
        //FileWriter file_Writer = null;
        String COMMA_DELIMITER = ",";
        //String NEW_LINE_SEPARATOR = "\n";
        PrintWriter out;
        String file_path_EPOS ;
        try 
        {    
        	File theDir = new File(Constants.EPOSAnswer+"run"+runNum);
        	if (!theDir.exists()){
        	    theDir.mkdirs();
        	}
        	
        	for(int i = 0; i < Constants.EPOS_NUM_AGENT; i++){
        		if(Constants.win == true)
        			file_path_EPOS = Constants.EPOSAnswer+"run"+runNum+"\\"+type+"\\agent_"+i+".csv";
        		else
        			file_path_EPOS = Constants.EPOSAnswer+"run"+runNum+"/"+type+"/agent_"+i+".csv";
        		
	             FileWriter file_Writer = new FileWriter (file_path_EPOS, append_value);
	             //if (runNum == 0) {
		             file_Writer.append("plan_index").append(COMMA_DELIMITER)
		            .append("serviceSize").append(COMMA_DELIMITER)
		            .append("selected").append(COMMA_DELIMITER)
		         	.append("dlViolCost").append(COMMA_DELIMITER)
		         	.append("energyCost").append(COMMA_DELIMITER)
		         	.append("procCost").append(COMMA_DELIMITER)
		         	.append("storCost").append(COMMA_DELIMITER)
		         	.append("deplCost").append(COMMA_DELIMITER)
		         	.append("commCost").append(COMMA_DELIMITER)
		         	.append("cO2Cost").append(COMMA_DELIMITER)
		         	.append("memCost").append(COMMA_DELIMITER)
		         	.append("totalLocalCost")
		         	.append(System.lineSeparator());
	         //  }
		     // writer_EPOS.write(String.format("%.4f", plans[col].utilPlan[i]))
             for(int col = 0; col < Constants.EPOS_NUM_PLANS; col++){
            	file_Writer.append(String.format(String.valueOf(plans[i][col].planIndex))).append(COMMA_DELIMITER)
            	.append(String.format(String.valueOf(plans[i][col].ServiceSize))).append(COMMA_DELIMITER)//preventing wrong written of values as negative values
            	.append(String.valueOf(plans[i][col].selected)).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i][col].getDlViolCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i][col].getEnergyCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i][col].getProcCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i][col].getStorCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i][col].getDeplCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i][col].getCommCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i][col].getCo2EmitCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i][col].getMemCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i][col].getTotalLocalCost()))
 	         	.append(System.lineSeparator());        
            	 	}
             
                
	       file_Writer.flush();
	       file_Writer.close();      
        	} 
        
        } 
        	catch (Exception e) 
        {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } 
        
        
        
        try {
        	String str;
        	String LocalCostpath = Constants.EPOSAnswer + "run-"+runNum+"-type"+type+".dat";
            out = new PrintWriter(new FileWriter(LocalCostpath));
			 
        	for(int i = 0; i < Constants.EPOS_NUM_AGENT; i++){
    			if (plans[i][planIndex.get(i)].empty_node) {
    				str = i+"";
    				for (int j = 0; j<Constants.APPLIEDCOST.length; j++) {
					 	str += " 0";
					}
    				out.println(str);
    					
    				
    			}
    			else {
    				str = i+"";
    				for (int j = 0; j<Constants.APPLIEDCOST.length; j++) {
    					str += " "+ Math.floor(plans[i][planIndex.get(i)].getCost(j)*100000000)/100000000;
    				}
    					out.println(str);
    				
	         }
        	}   
        	out.flush();
        	out.close(); 
        } 
        	catch (Exception e) 
        {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } 
        
		
	}
    
    
    
    private static void writeSelectedPlans(int type, Plan[][] plans, ArrayList<Integer> planIndex, int runNum, double beta, int numberOfPlans) {
		
    	String file_path = Constants.EPOSAnswer + "SelectedPlans-"+type+".csv";
        FileWriter fileWriter = null;
        String COMMA_DELIMITER = ",";
        String NEW_LINE_SEPARATOR = "\n";
        try 
        {    
        	if (runNum == 0) {
        		fileWriter = new FileWriter(file_path, false);
            	
	        	fileWriter.append("Run").append(COMMA_DELIMITER)
	        	.append("Beta").append(COMMA_DELIMITER)
	        	.append("PlanNum").append(COMMA_DELIMITER);
	        	
	        	for (int i = 0; i< Constants.EPOS_NUM_AGENT; i++) 
	        		fileWriter.append("agent-"+i).append(COMMA_DELIMITER);
	        		
	        	fileWriter.append(System.lineSeparator());
        	}
        	else 
        	{
        		fileWriter = new FileWriter(file_path, true);//append to file
            	
        	}
        	
        	fileWriter.append(String.valueOf(runNum)).append(COMMA_DELIMITER)
        	.append(String.valueOf(beta)).append(COMMA_DELIMITER)
        	.append(String.valueOf(numberOfPlans)).append(COMMA_DELIMITER);
        	
        	for (int i = 0; i< Constants.EPOS_NUM_AGENT; i++) {
        		fileWriter.append(String.valueOf(planIndex.get(i))).append(COMMA_DELIMITER);
        		plans[i][planIndex.get(i)].selected = true;//marks this plan as the selected for the agent 
        		
        	}
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
    
    
	
    /**
     * @param globalCosts
     * @param NumOfSimulations
     * @return the index of min(global cost) of NumOfSimulations entries
     */
    public static int findMinRun(double[] globalCosts, int NumOfSimulations){
        int minSim = 0;
        double min = globalCosts[0];
        for (int j = 0; j<NumOfSimulations; j++)
            if (globalCosts[j] < min){
                min = globalCosts[j];
                minSim = j;
            }
        return minSim;
    }
    
  
    /**
     * @param gc
     * @param lc
     * @param run
     * @param betaIndex
     * @param numPlans
     * writes global and local cost for the run with betaIndex and numPlans
     */
    public static void writeOverallGC(int type, double gc, double lc, int run, int betaIndex, int numPlans){

    	//boolean append_value = true;
    	String file_path = Constants.EPOSAnswerOverall+type+"gclc.csv";
        FileWriter fileWriter = null;
        String COMMA_DELIMITER = ",";
        String NEW_LINE_SEPARATOR = "\n";
        try 
        {    
        	if ((run == 0)&&(betaIndex == 0)) {
        		fileWriter = new FileWriter(file_path, false);
                
	        	fileWriter.append("Run").append(COMMA_DELIMITER)
		    	.append("hopLevel").append(COMMA_DELIMITER)
		    	.append("Beta").append(COMMA_DELIMITER)
		    	.append("PlanNumber").append(COMMA_DELIMITER)
		    	.append("Global-cost").append(COMMA_DELIMITER)
		    	.append("Local-cost").append(COMMA_DELIMITER)
		    	.append(System.lineSeparator());
            }
        	else {
        		fileWriter = new FileWriter(file_path, true);//append to the file
        	}
            fileWriter.append(String.valueOf(run));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(Constants.hopLevel));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(Constants.BetaConfig[betaIndex]));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(numPlans));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.format("%.12f",gc));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.format("%.12f",lc));
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            //System.out.println("CSV file was created successfully !!!");
           fileWriter.flush();
           fileWriter.close();  
        } catch (Exception e) 
        {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } 
        
         
    }
    
    
    /**
     * @param gc
     * @param lc
     * @param run
     * @param beta
     * @param numberOfPlans
     * writes selected global and local cost to file
     * @param nLocalCost 
     * @param nGlobalCost 
     */
    public static void writeGC(int type, double gc, double lc, int run, double beta, int numberOfPlans, double nGlobalCost, double nLocalCost){
        //boolean append_value = true;
    	//boolean append_value = true;
    	String file_path = Constants.EPOSAnswerOverall + "selectedgc-"+type+".csv";
        FileWriter fileWriter = null;
        String COMMA_DELIMITER = ",";
        String NEW_LINE_SEPARATOR = "\n";
        try 
        {    
        	if (run == 0) {
        		fileWriter = new FileWriter(file_path, false);
                
	        	fileWriter.append("Run").append(COMMA_DELIMITER)
		    	.append("hopLevel").append(COMMA_DELIMITER)
		    	.append("Beta").append(COMMA_DELIMITER)
		    	.append("PlanNum").append(COMMA_DELIMITER)
		    	.append("Global-cost").append(COMMA_DELIMITER)
		    	.append("Local-cost").append(COMMA_DELIMITER)
		    	.append("nGlobal-cost").append(COMMA_DELIMITER)
		    	.append("nLocal-cost").append(COMMA_DELIMITER)
		    	.append(System.lineSeparator());
            }
        	else {
        		fileWriter = new FileWriter(file_path, true);
        	}
            fileWriter.append(String.valueOf(run));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(Constants.hopLevel));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(beta));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(numberOfPlans));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.format("%.12f",gc));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.format("%.12f",lc));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.format("%.12f",nGlobalCost));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.format("%.12f",nLocalCost));
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            //System.out.println("CSV file was created successfully !!!");
           fileWriter.flush();
           fileWriter.close();  
        } catch (Exception e) 
        {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } 
        
         
    }
   
    /**
     * @param plans
     * @param id
     * prints the utilization vector (including both cpu and pow) into file
     */
    public static void writeUtilizationToFile(Plan[] plans, int id) {
	
    	boolean append_value = false;
	    	
        try { 
                String file_path_EPOS = Constants.PlanUtilDataset+"agent_"+id+".plans";
                FileWriter writer_EPOS = new FileWriter (file_path_EPOS, append_value);
                
                for(int col =0; col < Constants.EPOS_NUM_PLANS; col++){
                    writer_EPOS.append(String.valueOf(plans[col].getTotalLocalCost()));
                    writer_EPOS.append(":");
                    int size = plans[col].utilPlan.length;
                    for (int i =0; i<size ; i++){//both of CPU and pow will be printed
                         writer_EPOS.write(String.format("%.12f", plans[col].utilPlan[i]));//preventing wrong written of values as negative values
                         if (i != size-1){
                             writer_EPOS.append(",");
                         }
                     }
                    writer_EPOS.append("\r\n");
                } 
            writer_EPOS.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
      
    }
    
	/**
	 * @param plans
	 * @param id
	 * writes selected hosts for the received services in this plan
	 */
	private static void writeBinaryToFile(Plan[] plans, int id) {
		
		boolean append_value = false;
		//System.out.println("Writing Binary Plans...");
	    
		try { 
             String file_path_EPOS = Constants.PlanBinaryDataset+"agent_"+id+".plans";
             FileWriter writer_EPOS = new FileWriter (file_path_EPOS, append_value);
            
				/*
				 * writer_EPOS.append("- "); for(int snum = 0; snum < plans[0].y.length;
				 * snum++)
				 * {
				 *  writer_EPOS.append(String.valueOf(snum)+" "); 
				 *  }
				 */
           
             for(int col = 0; col < Constants.EPOS_NUM_PLANS; col++){
            	 if(plans[col].empty_node == true) {
            		 writer_EPOS.write(String.format("Node "+col+ " without any received request!"));
            		 break;
            	 }
            	 
        	 	int size = plans[col].y.length;//num of services
        		
        	 	for (int i = 0; i<size ; i++){
                     writer_EPOS.write(String.format(String.valueOf(plans[col].y[i])));
                     if (i != plans[col].y.length-1){
                         writer_EPOS.append(",");
                     }
        	 	}
             
                 writer_EPOS.append("\r\n");
             } 
         writer_EPOS.close();
                      
     }
     catch(Exception ex)
     {
         ex.printStackTrace();
     }
   
		
	}
	/**
     * @param plans
     * @param id
     * prints the utilization vector (including both cpu and pow) into file
     */
    public static void writeUtilizationToFile(Plan plan, int id) {
	
    	boolean append_value = false;
	    	
        try { 
                String file_path_EPOS = Constants.PlanUtilDataset+"agent_"+id+".plans";
                FileWriter writer_EPOS = new FileWriter (file_path_EPOS, append_value);
                
                //for(int col =0; col < Constants.EPOS_NUM_PLANS; col++){
                    writer_EPOS.append(String.valueOf(plan.getTotalLocalCost()));
                    writer_EPOS.append(":");
                    int size = plan.utilPlan.length;
                    for (int i =0; i<size ; i++){//both of CPU and ram will be printed
                         writer_EPOS.write(String.format("%.12f", plan.utilPlan[i]));//preventing wrong written of values as negative values
                         if (i != size-1){
                             writer_EPOS.append(",");
                         }
                     }
                    writer_EPOS.append("\r\n");
                 
            writer_EPOS.close();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
      
    }
    
	/**
	 * @param plans
	 * @param id
	 * writes selected hosts for the received services in this plan
	 */
	private static void writeBinaryToFile(Plan plan, int id) {
		
		boolean append_value = false;
		//System.out.println("Writing Binary Plans...");
	    
		try { 
             String file_path_EPOS = Constants.PlanBinaryDataset+"agent_"+id+".plans";
             FileWriter writer_EPOS = new FileWriter (file_path_EPOS, append_value);
            
				/*
				 * writer_EPOS.append("- "); for(int snum = 0; snum < plans[0].y.length;
				 * snum++)
				 * {
				 *  writer_EPOS.append(String.valueOf(snum)+" "); 
				 *  }
				 */
           
             	 if(plan.empty_node == true) {
            		 writer_EPOS.write(String.format("Node without any received request!"));
             	 }
             	 else {
	        	 	int size = plan.y.length;//num of services
	        		
	        	 	for (int i = 0; i<size ; i++){
	                     writer_EPOS.write(String.format(String.valueOf(plan.y[i])));
	                     if (i != plan.y.length-1){
	                         writer_EPOS.append(",");
	                     }
	        	 	}
	             
	                writer_EPOS.append("\r\n");
             	}
         writer_EPOS.close();
                      
     }
     catch(Exception ex)
     {
         ex.printStackTrace();
     }
   
	}
    
	
    private static File getFileLastModified(File[] files) {
	    File fileLastModified = null;
	    long maxLastModified = Long.MIN_VALUE;
	    for (File file : files) {
	        if (file.isDirectory()) {
	            final long lastModified = file.lastModified();
	            if (lastModified > maxLastModified) {
	                fileLastModified = file;
	                maxLastModified = lastModified;
	            }
	        }
	    }
	    return fileLastModified;
	}
    
	
	/**
	 * @param Directory
	 * @return all the files/directories in Directory based on their name (sorted by ascending date)
	 */
	public static File[] GetLastFiles(String Directory) {
		
		File folder = new File(Directory);
		File[] listOfFiles = folder.listFiles();
		//Arrays.sort(listOfFiles, Collections.reverseOrder());
		Arrays.sort(listOfFiles);//ascending
		return listOfFiles;
		
		
	}
	
	/**
	 * @param newLambda
	 * changes the epos.properties file based on the experiment configuration
	 */
	public static void setEPOSPropFile(double newLambda) {
		
		/*try{		Files.copy(Paths.get(Constants.BetaConfPath+"epos"+lambdaConfig+".properties"), Paths.get(Constants.BetaConfBasePath+"epos.properties"), StandardCopyOption.REPLACE_EXISTING);  }
        catch(Exception ex){            ex.printStackTrace();        }        
		*/
		
	      Scanner sc;
	      try {
	      	  	  sc = new Scanner(new File(Constants.BetaConfTempPath+"epos.properties"));
			      StringBuffer buffer = new StringBuffer();//instantiates StringBuffer class
			      
			      while (sc.hasNextLine()) { //Reads lines of the file and appending them to StringBuffer
			         buffer.append(sc.nextLine()+System.lineSeparator());
			      }
			      
			      String fileContents = buffer.toString();
			      sc.close();
			      
			      String oldLine = "numSimulations=10";
			      String newLine = "numSimulations="+Constants.EPOS_NUM_SIMULATION;
			      fileContents = fileContents.replaceAll(oldLine, newLine);
			      
			      String oldLine1 = "numIterations=10";
			      String newLine1 = "numIterations="+Constants.EPOS_NUM_ITERATION;
			      fileContents = fileContents.replaceAll(oldLine1, newLine1);
			      
			      String oldLine2 = "numAgents=109";
			      String newLine2 = "numAgents="+Constants.EPOS_NUM_AGENT;
			      fileContents = fileContents.replaceAll(oldLine2, newLine2);
			      
			      String oldLine3 = "numPlans=10";
			      String newLine3 = "numPlans="+Constants.EPOS_NUM_PLANS;
			      fileContents = fileContents.replaceAll(oldLine3, newLine3);
			      
			      String oldLine4 = "planDim=228";
			      String newLine4 = "planDim="+Constants.EPOS_PLAN_DIM;
			      fileContents = fileContents.replaceAll(oldLine4, newLine4);
			      
			      String oldLine5 = "weightsString = \"0.0,0.0\"";
			      String newLine5 = "weightsString = \"0.0,"+newLambda+"\"";
			      fileContents = fileContents.replaceAll(oldLine5, newLine5);
			      
			      if (Constants.onoff) {
			    	  
			    	  String oldLine6 = "globalCostFunction=VAR";
				      String newLine6 = "globalCostFunction="+Constants.gcFunc;//var
				      fileContents = fileContents.replaceAll(oldLine6, newLine6);
				      
			    	  String oldLine7 = "constraint=SOFT";
				      String newLine7 = "constraint=HARD_PLANS";
				      fileContents = fileContents.replaceAll(oldLine7, newLine7);
			      }
			      else if(Constants.target) {
			    	  
				      String oldLine8 = "globalCostFunction=VAR";
				      String newLine8 = "globalCostFunction=\"RMSE\"";
				      fileContents = fileContents.replaceAll(oldLine8, newLine8);
				  
				      //String oldLine9 = "goalSignalPath=default";
				      //String newLine9 = "goalSignalPath=\"datasets/incentive-signal.csv\"";
				      //fileContents = fileContents.replaceAll(oldLine9, newLine9);
					        
				      //String oldLine7 = "constraint=SOFT";
				      //String newLine7 = "constraint="+Constants.constraint;
				      //fileContents = fileContents.replaceAll(oldLine7, newLine7);
			      
			      }
			      else {
			    	  String oldLine6 = "globalCostFunction=VAR";
				      String newLine6 = "globalCostFunction="+Constants.gcFunc;
				      fileContents = fileContents.replaceAll(oldLine6, newLine6);
				      
				      //String oldLine7 = "constraint=SOFT";
				      //String newLine7 = "constraint="+Constants.constraint;
				      //fileContents = fileContents.replaceAll(oldLine7, newLine7);
			      
			      }
			      //constraints???
			      //globalCostFunction=RMSE
			    		  
			      FileWriter writer = new FileWriter(Constants.BetaConfBasePath+"epos.properties");//instantiates the FileWriter class
			      writer.append(fileContents);
			      
			      writer.flush();
			      writer.close();
				
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
		
	}
	
	public static void writeCommDelay() {
		// TODO Auto-generated method stub
		String COMMA_DELIMITER = ",";
		String file_path_CommDelaySC = Constants.NetOutFile + "CommDelaySC.csv";
		String file_path_CommUpDelaySC = Constants.NetOutFile + "CommUpDelaySC.csv";
		 try 
	        { 
			FileWriter file_Writer_CommDelaySC= new FileWriter (file_path_CommDelaySC, false);; 
			FileWriter file_Writer_CommUpDelaySC= new FileWriter (file_path_CommUpDelaySC, false);; 
	    	
			file_Writer_CommDelaySC.append("").append(COMMA_DELIMITER);
			for(int i = 0; i < Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS; i++)
				file_Writer_CommDelaySC.append("cloudlet-"+i).append(COMMA_DELIMITER);
			file_Writer_CommDelaySC.append(System.lineSeparator());
			
			for(int i = 0; i < Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS; i++)
				file_Writer_CommUpDelaySC.append("cloudlet-"+i).append(COMMA_DELIMITER);
			file_Writer_CommUpDelaySC.append(System.lineSeparator());
			
			
			for(int i = 0; i<Constants.dFFC.length; i++) {
				file_Writer_CommDelaySC.append("cloudlet-"+i).append(COMMA_DELIMITER);
				for(int j = 0; j<Constants.dFFC[0].length ; j++) {
					file_Writer_CommDelaySC.append(String.format(String.valueOf(Constants.dFFC[i][j]))).append(COMMA_DELIMITER);
				}
				file_Writer_CommDelaySC.append(System.lineSeparator());
			}
			
			for(int j = 0; j<Constants.upLinkLat.length ; j++) {
					file_Writer_CommUpDelaySC.append(String.format(String.valueOf(Constants.upLinkLat[j]))).append(COMMA_DELIMITER);
				}
			file_Writer_CommUpDelaySC.append(System.lineSeparator());
			
			
			file_Writer_CommDelaySC.flush();
			file_Writer_CommDelaySC.close();
			file_Writer_CommUpDelaySC.flush();
			file_Writer_CommUpDelaySC.close();
	        }
		 catch (Exception e) 
	        {
	        System.out.println("Error in CsvFileWriter !!!");
	        }
		
	
	}
	public static void writeUnitCosts() {
		System.out.println("Writing unit costs for experiment setup...");
		
		double [][] comCost = Constants.UNIT_COMM_COST;
		double [] storCost = Constants.UNIT_STOR_COST;
		double [] procCost = Constants.UNIT_PROC_COST;
		double [] memCost = Constants.UNIT_MEM_COST;
		
		String COMMA_DELIMITER = ",";
		        
		        try 
		        {    
		        	String file_path_CommCostSC = Constants.NetOutFile + "CommCostSC.csv";
		        	String file_path_StorCostSC = Constants.NetOutFile + "StorCostSC.csv";
		        	String file_path_ProcCostSC = Constants.NetOutFile + "ProcCostSC.csv";
		        	String file_path_MemCostSC = Constants.NetOutFile + "MemCostSC.csv";
		    		
		        	FileWriter file_Writer_CommCostSC= new FileWriter (file_path_CommCostSC, false); 
		        	FileWriter file_Writer_StorCostSC = new FileWriter (file_path_StorCostSC, false);
		        	FileWriter file_Writer_ProcCostSC = new FileWriter (file_path_ProcCostSC, false);
		        	FileWriter file_Writer_MemCostSC = new FileWriter (file_path_MemCostSC, false);
		        		
		        	file_Writer_CommCostSC.append("").append(COMMA_DELIMITER);
		        	
		        	
		    		for(int i = 0; i < Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS; i++) {
		    			file_Writer_CommCostSC.append("cloudlet-"+i).append(COMMA_DELIMITER);
		    			file_Writer_StorCostSC.append("cloudlet-"+i).append(COMMA_DELIMITER);
		    			file_Writer_ProcCostSC.append("cloudlet-"+i).append(COMMA_DELIMITER);
		    			file_Writer_MemCostSC.append("cloudlet-"+i).append(COMMA_DELIMITER);
		    		}
		    			file_Writer_CommCostSC.append(System.lineSeparator());
		    			file_Writer_StorCostSC.append(System.lineSeparator());
	    				file_Writer_ProcCostSC.append(System.lineSeparator());
	    				file_Writer_MemCostSC.append(System.lineSeparator());
			          
		    		
		    		//comm cost:
		    		for(int i = 0; i<Constants.UNIT_COMM_COST.length; i++) {
		    			file_Writer_CommCostSC.append("cloudlet-"+i).append(COMMA_DELIMITER);
		    			for(int j = 0; j<comCost[0].length ; j++) {
		    				file_Writer_CommCostSC.append(String.format(String.valueOf(comCost[i][j]))).append(COMMA_DELIMITER);
		    			}
		    			file_Writer_CommCostSC.append(System.lineSeparator());
		    		}
		    		
		    		//storage cost:
		    		for(int i = 0; i < Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS; i++)
			    		file_Writer_StorCostSC.append(String.format(String.valueOf(storCost[i]))).append(COMMA_DELIMITER);
			        	 
		    		//processing cost:
		    		for(int i = 0; i < Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS; i++)
		    			file_Writer_ProcCostSC.append(String.format(String.valueOf(procCost[i]))).append(COMMA_DELIMITER);
			        	
		    		//memory cost:
		    		for(int i = 0; i < Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS; i++)
		    			file_Writer_MemCostSC.append(String.format(String.valueOf(memCost[i]))).append(COMMA_DELIMITER);
			       
			        
		    		file_Writer_CommCostSC.flush();
			        file_Writer_CommCostSC.close();  
			        
			        file_Writer_StorCostSC.flush();
			        file_Writer_StorCostSC.close();
			        
			        file_Writer_ProcCostSC.flush();
			        file_Writer_ProcCostSC.close();
			        
			        file_Writer_MemCostSC.flush();
			        file_Writer_MemCostSC.close();
			        
		             
		        } 
		    	catch (Exception e) 
		        {
		        System.out.println("Error in CsvFileWriter !!!"+e);
		        }
			
		
	}
	//check how it writes the incentive.csv file
	/**Writes the goal signal for EPOS optimization towards renewable powered servers
	 * @param sC_RenewablePortion
	 */
	public static void writeRNVector(double[] sC_RenewablePortion) {

		String COMMA_DELIMITER = ",";
		String file_path_RNvector = Constants.PlanUtilDataset + "incentive.target";
		String file_path_RNvector1 = Constants.PlanUtilDataset + "incentive.csv";

		try 
	        { 
			FileWriter file_Writer_RNvector= new FileWriter (file_path_RNvector, false);; 
			FileWriter file_Writer_RNvector1= new FileWriter (file_path_RNvector1, false);; 
			
			for(int j = 0; j<sC_RenewablePortion.length ; j++) {
				file_Writer_RNvector.append(String.format(String.valueOf(sC_RenewablePortion[j]))).append(COMMA_DELIMITER);
				file_Writer_RNvector1.append(String.format(String.valueOf(sC_RenewablePortion[j]))).append(System.lineSeparator());
				
			}
			
			file_Writer_RNvector.flush();
			file_Writer_RNvector.close();
			file_Writer_RNvector1.flush();
			file_Writer_RNvector1.close();
			
			}
		 catch (Exception e) 
	        {
	        System.out.println("Error in CsvFileWriter !!!");
	        }
		
		
	}

	/**writes the cost components after the deployment of plans
	 * @param scheme
	 */
	public static void writeResults(ServiceDeploymentMethod scheme) {
		
		String file_path = Constants.methodOutput + scheme.methodType+"LocalCost-AfterDeployment.csv";
        FileWriter fileWriter = null;
        String COMMA_DELIMITER = ",";
        String NEW_LINE_SEPARATOR = "\n";
        try 
        {    
        	if (scheme.run == 0) {
        		fileWriter = new FileWriter(file_path, false);
                
	        	fileWriter.append("Run").append(COMMA_DELIMITER)
		    	.append("Beta").append(COMMA_DELIMITER)
		    	.append("Num. Plans").append(COMMA_DELIMITER)
		    	.append("Global-cost").append(COMMA_DELIMITER)
		    	.append("TotalLocal-cost").append(COMMA_DELIMITER)
		    	.append("dl-cost").append(COMMA_DELIMITER)
		    	.append("Pro-cost").append(COMMA_DELIMITER)
		    	.append("Sto-cost").append(COMMA_DELIMITER)
		    	.append("Mem-cost").append(COMMA_DELIMITER)
		    	.append("Dep-cost").append(COMMA_DELIMITER)
		    	.append("Com-cost").append(COMMA_DELIMITER)
		    	.append("Ene-cost").append(COMMA_DELIMITER)
		    	.append("Co2-cost").append(COMMA_DELIMITER)
		    	.append("CloudTasks-cost").append(COMMA_DELIMITER)
		    	.append("Ene-cost0").append(COMMA_DELIMITER)
		    	.append("Ene-cost1").append(COMMA_DELIMITER)
		    	.append("Ene-cost2").append(COMMA_DELIMITER)
		    	.append("Unass-cost").append(COMMA_DELIMITER)
		    	.append(System.lineSeparator());
            }
        	else {
	        		fileWriter = new FileWriter(file_path, true);//append to the file
	        	}
	            fileWriter.append(String.valueOf(scheme.run));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.valueOf(scheme.optBeta));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.valueOf(scheme.optNumPlan));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",scheme.GPlan.getGlobalCost()));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",scheme.GPlan.getTotalLocalCost()));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",scheme.GPlan.getDlViolCost()));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",scheme.GPlan.getProcCost()));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",scheme.GPlan.getStorCost()));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",scheme.GPlan.getMemCost()));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",scheme.GPlan.getDeplCost()));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",scheme.GPlan.getCommCost()));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",scheme.GPlan.getEnergyCost()));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",scheme.GPlan.getCo2EmitCost()));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.valueOf(scheme.GPlan.getAssToCloud()));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",scheme.GPlan.encomp[0]));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",scheme.GPlan.encomp[1]));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",scheme.GPlan.encomp[2]));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.valueOf(scheme.GPlan.getUnassTasks()));
	            fileWriter.append(NEW_LINE_SEPARATOR);
	            
	            //System.out.println("CSV file was created successfully !!!");
	           fileWriter.flush();
	           fileWriter.close();  
	        } catch (Exception e) 
	        {
	            System.out.println("Error in CsvFileWriter !!!");
	            e.printStackTrace();
	        } 
	        
	      writeUtil(scheme.GPlan, scheme.run, scheme.optBeta, scheme.optNumPlan, scheme.methodType);   
	}

	/**Writes the utilization of fog servers after deployment of plans
	 * @param gPlan
	 * @param run
	 * @param beta
	 * @param NumPlan
	 * The output of deployment of epos optimal solution on the network----> util < 1
	 * @param type 
	 */
	private static void writeUtil(Plan gPlan, int run, double beta, int NumPlan, int type) {
		
	    	boolean append_value = true;
	        String COMMA_DELIMITER = ",";
	        FileWriter file_Writer_CPUUtil;
			FileWriter file_Writer_MemUtil;
			int NumNodes = Constants.NUM_BACKBONE_ROUTERS + Constants.NUM_EDGE_ROUTERS;
			
	        try 
	        {    
	        	
	    		String file_path_pow_Util = Constants.methodOutput + type +"MemUtilRespoOpt.csv";
	    		String file_path_CPU_Util = Constants.methodOutput + type + "CPUUtilRespoOpt.csv";
	    		
	    		if (run == 0) {
	    			file_Writer_CPUUtil = new FileWriter (file_path_CPU_Util, false);
	        		file_Writer_MemUtil = new FileWriter (file_path_pow_Util, false);
	        		
		    		file_Writer_CPUUtil.append("run").append(COMMA_DELIMITER)
		    		.append("beta").append(COMMA_DELIMITER)
		    		.append("planNum").append(COMMA_DELIMITER);
		    		
		    		file_Writer_MemUtil.append("run").append(COMMA_DELIMITER)
		    		.append("beta").append(COMMA_DELIMITER)
		    		.append("planNum").append(COMMA_DELIMITER);
		    		
		    		for(int i = 0; i < globalLoadedServices.length; i++){
		    			file_Writer_CPUUtil.append("cloudlet-"+i).append(COMMA_DELIMITER);
		    			file_Writer_MemUtil.append("cloudlet-"+i).append(COMMA_DELIMITER);
			        }
		    		file_Writer_CPUUtil.append(System.lineSeparator());
		    		file_Writer_MemUtil.append(System.lineSeparator());
	    		}
	    		else{
	    			file_Writer_CPUUtil = new FileWriter (file_path_CPU_Util, append_value);
	        		file_Writer_MemUtil = new FileWriter (file_path_pow_Util, append_value);
	        	}
		         
	    		 
		        file_Writer_CPUUtil.append(String.format(String.valueOf(run))).append(COMMA_DELIMITER)
		        .append(String.format(String.valueOf(beta))).append(COMMA_DELIMITER)
		        .append(String.format(String.valueOf(NumPlan))).append(COMMA_DELIMITER);
		        
		        file_Writer_MemUtil.append(String.format(String.valueOf(run))).append(COMMA_DELIMITER)
		        .append(String.format(String.valueOf(beta))).append(COMMA_DELIMITER)
		        .append(String.format(String.valueOf(NumPlan))).append(COMMA_DELIMITER);
		        
		        
		        for(int i = 0; i <NumNodes ; i++){
		        	file_Writer_CPUUtil.append(String.format("%.5f",gPlan.utilPlan[i])).append(COMMA_DELIMITER);
		        	file_Writer_MemUtil.append(String.format("%.5f",gPlan.utilPlan[i+NumNodes])).append(COMMA_DELIMITER);
		        }


		        file_Writer_CPUUtil.append(System.lineSeparator()); 
		        file_Writer_MemUtil.append(System.lineSeparator()); 
		        
		        file_Writer_CPUUtil.flush();
		        file_Writer_CPUUtil.close(); 
		        file_Writer_MemUtil.flush();
		        file_Writer_MemUtil.close(); 
		        
	             
	        } 
	    	catch (Exception e) 
	        {
	        System.out.println("Error in CsvFileWriter !!!");
	        e.printStackTrace();
	        } 
	    
	}
	
	public static void dataPrep() {
    	
    	String CSV_SEPARATOR = ",";
    		String line;
    		String[] data;
    		
    		try {
    			
    		for (int type = 0; type < 2; type++) {
    	        for (int runNum =0; runNum<Constants.runNum; runNum++) {
    	        	String LocalReadCostpath0 = Constants.EPOSAnswerOverall + type+ "gclc.csv";
    	            LineNumberReader csvReader0 = new LineNumberReader(new FileReader(LocalReadCostpath0));
    				
    	        	String file_path = Constants.EPOSAnswerOverall + type + "-" + runNum + ".csv";
    	        	FileWriter fileWriter = new FileWriter(file_path, false);
    	        	
    	        	String LocalReadCostpath1 = Constants.EPOSAnswerOverall + type+ "Normalizedgclc.csv";
    	            LineNumberReader csvReader1 = new LineNumberReader(new FileReader(LocalReadCostpath1));
    				
    	        	String file_path1 = Constants.EPOSAnswerOverall + type + "-" + runNum + "Normalized.csv";
    	        	FileWriter fileWriter1 = new FileWriter(file_path1, false);
    	        	
    	            csvReader0.readLine();
    	            csvReader1.readLine();
    				
    				while ((line = csvReader0.readLine()) != null) {
    					data = line.split(CSV_SEPARATOR);
    					if(Integer.parseInt(data[0]) != runNum)				
    						 continue;
    					else {
    						//String COMMA_DELIMITER = ",";
    				        String NEW_LINE_SEPARATOR = "\n";
    				        fileWriter.write(line);
    			            fileWriter.append(NEW_LINE_SEPARATOR);
    					}
    		           
    		         }
    				 
    				while ((line = csvReader1.readLine()) != null) {
    					data = line.split(CSV_SEPARATOR);
    					if(Integer.parseInt(data[0]) != runNum)				
    						 continue;
    					else {
    						//String COMMA_DELIMITER = ",";
    				        String NEW_LINE_SEPARATOR = "\n";
    				        fileWriter1.write(line);
    			            fileWriter1.append(NEW_LINE_SEPARATOR);
    					}
    		           
    		         }
    				
    				fileWriter.flush();
    		        fileWriter.close(); 
    		        csvReader0.close();
    		        fileWriter1.flush();
    		        fileWriter1.close(); 
    		        csvReader1.close();
    		        
    	       }
    		}
    		}
    				catch (Exception e) 
    		         {
    		             System.out.println("Error in CsvFileWriter !!!");
    		             e.printStackTrace();
    		         } 
    			
    		convercsvtodat();
    		convertReqRes();
    		methodtodat();
    	}
	
	private static void methodtodat() {
		// TODO Auto-generated method stub
		String CSV_SEPARATOR = ",";
		String line;
		String str1, str0;
		String[] data;
		
		try {
			
			for (int type = 0; type <Constants.numPlacementMethod; type++) {
				
				String file_path_pow_Util = Constants.methodOutput + type +"MemUtilRespoOpt.csv";
				LineNumberReader csvReader0 = new LineNumberReader(new FileReader(file_path_pow_Util));
				
				String file_path_CPU_Util = Constants.methodOutput + type + "CPUUtilRespoOpt.csv";
	    		LineNumberReader csvReader1 = new LineNumberReader(new FileReader(file_path_CPU_Util));
					
	            String LocalWriteCostpath0 =  Constants.methodOutput + type +"MemUtilRespoOpt.dat";
	            String LocalWriteCostpath1 =  Constants.methodOutput + type +"CPUUtilRespoOpt.dat";
			        
	            PrintWriter out0 = new PrintWriter(new FileWriter(LocalWriteCostpath0));
	            PrintWriter out1 = new PrintWriter(new FileWriter(LocalWriteCostpath1));
					
		        	
	            csvReader0.readLine();
	            csvReader1.readLine();
	            
	            while ((line = csvReader0.readLine()) != null) {
					data = line.split(CSV_SEPARATOR);
					for(int i = 0; i<Constants.numNodes; i++) {
							 str0 = Integer.parseInt(data[0])+" ";
							 str0 += i + " "+Double.parseDouble(data[i+3]);
							 out0.println(str0);
					}
					out0.println();
					
					
				}
					
				
		    	
				while ((line = csvReader1.readLine()) != null) {
					data = line.split(CSV_SEPARATOR);
					 for(int i = 0; i<Constants.numNodes; i++) {
							 str1 = Integer.parseInt(data[0])+" ";
							 str1 += i + " "+Double.parseDouble(data[i+3]);
							 out1.println(str1);
							 
						 }
						 out1.println();
					}
				
					
					
				csvReader0.close();
				csvReader1.close();
				out0.flush();
	        	out0.close();
	        	out1.flush();
	        	out1.close();
			}
	        
		 }
        catch (Exception e) 
        {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        }
		
	}
	private static void convercsvtodat() {
		// TODO Auto-generated method stub
		String CSV_SEPARATOR = ",";
		String line;
		String str1, str0;
		String[] data;
		int run = 35;
		int nodes =Constants.numNodes;
		
		try {
			for (int type = 0; type < 2; type++) {
	        for (int runNum = 0; runNum<Constants.runNum; runNum++) {
	        	String LocalReadCostpath0 = Constants.ReqRes + type+ "CPULoadReq.csv";
	            LineNumberReader csvReader0 = new LineNumberReader(new FileReader(LocalReadCostpath0));
				
	        	String LocalReadCostpath = Constants.EPOSAnswerOverall + type + "CPULoadRes.csv";
	            LineNumberReader csvReader1 = new LineNumberReader(new FileReader(LocalReadCostpath));
				
	            String LocalWriteCostpath = Constants.EPOSAnswerOverall + type + "loadvsbeta" + runNum + ".dat";
		        PrintWriter out = new PrintWriter(new FileWriter(LocalWriteCostpath));
				csvReader1.readLine();
				csvReader0.readLine();
				
				while ((line = csvReader0.readLine()) != null) {
					data = line.split(CSV_SEPARATOR);
					if(Integer.parseInt(data[0]) != runNum)					
						 continue;
					else {
						
						 for(int i = 0; i<nodes; i++) {
							 str0 = -0.05+" ";
							 str0 += i + " "+Double.parseDouble(data[i+1]);
							 out.println(str0);
						 }
						 out.println();
					}
				}
					
				while ((line = csvReader1.readLine()) != null) {
					data = line.split(CSV_SEPARATOR);
					if(Integer.parseInt(data[0]) != runNum)					
						 continue;
					else {
						
						 for(int i = 0; i<nodes; i++) {
							 str1 = Double.parseDouble(data[1])+" ";
							 str1 += i + " "+Double.parseDouble(data[i+3]);
							 out.println(str1);
						 }
						 out.println();
					}
				}
					
					
				csvReader0.close();
				csvReader1.close();
				out.flush();
	        	out.close();
	        	}
			}
	        
		 }
	        	catch (Exception e) 
	        {
	            System.out.println("Error in CsvFileWriter !!!");
	            e.printStackTrace();
	        }
		
	}

	private static void convertReqRes() {

			//int run = 35;
			int nodes = Constants.numNodes;
			String CSV_SEPARATOR = ",";
			String line0, line1, line2;
			String str0, str2, str1;
			String[] data;
			String CPULoad, CPULoadd, MemLoad, MemLoadd, SrvLoad, SrvLoadd;
			
			try {
				for (int type = 0; type <Constants.numPlacementMethod; type++) {
				
					 CPULoad = Constants.ReqRes + type + "CPULoadReq.csv";
					 CPULoadd = Constants.ReqRes + type + "CPULoadReq.dat";
					
					 MemLoad = Constants.ReqRes + type + "MemLoadReq.csv";
					 MemLoadd = Constants.ReqRes + type + "MemLoadReq.dat";
					
					 SrvLoad = Constants.ReqRes + type + "SrvReq.csv";
					 SrvLoadd = Constants.ReqRes + type + "SrvReq.dat";
				
				
					LineNumberReader csvcpuReader = new LineNumberReader(new FileReader(CPULoad));
					LineNumberReader csvsrvReader = new LineNumberReader(new FileReader(SrvLoad));
					LineNumberReader csvmemReader = new LineNumberReader(new FileReader(MemLoad));
					//MemLoad
					
					PrintWriter cpuout = new PrintWriter(new FileWriter(CPULoadd));
					PrintWriter srvout = new PrintWriter(new FileWriter(SrvLoadd));
					PrintWriter memout = new PrintWriter(new FileWriter(MemLoadd));
					
					csvsrvReader.readLine();
					csvcpuReader.readLine();
					csvmemReader.readLine();
					
					for (int i = 0; i<Constants.runNum; i++) { 
						
						 if ((line0 = csvsrvReader.readLine()) != null) {
							data = line0.split(CSV_SEPARATOR);
							for (int j = 0; j<nodes; j++) {
							 	str0 = i+" "+j +" "+ Integer.parseInt(data[j]);
								srvout.println(str0);
								
							}
						}
						srvout.println();	
						
						 if ((line1 = csvcpuReader.readLine()) != null) {
							data = line1.split(CSV_SEPARATOR);
							for (int j = 0; j<nodes; j++) {
							 	str1 = i+" "+j +" "+ Double.parseDouble(data[j]);
								cpuout.println(str1);
							}
						}
						
						cpuout.println();	
						
						if ((line2 = csvmemReader.readLine()) != null) {
							data = line2.split(CSV_SEPARATOR);
							for (int j = 0; j<nodes; j++) {
							 	str2 = i+" "+j +" "+ Double.parseDouble(data[j]);
								memout.println(str2);
							}
						}
						
						memout.println();	
					}	
					 		
					cpuout.close();
					srvout.close();
					memout.close();
				}
			 }
			 catch (IOException e) {
			 	      System.out.print("Error: " + e);
			 	      System.exit(1);
			 }
			
			//for responses:
			try {
				for (int type = 0; type <Constants.numPlacementMethod; type++) {
					
					CPULoad = Constants.ReqRes + type + "CPULoadRespo.csv";
					CPULoadd = Constants.ReqRes + type + "CPULoadRespo.dat";
					
					SrvLoad = Constants.ReqRes + type + "SrvRespo.csv";
					SrvLoadd = Constants.ReqRes + type + "SrvRespo.dat";
					
					MemLoad = Constants.ReqRes + type + "MemLoadRespo.csv";
					MemLoadd = Constants.ReqRes + type + "MemLoadRespo.dat";
					
				
					LineNumberReader csvReader = new LineNumberReader(new FileReader(CPULoad));
					LineNumberReader csvReader1 = new LineNumberReader(new FileReader(SrvLoad));
					LineNumberReader csvReader2 = new LineNumberReader(new FileReader(MemLoad));
					
					PrintWriter out = new PrintWriter(new FileWriter(CPULoadd));
					PrintWriter out1 = new PrintWriter(new FileWriter(SrvLoadd));
					PrintWriter out2 = new PrintWriter(new FileWriter(MemLoadd));
					
					csvReader.readLine();
					csvReader1.readLine();
					csvReader2.readLine();
					
					//services
					for (int i = 0; i<Constants.runNum; i++) { 
						 if ((line0 = csvReader1.readLine()) != null) {
							data = line0.split(CSV_SEPARATOR);
							for (int j = 0; j<nodes; j++) {
							 	str0 = i+" "+j +" "+ Integer.parseInt(data[j+3]);
								out1.println(str0);
								
							}
						}
						out1.println();	
						
					}
					//cpuload:
					for (int i = 0; i<Constants.runNum; i++) { 
						 if ((line1 = csvReader.readLine()) != null) {
							data = line1.split(CSV_SEPARATOR);
							for (int j = 0; j<nodes; j++) {
							 	str1 = i+" "+j +" "+ Double.parseDouble(data[j+3]);
								out.println(str1);
							}
						}
						
						out.println();	
					}	
					 	
					//memload:
					for (int i = 0; i<Constants.runNum; i++) { 
						 if ((line2 = csvReader2.readLine()) != null) {
							data = line2.split(CSV_SEPARATOR);
							for (int j = 0; j<nodes; j++) {
							 	str2 = i+" "+j +" "+ Double.parseDouble(data[j+3]);
								out2.println(str2);
							}
						}
						
						out2.println();	
					}	
					
					out.close();
					out1.close();
					out2.close();
					
					csvReader.close();
					csvReader1.close();
					csvReader2.close();
				}
			}
				
			 catch (IOException e) {
			 	      System.out.print("Error: " + e);
			 	      System.exit(1);
			 }
			
		}

	
	/**
	 * Writes the hard constraints including CPU and Memory vectors for EPOS optimization 
	 * @param hardPlanFactor 
	 * @param hardPlan 
	 */
	public static void writeHardConstraints(boolean hc, double[] hardPlan, double[] hardPlanFactor) {
		
		if(!hc)
			return;
		
		String COMMA_DELIMITER = ",";
		String file_path_hcvector = Constants.PlanUtilDataset + "hard-constraints-plans.csv";

		try 
	        { 
			FileWriter file_Writer_hardConstVector= new FileWriter (file_path_hcvector, false);
			
			for(int i = 0; i < 2*Constants.numNodes; i++) {
				file_Writer_hardConstVector.append(hardPlan[i]+"").append(COMMA_DELIMITER);
			}
			
			file_Writer_hardConstVector.append(System.lineSeparator());
			
			for(int i = 0; i < 2*Constants.numNodes; i++)
				file_Writer_hardConstVector.append(hardPlanFactor[i]+"").append(COMMA_DELIMITER);
				
			file_Writer_hardConstVector.append(System.lineSeparator());
			
			
			file_Writer_hardConstVector.flush();
			file_Writer_hardConstVector.close();
			}
		 catch (Exception e) 
	        {
	        System.out.println("Error in CsvFileWriter !!!");
	        }
		
	}

	public static void writeOutputLoad(int type, List<Agent> agents, Plan[] plans, int runNum, int beta, int numberOfPlans) {
		int cloudletIndex;
    	boolean append_value = true;

        String COMMA_DELIMITER = ",";
        
        FileWriter file_Writer_Srvload;
		FileWriter file_Writer_CPUload, file_Writer_Memload;
		FileWriter file_Writer_CPUUtil, file_Writer_MemUtil;
		
		int nodes = Constants.numNodes;
		
        try 
        {    
        	
    		String file_path_srvloadrespo = Constants.ReqRes +type+"SrvRespo.csv";
    		String file_path_cpuloadrespo = Constants.ReqRes +type+"CPULoadRespo.csv";
    		String file_path_memloadrespo = Constants.ReqRes +type+"MemLoadRespo.csv";
    		String file_path_mem_utilrespo = Constants.ReqRes +type+"MemUtilRespo.csv";
    		String file_path_cpu_utilrespo = Constants.ReqRes +type+"CPUUtilRespo.csv";
    		
    		if (runNum == 0) {
    			file_Writer_Srvload = new FileWriter (file_path_srvloadrespo, false);
        		file_Writer_CPUload = new FileWriter (file_path_cpuloadrespo, false);
        		file_Writer_Memload = new FileWriter (file_path_memloadrespo, false);
        		file_Writer_CPUUtil = new FileWriter (file_path_cpu_utilrespo, false);
        		file_Writer_MemUtil = new FileWriter (file_path_mem_utilrespo, false);
        		
	    		file_Writer_Srvload.append("run").append(COMMA_DELIMITER)
	    		.append("beta").append(COMMA_DELIMITER)
	    		.append("planNum").append(COMMA_DELIMITER);
	    		
	    		file_Writer_CPUload.append("run").append(COMMA_DELIMITER)
	    		.append("beta").append(COMMA_DELIMITER)
	    		.append("planNum").append(COMMA_DELIMITER);
	    		
	    		file_Writer_Memload.append("run").append(COMMA_DELIMITER)
	    		.append("beta").append(COMMA_DELIMITER)
	    		.append("planNum").append(COMMA_DELIMITER);
	    		
	    		file_Writer_CPUUtil.append("run").append(COMMA_DELIMITER)
	    		.append("beta").append(COMMA_DELIMITER)
	    		.append("planNum").append(COMMA_DELIMITER);
	    		
	    		file_Writer_MemUtil.append("run").append(COMMA_DELIMITER)
	    		.append("beta").append(COMMA_DELIMITER)
	    		.append("planNum").append(COMMA_DELIMITER);


	    		for(int i = 0; i < globalLoadedServices.length; i++){
	    			file_Writer_Srvload.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_CPUload.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_Memload.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_CPUUtil.append("cloudlet-"+i).append(COMMA_DELIMITER);
	    			file_Writer_MemUtil.append("cloudlet-"+i).append(COMMA_DELIMITER);
		          }
	    		file_Writer_Srvload.append(System.lineSeparator());
	    		file_Writer_CPUload.append(System.lineSeparator());
	    		file_Writer_Memload.append(System.lineSeparator());
	    		file_Writer_CPUUtil.append(System.lineSeparator());
	    		file_Writer_MemUtil.append(System.lineSeparator());
    		}
    		else{
    			file_Writer_Srvload = new FileWriter (file_path_srvloadrespo, append_value);
        		file_Writer_CPUload = new FileWriter (file_path_cpuloadrespo, append_value);
        		file_Writer_Memload = new FileWriter (file_path_memloadrespo, append_value);
        		file_Writer_CPUUtil = new FileWriter (file_path_cpu_utilrespo, append_value);
        		file_Writer_MemUtil = new FileWriter (file_path_mem_utilrespo, append_value);
        	}
	         
    		for(int i = 0; i < Constants.EPOS_NUM_AGENT; i++){
    			if (plans[i].empty_node) 
    				continue;
    			for(int col = 0; col < plans[i].y.length; col++){
        			cloudletIndex = plans[i].y[col];							//selected host
        		 	globalLoadedServices[cloudletIndex] += 1;
        		 	globalCPULoad[cloudletIndex] += agents.get(i).serviceList.get(col).getCpuDemand1();
        		 	globalMemLoad[cloudletIndex] += agents.get(i).serviceList.get(col).getMemDemand();
        		 }
    			for(int col = 0; col<nodes; col++) {
    				globalCPULoadFromUtil[col] += plans[i].utilPlan[col];
    				globalMemLoadFromUtil[col] += plans[i].utilPlan[col+nodes];
    			}
	         }
	         
	        file_Writer_Srvload.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(beta))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numberOfPlans))).append(COMMA_DELIMITER);
	        
	        file_Writer_CPUload.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(beta))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numberOfPlans))).append(COMMA_DELIMITER);
	        
	        file_Writer_Memload.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(beta))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numberOfPlans))).append(COMMA_DELIMITER);
	        
	        file_Writer_CPUUtil.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(beta))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numberOfPlans))).append(COMMA_DELIMITER);
	        
	        file_Writer_MemUtil.append(String.format(String.valueOf(runNum))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(beta))).append(COMMA_DELIMITER)
	        .append(String.format(String.valueOf(numberOfPlans))).append(COMMA_DELIMITER);
	        
	       
	        for(int i = 0; i < globalLoadedServices.length; i++){
	        	file_Writer_Srvload.append(String.format(String.valueOf(globalLoadedServices[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	file_Writer_CPUload.append(String.format(String.valueOf(globalCPULoad[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	file_Writer_Memload.append(String.format(String.valueOf(globalMemLoad[i]))).append(COMMA_DELIMITER);//preventing wrong written of values as negative values
	        	file_Writer_CPUUtil.append(String.format(String.valueOf(globalCPULoadFromUtil[i]))).append(COMMA_DELIMITER);
	        	file_Writer_MemUtil.append(String.format(String.valueOf(globalMemLoadFromUtil[i]))).append(COMMA_DELIMITER);
	        }
	        file_Writer_Srvload.append(System.lineSeparator());        
	        file_Writer_CPUload.append(System.lineSeparator());    
	        file_Writer_Memload.append(System.lineSeparator());    
	        file_Writer_CPUUtil.append(System.lineSeparator()); 
	        file_Writer_MemUtil.append(System.lineSeparator()); 
	        
	        file_Writer_Srvload.flush();
	        file_Writer_Srvload.close();
	        file_Writer_CPUload.flush();
	        file_Writer_CPUload.close();
	        file_Writer_Memload.flush();
	        file_Writer_Memload.close();
	        file_Writer_CPUUtil.flush();
	        file_Writer_CPUUtil.close(); 
	        file_Writer_MemUtil.flush();
	        file_Writer_MemUtil.close(); 
	        
             
        } 
    	catch (Exception e) 
        {
        System.out.println("Error in CsvFileWriter !!!");
        e.printStackTrace();
        } 

	}

	
	
	public static void writePlansAndCosts(int type, Plan[] plans, int runNum) {
		
    	
    	boolean append_value = false;
    	//String file_path = Constants.PlansAndCosts + "SelectedPlans.csv";
        //FileWriter file_Writer = null;
        String COMMA_DELIMITER = ",";
        //String NEW_LINE_SEPARATOR = "\n";
        PrintWriter out;
        String file_path_EPOS ;
        try 
        {    
        	File theDir = new File(Constants.EPOSAnswer+"run"+runNum);
        	if (!theDir.exists()){
        	    theDir.mkdirs();
        	}
        	
        	for(int i = 0; i < Constants.EPOS_NUM_AGENT; i++){
        		if(Constants.win == true)
        			file_path_EPOS = Constants.EPOSAnswer+"run"+runNum+"\\"+type+"\\agent_"+i+".csv";
        		else
        			file_path_EPOS = Constants.EPOSAnswer+"run"+runNum+"/"+type+"/agent_"+i+".csv";
        		
	             FileWriter file_Writer = new FileWriter (file_path_EPOS, append_value);
	             //if (runNum == 0) {
		             file_Writer.append("plan_index").append(COMMA_DELIMITER)
		            .append("serviceSize").append(COMMA_DELIMITER)
		            .append("selected").append(COMMA_DELIMITER)
		         	.append("dlViolCost").append(COMMA_DELIMITER)
		         	.append("energyCost").append(COMMA_DELIMITER)
		         	.append("procCost").append(COMMA_DELIMITER)
		         	.append("storCost").append(COMMA_DELIMITER)
		         	.append("deplCost").append(COMMA_DELIMITER)
		         	.append("commCost").append(COMMA_DELIMITER)
		         	.append("cO2Cost").append(COMMA_DELIMITER)
		         	.append("memCost").append(COMMA_DELIMITER)
		         	.append("totalLocalCost")
		         	.append(System.lineSeparator());
	         //  }
		     	file_Writer.append(String.format(String.valueOf(plans[i].planIndex))).append(COMMA_DELIMITER)
            	.append(String.format(String.valueOf(plans[i].ServiceSize))).append(COMMA_DELIMITER)//preventing wrong written of values as negative values
            	.append(String.valueOf(plans[i].selected)).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i].getDlViolCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i].getEnergyCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i].getProcCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i].getStorCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i].getDeplCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i].getCommCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i].getCo2EmitCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i].getMemCost())).append(COMMA_DELIMITER)
 	         	.append(String.format("%.8f",plans[i].getTotalLocalCost()))
 	         	.append(System.lineSeparator());        
            	 
        	
            file_Writer.flush();
	        file_Writer.close();      
        	} 
        
        } 
        	catch (Exception e) 
        {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } 
        
        
        
        try {
        	String str;
        	String LocalCostpath = Constants.EPOSAnswer + "run-"+runNum+"-type"+type+".dat";
            out = new PrintWriter(new FileWriter(LocalCostpath));
			 
        	for(int i = 0; i < Constants.EPOS_NUM_AGENT; i++){
    			if (plans[i].empty_node) {
    				str = i+"";
    				for (int j = 0; j<Constants.APPLIEDCOST.length; j++) {
					 	str += " 0";
					}
    				out.println(str);
    					
    				
    			}
    			else {
    				str = i+"";
    				for (int j = 0; j<Constants.APPLIEDCOST.length; j++) {
    					str += " "+ Math.floor(plans[i].getCost(j)*100000000)/100000000;
    				}
    					out.println(str);
    				
	         }
        	}   
        	out.flush();
        	out.close(); 
        } 
        	catch (Exception e) 
        {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } 
        
		
	}

	public static void writeOverallGC(int type, List<RunCost> runs, int run, int ePOS_NUM_PLANS) {

		//boolean append_value = true;
    	String file_path = Constants.EPOSAnswerOverall+type+"Normalizedgclc.csv";
        FileWriter fileWriter = null;
        String COMMA_DELIMITER = ",";
        String NEW_LINE_SEPARATOR = "\n";
        try 
        {    
        	if (run == 0) {
        		fileWriter = new FileWriter(file_path, false);
                
	        	fileWriter.append("Run").append(COMMA_DELIMITER)
		    	.append("hopLevel").append(COMMA_DELIMITER)
		    	.append("Beta").append(COMMA_DELIMITER)
		    	.append("PlanNumber").append(COMMA_DELIMITER)
		    	.append("Global-cost").append(COMMA_DELIMITER)
		    	.append("Local-cost").append(COMMA_DELIMITER)
		    	.append(System.lineSeparator());
            }
        	else {
        		fileWriter = new FileWriter(file_path, true);//append to the file
        	}
        	
        	for (int b = 0; b<Constants.betaConfigSize ; b++) {
	            fileWriter.append(String.valueOf(run));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.valueOf(Constants.hopLevel));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.valueOf(Constants.BetaConfig[b]));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.valueOf(ePOS_NUM_PLANS));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",runs.get(b).globalCost));
	            fileWriter.append(COMMA_DELIMITER);
	            fileWriter.append(String.format("%.12f",runs.get(b).localCost));
	            fileWriter.append(NEW_LINE_SEPARATOR);
        }
            //System.out.println("CSV file was created successfully !!!");
           fileWriter.flush();
           fileWriter.close();  
        } catch (Exception e) 
        {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } 
        
    
	}
	
	/**Writes the costs associated by each placement method
	 * @param costs
	 */
	public static void WriteMethodsCosts(RunCost[][] costs) {

		normalizeCosts(costs);
		String file_path = Constants.methodOutput + "CostsAfterDeployment.csv";
        FileWriter fileWriter = null;
        String COMMA_DELIMITER = ",";
        String NEW_LINE_SEPARATOR = "\n";
        int run = costs[0][0].run;
        
        try 
        {    
        	if (run == 0) {
        		fileWriter = new FileWriter(file_path, false);
                
	        	fileWriter.append("Run").append(COMMA_DELIMITER);
	        	
		    	for (int i=0; i < Constants.numPlacementMethod; i++) {
		        	fileWriter.append("MethodType").append(COMMA_DELIMITER)
			    	.append("Global-cost").append(COMMA_DELIMITER)
			    	.append("Local-cost").append(COMMA_DELIMITER)
			    	.append("nGlobal-cost").append(COMMA_DELIMITER)
			    	.append("nLocal-cost").append(COMMA_DELIMITER);
		    	}
			    
		    	fileWriter.append(System.lineSeparator());
            }
        	else {
        		fileWriter = new FileWriter(file_path, true);
        	}
        	
        	for (int j = 0; j<costs.length; j++) {//35
        		fileWriter.append(String.valueOf(costs[j][0].run)).append(COMMA_DELIMITER);
        	
        		for (int i=0; i < Constants.numPlacementMethod; i++) {
	        		
			        fileWriter.append(String.valueOf(costs[j][i].methodType)).append(COMMA_DELIMITER);
			        fileWriter.append(String.format("%.10f",costs[j][i].globalCost)).append(COMMA_DELIMITER);
			        fileWriter.append(String.format("%.10f",costs[j][i].localCost)).append(COMMA_DELIMITER);
			        fileWriter.append(String.format("%.10f",costs[j][i].nGlobalCost)).append(COMMA_DELIMITER);
			        fileWriter.append(String.format("%.10f",costs[j][i].nLocalCost)).append(COMMA_DELIMITER);
			       }
        		 fileWriter.append(NEW_LINE_SEPARATOR);
 	        	
        	}
        	
           fileWriter.flush();
           fileWriter.close();  
        } catch (Exception e) 
        {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } 
		
	}
	
	public static void normalizeCosts(RunCost[][] array) {
        
    	for (int i = 0; i < array.length; i++) {//number of rows
    		findMinAndMax(array[i]);
    		
        	if (lmax != 0) {
        		for(int j = 0; j< array[i].length; j++)//number of methods
	        		array[i][j].nLocalCost = (array[i][j].localCost - lmin) / (lmax - lmin);
		        	
        	}
        	if (gmax == 0) {
        		continue;
        	}
        	else {
        		for(int j = 0; j< array[i].length; j++)
	        		array[i][j].nGlobalCost = (array[i][j].globalCost - gmin) / (gmax - gmin);
        	}     
        }    
            
        
    }

    /**
     * finds the minimum and maximum of costs (internal parameters for min and max are updated)
     * @param array 
     *
     */
    private static void findMinAndMax(RunCost[] array) {
    	
        lmin = Double.MAX_VALUE; 
        gmin = lmin;
        lmax = Double.MIN_VALUE; 
        gmax = lmax; 
        
        for (int i = 0; i <array.length; i++) {//number of methods 6
            if (array[i].localCost < lmin) {
                lmin = array[i].localCost;
            }
            if (array[i].localCost > lmax) {
                lmax = array[i].localCost;
            }
            if (array[i].globalCost < gmin) {
                gmin = array[i].globalCost;
            }
            if (array[i].globalCost > gmax) {
                gmax = array[i].globalCost;
            }
        }
        //System.out.print("\nCost normalization: lmin="+lmin+", lmax="+lmax);
        //System.out.println(", gmin="+gmin+", gmax="+gmax);
        
    }

	public static void print2File(List<AvgRuns> avgruns) {


		String file_path = Constants.methodOutput + "testedNodes.csv";
        FileWriter fileWriter = null;
        String COMMA_DELIMITER = ",";
        String NEW_LINE_SEPARATOR = "\n";
        int tests = avgruns.size();
        
        try 
        {    
        		fileWriter = new FileWriter(file_path, true);
                
	        	fileWriter.append("Node0").append(COMMA_DELIMITER)
			    	.append("Node1").append(COMMA_DELIMITER)
			    	.append("TotCpu").append(COMMA_DELIMITER)
			    	.append("TotMem").append(COMMA_DELIMITER)
			    	.append("TotStorage").append(COMMA_DELIMITER)
			    	.append("Validity").append(System.lineSeparator());
            
        	for (int j = 0; j<tests; j++) {
        		for (int k=0; k<avgruns.get(j).onNodes.length; k++) 
        			fileWriter.append(String.valueOf(""+avgruns.get(j).onNodes[k])).append(COMMA_DELIMITER);
        		//fileWriter.append(String.valueOf(""+avgruns.get(j).onNodes[1])).append(COMMA_DELIMITER);
        		fileWriter.append(String.valueOf(""+avgruns.get(j).totalCPUCap)).append(COMMA_DELIMITER);
        		fileWriter.append(String.valueOf(""+avgruns.get(j).totalMemCap)).append(COMMA_DELIMITER);
        		fileWriter.append(String.valueOf(""+avgruns.get(j).totalStoCap)).append(COMMA_DELIMITER);
        		fileWriter.append(String.valueOf(""+avgruns.get(j).isValid)).append(COMMA_DELIMITER);
        	    
        		fileWriter.append(NEW_LINE_SEPARATOR);
 	        	
        	}
        	
           fileWriter.flush();
           fileWriter.close();  
        } catch (Exception e) 
        {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } 
		
	}

    
}
