package networkflow;

//Student Name: Tharana Navithaka Bopearachchi
// Student ID: 20232948

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
    public static FlowNetwork parseFromFile(String filename) throws IOException {//Method to parse a flow network from a file
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {//Try with a buffered reader
            //Read the number of vertices
            int vertices = Integer.parseInt(reader.readLine().trim());
            
            //Create a new flow network
            FlowNetwork network = new FlowNetwork(vertices);//Create the flow network
            
            //Read the edges
            String line;//Set the line
            while ((line = reader.readLine()) != null) {//While there is a line
                String[] parts = line.trim().split("\\s+");//Split the line into parts
                if (parts.length == 3) {//If there are 3 parts
                    int from = Integer.parseInt(parts[0]);//Set the from vertex
                    int to = Integer.parseInt(parts[1]);//Set the to vertex
                    int capacity = Integer.parseInt(parts[2]);//Set the capacity
                    
                    //Add the edge to the network
                    network.addEdge(from, to, capacity);//Add the edge to the network
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
    public static boolean validateNetwork(FlowNetwork network) {//Method to validate a flow network
        int vertices = network.getVertices();//Set the vertices
        
        //Check that there are at least 2 vertices (source and sink)
        if (vertices < 2) {//If there are less than 2 vertices      
            System.err.println("Error: Network must have at least 2 vertices");//Print an error message
            return false;//Return false
        }
        
        //Check that all edges have valid source and destination vertices
        for (Edge edge : network.getEdges()) {//For each edge
            int from = edge.getFrom();//Set the from vertex
            int to = edge.getTo();//Set the to vertex
            
            if (from < 0 || from >= vertices) {//If the from vertex is less than 0 or greater than or equal to the number of vertices
                System.err.println("Error: Invalid source vertex: " + from);//Print an error message
                return false;//Return false
            }
            
            if (to < 0 || to >= vertices) {//If the to vertex is less than 0 or greater than or equal to the number of vertices
                System.err.println("Error: Invalid destination vertex: " + to);//Print an error message
                return false;//Return false
            }
            
            if (edge.getCapacity() < 0) {//If the capacity is less than 0
                System.err.println("Error: Negative capacity: " + edge.getCapacity());//Print an error message
                return false;//Return false
            }
        }
        
        return true;
    }
}
