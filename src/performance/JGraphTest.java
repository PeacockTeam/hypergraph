package performance;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.event.ConnectedComponentTraversalEvent;
import org.jgrapht.event.EdgeTraversalEvent;
import org.jgrapht.event.TraversalListener;
import org.jgrapht.event.VertexTraversalEvent;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;

public class JGraphTest {

	public static void main(String[] args) {
		
		UndirectedGraph<Integer, String> graph = createJGraphT();

		testTraverse(graph);
		testConnectedSets(graph);
	}
	
	public static UndirectedGraph<Integer, String> createJGraphT() {
		
		/*
		 * full 18c, 3,5G
		 */
	
		StopWatch timer = new LoggingStopWatch("createJGraphT()");
		
		Map<Integer, Integer> nodeMap = new TreeMap<>();
		
		UndirectedGraph<Integer, String> graph = new Pseudograph<Integer, String>(String.class);
		List<String> temp = new ArrayList<>();
		
		int count = 10 * 1000 * 1000;
		
		StopWatch timer1 = new LoggingStopWatch("Adding simple nodes");
		for (Integer i = 0; i < count; i++) {
			graph.addVertex(i);
			nodeMap.put(i, i);
			temp.add("N");
		}
		timer1.stop();
		
		StopWatch timer2 = new LoggingStopWatch("Adding simple edges");
		for (Integer i = 0; i < count; i++) {
			
			Integer a = nodeMap.get(i % count);
			Integer b = nodeMap.get(i * 2 % count);
			
			graph.addEdge(a, b, "E" + i.toString());
			temp.add("E");
		}
		timer2.stop();
		
		System.out.println("temp: " + temp.size());
		System.out.println("nodeMap: " + temp.size());
		
		timer.stop();
		Debug.printMemoryState();
		
		return graph;
	}
	
	public static void testTraverse(UndirectedGraph<Integer, String> graph) {
		StopWatch timer = new LoggingStopWatch("testTraverse");
		
		class Counter {
			long count = 0;
		}
		final Counter connectedSetsCounter = new Counter();
	
		
		BreadthFirstIterator<Integer, String> it =
			new BreadthFirstIterator<Integer, String>(graph);
		
		it.addTraversalListener(new TraversalListener<Integer, String>() {
			@Override
			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
				connectedSetsCounter.count++;
			}
			
			@Override public void vertexTraversed(VertexTraversalEvent<Integer> e) {}
			@Override public void vertexFinished(VertexTraversalEvent<Integer> e) {}			
			@Override public void edgeTraversed(EdgeTraversalEvent<Integer, String> e) {}
			@Override public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {}
		});
				
		while (it.hasNext()) {
			it.next();
		}
			
		System.out.println("Connected sets: " + connectedSetsCounter.count);
			
		timer.stop();
		Debug.printMemoryState();
	}
	
	public static void testConnectedSets(UndirectedGraph<Integer, String> graph) {
		
		StopWatch timer = new LoggingStopWatch("testConnectedSets()");	
				
		ConnectivityInspector<Integer, String> connectivityInspector = 
			new ConnectivityInspector<Integer, String>(graph);			
		List<Set<Integer>> connectedSets = connectivityInspector.connectedSets();
				
		Collections.sort(connectedSets, new Comparator<Set<Integer>>() {
			@Override
			public int compare(Set<Integer> set1, Set<Integer> set2) {
				/* Descending order */
				return Long.compare(set2.size(), set1.size());
			}			
		});
		
		timer.stop();
		Debug.printMemoryState();
		
		System.out.println("Connected sets: " + connectedSets.size());
		for (Set<Integer> connectedSet : connectedSets) {
			System.out.print(connectedSet.size() + " ");
		}		
	}

}
