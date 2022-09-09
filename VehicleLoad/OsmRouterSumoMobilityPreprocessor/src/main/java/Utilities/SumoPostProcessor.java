package Utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.graphstream.algorithm.generator.BarabasiAlbertGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;


import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import org.graphstream.algorithm.Toolkit.*;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.algorithm.generator.WattsStrogatzGenerator;


import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

//https://www.daniel-braun.com/ java -jar reverseGeocoding.jar -lat 57.1653392 -lon -2.1056118


public class SumoPostProcessor {
	
	static ConcurrentHashMap<String, String> hash_map1 = new ConcurrentHashMap<String, String>(); 
	static ConcurrentHashMap<String, String> hash_map2 = new ConcurrentHashMap<String, String>(); 
	static ConcurrentHashMap<String, String> hash_map3 = new ConcurrentHashMap<String, String>(); 
	static ConcurrentHashMap<String, String> hash_map5 = new ConcurrentHashMap<String, String>(); 
	
	
	static MultiValuedMap<String, String> Linkmap = new ArrayListValuedHashMap<>();
	
	static ArrayList<Travel> Vehicles = new ArrayList<Travel>();
	static ArrayList<TimeStamp> Simulation = new ArrayList<TimeStamp>(3600);
	static CPoint[] NodesLoc = new CPoint[468];//473
	private static final String CSVFILE = "D:\\osm.csv";
	private static final String APLocation = "G:\\MobFogSim-master\\MobFogSim-master\\src\\EdgeEPOS\\City\\config\\MunichCartSelectedAP.csv";
	static double maxDisAPToVeh = 0;
	static int numOfTsWithVehicle;
	public static final char CSV_SEPARATOR = ','; // it could be a comma or a semi colon
	public static final String CSV_SEPARATOR1 = ";"; // it could be a comma or a semi colon

	private static FileInputStream input;
	
	public static void main(String[] args) {
		
		//HashSet<String> compset=new HashSet<String>();  
		
		/*preprocess osm output files: 
		 * in osm.sumocfg :
		 * fcd output: Floating Car Data includes name, position, angle and type for every vehicle
		 * All output files written by SUMO are in XML-format by default. However, with the python tool xml2csv.py 
		 * you can convert any of them to a flat-file (CSV) format 
		 * C:\Program Files (x86)\Eclipse\Sumo\tools\xml
		 * overall csv to individual ones
		 */
		
		String file1 = "H:\\project2\\MyPlots\\1hour-workloadAP\\VehPerAP.dat";
		String file2 = "H:\\project2\\MyPlots\\1hour-workloadAP\\SortedVehPerAP.dat";
		String file3 = "H:\\project2\\MyPlots\\1hour-workloadAP\\VehiclesInSelectedArea.csv";
		File file4 = new File ("H:\\project2\\MyPlots\\1hour-workload\\VehiclesInSelectedArea.csv");
		
		OSMOutProcessor();
		
		WriteTotals();
		VehiclesPerAP(0,file1);
		VehiclesPerAP(1,file2);
		
		//System.out.println("Number of Lines "+lineNum(file4));
		VehiclesAndTravels(file3);
		PrintTravels();
		
		System.out.println("Max distance between APs and Vehicles during the whole simulation: "+maxDisAPToVeh);
		
		
		//processLines();
		//EmuFogProcessor();
		//DrawGraph();
		 
	}
	
	
	private static void PrintTravels() {
		// TODO Auto-generated method stub
		System.out.println("Num of vehicles: "+Vehicles.size());
		String str = "";String str1 = "";
		try {
			
			
		      PrintWriter out = new PrintWriter(new FileWriter("H:\\project2\\MyPlots\\1hour-workloadAP\\VehTravels.dat"));
		      BufferedWriter out1 = new BufferedWriter(new FileWriter("H:\\project2\\MyPlots\\1hour-workloadAP\\VehTravels.csv"));
				
				for (int j = 0; j<Vehicles.size() ; j++) {
					str = j+ " "+Vehicles.get(j).VehicleId+" "+Vehicles.get(j).StartTime+" "+Vehicles.get(j).FinishTime;
					str1 = j+ ","+Vehicles.get(j).VehicleId+","+Vehicles.get(j).StartTime+","+Vehicles.get(j).FinishTime;
					
					out.println(str);

					out1.append(j+"").append(CSV_SEPARATOR)	//timestep_time
					.append(Vehicles.get(j).VehicleId).append(CSV_SEPARATOR)			//vehicle_angle	
					.append(Vehicles.get(j).StartTime+"").append(CSV_SEPARATOR)			//vehicle_x;	
					.append(Vehicles.get(j).FinishTime+"").append(CSV_SEPARATOR)			//vehicle_y	
					.append(System.lineSeparator());
					
				}
		
				out.close();
				out1.close();
		}
		catch (IOException e) {
	      System.out.print("Error: " + e);
	      System.exit(1);
    }
	}


