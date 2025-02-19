package myshgs.MyApproaches.IZOrderRTree.RTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a directory node in the R-Tree structure, extending the basic Node class.
 * This class is responsible for managing child nodes and implementing methods specific to directory nodes.
 */
public class RTDirNode extends Node {
    protected List<Node> children; // List of child nodes

    /**
     * Constructs a new directory node with the specified parameters.
     *
     * @param rtree  The RTree instance this node belongs to
     * @param parent The parent node of this directory node
     * @param level  The level of this node in the RTree hierarchy
     */
    public RTDirNode(RTree rtree, Node parent, int level) {
        super(rtree, parent, level);
        children = new ArrayList<>();
        datas = new MBR[rtree.getFanout() + 1]; // +1 for splitting
    }

    /**
     * Retrieves the child node at the specified index.
     *
     * @param index The index of the child node to retrieve
     * @return The child node at the specified index
     */
    public Node getChild(int index) {
        return children.get(index);
    }

    /**
     * Chooses a leaf node to insert a new data entry.
     *
     * @param rec The MBR (Minimum Bounding Rectangle) of the data entry to insert
     * @return The leaf node chosen for insertion
     */
    @Override
    public RTDataNode chooseLeaf(MBR rec) {
        int index = 0;
        insertIndex = index;
        return getChild(index).chooseLeaf(rec);
    }

