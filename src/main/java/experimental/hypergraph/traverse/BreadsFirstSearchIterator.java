package experimental.hypergraph.traverse;



import experimental.hypergraph.Node;
import experimental.hypergraph.projection.Projection;
import gnu.trove.set.hash.TIntHashSet;

import java.util.ArrayDeque;
import java.util.Deque;


public class BreadsFirstSearchIterator extends TraverseIterator {
	
	protected TIntHashSet visitedVertices = new TIntHashSet();
    protected Deque<Node> queue = new ArrayDeque<Node>();
    
    public BreadsFirstSearchIterator(Projection projection) {
        super(projection);
    }
 
    @Override
    protected boolean isConnectedComponentExhausted() {
        return queue.isEmpty();
    }

    @Override
	protected boolean isVisited(Node vertex) {
		return visitedVertices.contains(vertex.id);
	}

    @Override
    protected void encounterVertex(Node vertex, Node from) {
    	visitedVertices.add(vertex.id);
        queue.add(vertex);
    }

    @Override
    protected void encounterVertexAgain(Node vertex, Node from) {
    }

    @Override
    protected Node provideNextVertex() {
        return queue.removeFirst();
    }
}
