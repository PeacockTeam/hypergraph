
import hypergraph.HyperGraph;
import hypergraph.Node;
import hypergraph.projection.Projection;
import hypergraph.traverse.BreadsFirstSearchIterator;
import hypergraph.traverse.TraversalListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.Pseudograph;
import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;




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
		
		
		graph.printSize();
		graph.printTypeSizes();
		graph.printLinkInfo();
		
		
		Projection projection = new Projection(
			graph,
			new String[] {"Object"},
			//new String[] {"Edge", "Group"}
			//new String[] {"Edge"}
			new String[] {"Group"}
		);
		
		System.out.println(projection.getVertices());
		System.out.println(projection.getEdges());
		
		System.out.println(projection.getNeighborsOf(graph.getNode("E")));
		System.out.println(projection.getDegreeOf(graph.getNode("E")));
		
		System.out.println(projection.getNeighborsOf(graph.getNode("A")));
		System.out.println(projection.getNeighborsOf(graph.getNode("F")));
		System.out.println(projection.getEdgesOf(graph.getNode("F")));
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
			System.out.print(node.id + " ");
		}	
	}

}