	private static void VehiclesAndTravels(String CSVfile) {
		// TODO Auto-generated method stub
		HashSet<String> set=new HashSet<String>();  
		double MeanTravelTime = 0;
		double SumTravelTime = 0;
		String line,line1;
		int NumVehciles = -1;
		int count;
		int lastTimeStamp = 0;
		String CSV_SEPARATOR = ",";
		 
		try {
			LineNumberReader csvReader = new LineNumberReader(new FileReader(CSVfile));
			//line = csvReader.readLine();
			while ((line = csvReader.readLine()) != null) {
				String[] data = line.split(CSV_SEPARATOR);
				//System.out.println(line);
				if (data.length<3)
			    	continue;
				
				if (!set.contains(data[2])) {
					NumVehciles++;
					String vehicle = data[2];
			    	int StartTime =(int) (Double.parseDouble(data[0]));
			    	Travel t = new Travel(vehicle, StartTime);
			    	Vehicles.add(NumVehciles,t);
			    	
			    	BufferedWriter csvWriter = new BufferedWriter(new FileWriter("H:\\project2\\MyPlots\\1hour-workloadAP\\vehicles\\"+NumVehciles+".csv"));
					csvWriter.append(data[0]).append(CSV_SEPARATOR)	//timestep_time
					.append(data[1]).append(CSV_SEPARATOR)			//vehicle_angle	
					.append(data[3]).append(CSV_SEPARATOR)			//vehicle_x;	
					.append(data[4]).append(CSV_SEPARATOR)			//vehicle_y	
					.append(data[5]).append(CSV_SEPARATOR)			//vehicle_speed	
					.append(data[2]).append(CSV_SEPARATOR)			//id
					.append(data[6]).append(CSV_SEPARATOR)
					.append(System.lineSeparator());
					
					count = csvReader.getLineNumber();
					LineNumberReader csvReader1 = new LineNumberReader(new FileReader(CSVfile));
					
					while ((line1 = csvReader1.readLine()) != null) {
						if(csvReader1.getLineNumber() <= count) {
							 continue;
						    	
						}
						data = line1.split(CSV_SEPARATOR);
					  
					
					    if (data[2].equals(vehicle)) {
					    	
					    	csvWriter.append(data[0]).append(CSV_SEPARATOR)	//timestep_time
							.append(data[1]).append(CSV_SEPARATOR)			//vehicle_angle	
							.append(data[3]).append(CSV_SEPARATOR)			//vehicle_x;	
							.append(data[4]).append(CSV_SEPARATOR)			//vehicle_y	
							.append(data[5]).append(CSV_SEPARATOR)			//vehicle_speed	
							.append(data[2]).append(CSV_SEPARATOR)			//id
							.append(data[6]).append(CSV_SEPARATOR)
							.append(System.lineSeparator());
					    	
							lastTimeStamp = (int) (Double.parseDouble(data[0]));
					    }
					    
					}
					Vehicles.get(NumVehciles).FinishTime = lastTimeStamp;
				    set.add(vehicle);
					csvWriter.flush();
					csvWriter.close();
					
					SumTravelTime += (lastTimeStamp - StartTime);
					System.out.println("id "+Vehicles.get(NumVehciles).VehicleId+" "+Vehicles.get(NumVehciles).StartTime+" "+Vehicles.get(NumVehciles).FinishTime);
					//System.out.println("sum "+SumTravelTime);
					//System.out.println("");
				}
				
			}
			csvReader.close();
		}
			catch (Exception e) {
		        System.out.println("Error in FileReader !!!");
		        e.printStackTrace();
		    }       
		System.out.println("Sum of Travel Time of All Vehicles: "+SumTravelTime);
		System.out.println("Number of Vehicles: "+NumVehciles+" size "+Vehicles.size());
		System.out.println("Mean Travel Time: "+SumTravelTime/NumVehciles);
	}


