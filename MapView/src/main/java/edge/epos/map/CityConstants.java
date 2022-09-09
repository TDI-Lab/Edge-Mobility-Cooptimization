package edge.epos.map;

import org.openstreetmap.gui.jmapviewer.Coordinate;
/**
 * 
 * @author rooyesh
 * This class includes required measures for Munich city
 */
public class CityConstants {
	
	  public static final String city ="Munich"; 
	  public static final Coordinate cityCenter = new Coordinate (48.136867, 11.575589); 
	  public static final double base_lat = 38.942337;//bottom left corner (towards up) 
	  public static final double base_long = -76.540461;//bottom left corner (towards right)
	  public static final double max_lat = 39.000536;//up right corner 
	  public static final double max_long = -76.470140;//up right corner
	  
	  public static final int zoomLevel = 10; 
	  public static final double area = 310.7;// km²; 
	  public static final int numofCoreRouters = 154; //number of backbone routers in the Munich city, from Caida and EmuFog
	  public static final int numofEdgeRouters = 5332; //do not use now

	  //path to the border points of the selected area of our tested city:
	  public static final String PathtoCityGeoPointsFile = 
			  "BS\\MunichGeoBorderPoints.txt";
	  //https://www.openstreetmap.org/export#map=11/48.1308/11.7368
	  //"C:\\Users\\rooyesh\\eclipse-workspace\\MapView\\src\\main\\java\\edge\\epos\\map\\GeoPointsMunich";
	  
	  //due to delay is directly placed in the code:
	 // public static final String PathtoCityGeoBSFile = 
		//	  "C:\\Users\\rooyesh\\eclipse-workspace\\MapView\\BS\\GermanyAP.csv\\MunichBS.csv";
		 
	}
