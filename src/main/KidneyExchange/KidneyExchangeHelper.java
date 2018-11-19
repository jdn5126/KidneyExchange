package KidneyExchange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

// Helper class for implementing functions needed for KidneyExchange
public class KidneyExchangeHelper {
    // Helper variables that are initialized when class is first loaded
    static int hospitalId = 0;
    static int participantId = 0;
    static Random rand = new Random();
    private static KidneyType[] kidneyTypes = KidneyType.values();

    // Helper function for creating Hospital
    public static Hospital createHospital(int numPairs, int maxSurgeries) {
        Hospital hospital = new Hospital(++hospitalId, maxSurgeries);

        // Randomly assign ExchangePairs to Hospital
        for(int i=0; i < numPairs; i++) {
            KidneyType donorType = kidneyTypes[rand.nextInt(kidneyTypes.length)];
            KidneyType receiverType = kidneyTypes[rand.nextInt(kidneyTypes.length)];
            // Avoid creating ExchangePair with matching kidney types
            while(donorType == receiverType) {
                receiverType = kidneyTypes[rand.nextInt(kidneyTypes.length)];
            }
            ExchangePair pair = new ExchangePair(donorType, receiverType, ++participantId);
            hospital.addPair(pair);
        }
        return hospital;
    }

    // Helper function for creating DirectedGraph from Hospital
    public static DirectedGraph createDirectedGraph(Hospital hospital) {
        DirectedGraph graph = new DirectedGraph(hospital.getSize());
        // For each ExchangePair, build adjacency list
        for(ExchangePair pair: hospital.getPairs()) {
            DirectedGraph.Node node = graph.createNode(pair);
            // Add compatible pairs to neighbor list, including own pair
            // if donor and recipient are already matched
            for(ExchangePair neighbor: hospital.getPairs()) {
                if(pair.canReceive(neighbor)) {
                    node.addNeighbor(neighbor);
                }
            }
        }
        return graph;
    }

    // Helper function for performing greedy matching algorithm
    public static HashMap<ExchangePair, ExchangePair> greedyMatches(Hospital hospital, DirectedGraph graph) {
        // Greedy matching algorithm is implemented as a variant of the Top Trading Cycle algorithm.
        //     1. Select unmatched node from graph.
        //     2. Use DFS from node to try to find first cycle of length less than
        //        remainingSlots.
        //            2a. If cycle exists, remove all nodes in cycle from graph.
        //     3. If there are no remainingSlots or all nodes have been covered, return
        //        existing matches. Otherwise, continue at 1.

        HashMap<ExchangePair, ExchangePair> matches = new HashMap<>();
        int remainingSlots = hospital.getMaxSurgeries();

        // Add nodes to array to allow iteration while mutating graph. Iterating guarantees
        // forward progress in the presence of bad random selection or all nodes being part
        // of a cycle that is larger than the remaining  number of surgery slots.
        DirectedGraph.Node[] iterable = new DirectedGraph.Node[graph.getSize()];
        for(int i=0; i < graph.getSize(); i++) {
            iterable[i] = graph.getNodes().get(i);
        }

        for(DirectedGraph.Node node: iterable) {
            // Validate that node still exists in graph
            if(!graph.hasNode(node)) {
                continue;
            }
            // Find first cycle doing DFS starting from node
            ArrayList<DirectedGraph.Node> cycle = findCycle(graph, node, remainingSlots);
            int cycleSize = cycle.size();
            if(cycleSize == 0) {
                // Graph no longer contains any cycles
                break;
            } else if(cycleSize > remainingSlots) {
                // Matching is too large, so move to next node
                continue;
            } else {
                // Mark matches and remove cycle from graph
                remainingSlots -= cycleSize;
                for(int i=0; i < cycleSize; i++) {
                    ExchangePair pair = cycle.get(i).getPair();
                    ExchangePair match = cycle.get((i+1) % cycleSize).getPair();
                    matches.put(pair, match);
                    graph.removeNode(graph.getNode(pair));
                }
            }
        }
        return matches;
    }

    // Helper function for finding cycle starting with root
    public static ArrayList<DirectedGraph.Node> findCycle(DirectedGraph graph, DirectedGraph.Node root, int maxLength) {
        // Algorithm to find cycle operates as follows:
        //     1. Add current node to visiting set
        //     2. For each neighbor of node:
        //            2a. If neighbor is in finished set, skip
        //            2b. Else if neighbor is in visiting set, add to travel map and break
        //            2c. Else continue at 1 with neighbor as current node
        ArrayList<DirectedGraph.Node> cycle = new ArrayList<>();

        // After calling cycleRecurse, currentNode will be set to last node in cycle, or null if there is no cycle.
        DirectedGraph.Node currentNode = root;
        // If cycle exists, it will be stored in travel map.
        HashMap<DirectedGraph.Node, DirectedGraph.Node> travelMap = new HashMap<>();
        DirectedGraph.Node cycleRoot = cycleRecurse(graph, currentNode, null,
                                                    new ArrayList<>(), new ArrayList<>(), travelMap);
        if(cycleRoot != null) {
            // Build cycle from travelMap
            DirectedGraph.Node prev = travelMap.get(cycleRoot);
            while(prev != cycleRoot) {
                // Add each node to front of list
                cycle.add(0, prev);
                prev = travelMap.get(prev);
            }
            cycle.add(0, root);
        }
        return cycle;
    }

    // Helper function for doing recursive DFS
    public static DirectedGraph.Node cycleRecurse(DirectedGraph graph,
                                                  DirectedGraph.Node currentNode,
                                                  DirectedGraph.Node previousNode,
                                                  ArrayList<DirectedGraph.Node> visiting,
                                                  ArrayList<DirectedGraph.Node> finished,
                                                  HashMap<DirectedGraph.Node, DirectedGraph.Node> travelMap) {
        // Add current node to visiting and to travel map
        visiting.add(currentNode);
        travelMap.put(currentNode, previousNode);

        for(ExchangePair neighborPair: currentNode.getNeighbors()) {
            DirectedGraph.Node neighbor = graph.getNode(neighborPair);
            // If neighbor is in visiting, cycle has been formed
            if(visiting.contains(neighbor)) {
                // Return node that starts and ends cycle
                travelMap.put(neighbor, currentNode);
                return neighbor;
            } else if(finished.contains(neighbor)) {
                // Neighbor is not part of cycle, so continue to next neighbor.
                continue;
            } else {
                DirectedGraph.Node cycleRoot = cycleRecurse(graph, neighbor, currentNode,
                                                            visiting, finished, travelMap);
                if(cycleRoot != null) {
                    return cycleRoot;
                }
            }
        }
        // If no cycle has been found, move node to finished and return false.
        visiting.remove(currentNode);
        finished.add(currentNode);
        return null;
    }
}
