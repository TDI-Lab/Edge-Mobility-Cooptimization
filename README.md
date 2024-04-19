# Edge-Mobility-Cooptimization
	/*  Example parameters
		 *  
		 *  1 290538 0 0 1 11 0 0 0 61
		 *  
		 *  First parameter: 0/1 -> migrations are denied or allowed
		 *  Second parameter: Positive Integer -> seed to be used in the random numbers generation
		 *  Third parameter: 0/1 -> Migration point approach is fixed (0) or based on the user speed (1)
		 *  Fourth parameter: 0/1/2 -> Migration strategy approach is based on the lowest latency (0), lowest distance between the user and cloudlet (1), or lowest distance between user and Access Point (2)
		 *  Fifth parameter: Positive Integer -> Number of users
		 *  Sixth parameter: Positive Integer -> Base Network Bandwidth between cloudlets
		 *  Seventh parameter: 0/1/2 -> Migration policy based on Complete VM/Cold migration (0), Complete Container migration (1), or Container Live Migration (3)
		 *  Eighth parameter: Non Negative Integer -> User Mobility prediction, in seconds
		 *  Ninth parameter: Non Negative Integer -> User Mobility prediction inaccuracy, in meters
		 *  Tenth parameter: Positive negative Integer -> Base Network Latency between cloudlets
		 */
		 
workload?

1. Edit the config.json file in D:\\MobFogSim-master\\MobFogSim-master\\src\\EdgeEPOS\\Setting\\input\\CityConfig.json and sets parameters using C:\Users\znb_n\eclipse-workspace\MapView\geo2cart\7Area
2. Edit C:\Users\znb_n\eclipse-workspace\MapView\BS\areaconfig.txt
3. rIFC_Down and FS, FM: ?? should be in Mbit so check the network
 CloudletPathLentgh include path lenght in meter for edge + core routers: 0 to 112 without cloud
creates a Cartesian polygon with the area borders
3. sets constants using initialize method:

**************************Constants******************************
	1. sets file paths
	2. initializes the nodes capacity and service demands
	3. sets dFFC matrix with the delay between all nodes using the path length matrix from MapView, for cloud a random value (MIN_dFC, MAX_dFC) is assigned
	   for all the nodes the latency is calculated using disToLatFiberFactor = 3.33 (microseconds per km)
	   
**************************Network********************************
4. creates ap (edge_routers) with MIN_ST_IN_AP=500 from Cartesian coordinates in MunichCartSelectedAP.csv
5. creates ap (back_routers-1) with MIN_ST_IN_AP=0 for core routers from Cartesian coordinates in MunichCartCore.csv
6. creates cloud with the index back_routers from MunichCartCore.csv
7. creates cloudlets for every edge router/core router
8. creates agents for every edge router
9. creates ServerCloudletsNetwork: builds a full undirectional graph between serverCloudlets using dFFC 
10. addSmartThing
11. reads Mobility_Dataset2 for smart mobile thing
12. initializes VehiclesToAP with the predicted APs in the mobility profiles
13. Writes vehicle to AP mapping to file AP_Veh.csv
14. Creates connection between Smart Things and closest Access Points with the delay based on their distance
15. Creates connection/link between Cloudlets and their co-located Access Points, then between Cloudlets and Smart Things.
14. createBroker for every smart thing
15. Prints NetCapacity to file AP_SC.csv

