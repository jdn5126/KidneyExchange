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

    public static void setRandomSeed( long seed ) {
        rand.setSeed( seed );
    }

    // Helper function for creating random ExchangePair
    public static ExchangePair randomExchangePair(int hospitalId) {
        KidneyType donorType = kidneyTypes[rand.nextInt(kidneyTypes.length)];
        KidneyType receiverType = kidneyTypes[rand.nextInt(kidneyTypes.length)];
        // Avoid creating ExchangePair with matching kidney types
        while(donorType == receiverType) {
            receiverType = kidneyTypes[rand.nextInt(kidneyTypes.length)];
        }
        return new ExchangePair(donorType, receiverType, ++participantId, hospitalId);
    }

    // Helper function for adding random ExchangePair to hospital
    public static void addRandomExchangePair(Hospital hospital) {
        ExchangePair pair = KidneyExchangeHelper.randomExchangePair(hospital.getHospitalId());
        ConsoleLogger.println("INCREMENTAL: adding " + pair.toString() + " to hospital " + hospital.getHospitalId());
        hospital.addPair(pair);
    }

    // Helper function for removing random ExchangePair from hospital
    public static ExchangePair delRandomExchangePair(Hospital hospital) {
        int index = rand.nextInt(hospital.getSize());
        ExchangePair pair = hospital.getPairs().get(index);
        ConsoleLogger.println("INCREMENTAL: removing " + pair.toString() + " from hospital " + pair.getCurrentHospital());
        return pair;
    }

    // Helper function for creating Hospital
    public static Hospital createHospital(int numPairs, int maxSurgeries) {
        Hospital hospital = new Hospital(++hospitalId, maxSurgeries);

        // Randomly assign ExchangePairs to Hospital
        for(int i=0; i < numPairs; i++) {
            hospital.addPair(randomExchangePair(hospitalId));
        }
        return hospital;
    }

    // Helper function for creating DirectedGraph from Hospital
    public static DirectedGraph createDirectedGraph(Hospital hospital) {
        DirectedGraph graph = new DirectedGraph(hospital.getSize());
        // For each ExchangePair, build adjacency list
        for(ExchangePair pair: hospital.getPairs()) {
            DirectedGraph.Node node = graph.createNode(pair);
            // Add compatible pairs to neighbor list
            for(ExchangePair neighbor: hospital.getPairs()) {
                if(pair.canReceive(neighbor)) {
                    node.addNeighbor(neighbor);
                }
            }
        }
        return graph;
    }

    // Helper function for performing greedy matching algorithm
    public static ArrayList<HashMap<ExchangePair, ExchangePair>> greedyMatches(Hospital hospital) {
        // Create directed graph from ExchangePairs
        DirectedGraph graph = KidneyExchangeHelper.createDirectedGraph(hospital);
        ConsoleLogger.println("Adjacency List for Hospital " + hospital.getHospitalId() + ":");
        ConsoleLogger.print(graph);

        // Greedy matching algorithm is implemented as a variant of the Top Trading Cycle algorithm.
        //     1. Select unmatched node from graph.
        //     2. Use DFS from node to try to find first cycle of length less than
        //        remainingSlots.
        //            2a. If cycle exists, remove all nodes in cycle from graph.
        //     3. If there are no remainingSlots or all nodes have been covered, return
        //        existing matches. Otherwise, continue at 1.

        // Return null if hospital is empty, as non null hospital guarantees graph has at least one node.
        if(hospital.getSize() == 0) {
            return null;
        }
        ArrayList<HashMap<ExchangePair, ExchangePair>> matches = new ArrayList<>();
        int remainingSlots = hospital.getMaxSurgeries();

        // Add nodes to array to allow iteration while mutating graph. Iterating guarantees
        // forward progress in the presence of bad random selection or all nodes being part
        // of a cycle that is larger than the remaining  number of surgery slots.
        DirectedGraph.Node[] iterable = new DirectedGraph.Node[graph.getSize()];
        int x = 0;
        for(DirectedGraph.Node node: graph.getNodes()) {
            iterable[x] = node;
            x += 1;
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
                // Either no cycle exists along the traversal from this node or the cycle
                // is too large. Either way, move to the next node and run DFS.
                continue;
            } else {
                // Record matches and remove cycle from graph
                remainingSlots -= cycleSize;
                HashMap<ExchangePair, ExchangePair> pairCycle = new HashMap<>();
                for(int i=0; i < cycleSize; i++) {
                    DirectedGraph.Node startNode = cycle.get(i);
                    ExchangePair match = cycle.get((i+1) % cycleSize).getPair();
                    pairCycle.put(startNode.getPair(), match);
                    graph.removeNode(startNode);
                }
                matches.add(pairCycle);
            }
        }
        return matches;
    }

    // Helper function for finding cycle within graph starting at root
    public static ArrayList<DirectedGraph.Node> findCycle(DirectedGraph graph, DirectedGraph.Node root,
                                                          int remainingSlots) {
        ArrayList<DirectedGraph.Node> cycle = new ArrayList<>();

        // cycleRecurse will store the progression of the DFS-walk in travelMap
        HashMap<DirectedGraph.Node, TravelMapVal> travelMap = new HashMap<>();
        // If a cycle was found, cycleRoot will be set to the node starting and ending the cycle.
        DirectedGraph.Node cycleRoot = cycleRecurse(graph, root, null, new ArrayList<>(),
                                                    new ArrayList<>(), travelMap, 1, remainingSlots);
        if(cycleRoot != null) {
            // Build cycle from travelMap
            DirectedGraph.Node prev = travelMap.get(cycleRoot).getNode();
            while(prev != cycleRoot) {
                // Add each node to front of list
                cycle.add(0, prev);
                prev = travelMap.get(prev).getNode();
            }
            cycle.add(0, cycleRoot);
        }
        return cycle;
    }

    // Helper function for doing recursive DFS-walk
    public static DirectedGraph.Node cycleRecurse(DirectedGraph graph,
                                                  DirectedGraph.Node currentNode,
                                                  DirectedGraph.Node previousNode,
                                                  ArrayList<DirectedGraph.Node> visiting,
                                                  ArrayList<DirectedGraph.Node> finished,
                                                  HashMap<DirectedGraph.Node, TravelMapVal> travelMap,
                                                  int step,
                                                  int remainingSlots) {
        // Algorithm to find cycle operates as follows:
        //     1. Add current node to visiting set and mark how we got to this node in travel map
        //     2. For each neighbor of node:
        //            2a. If neighbor is in visiting set, then neighbor is the root of the cycle.
        //                If cycle is not longer than remainingSlots, map cycleRoot as introduced by
        //                currentNode so that iterating backward in travelMap until cycleRoot yields cycle.
        //            2a. Else if neighbor is in finished set, skip neighbor.
        //            2c. Else continue at 1 with current node updated as neighbor
        // Add current node to visiting and to travel map
        visiting.add(currentNode);
        travelMap.put(currentNode, new TravelMapVal(previousNode, step));

        for(ExchangePair neighborPair : currentNode.getNeighbors()) {
            DirectedGraph.Node neighbor = graph.getNode(neighborPair);
            // If neighbor is in visiting, cycle has been formed
            if (visiting.contains(neighbor)) {
                // Cycle length cannot exceed remainingSlots, otherwise skip neighbor
                int startStep = travelMap.get(neighbor).getStep();
                if (step - startStep >= remainingSlots) {
                    continue;
                } else {
                    // Return node that starts and ends cycle
                    travelMap.put(neighbor, new TravelMapVal(currentNode, step + 1));
                    return neighbor;
                }
            } else if (finished.contains(neighbor)) {
                // Neighbor is not part of cycle, so continue to next neighbor.
                continue;
            } else {
                DirectedGraph.Node cycleRoot = cycleRecurse(graph, neighbor, currentNode,
                        visiting, finished, travelMap, step + 1, remainingSlots);
                if (cycleRoot != null) {
                    return cycleRoot;
                }
            }
        }
        // If no cycle has been found, move node to finished and return false.
        visiting.remove(currentNode);
        finished.add(currentNode);
        return null;
    }

    // Helper function for simulating an ExchangePair dropping out after already matched
    public static ExchangePair delRandomMatchedPair(ArrayList<HashMap<ExchangePair, ExchangePair>> matches) {
        // Get random cycle from list of cycles
        int randCycle = rand.nextInt(matches.size());
        HashMap<ExchangePair, ExchangePair> cycle = matches.get(randCycle);

        // Choose first pair in cycle as pair to be removed
        ExchangePair pair = cycle.keySet().iterator().next();
        // Remove cycle from list and return random pair to be removed from exchange
        matches.remove(cycle);
        return pair;
    }
}
