/**
 * PerformanceMonitor tracks timing and memory usage metrics for HexLife
 * Provides utilities to measure generation time, board initialization, and memory pressure
 */
public class PerformanceMonitor {

    private static class Measurement {
        long startTime;
        String name;

        Measurement(String name) {
            this.name = name;
            this.startTime = System.nanoTime();
        }

        long getElapsedMs() {
            return (System.nanoTime() - startTime) / 1_000_000;
        }

        double getElapsedSeconds() {
            return (System.nanoTime() - startTime) / 1_000_000_000.0;
        }
    }

    // Singleton instance
    private static PerformanceMonitor instance = new PerformanceMonitor();

    // Statistics tracking
    private long totalGenerationTime = 0;
    private long generationCount = 0;
    private long minGenerationTime = Long.MAX_VALUE;
    private long maxGenerationTime = 0;
    private long currentMeasurementStart;
    private String currentMeasurementName;
    private boolean enableLogging = false;  // Silent by default, enable in debug mode

    // Painting statistics
    private long totalPaintTime = 0;
    private long paintCount = 0;
    private long minPaintTime = Long.MAX_VALUE;
    private long maxPaintTime = 0;

    // Board initialization timing
    private long lastBoardInitTime = 0;

    public static PerformanceMonitor getInstance() {
        return instance;
    }

    /**
     * Start measuring a named operation
     */
    public void startMeasurement(String name) {
        if (!enableLogging) return;
        currentMeasurementStart = System.nanoTime();
        currentMeasurementName = name;
    }

    /**
     * End the current measurement and log result
     */
    public long endMeasurement() {
        if (!enableLogging) return 0;
        if (currentMeasurementName == null) return 0;

        long elapsedMs = (System.nanoTime() - currentMeasurementStart) / 1_000_000;
        String message = String.format("%-30s %6d ms", currentMeasurementName + ":", elapsedMs);
        System.out.println(message);

        return elapsedMs;
    }

    /**
     * Record a single generation timing (used for statistics)
     */
    public void recordGeneration(long elapsedMs) {
        if (!enableLogging) return;

        totalGenerationTime += elapsedMs;
        generationCount++;
        minGenerationTime = Math.min(minGenerationTime, elapsedMs);
        maxGenerationTime = Math.max(maxGenerationTime, elapsedMs);
    }

    /**
     * Get current memory usage in MB
     */
    public double getMemoryUsageMB() {
        Runtime runtime = Runtime.getRuntime();
        return (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0);
    }

    /**
     * Get heap size in MB
     */
    public double getHeapSizeMB() {
        return Runtime.getRuntime().totalMemory() / (1024.0 * 1024.0);
    }

    /**
     * Get max heap size in MB
     */
    public double getMaxHeapSizeMB() {
        return Runtime.getRuntime().maxMemory() / (1024.0 * 1024.0);
    }

    /**
     * Print current memory statistics
     */
    public void printMemoryStats() {
        if (!enableLogging) return;

        System.out.println("\n--- Memory Statistics ---");
        System.out.printf("  Used memory:  %.2f MB\n", getMemoryUsageMB());
        System.out.printf("  Heap size:    %.2f MB\n", getHeapSizeMB());
        System.out.printf("  Max heap:     %.2f MB\n", getMaxHeapSizeMB());
    }

    /**
     * Record board initialization timing
     */
    public void recordBoardInit(long elapsedMs) {
        lastBoardInitTime = elapsedMs;
    }

    /**
     * Get last board initialization time
     */
    public long getLastBoardInitTime() {
        return lastBoardInitTime;
    }

    /**
     * Record a paint operation timing
     */
    public void recordPaint(long elapsedMs) {
        if (!enableLogging) return;

        totalPaintTime += elapsedMs;
        paintCount++;
        minPaintTime = Math.min(minPaintTime, elapsedMs);
        maxPaintTime = Math.max(maxPaintTime, elapsedMs);
    }

    /**
     * Print generation statistics accumulated so far
     */
    public void printGenerationStats() {
        if (!enableLogging || generationCount == 0) return;

        System.out.println("\n--- Generation Statistics ---");
        System.out.printf("  Generations:  %d\n", generationCount);
        System.out.printf("  Total time:   %.2f seconds\n", totalGenerationTime / 1000.0);
        System.out.printf("  Average time: %.2f ms/gen\n", (double) totalGenerationTime / generationCount);
        System.out.printf("  Min time:     %d ms\n", minGenerationTime);
        System.out.printf("  Max time:     %d ms\n", maxGenerationTime);
        System.out.printf("  Est. FPS:     %.1f\n", 1000.0 / ((double) totalGenerationTime / generationCount));
    }

    /**
     * Print painting/drawing statistics
     */
    public void printPaintStats() {
        if (!enableLogging || paintCount == 0) return;

        System.out.println("\n--- Paint/Drawing Statistics ---");
        System.out.printf("  Paint calls:  %d\n", paintCount);
        System.out.printf("  Total time:   %.2f seconds\n", totalPaintTime / 1000.0);
        System.out.printf("  Average time: %.2f ms/paint\n", (double) totalPaintTime / paintCount);
        System.out.printf("  Min time:     %d ms\n", minPaintTime);
        System.out.printf("  Max time:     %d ms\n", maxPaintTime);
        System.out.printf("  Est. paint FPS: %.1f\n", 1000.0 / ((double) totalPaintTime / paintCount));
    }

    /**
     * Reset all statistics
     */
    public void resetStats() {
        totalGenerationTime = 0;
        generationCount = 0;
        minGenerationTime = Long.MAX_VALUE;
        maxGenerationTime = 0;
        totalPaintTime = 0;
        paintCount = 0;
        minPaintTime = Long.MAX_VALUE;
        maxPaintTime = 0;
    }

    /**
     * Enable or disable logging output
     */
    public void setLoggingEnabled(boolean enabled) {
        enableLogging = enabled;
    }

    /**
     * Get estimated frames per second based on current generation time
     */
    public double getEstimatedFPS() {
        if (generationCount == 0) return 0;
        long avgMs = totalGenerationTime / generationCount;
        return avgMs > 0 ? 1000.0 / avgMs : 0;
    }
}
