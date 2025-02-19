package myshgs.MyApproaches.IQuadPlusTree.QuadPlusTree;

import java.util.BitSet;

/**
 * Utility class for converting between multi-dimensional points and Z-curves.
 */
public class Utils {
    /**
     * Converts a multi-dimensional point to a Z-curve.
     *
     * @param point An array representing the coordinates of a multi-dimensional point.
     * @return A BitSet representing the Z-curve of the given multi-dimensional point.
     */
    public static BitSet fromPtoZ(long[] point) {
        int d = point.length;
        int maxLength = 0;
        // Find the maximum number of bits required to represent the coordinates in the point array.
        for (long k : point) {
            maxLength = Math.max(maxLength, Long.SIZE - Long.numberOfLeadingZeros(k));
        }
        BitSet result = new BitSet();
        // Convert each coordinate into bits and interleave them to form the Z-curve.
        for (int i = 0; i < maxLength; i++) {
            for (int j = 0; j < d; j++) {
                if ((point[j] & (1L << i)) != 0)
                    result.set(d * i + d - j - 1);
            }
        }
        return result;
    }

    /**
     * Converts a Z-curve back to a multi-dimensional point.
     *
     * @param z A BitSet representing the Z-curve.
     * @param d The dimensionality of the multi-dimensional point.
     * @return An array representing the coordinates of the multi-dimensional point.
     */
    public static long[] fromZtoP(BitSet z, int d) {
        long[] point = new long[d];
        int bit = z.length();
        // Extract each bit from the Z-curve and deinterleave it back into the original coordinates.
        for (int i = 0; i < bit; i++)
            if (z.get(i)) {
                point[d - i % d - 1] |= 1L << (i / d);
            }
        return point;
    }

    /**
     * Compares two Z-curves represented as BitSets.
     *
     * @param bitSet1 The first Z-curve.
     * @param bitSet2 The second Z-curve.
     * @return An integer indicating the order of the two Z-curves.
     */
    public static int compare(BitSet bitSet1, BitSet bitSet2) {
        int length1 = bitSet1.length();
        int length2 = bitSet2.length();
        // Compare the lengths of the two BitSets first.
        if (length1 > length2)
            return 1;
        else if (length1 < length2)
            return -1;
        else {
            // If the lengths are equal, compare each bit from the most significant bit to the least significant bit.
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
}
