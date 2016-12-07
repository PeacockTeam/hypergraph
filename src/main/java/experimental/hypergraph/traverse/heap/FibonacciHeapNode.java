package experimental.hypergraph.traverse.heap;

public class FibonacciHeapNode<T> {

    T data;
    
    FibonacciHeapNode<T> child;   
    FibonacciHeapNode<T> left;    
    FibonacciHeapNode<T> parent;    
    FibonacciHeapNode<T> right;

    /** True if this node has had a child removed since this node was added to its parent */
    boolean mark;

    /** Key value for this node */
    double key;

    /** Number of children of this node (does not count grandchildren) */
    int degree;

    
    public FibonacciHeapNode(T data) {
        right = this;
        left = this;
        this.data = data;
    }

    public final double getKey() {
        return key;
    }

    public final T getData() {
        return data;
    }

    public String toString() {
        return Double.toString(key);
    }
}
