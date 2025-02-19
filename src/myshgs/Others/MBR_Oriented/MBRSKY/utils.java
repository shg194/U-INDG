package myshgs.Others.MBR_Oriented.MBRSKY;

/**
 * Utility class for MBR (Minimum Bounding Rectangle) operations.
 */
public class utils {

    /**
     * Determines if MBR 'a' dominates MBR 'b'.
     *
     * Dominance is defined as follows:
     * - MBR 'a' has at least one dimension where its minimum value is less than the corresponding minimum value of MBR 'b'.
     * - For all other dimensions, the minimum value of MBR 'a' is not greater than the corresponding minimum value of MBR 'b'.
     * - Additionally, MBR 'a' has at least one dimension where its maximum value is greater than the corresponding minimum value of MBR 'b'.
     *
     * @param a      The first MBR object to compare.
     * @param b      The second MBR object to compare.
     * @param d      The number of dimensions.
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return       True if MBR 'a' dominates MBR 'b', otherwise false.
     */
    public static boolean DTDominated(MBR a, MBR b, int d, long[] count) {
        count[0]++;

        // Initialize flags for dominance check
        boolean flag = false, isDominate = false, equ = true, equ1 = true;

        // Iterate over each dimension to check dominance conditions
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

            // Check if MBR 'a' is degenerate (min == max for all dimensions)
            if (a.getMin()[i] != a.getMax()[i])
                equ1 = false;
        }

        // If all dimensions are equal but MBR 'a' is not degenerate, return true
        if (equ && !equ1)
            return true;

        return isDominate;
    }
}
