# Edge-Mobility-Cooptimization

## Preparation Steps

1. **Identify Test Area and Traffic Network**:
   - Determine the test area and set up its traffic network using appropriate tools.

2. **Identify Base Stations and Edge/Core Routers**:
   - Utilize OpenCellId for Base Station identification and Emufog, Caida, Munich Scientific Internet Network for edge/core routers.
   - Record their locations in Cartesian coordinates based on the Munich transport network (Convert Geo-coordinates to Cartesian using Geo2Cart).

3. **Build Network Topology**:
   - Construct the network topology using Base Stations, edge/backbone routers, and cloud centers.
   - Calculate connections and latency using Dijkstra's shortest path algorithm.

4. **Prepare Mobility Profiles**:
   - Generate mobility profiles for vehicles/mobile devices using tools like Sumo, SumoTools for ODmatrix, and SumoMobilityPreProcessor.
   - Ensure the format includes timestep (second), angle, x-coordinate, y-coordinate, speed, and access point (AP).

5. **Prepare IoT Workload**:
   - Acquire IoT workload data, such as MAWI, for experimentation.

## Run Instructions

1. **Edit Configurations**:
   - Modify Config.json file according to the specific requirements of your experiment.

2. **Adjust Constants**:
   - Update parameters in Constants.java file as necessary for your experiment setup.

3. **Execute Experiments**:
   - Run experiments using available classes in the EdgeEPOS directory or execute jar files located in the root directory.