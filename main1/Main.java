package main1;
import java.util.*;

public class Main {
    
    // Represents the network as an adjacency matrix
    static int[][] graph;
    static int[] parent;
    
    public static void main(String[] args) {
        // Example network with 6 nodes (0-5)
        // 0 is source, 5 is sink
        int vertices = 6;
        graph = new int[vertices][vertices];
        
        // Add edges with capacities
        addEdge(0, 1, 16);
        addEdge(0, 2, 13);
        addEdge(1, 2, 10);
        addEdge(1, 3, 12);
        addEdge(2, 1, 4);
        addEdge(2, 4, 14);
        addEdge(3, 2, 9);
        addEdge(3, 5, 20);
        addEdge(4, 3, 7);
        addEdge(4, 5, 4);
        
        // Calculate and print the maximum flow
        int maxFlow = fordFulkerson(0, 5);
        System.out.println("The maximum possible flow is: " + maxFlow);
    }
    
    // Add an edge with capacity c from u to v
    static void addEdge(int u, int v, int c) {
        graph[u][v] = c;
    }
    
    // Returns true if there is a path from source to sink
    static boolean bfs(int source, int sink) {
        parent = new int[graph.length];
        Arrays.fill(parent, -1);
        
        boolean[] visited = new boolean[graph.length];
        Queue<Integer> queue = new LinkedList<>();
        
        queue.add(source);
        visited[source] = true;
        parent[source] = -1;
        
        while (!queue.isEmpty()) {
            int u = queue.poll();
            
            for (int v = 0; v < graph.length; v++) {
                if (!visited[v] && graph[u][v] > 0) {
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;
                }
            }
        }
        
        return visited[sink];
    }
    
    // Ford-Fulkerson algorithm to find max flow
    static int fordFulkerson(int source, int sink) {
        int maxFlow = 0;
        
        // Augment the flow while there is a path from source to sink
        while (bfs(source, sink)) {
            // Find the maximum flow through the found path
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, graph[u][v]);
            }
            
            // Update residual capacities and reverse edges
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                graph[u][v] -= pathFlow;
                graph[v][u] += pathFlow; // Add reverse edge for residual graph
            }
            
            maxFlow += pathFlow;
        }
        
        return maxFlow;
    }
}