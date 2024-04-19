package EdgeEPOS.CostComponents;

import java.util.List;

import fog.entities.FogDevice;
import EdgeEPOS.PlacementMethods.Agent;
import EdgeEPOS.PlacementMethods.Plan;
import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.MobileARservice;
import EdgeEPOS.Utility.ArrayFiller;


/**
 * @author rooyesh
 * checks the Equations 3-4, 12-13
 *
 */
public class ComProcessDepStorCost {

	/**
	 * https://www.techtarget.com/whatis/Breaking-Down-the-Cost-of-Cloud-Computing
     * https://aws.amazon.com/lambda/pricing/:
     * dollar cost of processing per request; $0.20 per 1 million requests, edge: $0.60 per 1M requests
     */
    //double ProcesingCost = 0.0000002; 
    /**
     * https://aws.amazon.com/s3/pricing/:
     * dollar per GB per month , $0.021-$0.023 per GB per month
     */
    //double StorageCost = 0.023;//0.023 (GB-month)	2628000 
    /**
     * https://aws.amazon.com/ec2/pricing/on-demand/
     * dollar per GB in each direction (0.01, 0.09)
     */
    //double TransCost = 0.01;
    
    /* https://www.clickittech.com/devops/aws-lambda-pricing/
     * Memory (MB)	Price per 1ms
		128	$0.0000000021
		512	$0.0000000083
     */
    List <MobileARservice> serviceList;
    List<FogDevice> serverCloudlets;
	
