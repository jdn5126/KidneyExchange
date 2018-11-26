package KidneyExchange;

// Helper class for storing node found along DFS walk
public class TravelMapVal {
    private DirectedGraph.Node node;
    private int step;

    public TravelMapVal(DirectedGraph.Node node, int step) {
        this.node = node;
        this.step = step;
    }

    public DirectedGraph.Node getNode() {
        return node;
    }

    public int getStep() {
        return step;
    }
}