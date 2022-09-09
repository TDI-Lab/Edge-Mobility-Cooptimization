package edge.epos.map;
//import java.util.concurrent.TimeUnit;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.JMapViewerTree;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.LayerGroup;
import org.openstreetmap.gui.jmapviewer.MapMarkerCircle;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapObjectImpl;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.MapRectangleImpl;
//import org.openstreetmap.gui.jmapviewer.OsmMercator;
import org.openstreetmap.gui.jmapviewer.OsmTileLoader;
import org.openstreetmap.gui.jmapviewer.Style;
import org.openstreetmap.gui.jmapviewer.events.JMVCommandEvent;
//import org.openstreetmap.gui.jmapviewer.interfaces.ICoordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.JMapViewerEventListener;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;
import org.openstreetmap.gui.jmapviewer.interfaces.TileLoader;
import org.openstreetmap.gui.jmapviewer.interfaces.TileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.BingAerialTileSource;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;
//import org.openstreetmap.josm.tools.Geometry.PolygonIntersection;
import org.jgrapht.*;
import org.jgrapht.alg.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;


public class RouterMap extends JFrame implements JMapViewerEventListener {

	 private static final long serialVersionUID = 1L;
	
	 private final JMapViewerTree treeMap;
	
	 private final JLabel zoomLabel;
	 private final JLabel zoomValue;
	
	 private final JLabel mperpLabelName;
	 private final JLabel mperpLabelValue;
	 
	 public MapPolygon geoPolygon;
	 public MapPolygon linePolygon1;
	 public MapPolygon linePolygon2;
	 public MapPolygon linePolygon3;
	 public MapPolygon linePolygon4;
	 public MapPolygon linePolygon5;
	 public MapPolygon linePolygon6;
	 public MapPolygon[] linePolygon = new MapPolygon[500];
	 
	 public Polygon cartPolygon;
	 List<Coordinate> polygonGeoCoords;
	 
	 List<Coordinate> route1;
	 List<Coordinate> route2;
	 List<Coordinate> route3;
	 List<Coordinate> route4;
	 List<Coordinate> route5;
	 List<Coordinate> route6;
	 List<ArrayList<Coordinate>> route = new ArrayList<ArrayList<Coordinate>>(500);
	 
	 public double[][] adjacencyMX = new double[472][472];
	 
	 private String city;
 
