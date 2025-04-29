package networkflow;

import java.util.*;

//Implementation of the Ford-Fulkerson algorithm with Edmonds-Karp optimization for finding the maximum flow in a network.

public class MaxFlowFinder {
    private final FlowNetwork network;
    private final int source;
    private final int sink;
    private boolean detailedLogging; // Added boolean to control detailed logging
    
    /**
     * Constructs a maximum flow finder for the specified network.
     * @param network The flow network
     * @param source The source vertex (typically 0)
     * @param sink The sink vertex (typically vertices-1)
     */
    public MaxFlowFinder(FlowNetwork network, int source, int sink) {
        this(network, source, sink, true); // Default to detailed logging
    }
    
    /**
     * Constructs a maximum flow finder for the specified network with logging control.
     * @param network The flow network
     * @param source The source vertex (typically 0)
     * @param sink The sink vertex (typically vertices-1)
     * @param detailedLogging Whether to log detailed information about each iteration
     */
    public MaxFlowFinder(FlowNetwork network, int source, int sink, boolean detailedLogging) {
        this.network = network;
        this.source = source;
        this.sink = sink;
        this.detailedLogging = detailedLogging;
    }
    
    /**
     * Finds the maximum flow in the network using the Ford-Fulkerson algorithm
     * with Edmonds-Karp optimization (using BFS to find augmenting paths). 
     * @return The maximum flow value
     */
    public int findMaxFlow() {
        int maxFlow = 0;
        System.out.println("Starting Ford-Fulkerson algorithm with Edmonds-Karp optimization");
        System.out.println("Source: " + source + ", Sink: " + sink + "\n");
        
        //Create a residual graph
        FlowNetwork residualGraph = network.createResidualGraph();
        
        //Find augmenting paths and update flow
        List<Edge> path;
        int iteration = 1;
        
        while ((path = findAugmentingPath(residualGraph)) != null) {
            //Find the bottleneck capacity (minimum residual capacity along the path)
            int bottleneckCapacity = findBottleneckCapacity(path);
            
            //Log the augmenting path and bottleneck capacity
            if (detailedLogging) {
                System.out.println("Iteration " + iteration++ + ":");
                System.out.print("Augmenting Path: ");
                for (Edge edge : path) {
                    System.out.print(edge.getFrom() + " -> " + edge.getTo() + " ");
                }
                System.out.println();
                System.out.println("Bottleneck Capacity: " + bottleneckCapacity);
            } else if (iteration % 1000 == 0) {
                // For large networks, only print every 1000th iteration
                System.out.println("Completed " + iteration + " iterations...");
            }
            
            iteration++;
            
            //Update the flow along the path
            updateFlow(path, bottleneckCapacity);
            
            //Update the maximum flow
            maxFlow += bottleneckCapacity;
            if (detailedLogging) {
                System.out.println("Current Maximum Flow: " + maxFlow + "\n");
            }
            
            //Update the residual graph
            residualGraph = network.createResidualGraph();
        }
        
        System.out.println("Final Maximum Flow: " + maxFlow);
        return maxFlow;
    }
    
    /**
     * Finds an augmenting path from source to sink in the residual graph using BFS.
     * @param residualGraph The residual graph
     * @return A list of edges forming an augmenting path, or null if no path exists
     */
    private List<Edge> findAugmentingPath(FlowNetwork residualGraph) {
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
    private void updateFlow(List<Edge> path, int bottleneckCapacity) {
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
    
    /**
     * Sets whether to use detailed logging.
     * Use false for large networks to avoid excessive console output.
     * @param detailedLogging True for detailed logging, false for minimal output
     */
    public void setDetailedLogging(boolean detailedLogging) {
        this.detailedLogging = detailedLogging;
    }
}
