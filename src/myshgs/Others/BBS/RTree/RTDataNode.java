// Class for data node
package myshgs.Others.BBS.RTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a data node in the R-Tree.
 */
public class RTDataNode extends RTNode {
    /**
     * Constructs a data node.
     *
     * @param rtree  the R-Tree to which this node belongs
     * @param parent the parent node
     */
    public RTDataNode(RTree rtree, RTNode parent) {
        super(rtree, parent, 0);
    }

    /**
     * Inserts a new MBR (Minimum Bounding Rectangle) into the data node.
     *
     * @param rec the MBR to insert
     * @return true if the insertion is successful
     */
    public boolean insert(MBR rec) {
        if (usedSpace < rtree.getCap()) {
            datas[usedSpace++] = rec;
            RTDirNode parent = (RTDirNode) getParent();
            if (parent != null) {
                parent.adjustTree(this, null);
            }
        } else {
            RTDataNode[] splitNodes = splitLeaf(rec);
            RTDataNode l1 = splitNodes[0];
            RTDataNode l2 = splitNodes[1];
            if (isRoot()) {
                RTDirNode rdir = new RTDirNode(rtree, null, level + 1);
                rtree.setRoot(rdir);
                rdir.addData(l1.getNodeRectangle());
                rdir.addData(l2.getNodeRectangle());
                l1.parent = rdir;
                l2.parent = rdir;
                rdir.children.add(l1);
                rdir.children.add(l2);
            } else {
                RTDirNode parentNode = (RTDirNode) getParent();
                parentNode.adjustTree(l1, l2);
            }
        }
        return true;
    }

    /**
     * Splits the leaf node when the data amount reaches its maximum.
     *
     * @param rec the MBR that triggered the split
     * @return an array containing the two new RTDataNode objects after the split
     */
    public RTDataNode[] splitLeaf(MBR rec) {
        int[][] group = quadraticSplit(rec);

        RTDataNode l1 = new RTDataNode(rtree, parent);
        RTDataNode l2 = new RTDataNode(rtree, parent);
        int[] group1 = null, group2 = null;
        if (group != null) {
            group1 = group[0];
            group2 = group[1];
            for (int j : group1) {
                l1.addData(datas[j]);
            }
            for (int j : group2) {
                l2.addData(datas[j]);
            }
        }
        return new RTDataNode[]{l1, l2};
    }

    /**
     * Chooses a leaf node for inserting a new MBR.
     *
     * @param rec the MBR to insert
     * @return the chosen leaf node
     */
    @Override
    public RTDataNode chooseLeaf(MBR rec) {
        insertIndex = usedSpace;
        return this;
    }

    /**
     * Performs a quadratic split on the data node.
     *
     * @param rec the MBR that triggered the split
     * @return a 2D array representing the two groups after the split
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
        int minSize = (int) Math.round(rtree.getCap() * 0.5);
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
            if (minSize - i1 == rem) {
                for (int i = 0; i < total; i++) {
                    if (mask[i] != -1) {
                        group1[i1++] = i;
                        mask[i] = -1;
                        rem--;
                    }
                }
            } else if (minSize - i2 == rem) {
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
                } else if (areaDiff1 > areaDiff2) {
                    group2[i2++] = sel;
                } else if (r1.getArea() < r2.getArea()) {
                    group1[i1++] = sel;
                } else if (r1.getArea() > r2.getArea()) {
                    group2[i2++] = sel;
                } else if (i1 < i2) {
                    group1[i1++] = sel;
                } else if (i1 > i2) {
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

    /**
     * Returns a string representation of the data node.
     *
     * @return a string representation of the data node
     */
    @Override
    public String toString() {
        StringBuilder res = new StringBuilder("{");
        for (MBR data : datas) {
            res.append(data).append(", ");
        }
        res.append("}");
        return res.toString();
    }

    /**
     * Deletes an MBR from the data node.
     *
     * @param rec the MBR to delete
     * @return the index of the deleted MBR, or -1 if not found
     */
    public int delete(MBR rec) {
        for (int i = 0; i < usedSpace; i++) {
            if (datas[i].equals(rec)) {
                deleteData(i);
                List<RTNode> deleteEntries = new ArrayList<>();
                condenseTree(deleteEntries);
                for (int j = 0; j < deleteEntries.size(); j++) {
                    RTNode node = deleteEntries.get(j);
                    if (node.isLeaf()) {
                        for (int k = 0; k < node.usedSpace; k++) {
                            rtree.insert(node.datas[k]);
                        }
                    } else {
                        List<RTNode> traverseNodes = rtree.traversePost(node);
                        for (int k = 0; k < traverseNodes.size(); k++) {
                            RTNode traverseNode = traverseNodes.get(k);
                            if (traverseNode.isLeaf()) {
                                for (int t = 0; t < traverseNode.usedSpace; t++) {
                                    rtree.insert(traverseNode.datas[t]);
                                }
                            }
                        }
                    }
                }
                return deleteIndex;
            }
        }
        return -1;
    }

    /**
     * Finds a leaf node that encloses the given MBR.
     *
     * @param rec the MBR to find
     * @return the found leaf node, or null if not found
     */
    @Override
    protected RTDataNode findLeaf(MBR rec) {
        for (int i = 0; i < usedSpace; i++) {
            if (datas[i].enclosure(rec)) {
                deleteIndex = i;
                return this;
            }
        }
        return null;
    }

    /**
     * Searches for MBRs in the leaf node that are enclosed by the given MBR.
     *
     * @param rec the MBR to search for
     * @return a list of MBRs that match the search criteria
     */
    @Override
    protected List<MBR> searchLeaf(MBR rec) {
        List<MBR> res = new ArrayList<>();
        for (int i = 0; i < usedSpace; i++) {
            if (rec.enclosure(datas[i])) {
                res.add(datas[i]);
            }
        }
        return res;
    }
}
