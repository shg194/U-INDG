package myshgs.MyApproaches.IZSearch.ZBTree;

import java.util.Arrays;
import java.util.BitSet;

/**
 * Abstract class representing a node in the ZBTree.
 * ZBNode manages the storage of RZRegion objects, and provides basic operations such as getting and setting data,
 * as well as manipulating the node's parent and used space.
 */
public abstract class ZBNode {
    // Array storing RZRegion objects, with an extra space for split operations
    private final RZRegion[] datas;
    // Pointer to the parent node
    protected ZBNode parent;
    // Number of RZRegion objects currently stored
    private int usedSpace;
    // Dimensionality of the data
    private final int d;
    // Minimum point in d-dimensional space
    public long[] minpt;
    // Position of the node, used for certain operations
    public int pos;
    // skyline pointers skyline[0] = pointers.starting skyline[1] = pointers.ending
    public int[] skyline;

    /**
     * Constructor for ZBNode.
     * Initializes the node with the given parent, dimensionality, and capacity.
     *
     * @param parent Parent node
     * @param d      Dimensionality of the data
     * @param N      Capacity of the node
     */
    public ZBNode(ZBNode parent, int d, int N) {
        this.datas = new RZRegion[N + 1]; //for spilt
        this.parent = parent;
        this.usedSpace = 0;
        this.pos = 0;
        this.d = d;
        minpt = new long[d];
        Arrays.fill(minpt, Long.MAX_VALUE);
        this.skyline = new int[2];
    }

    /**
     * Gets the RZRegion object at the specified index.
     *
     * @param index Index of the RZRegion object
     * @return RZRegion object at the specified index
     */
    public RZRegion getDatas(int index) {
        return datas[index];
    }

    /**
     * Gets the array of RZRegion objects.
     *
     * @return Array of RZRegion objects
     */
    public RZRegion[] getRZRegion() {
        return this.datas;
    }

    /**
     * Sets the RZRegion object at the specified index.
     *
     * @param index   Index to set the RZRegion object
     * @param region RZRegion object to set
     */
    public void setDatas(int index, RZRegion region) {
        this.datas[index] = region;
    }

    /**
     * Gets the parent node.
     *
     * @return Parent node
     */
    public ZBNode getParent() {
        return parent;
    }

    /**
     * Creates and returns a new RZRegion object representing the current node's region.
     *
     * @return New RZRegion object representing the current node's region
     */
    public RZRegion getCurRzRegion() {
        return new RZRegion(d, getMinzt(), getMaxzt());
    }

    /**
     * Sets the parent node.
     *
     * @param parent New parent node
     */
    public void setParent(ZBNode parent) {
        this.parent = parent;
    }

    /**
     * Gets the current used space.
     *
     * @return Current used space
     */
    public int getUsedSpace() {
        return usedSpace;
    }

    /**
     * Sets the used space.
     *
     * @param usedSpace New used space
     */
    public void setUsedSpace(int usedSpace) {
        this.usedSpace = usedSpace;
    }

    /**
     * Gets the minimum Z-t curve value of the current node.
     *
     * @return Minimum Z-t curve value
     */
    public BitSet getMinzt() {
        if (usedSpace > 0) {
            RZRegion rz = this.getDatas(0);
            return rz.getMinzt();
        }
        return new BitSet();
    }

    /**
     * Gets the maximum Z-t curve value of the current node.
     *
     * @return Maximum Z-t curve value
     */
    public BitSet getMaxzt() {
        if (usedSpace > 0) {
            RZRegion rz = this.getDatas(usedSpace - 1);
            return rz.getMaxzt();
        }
        return new BitSet();
    }

    /**
     * Deletes the RZRegion object at the specified index.
     *
     * @param i Index of the RZRegion object to delete
     */
    public void delete(int i) {
        if (datas[i + 1] != null) {
            System.arraycopy(datas, i + 1, datas, i, usedSpace - i - 1);
            datas[usedSpace - 1] = null;
        } else {
            datas[i] = null;
        }
        usedSpace--;
    }

    /**
     * Returns a string representation of the ZBNode.
     *
     * @return String representation of the ZBNode
     */
    @Override
    public String toString() {
        return "ZBNode{" +
                "datas=" + getCurRzRegion() +
//                ", parent=" + parent +
                ", usedSpace=" + usedSpace +
                '}';
    }
}
