package experimental.hypergraph_samples.performance;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.traverse.BreadthFirstIterator;
import org.jgrapht.traverse.ClosestFirstIterator;
import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;

import tools.Debug;

public class JGraphTest {

	public static int count = 10 * 1000 * 1000;

	public static void main(String[] args) {

		UndirectedGraph<Integer, DefaultEdge> graph = createJGraphT();

		testTraverse(graph);
		testClosestTraverse(graph);
		testConnectedSets(graph);
		testShortestPath(graph);
	}

	public static UndirectedGraph<Integer, DefaultEdge> createJGraphT() {

		StopWatch timer = new LoggingStopWatch("createJGraphT()");

		Map<Integer, Integer> nodeMap = new TreeMap<>();

		UndirectedGraph<Integer, DefaultEdge> graph =
			new Pseudograph<Integer, DefaultEdge>(DefaultEdge.class);

		StopWatch timer1 = new LoggingStopWatch("Adding simple nodes");
		for (Integer i = 0; i < count; i++) {
			graph.addVertex(i);
			nodeMap.put(i, i);
		}
		timer1.stop();

		StopWatch timer2 = new LoggingStopWatch("Adding simple edges");
		for (Integer i = 0; i < count; i++) {

			Integer a = nodeMap.get(i % count);
			Integer b = nodeMap.get(i * 2 % count);

			graph.addEdge(a, b);
		}
		timer2.stop();

		timer.stop();
		Debug.printMemoryState();

		return graph;
	}

	public static void testTraverse(UndirectedGraph<Integer, DefaultEdge> graph) {
		StopWatch timer = new LoggingStopWatch("testTraverse");

		class Counter {
			long count = 0;
		}
		final Counter connectedSetsCounter = new Counter();


		BreadthFirstIterator<Integer, DefaultEdge> it =
			new BreadthFirstIterator<Integer, DefaultEdge>(graph);

//		it.addTraversalListener(new TraversalListener<Integer, DefaultEdge>() {
//			@Override
//			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
//				connectedSetsCounter.count++;
//			}
//
//			@Override public void vertexTraversed(VertexTraversalEvent<Integer> e) {}
//			@Override public void vertexFinished(VertexTraversalEvent<Integer> e) {}
//			@Override public void edgeTraversed(EdgeTraversalEvent<Integer, DefaultEdge> e) {}
//			@Override public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {}
//		});

		while (it.hasNext()) {
			it.next();
		}

		System.out.println("Connected sets: " + connectedSetsCounter.count);

		timer.stop();
		Debug.printMemoryState();
	}


	public static void testClosestTraverse(UndirectedGraph<Integer, DefaultEdge> graph) {
		StopWatch timer = new LoggingStopWatch("testClosestTraverse");

		class Counter {
			long count = 0;
		}
		final Counter connectedSetsCounter = new Counter();


		ClosestFirstIterator<Integer, DefaultEdge> it =
			new ClosestFirstIterator<Integer, DefaultEdge>(graph);

//		it.addTraversalListener(new TraversalListener<Integer, DefaultEdge>() {
//			@Override
//			public void connectedComponentFinished(ConnectedComponentTraversalEvent e) {
//				connectedSetsCounter.count++;
//			}
//
//			@Override public void vertexTraversed(VertexTraversalEvent<Integer> e) {}
//			@Override public void vertexFinished(VertexTraversalEvent<Integer> e) {}
//			@Override public void edgeTraversed(EdgeTraversalEvent<Integer, DefaultEdge> e) {}
//			@Override public void connectedComponentStarted(ConnectedComponentTraversalEvent e) {}
//		});

		while (it.hasNext()) {
			it.next();
		}

		System.out.println("Connected sets: " + connectedSetsCounter.count);

		timer.stop();
		Debug.printMemoryState();
	}


	public static void testConnectedSets(UndirectedGraph<Integer, DefaultEdge> graph) {

		StopWatch timer = new LoggingStopWatch("testConnectedSets()");

		ConnectivityInspector<Integer, DefaultEdge> connectivityInspector =
			new ConnectivityInspector<Integer, DefaultEdge>(graph);
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
	}

	public static void testShortestPath(UndirectedGraph<Integer, DefaultEdge> graph) {

		List<Integer> vertices = new ArrayList<Integer>(graph.vertexSet());

		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			StopWatch timer = new LoggingStopWatch("testProjectionShortestPath()");

			DijkstraShortestPath.findPathBetween(
				graph,
				vertices.get(Math.abs(r.nextInt()) % count),
				vertices.get(Math.abs(r.nextInt()) % count));

			timer.stop();
		}

		Debug.printMemoryState();
	}
}
