package EdgeEPOS.CostComponents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Utility.Utility;

public class ComboCosts {
	
	//first fit
	private int agent;
	int srvIndex;
	public List<LocalCostUnits> neighbors;
	private double min, max;
	private final static double SMOOTHING_NUMBER = 0.000000000001d; // used so that we will not have absolute 0

	
	//epos
	private double lmin, gmin, lmax, gmax; 
    public List <RunCost> Runs;
	int [] poPoints;
	
	/**
	 * Pareto optimal run among different Runs
	 */
	int poFileIndex;			
	
	/**
	 * Pareto optimal run index among different Runs
	 */
	public int optRun;
	double minX;
	double minY;
	private int type;
	
	/**EPOS costs
	 * @param type
	 */
	public ComboCosts(int type){
		Runs = new ArrayList<RunCost>(Constants.betaConfigSize);		// One profile and different "epos runs" with different beta values
		poPoints = new int [Constants.betaConfigSize];					// Output list - pareto optimal points per profile
		this.type = type;
	}

	/**
	 * First Fit cost for every neighbour
	 */
	public ComboCosts(int a, int agent){
		neighbors = new ArrayList<LocalCostUnits>();
	    this.srvIndex = a;
	    this.agent = agent;
	}

	/**
	 * sorts the list of neighbours in terms of local cost in an ascending order
	 */
	public void sort() {
		
		normalizeCosts(1);
		Collections.sort(neighbors, Comparator.comparingDouble(o -> o.getTotCost()));
		// Create a list of sorted indices
	    List<Integer> sortedIndices = new ArrayList<>();
	    for (LocalCostUnits pair : neighbors) {
	         sortedIndices.add(pair.getIndex());
	         //System.out.println(pair.getIndex()+""+pair.getCosts()+":"+pair.getNormCosts());
	     }
		//System.out.println(sortedIndices);
	}
	 /**
     * Normalizes traffic values for all time-stamps, such that the each traffic
     * element (per fog node) is not going to be large for the fog queues
     */
    private void normalizeCosts() {
        
    	findMinAndMax();
        
    	for (int i = 0; i < Runs.size(); i++) {
        	if (lmax == 0) {
        		System.out.println("lbreak");
                break;
        	}
        	Runs.get(i).nLocalCost = Runs.get(i).localCost ;
        	Runs.get(i).localCost = (Runs.get(i).localCost - lmin) / (lmax - lmin) ;
        	System.out.println("lcos="+Runs.get(i).localCost+" , lmin "+ lmin +" lmax "+lmax);
            
        }    
        for (int i = 0; i < Runs.size(); i++) {
        	if (gmax == 0) {
        		System.out.println("gbreak");
                break;
        	}
        	Runs.get(i).nGlobalCost = Runs.get(i).globalCost  ;
        	Runs.get(i).globalCost = (Runs.get(i).globalCost - gmin) / (gmax - gmin) ;
        	System.out.println("gcos="+Runs.get(i).globalCost+" , gmin "+ gmin +" gmax "+gmax);
            
        }    
        
    }

    
    /**
     * Normalizes every component of local cost for every neighbour
     * What to do with the double.Maxvalue?
     */
    public void normalizeCosts(int ff) {
    	
    	for (int j = 0; j<Constants.APPLIEDCOST.length-2 ; j++) {
    		if (Constants.APPLIEDCOST[j] == 1) {
    			findMinAndMax(j);
    	
    			//System.out.println("j "+j+" max: "+max+" min "+min);
	        	
		    	for (int i = 0; i < neighbors.size(); i++) {
		        	if ((max == 0)||(max == min)) {
		        		neighbors.get(i).costs[j] = neighbors.get(i).normCosts[j] ;
			        	break;
		        	}
		        	
		        	neighbors.get(i).costs[j] = neighbors.get(i).normCosts[j] ;
		        	neighbors.get(i).normCosts[j] = (neighbors.get(i).normCosts[j] - min) / (max - min) ;
		        	//System.out.println("swap:"+neighbors.get(i).costs[j]+" "+neighbors.get(i).normCosts[j]);
		        	
		        }    
		        
    		}
    	}
    	
    	for (int i = 0; i < neighbors.size(); i++) {
    		neighbors.get(i).calTotCost();
    	//	System.out.println("after-totcost:"+neighbors.get(i).getTotCost()+" "+neighbors.get(i).getProcCost());
    	}
        
    }
    
    private void findMinAndMax(int cindex) {
    	
    	min = Double.MAX_VALUE;
        max = Double.MIN_VALUE; 
        
        for (int i = 0; i <neighbors.size(); i++) {
            if (neighbors.get(i).normCosts[cindex] < min) {
                min = neighbors.get(i).normCosts[cindex];
            }
            if (neighbors.get(i).normCosts[cindex] > max) {
                max = neighbors.get(i).normCosts[cindex];
            }
        }
       
    }
    	
    /**
     * finds the minimum and maximum of costs (internal parameters for min and max are updated)
     *
     */
    private void findMinAndMax() {
    	
        lmin = Double.MAX_VALUE; gmin = lmin;
        lmax = Double.MIN_VALUE; gmax = lmax; 
        
        for (int i = 0; i <Runs.size(); i++) {
            if (Runs.get(i).localCost < lmin) {
                lmin = Runs.get(i).localCost;
            }
            if (Runs.get(i).localCost > lmax) {
                lmax = Runs.get(i).localCost;
            }
            if (Runs.get(i).globalCost < gmin) {
                gmin = Runs.get(i).globalCost;
            }
            if (Runs.get(i).globalCost > gmax) {
                gmax = Runs.get(i).globalCost;
            }
        }
        System.out.print("\nCost normalization: lmin="+lmin+", lmax="+lmax);
        System.out.println(", gmin="+gmin+", gmax="+gmax);
        
    }
    

