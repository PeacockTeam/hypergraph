package performance;
import hypergraph.HyperGraph;
import hypergraph.Node;
import hypergraph.projection.Projection;
import hypergraph.traverse.BreadsFirstSearchIterator;
import hypergraph.traverse.TraversalListener;

import java.util.List;
import java.util.Set;

import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;


public class HyperGraphTest {

	public static void main(String[] args) {
			
		HyperGraph graph = generateHyperGraph();
				
		Projection projection = new Projection(
			graph,
			new String[] { "N" },
			new String[] { "E" }
		);
		
		//testProjectionTraverse(projection);
		testProjectionConnectedSets(projection);
		testProjectionConnectedSets(projection);
		testProjectionTraverse(projection);
	}
	
	public static HyperGraph generateHyperGraph() {
		StopWatch timer = new LoggingStopWatch("generateHyperGraph()");
		
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
		
		
		StopWatch timer1 = new LoggingStopWatch("Adding simple nodes");
		for (Integer i = 0; i < count; i++) {
			graph.addNode(new Node(i, "N"));
		}
		timer1.stop();
		
		
		StopWatch timer2 = new LoggingStopWatch("Adding simple edges");
		for (Integer i = 0; i < count; i++) {

			Node e = new Node("E" + i.toString(), "E");
			e.add(graph.getNode(i % count));
			e.add(graph.getNode(i * 2 % count));
			graph.addNode(e);
		}
		timer2.stop();
		
		
//		StopWatch timer3 = new LoggingStopWatch("Adding group edges");
//		for (Integer i = 0; i < count / 10; i++) {
//
//			Node g = new Node("G", "G" + i.toString());
//			g.add(graph.getNode(i * 2 % count));
//			g.add(graph.getNode(i * 3 % count));
//			g.add(graph.getNode(i * 4 % count));
//			
//			graph.addNode(g);
//		}
//		timer3.stop();
		
		timer.stop();
		Debug.printMemoryState();
		
		graph.printSize();
		graph.printTypeSizes();
		graph.printLinkInfo();
		
		return graph;
	}
	
	public static void testProjectionTraverse(Projection projection) {
		StopWatch timer = new LoggingStopWatch("testProjectionTraverse");
		
		class Counter {
			long count = 0;
		}
		final Counter connectedSetsCounter = new Counter();
		
		BreadsFirstSearchIterator it =
			new BreadsFirstSearchIterator(projection);
			
		it.addTraversalListener(new TraversalListener() {
			@Override
			public void connectedComponentStarted() {
				//connectedSetsCounter.count++;
			}
				
			@Override
			public void connectedComponentFinished() {
			}
		});
		
		while (it.hasNext()) {
			it.next();
		}
			
		System.out.println("Connected sets: " + connectedSetsCounter.count);
			
		timer.stop();
		Debug.printMemoryState();
	}
	
	public static void testProjectionConnectedSets(Projection projection) {
		
		StopWatch timer = new LoggingStopWatch("testProjectionConnectedSets()");	
		
		List<Set<Node>> connectedSets = projection.getConnectedSets();
		
		timer.stop();
		Debug.printMemoryState();
		
		System.out.println("Connected sets: " + connectedSets.size());
		for (Set<Node> connectedSet : connectedSets) {
			System.out.print(connectedSet.size() + " ");
		}		
	}
}
