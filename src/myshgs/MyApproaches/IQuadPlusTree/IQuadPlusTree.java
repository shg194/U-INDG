package myshgs.MyApproaches.IQuadPlusTree;

import myshgs.MyApproaches.IQuadPlusTree.QuadPlusTree.*;

import java.util.*;

public class IQuadPlusTree {
    public int C;
    public int F;
    public int Q;
    public int d;
    public Node root;

    /**
     * Constructs a new instance of the IQuadPlusTree class.
     *
     * @param C The capacity threshold for nodes, used to determine when to split nodes.
     * @param F The fanout, determining the maximum number of child nodes a directory node can have.
     * @param Q A parameter.
     * @param d The dimensionality of the data points stored in the tree.
     */
    public IQuadPlusTree(int C, int F, int Q, int d) {
        this.C = C;
        this.F = F;
        this.d = d;
        this.Q = Q;
        this.root = new LeafNode(this, 0);
    }

    /**
     * Inserts a new point into the IQuadPlusTree.
     *
     * @param p The point to be inserted, represented as an array of long integers.
     */
    public void insert(long[] p) {
        BitSet set = Utils.fromPtoZ(p);
        LeafNode node = (LeafNode) root.chooseNode(p, set);
        node.insert(p);
    }

    /**
     * Performs bulk loading of points into the QuadPlusTree, optimizing the tree structure for efficient storage and querying.
     *
     * @param points A dataset.
     */
    public void bulkLoading(long[][] points) {
        ArrayList<BitSet> data = new ArrayList<>();
        for (long[] p : points) {
            data.add(Utils.fromPtoZ(p));
        }

        data.sort(new Comparator<BitSet>() {
            @Override
            public int compare(BitSet o1, BitSet o2) {
                return -Utils.compare(o1, o2);
            }
        });

        HashMap<ArrayList<BitSet>, Node> record = new HashMap<>();
        Queue<ArrayList<BitSet>> stack = new ArrayDeque<>();
        stack.add(data);
        record.put(data, this.root);
        this.root.setFrom(data.get(0).length() - 1);

        while (!stack.isEmpty()) {
            ArrayList<BitSet> set = stack.poll();
            Node poll = record.remove(set);
            int from = poll.getFrom();

            if (set.size() > C && from >= 0) {
                int size = set.size() - 1;
                if (set.get(0).get(from) == set.get(size).get(from)) {
                    poll.setFrom(from - 1);
                    DirNode parents = poll.parents;
                    if (parents != null) {
                        BitSet key = parents.key[poll.pos];
                        int length = key.length();
                        key.set(length);
                        key.set(length - 1, set.get(0).get(from));
                    }
                    stack.add(set);
                    record.put(set, poll);
                } else {
                    ArrayList<BitSet> a = new ArrayList<>();
                    myshgs.MyApproaches.IQuadPlusTree.QuadPlusTree.Node la = new LeafNode(this, from - 1);

                    ArrayList<BitSet> b = new ArrayList<>();
                    myshgs.MyApproaches.IQuadPlusTree.QuadPlusTree.Node lb = new LeafNode(this, from - 1);

                    for (int j = 0; j <= size; j++) {
                        if (set.get(j).get(from)) {
                            b.add(set.get(j));
                        } else
                            a.add(set.get(j));
                    }
                    stack.add(a);
                    stack.add(b);

                    record.put(a, la);
                    record.put(b, lb);

                    DirNode parents = poll.parents;
                    if (parents == null) {
                        DirNode k = new DirNode(this, from);
                        k.add(la, lb);
                        this.root = k;
                    } else {
                        if (parents.usedSpace >= F) {
                            DirNode k = new DirNode(this, from);
                            k.add(la, lb);
                            parents.alter(poll.pos, k);
                        } else {
                            parents.alter(poll.pos, la);
                            parents.insert(poll.pos + 1, lb);
                        }
                    }
                }
            } else {
                DirNode cur = poll.parents;
                LeafNode lf = (LeafNode) poll;

                for (BitSet bp : set) {
                    lf.insert(Utils.fromZtoP(bp, d));
                }
                long[] minpt = lf.minpt;

                while (cur != null) {
                    cur.setMinpt(minpt);
                    cur = cur.parents;
                }
            }
        }
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

        // Calculate the NDG of the object
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

                        if (last - pre > 0 && myshgs.Utils.isDominatedBy(block.minpt, minpt, count)) {
                            stack.add(block);
                        }

                    }

                    for (int i = cur.pos - 1; i >= 0; i--) {
                        Node block = parent.child[i];

                        count[1]++;
                        int pre = block.skyline[0];
                        int last = block.skyline[1];
                        if (last - pre > 0 && myshgs.Utils.isDominatedBy(block.minpt, minpt, count)) {
                            stack.add(block);
                        }

                    }
                }
            }
            cur = parent;
            count[1]++;
        }

        //dominance tests
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
                    for (int i = pop.usedSpace - 1; i >= 0; i--) {
                        Node block = zds.child[i];

                        int pre = block.skyline[0];
                        int last = block.skyline[1];
                        count[1]++;
                        if (last - pre > 0 && myshgs.Utils.isDominatedBy(block.minpt, minpt, count)) {
                            stack.add(block);
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
            List<long[]> data = node.data;
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
            int[] its = node.skyline;
            its[0] = its[1] = skyline.size();
            count[1]++;

            // Check if the current node's rectangle is dominated by the current skyline
            if (isDominate(node, node.minpt, curNum, skyline, count)) { // Rectangle is not dominated by current skyline, continue
                // If the node is a directory node, add its child nodes to the stack
                if (node instanceof DirNode r2) {
                    for (int i = node.usedSpace - 1; i >= 0; i--) {
                        count[1]++;
                        deque.add(r2.child[i]);
                    }
                } else {
                    // If the node is a leaf node, process the data in the leaf node
                    List<long[]> data = ((LeafNode) node).data;

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
