package KidneyExchange.Graph;

import java.util.*;
import java.util.stream.Collectors;

// Specifies a cycle found by CycleFinder.findCycles. A cycle is defined by a starting node and a
// directed path from that starting node that eventually arrives back at the starting node.
//
// For convenience, this class also lets one get the cycle's weight (which is the sum of the weights
// of each edge in the cycle's path) and test if a node is in a cycle in an efficient manner.
//
public class Cycle<T> {
    private Node<T> startNode;
    private List<DirectedEdge<T>> path;
    private double cycleWeight;
    private Set<Node<T>> nodes;

    public Cycle( Node<T> startNode, List<DirectedEdge<T>> path ) {
        this.startNode = startNode;
        this.path = path;
        this.cycleWeight = this.path.stream().mapToDouble( e -> e.getWeight() ).sum();
        this.nodes = new HashSet<>();
        for( DirectedEdge<T> edge : this.path ) {
            nodes.add( edge.getTarget() );
        }
    }

    public Node<T> getStartNode() {
        return startNode;
    }

    public List<DirectedEdge<T>> getPath() {
        return Collections.unmodifiableList( path );
    }

    public double getCycleWeight() {
        return cycleWeight;
    }

    public boolean hasNode( Node<T> node ) {
        return nodes.contains( node );
    }

    @Override
    public boolean equals( Object o ) {
        if( this == o ) return true;
        if( o == null || getClass() != o.getClass() ) return false;
        Cycle<?> cycle = (Cycle<?>) o;
        return Objects.equals( startNode, cycle.startNode ) &&
                Objects.equals( path, cycle.path );
    }

    @Override
    public int hashCode() {
        return Objects.hash( startNode, path );
    }
}
