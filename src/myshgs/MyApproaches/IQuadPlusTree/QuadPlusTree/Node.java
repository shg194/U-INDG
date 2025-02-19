package myshgs.MyApproaches.IQuadPlusTree.QuadPlusTree;

import myshgs.MyApproaches.IQuadPlusTree.IQuadPlusTree;
import java.util.Arrays;
import java.util.BitSet;

/**
 * Represents a node in the QuadPlusTree structure, holding spatial data and providing methods for skyline querying.
 */
public class Node {
    // Reference to the parent node, for navigating the tree structure.
    public DirNode parents;
    // Indicates the amount of space used by the current node.
    public int usedSpace;
    // Position information of the node, specific usage depends on the context.
    public int pos;
    // Starting point of the node's spatial range, from this value onwards the node's data is valid.
    public int from;
    // Minimum bounding point of the node, used to define the spatial range.
    public long[] minpt;
    // Skyline pointers, skyline[0] points to the start of the skyline, skyline[1] points to the end of the skyline.
    public int[] skyline;
    // Reference to the QuadPlusTree interface, used for interacting with tree operations.
    protected IQuadPlusTree tree;

    /**
     * Constructs a Node instance.
     *
     * @param tree The QuadPlusTree interface instance this node belongs to.
     * @param from The starting point of the node's spatial range.
     */
    public Node(IQuadPlusTree tree, int from) {
        this.tree = tree;
        this.from = from;
        this.usedSpace = 0;
        this.parents = null;
        this.minpt = new long[tree.d];
        Arrays.fill(this.minpt,Long.MAX_VALUE);
        this.skyline = new int[2];
        this.pos = 0;
    }

    /**
     * Gets the parent node of the current node.
     *
     * @return The parent node.
     */
    public DirNode getParent() {
        return this.parents;
    }

    /**
     * Sets the parent node of the current node.
     *
     * @param parent The new parent node.
     */
    public void setParent(DirNode parent) {
        this.parents = parent;
    }

    /**
     * Gets the starting point of the node's spatial range.
     *
     * @return The starting point.
     */
    public int getFrom() {
        return from;
    }

    /**
     * Sets the starting point of the node's spatial range.
     *
     * @param from The new starting point.
     */
    public void setFrom(int from) {
        this.from = from;
    }

    /**
     * Chooses an appropriate child node for a given point and value.
     * This method is intended to be implemented by subclasses to decide the insertion or search path based on specific logic.
     *
     * @param p A point in space.
     * @param value A BitSet representing some properties or values.
     * @return The child node that should be used, returns null if not applicable or not implemented.
     */
    public Node chooseNode(long[] p, BitSet value) {
        return null;
    }
}
