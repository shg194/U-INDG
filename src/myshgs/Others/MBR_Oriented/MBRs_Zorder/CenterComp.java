package myshgs.Others.MBR_Oriented.MBRs_Zorder;

import java.util.Comparator;

/**
 * Comparator class to compare MBRs (Minimum Bounding Rectangles) based on their center coordinates.
 * This is primarily used for sorting MBRs along a specific dimension.
 */
public class CenterComp implements Comparator<long[]> {
    // Stores the dimension index for comparison, -1 indicates no dimension has been set
    int dim = -1;

    /**
     * Sets the dimension index for comparison.
     *
     * @param dim Dimension value specifying which dimension of the MBR's center coordinate to compare
     */
    void setDim(int dim) {
        this.dim = dim;
    }

    /**
     * Compares the center coordinates of two MBRs.
     * This method is used for sorting MBRs based on their center coordinates in a specified dimension.
     *
     * @param o1 The first MBR's coordinate array
     * @param o2 The second MBR's coordinate array
     * @return The result of comparing the center coordinates, used for sorting
     */
    @Override
    public int compare(long[] o1, long[] o2) {
        // Calculate the center coordinate of the first MBR in the specified dimension
        long c1 = o1[dim]; // *0.5
        // Calculate the center coordinate of the second MBR in the specified dimension
        long c2 = o2[dim]; // *0.5
        // Compare the two center coordinates and return the result for sorting
        return Long.compare(c1, c2);
    }
}