 public RouterMap(String city) {
	 
     super("CityMapViewer");
     setSize(400, 400);
     treeMap = new JMapViewerTree("Zones");
     this.city = city;
     // Listen to the map viewer for user operations so components will
     // receive events and update
     map().addJMVListener(this);

     setLayout(new BorderLayout());
     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     setExtendedState(JFrame.MAXIMIZED_BOTH);
     JPanel panel = new JPanel(new BorderLayout());
     //JPanel panel = new Map1("Boulder", new Coordinate(38.9725, -76.5042))
     JPanel panelTop = new JPanel();
     JPanel panelBottom = new JPanel();
     JPanel helpPanel = new JPanel();

     mperpLabelName = new JLabel("Meters/Pixels: ");
     mperpLabelValue = new JLabel(String.format("%s", map().getMeterPerPixel()));

     zoomLabel = new JLabel("Zoom: ");
     zoomValue = new JLabel(String.format("%s", map().getZoom()));

     
     add(panel, BorderLayout.NORTH);
     add(helpPanel, BorderLayout.SOUTH);
     panel.add(panelTop, BorderLayout.NORTH);
     panel.add(panelBottom, BorderLayout.SOUTH);
     JLabel helpLabel = new JLabel("Use right mouse button to move,\n "
             + "left double click or mouse wheel to zoom.");
     helpPanel.add(helpLabel);
     JButton button = new JButton("setDisplayToFitMapMarkers");
     button.addActionListener(e -> map().setDisplayToFitMapMarkers());
     JComboBox<TileSource> tileSourceSelector = new JComboBox<>(new TileSource[] {
             new OsmTileSource.Mapnik(),
             new OsmTileSource.TransportMap(),
             new BingAerialTileSource(),
     });
     tileSourceSelector.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent e) {
             map().setTileSource((TileSource) e.getItem());
         }
     });
     JComboBox<TileLoader> tileLoaderSelector;
     tileLoaderSelector = new JComboBox<>(new TileLoader[] {new OsmTileLoader(map())});
     tileLoaderSelector.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent e) {
             map().setTileLoader((TileLoader) e.getItem());
         }
     });
     map().setTileLoader((TileLoader) tileLoaderSelector.getSelectedItem());
     panelTop.add(tileSourceSelector);
     panelTop.add(tileLoaderSelector);
     final JCheckBox showMapMarker = new JCheckBox("Map markers visible");
     showMapMarker.setSelected(map().getMapMarkersVisible());
     showMapMarker.addActionListener(e -> map().setMapMarkerVisible(showMapMarker.isSelected()));
     panelBottom.add(showMapMarker);
     ///
     final JCheckBox showTreeLayers = new JCheckBox("Tree Layers visible");
     showTreeLayers.addActionListener(e -> treeMap.setTreeVisible(showTreeLayers.isSelected()));
     panelBottom.add(showTreeLayers);
     ///
     final JCheckBox showToolTip = new JCheckBox("ToolTip visible");
     showToolTip.addActionListener(e -> map().setToolTipText(null));
     panelBottom.add(showToolTip);
     ///
     final JCheckBox showTileGrid = new JCheckBox("Tile grid visible");
     showTileGrid.setSelected(map().isTileGridVisible());
     showTileGrid.addActionListener(e -> map().setTileGridVisible(showTileGrid.isSelected()));
     panelBottom.add(showTileGrid);
     final JCheckBox showZoomControls = new JCheckBox("Show zoom controls");
     showZoomControls.setSelected(map().getZoomControlsVisible());
     showZoomControls.addActionListener(e -> map().setZoomControlsVisible(showZoomControls.isSelected()));
     panelBottom.add(showZoomControls);
     final JCheckBox scrollWrapEnabled = new JCheckBox("Scrollwrap enabled");
     scrollWrapEnabled.addActionListener(e -> map().setScrollWrapEnabled(scrollWrapEnabled.isSelected()));
     panelBottom.add(scrollWrapEnabled);
     panelBottom.add(button);

     panelTop.add(zoomLabel);
     panelTop.add(zoomValue);
     panelTop.add(mperpLabelName);
     panelTop.add(mperpLabelValue);

     add(treeMap, BorderLayout.CENTER);
     
     map().setDisplayPosition(CityConstants.cityCenter, CityConstants.zoomLevel);

     map().addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
             if (e.getButton() == MouseEvent.BUTTON1) {
                 map().getAttribution().handleAttribution(e.getPoint(), true);
             }
         }
     });
        
     map().addMouseMotionListener(new MouseAdapter() {
         @Override
         public void mouseMoved(MouseEvent e) {
             Point p = e.getPoint();
             boolean cursorHand = map().getAttribution().handleAttributionCursor(p);
             if (cursorHand) {
                 map().setCursor(new Cursor(Cursor.HAND_CURSOR));
             } else {
                 map().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
             }
             if (showToolTip.isSelected()) map().setToolTipText(map().getPosition(p).toString());
         }
     });
     
     drawCityGeoBoundry();
     
 }

 private JMapViewer map() {
     return treeMap.getViewer();
 }


 private void drawCityGeoBoundry(){
	 
	  
	 Layer Munich = treeMap.addLayer(CityConstants.city+" Selected Area");
	 polygonGeoCoords = new ArrayList<Coordinate>();
	 getCityPoints();
	 
	//10 points +1end
	 System.out.println("size geopolygon: "+polygonGeoCoords.size());
	 geoPolygon = new MapPolygonImpl(Munich, polygonGeoCoords);//CityConstants.city+" Selected Boundary"
	 map().addMapPolygon(geoPolygon);
	 
	 Munich.setVisible(Boolean.TRUE);
	 
	 
	 
}
 
 private void getCityPoints() {
	//read boundary points
		
	 String line;
		String [] strArgs;
		int j = 0, i = 0;
		
		try (BufferedReader br = new BufferedReader(new FileReader(CityConstants.PathtoCityGeoPointsFile))) {

			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) 
	        		continue;
         		strArgs = line.split(",");
         		j = strArgs.length;
            	if (j > 1) {
         			polygonGeoCoords.add(new Coordinate(Double.parseDouble(strArgs[0]),Double.parseDouble(strArgs[1])));
         			//System.out.println(i+" x: "+Double.parseDouble(strArgs[0])+" y "+Double.parseDouble(strArgs[1]));	
            		i++;
	         	}
	         	else 
	         	{
	         		System.out.println("invalid input points");
	         		continue;
	         	}
			}
			
				
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("num of points read: "+i);
 }
 

 /**
 * read geo-coordinates from a file 
 * converts it to cart-coordinate
 * checks if the cart-coordinate is inside the area then put marker
 */
