package myshgs.MyApproaches.IQuadTree;

import myshgs.MyApproaches.IQuadTree.QuadTree.*;
import myshgs.Utils;

import java.util.*;

/**
 * The QuadTree class represents an implementation of the quadtree data structure.
 * It is used for efficient skyline querying and management of points in a multi-dimensional space.
 */
public class IQuadTree {
    public Node root;
    private int C;
    private int d;
    private int Q;
    private final int len;

    /**
     * Constructs an instance of QuadTree with specified parameters.
     *
     * @param C The capacity of the node
     * @param Q The threshold for the number of skyline points.
     * @param d The dimension of the space.
     */
    public IQuadTree(int C, int Q, int d) {
        this.d = d;
        this.C = C;
        this.Q = Q;
        this.len = (1 << d) - 1;
    }

    /**
     * Loads points into the quadtree.
     *
     * @param points The array of points to be loaded into the quadtree.
     * @return The root node of the quadtree after loading the points.
     */
    protected Node loadingToB(long[][] points) {
        List<BitSet> data = new ArrayList<>();
        for (long[] p : points) {
            data.add(Utils.fromPtoZ(p));
        }
        data.sort(new Comparator<BitSet>() {
            @Override
            public int compare(BitSet o1, BitSet o2) {
                return -Utils.compare(o1, o2);
            }
        });
        int len = data.get(0).length();
        int t = (len / d) * d;
        len = (len % d == 0) ? t - d : t;

        this.root = new LeafNode(d, -1);
        this.root.setFrom(len);
        for (BitSet a : data) {
            ((LeafNode) this.root).addBitset(a);
        }
        Stack<LeafNode> stack = new Stack<>();
        stack.add((LeafNode) this.root);

        while (!stack.isEmpty()) {
            LeafNode poll = stack.pop();

            ArrayList<BitSet> set = poll.getBitSet();
            int from = poll.getFrom();

            if (poll.getBitSet().size() > C && from >= 0) {
                int size = set.size() - 1;
                if (set.get(0).get(from, from + d).equals(set.get(size).get(from, from + d))) {
                    poll.setFrom(from - d);
                    stack.add(poll);
                } else {

                    BitSet cur = set.get(0).get(from, from + d);
                    int pos = cur.isEmpty() ? 0 : (int) cur.toLongArray()[0];
                    LeafNode zcur = new LeafNode(d, pos);
                    zcur.setFrom(from - d);
                    stack.add(zcur);

                    DirNode k = new DirNode(d, poll.pos);
                    k.setFrom(from);

                    for (int j = 0; j <= size; j++) {
                        BitSet bitSet = set.get(j);
                        if (Utils.compare(cur, bitSet.get(from, from + d)) == 0) {
                            zcur.addBitset(bitSet);
                        } else {
                            k.setChild(pos, zcur);
                            cur = bitSet.get(from, from + d);
                            pos = cur.isEmpty() ? 0 : (int) cur.toLongArray()[0];
                            zcur = new LeafNode(d, pos);
                            zcur.setFrom(from - d);
                            stack.add(zcur);
                            zcur.addBitset(bitSet);
                        }
                    }
                    k.setChild(pos, zcur);
                    if (poll.getParent() == null) {
                        this.root = k;
                    } else
                        poll.getParent().alterChild(poll.pos, k);
                }
            } else {
                poll.clear();
                DirNode cur = poll.getParent();
                while (cur != null) {
                    cur.alterMin(poll.zmbr.min);
                    cur = cur.getParent();
                }
            }
        }
        return this.root;
    }

