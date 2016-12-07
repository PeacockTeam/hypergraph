package experimental.hypergraph.aliases;

import gnu.trove.set.hash.TByteHashSet;

import java.util.Arrays;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class NodeTypeManager {
			
	private static BiMap<String, Byte> name2type = HashBiMap.create();
	private static byte nextType = 0;
	
	public static byte getType(String name) {
		Byte type = name2type.get(name);
		if (type == null) {			
			type = nextType++;
			name2type.put(name, type);			
		}		
		return type;
	}
	
//	public static byte allocateType(String name) {
//		if (name2type.containsKey(name)) {
//			throw new RuntimeException("Already existed type name: " + name);
//		}
//		
//		byte type = nextType++;
//		name2type.put(name, type);
//
//		return type;
//	}
//	
//	public static byte getType(String name) {
//		Byte type = name2type.get(name);
//		if (type == null) {
//			throw new RuntimeException("Unexpected type name: " + name);
//		}
//		return type;
//	}
	
	public static String getTypeName(byte type) {
		String name = name2type.inverse().get(type);
		if (name == null) {
			throw new RuntimeException("Unexpected type: " + type);
		}
		return name;
	}
	
	public static boolean containsType(String name) {
		return name2type.containsKey(name);
	}
	
	public static boolean containsType(byte type) {
		return name2type.inverse().containsKey(type);
	}
	
	public static byte[] convertTypes(String[] typeNames) {
		TByteHashSet typeSet = new TByteHashSet(); 
		for (String typeName : typeNames) {
			typeSet.add(NodeTypeManager.getType(typeName));
		}
		byte[] types = typeSet.toArray();
		Arrays.sort(types);		
		return types;
	}
	
	public static void printInfo() {
		System.out.println(
			"NodeTypeManager [name2type=" + name2type + ", nextType=" + nextType+ "]"
		);
	}		
}
