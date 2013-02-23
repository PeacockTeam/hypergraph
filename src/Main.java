
import hypergraph.Graph;
import hypergraph.Node;
import hypergraph.SimpleProjection;
import hypergraph.traverse.BreadsFirstSearchIterator;
import hypergraph.traverse.TraversalListener;

import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;



public class Main {

	public static void main(String[] args) {

		Graph graph = new Graph();
		
		graph.addNode(new Node("Object", "A"));
		graph.addNode(new Node("Object", "B"));
		graph.addNode(new Node("Object", "C"));
		graph.addNode(new Node("Object", "D"));
		graph.addNode(new Node("Object", "E"));
		graph.addNode(new Node("Object", "F"));
		graph.addNode(new Node("Object", "G"));
		graph.addNode(new Node("Object", "H"));
		
		
		Node AB = new Node("Edge", "AB");
		AB.add(graph.getNode("A"));
		AB.add(graph.getNode("B"));
		graph.addNode(AB);
		
		Node BC = new Node("Edge", "BC");
		BC.add(graph.getNode("B"));
		BC.add(graph.getNode("C"));
		graph.addNode(BC);
		
		Node CD = new Node("Edge", "CD");
		CD.add(graph.getNode("C"));
		CD.add(graph.getNode("D"));
		graph.addNode(CD);
		
		Node DA = new Node("Edge", "DA");
		DA.add(graph.getNode("D"));
		DA.add(graph.getNode("A"));
		graph.addNode(DA);
		
		Node FG = new Node("Edge", "FG");
		FG.add(graph.getNode("F"));
		FG.add(graph.getNode("G"));
		graph.addNode(FG);
		
		Node GH = new Node("Edge", "GH");
		GH.add(graph.getNode("G"));
		GH.add(graph.getNode("H"));
		graph.addNode(GH);
	
		
		Node ABC = new Node("Group", "ABC");
		ABC.add(graph.getNode("A"));
		ABC.add(graph.getNode("B"));
		ABC.add(graph.getNode("C"));
		graph.addNode(ABC);
		
		Node EDF = new Node("Group", "EDF");
		EDF.add(graph.getNode("E"));
		EDF.add(graph.getNode("D"));
		EDF.add(graph.getNode("F"));
		graph.addNode(EDF);
		
		
		graph.printTypeSizes();
		
		graph.printLinkInfo();
		
		SimpleProjection projection = new SimpleProjection("Object", "Edge");
		
		System.out.println(graph.getNode("A").getSupernodes("Group"));
		System.out.println(graph.getNode("A").getSupernodes());
		System.out.println(graph.getNode("A").getNeighbors(projection));
		System.out.println(graph.getNode("F").getNeighbors(projection));
		System.out.println(graph.getNode("F").getEdges(projection));
		System.out.println(graph.getNode("G").getDegree(projection));
		System.out.println();
		
		BreadsFirstSearchIterator it =
			new BreadsFirstSearchIterator(
				graph,
				new SimpleProjection("Object", "Edge"));
		
		it.addTraversalListener(new TraversalListener() {
			@Override
			public void connectedComponentStarted() {
				System.out.println("connectedComponentStarted()");
			}
			
			@Override
			public void connectedComponentFinished() {
				System.out.println("connectedComponentFinished()");
			}
		});
			
		
		while (it.hasNext()) {
			Node node = it.next();
			System.out.println(node.data);
		}
		
	
		Debug.printMemoryState();
		StopWatch timer = new LoggingStopWatch("Generation");
		Graph graph2 = generateRandom();
		timer.stop();
		
		
		graph2.printSize();
		graph2.printTypeSizes();
		graph2.printLinkInfo();
		
		
		Debug.printMemoryState();
	
		//System.out.println(graph2.getNode("G123").getSubnodes());
	}
	
	public static Graph generateRandom() {
		Graph graph = new Graph();
		
		int count = 1 * 1000 * 1000;
		
		StopWatch timer = new LoggingStopWatch("Adding simple nodes");
		for (Integer i = 0; i < count; i++) {
			graph.addNode(new Node("N", i));
		}
		timer.stop();
		
		timer = new LoggingStopWatch("Adding edges");
		for (Integer i = 0; i < count; i++) {

			Node e = new Node("E", "E" + i.toString());
						
			e.add(graph.getNode(i % count));
			e.add(graph.getNode(i * 2 % count));
			
			graph.addNode(e);
		}
		timer.stop();
		
		for (Integer i = 0; i < count; i++) {

			Node g = new Node("G", "G" + i.toString());
			g.add(graph.getNode(i * 2 % count));
			g.add(graph.getNode(i * 3 % count));
			g.add(graph.getNode(i * 4 % count));
			
			graph.addNode(g);
		}
		
		return graph;
	}

}

