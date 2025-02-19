package myshgs.Others.MBR_Oriented.MBRs_Zorder;

import java.util.BitSet;

/**
 * Utility class for MBR (Minimum Bounding Rectangle) related operations.
 */
public class utils {

    /**
     * Determines if MBR 'a' dominates MBR 'b'.
     *
     * @param a The first MBR object.
     * @param b The second MBR object.
     * @param d The dimension.
     * @param count A counter for the number of dominance test.
     * @return true if 'a' dominates 'b'; otherwise, false.
     */
    public static boolean DTDominated(MBR a, MBR b, int d, long[] count) {
        count[0]++;
        boolean flag = false, isDominate = false, equ = true, equ1 = true;
        for (int i = 0; i < d; i++) {
            if (a.getMax()[i] > b.getMin()[i]) {
                equ = false;
                if (flag)
                    return false;
                else {
                    flag = true;
                    if (a.getMin()[i] < b.getMin()[i]) {
                        isDominate = true;
                    } else if (a.getMin()[i] > b.getMin()[i])
                        return false;
                }
            } else if (a.getMax()[i] < b.getMin()[i]) {
                equ = false;
                isDominate = true;
            }
            if (a.getMin()[i] != a.getMax()[i])
                equ1 = false;
        }
        if (equ && !equ1)
            return true;
        return isDominate;
    }

    /**
     * Calculates the common area of two MBRs .
     *
     * @param a The Z-order value a.
     * @param b The Z-order value b.
     * @param d The dimension.
     * @return A BitSet representing the common area.
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
        }else {
            index1 = Math.max(Math.max(index1, index2) - 1, 0);
            int k = (index1) / d * d;
            k = (index1 % d == 0) ? k : k + d;
            target.set(0, k, true);
        }

        return target;
    }
}
