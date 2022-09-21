package EdgeEPOS.TrafficTraces;

//import Scheme.Parameters;
import java.util.ArrayList;

import EdgeEPOS.Setting.Constants;

/**
 *
 * @author rooyesh
 *
 * This class contains the core functions for traffic reading
 */
public abstract class GenericCombinedAppTraceReader {

    private static Double min, max; // internally used

    public static Double[] averageTrafficPerFogNode;
    private static double[] CumulativeAverageTrafficPerFogNode;
    
    private final static double SMOOTHING_NUMBER = 0.000000000001d; // used so that we will not have absolute 0 as traffic rate

    protected static ArrayList<Double[]> trafficTrace; // it is a list of arrays. Each array elements represents the amount of data for each app

    /**
     * Given a trace file, finds the minimum and maximum of traffic. (internal
     * parameters for min and max are updated)
     *
     * @param trace the traffic trace file
     */
    private static void findMinAndMax(ArrayList<Double[]> trace) {
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
        for (Double[] combinedAppPerFogNode : trace) {
            for (int j = 0; j < Constants.NumFogNodes; j++) {
                if (combinedAppPerFogNode[j] < min) {
                    min = combinedAppPerFogNode[j];
                }
                if (combinedAppPerFogNode[j] > max) {
                    max = combinedAppPerFogNode[j];
                }
            }
        }
    }

    /**
     * Normalizes traffic values for a give timestamp, such that the each
     * traffic element (per fog node) is not going to be large for the fog
     * queues
     *
     * @param combinedAppPerFogNode
     */
    private static void normalizeTraffic(Double[] combinedAppPerFogNode) {
        for (int j = 0; j < Constants.NumFogNodes; j++) {
            combinedAppPerFogNode[j] = ((combinedAppPerFogNode[j] - min + SMOOTHING_NUMBER) / (max - min)) * Constants.TRAFFIC_NORM_FACTOR * Constants.numServices;
            CumulativeAverageTrafficPerFogNode[j] += combinedAppPerFogNode[j];
        }
    }

    /**
     * Normalizes traffic values for all timestamps, such that the each traffic
     * element (per fog node) is not going to be large for the fog queues
     */
    protected static void normalizeTraceTraffic() {
        initializeAverageParameters();
        // we are going to divide the traffic, such that the each traffic element (per fog node)
        // is not going to be large for the fog queues
        findMinAndMax(trafficTrace);
        for (Double[] combinedAppPerFogNode : trafficTrace) {
            normalizeTraffic(combinedAppPerFogNode);
        }
        int times = trafficTrace.size();
        for (int j = 0; j < Constants.NumFogNodes; j++) {
            averageTrafficPerFogNode[j] = CumulativeAverageTrafficPerFogNode[j] / times;
            System.out.println("traffic j "+j+" "+averageTrafficPerFogNode[j]+" comulative "+CumulativeAverageTrafficPerFogNode[j]);
            
        }
    }

    /**
     * Initializes the parameters related to average traffic
     */
    private static void initializeAverageParameters() {
        averageTrafficPerFogNode = new Double[Constants.NumFogNodes];
        CumulativeAverageTrafficPerFogNode = new double[Constants.NumFogNodes];
        for (int j = 0; j < Constants.NumFogNodes; j++) {
            CumulativeAverageTrafficPerFogNode[j] = 0d;
        }
    }

}
