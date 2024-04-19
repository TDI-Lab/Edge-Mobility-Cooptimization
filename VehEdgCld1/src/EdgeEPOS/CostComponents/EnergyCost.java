package EdgeEPOS.CostComponents;

import java.util.List;

import fog.entities.ApDevice;
import fog.entities.FogDevice;
import EdgeEPOS.PlacementMethods.Agent;
import EdgeEPOS.PlacementMethods.Plan;
import EdgeEPOS.Setting.Constants;
import EdgeEPOS.Setting.MobileARservice;
import EdgeEPOS.Utility.ArrayFiller;


/**
 * @author rooyesh
 * Checks Equations 14-16 for energy and 17 for carbon footprint
 * 1 Watt = 1 Joule per second 
 *
 */
public class EnergyCost {
	
	public List <MetricsForVehisFromIntermediateAP> vehicleAPs;
	public List <MobileARservice> servicelist;
	
	
	/**
	 * total power consumption of fog-cloud infrastructure
	 */
	//private double total_power = 0;
	
	/**
	 * non-renewable power consumption of fog-cloud infrastructure
	 */
	private double nrnPower = 0;
	private double total_Energy_Cost = 0;
	
	
	public EnergyCost(List<MobileARservice> serviceList) {
		this.servicelist = serviceList;
	}
	

	public double getCO2Cost() {
		
		double co2 = (Constants.TAU * Constants.Cco2 * Constants.R * nrnPower);
		//System.out.println("nrn "+nrnPower+" co "+co2);
		return co2;//in second of power
		
	}
	
	/**
	 * @param p candidate plan
	 * @param vehicleConnectedAPsPerRun
	 * @return energy cost for an agent
	 */
	public double calEnergyCost(Plan p, List<MetricsForVehisFromIntermediateAP> vehicleConnectedAPsPerRun) {
			// total_power = 0;
			nrnPower = 0;
			total_Energy_Cost = ServerCost(p) + NetCost(p, vehicleConnectedAPsPerRun);
			return total_Energy_Cost;
		
	}
	
	/**
	 * @param gPlan
	 * @param agents
	 * @param eNCostParts 
	 * @param gc
	 * @return energy cost for the global plan as an array including [server, serverNet, net]
	 */
	public double calEnergyCost(Plan gPlan, List<Agent> agents, double[] eNCostParts, boolean gc) {
			
			nrnPower = 0;
			eNCostParts[0] = ServerCost(gPlan);
			eNCostParts[1] = NetCostHosts(gPlan);
			total_Energy_Cost = eNCostParts[0] + eNCostParts[1];
			
			for (int i = 0; i<agents.size(); i++)
				eNCostParts[2] += NetCost(gPlan, agents.get(i));
			
			total_Energy_Cost += eNCostParts[2];
			
			return total_Energy_Cost;
	
	}
	
