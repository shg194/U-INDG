package myshgs.Others.MBR_Oriented.MBRSKY;

import java.util.Comparator;

/**
 * Comparator to sort Multi-dimensional Rectangles (MBRs) based on their center coordinates.
 * This class is primarily used for comparing MBRs' centers in a specified dimension to facilitate sorting.
 */
public class CenterComp implements Comparator<long[]> {
    // Stores the current dimension for comparison, initialized to -1 indicating not set
    int dim = -1;

    /**
     * Sets the dimension for comparison.
     * @param dim The dimension value indicating which center coordinate will be compared.
     */
    void setDim(int dim) {
        this.dim = dim;
    }

    /**
     * Compares the center coordinates of two MBRs in the specified dimension.
     * @param o1 The first MBR's coordinate array.
     * @param o2 The second MBR's coordinate array.
     * @return A negative integer, zero, or a positive integer as the first MBR's center coordinate
     *         is less than, equal to, or greater than the second MBR's center coordinate in the specified dimension.
     */
    @Override
    public int compare(long[] o1, long[] o2) {
        // Retrieve the center coordinate of the first MBR in the specified dimension
        long c1 = o1[dim] ; // *0.5
        // Retrieve the center coordinate of the second MBR in the specified dimension
        long c2 = o2[dim] ; // *0.5
        // Compare the two center coordinates and return the result
        return Long.compare(c1, c2);
    }
}
