package myshgs.Experiments;

import myshgs.MyApproaches.IQuadPlusTree.IQuadPlusTree;
import myshgs.MyApproaches.IQuadTree.IQuadTree;
import myshgs.MyApproaches.IZOrderRTree.ZIBBS;
import myshgs.MyApproaches.IZSearch.IZBTree;
import myshgs.Others.BBS.BBS;
import myshgs.Others.MBR_Oriented.MBRSKY.MBRSky;
import myshgs.Others.MBR_Oriented.MBRs_Zorder.ZMBRSky;
import myshgs.Others.ZSearch.ZBTree;
import myshgs.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BenchmarkReal {

    public static ClassLoader classLoader = BenchmarkReal.class.getClassLoader();

    public static void main(String[] args) throws IOException {
        int[] realdata = new int[4];
        realdata[0] = 7;
        realdata[1] = 8;
        realdata[2] = 9;
        realdata[3] = 10;
        for (int d : realdata) {
            List<AlgorithmResult> results = new ArrayList<>();
            String[] algorithms = {"MBRSTR", "MBRZOrder", "BBS", "ZSearch", "IQuadPlusTree", "IQuadTree", "IZOrderRTree", "IZSearch"}; // Example algorithm names

            String filename = "benchmark_log_R" + d + ".txt";
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
                        case "MBRSTR" -> testMBRSTR(d);
                        case "MBRZOrder" -> testMBRZOrder(d);
                        case "BBS" -> testBBS(d);
                        case "ZSearch" -> testZSearch(d);
                        case "IQuadPlusTree" -> testIQuadPlusTree(d);
                        case "IQuadTree" -> testIQuadTree(d);
                        case "IZOrderRTree" -> testIZOrderRTree(d);
                        default -> testIZSearch(d);
                    };
                    dataSize = result.dataSize;
                    dimension = result.dimension;
                    skylinePointCount = result.SL;
                    totalQueryTime += result.queryTime;
                    totalIoCount += result.IO;
                    totalDominationCount += result.DT;
                    logWriter.write("Iteration " + (i + 1) + ": " + result + "\n");

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

            filename = "benchmark_results_R" + d + ".csv";
            // Save results to CSV
            saveResultsToCSV(results, filename);

            logWriter.write("Benchmark Complete\n");
            logWriter.close();
        }
    }

    private static void preheat() {
        for (int i = 0; i < 10000; i++) {
            System.gc(); // Trigger garbage collection
            dummyWorkload();
            System.gc(); // Trigger garbage collection
        }
    }

    private static void resetEnvironment() {
        System.gc(); // Trigger garbage collection
        dummyWorkload();
        for (int j = 0; j < 20; j++) {
            System.gc(); // Trigger garbage collection
        }
    }

    private static void dummyWorkload() {
        for (int i = 0; i < 1_000_0000; i++) {
            Math.sqrt(i);// Simulate CPU workload
        }
    }

    private static AlgorithmResult testMBRSTR(int dimension) {
        if (dimension == 7) {
            int C = calculateRCapacity(dimension);
            MBRSky loader = new MBRSky(C,-1, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("vehicles7D-390585.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("STRMBRs", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 8) {
            int C = calculateRCapacity(dimension);
            MBRSky loader = new MBRSky(C,-1 ,dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("htsensor8D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("STRMBRs", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 9) {
            int C = calculateRCapacity(dimension);
            MBRSky loader = new MBRSky(C,-1, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("house9D-227570.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;
            return new AlgorithmResult("STRMBRs", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else {
            int C = calculateRCapacity(dimension);
            MBRSky loader = new MBRSky(C,-1, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("covtype10D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;
            return new AlgorithmResult("STRMBRs", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        }
    }

    private static AlgorithmResult testMBRZOrder(int dimension) {
        if (dimension == 7) {
            int C = calculateRCapacity(dimension);
            ZMBRSky loader = new ZMBRSky(C, -1,dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("vehicles7D-390585.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("ZorderMBRs", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 8) {
            int C = calculateRCapacity(dimension);
            ZMBRSky loader = new ZMBRSky(C,-1, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("htsensor8D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("ZorderMBRs", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 9) {
            int C = calculateRCapacity(dimension);
            ZMBRSky loader = new ZMBRSky(C, -1,dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("house9D-227570.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("ZorderMBRs", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else {
            int C = calculateRCapacity(dimension);
            ZMBRSky loader = new ZMBRSky(C, -1,dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("covtype10D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("ZOrderMBRs", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        }
    }

    private static AlgorithmResult testBBS(int dimension) {
        if (dimension == 7) {
            int C = calculateRCapacity(dimension);
            int F = calculateRFanOut(dimension);
            BBS loader = new BBS(C, F, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("vehicles7D-390585.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("BBS", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 8) {
            int C = calculateRCapacity(dimension);
            int F = calculateRFanOut(dimension);
            BBS loader = new BBS(C, F, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("htsensor8D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("BBS", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 9) {
            int C = calculateRCapacity(dimension);
            int F = calculateRFanOut(dimension);
            BBS loader = new BBS(C, F, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("house9D-227570.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("BBS", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else {
            int C = calculateRCapacity(dimension);
            int F = calculateRFanOut(dimension);
            BBS loader = new BBS(C, F, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("covtype10D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("BBS", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        }
    }

    private static AlgorithmResult testZSearch(int dimension) {
        if (dimension == 7) {
            int C = calculateZCapacity(dimension);
            int F = calculateZFanOut(dimension);
            ZBTree loader = new ZBTree(C, F, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("vehicles7D-390585.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("ZSearch", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 8) {
            int C = calculateZCapacity(dimension);
            int F = calculateZFanOut(dimension);
            ZBTree loader = new ZBTree(C, F, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("htsensor8D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("ZSearch", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 9) {
            int C = calculateZCapacity(dimension);
            int F = calculateZFanOut(dimension);
            ZBTree loader = new ZBTree(C, F, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("house9D-227570.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("ZSearch", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else {
            int C = calculateZCapacity(dimension);
            int F = calculateZFanOut(dimension);
            ZBTree loader = new ZBTree(C, F, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("covtype10D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("ZSearch", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        }
    }

    private static AlgorithmResult testIQuadPlusTree(int dimension) {
        if (dimension == 7) {
            int C = calculateCapacity(dimension);
            int F = calculateFanOut(dimension);
            IQuadPlusTree loader = new IQuadPlusTree(C, F, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("vehicles7D-390585.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.bulkLoading(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IQuadPlusTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 8) {
            int C = calculateCapacity(dimension);
            int F = calculateFanOut(dimension);
            IQuadPlusTree loader = new IQuadPlusTree(C, F, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("htsensor8D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.bulkLoading(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IQuadPlusTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 9) {
            int C = calculateCapacity(dimension);
            int F = calculateFanOut(dimension);
            IQuadPlusTree loader = new IQuadPlusTree(C, F, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("house9D-227570.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.bulkLoading(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IQuadPlusTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else {
            int C = calculateCapacity(dimension);
            int F = calculateFanOut(dimension);
            IQuadPlusTree loader = new IQuadPlusTree(C, F, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("covtype10D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.bulkLoading(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IQuadPlusTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        }
    }

    private static AlgorithmResult testIQuadTree(int dimension) {
        if (dimension == 7) {
            int C = calculateCapacity(dimension);
            IQuadTree loader = new IQuadTree(C, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("vehicles7D-390585.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IQuadTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 8) {
            int C = calculateCapacity(dimension);
            IQuadTree loader = new IQuadTree(C, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("htsensor8D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IQuadTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 9) {
            int C = calculateCapacity(dimension);
            IQuadTree loader = new IQuadTree(C, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("house9D-227570.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);
            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IQuadTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else {
            int C = calculateCapacity(dimension);
            IQuadTree loader = new IQuadTree(C, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("covtype10D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IQuadTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        }
    }

    private static AlgorithmResult testIZSearch(int dimension) {
        if (dimension == 7) {
            int C = calculateZCapacity(dimension);
            int F = calculateZFanOut(dimension);
            IZBTree loader = new IZBTree(C, F, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("vehicles7D-390585.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IZSearch", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 8) {
            int C = calculateZCapacity(dimension);
            int F = calculateZFanOut(dimension);
            IZBTree loader = new IZBTree(C, F, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("htsensor8D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IZSearch", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 9) {
            int C = calculateZCapacity(dimension);
            int F = calculateZFanOut(dimension);
            IZBTree loader = new IZBTree(C, F, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("house9D-227570.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);
            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IZSearch", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else {
            int C = calculateZCapacity(dimension);
            int F = calculateZFanOut(dimension);
            IZBTree loader = new IZBTree(C, F, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("covtype10D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IZSearch", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        }
    }

    private static AlgorithmResult testIZOrderRTree(int dimension) {
        if (dimension == 7) {
            int C = calculateRCapacity(dimension);
            int F = calculateRFanOut(dimension);
            IZBTree loader = new IZBTree(C, F, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("vehicles7D-390585.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IZOrderRTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 8) {
            int C = calculateRCapacity(dimension);
            int F = calculateRFanOut(dimension);
            ZIBBS loader = new ZIBBS(C, F, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("htsensor8D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IZOrderRTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else if (dimension == 9) {
            int C = calculateRCapacity(dimension);
            int F = calculateRFanOut(dimension);
            ZIBBS loader = new ZIBBS(C, F, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("house9D-227570.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);
            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IZOrderRTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        } else {
            int C = calculateRCapacity(dimension);
            int F = calculateRFanOut(dimension);
            ZIBBS loader = new ZIBBS(C, F, 100, dimension);
            long[][] points = Utils.getRealData(Objects.requireNonNull(classLoader.getResource("covtype10D.txt")).getPath());
            int len = points.length;

            long[] ioCount = new long[2];
            loader.init(points);

            // Simulate test logic. Replace with actual algorithm test logic.
            long startTime = System.nanoTime();
            List<long[]> skyline = loader.skyline(ioCount);

            double queryTime = (System.nanoTime() - startTime) / 1000000.0;

            return new AlgorithmResult("IZOrderRTree", queryTime, ioCount[0], ioCount[1], len, dimension, skyline.size());
        }
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
        final int PAGE_SIZE = 4 * 1024 - 16 - dimension * Long.BYTES - 40;// 4 KB in bytes
        int entrySize = 4;
        return PAGE_SIZE / entrySize;
    }
}
