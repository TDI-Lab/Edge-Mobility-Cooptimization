package EdgeEPOS.additional;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import java.util.ArrayList;


import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;
/*
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.Polygonal;
import org.locationtech.jts.*;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import de.incentergy.geometry.impl.GreedyPolygonSplitter;
*/
import bio.singa.mathematics.geometry.faces.*;

public class CityMapPoly {

			//private JFrame mainMap;
	private static VertexPolygon simplePolygon;	    
	private Polygon poly;
		    private final int npoints;
		    Coordinate[] point;
		    private String city;
		    
		    public CityMapPoly (String city, int np){
				this.city = city;
				this.npoints = np;
				makePolygon();
				//initComponents();

			}
			
			private void makePolygon() {
				
				
				point = new Coordinate[npoints+1];
				
				String line;
				String [] strArgs;
				int j = 0, i = 0;
				
				try (BufferedReader br = new BufferedReader(new FileReader(Constants.BASE_ADDRESS+"RouterPositions-"+city+".txt"))) {

					while ((line = br.readLine()) != null) {
						if (line.startsWith("#")) 
			        		continue;
		            	//read bounadry
						strArgs = line.split(",");
		            	j = strArgs.length;
		               	if ((j > 1) && (i < npoints)) {
		            		
		            		point[i] = new Coordinate(Double.parseDouble(strArgs[0]), Double.parseDouble(strArgs[1]));
		            		
		            		i++;
		            		//System.out.println("0invalid points or additional points"+i);
		            		
		            	}
		            	else {
		            		System.out.println("invalid points or additional points"+i);
		            		continue;
		            	}
					}
					point[npoints] = new Coordinate(point[0].x, point[0].y);
            		/*
					GeometryFactory fact = new GeometryFactory();
					LinearRing linear = new GeometryFactory().createLinearRing(point);
					poly = new Polygon(linear, null, fact);
					*/
					//poly = (Polygon) new WKTReader().read("POLYGON ((2111.48 18168.82, 34.80 10841.64, 740.09 4376.47, 7871.36 1672.86, 10222.33 9078.41, 8304.44 11278.83, 13472.61 11607.71, 12861.82 16775.88, 8492.37 13017.21, 2111.48 18168.82))");
				    //poly = (Polygon) new WKTReader().read("POLYGON ((2111 18168,34 10841,740 4376,7871 1672,10222 9078,8304 11278,13472 11607,12861 16775,8492 13017,2111 18168))");
				    //poly.isValid();
					
					//poly = (Polygonal) new WKTReader().read("POLYGON ((1238.00 8757.46,231.67 5444.94,3963.49 1692.16,6877.66 2635.59,5703.61 6660.93,3334.53 8652.63,1238.00 8757.46))");
				    //poly.isValid();
					
				    List<Vector2D> vectors = new ArrayList<>();
			        vectors.add(new Vector2D(4.0, 10.0));
			        vectors.add(new Vector2D(9.0, 7.0));
			        vectors.add(new Vector2D(11.0, 2.0));
			        vectors.add(new Vector2D(-2.0, 2.0));
			        simplePolygon = new VertexPolygon(vectors.toArray(new Vector2D[0]));
			        
				    //List<Polygon> parts = new GreedyPolygonSplitter().split(poly,4);
					//List<Polygon> parts = new GreedyPolygonSplitter().split(poly,2);
					/*
					Geometry p = parts.get(0).getCentroid();
					Geometry p1 = parts.get(1).getCentroid();
					System.out.println("num part: "+parts.size()+"part0.cetre: "+p.getCoordinate().x+" "+p.getCoordinate().y+"part1.cetre: "+p1.getCoordinate().x+" "+p1.getCoordinate().y);
*/
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				
				
				System.out.println("polygon created with "+i+ " map points");
				
			}
			
			
			public boolean isInside(Geometry p) {
				return poly.contains(p);
			
			}
			
			
			public double area() {
				 return poly.getArea();
			}
		}


	
	
	
	