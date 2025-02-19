package myshgs.Others.ZSearch;

import java.util.BitSet;

/**
 * Abstract class representing a node in the ZB tree.
 * The ZB tree is a data structure used for spatial indexing, and this class provides basic functionalities for node operations.
 */
public abstract class ZBNode {
    // Array storing the region data of this node
    private RZRegion[] datas;
    // Parent node of this node
    protected ZBNode parent;
    // Amount of space used by this node
    private int usedSpace;
    // Dimensionality of the data
    private int d;

    /**
     * Constructor to initialize a ZBNode.
     *
     * @param parent The parent node of this node.
     * @param d      The dimensionality of the data.
     * @param N      The maximum number of child nodes.
     */
    public ZBNode(ZBNode parent, int d, int N) {
        this.datas = new RZRegion[N + 2];
        this.parent = parent;
        this.usedSpace = 0;
        this.d = d;
    }

    /**
     * Get the region data at the specified index.
     *
     * @param index The index of the region data to retrieve.
     * @return The region data at the specified index.
     */
    public RZRegion getDatas(int index) {
        return datas[index];
    }

    /**
     * Get the array of all region data of this node.
     *
     * @return The array of all region data.
     */
    public RZRegion[] getRZRegion() {
        return this.datas;
    }

    /**
     * Set the region data at the specified index.
     *
     * @param index  The index at which to set the region data.
     * @param region The region data to set.
     */
    public void setDatas(int index, RZRegion region) {
        this.datas[index] = region;
    }

    /**
     * Get the parent node of this node.
     *
     * @return The parent node.
     */
    public ZBNode getParent() {
        return parent;
    }

    /**
     * Get the current region of this node.
     *
     * @return The current region.
     */
    public RZRegion getCurRzRegion() {
        return new RZRegion(d, getMinzt(), getMaxzt());
    }

    /**
     * Set the parent node of this node.
     *
     * @param parent The new parent node.
     */
    public void setParent(ZBNode parent) {
        this.parent = parent;
    }

    /**
     * Get the amount of used space in this node.
     *
     * @return The amount of used space.
     */
    public int getUsedSpace() {
        return usedSpace;
    }

    /**
     * Set the amount of used space in this node.
     *
     * @param usedSpace The new amount of used space.
     */
    public void setUsedSpace(int usedSpace) {
        this.usedSpace = usedSpace;
    }

    /**
     * Get the minimum zoning token of the current node.
     *
     * @return The minimum zoning token.
     */
    public BitSet getMinzt() {
        if (usedSpace > 0) {
            RZRegion rz = this.getDatas(0);
            return rz.getMinzt();
        }
        return new BitSet();
    }

    /**
     * Get the maximum zoning token of the current node.
     *
     * @return The maximum zoning token.
     */
    public BitSet getMaxzt() {
        if (usedSpace > 0) {
            RZRegion rz = this.getDatas(usedSpace - 1);
            return rz.getMaxzt();
        }
        return new BitSet();
    }

    /**
     * Delete the region data at the specified index.
     *
     * @param i The index of the region data to delete.
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
     * Generate a string representation of this node.
     *
     * @return A string representation of this node.
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
