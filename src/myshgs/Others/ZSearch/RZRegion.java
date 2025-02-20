package myshgs.Others.ZSearch;

import myshgs.Utils;

import java.util.BitSet;

/**
 * Represents an RZRegion, which is a region in d-dimensional space defined by minimum and maximum points in both
 * Cartesian and Z-order curve representations.
 */
public class RZRegion {
    // Dimensionality of the space
    private int d;
    // Minimum point coordinates in Cartesian space
    private long[] minpt;
    // Maximum point coordinates in Cartesian space
    private long[] maxpt;

    // Minimum point coordinates in Z-order curve representation
    private BitSet minzt;
    // Maximum point coordinates in Z-order curve representation
    private BitSet maxzt;

    /**
     * Constructs an RZRegion object with the specified dimensionality and minimum and maximum points in Z-order curve representation.
     *
     * @param d     the dimensionality of the space
     * @param minzt the minimum point in Z-order curve representation
     * @param maxzt the maximum point in Z-order curve representation
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
     * Constructs an RZRegion object with the specified dimensionality and a single point, treating it as both the minimum and maximum.
     *
     * @param d  the dimensionality of the space
     * @param pt the point coordinates
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
     * Returns the dimensionality of the space.
     *
     * @return the dimensionality of the space
     */
    public int getD() {
        return d;
    }

    /**
     * Returns the minimum point coordinates in Cartesian space.
     *
     * @return the minimum point coordinates
     */
    public long[] getMinpt() {
        return minpt;
    }

    /**
     * Returns the maximum point coordinates in Cartesian space.
     *
     * @return the maximum point coordinates
     */
    public long[] getMaxpt() {
        return maxpt;
    }

    /**
     * Returns the minimum point coordinates in Z-order curve representation.
     *
     * @return the minimum point in Z-order curve representation
     */
    public BitSet getMinzt() {
        return minzt;
    }

    /**
     * Returns the maximum point coordinates in Z-order curve representation.
     *
     * @return the maximum point in Z-order curve representation
     */
    public BitSet getMaxzt() {
        return maxzt;
    }

    /**
     * Converts a BitSet to a binary string representation.
     *
     * @param bitSet the BitSet to convert
     * @return the binary string representation of the BitSet
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
     * Returns a string representation of the RZRegion, including the minimum and maximum points in Z-order curve representation.
     *
     * @return a string representation of the RZRegion
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
