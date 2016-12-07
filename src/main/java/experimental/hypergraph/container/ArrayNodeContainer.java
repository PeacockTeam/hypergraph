package experimental.hypergraph.container;

import java.util.Arrays;

import experimental.hypergraph.Node;

public class ArrayNodeContainer implements NodeContainer {

	public static float reserveCoefficient = 1.5f;
	public static int defaultNodeCapacity = 1;
	public static final Node[] emptyArray = new Node[0];

	protected Node[] nodes;
	protected int size;
	protected int[] typeOffsets;

	public ArrayNodeContainer() {
		this(defaultNodeCapacity);
	}

	public ArrayNodeContainer(int capasity) {
		this.nodes = new Node[capasity];
		this.typeOffsets = new int[0];
		this.size = 0;
	}


	/* Node capacity management */

	protected void increaseNodeCapacity(int required, boolean reserve) {
		int newsize = size + required;
		if (newsize > nodes.length) {
			if (reserve) {
				setNodeCapacity((int) (newsize * reserveCoefficient));
			} else {
				setNodeCapacity(newsize);
			}
		}
	}

	protected void decreaseNodeCapasity() {
		if (nodes.length >= size * reserveCoefficient) {
			setNodeCapacity(size);
		}
	}

	protected void setNodeCapacity(int capasity) {
		Node[] newnodes = new Node[capasity];
		System.arraycopy(nodes, 0, newnodes, 0, Math.min(capasity, size));
		nodes = newnodes;
	}

	@Override
	public void minimizeCapacity() {
		if (size != nodes.length) {
			setNodeCapacity(size);
		}
	}


	/* Type capacity management */

	public void increaseTypeCapacity() {
		setTypeCapacity(typeOffsets.length + 1);
	}

	public void decreaseTypeCapacity() {
		if (typeOffsets.length > 0) {
			setTypeCapacity(typeOffsets.length - 1);
		}
	}

	protected void setTypeCapacity(int capasity) {
		int[] newTypeOffsets = new int[capasity];
		System.arraycopy(typeOffsets, 0, newTypeOffsets, 0, Math.min(typeOffsets.length, newTypeOffsets.length));
		typeOffsets = newTypeOffsets;
	}


	/* Fast pushing */

	public void fastInsert(Node node) {
		increaseNodeCapacity(1, false);
		nodes[size++] = node;
	}

	public void fastInsert(Node[] nodes) {
		increaseNodeCapacity(nodes.length, false);
		System.arraycopy(nodes, 0, this.nodes, size, nodes.length);
		size += nodes.length;
	}


	/* Common methods */

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public void clear() {
		setNodeCapacity(0);
		setTypeCapacity(0);
		size = 0;
	}

	@Override
	public boolean containsNode(Node node) {
		int res = findNodeIndex(node);
		return res >= 0;
	}

	protected int findNodeIndex(Node node) {
		int low = 0;
		int high = size - 1;

		while (low <= high) {
			int i = (low + high) >>> 1;

			int comp = nodes[i].compareTo(node);
			if (comp < 0) {
				low = i + 1;
			}
			else if (comp > 0) {
				high = i - 1;
			}
			else {
				return i; // key found
			}
		}
		return -(low + 1); // key not found.
	}

	protected int findTypeOffsetIndex(byte type) {
		int low = 0;
		int high = typeOffsets.length - 1;

		while (low <= high) {
			int i = (low + high) >>> 1;
			int value = decodeType(typeOffsets[i]);

			if (value < type) {
				low = i + 1;
			}
			else if (value > type) {
				high = i - 1;
			}
			else {
				return i; // key found
			}
		}
		return -(low + 1); // key not found.
	}