	/**
	 * @param gPlan global plan
	 * @param agent
	 * @return consumed energy due to the APs on the route
	 */
	private double NetCost(Plan gPlan, Agent agent) {
		short ap;
		double e_network_cost = 0;
		double p_network = 0;
		double p_OnThePath_AP_router;
		
		
		for (int k = 0; k<agent.getServiceSize(); k++) {
			int hostIndex = gPlan.y[agent.serviceList.get(k).getServicId()];
			//if host >=edge nodes
			for (int j = 0; j < agent.vehicleConnectedAPsPerRunAgent.get(k).ConnectedAPs.size(); j++) {
				ap = agent.vehicleConnectedAPsPerRunAgent.get(k).ConnectedAPs.get(j);
				
				//if (ap != hostIndex) {
				 
					p_OnThePath_AP_router = pRouter(agent.serviceList.get(k), ap);
							
					e_network_cost +=  eRouter(agent.vehicleConnectedAPsPerRunAgent.get(k).ConnectionTime.get(j), p_OnThePath_AP_router, ap);
									  
					p_network += (agent.vehicleConnectedAPsPerRunAgent.get(k).ConnectionTime.get(j)/Constants.TAU) * p_OnThePath_AP_router;
					
					nrnPower += p_OnThePath_AP_router * (agent.vehicleConnectedAPsPerRunAgent.get(k).ConnectionTime.get(j)/Constants.TAU) * (1 - Constants.SC_RenewablePortion[ap]);
				
					//System.out.println("after "+agent.vehicleConnectedAPsPerRunAgent.get(k).ConnectionTime.get(j)+" "+p_OnThePath_AP_router+" "+e_network_cost);
						
				//}
				//else {//service is hosted on the server of its directly connected AP
				//}
		
			}
		}
		//update_TotalPower(p_network);

		return e_network_cost;

	}

	
	/**measures the networking energy cost for time interval Tau
	 * @param p
	 * @param vehicleConnectedAPsPerRun
	 * @return
	 */
	public double NetCost(Plan p, List<MetricsForVehisFromIntermediateAP> vehicleConnectedAPsPerRun) {
		
		short ap;
		double e_network_cost = 0;
		double p_AP_router = 0, p_network = 0;
		double p_OnThePath_AP_router;
		
		e_network_cost = NetCostHosts(p);
		
		
		//consumed energy due to the APs on the route:
		for (int k = 0; k<p.y.length; k++) {
			int hostIndex = p.y[k];
			for (int j = 0; j < vehicleConnectedAPsPerRun.get(k).ConnectedAPs.size(); j++) {
				ap = vehicleConnectedAPsPerRun.get(k).ConnectedAPs.get(j);
				
				//if (ap != hostIndex) {
				 
					p_OnThePath_AP_router = pRouter(servicelist.get(k), ap);
							
					e_network_cost +=  eRouter(vehicleConnectedAPsPerRun.get(k).ConnectionTime.get(j), p_OnThePath_AP_router, ap);
									  
					p_network += (vehicleConnectedAPsPerRun.get(k).ConnectionTime.get(j)/Constants.TAU) * p_OnThePath_AP_router;
					
					nrnPower += p_OnThePath_AP_router * (vehicleConnectedAPsPerRun.get(k).ConnectionTime.get(j)/Constants.TAU) * (1 - Constants.SC_RenewablePortion[ap]);
				//}
				//else {//service is hosted on the server of its directly connected AP
					
				//}
		
			}
		}
		//update_TotalPower(p_network);
		return e_network_cost;
	}
	
	
	/**
	 * @param p
	 * @param vehicleConnectedAPsPerRun
	 * 
 	 * @return calculates consumed energy part of networking equipments associated with the hosts including edge/core router
	 */
	private double NetCostHosts(Plan p) {
		
		double e_network_cost = 0;
		double p_AP_router = 0, p_network = 0;
		
		if (p.gp == 0) {//candidate plan
			for (int k = 0; k<p.y.length; k++) {
					int hostIndex = p.y[k];
					if (hostIndex == (Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS -1)||(hostIndex < Constants.NUM_EDGE_ROUTERS))//switch and edge router of cloud already has been processed in host part
						continue;
					
					p_AP_router = pRouter(servicelist.get(k), hostIndex);
					
					e_network_cost += eRouter(Constants.TAU, p_AP_router, hostIndex);
					
					p_network += p_AP_router;
					
					//update_TotalPower(p_network);
					nrnPower += (1 - Constants.SC_RenewablePortion[hostIndex]) * p_AP_router;
						
					}
			
		}
		else {//global plan
			for (int k = 0; k<p.y.length; k++) {
				int hostIndex = p.y[k];
				if (servicelist.get(k).isValid()) {
					//switch and edge router of cloud already has been processed in host part 
					//if the host is an access point the energy is already calculated
					if (hostIndex == (Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS -1)||(hostIndex < Constants.NUM_EDGE_ROUTERS))
						continue;
					
					p_AP_router = pRouter(servicelist.get(k), hostIndex);
							
					e_network_cost += eRouter(Constants.TAU, p_AP_router, hostIndex);
					
					p_network += p_AP_router;
					
					//update_TotalPower(p_network);
					nrnPower += (1 - Constants.SC_RenewablePortion[hostIndex]) * p_AP_router;
						
					}
				else {
					
				}
			}
		}
			return e_network_cost;
					
	}


