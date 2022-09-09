package edge.epos.map;

/*
 * author: Zeinab
 * this program gets the boundary points of an area and creates the corresponding polygon, 
 * then it checks every input geo-coordinate if its inside the area then puts markers (e.g., backbone and edge routers) for. 
 * the APs which are inside the selected area are written to the file MunichSelected.csv
 * parameters: string and integer
 * redirect files to H:\project2\geo2cart\7Area in the future
 */
public class Map {
	public static void main(String[] args) {
		
	    RouterMap rm = new RouterMap(args[1]);
	    rm.setVisible(true);
	    if (Integer.parseInt(args[0]) == 0)
	    	rm.putRoutersFromCoordinate();
	    else
	    	rm.putMarkersFromFile();
	 }
}
