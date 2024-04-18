package EdgeEPOS.City;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.snatik.polygon.Point;
import com.snatik.polygon.Polygon;
import com.snatik.polygon.Polygon.Builder;

import EdgeEPOS.Setting.Constants;


/**
 * @author znb_n
 *	this class reads the input parameters from .Json file and creates a city with those parameters
 *  it also sets the variables in the Constants class
 */
public class City {

		
	    public static Point[] points;
	    
	    public static int CoreRouterDistanceModifier; 
		public static int EdgeRouterDistanceModifier; 
		public static boolean found;
	    private static Polygon poly;//city as a polygon

		//public static int[] NUM_SMART_THINGS;
	    
	 
	    /**
		 * get the info of a city from JSON input file that includes the general info for all test cities
		 * @param cityName of the city
	     * @param cityconfigfile 
	     * @param nUM_SMART_THINGS 
		 * @param cityfile JSON input file
		 * @return boolean status of city creation
		 */
	    public static boolean initializeCity(String cityName, String cityconfigfile, int[] nUM_SMART_THINGS) 
	    {
	        int i = 0;
			JSONParser parser = new JSONParser();
			found = false;
			String cityname = "";
			
		    
			try {
		        //String path = new File(cityfile).getAbsolutePath();
		        Object obj = parser.parse(new FileReader(cityconfigfile));
		        JSONArray jsonObjects =  (JSONArray) obj;

		        for (Object o : jsonObjects) {
		            JSONObject jsonObject = (JSONObject) o;
		           
		            cityname = (String) jsonObject.get("NAME");
		            
		            if (cityname.compareTo(cityName) == 0) {
		            	
		            	//path2net = (String)jsonObject.get("ROAD_NETWORK");
			          
			            //read Cartesian boundary points
			            JSONArray jsonArrayX= (JSONArray) jsonObject.get("Xpoints");
			            JSONArray jsonArrayY= (JSONArray) jsonObject.get("Ypoints");
			            
			            points = new Point[jsonArrayY.size()];
			            
			            for(i=0; i<jsonArrayY.size(); i++){
			            	points[i] = new Point((Double)(jsonArrayX.get(i)),(Double)(jsonArrayY.get(i)));
			            }
			           
		            //pay attention to the assignments of core edge and cloud.....MaxAndMin.AP_COVERAGE = k;
					Constants.MAX_X = (int) (long) jsonObject.get("MAX_X");
					Constants.MIN_X = (int) (long) jsonObject.get("MIN_X");
					Constants.MIN_Y = (int) (long) jsonObject.get("MIN_Y");
					Constants.MAX_Y = (int) (long) jsonObject.get("MAX_Y");
					Constants.AREA = (int) (long) jsonObject.get("AREA");
					Constants.NUM_BACKBONE_ROUTERS = (int) (long) jsonObject.get("CORE_ROUTERS");
			 		Constants.NUM_EDGE_ROUTERS = (int) (long) jsonObject.get("EDGE_ROUTERS"); 
			 		Constants.numNodes = Constants.NUM_BACKBONE_ROUTERS + Constants.NUM_EDGE_ROUTERS;
			 		Constants.AP_COVERAGE = (int) (long) jsonObject.get("AP_COVERAGE");
			        Constants.CLOUDLET_COVERAGE = (int) (long) jsonObject.get("CLOUDLET_COVERAGE");
			 	    
			 		JSONArray jsonArrayst = (JSONArray) jsonObject.get("NUM_SMART_VEHICLES");
			 		//nUM_SMART_THINGS = new int[jsonArrayst.size()];
			    	
			 		for (i = 0; i < jsonArrayst.size(); i++) {
			 		    Object element = jsonArrayst.get(i);
			 		    if (element instanceof Long) {
			 		        nUM_SMART_THINGS[i] = ((Long) element).intValue();
			 		    } else if (element instanceof Integer) {
			 		        nUM_SMART_THINGS[i] = (int) element;
			 		    } else {
			 		        // Handle the case when the element has an unexpected data type.
			 		    }
			 		   System.out.println("numst["+i+"]: "+nUM_SMART_THINGS[i]);
				        
			 		}
			 		
			 		//for(i=0; i<jsonArrayst.size(); i++){
			 			//nUM_SMART_THINGS[i] = (int) jsonArrayst.get(i);
			 			//System.out.println("numst: "+nUM_SMART_THINGS[i]);
				        
			 		//}
					//Constants.base_Path = new File("").getAbsolutePath();//G:\MobFogSim-master\MobFogSim-master
			        //Constants.TAU = TAU;
			        //Constants.TRAFFIC_CHANGE_INTERVAL = 15 * 60;
			        //int q = Constants.TAU / Constants.TRAFFIC_CHANGE_INTERVAL; // the number of times that traffic changes between each run of the method. HERE 1
			       
			        //in the case of non fixed locations the following parameters are required for randomly generated nodes:
		            //(int) (long) jsonObject.get("CoreRouterDistanceModifier"), 
            		//(int) (long) jsonObject.get("EdgeRouterDistanceModifier"),
            		
		           
            		found = true;
            		break;
		            }		            
		        }
		        
		        if (found == false)
		        	 System.out.println("city does not exist in the config file");
		        else
		        	System.out.println("Area "+cityname+" is configured"+" with "+Constants.numNodes+" total number of servers.");
	                
		        
		    }
		        catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } catch (ParseException e) {
		            e.printStackTrace();
		        }
			
	    return found;
			
		}

		
	     
		/**
		 * creates a polygon from the area we want to study using Cartesian coordinates from the input JSON file
		 * 
		 */
	    public static void makeCityPolygon() {
			int i=0 ;
			
			
			Builder builder = Polygon.Builder();
			for (i = 0; i <  City.getPointSize(); i++) {
			    builder = builder.addVertex(City.getPoint(i));//reading boundary Cartesian points
			    //System.out.println("i: "+i+" x "+City.getPoint(i).x+" y "+City.getPoint(i).y);
			}
			poly = builder.build();
			//System.out.println("City created from "+i+" points.");
			
	     }
	    
	    /**
		 * calculates the area of the polygon using its vertices
		 * @return returns the value of area
		 */
	    /*
		public static double area() {
			 double sum = 0;
			    for (int i = 0; i < City.points.length ; i++)
			    {
			      if (i == 0)
			      {
			        //System.out.println(points[i].x + "x" + (points[i + 1].y + "-" + points[points.length - 1].y));
			        sum += City.points[i].x * (City.points[i + 1].y - City.points[City.points.length - 1].y);
			      }
			      else if (i == City.points.length - 1)
			      {
			        //System.out.println(points[i].x + "x" + (points[0].y + "-" + points[i - 1].y));
			        sum += City.points[i].x * (City.points[0].y - City.points[i - 1].y);
			      }
			      else
			      {
			        //System.out.println(points[i].x + "x" + (points[i + 1].y + "-" + points[i - 1].y));
			        sum += City.points[i].x * (City.points[i + 1].y - City.points[i - 1].y);
			      }
			    }

			    double area = 0.5 * Math.abs(sum);
			    return area;
		}
	     */
		/**
		 * checks whether a point is located inside the polygon. 
		 * @param p input point as a coordinate
		 * @return returns true if is inside, false otherwise
		 */
		public static boolean isInside(Point p) {
			//System.out.println("edge router  "+p.x+" "+p.y);
			return poly.contains(p);
			
		}

		public static Point getPoint(int i) {
			return points[i];
		}
		public static Point[] getPoints() {
			return points;
		}
		public static int getPointSize() {
			return points.length;
		}
		
		
			   
	}
