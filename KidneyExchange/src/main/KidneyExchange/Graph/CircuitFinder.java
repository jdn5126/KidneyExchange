package KidneyExchange.Graph;

import java.util.*;
import java.util.stream.Collectors;

// Helper class for implementing Donald Johnson's algorithm for finding elementary circuits in a
// directed graph. Based on the algorithm described in the following paper:
//
// Finding All the Elementary Circuits of a Directed Graph
// Donald B. Johnson
// SIAM Journal on Computing 1975 4:1, 77-84
// https://doi.org/10.1137/0204007
//
// Field, variable, and method names picked to closely resemble those used in the original paper.
//
public class CircuitFinder<T> {
    private Map<Node<T>, Boolean> blocked = new HashMap<>();
    private Map<Node<T>, Set<Node<T>>> B = new HashMap<>();
    private Deque<Node<T>> stack = new ArrayDeque<>();

    // Finds all elementary circuits in the given directed graph. The circuits are returned in a set.
    // Each circuit in the set is a list containing the ordered sequence of nodes along that circuit.
    // The first and last elements in each circuit are same, thus the edges in the circuit are found
    // looping over the nodes in the circuit and for each node i (0 <= i < circuit.size()), select
    // the edge (circuit[i].getId(), circuit[i+1].getId()).
    public static <T> Set<List<Node<T>>> findCircuits( DirectedGraph<T> g ) {
        return findCircuitsOfMaxSize( g, Integer.MAX_VALUE );
    }

    // Similar to CircuitFInder.findCircuits except this only returns circuits that are at most the
    // given size.
    public static <T> Set<List<Node<T>>> findCircuitsOfMaxSize( DirectedGraph<T> g , int maxSize ) {
        CircuitFinder<T> finder = new CircuitFinder<>();
        return finder.findCircuitsInner( g, maxSize );
    }

    private CircuitFinder() {
    }

    private Set<List<Node<T>>> findCircuitsInner( DirectedGraph<T> g, int maxSize ) {
        int s = 1;
        Set<Node<T>> nodes = g.getNodes();
        Set<List<Node<T>>> circuits = new HashSet<>();

        for( Node<T> v : nodes ) {
            blocked.put( v, false );
            B.put( v, new HashSet<>() );
        }

        while( s < nodes.size() ) {
            LeastScc<T> leastScc = getLeastScc( g, s );

            if( leastScc != null ) {
                Map<Node<T>, Set<Node<T>>> AK = leastScc.getSuccessors();
                s = leastScc.getLeastId();

                for( Node<T> i : AK.keySet() ) {
                    blocked.put( i, false );
                    B.get( i ).clear();
                }

                circuit( g.getNodeFromId( s ), s, maxSize, AK, circuits );
                s++;
            }
            else {
                s = nodes.size();
            }
        }

        return circuits;
    }

    private boolean circuit( Node<T> v, int s, int maxSize, Map<Node<T>, Set<Node<T>>> AK, Set<List<Node<T>>> circuits ) {
        boolean f = false;
        stack.push( v );
        blocked.put( v, true );

        Set<Node<T>> AKv = AK.get( v );
        for( Node<T> w : AKv ) {
            if( w.getId() == s ) {
                // ArrayDeque, when we push/pop, works at the head of the deque. This means that when we iterate
                // through the stack by default, it goes from top to bottom. We want bottom to top to get the
                // actual directed path traversed to form this circuit. This is why re-add the "last" element
                // to our result list -- that represents the initial starting node that we used when forming this
                // circuit.
                List<Node<T>> result = new ArrayList<>();
                stack.descendingIterator().forEachRemaining( result::add );
                result.add( stack.getLast() );
                circuits.add( result );
                f = true;
            }
            else if( stack.size() == maxSize ) {
                // We set f to true because if we break out because of cycle size restrictions, we don't want the
                // nodes we've already seen to remain blocked. It's possible they could be used in an alternate
                // path that yields a cycle.
                f = true;
                break;
            }
            else if( !blocked.get( w ) && circuit( w, s, maxSize, AK, circuits ) ) {
                f = true;
            }
        }

        if( f ) {
            unblock( v );
        }
        else {
            for( Node<T> w : AKv ) {
                Set<Node<T>> Bw = B.get( w );
                if( !Bw.contains( v ) )
                    Bw.add( v );
            }
        }

        stack.pop();
        return f;
    }

    private void unblock( Node<T> u ) {
        blocked.put( u, false );
        Set<Node<T>> Bu = B.get( u );
        for( Node<T> w : Bu ) {
            Bu.remove( w );
            if( blocked.get( w ) )
                unblock( w );
        }
    }

    private LeastScc<T> getLeastScc( DirectedGraph<T> g, int s ) {
        Set<Set<Node<T>>> sccs = SCC.findStronglyConnectedComponentsFromLeastNode( g, g.getNodeFromId( s ) );

        // NOTE: Exclude trivial strong component. Johnson's algorithm basically excludes loops (i.e., edges of the
        // form (v,v). Could probably add support for them by just checking to see if the trivial component as an
        // actual edge back to itself in the given graph, but for now that isn't strictly necessary.
        Set<Node<T>> leastScc = null;
        int leastId = Integer.MAX_VALUE;

        for( Set<Node<T>> scc : sccs ) {
            if( scc.size() > 1 ) {
                for( Node<T> v : scc ) {
                    if( v.getId() < leastId ) {
                        leastScc = scc;
                        leastId = v.getId();
                    }
                }
            }
        }

        // Java 8 lambdas complain if a local variable captured by the lambda isn't final or effectively final. So we
        // have to play games to make our leastScc set available in the filter below.
        final Set<Node<T>> leastSccFinal = leastScc;
        LeastScc<T> result = null;

        if( leastScc != null ) {
            Map<Node<T>, Set<Node<T>>> successors = new HashMap<>();
            for( Node<T> v : leastScc ) {
                successors.put( v, g.getSuccessorsForNode( v )
                        .stream()
                        .filter( n -> n.getId() >= s && leastSccFinal.contains( n ) )
                        .collect( Collectors.toSet() ) );
            }

            result = new LeastScc<>( successors, leastId );
        }

        return result;
    }

    // Used to store the least strongly connected component found for each subgraph induced by
    // Johnson's algorithm. Each instance of this class stores both the map going from each node in
    // the least SCC to its list of successors and the ID of the node in the least SCC that has the
    // lowest valued ID.
    private class LeastScc<T> {
        private Map<Node<T>, Set<Node<T>>> successors;
        private int leastId;

        LeastScc( Map<Node<T>, Set<Node<T>>> successors, int leastId ) {
            this.successors = successors;
            this.leastId = leastId;
        }

        Map<Node<T>, Set<Node<T>>> getSuccessors() {
            return successors;
        }

        int getLeastId() {
            return leastId;
        }
    }
}
