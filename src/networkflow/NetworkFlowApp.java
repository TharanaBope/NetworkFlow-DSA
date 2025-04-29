package networkflow;

//Student Name: Tharana Navithaka Bopearachchi
// Student ID: 20232948

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

//Main application class for the Network Flow algorithm implementation.

public class NetworkFlowApp {//Main application class for the Network Flow algorithm implementation
    
    // Base path for benchmark files
    private static final String BENCHMARKS_PATH = "benchmarks/";    
    
    /**
     * Main method to run the application.
     * @param args Command line arguments - expects a file name within the benchmarks directory
     */
    public static void main(String[] args) {
        String fileName = null;
        String inputFile = null;
        Scanner sc = new Scanner(System.in);
        boolean validFile = false;
        
        while (!validFile) {
            try {
                if (args.length < 1) {
                    //No argument provided, ask the user
                    System.out.print("Please enter the benchmark file name: ");
                    fileName = sc.nextLine();
                } else {
                    //Argument provided
                    fileName = args[0];
                    args = new String[0]; // Clear args so we can ask for input again if needed
                }
                
                // Construct the full path using the benchmarks directory
                inputFile = BENCHMARKS_PATH + fileName;
                
                // Check if file exists
                File file = new File(inputFile);
                if (!file.exists()) {
                    System.err.println("Error: File '" + inputFile + "' not found.");
                    System.out.println("Available benchmark files (showing first 3):");
                    File benchmarkDir = new File(BENCHMARKS_PATH);
                    if (benchmarkDir.exists() && benchmarkDir.isDirectory()) {
                        File[] files = benchmarkDir.listFiles();
                        if (files != null) {
                            int count = 0;
                            for (File f : files) {
                                if (count < 3) {
                                    System.out.println("  - " + f.getName());
                                    count++;
                                } else {
                                    System.out.println("  ... and " + (files.length - 3) + " more files");
                                    break;
                                }
                            }
                        }
                    }
                    System.out.println();
                    continue;
                }
                
                //Parse the network from the input file
                System.out.println("Parsing network from file: " + inputFile);
                FlowNetwork network = NetworkParser.parseFromFile(inputFile);
                
                //Validate the network
                if (!NetworkParser.validateNetwork(network)) {
                    System.err.println("Invalid network. Please choose another file.");
                    continue;
                }
                
                //Set source and sink vertices
                int source = 0;
                int sink = network.getVertices() - 1;
                
                //Check if this is a large network (more than 1000 vertices)
                boolean isLargeNetwork = network.getVertices() > 1000;
                
                //Create a maximum flow finder with appropriate logging setting
                MaxFlowFinder maxFlowFinder = new MaxFlowFinder(network, source, sink, !isLargeNetwork);
                
                if (isLargeNetwork) {
                    System.out.println("Large network detected (more than 1000 vertices). Detailed logging disabled to conserve memory.");
                }
                
                //Find the maximum flow
                System.out.println("Finding maximum flow...");
                maxFlowFinder.findMaxFlow();
                
                validFile = true; // If we get here, everything worked
                
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            } catch (NumberFormatException e) {
                System.err.println("Error parsing numbers in file: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
                e.printStackTrace();
            }
            
            if (!validFile) {
                System.out.println("Would you like to try another file? (y/n): ");
                String response = sc.nextLine().trim().toLowerCase();
                if (!response.startsWith("y")) {
                    break;
                }
            }
        }
        
        sc.close();
    }
}
