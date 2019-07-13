import java.io.File;
import java.util.Random;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.ArrayList;

public class ScenarioTestGenerator {

	public static void main(String[] args) {
		//checks if appropriate number of arguments have been entered
		if (args.length != 3) {
			System.out.println("Graph, Scenario Number and Output File Name must be input as Arguments");
			System.exit(0);
		}
		
		String fileName = args[0];
		String outputFileName = args[2];
		
		//check that argument can be turned into an int
		int scenario = 0;
		try {
			scenario = Integer.parseInt(args[1]);
		}
		catch (Exception e) {
			System.out.println("Scenario must be an int");
			System.exit(0);
		}
		
		//verifies that the input number corresponds to a relevant scenario number
		if(scenario > 3 || scenario < 1) {
			System.out.println("Invalid scenario number");
			System.exit(0);
		}
		
		//continues to main program to generate a test scenario
		new ScenarioTestGenerator(fileName, scenario, outputFileName);
	}
	
	private Random randGen = new Random();
	
	protected static final int OPERATION_AMOUNT = 150;
	
	public String fileName;
	public String outputFileName;
	public int scenario;
	ArrayList<String> data = new ArrayList<String>();
	public String[] output = new String[OPERATION_AMOUNT];
	
	public ScenarioTestGenerator(String fileName, int scenario, String outputFileName) {
		this.fileName = fileName;
		this.scenario = scenario;
		this.outputFileName = outputFileName;
		
		//reads the data into the lines and places them into data arraylist
		readFile();
		
		//calls relevant code for chosen scenario
		switch (scenario) {
		case 1:
			scenarioRemoval();
			break;
		case 2:
			scenarioNearestNeighbours();
			break;
		case 3:
			scenarioUpdateWeights();
			break;
		}
		
		printToFile();
	}
	
	//generate scenario for removal operations
	private void scenarioRemoval() {
		int incr = 0;
		//gets the data of source vertices and target vertices in read data
		ArrayList<Integer> srcVertices = getColumn(0);
		ArrayList<Integer> tarVertices = getColumn(1);
		
		//remove edges
		ArrayList<Integer> removed = new ArrayList<Integer>();
		//loops until half of operation limit is reached
		for (int i=0; i<(OPERATION_AMOUNT/2); i++) {
			//continues to loop until an operation to remove an edge has been created
			boolean canRemove = false;
			while (canRemove == false) {
				//if the source vertex to random vertex edge hasn't been removed already, it creates an operation to remove it
				int toRemove = randGen.nextInt(data.size());
				if (removed.contains(toRemove) == false) {
					output[incr] = "U " + srcVertices.get(toRemove) + " " + tarVertices.get(toRemove) + " 0";
					removed.add(toRemove);
					canRemove = true;
					incr++;
				}
			}
		}
		
		//remove vertices
		ArrayList<Integer> uniqueSrcVertices = getUniqueValues(srcVertices);
		removed = new ArrayList<Integer>();
		//loops until the next half of the operation limit is full
		for (int i=0; i<(OPERATION_AMOUNT/2); i++) {
			//loops until a vertex remove operation has been made
			boolean canRemove = false;
			while (canRemove == false) {
				//checks if the randomly chosen vertex already has an operation that removes it and if not generates a removal operation for it
				int toRemove = randGen.nextInt(uniqueSrcVertices.size());
				if (removed.contains(toRemove) == false) {
					output[incr] = "RV " + uniqueSrcVertices.get(toRemove);
					removed.add(toRemove);
					canRemove = true;
					incr++;
				}
			}
		}
	}
	
