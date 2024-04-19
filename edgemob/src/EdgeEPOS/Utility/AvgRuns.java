package EdgeEPOS.Utility;

import EdgeEPOS.Setting.Constants;

public class AvgRuns {

	
	public double [] allNodesCapRatio;
	double totalCPUCap;
	double totalMemCap;
	double totalStoCap;
	double totCapRatio;
	public int[] onNodes;
	public int isValid = 1;
	
	public AvgRuns(int[] nodes, double capRatio) {
		allNodesCapRatio = new double [Constants.numNodes];
		onNodes = new int[nodes.length];
		totCapRatio = capRatio;
		for (int j = 0 ; j<nodes.length; j++) {
			onNodes[j] = nodes[j];
			allNodesCapRatio[nodes[j]] = capRatio;//0.8/30=0.026
			totalCPUCap += Constants.FP_back[nodes[j]]*allNodesCapRatio[nodes[j]];
			totalMemCap += Constants.FM_back[nodes[j]]*allNodesCapRatio[nodes[j]];
			totalStoCap += Constants.FS_back[nodes[j]]*allNodesCapRatio[nodes[j]];
		}
	}

	public int getIsValid() {
		return isValid;
	}

	public void setIsValid(int isValid) {
		this.isValid = isValid;
	}
	
	
}
