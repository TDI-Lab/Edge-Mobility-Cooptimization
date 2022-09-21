package EdgeEPOS;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import EdgeEPOS.TrafficTraces.*;
import EdgeEPOS.Setting.Constants;

/**
 * @author rooyesh
 *
 */
public class Run2 {

    private static int TOTAL_RUN;

    private static int index = 0;

    private final static int TAU = 120; // time interval between run of the method (s)
    private final static int TRAFFIC_CHANGE_INTERVAL = 60; // time interval between run of the method (s)

    public static void main(String[] args) throws FileNotFoundException {
        // in each experiment, these parameters may vary

        Constants.numSmartThings=2;
        Constants.numCloudServers = 1;
    	Constants.numFogNodes = 10;
    	Constants.numServices = 2;
        Traffic.TRAFFIC_ENLARGE_FACTOR = 1;

        Constants.initialize();
        ArrayList<Double[]> traceList = CombinedAppTraceReader.readTrafficFromFile();

        TOTAL_RUN = traceList.size();
        
        System.out.println("traffic list size: "+ TOTAL_RUN);
        //System.exit(0);
        
        Constants.TAU = TAU;
        Constants.TRAFFIC_CHANGE_INTERVAL = TRAFFIC_CHANGE_INTERVAL;

        int q = TAU / TRAFFIC_CHANGE_INTERVAL; // the number of times that traffic changes between each run of the method

               Double[] combinedTrafficPerFogNode;

        //System.out.println("Traffic\tD(AC)\tD(AF)\tD(FS)\tD(MC)\tD(MV)\tD(OP)\tC(AC)\tC(AF)\tC(FS)\tC(MC)\tC(MV)\tC(OP)\tCNT(AC)\tCNT(AF)\tCNT(FS)\tCNT(MC)\tCNT(MV)\tCNT(OP)\tCCNT(AC)\tCCNT(AF)\tCCNT(FS)\tCCNT(MC)\tCCNT(MV)\tCCNT(OP)\tV(AC)\tV(AF)\tV(FS)\tV(MC)\tV(MV)\tV(OP)\tVS=" + violationSlack);
        for (int i = 0; i < TOTAL_RUN; i++) {
        	
        	combinedTrafficPerFogNode = nextRate(traceList); // gets the next rate
        	System.out.println((totalTraffic(combinedTrafficPerFogNode) * Constants.numServices));
        	        }
        
    }

    /**
     * Gets the next traffic rate from the trace
     *
     * @param traceList the trace
     * @return returns the next traffic rate from the trace
     */
    private static Double[] nextRate(ArrayList<Double[]> traceList) {
        return traceList.get(index++);
    }

    /**
     * Calculates the total rate of traffic from an array of traffic rates
     *
     * @param traffic the array of traffic rates
     */
    private static double totalTraffic(Double[] traffic) {
        double sum = 0;
        for (int j = 0; j < traffic.length; j++) {
            sum += traffic[j];
        }
        return sum;
    }

}