	private static void WriteTotals() {
		// TODO Auto-generated method stub
		System.out.println("Num of timestamps: "+Simulation.size());
		String str = "";
		try {
		      PrintWriter out = new PrintWriter(new FileWriter("H:\\project2\\MyPlots\\1hour-workloadAP\\VehPerTS.dat"));
		      
				for (int j = 0; j<=numOfTsWithVehicle ; j++) {
					if (Simulation.get(j).valid == true)
						str = (int)Simulation.get(j).getTime()+" "+Simulation.get(j).getTs().size();
					else
						str = (int)Simulation.get(j).getTime()+" "+0;
					out.println(str);
				}
		
				out.close();
		}
		catch (IOException e) {
	      System.out.print("Error: " + e);
	      System.exit(1);
    }
	}
	/**
	 * output format
	 * 0 1 1
	 * 0 2 3
	 * 0 3 5
	 * 0 5 8
	 * 
	 * 1 1 7
	 * 1 2 2
	 * 1 3 1
	 * 1 4 5
	 * @param file2 
	 */
	private static void VehiclesPerAP(int sorted, String file2) {
		// TODO Auto-generated method stub
		int maxnumAP = 0;
		int NumAP;
		try {
		      PrintWriter out = new PrintWriter(new FileWriter(file2));
		      String str = "";
				//System.out.println("Num of vehicles per AP: "); 
				
				if (sorted == 1) {
					for (int i = 0; i< Simulation.size(); i++) 
						if (Simulation.get(i).valid)
							Arrays.sort( Simulation.get(i).AP);
				}
				//empty ones?????
				for (int i = 0; i < Simulation.size(); i++) {
					//System.out.println("i "+i);
					for (int j = 0; j<468; j++) {
						str = "";
						str += (int) Simulation.get(i).time+" ";
						str += j+" ";
						if (Simulation.get(i).valid == true)
							NumAP = Simulation.get(i).AP[j];
						else
							NumAP = 0;
						if (NumAP>maxnumAP)
							maxnumAP = NumAP;
						str += NumAP;
						out.println(str);
					}
					out.println();
				}
				 out.close();
			    }
				catch (IOException e) {
			      System.out.print("Error: " + e);
			      System.exit(1);
			    }
		System.out.print("Max number of vehicles connected to the APs for all TimeStamps: "+maxnumAP+"\n");
		
	}


