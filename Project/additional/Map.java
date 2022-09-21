package EdgeEPOS.additional;

import java.awt.Polygon;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Map {

	private JFrame mainMap;
    private Polygon poly= new Polygon();
    private final int npoints;

	Map (int np){
		
		this.npoints = np;
		makePolygon();//mapFile, points
		initComponents();

	}
	
	 private void initComponents() {

	        mainMap = new JFrame();
	        mainMap.setResizable(false);

	        mainMap.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	        JPanel p = new JPanel() {
	            @Override
	            protected void paintComponent(Graphics g) {
	                super.paintComponent(g);
	                g.setColor(Color.BLUE);
	                g.drawPolygon(poly);
	            }

	            @Override
	            public Dimension getPreferredSize() {
	                return new Dimension(11000, 19000);
	            }
	        };
	        mainMap.add(p);
	        mainMap.pack();
	       // mainMap.setVisible(true);

	    }


	private void makePolygon() {
		
		int []xpoints = new int[npoints];
		int []ypoints = new int[npoints];
		String line;
		String [] strArgs;
		int j = 0, i = 0;
		
		try (BufferedReader br = new BufferedReader(new FileReader(Constants.BASE_ADDRESS+"RouterPositions.txt"))) {

			while ((line = br.readLine()) != null) {
				if (line.startsWith("#")) 
	        		continue;
            	//read boundry
				strArgs = line.split("\\s+");
            	j = strArgs.length;
            	if ((j > 1) && (i < npoints)) {
            		
            		xpoints[i] =  (int) Double.parseDouble(strArgs[0]); 
            		ypoints[i] =  (int) Double.parseDouble(strArgs[1]); 	
            		poly.addPoint(xpoints[i], ypoints[i]);
            		System.out.println("i "+i+ " "+xpoints[i]+" "+ ypoints[i]);
            		i++;
            		
            	}
            	else {
            		System.out.println("invalid points or additional points"+i);
            		break;
            	}
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("polygon created from map points"+i);
		//poly = new Polygon(xpoints, ypoints, npoints);

	}
	
	public boolean isInside(int x, int y) {
		return poly.contains(x, y);
	}
	public boolean isInside(double x, double y) {
		return poly.contains(x, y);
	}
	private void addPoints (int x, int y) {
		poly.addPoint(x, y);
	}
	
}
