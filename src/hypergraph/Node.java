package hypergraph;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;


public class Node implements Comparable<Node> {
	
	public Object id;
	public String type;
	public Object data;
		
	public SetMultimap<String, Node> type2subnodes;
	public SetMultimap<String, Node> type2supernodes;
		
	
	/* Constructor */
	
	public Node(Object id, String type) {
		this(id, type, null);
	}
	
	public Node(Object id, String type, Object data) {
		if (type == null) {
			type = "Default";
		}
		
		this.id = id;
		this.type = type;
		this.data = data;
	}
	
	
	/* Connection with other nodes */
	
	public void add(Node node) {
		this.getSubnodesMap().put(node.type, node);
		node.getSupernodesMap().put(this.type, this);
	}
	
	public void remove(Node node) {
		this.getSubnodesMap().remove(node.type, node); 
		node.getSupernodesMap().remove(this.type, this);
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
	
	
	/* Link container functions */	
		
	protected SetMultimap<String, Node> getSubnodesMap() {
		if (type2subnodes == null) {
			//type2subnodes = HashMultimap.create(1, 2);
			type2subnodes = TreeMultimap.create();
			//type2subnodes = LinkedHashMultimap.create(); /* Allows proper ordering */
		}
		return type2subnodes;
	}
	
	protected SetMultimap<String, Node> getSupernodesMap() {
		if (type2supernodes == null) {
			//type2supernodes = HashMultimap.create(1, 2);
			type2supernodes = TreeMultimap.create();
			//type2supernodes = LinkedHashMultimap.create(); /* Allows proper ordering */
		}
		return type2supernodes;
	}
	
	public boolean hasSubnodes() {
		return type2subnodes != null && !type2subnodes.isEmpty();
	}
	
	public boolean hasSupernodes() {
		return type2supernodes != null && !type2supernodes.isEmpty();
	}
	
	
	/* Subnode access */

	public Collection<Node> getSubnodes() {
		return (hasSubnodes())
			? type2subnodes.values()
			: null;
	}
	
	public Set<Node> getSubnodes(String type) {
		return (hasSubnodes())
			? type2subnodes.get(type)
			: null;
	}
	
	public Set<Node> getSubnodes(Collection<String> types) {
		if (!hasSubnodes()) {
			return null;
		}				
		
		Set<Node> subnodes = new HashSet<Node>();
		for (String type : types) {
			subnodes.addAll(getSubnodes(type));
		}
		return subnodes;
	}
	
	public Set<Node> getSubnodes(String... types) {
		return getSubnodes(Arrays.asList(types));
	}

	
	/* Supernode access */

	public Collection<Node> getSupernodes() {		
		return (hasSupernodes())
			? type2supernodes.values()
			: null;
	}
	
	public Set<Node> getSupernodes(String type) {
		return (hasSupernodes())
			? type2supernodes.get(type)
			: null;
	}
		
	public Set<Node> getSupernodes(Collection<String> types) {
		if (!hasSupernodes()) {
			return null;
		}				
		
		Set<Node> supernodes = new HashSet<Node>();
		for (String type : types) {
			supernodes.addAll(getSupernodes(type));
		}
		return supernodes;
	}
	
	public Set<Node> getSupernodes(String... types) {
		return getSupernodes(Arrays.asList(types));
	}

	
	/* Other functions */
	
	@Override
	public String toString() {
		return "Node [id=" + id + ", type=" + type + ", data=" + data +
				", subnodes=" + ((type2subnodes == null) ? 0 : type2subnodes.size()) +
				", supernodes=" + ((type2supernodes == null) ? 0 : type2supernodes.size()) + "]";
	}

	@Override
	public int compareTo(Node o) {
		//return Integer.compare(this.data.hashCode(), o.data.hashCode());
		return Integer.compare(this.hashCode(), o.hashCode());
	}
}
