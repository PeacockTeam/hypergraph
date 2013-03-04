package performance;

import java.io.IOException;

public class Debug {

	final static int mb = 1024 * 1024;
	
	public static void printMemoryState() {
        Runtime runtime = Runtime.getRuntime();		
        System.out.println("Used Memory:" + (runtime.totalMemory() - runtime.freeMemory()) / mb + " mb");
        System.out.println("Free Memory:" + runtime.freeMemory() / mb + " mb");
        System.out.println("Total Memory:" + runtime.totalMemory() / mb + " mb");
        System.out.println("Max Memory:" + runtime.maxMemory() / mb + " mb");
        System.out.println();
	}
	
	public static void pause() {
		try {
			System.out.println("Hit any button continue");
			System.in.read();
		} catch (IOException e) {}
	}
	
	public static void runGC() {
		System.out.println("Running gc...");
		
		long before = Runtime.getRuntime().freeMemory();
		System.gc();
		long after = Runtime.getRuntime().freeMemory();
		
		System.out.println("Memory cleaned: " + (after - before) / mb + " mb");
	}

}