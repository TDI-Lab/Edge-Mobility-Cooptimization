package EdgeEPOS.CostComponents;

import java.util.List;

import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.MobileARservice;
import EdgeEPOS.Utility.ArrayFiller;

/**
 *
 * @author rooyesh
 *
 * This class contains the functions and variables related to calculating SLA
 * violation
 */
public class Violation {

    /**
     * Calculate delay Violation Percentage. (Percentage of IoT requests that do
     * not meet the delay requirement for service a (V^%_a))ˇ
     *
     * @param a the index of the service
     * @param method
     * 
     */
	
	public static int timePeriod;
    public double Vper[];
	private double basePercentage;
	
    public Violation(){
    	
    	timePeriod = Constants.TAU;//second
    	basePercentage = (double) 1/(Constants.SAMPLENUMBERS+2);
  	    
	}
    /**
     * calculates the percentage of the delay violations for a particular service per second
     *
     * @param a the index of the service
     * @param method
     */
    
    public void calcViolationPerc(List <Double[]> waitingTime, List <MobileARservice> serviceList) {
    	int j, k;
    	Vper = new double [serviceList.size()];

        for (j = 0; j < serviceList.size(); j++) {
        	for (k = 0; k<waitingTime.get(j).length; k++) {
	            if (waitingTime.get(j)[k] > serviceList.get(j).getsDeadline()) {
	            	 Vper[j] += 1 * basePercentage;
	            } else {
	            	 Vper[j] += 0;
	            }
	            
        	}
        	// System.out.println(" Vper "+Vper[j]);
        }
      
       
    }

   
    /**
     * Calculates the cost of delay violations for a particular fog node for a
     * given service in the duration of `time`
     *
     * @param time the duration of the time
     * @param a the index of service
     * @param j the index of fog node
     * @param Vper_a the percentage of IoT service delay samples of service a
     * that do not meet the delay requirement.
     * @param q desired quality of service for service a
     * @param lambda_in incoming traffic rate from IoT nodes to fog node j for
     * service a (request/second)
     */
    
    public double costViol(List <Double[]> waitingTimes, List <MobileARservice> serviceList) {
    	
    	double violcost =  0;
    	double totalViolCost = 0;
    	calcViolationPerc(waitingTimes, serviceList);
        
    	for (int i = 0; i<serviceList.size(); i++) {
    		violcost = Math.max(0, Vper[i] - (1 - serviceList.get(i).getQoS())) * serviceList.get(i).getRequestPerSec() * serviceList.get(i).getPenalty() * timePeriod;
    		totalViolCost += violcost;
    		
    		/*System.out.print("i "+i);
    		System.out.printf(" viol %.5f",violcost);
    		System.out.println(" Vper "+Vper[i]+" qos "+serviceList.get(i).getQoS()+" "+ serviceList.get(i).getPenalty() +" "+ timePeriod);
    		*/
    	}
    	//System.out.printf(" totviol %.5f\n", (totalViolCost / serviceList.size()));
    	return totalViolCost / serviceList.size();
    }
    
 
    //not needed yet
    /**
     * Calculates the percentage of the delay violations for a particular service
     *
     * @param a the index of the service
     * @param method
     */
    private void getViolationPercentage() {
        for (int i = 0; i< Vper.length; i++)
        	Vper[i] *= 100;
    }


    /**not needed???
     * Calculates the average percentage of delay violations of all services
     *
     * @param method
     */
    public double getViolationPercentage(List <Double[]> waitingTime, List <MobileARservice> serviceList) {
    	
    	double sum = 0;
    	calcViolationPerc(waitingTime, serviceList);
        
    	for (int a = 0; a <serviceList.size() ; a++) {
            sum += Vper[a] * 100;
        }
        return (sum / serviceList.size());
    }

    
}
