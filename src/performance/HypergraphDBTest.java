package performance;


import org.hypergraphdb.HGHandle;
import org.hypergraphdb.HGPlainLink;
import org.hypergraphdb.HyperGraph;
import org.hypergraphdb.HGQuery.hg;
import org.hypergraphdb.algorithms.DefaultALGenerator;
import org.hypergraphdb.algorithms.HGALGenerator;
import org.hypergraphdb.algorithms.HGBreadthFirstTraversal;
import org.hypergraphdb.util.Pair;
import org.perf4j.LoggingStopWatch;
import org.perf4j.StopWatch;

public class HypergraphDBTest {

	public static HGHandle first;
	
	public static void main(String[] args) {

		HyperGraph graph = generateHyperGraph();
				
		testTraverse(graph);
		//testProjectionConnectedSets(projection);
	}
	
	public static HyperGraph generateHyperGraph() {
		/* Open and clear db */
		HyperGraph graph = new HyperGraph("c:\\HypergraphDB");	
				
		StopWatch timer = new LoggingStopWatch("generateHyperGraph()");
		
		int count = 10 * 1000 * 1000;
			
		HGHandle[] handles = new HGHandle[count];
		
		
		StopWatch timer1 = new LoggingStopWatch("Adding simple nodes");
		for (Integer i = 0; i < count; i++) {
			handles[i] = graph.add(i);
		}
		timer1.stop();
		
		first = handles[0];
		
		StopWatch timer2 = new LoggingStopWatch("Adding simple edges");
		for (Integer i = 0; i < count - 1; i++) {

			graph.add(new HGPlainLink(
				handles[i],
				handles[i + 1]));
			
//			Node e = new Node("E" + i.toString(), "E");
//			e.add(graph.getNode(i % count));
//			e.add(graph.getNode(i * 2 % count));		
//			graph.addNode(e);
		}
		timer2.stop();
		
//		StopWatch timer2 = new LoggingStopWatch("Adding simple edges");
//		for (Integer i = 0; i < count; i++) {
//
//			graph.add(new HGPlainLink(
//				handles[i % count],
//				handles[i * 2 % count]));
//			
////			Node e = new Node("E" + i.toString(), "E");
////			e.add(graph.getNode(i % count));
////			e.add(graph.getNode(i * 2 % count));		
////			graph.addNode(e);
//		}
//		timer2.stop();
		
		
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
		
		return graph;
	}
	
	public static void testTraverse(HyperGraph graph) {
		StopWatch timer = new LoggingStopWatch("testTraverse()");
		
		HGALGenerator alGen = new DefaultALGenerator(
			graph,
			hg.type(HGPlainLink.class), // link
			hg.type(Integer.class),	// atom
			true, true, false);
		
			
		HGBreadthFirstTraversal trav = new HGBreadthFirstTraversal(first, alGen);
		
		while (trav.hasNext()) {
			Pair<HGHandle, HGHandle> pair = trav.next();
			//System.out.println(graph.get(pair.getSecond()));
		}
			
		timer.stop();
		Debug.printMemoryState();
	}
	
	/*
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
	*/

}
