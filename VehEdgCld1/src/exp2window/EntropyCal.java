package exp2window;


import java.awt.Color;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.math3.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import javax.swing.JFrame;


public class EntropyCal {

    public static void main(String[] args) {
        double[] data = { 5.039096262, 4.8912386786, 4.173855559, 3.4976979469, 4.3375549945, 4.386140695, 4.0947223387, 4.5782624086, 6.1268663715, 
        		4.9774950515, 2.7303032343, 2.2693679156, 4.850901915, 2.6454458527, 2.6996902604, 2.4919829756, 3.783309295, 1.7241052042, 1.5844946697,
        		1.4063664793, 3.8085411474, 1.7980434745, 1.5275765462, 2.2097687085, 2.9984981879, 2.5567259621, 2.6520608877, 2.7205427054, 2.8853286727, 
        		2.8920070578, 3.0519323064, 3.4889821753, 4.2395686824, 4.3442223547, 4.7847664747, 5.7166203396, 6.8523618889, 6.7555131046, 6.4003575377,
        		6.7809242608, 6.9609141038, 6.6758672946, 7.6500733278, 8.691529691, 8.160122569, 8.2609657149, 7.7149014219, 7.2479358083, 8.6870296206, 
        		7.166387858, 9.673238007, 9.9129681777, 10.178919142, 9.359239942, 8.966853109, 9.3940423825, 9.950826383, 9.3027379933, 9.029996484, 
        		9.8027194105, 10.1498789357, 9.9798077829, 9.9563908103, 9.3222315543, 9.2467876495, 8.5575332826, 8.1987777881, 7.9103388728, 7.7417796743,
        		8.1980010223, 7.7351445686, 7.7131628538, 7.1563379185, 7.8565396257, 6.4563516275, 5.8609883866, 6.0307139021, 5.766446777, 6.3932660334, 
        		7.055587284, 6.4044055772, 7.3168898105, 6.2732068101, 7.0516527809, 8.7014007062, 8.1180899545, 5.5478409663, 5.4902200544, 5.6624899479,
        		6.8279609696, 5.8014818513, 6.3448725278, 7.0060026166, 6.3187420714, 5.9872107521, 5.6638112961, 5.8454620991, 5.4028286371, 5.3113437749, 
        		4.6364513829, 4.685089321, 4.3395320555, 3.6910817517, 3.5870176343, 3.1061577922, 2.6468181244, 2.7474305051, 2.8484806727, 4.2732994845, 
        		4.3478367803, 4.1047774167, 3.7334973117, 3.8054214312, 3.0360648181, 2.5306488803, 2.0872384402, 4.0948287401, 2.1592919712, 2.3313181961, 
        		1.7607254357, 3.619009156, 2.6730932958, 2.6747764067, 2.599044159, 3.4058158457, 4.1400723379, 3.2006047872, 2.3039468087, 3.2158580965, 
        		3.5890504068, 3.6544150868, 4.7411783606, 6.2552165106, 6.4591068111, 7.4685655783, 6.3623980506, 6.3121092699, 7.2132757379, 8.1172464232,
        		7.8025640488, 8.2355135091, 8.2245207286, 8.6706870038, 8.2155987706, 8.0632442664, 8.3760628714, 9.8597351044, 10.098165092, 9.7639960799, 
        		9.9641118535, 9.3633176235, 9.3003905467, 10.2779491105, 10.3154521206, 10.1652730724, 10.4128337003, 10.3226050221, 10.1642873337, 9.5859930322, 
        		9.6849911895, 9.7925979856, 8.8372006034, 8.6550269946, 8.4943996436, 8.5024453895, 7.7045455801, 8.1850919038, 7.8847964183, 7.2892523302, 
        		7.0261674834, 6.4738676386, 7.0503616547, 6.4836623346, 5.867157408, 5.4806657567, 6.0090645586, 6.4738796982, 6.3740010753, 5.570414322, 
        		5.9144895307, 5.7782364823, 6.5317963383, 5.621104496, 5.9112303198, 5.4463600569, 6.481561544, 5.7606249156, 5.8316940374, 6.0573223043, 
        		5.4705326307, 4.4727014351 };

        		String entfile = "C:\\Users\\znb_n\\eclipse-workspace\\Test4-profiles\\entFile.csv";
                String confile = "C:\\Users\\znb_n\\eclipse-workspace\\Test4-profiles\\conFile.csv";
        		int windowSize = 12;

                // Calculate means for overlapping windows
                ArrayList<Double> means = calculateMeans(data, windowSize);

                // Calculate entropies for each window
                ArrayList<Double> entropies = calculateEntropies(data, windowSize);
                
                System.out.println("Entropies:");
                print(entfile, entropies);
                
                // Calculate confidence intervals for each mean
                ArrayList<Pair<Double, Double>> confidenceIntervals = calculateConfidenceIntervals(means, data, windowSize);
                printc(confile, confidenceIntervals);
                
                System.out.println("Conf Interval:");
                System.out.println(confidenceIntervals);
                
                // Plot the results
                plotEntropyAndMeans(entropies, means, confidenceIntervals);
            }