	@Override
	public boolean insertNode(Node node) {

		int i = findNodeIndex(node);
		if (i < 0) { // node not found
			increaseNodeCapacity(1, true);

			i  = - i - 1; // insertion point, see binarySearch() docs
			System.arraycopy(nodes, i, nodes, i + 1, size - i);
			nodes[i] = node;
			size++;

			updateTypeOffsetsOnNodeInserted(node.type, i);

			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean removeNode(Node node) {
		int i = findNodeIndex(node);
		if (i < 0) { // not found
			return false;
		}

		System.arraycopy(nodes, i + 1, nodes, i, size - i - 1);
		size--;

		decreaseNodeCapasity();

		updateTypeOffsetsOnNodeRemoved(node.type);

		return true;
	}


	protected void updateTypeOffsetsOnNodeInserted(byte type, int offset) {

		if (isValidOffset(offset) == false) {
//			assert(false);
		}

		int i = findTypeOffsetIndex(type);
		if (i < 0) { // type not found
			i = - i - 1; // insertion point, see binarySearch() docs
			insertTypeOffset(i, encodeTypeOffset(type, offset));
		}

		// shift following type offsets to the right
		for (int j = i + 1; j < typeOffsets.length; j++) {
			typeOffsets[j]++;
		}
	}


	protected void updateTypeOffsetsOnNodeRemoved(byte type) {

		int i = findTypeOffsetIndex(type);

		assert(i >= 0);

		// shift following offsets to the left
		for (int j = i + 1; j < typeOffsets.length; j++) {
			typeOffsets[j]--;
		}

		if (i + 1 < typeOffsets.length) {
			if (decodeOffset(typeOffsets[i]) == decodeOffset(typeOffsets[i + 1])) {
				// next type exists and has the same offset
				removeTypeOffset(i);
			}
		}
		else if (decodeOffset(typeOffsets[i]) >= size) {
			// last element of the rightmost type
			removeTypeOffset(i);
		}
	}


	protected void insertTypeOffset(int i, int value) {
		increaseTypeCapacity();
		System.arraycopy(typeOffsets, i, typeOffsets, i + 1, typeOffsets.length - i - 1);
		typeOffsets[i] = value;
	}

	protected void removeTypeOffset(int i) {
		System.arraycopy(typeOffsets, i + 1, typeOffsets, i, typeOffsets.length - i - 1);
		decreaseTypeCapacity();
	}


	/* Type-offset encoding */

	protected byte decodeType(int value) {
		return (byte)(value >> 24);
	}

	protected int decodeOffset(int value) {
		return value & 0x00FFFFFF;
	}

	protected int encodeTypeOffset(byte type, int offset) {
		return (type << 24) + offset;
	}

	protected boolean isValidOffset(int offset) {
		return offset < 0xFF000000;
	}


	@Override
	public Node[] getNodes() {
		return Arrays.copyOfRange(nodes, 0, size);
	}

	// XXX Implement iterator
	@Override
	public Node[] getNodes(byte type) {
		int i = findTypeOffsetIndex(type);

		if (i < 0) { // not found
			return emptyArray;
		}

		int from = decodeOffset(typeOffsets[i]);
		int to =
			(i + 1 < typeOffsets.length)
			? decodeOffset(typeOffsets[i + 1])
			: size;

		return Arrays.copyOfRange(nodes, from, to);
	}

	// XXX Implement iterator
	@Override
	public Node[] getNodes(byte... types) {
		// XXX Opt
		int degree = getDegree(types);
		Node[] nodes = new Node[degree];

		Arrays.sort(types);
		int i = 0;
		for (byte type : types) {
			Node[] tmp = getNodes(type);
			System.arraycopy(tmp, 0, nodes, i, tmp.length);
			i += tmp.length;
		}
		return nodes;
	}

	// XXX Add type support
	public Node[] getOppositeNodes(Node node) {
		int i = findNodeIndex(node);
		if (i >= 0) {
			Node[] res = new Node[size - 1];
			System.arraycopy(nodes, 0, res, 0, i);
			System.arraycopy(nodes, i + 1, res, i + 1, size - i - 1);
			return res;
		} else {
			return getNodes();
		}
	}

	// XXX Add type support
	public Node getOppositeNode(Node node) {
		for (int i = 0; i < size; i++) {
			if (nodes[i] != node) {
				return nodes[i];
			}
		}
		return null;
	}

	@Override
	public int getDegree() {
		return size;
	}

	@Override
	public int getDegree(byte type) {
		int i = findTypeOffsetIndex(type);

		if (i < 0) { // not found
			return 0;
		}

		int from = decodeOffset(typeOffsets[i]);
		int to =
			(i + 1 < typeOffsets.length)
			? decodeOffset(typeOffsets[i + 1])
			: size;

		return to - from;
	}

	@Override
	public int getDegree(byte... types) {
		int degree = 0;
		for (byte type : types) {
			degree += getDegree(type);
		}
		return degree;
	}

	@Override
	public int getNumberOfTypes() {
		return typeOffsets.length;
	}


	public static Node[] getMutualNodes(ArrayNodeContainer a, ArrayNodeContainer b, byte... types) {
		if (types.length > 0) {
			Node[] nodes1 = a.getNodes(types);
			Node[] nodes2 = b.getNodes(types);

			return ArrayNodeContainer.getMutualNodes(
				a.getNodes(types), nodes1.length,
				b.getNodes(types), nodes2.length);
		}

		return ArrayNodeContainer.getMutualNodes(
			a.nodes, a.size,
			b.nodes, b.size);
	}

	public static Node[] getMutualNodes(Node[] a, int asize, Node[] b, int bsize) {
		Node[] c = new Node[Math.min(asize, bsize)];

	    int ai = 0, bi = 0, ci = 0;
	    while (ai < asize && bi < bsize) {
	    	int comp = a[ai].compareTo(b[bi]);

	    	if (comp < 0) {
	            ai++;
	        }
	    	else if (comp > 0) {
	            bi++;
	        }
	    	else {
	            c[ci++] = a[ai];
	            ai++; bi++;
	        }
	    }
	    return Arrays.copyOfRange(c, 0, ci);
	}


	/* Debug */

	@Override
	public void print() {
		System.out.println("Container: " + size + "/" + nodes.length);
		for (int i = 0; i < size; i++) {
			System.out.println(i + "\t" + nodes[i].type + "\t" + nodes[i].id);
		}
		System.out.println("Types: " + typeOffsets.length);
		for (int i = 0; i < typeOffsets.length; i++) {
			System.out.println(decodeType(typeOffsets[i]) + "\t" + decodeOffset(typeOffsets[i]));
		}
	}
}
