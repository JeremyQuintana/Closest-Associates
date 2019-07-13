import java.io.*;
import java.util.*;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import java.lang.String;

/**
 * Framework to test the association graph implementations.
 *
 * There should be no need to change this for task A.  If you need to make changes for task B, please make a copy, then modify the copy for task B.
 * Modified version of original GraphEval
 *
 * @author Jeffrey Chan, 2019.
 */
public class GraphEvalTaskB
{
	protected static final String progName = "GraphEvalTaskB";
	/**
	 * Print help/usage message.
	 */
	private static AbstractAssocGraph graph;
	private static long startTime;
	
	//prints whats needed to run the code
	public static void usage(String progName) {
		System.err.println(progName + ": <implementation> [-f <filename to load graph>] < <test file>");
		System.exit(1);
	} // end of usage

	//runs the relevant methods in the graph for the operation line
	public static void processOperations(String[] tokens, AssociationGraph graph){
		switch (tokens[0]) {
		case "AV":
			graph.addVertex(tokens[1]);
			break;
		case "AE":
			graph.addEdge(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
			break;
		case "W":
			graph.getEdgeWeight(tokens[1], tokens[2]);
			break;
		case "U":
			graph.updateWeightEdge(tokens[1], tokens[2], Integer.parseInt(tokens[3]));
			break;
		case "RV":
			graph.removeVertex(tokens[1]);
			break;
		case "IN":
			graph.inNearestNeighbours(Integer.parseInt(tokens[1]), tokens[2]);
			break;
		case "ON":
			graph.inNearestNeighbours(Integer.parseInt(tokens[1]), tokens[2]);
			break;
		case "PV":
			graph.printVertices(new PrintWriter(System.out));;
			break;
		case "PE":
			graph.printVertices(new PrintWriter(System.out));;
			break;
		case "Q":
			//ends the test time
			long endTime = System.nanoTime();
			double estimatedTime = ((double)(endTime - startTime)) / Math.pow(10, 9);
			System.out.println("End Test");
			System.out.println("Scenario Test Time: " + estimatedTime);
			System.exit(0);
			break;
		default:
			System.out.println("Operation not known");
			System.exit(0);
		}

	} // end of processOperations()



	/**
	 * Main method.  Determines which implementation to test and processes command line arguments.
	 */
	public static void main(String[] args) {

		// parse command line options
		OptionParser parser = new OptionParser("f:");
		OptionSet options = parser.parse(args);
		
		//gets the input file
		String inputFilename = null;
		// -f <inputFilename> specifies the file that contains edge list information to construct the initial graph with.
		if (options.has("f")) {
			if (options.hasArgument("f")) {
				inputFilename = (String) options.valueOf("f");
			}
			else {
				System.err.println("Missing filename argument for -f option.");
				usage(progName);
			}
		}
		// non option arguments
		List<?> tempArgs = options.nonOptionArguments();
		List<String> remainArgs = new ArrayList<String>();
		for (Object object : tempArgs) {
			remainArgs.add((String) object);
		}
		
		//ensures that 1 argument of data structure type is input
		// check number of non-option command line arguments
		if (remainArgs.size() != 1) {
			System.err.println("Incorrect number of arguments.");
			usage(progName);
		}

		// parse non-option arguments
		String implementationType = remainArgs.get(0);

		// determine which implementation to test
		switch(implementationType) {
			case "adjlist":
				graph = new AdjList();
				break;
			case "incmat":
				graph = new IncidenceMatrix();
				break;
			default:
				System.err.println("Unknown implmementation type.");
				usage(progName);
		}
		
		// reads the input data file
		if (inputFilename != null) {

			try {
				BufferedReader reader = new BufferedReader(new FileReader(inputFilename));

		    	String line;
		    	String delimiter = ",";
		    	String[] tokens;
		    	String srcLabel, tarLabel;
				int weight;

		    	while ((line = reader.readLine()) != null) {
		    		tokens = line.split(delimiter);
		    		srcLabel = tokens[0];
		    		tarLabel = tokens[1];
					weight = Integer.parseInt(tokens[2]);
					//checks if vertex has already been added
					if (graph.getVertices().containsKey(srcLabel) == false)
						graph.addVertex(srcLabel);
					if (graph.getVertices().containsKey(tarLabel) == false)
						graph.addVertex(tarLabel);
		    		graph.addEdge(srcLabel, tarLabel, weight);
		    	}
			}
			catch (FileNotFoundException ex) {
				System.err.println("File " + args[1] + " not found.");
			}
			catch(IOException ex) {
				System.err.println("Cannot open file " + args[1]);
			}
		}
		
		//to read the operations from the test .in file
		Scanner in = new Scanner(System.in);
		
		ArrayList<String> lines = new ArrayList<String>();
		while (in.hasNext()) lines.add(in.nextLine());
		int incr = 0;
		//reads all the lines of operations, splits them and stores them on an array
		ArrayList<String[]> operations = new ArrayList<String[]>();
		for (String line : lines) {
			String[] tokens = line.split(" ");
			operations.add(tokens);
		}
		
		//starts the operation time test
		System.out.println("Start Test");
		startTime = System.nanoTime();
		for (String[] operation : operations) processOperations(operation, graph);
	} // end of main()


} // end of class GraphEvak