package caida;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import java.io.File;

//https://www.daniel-braun.com/ java -jar reverseGeocoding.jar -lat 57.1653392 -lon -2.1056118


public class EmuProcess {
	
	static ConcurrentHashMap<String, String> hash_map1 = new ConcurrentHashMap<String, String>(); 
	static ConcurrentHashMap<String, String> hash_map2 = new ConcurrentHashMap<String, String>(); 
	static ConcurrentHashMap<String, String> hash_map3 = new ConcurrentHashMap<String, String>(); 
	static ConcurrentHashMap<String, String> hash_map5 = new ConcurrentHashMap<String, String>(); 
	
	
	static MultiValuedMap<String, String> Linkmap = new ArrayListValuedHashMap<>();
	
	public static final char CSV_SEPARATOR = ','; // it could be a comma or a semi colon
	public static final String CSV_SEPARATOR1 = ";"; // it could be a comma or a semi colon
   

	private static FileInputStream input;
	
	public static int Process(){
		
		processLines();
		EmuFogProcessor();
		DrawGraph();
		return 0;
		 
	}
	
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

	
}
