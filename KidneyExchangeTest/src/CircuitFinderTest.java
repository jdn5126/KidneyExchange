import KidneyExchange.Graph.CircuitFinder;
import KidneyExchange.Graph.DirectedGraph;
import KidneyExchange.Graph.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CircuitFinderTest {
    // Finds all circuits in a simple test graph.
    @Test
    public void t1() {
        DirectedGraph<Integer> g = TestData.createG2();

        // Setup the expected output.
        Node<Integer> n1 = g.getNodeFromT( 1 );
        Node<Integer> n2 = g.getNodeFromT( 2 );
        Node<Integer> n3 = g.getNodeFromT( 3 );

        List<Node<Integer>> circuit1 = new ArrayList<>();
        circuit1.add( n1 );
        circuit1.add( n2 );
        circuit1.add( n1 );

        // Find all circuits in the test graph.
        Set<List<Node<Integer>>> circuits = CircuitFinder.findCircuits( g );
        Assertions.assertEquals( circuits.size(), 1 );
        Assertions.assertTrue( circuits.contains( circuit1 ) );
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

        List<Node<Integer>> circuit1 = new ArrayList<>();
        circuit1.add( n1 );
        circuit1.add( n2 );
        circuit1.add( n3 );
        circuit1.add( n1 );

        List<Node<Integer>> circuit2 = new ArrayList<>();
        circuit2.add( n4 );
        circuit2.add( n5 );
        circuit2.add( n4 );

        List<Node<Integer>> circuit3 = new ArrayList<>();
        circuit3.add( n6 );
        circuit3.add( n7 );
        circuit3.add( n6 );

        // Find all circuits in the test graph.
        Set<List<Node<Integer>>> circuits = CircuitFinder.findCircuits( g );
        Assertions.assertEquals( circuits.size(), 3 );
        Assertions.assertTrue( circuits.contains( circuit1 ) );
        Assertions.assertTrue( circuits.contains( circuit2 ) );
        Assertions.assertTrue( circuits.contains( circuit3 ) );
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

        List<Node<Integer>> circuit1 = new ArrayList<>();
        circuit1.add( n4 );
        circuit1.add( n5 );
        circuit1.add( n4 );

        List<Node<Integer>> circuit2 = new ArrayList<>();
        circuit2.add( n6 );
        circuit2.add( n7 );
        circuit2.add( n6 );

        // Find all circuits in the test graph.
        Set<List<Node<Integer>>> circuits = CircuitFinder.findCircuitsOfMaxSize( g, 2 );
        Assertions.assertEquals( circuits.size(), 2 );
        Assertions.assertTrue( circuits.contains( circuit1 ) );
        Assertions.assertTrue( circuits.contains( circuit2 ) );
    }
}
