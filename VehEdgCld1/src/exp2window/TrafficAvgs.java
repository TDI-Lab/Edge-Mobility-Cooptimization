package exp2window;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import EdgeEPOS.Setting.Constants;
import EdgeEPOS.TrafficTraces.AggregatedTraceReader;


public class TrafficAvgs {

	private static int index = 1;				
	private static ArrayList<Double> trafficTrace; // array list that has the traffic trace
	private static ArrayList<Window> trafficTraceMean; // array list that has the traffic trace
    
	public static double averageTrafficTrace;
    private static Scanner in;

    public static String inFile = "C:\\Users\\znb_n\\eclipse-workspace\\Test4-profiles\\traffic-pattern1.txt";
    public static String meanFileSorted = "C:\\Users\\znb_n\\eclipse-workspace\\Test4-profiles\\MeanTraffic-sorted.csv";
    public static String meanFileNoSorted = "C:\\Users\\znb_n\\eclipse-workspace\\Test4-profiles\\MeanTraffic-notsorted.csv";
	
	public static void main(String[] args) throws FileNotFoundException {
		
			Double avgTrafficPerWindow;
			trafficTrace = new ArrayList<>();
			trafficTraceMean = new ArrayList<Window>();
			
			ArrayList<Double> PROFILES = readTrafficFromFile(); 
	        
			
	        for (int window = 0 ; window < PROFILES.size()-11 ; window++) {//[0:192-12]
	        	  
	        	avgTrafficPerWindow = mean(window);
	        	trafficTraceMean.add(new Window(window, avgTrafficPerWindow, PROFILES.get(window)));
	        	System.out.println("window "+window+", mean: "+avgTrafficPerWindow);
	              
            }
	        
	        print(meanFileNoSorted);
	        
	        Collections.sort(trafficTraceMean, Comparator.comparingDouble(o -> o.getAvgTraffic()));
	        print(meanFileSorted);
	        
        	
     
      }
		
	
	    public static ArrayList<Double> readTrafficFromFile() throws FileNotFoundException {
	    	
	    	trafficTrace = new ArrayList<>();
	        File inputFile = new File(inFile);
	        in = new Scanner(inputFile);
	        return extractTrafficTrace(in);
	    }

	    /**
	     * 
	     *
	     * @param in the input scanner
	     * @return returns the traffic: an arrayList (time-stamped) of array of
	     * doubles (each element of array is traffic per fog node)
	     */
	    private static ArrayList<Double> extractTrafficTrace(Scanner in) {
	    	
	    	double input;
	        averageTrafficTrace = in.nextDouble();//first value is the average so ignore it
	        
	        while (in.hasNext()) {
	            input = in.nextDouble();
	            trafficTrace.add(input);
	        }
	        return trafficTrace;
	    }
	    
	    
	    
	    private static double mean(int j) {
	    	double sum = 0;
	    	
	    	for(int i=j; i<j+12; i++) 
		    	sum += trafficTrace.get(i);
		    	
		    return sum/12;
		    	
	    }
	    
	    private static void print(String file) {
	    	String CSV_SEPARATOR = ",";
	    	try {
		    	//@SuppressWarnings("resource")
				BufferedWriter csvWriter = new BufferedWriter(new FileWriter(file, true));
		    	
		    	for (int i = 0; i<trafficTraceMean.size() ; i++) {
					csvWriter.append(trafficTraceMean.get(i).getWinId()+"").append(CSV_SEPARATOR)
					.append(trafficTraceMean.get(i).getAvgTraffic()+"").append(CSV_SEPARATOR)
					.append(trafficTraceMean.get(i).getFirstPro()+"").append(System.lineSeparator());
		    	}
		    	//csvWriter.append(System.lineSeparator());
				
	    	
	    	csvWriter.close();
	    	}
			catch (Exception e) {
		        System.out.println("Error in FileWriter !!!");
		        e.printStackTrace();
		    }
	    	
	    }

}
