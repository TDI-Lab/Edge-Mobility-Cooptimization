package EdgeEPOS.Setting;

import java.util.Arrays;
import java.util.List;

//import com.sun.org.apache.bcel.internal.classfile.Constant;

import EdgeEPOS.Utility.ArrayFiller;
import EdgeEPOS.Utility.Utility;
import fog.entities.ApDevice;
import fog.entities.FogDevice;

public class Constraint {
	private int run;
	private static int numNodes;
	private boolean ONOF = false;
	private boolean TARGET = false;
	
	public double [] hardPlan;
    public double [] hardPlanFactor;
    
	private double inProcLoad;
	private double inMemLoad;
	private double inStorLoad;
	
	double pTotalCap = 0, mTotalCap = 0, sTotalCap = 0;
	private boolean CapFactor = false;
	private int onNodeCount;
	
	public Constraint(int nodes, boolean onof, boolean target, boolean capacity){
		numNodes = nodes;
		ONOF = onof;
		TARGET = target;
		CapFactor = capacity;
	}
	
	public Constraint(int nodes, boolean onof, boolean target, boolean capacity, int onNodes) {
		numNodes = nodes;
		ONOF = onof;
		TARGET = target;
		CapFactor = capacity;
		onNodeCount = onNodes;
		
	}

	/*generates hard plan vector and adjust network/nodes capacity based on the input workload in every run of EPOS
	 * @param run
	 * @param inProcLoad
	 * @param inMemLoad
	 * @param intStorLoad
	 */
	public void setTrficDemand(int run, double inProcLoad, double inMemLoad, double intStorLoad){
		this.run = run;
		this.inProcLoad = inProcLoad;
		this.inMemLoad = inMemLoad;
		this.inStorLoad = intStorLoad;
	}
	
	public void setAgentsConstraintAdaptively() {

		if (ONOF) {
    		
			hardPlan = new double [2*numNodes];
		    hardPlanFactor = new double [2*numNodes];
		    
	    	int eIndex = selectEnoughRsrc(Constants.cpuUtilRatio, Constants.memUtilRatio, Constants.stoUtilRatio, inProcLoad, inMemLoad, inStorLoad);
	    	eIndex++;
	    	int r1 = 0, r2 = eIndex ;//80
			int r3 = r2, r4 = numNodes;
	    	double maxUtil = 0.05, zeroUtil = 0;//can be ignored
	    	int maxFact = 1, zeroFact = 3;
	    	
	    	Arrays.fill(hardPlan, r1, r2, maxUtil);
	    	Arrays.fill(hardPlan, r1+numNodes, r2+numNodes, maxUtil);
	    	Arrays.fill(hardPlan, r3, r4, zeroUtil);
	    	Arrays.fill(hardPlan, r3+numNodes, r4+numNodes, zeroUtil);
	    	hardPlan[numNodes-1] = maxUtil;
	    	hardPlan[2*numNodes-1] = maxUtil;
	    	
	    	Arrays.fill(hardPlanFactor, r1, r2, maxFact);
	    	Arrays.fill(hardPlanFactor, r3, r4, zeroFact);
	    	Arrays.fill(hardPlanFactor, r1+numNodes, r2+numNodes, maxFact);
	    	Arrays.fill(hardPlanFactor, r3+numNodes, r4+numNodes, zeroFact);
	    	hardPlanFactor[numNodes-1] = maxFact;
	    	hardPlanFactor[2*numNodes-1] = maxFact;
	    	
	    	ArrayFiller.adapt1DArrayWithArrays(Constants.FP, Constants.FM, Constants.FS, Constants.FP_back, Constants.FM_back, Constants.FS_back, 0, Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS-2, hardPlan);
	    	//cloud:
	    	Constants.FP[numNodes-1] = Constants.FP_back[numNodes-1]; 
	    	Constants.FM[numNodes-1] = Constants.FM_back[numNodes-1];
	    	Constants.FS[numNodes-1] = Constants.FS_back[numNodes-1];
	    	
	    	Utility.printTotCap(pTotalCap, mTotalCap, sTotalCap, eIndex);
	    	
	    	Utility.writeHardConstraints(ONOF, hardPlan, hardPlanFactor);

		}
		if (CapFactor) {
			hardPlan = new double [2*numNodes];
		  
			int eIndex = selectEnoughRsrc(0.05, 0.05, 0.05, inProcLoad, inMemLoad, inStorLoad);
	    	eIndex++;
	    	int r1 = 0, r2 = eIndex ;//80
			int r3 = r2, r4 = numNodes;
	    	double maxUtil = 0.05, zeroUtil = 0;//can be ignored
	    	
	    	Arrays.fill(hardPlan, r1, r2, maxUtil);
	    	Arrays.fill(hardPlan, r1+numNodes, r2+numNodes, maxUtil);
	    	Arrays.fill(hardPlan, r3, r4, zeroUtil);
	    	Arrays.fill(hardPlan, r3+numNodes, r4+numNodes, zeroUtil);
	    	hardPlan[numNodes-1] = maxUtil;
	    	hardPlan[2*numNodes-1] = maxUtil;
	    	
	    	ArrayFiller.adapt1DArrayWithArrays(Constants.FP, Constants.FM, Constants.FS, Constants.FP_back, Constants.FM_back, Constants.FS_back, 0, Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS-2, hardPlan);
	    	//cloud:
	    	Constants.FP[numNodes-1] = Constants.FP_back[numNodes-1]; 
	    	Constants.FM[numNodes-1] = Constants.FM_back[numNodes-1];
	    	Constants.FS[numNodes-1] = Constants.FS_back[numNodes-1];
	    	
	    	Utility.printTotCap(pTotalCap, mTotalCap, sTotalCap, eIndex);
	    	
		}
		
		if(TARGET) {
			Constants.gcFunc ="RMSE";
			Utility.writeRNVector(Constants.SC_RenewablePortion);
	        
		}
		
	}