	/**
	 * @param p plan  
	 * @return energy consumption of servers (dynamic part which depends on the workload amount)
	 */
	public double ServerCost(Plan p) {
		
		double e_dynamic_cost = 0, p_cloud_switch, p_cloud_server;
		double p_dynamic = 0;
		
		if (p.gp == 0) {//candidate plan
			for (int k = 0; k<p.y.length; k++) {//fog servers
				int hostIndex = p.y[k];
				if (p.energyPlan[hostIndex]==0) {
					
					if (hostIndex != Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS -1){// check if it is cloud
						
						e_dynamic_cost += eServerCost(Constants.TAU, hostIndex, p.utilPlan[hostIndex]); 
						
						nrnPower += nrnPow(hostIndex, p.utilPlan[hostIndex]);
						
						p_dynamic += (Constants.SC_MaxPow[hostIndex] - Constants.SC_IdlePow[hostIndex]) * p.utilPlan[hostIndex];
						
								
					}
					else {//cloud node
						p_cloud_switch = pSwitch(servicelist.get(k), hostIndex);
						 
						p_cloud_server = pServer(hostIndex, p.utilPlan[hostIndex]);
						
						e_dynamic_cost += eCloud(Constants.TAU , hostIndex, p_cloud_switch , p_cloud_server); 
								
								
						p_dynamic += Constants.PUE * (p_cloud_switch + p_cloud_server);
						nrnPower += Constants.PUE * (p_cloud_switch + p_cloud_server) * (1 - Constants.SC_RenewablePortion[hostIndex]);
								
					}
					p.energyPlan[hostIndex]=1;
				}
			}
		}
			
		else {//global plan
			for (int k = 0; k<p.y.length; k++) {//fog servers
				int hostIndex = p.y[k];
				if ((servicelist.get(k).isValid()) && (p.energyPlan[hostIndex]==0)) {
						if (hostIndex != Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS -1){// check if it is cloud
							
							e_dynamic_cost += eServerCost(Constants.TAU, hostIndex, p.utilPlan[hostIndex]);
							
							p_dynamic += pServer(hostIndex, p.utilPlan[hostIndex]);
							
							nrnPower += nrnPow(hostIndex, p.utilPlan[hostIndex]);
							
						}
						else {//cloud node
							p_cloud_switch = pSwitch(servicelist.get(k), hostIndex);
									
							p_cloud_server = pServer(hostIndex, p.utilPlan[hostIndex]);
							
							e_dynamic_cost += eCloud(Constants.TAU , hostIndex, p_cloud_switch , p_cloud_server); 
							
							
							p_dynamic += Constants.PUE * (p_cloud_switch + p_cloud_server);
							nrnPower += Constants.PUE * (p_cloud_switch + p_cloud_server) * (1 - Constants.SC_RenewablePortion[hostIndex]);
									
						}
						p.energyPlan[hostIndex] = 1;
					
			}
			else {
				
			}
		}
		}
			
		//update_TotalPower(p_dynamic);
		return e_dynamic_cost;
		
	}

	private double eCloud(double time, int hostIndex, double p_cloud_switch, double p_cloud_server) {
		return time * Constants.PUE * (
				(Constants.NRNElectricityCost * (1 - Constants.SC_RenewablePortion[hostIndex]) * (p_cloud_switch + p_cloud_server))
				+
				Constants.RNCOST * Constants.SC_RenewablePortion[hostIndex] *(p_cloud_switch + p_cloud_server)
				);
		
	}

	/**
	 * @param mobileARservice
	 * @param hostIndex
	 * @return power consumption of access point/router
	 */
	private double pRouter(MobileARservice mobileARservice, int hostIndex) {
		 double power = (mobileARservice.getRequestPerSec())*
				(Constants.Net_Pow_ul[hostIndex] * mobileARservice.getRequestSize() + Constants.Net_Pow_dl[hostIndex] * mobileARservice.getResponseSize())/Constants.nj2j;
		 
		 return power;
		
	}
	
	private double eRouter(double time, double p_AP_router, int hostIndex) {
		return (time * p_AP_router * ((Constants.NRNElectricityCost) * (1 - Constants.SC_RenewablePortion[hostIndex])
				+ (Constants.RNCOST * Constants.SC_RenewablePortion[hostIndex])));
		
	}
	
	public double pServer(int hostIndex, double u) {
		return (Constants.SC_MaxPow[hostIndex] - Constants.SC_IdlePow[hostIndex]) * u;
		
	}


	private double pSwitch(MobileARservice mobileARservice, int hostIndex) {
		return (mobileARservice.getRequestPerSec())* (Constants.Net_Pow_ul[hostIndex] * mobileARservice.getRequestSize() + Constants.Net_Pow_dl[hostIndex] * mobileARservice.getResponseSize())/Constants.nj2j;
		//j/sec
	}


