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

public class CityMap {

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
		
        
		
	}
	
    

}

