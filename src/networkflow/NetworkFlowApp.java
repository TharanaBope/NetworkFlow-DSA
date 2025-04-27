package networkflow;

import java.io.IOException;
import java.util.Scanner;

//Main application class for the Network Flow algorithm implementation.]

public class NetworkFlowApp {
    
    /**
     * Main method to run the application.
     * @param args Command line arguments - expects a file path to the network description
     */
    public static void main(String[] args) {
        String inputFile;

        if (args.length < 1) {
            //No argument provided, ask the user
            Scanner sc = new Scanner(System.in);
            System.out.print("Please enter the input file path: ");
            inputFile = sc.nextLine();
        } else {
            //Argument provided
            inputFile = args[0];
        }
        
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
            
            //Create a maximum flow finder
            MaxFlowFinder maxFlowFinder = new MaxFlowFinder(network, source, sink);
            
            //Find the maximum flow
            System.out.println("Finding maximum flow...");
            int maxFlow = maxFlowFinder.findMaxFlow();
            
            //Print the results
            System.out.println("\nResults:");
            System.out.println("Maximum Flow: " + maxFlow);
            System.out.println("\nDetailed Execution Log:");
            System.out.println(maxFlowFinder.getLog());
            
            //Print the final flow network
            System.out.println("\nFinal Flow Network:");
            System.out.println(network);
            
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
