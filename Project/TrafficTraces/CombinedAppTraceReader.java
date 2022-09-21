package EdgeEPOS.TrafficTraces;

//import Scheme.Parameters;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import EdgeEPOS.Setting.Constants;

/**
 *
 * @author rooyesh
 */
public class CombinedAppTraceReader extends GenericCombinedAppTraceReader{
//trace-240m-PerNode1MinutePeriod.txt
	private static final String FILE_ADDRESS = "G:\\MobFogSim-master\\MobFogSim-master\\src\\EdgeEPOS\\TrafficTraces\\trace-combined-apps.txt"; 
    //trace-combined-apps.txt 
    //This files includes the combined app trace information for 4 hours, 2017/04/12-13 (12pm-4pm of day 1)
    
    private static Scanner in;
    
    /**
     * Reads traffic from the file (addressed in FILE_ADDRESS)
     *
     * @return returns the traffic an an arrayList (timestamped) of array of
     * doubles (each element of array is traffic per fog node)
     * @throws FileNotFoundException if the file is not found
     */
    public static ArrayList<Double[]> readTrafficFromFile() throws FileNotFoundException {

        trafficTrace = new ArrayList<>();
        if (FILE_ADDRESS.equalsIgnoreCase("")) {
            in = new Scanner(System.in); // read from console
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
     * @return returns the traffic an an arrayList (timestamped) of array of
     * doubles (each element of array is traffic per fog node)
     */
    private static ArrayList<Double[]> extractTrafficTrace(Scanner in) {
        double input;
        while (in.hasNext()) {
            in.nextInt(); // ignore the first number, which is the second in (1,15)
            Double[] trafficForCombinedServicesPerFogNode = new Double[Constants.numFogNodes];
            for (int j = 0; j < Constants.numFogNodes; j++) {
                in.nextInt(); // ignore the first numbers, which are the indices of fog node
                // Exception WILL BE THROWN, if the number of fog nodes is more than 10
                trafficForCombinedServicesPerFogNode[j] = in.nextDouble();
               
            }
            trafficTrace.add(trafficForCombinedServicesPerFogNode);
        }
        normalizeTraceTraffic(); // normalzie the traffic before finishing
        return trafficTrace;
    }

}
