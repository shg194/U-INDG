package myshgs.Others.ZSearch;

import java.util.BitSet;

/**
 * Utility class for calculating RZRegions and common bit areas.
 */
public class utils {
    /**
     * Calculates the RZRegions region between two bit sets.
     *
     * @param bitSet1 The first Z-order value bitSet1.
     * @param bitSet2 The second Z-order value bitSet2.
     * @param d The Dimension.
     * @return An RZ region by bitSet1 and bitSet2.
     */
    public static BitSet[] getRZRegion(BitSet bitSet1, BitSet bitSet2, int d) {
        BitSet[] bitSet = new BitSet[2];
        bitSet[0] = new BitSet();
        bitSet[1] = (BitSet) bitSet2.clone();

        int index1 = bitSet1.length();
        int index2 = bitSet2.length();
        if (index1 == index2) {
            int k = (index1) / d * d;
            k = (index1 % d == 0) ? k - d : k;
            while (k >= 0 && bitSet1.get(k, k + d).equals(bitSet2.get(k, k + d))) {
                k -= d;
            }
            k += d;
            bitSet[1].set(0, k, false);
            bitSet[0] = (BitSet) bitSet[1].clone();
            bitSet[1].set(0, k, true);
        } else {
            index1 = Math.max(Math.max(index1, index2) - 1, 0);
            int k = (index1) / d * d;
            k = (index1 % d == 0) ? k : k + d;
            bitSet[1].set(0, k, true);
        }
        return bitSet;
    }

    /**
     * Calculates the common area between two Z-order values.
     *
     * @param a The first Z-order value a.
     * @param b The second Z-order value b.
     * @param d The Dimension.
     * @return The common area as a BitSet.
     */
    public static BitSet getArea(BitSet a, BitSet b, int d) {
        BitSet target = new BitSet();

        int index1 = a.length();
        int index2 = b.length();
        if (index1 == index2) {
            int k = (index1) / d * d;
            k = (index1 % d == 0) ? k - d : k;
            while (k >= 0 && a.get(k, k + d).equals(b.get(k, k + d))) {
                k -= d;
            }
            k += d;
            target.set(0, k, true);
        } else {
            index1 = Math.max(Math.max(index1, index2) - 1, 0);
            int k = (index1) / d * d;
            k = (index1 % d == 0) ? k : k + d;
            target.set(0, k, true);
        }

        return target;
    }
}
