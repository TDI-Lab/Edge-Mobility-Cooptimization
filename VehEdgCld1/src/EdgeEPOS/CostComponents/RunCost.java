package EdgeEPOS.CostComponents;

public class RunCost {
	//epos cost
	public int betaindex;
	public double beta;
	public int numberOfPlans;
	public int optRun;//optimal index of beta
	
	public double localCost;
	public double nLocalCost;
	public double globalCost;
	public double nGlobalCost;
	
	//method cost
	public int run;
	public int methodType;
	
	/**one EPOS simulation
	 * 
	 * @param betaindex
	 * @param Beta
	 * @param planNumber
	 * @param localCost
	 * @param globalCost
	 * @param optin
	 */
	public RunCost(int betaindex, double Beta, int planNumber, double localCost, double globalCost, int optin) {
		this.betaindex = betaindex;
		this.beta = Beta;
		numberOfPlans = planNumber;
		this.localCost = localCost;
		this.globalCost = globalCost;
		optRun = optin;
		
	}

	public int getRun() {
		return betaindex;
	}

	public RunCost(int run, int methodType, double value2, double value3) {
        this.run = run;
        this.methodType = methodType;
        this.localCost = value2;
        this.globalCost = value3;
    }
}
