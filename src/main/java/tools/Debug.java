package tools;

import java.io.IOException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.perf4j.StopWatch;


public class Debug {

	static Logger logger = Logger.getLogger(Debug.class);


	final static int mb = 1024 * 1024;

	public static void printMemoryState() {
		printMemoryState(logger);
	}

	public static void printMemoryState(Logger logger) {
        Runtime runtime = Runtime.getRuntime();

        // Memory state, used/total/max: 123/323/500; free: 312

        logger.debug("");
        logger.debug(String.format(
        		"Memroy state (mb), used/total/max: %d/%d/%d; free: %d",
        		(runtime.totalMemory() - runtime.freeMemory()) / mb,
        		runtime.totalMemory() / mb,
        		runtime.maxMemory() / mb,
        		runtime.freeMemory() / mb
        		));
        logger.debug("");
	}

	public static void printMemoryStateFull() {
        Runtime runtime = Runtime.getRuntime();
        logger.debug("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) + " b");
        logger.debug("Free Memory:" + runtime.freeMemory() + " b");
        logger.debug("Total Memory:" + runtime.totalMemory() + " b");
        logger.debug("Max Memory:" + runtime.maxMemory() + " b");
        logger.debug("\n");
	}

	public static void pause() {
		try {
			logger.debug("Hit any button continue");
			System.in.read();
		} catch (IOException e) {}
	}

	public static void runGC() {
		runGC(logger);
	}

	public static void runGC(Logger logger) {
		runGC(logger, Level.DEBUG);
	}

	public static void runGC(Logger logger, Level level) {
		StopWatch timer = new StopWatch("runGC");
		logger.log(level, "Running gc...");

		long before = Runtime.getRuntime().freeMemory();
		System.gc();
		long after = Runtime.getRuntime().freeMemory();

		logger.log(level,
				String.format("Memory cleaned: %d mb; Running time %ds",
						(after - before) / mb, timer.getElapsedTime() / 1000));
	}

//	public static <V, E> String graphInfo(org.jgrapht.Graph<V, E> g) {
//		return "(" + g.vertexSet().size() + "/" + g.edgeSet().size() + ")";
//	}
//
//	public static <V, E> String graphInfo(edu.uci.ics.jung.graph.Graph<V, E> g) {
//		if (g == null)
//			return null;
//
//		return "(" + g.getVertexCount() + "/" + g.getEdgeCount() + ")";
//	}
}
