package myshgs.Others.MBR_Oriented.MBRs_Zorder;

import myshgs.Utils;

import java.util.Arrays;
import java.util.BitSet;

/**
 * MBR class represents a Minimum Bounding Rectangle in a multi-dimensional space.
 * It is used to enclose a set of points and provides functionalities such as comparison, addition, and deletion.
 */
public class MBR implements Comparable<MBR> {
    private final long[] min;
    private final long[] max;
    public BitSet minpt;
    public BitSet maxpt;
    public long[][] datas;
    public int space;
    public int usedSpace;
    public int d;
    public boolean isDominate;

    /**
     * Constructs an MBR object that covers a specific area.
     * @param d The dimension of the space.
     * @param C The capacity of the MBR, i.e., the maximum number of points it can enclose.
     */
    public MBR(int d, int C) { // Create rectangle that covers an area
        this.min = new long[d];
        this.max = new long[d];
        this.d = d;
        this.space = C;
        this.isDominate = false;
        this.datas = new long[C + 1][d];
        this.usedSpace = 0;
        this.minpt = new BitSet();
        this.maxpt = new BitSet();
    }

    /**
     * Returns a copy of the minimum bounding coordinates of the MBR.
     * @return A copy of the minimum bounding coordinates.
     */
    public long[] getMin() {
        return min.clone();
    }

    /**
     * Returns a copy of the maximum bounding coordinates of the MBR.
     * @return A copy of the maximum bounding coordinates.
     */
    public long[] getMax() {
        return max.clone();
    }

    /**
     * Sets whether the MBR is dominated.
     * @param dominate The domination status to be set.
     */
    public void setDominate(boolean dominate) {
        isDominate = dominate;
    }

    /**
     * Adds a point to the MBR and updates the bounding coordinates.
     * @param rec The point to be added, represented as an array of coordinates.
     */
    protected void addData(long[] rec) {
        datas[usedSpace++] = rec;

        for (int j = 0; j < d; j++) {
            min[j] = Math.min(rec[j], min[j]);
            max[j] = Math.max(rec[j], max[j]);
        }

        minpt = Utils.fromPtoZ(min);
        maxpt = Utils.fromPtoZ(max);
    }

    /**
     * Deletes a point from the MBR by its index and updates the data structure.
     * @param i The index of the point to be deleted.
     */
    protected void delete(int i) {
        long[] tmp = datas[i];
        datas[i] = datas[--usedSpace];
        datas[usedSpace] = tmp;
    }

    /**
     * Returns a string representation of the MBR, including its enclosed points.
     * @return A string representation of the MBR.
     */
    @Override
    public String toString() {
        if (!Arrays.equals(min, max)) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < usedSpace; i++) {
                str.append(Arrays.toString(datas[i]));
            }

            return " {MBR " +
//                    "min: " + Arrays.toString(min) + ", max: " + Arrays.toString(max)
                    ", points = {" + str + "}"
                    + " }";
        } // For rectangle
        return " { points: " + Arrays.toString(min) + ", " + usedSpace + " }"; // For single point
    }

    /**
     * Compares this MBR with another based on their minimum distances.
     * @param arg0 The other MBR to be compared.
     * @return An integer value indicating the comparison result.
     */
    @Override
    public int compareTo(MBR arg0) { // Compare 2 rectangles by their mindists
        if (arg0 != null) {
            return Utils.compare(minpt, Utils.fromPtoZ(arg0.getMin()));
        }
        return 0;
    }
}
