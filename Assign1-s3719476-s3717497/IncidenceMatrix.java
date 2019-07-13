
import java.io.*;
import java.util.*;


/**
 * Incident matrix implementation for the AssociationGraph interface.
 *
 * Your task is to complete the implementation of this class.  You may add methods, but ensure your modified class compiles and runs.
 *
 * @author Jeffrey Chan, 2019.
 */
public class IncidenceMatrix extends AbstractAssocGraph
{

	/**
	 * Contructs empty graph.
	 */
	
	// some times edges removed from middle of graph, leaving hole. Fix?
	// make sort method more efficient
	// resize array
	// make sure neighbour adds all members if <k available
	
	// corresponding column
	private Map<String, Integer> edges;
	private int lastCol;
	private int[][] matrix = new int[ARRAY_AMOUNT][ARRAY_AMOUNT];
	
    public IncidenceMatrix() {
    	
    	 vertices = new HashMap<>();
    	 edges = new HashMap<>();
    	 
    	 lastRow = 0;
    	 lastCol = 0;
    }

    public void addVertex(String vertLabel) {
    	//check if vertex exists
    	if (validateVertexInputs(null, null, vertLabel) == true) return;
    	if (vertices.size() == matrix.length) resizeRow();
    	vertices.put(vertLabel, lastRow++);
    } 

    // adds an extra column of values of weight, -weight, 0
    public void addEdge(String srcLabel, String tarLabel, int weight) {
    	//check if inputs are valid
    	boolean errors = false;
    	if (validateVertexInputs(srcLabel, tarLabel, null) == true) errors = true;
    	if (weight <= 0) {
    		errors = true;
    		System.err.println("Weight can not be less than or equal to 0");
    	}
    	if (errors == true) return;
    	
    	String key = getEdgeKey(srcLabel, tarLabel);
    	if(edges.get(key) != null) return;
    	
    	if (edges.size() == matrix[0].length) resizeCol();
        edges.put(key, lastCol++);
        
        updateWeightInMatrix(key, weight);
    }     

    
    public int getEdgeWeight(String srcLabel, String tarLabel) {
    	//check if inputs are valid
    	boolean errors = false;
    	if (validateVertexInputs(srcLabel, tarLabel, null) == true) errors = true;
    	if (edges.get(getEdgeKey(srcLabel, tarLabel)) == null) errors = true;
    	if (errors == true) return EDGE_NOT_EXIST;
    	
    	//gets the relevant column then iterates through rows and returns the weight
    	int col = edges.get(getEdgeKey(srcLabel, tarLabel));
    	for (String key : vertices.keySet()) {
    		int row = vertices.get(key);
    		if (matrix[row][col] > 0) return matrix[row][col];
    		row++;
    	}
    	
    	return EDGE_NOT_EXIST;
	}
    
	public void updateWeightEdge(String srcLabel, String tarLabel, int weight) {
		//check if inputs are valid
    	boolean errors = false;
    	if (validateVertexInputs(srcLabel, tarLabel, null) == true) errors = true;
    	if (weight < 0) {
    		errors = true;
    		System.err.println("Weight can not be be less than 0");
    	}
    	if (errors == true) return;
    	
		String key = getEdgeKey(srcLabel, tarLabel);
		if (edges.get(key) == null) {
			System.err.println("Edge Not Found");
			return;
		}
		
		//detemrines whether to remove the edge or update the weight
		if (weight == 0) edges.remove(key);
		else updateWeightInMatrix(key, weight);
    } 

    public void removeVertex(String vertLabel) {
    	//check if inputs are valid
    	if (validateVertexInputs(vertLabel, null, null) == true) return;
    	
    	//removes vertex from visibility of the program
    	vertices.remove(vertLabel);
    	
    	// remove all edges that contain this vertex
    	// can not remove from map while iterating through it
    	String[] toRemove = new String[ARRAY_AMOUNT];
    	int iter = 0;
    	for (String key : edges.keySet()) {
    		String[] keyLabels = key.split("->", 2);
    		if (keyLabels[0].equals(vertLabel) || keyLabels[1].equals(vertLabel))
	    		toRemove[iter] = key;
    		iter++;
    	}
    	for (String key : toRemove) {
    		edges.remove(key);
    	}
    	
    	// no need to reset weights in matrix, these values won't be accessed
    	// and will get replaced when a nnew vertex/edge added
    } 


