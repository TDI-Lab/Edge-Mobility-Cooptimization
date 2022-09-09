package edge.epos.map.Old;

import org.openstreetmap.gui.jmapviewer.Coordinate;
/**
 * 
 * @author rooyesh
 * This class includes required measures for Annapolis city
 */
public class CityConstantsAnnapolis {
		public static final String city ="Annapolis";
		public static final Coordinate cityCenter = new Coordinate (38.9725, -76.5042);
		public static final double base_lat = 38.942337;//bottom left corner (towards up)
		public static final double base_long = -76.540461;//bottom left corner (towards right)
		public static final double max_lat = 39.000536;//up right corner
		public static final double max_long = -76.470140;//up right corner
		 
		public static final int zoomLevel = 12;
		public static final double area = 25348278;
		public static final int numofCoreRouters = 45;
		public static final int numofEdgeRouters = 5332;
		public static final int numofVehicules = 53320;
		public static final String PathtoCityGeoPointsFile = "C:\\Users\\rooyesh\\eclipse-workspace\\MapView\\src\\main\\java\\edge\\epos\\map\\GeoPointsAnnapolis";
		//public static final String PathtoCityGeoPointsFile = "C:\\Users\\rooyesh\\eclipse-workspace\\MapView\\src\\main\\java\\edge\\epos\\map\\GeoPointsAnnapolis";
}
