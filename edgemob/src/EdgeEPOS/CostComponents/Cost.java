package EdgeEPOS.CostComponents;

import java.lang.management.GarbageCollectorMXBean;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//import org.apache.commons.collections4.splitmap.AbstractIterableGetMapDecorator;
import org.apache.commons.math3.analysis.function.Constant;
import fog.entities.ApDevice;
import fog.entities.FogDevice;
import fog.entities.MobileDevice;
import org.fog.localization.Coordinate;
import org.fog.localization.Distances;

import EdgeEPOS.PlacementMethods.Plan;
import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.MobileARservice;



/**
 *
 * @author rooyesh
 *
 * This class contains all the functions and parameters that are related to the
 * cost
 */
public class Cost {

	public Plan agentPlan;
	private FogDevice fogServer;
	public List <MobileARservice> serviceList;
	public List<FogDevice> serverCloudlets;
	public List<ApDevice> apDevices;
	private int Time;
	private Violation v;
	private ComProcessDepStorCost pc;
	private ServiceDelay sd;
	private EnergyCost ec;
    //private static int samplingInterval = (int) (Constants.TAU / (Constants.SAMPLENUMBERS+1)); //tau = 60 sec --> samplingInterval = 15sec
    
    
    /**
     * Constructor of the cost class. The input parameters are number of cloud
     * servers, fog nodes, and services. This constructor will initializes the
     * unit cost parameters.
     * @param vehicleAPs2 
     * @param costComProcDep 
     * @param sD 
     * @param eC 
     *
     * @param NUM_CLOUD_SERVERS
     * @param NUM_FOG_NODES
     */
    public Cost(FogDevice fogserver, List<ApDevice> apdevices, List<FogDevice> servercloudlets, List<MobileARservice> servicelist, ComProcessDepStorCost costComProcDep, ServiceDelay sD, EnergyCost eC) {
    	
    	//connectedVehicleArray = connectedVehicules.toArray(new MobileDevice[connectedVehicules.size()]);
  		pc = costComProcDep;  
  		sd = sD;
    	ec = eC; 
    	  

      this.fogServer = fogserver;
  	  this.serverCloudlets = servercloudlets;
  	  this.serviceList = servicelist;
  	  this.apDevices = apdevices;
	  	  
       }

    
    public void calcLocalCost(Plan p, int currentTime, List<MetricsForVehisFromIntermediateAP> vehicleConnectedAPsPerRun, boolean[] _local_deploy) {
    	 
    	 Time = currentTime;
    	 double[] PCCDcosts = new double[5];//saves costs for processing, storage, Memory, deployment, communication
    	 double dlCost = 0, procStorDeplCommCost = 0, energyCost = 0, emissionCost = 0;
    	 
    	 procStorDeplCommCost = pc.calProcStorDepCommCost(p, PCCDcosts, vehicleConnectedAPsPerRun, _local_deploy);//it does not change _local_deploy 
    	 
    	 if (Constants.APPLIEDCOST[0] != 0) {
    		 dlCost = Constants.APPLIEDCOST[0] * calDLViolationCost(p, vehicleConnectedAPsPerRun, _local_deploy);//it changes _local_deploy in service delay calculation; but this is a temp array limited to this agent
    		// System.out.printf("Cost of delay in services: %.12f",dlCost);
    	 }
    	 
    	 
    	 if (Constants.APPLIEDCOST[6] == 1)
    		 energyCost = ec.calEnergyCost(p, vehicleConnectedAPsPerRun);
    	 
    	 if (Constants.APPLIEDCOST[7] == 1)
    		 emissionCost = ec.getCO2Cost();
    	
    	 p.setDlViolCost(dlCost);
    	 p.setProcCost(PCCDcosts[0]);
    	 p.setStorCost(PCCDcosts[1]);
    	 p.setMemCost(PCCDcosts[2]);
    	 p.setCommCost(PCCDcosts[3]);
    	 p.setDeplCost(PCCDcosts[4]);
    	 p.setEnergyCost(energyCost);
    	 p.setCo2EmitCost(emissionCost);
    	 p.setTotalLocalCost();
    	
    	 
//    	 System.out.println("cost of dl violation: "+dlCost);
//    	 System.out.println("cost of processing: "+PCCDcosts[0]);
//    	 System.out.println("cost of deployment: "+PCCDcosts[3]);
//    	 System.out.println("cost of com: "+PCCDcosts[4]);
//    	 System.out.println("cost of mem: "+PCCDcosts[2]);
//    	 System.out.println("cost of storage: "+PCCDcosts[1]);
//    	 System.out.println("cost of power: "+energyCost);
//    	 System.out.println("cost of emmision: "+emissionCost);
//    	 
    	 
    	 //System.out.println("	total cost: "+p.getTotalLocalCost()+" tot "+ (dlCost + procStorDeplCommCost + energyCost));
    	 //return p.getTotalLocalCost();
    }
  
    
    /**dep is not updated
     * claculates the local cost components for first fit.  
     * @param mobileARservice
     * @param a service index
     * @param j host index
     * @param p local plan
     * @param currentTime
     * @param vehicleConnectedAPsPerRun
     * @param dep
     * @param unitLocalCost 
     * @param indexValuePair 
     * @param indexValuePair 
     * @return
     */
    public void calcUnitCost(MobileARservice mobileARservice, int a, int j, Plan p, int currentTime, MetricsForVehisFromIntermediateAP vehicleConnectedAPsPerRun, LocalCostUnits unitLocalCost) {
   	 
	   	Time = currentTime;
	   	int depFact = 0;
	   	
	   	if (p.deployPlan[j])
    		depFact = 0;
    	else
    		depFact = 1;
	    
	   	//double dlCost = 0, procStorDeplCommCost = 0, energyCost = 0, emissionCost = 0;
	   	 
	   	 if (Constants.APPLIEDCOST[0] == 1) {
	   		calDelayAndRate(j, vehicleConnectedAPsPerRun);
	   		sd.initialize(p);
			sd.calcServiceDelayPriori(a, j, vehicleConnectedAPsPerRun, p.deployPlan[j]);//dep from previous agent
	   		v = new Violation();
	   		unitLocalCost.setDlViolCost(Constants.APPLIEDCOST[0] * v.violCostPerService(mobileARservice, v.violPerService(vehicleConnectedAPsPerRun, mobileARservice)));
	   		//unitLocalCost.setDlViolCost(totalUnitCost); //System.out.printf("Cost of delay in services: %.12f",dlCost);
	   	 }
	   	//ProcCost[1], StorCost, MemoryCost, CommCost, DeplCost[5]
	   	double pro = Constants.APPLIEDCOST[1] * pc.costP(Constants.TAU, j, mobileARservice.getRequestPerSec());
	   	double sto = Constants.APPLIEDCOST[2] * pc.costS(Constants.TAU, j, mobileARservice.getStorageDemand());
	   	double mem = Constants.APPLIEDCOST[3] * pc.costM(Constants.TAU, j, mobileARservice.getMemDemand());
	   	double com = Constants.APPLIEDCOST[4] * pc.costC(vehicleConnectedAPsPerRun.ConnectionTime, j, mobileARservice.getRequestPerSec(), mobileARservice.getRequestSize(), mobileARservice.getResponseSize(), vehicleConnectedAPsPerRun.ConnectedAPs);
	   	double depc = Constants.APPLIEDCOST[5] * depFact * pc.costD(j, mobileARservice.getStorageDemand());
	   			
	   	unitLocalCost.setProcCost(pro);
	   	unitLocalCost.setMemCost(mem);
	   	unitLocalCost.setStorCost(sto);
	   	unitLocalCost.setCommCost(com);
	   	unitLocalCost.setDeplCost(depc);
	   	
	   	if (Constants.APPLIEDCOST[6] == 1) {
	   		double en = ec.eUnit(j, mobileARservice, p.utilPlan[j], vehicleConnectedAPsPerRun);
	   		unitLocalCost.setEnergyCost(en);
	   	}
	   	
	   	if (Constants.APPLIEDCOST[7] == 1) {
	   		double co = ec.getCO2Cost();
	   		unitLocalCost.setCo2EmitCost(co);
	   	}
	}
 
 
  
