package caida;

import eu.bitm.NominatimReverseGeocoding.NominatimReverseGeocodingJAPI;
import eu.bitm.NominatimReverseGeocoding.Address;

import com.neovisionaries.i18n.CountryCode;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class Processor {

	static String Spath;
	static String Dpath;
	static double Elapsedtime;
	ConcurrentHashMap<String, Integer> hash_map = new ConcurrentHashMap<String, Integer>(); 
	final Map<String,String> immutableMap;
    static String continent;
    static String country ;
    static String state;
    static String city;
    
    //constructor to initialize source and destination paths and continent map which maps continent name to the two-code continent chars
	Processor(String s, String d){
		Processor.Spath = s;
		Processor.Dpath = d;
		
		Map<String,String> continents = new HashMap<>();
	    continents.put("Africa","AF");
	    continents.put("Antarctica","AN");
	    continents.put("Europe","EU");
	    continents.put("Asia","AS");
	    continents.put("North America","NA");
	    continents.put("NorthAmerica","NA");
	    continents.put("SouthAmerica","SA");
	    continents.put("South America","SA");
	    continents.put("Oceania","OC");
	    immutableMap = Collections.unmodifiableMap(continents);
	    
	}
	
	//overloaded method: processes input files and split and returns the data for a specific continent
	public long processInput(String conti) {
		long start = System.nanoTime();
		
		String continent = immutableMap.get(conti); 
		if(continent == null) 
			throw new IllegalArgumentException(" invalid params!");
		
		String SrcNodes = Spath+"midar-iff.nodes.geo";
		String DesNodes = Dpath+"topo.nodes.geo."+continent;
		
		String SrcAS = Spath+"midar-iff.nodes.as";
		String DesAS = Dpath+"topo.nodes.as."+continent;
		
		String SrcLinks =  Spath+"midar-iff.links";
		String DesLinks = Dpath+"topo.links."+continent;
		
		splitCountry(continent, "", SrcNodes, DesNodes);
		fillMapWithNodes(DesNodes);
		removNodes(SrcAS, DesAS);
		removLinks(SrcLinks, DesLinks);
		
		long end = System.nanoTime();
		return (end-start);
	}
	//overloaded method: processes input files and split and return the data for a specific country
	public long processInput(String conti, String count) {
		long start = System.nanoTime();
		String continent = immutableMap.get(conti); 
		String country =  CountryCode.findByName(count).get(0).name();
		
		if((continent == null)||(country == null)) 
			throw new IllegalArgumentException(" invalid params!");
		
		String SrcNodes = Spath+"topo.nodes.geo.de";//This is a file we extracted from the original one (just due to the storage size reduction), it can be replaced by "midar-iff.nodes.geo".
		String DesNodes = Dpath+"topo.nodes.geo."+continent;
		
		String SrcAS = Spath+"midar-iff.nodes.as";
		String DesAS = Dpath+"topo.nodes.as."+continent;
		
		String SrcLinks =  Spath+"midar-iff.links";
		String DesLinks = Dpath+"topo.links."+continent;
		
		splitCountry(continent, country, SrcNodes, DesNodes);
		fillMapWithNodes(DesNodes);
		removNodes(SrcAS, DesAS);
		removLinks(SrcLinks, DesLinks);
		
		long end = System.nanoTime();
		return (end-start);
	}
	
	//overloaded method: processes input files and split and return the data for a specific city
	public long processInput(String conti, String count, String state, String city) {
		
		long start = System.nanoTime();
		
		String continent = immutableMap.get(conti); 
		String country =  CountryCode.findByName(count).get(0).name();
		String stateAbb = State.valueOfName(state).getAbbreviation();
		
		
		//System.out.println("state"+stateAbb);
		if(stateAbb==null) throw new IllegalArgumentException(" invalid state param!");
		
		String SrcNodes = Spath+"topo.nodes.geo.de";//midar-iff.nodes.geo";
		String Temp = Spath+"topo.nodes.temp";
		String DesNodes = Dpath+"topo.nodes.geo";
		
		String SrcAS = Spath+"midar-iff.nodes.as";
		String DesAS = Dpath+"topo.nodes.as";
		
		String SrcLinks =  Spath+"midar-iff.links";
		String DesLinks = Dpath+"topo.links";
		
		
		splitCountry(continent, country, SrcNodes, Temp);
		findLinesContainCities(stateAbb, city, Temp, DesNodes);
		
		fillMapWithNodes(DesNodes);
		removNodes(SrcAS, DesAS);
		removLinks(SrcLinks, DesLinks);
		
		long end = System.nanoTime();
		return (end-start);
		
	}
	//fill a hashmap with the node ids belong to the specified continent, country, or city.
	private void fillMapWithNodes(String Src) {
		int j;
		String line;
	    String[] strArgs;
	    
		try {
			LineNumberReader br = new LineNumberReader(new FileReader(Src));
	           
	            while((line = br.readLine())!= null) {
	            	if (line.startsWith("#")) 
		        		continue;
	            	
	            	strArgs = line.split("\\s+");
	            	j = strArgs.length;
	            	if (j>3) {
	                	hash_map.put(removeColon(strArgs[1]),1);//node id without colon
	                	
	                	}
	                else {
	                		
	                }
	                  
	            } 
	          br.close(); 
                    
		}
		catch (Exception e) {
	        System.out.println("Error in FileReader !!!");
	        e.printStackTrace();
	    }
		finally {
			
		}
		
	}
	
	private String removeColon(String str) {
	    if (str != null && str.length() > 0 && str.charAt(str.length() - 1) == ':') {
	        str = str.substring(0, str.length() - 1);
	    }
	    return str;
	}
	
	//Processes link (.links) file and keeps the links which belong to the specified continent, country, or city.
	private void removLinks(String sPath2, String dPath) {
		
		
		int i = 1, j;
		String SrcLinks = sPath2;
		String Ds = dPath;
	    String line1;
	    String[] strArgs;
	    String[] strArgs1;
	    FileWriter fileWriter = null;
	    String NEW_LINE_SEPARATOR = "\n";
	    	
	    try {
			fileWriter = new FileWriter(Ds);
			
			LineNumberReader br = new LineNumberReader(new FileReader(SrcLinks));
	           
	            while((line1 = br.readLine()) != null) {
	            	if (line1.startsWith("#")) 
		        		continue;
	            	strArgs = line1.split("\\s+");
	            	j = strArgs.length;
	            	if (j>3) {
		                	
		        	    	int k = 2;
		        			String candidateNode;
		        	        while (k<j){
		        	        	
		        	        		 int iend = strArgs[k].indexOf(":"); //this finds the first occurrence of ":" 
		        					 if (iend != -1) {
		        						 candidateNode = strArgs[k].substring(0 , iend); 
		        						 
		        					 }
		        					 else {
		        						 candidateNode = strArgs[k]; 
		        						
		        					 }
		        					 if (hash_map.containsKey(candidateNode)){
		        						 
		        					 }
		        					 else {
		        						 line1=line1.replace(strArgs[k]+" ", " ");
		        						 
		        						 }
		        					k++;
		        				}
		        				
		                	strArgs1 = line1.split("\\s+");
		                	
		                	if(strArgs1.length > 3) {
		                		
			            		 String printStr = "";
			            		 for(i=0; i<strArgs1.length-1; i++) {
			            			 if (i!=2)
			            				 printStr = printStr + strArgs1[i]+" ";
			            			 else
			            				 printStr = printStr + " " + strArgs1[i]+" ";
			            			  
			            		 }
			            		 
		            		 printStr = printStr + strArgs1[i];
		            		 fileWriter.append(printStr);
		            		 fileWriter.append(NEW_LINE_SEPARATOR);
							}
							else {
							
							}
		                }	
		       	
	            } 
	            
	            br.close();   
	            
	           }
		
    	catch (Exception e) 
		{
                    System.out.println("Error in FileWriter !!!");
                    e.printStackTrace();
        } 
		
		finally {
                     
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println("Error while flushing/closing fileWriter !!!");
                    e.printStackTrace();
                }
        }
	}
		
	//Processes node (.nodes.as) file and keeps the nodes which belong to the specified continent, country, or city.
	private void removNodes(String sPath, String dPath) {
	
		int i = 1, j;
		String line1;
	    String[] strArgs;
	    FileWriter fileWriter = null;
	    String NEW_LINE_SEPARATOR = "\n";
	    
		
	   // System.out.println ("hash size2: "+hash_map.size());      
		try {
			fileWriter = new FileWriter(dPath);
			
			LineNumberReader br = new LineNumberReader(new FileReader(sPath));
	           
	            while((line1 = br.readLine()) != null) {
            	if (line1.startsWith("#")) 
	        		continue;
            	
            	strArgs = line1.split("\\s+");
	            	j = strArgs.length;
	                if (j>1) {
	                	if (hash_map.containsKey(strArgs[1])){
	                		fileWriter.append(line1);
		                	fileWriter.append(NEW_LINE_SEPARATOR);
	                	}
	                }
                	else {
                		//System.out.println("endOfFole");
                	}
	             					
            } 
            
            br.close();   
		}
    	catch (Exception e) 
		{
                    System.out.println("Error in FileWriter !!!");
                    e.printStackTrace();
        } 
		finally {
                     
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println("Error while flushing/closing fileWriter !!!");
                    e.printStackTrace();
                }
        }
}
	
	//Processes (.nodes.geo) file and keeps the nodes which belong to the specified city.
	private void findLinesContainCities(String state, String city, String sPath, String dPath) {
		
		int j;
		String stateCode = "";
		Address a;
		String line;
        String[] strArgs;

		if (state.compareTo("BY")==0)
			stateCode = "02";//state code of Bavaria
		
		
        FileWriter fileWriter = null;
        String NEW_LINE_SEPARATOR = "\n";
       int i = 0;
		
        try {
			fileWriter = new FileWriter(dPath);
			LineNumberReader br = new LineNumberReader(new FileReader(sPath));
	        
			while((line = br.readLine())!= null) {
				i++;
				//System.out.println("i"+i);
				if (line.startsWith("#")) 
	        		continue;
		            
	            	strArgs = line.split("\\s+");
	            	j = strArgs.length;
	                if (j>4) {
	                	//System.out.println(line);
	                	//System.out.println("j>3 "+city+" "+state+" "+strArgs[4]+" "+strArgs[5]);
	                	if ((strArgs[4].compareTo(stateCode) == 0)&&(strArgs[5].compareTo(city) == 0)) {
	                		//System.out.println("done");
	                		fileWriter.append(line);
	                		fileWriter.append(NEW_LINE_SEPARATOR);
	                        
	                	}
	                	else {
	                		//System.out.println("endOfFole");
	                	}
	                } 
	            	
	            }
	            br.close();   
		}
		catch (Exception e) {
            System.out.println("Error in FileWriter !!!");
            e.printStackTrace();
        } finally {
             
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
             
        }
		
	      	  
	}
	
	

	//Processes (.nodes.geo) file and keeps the nodes belong to the specified continent or country.
	private void splitCountry(String continent, String country, String Src, String Des) {
	
		int j;
		//int linenum = 0;
		String line;
        String[] strArgs;
        FileWriter fileWriter = null;
        String NEW_LINE_SEPARATOR = "\n";
        
		try {
				fileWriter = new FileWriter(Des);
				LineNumberReader br = new LineNumberReader(new FileReader(Src));
	            
				while(((line = br.readLine())!= null)) {
	            	
					//linenum++;
					
	            	if (line.startsWith("#")) 
	            		continue;
	            	
	            	strArgs = line.split("\\s+");
	            	j = strArgs.length;
	                if (j>3) {
	                	
	                	if (country.compareTo("") == 0) {
	                		
	                		if (strArgs[2].compareTo(continent) == 0) {
	                			fileWriter.append(line);
                				fileWriter.append(NEW_LINE_SEPARATOR);
                				}
	                	}
	                	else {
	                		
	                		if ((strArgs[3].compareTo(country) == 0)&&(strArgs[2].compareTo(continent) == 0)) {
	                			
		                		fileWriter.append(line);
		                		fileWriter.append(NEW_LINE_SEPARATOR);
		                		
		                	}
		                	else {
		                	
		                	}
	                	} 
	                }
				}
	            br.close();   
		}
		catch (Exception e) {
            System.out.println("Error in FileWriter !!!");
            e.printStackTrace();
        } finally {
             
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
             
        }
		
		
	}

