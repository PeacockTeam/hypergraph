package experimental.hypergraph.container;

import experimental.hypergraph.Node;

public interface NodeContainer {

	int size();
	boolean isEmpty();
	void clear();
	void minimizeCapacity();
	
	boolean insertNode(Node node);
	boolean removeNode(Node node);
	boolean containsNode(Node node);
	
	Node[] getNodes();
	Node[] getNodes(byte type);
	Node[] getNodes(byte... types);
	
	Node getOppositeNode(Node node);
	Node[] getOppositeNodes(Node node);
	
	int getDegree();
	int getDegree(byte type);
	int getDegree(byte... types);
	
	int getNumberOfTypes();
	
	void print();
}
