import KidneyExchange.Graph.DirectedGraph;
import KidneyExchange.Graph.Node;
import KidneyExchange.Graph.SCC;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class SCCTest {
    // Asserts all SCCs are found in the test graph.
    @Test
    public void t1() {
        DirectedGraph<Integer> g = TestData.createG1();

        // Setup the expected output.
        Node<Integer> n1 = g.getNodeFromT( 1 );
        Node<Integer> n2 = g.getNodeFromT( 2 );
        Node<Integer> n3 = g.getNodeFromT( 3 );
        Node<Integer> n4 = g.getNodeFromT( 4 );
        Node<Integer> n5 = g.getNodeFromT( 5 );
        Node<Integer> n6 = g.getNodeFromT( 6 );
        Node<Integer> n7 = g.getNodeFromT( 7 );
        Node<Integer> n8 = g.getNodeFromT( 8 );

        Set<Node<Integer>> scc1 = new HashSet<>();
        scc1.add( n1 );
        scc1.add( n2 );
        scc1.add( n3 );

        Set<Node<Integer>> scc2 = new HashSet<>();
        scc2.add( n4 );
        scc2.add( n5 );

        Set<Node<Integer>> scc3 = new HashSet<>();
        scc3.add( n6 );
        scc3.add( n7 );

        Set<Node<Integer>> scc4 = new HashSet<>();
        scc4.add( n8 );

        // Find all SCCs in the test graph.
        Set<Set<Node<Integer>>> sccs = SCC.findStronglyConnectedComponents( g );

        Assertions.assertEquals( sccs.size(), 4 );
        Assertions.assertTrue( sccs.contains( scc1 ) );
        Assertions.assertTrue( sccs.contains( scc2 ) );
        Assertions.assertTrue( sccs.contains( scc3 ) );
        Assertions.assertTrue( sccs.contains( scc4 ) );
    }

    // Asserts all SCCs are found in the subgraph of the test graph induced by nodes greater than or
    // equal to a given least node.
    @Test
    public void t2() {
        DirectedGraph<Integer> g = TestData.createG1();

        // Setup the expected output.
        Node<Integer> n1 = g.getNodeFromT( 1 );
        Node<Integer> n2 = g.getNodeFromT( 2 );
        Node<Integer> n3 = g.getNodeFromT( 3 );
        Node<Integer> n4 = g.getNodeFromT( 4 );
        Node<Integer> n5 = g.getNodeFromT( 5 );
        Node<Integer> n6 = g.getNodeFromT( 6 );
        Node<Integer> n7 = g.getNodeFromT( 7 );
        Node<Integer> n8 = g.getNodeFromT( 8 );

        Set<Node<Integer>> scc1 = new HashSet<>();
        scc1.add( n3 );

        Set<Node<Integer>> scc2 = new HashSet<>();
        scc2.add( n4 );
        scc2.add( n5 );

        Set<Node<Integer>> scc3 = new HashSet<>();
        scc3.add( n6 );
        scc3.add( n7 );

        Set<Node<Integer>> scc4 = new HashSet<>();
        scc4.add( n8 );

        // Find all SCCs in the induced subgraph.
        Set<Set<Node<Integer>>> sccs = SCC.findStronglyConnectedComponentsFromLeastNode( g, n3 );
        Assertions.assertEquals( sccs.size(), 4 );
        Assertions.assertTrue( sccs.contains( scc1 ) );
        Assertions.assertTrue( sccs.contains( scc2 ) );
        Assertions.assertTrue( sccs.contains( scc3 ) );
        Assertions.assertTrue( sccs.contains( scc4 ) );
    }
}
