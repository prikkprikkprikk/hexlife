/**
 * PerformanceBenchmark - Test harness for measuring performance across different board sizes
 * Runs without GUI to focus purely on computation performance
 */
public class PerformanceBenchmark implements HexLifeConstants {

    public static void main(String[] args) {
        System.out.println("HexLife Performance Benchmark");
        System.out.println("=============================\n");

        // Test hexagonal boards at various sizes
        testHexBoards();

        System.out.println("\n");

        // Test rectangular boards at various sizes
        testRectBoards();

        System.out.println("\n");
        System.out.println("Benchmark complete!");
    }

    private static void testHexBoards() {
        System.out.println("HEXAGONAL BOARD BENCHMARKS");
        System.out.println("--------------------------");

        int[] hexSizes = {10, 20, 30, 50, 75, 100};

        for (int size : hexSizes) {
            benchmarkBoard(HEX, size);
        }
    }

    private static void testRectBoards() {
        System.out.println("RECTANGULAR BOARD BENCHMARKS");
        System.out.println("----------------------------");

        int[] rectSizes = {30, 60, 90, 120, 150};

        for (int size : rectSizes) {
            benchmarkBoard(RECT, size);
        }
    }

    private static void benchmarkBoard(String shape, int size) {
        PerformanceMonitor perf = PerformanceMonitor.getInstance();
        perf.resetStats();
        perf.setLoggingEnabled(false);  // Disable per-measurement logging

        try {
            System.out.println("\nTesting " + shape + " board size " + size + "...");

            // Create board (with timing - will be suppressed)
            Board board = new Board(shape, size);

            // Randomize the board
            board.randomize();

            // Run 100 generations
            long batchStartTime = System.nanoTime();

            for (int gen = 0; gen < 100; gen++) {
                board.advance();
            }

            long batchElapsedMs = (System.nanoTime() - batchStartTime) / 1_000_000;

            // Calculate cell count
            long cellCount = calculateCellCount(shape, size);

            // Print summary statistics
            long avgMs = batchElapsedMs / 100;
            System.out.printf("  Cell count:       %,d cells\n", cellCount);
            System.out.printf("  Total time:       %,d ms (100 generations)\n", batchElapsedMs);
            System.out.printf("  Avg per gen:      %.2f ms\n", batchElapsedMs / 100.0);
            System.out.printf("  Throughput:       %.0f cells/ms\n", (cellCount * 100.0) / batchElapsedMs);
            System.out.printf("  Est. FPS:         %.1f\n", 1000.0 / (avgMs == 0 ? 1 : avgMs));

        } catch (Exception e) {
            System.err.println("  ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static long calculateCellCount(String shape, int size) {
        if (HEX.equals(shape)) {
            // Hexagonal: roughly size^2 * sqrt(3)
            long diameter = size * 2 - 1;
            long middleRow = size;
            long cellCount = 0;

            // Upper half
            long rowLength = size;
            for (long row = 0; row < middleRow; row++) {
                cellCount += rowLength;
                rowLength++;
            }

            // Middle and lower half
            rowLength = size + (diameter - middleRow - 1);
            for (long row = middleRow; row < diameter; row++) {
                cellCount += rowLength;
                rowLength--;
            }

            return cellCount;
        } else {
            // Rectangular: size * (size / hex_ratio)
            int numberOfRows = (int) ((double) size / HEX_HEIGHT_TO_WIDTH_RATIO);
            numberOfRows = numberOfRows - (numberOfRows + 1) % 2;
            return (long) size * numberOfRows;
        }
    }
}
