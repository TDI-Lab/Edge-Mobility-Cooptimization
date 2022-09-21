package EdgeEPOS.City;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.snatik.polygon.Point;
import com.snatik.polygon.Polygon;
import com.snatik.polygon.Polygon.Builder;

import EdgeEPOS.Setting.Constants;

/**
 * 
 * @author rooyesh
 * This class contains all the methods and parameters related to the the city construction
 */

public class CityMapOld {

	private Polygon poly;//city as a polygon
    private Point[] points;//city vertices as the points of the polygon
    
    /**
     * makes a city as a polygon
     * @param city the name of the city
     */
    public CityMapOld (String city){
		makePolygon(city);
	}
	
	/**
	 * creates a polygon using vertices' coordinates saved in a file 
	 * @param city is the name of the city
	 */
    
	private void makePolygon(String city) {
		
		String City_Points_BASE_ADDRESS = "G:\\MobFogSim-master\\MobFogSim-master\\src\\EdgeEPOS\\CityMaps\\RouterPositions-"+city+".txt";
		
		int numOfPoints = getCityInfo(city);
		
		if (numOfPoints == -1) {
			System.out.println("invalid city: "+city);
			return;
		}
		
		points = new Point[numOfPoints];
		
		String line;
		String [] strArgs;
		int j = 0, i = 0;

		//reading boundary points:
		try (BufferedReader br = new BufferedReader(new FileReader(City_Points_BASE_ADDRESS))) {
        		while ((line = br.readLine()) != null) {
			
					if (line.startsWith("#")) 
		        		continue;
					strArgs = line.split(",");
		        	j = strArgs.length;
		           	
		        	if ((j > 1) && (i < points.length)) {
		        		points[i] = new Point((int) Double.parseDouble(strArgs[0]),(int) Double.parseDouble(strArgs[1]));
		        		i++;
		        	}
		        	else {
		        		System.out.println("invalid points or additional points"+i);
		        		continue;
		        	}
				}
				Builder builder = Polygon.Builder();
	
				for (i = 0; i <  points.length; i++) {
				    builder = builder.addVertex(points[i]);
				}
			
			poly = builder.build();
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("City map created from "+i+ " map points");
		
	}
	
	
	/**
	 * get the info of a city from .csv input file that includes the general info for all cities
	 * @param city name of the city
	 * @return number of vertices of the city
	 */
	private int getCityInfo(String city) {
		
		       String line = "";
	           String cvsSplitBy = ",";
	           int i = 1;
	           int citypoint = -1;
	           String[] input; 

	    try (BufferedReader br = new BufferedReader(new FileReader(Constants.base_Path))) {
	    	while ((line = br.readLine()) != null) {
		
	                input = line.split(cvsSplitBy);
	                
	                if (input.length>1) {
	                    if (input[1].compareTo(city) == 0) {
	                    	citypoint = Integer.parseInt(input[8]);
	                    	break;
	                    }
	                }
	    	}
	    }
	    catch (IOException e) {
	            e.printStackTrace();
	    }  
	    
		
		return citypoint;
		
		
	}

	/**
	 * checks whether a point is located inside the polygon. 
	 * @param p input point as a coordinate
	 * @return returns true if is inside, false otherwise
	 */
	public boolean isInside(Point p) {
		return poly.contains(p);
		
	}
	
	/**
	 * calculates the area of the polygon using its vertices
	 * @return returns the value of area
	 */
	public double area() {
		 double sum = 0;
		    for (int i = 0; i < points.length ; i++)
		    {
		      if (i == 0)
		      {
		        //System.out.println(points[i].x + "x" + (points[i + 1].y + "-" + points[points.length - 1].y));
		        sum += points[i].x * (points[i + 1].y - points[points.length - 1].y);
		      }
		      else if (i == points.length - 1)
		      {
		        //System.out.println(points[i].x + "x" + (points[0].y + "-" + points[i - 1].y));
		        sum += points[i].x * (points[0].y - points[i - 1].y);
		      }
		      else
		      {
		        //System.out.println(points[i].x + "x" + (points[i + 1].y + "-" + points[i - 1].y));
		        sum += points[i].x * (points[i + 1].y - points[i - 1].y);
		      }
		    }

		    double area = 0.5 * Math.abs(sum);
		    return area;
	}

}

