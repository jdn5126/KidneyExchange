package KidneyExchange;

import java.util.ArrayList;
import java.util.concurrent.RejectedExecutionHandler;

// Class representing directed graph amongst ExchangePairs within a single hospital
public class DirectedGraph {
    // Each node is an adjacency list
    public class Node {
        private final ExchangePair pair;
        private ArrayList<ExchangePair> neighbors;

        public Node(ExchangePair pair) {
            this.pair = pair;
            this.neighbors = new ArrayList<ExchangePair>();
        }

        public ExchangePair getPair() {
            return pair;
        }

        public ArrayList<ExchangePair> getNeighbors() {
            return neighbors;
        }

        public void addNeighbor(ExchangePair neighbor) {
            this.neighbors.add(neighbor);
        }

        public void removeNeighbor(ExchangePair neighbor) {
            this.neighbors.remove(neighbor);
        }

        public boolean hasNeighbor(ExchangePair neighbor) {
            return this.neighbors.contains(neighbor);
        }
    }

    private ArrayList<Node> nodes;

    public DirectedGraph(int length) {
        this.nodes = new ArrayList<>(length);
    }

    // Return ArrayList of nodes
    public ArrayList<Node> getNodes() {
        return nodes;
    }

    // Return number of nodes in graph
    public int getSize() {
        return nodes.size();
    }

    // Add ExchangePair as new node in graph
    public Node createNode(ExchangePair pair) {
        Node node = new Node(pair);
        nodes.add(node);
        return node;
    }

    // Return node corresponding to ExchangePair
    public Node getNode(ExchangePair pair) {
        for(Node node: nodes) {
            if(node.getPair().equals(pair)) {
                return node;
            }
        }
        return null;
    }

    public boolean hasNode(Node node) {
        return nodes.contains(node);
    }

    public void removeNode(Node node) {
        // Remove node from each adjacency list before removing from array
        for(Node n: nodes) {
            n.removeNeighbor(node.getPair());
        }
        nodes.remove(node);
    }

    // String representation for adjacency list
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(Node node: nodes) {
            s.append(node.getPair().toString() + ": [" );
            for(ExchangePair neighbor: node.getNeighbors()) {
                s.append(neighbor.toString() + ", ");
            }
            s.append("]\n");
        }
        return s.toString();
    }
}
