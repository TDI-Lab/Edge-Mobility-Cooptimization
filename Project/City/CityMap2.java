package EdgeEPOS.City;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;

import com.snatik.polygon.Point;
import com.snatik.polygon.Polygon;
import com.snatik.polygon.Polygon.Builder;

import EdgeEPOS.Setting.Constants;

/**
 * 
 * @author rooyesh
 * This class contains all the methods and parameters related to the the city construction
 */

public class CityMap2 {

	private static Polygon poly;//city as a polygon
     
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
		    System.out.println("i: "+i+" x "+City.getPoint(i).x+" y "+City.getPoint(i).y);
		}
		poly = builder.build();
		System.out.print("City map created from "+i+" points, ");
		//System.out.println(" reading APs...");
		//readBS();
        
		
	}
	
    public static void readBS() {
		
		String line;
		String CSV_SEPARATOR = ","; // it could be a comma or a semi colon
		Point p;
					
		try {
			
			  LineNumberReader csvReader = new LineNumberReader(new FileReader(City.getAPLocations())); 
			  line = csvReader.readLine(); 
			  while ((line =  csvReader.readLine()) != null) {
				  String[] data = line.split(CSV_SEPARATOR);
		  		  System.out.println(line); 
		  		  
		  		  if (data.length<1) {
		  			System.out.println("unknown AP location in input file");
		  			  continue;
		  		  }
			    
		  		p = new Point(Double.parseDouble(data[0]),Double.parseDouble(data[1]));
		  		//coordX = Integer. parseInt(data[0]);coordY = Integer. parseInt(data[1]);
		  		System.out.println("x: "+Double.parseDouble(data[0])+"y"+Double.parseDouble(data[1]));
		  		  
		  		  if(isInside(p)) {
		  			System.out.println("inside selected area AP");
			    	
		  		  }
				
			  } 
			  csvReader.close();
			  
		}
		  catch (Exception e) {
		  System.out.println("Error in FileReader !!!"); e.printStackTrace(); }
		 
	}
	
	/**
	 * checks whether a point is located inside the polygon. 
	 * @param p input point as a coordinate
	 * @return returns true if is inside, false otherwise
	 */
	public static boolean isInside(Point p) {
		//System.out.println("edge router  "+p.x+" "+p.y);
		return poly.contains(p);
		
	}
	
	/**
	 * calculates the area of the polygon using its vertices
	 * @return returns the value of area
	 */
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

}

