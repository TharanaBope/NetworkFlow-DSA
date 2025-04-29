package networkflow;

import java.util.*;

public class FlowNetwork {
    private final int vertices;//Number of vertices
    private final List<List<Edge>> adjacencyList; //Adjacency list representation
    private final List<Edge> edges;//List of all edges
    
    /**
     * Constructs a flow network with the specified number of vertices.
     * @param vertices The number of vertices in the network
     */

    public FlowNetwork(int vertices) {
        this.vertices = vertices;
        this.adjacencyList = new ArrayList<>(vertices);
        this.edges = new ArrayList<>();
        
        //Initialize adjacency lists for all vertices
        for (int i = 0; i < vertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
    }
    
    /**
     * Returns the number of vertices in this network. 
     * @return The number of vertices
     */
    public int getVertices() {
        return vertices;
    }
    
    /**
     * Adds a directed edge to the network. 
     * @param from  The source vertex
     * @param to  The destination vertex
     * @param capacity The capacity of the edge
     */
    public void addEdge(int from, int to, int capacity) {
        //Create forward edge
        Edge edge = new Edge(from, to, capacity);
        adjacencyList.get(from).add(edge);
        edges.add(edge);
    }
    
    /**
     * Returns the adjacency list for a specific vertex.
     * @param vertex The vertex
     * @return The list of edges from the vertex
     */
    public List<Edge> getAdjacencyList(int vertex) {
        return adjacencyList.get(vertex);
    }
    
    /**
     * Returns all edges in the network. 
     * @return The list of all edges
     */
    public List<Edge> getEdges() {
        return edges;
    }
    
    /**
     * Creates a residual graph for the Ford-Fulkerson algorithm. 
     * @return A new flow network representing the residual graph
     */
    public FlowNetwork createResidualGraph() {
        FlowNetwork residualGraph = new FlowNetwork(vertices);
        
        //Add forward and backward edges to the residual graph
        for (Edge edge : edges) {
            int from = edge.getFrom();
            int to = edge.getTo();
            int capacity = edge.getCapacity();
            int flow = edge.getFlow();
            
            //Add forward edge with residual capacity
            if (capacity - flow > 0) {
                residualGraph.addEdge(from, to, capacity - flow);
            }
            
            //Add backward edge with flow as capacity
            if (flow > 0) {
                residualGraph.addEdge(to, from, flow);
            }
        }
        //Return the residual graph
        return residualGraph;
    }
    
    /**
     * Returns a string representation of this flow network. 
     * @return A string representation of the network
     */

    //Override the toString method to print the flow network
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Flow Network with ").append(vertices).append(" vertices:\n");
        //Iterate through the adjacency list and print the edges
        for (int i = 0; i < vertices; i++) {
            if (!adjacencyList.get(i).isEmpty()) {
                sb.append("Vertex ").append(i).append(":\n");
                for (Edge edge : adjacencyList.get(i)) {
                    sb.append("  ").append(edge).append("\n");
                }
            }
        }
        
        return sb.toString();
    }
}
