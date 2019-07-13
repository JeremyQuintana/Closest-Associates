
import java.io.*;
import java.util.*;

/**
 * Adjacency list implementation for the AssociationGraph interface.
 *
 * Your task is to complete the implementation of this class.  You may add methods, but ensure your modified class compiles and runs.
 *
 * @author Jeffrey Chan, 2019.
 */
public class AdjList extends AbstractAssocGraph
{

    // resizeArray of nodes
	
	// nodes formed from edges to the main vertices
	private Node[][] nodes;
	
    public AdjList() {
    	
    	lastRow = 0;
    	vertices = new HashMap<>();	// stores location of vertex in nodes 2d array
    	nodes = new Node[ARRAY_AMOUNT][ARRAY_AMOUNT];	// stores array of nodes sorted by source vertexes
    } // end of AdjList()


    public void addVertex(String vertLabel) {
    	//check if vertex exists
    	if (validateVertexInputs(null, null, vertLabel) == true) return;
    	if (lastRow == nodes.length) resizeRow();
    	vertices.put(vertLabel, lastRow++);
    } 

    // find the inVertex row, create a new outVertex node
    public void addEdge(String srcLabel, String tarLabel, int weight) {
    	//check if inputs are valid
    	boolean errors = false;
    	if (validateVertexInputs(srcLabel, tarLabel, null) == true) errors = true;
    	if (weight <= 0) {
    		errors = true;
    		System.err.println("Weight can not be less than or equal to 0");
    	}
    	if (errors == true) return;
    	
    	//get the relevant row of nodes
    	int row = vertices.get(srcLabel);
    	Node[] vertexNodes = nodes[row]; 
    	
    	//check is there is not a pre existing edge
    	for (Node node : vertexNodes)
    		if (node != null && node.tarLabel.equals(tarLabel)) return;
    	
    	//places the new edge in a the next free index in the array
    	boolean placed = false;
    	int iter = 0;
    	while(placed == false) {
    		if(vertexNodes[iter] == null) {
    			vertexNodes[iter] = new Node(tarLabel, weight);
    			placed = true;
    		}
    		iter++;
    		if (iter == vertexNodes.length) resizeCol();
    	}
    }

    // given in/outVertex label, find outVertex and its weight
    public int getEdgeWeight(String srcLabel, String tarLabel) {
    	//check if inputs are valid
    	if (validateVertexInputs(srcLabel, tarLabel, null) == true) return EDGE_NOT_EXIST;
    	
    	//gets the relevant row to search
    	int row = vertices.get(srcLabel);
    	Node[] vertexNodes = nodes[row]; 
    	
    	//checks each column in the row if there is an edge and returns the edge else it returns -1
    	for (Node node : vertexNodes)
    		if (node != null && node.tarLabel.equals(tarLabel))
    			return node.weight;
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
    	
    	//gets relevant row
    	int row = vertices.get(srcLabel);
    	Node[] vertexNodes = nodes[row]; 
    	
    	//searches each column for the row for the edge
    	boolean placed = false;
    	int iter = 0;
    	while(placed == false && iter < vertexNodes.length-1) {
    		//if the edge is found
    		if(vertexNodes[iter] != null && vertexNodes[iter].tarLabel.equals(tarLabel)) {
    			//checks if the weight is 0 and if it is make the edge null
    			if (weight == 0) vertexNodes[iter] = null;
    			//else turn the edge into a new Node with the given weight
    			else vertexNodes[iter] = new Node(tarLabel, weight);
    			placed = true;
    		}
    		iter++;
    	}
    	//if not found
    	if (placed == false) System.err.println("Edge Not Found");
    }

    public void removeVertex(String vertLabel) {
    	//check if inputs are valid
    	if (validateVertexInputs(vertLabel, null, null) == true) return;
    	
        vertices.remove(vertLabel);
        
        // remove edge from other vertices
        for (int row : vertices.values())
        	for (int i=0; i<nodes[row].length; i++) {
        		if (nodes[row][i] != null && vertLabel.equals(nodes[row][i].tarLabel))
        			nodes[row][i] = null;
        		i = nodes[row].length;
        	}
    } 
    
