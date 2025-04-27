package networkflow;

import java.util.*;

//Implementation of the Ford-Fulkerson algorithm with Edmonds-Karp optimization for finding the maximum flow in a network.

public class MaxFlowFinder {
    private final FlowNetwork network;
    private final int source;
    private final int sink;
    private final StringBuilder log;
    
    /**
     * Constructs a maximum flow finder for the specified network.
     * @param network The flow network
     * @param source The source vertex (typically 0)
     * @param sink The sink vertex (typically vertices-1)
     */
    public MaxFlowFinder(FlowNetwork network, int source, int sink) {
        this.network = network;
        this.source = source;
        this.sink = sink;
        this.log = new StringBuilder();
    }
    
    /**
     * Finds the maximum flow in the network using the Ford-Fulkerson algorithm
     * with Edmonds-Karp optimization (using BFS to find augmenting paths). 
     * @return The maximum flow value
     */
    public int findMaxFlow() {
        int maxFlow = 0;
        log.append("Starting Ford-Fulkerson algorithm with Edmonds-Karp optimization\n");
        log.append("Source: ").append(source).append(", Sink: ").append(sink).append("\n\n");
        
        //Create a residual graph
        FlowNetwork residualGraph = network.createResidualGraph();
        
        //Find augmenting paths and update flow
        List<Edge> path;
        int iteration = 1;
        
        while ((path = findAugmentingPath(residualGraph)) != null) {
            //Find the bottleneck capacity (minimum residual capacity along the path)
            int bottleneckCapacity = findBottleneckCapacity(path);
            
            //Log the augmenting path and bottleneck capacity
            log.append("Iteration ").append(iteration++).append(":\n");
            log.append("Augmenting Path: ");
            for (Edge edge : path) {
                log.append(edge.getFrom()).append(" -> ").append(edge.getTo()).append(" ");
            }
            log.append("\n");
            log.append("Bottleneck Capacity: ").append(bottleneckCapacity).append("\n");
            
            //Update the flow along the path
            updateFlow(path, bottleneckCapacity);
            
            //Update the maximum flow
            maxFlow += bottleneckCapacity;
            log.append("Current Maximum Flow: ").append(maxFlow).append("\n\n");
            
            //Update the residual graph
            residualGraph = network.createResidualGraph();
        }
        
        log.append("Final Maximum Flow: ").append(maxFlow).append("\n");
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
     * Returns the log of the algorithm's execution.
     * @return The execution log
     */
    public String getLog() {
        return log.toString();
    }
}
