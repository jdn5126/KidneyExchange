package KidneyExchange.Graph;

import java.util.*;

// Directed graph where each node stores domain-specific data of type T. Values of type T must be
// usable as keys in a HashMap.
public class DirectedGraph<T> {
    private int nextNodeId = Node.MIN_ID;

    // Checks to see if a previous T has been already wrapped as a Node that we can reuse.
    private Map<T, Node<T>> dataToNodes = new HashMap<>();

    // This is just provide fast lookup for finding a node by ID.
    private Map<Integer, Node<T>> idToNodes = new HashMap<>();

    private Set<Node<T>> nodes = new HashSet<>();
    private Map<Node<T>, Set<DirectedEdge<T>>> successors = new HashMap<>();

    public void addEdge( T from, T to ) {
        addEdgeWithWeight( from, to, 1 );
    }

    public void addEdgeWithWeight( T from, T to, int weight ) {
        Node<T> fromNode = dataToNodes.computeIfAbsent( from, k -> {
            Node<T> n = createNode( from );
            nodes.add( n );
            idToNodes.put( n.getId(), n );
            return n;
        } );

        Node<T> toNode = dataToNodes.computeIfAbsent( to, k -> {
            Node<T> n = createNode( to );
            nodes.add( n );
            idToNodes.put( n.getId(), n );
            return n;
        } );

        Set<DirectedEdge<T>> fromSuccessors = successors.computeIfAbsent( fromNode, key -> new HashSet<>() );
        fromSuccessors.add( new DirectedEdge<>( toNode, weight ) );
    }

    public Set<Node<T>> getNodes() {
        return Collections.unmodifiableSet( nodes );
    }

    public Node<T> getNodeFromId( int id ) {
        return idToNodes.get( id );
    }

    public Node<T> getNodeFromT( T data ) {
        return dataToNodes.get( data );
    }

    public Set<DirectedEdge<T>> getOutgoingEdgesForNode( Node<T> node ) {
        return Collections.unmodifiableSet( successors.computeIfAbsent( node, key -> new HashSet<>() ) );
    }

    private Node<T> createNode( T data ) {
        return Node.create( nextNodeId++, data );
    }
}
