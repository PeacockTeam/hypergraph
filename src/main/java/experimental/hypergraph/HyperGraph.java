package experimental.hypergraph;

import experimental.hypergraph.aliases.NodeNameManager;
import experimental.hypergraph.aliases.NodeTypeManager;
import experimental.hypergraph.container.NodeContainer;
import gnu.trove.iterator.TByteObjectIterator;
import gnu.trove.map.hash.TByteObjectHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.log4j.Logger;

public class HyperGraph {

	public Logger logger = Logger.getLogger(getClass());

	public TIntObjectHashMap<Node> id2node = new TIntObjectHashMap<>();
	public TByteObjectHashMap<TIntObjectHashMap<Node>> type2nodes = new TByteObjectHashMap<>();
	public int numberOfNodes = 0;

	
	/* Add functions */

	public void addNode(Node node) {
		id2node.put(node.id, node);

		TIntObjectHashMap<Node> nodes = type2nodes.get(node.type);
		if (nodes == null) {
			nodes = new TIntObjectHashMap<>();
			type2nodes.put(node.type, nodes);
		}

		nodes.put(node.id, node);
		numberOfNodes++;
	}


	/* Remove functions */

	public void removeNode(Node node) {
		if (id2node.remove(node.id) == null) {
			throw new RuntimeException("Unexpected node id: " + node.id);
		}

		TIntObjectHashMap<Node> nodes = type2nodes.get(node.type);
		if (nodes == null) {
			throw new RuntimeException("Unexpected node type: " + node.type);
		}
		nodes.remove(node.id);
		numberOfNodes--;

		if (nodes.isEmpty()) {
			type2nodes.remove(node.type);
		}

		node.unlink();
	}

	public void removeNode(int id) {
		Node node = id2node.get(id);
		if (node != null) {
			removeNode(node);
		} else {
			throw new RuntimeException("Unexpected node id: " + id);
		}
	}


	/* Get functions */

	public Node getNode(String name) {
		return getNode(NodeNameManager.getId(name));
	}

	public boolean containsNode(int id) {
		return id2node.containsKey(id);
	}

	public Node getNode(int id) {
		return id2node.get(id);
	}

	public Collection<Node> getNodes(String type) {
		if (!NodeTypeManager.containsType(type)) {
			return Collections.emptyList();
		}
		return getNodes(NodeTypeManager.getType(type));
	}

	public Collection<Node> getNodes(String... types) {
		Collection<Node> nodes = new ArrayList<>();
		for (String type : types) {
			nodes.addAll(getNodes(type));
		}
		return nodes;
	}

	public Collection<Node> getNodes(byte type) {
		TIntObjectHashMap<Node> nodes = type2nodes.get(type);
		if (nodes == null) {
			return Collections.emptyList();
		}
		return nodes.valueCollection();
	}

	public Collection<Node> getNodes(byte... types) {
		Collection<Node> nodes = new ArrayList<>();
		for (byte type : types) {
			nodes.addAll(getNodes(type));
		}
		return nodes;
	}

	public Collection<Node> getNodes() {
		Collection<Node> nodes = new ArrayList<>(numberOfNodes);
		TByteObjectIterator<TIntObjectHashMap<Node>> it = type2nodes.iterator();
		while (it.hasNext()) {
			it.advance();
			nodes.addAll(it.value().valueCollection());
		}
		return nodes;
	}

	public Collection<Node> getNodesWithId() {
		return id2node.valueCollection();
	}

	public byte[] getTypes() {
		return type2nodes.keys();
	}

	/* === Debug functions === */

	public void printInfo() {
		System.out.println("Graph nodes/types: " + numberOfNodes + "/" + type2nodes.size() + "\n");

		System.out.println("Node numbers by types:");

		TByteObjectIterator<TIntObjectHashMap<Node>> it = type2nodes.iterator();

		while (it.hasNext()) {
			it.advance();

			System.out.println(
				NodeTypeManager.getTypeName(it.key()) + "\t" +
				it.value().size());
		}
		System.out.println("Total: " + numberOfNodes + "\n");
	}

	public void printInfo(Logger logger) {
		logger.debug("Graph nodes/types: " + numberOfNodes + "/" + type2nodes.size() + "\n");

		logger.debug("Node numbers by types:");

		TByteObjectIterator<TIntObjectHashMap<Node>> it = type2nodes.iterator();

		while (it.hasNext()) {
			it.advance();

			logger.debug(
				NodeTypeManager.getTypeName(it.key()) + "\t" +
				it.value().size());
		}
		logger.debug("Total: " + numberOfNodes + "\n");
	}

	public void printLinkInfo() {

		class CounterContainer {

			class Counter {
				public int count;
				public Counter(int count) {
					this.count = count;
				}
			}

			public TByteObjectHashMap<Counter> counters = new TByteObjectHashMap<>();

			public void updateCounters(NodeContainer nodeContainer) {

				for (Node node : nodeContainer.getNodes()) {

					Counter counter = counters.get( node.type);
					if (counter == null) {
						counters.put(node.type, new Counter(1));
					} else {
						counter.count++;
					}
				}
			}
		}

		CounterContainer counterContainer = new CounterContainer();

		for (Node node : getNodes()) {
			if (!node.linkedNodes.isEmpty()) {
				counterContainer.updateCounters(node.linkedNodes);
			}
		}

		System.out.println("Link counts for types: ");

		int totalLinkCount = 0;
		for (byte type : counterContainer.counters.keys()) {
			String typeName = NodeTypeManager.getTypeName(type);
			int count = counterContainer.counters.get(type).count;
			System.out.println(typeName + "\t" + count);

			totalLinkCount += count;
		}

		System.out.println("Total:\t" + totalLinkCount);
	}

}