public void putMarkersFromFile() {
	 
	 Layer layerEdge = treeMap.addLayer(CityConstants.city+"-EdgeRouters");
	 Layer layerCore = treeMap.addLayer(CityConstants.city+"-CoreRouters");
	 
	 drawCartPolygon();
	 
		String pathToMunichBS = "BS\\MunichGeoBS.csv";	 	
		String pathToSelectedBSCsv = "BS\\MunichGeoSelectedAP.csv";
		String pathToMunichCore = "BS\\MunichGeoCore.csv";
		String line;
		String CSV_SEPARATOR = ","; // it could be a comma or a semi colon
		int numberofLTE = 500;
		String [][] array = new String [numberofLTE][4];
		Coordinate [] CorePoints = new Coordinate[5];
		Coordinate [] EdgePoints = new Coordinate[numberofLTE];
		
		int i = 0; int j = 0;
		int counter = 0, LTENum = 0, EdgeCounter = 0, CoreCounter = 0;
		System.out.println("edge router generation.................");
			  try { 
				  LineNumberReader csvReaderBS = new LineNumberReader(new  FileReader(pathToMunichBS)); 
				  LineNumberReader csvReaderCore = new LineNumberReader(new  FileReader(pathToMunichCore)); 
				  BufferedWriter csvWriter = new  BufferedWriter(new FileWriter(pathToSelectedBSCsv));
				  //BufferedWriter csvWriter1 = new BufferedWriter(new FileWriter(pathToMunichBS));
					
				  //line = csvReaderBS.readLine();
				  while ((line = csvReaderBS.readLine()) != null) { 
					  String[] data =  line.split(CSV_SEPARATOR); 
					  counter = 0;
					  
					  Point cart_Coordination = map().getMapPosition(Double.parseDouble(data[7]),Double.parseDouble(data[6])) ;
					  //the following useless lines of print are for printing to have the map on the screen and then show the points:
					  
					
					  j++;
					  
					  i++;
					  //String rfTech = data[0]; &(data[0].equalsIgnoreCase("LTE"))
					  if (cartPolygon.contains(cart_Coordination)) {
						 //System.out.println(line);
						  
						  array[EdgeCounter][counter++] = data[0];
						  array[EdgeCounter][counter++] = data[4];
						  array[EdgeCounter][counter++] = data[6];
						  array[EdgeCounter][counter++] = data[7];
						  //EdgeCounter;
						 
						  
						  //the radius value of 1.0 degree corresponds to 111 km 
						  
						  Coordinate  geo_coorindate = new  Coordinate(Double.parseDouble(data[7]),Double.parseDouble(data[6]));
						  EdgePoints[EdgeCounter++] = new  Coordinate(Double.parseDouble(data[7]),Double.parseDouble(data[6]));
						   
						  MapMarkerCircle m = new MapMarkerCircle(layerEdge, geo_coorindate, .000035);
						  m.setColor(Color.YELLOW); 
						  map().addMapMarker(m); 
						  MapMarkerDot m1 = new MapMarkerDot(layerEdge, geo_coorindate); 
						  m1.setColor(Color.GRAY);
						  map().addMapMarker(m1); 
						  
					  }
					  
			  	}
				  
				  			 
			     for(i = 0 ; i<EdgeCounter ; i++)
    				{
				        for (int k = 0 ;k<4 ;k++)
				        	csvWriter.append(array[i][k]).append(CSV_SEPARATOR);
				        	csvWriter.append(System.lineSeparator());
					 }
						 
			  csvReaderBS.close(); 
			  csvWriter.flush(); 
			  csvWriter.close(); 
			  } 
			  catch (Exception e) {
				  System.out.println("Error in FileReader !!!"); 
				  e.printStackTrace(); 
			}
			 
			  System.out.println("core router generation.................");
			  
			  try { 
				  LineNumberReader csvReaderCore = new LineNumberReader(new  FileReader(pathToMunichCore)); 
				  
				 while ((line = csvReaderCore.readLine()) != null) { 
					  String[] data =  line.split(CSV_SEPARATOR); 
					  CoreCounter = 0;
					  
					  Point cart_Coordination = map().getMapPosition(Double.parseDouble(data[1]),Double.parseDouble(data[0])) ;
					  //the following useless lines of print are for printing to have the map on the screen and then show the points:
					  //System.out.println("x: "+cart_Coordination.x);
					  //j++;
					  
					  //i++;
					  //String rfTech = data[0]; &(data[0].equalsIgnoreCase("LTE"))
					  //if (cartPolygon.contains(cart_Coordination)) {
						   //the radius value of 1.0 degree corresponds to 111 km 
					  	    
					  	//System.out.println(line);
						  Coordinate  geo_coorindate = new  Coordinate(Double.parseDouble(data[1]),Double.parseDouble(data[0]));
//						  CorePoints[CoreCounter++] = new  Coordinate(Double.parseDouble(data[1]),Double.parseDouble(data[0]));
							
						  MapMarkerCircle m = new MapMarkerCircle(layerCore, geo_coorindate, .000035);
						  m.setColor(Color.BLACK); 
						  map().addMapMarker(m); 
						  MapMarkerDot m1 = new MapMarkerDot(layerCore, geo_coorindate); 
						  m1.setColor(Color.RED);
						  map().addMapMarker(m1); 
						  
						  // }
				  
				  
				  }
				  
					  csvReaderCore.close(); 
				  } 
				  catch (Exception e) {
					  System.out.println("Error in FileReader !!!"); 
					  e.printStackTrace(); 
				}
					 
			 DrawCoreConnections(EdgePoints, EdgeCounter);
			 
	layerCore.setVisible(Boolean.TRUE);
	layerEdge.setVisible(Boolean.TRUE);
	System.out.println("Total LTE Cell Tower: "+EdgeCounter+" Num of Processed Lines: "+j);
	areaDistance();

	//Munich Total Cell Antennas: 71778 GSM: 22026 UMTS: 35797 LTE: 13955
	//total LTE in Germany: 427,884
	//Munich area: 310.7 km2
	//coverage range 1000 to 3000
 }
 
 private void DrawCoreConnections(Coordinate[] edgePoints, int edgePointsNum ) {
	// TODO Auto-generated method stub
	
	 String Ds = "BS\\CloudletAdjacency.txt"; 
	 FileWriter fileWriter = null;
	 String NEW_LINE_SEPARATOR = "\n";
	    	
    try {
		fileWriter = new FileWriter(Ds);
 
	 //for(double[] arr1 : adjacencyMX)   Arrays.fill(arr1, 700.0);
	 for(int k = 0; k<472; k++) 
		 for(int j = 0; j<472; j++) 
		 	adjacencyMX[k][j]=7000;
		 
	
	 
	 int i=0;
	 double min2, min1, fdis;
	 int index1, index2, findex;
	 Layer CoreConnection = treeMap.addLayer("CoreConnection");
	 Layer Edge_Core_Connection = treeMap.addLayer("Edge_Core_Connection");
	 
	 Coordinate[] cores = new Coordinate[4];
	 cores[0] = new  Coordinate(48.150437,11.58055);
	 cores[1] = new  Coordinate(48.150347,11.56781);
	 cores[2] = new  Coordinate(48.141544,11.58053);
	 cores[3] = new  Coordinate(48.153444,11.55274);
	 Coordinate one = new Coordinate(48.150437,11.58055);
	 Coordinate two = new Coordinate(48.150347,11.56781);
	 Coordinate three = new Coordinate(48.141544,11.58053);
	 Coordinate four = new Coordinate(48.153444,11.55274);
	 //Coordinate five = new  Coordinate(48.39570831,11.730755); most outer point

		
		route1 = new ArrayList<Coordinate>(Arrays.asList(one, two,two));//, five,five));
		linePolygon1 = new MapPolygonImpl(CoreConnection, "rout12", route1);
		map().addMapPolygon(linePolygon1);
		adjacencyMX[469][470] = distance_old(route1.get(0).getLat(),route1.get(0).getLon(),route1.get(1).getLat(),route1.get(1).getLon());
		adjacencyMX[470][469]= adjacencyMX[469][470];
		
		route2 = new ArrayList<Coordinate>(Arrays.asList(one, three, three));//, five,five));
		linePolygon2 = new MapPolygonImpl(CoreConnection,"rout13", route2);
		map().addMapPolygon(linePolygon2);
		adjacencyMX[468][470] = distance_old(route1.get(0).getLat(),route1.get(0).getLon(),route1.get(1).getLat(),route1.get(1).getLon());
		adjacencyMX[470][468]= adjacencyMX[468][470];
		
		route3 = new ArrayList<Coordinate>(Arrays.asList(two, three, three));//, five,five));
		linePolygon3 = new MapPolygonImpl(CoreConnection,"rout23", route3);
		map().addMapPolygon(linePolygon3);
		adjacencyMX[469][470] = distance_old(route1.get(0).getLat(),route1.get(0).getLon(),route1.get(1).getLat(),route1.get(1).getLon());
		adjacencyMX[470][469] = adjacencyMX[469][470];
		
		route4 = new ArrayList<Coordinate>(Arrays.asList(two, four, four));//, five,five));
		linePolygon4 = new MapPolygonImpl(CoreConnection,"rout24", route4);
		map().addMapPolygon(linePolygon4);
		adjacencyMX[469][471] = distance_old(route1.get(0).getLat(),route1.get(0).getLon(),route1.get(1).getLat(),route1.get(1).getLon());
		adjacencyMX[471][469] = adjacencyMX[469][471];
		
		route5 = new ArrayList<Coordinate>(Arrays.asList(one, four, four));//, five,five));
		linePolygon5 = new MapPolygonImpl(CoreConnection,"rout14", route5);
		map().addMapPolygon(linePolygon5);
		adjacencyMX[468][471] = distance_old(route1.get(0).getLat(),route1.get(0).getLon(),route1.get(1).getLat(),route1.get(1).getLon());
		adjacencyMX[471][468] = adjacencyMX[468][471] ;
	
	for (i = 0; i<edgePointsNum; i++) {
		double dis1 = distance_old(route1.get(0).getLat(),route1.get(0).getLon(),edgePoints[i].getLat(),edgePoints[i].getLon());
		double dis2 = distance_old(route3.get(0).getLat(),route3.get(0).getLon(),edgePoints[i].getLat(),edgePoints[i].getLon());
		double dis3 = distance_old(route3.get(1).getLat(),route3.get(1).getLon(),edgePoints[i].getLat(),edgePoints[i].getLon());
		double dis4 = distance_old(route4.get(1).getLat(),route4.get(1).getLon(),edgePoints[i].getLat(),edgePoints[i].getLon());
		
		if (dis1<dis2) { 
			min1 = dis1;
			index1 = 1;
		}
		else {
			min1 = dis2;
			index1 = 2 ;
		}
		if (dis3<dis4) {
			min2 = dis3;
			index2 = 3;
		}
		else {
			min2 = dis4;
			index2 = 4;
		}
		
		if (min1<min2) {
			findex = index1;
			fdis = min1;
		}
		else {
			findex = index2;
			fdis = min2;
		}
		
		//Color color = new Color(0x912EF5, true);
		adjacencyMX[i][findex+467] = fdis;
		adjacencyMX[findex+467][i] = fdis;
		
		route.add(i, new ArrayList<Coordinate>(Arrays.asList(edgePoints[i], cores[findex-1], cores[findex-1])));
		linePolygon[i] = new MapPolygonImpl(Edge_Core_Connection, route.get(i));
		((MapObjectImpl) linePolygon[i]).setColor(Color.BLACK);
		//((MapObjectImpl) linePolygon[i]).setBackColor(color);
		map().addMapPolygon(linePolygon[i]);
	}
	System.out.println("last drawn edge index "+i);
	
	CoreConnection.setVisible(Boolean.TRUE);  
	Edge_Core_Connection.setVisible(Boolean.TRUE);  
	
	String printStr;
	System.out.println("printing adjacency matrix.....");
	 for(int k = 0; k<472; k++) {
		 fileWriter.append(k+" ");
		 for(int j = 0; j<472; j++) {//"%.2f ", adjacencyMX[k][j]);
			 printStr = " "+adjacencyMX[k][j]+" ";
		 	 fileWriter.append(printStr);
	   		 
		}
		 fileWriter.append(NEW_LINE_SEPARATOR);
	}
	     fileWriter.flush();
	     fileWriter.close();
	 } catch (IOException e) {
	     System.out.println("Error while flushing/closing fileWriter !!!");
	     e.printStackTrace();
	 }
	 
	findPath();
}

 
 private void findPath() {
	// TODO Auto-generated method stub
	 String printStr;
	 String CSV_SEPARATOR = ",";
	 String DesFile = "BS\\CloudletPathLentgh.csv"; 
	 String NEW_LINE_SEPARATOR = "\n";
	 
	 DefaultWeightedEdge [] edge = new DefaultWeightedEdge[500];
	 int n = 0;
	 SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>  graph = 
	            new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>
	            (DefaultWeightedEdge.class); 
		 	
	 for(int k = 0; k<472; k++) 
			 	 graph.addVertex(String.valueOf(k));
		            
	 for(int k = 0; k<472; k++) {
		 for(int j = 0; j<472; j++) {
			 if (k == j)
				 continue;
			 edge[n] = graph.addEdge(String.valueOf(k),String.valueOf(j));
			 graph.setEdgeWeight(edge[n], adjacencyMX[k][j]);
		 }
	 }
	
	// GraphPath<String, DefaultWeightedEdge> route = null;
	 double PathLentgh;
	 //double a; 
	 System.out.println("Finding shortest paths...");
	 
	 try {
		 BufferedWriter csvWriter = new  BufferedWriter(new FileWriter(DesFile));
		 
		 for(int k = 0; k<472; k++) {
			 //csvWriter.append(k+" ");
			 for(int j = 0; j<472; j++) {
				 
				if (k == j) {
					csvWriter.append(String.valueOf(0)).append(CSV_SEPARATOR);
		        	
					 continue;
				 }
				 
				PathLentgh = DijkstraShortestPath.findPathBetween(graph, String.valueOf(k),String.valueOf(j)).getWeight();
		        //a =   DijkstraShortestPath.findPathBetween(graph, String.valueOf(k),String.valueOf(j)).getLength();
            	 //printStr = " "+PathLentgh+" ";
            	 csvWriter.append(String.valueOf(PathLentgh)).append(CSV_SEPARATOR);;
	 
			 }
			 csvWriter.append(System.lineSeparator());
		 }
	 
	
		 csvWriter.flush();
		 csvWriter.close();
 } catch (IOException e) {
     System.out.println("Error while flushing/closing fileWriter !!!");
     e.printStackTrace();
 }
 
	 
	 
// System.out.println(p.getPathEdgeList());
	//    System.out.println("Cost of shortest (i.e cheapest) path = £" + p.getPathLength() );
	 
	 
}