	//genrates scenario for nearest neighbour operations
	private void scenarioNearestNeighbours() {
		int incr = 0;
		//gets source and target vertices in data
		ArrayList<Integer> srcVertices = getColumn(0);
		ArrayList<Integer> tarVertices = getColumn(1);
		//gets an array of all the unique values of source and target vertices in the data
		ArrayList<Integer> uniqueSrcVertices = getUniqueValues(srcVertices);
		ArrayList<Integer> uniqueTarVertices = getUniqueValues(tarVertices);

		//loops until first half of operation limit is filled
		ArrayList<Integer> collected = new ArrayList<Integer>();
		for(int i=0; i<(OPERATION_AMOUNT/2); i++) {
			//loops until an operation is created
			boolean cantUse = false;
			while (cantUse == false) {
				//gets a random target value
				int toUse = randGen.nextInt(uniqueTarVertices.size());
				//checks if it has already been used for an IN operation and if not creates one for it
				if (collected.contains(toUse) == false) {
					Integer tarVertex = uniqueTarVertices.get(toUse);
					output[incr] = "IN -1 " + tarVertex;
					collected.add(toUse);
					cantUse = true;
					incr++;
				}
			}
		}
		
		collected = new ArrayList<Integer>();
		//loops until next half of operation limit is filled
		for(int i=0; i<(OPERATION_AMOUNT/2); i++) {
			//loops until operation is created
			boolean canRemove = false;
			while (canRemove == false) {
				//gets a random source vertex
				int toUse = randGen.nextInt(uniqueSrcVertices.size());
				//checks if it has already been used for an ON operation and if not creates one for it
				if (collected.contains(toUse) == false) {
					int amountOccured = 0;
					for (Integer values : srcVertices)
						if (uniqueSrcVertices.get(toUse).equals(values)) amountOccured++;
					output[incr] = "ON -1 " + uniqueSrcVertices.get(toUse);
					collected.add(toUse);
					canRemove = true;
					incr++;
				}
			}
		}
	}
	
	//generates scenario for updating weights
	private void scenarioUpdateWeights() {
		int incr = 0;
		//gets data of aource and target vertices
		ArrayList<Integer> srcVertices = getColumn(0);
		ArrayList<Integer> tarVertices = getColumn(1);
		
		//while the operation limit has not been reached
		for (int i=0; i<OPERATION_AMOUNT; i++) {
			//gets a random integer to select a read edge
			int toUse = randGen.nextInt(data.size());
			//creates a an update operation for the relevant edge with a new random weight
			output[incr] = "U " + srcVertices.get(toUse) + " " + tarVertices.get(toUse) + " " + (1 + randGen.nextInt(25));
			incr++;
		}
	}
	
	//returns a list of unique values from the passed in list to prevent double selection of a vertex
	private ArrayList<Integer> getUniqueValues(ArrayList<Integer> dataColumn){
		ArrayList<Integer> uniqueData = new ArrayList<Integer>();
		//goes through each value in passed in list and checks if its already been encountered
		for (Integer line : dataColumn) 
			if (uniqueData.contains(line) == false) uniqueData.add(line);
		return uniqueData;
	}
	
	//gets a specific column from the data read fro mthe file
	private ArrayList<Integer> getColumn(int column) {
		ArrayList<Integer> columnData = new ArrayList<Integer>();
		
		for (String line : data) {
			String[] lineSplit = line.split(",");
			int tempValue = Integer.parseInt(lineSplit[column]);
			columnData.add(tempValue);
		}
		
		return columnData;
	}
	
	//reads the data from the file
	private void readFile() {
		//searches for file
		Scanner inputStream = null;
		File file = new File(fileName);
		if(file.exists() == false) {
			System.out.println("File Not Found");
			System.exit(0);
		}
		//attempts to ope nthe file
		try {
			inputStream = new Scanner(new File(fileName));
		}
		catch(FileNotFoundException e){
			System.out.println("Error opening the file " + fileName);
			System.exit(0);	//exits the system
		}
		//for each line adds the line to an array
		while (inputStream.hasNextLine()) data.add(inputStream.nextLine());
	}
	
	//prints the output array to a file with given name
	private void printToFile() {
		PrintWriter outputStream = null;
		try {
			outputStream = new PrintWriter(new FileOutputStream(outputFileName + ".in"));
			//for each element, prints it to the file on a new line
			for (String line : output) outputStream.write(line + "\r\n");
		} catch (FileNotFoundException e) {
			System.out.println("Could not write to file");
			System.exit(0);
		}
		//writes Q to end the operation test then closes the stream
		outputStream.write("Q");
		outputStream.close();
	}


}
