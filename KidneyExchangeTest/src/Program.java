import KidneyExchange.Graph.CircuitFinder;
import KidneyExchange.Graph.DirectedGraph;
import KidneyExchange.Graph.Node;
import KidneyExchange.Graph.SCC;

import java.util.List;
import java.util.Set;

public class Program {
    public static void main( String[] args ) {
        {
            DirectedGraph<Integer> g = TestData.createG1();

            System.out.println( "Printing SCCs of G1." );
            printStronglyConnectedComponents(
                    SCC.findStronglyConnectedComponents( g )
            );
        }


        System.out.println();
        {
            DirectedGraph<Integer> g = TestData.createG1();
            Node<Integer> n3 = g.getNodeFromT( 3 );

            System.out.println( "Printing SCCs of subgraph of G1 induced by nodes {n3, ... n8}." );
            printStronglyConnectedComponents(
                    SCC.findStronglyConnectedComponentsFromLeastNode( g, n3 )
            );
        }

        System.out.println();
        {
            DirectedGraph<Integer> g = TestData.createG2();
            System.out.println( "Printing circuits of G2." );
            printCircuits(
                    CircuitFinder.findCircuits( g )
            );
        }

        System.out.println();
        {
            DirectedGraph<Integer> g = TestData.createG1();
            System.out.println( "Printing circuits of G1." );
            printCircuits(
                    CircuitFinder.findCircuits( g )
            );
        }

        System.out.println();
        {
            DirectedGraph<Integer> g = TestData.createG1();
            System.out.println( "Printing circuits of G1 with max size 2." );
            printCircuits(
                    CircuitFinder.findCircuitsOfMaxSize( g,2 )
            );
        }

        System.out.println();
        SimplexTest.run();
    }

    static void printStronglyConnectedComponents( Set<Set<Node<Integer>>> sccs ) {
        for( Set<Node<Integer>> scc : sccs ) {
            System.out.print( "SCC: " );
            for( Node v : scc ) {
                System.out.print( v.getId() + " " );
            }
            System.out.println();
        }
    }

    static void printCircuits( Set<List<Node<Integer>>> circuits ) {
        for( List<Node<Integer>> circuit : circuits ) {
            System.out.print( "Circuit: " );
            for( Node<Integer> v : circuit ) {
                System.out.print( v.getId() + " " );
            }
            System.out.println();
        }
    }
}
