import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.*;

import java.util.Arrays;

public class SimplexTest {
    public static void run() {
        LinearObjectiveFunction objectiveFunction = new LinearObjectiveFunction( new double[] { -1.0, -1.0 }, 0.0 );
        LinearConstraintSet constraints = new LinearConstraintSet(
                new LinearConstraint( new double[] { 1.0, 2.0 }, Relationship.LEQ, 3.0 ),
                new LinearConstraint( new double[] { 2.0, 1.0 }, Relationship.LEQ, 3.0 )
        );

        SimplexSolver solver = new SimplexSolver();
        PointValuePair solution = solver.optimize( objectiveFunction, constraints );
        System.out.println( "minimize: -x - y" );
        System.out.println( "subject to:" );
        System.out.println( "     x + 2y <= 3" );
        System.out.println( "    2x +  y <= 3" );
        System.out.println( "Optimal point: " + Arrays.toString( solution.getPoint() ) );
        System.out.println( "Optimal value: " + solution.getValue() );
    }
}
