package experimental.hypergraph.container;

import experimental.hypergraph.Node;
import gnu.trove.map.hash.TByteObjectHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class NestedHashMapNodeContainer implements NodeContainer {

	public TByteObjectHashMap<TIntObjectHashMap<Node>> typeMap;
	public int size = 0;


	public NestedHashMapNodeContainer() {
		typeMap = new TByteObjectHashMap<>();
	}

	public int size() {
		return size;
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public void clear() {
		for (TIntObjectHashMap<Node> nodeMap : typeMap.valueCollection()) {
			nodeMap.clear();
		}
		typeMap.clear();
		size = 0;
	}

	public void minimizeCapacity() {
		typeMap.compact();
		for (TIntObjectHashMap<Node> nodeMap : typeMap.valueCollection()) {
			nodeMap.compact();
		}
	}

	public boolean insertNode(Node node) {
		TIntObjectHashMap<Node> nodeMap = typeMap.get(node.type);
		if (nodeMap == null) {
			nodeMap = new TIntObjectHashMap<>();
			typeMap.put(node.type, nodeMap);
		}

		nodeMap.put(node.id, node);
		size++;

		return true;
	}

	public boolean removeNode(Node node) {
		TIntObjectHashMap<Node> nodeMap = typeMap.get(node.type);
		if (nodeMap == null) {
			return false;
		}

		nodeMap.remove(node.id);
		size--;

		if (nodeMap.isEmpty()) {
			typeMap.remove(node.type);
		}

		return true;
	}

	public boolean containsNode(Node node) {
		if (typeMap == null) {
			return false;
		}

		TIntObjectHashMap<Node> nodeMap = typeMap.get(node.type);
		if (nodeMap == null) {
			return false;
		}

		return nodeMap.contains(node.id);
	}

	public Node[] getNodes(byte type) {
//		TIntObjectHashMap<Node> nodeMap = typeMap.get(type);
//		if (nodeMap == null) {
//			return Collections.emptyList();
//		}
//		return nodeMap.valueCollection();

		// XXX Implement
		return null;
	}

	public Node[] getNodes(byte... types) {
//		Collection<Node> nodes = new ArrayList<>();
//		for (byte type : types) {
//			nodes.addAll(getNodes(type));
//		}
//
//		return nodes;

		// XXX Implement
		return null;
	}

	public Node[] getNodes() {
//		Collection<Node> nodes = new ArrayList<>(size);
//		for (TIntObjectHashMap<Node> nodeMap : typeMap.valueCollection()) {
//			nodes.addAll(nodeMap.valueCollection());
//		}
//
//		return nodes;

		// XXX Implement
		return null;
	}

	public int getDegree() {
		return size;
	}

	public int getDegree(byte type) {
		TIntObjectHashMap<Node> nodeMap = typeMap.get(type);
		if (nodeMap == null) {
			return 0;
		}
		return nodeMap.size();
	}

	public int getDegree(byte... types) {
		int degree = 0;
		for (byte type : types) {
			degree += getDegree(type);
		}
		return degree;
	}

	public void print() {
	}

	@Override
	public Node[] getOppositeNodes(Node node) {
		return null;
	}

	@Override
	public int getNumberOfTypes() {
		return 0;
	}

	@Override
	public Node getOppositeNode(Node node) {
		return null;
	}
}