	public double nrnPow(int hostIndex, double u) {
		return ((1 - Constants.SC_RenewablePortion[hostIndex])*(Constants.SC_MaxPow[hostIndex] - Constants.SC_IdlePow[hostIndex]) * u);
		
	}


	private double eServerCost(double time, int hostIndex, double u) {
		return (time * Constants.NRNElectricityCost) * (1 - Constants.SC_RenewablePortion[hostIndex]) * 
		(Constants.SC_MaxPow[hostIndex] - Constants.SC_IdlePow[hostIndex]) * u
				+
		(time * Constants.RNCOST) * (Constants.SC_RenewablePortion[hostIndex]) * 
		(Constants.SC_MaxPow[hostIndex] - Constants.SC_IdlePow[hostIndex]) * u;
		
	}
		
	private double pServerNonRenewable(int hostIndex, double u) {
		return (1 - Constants.SC_RenewablePortion[hostIndex]) * 
				(Constants.SC_MaxPow[hostIndex] - Constants.SC_IdlePow[hostIndex]) * u;
				
	}
	
	/**calculate the costs of deploying a service on a candidate host
	 * @param j
	 * @param mobileARservice
	 * @param util: total util of the host
	 * @param vehicleConnectedAPsPerRun
	 * @return
	 */
	public double eUnit(int j, MobileARservice mobileARservice, double util, MetricsForVehisFromIntermediateAP vehicleConnectedAPsPerRun) {
		int ap;
		double e_cost = 0;
		nrnPower = 0;
		double eserver = 0, enetserver = 0, eap = 0;
		int cloud = Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS -1;
		
		double expectedUtilForHost = (mobileARservice.getCpuDemand1() + (util*Constants.FP[j])) / (Constants.FP[j]);//update utilization considering this service is deployed on this host
        
		//server energy cost
		if (j == cloud) {
			double p_switch = pSwitch(mobileARservice, j);
			double p_server = pServer(cloud, expectedUtilForHost);
			eserver = eCloud(Constants.TAU, cloud, p_switch, p_server);
			nrnPower += Constants.PUE * (1 - Constants.SC_RenewablePortion[j]) * (p_switch + p_server);
		}
		else {                 
			eserver = eServerCost(Constants.TAU, j, expectedUtilForHost);
			nrnPower += pServerNonRenewable(j, expectedUtilForHost);
		}
	
		//network energy cost
		enetserver = eNetPerHost(j, mobileARservice);
		e_cost += eserver + enetserver; 
		
		//consumed energy due to the APs on the route connecting this vehicle/service to its host:
		for (int p = 0; p < vehicleConnectedAPsPerRun.ConnectedAPs.size(); p++) {
				ap = vehicleConnectedAPsPerRun.ConnectedAPs.get(p);
				
				//if (ap != j) {
				
					double p_OnThePath_AP_router = pRouter(mobileARservice, ap);
							
					eap +=  eRouter(vehicleConnectedAPsPerRun.ConnectionTime.get(p), p_OnThePath_AP_router, ap);
									  
					nrnPower += p_OnThePath_AP_router * (vehicleConnectedAPsPerRun.ConnectionTime.get(p)/Constants.TAU) * (1 - Constants.SC_RenewablePortion[ap]);
				//}
				//else {//service is hosted on the server of its directly connected AP
					
				//}
		
		}
		e_cost += eap;
		
		return e_cost;
	
	
	}

	
	/** energy consumption due to the networking cost of connecting devices (such as edge routers) for this host
	 * @param hostIndex
	 * @param a
	 * @return
	 */
	public double eNetPerHost(int hostIndex, MobileARservice a){
		
			if (hostIndex == (Constants.NUM_EDGE_ROUTERS + Constants.NUM_BACKBONE_ROUTERS -1)||(hostIndex < Constants.NUM_EDGE_ROUTERS))//switch and edge router of cloud already has been processed in host part
				return 0;
			else {
				double p_router = pRouter(a, hostIndex);
				double e_network_host_cost = eRouter(Constants.TAU, p_router, hostIndex);
				
				nrnPower += (1 - Constants.SC_RenewablePortion[hostIndex]) * p_router;
				return e_network_host_cost;
			}
			
		
	}


	
}