    public List<MyPair> inNearestNeighbours(int k, String vertLabel) {
    	//check if inputs are valid
    	if (validateVertexInputs(vertLabel, null, null) == true) return new ArrayList<MyPair>();
    	
    	//return vertex and weight
        List<MyPair> neighbours = new ArrayList<MyPair>();
    	
        //searches through each point in matrix
    	//goes through each row
        for (String vertex : vertices.keySet()) {
        	int row = vertices.get(vertex);
        	//goes through each column
        	for (int i=0; i<nodes.length; i++) {
        		//if point in matrix holds a target label of the given vertex it records it and the weight
        		if(nodes[row][i] != null && nodes[row][i].tarLabel.equals(vertLabel)) {
        			neighbours.add
        				(new MyPair(vertex, getEdgeWeight(vertex, nodes[row][i].tarLabel)));
        			i = nodes.length;
        		}
        	}
    	}
    	
        //sorts the recorded IN vertices and sorts in a descending order of weights
    	sortMyPairs(neighbours);
    	//either returns all vertices or the top k in the array
        return (k == -1) ? neighbours : neighbours.subList(0, k);
    } // end of inNearestNeighbours()


    public List<MyPair> outNearestNeighbours(int k, String vertLabel) {
    	if (validateVertexInputs(vertLabel, null, null) == true) return new ArrayList<MyPair>();
    	
        List<MyPair> neighbours = new ArrayList<MyPair>();
        //gets the relevant column
        int row = vertices.get(vertLabel);
        
        //for each column add the node to the record
        for (int i=0; i<nodes.length; i++) {
        	if(nodes[row][i] != null) {
        		String outVertex = nodes[row][i].tarLabel;
        		neighbours.add(new MyPair(outVertex, getEdgeWeight(vertLabel,outVertex)));
        	}
        }
        
        //sort record in descending order of weights
		sortMyPairs(neighbours);
		//either return all OUT vertices or the top k in the array
		return (k == -1) ? neighbours : neighbours.subList(0, k);
    } // end of outNearestNeighbours()

    public void printVertices(PrintWriter os) {
    	String vertexList = "";
    	if (vertices.isEmpty()) return;
    	for (String vertex : vertices.keySet()) vertexList = vertexList + vertex + " ";
    	vertexList = vertexList.substring(0, vertexList.length()-1);
    	os.println(vertexList);
    	os.flush();
    }
    
    public void printEdges(PrintWriter os) {
    	for (String inVertex : vertices.keySet()) {
    		int row = vertices.get(inVertex);
    		
    		for (Node outVertex : nodes[row]) {
    			if(outVertex != null) os.println
    				(inVertex + " " + outVertex.tarLabel + " " + outVertex.weight);
    		}
    	}
    	os.flush();
    }
    
    //resize array row
    public void resizeRow() {
    	Node[][] newNodes = new Node[nodes.length + 10000][nodes[0].length];
    	
    	for (int row=0; row<nodes.length; row++) {
    		for (int col=0; col<nodes[0].length; col++) {
    			newNodes[row][col] = nodes[row][col];
    		}
    	}
    	
    	nodes = newNodes;
    }
    
    //resize array col
    public void resizeCol() {
    	Node[][] newNodes = new Node[nodes.length][nodes[0].length + 10000];
    	
    	for (int row=0; row<nodes.length; row++) {
    		for (int col=0; col<nodes[0].length; col++) {
    			newNodes[row][col] = nodes[row][col];
    		}
    	}
    	
    	nodes = newNodes;
    }

    // represents an outVertex/edge
    protected class Node {
    	
    	String tarLabel;
    	int weight;
   
    	public Node(String label, int weight) {
    		tarLabel = label;
    		this.weight = weight;
    	}
    }
    
    
    
} // end of class AdjList