            private static void printc(String conFile, ArrayList<Pair<Double, Double>> confidenceIntervals) {

            	String CSV_SEPARATOR = ",";
		    	try {
			    	
					BufferedWriter csvWriter = new BufferedWriter(new FileWriter(conFile, false));
			    	
			    	for (int i = 0; i<confidenceIntervals.size() ; i++) {
						csvWriter.append(confidenceIntervals.get(i).getKey()+"").append(CSV_SEPARATOR);
						csvWriter.append(confidenceIntervals.get(i).getValue()+"").append(CSV_SEPARATOR).append(System.lineSeparator());
						
			    	}
			    	
		    	csvWriter.close();
		    	}
				catch (Exception e) {
			        System.out.println("Error in FileWriter !!!");
			        e.printStackTrace();
			    }
		    	
	}

			private static void print(String entFile, ArrayList<Double> entropies) {

            	String CSV_SEPARATOR = ",";
		    	try {
			    	
					BufferedWriter csvWriter = new BufferedWriter(new FileWriter(entFile, false));
			    	
			    	for (int i = 0; i<entropies.size() ; i++) {
						csvWriter.append(entropies.get(i)+"").append(CSV_SEPARATOR).append(System.lineSeparator());
						
			    	}
			    	
		    	csvWriter.close();
		    	}
				catch (Exception e) {
			        System.out.println("Error in FileWriter !!!");
			        e.printStackTrace();
			    }
		    	

	}

			// Method to calculate the mean of each overlapping window
            public static ArrayList<Double> calculateMeans(double[] data, int windowSize) {
                ArrayList<Double> means = new ArrayList<>();
                int numWindows = data.length - windowSize + 1;

                for (int i = 0; i < numWindows; i++) {
                    double sum = 0;
                    for (int j = i; j < i + windowSize; j++) {
                        sum += data[j];
                    }
                    means.add(sum / windowSize);
                }
                return means;
            }

            // Method to calculate the entropy of each window
            public static ArrayList<Double> calculateEntropies(double[] data, int windowSize) {
                ArrayList<Double> entropies = new ArrayList<>();
                int numWindows = data.length - windowSize + 1;

                for (int i = 0; i < numWindows; i++) {
                    double[] window = Arrays.copyOfRange(data, i, i + windowSize);
                    double[] probabilities = calculateProbabilityDistribution(window);
                    entropies.add(calculateEntropy(probabilities));
                }
                return entropies;
            }

            // Method to calculate the probability distribution of values within a window
            public static double[] calculateProbabilityDistribution(double[] window) {
                double sum = 0;
                for (double value : window) {
                    sum += value;
                }
                double[] probabilities = new double[window.length];
                for (int i = 0; i < window.length; i++) {
                    probabilities[i] = window[i] / sum;
                }
                return probabilities;
            }

