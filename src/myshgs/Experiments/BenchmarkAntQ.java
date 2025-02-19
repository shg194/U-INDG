package myshgs.Experiments;

import myshgs.MyApproaches.IQuadPlusTree.IQuadPlusTree;
import myshgs.MyApproaches.IQuadTree.IQuadTree;
import myshgs.MyApproaches.IZSearch.IZBTree;
import myshgs.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkAntQ {
    public static void main(String[] args) throws IOException {
        int Cardinality = 500000;
        int Fanout = 400;
        int d = 8;
        long[][] points = Utils.generateAntiCorrelatedData(d, Cardinality, 1000000000, 0.1);
        for (int Q = 600; Q >= 50; Q -= 50) {
            List<AlgorithmResult> results = new ArrayList<>();
            String[] algorithms = {"IQuadPlusTree", "IQuadTree", "IZOrderRTree", "IZSearch"}; // Example algorithm names

            String filename = "Nbenchmark_log_AntiQ" + Q + ".txt";
            File logFile = new File(filename);
            FileWriter logWriter = new FileWriter(logFile);
            logWriter.write("Benchmark Start\n");

            // Preheat to stabilize the environment
            System.out.println("Preheating...");
            preheat();

            // Run benchmarks
            for (String algorithm : algorithms) {
                System.out.println("Testing " + algorithm);
                double totalQueryTime = 0;
                long totalIoCount = 0;
                long totalDominationCount = 0;

                int dataSize = -1;
                int dimension = -1;
                int skylinePointCount = -1;

                int k = 50;
                for (int i = 0; i < k; i++) {
                    AlgorithmResult result = switch (algorithm) {
                        case "IQuadPlusTree" -> testIQuadPlusTree(points, Fanout, Q, d);
                        case "IQuadTree" -> testIQuadTree(points, Q, d);
                        case "IZOrderRTree" -> testIZOrderRTree(points, Fanout, Q, d);
                        default -> testIZSearch(points, Fanout, Q, d);
                    };
                    dataSize = result.dataSize;
                    dimension = result.dimension;
                    skylinePointCount = result.SL;
                    totalQueryTime += result.queryTime;
                    totalIoCount += result.IO;
                    totalDominationCount += result.DT;
                    logWriter.write("Iteration " + (i + 1) + ": " + result + "\n");
                    logWriter.flush();

                    // Reset environment after each test
                    resetEnvironment();
                }

                // Compute averages
                AlgorithmResult avgResult = new AlgorithmResult(
                        algorithm,
                        totalQueryTime / k,
                        totalIoCount / k,
                        totalDominationCount / k,
                        dataSize,
                        dimension,
                        skylinePointCount
                );
                results.add(avgResult);
            }

            filename = "Nbenchmark_results_AntiQ" + Q + ".csv";
            // Save results to CSV
            saveResultsToCSV(results, filename);

            logWriter.write("NBenchmark Complete\n");
            logWriter.close();
        }
    }

    private static void preheat() {
        for (int i = 0; i < 100; i++) {
            System.gc(); // Trigger garbage collection
            dummyWorkload();
            System.gc(); // Trigger garbage collection
        }
    }

    private static void resetEnvironment() {
        System.gc(); // Trigger garbage collection
        dummyWorkload();
        System.gc(); // Trigger garbage collection
    }

    private static void dummyWorkload() {
        for (int i = 0; i < 1_000_00000; i++) {
            double sqrt = Math.sqrt(i);// Simulate CPU workload
            double sqrt1 = Math.sqrt(sqrt);
        }
    }

    private static AlgorithmResult testIQuadPlusTree(long[][] points, int F, int Q, int dimension) {
        int C = calculateCapacity(dimension);
        IQuadPlusTree loader = new IQuadPlusTree(C, F, Q, dimension);
//            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("vehicles7D-390585.txt")).getPath());
        int len = points.length;

        long[] ioCount = new long[2];
        loader.bulkLoading(points);

        // Simulate test logic. Replace with actual algorithm test logic.
        long startTime = System.nanoTime();
        List<long[]> skyline = loader.skyline(ioCount);

        double queryTime = (System.nanoTime() - startTime) / 1000000.0;

        return new AlgorithmResult("IQuadPlusTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());

    }

    private static AlgorithmResult testIQuadTree(long[][] points, int Q, int dimension) {
        int C = calculateCapacity(dimension);
        IQuadTree loader = new IQuadTree(C, Q, dimension);
//            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("vehicles7D-390585.txt")).getPath());
        int len = points.length;

        long[] ioCount = new long[2];
        loader.init(points);

        // Simulate test logic. Replace with actual algorithm test logic.
        long startTime = System.nanoTime();
        List<long[]> skyline = loader.skyline(ioCount);

        double queryTime = (System.nanoTime() - startTime) / 1000000.0;

        return new AlgorithmResult("IQuadTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());

    }

    private static AlgorithmResult testIZSearch(long[][] points, int F, int Q, int dimension) {
//        if (dimension == 7) {
        int C = calculateZCapacity(dimension);
        IZBTree loader = new IZBTree(C, F, Q, dimension);
//            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("vehicles7D-390585.txt")).getPath());
        int len = points.length;

        long[] ioCount = new long[2];
        loader.init(points);

        // Simulate test logic. Replace with actual algorithm test logic.
        long startTime = System.nanoTime();
        List<long[]> skyline = loader.skyline(ioCount);

        double queryTime = (System.nanoTime() - startTime) / 1000000.0;

        return new AlgorithmResult("IZSearch", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());

    }

    private static AlgorithmResult testIZOrderRTree(long[][] points, int F, int Q, int dimension) {
        int C = calculateRCapacity(dimension);
        IZBTree loader = new IZBTree(C, F, Q, dimension);
//            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("vehicles7D-390585.txt")).getPath());
        int len = points.length;

        long[] ioCount = new long[2];
        loader.init(points);

        // Simulate test logic. Replace with actual algorithm test logic.
        long startTime = System.nanoTime();
        List<long[]> skyline = loader.skyline(ioCount);

        double queryTime = (System.nanoTime() - startTime) / 1000000.0;

        return new AlgorithmResult("IZOrderRTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
    }

    private static void saveResultsToCSV(List<AlgorithmResult> results, String fileName) throws IOException {
        File csvFile = new File(fileName);
        FileWriter writer = new FileWriter(csvFile);
        writer.write("Algorithm,QueryTime(ms),DT,IO,DataSize,Dimension,SL\n");
        for (AlgorithmResult result : results) {
            writer.write(result.toString() + "\n");
        }
        writer.close();
    }

    private static int calculateRCapacity(int dimension) {
        final int PAGE_SIZE = 4 * 1024 - 16 - dimension * Long.BYTES * 2 - 40; // 4 KB in bytes  40-byte for Tuple Information
        int entrySize = dimension * Long.BYTES;
        return PAGE_SIZE / entrySize;
    }

    private static int calculateRFanOut(int dimension) {
        final int PAGE_SIZE = 4 * 1024 - 16 - dimension * Long.BYTES * 2 - 40; // 4 KB in bytes  40-byte for Tuple Information
        int entrySize = 4;
        return PAGE_SIZE / entrySize;
    }

    private static int calculateZCapacity(int dimension) {
        final int PAGE_SIZE = 4 * 1024 - 16 - (dimension * 30) / 4 - 40;  // 4 KB in bytes  40-byte for Tuple Information
        int entrySize = (dimension * 30) / 8;
        return PAGE_SIZE / entrySize;
    }

    private static int calculateZFanOut(int dimension) {
        final int PAGE_SIZE = 4 * 1024 - 16 - (dimension * 30) / 4 - 40; // 4 KB in bytes  40-byte for Tuple Information
        int entrySize = 4;
        return PAGE_SIZE / entrySize;
    }

    private static int calculateCapacity(int dimension) {
        final int PAGE_SIZE = 4 * 1024 - 16 - dimension * Long.BYTES - 40; // 4 KB in bytes  40-byte for Tuple Information
        int entrySize = dimension * Long.BYTES;
        return PAGE_SIZE / entrySize;
    }

    private static int calculateFanOut(int dimension) {
        final int PAGE_SIZE = 4 * 1024 - 16 - dimension * Long.BYTES - 40;// 8 KB in bytes
        int entrySize = 4;
        return PAGE_SIZE / entrySize;
    }
}