public static void DrawGraph(){
	
	}
	
 
/**
 * draws a convex polygon from border points in Cartesian format
 * note that for this the first and the last points must be the same
 */
public void drawCartPolygon() {
	 
	 int i = 0; int j = 0;
	 Point p;
	 int cartPolygonSize = polygonGeoCoords.size() + 1;
	 int[] polygonPointsX = new int[cartPolygonSize];
	 int[] polygonPointsY = new int[cartPolygonSize];
	 
	 while (j < polygonGeoCoords.size()) {
		 p = map().getMapPosition(polygonGeoCoords.get(j).getLat(), polygonGeoCoords.get(j).getLon());
	 	 polygonPointsX[j]=p.x;
	 	 polygonPointsY[j]=p.y;
	 	 j++;
	 	
	 }
	 
	 p = map().getMapPosition(polygonGeoCoords.get(0).getLat(), polygonGeoCoords.get(0).getLon());
 	 polygonPointsX[j]=p.x;
 	 polygonPointsY[j]=p.y;
	 
 	 cartPolygon = new Polygon(polygonPointsX, polygonPointsY, cartPolygonSize);
	 	
 	
 }
 
 /**
 * after drawing a polygon using the drawCartPolygon,
 * evenly distributes CityConstants.numofEdgeRouters into the CityConstants.area determined in CityConstants.
 * the output is shown using layerEdge
 * then another method is called to draw the core routers.
 */
