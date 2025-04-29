package networkflow;

import java.util.*;

//Implementation of the Edmonds-Karp algorithm for finding the maximum flow in a network.

public class MaxFlowFinder {//Class for finding the maximum flow in a network using the Edmonds-Karp algorithm
    private final FlowNetwork network; //The flow network to find the maximum flow in
    private final int source; //The source vertex
    private final int sink; //The sink vertex
    private boolean detailedLogging; //Added boolean to control detailed logging
    private List<PathInfo> augmentingPaths; //Added to store path information
    private long startTime; //Added for timing
    
    private static class PathInfo {//Class for storing path information
        List<Edge> path; //The path
        int flow; //The flow
        int bottleneck; //The bottleneck capacity
        
        PathInfo(List<Edge> path, int flow, int bottleneck) {//Constructor for the PathInfo class   
            this.path = path;
            this.flow = flow;
            this.bottleneck = bottleneck;
        }
    }
    
    /**
     * Constructs a maximum flow finder for the specified network.
     * @param network The flow network
     * @param source The source vertex (typically 0)
     * @param sink The sink vertex (typically vertices-1)
     */
    public MaxFlowFinder(FlowNetwork network, int source, int sink) {//Constructor for the MaxFlowFinder class
        this(network, source, sink, true); // Default to detailed logging
    }
    
    /**
     * Constructs a maximum flow finder for the specified network with logging control.
     * @param network The flow network
     * @param source The source vertex (typically 0)
     * @param sink The sink vertex (typically vertices-1)
     * @param detailedLogging Whether to log detailed information about each iteration
     */
    public MaxFlowFinder(FlowNetwork network, int source, int sink, boolean detailedLogging) {//Constructor for the MaxFlowFinder class
        this.network = network;//Set the network    
        this.source = source;//Set the source
        this.sink = sink;//Set the sink
        this.detailedLogging = detailedLogging;//Set the detailed logging
        this.augmentingPaths = new ArrayList<>();//Set the augmenting paths
    }
    
    /**
     * Finds the maximum flow in the network using the Edmonds-Karp algorithm
     * (using BFS to find augmenting paths). 
     * @return The maximum flow value
     */
    public int findMaxFlow() {//Method to find the maximum flow in the network using the Edmonds-Karp algorithm
        int maxFlow = 0;//Set the maximum flow to 0
        startTime = System.nanoTime();//Set the start time
        augmentingPaths.clear();//Clear the augmenting paths
        
        //Create a residual graph
        FlowNetwork residualGraph = network.createResidualGraph();//Create the residual graph
        
        //Find augmenting paths and update flow
        List<Edge> path;//Set the path
        int iteration = 1;//Set the iteration
        
        while ((path = findAugmentingPath(residualGraph)) != null) {//While there is an augmenting path
            //Find the bottleneck capacity
            int bottleneckCapacity = findBottleneckCapacity(path);
            
            //Store path information only for small networks
            if (network.getVertices() < 1000) {
                augmentingPaths.add(new PathInfo(new ArrayList<>(path), maxFlow + bottleneckCapacity, bottleneckCapacity));
            } else if (iteration % 1000 == 0) {
                // For large networks, print progress every 1000 iterations
                System.out.println("Completed " + iteration + " iterations...");
            }
            
            //Update the flow along the path
            updateFlow(path, bottleneckCapacity);
            
            //Update the maximum flow
            maxFlow += bottleneckCapacity;
            
            //Update the residual graph
            residualGraph = network.createResidualGraph();
            iteration++;
        }
        
        if (network.getVertices() < 1000) {//If the network is small
            printResults(maxFlow);//Print the results
        } else {
            // For large networks, only print the maximum flow
            System.out.println("\nMaximum Flow: " + maxFlow);
        }
        return maxFlow;
    }
    