//---------------------------------------------------------------------------------------

	//additional functionality for future usage
	//find the city for each 'Latitude and Longitude' pair in the input dataset
	//uses reverseGeocoding API provided by //https://www.daniel-braun.com/
	private void findNodesinCitiesFromCoordinate(String country, String city) {
		int j;
		Address a;
		int zoom = 18;
		double lat, lon;
		String Sr = "/topo.nodes.geo";
		String Ds = "/topo.nodes.geo.csv";
        String line;
        String[] strArgs;
        String stateCountyCity="";
        FileWriter fileWriter = null;
        String NEW_LINE_SEPARATOR = "\n";
        String COMMA_DELIMITER = ",";
		try {
			fileWriter = new FileWriter(Ds);
			String FILE_HEADER2 = "Node,City,Country";
			fileWriter.append(FILE_HEADER2.toString());
			fileWriter.append(NEW_LINE_SEPARATOR);
			
			LineNumberReader br = new LineNumberReader(new FileReader(Sr));
	           
	            
	            while((line = br.readLine())!= null) {
	            	if (line.startsWith("#")) 
	            		continue;
	            	
	            	
	            	    int lineNumber = br.getLineNumber();
	            	    if (lineNumber >= 19 && lineNumber <= 29) {
	                
	                strArgs = line.split("\\s+");
	                j = strArgs.length;
	                if (j>3) {
		                lat = Double.parseDouble(strArgs[j-2]);
		                lon = Double.parseDouble(strArgs[j-1]);
		                
		                fileWriter.append(strArgs[1]);
		                fileWriter.append(COMMA_DELIMITER); 
		                
		                System.out.println("line: "+lineNumber);
					
				    if (strArgs[5].compareTo("***") ==0) {
				    	//create instance with default zoom level (18)
						NominatimReverseGeocodingJAPI nominatim1 = new NominatimReverseGeocodingJAPI(zoom); a = nominatim1.getAdress(lat, lon); //returns Address object for the given position
					    System.out.println(a);
					    stateCountyCity= a.getState()+" "+a.getCounty()+" "+a.getCity();
					    fileWriter.append(stateCountyCity);
					    
				    }
				    else {
				    	stateCountyCity="";
				    	for (int k=5;k<=(j-3);k++)
				    		stateCountyCity=stateCountyCity+" "+strArgs[k];
				    		fileWriter.append(stateCountyCity);
				    }
				    //fileWriter.append(a.getCountry()); 
				    //https://stackoverflow.com/questions/14155049/iso2-country-code-from-country-name
				    //String code =  CountryCode.findByName("Switzerland").get(0).name();
				    
				    fileWriter.append(COMMA_DELIMITER); 
				    fileWriter.append(strArgs[3]);
				    fileWriter.append(COMMA_DELIMITER); 
				    fileWriter.append(NEW_LINE_SEPARATOR);
		            
		
	            }
	            else {
	                	System.out.println("invalid line");
	            }
		            //System.out.println("CSV file was created successfully !!!");
		       
	         } 
	            }
	            br.close();   
		}
		catch (Exception e) {
            System.out.println("Error in CsvFileWriter !!!");
            e.printStackTrace();
        } finally {
             
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
             
        }
		
	}

	//additional methods
	//print specified number of lines of 'sPath' file to the console
	private void printFiletoConsole (String sPath, int numLines) {
		int i = 0;
		String line;
		 try {
				
				LineNumberReader br = new LineNumberReader(new FileReader(sPath));
		           
	            while (i < numLines) {
	            	line=br.readLine();
	            	System.out.println(line);
	            	i++;
	           }
           br.close();   
		}
		catch (Exception e) {
            System.out.println("Error in FileReader !!!");
            e.printStackTrace();
        }
	
		
	}
		
	//print specified number of lines of 'sPath' file to to 'dPath' file
	private static void printtoFile (String sPath, String dPath, int numLines) {
		int i = 0;
		String line1;
		FileWriter fileWriter = null;
	    String NEW_LINE_SEPARATOR = "\n";
		 try {
				fileWriter = new FileWriter(dPath);
				
				LineNumberReader br = new LineNumberReader(new FileReader(sPath));
		           
		            while (i < numLines) {
		            	line1=br.readLine();
		            	fileWriter.append(line1);
		                fileWriter.append(NEW_LINE_SEPARATOR);
		                i++;
		            }
           br.close();   
		}
		 catch (Exception e) 
			{
	                    System.out.println("Error in FileWriter !!!");
	                    e.printStackTrace();
	        } 
			finally {
	                     
	                try {
	                    fileWriter.flush();
	                    fileWriter.close();
	                } catch (IOException e) {
	                    System.out.println("Error while flushing/closing fileWriter !!!");
	                    e.printStackTrace();
	                }
	        }
		
	}

	//count the number of lines for 'filename'
	private static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];

	        int readChars = is.read(c);
	        if (readChars == -1) {
	            return 0;
	        }

	        int count = 0;
	        while (readChars == 1024) {
	            for (int i=0; i<1024;) {
	                if (c[i++] == '\n') {
	                    ++count;
	                }
	            }
	            readChars = is.read(c);
	        }

	        // count remaining characters
	        while (readChars != -1) {
	            for (int i=0; i<readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	            readChars = is.read(c);
	        }

	        return count == 0 ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
	
	//create a directory
	private void mkdir(){
	
	      //Creating a File object
	      File file = new File(Spath+"destDir");
	      //Creating the directory
	      boolean bool = file.mkdir();
	      if(bool){
	         System.out.println("Destination Directory created successfully");
	         //setDpath(file.toString());
	      }else{
	         System.out.println("Sorry couldn’t create destination directory");
	      }
	}
}
