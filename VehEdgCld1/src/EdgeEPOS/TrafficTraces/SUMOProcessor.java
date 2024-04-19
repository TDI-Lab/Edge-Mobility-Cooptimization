package EdgeEPOS.TrafficTraces;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.LineNumberReader;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author rooyesh
 * 
 * preprocess osm output file: convert overall .csv file to individual ones
 * in osm.sumocfg :
 * fcd output: Floating Car Data includes name, position, angle and type for every vehicle
 * All output files written by SUMO are in XML-format by default. Using python tool xml2csv.py 
 * you can convert them to a flat-file (CSV) format 
 * C:\Program Files (x86)\Eclipse\Sumo\tools\xml
 * 	 
 */
public class SUMOProcessor {

	static ConcurrentHashMap<String, Integer> hash_map1 = new ConcurrentHashMap<String, Integer>(); 
	public static final char CSV_SEPARATOR = ','; // it could be a comma or a semicolon
	public static final String CSV_SEPARATOR1 = ";"; // it could be a comma or a semicolon

	private static FileInputStream input;
	
	public static void overalToIndividualVehicles() {
		HashSet<String> set=new HashSet<String>();  
		
		
		String pathToCsv = "C:\\Users\\rooyesh\\sumo\\2020-12-05-19-20-26\\osm.csv";
		String line,line1;
		
		try {
			LineNumberReader csvReader = new LineNumberReader(new FileReader(pathToCsv));
			line = csvReader.readLine();
			while ((line = csvReader.readLine()) != null) {
				String[] data = line.split(CSV_SEPARATOR1);
			    
			    if (data.length<8)
			    	continue;
			    
			    if (!set.contains(data[2])) {
			    	String vehicle = data[2];
			    	//output files named with respect to the included vehicles in the input trace:
			    	BufferedWriter csvWriter = new BufferedWriter(new FileWriter("C:\\Users\\rooyesh\\eclipse-workspace\\CaidaPro\\f\\"+data[2]+".csv"));
					csvWriter.append(data[0]).append(CSV_SEPARATOR)
					.append(data[1]).append(CSV_SEPARATOR)			    	
					.append(data[8]).append(CSV_SEPARATOR)			    	
					.append(data[9]).append(CSV_SEPARATOR)			    	
					.append(data[6]).append(CSV_SEPARATOR)			    	
					.append(System.lineSeparator());
					
					int count = csvReader.getLineNumber();
					LineNumberReader csvReader1 = new LineNumberReader(new FileReader(pathToCsv));
					
					while ((line1 = csvReader1.readLine()) != null) {
						if(csvReader1.getLineNumber() <= count) {
							 continue;
						    	
						}
						data = line1.split(CSV_SEPARATOR1);
						
						if (data.length<8)
					    	continue;
					    
					    if (data[2].equals(vehicle)) {
					    	csvWriter.append(data[0]).append(CSV_SEPARATOR)
							.append(data[1]).append(CSV_SEPARATOR)			    	
							.append(data[8]).append(CSV_SEPARATOR)			    	
							.append(data[9]).append(CSV_SEPARATOR)			    	
							.append(data[6]).append(CSV_SEPARATOR)			    	
							.append(System.lineSeparator());
							
					    }
					    
					}
					
			    set.add(vehicle);
				csvWriter.flush();
				csvWriter.close();
			    }
			    
			}
		csvReader.close();
		           
	    		
		}
		catch (Exception e) {
	        System.out.println("Error in FileReader !!!");
	        e.printStackTrace();
		}
	}
}
