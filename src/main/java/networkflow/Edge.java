package networkflow;

/**
 * Represents an edge in the flow network.
 * 
 * Student ID: [Your Student ID]
 * Name: [Your Name]
 */
public class Edge {
    private final int from;       // Source vertex
    private final int to;         // Destination vertex
    private final int capacity;   // Maximum capacity of the edge
    private int flow;             // Current flow through the edge
    
    /**
     * Constructs an edge with the specified source, destination, and capacity.
     * 
     * @param from     The source vertex
     * @param to       The destination vertex
     * @param capacity The maximum capacity of the edge
     */
    public Edge(int from, int to, int capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = 0;
    }
    
    /**
     * Returns the source vertex of this edge.
     * 
     * @return The source vertex
     */
    public int getFrom() {
        return from;
    }
    
    /**
     * Returns the destination vertex of this edge.
     * 
     * @return The destination vertex
     */
    public int getTo() {
        return to;
    }
    
    /**
     * Returns the maximum capacity of this edge.
     * 
     * @return The capacity
     */
    public int getCapacity() {
        return capacity;
    }
    
    /**
     * Returns the current flow through this edge.
     * 
     * @return The flow
     */
    public int getFlow() {
        return flow;
    }
    
    /**
     * Sets the flow through this edge.
     * 
     * @param flow The new flow value
     */
    public void setFlow(int flow) {
        this.flow = flow;
    }
    
    /**
     * Returns the residual capacity of this edge.
     * 
     * @return The residual capacity (capacity - flow)
     */
    public int getResidualCapacity() {
        return capacity - flow;
    }
    
    /**
     * Returns a string representation of this edge.
     * 
     * @return A string in the format "from -> to (flow/capacity)"
     */
    @Override
    public String toString() {
        return from + " -> " + to + " (" + flow + "/" + capacity + ")";
    }
}
