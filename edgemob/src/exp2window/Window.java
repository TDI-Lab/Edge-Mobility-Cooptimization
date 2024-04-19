package exp2window;

public class Window {
	private int winId = -1;		//host index
	private double avgTraffic = 0;
	private double firstPro;
	private int profileNum = 35;
	
	public double[] lCosts;
	public double[] gCosts;
	
	public Window(int winId, double avgTraffic, double pro1) {
		this.winId = winId;
		this.avgTraffic = avgTraffic;
		firstPro = pro1;
		lCosts = new double[6];
		gCosts = new double[6];
	}

	public double getAvgTraffic() {
		return avgTraffic;
	}

	public int getWinId() {
		return winId;
	}

	public double getFirstPro() {
		return firstPro;
	}

	public void addCost(int i, double gcost, double lcost) {
		lCosts[i] += lcost;
		gCosts[i] +=gcost;
	}
    
	public void calAvg() {

		for (int i = 0; i<6; i++) {
			lCosts[i] /= profileNum;
			gCosts[i] /= profileNum;
			
		}
		
	}
}
