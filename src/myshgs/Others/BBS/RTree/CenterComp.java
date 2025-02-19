package myshgs.Others.BBS.RTree;

import java.util.Comparator;

/**
 * The CenterComp class implements the Comparator interface to compare the center points
 * of objects along a specified dimension. This is primarily used for sorting nodes or rectangles
 * in an R-tree data structure.
 */
public class CenterComp implements Comparator<Object> {
    // Dimension for comparison, initialized to -1
    int dim = -1;

    /**
     * Sets the dimension for comparison.
     *
     * @param dim The dimension value
     */
    void setDim(int dim) {
        this.dim = dim;
    }

    /**
     * Compares two objects based on their center point coordinates along the specified dimension.
     * The objects can be instances of MBR or RTNode. For RTNode instances, the node rectangle is used for comparison.
     *
     * @param a The first object to compare
     * @param b The second object to compare
     * @return The result of comparing the center point coordinates of the two objects
     */
    @Override
    public int compare(Object a, Object b) {
        // Define two MBR objects for comparison
        MBR o1, o2;

        // Determine if the objects are instances of MBR; if not, cast them to RTNode and get their node rectangles
        if (a instanceof MBR) {
            o1 = (MBR) a;
            o2 = (MBR) b;
        } else {
            o1 = ((RTNode) a).getNodeRectangle();
            o2 = ((RTNode) b).getNodeRectangle();
        }

        // Calculate the center point coordinates for both objects along the specified dimension
        long c1 = o1.getMax()[dim] + o1.getMin()[dim]; // *0.5
        long c2 = o2.getMax()[dim] + o2.getMin()[dim]; // *0.5

        // Compare the center point coordinates and return the result
        return Long.compare(c1, c2);
    }
}
