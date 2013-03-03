package hypergraph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class HyperGraph {
	
	/* XXX
	 * Use single container? Performance increase (30s -> 17s) and memory gain (~ 1G)
	 */
	
	public Map<Object, Node> id2node = new HashMap<>();
	public SetMultimap<String, Node> type2nodes = HashMultimap.create();

		
	/* Add functions */
		
	public void addNode(Node node) {
		if (node.id != null) {
			id2node.put(node.id, node);
		}
		type2nodes.put(node.type, node);
	}
	
	
	/* Remove functions */
	
	public void removeNode(Node node) {
		node.unlink();
		if (node.id != null) {
			id2node.remove(node.id);
		}
		type2nodes.remove(node.type, node);
	}
	
	public void removeNode(Object id) {
		Node node = id2node.get(id);
		if (node != null) {
			removeNode(node);
		} else {
			throw new RuntimeException("Node id " + id + " is not found");
		}
	}
	
	
	/* Get functions */
	
	public Node getNode(Object id) {
		return id2node.get(id);
	}
	
	public boolean containsNode(Object id) {
		return id2node.containsKey(id);
	}
	
	public Collection<Node> getNodes() {
		return type2nodes.values();
	}
		
	public Collection<Node> getNodes(String type) {
		return type2nodes.get(type);
	}
	
	public Collection<Node> getNodes(String... types) {
		Collection<Node> nodes = new ArrayList<>();
		for (String type : types) {
			nodes.addAll(getNodes(type));
		}
		return nodes;
	}
	
	public Collection<Node> getSupernodesOf(Node... subnodes) {
		Set<Node> supernodes = new HashSet<>();
		for (Node subnode : subnodes) {
			Collection<Node> temp = subnode.getSupernodes();
			if (temp != null) {
				supernodes.addAll(temp);
			}
		}
		return supernodes;
	}
	
	public Collection<Node> getSubnodesOf(Node... supernodes) {
		Set<Node> subnodes = new HashSet<>();
		for (Node supernode : supernodes) {
			Collection<Node> temp = supernode.getSubnodes();
			if (temp != null) {
				subnodes.addAll(temp);
			}
		}
		return subnodes;
	}
	
	
	/* New */
		
	public Node getEdge(String edgeType, Node node1, Node node2) {
		Collection<Node> supernodes1 = node1.getSupernodes(edgeType);
		Collection<Node> supernodes2 = node2.getSupernodes(edgeType);
		
		if (supernodes1 == null || supernodes2 == null) {
			return null;
		}
		
		for (Node sn1 : supernodes1) {
			for (Node sn2 : supernodes2) {
				if (sn1 == sn2) {
					return sn1;
				}
			}
		}
		return null;
	}
	
	
	/* === Debug functions === */
	
	public void printSize() {
		System.out.println("Graph: " + type2nodes.size() + "\n");
	}
	
	public void printTypeSizes() {
		System.out.println("Node numbers by types:");
		
		for (Entry<String, Collection<Node>> entry
			: type2nodes.asMap().entrySet())
		{
			System.out.println(entry.getKey() + "\t" + entry.getValue().size());
		}
		
		System.out.println("");
	}
	
	public void printLinkInfo() {
		
		class CounterContainer {
			
			class Counter {
				public long count;
				public Counter(long count) {
					this.count = count;
				}
			}
			
			public Map<String, Counter> counters = new TreeMap<>();
			
			public void updateCounters(SetMultimap<String, Node> multimap) {
				for (Entry<String, Collection<Node>> entry :
					multimap.asMap().entrySet())
				{
					String type = entry.getKey();
					long linksNumber = entry.getValue().size();
					
					Counter counter = counters.get(type);
					if (counter == null) {
						counters.put(type, new Counter(linksNumber));
					} else {
						counter.count += linksNumber;
					}
				}
			}
		}

		CounterContainer counterContainer = new CounterContainer();
		
		long linkContainersCount = 0;
		
		for (Node node : type2nodes.values()) {
			if (node.type2subnodes != null) {
				counterContainer.updateCounters(node.type2subnodes);
				linkContainersCount++;
			}
			if (node.type2supernodes != null) {
				counterContainer.updateCounters(node.type2supernodes);
				linkContainersCount++;
			}
		}
		
		System.out.println("Link containers: " + linkContainersCount);
		
		long totalLinkCount = 0;
		System.out.println("Link counts by type: ");
		for (Entry<String, CounterContainer.Counter> entry :
			counterContainer.counters.entrySet())
		{
			System.out.println(entry.getKey() + "\t" + entry.getValue().count);
			totalLinkCount += entry.getValue().count;
		}
		System.out.println("Total\t" + totalLinkCount);
	}
	
}
