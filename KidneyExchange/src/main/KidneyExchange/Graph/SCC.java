package KidneyExchange.Graph;

import java.util.*;

// Helper class that implements Tarjan's strongly connected components algorithm. Each node in the
// graph wraps a value of type T.
//
// Based on pseudocode from wikipedia: https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm
//
// Field, variable, and method names picked to closely resemble those used in the linked article.
//
public class SCC<T> {
    private int nextNodeIndex = 0;
    private Map<Node<T>, Integer> nodeIndices = new HashMap<>();
    private Map<Node<T>, Integer> nodeLowLinks = new HashMap<>();
    private Map<Node<T>, Boolean> nodeOnStack = new HashMap<>();
    private Deque<Node<T>> S = new ArrayDeque<>();

    // Finds the strongly connected components in the given graph. Returns a set containing all the
    // SCCs found. Each SCC is a set of nodes that comprises the SCC.
    public static <T> Set<Set<Node<T>>> findStronglyConnectedComponents( DirectedGraph<T> g ) {
        return findStronglyConnectedComponentsFromLeastNode( g, Node.create( Node.MIN_ID, null ) );
    }

    // Similar to SCC.findStronglyConnectedComponents except this only finds SCCs in the subgraph of
    // the given directed graph induced by the nodes whose ID is at least as large as the ID of the
    // given least node.
    public static <T> Set<Set<Node<T>>> findStronglyConnectedComponentsFromLeastNode( DirectedGraph<T> g, Node<T> leastNode ) {
        SCC<T> scc = new SCC<>();
        return scc.findStronglyConnectedComponentsFromLeastNodeInner( g, leastNode );
    }

    private SCC() {
    }

    private Set<Set<Node<T>>> findStronglyConnectedComponentsFromLeastNodeInner( DirectedGraph<T> g, Node<T> leastNode ) {
        Set<Set<Node<T>>> sccs = new HashSet<>();

        for( Node<T> v : g.getNodes() ) {
            if( v.getId() < leastNode.getId() )
                continue;

            if( !nodeIndices.containsKey( v ) ) {
                stronglyConnectFromLeastNode( v, g, leastNode, sccs );
            }
        }

        return sccs;
    }

    private void stronglyConnectFromLeastNode( Node<T> v, DirectedGraph<T> g, Node<T> leastNode, Set<Set<Node<T>>> sccs ) {
        nodeIndices.put( v, nextNodeIndex );
        nodeLowLinks.put( v, nextNodeIndex );
        ++nextNodeIndex;

        S.push( v );
        nodeOnStack.put( v, true );

        for( DirectedEdge<T> edge : g.getOutgoingEdgesForNode( v ) ) {
            Node<T> w = edge.getTarget();

            if( w.getId() < leastNode.getId() )
                continue;

            if( !nodeIndices.containsKey( w ) ) {
                stronglyConnectFromLeastNode( w, g, leastNode, sccs );
                nodeLowLinks.put( v, Math.min( nodeLowLinks.get( v ), nodeLowLinks.get( w ) ) );
            }
            else if( nodeOnStack.get( w ) ) {
                nodeLowLinks.put( v, Math.min( nodeLowLinks.get( v ), nodeIndices.get( w ) ) );
            }
        }

        if( nodeLowLinks.get( v ) == nodeIndices.get( v ) ) {
            Set<Node<T>> scc = new HashSet<>();
            Node<T> w;

            do {
                w = S.pop();
                nodeOnStack.put( w, false );
                scc.add( w );
            } while( !w.equals( v ) );

            sccs.add( scc );
        }
    }
}
