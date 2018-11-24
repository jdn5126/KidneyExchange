import KidneyExchange.Graph.DirectedGraph;

public class TestData {
    // G1 is the graph depicted at the start of the Wikipedia article on Tarjan's strongly connected
    // component algorithm: https://en.wikipedia.org/wiki/Tarjan%27s_strongly_connected_components_algorithm
    public static DirectedGraph<Integer> createG1() {
        DirectedGraph<Integer> g = new DirectedGraph<>();

        g.addEdge( 1, 2 );
        g.addEdge( 2, 3 );
        g.addEdge( 3, 1 );
        g.addEdge( 4, 2 );
        g.addEdge( 4, 3 );
        g.addEdge( 4, 5 );
        g.addEdge( 5, 4 );
        g.addEdge( 5, 6 );
        g.addEdge( 6, 3 );
        g.addEdge( 6, 7 );
        g.addEdge( 7, 6 );
        g.addEdge( 8, 7 );
        g.addEdge( 8, 5 );
        g.addEdge( 8, 8 );

        return g;
    }

    // G2 is a graph with the following structure:
    //
    //     n1 <----> n2 ----> n3
    //
    public static DirectedGraph<Integer> createG2() {
        DirectedGraph<Integer> g = new DirectedGraph<>();

        g.addEdge( 1, 2 );
        g.addEdgeWithWeight( 2, 1, 2.0 );
        g.addEdge( 2, 3 );

        return g;
    }
}
