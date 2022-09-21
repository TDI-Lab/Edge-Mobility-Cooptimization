package EdgeEPOS.City;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.fog.vmmobile.constants.MaxAndMin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.snatik.polygon.Point;
import com.snatik.polygon.Polygon;
import com.snatik.polygon.Polygon.Builder;

import EdgeEPOS.Setting.Constants;

/**
 * @author rooyesh
 *
 */
public class City {

		private static String name;
	    //public static int MAX_X, MAX_Y, MIN_X, MIN_Y, BACKBONE_ROUTERS, EDGE_ROUTERS;//coordinates with respect to netedit
	    public static Point[] points;
	    private static double area;
	    public static int CoreRouterDistanceModifier; 
		public static int EdgeRouterDistanceModifier; 
		public static boolean found;
	    private static Polygon poly;//city as a polygon
	    
	    //@SuppressWarnings("unchecked")
	    /**
		 * get the info of a city from Json input file that includes the general info for all cities
		 * @param city name of the city
		 * @return number of vertices of the city
		 */
	    public static boolean initializeCity(String cityName, String cityfile) 
	    {
	        int i = 0;
			JSONParser parser = new JSONParser();
			found = false;
			String cityname = "";
			String path2net = "";
		    
			try {
		        String path = new File(cityfile).getAbsolutePath();
		        Object obj = parser.parse(new FileReader(path));
		        JSONArray jsonObjects =  (JSONArray) obj;

		        for (Object o : jsonObjects) {
		            JSONObject jsonObject = (JSONObject) o;
		           
		            cityname = (String) jsonObject.get("name");
		            
		            if (cityname.compareTo(cityName) == 0) {
		            	
		            	name = cityname;
			            path2net = (String)jsonObject.get("RoadNetwork");
			          
			            //int pointsNumber = (int) (long) jsonObject.get("pointsNumber");
		            	
			            //read Cartesian boundary points
			            JSONArray jsonArrayX= (JSONArray) jsonObject.get("Xpoints");
			            JSONArray jsonArrayY= (JSONArray) jsonObject.get("Ypoints");
			            
			            points = new Point[jsonArrayY.size()];
			            
			            for(i=0; i<jsonArrayY.size(); i++){
			            	points[i] = new Point((Double)(jsonArrayX.get(i)),(Double)(jsonArrayY.get(i)));
			            }
			            
			           
		            SetAtt((int) (long) jsonObject.get("max_x"), (int) (long) jsonObject.get("max_y"), 
		            		(int) (long) jsonObject.get("min_x"),(int) (long) jsonObject.get("min_y"), 
		            		(int) (long) jsonObject.get("back_routers"), (int) (long) jsonObject.get("edge_routers"),
		            		(int) (long) jsonObject.get("NumSmartVehicles"),
		            		(int) (long) jsonObject.get("area"),
		            		(int) (long) jsonObject.get("AP_COVERAGE"),
		            		(int) (long) jsonObject.get("CLOUDLET_COVERAGE"), 
		            		((String) jsonObject.get("APLocation")),
		            		((String) jsonObject.get("CoreRouterLocation")),
		            		((String) jsonObject.get("LatLocation"))
		            		);    
		           //in the case of fixed locations are not required:
		            //(int) (long) jsonObject.get("CoreRouterDistanceModifier"), 
            		//(int) (long) jsonObject.get("EdgeRouterDistanceModifier"),
            		
		           
            		found = true;
            		break;
		            }		            
		        }
		        
		        if (found == false)
		        	 System.out.println("city does not exist in the config file");
		        else
		        	System.out.println("City: "+cityname+" is configured"+" with net path "+path2net);
	                
		        
		    }catch (FileNotFoundException e) {
		            e.printStackTrace();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } catch (ParseException e) {
		            e.printStackTrace();
		        }
			
			
			
		    return found;
	    }
	    
	    
		public static void SetAtt(int max_x1, int max_y1, int min_x1, int min_y1, int backbone_routers1, int edge_routers1, int NumSmartThings, double area1, int k, int l, String APs, String CRs, String CloudletLat) {
			
			
			//pay attention to the assignments of core edge and cloud.....
			 //MaxAndMin.AP_COVERAGE = k;
			//BACKBONE_ROUTERS = backbone_routers1;
			//EDGE_ROUTERS = edge_routers1;
			area = area1;
			
			//pay attention to other fields also:
			Constants.MAX_X = max_x1 ;
			Constants.MIN_X = min_x1;
			Constants.MIN_Y = min_y1;
			Constants.MAX_Y = max_y1;
			
			Constants.APFile = APs;
			Constants.CRFile = CRs;
			Constants.LatFile = CloudletLat;
			//Constants.ARLoadFile = ServiceFi;
			// TODO Auto-generated method stub
	    	Constants.area = area1;
			Constants.numCloudServers = 1;//
	    	//Constants.numCloudServers = City.BACKBONE_ROUTERS;//5
	        Constants.numFogNodes = edge_routers1 + backbone_routers1;//? + cloud??10
	        
	        
	 		Constants.BACKBONE_ROUTERS = backbone_routers1;
	 		Constants.EDGE_ROUTERS = edge_routers1; 
	 		Constants.numSmartThings=NumSmartThings;
	        Constants.numServices = 30;
	        Constants.AP_COVERAGE = k;
	        Constants.CLOUDLET_COVERAGE = l;
	 	    Constants.base_Path = new File("").getAbsolutePath();//G:\MobFogSim-master\MobFogSim-master
	        //Constants.TAU = TAU;
	        Constants.TRAFFIC_CHANGE_INTERVAL = 15 * 60;
	       
	        
	        //int q = Constants.TAU / Constants.TRAFFIC_CHANGE_INTERVAL; // the number of times that traffic changes between each run of the method. HERE 1
	        
	        //System.out.println("st "+Constants.numSmartThings+" "+NumSmartThings);
		}

		
	     
		/**
		 * creates a polygon using vertices' coordinates saved in json file read in City class
		 * @param city is the name of the city
		 */
	    public static void makeCityPolygon() {
			int i=0 ;
			
			//reading boundary Cartesian points:
			Builder builder = Polygon.Builder();
			for (i = 0; i <  City.getPointSize(); i++) {
			    builder = builder.addVertex(City.getPoint(i));
			    //System.out.println("i: "+i+" x "+City.getPoint(i).x+" y "+City.getPoint(i).y);
			}
			poly = builder.build();
			System.out.println("City created from "+i+" points.");
			
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
		
		public static String getName() {
	        return name;
	    }

		/*
		 * public static int getMax_x() { return MAX_X; }
		 * 
		 * public static int getMax_y() { return MAX_Y; }
		 * 
		 * public static int getMin_x() { return MIN_X; }
		 * 
		 * public static int getMin_y() { return MIN_Y; }
		 * 
		 * public static int getBackbone_routers() { return BACKBONE_ROUTERS; }
		 * 
		 * public static int getEdge_routers() { return EDGE_ROUTERS; }
		 */

		public static Point getPoint(int i) {
			return points[i];
		}
		public static Point[] getPoints() {
			return points;
		}

		public static int getPointSize() {
			return points.length;
		}
		public static double getArea() {
			return area;
		}
		
			   
	}
