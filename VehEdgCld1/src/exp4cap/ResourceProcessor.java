package exp4cap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
//Dispersion measures: https://economipedia.com/definiciones/dispersion-measures.html

import org.apache.commons.math3.util.Pair;

public class ResourceProcessor {

	static String cvsSplitBy = ",";
    static int windowNum = 178;
   
	private static ArrayList<CapToDem> Sample; 
    public static String base = "C:\\Users\\znb_n\\eclipse-workspace\\Test5-capacity\\";
	private static int gFunc = 3;		//0:var, 1:std, 2:range, 3:Coefficient of variation (greater than 1)
    
	public static void main(String[] args) {
		       
		String pathToWindow;
		Sample = new ArrayList<CapToDem>(); 
		
		
		//Variance for active nodes:
		String costFile1 = base+"costPerRatio-"+gFunc+".csv";
		int[] a = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22};//10,15,20,30,....
		int sindex = 0;
		
		for (int i = 0 ; i<a.length ; i++) {
			for (int j = 0 ; j<5 ; j++) {
				Sample.add(new CapToDem(a[i]*5,0.8+j*0.1));
				
				for (int k = 0; k<10; k++) {
						pathToWindow = base+a[i]*5+"-"+String.format("%.1f", (0.8 + j * 0.1))+"-"+k+"\\Methods\\";//5-1.2-2
						readCostsOnActiveNodes(sindex, a[i]*5, pathToWindow, k);
					}
					Sample.get(sindex).calculateConfidenceIntervals();
					Sample.get(sindex).calAvg();
				
				sindex++;	
			}//end j
		}
		
