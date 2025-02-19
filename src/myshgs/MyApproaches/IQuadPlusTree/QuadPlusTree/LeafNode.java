package myshgs.MyApproaches.IQuadPlusTree.QuadPlusTree;

import myshgs.MyApproaches.IQuadPlusTree.IQuadPlusTree;
import java.util.*;

/**
 * Represents a leaf node in the QuadPlusTree structure.
 * LeafNode is a special type of Node that contains actual data points.
 */
public class LeafNode extends Node {
    public List<long[]> data;

    /**
     * Constructs a LeafNode instance.
     * @param tree The QuadPlusTree this node belongs to.
     * @param from The starting dimension for node splitting.
     */
    public LeafNode(IQuadPlusTree tree, int from) {
        super(tree, from);
        this.data = new ArrayList<>();
    }

    /**
     * In a leaf node, this method simply returns the node itself, as leaf nodes do not have children.
     * @param p The point to be inserted.
     * @param value Unused in this context, reserved for interface compliance.
     * @return Returns the leaf node itself.
     */
    public LeafNode chooseNode(long[] p, BitSet value) {
        return this;
    }

    /**
     * Inserts a point into the leaf node, maintaining the sorted order of the data.
     * @param p The point to be inserted.
     */
    public void insert(long[] p) {
        int pos = Collections.binarySearch(data, p, new MyComparator());
        if (pos < 0) {
            pos = -pos - 1;
        }
        data.add(pos,p);

        // Update the minimum bounding rectangle of the node to encompass the newly inserted point.
        for (int i = 0; i < tree.d; i++) {
            minpt[i] = Math.min(minpt[i], p[i]);
        }
    }

    /**
     * Deletes a point from the leaf node, if it exists.
     * @param p The point to be deleted.
     */
    public void delete(long[] p) {
        int pos = Collections.binarySearch(data, p, new MyComparator());
        if (pos >= 0) {
           data.remove(pos);
        }
    }

    /**
     * Returns a string representation of the LeafNode, including its data points, minimum bounding point, used space, and position.
     * @return A string describing the LeafNode.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        for (long[] datum : data) {
            sb.append(Arrays.toString(datum)).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(" } ");
        return "LeafNode{ " +
                "data = " + sb +
                ", minpt = " + Arrays.toString(minpt) +
                ", usedSpace = " + usedSpace +
                ", pos = " + pos +
                " }";
    }
}
