package exp1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;


/**
 * @author znb_n
 * cpu utilization file processor for average calculation over 35 run
 */
public class UtilAvgCsv {


	public static void main(String[] args) {
		String base = "C:\\Users\\znb_n\\eclipse-workspace\\Test3-target-upEne\\Methods\\";
    	
		for (int methods = 0; methods<6; methods++) {
	    	double[] matrix = new double[114];
	    	String inputFilePath = base+methods+"CPUUtilRespoOpt.csv";

	        try (BufferedReader reader = new BufferedReader(new FileReader(inputFilePath))) {
	            String header = reader.readLine(); // Read the header to get column names

	            int matrixRows = 114;
	            int matrixCols = 35;
	            String line;
	            
	            line = reader.readLine();//ignore header
	        	  
	            // Loop through the data and create 6x20 matrices in separate CSV files for each row
	            while ((line = reader.readLine()) != null) {
	                String[] row = line.split(",");
	                
	                for (int i = 0; i < matrixRows; i++) {
	                    matrix[i] += Double.parseDouble(row[i+3]);
	                }
	            }

	             for (int i = 0; i < matrixRows; i++) {
	                    matrix[i] /= matrixCols;
	                    
	                }

	                // Write the matrix to a new CSV file with a header including a series from 1 to 19
	                String outputFilePath = base+methods+"CPUUtilRespoOpt-avg.csv";
	                writeMatrixToCSV(matrix, outputFilePath);
	                
	                Arrays.sort(matrix);
	                
	                String outputFilePaths = base+methods+"CPUUtilRespoOpt-savg.csv";
	                writeMatrixToCSV(matrix, outputFilePaths);

	            
	            System.out.println("Processing completed successfully.");

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	    }

	    private static void writeMatrixToCSV(double[] matrix, String outputFilePath) {
	    	String CSV_SEPARATOR = ",";
	    	
	    	try (BufferedWriter csvWriter = new BufferedWriter(new FileWriter(outputFilePath, false))) {
		    	
		    	for (int i = 0; i<matrix.length ; i++) {
					csvWriter.append(matrix[i]+"").append(CSV_SEPARATOR)
					.append(System.lineSeparator());
					}
					
		    csvWriter.close();
	    	}
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	}