	public ComProcessDepStorCost(List <MobileARservice> serviceList, List<FogDevice> serverCloudlets){
		
		this.serviceList = serviceList;
	    this.serverCloudlets = serverCloudlets;

	    	
	}
    
	
	/**
     * Calculates the total cost (processing + storage + RAM + communication
     * + deployment + violation) for cloud servers and fog nodes for the
     * duration of `time`
	 * @param vehicleAPs 
     */
	public double calProcStorDepCommCost(Plan p, double [] costs, List<MetricsForVehisFromIntermediateAP> vehicleAPs, boolean[] _backup_dep) {
		
		boolean [] dep_back = new boolean[Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
		
    	//System.out.println("deployment back");
    	for (int j = 0; j < Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS; j++) {
		    dep_back[j] = _backup_dep[j];
		    //System.out.println(j+" "+dep_back[j]);
    	}
		
        //cost of processing 
    	if (Constants.APPLIEDCOST[1] == 1) {
    		costs[0] = costPro(p);
	        
    	}
    	
        // cost of storage 
    	if (Constants.APPLIEDCOST[2] == 1) {
    		costs[1] = costStor (p);
    	
	    }
    	
    	// cost of Memory 
    	if (Constants.APPLIEDCOST[3] == 1) {
	       costs[2] = costMem(p);
	    }
    	
        // cost of communication between fog nodes, and fog and cloud nodes
    	if (Constants.APPLIEDCOST[4] == 1) {
	    	
	        costs[3] = costCom(p, vehicleAPs);
    	}
		
    		
        //cost of container deployment, for cloud == 0
        if (Constants.APPLIEDCOST[5] == 1) {
	        costs[4] = costDep(p, dep_back);
        }
        
        Double c = (costs[4] + costs[3] + costs[2] + costs[1] + costs[0]);
        
        return c;
    }


	private double costCom(Plan p, List<MetricsForVehisFromIntermediateAP> vehicleAPs) {
		int NUM_SERVICES= serviceList.size();
		double costC = 0;
		
		for (int a = 0; a < NUM_SERVICES; a++) {
    		if (serviceList.get(a).isValid())
    			costC += costC(vehicleAPs.get(a).ConnectionTime, p.y[a], serviceList.get(a).getRequestPerSec(), 
                		serviceList.get(a).getRequestSize(), serviceList.get(a).getResponseSize(),
                		vehicleAPs.get(a).ConnectedAPs);
        }
        
		return costC;
	}


	private double costDep(Plan p, boolean[] dep_back) {
		
		int NUM_SERVICES= serviceList.size();
		double costD = 0;
		/*
		 * if (p.isFF()) { for (int a = 0; a < NUM_SERVICES; a++) { if (dep_back[p.y[a]]
		 * == false) {//As we have one service type we just check if the candidate host
		 * of the service is already hosting the service or not if
		 * (serviceList.get(a).isValid()) { costD += costD(p.y[a],
		 * serviceList.get(a).getStorageDemand()); dep_back[p.y[a]] = true;
		 * //System.out.println("	cost deployment "+costDep); } } }
		 * 
		 * } else {
		 */	
			for (int a = 0; a < NUM_SERVICES; a++) {
	            if (dep_back[p.y[a]] == false) {//As we have one service type we just check if the candidate host of the service is already hosting the service or not 
	            	if (serviceList.get(a).isValid())  {  
		            	costD += costD(p.y[a], serviceList.get(a).getStorageDemand());
		                dep_back[p.y[a]] = true; //System.out.println("	cost deployment "+costDep);
	                }
	            }
	        }
		//}
		return costD;
	}


	private double costMem(Plan p) {
		double time = Constants.TAU;// the time duration this run lasts (the interval between two consecutive run of EPOS) in second
		int NUM_SERVICES= serviceList.size();
		double costM = 0;
		
		 for (int a = 0; a < NUM_SERVICES; a++) {
	        	if (serviceList.get(a).isValid())
	                    costM += costM(time, p.y[a], serviceList.get(a).getMemDemand());
         }
	           
		return costM;
	}


	private double costStor(Plan p) {

		double time = Constants.TAU;// the time duration this run lasts (the interval between two consecutive run of EPOS) in second
		int NUM_SERVICES= serviceList.size();
		double costS = 0;
		
		for (int a = 0; a < NUM_SERVICES; a++) {
        	if (serviceList.get(a).isValid())
                    costS += costS(time, p.y[a], serviceList.get(a).getStorageDemand());
        }
		return costS;
	}


	private double costPro(Plan p) {
		
		double time = Constants.TAU;// the time duration this run lasts (the interval between two consecutive run of EPOS) in second
		int NUM_SERVICES= serviceList.size();
		double costP = 0;
		
		for (int a = 0; a < NUM_SERVICES; a++) {
    		if (serviceList.get(a).isValid())
                costP += costP(time, p.y[a], serviceList.get(a).getRequestPerSec());
		}
		return costP;
	}


	public double calProcStorDepCommCostAfterDeployment(Plan gPlan, List<Agent> agents, double[] costs, boolean[] deploy_backup) {
	
		boolean [] dep_back = new boolean[Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS];
		
    	//System.out.println("deployment back");
    	for (int j = 0; j < Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS; j++) {
		    dep_back[j] = deploy_backup[j];
		    //System.out.println(j+" "+dep_back[j]);
    	}
		
        //cost of processing 
    	if (Constants.APPLIEDCOST[1] == 1) {
    		costs[0] = costPro(gPlan);
	        
    	}
    	
        // cost of storage 
    	if (Constants.APPLIEDCOST[2] == 1) {
    		costs[1] = costStor (gPlan);
    	
	    }
    	
    	// cost of Memory 
    	if (Constants.APPLIEDCOST[3] == 1) {
	       
	        costs[2] = costMem(gPlan);
	    }
    	
    	// cost of communication between fog nodes, and fog and cloud nodes
    	if (Constants.APPLIEDCOST[4] == 1) {
	    	costs[3] = costComGP(gPlan, agents);
    	}
    	
    	//cost of container deployment, for cloud == 0
        if (Constants.APPLIEDCOST[5] == 1) {
	        
	        costs[4] = costDep(gPlan, dep_back);
        }
        
        
        Double c = (costs[4] + costs[3] + costs[2] + costs[1] + costs[0]);
        
        return c;
    
	}
	
	public double costComGP(Plan p, List<Agent> agents) {

		int NUM_SERVICES;
		double costC = 0;
	
			
			for (int i = 0; i < Constants.EPOS_NUM_AGENT; i++) {
				NUM_SERVICES = agents.get(i).serviceList.size();
			
				if (NUM_SERVICES != 0){
					for (int a = 0; a < NUM_SERVICES; a++) {
						int sId = agents.get(i).serviceList.get(a).getServicId();
						costC += costC(agents.get(i).vehicleConnectedAPsPerRunAgent.get(a).ConnectionTime, p.y[sId], agents.get(i).serviceList.get(a).getRequestPerSec(), 
								agents.get(i).serviceList.get(a).getRequestSize(), agents.get(i).serviceList.get(a).getResponseSize(),
	                		agents.get(i).vehicleConnectedAPsPerRunAgent.get(a).ConnectedAPs);
					}
				}
			}
	        
	        return costC;
		
	}
	
   

	/**
     * Calculates the cost of processing in a particular server for a
     * particular service for the duration of `time`
     *
     * @param time duration for which the cost of processing in the host is
     * being calculated
     * @param j the index of fog node
     * @param L_P required amount of processing for service `a` per request, (in
     * million instructions per request)
     * @param lambda_in incoming traffic rate to fog node j for service `a`
     * (request/second)
     * @return returns the cost of processing in the node j for the duration of
     * `time`
     */
    public double costP(double time, int j, double CPU_ReqPerSec) {
        return Constants.UNIT_PROC_COST[j] * CPU_ReqPerSec * time;
    }

   
    /**
     * Calculates the cost of service storage in a particular fog server  for the duration of `time`
     *
     * @param time duration of time in second
     * @param j the index of server
     * @param L_S storage size of service, in Megabytes
     */
    public double costS(double time, int j, double L_S) {
        return Constants.UNIT_STOR_COST[j] * (L_S/1000) * (time/2628000);//2628000: conversion from month to second
    }

    /**
     * Calculates the cost of Memory in a particular server for a
     * particular service for the duration of `time`
     *
     * @param time duration of time in second
     * @param k the index of cloud server
     * @param a the index of service
     * @param L_M Memory size of service a, in Megabytes
     */
    public double costM(double time, int k, double L_M) {
        return Constants.UNIT_MEM_COST[k] * (L_M) * (time * 1000);
    }
    
     /**
     * Calculates the cost of communication between nodes
     *
     * @param propagTime duration of time
     * @param j the index of fog node
     * @param a the index of service
     * @param reqPerSec rate of traffic for service a to the host fog server (request/second) j
     * @param connectedAPs index of the access point through which the traffic for service a is
     * routed to host node j
     */
     public double costC(List<Double> connectionTime, int j, double reqPerSec, int l_rq, int l_rp, List<Short> connectedAPs) {
    	 int k = 0;
         double cost = 0;
         
         while(k < connectedAPs.size()) {
         	
         	//mediateApId = connectedAPs.get(k);//connecting access points to the host node
         	//serverCloudletIndex = Distances.theClosestServerCloudletToAp(serverCloudlets, apDevices.get(mediateApId));
     		
         	cost += Constants.UNIT_COMM_COST[j][connectedAPs.get(k)] * reqPerSec * (l_rp + l_rq)/8000000 * connectionTime.get(k)/1000;//bit to GB
         	//if (connectedAPs.get(k) == j)
         		//System.out.println("k "+k+" " +connectedAPs.get(k)+ " "+j);
     		k++;
     		
     				
     	}
     	return cost;
     	
    }

	/**
     * Calculates the cost of service deployment (from cloud) on a particular fog node
     *
     * @param j the index of the fog node
     * @param storage storage size of service a, in Megabytes
     */
    public double costD(int j, double storage) {
           	double deployCost = 0;
       		deployCost += storage/1000 * Constants.UNIT_COMM_COST[Constants.NUM_EDGE_ROUTERS+Constants.NUM_BACKBONE_ROUTERS-1][j] ;
       		//System.out.printf("	cost deployment %.12f",COMM_UNIT_COST[j][serverCloudlets.size()-1]);
            
       	return deployCost;
       
    }
    
    public static double[] getUNIT_PROC_COST() {
		return Constants.UNIT_PROC_COST;
	}

    public static double[] getUNIT_STOR_COST() {
		return Constants.UNIT_STOR_COST;
	}

	public static double[][] getUNIT_COMM_COST() {
		return Constants.UNIT_COMM_COST;
	}
    
    
    }
