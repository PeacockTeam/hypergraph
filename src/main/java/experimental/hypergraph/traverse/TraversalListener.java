package experimental.hypergraph.traverse;

public interface TraversalListener {
	public void connectedComponentStarted();
	public void connectedComponentFinished();
}
