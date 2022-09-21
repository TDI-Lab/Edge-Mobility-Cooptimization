package EdgeEPOS.TrafficTraces;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import EdgeEPOS.Setting.Constants;

/**
 * @author rooyesh
 * This class reads the aggregated traffic of all services over the network for all profiles
 */
public class AggregatedTraceReader {

    public static final String FILE_ADDRESS = Constants.base_Path.concat("\\src\\EdgeEPOS\\TrafficTraces\\traffic-pattern.txt"); 
    // the file traffic pattern must be in the same directory that is used to run the Java jar file from the command line
    
    
    private static Scanner in;

    private static ArrayList<Double> trafficTrace; // array list that has the traffic trace
    public static double averageTrafficTrace;

    private static double min, max; 

    private final static double SMOOTHING_NUMBER = 0.000000000001d; // used so that we will not have absolute 0 as traffic rate

    
    /**
     * Reads traffic from the file (addressed in FILE_ADDRESS)
     *
     * @return returns the traffic as an arrayList (time-stamped) of array of
     * doubles (each element of array is traffic per fog node)
     * @throws FileNotFoundException if the file is not found
     */
    public static ArrayList<Double> readTrafficFromFile() throws FileNotFoundException {

        trafficTrace = new ArrayList<>();
        if (FILE_ADDRESS.equalsIgnoreCase("")) {
            in = new Scanner(System.in);
        } else {
            File inputFile = new File(FILE_ADDRESS);
            in = new Scanner(inputFile);
        }

        return extractTrafficTrace(in);
    }

    /**
     * extract the traffic trace from scanner
     *
     * @param in the input scanner
     * @return returns the traffic: an arrayList (time-stamped) of array of
     * doubles (each element of array is traffic per fog node)
     */
    private static ArrayList<Double> extractTrafficTrace(Scanner in) {
    	
    	double input;
        averageTrafficTrace = in.nextDouble();
        
        while (in.hasNext()) {
            input = in.nextDouble();
            trafficTrace.add(input);
           
        }
        normalizeTraceTraffic();
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

    }
}
