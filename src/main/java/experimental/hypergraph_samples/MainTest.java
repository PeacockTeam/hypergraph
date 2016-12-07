package experimental.hypergraph_samples;

import java.util.Arrays;
import java.util.List;

import experimental.hypergraph.HyperGraph;
import experimental.hypergraph.Node;
import experimental.hypergraph.aliases.NodeNameManager;
import experimental.hypergraph.projection.Projection;
import experimental.hypergraph.traverse.BreadsFirstSearchIterator;
import experimental.hypergraph.traverse.TraversalListener;


public class MainTest {

	public static void main(String[] args) {

		HyperGraph graph = new HyperGraph();

		graph.addNode(new Node("A", "Object"));
		graph.addNode(new Node("B", "Object"));
		graph.addNode(new Node("C", "Object"));
		graph.addNode(new Node("D", "Object"));
		graph.addNode(new Node("E", "Object"));
		graph.addNode(new Node("F", "Object"));
		graph.addNode(new Node("G", "Object"));
		graph.addNode(new Node("H", "Object"));

		Node AB = new Node("AB", "Edge");
		AB.add(graph.getNode("A"));
		AB.add(graph.getNode("B"));
		graph.addNode(AB);

		Node BC = new Node("BC", "Edge");
		BC.add(graph.getNode("B"));
		BC.add(graph.getNode("C"));
		graph.addNode(BC);

		Node CD = new Node("CD", "Edge");
		CD.add(graph.getNode("C"));
		CD.add(graph.getNode("D"));
		graph.addNode(CD);

		Node DA = new Node("DA", "Edge");
		DA.add(graph.getNode("D"));
		DA.add(graph.getNode("A"));
		graph.addNode(DA);

		Node FG = new Node("FG", "Edge");
		FG.add(graph.getNode("F"));
		FG.add(graph.getNode("G"));
		graph.addNode(FG);

		Node GH = new Node("GH", "Edge");
		GH.add(graph.getNode("G"));
		GH.add(graph.getNode("H"));
		graph.addNode(GH);


		Node ABC = new Node("ABC", "Group");
		ABC.add(graph.getNode("A"));
		ABC.add(graph.getNode("B"));
		ABC.add(graph.getNode("C"));
		graph.addNode(ABC);

		Node EDF = new Node("EDF", "Group");
		EDF.add(graph.getNode("E"));
		EDF.add(graph.getNode("D"));
		EDF.add(graph.getNode("F"));
		graph.addNode(EDF);

		Node AH = new Node("AH", "Group");
		AH.add(graph.getNode("A"));
		AH.add(graph.getNode("H"));
		graph.addNode(AH);


		graph.printInfo();
		graph.printLinkInfo();


		Projection projection = new Projection(
			graph,
			new String[] {"Object"},
			new String[] {"Edge" , "Group"}
		);

		System.out.println(projection.toString());


		System.out.println(projection.getVertices());
		System.out.println(projection.getEdges());
		System.out.println();

		System.out.println(projection.getNeighborsOf(graph.getNode("E")));
		System.out.println(projection.getDegreeOf(graph.getNode("E")));
		System.out.println();

		System.out.println(projection.getNeighborsOf(graph.getNode("A")));
		System.out.println(projection.getNeighborsOf(graph.getNode("F")));
		System.out.println(Arrays.asList(projection.getEdgesOf(graph.getNode("F"))));
		System.out.println(projection.getDegreeOf(graph.getNode("F")));
		System.out.println(projection.getDegreeOf(graph.getNode("H")));

		System.out.println();


		/* Iteration */

		BreadsFirstSearchIterator it =
			new BreadsFirstSearchIterator(projection);

		it.addTraversalListener(new TraversalListener() {
			@Override
			public void connectedComponentStarted() {
				System.out.println("connectedComponentStarted()");
			}

			@Override
			public void connectedComponentFinished() {
				System.out.println("\nconnectedComponentFinished()");
			}
		});

		while (it.hasNext()) {
			Node node = it.next();
			System.out.print(NodeNameManager.getName(node.id) + " ");
		}

		List<Node> path = projection.getShortestPath(
			graph.getNode("A"),
			graph.getNode("C"));

		System.out.println(path);
	}

}

