package experimental.hypergraph.traverse;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import experimental.hypergraph.Node;
import experimental.hypergraph.projection.Projection;

public abstract class TraverseIterator implements Iterator<Node> {

	protected Projection projection;
	protected boolean isCrossComponentTraversal = true;
	
	protected Node startVertex;
	protected Iterator<Node> vertexIterator;
	
	enum ConnectedComponentState {
		BEFORE_COMPONENT,
		WITHIN_COMPONENT,
		AFTER_COMPONENT,
	}
	
	protected ConnectedComponentState state =
		ConnectedComponentState.BEFORE_COMPONENT;

	
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

    public TraverseIterator(Projection projection) {
    	this(projection, true, null);
    }
        
	public TraverseIterator(Projection projection, boolean isCrossComponentTraversal, Node startVertex) {
		this.projection = projection;
		this.isCrossComponentTraversal = isCrossComponentTraversal;
        
        vertexIterator = projection.getVertices().iterator();

        if (startVertex != null) {
        	this.startVertex = startVertex;
        }
        else if (vertexIterator.hasNext()) {
            this.startVertex = vertexIterator.next();
        }
    }
	
	/* Abstract functions */
	
	protected abstract boolean isConnectedComponentExhausted();
	protected abstract boolean isVisited(Node vertex);
	protected abstract void encounterVertex(Node vertex, Node from);
	protected abstract void encounterVertexAgain(Node vertex, Node from);
	protected abstract Node provideNextVertex();

	
	/* Algorithm functions */
	
	private void encounterStartVertex() {
        encounterVertex(startVertex, null);
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
		
		// XXX whole lot of iterations after each connected set?		
		while (vertexIterator.hasNext()) {
			Node vertex = vertexIterator.next();

			if (!isVisited(vertex)) {
				encounterVertex(vertex, null);
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
		
		// XXX Use getEdges() instead of getNeighborsOf()?
		
		for (Node neighbor : projection.getNeighborsOf(vertex)) {
			// fire edge traversed
			if (isVisited(neighbor)) {
                encounterVertexAgain(neighbor, vertex);
            } else {
                encounterVertex(neighbor, vertex);
            }
        }
    }

	@Override
	public void remove() {
	}
}
