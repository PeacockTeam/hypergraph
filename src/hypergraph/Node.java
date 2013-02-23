package hypergraph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;

public class Node {
	
	public String type;
	public Object data;
		
	public SetMultimap<String, Node> type2subnodes;
	public SetMultimap<String, Node> type2supernodes;
			
			
	public Node(String type, Object data) {
		this.type = type;
		this.data = data;
	}
		
	protected SetMultimap<String, Node> getSubnodesMap() {
		if (type2subnodes == null) {
			type2subnodes = HashMultimap.create();
		}
		return type2subnodes;
	}
	
	protected SetMultimap<String, Node> getSupernodesMap() {
		if (type2supernodes == null) {
			type2supernodes = HashMultimap.create(); 
		}
		return type2supernodes;
	}
	
	public boolean hasSubnodes() {
		return type2subnodes != null && !type2subnodes.isEmpty();
	}
	
	public boolean hasSupernodes() {
		return type2supernodes != null && !type2supernodes.isEmpty();
	}
	
	public Set<Node> getSubnodes(String type) {
		return (hasSubnodes())
			? type2subnodes.get(type)
			: null;
	}
	
	public Collection<Node> getSubnodes() {
		return (hasSubnodes())
			? type2subnodes.values()
			: null;
	}
	
	public Set<Node> getSupernodes(String type) {
		return (hasSupernodes())
			? type2supernodes.get(type)
			: null;
	}
	
	public Collection<Node> getSupernodes() {		
		return (hasSupernodes())
			? type2supernodes.values()
			: null;
	}
	
	
	/* Adding/Removing subnodes */
	
	public void add(Node node) {
		this.getSubnodesMap().put(node.type, node);
		node.getSupernodesMap().put(this.type, this);
	}	
	
	public void remove(Node node) {
		this.getSubnodesMap().remove(node.type, node); 
		node.getSupernodesMap().remove(this.type, this);
	}
		
	public Collection<Node> getEdges(SimpleProjection projection) {
		if (!this.type.equals(projection.vertexType)) {
			return null;
		}
		
		return getSupernodes(projection.edgeType);
	}
	
	public List<Node> getNeighbors(SimpleProjection projection) {
		if (!this.type.equals(projection.vertexType)) {
			return null;
		}
				
		List<Node> neighbors = new ArrayList<>();
		for (Node edge : getEdges(projection)) {
			for (Node neighbor : edge.getSubnodes(projection.vertexType)) {
				if (neighbor == this) {
					continue;
				}
				
				neighbors.add(neighbor);
			}
		}
		
		return neighbors;
	}

	
	public long getDegree(SimpleProjection projection) {		
		if (!this.type.equals(projection.vertexType)) {
			return 0;
		}
		
		return getNeighbors(projection).size();
	}
	

	public void unlink() {
		for (Node subnode : getSubnodesMap().values()) {
			subnode.getSupernodesMap().remove(this.type, this);
		}
		for (Node supernode : getSupernodesMap().values()) {
			supernode.getSubnodesMap().remove(this.type, this);
		}
		getSubnodesMap().clear();
		getSupernodesMap().clear();
	}

	
	@Override
	public String toString() {
		return "Node [type=" + type + ", data=" + data +
				", subnodes=" + ((type2subnodes == null) ? 0 : type2subnodes.size()) +
				", supernodes=" + ((type2supernodes == null) ? 0 : type2supernodes.size()) + "]";
	}
}
