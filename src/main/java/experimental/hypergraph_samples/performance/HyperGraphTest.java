package experimental.hypergraph_samples.performance;

import java.util.List;
import java.util.Random;

import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;

import tools.Debug;
import experimental.hypergraph.HyperGraph;
import experimental.hypergraph.Node;
import experimental.hypergraph.aliases.NodeNameManager;
import experimental.hypergraph.aliases.NodeTypeManager;
import experimental.hypergraph.projection.Projection;
import experimental.hypergraph.traverse.BreadsFirstSearchIterator;
import experimental.hypergraph.traverse.ClosestFirstIterator;


public class HyperGraphTest {

	public static int count = 10 * 1000 * 1000;


	public static void main(String[] args) {

		HyperGraph graph = generateHyperGraph();

		Projection p = new Projection(
			graph,
			new String[] { "N" },
			new String[] { "E" }
		);

		System.out.println(p);

		testProjectionConnectedSets(p);
		testProjectionTraverse(p);
		testProjectionClosestTraverse(p);
		testProjectionShortestPath(p);
	}


	public static HyperGraph generateHyperGraph() {
		StopWatch timer = new LoggingStopWatch("generateHyperGraph()");

		HyperGraph graph = new HyperGraph();

		StopWatch timer1 = new LoggingStopWatch("Adding simple nodes");
		for (int i = 0; i < count; i++) {

			if (i % 1000000 == 0) {
				System.out.println(i);
			}

			graph.addNode(new Node(i, "N"));
		}
		timer1.stop();

		Random r = new Random();

		StopWatch timer2 = new LoggingStopWatch("Adding simple edges");
		for (Integer i = 0; i < count; i++) {

			if (i % 1000000 == 0) {
				System.out.println(i);
			}

			Node e = new Node((Integer)null, "E");
			e.add(graph.getNode(Math.abs(r.nextInt()) % count));
			e.add(graph.getNode(Math.abs(r.nextInt()) % count));
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

		graph.printInfo();
		graph.printLinkInfo();

		NodeTypeManager.printInfo();
		NodeNameManager.printInfo();

		Debug.printMemoryState();

		return graph;
	}

	public static void testProjectionTraverse(Projection p) {
		StopWatch timer = new LoggingStopWatch("testProjectionTraverse");

		BreadsFirstSearchIterator it =
			new BreadsFirstSearchIterator(p);

		while (it.hasNext()) {
			it.next();
		}

		timer.stop();
		Debug.printMemoryState();
	}

	public static void testProjectionClosestTraverse(Projection p) {
		StopWatch timer = new LoggingStopWatch("testProjectionClosestTraverse");

		ClosestFirstIterator it =
			new ClosestFirstIterator(p, true, null);

		while (it.hasNext()) {
			it.next();
		}

		timer.stop();
		Debug.printMemoryState();
	}

	public static void testProjectionConnectedSets(Projection p) {

		StopWatch timer = new LoggingStopWatch("testProjectionConnectedSets()");

		List<List<Node>> connectedSets = p.getConnectedSets();

		timer.stop();
		Debug.printMemoryState();

		System.out.println("Connected sets: " + connectedSets.size());
	}

	public static void testProjectionShortestPath(Projection p) {

		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			StopWatch timer = new LoggingStopWatch("testProjectionShortestPath()");
			List<Node> path = p.getShortestPath(
				p.graph.getNode(Math.abs(r.nextInt()) % count),
				p.graph.getNode(Math.abs(r.nextInt()) % count));
			System.out.println(path.size());
			timer.stop();
		}

		Debug.printMemoryState();
	}
}
