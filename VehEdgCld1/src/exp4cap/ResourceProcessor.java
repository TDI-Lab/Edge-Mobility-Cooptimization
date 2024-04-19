package exp4cap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;

public class ResourceProcessor {

	static String cvsSplitBy = ",";
    static int windowNum = 178;
   
	private static ArrayList<CapToDem> Sample; 
    public static String base = "C:\\Users\\znb_n\\eclipse-workspace\\Test5-capacity\\";
			
    public static void main(String[] args) {
		
    	String pathToWindow;
		Sample = new ArrayList<CapToDem>(); 
		Sample = new ArrayList<>(Collections.nCopies(30, null)); // Initialize Sample with null values

		String costFile = base+"costPerRatio.csv";
		int index = 0;
		
		int[] a = {1, 2, 4};

		for (int i = 0 ; i<a.length ; i++) {
			for (int j = 0 ; j<5 ; j++) {
				index = a[i]*5+j;
				Sample.add(index, new CapToDem(a[i]*5,0.8+j*0.1));
				if((j==2)&&(i==1)) {
					for (int k = 0; k<9; k++) {
						pathToWindow = base+a[i]*5+"-"+String.format("%.1f", (0.8 + j * 0.1))+"-"+k+"\\Methods\\";//5-1.2-2
						readCosts(index, pathToWindow);
					}
					Sample.get(index).calAvg(1);
				}
				
				else {
					for (int k = 0; k<10; k++) {
						pathToWindow = base+a[i]*5+"-"+String.format("%.1f", (0.8 + j * 0.1))+"-"+k+"\\Methods\\";//5-1.2-2
						readCosts(index, pathToWindow);
					}
					Sample.get(index).calAvg();
					
				}
			
				
			}
		}
			
			System.out.println("done1");
			print(costFile);
			
			
		}
	

   private static void createDir() {
	   String parentDirectory = base;
       String[] firstLevels = {"10", "20", "30", "40", "50", "60", "70", "80", "90", "100", "110"};
       String[] secondLevels = {"0.8", "0.9", "1.0", "1.1", "1.2"};
       String[] thirdLevels = {"0", "1", "2", "3", "4", "5", "6", "7","8","9"};

    	        for (String firstLevel : firstLevels) {
    	            for (String secondLevel : secondLevels) {
    	                for (String thirdLevel : thirdLevels) {
    	                    String folderName = firstLevel + "-" + secondLevel + "-" + thirdLevel;
    	                    File folder = new File(parentDirectory, folderName);
    	                    if (!folder.exists()) {
    	                        boolean created = folder.mkdirs();
    	                        if (created) {
    	                            System.out.println("Folder created: " + folder.getAbsolutePath());
    	                        } else {
    	                            System.out.println("Failed to create folder: " + folder.getAbsolutePath());
    	                        }
    	                    }
    	                }
    	            }
    	        }
    	    
    	

    	

   }
		private static void print(String costFile) {
		    	String CSV_SEPARATOR = ",";
		    	try {
			    	
					BufferedWriter csvWriter = new BufferedWriter(new FileWriter(costFile, false));
			    	
			    	for (int i = 0; i<Sample.size() ; i++) {
						csvWriter.append(Sample.get(i).nodenum+"").append(CSV_SEPARATOR)
						.append(Sample.get(i).cap2dem+"").append(CSV_SEPARATOR);
						for (int j = 0; j<Sample.get(i).lCosts.length ; j++) {
							csvWriter.append(Sample.get(i).lCosts[j]+"").append(CSV_SEPARATOR)
							.append(Sample.get(i).gCosts[j]+"").append(CSV_SEPARATOR);
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
		        		 Sample.get(i).addCost(k, Double.parseDouble(input[index+(5*k)]), Double.parseDouble(input[index +(5*k)+ 1]));//2,3
		
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

		
	}
