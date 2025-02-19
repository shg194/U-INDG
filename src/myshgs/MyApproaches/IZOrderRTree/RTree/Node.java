package myshgs.MyApproaches.IZOrderRTree.RTree;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract class representing a node in the RTree structure.
 * Nodes in the RTree can be either internal nodes (directory nodes) or leaf nodes.
 */
public abstract class Node {
    protected RTree rtree; // Reference to the RTree to which the node belongs
    public int level; // Level of the node in the RTree, 0 for leaf nodes
    public MBR[] datas; // Array storing the MBRs (Minimum Bounding Rectangles) of the data or child nodes
    public int pos; // Position of the node, used for identifying the node's location in the tree
    protected Node parent; // Parent node of the current node
    protected int usedSpace; // How many data are in this node currently
    protected int insertIndex; // The next index to be inserted a new element
    protected int deleteIndex; // The next index to be deleted
    // skyline pointers skyline[0] = pointers.starting skyline[1] = pointers.ending
    public int[] skyline;

    /**
     * Constructor for creating a new Node instance.
     *
     * @param rtree  The RTree to which the node belongs.
     * @param parent The parent node of the current node.
     * @param level  The level of the node in the RTree.
     */
    public Node(RTree rtree, Node parent, int level) {
        this.rtree = rtree;
        this.parent = parent;
        this.level = level;
        this.pos = 0;
        if (this instanceof RTDirNode)
            datas = new MBR[rtree.getFanout() + 1]; // +1 for splitting
        else
            datas = new MBR[rtree.getCap() + 1]; // +1 for splitting
        this.usedSpace = 0;
        this.skyline = new int[2];
    }

    /**
     * Adds a new MBR to the node.
     *
     * @param rec The MBR to be added.
     * @throws IllegalArgumentException If the node is full and cannot accommodate more data.
     */
    public void addData(MBR rec) {
        if (this instanceof RTDirNode) {
            if (usedSpace == rtree.getFanout()) {
                throw new IllegalArgumentException("Node is full.");
            }
        } else {
            if (usedSpace == rtree.getCap()) {
                throw new IllegalArgumentException("Node is full.");
            }
        }
        datas[usedSpace++] = rec;
    }

    /**
     * Returns the parent node of the current node.
     *
     * @return The parent node.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Returns the number of data entries currently used in the node.
     *
     * @return The number of used data entries.
     */
    public int getUsedSpace() {
        return usedSpace;
    }

    /**
     * Sets the number of data entries currently used in the node.
     *
     * @param usedSpace The number of used data entries.
     */
    public void setUsedSpace(int usedSpace) {
        this.usedSpace = usedSpace;
    }

    /**
     * Deletes a data entry at the specified index.
     *
     * @param i The index of the data entry to be deleted.
     */
    public void deleteData(int i) {
        if (datas[i + 1] != null) {
            System.arraycopy(datas, i + 1, datas, i, usedSpace - i - 1);
            datas[usedSpace - 1] = null;
        } else {
            datas[i] = null;
        }
        usedSpace--;
    }

    /**
     * Adjusts the tree structure starting from the current node, mainly handling node deletion and reorganization.
     *
     * @param list A list for collecting nodes that need to be reorganized.
     */
    protected void condenseTree(List<Node> list) {
        if (isRoot()) { // only one child for root, set it to new root
            if (!isLeaf() && usedSpace == 1) {
                RTDirNode root = (RTDirNode) this;
                Node child = root.getChild(0);
                root.children.remove(this);
                child.parent = null;
                rtree.setRoot(child);
            }
        } else {
            Node parent = getParent();
            int min = (int) Math.round(rtree.getFanout() * 0.5); // If the data capacity has reached its minimum
            if (usedSpace < min) {
                parent.deleteData(parent.deleteIndex);
                ((RTDirNode) parent).children.remove(this);
                this.parent = null;
                list.add(this);
            } else {
                parent.datas[parent.deleteIndex] = getNodeRectangle();
            }
            parent.condenseTree(list);
        }
    }

    /**
     * Selects two seeds for node splitting based on the area of the MBRs.
     *
     * @return An array containing the indices of the two seeds.
     */
    protected int[] pickSeeds() {
        double inefficiency = Double.NEGATIVE_INFINITY;
        int i1 = 0, i2 = 0;
        for (int i = 0; i < usedSpace; i++) {
            for (int j = i + 1; j <= usedSpace; j++) {
                MBR rec = datas[i].getUnion(datas[j]);
                double d = rec.getArea() - datas[i].getArea() - datas[j].getArea();
                if (d > inefficiency) {
                    inefficiency = d;
                    i1 = i;
                    i2 = j;
                }
            }
        }
        return new int[]{i1, i2};
    }

    /**
     * Calculates the minimum bounding rectangle that covers all data entries in the node.
     *
     * @return The minimum bounding rectangle.
     */
    public MBR getNodeRectangle() {
        if (usedSpace > 0) {
            MBR[] rec = new MBR[usedSpace];
            System.arraycopy(datas, 0, rec, 0, usedSpace);
            return MBR.getUnion(rec);
        }
        return new MBR(new long[]{0, 0}, new long[]{0, 0});
    }

    /**
     * Returns a string representation of the node.
     *
     * @return The string representation of the node.
     */
    @Override
    public String toString() {
        String res = "{" + this.getNodeRectangle() +
                " , usedSpaces = " + usedSpace +
                " , skyline = " + Arrays.toString(skyline) +
                " , pos = " + pos +
                " }";
        return res;
    }

    /**
     * Checks if the current node is the root node.
     *
     * @return true if the node is the root node, otherwise false.
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Checks if the current node is an index node (non-leaf node).
     *
     * @return true if the node is an index node, otherwise false.
     */
    public boolean isIndex() {
        return level != 0;
    }

    /**
     * Checks if the current node is a leaf node.
     *
     * @return true if the node is a leaf node, otherwise false.
     */
    public boolean isLeaf() {
        return level == 0;
    }

    /**
     * Abstract method for choosing a leaf node for inserting a new data entry.
     *
     * @param rec The MBR of the data entry to be inserted.
     * @return The selected leaf node.
     */
    protected abstract RTDataNode chooseLeaf(MBR rec);

    /**
     * Abstract method for finding a leaf node that contains a specific data entry.
     *
     * @param rec The MBR of the data entry to be found.
     * @return The found leaf node.
     */
    protected abstract RTDataNode findLeaf(MBR rec);

    /**
     * Abstract method for searching for data entries in the leaf nodes that intersect with a specified MBR.
     *
     * @param rec The MBR for searching.
     * @return A list of MBRs that intersect with the specified MBR.
     */
    protected abstract List<MBR> searchLeaf(MBR rec);
}
