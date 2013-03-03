
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




public class Main {

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
		

		BreadsFirstSearchIterator it =
			new BreadsFirstSearchIterator(projection);
		
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
			System.out.println(node.id);
		}

		Debug.printMemoryState();
		
		
		/* Generation */
		
		StopWatch timer = new LoggingStopWatch("Generation");
		HyperGraph graph2 = generateRandom();
		timer.stop();		
		Debug.printMemoryState();
	
		
		
		projection = new Projection(
			graph2,
			new String[] { "N" },
			new String[] { "E", "G" }
		);
		
		class Counter {
			long count = 0;
		}
		final Counter connectedSetsCounter = new Counter();

		
		timer = new LoggingStopWatch("Traverse");
		BreadsFirstSearchIterator it1 =
			new BreadsFirstSearchIterator(projection);
			
		it1.addTraversalListener(new TraversalListener() {
			@Override
			public void connectedComponentStarted() {
				connectedSetsCounter.count++;
				//System.out.println("connectedComponentStarted()");
			}
				
			@Override
			public void connectedComponentFinished() {
				//System.out.println("connectedComponentFinished()");
			}
		});
		while (it1.hasNext()) {
			Node node = it1.next();
			//System.out.println(node.id);
		}
			
		System.out.println("Connected sets: " + connectedSetsCounter.count);
			
		timer.stop();
		
		Debug.printMemoryState();
		
		
		
		
		/* Connected sets */
		
		timer = new LoggingStopWatch("Connected sets");
		List<Set<Node>> connectedSets = projection.getConnectedSets();
		timer.stop();
		Debug.printMemoryState();
		
		System.out.println("Connected sets: " + connectedSets.size());
		for (Set<Node> connectedSet : connectedSets) {
			System.out.println(connectedSet.size());
		}
		
		
		//graph2.printSize();
		//graph2.printTypeSizes();
		//graph2.printLinkInfo();
		
		//Debug.printMemoryState();
	
		//System.out.println(graph2.getNode("G123").getSubnodes());
	}
	
	public static HyperGraph generateRandom() {
		HyperGraph graph = new HyperGraph();
		
		/*
		 * add, get без type2nodes - ~4с, 4G
		 * add, get c type2nodes - ~14с, 5G
		 * full без type2nodes - 16с, 10G
		 * full c type2nodes - 25с, 11G
		 * 
		 * HashMap:
		 * связи нодов ~ 6G
		 * пустые контейнеры связей ~ 2G
		 * данные ~ 4G
		 * 
		 * TreeMap:
		 * связи нодов ~ 3G
		 * пустые контейнеры связей ~ 2G
		 * данные ~ 1G
		 */
		
		int count = 10 * 1000 * 1000;
		
		StopWatch timer = new LoggingStopWatch("Adding simple nodes");
		for (Integer i = 0; i < count; i++) {
			graph.addNode(new Node(i, "N"));
		}
		timer.stop();
		
		timer = new LoggingStopWatch("Adding edges");
		for (Integer i = 0; i < count; i++) {

			Node e = new Node("E" + i.toString(), "E");
	
			e.add(graph.getNode(i % count));
			e.add(graph.getNode(i * 2 % count));		

			graph.addNode(e);
		}
		timer.stop();
		
		for (Integer i = 0; i < count / 10; i++) {

			Node g = new Node("G", "G" + i.toString());
			g.add(graph.getNode(i * 2 % count));
			g.add(graph.getNode(i * 3 % count));
			g.add(graph.getNode(i * 4 % count));
			
			graph.addNode(g);
		}
		
		return graph;
	}
	
	public static UndirectedGraph<Integer, String> createRandomJGraphT() {
		
		/*
		 * full 18c, 3,5G
		 */
		
		Map<Integer, Integer> nodeMap = new TreeMap<>();
		
		UndirectedGraph<Integer, String> graph = new Pseudograph<Integer, String>(String.class);
		List<String> temp = new ArrayList<>();
		
		int count = 10 * 1000 * 1000;
		
		StopWatch timer = new LoggingStopWatch("Adding simple nodes");
		for (Integer i = 0; i < count; i++) {
			graph.addVertex(i);
			nodeMap.put(i, i);
			temp.add("N");
		}
		timer.stop();
		
		timer = new LoggingStopWatch("Adding edges");
		for (Integer i = 0; i < count; i++) {
			
			Integer a = nodeMap.get(i % count);
			Integer b = nodeMap.get(i * 2 % count);
			
			graph.addEdge(a, b, "E" + i.toString());
			temp.add("E");
		}
		timer.stop();
		
		/*
		for (Integer i = 0; i < count; i++) {

			Node g = new Node("G", "G" + i.toString());
			g.add(graph.getNode(i * 2 % count));
			g.add(graph.getNode(i * 3 % count));
			g.add(graph.getNode(i * 4 % count));
			
			graph.addNode(g);
		}*/
		System.out.println("temp: " + temp.size());
		System.out.println("nodeMap: " + temp.size());
		
		return graph;
	}

}

