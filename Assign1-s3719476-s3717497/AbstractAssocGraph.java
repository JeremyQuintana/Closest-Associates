import java.io.*;
import java.util.*;

/**
 * Abstract class for Association graph that implements some of the common functionality.
 *
 * Note, you should not need to modify this but can if need to.  Just make sure to test to make sure everything works.
 *
 * @author Jeffrey Chan, 2019.
 */
public abstract class AbstractAssocGraph implements AssociationGraph
{
	// corresponding row
	Map<String, Integer> vertices;
	int lastRow;
	
	protected static final int ARRAY_AMOUNT = 20000;
	protected static final int EDGE_NOT_EXIST = -1;
	
	// sort by weight greatest to smallest (bubble sort)						/*make more efficient?*/
    protected void sortMyPairs(List<MyPair> pairs) {
    	boolean noSwap = true;
    	do {
    		noSwap = true;
	    	for (int i=1; i<pairs.size(); i++) {
	    		MyPair prevPair = pairs.get(i-1);
	    		MyPair pair 	= pairs.get(i);
	    		
	    		if (prevPair.getValue() < pair.getValue()) {
	    			pairs.set(i-1, pair);
	    			pairs.set(i, prevPair);
	    			noSwap = false;
	    		}
	    	}
    	} while (noSwap == false);
    }
    
    //combined checks for vertexes
    protected boolean validateVertexInputs(String srcVertex, String tarVertex, String vertexExists) {
    	boolean errors = false;
    	if (srcVertex != null && vertices.get(srcVertex) == null) {
    		errors = true;
    		System.err.println("Source Vertex Not Found");
    	}
    	if (tarVertex != null && vertices.get(tarVertex) == null) {
    		errors = true;
    		System.err.println("Target Vertex Not Found");
    	}
    	if (vertexExists != null && vertices.get(vertexExists) != null) {
    		errors = true;
    		System.err.println("Vertex already exists");
    	}
    	return errors;
    }
    
    public Map<String, Integer> getVertices(){
    	return vertices;
    }

} // end of abstract graph AbstractAssocGraph
