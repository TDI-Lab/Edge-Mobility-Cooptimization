package exp4cap;

import java.util.ArrayList;

import org.apache.commons.math3.util.Pair;

public class CapToDem {

	ArrayList<Pair<Double, Double>> confidenceIntervals = new ArrayList<>();//6 2-pair of [l,g] for all methods
	 
	private int profileNum = 35;
	private int samples = 10;
	public double[] lCosts;
	public double[] gCosts;
	public int nodenum;
	public double cap2dem;
	
	double [] lMean = new double[6];
    double [] gMean = new double[6];
    
	public double[] lCosts0;
	public double[] gCosts0;
	public double[] lCosts1;
	public double[] gCosts1;
	public double[] lCosts2;
	public double[] gCosts2;
	public double[] lCosts3;
	public double[] gCosts3;
	public double[] lCosts4;
	public double[] gCosts4;
	public double[] lCosts5;
	public double[] gCosts5;

	
	public CapToDem(int nodenum, double cap2dem) {
		this.nodenum = nodenum;
		this.cap2dem = cap2dem;
		
		lCosts = new double[6];
		gCosts = new double[6];
		
		lCosts0 = new double[350];
		gCosts0 = new double[350];
		
		lCosts1 = new double[350];
		gCosts1 = new double[350];
		
		lCosts2 = new double[350];
		gCosts2 = new double[350];
		
		lCosts3 = new double[350];
		gCosts3 = new double[350];
		
		lCosts4 = new double[350];
		gCosts4 = new double[350];
		
		lCosts5 = new double[350];
		gCosts5 = new double[350];
	}

	public void addCost(int i, double gcost, double lcost) {
		lCosts[i] += lcost;
		gCosts[i] +=gcost;
	}
	
	public void addCost(int i, double gcost, double lcost, int j) {
		lCosts[i] += lcost;
		gCosts[i] +=gcost;
		
		switch (i) {
	    case 0:
	        lCosts0[j] = lcost;
	        gCosts0[j] = gcost;
	        break;
	    case 1:
	        lCosts1[j] += lcost;
	        gCosts1[j] += gcost;
	        break;
	    case 2:
	        lCosts2[j] = lcost;
	        gCosts2[j] = gcost;
	        break;
	    case 3:
	        lCosts3[j] = lcost;
	        gCosts3[j] = gcost;
	        break;
	    case 4:
	        lCosts4[j] = lcost;
	        gCosts4[j] = gcost;
	        break;
	    case 5:
	        lCosts5[j] = lcost;
	        gCosts5[j] = gcost;
	        
	    
		}

			
	}
    
	public void calAvg() {

		for (int i = 0; i<6; i++) {
			lCosts[i] /= (profileNum*samples);
			gCosts[i] /= (profileNum*samples);
		}
		
	}
	
	public void calculateConfidenceIntervals() {

        double alpha = 0.05; // Significance level

        // Calculate the standard error
        double [] std_l_variance = new double[6];
        double [] std_g_variance = new double[6];
        
        
        calculateStandardError(std_l_variance, std_g_variance);

        for (int i = 0; i < lMean.length; i++) {
            // Calculate critical value (z-score)
            double criticalValue = 1.96; // For alpha = 0.05 (two-tailed test)
            // Calculate margin of error
            double lmarginOfError = criticalValue * std_l_variance[i];
            double gmarginOfError = criticalValue * std_g_variance[i];
            
            // Calculate confidence interval
            double l_lowerBound = lMean[i] - lmarginOfError;
            double l_upperBound = lMean[i] + lmarginOfError;
            
            double g_lowerBound = gMean[i] - gmarginOfError;
            double g_upperBound = gMean[i] + gmarginOfError;
            
            confidenceIntervals.add(new Pair<>(l_lowerBound, l_upperBound));
            confidenceIntervals.add(new Pair<>(g_lowerBound, g_upperBound));
        }
        
    }
 
 
	// Method to calculate the standard error
	 public void calculateStandardError(double [] std_l_variance, double [] std_g_variance) {
	     
		 int methods = 6;
		 double sumOfSquaredDeviations = 0;
	     
         double [] g_sumOfSquaredDeviations = new double[6];
         double [] l_sumOfSquaredDeviations = new double[6];
         double [] l_variance = new double[6];
         double [] g_variance = new double[6];
         
         for (int i = 0; i < 6; i++) {
             lMean[i] = lCosts[i] / (profileNum*samples);
	         gMean[i] = gCosts[i] / (profileNum*samples);
         }
	     
         // Calculate the sum of squared deviations
         
	      for (int i = 0; i < (profileNum*samples); i++) {
	             l_sumOfSquaredDeviations[0] += Math.pow(lCosts0[i] - lMean[0], 2);
	             g_sumOfSquaredDeviations[0] += Math.pow(gCosts0[i] - gMean[0], 2);
	             
	             l_sumOfSquaredDeviations[1] += Math.pow(lCosts1[i] - lMean[1], 2);
	             g_sumOfSquaredDeviations[1] += Math.pow(gCosts1[i] - gMean[1], 2);
	             
	             l_sumOfSquaredDeviations[2] += Math.pow(lCosts2[i] - lMean[2], 2);
	             g_sumOfSquaredDeviations[2] += Math.pow(gCosts2[i] - gMean[2], 2);
	            
	             l_sumOfSquaredDeviations[3] += Math.pow(lCosts3[i] - lMean[3], 2);
	             g_sumOfSquaredDeviations[3] += Math.pow(gCosts3[i] - gMean[3], 2);
	            
	             l_sumOfSquaredDeviations[4] += Math.pow(lCosts4[i] - lMean[4], 2);
	             g_sumOfSquaredDeviations[4] += Math.pow(gCosts4[i] - gMean[4], 2);
	            
	             l_sumOfSquaredDeviations[5] += Math.pow(lCosts5[i] - lMean[5], 2);
	             g_sumOfSquaredDeviations[5] += Math.pow(gCosts5[i] - gMean[5], 2);
	            
	         }
         
     	
	
	     // Calculate the variance and standard error
	      for (int i = 0; i < methods; i++) {
              	g_variance[i] = g_sumOfSquaredDeviations[i] / (profileNum*samples);
	    	  	l_variance[i] = l_sumOfSquaredDeviations[i] / (profileNum*samples);
	    	  	
	    	  	std_l_variance[i] = Math.sqrt(l_variance[i] / (profileNum*samples));
	    	  	std_g_variance[i] = Math.sqrt(g_variance[i] / (profileNum*samples));
	      }
	
	 }
		
	
		
	

}
