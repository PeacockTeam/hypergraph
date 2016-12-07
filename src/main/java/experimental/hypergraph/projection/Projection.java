package experimental.hypergraph.projection;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import experimental.hypergraph.HyperGraph;
import experimental.hypergraph.Node;
import experimental.hypergraph.aliases.NodeTypeManager;
import experimental.hypergraph.container.ArrayNodeContainer;
import experimental.hypergraph.traverse.BreadsFirstSearchIterator;
import experimental.hypergraph.traverse.ClosestFirstIterator;
import experimental.hypergraph.traverse.TraversalListener;


public class Projection {

	public HyperGraph graph;
	public byte[] vertexTypes;
	public byte[] edgeTypes;

	public boolean useTypeRestrictions = false;

	/* Constructors */

	public Projection(HyperGraph graph, String[] vertexTypes, String[] edgeTypes) {
		this.graph = graph;
		this.vertexTypes = NodeTypeManager.convertTypes(vertexTypes);
		this.edgeTypes = NodeTypeManager.convertTypes(edgeTypes);

		configure();
	}

	public Projection(HyperGraph graph, String vertexType, String edgeType) {
		this(graph, new String[] { vertexType }, new String[] { edgeType });
	}
	
	public Projection(HyperGraph graph, String vertexType, String[] edgeTypes) {
		this(graph, new String[] { vertexType }, edgeTypes);
	}
	
	public Projection(HyperGraph graph, String[] vertexTypes, String edgeType) {
		this(graph, vertexTypes, new String[] { edgeType });
	}
	
	protected void configure() {

		/* PerformTypeRestrictions only if graph has types not supported by this projection */
		/* XXX This code will work if only types in graph are immutable */ 
//		for (byte graphType : graph.getTypes()) {
//			if (!ArrayUtils.contains(vertexTypes, graphType) &&
//				!ArrayUtils.contains(edgeTypes, graphType))
//			{
//				useTypeRestrictions = true;
//				break;
//			}
//		}

		useTypeRestrictions = true;
	}

	/* Projection functions */

	public Collection<Node> getVertices() {
		return graph.getNodes(vertexTypes);
	}

	public Collection<Node> getEdges() {
		return graph.getNodes(edgeTypes);
	}

	public Node[] getEdgesOf(Node node) {
		if (useTypeRestrictions) {
			return node.getNodes(edgeTypes);
		} else {
			return node.getNodes();
		}
	}

	public Node[] getVerticesOf(Node node) {
		if (useTypeRestrictions) {
			return node.getNodes(vertexTypes);
		} else {
			return node.getNodes();
		}
	}

	public Node[] getEdgesOf(Node vertex1, Node vertex2) {

		Node[] mutualEdges =
			ArrayNodeContainer.getMutualNodes(
				(ArrayNodeContainer) vertex1.linkedNodes,
				(ArrayNodeContainer) vertex2.linkedNodes,
				useTypeRestrictions ? edgeTypes : ArrayUtils.EMPTY_BYTE_ARRAY
			);

		return mutualEdges;
	}

	// XXX Opt
	public Collection<Node> getNeighborsOf(Node node) {

		if (node.linkedNodes.getNumberOfTypes() > 1) {
			if (useTypeRestrictions) {
				return Implementations.getNeighborsOf_multipleEdgesTypes_useTypeRestriction(
					node,
					vertexTypes,
					edgeTypes);
			}
			else {
				return Implementations.getNeighborsOf_multipleEdgesTypes_noTypeRestriction(
					node);
			}
		}
		else {
			if (useTypeRestrictions) {
				return Implementations.getNeighborsOf_singleEdgeType_useTypeRestriction(
					node,
					vertexTypes,
					edgeTypes);
			}
			else {
				return Implementations.getNeighborsOf_singleEdgeType_noTypeRestriction(
					node);
			}
		}
	}

	// XXX Opt
	public int getDegreeOf(Node node) {

		if (node.linkedNodes.getNumberOfTypes() > 1) {
			if (useTypeRestrictions) {
				return Implementations.getDegreeOf_multipleEdgesTypes_useTypeRestriction(
					node,
					vertexTypes,
					edgeTypes);
			}
			else {
				return Implementations.getDegreeOf_multipleEdgesTypes_noTypeRestriction(
					node);
			}
		}
		else {
			if (useTypeRestrictions) {
				return Implementations.getDegreeOf_singleEdgeType_useTypeRestriction(
					node,
					vertexTypes,
					edgeTypes);
			}
			else {
				return Implementations.getDegreeOf_singleEdgeType_noTypeRestriction(
					node);
			}
		}
	}

	/* Common useful functions */


	public List<List<Node>> getConnectedSets() {

		final List<List<Node>> connectedSets = new ArrayList<List<Node>>();
		final List<Node> tempConnectedSet = new ArrayList<Node>();

		BreadsFirstSearchIterator it = new BreadsFirstSearchIterator(this);

		it.addTraversalListener(new TraversalListener() {
			@Override
			public void connectedComponentStarted() {
			}

			@Override
			public void connectedComponentFinished() {
				if (!tempConnectedSet.isEmpty()) {
					connectedSets.add(new ArrayList<Node>(tempConnectedSet));
					tempConnectedSet.clear();
				}
			}
		});

		while (it.hasNext()) {
			tempConnectedSet.add(it.next());
		}

		/* Sort by size */
		Collections.sort(connectedSets, new Comparator<List<Node>>() {
			@Override
			public int compare(List<Node> set1, List<Node> set2) {
				/* Descending order */
				return Integer.compare(set2.size(), set1.size());
			}
		});

		return connectedSets;
	}

	public List<Node> getShortestPath(Node startVertex, Node endVertex) {

		List<Node> path = new ArrayList<Node>();

        ClosestFirstIterator it =
            new ClosestFirstIterator(this, false, startVertex);

        while (it.hasNext()) {
            Node vertex = it.next();

            if (vertex == endVertex) {
                Node v = endVertex;
                while (v != null) {
                    path.add(v);
                	v = it.getSpanningTreeVertex(v);
                }
                Collections.reverse(path);
                break;
            }
        }

        return path;
	}


	@Override
	public String toString() {
		return "Projection [vertexTypes=" + Arrays.toString(vertexTypes)
				+ ", edgeTypes=" + Arrays.toString(edgeTypes)
				+ ", useTypeRestrictions=" + useTypeRestrictions
				+ "]";
	}
}
