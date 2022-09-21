package EdgeEPOS.additional;

import java.io.BufferedReader;
	import java.io.FileReader;
	import java.io.IOException;
	import java.util.List;

	import java.util.ArrayList;


	import bio.singa.mathematics.geometry.model.Polygon;
	import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.mathematics.vectors.Vectors2D;
import bio.singa.mathematics.algorithms.voronoi.VoronoiGenerator;
import bio.singa.mathematics.algorithms.voronoi.VoronoiRelaxation;
import bio.singa.mathematics.algorithms.voronoi.model.VoronoiDiagram;
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

	public class CityMapVoronoi {


				//private JFrame mainMap;
				private static VertexPolygon simplePolygon;	    
			    private final int npoints;
			    private List<Vector2D> points; 
			    private String city;
			    
			    public CityMapVoronoi (String city, int np){
					this.city = city;
					this.npoints = np;
					makePolygon();
					//initComponents();

				}
				
				private void makePolygon() {
					
					points = new ArrayList<>();
					
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
			            		
			               		points.add(new Vector2D(Double.parseDouble(strArgs[0]), Double.parseDouble(strArgs[1])));
			            		
			            		i++;
			            		//System.out.println("0invalid points or additional points"+i);
			            		
			            	}
			            	else {
			            		System.out.println("invalid points or additional points"+i);
			            		continue;
			            	}
						}



						simplePolygon = new VertexPolygon(points.toArray(new Vector2D[0]));
						Vector2D v1 = new Vector2D(11.0, 2.0);
						System.out.println("y/n"+	simplePolygon.containsVector(v1));
						//VoronoiRelaxation a = new VoronoiRelaxation();
						//VoronoiDiagram v = new VoronoiDiagram();
						//java.util.List<Vector2D> a = VoronoiRelaxation.relax(simplePolygon);
					   
						//List<Polygon> parts = new GreedyPolygonSplitter().split(poly,4);
						
						// create new rectangle from point (0,0) to (200,200)
						Rectangle boundingBox = new Rectangle(200, 200);
						// create random points within the rectangle
						List<Vector2D> points = Vectors2D.generateMultipleRandom2DVectors(25, boundingBox);
						// generate initial voronoi diagram from the points
						VoronoiDiagram voronoiDiagram = VoronoiGenerator.generateVoronoiDiagram(points, boundingBox);
						// run relaxation 10 times
						int runs = 10;
						for (i = 0; i < runs; i++) {
						    // perform one round of relaxation
						    List<Vector2D> relaxedSites = VoronoiRelaxation.relax(voronoiDiagram);
						    // create new voronoi diagram using the relaxed sites
						    voronoiDiagram = VoronoiGenerator.generateVoronoiDiagram(relaxedSites, boundingBox);
						}
						
					}
					catch (IOException e) {
						e.printStackTrace();
					}
					
					
					System.out.println("polygon created with "+i+ " map points");
					
				}
				
				
				public boolean isInside(Vector2D p) {
					return simplePolygon.containsVector(p);
				
				}
				
				public double area() {
					 return simplePolygon.getArea();
				}
			}


		
		
		
	
