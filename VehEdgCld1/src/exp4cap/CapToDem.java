package exp4cap;

public class CapToDem {

private int profileNum = 35;
	
	public double[] lCosts;
	public double[] gCosts;
	public int nodenum;
	public double cap2dem;
	
	public CapToDem(int nodenum, double cap2dem) {
		this.nodenum = nodenum;
		this.cap2dem = cap2dem;
		lCosts = new double[6];
		gCosts = new double[6];
	}

	public void addCost(int i, double gcost, double lcost) {
		lCosts[i] += lcost;
		gCosts[i] +=gcost;
	}
    
	public void calAvg() {

		for (int i = 0; i<6; i++) {
			lCosts[i] /= profileNum*10;
			gCosts[i] /= profileNum*10;
			
		}
		
	}

	public void calAvg(int l) {

		for (int i = 0; i<6; i++) {
			lCosts[i] /= profileNum*9;
			gCosts[i] /= profileNum*9;
			
		}
		
	}

}
