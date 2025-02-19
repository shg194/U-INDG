package myshgs.MyApproaches.IZOrderRTree;

import myshgs.MyApproaches.IZOrderRTree.RTree.*;
import myshgs.Utils;

import java.util.*;


public class ZIBBS {
    private final int C;
    private final int F;
    private final int d;
    private final int Q;
    private Node root;

    /**
     * Constructs a BBS instance with the specified parameters.
     *
     * @param C the capacity of each node
     * @param F the fanout of each directory node
     * @param Q the threshold for querying
     * @param dim the dimensionality of the data
     */
    public ZIBBS(int C, int F, int Q, int dim) {
        this.C = C;
        this.F = F;
        this.Q = Q;
        this.d = dim;
    }

    /**
     * Initializes the R-tree with a set of points.
     *
     * @param points the array of points to load into the R-tree
     */
    public void init(long[][] points) {
        RTree tree = new RTree(C, F, d);
        tree.ZOrderLoad(points);
        root = tree.root;
    }

    /**
     * Checks if a point is dominated by any point in a specified node.
     *
     * @param pre The pointer of starting.
     * @param last The pointer of ending.
     * @param skyline The list of skyline points.
     * @param p The point to check.
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return true if p is dominated by any point in the specified range, otherwise false.
     */
    public boolean SDominate(int pre, int last, List<long[]> skyline, long[] p, long[] count) {
        for (int i = pre; i < last; i++) {
            if (Utils.isDominatedBy(skyline.get(i), p, count))
                return true;
        }
        return false;
    }

    /**
     * Determines if an object is dominated.
     *
     * @param node the node of the object.
     * @param mbr the object
     * @param curNum the current number of skyline points processed
     * @param skyline the list of skyline points
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return true if the MBR is dominated; otherwise false
     */
    public boolean isDominate(Node node, MBR mbr, int curNum, List<long[]> skyline, long[] count) {
        if (curNum < 1000) {
            return !SDominate(0, skyline.size(), skyline, mbr.getMin(), count);
        }

        //Looking for NDG of objects

        Node cur = node;
        Stack<MBR> stack = new Stack<>();
        HashMap<MBR, Node> record = new HashMap<>();

        boolean Threshold = false;
        while (cur != null) {
            RTDirNode parent = (RTDirNode) cur.getParent();
            if (parent != null) {
                int T = parent.skyline[1] - parent.skyline[0];
                if (T > Q || Threshold) {
                    if (!Threshold) {
                        Threshold = true;

                        Node block = parent.getChild(cur.pos);
                        MBR bmbr = parent.datas[cur.pos];

                        count[1]++;
                        int pre = block.skyline[0];
                        int last = block.skyline[1];

                        if (last - pre > 0) {
                            if (Utils.isDominatedBy(bmbr.getMin(), mbr.getMin(), count)) {
                                stack.add(bmbr);
                                record.put(bmbr, block);
                            }
                        }
                    }

                    for (int i = cur.pos - 1; i >= 0; i--) {
                        Node block = parent.getChild(i);
                        MBR bmbr = parent.datas[i];

                        count[1]++;
                        int pre = block.skyline[0];
                        int last = block.skyline[1];
                        if (last - pre > 0) {
                           if (Utils.isDominatedBy(bmbr.getMin(), mbr.getMin(), count)) {
                                stack.add(bmbr);
                                record.put(bmbr, block);
                            }
                        }
                    }
                }
            }
            cur = parent;
            count[1]++;
        }

        while (!stack.isEmpty()) {
            MBR pop = stack.pop();
            Node poll = record.remove(pop);
            count[1]++;

            if (poll instanceof RTDirNode zds) {
                int[] its = zds.skyline;

                if (its[1] - its[0] <= Q) {
                    if (SDominate(its[0], its[1], skyline, mbr.getMin(), count)) {
                        return false;
                    }
                } else {
                    for (int i = poll.getUsedSpace() - 1; i >= 0; i--) {
                        Node block = zds.getChild(i);
                        MBR bmbr = zds.datas[i];

                        count[1]++;
                        int pre = block.skyline[0];
                        int last = block.skyline[1];

                        if (last - pre > 0) {
                          if (Utils.isDominatedBy(bmbr.getMin(), mbr.getMin(), count)) {
                                stack.add(bmbr);
                                record.put(bmbr, block);
                            }
                        }
                    }
                }
            } else {
                RTDataNode n = (RTDataNode) poll;
                if (SDominate(n.skyline[0], n.skyline[1], skyline, mbr.getMin(), count)) {
                    return false;
                }
            }
        }
        return !SDominate(curNum, skyline.size(), skyline, mbr.getMin(), count);
    }

    /**
     * Computes the skyline of the dataset.
     *
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return the list of skyline points
     */
    public List<long[]> skyline(long[] count) {
        List<long[]> skyline = new ArrayList<>();
        Stack<MBR> deque = new Stack<>();
        HashMap<MBR, Node> record = new HashMap<>();

        Set<Node> updatePointer = new HashSet<>();
        int curNum = 0;

        if (root instanceof RTDataNode node) {
            MBR[] data = node.datas;
            int[] its = node.skyline;
            for (int i = 0; i < node.getUsedSpace(); i++) {
                MBR p = data[i];
                if (isDominate(node, p, curNum, skyline, count)) {
                    skyline.add(p.getMin());
                    its[1]++;
                }
            }
            return skyline;
        }

        RTDirNode r = (RTDirNode) root;

        for (int i = r.getUsedSpace() - 1; i >= 0; i--) {
            deque.add(r.datas[i]);
            record.put(r.datas[i], r.getChild(i));
        }

        while (!deque.isEmpty()) {
            MBR pop = deque.pop();
            Node node = record.remove(pop);
            int[] its = node.skyline;
            its[0] = its[1] = skyline.size();
            count[1]++;

            if (isDominate(node, pop, curNum, skyline, count)) { // Rectangle is not dominated by current skyline, continue
                if (node instanceof RTDirNode r2) {
                    for (int i = node.getUsedSpace() - 1; i >= 0; i--) {
                        count[1]++;
                        deque.add(node.datas[i]);
                        record.put(r2.datas[i], r2.getChild(i));
                    }
                } else {
                    MBR[] data = node.datas;

                    for (int i = 0; i < node.getUsedSpace(); i++) {
                        MBR p = data[i];
                        if (isDominate(node, p, curNum, skyline, count)) {
                            skyline.add(p.getMin());
                            its[1]++;
                        }
                    }
                    int num = its[1] - curNum;
                    if (its[1] != its[0]) {
                        Node parent = node.getParent();
                        count[1]++;
                        if (parent != null) {
                            parent.skyline[1] = its[1];
                            updatePointer.add(parent);
                        }

                        if (num > Q) {
                            updatePP(updatePointer,count);
                            curNum = its[1];
                        }
                    }
                }
            }
        }
        updatePP(updatePointer,count);
        return skyline;
    }

    /**
     * Update skyline pointers of the parent node of the node
     * This method gradually updates the properties of each node's parent node by traversing the given node set until all nodes have no parent nodes
     *
     * @param set A collection of nodes that need to be processed
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     */
    public void updatePP(Set<Node> set,long[] count) {
        while (!set.isEmpty()) {
            Set<Node> cur = new HashSet<>();
            for (Node p : set) {
                Node parent = p.getParent();
                count[1]++;
                if (parent != null) {
                    parent.skyline[1] = Math.max(p.skyline[1], parent.skyline[1]);
                    cur.add(parent);
                }
            }
            set = cur;
        }
    }

}