package hypergraph;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class Graph {
	
	/* XXX
	 * Можно ли объединить в один контейнер?
	 * Это сильно ускорит создание графа (30s -> 17s) и освободит память (~ 1G)
	 */
	public SetMultimap<String, Node> type2nodes = HashMultimap.create();
	public Map<Object, Node> data2node = new HashMap<>();
	
	
	/* Add/Remove functions */
	
	public void addNode(Node node) {
		type2nodes.put(node.type, node);
		data2node.put(node.data, node);
	}
	
	public void removeNode(Node node) {
		node.unlink();
		type2nodes.remove(node.type, node);
		data2node.remove(node.data);
	}
	
	
	/* Get functions */
	
	public Node getNode(Object data) {
		return data2node.get(data); 
	}
	
	public Collection<Node> getNodes() {
		return data2node.values();
	}
	
	public Collection<Node> getNodes(String type) {
		return type2nodes.get(type);
	}
	
	public Collection<Node> getVertices(SimpleProjection projection) {
		return getNodes(projection.vertexType);
	}
	
	public Collection<Node> getEdges(SimpleProjection projection) {
		return getNodes(projection.edgeType);
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
