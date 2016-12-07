package experimental.hypergraph.projection;

import java.util.ArrayList;
import java.util.Collection;

import experimental.hypergraph.Node;
import gnu.trove.set.hash.TIntHashSet;

public class Implementations {
	
	public static Collection<Node>
		getNeighborsOf_singleEdgeType_noTypeRestriction(Node node)
	{
		Collection<Node> neighbors = new ArrayList<>();
		
		for (Node edge : node.getNodes()) {
			for (Node neighbor : edge.getNodes()) {
				if (neighbor == node) {
					continue;
				}
				neighbors.add(neighbor);
			}
		}
		
		return neighbors;
	}
	
	
	public static Collection<Node>
		getNeighborsOf_multipleEdgesTypes_noTypeRestriction(Node node)
	{
		Collection<Node> neighbors = new ArrayList<>();
		
		TIntHashSet uniqNodes = new TIntHashSet();
		
		for (Node edge : node.getNodes()) {
			for (Node neighbor : edge.getNodes()) {
				if (neighbor == node) {
					continue;
				}
				if (uniqNodes.add(neighbor.id)) {
					neighbors.add(neighbor);
				}
			}
		}
		
		return neighbors;
	}

	public static Collection<Node> getNeighborsOf_singleEdgeType_useTypeRestriction(
		Node node,
		byte[] vertexTypes,
		byte[] edgeTypes)
	{
		Collection<Node> neighbors = new ArrayList<>();
		
		for (Node edge : node.getNodes(edgeTypes)) {
			for (Node neighbor : edge.getNodes(vertexTypes)) {

				if (neighbor == node) {
					continue;
				}
				neighbors.add(neighbor);
			}
		}
		
		return neighbors;
	}
	
	public static Collection<Node> getNeighborsOf_multipleEdgesTypes_useTypeRestriction(
		Node node,
		byte[] vertexTypes,
		byte[] edgeTypes) 
	{
		Collection<Node> neighbors = new ArrayList<>();
			
		TIntHashSet uniqNodes = new TIntHashSet();
		
		for (Node edge : node.getNodes(edgeTypes)) {
			for (Node neighbor : edge.getNodes(vertexTypes)) {
		
				if (neighbor == node) {
					continue;
				}
				if (uniqNodes.add(neighbor.id)) {
					neighbors.add(neighbor);
				}
			}
		}
		
		return neighbors;
	}
	
	public static int getDegreeOf_singleEdgeType_noTypeRestriction(Node node) {		
		int degree = 0;
		for (Node edge : node.getNodes()) {
			degree += edge.linkedNodes.getDegree() - 1;
		}
		return degree;
	}
	
	public static int getDegreeOf_multipleEdgesTypes_noTypeRestriction(Node node) {
		
		TIntHashSet uniqNodes = new TIntHashSet();
		
		for (Node edge : node.getNodes()) {
			for (Node neighbor : edge.getNodes()) {
				if (neighbor == node) {
					continue;
				}
				uniqNodes.add(neighbor.id);
			}
		}
		return uniqNodes.size();
	}
	
	public static int getDegreeOf_singleEdgeType_useTypeRestriction(
		Node node,
		byte[] vertexTypes,
		byte[] edgeTypes)
	{
		int degree = 0;
		for (Node edge : node.getNodes(edgeTypes)) {
			degree += edge.getNodes(vertexTypes).length - 1;
		}
		return degree;
	}

	
	public static int getDegreeOf_multipleEdgesTypes_useTypeRestriction(
		Node node,
		byte[] vertexTypes,
		byte[] edgeTypes)
	{
		TIntHashSet uniqNodes = new TIntHashSet();
		for (Node edge : node.getNodes(edgeTypes)) {
			for (Node neighbor : edge.getNodes(vertexTypes)) {
		
				if (neighbor == node) {
					continue;
				}
				uniqNodes.add(neighbor.id);
			}
		}
		return uniqNodes.size();
	}
	
}
