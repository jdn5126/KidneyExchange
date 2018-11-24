import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class MILPTest {
    public static void run() {
        // Example same as one on Wikipedia article here: https://en.wikipedia.org/wiki/Integer_programming
        Model model = new Model( "Maximize y" );

        // Setup decision variables.
        IntVar x = model.intVar( "X", 0, IntVar.MAX_INT_BOUND );
        IntVar y = model.intVar( "Y", 0, IntVar.MAX_INT_BOUND );
        IntVar[] variables = new IntVar[] { x, y };

        // Setup constraints.
        model.scalar( variables, new int[] { -1, 1 }, "<=", 1 ).post();
        model.scalar( variables, new int[] { 3, 2 }, "<=", 12 ).post();
        model.scalar( variables, new int[] { 2, 3 }, "<=", 12 ).post();

        // Find optimal solution.
        Solver solver = model.getSolver();
        final boolean maximize = true;
        Solution solution = solver.findOptimalSolution( y, maximize );

        // Output results.
        solver.printStatistics();
        System.out.println( "Value for x: " + solution.getIntVal( x ) );
        System.out.println( "Value for y: " + solution.getIntVal( y ) );
    }
}
