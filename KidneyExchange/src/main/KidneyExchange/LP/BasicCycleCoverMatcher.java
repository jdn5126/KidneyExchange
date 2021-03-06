package KidneyExchange.LP;

import KidneyExchange.ConsoleLogger;
import KidneyExchange.ExchangePair;
import KidneyExchange.Graph.*;
import KidneyExchange.Hospital;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

import java.util.*;
import java.util.stream.IntStream;

public class BasicCycleCoverMatcher {
    public static ArrayList<HashMap<ExchangePair, ExchangePair>> findMatches( Hospital hospital, IWeightStrategy weightStrategy ) {
        // Build the directed graph of pairs within the hospital.
        DirectedGraph<ExchangePair> graph = new DirectedGraph<>();
        List<ExchangePair> pairs = hospital.getPairs();

        for( ExchangePair fromPair : pairs ) {
            for( ExchangePair toPair : pairs ) {
                if( fromPair.canReceive( toPair ) ) {
                    graph.addEdgeWithWeight( fromPair, toPair, weightStrategy.getWeight( hospital, fromPair, toPair ) );
                }
            }
        }

        // Find all short cycles in our pair graph up to a certain size. Use a hospital's max surgeries as a proxy for
        // max cycle length.
        List<Cycle<ExchangePair>> cycles = CycleFinder.findCyclesOfMaxSize( graph, hospital.getMaxSurgeries() );

        ConsoleLogger.println();
        ConsoleLogger.println( "Directed graph used in LP" );
        ConsoleLogger.println( graph );
        for( Cycle<ExchangePair> cycle : cycles ) {
            ConsoleLogger.println( cycle );
        }

        // Check if there are even cycles that we can optimize over.
        ArrayList<HashMap<ExchangePair, ExchangePair>> matches = new ArrayList<>();

        if( cycles.size() == 0 )
            return matches;

        // Build a new optimization model that looks to get the max weighted cycle cover in our hospital graph.
        Model model = new Model( "Max-weight Cycle Cover" );

        // Decision variables: one binary variable for each cycle in our list of possible cycles. The variable will be
        // set equal to 1 (true) if the cycle is in the optimal cover, otherwise it will be set to 0 (false).
        BoolVar[] cycleVars = model.boolVarArray( "cycle", cycles.size() );

        // Each coefficient is equal to the weight of the corresponding cycle.
        int[] weights = cycles.stream().mapToInt( c -> c.getCycleWeight() ).toArray();
        int[] sizes = cycles.stream().mapToInt( c -> c.getCycleLength() ).toArray();

        // When doing an optimization problem, Choco requires that the objective function is represented by a single
        // variable. For objective functions that incorporate multiple objective variables, that needs to be modeled as
        // an equality constraint. We also need to ensure we don't violate our max surgeries constraint, which means the
        // the max optimal weight is bounded above by the max surgeries the hospital can perform.
        IntVar maxWeightVar = model.intVar( "maxWeight", 0, IntVar.MAX_INT_BOUND );
        model.scalar( cycleVars, weights, "=", maxWeightVar ).post();
        model.scalar( cycleVars, sizes, "<=", hospital.getMaxSurgeries() ).post();

        // Setup per-node constraints. Each constraint enforces that a given node appears in only one cycle in the
        // optimal cycle cover.
        for( Node<ExchangePair> node : graph.getNodes() ) {
            BoolVar[] nodeInCycleVars = IntStream.range( 0, cycles.size() )
                    .filter( iCycle -> cycles.get( iCycle ).hasNode( node ) )
                    .mapToObj( iCycle -> cycleVars[iCycle] )
                    .toArray( BoolVar[]::new );

            if( nodeInCycleVars.length > 0 ) {
                ConsoleLogger.println( "Vertex constraint for node " + node.getId() + " with " + nodeInCycleVars.length + " variables" );
                model.sum( nodeInCycleVars, "<=", 1 ).post();
            }
        }

        // Run the solver to compute the optimal cycle cover.
        final boolean maximize = true;
        Solver solver = model.getSolver();
        Solution solution = solver.findOptimalSolution( maxWeightVar, maximize );

        if( !ConsoleLogger.getQuiet() )
            solver.printStatistics();
        ConsoleLogger.println();

        // This shouldn't really happen, but in case it does, fail hard and fail fast.
        if( solution == null )
            throw new RuntimeException( "Failed to find optimal kidney exchange solution via constraint solver." );

        // Build out the list of matches in the format expected by the caller.
        for( int iCycle = 0; iCycle < cycleVars.length; ++iCycle ) {
            if( solution.getIntVal( cycleVars[iCycle] ) == 1 ) {
                ConsoleLogger.println( "Cycle " + (iCycle + 1) + " selected. " + cycles.get( iCycle ) );
                HashMap<ExchangePair, ExchangePair> cycleMatches = new HashMap<>();
                Cycle<ExchangePair> cycle = cycles.get( iCycle );
                ExchangePair fromPair = cycle.getStartNode().unwrap();

                for( DirectedEdge<ExchangePair> edge : cycle.getPath() ) {
                    ExchangePair toPair = edge.getTarget().unwrap();
                    cycleMatches.put( fromPair, toPair );
                    fromPair = toPair;
                }

                matches.add( cycleMatches );
            }
        }

        return matches;
    }
}