    /**
     * Finds the child node with the least overlap area with the given MBR.
     * If there are multiple nodes with the same overlap area, the one with the smaller area is chosen.
     *
     * @param rec The MBR to compare against
     * @return The index of the chosen child node
     */
    private int findOverlap(MBR rec) {
        double overlap = Double.POSITIVE_INFINITY;
        int sel = -1;
        for (int i = 0; i < usedSpace; i++) {
            Node node = getChild(i);
            double ol = 0;
            for (int j = 0; j < node.datas.length; j++) {
                ol += rec.intersectArea(node.datas[j]);
            }

            if (ol < overlap) {
                overlap = ol;
                sel = i;
            } else if (ol == overlap) {
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
     * Finds the child node that requires the smallest area enlargement to accommodate the given MBR.
     * If there are multiple nodes with the same enlargement area, the one with the smaller area is chosen.
     *
     * @param rec The MBR to compare against
     * @return The index of the chosen child node
     */
    private int findEnlarge(MBR rec) {
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
     * Adjusts the tree structure after insertion, ensuring that the R-Tree properties are maintained.
     *
     * @param n1 The first node to adjust
     * @param n2 The second node to adjust, or null if not applicable
     */
    public void adjustTree(Node n1, Node n2) {
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
     * Adds a child node to this directory node.
     *
     * @param node The child node to add
     */
    protected void add(Node node) {
        datas[usedSpace++] = node.getNodeRectangle();
        children.add(node);
        node.parent = this;
    }

    /**
     * Inserts a child node into this directory node.
     * If the node is full, it initiates a split.
     *
     * @param node The child node to insert
     * @return true if a split occurred, false otherwise
     */
    protected boolean insert(Node node) {
        if (usedSpace <= rtree.getFanout()) {
            node.pos = usedSpace;
            datas[usedSpace++] = node.getNodeRectangle();
            children.add(node);
            node.parent = this;
            RTDirNode parent = (RTDirNode) getParent();
            if (parent != null) {
                parent.adjustTree(this, null);
            }
            return false;
        } else {
            RTDirNode[] a = splitIndex(node);
            RTDirNode n1 = a[0];
            RTDirNode n2 = a[1];
            if (isRoot()) {
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
     * Splits an index node into two new nodes based on the given child node.
     *
     * @param node The child node that triggered the split
     * @return An array containing the two new nodes resulting from the split
     */
    private RTDirNode[] splitIndex(Node node) {
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
     * Finds a leaf node that encloses the given MBR.
     *
     * @param rec The MBR to find
     * @return The leaf node that encloses the given MBR, or null if not found
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
     * Performs a quadratic split on the node's entries, including the given MBR.
     *
     * @param rec The MBR to include in the split
     * @return A 2D array representing the two groups resulting from the split
     */

    protected int[][] quadraticSplit(MBR rec) {
        if (rec == null) {
            throw new IllegalArgumentException("Rectangle cannot be null.");
        }
        datas[usedSpace] = rec;
        int total = usedSpace + 1;
        int[] mask = new int[total];
        Arrays.fill(mask, 1);

        int c = total / 2 + 1;
        int minSize = (int) Math.round(rtree.getFanout() * 0.5); // Minimum data
        if (minSize < 2) {
            minSize = 2;
        }
        int rem = total;
        int[] group1 = new int[c];
        int[] group2 = new int[c];
        int i1 = 0, i2 = 0;
        int[] seed = pickSeeds();
        group1[i1++] = seed[0];
        group2[i2++] = seed[1];
        rem -= 2;
        mask[group1[0]] = -1;
        mask[group2[0]] = -1;

        while (rem > 0) {
            if (minSize - i1 == rem) { // Fewer data than the minimum value
                for (int i = 0; i < total; i++) {
                    if (mask[i] != -1) {
                        group1[i1++] = i;
                        mask[i] = -1;
                        rem--;
                    }
                }
            } else if (minSize - i2 == rem) { // Fewer data than the minimum value
                for (int i = 0; i < total; i++) {
                    if (mask[i] != -1) {
                        group2[i2++] = i;
                        mask[i] = -1;
                        rem--;
                    }
                }
            } else {
                MBR r1 = datas[group1[0]].clone();
                for (int i = 1; i < i1; i++) {
                    r1 = r1.getUnion(datas[group1[i]]);
                }
                MBR r2 = datas[group1[0]].clone();
                for (int i = 1; i < i2; i++) {
                    r2 = r2.getUnion(datas[group2[i]]);
                }

                // Get next splitting index
                double dif = Double.NEGATIVE_INFINITY;
                double areaDiff1 = 0, areaDiff2 = 0;
                int sel = -1;
                for (int i = 0; i < total; i++) {
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

                if (areaDiff1 < areaDiff2) {
                    group1[i1++] = sel;
                } // Firstly, area difference
                else if (areaDiff1 > areaDiff2) {
                    group2[i2++] = sel;
                } else if (r1.getArea() < r2.getArea()) {
                    group1[i1++] = sel;
                } // Secondly, area
                else if (r1.getArea() > r2.getArea()) {
                    group2[i2++] = sel;
                } else if (i1 < i2) {
                    group1[i1++] = sel;
                } // Lastly, amount of data
                else if (i1 > i2) {
                    group2[i2++] = sel;
                } else {
                    group1[i1++] = sel;
                }

                mask[sel] = -1;
                rem--;
            }
        }

        int[][] res = new int[2][];
        res[0] = new int[i1];
        res[1] = new int[i2];
        System.arraycopy(group1, 0, res[0], 0, i1);
        System.arraycopy(group2, 0, res[1], 0, i2);
        return res;
    }

    @Override
    /**
     * Search for leaf node MBRs that overlap with the specified MBR in the current node
     *
     * @param rec The MBR to search for overlaps with leaf nodes
     * @return A list of MBRs that overlap with the specified MBR
     */
    protected List<MBR> searchLeaf(MBR rec) {
        // Initialize an empty list to store the search results
        List<MBR> res = new ArrayList<>();
        // Iterate through all the data entries in the current node
        for (int i = 0; i < usedSpace; i++) {
            // Check if the specified MBR overlaps with the current data entry's MBR
            if (rec.getUnion(datas[i]) != null) {
                // If there is an overlap, recursively search the child nodes of the current node and add the results to the list of results
                res.addAll(children.get(i).searchLeaf(rec));
            }
        }
        // Return the list of results
        return res;
    }
}