	 /**calculates the deadline-violation cost for the plan p
	 * @param p
	 * @param vehicleConnectedAPsPerRun 
	 * @param _backup_deploy 
	 * @return
	 */
	private double calDLViolationCost(Plan p, List<MetricsForVehisFromIntermediateAP> vehicleConnectedAPsPerRun, boolean[] _backup_deploy) {
	  
		double violCost = 0;
		
	    expectedLinkDelayAndTranmiRate(p, vehicleConnectedAPsPerRun);
	    waitingTime(p, vehicleConnectedAPsPerRun, _backup_deploy);
	  
	    v = new Violation();
	    violCost = v.costViol(vehicleConnectedAPsPerRun, serviceList);
	  
	return violCost;
}


	/* Finds different edge nodes connecting source vehicles to their candidate hosts for one candidate plan
	 * then calculates the average of propagation delay for one vehicle based on its connecting links 
	 * @param p plan
	 * @param vehicleConnectedAPsPerRun 
	 * @param avgPropagDelay average propagation delay for the link (j,j') or (j,k)
	 */
	public void expectedLinkDelayAndTranmiRate(Plan p, List<MetricsForVehisFromIntermediateAP> vehicleConnectedAPsPerRun) {
 	   	
		int i;
	    for (i = 0 ; i<serviceList.size() ; i++) {
 	    	calDelayAndRate(p.y[i], vehicleConnectedAPsPerRun.get(i));
 	    	
 	   }
			
  	}
	
	
	private void calDelayAndRate(int host, MetricsForVehisFromIntermediateAP metricsForVehisFromIntermediateAP) {
		metricsForVehisFromIntermediateAP.PropagDelayTransRate(host);
			
	}


	/* calculates the propagation time plus waiting time for each service based on the queueing theory
	 * @param p
	 * @param vehicleConnectedAPsPerRun 
	 * @param waitingTimes 
	 * @param propagDelay
	 * @param waitingTime
	 */
	private void waitingTime (Plan p, List<MetricsForVehisFromIntermediateAP> vehicleConnectedAPsPerRun, boolean[] _backup_deploy) {
		sd.initialize(p);
		for (int i = 0; i<serviceList.size(); i++) {
			   //System.out.println("service "+i);
			   sd.calcServiceDelay(i, p.y[i], vehicleConnectedAPsPerRun.get(i), _backup_deploy);
	 		
		  }
	 	    	
 	}
 	     
  	 }