	public List<MyPair> inNearestNeighbours(int k, String vertLabel) {
		//check if inputs are valid
		if (validateVertexInputs(vertLabel, null, null) == true) return new ArrayList<MyPair>();
		return neighbours(k, vertLabel, true);
	}

    public List<MyPair> outNearestNeighbours(int k, String vertLabel) {
    	if (validateVertexInputs(vertLabel, null, null) == true) return new ArrayList<MyPair>();
    	return neighbours(k, vertLabel, false);
    } 
    
    public void printVertices(PrintWriter os) {
    	String vertexList = "";
    	if (vertices.isEmpty()) return;
    	for (String vertex : vertices.keySet()) vertexList = vertexList + vertex + " ";
    	vertexList = vertexList.substring(0, vertexList.length()-1);
    	os.println(vertexList);
    	os.flush();
    }

    public void printEdges(PrintWriter os) {
    	for (String edge : edges.keySet()) {
    		int row = vertices.get(getSrcLabel(edge));
    		int col = edges.get(edge);
    		os.println(getSrcLabel(edge) + " " + getTarLabel(edge) + " " +  matrix[row][col]);
    	}
    	os.flush();
    }
    
    private void updateWeightInMatrix(String edgeKey, int weight) {
    	int col = edges.get(edgeKey);
    	//iterates through the rows and changes the value depending on the vertex selected
    	 for (String vertex : vertices.keySet()) {
			 int row = vertices.get(vertex);
			 
			 if		 (vertex.equals(getTarLabel(edgeKey)))	matrix[row][col] = -weight;
			 else if (vertex.equals(getSrcLabel(edgeKey)))	matrix[row][col] = weight;
			 else											matrix[row][col] = 0;
    	 }
    }
    
    //common neighbor in out functionality
    private List<MyPair> neighbours(int k, String vertLabel, boolean inOrOut) {
    	//return vertex and weight
        List<MyPair> neighbours = new ArrayList<MyPair>();
    	
        //gets relevant row in matrix
        int row = vertices.get(vertLabel);
    	
        //iterates through each column
    	for (String key : edges.keySet()) {
    		int col = edges.get(key);
    		//determines whether its searching for in or out values
    		if (inOrOut == true) {
    			//if the value is negative then it means its going into the selected vertex hence in and is then added to list
    			if (matrix[row][col] < 0) neighbours.add
    				(new MyPair(getSrcLabel(key), Math.abs(matrix[row][col])));}
    		else {
    			//if the value is positive then it means it going out of the selected vertex and is added to the list
    			if (matrix[row][col] > 0) neighbours.add
				(new MyPair(getTarLabel(key), matrix[row][col]));
    		}
    	}
    	
    	//sorts the list of vertices obatined from the serach from highest to lowest weights
    	sortMyPairs(neighbours);
    	//returns either all of the them or the top k amount of neighbours
        return (k == -1 || neighbours.size() < k) ? neighbours : neighbours.subList(0, k);
    }
    
    protected String getEdgeKey(String srcLabel, String tarLabel) {
    	return srcLabel + "->" + tarLabel;
    }
	protected String getSrcLabel(String key) {
    	int endIndex = key.indexOf("->");
    	return key.substring(0, endIndex);
    }
	
	protected String getTarLabel(String key) {
    	int startIndex = key.indexOf("->") + 2;
    	return key.substring(startIndex);
    }
	
	//resize array row
    public void resizeRow() {
    	int[][] newMatrix = new int[matrix.length + 10000][matrix[0].length];
    	
    	for (int row=0; row<matrix.length; row++) {
    		for (int col=0; col<matrix[0].length; col++) {
    			newMatrix[row][col] = matrix[row][col];
    		}
    	}
    	
    	matrix = newMatrix;
    }
    
    //resize array col
    public void resizeCol() {
    	int[][] newMatrix = new int[matrix.length][matrix[0].length + 10000];
    	
    	for (int row=0; row<matrix.length; row++) {
    		for (int col=0; col<matrix[0].length; col++) {
    			newMatrix[row][col] = matrix[row][col];
    		}
    	}
    	
    	matrix = newMatrix;
    }
    

} // end of class IncidenceMatrix
