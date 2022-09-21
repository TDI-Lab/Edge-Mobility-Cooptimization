package EdgeEPOS.Utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import EdgeEPOS.PlacementMethods.EPOSAnswer;
import EdgeEPOS.PlacementMethods.Plan;
import EdgeEPOS.PlacementMethods.PlanWrapper;
import EdgeEPOS.Setting.Constants;
/**
 * @author rooyesh
 *
 */
public class Utility {
	
	static boolean append_value = false;
    static String planDatasetPath = Constants.base_Path; 
    static String line = "";
    static String cvsSplitBy = ",";
    static int agentNumber;
    static int iteration;
    static int run;
	public static void initialize() {
		
	}
	public static void writePlans(Plan[] agentPlans, int agIndex){
	    	
	    desSort(agentPlans);
        writeUtilizationToFile(agentPlans, agIndex, Constants.lambda);
        writeBinaryToFile(agentPlans, agIndex, Constants.lambda);
        
	    }
 
	public static void writeUtilizationToFile(Plan[] plans, int id, double lambda) {
        try { 
                String file_path_EPOS =planDatasetPath+"/datasets/Utilization/agent_"+id+".plans";
                FileWriter writer_EPOS = new FileWriter (file_path_EPOS, append_value);
                
                for(int col =0; col < Constants.EPOS_NUM_PLANS; col++){
                    writer_EPOS.append(String.valueOf(plans[col].cost));
                    writer_EPOS.append(":");
                    int size = plans[col].utilPlan.length;
                    for (int i =0; i<size ; i++){//both of CPU and Mem will be printed
                         writer_EPOS.write(String.format("%.4f", plans[col].utilPlan[i]));//preventing wrong written of values as negative values
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
	private static void writeBinaryToFile(Plan[] plans, int id, double lambdaValue) {
		// TODO Auto-generated method stub
		
		 try { 
             String file_path_EPOS =planDatasetPath+"/datasets/Binary/agent_"+id+".plans";
             FileWriter writer_EPOS = new FileWriter (file_path_EPOS, append_value);
            
				/*
				 * writer_EPOS.append("- "); for(int snum = 0; snum < plans[0].y.length;
				 * snum++){ writer_EPOS.append(String.valueOf(snum)+" "); }
				 */
           
             for(int col = 0; col < Constants.EPOS_NUM_PLANS; col++){
            	 if(plans[col].empty_node == true) {
            		 writer_EPOS.write(String.format("node without any received request"));
            		 break;
            	 }
            	 	int size = plans[col].y.length;
            	 	for (int i = 0; i<size ; i++){//both of CPU and Mem will be printed
	                     writer_EPOS.write(String.format(String.valueOf(plans[col].y[i])));//preventing wrong written of values as negative values
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
	 /*
	  * get the index of selected plans from I-EPOS:
      * In addition to the selected plans, the global-cost (utilization variance) of the selected plans and corresponding local-cost 
      *is stored for further comparison with EPOS Fog results: the realized variance and the predicted one
      *
      */
    
	 //get the index of selected plans from I-EPOS:
    public static EPOSAnswer getInputFromEpos(ArrayList<Integer> planIndex, int periodnum){
    
        System.out.println("reading input from EPOS......\n");
        //ArrayList<Integer> selectedPlans = new ArrayList<Integer>();// output of I-EPOS; index of selected plans
        File dir = new File("output");
        File[] files = dir.listFiles();
        //to simply check the last output folder of I-EPOS which contains the output of last recent run:
        File lastModified = getFileLastModified(files);
        System.out.println(lastModified);
        /*
        * In addition to the selected plans, the global-cost (utilization variance) of the selected plans and corresponding local-cost 
        *is stored for further comparison with EPOS Fog results: 
        *the realized variance and the predicted one
        **/
        String global_cost_File =lastModified+"/global-cost.csv";
        String local_cost_File =lastModified+"/local-cost.csv";
        String selected_plans_File =lastModified+"/selected-plans.csv";
        String line = "";
        String cvsSplitBy = ",";
        int i = 1;
        int minRun=0;
        int index=0;
        double loc_costs=0.0;
        double[] costs= new double[run];
        //extract the iteration and run number with the minimum global-cost
        try (BufferedReader br = new BufferedReader(new FileReader(global_cost_File))) 
        {
            // br.readHeaders();
            for (i = 0; i < iteration; i++)
                br.readLine();
            line = br.readLine();
            String[] input = line.split(cvsSplitBy);
            for (i = 0; i < run; i++)
                costs[i] = Double.parseDouble(input[3+i]);

            minRun = findMinRun(costs, run);
            index = iteration*minRun + iteration;
            //System.out.println(" minRun0: "+minRun);
                
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }  
        //read the corresponding local-cost with the glocal-cost
        try (BufferedReader br = new BufferedReader(new FileReader(local_cost_File))) 
        {
            //System.out.println(" minRun1: "+minRun);
            for (i = 0; i < iteration; i++)
                br.readLine();
            line = br.readLine();
            String[] input = line.split(cvsSplitBy);
            loc_costs = Double.parseDouble(input[3+minRun]);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        //find the selected plan index for each agent/node:
        try (BufferedReader br = new BufferedReader(new FileReader(selected_plans_File))) 
        {
            // br.readHeaders();
            for (i = 0; i < index; i++)
                br.readLine();
            line = br.readLine();//index+1
            String[] input = line.split(cvsSplitBy);
            for (i = 0; i < agentNumber; i++){
            	planIndex.add(Integer.parseInt(input[2+i]));
            }

        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    
    writeGC(costs[minRun],loc_costs, periodnum); 
    EPOSAnswer EPOSa = new EPOSAnswer(costs[minRun],loc_costs, periodnum);
    return EPOSa;
}


    
    public static int findMinRun(double[] globalCosts, int index){
        int mini = 0;
        double min = globalCosts[0];
        for (int j = 0; j<index; j++)
            if (globalCosts[j] < min){
                min = globalCosts[j];
                mini = j;
            }
        return mini;
    }
    
    public static void writeGC(double gc, double lc, int pw){
        boolean append_value = true;
        String file_path = "output/EPOSAnswer/gc.csv";
        FileWriter fileWriter = null;
        String COMMA_DELIMITER = ",";
        String NEW_LINE_SEPARATOR = "\n";
        try 
        {    
        	fileWriter = new FileWriter(file_path, append_value);
            
            fileWriter.append(String.valueOf(Constants.hopLevel));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(pw));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(Constants.lambda));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(gc));
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(lc));
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            System.out.println("CSV file was created successfully !!!");
             
        } catch (Exception e) 
        {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } 
        finally 
        {
            try {
                fileWriter.flush();
                fileWriter.close();
            }
            catch (IOException e) 
            {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
             
        }
         
    }
    
private static void desSort(Plan [] genPlans){
	    
	    Plan temp = new Plan();
	        for (int j=0 ; j < (Constants.EPOS_NUM_PLANS-1) ; j++){
	            for (int k=j+1 ; k < Constants.EPOS_NUM_PLANS ; k++){
	                PlanWrapper cw1 = new PlanWrapper(genPlans[j]);
	                PlanWrapper cw2 = new PlanWrapper(genPlans[k]); 
	                if (genPlans[j].cost < genPlans[k].cost){
	                    temp = cw1.p;
	                    genPlans[j] = cw2.p;
	                    genPlans[k] = temp;
	                }    
	            }
	        }
	        
	    
	}

	
	
    }
