import java.util.ArrayList;
import java.util.Random;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class DataGenerator {

	public static void main(String[] args) {
		//check if appropriate number or arguments have been placed
		if (args.length != 2) {
			System.out.println("Density and File Name must be input as Arguments");
			System.exit(0);
		}
		
		//check if argument is of type double
		double density = 0;
		try {
			density = Double.parseDouble(args[0]);
		}
		catch (Exception e) {
			System.out.println("Density must be a double");
			System.exit(0);
		}
		
		if (density > 1) {
			System.out.println("Density must be between 0 and 1");
			System.exit(0);
		}
		
		//continues to program to generate data
		new DataGenerator(density, args[1]);
	}
	
	private Random randGen = new Random();
	
	protected static final int VERTEX_INCR_AMOUNT = 25;
	protected static final int MIN_AMOUNT_DIFF_VERTEX = 1900;
	
	public int verticesAmount = MIN_AMOUNT_DIFF_VERTEX + randGen.nextInt(100);	//random to avg out data depending on amount of vertices
	public int edgesAmount;
	public double density;
	public String[] edges;
	public int[] vertices = new int[verticesAmount];
	public String fileName;
	
	public DataGenerator(double density, String fileName) {
		this.density = density;
		this.fileName = fileName;
		
		edgesAmount = (int) (density * (verticesAmount * verticesAmount));
		edges = new String[edgesAmount];
		System.out.println("Edges:    " + edgesAmount);
		System.out.println("Vertices: " + verticesAmount);
		createData();
	}
	
	//main method controlling the order of data creation
	private void createData() {
		createVertices();
		createEdges();
		printToFile();
	}
	
	//continues to print from edges array to the file with given fileName line by line
	private void printToFile() {
		PrintWriter outputStream = null;
		try {
			outputStream = new PrintWriter(new FileOutputStream(fileName + ".csv"));
			for (String edge : edges) outputStream.write(edge + "\r\n");
		} catch (FileNotFoundException e) {
			System.out.println("Could not write to file");
			System.exit(0);
		}
		outputStream.close();
	}
	
	//creates edges to place into file
	private void createEdges() {
		int[] amountEdgePerVertex = genRandNumThatSumTotal(verticesAmount, edgesAmount);
		int edgesMade = 0;
		
		//for each vertex
		for(int i=0; i<vertices.length; i++) {
			int srcVertex = vertices[i];
			//array to keep track of all edges already made
			ArrayList<Integer> srcConnections = new ArrayList<Integer>();
			//loops until vertex has created the intended amount of edges
			int connectionAmount = amountEdgePerVertex[i];
			int connectionsMade = 0;
			while (connectionsMade < connectionAmount) {
				//chooses random vertex to attempt to connect to
				int indexTarget = randGen.nextInt(verticesAmount);
				
				//checks if the connection can be made, ensure it isnt connecting to itself or a previously connected vertex
				boolean canConnect = true;
				if (indexTarget == i) canConnect = false;
				for (Integer connection : srcConnections)
					if (connection == indexTarget) canConnect = false;
				
				//if it can connect to it it does so with a random weight and records in all relevant arrays
				if (canConnect == true) {
					int tarVertex = vertices[indexTarget];
					edges[edgesMade] = formatEdges(srcVertex, tarVertex, (1 + randGen.nextInt(24)));
					connectionsMade++;
					edgesMade++;
				}
				
			}
		}
	}
	
	//returns an array of random numbers where all elements equal a sum number 
	//used to find how many edges a given vertex and giving the end result of or vertexes having a sum total of edges to the desired amount
	private int[] genRandNumThatSumTotal(int totalNumbers, int sum) {
		//creates an array of size amount of vertices
		int[] numbers = new int[totalNumbers];
		int sumLeft = sum;
		int min = 0;
		
		//iterates until all values in numbers array have been given values
		for(int i=0; i<numbers.length; i++) {
			//calculates min number of edges the vertex should have out
			min = sumLeft/(totalNumbers-i);
			//adds a number of random value max being the amount left to sum to / amount of vertices left, randomizing the min value gives more randomness
			//if the amount left is less than the min value then it gives the minimum value
			numbers[i] = (sumLeft > min) ? 1 + randGen.nextInt(min) + randGen.nextInt(sumLeft/(totalNumbers-i)) : min;
			sumLeft -= numbers[i];
		}
		
		return numbers;
	}
	
	//returns edges in a format to print to the file
	private String formatEdges(int srcVertex, int tarVertex, int weight) {
		return srcVertex + "," + tarVertex + "," + weight;
	}
	
	//create vertices that the edges are going to be based on
	private void createVertices() {
		int prevVertex = 0;
		int verticesCreated = 0;
		
		//keeps creating vertices of increasing number until it reaches amount of vertices needed
		while (verticesCreated < verticesAmount) {
			prevVertex = prevVertex + 1 + randGen.nextInt(VERTEX_INCR_AMOUNT);
			vertices[verticesCreated] = prevVertex;
			verticesCreated++;
		}
	}
	
}
