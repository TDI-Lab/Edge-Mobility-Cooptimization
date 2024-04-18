package EdgeEPOS.CostComponents;

import EdgeEPOS.Setting.Constants;

public class LocalCostUnits {

	
		private int canHost = -1;		//host index
		private double totCost = 0;
        double[] normCosts;				//normalized costs
        double[] costs;					//real costs
        
        public LocalCostUnits(int j, double cost) {
        	this.canHost = j;
        	this.totCost = cost;//max value
        	normCosts = new double[Constants.APPLIEDCOST.length-2];
        	costs = new double[Constants.APPLIEDCOST.length-2];
        	
		}

        public void calTotCost() {
        	for (int i = 0; i < (normCosts.length - 2); i++) {
            	this.totCost += normCosts[i];
            	//System.out.println("totcost:"+totCost+" "+i+" "+normCosts[i]);
            	
            }
		}

    	@Override
    	public String toString() {
    		return "LocalCostUnits [getDlViolCost()=" + getDlViolCost() + ", getProcCost()=" + getProcCost()
    				+ ", getStorCost()=" + getStorCost() + ", getMemCost()=" + getMemCost() + ", getCommCost()="
    				+ getCommCost() + ", getDeplCost()=" + getDeplCost() + ", getEnergyCost()=" + getEnergyCost()
    				+ ", getCo2EmitCost()=" + getCo2EmitCost() + ", getTotCost()=" + getTotCost() + "]";
    		
    	}
    		
		public String getNormCosts() {
			String costs = "norms:";
		
			for (int i = 0; i < (normCosts.length - 2); i++) 
				costs += " "+normCosts[i];
            	
			return costs;

		}

		public String getCosts() {
			String cost = "costs:";
			
			for (int i = 0; i < (normCosts.length - 2); i++) 
				cost += " "+costs[i];
            	
			return cost;
		}
		
		public double getDlViolCost() {
			return normCosts[0];
		}

		public void setDlViolCost(double dlViolCost) {
			this.normCosts[0] = dlViolCost;
		}

		public double getProcCost() {
			return normCosts[1];
		}

		public void setProcCost(double procCost) {
			this.normCosts[1] = procCost;
		}

		public double getStorCost() {
			return normCosts[2];
		}

		public void setStorCost(double storCost) {
			this.normCosts[2] = storCost;
		}

		public double getMemCost() {
			return normCosts[3];
		}

		public void setMemCost(double memCost) {
			this.normCosts[3] = memCost;
		}

		public double getCommCost() {
			return normCosts[4];
		}

		public void setCommCost(double commCost) {
			this.normCosts[4] = commCost;
		}

		public double getDeplCost() {
			return normCosts[5];
		}

		public void setDeplCost(double deplCost) {
			this.normCosts[5] = deplCost;
		}

		public double getEnergyCost() {
			return normCosts[6];
		}

		public void setEnergyCost(double energyCost) {
			this.normCosts[6] = energyCost;
		}

		public double getCo2EmitCost() {
			return normCosts[7];
		}

		public void setCo2EmitCost(double co2EmitCost) {
			this.normCosts[7] = co2EmitCost;
		}

        public int getIndex() {
            return canHost;
        }

        public double getTotCost() {
            return totCost;
        }
    
 	 
}
