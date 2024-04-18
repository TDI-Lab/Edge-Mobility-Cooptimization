package EdgeEPOS.CostComponents;

import java.util.List;

import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.MobileARservice;

/**
 *
 * @author rooyesh
 * Checks Equation 11
 * This class contains the functions and variables related to calculating SLA
 * violation
 */
public class Violation {
	double violCostL99p5EG99 = 0.1;
	double violCostL99EG95 = 0.3;
	double violcostL95 = 1;
	/**
     * Calculate delay Violation Percentage. (Percentage of IoT requests that do
     * not meet the delay requirement for service a 
     * 
     */
	public double Vper[];
	
	
    public Violation(){
    	
        
	}
    

   
    /**
     * Calculates the cost of delay violations for a plan in the time period of `Tau`
     *
     */
    public double costViol(List<MetricsForVehisFromIntermediateAP> vehicleAPs, List <MobileARservice> serviceList) {
    	
    	double totalViolCost = 0;
    	calcViolationPerc(vehicleAPs, serviceList);
    	for (int i = 0; i<serviceList.size(); i++) {
    		totalViolCost += violCostPerService(serviceList.get(i), Vper[i]);
	    	//totalViolCost += violcost;
	    }
    	
    	return totalViolCost;
    }
    
    public double violCostPerService(MobileARservice mobileARservice, double vper) {
    	
    	double violPerc = Math.max(0, 1 - vper);
		double violcost = 0;
		
		if (violPerc < 0.95) {
			violcost = violPerc * mobileARservice.getRequestPerSec() * violcostL95 * Constants.SLAPenaltyCredit * Constants.TAU;
		}
		else if((violPerc < 0.99)&&(violPerc >= 0.95)) {
			violcost = violPerc * mobileARservice.getRequestPerSec() * violCostL99EG95 * Constants.SLAPenaltyCredit * Constants.TAU;
			
		}
		else if((violPerc < 0.995)&&(violPerc >= 0.99)) {
			violcost = violPerc * mobileARservice.getRequestPerSec() * violCostL99p5EG99 * Constants.SLAPenaltyCredit * Constants.TAU;
		
		}
		return violcost;
	}



	/**
     * calculates the percentage of the delay violations for all services in a plan
     */
    public void calcViolationPerc(List <MetricsForVehisFromIntermediateAP> vehicleAP, List <MobileARservice> serviceList) {
    	
    	Vper = new double [serviceList.size()];
    	
        for (int i = 0; i < serviceList.size(); i++) {       	//System.out.println("calculate violation rate for service "+j+" : size waitingTimePerAp: "+vehicleAP.get(j).waitingTimePerAp.size()+" size ConnectedTime: "+vehicleAP.get(j).ConnectedTime.size());
        	Vper[i] += violPerService(vehicleAP.get(i), serviceList.get(i));
        }
    }   
  
    public double violPerService(MetricsForVehisFromIntermediateAP vehicleAP, MobileARservice a) {
    
    	double vPer, diff, vPercPerService = 0;
    	
    	for (int k = 0; k < vehicleAP.waitingTimePerAp.size(); k++) {//next access points
    		//System.out.println("viol "+vehicleAP.waitingTimePerAp.get(k)+" "+vehicleAP.getDeployTime()+" "+vehicleAP.ConnectionTime.get(k)+" "+a.getDeadline());
    		
    		if(vehicleAP.getDeployTime() != 0) {//service not deployed already
		    		
					if(vehicleAP.getDeployTime() >= vehicleAP.ConnectionTime.get(k)) {//service not deployed yet
		    			
						 vPer = vehicleAP.ConnectionTime.get(k)/Constants.TAU;
		            	 vehicleAP.violPercPerAp.set(k, vPer);
		            	 vPercPerService += vPer;
		            	 diff = vehicleAP.getDeployTime() - vehicleAP.ConnectionTime.get(k);
		            	 vehicleAP.setDeployTime(diff);
		            	
		    		}
		            else {	//DeployTime() < ConnectedTime
		            	
		        	    	 vPer = vehicleAP.getDeployTime()/Constants.TAU;//add the remained DeployTime to Vper 
			            	 vehicleAP.violPercPerAp.add(k, vPer);
			            	 vPercPerService += vPer;
			            	 
			            	 diff = vehicleAP.ConnectionTime.get(k) - vehicleAP.getDeployTime();
					         //now service is completely deployed
			            	 
			            	 if (vehicleAP.waitingTimePerAp.get(k) > a.getDeadline()) {//checks with the delay threshold
			            		 vPer += 1 * (diff)/Constants.TAU;
				            	 vehicleAP.violPercPerAp.add(k, vPer);
				            	 vPercPerService += vPer;
				            	 
			            	   } 
			            	 else //Deadline for the remaining time is not violated
			            	 {
				            	}
			            	   
			            	 vehicleAP.setDeployTime(0);
		    		}
				}
				
				else {//DeployTime() == 0
					if (vehicleAP.waitingTimePerAp.get(k) > a.getDeadline()) {//deadline violated
		        		 vPer = vehicleAP.ConnectionTime.get(k)/Constants.TAU;
		            	 vehicleAP.violPercPerAp.add(k, vPer);
		            	 vPercPerService += vPer;
		            	 } 
		        	else {//deadline not violated
		        		 vPercPerService += 0;
		            	 vehicleAP.violPercPerAp.add(k, 0.0);
		        	    }
		       }
	
    		
    	}//end for
    	//System.out.println("vioperc "+vPercPerService);
    	return vPercPerService;
}

}