	private int selectEnoughRsrc(double a, double b, double c, double inProcLoad, double inMemLoad, double inStorLoad) {
		
		boolean flag = false;
		int i = 0;
		pTotalCap = 0;
		mTotalCap = 0;
		sTotalCap = 0;
		
		while (!flag) {
			
				pTotalCap += Constants.FP_back[i] * a ;
				mTotalCap += Constants.FM_back[i] * b;
				sTotalCap += Constants.FS_back[i] * c;
				
				System.out.println(i+" pTotalCap "+pTotalCap+" mTotalCap "+mTotalCap+" sTotalCap "+sTotalCap);
				
				if((inProcLoad < pTotalCap)&&(inMemLoad < mTotalCap)&&(inStorLoad < sTotalCap))
					flag = true;
				else
					i++;
		}

		if (!flag) {
			System.out.println("not enough resources available in the network! Debug");
			return -1;
		}
		else 
			return i;
	}

	/**
	 * @param a list of network nodes with their allocated capacity
	 * @param b list of network on nodes
	 * @param capRatio capacity ration of allocated nodes
	 * @return
	 */
	public boolean setAgentsConstraintRandomly(double[] a, int[] b, double capRatio) {

		double[] P = new double[Constants.numNodes];
		double[] M = new double[Constants.numNodes];
		double[] S = new double[Constants.numNodes];
		
		if (checkResources(a, P, M, S, capRatio)) {
	    		
				ArrayFiller.adapt1DArrayWithArrays(Constants.FP, Constants.FM, Constants.FS, Constants.FP_back, Constants.FM_back, Constants.FS_back, 0, Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS-2, P, M, S);
		    	//cloud:
		    	Constants.FP[numNodes-1] = Constants.FP_back[numNodes-1]; 
		    	Constants.FM[numNodes-1] = Constants.FM_back[numNodes-1];
		    	Constants.FS[numNodes-1] = Constants.FS_back[numNodes-1];
		    	
		    	Utility.printTotCap(pTotalCap, mTotalCap, sTotalCap, onNodeCount);//which nodes???
		    	return true;
			}
			else{
				return false;
			}
		
	}
	
	
	public boolean checkResources(double[] a, double[] p, double[] m, double[] s, double capRatio){
		
		boolean flag = true;
		pTotalCap = 0;
		mTotalCap = 0;
		sTotalCap = 0;
		
		for (int i = 0; i < a.length; i++) {
			if (a[i] != 0.0) {
				p[i] = inProcLoad * a[i]/Constants.FP_back[i];//100*0.02/200
				pTotalCap += p[i] * Constants.FP_back[i];
				
				m[i] = inMemLoad * a[i]/Constants.FM_back[i];
				mTotalCap += m[i] * Constants.FM_back[i];
				
				s[i] = inStorLoad * a[i]/Constants.FS_back[i] ;
				sTotalCap += s[i] * Constants.FS_back[i];
				
				if((1 < p[i])||(1 < m[i])||(1 < s[i])) {
					flag = false;
					break;
				}
			}	
		}

		if (!flag) {
			System.out.println("Not enough resources available in the network! Debug");
			return false;
		}
		else {
			return true;
		}
	}
	

}