		print(costFile1);
		printEB();
	}
	
	
			/**calculates the variance from the results which is the variance for all nodes
		 * @param i
		 * @param pathToWindow
		 */
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
	    

		/**calculates the variance/STD of utilizations for active nodes
		 * @param i
		 * @param nodes
		 * @param pathToWindow
		 */
		private static void readCostsOnActiveNodes(int i, int nodes, String pathToWindow, int sample) {
			
			int sindex = sample*35;
			BufferedReader br = null;
			String [] cbrfiles = {pathToWindow+0+"MemUtilRespoOpt.csv",pathToWindow+1+"MemUtilRespoOpt.csv",pathToWindow+2+"MemUtilRespoOpt.csv",
					pathToWindow+3+"MemUtilRespoOpt.csv",pathToWindow+4+"MemUtilRespoOpt.csv",pathToWindow+5+"MemUtilRespoOpt.csv"};
	    	
			String [] mbrfiles = {pathToWindow+0+"CPUUtilRespoOpt.csv",pathToWindow+1+"CPUUtilRespoOpt.csv",pathToWindow+2+"CPUUtilRespoOpt.csv",
					pathToWindow+3+"CPUUtilRespoOpt.csv",pathToWindow+4+"CPUUtilRespoOpt.csv",pathToWindow+5+"CPUUtilRespoOpt.csv"};
	    	
			BufferedReader[] csvReader = new BufferedReader[6];
			BufferedReader[] mcsvReader = new BufferedReader[6];
			
			String line = "", mline, cline;
		    int methods = 6; 
		    int index = 2; 
		    double gc = 0;
		    double mean, max, min;
		    double [] cdat = new double[114];
		    double [] mdat = new double[114];
		    double [] tdat = new double[nodes*2];
		    double sumSquaredDifferences = 0;
		    
		    try {
		    	for (int m = 0; m < 6; m++) {
		            csvReader[m] = new BufferedReader(new FileReader(cbrfiles[m]));
		            mcsvReader[m] = new BufferedReader(new FileReader(mbrfiles[m]));
		        }
		    	
	        		br = new BufferedReader(new FileReader(pathToWindow+"CostsAfterDeployment.csv"));
	        			
	        		line = br.readLine();//header
    			  	line = br.readLine();//first run
    			  	
    			  	for (int m = 0;m<methods; m++) {
	    		 		cline = csvReader[m].readLine();//header,first
		    		   	mline = mcsvReader[m].readLine();//header,first
    			  	}
    			  	for (int m = 0;m<methods; m++) {
	    		 		cline = csvReader[m].readLine();//header,first
		    		   	mline = mcsvReader[m].readLine();//header,first
    			  	}
    			  	
	  			for (int run = 1 ; run < 36 ; run++) {
	  						
	  						line = br.readLine();//next run
	  						String[] input = line.split(cvsSplitBy);
	  			        	 
    	  					for (int k = 0; k<methods ; k++) {
            	  			    
    	  	    		   		cline = csvReader[k].readLine();
    	  	    		   		mline = mcsvReader[k].readLine();
    	  	    			  	
    	  	    	  			String[] cinput = cline.split(cvsSplitBy);
    	 						String[] minput = mline.split(cvsSplitBy);
    	 						
        	  					for(int j=0;j<114; j++){
    	 							cdat[j] = Double.parseDouble(cinput[j+3]);
    	 							mdat[j] = Double.parseDouble(minput[j+3]); 
    	 							}
    	 						Arrays.sort(cdat);//ascending
						        Arrays.sort(mdat);
						        
						        // Reversing the sorted array to get descending order
						        for(int f = 0; f < cdat.length / 2; f++) {
						            double temp = cdat[f];
						            cdat[f] = cdat[cdat.length - 1 - f];
						            cdat[cdat.length - 1 - f] = temp;
						        }
						        for(int f = 0; f < mdat.length / 2; f++) {
						        	double temp = mdat[f];
						            mdat[f] = mdat[mdat.length - 1 - f];
						            mdat[mdat.length - 1 - f] = temp;
						        
						        }
    	 						
    	 						for(int f = 0; f < nodes; f++) {
    	 							tdat[f] = cdat[f];
    	 							tdat[f+nodes] = mdat[f];
    	 						}
    	 						
    	 						Arrays.sort(tdat);//descending
    	 						max = tdat[2*nodes- 1];
    	 						min = tdat[0];
    	 						mean = calculateMean(tdat);
						        sumSquaredDifferences = calculateSumSquaredDifferences(tdat, mean, nodes*2);
						        
						        //Dispersion measures:
						        
						        if (gFunc == 0)//variance
						        	gc = sumSquaredDifferences / tdat.length;
						        else if(gFunc == 1)//std
						        	gc = Math.sqrt(sumSquaredDifferences / tdat.length);
						        else if(gFunc == 2)//range
						        	gc = max-min;
						        else if(gFunc == 3)//Coefficient of variation
						        	gc = ((sumSquaredDifferences / tdat.length))/mean;//Math.sqrt
						        	
						        	//System.out.println("len: " + mean+" sumdiff "+sumSquaredDifferences+" tdat.length "+tdat.length+" gc "+gc);
						Sample.get(i).addCost(k, gc, Double.parseDouble(input[index +(5*k)+ 1]), (sindex + run -1));//2,3
    		    	
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
		            for (int m = 0;m<methods; m++) {
	    		 		csvReader[m].close();
		    		   	mcsvReader[m].close();
    			  	}	            
		            } 
		        catch(IOException e)
		        {
		            System.out.println(e);
		         }
			}
	    }
		
		
		private static void printEB() {
			String CSV_SEPARATOR = ",";
	    	
		    try {
				   
		    	String [] ebFilea = {base+"nodes-eb-costPerRatio"+gFunc+"-"+0.8+".csv",base+"nodes-eb-costPerRatio"+gFunc+"-"+0.9+".csv",base+"nodes-eb-costPerRatio"+gFunc+"-"+1.0+".csv",
		    			base+"nodes-eb-costPerRatio"+gFunc+"-"+1.1+".csv",base+"nodes-eb-costPerRatio"+gFunc+"-"+1.2+".csv"};
		    	
		    	BufferedWriter [] csvWriter = {new BufferedWriter(new FileWriter(ebFilea[0], false)),
		    			new BufferedWriter(new FileWriter(ebFilea[1], false)),
		    			new BufferedWriter(new FileWriter(ebFilea[2], false)),
		    			new BufferedWriter(new FileWriter(ebFilea[3], false)),
		    			new BufferedWriter(new FileWriter(ebFilea[4], false))};
		    	
		    	for (int i = 0; i<Sample.size() ; i++) {
			    		
		    		csvWriter[i%5].append(Sample.get(i).nodenum+"").append(CSV_SEPARATOR)
					.append(Sample.get(i).cap2dem+"").append(CSV_SEPARATOR);
					
			    	for (int j = 0; j<2*Sample.get(i).lCosts.length ; j++) {
			    			//local cost,global cost
			    			csvWriter[i%5].append(Sample.get(i).confidenceIntervals.get(j).getKey()+"").append(CSV_SEPARATOR);
							csvWriter[i%5].append(Sample.get(i).confidenceIntervals.get(j).getValue()+"").append(CSV_SEPARATOR);
							//
							//csvWriter[i%5].append(Sample.get(i).confidenceIntervals.get(j).getKey()+"").append(CSV_SEPARATOR);
							//csvWriter[i%5].append(Sample.get(i).confidenceIntervals.get(j).getValue()+"").append(CSV_SEPARATOR);
							
						}
						csvWriter[i%5].append(System.lineSeparator());
						
			    	}
			    	
		    	csvWriter[0].close();
		    	csvWriter[1].close();
		    	csvWriter[2].close();
		    	csvWriter[3].close();
		    	csvWriter[4].close();
		    	}
				catch (Exception e) {
			        System.out.println("Error in FileWriter !!!");
			        e.printStackTrace();
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
						.append(Sample.get(i).gCosts[j]*100+"").append(CSV_SEPARATOR);
						
					}
					csvWriter.append(System.lineSeparator());
					
		    	}
		    	
	    	csvWriter.close();
	    	}
			catch (Exception e) {
		        System.out.println("Error in FileWriter !!!");
		        e.printStackTrace();
		    }
	    	
	    	
	    try {
			   
	    	String [] costFilea = {base+"nodes-costPerRatio"+gFunc+"-"+0.8+".csv",base+"nodes-costPerRatio"+gFunc+"-"+0.9+".csv",base+"nodes-costPerRatio"+gFunc+"-"+1.0+".csv",
	    			base+"nodes-costPerRatio"+gFunc+"-"+1.1+".csv",base+"nodes-costPerRatio"+gFunc+"-"+1.2+".csv"};
	    	
	    	BufferedWriter [] csvWriter = {new BufferedWriter(new FileWriter(costFilea[0], false)),
	    			new BufferedWriter(new FileWriter(costFilea[1], false)),
	    			new BufferedWriter(new FileWriter(costFilea[2], false)),
	    			new BufferedWriter(new FileWriter(costFilea[3], false)),
	    			new BufferedWriter(new FileWriter(costFilea[4], false))};
	    	
	    	for (int i = 0; i<Sample.size() ; i++) {
		    		
		    		csvWriter[i%5].append(Sample.get(i).nodenum+"").append(CSV_SEPARATOR)
					.append(Sample.get(i).cap2dem+"").append(CSV_SEPARATOR);
					
		    		for (int j = 0; j<Sample.get(i).lCosts.length ; j++) {
						csvWriter[i%5].append(Sample.get(i).lCosts[j]+"").append(CSV_SEPARATOR)
						.append(Sample.get(i).gCosts[j]+"").append(CSV_SEPARATOR);//100 for %
						
					}
					csvWriter[i%5].append(System.lineSeparator());
					
		    	}
		    	
	    	csvWriter[0].close();
	    	csvWriter[1].close();
	    	csvWriter[2].close();
	    	csvWriter[3].close();
	    	csvWriter[4].close();
	    	}
			catch (Exception e) {
		        System.out.println("Error in FileWriter !!!");
		        e.printStackTrace();
		    }
	    	
	}

	    // Method to calculate the mean
	    private static double calculateMean(double[] array) {
	        double sum = 0;
	        for (double num : array) {
	            sum += num;
	        }
	        return sum / array.length;
	    }
	
	    // Method to calculate the sum of squared differences
	    private static double calculateSumSquaredDifferences(double[] array, double mean, int size) {
	        double sumSquaredDifferences = 0;
	        for (int i = 0; i<size; i++) {
	            sumSquaredDifferences += Math.pow(array[i] - mean, 2);
	        }
	        return sumSquaredDifferences;
	    }

	    /*
		String parentDirectory = "C:\\Users\\znb_n\\eclipse-workspace\\Test5-capacity\\";;
		
	    String[] firstLevels = {"15", "25", "35", "45", "55", "65", "75", "85", "95", "105"};
	    	        String[] secondLevels = {"0.8", "0.9", "1.0", "1.1", "1.2"};
	    	        String[] thirdLevels = {"0", "1", "2", "3", "4", "5", "6", "7","8","9","10"};

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
	    	    
	    	

	    	System.exit(0);

	     */

		
		/* Variance for all nodes: active+deactive 
		String costFile = base+"costPerRatio.csv";
		
		int []a = {2, 4, 6, 20};//8,10
		int sindex = 0;
		for (int i = 0 ; i<a.length ; i++) {
			for (int j = 0 ; j<5 ; j++) {
				Sample.add(new CapToDem(a[i]*5,0.8+j*0.1));
				
				if((j==1)&&(i==3)) {
					
					for (int k = 0; k<10; k++) {
						pathToWindow = base+a[i]*5+"-"+String.format("%.1f", (0.8 + j * 0.1))+"-"+k+"\\Methods\\";//5-1.2-2
						readCosts(sindex, pathToWindow);
					}
					Sample.get(sindex).calAvg(1);
					break;
					
				}
				
				else {
					
					for (int k = 0; k<10; k++) {
						pathToWindow = base+a[i]*5+"-"+String.format("%.1f", (0.8 + j * 0.1))+"-"+k+"\\Methods\\";//5-1.2-2
						readCosts(sindex, pathToWindow);
					}
					Sample.get(sindex).calAvg();
					
				}
				sindex++;	
			}//end j
		}
			
			System.out.println("done1");
			print(costFile);
			
			*/
	}
