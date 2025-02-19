package myshgs.Others.BBS.RTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a directory node in the R-tree, which is a type of node in the R-tree structure.
 * Inherits from RTNode.
 */
public class RTDirNode extends RTNode {
    protected List<RTNode> children; // List of child nodes

    /**
     * Constructor for the RTDirNode.
     * Initializes the node with the given R-tree, parent node, and level.
     *
     * @param rtree The R-tree to which this node belongs
     * @param parent The parent node of this node
     * @param level The level of this node in the R-tree
     */
    public RTDirNode(RTree rtree, RTNode parent, int level) {
        super(rtree, parent, level);
        children = new ArrayList<>();
        datas = new MBR[rtree.getFanout() + 1]; // +1 for splitting
    }

    /**
     * Gets the child node at the specified index.
     *
     * @param index The index of the child node
     * @return The child node at the specified index
     */
    public RTNode getChild(int index) {
        return children.get(index);
    }

    /**
     * Chooses a leaf node to insert a new data rectangle.
     * This is an overridden method from the parent class.
     *
     * @param rec The data rectangle to be inserted
     * @return The chosen leaf node
     */
    @Override
    public RTDataNode chooseLeaf(MBR rec) { // Choose the leaf to be split (data node)
        int index = 0;
        insertIndex = index;
        return getChild(index).chooseLeaf(rec);
    }

    /**
     * Finds the node with the least overlap area with its data.
     * This is a private method used internally by the RTDirNode class.
     *
     * @param rec The data rectangle
     * @return The index of the node with the least overlap area
     */
    private int findOverlap(MBR rec) { // Find the node with the least overlap area with its data
        double overlap = Double.POSITIVE_INFINITY;
        int sel = -1;
        for (int i = 0; i < usedSpace; i++) {
            RTNode node = getChild(i);
            double ol = 0;
            for (int j = 0; j < node.datas.length; j++) {
                ol += rec.intersectArea(node.datas[j]);
            }

            if (ol < overlap) {
                overlap = ol;
                sel = i;
            } else if (ol == overlap) { // If having same overlap areas then choose the smaller rectangle
                double area1 = datas[i].getUnion(rec).getArea() - datas[i].getArea();
                double area2 = datas[sel].getUnion(rec).getArea() - datas[sel].getArea();
                if (area1 == area2) {
                    sel = datas[sel].getArea() <= datas[i].getArea() ? sel : i;
                } else {
                    sel = area1 < area2 ? i : sel;
                }
            }
        }
        return sel;
    }

    /**
     * Finds the node with the largest area enlargement.
     * This is a private method used internally by the RTDirNode class.
     *
     * @param rec The data rectangle
     * @return The index of the node with the largest area enlargement
     */
    private int findEnlarge(MBR rec) { // Find the node with the largest area enlargement
        double area = Double.POSITIVE_INFINITY;
        int sel = -1;
        for (int i = 0; i < usedSpace; i++) {
            double enlarge = datas[i].getUnion(rec).getArea() - datas[i].getArea();
            if (enlarge < area) {
                area = enlarge;
                sel = i;
            } else if (enlarge == area) {
                sel = datas[sel].getArea() < datas[i].getArea() ? sel : i;
            }
        }
        return sel;
    }

    /**
     * Adjusts the tree recursively after insertion.
     *
     * @param n1 The first node to adjust
     * @param n2 The second node to adjust, may be null
     */
    public void adjustTree(RTNode n1, RTNode n2) { // Adjust the tree recursively after insertion
        datas[insertIndex] = n1.getNodeRectangle();
        children.set(insertIndex, n1);
        if (n2 != null) {
            insert(n2);
        } else if (!isRoot()) {
            RTDirNode parent = (RTDirNode) getParent();
            parent.adjustTree(this, null);
        }
    }

    /**
     * Adds a node to this directory node.
     *
     * @param node The node to add
     */
    protected void add(RTNode node) {
        datas[usedSpace++] = node.getNodeRectangle();
        children.add(node);
        node.parent = this;
    }

    /**
     * Inserts a node into this directory node.
     * If the node is full, it will be split.
     *
     * @param node The node to insert
     * @return true if the node was inserted and caused a split, otherwise false
     */
    protected boolean insert(RTNode node) {
        if (usedSpace <= rtree.getFanout()) {
            datas[usedSpace++] = node.getNodeRectangle();
            children.add(node);
            node.parent = this;
            RTDirNode parent = (RTDirNode) getParent();
            if (parent != null) {
                parent.adjustTree(this, null);
            }
            return false;
        } else { // Non-leaf needs to be split
            RTDirNode[] a = splitIndex(node);
            RTDirNode n1 = a[0];
            RTDirNode n2 = a[1];
            if (isRoot()) { // Set a new root
                RTDirNode newRoot = new RTDirNode(rtree, null, level + 1);
                newRoot.addData(n1.getNodeRectangle());
                newRoot.addData(n2.getNodeRectangle());
                newRoot.children.add(n1);
                newRoot.children.add(n2);
                n1.parent = newRoot;
                n2.parent = newRoot;
                rtree.setRoot(newRoot);
            } else {
                RTDirNode p = (RTDirNode) getParent();
                p.adjustTree(n1, n2);
            }
        }
        return true;
    }

