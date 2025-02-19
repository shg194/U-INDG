package myshgs.Others.BBS.RTree;

import java.util.List;

/**
 * Abstract class representing a node in the R-Tree.
 */
public abstract class RTNode {
    protected RTree rtree; // The R-Tree to which this node belongs
    public int level; // The level of this node in the R-Tree
    public MBR[] datas; // Array storing the MBRs of the data or child nodes
    protected RTNode parent; // The parent node of this node
    protected int usedSpace; // How many data are in this node currently
    protected int insertIndex; // The next index to be inserted a new element
    protected int deleteIndex; // The next index to be deleted

    /**
     * Constructor for creating a new RTNode.
     *
     * @param rtree  The R-Tree to which this node belongs
     * @param parent The parent node of this node
     * @param level  The level of this node in the R-Tree
     */
    public RTNode(RTree rtree, RTNode parent, int level) {
        this.rtree = rtree;
        this.parent = parent;
        this.level = level;
        if (this instanceof RTDirNode)
            datas = new MBR[rtree.getFanout() + 1]; // +1 for splitting
        else
            datas = new MBR[rtree.getCap() + 1]; // +1 for splitting
        usedSpace = 0;
    }

    /**
     * Adds an MBR to this node.
     *
     * @param rec The MBR to be added
     * @throws IllegalArgumentException If the node is full
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
     * Returns the parent node of this node.
     *
     * @return The parent node
     */
    public RTNode getParent() {
        return parent;
    }

    /**
     * Returns the number of data elements currently in this node.
     *
     * @return The number of data elements
     */
    public int getUsedSpace() {
        return usedSpace;
    }

    /**
     * Sets the number of data elements currently in this node.
     *
     * @param usedSpace The number of data elements
     */
    public void setUsedSpace(int usedSpace) {
        this.usedSpace = usedSpace;
    }

    /**
     * Deletes an MBR from this node.
     *
     * @param i The index of the MBR to be deleted
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
     * Reorganizes the tree structure starting from this node.
     *
     * @param list A list for collecting nodes that need further processing
     */
    protected void condenseTree(List<RTNode> list) {
        if (isRoot()) { // only one child for root, set it to new root
            if (!isLeaf() && usedSpace == 1) {
                RTDirNode root = (RTDirNode) this;
                RTNode child = root.getChild(0);
                root.children.remove(this);
                child.parent = null;
                rtree.setRoot(child);
            }
        } else {
            RTNode parent = getParent();
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

    // Split the node into two depends on the area, stop when one group reaches the minimum datas
    // Divide data to two groups by making their corresponding rectangles have larger difference of area

    // Calculate the area of U - R1 - R2, where U is the union rectangle of R1 and R2
    // Pick the 2 rectangles with the largest area of U - R1 - R2 as seeds
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
     * Returns the minimum bounding rectangle that covers all data in this node.
     *
     * @return The minimum bounding rectangle
     */
    public MBR getNodeRectangle() {
        if (usedSpace > 0) {
            MBR[] rec = new MBR[usedSpace];
            System.arraycopy(datas, 0, rec, 0, usedSpace);
            return MBR.getUnion(rec);
        }
        return new MBR(new long[]{0, 0}, new long[]{0, 0});
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("{");
        res.append(this.getNodeRectangle());
//        for (MBR data : datas) {
//            res.append(data).append(", ");
//        }
        res.append("}");
        return res.toString();
    }

    /**
     * Checks if this node is the root node.
     *
     * @return true if this node is the root node, otherwise false
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Checks if this node is an index node.
     *
     * @return true if this node is an index node, otherwise false
     */
    public boolean isIndex() {
        return level != 0;
    }

    /**
     * Checks if this node is a leaf node.
     *
     * @return true if this node is a leaf node, otherwise false
     */
    public boolean isLeaf() {
        return level == 0;
    }

    /**
     * Abstract method for choosing a leaf node.
     *
     * @param rec The MBR for searching
     * @return The chosen leaf node
     */
    protected abstract RTDataNode chooseLeaf(MBR rec);

    /**
     * Abstract method for finding a leaf node.
     *
     * @param rec The MBR for searching
     * @return The found leaf node
     */
    protected abstract RTDataNode findLeaf(MBR rec);

    /**
     * Abstract method for searching leaf nodes.
     *
     * @param rec The MBR for searching
     * @return A list of matching leaf nodes
     */
    protected abstract List<MBR> searchLeaf(MBR rec);
}
