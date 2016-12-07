package experimental.hypergraph.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import experimental.hypergraph.HyperGraph;
import experimental.hypergraph.Node;
import experimental.hypergraph.projection.Projection;

public class HyperGraphValidator {

	public static Collection<Node> getVoidEdges(Projection p) {
		
		List<Node> voidEdges = new ArrayList<>(); 
		for(Node edge : p.getEdges()) {
			if (edge.linkedNodes.getDegree() == 1) {
				voidEdges.add(edge);
			}
		}
		return voidEdges;		
	}
	
	public static boolean isOK(HyperGraph graph) {
		

		
		
		return true;
	}
}