	/**
	 * @param run 
	 * @return optimal run number
	 */
	public int OptPoint(int run) {
		
		int optIndex = 0;	
		
		if (run != 0)
			normalizeCosts();
		
		Utility.writeOverallGC(type, Runs, run, Constants.EPOS_NUM_PLANS); 
        
		System.out.println("-----------------------------------------------");
		System.out.println("Pareto Optimal:");
		
		int OptSetSize = FindOptimalSet();//number of pareto points
		
		if (OptSetSize == 1) {
			optIndex = poPoints[0];
			return Runs.get(poPoints[0]).betaindex; 
		}
		else //finds the best optimal point
		{
			double [] disToMean = new double[OptSetSize];
			double minRunDis = 500 ;// a big number 
			
			//minY = Runs.get(poPoints[0]).GlobalCost;
			 
			for (int j=0 ; j < OptSetSize ; j++){
				
				/*
				 * disToMean[j] = (Runs.get(poPoints[j]).LocalCost - minX) +
				 * (Runs.get(poPoints[j]).GlobalCost - minY); if (disToMean[j] < minRunDis) {
				 * minRunDis = disToMean[j]; minRunIndex = j; }
				 */ 
				
				 disToMean[j] = Math.sqrt(Math.pow(Runs.get(poPoints[j]).localCost , 2) + Math.pow(Runs.get(poPoints[j]).globalCost , 2));
				 System.out.println("j "+j+" lc "+Runs.get(poPoints[j]).localCost+" gc "+Runs.get(poPoints[j]).globalCost+" dis="+disToMean[j]+" betain "+Runs.get(poPoints[j]).betaindex+" optrun "+Runs.get(poPoints[j]).optRun);
				 if (disToMean[j] < minRunDis) {
					 minRunDis = disToMean[j];
					 optIndex = j;
					 System.out.println("j "+j+" optIndex "+optIndex);
						
				 }
				 
			 }
			 System.out.println("Final optindex "+optIndex+" dis="+minRunDis);
			 optRun = poPoints[optIndex];
			 poFileIndex = Runs.get(poPoints[optIndex]).betaindex;
			}
		System.out.println("Optimal epos run (beta index): "+poFileIndex);
		return poFileIndex; 
	}

	/**
	 * @return the number of pareto optimal points and their index (uses a sorted array based on x:local-cost)
	 */
	public int FindOptimalSet() {
		
		Sort(Runs);
		
		minX = Runs.get(0).localCost;
		
		int i = 0; 
		poPoints[i++] = 0;
		double currMinY = Runs.get(0).globalCost;
		
		for (int runindex = 1; runindex <Runs.size(); runindex++) {//finds the pareto optimal points and count them (i)
		  if (Runs.get(runindex).globalCost < currMinY) {
		     
			  poPoints[i++] = runindex;
		      currMinY = Runs.get(runindex).globalCost;
		  }
		}
		minY = currMinY;	
		
		return i;
	}
	
	
	
	/**
	 * @param runs2
	 * sorts runs based on local-cost in ascending order
	 */
	private void Sort(List<RunCost> runs2) {
		
		double ltemp, gtemp, btemp, ngc, nlc;
		int itemp, ptemp, ortemp;
		
		for (int j=0 ; j < (Constants.betaConfigSize-1) ; j++){
		        for (int k=j+1 ; k < Constants.betaConfigSize ; k++){
		            if (runs2.get(k).localCost < runs2.get(j).localCost){
		            	ltemp = runs2.get(j).localCost;
		            	itemp = runs2.get(j).betaindex;
		            	btemp = runs2.get(j).beta;
		            	ptemp = runs2.get(j).numberOfPlans;
		            	gtemp = runs2.get(j).globalCost;
		            	ortemp = runs2.get(j).optRun;
		            	ngc = runs2.get(j).nGlobalCost;
		            	nlc = runs2.get(j).nLocalCost;
		            	
		                runs2.get(j).localCost = runs2.get(k).localCost;
		                runs2.get(j).betaindex = runs2.get(k).betaindex;
		                runs2.get(j).beta = runs2.get(k).beta;
		                runs2.get(j).numberOfPlans = runs2.get(k).numberOfPlans;
		                runs2.get(j).globalCost = runs2.get(k).globalCost;
		                runs2.get(j).optRun = runs2.get(k).optRun;
		                runs2.get(j).nGlobalCost = runs2.get(k).nGlobalCost;
		                runs2.get(j).nLocalCost =  runs2.get(k).nLocalCost;
		                
		                runs2.get(k).localCost = ltemp;
		                runs2.get(k).betaindex = itemp;
		                runs2.get(k).beta = btemp;
		                runs2.get(k).numberOfPlans = ptemp;
		                runs2.get(k).globalCost = gtemp;
		                runs2.get(k).optRun = ortemp;
		                runs2.get(k).nGlobalCost = ngc;
		                runs2.get(k).nLocalCost = nlc; 
		                
		            }    
		        }
		    }
	}
	
}
