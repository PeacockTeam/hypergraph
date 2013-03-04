package hypergraph.projection;

import hypergraph.HyperGraph;
import hypergraph.Node;
import hypergraph.traverse.BreadsFirstSearchIterator;
import hypergraph.traverse.TraversalListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class Projection {

	public HyperGraph graph;	
	public Set<String> vertexTypes = new TreeSet<>();
	public Set<String> edgeTypes = new TreeSet<>();
	
	
	/* Constructors */
	
	public Projection(HyperGraph graph, List<String> vertexTypes, List<String> edgeTypes) {
		this.graph = graph;
		this.vertexTypes.addAll(vertexTypes);
		this.edgeTypes.addAll(edgeTypes);
	}

	public Projection(HyperGraph graph, String[] vertexTypes, String[] edgeTypes) {
		this(
			graph,
			Arrays.asList(vertexTypes),
			Arrays.asList(edgeTypes));
	}

	
	/* Projection functions */
	
	public Collection<Node> getVertices() {
		Collection<Node> vertices = new ArrayList<>();
		for (String vertexType : vertexTypes) {
			vertices.addAll(graph.getNodes(vertexType));
		}		
		return vertices;
	}
	
	public Collection<Node> getEdges() {
		Collection<Node> edges = new ArrayList<>();
		for (String edgeType : edgeTypes) {
			edges.addAll(graph.getNodes(edgeType));
		}		
		return edges;
	}
	
	public Collection<Node> getEdgesOf(Node node) {
		checkVertexType(node);
		return node.getSupernodes(edgeTypes);
	}
		
	public Collection<Node> getNeighborsOf(Node node) {
		checkVertexType(node);
				
		Collection<Node> neighbors = new HashSet<>(); // Collection of unique nodes

		Collection<Node> edges = node.getSupernodes(edgeTypes);
		if (edges == null) {
			return neighbors;
		}
		
		for (Node edge : edges) {
			for (Node neighbor : edge.getSubnodes(vertexTypes)) {
				if (neighbor == node) {
					continue;
				}
				neighbors.add(neighbor);
			}
		}
		
		return neighbors;
	}

	public long getDegreeOf(Node node) {
		/* XXX Make it faster */
		return getNeighborsOf(node).size();
	}
	
	
	public List<Set<Node>> getConnectedSets() {

		final List<Set<Node>> connectedSets = new ArrayList<Set<Node>>();
		final Set<Node> tempConnectedSet = new HashSet<Node>();
		
		BreadsFirstSearchIterator it = new BreadsFirstSearchIterator(this);
			
		it.addTraversalListener(new TraversalListener() {
			@Override
			public void connectedComponentStarted() {
			}
				
			@Override
			public void connectedComponentFinished() {
				if (!tempConnectedSet.isEmpty()) {
					connectedSets.add(new HashSet<Node>(tempConnectedSet));
					tempConnectedSet.clear();
				}
			}
		});
		
		while (it.hasNext()) {
			tempConnectedSet.add(it.next());
		}
		
		/* Sort by size */
		Collections.sort(connectedSets, new Comparator<Set<Node>>() {
			@Override
			public int compare(Set<Node> set1, Set<Node> set2) {
				/* Descending order */
				return Long.compare(set2.size(), set1.size());
			}			
		});
		
		return connectedSets;
	}
	
	
	/* Internal functions */
	
	protected void checkVertexType(Node node) {
		if (!vertexTypes.contains(node.type)) {
			throw new RuntimeException(
				"Node type " + node.type +
				" not allowed in this projection");
		}
	}
}
