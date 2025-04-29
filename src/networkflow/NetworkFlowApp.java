package networkflow;

import java.io.IOException;
import java.util.Scanner;

//Main application class for the Network Flow algorithm implementation.]

public class NetworkFlowApp {
    
    // Base path for benchmark files
    private static final String BENCHMARKS_PATH = "benchmarks/";
    
    /**
     * Main method to run the application.
     * @param args Command line arguments - expects a file name within the benchmarks directory
     */
    public static void main(String[] args) {
        String fileName;
        String inputFile;

        if (args.length < 1) {
            //No argument provided, ask the user
            Scanner sc = new Scanner(System.in);
            System.out.print("Please enter the benchmark file name: ");
            fileName = sc.nextLine();
            sc.close();
        } else {
            //Argument provided
            fileName = args[0];
        }
        
        // Construct the full path using the benchmarks directory
        inputFile = BENCHMARKS_PATH + fileName;
        
        try {
            //Parse the network from the input file
            System.out.println("Parsing network from file: " + inputFile);
            FlowNetwork network = NetworkParser.parseFromFile(inputFile);
            
            //Validate the network
            if (!NetworkParser.validateNetwork(network)) {
                System.err.println("Invalid network. Exiting.");
                return;
            }
            
            //Set source and sink vertices
            int source = 0;
            int sink = network.getVertices() - 1;
            
            //Check if this is a large network (more than 1000 vertices)
            boolean isLargeNetwork = network.getVertices() > 1000;
            
            //Create a maximum flow finder with appropriate logging setting
            MaxFlowFinder maxFlowFinder = new MaxFlowFinder(network, source, sink, !isLargeNetwork);
            
            if (isLargeNetwork) {
                System.out.println("Large network detected. Detailed logging disabled to conserve memory.");
            }
            
            //Find the maximum flow
            System.out.println("Finding maximum flow...");
            int maxFlow = maxFlowFinder.findMaxFlow();
            
            //Print the results
            System.out.println("\nResults:");
            System.out.println("Maximum Flow: " + maxFlow);
            
            //Print the final flow network (for smaller networks only)
            if (!isLargeNetwork) {
                System.out.println("\nFinal Flow Network:");
                System.out.println(network);
            }
            
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing numbers in input file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