	/**
	 * process the output of SUMO and provides mobility files for each vehicle
	 */
	private static void OSMOutProcessor() {
		// TODO Auto-generated method stub
		HashSet<String> set=new HashSet<String>();  
		
		ReadAP();
		//String pathToCsv = "C:\\Users\\rooyesh\\sumo\\2020-12-05-19-20-26\\osm.csv";
		String line,line1;
		double Time;
		int i = -1;
		double x, y;
		int ap;
		int [] ap1 = new int [2];
		int count = 1000;
		String CSV_SEPARATOR = ",";
		String file = "H:\\project2\\MyPlots\\1hour-workload\\VehiclesInSelectedArea.csv";
		try {
			//LineNumberReader csvReader = new LineNumberReader(new FileReader(CSVFILE));
			//BufferedWriter csvWriter = new BufferedWriter(new FileWriter("H:\\project2\\MyPlots\\1hour-workload\\VehiclesInSelectedArea.csv"));
			//line = csvReader.readLine();
			//CSV_SEPARATOR1
			
			LineNumberReader csvReader = new LineNumberReader(new FileReader(file));
			BufferedWriter csvWriter = new BufferedWriter(new FileWriter("H:\\project2\\MyPlots\\1hour-workloadAP\\VehiclesInSelectedArea.csv"));
			
			
			while ((line = csvReader.readLine()) != null) {
				String[] data = line.split(CSV_SEPARATOR);
				/*
				if(csvReader.getLineNumber() > count) {
					 break;
				}
			    */
				if (data.length<4)
			    	continue;

			    //file format: 
			    //timestep_time;vehicle_angle;vehicle_id;vehicle_lane;vehicle_pos;vehicle_slope;vehicle_speed;vehicle_type;vehicle_x;vehicle_y
			    //0.00;222.64;70859;-374107261_0;5.10;0.00;8.83;DEFAULT_VEHTYPE;3437.31;10388.98
			    
			    Time = Double.parseDouble(data[0]);
			    int ts =(int) Time;
			    x = Double.parseDouble(data[3]);
				y = Double.parseDouble(data[4]);
				//System.out.println(" x "+x+ " y "+y+ " t "+ts);
				
				if (CityContains(x,y)) {
					ap = ConnectedAP(x,y, ap1);
					
					csvWriter.append(data[0]).append(CSV_SEPARATOR)//timestep_time
					.append(data[1]).append(CSV_SEPARATOR)			//vehicle_angle	 
					.append(data[2]).append(CSV_SEPARATOR)			//vehicle_id
					.append(data[3]).append(CSV_SEPARATOR)			//vehicle_x;vehicle_y	    	
					.append(data[4]).append(CSV_SEPARATOR)			    	
					.append(data[5]).append(CSV_SEPARATOR)			//vehicle_speed	
					.append(ap+"").append(CSV_SEPARATOR)
					.append(System.lineSeparator());
					
					if (!set.contains(data[0])) {
						//System.out.println("set not contains"+" x "+x+ " y "+y+ " i "+i+ " t "+ts+" ap "+ap);
					    	//System.out.println(" ap "+ap);
				    	i++;
				    	if (i<ts) {
				    		for (int k = i; k<ts; k++) {
				    			TimeStamp t1 = new TimeStamp(i);
				    			Simulation.add(k,t1);
				    		}
				    	}
				    	Vehicle vehicle = new Vehicle(data[2],x,y,ap1);
				    	TimeStamp t = new TimeStamp(Time,vehicle);
				    	Simulation.add(ts,t);
				    	//Simulation.get(i).
				    	//int count = csvReader.getLineNumber();
						//LineNumberReader csvReader1 = new LineNumberReader(new FileReader(CSVFILE));
				    	i = ts;
								
						}
				    else 
				    {
				    	//System.out.println("");
				    	//System.out.println("set contains"+" x "+x+ " y "+y+ " i "+i+" t "+ts+" ap "+ap);
				    	Vehicle vehicle = new Vehicle(data[2],x,y,ap1);
				    	Simulation.get(ts).addVehicle(vehicle);   
						}
						
				    set.add(data[0]);
				
			}
			  
			}
			
			csvWriter.flush();
			csvWriter.close();
			csvReader.close();
		           
	    		
		}
		catch (Exception e) {
	        System.out.println("Error in FileReader !!!");
	        e.printStackTrace();
	    }
		numOfTsWithVehicle = i;
		System.out.println("i "+i);
		
	}


	
private static int ConnectedAP(double x, double y, int[] ap1) {
		
	double mindis, dis = 0;
	int i, mini;
	mindis = Math.sqrt((NodesLoc[0].x -x)*(NodesLoc[0].x -x)+(NodesLoc[0].y -y)*(NodesLoc[0].y -y));
	mini = 0;
	//System.out.println(" dis0 "+mindis);
	for(i = 1; i<468; i++) {
		dis = Math.sqrt((NodesLoc[i].x -x)*(NodesLoc[i].x -x)+(NodesLoc[i].y -y)*(NodesLoc[i].y -y));
		//System.out.println(" dis "+dis);
		if (dis < mindis) {
			mindis = dis;
			mini = i;
		}
	}
	//System.out.println(" mindis "+mindis+" mini "+mini);
		ap1[0] = mini;
		ap1[1] = (int) mindis;
		if (mindis>maxDisAPToVeh)// to know the required coverage range of APs. 
			maxDisAPToVeh = mindis;
		return mini;
	}

private static boolean CityContains(double x, double y) {
	// TODO Auto-generated method stub
	if (((13409<x)&&(x<15577))&&((y>14284)&&(y<15725))) 
				return true;
	return false;
	}

private static void ReadAP() {
	// TODO Auto-generated method stub
	 String line;
	 String CSV_SEPARATOR = ","; // it could be a comma or a semicolon
	 int AP = 468;
	 int i = 0;
	 int coordX, coordY;
	 try {
		
		  LineNumberReader csvReader = new LineNumberReader(new FileReader(APLocation)); 
		 
		  while (((line =  csvReader.readLine()) != null)&&(i < AP)) {
			  String[] data = line.split(CSV_SEPARATOR);
	  		  
	  		  if (data.length<1) {
	  			System.out.println("unknown AP location in input file");
	  			  continue;
	  		  }

	  		  	coordX = Integer.parseInt(data[0]);
		  		coordY = Integer.parseInt(data[1]);
		  		NodesLoc[i] = new CPoint(); 
		  		NodesLoc[i].x = coordX;
		  		NodesLoc[i].y = coordY;
		  		i++;
		  }
		  csvReader.close();
		  
	}
	  catch (Exception e) {
	  System.out.println("Error in FileReader !!!"); e.printStackTrace(); 
	  }
	 	
	System.out.println("Number of access points read: "+i);
	}

/*
	if (!set.contains(data[2])) {
			    	String vehicle = data[2];
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
		           
	
/*
	
	/**
	 * process the output file of EmuFog and provide number of any type of nodes in the topology 
	 * including: routers, hosts, switches, links, connections.
	 */
	public static void EmuFogProcessor() {
		String Src = "C:\\Users\\rooyesh\\Downloads\\Compressed\\emufog_2\\emufog\\bin\\out.py";
		int j, i = 0;
		String line;
	    String[] strArgs;
	    
		try {
			LineNumberReader br = new LineNumberReader(new FileReader(Src));
	           
	            while((line = br.readLine())!= null) {
	            	if (line.startsWith("s")) {
		        		
	            	    	strArgs = line.split("\\s+");
			            	j = strArgs.length;
			                if (j>1) {
			                	//i++;
			                	hash_map1.putIfAbsent(strArgs[0],strArgs[0]);//node id without colon
			                	//System.out.println(strArgs[0]);	
			                }
			                else {
			                		
			                }
			                
			            } 
	            	
	            	else if (line.startsWith("r")) {
		        		
			            	strArgs = line.split("\\s+");
			            	//strArgs = line.split("[(,]");
			            	j = strArgs.length;
			                if (j>1) {
			                	hash_map2.putIfAbsent(strArgs[0],strArgs[0]);//node id without colon
			                	//System.out.println(strArgs[0]);
			                	}
			                else {
			                		
			                }
			                
	            	}
	            	
	            	else if (line.startsWith("h")) {
		        		
		            	strArgs = line.split("\\s+");
		            	//strArgs = line.split("[(,]");
		            	j = strArgs.length;
		                if (j>1) {
		                	hash_map3.putIfAbsent(strArgs[0],strArgs[0]);//node id without colon
		                	//System.out.println(strArgs[0]);
		                	}
		                else {
		                		
		                }
		                
	            	}
	            	/*
	            	else if (line.startsWith("c")) {
		        		
			            	strArgs = line.split("\\s+");
			            	//strArgs = line.split("[(,]");
			            	j = strArgs.length;
			                if (j>1) {
			                	//i++;
			                	hash_map5.putIfAbsent(strArgs[0],strArgs[0]);//node id without colon
			                	//System.out.println(strArgs[0]);
			                	}
			                else {
			                		
			                }
		                
	            	}
	            	*/
	            	
	            	else if (line.startsWith("topo.addLink(")) {
		        		
		            	strArgs = line.split("[(,]");
		            	//strArgs = line.split("[(, ]");
		            	j = strArgs.length;
		                if (j>1) {
		                	//i++;
		                	if (strArgs[1].contains("h")||strArgs[2].contains("h")||strArgs[1].contains("c")||strArgs[2].contains("c")) {
		                		//System.out.println("c/s"+line);
		                		continue;
		                	}
		                	else {
		                		String A = strArgs[1].replace(" ","");
		                		String B = strArgs[2].replace(" ","");
		                		if (Linkmap.containsMapping(A,B))
		                			continue;
		                		i++;
		                		Linkmap.put(A,B);
		                		//System.out.println(A+"**"+B);
		                	}
		                }
		                else {
		                		
		                }
	                
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
		System.out.println("num switches: "+hash_map1.size());
		System.out.println("num edge routers:  "+hash_map2.size());
		System.out.println("num edge hosts:  "+hash_map3.size());
		System.out.println("numconnectors:  "+hash_map5.size());
		System.out.println("num links:  "+Linkmap.size());
		
		
	}
	
	public boolean contains(String str, char chr) {
		  return str.indexOf(chr) != -1;
		}
	
	/**
	 * Draw a graph from the output data provided in EmuFogProcessor
	 */
	public static void DrawGraph(){
		
		
		System.out.println("generating nodes.....");
        
        
         Graph graph = new SingleGraph("edge-core");
         //Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
         
         	//adding switches
            for(Map.Entry<String, String> set : hash_map1.entrySet()) 
            	graph.addNode(set.getKey());
            for (Node node : graph) {
                node.setAttribute("ui.label", node.getId());
            }
 
            //adding routers
            for (Map.Entry<String, String> set : hash_map2.entrySet()) 
            	graph.addNode(set.getKey());
            for (Node node : graph) {
                node.setAttribute("ui.label", node.getId());
            }
            
            //adding connectors
            for (Map.Entry<String, String> set : hash_map5.entrySet()) 
            	graph.addNode(set.getKey());
            for (Node node : graph) {
                node.setAttribute("ui.label", node.getId());
            }
            
            System.out.println("generating links.....");
            Iterator<String> mapIterator = Linkmap.keySet().iterator();
    		
    		// iterate over the map
    		while (mapIterator.hasNext()) {
    			String key = mapIterator.next();
    			//System.out.println("key:" + key + ", values=" + Linkmap.get(key));
    			
    			Collection<String> values = Linkmap.get(key);
    			
    			// iterate over the entries for this key in the map
    			for(Iterator<String> entryIterator = values.iterator(); entryIterator.hasNext();) {
    				String value = entryIterator.next();
    				
    				if ((graph.getEdge(key+value) == null) && (graph.getEdge(value+key) == null))
    					graph.addEdge(key+value, key, value,false);
    				

    				//System.out.println("    value:" + value);
    			}
    		
    		}
            
            
            		
            		
            //System.setProperty("org.graphstream.ui", "swing"); 
            graph.display();
        //}
        
	}
	
	public static void processLines() {
		
		String Src = "C:\\Users\\rooyesh\\eclipse-workspace\\CaidaProcessor1\\Caida\\src\\topo.nodes.geo.de";
		//String Src = "C:\\Users\\rooyesh\\eclipse-workspace\\CaidaProcessor1\\Caida\\src\\midar-iff.nodes.as";
		//String Src = "C:\\Users\\rooyesh\\eclipse-workspace\\CaidaProcessor1\\Caida\\src\\midar-iff.links";
		
		
		int i = 0, j;
		int k; int l = 0 ;
		String line;
	    String[] strArgs;
	    
		try {
			LineNumberReader br = new LineNumberReader(new FileReader(Src));
	           
	            while(((line = br.readLine())!= null)&&(l<100000)) {
	            	l++;
	            	System.out.println("line "+line+"\n");
	            	
	            	/*
	            	strArgs = line.split("\\s+");
	            	j = strArgs.length;
	            	if (j>3) {
	            	for (k=0;k<j;k++)
	            		if (strArgs[k].contains("N"))
	            			i++	;
	            	}
	                else {
	                	//System.out.println("!!!");
	                }
	              */  
	            }
	            
	            br.close();    
	    		
		}
		catch (Exception e) {
	        System.out.println("Error in FileReader !!!");
	        e.printStackTrace();
	    }
		finally {
			
		}
		
		System.out.println(" l "+l);
	
	}
	
	
	public static void csvreaderCell() throws Throwable {
		
		try {
		Process a = Runtime.getRuntime().exec("H:\\project2\\geo2cart.py");
		}catch(Exception e) {
	          System.out.println("Exception Raised" + e.toString());
	       }
		
		
		 
	}
	
	public static void pcapProcessor() {
		
		String Src = "G:\\201704121244.pcap";
		
		try {
			
            input = new FileInputStream(Src);
            int content = 0;
            int counter = 0;
            while ((content = input.read()) != -1) {
                System.out.println(counter + ". " + (byte)content);
                counter++;
            }
        } catch (Exception e) {
            System.out.println("Error");
        }
		
			
		
	}
	
		
	public static void countRouters() {
		
		String Src = "C:\\Users\\rooyesh\\eclipse-workspace\\CaidaProcessor\\Caida\\us.man\\topo.links.US.Manhattan";
		int i = 0, j;
		int k;
		String line;
	    String[] strArgs;
	    
		try {
			LineNumberReader br = new LineNumberReader(new FileReader(Src));
	           
	            while((line = br.readLine())!= null) {
	            	strArgs = line.split("\\s+");
	            	j = strArgs.length;
	            	if (j>3) {
	            	for (k=0;k<j;k++)
	            		if (strArgs[k].contains("N"))
	            			i++	;
	            	}
	                else {
	                	//System.out.println("!!!");
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
		
		System.out.println(" n num 2052 "+"new "+i);
		
	}
	
	public static long lineNum(File file){
		long count = 0;
		try {

			// create a new file object
		     // File file = new File("input.txt");

		      // create an object of Scanner
		      // associated with the file
		      Scanner sc = new Scanner(file);

		      // read each line and
		      // count number of lines
		      while(sc.hasNextLine()) {
		        sc.nextLine();
		        count++;
		      }
		      System.out.println("Total Number of Lines: " + count);

		      // close scanner
		      sc.close();
		    } catch (Exception e) {
		      e.getStackTrace();
		    }
		
		return count;
	}

	/*
	public static class SearchTask implements Callable<String> {
	     private String localCounter = "*";
	     private ConcurrentHashMap<String, Integer> mapWords;
	     private String searchWords;

	public SearchTask(String searchWords, ConcurrentHashMap<String, Integer> mapWords) {
	         this.searchWords = searchWords;
	         this.mapWords = mapWords;
	         
	     }
	
	@Override
	public String call() throws Exception {
	    	String [] strArgs;//take care
	    	int j, k ;
	    	k = 2;
			strArgs = searchWords.split("\\s+");
	        j = strArgs.length;
	        String candidateNode;
	        
			if (j > 2){
				 while (k<=j)
			        {
					 int iend = strArgs[k].indexOf(":"); //this finds the first occurrence of ":" 
					 if (iend != -1) 
						 candidateNode= strArgs[k].substring(0 , iend); 
					 
					 candidateNode= strArgs[k]; 
					 if (mapWords.containsKey(candidateNode)){
						 
					 }
					 else {
						 searchWords.replace(strArgs[k], "");
					 }
					 k++;
			        }
				}
		    
		     return searchWords;
	     }        
	
	}
	
*/	
}