    /**
     * Initializes the quadtree by loading points into it.
     *
     * @param points The dataset.
     */
    public void init(long[][] points) {
        this.root = loadingToB(points);
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
            if (myshgs.Utils.isDominatedBy(skyline.get(i), p, count))
                return true;
        }
        return false;
    }

    /**
     * Determines if a given object is dominated.
     *
     * @param node The node to evaluate.
     * @param minpt The minimum bounding rectangle of the query point.
     * @param curNum The current skyline number.
     * @param skyline The list of skylining points.
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return true if the node is dominated by the skyline points; otherwise, false.
     */
    public boolean isDominate(Node node, long[] minpt, int curNum, List<long[]> skyline, long[] count) {
        // When the number of query points is less than 1000, use a simple domination check.
        if (curNum < 1000) {
            return !SDominate(0, skyline.size(), skyline, minpt, count);
        }

        Node cur = node;
        Stack<Node> stack = new Stack<>();

        boolean Threshold = false;
        while (cur != null) {
            DirNode parent = cur.getParent();
            if (parent != null) {
                int T = parent.skyline[1] - parent.skyline[0];
                if (T > Q || Threshold) {
                    if (!Threshold) {
                        Threshold = true;

                        Node block = parent.child[cur.pos];

                        count[1]++;
                        int pre = block.skyline[0];
                        int last = block.skyline[1];

                        if (last - pre > 0) {
                            if (myshgs.Utils.isDominatedBy(block.zmbr.min, minpt, count)) {
                                stack.add(block);
                            }
                        }

                    }

                    List<Integer> bit = parent.zmbr.bit;
                    int p = Collections.binarySearch(bit, cur.pos, new Comparator<Integer>() {
                        @Override
                        public int compare(Integer o1, Integer o2) {
                            return Integer.compare(o2, o1);
                        }
                    });

                    for (int i = p + 1; i < bit.size(); i++) {
                        int pos = bit.get(i);
                        if ((pos & cur.pos) == pos) {
                            count[1]++;
                            Node block = parent.child[pos];
                            int pre = block.skyline[0];
                            int last = block.skyline[1];
                            if (last - pre > 0) {
                                if (myshgs.Utils.isDominatedBy(block.zmbr.min, minpt, count)) {
                                    stack.add(block);
                                }
                            }
                        }
                    }
                }
            }
            cur = parent;
            count[1]++;
        }

        while (!stack.isEmpty()) {
            Node pop = stack.pop();
            count[1]++;

            if (pop instanceof DirNode zds) {
                int[] its = zds.skyline;

                if (its[1] - its[0] <= Q) {
                    if (SDominate(its[0], its[1], skyline, minpt, count)) {
                        return false;
                    }
                } else {
                    for (int i : zds.zmbr.bit) {
                        Node zbk = zds.child[i];
                        count[1]++;
                        int pre = zbk.skyline[0];
                        int last = zbk.skyline[1];
                        if (last - pre > 0) {
                            if (myshgs.Utils.isDominatedBy(zbk.zmbr.min, minpt, count)) {
                                stack.add(zbk);
                            }
                        }
                    }
                }
            } else {
                LeafNode n = (LeafNode) pop;
                if (SDominate(n.skyline[0], n.skyline[1], skyline, minpt, count)) {
                    return false;
                }
            }
        }
        return !SDominate(curNum, skyline.size(), skyline, minpt, count);
    }

    /**
     * Calculates the skyline.
     * The skyline is a set of points that are not dominated by any other points in the dataset.
     * This method uses a tree structure to efficiently calculate the skyline.
     *
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return Returns a list containing the points of the skyline.
     */
    public List<long[]> skyline(long[] count) {
        // Initialize the skyline list to store the resulting skyline points
        List<long[]> skyline = new ArrayList<>();
        // Use a stack for depth-first traversal of the tree structure
        Stack<Node> deque = new Stack<>();
        // Use a set to store nodes that need to update the skyline pointers
        Set<Node> updatePointer = new HashSet<>();
        // Initialize the current number of skyline points processed
        int curNum = 0;

        // If the root node is a leaf node, directly process the data in the leaf node
        if (root instanceof LeafNode node) {
            List<long[]> data = node.getData();
            int[] its = node.skyline;
            for (long[] p : data) {
                if (isDominate(node, p, curNum, skyline, count)) {
                    skyline.add(p);
                    its[1]++;
                }
            }
            return skyline;
        }

        // If the root node is a directory node, start traversal from the root node
        DirNode r = (DirNode) root;
        deque.add(r);

        // Traverse each node in the tree
        while (!deque.isEmpty()) {
            Node node = deque.pop();
            ZIMBRA zm = node.zmbr;
            int[] its = node.skyline;
            its[0] = its[1] = skyline.size();
            count[1]++;

            // Check if the current node's rectangle is dominated by the current skyline
            if (isDominate(node, node.zmbr.min, curNum, skyline, count)) {
                // If the node is a directory node, add its child nodes to the stack
                if (node instanceof DirNode zdb) {
                    int size = zm.bit.size();
                    int pi = zm.bit.get(0);
                    if (pi == this.len && zm.bit.get(size - 1) == 0) {
                        count[1]++;
                        pi = 1;
                    } else {
                        pi = 0;
                    }
                    for (int i = pi; i < size; i++) {
                        count[1]++;
                        Node zbk = zdb.child[zm.bit.get(i)];
                        deque.add(zbk);
                    }
                } else {
                    // If the node is a leaf node, process the data in the leaf node
                    List<long[]> data = ((LeafNode) node).getData();
                    for (long[] p : data) {
                        if (isDominate(node, p, curNum, skyline, count)) {
                            skyline.add(p);
                            its[1]++;
                        }
                    }
                    // Update the skyline information of the parent node
                    int num = its[1] - curNum;
                    if (its[1] != its[0]) {
                        Node parent = node.getParent();
                        count[1]++;
                        if (parent != null) {
                            parent.skyline[1] = its[1];
                            updatePointer.add(parent);
                        }
                        // If the number of new points added exceeds the threshold, update the pointers in updatePointer.
                        if (num > Q) {
                            updatePP(updatePointer, count);
                            curNum = its[1];
                        }
                    }
                }
            }
        }
        // Finally, update the pointers in updatePointer.
        updatePP(updatePointer, count);
        return skyline;
    }
    /**
     * Update skyline pointers of the parent node of the node
     * This method gradually updates the properties of each node's parent node by traversing the given node set until all nodes have no parent nodes
     *
     * @param set A collection of nodes that need to be processed
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     */
    public void updatePP(Set<Node> set, long[] count) {
        while (!set.isEmpty()) {
            Set<Node> cur = new HashSet<>();
            for (Node p : set) {
                count[1]++;
                Node parent = p.getParent();
                if (parent != null) {
                    parent.skyline[1] = Math.max(p.skyline[1], parent.skyline[1]);
                    cur.add(parent);
                }
            }
            set = cur;
        }
    }
}
