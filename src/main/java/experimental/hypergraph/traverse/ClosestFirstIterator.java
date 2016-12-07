package experimental.hypergraph.traverse;

import experimental.hypergraph.Node;
import experimental.hypergraph.projection.Projection;
import experimental.hypergraph.traverse.heap.FibonacciHeap;
import experimental.hypergraph.traverse.heap.FibonacciHeapNode;
import gnu.trove.map.hash.TIntObjectHashMap;

public class ClosestFirstIterator extends TraverseIterator {
	
	protected TIntObjectHashMap<FibonacciHeapNode<QueueEntry>> visitedVertices = new TIntObjectHashMap<>();
    protected FibonacciHeap<QueueEntry> heap = new FibonacciHeap<QueueEntry>();    
        
    public ClosestFirstIterator(Projection p, boolean isCrossComponentTraversal, Node startVertex) {
    	super(p, isCrossComponentTraversal, startVertex);
    }
    
	@Override
	protected boolean isVisited(Node vertex) {
		return visitedVertices.containsKey(vertex.id);
	}

    @Override
    protected boolean isConnectedComponentExhausted() {
        return heap.size() == 0;
    }

    @Override
    protected void encounterVertex(Node vertex, Node previous) {
        double shortestPathLength;
        if (previous == null) {
            shortestPathLength = 0;
        } else {
            shortestPathLength = calculatePathLength(vertex, previous);
        }
        
        QueueEntry entry = new QueueEntry(vertex, previous);
        FibonacciHeapNode<QueueEntry> node = new FibonacciHeapNode<>(entry);
        
        putVertexData(vertex, node);
        heap.insert(node, shortestPathLength);
    }

    @Override
    protected void encounterVertexAgain(Node vertex, Node prev) {
        FibonacciHeapNode<QueueEntry> node = getVertexData(vertex);

        if (node.getData().frozen) {
            // no improvement for this vertex possible
            return;
        }

        double candidatePathLength = calculatePathLength(vertex, prev);

        if (candidatePathLength < node.getKey()) {
            node.getData().spanningTreeVertex = prev;
            heap.decreaseKey(node, candidatePathLength);
        }
    }

    @Override
    protected Node provideNextVertex() {
        FibonacciHeapNode<QueueEntry> node = heap.removeMin();
        node.getData().frozen = true;
        return node.getData().vertex;
    }   
    
    
    public double getShortestPathLength(Node vertex) {
        FibonacciHeapNode<QueueEntry> node = getVertexData(vertex);

        if (node == null) {
            return Double.POSITIVE_INFINITY;
        }

        return node.getKey();
    }

    public Node getSpanningTreeVertex(Node vertex) {
        FibonacciHeapNode<QueueEntry> node = getVertexData(vertex);

        if (node == null) {
            return null;
        }

        return node.getData().spanningTreeVertex;
    }

    private double calculatePathLength(Node vertex, Node spanningTreeVertex) {
        FibonacciHeapNode<QueueEntry> prevEntry = getVertexData(spanningTreeVertex);
        return prevEntry.getKey() + 1; // weight is 1
    }
    
    protected FibonacciHeapNode<QueueEntry> getVertexData(Node vertex) {
    	return visitedVertices.get(vertex.id);
    }
    
    protected void putVertexData(Node vertex, FibonacciHeapNode<QueueEntry> n) {
    	visitedVertices.put(vertex.id, n);
    }    

    
    static class QueueEntry {

    	/** The vertex reached */
        Node vertex;
    	
    	/** Best spanning tree vertex seen so far */
        Node spanningTreeVertex;        

        /** True once previousVertex is guaranteed to be the true minimum */
        boolean frozen;
    	
    	public QueueEntry(Node vertex, Node spanningTreeVertex) {
    		this.vertex = vertex;
    		this.spanningTreeVertex = spanningTreeVertex;
    		this.frozen = false;
    	}
    }
 
}
