package networkflow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//Parser for reading flow network descriptions from input files.

public class NetworkParser {
    
    /**
     * Parses a flow network from the specified file.
     * @param filename The name of the file to parse
     * @return The parsed flow network
     * @throws IOException If an I/O error occurs
     */
    public static FlowNetwork parseFromFile(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            //Read the number of vertices
            int vertices = Integer.parseInt(reader.readLine().trim());
            
            //Create a new flow network
            FlowNetwork network = new FlowNetwork(vertices);
            
            //Read the edges
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length == 3) {
                    int from = Integer.parseInt(parts[0]);
                    int to = Integer.parseInt(parts[1]);
                    int capacity = Integer.parseInt(parts[2]);
                    
                    //Add the edge to the network
                    network.addEdge(from, to, capacity);
                }
            }
            
            return network;
        }
    }
    
    /**
     * Validates that the parsed network follows the expected format.
     * @param network The network to validate
     * @return true if the network is valid, false otherwise
     */
    public static boolean validateNetwork(FlowNetwork network) {
        int vertices = network.getVertices();
        
        //Check that there are at least 2 vertices (source and sink)
        if (vertices < 2) {
            System.err.println("Error: Network must have at least 2 vertices");
            return false;
        }
        
        //Check that all edges have valid source and destination vertices
        for (Edge edge : network.getEdges()) {
            int from = edge.getFrom();
            int to = edge.getTo();
            
            if (from < 0 || from >= vertices) {
                System.err.println("Error: Invalid source vertex: " + from);
                return false;
            }
            
            if (to < 0 || to >= vertices) {
                System.err.println("Error: Invalid destination vertex: " + to);
                return false;
            }
            
            if (edge.getCapacity() < 0) {
                System.err.println("Error: Negative capacity: " + edge.getCapacity());
                return false;
            }
        }
        
        return true;
    }
}
