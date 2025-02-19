/**
 * Represents an RZRegion, which is a region in d-dimensional space defined by minimum and maximum points,
 * as well as their corresponding Z-order value (minzt and maxzt).
 * This class provides methods to operate on and access the region's properties.
 */
package myshgs.MyApproaches.IZSearch.ZBTree;

import myshgs.MyApproaches.IZSearch.utils;
import myshgs.Utils;

import java.util.BitSet;

public class RZRegion {
    // Dimension of the space
    private int d;
    // Minimum point of the region
    private long[] minpt;
    // Maximum point of the region
    private long[] maxpt;

    // Flag indicating whether it is dominated.
    private boolean isDT;

    // Z-order value of the minimum point
    private BitSet minzt;
    // Z-order value of the maximum point
    private BitSet maxzt;

    /**
     * Constructs an RZRegion object using the minimum and maximum Z-order value.
     *
     * @param d Dimension of the space
     * @param minzt Z-order value of the minimum point
     * @param maxzt Z-order value of the maximum point
     */
    public RZRegion(int d, BitSet minzt, BitSet maxzt) {
        this.d = d;
        this.minzt = (BitSet) minzt.clone();
        this.maxzt = (BitSet) maxzt.clone();
        BitSet[] rzRegion = utils.getRZRegion(minzt, maxzt, d);
        minpt = Utils.fromZtoP(rzRegion[0], d);
        maxpt = Utils.fromZtoP(rzRegion[1], d);
    }

    /**
     * Constructs an RZRegion object using a single point, treating it as both the minimum and maximum point.
     *
     * @param d Dimension of the space
     * @param pt Coordinates of the point
     */
    public RZRegion(int d, long[] pt) {
        this.d = d;
        BitSet zt = Utils.fromPtoZ(pt);
        this.minzt = (BitSet) zt.clone();
        this.maxzt = (BitSet) zt.clone();
        minpt = pt.clone();
        maxpt = pt.clone();
    }

    /**
     * Checks if it is dominated.
     *
     * @return true if it is dominated, otherwise false
     */
    public boolean isDT() {
        return isDT;
    }

    /**
     * Sets whether it is dominated.
     *
     * @param DT true if it is dominated, otherwise false
     */
    public void setDT(boolean DT) {
        isDT = DT;
    }

    /**
     * Gets the dimension of the space.
     *
     * @return Dimension of the space
     */
    public int getD() {
        return d;
    }

    /**
     * Gets the minimum point of the region.
     *
     * @return Minimum point
     */
    public long[] getMinpt() {
        return minpt;
    }

    /**
     * Gets the maximum point of the region.
     *
     * @return Maximum point
     */
    public long[] getMaxpt() {
        return maxpt;
    }

    /**
     * Gets the Z-order value of the minimum point.
     *
     * @return  Z-order value of the minimum point
     */
    public BitSet getMinzt() {
        return minzt;
    }

    /**
     * Gets the Z-order value of the maximum point.
     *
     * @return Z-order value of the maximum point
     */
    public BitSet getMaxzt() {
        return maxzt;
    }

    /**
     * Converts a BitSet to a binary string representation.
     *
     * @param bitSet The BitSet to convert
     * @return Binary string representation
     */
    protected String bitsetToBinaryString(BitSet bitSet) {
        int size = bitSet.length();
        StringBuilder binaryStr = new StringBuilder();
        for (int i = 0; i < size; i++) {
            binaryStr.append(bitSet.get(i) ? '1' : '0');
        }
        return binaryStr.reverse().toString();
    }

    /**
     * Returns a string representation of the RZRegion.
     *
     * @return String representation of the RZRegion
     */
    @Override
    public String toString() {
        return "RZRegion{" +
                "minzt=" + bitsetToBinaryString(minzt) +
//                ",minpt=" + Arrays.toString(minpt) +
                ", maxzt=" + bitsetToBinaryString(maxzt) +
//                ", maxpt=" + Arrays.toString(maxpt) +
                '}';
    }

}
