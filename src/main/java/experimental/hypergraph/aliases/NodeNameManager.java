package experimental.hypergraph.aliases;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class NodeNameManager {
	
	private static BiMap<Integer, String> id2name = HashBiMap.create();
	private static int nextId = 0;	
	
	public static int getId(String name) {
		Integer id = id2name.inverse().get(name);
		if (id == null) {
			id = nextId++;
			id2name.put(id, name);
		}
		return id;
	}
	
	public static String getName(int id) {
		return id2name.get(id);
	}
	
	public static void printInfo() {
		System.out.println(
			"NodeNameManager [id2name=" + id2name + "]"
		);
	}
}
