package hypergraph.traverse;

import hypergraph.Graph;
import hypergraph.Node;
import hypergraph.SimpleProjection;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class TraverseIterator implements Iterator<Node> {

	public SimpleProjection projection;
	public Graph graph;
	public boolean isCrossComponentTraversal = true;
	
	
	/* Internal data */
	
	enum ConnectedComponentState {
		BEFORE_COMPONENT,
		WITHIN_COMPONENT,
		AFTER_COMPONENT,
	}
	
	protected ConnectedComponentState state =
		ConnectedComponentState.BEFORE_COMPONENT;

	protected Node startVertex;
	protected Iterator<Node> vertexIterator;
	protected Set<Node> visitedVertices = new HashSet<>();
	
	
	/* Connected component traversal listeners */
	
	protected Set<TraversalListener> traversalListeners = new HashSet<>();
	
    public void addTraversalListener(TraversalListener listener) {
    	traversalListeners.add(listener);
    }
    
    public void removeTraversalListener(TraversalListener listener){
        traversalListeners.remove(listener);
    }
    
    protected void fireConnectedComponentStarted() {
    	for (TraversalListener listener : traversalListeners) {
    		listener.connectedComponentStarted();
        }
    }
    
    protected void fireConnectedComponentFinished() {
    	for (TraversalListener listener : traversalListeners) {
    		listener.connectedComponentFinished();
        }
    }

    
    /* Constructors */

    public TraverseIterator(Graph g, SimpleProjection projection) {
    	this(g, projection, true);
    }
    
	public TraverseIterator(Graph g, SimpleProjection projection, boolean isCrossComponentTraversal) {
		this.graph = g;
		this.projection = projection;
		this.isCrossComponentTraversal = isCrossComponentTraversal;
        
        vertexIterator = g.getVertices(projection).iterator();
                
        if (vertexIterator.hasNext()) {
            this.startVertex = vertexIterator.next();
        }
    }
	
	
	/* Abstract functions */
	
	protected abstract boolean isConnectedComponentExhausted();
	protected abstract void encounterVertex(Node vertex);
	protected abstract void encounterVertexAgain(Node vertex);
	protected abstract Node provideNextVertex();

	
	/* Algorithm functions */
	
	private void encounterStartVertex() {
        encounterVertex(startVertex);
        startVertex = null;
    }
	
	@Override
	public boolean hasNext() {

		if (startVertex != null) {
			encounterStartVertex();
	    }
		
		if (!isConnectedComponentExhausted()) {
			return true;
		}
				
		if (state == ConnectedComponentState.WITHIN_COMPONENT) {
			state = ConnectedComponentState.AFTER_COMPONENT;			
			if (!traversalListeners.isEmpty()) {
				fireConnectedComponentFinished();
			}
		}
		
		if (!isCrossComponentTraversal) {
			return false;
		}
		
		while (vertexIterator.hasNext()) {
			Node vertex = vertexIterator.next();

			if (!visitedVertices.contains(vertex)) {
				encounterVertex(vertex);
				state = ConnectedComponentState.BEFORE_COMPONENT;
				return true;
			}
		}

		return false;	    
	}


	@Override
	public Node next() {
		if (startVertex != null) {
            encounterStartVertex();
        }

        if (!hasNext()) {
        	throw new NoSuchElementException();
        }
        
        if (state == ConnectedComponentState.BEFORE_COMPONENT) {
        	state = ConnectedComponentState.WITHIN_COMPONENT;
        	if (!traversalListeners.isEmpty()) {
            	fireConnectedComponentStarted();
            }
        }

		Node nextVertex = provideNextVertex();
		// fire vertexTraversed
		addUnseenChildrenOf(nextVertex);

		return nextVertex;
	}
	
	private void addUnseenChildrenOf(Node vertex) {
		
		for (Node neighbor : vertex.getNeighbors(projection)) {
			// fire edge traversed
            if (visitedVertices.contains(neighbor)) {
                encounterVertexAgain(neighbor);
            } else {
                encounterVertex(neighbor);
            }
        }
    }

	@Override
	public void remove()
	{
	}
}
