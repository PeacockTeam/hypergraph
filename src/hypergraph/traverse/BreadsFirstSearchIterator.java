package hypergraph.traverse;


import hypergraph.Node;
import hypergraph.projection.Projection;

import java.util.ArrayDeque;
import java.util.Deque;


public class BreadsFirstSearchIterator extends TraverseIterator {
	
    private Deque<Node> queue = new ArrayDeque<Node>();


    public BreadsFirstSearchIterator(Projection projection) {
        super(projection);
    }
 
    protected boolean isConnectedComponentExhausted() {
        return queue.isEmpty();
    }


    protected void encounterVertex(Node vertex) {
    	super.visitedVertices.add(vertex);
        this.queue.add(vertex);
    }

    protected void encounterVertexAgain(Node vertex)
    {
    }

    protected Node provideNextVertex() {
        return queue.removeFirst();
    }
}
