package exp2window;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
//After this copy confFile to MeanTraffic-sorted/notsorted (column 3,4)

public class ProfileProcesspr {
	
	static String cvsSplitBy = ",";
    static int windowNum = 179;
   
	private static ArrayList<Window> Windows; // array list that has the traffic trace
    public static String meanFile = "C:\\Users\\znb_n\\eclipse-workspace\\VehEdgCld\\src\\EdgeEPOS\\Setting\\input\\MeanTraffic-sorted.csv";
			
	public static void main(String[] args) {
		
		String pathToWindow;
		Windows = new ArrayList<Window>();
		readIndex();
		System.out.println("done"+Windows.size());
		
		//sorted costs
		String costFile = "C:\\Users\\znb_n\\eclipse-workspace\\Test4-profiles\\costPerWindow-sorted.csv";
		
		for (int i = 0; i<Windows.size(); i++) {
			pathToWindow = "C:\\Users\\znb_n\\eclipse-workspace\\Test4-profiles\\"+Windows.get(i).getWinId()+"\\Methods\\";
			readCosts(i, pathToWindow);
			Windows.get(i).calAvg();
		}
		print(costFile);
		
		//not sorted costs
		Windows = new ArrayList<Window>();
		
		for (int i = 0; i<179; i++) {
			Windows.add(i, new Window(i,0,0));
		    pathToWindow = "C:\\Users\\znb_n\\eclipse-workspace\\Test4-profiles\\"+i+"\\Methods\\";
			readCosts(i, pathToWindow);
			Windows.get(i).calAvg();
		}
		costFile = "C:\\Users\\znb_n\\eclipse-workspace\\Test4-profiles\\costPerWindow-notsorted.csv";
		print(costFile);
		
	}

	private static void print(String costFile) {
	    	String CSV_SEPARATOR = ",";
	    	try {
		    	
				BufferedWriter csvWriter = new BufferedWriter(new FileWriter(costFile, false));
		    	
		    	for (int i = 0; i<Windows.size() ; i++) {
					csvWriter.append(Windows.get(i).getWinId()+"").append(CSV_SEPARATOR)
					.append(Windows.get(i).getAvgTraffic()+"").append(CSV_SEPARATOR)
					.append(Windows.get(i).getFirstPro()+"").append(CSV_SEPARATOR);
					for (int j = 0; j<Windows.get(i).lCosts.length ; j++) {
						csvWriter.append(Windows.get(i).lCosts[j]+"").append(CSV_SEPARATOR)
						.append(Windows.get(i).gCosts[j]+"").append(CSV_SEPARATOR);
					}
					csvWriter.append(System.lineSeparator());
					
		    	}
		    	
	    	csvWriter.close();
	    	}
			catch (Exception e) {
		        System.out.println("Error in FileWriter !!!");
		        e.printStackTrace();
		    }
	    	
		
	}



	private static void readCosts(int i, String pathToWindow) {
		
		BufferedReader br = null;
		String line = "";
	    int methods = 6; 
	    int index = 2; 
	    try {
        	
        	br = new BufferedReader(new FileReader(pathToWindow+"CostsAfterDeployment.csv"));
        	line = br.readLine();//header
        	line = br.readLine();//first run
        	
            for (int run = 1 ; run < 36 ; run++) {
	        	  line = br.readLine();
	        	  String[] input = line.split(cvsSplitBy);
	        	 
	        	  for (int k = 0; k<methods ; k++) {
	        		 Windows.get(i).addCost(k, Double.parseDouble(input[index+(5*k)]), Double.parseDouble(input[index +(5*k)+ 1]));//2,3
	
            	}
	        }
            
        }
        
        catch (IOException e) 
        {
            e.printStackTrace();
        
		} 
        finally {
	        try {
	            br.close();
            } 
	        catch(IOException e)
	        {
	            System.out.println(e);
	         }
		}
        
		
	}

	
	/**
	 * windowId, windowAvg, window1Profile
	 */
	private static void readIndex() { 
		BufferedReader br = null;
		String line = "";
	    
		try {
        
        	br = new BufferedReader(new FileReader(meanFile));
            
            for (int window = 0 ; window < windowNum ; window++) {//0-178
	        	  line = br.readLine();//last iteration
	        	  String[] input = line.split(cvsSplitBy);
	        	  Windows.add(new Window(Integer.parseInt(input[0]), Double.parseDouble(input[1]), Double.parseDouble(input[2])));
	        }
            
        }
        
        catch (IOException e) 
        {
            e.printStackTrace();
        
		} 
        finally {
	        try {
	            br.close();
            } 
	        catch(IOException e)
	        {
	            System.out.println(e);
	         }
		}
        
		
	}

}
