package exp1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;


/**
 * @author znb_n
 * local cost sort over different runs for all agents
 */
public class AgentCostAnalyzer {

    public static void main(String[] args) {
    	
    	for (int l = 0; l<6; l++){
    		
    	
	        String outputFile = "C:\\Users\\znb_n\\eclipse-workspace\\Test3-target-p20\\EPOSAnswer\\overall\\cost\\SortedCost-type"+l+".dat";
	
	        // Initialize the agentCosts map to accumulate costs from multiple files
	        Map<Integer, Double> agentCosts = new HashMap<>();
	
	        for (int i = 0; i <= 35; i++) {
	            String inputFile = "C:\\Users\\znb_n\\eclipse-workspace\\Test3-target-p20\\EPOSAnswer\\overall\\cost\\run-"+i+"-type"+l+".dat";     
	            Map<Integer, Double> costsFromFile = readAgentCosts(inputFile);
	
	            // Update agentCosts with the costs from the current file
	            costsFromFile.forEach((agentIndex, cost) ->
	                    agentCosts.merge(agentIndex, cost, Double::sum));
	
	           // System.out.println("Step " + (i + 1) + ": Accumulated costs from " + inputFile);
	           // System.out.println("Intermediate agentCosts: " + agentCosts);
	        }
	
	        // Calculate average costs
	        Map<Integer, Double> averageCosts = calculateAverageCosts(agentCosts);
	
	        //System.out.println("Average costs calculated");
	        //System.out.println("Intermediate averageCosts: " + averageCosts);
	
	        // Write the results to the output file sorted by cost
	        try {
	            writeSortedAverages(outputFile, averageCosts);
	            System.out.println("Results written to " + outputFile);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
    	}
    }

    private static Map<Integer, Double> readAgentCosts(String inputFile) {
        Map<Integer, Double> agentCosts = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length >= 2) {
                    int agentIndex = Integer.parseInt(parts[0]);
                    double cost = Double.parseDouble(parts[10]);
                    //System.out.println("hiiii"+agentIndex+" "+cost+parts);
                    agentCosts.merge(agentIndex, cost, Double::sum);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return agentCosts;
    }

    private static Map<Integer, Double> calculateAverageCosts(Map<Integer, Double> agentCosts) {
        Map<Integer, Double> averageCosts = new HashMap<>();

        for (Map.Entry<Integer, Double> entry : agentCosts.entrySet()) {
            int agentIndex = entry.getKey();
            double totalCost = entry.getValue();
            double averageCost = totalCost / 36;  // Since there are 36 files
            averageCosts.put(agentIndex, averageCost);
        }

        return averageCosts;
    }

    private static void writeSortedAverages(String outputFile, Map<Integer, Double> averageCosts) throws IOException {
        // Sort the map by values (average costs)
        TreeMap<Integer, Double> sortedAverages = new TreeMap<>((a, b) -> {
            int compare = Double.compare(averageCosts.get(b), averageCosts.get(a));
            return compare != 0 ? compare : a.compareTo(b);
        });

        sortedAverages.putAll(averageCosts);

        // Write sorted averages to the output file
        try (FileWriter writer = new FileWriter(outputFile)) {
            for (Map.Entry<Integer, Double> entry : sortedAverages.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
        }
    }
}