public void putRoutersFromCoordinate() {
	 
	 int i = 0;
	 Point p;
	 Layer edgeLayer = treeMap.addLayer(CityConstants.city+"-EdgeRouters");
	 
	 drawCartPolygon();
	
 	 double edgeRouterDistance = 2*Math.sqrt((CityConstants.area/CityConstants.numofEdgeRouters)/Math.PI);
 	/* 
	 double base_lat = 38.942337;//towards up
	 double base_long = -76.540461;//towards right
	 double max_lat = 39.000536;
	 double max_long = -76.470140;
	 */
	 Coordinate base_coor = new Coordinate(CityConstants.base_lat, CityConstants.base_long);
	 Coordinate max_long_coor = new Coordinate(CityConstants.base_lat, CityConstants.max_long);
	 Coordinate max_lat_coor = new Coordinate(CityConstants.max_lat, CityConstants.base_long);
	 
	 double dis_long = distance(base_coor, max_long_coor); 
	 double dis_lat = distance(base_coor, max_lat_coor);
 	 double meters = edgeRouterDistance;
	 double coef = meters * 0.0000089;
	 
	 double new_lat = CityConstants.base_lat;
	 double new_long = CityConstants.base_long;
	 
	 Coordinate new_coor = base_coor; 
	 Coordinate long_comparator = base_coor;
	 Coordinate lat_comparator = base_coor;
	  i = 0;
	 
	 while (distance(base_coor, lat_comparator) < dis_lat) {
	 		
		 while(distance(base_coor, long_comparator) < dis_long) {
				Point p1 = map().getMapPosition(new_coor.getLat(),new_coor.getLon());
	 			
	 			if (cartPolygon.contains(p1)) {
	 				 //the radius value of 1.0 degree corresponds to 111 km
	 				MapMarkerCircle m = new MapMarkerCircle(edgeLayer, new_coor, .00035);
	 				m.setColor(Color.YELLOW);
	 				map().addMapMarker(m);
					 
	 				 MapMarkerDot m1 = new MapMarkerDot(edgeLayer, new_coor);
					 m1.setColor(Color.YELLOW);
					 map().addMapMarker(m1);
					 
					 i++;
				}
				 new_long = new_long + coef / Math.cos(new_coor.getLat() * 0.018);
				 new_coor = new Coordinate(new_coor.getLat(), new_long);
				 long_comparator = new Coordinate(base_coor.getLat(), new_long);
	 		}
		 
	 		new_lat = new_lat +coef;
	 		new_coor = new Coordinate(new_lat, base_coor.getLon());
	 		lat_comparator = new Coordinate(new_lat, base_coor.getLon());
	 		long_comparator = new Coordinate(base_coor.getLat(), base_coor.getLon());
	 		new_long = CityConstants.base_long;
	 }	
	 
	 edgeLayer.setVisible(Boolean.TRUE);
	 putCoreRouter();
	 System.out.println("number of edge routers: "+i);
 }
 
 public void putCoreRouter() {
	 
	 Layer coreLayer = treeMap.addLayer(CityConstants.city+"-CoreRouters");
		
	 double coreRouterDistance = 2*Math.sqrt((CityConstants.area/CityConstants.numofCoreRouters)/Math.PI);
	 
	 Coordinate base_coor = new Coordinate(CityConstants.base_lat, CityConstants.base_long);
	 Coordinate max_long_coor = new Coordinate(CityConstants.base_lat, CityConstants.max_long);
	 Coordinate max_lat_coor = new Coordinate(CityConstants.max_lat, CityConstants.base_long);
	 
	 double dis_long = distance(base_coor, max_long_coor); 
	 double dis_lat = distance(base_coor, max_lat_coor);
 	 double meters = coreRouterDistance;
	 double coef = meters * 0.0000089;
	 
	 double new_lat = CityConstants.base_lat;
	 double new_long = CityConstants.base_long;
	 
	 Coordinate new_coor = base_coor; 
	 Coordinate long_comparator = base_coor;
	 Coordinate lat_comparator = base_coor;
	 int j = 0;
	 
	 while (distance(base_coor, lat_comparator) < dis_lat) {
	 		
		 while(distance(base_coor, long_comparator) < dis_long) {
			 
	 			Point p1 = map().getMapPosition(new_coor.getLat(),new_coor.getLon());
	 			 
	 			//System.out.println("x: "+new_coor.getLat()+"y"+new_coor.getLon());
		  		//System.out.println("xcart: "+p1.getX()+"ycart"+p1.getY());
		  		  
	 			if (cartPolygon.contains(p1)) {
	 				
	 				MapMarkerCircle m = new MapMarkerCircle(coreLayer, new_coor, .00015);
	 				m.setColor(Color.BLACK);
	 				map().addMapMarker(m);
					//Color color = new Color(50, 0, 0,100);
	 				
					 MapMarkerDot m1 = new MapMarkerDot(coreLayer, new_coor);
					 m1.setColor(Color.BLACK);
					 m.setBackColor(Color.BLACK);
					 map().addMapMarker(m1);
					 
					 j++;
				}
				 new_long = new_long + coef / Math.cos(new_coor.getLat() * 0.018);
				 new_coor = new Coordinate(new_coor.getLat(), new_long);
				 long_comparator = new Coordinate(base_coor.getLat(), new_long);
	 		}
		 
	 		new_lat = new_lat +coef;
	 		new_coor = new Coordinate(new_lat, base_coor.getLon());
	 		lat_comparator = new Coordinate(new_lat, base_coor.getLon());
	 		long_comparator = new Coordinate(base_coor.getLat(), base_coor.getLon());
	 		new_long = CityConstants.base_long;
	 }	
	 System.out.println("number of core routers: "+j);
	 
	 coreLayer.setVisible(Boolean.TRUE);
	 

 }

 public double areaDistance() {
	 
	 double area = 0.0;
	 
	 System.out.println("dimensions: "+distance_old(polygonGeoCoords.get(0).getLat(),polygonGeoCoords.get(0).getLon(),polygonGeoCoords.get(1).getLat(),polygonGeoCoords.get(1).getLon())
			 +" "+distance_old(polygonGeoCoords.get(1).getLat(),polygonGeoCoords.get(1).getLon(),polygonGeoCoords.get(2).getLat(),polygonGeoCoords.get(2).getLon()));
	 area = distance_old(polygonGeoCoords.get(0).getLat(),polygonGeoCoords.get(0).getLon(),polygonGeoCoords.get(1).getLat(),polygonGeoCoords.get(1).getLon())
			 *distance_old(polygonGeoCoords.get(1).getLat(),polygonGeoCoords.get(1).getLon(),polygonGeoCoords.get(2).getLat(),polygonGeoCoords.get(2).getLon());
	
	 System.out.printf("area in meters: %.0f",area);
	 
	 return area;

	 /*
	 System.out.println("\n\nMethod2: wrong ");
	 System.out.println("dimensions: "+distance(polygonGeoCoords.get(0),polygonGeoCoords.get(1))+" "+distance(polygonGeoCoords.get(1),polygonGeoCoords.get(2)));
	 return distance(polygonGeoCoords.get(0),polygonGeoCoords.get(1)) * distance(polygonGeoCoords.get(1),polygonGeoCoords.get(2));
*/
	 
	 
 }
 	
 public double area() {
	 
	 double sum = 0;
	 int [] xpoints = new int[cartPolygon.xpoints.length];
	 int [] ypoints = new int[cartPolygon.xpoints.length];
	 
	 System.out.println("area lenght: "+cartPolygon.xpoints.length);
	 
	 for (int i = 0; i<cartPolygon.xpoints.length-1; i++) {
		 xpoints[i] = cartPolygon.xpoints[i];
		 ypoints[i] = cartPolygon.ypoints[i];
	 }
	    for (int i = 0; i < xpoints.length ; i++)
	    {
	      if (i == 0)
	      {
	        sum += xpoints[i] * (ypoints[i + 1] - ypoints[ypoints.length - 1]);
	      }
	      else if (i == ypoints.length - 1)
	      {
	        sum += xpoints[i] * (ypoints[0] - ypoints[i - 1]);
	      }
	      else
	      {
	        sum += xpoints[i] * (ypoints[i + 1] - ypoints[i - 1]);
	      }
	    }

	    double area = 0.5 * Math.abs(sum);
	    return area;
}

 
 /**
 * @param c1
 * @param c2
 * @return the distance between two geo-coordinates in meters
 */
