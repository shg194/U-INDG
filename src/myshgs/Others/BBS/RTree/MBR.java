package myshgs.Others.BBS.RTree;

import java.util.Arrays;

/**
 * Represents a Minimum Bounding Rectangle (MBR) in multi-dimensional space.
 * An MBR is defined by its lower-left corner (low) and upper-right corner (high).
 * Implements Cloneable and Comparable interfaces for duplication and comparison functionalities.
 */
public class MBR implements Cloneable, Comparable<MBR> {
    private final long[] low;
    private final long[] high;

    /**
     * Constructs an MBR that covers an area defined by two points.
     *
     * @param p1 The lower-left corner of the rectangle.
     * @param p2 The upper-right corner of the rectangle.
     */
    public MBR(long[] p1, long[] p2) { // Create rectangle that covers an area
        low = p1.clone();
        high = p2.clone();
    }

    /**
     * Constructs an MBR for a single point.
     *
     * @param p The point representing both the lower-left and upper-right corners.
     */
    public MBR(long[] p) { // Create rectangle for a single point
        low = high = p;
    }

    /**
     * Returns the lower-left corner of the MBR.
     *
     * @return A copy of the lower-left corner.
     */
    public long[] getMin() {
        return low.clone();
    }

    /**
     * Returns the upper-right corner of the MBR.
     *
     * @return A copy of the upper-right corner.
     */
    public long[] getMax() {
        return high.clone();
    }

    /**
     * Computes the minimum bounding rectangle that contains both this MBR and another MBR.
     *
     * @param rec The other MBR to compute the union with.
     * @return The minimum bounding rectangle containing both MBRs.
     * @throws IllegalArgumentException If the input MBR is null or of a different dimension.
     */
    public MBR getUnion(MBR rec) { // Get the minimum rectangle that contains both of the 2 targeted rectangles
        if (rec == null) {
            throw new IllegalArgumentException("Rectangle cannot be null.");
        }
        if (rec.getDimension() != getDimension()) {
            throw new IllegalArgumentException("Rectangle must be of same dimension.");
        }
        long[] min = new long[getDimension()];
        long[] max = new long[getDimension()];
        for (int i = 0; i < getDimension(); i++) {
            min[i] = Math.min(low[i], rec.low[i]);
            max[i] = Math.max(high[i], rec.high[i]);
        }
        return new MBR(min, max);
    }

    /**
     * Calculates the area of the MBR.
     *
     * @return The area of the MBR.
     */
    public double getArea() {
        double area = 1;
        for (int i = 0; i < getDimension(); i++) {
            area *= high[i] - low[i];
        }
        return area;
    }

    /**
     * Computes the minimum bounding rectangle that contains all the given MBRs.
     *
     * @param rec An array of MBRs to compute the union of.
     * @return The minimum bounding rectangle containing all MBRs.
     * @throws IllegalArgumentException If the input array is null or empty.
     */
    public static MBR getUnion(MBR[] rec) { // Get the minimum rectangle that contains all the targeted rectangles
        if (rec == null || rec.length == 0) {
            throw new IllegalArgumentException("Rectangle array is empty.");
        }
        MBR res = (MBR) rec[0].clone();
        for (int i = 1; i < rec.length; i++) {
            res = res.getUnion(rec[i]);
        }
        return res;
    }

    /**
     * Returns a string representation of the MBR.
     * If the MBR represents a point, it returns the coordinates of that point.
     * Otherwise, it returns the coordinates of the lower-left and upper-right corners.
     *
     * @return A string representation of the MBR.
     */
    @Override
    public String toString() {
        if (!Arrays.equals(low, high)) {
            return "MBR min: " + Arrays.toString(low) + ", max: " + Arrays.toString(high);
        } // For rectangle
        return Arrays.toString(low); // For single point
    }

