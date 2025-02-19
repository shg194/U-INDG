package myshgs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Random;

/**
 * Utility class containing methods for generating and processing data.
 */
public class Utils {
    /**
     * Generates anti-correlated data.
     *
     * @param numberOfDimensions The number of dimensions for the data.
     * @param numberOfData The number of data points to generate.
     * @param maxDataValue The maximum value for the data points.
     * @param spread The spread of the data around the related value.
     * @return An array of anti-correlated data points.
     */
    public static long[][] generateAntiCorrelatedData(int numberOfDimensions, int numberOfData, long maxDataValue, double spread) {
        long[][] data = new long[numberOfData][numberOfDimensions];
        Random random = new Random();
        for (int i = 0; i < numberOfData; i++) {
            data[i][0] = random.nextLong(maxDataValue);
            long relatedValue = maxDataValue - data[i][0];
            for (int j = 1; j < numberOfDimensions; j++) {
                long t = (long) (relatedValue + (relatedValue * (random.nextDouble() - 0.5) * spread));
                data[i][j] = Math.min(Math.max(t, 0), maxDataValue); // Ensure the value stays within bounds;
            }
        }
        return data;
    }

    /**
     * Generates independent data.
     *
     * @param numberOfDimensions The number of dimensions for the data.
     * @param numberOfData The number of data points to generate.
     * @param maxDataValue The maximum value for the data points.
     * @return An array of independent data points.
     */
    public static long[][] generateIndependentData(int numberOfDimensions, int numberOfData, int maxDataValue) {
        long[][] data = new long[numberOfData][numberOfDimensions];
        Random random = new Random();
        for (int i = 0; i < numberOfData; i++) {
            for (int j = 0; j < numberOfDimensions; j++) {
                data[i][j] = random.nextLong(maxDataValue);
            }
        }
        return data;
    }

    /**
     * Checks if object a is dominated by object b.
     *
     * @param a The first object.
     * @param b The second object.
     * @param count A counter for the number of dominance test.
     * @return True if a is dominated by b, false otherwise.
     */
    public static boolean isDominatedBy(long[] a, long[] b, long[] count) {
        count[0]++;
        boolean isDominated = false;
        for (int i = 0; i < a.length; i++) {
            if (b[i] > a[i]) {
                isDominated = true;
            } else if (b[i] < a[i]) {
                return false;
            }
        }
        return isDominated;
    }

    /**
     * Converts a Z-order value to an object.
     *
     * @param z The Z-order value to convert.
     * @param d The dimension of the object.
     * @return The converted object.
     */
    public static long[] fromZtoP(BitSet z, int d) {
        long[] point = new long[d];
        int bit = z.length();
        for (int i = 0; i < bit; i++)
            if (z.get(i)) {
                point[d - i % d - 1] |= 1L << (i / d);
            }
        return point;
    }

    /**
     * Converts an object to Z-order values.
     *
     * @param point The object to convert.
     * @return The converted Z-order values.
     */
    public static BitSet fromPtoZ(long[] point) {
        int d = point.length;
        int maxLength = 0;
        for (long k : point) {
            maxLength = Math.max(maxLength, Long.SIZE - Long.numberOfLeadingZeros(k));
        }
        BitSet result = new BitSet();
        for (int i = 0; i < maxLength; i++) {
            for (int j = 0; j < d; j++) {
                if ((point[j] & (1L << i)) != 0)
                    result.set(d * i + d - j - 1);
            }
        }
        return result;
    }

    /**
     * Compares two objects for dominance test.
     *
     * @param p1 The first object.
     * @param p2 The second object.
     * @param count A counter for the number of dominance test.
     * @return -1 if p1 dominates p2, 1 if p2 dominates p1, 0 otherwise.
     */
    public static int DtDev(long[] p1, long[] p2, long[] count) {
        count[0]++;
        boolean t1_better = false, t2_better = false;
        for (int d = 0; d < p1.length; d++) {
            t1_better = p1[d] < p2[d] || t1_better;
            t2_better = p1[d] > p2[d] || t2_better;
            if (t1_better && t2_better) {
                return 0;
            }
        }
        if (!t1_better && t2_better) {
            return 1;
        }
        if (t1_better) {
            return -1;
        }
        return 0;
    }

    /**
     * Compares two Z-order values.
     *
     * @param bitSet1 The first Z-order values.
     * @param bitSet2 The second Z-order values.
     * @return -1 if bitSet1 is less than bitSet2, 1 if bitSet1 is greater than bitSet2, 0 otherwise.
     */
    public static int compare(BitSet bitSet1, BitSet bitSet2) {
        int length1 = bitSet1.length();
        int length2 = bitSet2.length();
        if (length1 > length2)
            return 1;
        else if (length1 < length2)
            return -1;
        else {
            for (int i = length1 - 1; i >= 0; i--) {
                boolean bit1 = bitSet1.get(i);
                boolean bit2 = bitSet2.get(i);
                if (bit1 != bit2) {
                    return bit1 ? 1 : -1;
                }
            }
            return 0;
        }
    }

    /**
     * Reads real data from a txt file.
     *
     * @param csvFile The path to the txt file.
     * @return An array of long arrays containing the data from the CSV file.
     */
    public static long[][] getRealData(String csvFile) {
        List<long[]> points = new ArrayList<>();
        String line;
        String csvDelimiter = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] values = line.split(csvDelimiter);
                int index = 0;

                if (csvFile.contains("vehicles7D-390585.txt")) {
                    int d = 7;
                    long[] p = new long[d];
                    for (int i = 0; i < d; i++) {
                        p[index++] = (long) Double.parseDouble(values[i]);
                    }
                    points.add(p);
                } else if (csvFile.contains("htsensor8D.txt")) {
                    int d = 8;
                    long[] p = new long[d];
                    for (int i = 0; i < d; i++) {
                        p[index++] = Integer.parseInt(values[i]);
                    }
                    points.add(p);
                } else if (csvFile.contains("house9D-227570.txt")) {
                    int d = 9;
                    long[] p = new long[d];
                    for (int i = 0; i < d; i++) {
                        p[index++] = (long) Double.parseDouble(values[i]);
                    }
                    points.add(p);
                } else if (csvFile.contains("covtype10D.txt")) {
                    int d = 10;
                    long[] p = new long[d];
                    for (int i = 0; i < d; i++) {
                        p[index++] = (long) Double.parseDouble(values[i]);
                    }
                    points.add(p);
                }
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
        long[][] result = new long[points.size()][];
        int dex = 0;
        for (long[] p : points) {
            result[dex++] = p;
        }
        return result;
    }

    /**
     * For test the 正确性correctness of algorithms, to find a set of points for the skyline.
     *
     * @param points The set of points to find the skyline of.
     * @return A list of points that form the skyline.
     */
    public static List<long[]> findSkyline(long[][] points) {
        List<long[]> skyline = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            long[] current = points[i];
            boolean isSkyline = true;
            for (int j = 0; j < points.length; j++) {
                if (i != j) {
                    long[] other = points[j];
                    if (isDominatedBy(other, current, new long[]{0, 0})) {
                        isSkyline = false;
                        break;
                    }
                }
            }
            if (isSkyline) {
                skyline.add(current);
            }
        }
        return skyline;
    }
}
