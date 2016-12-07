package experimental.hypergraph;

import experimental.hypergraph.aliases.NodeNameManager;
import experimental.hypergraph.aliases.NodeTypeManager;
import experimental.hypergraph.container.ArrayNodeContainer;
import experimental.hypergraph.container.NodeContainer;


public class Node implements Comparable<Node> {
	
	public int id;
	public byte type;
	public Object data;
	
	public NodeContainer linkedNodes = new ArrayNodeContainer();
	
	/* Constructor */

	public Node(int id, String type) {
		this(
			id,
			NodeTypeManager.getType(type),
			null);
	}
	
	public Node(String name, String type) {		
		this(
			NodeNameManager.getId(name),
			NodeTypeManager.getType(type),
			null);
	}
	
	public Node(String name, String type, Object data) {		
		this(
			NodeNameManager.getId(name),
			NodeTypeManager.getType(type),
			data);
	}
	
	public Node(int id, String type, Object data) {		
		this(
			id,
			NodeTypeManager.getType(type),
			data);
	}

	
	public Node(Node other) {
		this(
			other.id,
			other.type,
			other.data);
	}
	
	public Node(int id, byte type, Object data) {
		this.id = id;
		this.type = type;
		this.data = data;
	}
	
	/* Connection with other nodes */
	
	public void add(Node node) {
		this.linkedNodes.insertNode(node);
		node.linkedNodes.insertNode(this);
	}
	
	public void remove(Node node) {
		this.linkedNodes.removeNode(node);
		node.linkedNodes.removeNode(this);
	}
	
	public void unlink() {
		for (Node node : linkedNodes.getNodes()) {
			node.linkedNodes.removeNode(this);
		}
		this.linkedNodes.clear();
	}
	
		
	/* Linked nodes access */
	
	public Node[] getNodes(byte type) {
		return linkedNodes.getNodes(type);
	}
		
	public Node[] getNodes(byte... types) {
		return linkedNodes.getNodes(types);
	}
	
	public Node[] getNodes() {		
		return linkedNodes.getNodes();
	}

	public boolean hasNodes() {
		return !linkedNodes.isEmpty();
	}
	
	
	public Node getOppositeNode(Node node) {
		return linkedNodes.getOppositeNode(node);
	}
	
	public Node[] getOppositeNodes(Node node) {
		return linkedNodes.getOppositeNodes(node);
	}
	
	
	@Override
	public int compareTo(Node other) {
		int res = Integer.compare(this.type, other.type);
		if (res != 0) {
			return res;
		}
		return Integer.compare(this.id, other.id);
	}
	
	/* Other functions */
	
	@Override
	public String toString() {
		
		String displayedId = null;
		
		displayedId = NodeNameManager.getName(id);
		if (displayedId == null) {
			displayedId = Integer.toString(id);
		}
		
		String displayedType = NodeTypeManager.getTypeName(type);
		
		return "Node [id=" + displayedId + ", type=" + displayedType + ", data=" + data +
				", linkedNodes=" + linkedNodes.size() + "]";
	}
}
