package EdgeEPOS.TrafficTraces;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import EdgeEPOS.Setting.Constants;

/**
 * @author rooyesh
 * This class reads the aggregated traffic of all services over the network for all IoT profiles
 */
public class AggregatedTraceReader {

    private static Scanner in;

    private static ArrayList<Double> trafficTrace; // array list that has the traffic trace
    public static double averageTrafficTrace;
    public static double deviation;
    
	private static double min, max; 

    private final static double SMOOTHING_NUMBER = 0.000000000001d; // used so that we will not have absolute 0 as traffic rate

    
    /**
     * Reads traffic from the file (192 values), the incoming traffic sample to one service on one node
     * @return returns the traffic as an arrayList (time-stamped) of array of
     * doubles (each element of array is traffic per fog node)
     * @throws FileNotFoundException if the file is not found
     */
    public static ArrayList<Double> readTrafficFromFile() throws FileNotFoundException {
    	
    	System.out.println("\n----------------------------------Workload distribution----------------------------------");
		
        trafficTrace = new ArrayList<>();
        if (Constants.WorkloadFile.equalsIgnoreCase("")) {
            in = new Scanner(System.in);
        } else {
            File inputFile = new File(Constants.WorkloadFile);
            in = new Scanner(inputFile);
        }

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
    	
    	double input, sum = 0, mean = 0;
        averageTrafficTrace = in.nextDouble();//first value is the average so ignore it
       
        
        while (in.hasNext()) {
            input = in.nextDouble();
            trafficTrace.add(input);
            
        }
        
        for(int i=0;i<trafficTrace.size();i++) 
    	{
    		sum += Math.pow((trafficTrace.get(i) - averageTrafficTrace),2);
    	
    	}
    	mean = sum/(trafficTrace.size()-1);
    	deviation = Math.sqrt(mean);
    	
        //normalizeTraceTraffic();
        return trafficTrace;
    }

    /**
     * Normalizes traffic values for all time-stamps, such that the each traffic
     * element (per fog node) is not going to be large for the fog queues
     */
    private static void normalizeTraceTraffic() {
        findMinAndMax(trafficTrace);
        for (int i = 0; i < trafficTrace.size(); i++) {
        	trafficTrace.set(i, (trafficTrace.get(i) - min + SMOOTHING_NUMBER) / (max - min) );
        	//System.out.println("traffic after norm: "+i+" "+trafficTrace.get(i));
        	//System.out.println(trafficTrace.get(i)*10);
        	
        }

        averageTrafficTrace = (averageTrafficTrace - min) / (max - min) ;
    
        
    }

    /**
     * Given a trace file, finds the minimum and maximum of traffic. (internal
     * parameters for min and max are updated)
     *
     * @param trace the traffic trace file
     */
    private static void findMinAndMax(ArrayList<Double> trace) {
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        
        for (Double traffic : trace) {
            if (traffic < min) {
                min = traffic;
            }
            if (traffic > max) {
                max = traffic;
            }
        }
        System.out.println("Traffic normalization: min="+min+", max="+max);
    }
    
    
    public static double getAverageTrafficTrace() {
		return averageTrafficTrace;
	}

	public static double getDeviation() {
		return deviation;
	}
}