            // Method to calculate the entropy of a probability distribution
            public static double calculateEntropy(double[] probabilities) {
                double entropy = 0;
                for (double probability : probabilities) {
                    if (probability != 0) {
                        entropy -= probability * Math.log(probability);
                    }
                }
                return entropy;
            }

            // Method to calculate the standard error
            public static double calculateStandardError(double[] data, int windowSize) {
                double sumOfSquaredDeviations = 0;
                int numWindows = data.length - windowSize + 1;

                for (int i = 0; i < numWindows; i++) {
                    double sum = 0;
                    for (int j = i; j < i + windowSize; j++) {
                        sum += data[j];
                    }
                    double mean = sum / windowSize;

                    // Calculate the sum of squared deviations
                    for (int j = i; j < i + windowSize; j++) {
                        sumOfSquaredDeviations += Math.pow(data[j] - mean, 2);
                    }
                }

                // Calculate the variance
                double variance = sumOfSquaredDeviations / (data.length - windowSize);

                // Calculate the standard error
                return Math.sqrt(variance / windowSize);
            }

            // Method to calculate the confidence interval for each mean
            public static ArrayList<Pair<Double, Double>> calculateConfidenceIntervals(ArrayList<Double> means, double[] data, int windowSize) {
                ArrayList<Pair<Double, Double>> confidenceIntervals = new ArrayList<>();
                double alpha = 0.05; // Significance level

                // Calculate the standard error
                double standardError = calculateStandardError(data, windowSize);

                for (double mean : means) {
                    // Calculate critical value (z-score)
                    double criticalValue = 1.96; // For alpha = 0.05 (two-tailed test)
                    // Calculate margin of error
                    double marginOfError = criticalValue * standardError;
                    // Calculate confidence interval
                    double lowerBound = mean - marginOfError;
                    double upperBound = mean + marginOfError;
                    confidenceIntervals.add(new Pair<>(lowerBound, upperBound));
                }
                return confidenceIntervals;
            }

            // Method to plot entropy, means, and confidence intervals
            public static void plotEntropyAndMeans(ArrayList<Double> entropies, ArrayList<Double> means, ArrayList<Pair<Double, Double>> confidenceIntervals) {
                XYSeriesCollection dataset = new XYSeriesCollection();

                // Add mean values series
                XYSeries meanSeries = new XYSeries("Mean Values");
                for (int i = 0; i < means.size(); i++) {
                    meanSeries.add(i, means.get(i));
                }
                dataset.addSeries(meanSeries);

                // Add confidence intervals as markers
                for (int i = 0; i < confidenceIntervals.size(); i++) {
                    double lowerBound = confidenceIntervals.get(i).getFirst();
                    double upperBound = confidenceIntervals.get(i).getSecond();
                    ValueMarker lowerMarker = new ValueMarker(i - 0.5);
                    ValueMarker upperMarker = new ValueMarker(i + 0.5);
                    lowerMarker.setPaint(Color.LIGHT_GRAY);
                    upperMarker.setPaint(Color.LIGHT_GRAY);
                    lowerMarker.setValue(lowerBound);
                    upperMarker.setValue(upperBound);
                    // Add markers to the plot
                    // Add markers to the plot
                }

                // Add entropy series
                XYSeries entropySeries = new XYSeries("Entropy");
                for (int i = 0; i < entropies.size(); i++) {
                    entropySeries.add(i, entropies.get(i));
                }
                dataset.addSeries(entropySeries);

                // Create chart
                JFreeChart chart = ChartFactory.createXYLineChart(
                        "Entropy vs Mean Values with Confidence Intervals", "Window Index", "Value",
                        dataset
                );

                // Display chart
                ChartPanel chartPanel = new ChartPanel(chart);
                JFrame frame = new JFrame("Entropy vs Mean Values with Confidence Intervals");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(chartPanel);
                frame.pack();
                frame.setVisible(true);
            }
        }
