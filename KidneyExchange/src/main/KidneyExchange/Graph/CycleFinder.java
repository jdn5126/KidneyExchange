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
// Adapted to limit cycles returned to a max size and to better handle directed weighted graphs by
// returning weights of edges along cycle in addition to nodes along the cycle's path.
//
public class CycleFinder<T> {
    private Map<Node<T>, Boolean> blocked = new HashMap<>();
    private Map<Node<T>, Set<Node<T>>> B = new HashMap<>();
    private Deque<DirectedEdge<T>> pathStack = new ArrayDeque<>();

    // Finds all cycles in the given directed graph, where a cycle here is meant to be an elementary
    // circuit. The cycles are returned in a set. Each cycle in the set specifies the start node
    // for the cycle and an ordered list of directed edges that specifies the subsequent nodes in
    // the cycle's path along with the weights of each of those edges.
    public static <T> Set<Cycle<T>> findCycles( DirectedGraph<T> g ) {
        return findCyclesOfMaxSize( g, Integer.MAX_VALUE );
    }

    // Similar to CycleFinder.findCycles except this only returns cycles that are at most the
    // given size.
    public static <T> Set<Cycle<T>> findCyclesOfMaxSize( DirectedGraph<T> g , int maxSize ) {
        CycleFinder<T> finder = new CycleFinder<>();
        return finder.findCyclesInner( g, maxSize );
    }

    private CycleFinder() {
    }

    private Set<Cycle<T>> findCyclesInner( DirectedGraph<T> g, int maxSize ) {
        int s = 1;
        Set<Node<T>> nodes = g.getNodes();
        Set<Cycle<T>> cycles = new HashSet<>();

        for( Node<T> v : nodes ) {
            blocked.put( v, false );
            B.put( v, new HashSet<>() );
        }

        while( s < nodes.size() ) {
            LeastScc<T> leastScc = getLeastScc( g, s );

            if( leastScc != null ) {
                Map<Node<T>, Set<DirectedEdge<T>>> AK = leastScc.getOutgoingEdges();
                s = leastScc.getLeastId();

                for( Node<T> i : AK.keySet() ) {
                    blocked.put( i, false );
                    B.get( i ).clear();
                }

                circuit( g.getNodeFromId( s ), s, maxSize, AK, cycles );
                s++;
            }
            else {
                s = nodes.size();
            }
        }

        return cycles;
    }

    private boolean circuit( Node<T> v, int s, int maxSize, Map<Node<T>, Set<DirectedEdge<T>>> AK, Set<Cycle<T>> cycles ) {
        boolean f = false;
        blocked.put( v, true );

        Set<DirectedEdge<T>> AKv = AK.get( v );
        for( DirectedEdge<T> edge : AKv ) {
            pathStack.push( edge );
            Node<T> w = edge.getTarget();

            if( w.getId() == s ) {
                // ArrayDeque, when we push/pop, works at the head of the deque. This means that when we iterate
                // through the stack by default, it goes from top to bottom. We want bottom to top to get the
                // actual directed path traversed to form this cycle.
                List<DirectedEdge<T>> path = new ArrayList<>();
                pathStack.descendingIterator().forEachRemaining( path::add );
                Cycle result = new Cycle<>( w, path );
                cycles.add( result );
                f = true;
            }
            else if( pathStack.size() == maxSize ) {
                // We set f to true because if we break out because of cycle size restrictions, we don't want the
                // nodes we've already seen to remain blocked. It's possible they could be used in an alternate
                // path that yields a cycle.
                pathStack.pop();
                f = true;
                break;
            }
            else if( !blocked.get( w ) && circuit( w, s, maxSize, AK, cycles ) ) {
                f = true;
            }

            pathStack.pop();
        }

        if( f ) {
            unblock( v );
        }
        else {
            for( DirectedEdge<T> edge : AKv ) {
                Set<Node<T>> Bw = B.get( edge.getTarget() );
                if( !Bw.contains( v ) )
                    Bw.add( v );
            }
        }

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
            Map<Node<T>, Set<DirectedEdge<T>>> successors = new HashMap<>();
            for( Node<T> v : leastScc ) {
                successors.put( v, g.getOutgoingEdgesForNode( v )
                        .stream()
                        .filter( e -> e.getTarget().getId() >= s && leastSccFinal.contains( e.getTarget() ) )
                        .collect( Collectors.toSet() ) );
            }

            result = new LeastScc<>( successors, leastId );
        }

        return result;
    }

    // Used to store the least strongly connected component found for each subgraph induced by
    // Johnson's algorithm. Each instance of this class stores both the map going from each node in
    // the least SCC to its list of outgoingEdges and the ID of the node in the least SCC that has the
    // lowest valued ID.
    private class LeastScc<T> {
        private Map<Node<T>, Set<DirectedEdge<T>>> outgoingEdges;
        private int leastId;

        LeastScc( Map<Node<T>, Set<DirectedEdge<T>>> outgoingEdges, int leastId ) {
            this.outgoingEdges = outgoingEdges;
            this.leastId = leastId;
        }

        Map<Node<T>, Set<DirectedEdge<T>>> getOutgoingEdges() {
            return outgoingEdges;
        }

        int getLeastId() {
            return leastId;
        }
    }
}
