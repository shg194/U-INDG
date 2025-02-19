package myshgs.Others.MBR_Oriented.MBRSKY;

import java.util.Arrays;

/**
 * MBR class: Represents a Minimum Bounding Rectangle in multi-dimensional space.
 * Used for spatial indexing and data organization, supporting spatial queries and data management.
 */
public class MBR implements Comparable<MBR> {
    private final long[] min; // The minimum value of each dimension of the MBR
    private final long[] max; // The maximum value of each dimension of the MBR
    public long[][] datas; // Stores the data points covered by the MBR
    public int space; // The maximum capacity of data points the MBR can hold
    public int usedSpace; // The number of data points currently stored in the MBR
    public int d; // The dimensionality of the data
    public long minValue; // The sum of the minimum values of all dimensions
    public long maxValue; // The sum of the maximum values of all dimensions
    public boolean isDominate; // Flag indicating whether the MBR is dominant

    /**
     * Constructs an MBR object.
     * @param d The dimensionality of the data
     * @param C The maximum capacity of data points the MBR can hold
     */
    public MBR(int d, int C) { // Create rectangle that covers an area
        this.min = new long[d];
        this.max = new long[d];
        this.d = d;
        this.space = C;
        this.isDominate = false;
        Arrays.fill(min, Integer.MAX_VALUE);
        Arrays.fill(max, Integer.MIN_VALUE);
        this.datas = new long[C + 1][d];
        this.usedSpace = 0;
    }

    /**
     * Returns the minimum value of each dimension of the MBR.
     * @return The minimum value array of each dimension
     */
    public long[] getMin() {
        return min.clone();
    }

    /**
     * Returns the maximum value of each dimension of the MBR.
     * @return The maximum value array of each dimension
     */
    public long[] getMax() {
        return max.clone();
    }

    /**
     * Sets the dominance flag of the MBR.
     * @param dominate The dominance flag
     */
    public void setDominate(boolean dominate) {
        isDominate = dominate;
    }

    /**
     * Adds a data point to the MBR and updates the MBR's boundaries.
     * @param rec The data point to add
     */
    protected void addData(long[] rec) {
        datas[usedSpace++] = rec;

        for (int j = 0; j < d; j++) {
            min[j] = Math.min(rec[j], min[j]);
            max[j] = Math.max(rec[j], max[j]);
        }
        this.minValue = Arrays.stream(min).sum();
        this.maxValue = Arrays.stream(max).sum();
    }

    /**
     * Deletes a data point from the MBR.
     * @param i The index of the data point to delete
     */
    protected void delete(int i) {
        long[] tmp = datas[i];
        datas[i] = datas[--usedSpace];
        datas[usedSpace] = tmp;
    }

    /**
     * Returns a string representation of the MBR.
     * @return The string representation
     */
    @Override
    public String toString() {
        if (!Arrays.equals(min, max)) {
            StringBuffer str = new StringBuffer();
            for (int i = 0; i < usedSpace; i++) {
                str.append(Arrays.toString(datas[i]));
            }

            return " {MBR min: " + Arrays.toString(min) + ", max: " + Arrays.toString(max) + ", points = {" + str + "} }";
        } // For rectangle
        return " { points: " + Arrays.toString(min) + ", " + usedSpace + " }"; // For single point
    }

    /**
     * Calculates the square of the minimum distance from the origin to the MBR.
     * @return The square of the distance
     */
    public double getDistance() { // Calculate the square of mindist of the point (distance to point o)
        double res = 0;
        for (long datum : min) {
            res += datum;
        }
        return res;
    }

    /**
     * Compares this MBR with another MBR based on the square of the minimum distance.
     * @param o The other MBR to compare with
     * @return The comparison result
     */
    @Override
    public int compareTo(MBR o) {
        int compare = Double.compare(getDistance(), o.getDistance());
        if (compare == 0) {
            for (int i = 0; i < d; i++) {
                compare = Long.compare(min[i], o.getMin()[i]);
                if (compare != 0)
                    return compare;
            }
        }
        return compare;
    }
}