public double distance(Coordinate c1, Coordinate c2) {

	    double lat1 = c1.getLat();
	    double lon1 = c1.getLon();
	    double lat2 = c2.getLat();
	    double lon2 = c2.getLon();
	    double theta = lon1 - lon2;
	    double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
	    dist = Math.acos(dist);
	    dist = rad2deg(dist);
	    dist = dist * 1.609344 * 1000;        
	    return (dist); // 134910.69784909734
	}
 
	/**
	 * converts decimal into radian 
	 * @param deg
	 * @return
	 */
	private double deg2rad(double deg) {
	    return (deg * Math.PI / 180.0);
	}       
	    /* The function to convert radian into decimal */
	private double rad2deg(double rad) {
	    return (rad * 180.0 / Math.PI);
	}
	
	/** calculates the distance between two locations in MILES */
private double distance_old(double lat1, double lng1, double lat2, double lng2) {

	    double earthRadius = 6371; // 3958.75 in miles, change to 6371 for kilometer output

	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);

	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);

	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
	        * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

	    double dist = earthRadius * c;

	    return dist*1609.34; // output distance, in Meter
	}

 private class MyMarker extends MapMarkerDot {

     public MyMarker(Layer layer, String name, Coordinate coord) {
         super(layer, name, coord);
         
         Style style = MapMarkerDot.getDefaultStyle();
         style.setBackColor(Color.magenta);
         style.setColor(Color.magenta);
         
     }
 }
 private void updateZoomParameters() {
     if (mperpLabelValue != null)
         mperpLabelValue.setText(String.format("%s", map().getMeterPerPixel()));
     if (zoomValue != null)
         zoomValue.setText(String.format("%s", map().getZoom()));
 }

 @Override
 public void processCommand(JMVCommandEvent command) {
     if (command.getCommand().equals(JMVCommandEvent.COMMAND.ZOOM) ||
             command.getCommand().equals(JMVCommandEvent.COMMAND.MOVE)) {
         updateZoomParameters();
     }
 }

}