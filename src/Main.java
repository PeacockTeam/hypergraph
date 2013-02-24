
import hypergraph.Graph;
import hypergraph.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.Pseudograph;
import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;



public class Main {

	public static void main(String[] args) {

	/*	Graph graph = new Graph();
		
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
		*/
	
		Debug.printMemoryState();
		StopWatch timer = new LoggingStopWatch("Generation");
		
		Graph graph2 = generateRandom();
		
		//UndirectedGraph<Integer, String> g =createRandomJGraphT();
		//System.out.println("g: " + g.vertexSet().size() + " " + g.edgeSet().size());
		
		timer.stop();
		
		Debug.printMemoryState();
		
		//graph2.printSize();
		//graph2.printTypeSizes();
		//graph2.printLinkInfo();
		
		//Debug.printMemoryState();
	
		//System.out.println(graph2.getNode("G123").getSubnodes());
	}
	
	public static Graph generateRandom() {
		Graph graph = new Graph();
		
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
		
		/*
		for (Integer i = 0; i < count; i++) {

			Node g = new Node("G", "G" + i.toString());
			g.add(graph.getNode(i * 2 % count));
			g.add(graph.getNode(i * 3 % count));
			g.add(graph.getNode(i * 4 % count));
			
			graph.addNode(g);
		}*/
		
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