    /**
     * Finds an augmenting path from source to sink in the residual graph using BFS.
     * @param residualGraph The residual graph
     * @return A list of edges forming an augmenting path, or null if no path exists
     */
    private List<Edge> findAugmentingPath(FlowNetwork residualGraph) {//Method to find an augmenting path from source to sink in the residual graph using BFS
        //Initialize BFS data structures
        boolean[] visited = new boolean[residualGraph.getVertices()];
        Map<Integer, Edge> edgeTo = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();
        
        //Start BFS from the source
        visited[source] = true;
        queue.add(source);
        
        //BFS to find a path from source to sink
        while (!queue.isEmpty() && !visited[sink]) {
            int v = queue.poll();
            
            //Explore all adjacent vertices
            for (Edge edge : residualGraph.getAdjacencyList(v)) {
                int w = edge.getTo();
                
                //If not visited and has residual capacity
                if (!visited[w] && edge.getResidualCapacity() > 0) {
                    edgeTo.put(w, edge);
                    visited[w] = true;
                    queue.add(w);
                }
            }
        }
        
        //Check if we found a path to the sink
        if (!visited[sink]) {
            return null; //No augmenting path exists
        }
        
        //Reconstruct the path from source to sink
        List<Edge> path = new ArrayList<>();
        for (int v = sink; v != source; ) {
            Edge edge = edgeTo.get(v);
            path.add(edge);
            v = edge.getFrom();
        }
        
        //Reverse the path to get it from source to sink
        Collections.reverse(path);
        return path;
    }
    
    /**
     * Finds the bottleneck capacity of an augmenting path. 
     * @param path The augmenting path
     * @return The bottleneck capacity
     */
    private int findBottleneckCapacity(List<Edge> path) {
        int bottleneckCapacity = Integer.MAX_VALUE;
        
        for (Edge edge : path) {
            bottleneckCapacity = Math.min(bottleneckCapacity, edge.getResidualCapacity());
        }
        
        return bottleneckCapacity;
    }
    
    /**
     * Updates the flow along an augmenting path. 
     * @param path  The augmenting path
     * @param bottleneckCapacity The bottleneck capacity
     */
    private void updateFlow(List<Edge> path, int bottleneckCapacity) {//Method to update the flow along an augmenting path
        //Find the corresponding edges in the original network
        for (Edge residualEdge : path) {
            int from = residualEdge.getFrom();
            int to = residualEdge.getTo();
            
            //Find the edge in the original network
            boolean isBackwardEdge = true;
            for (Edge edge : network.getAdjacencyList(from)) {
                if (edge.getTo() == to) {
                    //Forward edge - increase flow
                    edge.setFlow(edge.getFlow() + bottleneckCapacity);
                    isBackwardEdge = false;
                    break;
                }
            }
            
            if (isBackwardEdge) {
                //Backward edge - decrease flow in the corresponding forward edge
                for (Edge edge : network.getAdjacencyList(to)) {
                    if (edge.getTo() == from) {
                        edge.setFlow(edge.getFlow() - bottleneckCapacity);
                        break;
                    }
                }
            }
        }
    }
    
    private void printResults(int maxFlow) {//Method to print the results
        long endTime = System.nanoTime();//Set the end time
        double runtimeMs = (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
        
        System.out.println("============================================================");
        System.out.println("                    NETWORK FLOW DETAILS                    ");
        System.out.println("============================================================\n");
        
        System.out.println("NETWORK STATISTICS:");
        System.out.println("Total Nodes: " + network.getVertices());
        System.out.println("Source Node: " + source);
        System.out.println("Sink Node: " + sink + "\n");
        
        System.out.println("EDGE FLOW DETAILS:");
        System.out.println("Final Flow Network:");
        System.out.println(network);
        
        System.out.println("AUGMENTING PATHS:");
        for (int i = 0; i < augmentingPaths.size(); i++) {
            PathInfo pathInfo = augmentingPaths.get(i);
            System.out.printf("Path %2d (Flow = %d, Bottleneck = %d): ", i + 1, pathInfo.flow, pathInfo.bottleneck);
            
            for (int j = 0; j < pathInfo.path.size(); j++) {
                Edge edge = pathInfo.path.get(j);
                System.out.print(edge.getFrom() + "→" + edge.getTo());
                if (j < pathInfo.path.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println();
        }
        
        System.out.println("\nTotal Paths Found: " + augmentingPaths.size());
        System.out.println("============================================================");
        System.out.println("MAXIMUM FLOW: " + maxFlow + "\n");
        
        System.out.println("TIME COMPLEXITY:");
        System.out.println("- Edmonds-Karp Algorithm: O(V·E²)");
        System.out.println("  where V = " + network.getVertices() + " nodes, E = edges");
        System.out.printf("Runtime: %.2f ms\n", runtimeMs);
    }
    
    /**
     * Sets whether to use detailed logging.
     * Use false for large networks to avoid excessive console output.
     * @param detailedLogging True for detailed logging, false for minimal output
     */
    public void setDetailedLogging(boolean detailedLogging) {
        this.detailedLogging = detailedLogging;
    }
}
