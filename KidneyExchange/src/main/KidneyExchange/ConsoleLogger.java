package KidneyExchange;

public class ConsoleLogger {
    private static boolean quiet = false;

    public static boolean getQuiet() {
        return ConsoleLogger.quiet;
    }

    public static void setQuiet( boolean quiet ) {
        ConsoleLogger.quiet = quiet;
    }

    public static void print( Object x ) {
        if( !quiet )
            System.out.print( x );
    }

    public static void print( String x ) {
        if( !quiet )
            System.out.print( x );
    }

    public static void println() {
        if( !quiet )
            System.out.println();
    }

    public static void println( Object x ) {
        if( !quiet )
            System.out.println( x );
    }

    public static void println( String x ) {
        if( !quiet )
            System.out.println( x );
    }
}
