package KidneyExchange.Graph;

import java.util.Objects;

// Class that wraps a value of type T and associates it with an ID. This ID is used by the circuit
// finding algorithms in this package to provide an ordering on the nodes of a directed graph.
public class Node<T> {
    public static final int MIN_ID = 1;
    private int id;
    private T data;

    public static <T> Node<T> create( int id, T data ) {
        return new Node<>( id, data );
    }

    private Node( int id, T data ) {
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        Node<?> node = (Node<?>) o;
        return id == node.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash( id );
    }
}
