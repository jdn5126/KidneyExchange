import KidneyExchange.Graph.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;

public class CycleFinderTest {
    // Finds all circuits in a simple test graph.
    @Test
    public void t1() {
        DirectedGraph<Integer> g = TestData.createG2();

        // Setup the expected output.
        Node<Integer> n1 = g.getNodeFromT( 1 );
        Node<Integer> n2 = g.getNodeFromT( 2 );
        Node<Integer> n3 = g.getNodeFromT( 3 );

        Cycle<Integer> cycle1 = new Cycle<>( n1,
                Arrays.asList(
                        new DirectedEdge<>( n2 ),
                        new DirectedEdge<>( n1, 2 )
                )
        );

        // Find all circuits in the test graph.
        Set<Cycle<Integer>> cycles = CycleFinder.findCycles( g );
        Assertions.assertEquals( 1, cycles.size() );
        Assertions.assertTrue( cycles.contains( cycle1 ) );

        Cycle<Integer> foundCycle = cycles.iterator().next();
        Assertions.assertEquals( 3.0, foundCycle.getCycleWeight() );
        Assertions.assertTrue( foundCycle.hasNode( n1 ) );
        Assertions.assertTrue( foundCycle.hasNode( n2 ) );
        Assertions.assertFalse( foundCycle.hasNode( n3 ) );
    }

    // Finds all circuits in a more complicated test graph.
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

        Cycle<Integer> cycle1 = new Cycle<>( n1,
                Arrays.asList(
                        new DirectedEdge<>( n2 ),
                        new DirectedEdge<>( n3 ),
                        new DirectedEdge<>( n1 )
                )
        );

        Cycle<Integer> cycle2 = new Cycle<>( n4,
                Arrays.asList(
                        new DirectedEdge<>( n5 ),
                        new DirectedEdge<>( n4 )
                )
        );

        Cycle<Integer> cycle3 = new Cycle<>( n6,
                Arrays.asList(
                        new DirectedEdge<>( n7 ),
                        new DirectedEdge<>( n6 )
                )
        );

        // Find all circuits in the test graph.
        Set<Cycle<Integer>> cycles = CycleFinder.findCycles( g );
        Assertions.assertEquals( 3, cycles.size() );
        Assertions.assertTrue( cycles.contains( cycle1 ) );
        Assertions.assertTrue( cycles.contains( cycle2 ) );
        Assertions.assertTrue( cycles.contains( cycle3 ) );
    }

    // Finds all circuits in a more complicated test graph with a max size constraint.
    @Test
    public void t3() {
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

        Cycle<Integer> cycle1 = new Cycle<>( n4,
                Arrays.asList(
                        new DirectedEdge<>( n5 ),
                        new DirectedEdge<>( n4 )
                )
        );

        Cycle<Integer> cycle2 = new Cycle<>( n6,
                Arrays.asList(
                        new DirectedEdge<>( n7 ),
                        new DirectedEdge<>( n6 )
                )
        );

        // Find all circuits in the test graph.
        Set<Cycle<Integer>> cycles = CycleFinder.findCyclesOfMaxSize( g, 2 );
        Assertions.assertEquals( 2, cycles.size() );
        Assertions.assertTrue( cycles.contains( cycle1 ) );
        Assertions.assertTrue( cycles.contains( cycle2 ) );
    }
}
