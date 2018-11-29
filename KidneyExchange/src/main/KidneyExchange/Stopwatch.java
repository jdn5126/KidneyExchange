package KidneyExchange;

public class Stopwatch {
    private long startTime;

    public long start() {
        long oldStart = startTime;
        startTime = System.nanoTime();
        return startTime - oldStart;
    }

    public long stop() {
        return System.nanoTime() - startTime;
    }
}