    /**
     * Calculates the area of intersection between this MBR and another MBR.
     *
     * @param rec The other MBR to calculate the intersection area with.
     * @return The area of intersection.
     */
    public double intersectArea(MBR rec) { // Calculate the area of intersection with another rectangle
        if (isIntersect(rec)) {
            return 0;
        }
        double area = 1;
        for (int i = 0; i < rec.getDimension(); i++) { // Multiply the intersected edges of each dimension
            double l1 = this.low[i];
            double h1 = this.high[i];
            double l2 = rec.low[i];
            double h2 = rec.high[i];

            if (l1 <= l2 && h1 <= h2) {
                area *= (h1 - l1) - (l2 - l1);
            } // Left
            else if (l1 >= l2 && h1 >= h2) {
                area *= (h2 - l2) - (l1 - l2);
            } // Right
            else if (l1 >= l2) {
                area *= h1 - l1;
            } // within
            else {
                area *= h2 - l2;
            } // enclosure
        }
        return area;
    }

    /**
     * Determines if this MBR intersects with another MBR.
     *
     * @param rec The other MBR to check intersection with.
     * @return True if the MBRs intersect, false otherwise.
     * @throws IllegalArgumentException If the input MBR is null or of a different dimension.
     */
    public boolean isIntersect(MBR rec) { // Judge if it's intersect with the targeted rectangle
        if (rec == null) {
            throw new IllegalArgumentException("Rectangle cannot be null.");
        }
        if (rec.getDimension() != getDimension()) {
            throw new IllegalArgumentException("Rectangle must be of same dimension.");
        }
        for (int i = 0; i < getDimension(); i++) {
            if (low[i] > rec.high[i] || high[i] < rec.low[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the square of the minimum distance from a point to this MBR.
     *
     * @param data The coordinates of the point.
     * @return The square of the minimum distance.
     */
    public long getDistance(long[] data) { // Calculate the square of mindist of the point (distance to point o)
        long res = 0;
        for (long datum : data) {
            res += datum;
        }
        return res;
    }

    /**
     * Returns the dimension of the MBR.
     *
     * @return The dimension of the MBR.
     */
    private int getDimension() {
        return low.length;
    }

    /**
     * Determines if this MBR encloses another MBR.
     *
     * @param rec The other MBR to check enclosure with.
     * @return True if this MBR encloses the other MBR, false otherwise.
     * @throws IllegalArgumentException If the input MBR is null or of a different dimension.
     */
    public boolean enclosure(MBR rec) { // Judge if the targeted rectangle is inside it
        if (rec == null) {
            throw new IllegalArgumentException("Rectangle cannot be null.");
        }
        if (rec.getDimension() != getDimension()) {
            throw new IllegalArgumentException("Rectangle must be of same dimension.");
        }
        for (int i = 0; i < getDimension(); i++) {
            if (rec.low[i] < low[i] || rec.high[i] > high[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Compares this MBR with another MBR based on their minimum distances.
     *
     * @param arg0 The other MBR to compare with.
     * @return A negative, zero, or positive value as this MBR is less than, equal to, or greater than the other MBR.
     */
    @Override
    public int compareTo(MBR arg0) { // Compare 2 rectangles by their mindists
        if (arg0 != null) {
            int compare = Long.compare(getDistance(getMin()), getDistance(arg0.getMin()));

            for (int i = 0; i < low.length; i++) {
                if (compare != 0)
                    return compare;
                compare = Long.compare(getMin()[i], arg0.getMin()[i]);
            }
            return compare;
        }
        return 0;
    }

    /**
     * Creates a copy of this MBR.
     *
     * @return A new MBR that is a copy of this MBR.
     */
    @Override
    public MBR clone() {
        return new MBR(this.low, this.high);
    }

    /**
     * Generates a hash code for this MBR.
     *
     * @return A hash code for this MBR.
     */
    @Override
    public int hashCode() {
        int result = Arrays.hashCode(low);
        result = 31 * result + Arrays.hashCode(high);
        return result;
    }
}