    /**
     * Splits an index node.
     * This is a private method used internally by the RTDirNode class.
     *
     * @param node The node that triggered the split
     * @return An array containing the two new nodes after the split
     */
    private RTDirNode[] splitIndex(RTNode node) { // Split index node
        int[][] group = quadraticSplit(node.getNodeRectangle());

        RTDirNode index1 = new RTDirNode(rtree, parent, level);
        RTDirNode index2 = new RTDirNode(rtree, parent, level);
        int[] group1 = group[0];
        int[] group2 = group[1];

        for (int k : group1) {
            index1.addData(datas[k]);
            index1.children.add(this.children.get(k));
            this.children.get(k).parent = index1;
        }
        for (int j : group2) {
            index2.addData(datas[j]);
            index2.children.add(this.children.get(j));
            this.children.get(j).parent = index2;
        }
        return new RTDirNode[]{index1, index2};
    }

    /**
     * Finds a leaf node that encloses the specified data rectangle.
     *
     * @param rec The data rectangle to find
     * @return The found leaf node, or null if not found
     */
    @Override
    protected RTDataNode findLeaf(MBR rec) {
        for (int i = 0; i < usedSpace; i++) {
            if (datas[i].enclosure(rec)) {
                deleteIndex = i;
                RTDataNode leaf = children.get(i).findLeaf(rec);
                if (leaf != null) {
                    return leaf;
                }
            }
        }
        return null;
    }


    /**
     * Performs a quadratic split on a set of MBRs (Minimum Bounding Rectangles)
     * This method is used to split the data in a node when the node overflows, ensuring both groups meet the minimum data requirement.
     *
     * @param rec The MBR to be added, which triggers the split.
     * @return Returns a two-dimensional array containing the split two groups of data indices.
     * @throws IllegalArgumentException If the input MBR is null.
     */
    protected int[][] quadraticSplit(MBR rec) {
        if (rec == null) { throw new IllegalArgumentException("Rectangle cannot be null."); }
        datas[usedSpace] = rec;
        int total = usedSpace + 1;
        int[] mask = new int[total];
        Arrays.fill(mask, 1);

        int c = total / 2 + 1;
        int minSize = (int) Math.round(rtree.getFanout() * 0.5); // Minimum data
        if (minSize < 2) { minSize = 2; }
        int rem = total;
        int[] group1 = new int[c];
        int[] group2 = new int[c];
        int i1 = 0, i2 = 0;
        int[] seed = pickSeeds();
        group1[i1 ++] = seed[0];
        group2[i2 ++] = seed[1];
        rem -= 2;
        mask[group1[0]] = -1;
        mask[group2[0]] = -1;

        while (rem > 0) {
            if (minSize - i1 == rem) { // Fewer data than the minimum value
                for (int i = 0; i < total; i ++) {
                    if (mask[i] != -1) {
                        group1[i1 ++] = i;
                        mask[i] = -1;
                        rem --;
                    }
                }
            } else if (minSize - i2 == rem) { // Fewer data than the minimum value
                for (int i = 0; i < total; i ++) {
                    if (mask[i] != -1) {
                        group2[i2 ++] = i;
                        mask[i] = -1;
                        rem --;
                    }
                }
            } else {
                MBR r1 = datas[group1[0]].clone();
                for (int i = 1; i < i1; i ++) {
                    r1 = r1.getUnion(datas[group1[i]]);
                }
                MBR r2 = datas[group1[0]].clone();
                for (int i = 1; i < i2; i ++) {
                    r2 = r2.getUnion(datas[group2[i]]);
                }

                // Get next splitting index
                double dif = Double.NEGATIVE_INFINITY;
                double areaDiff1 = 0, areaDiff2 = 0;
                int sel = -1;
                for (int i = 0; i < total; i ++) {
                    if (mask[i] != -1) {
                        MBR a = r1.getUnion(datas[i]);
                        areaDiff1 = a.getArea() - r1.getArea();
                        MBR b = r2.getUnion(datas[i]);
                        areaDiff2 = b.getArea() - r2.getArea();
                        if (Math.abs(areaDiff1 - areaDiff2) > dif) {
                            dif = Math.abs(areaDiff1 - areaDiff2);
                            sel = i;
                        }
                    }
                }

                if (areaDiff1 < areaDiff2) { group1[i1 ++] = sel; } // Firstly, area difference
                else if (areaDiff1 > areaDiff2) { group2[i2 ++] = sel; }
                else if (r1.getArea() < r2.getArea()) { group1[i1 ++] = sel; } // Secondly, area
                else if (r1.getArea() > r2.getArea()) { group2[i2 ++] = sel; }
                else if (i1 < i2) { group1[i1 ++] = sel; } // Lastly, amount of data
                else if (i1 > i2) { group2[i2 ++] = sel; }
                else { group1[i1 ++] = sel; }

                mask[sel] = -1;
                rem --;
            }
        }

        int[][] res = new int[2][];
        res[0] = new int[i1];
        res[1] = new int[i2];
        System.arraycopy(group1, 0, res[0], 0, i1);
        System.arraycopy(group2, 0, res[1], 0, i2);
        return res;
    }

    /**
     * Searches for leaf nodes that need to be updated or queried based on the MBR.
     * This method is used to find all leaf nodes that may intersect with a given MBR.
     *
     * @param rec The MBR used for search.
     * @return Returns a list of MBRs of the leaf nodes found.
     */
    @Override
    protected List<MBR> searchLeaf(MBR rec) {
        List<MBR> res = new ArrayList<>();
        for (int i = 0; i < usedSpace; i++) {
            if (rec.getUnion(datas[i]) != null) {
                res.addAll(children.get(i).searchLeaf(rec));
            }
        }
        return res;
    }
}