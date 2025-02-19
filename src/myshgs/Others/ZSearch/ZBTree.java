package myshgs.Others.ZSearch;

import myshgs.Utils;

import java.util.*;

public class ZBTree {
    public ZBNode root;
    private int C = -1;
    private int F = -1;
    private int d = 0;
    private ZBNode skyline;

    /**
     * Constructs a ZBTree instance with specified parameters.
     *
     * @param C The capacity of a single node.
     * @param F The fan-out of a directory node. Must be greater than or equal to 4 to avoid infinite loops.
     * @param d The dimension of the data.
     */
    public ZBTree(int C, int F, int d) {
        if (F >= 4) {
            this.C = C;
            this.F = F;
            this.d = d;
            this.root = new ZBDataNode(null, d, C);
            this.skyline = new ZBDataNode(null, d, C);
        } else {
            System.out.println("F > 4, otherwise there will be an infinite loop");
            System.exit(0);
        }
    }

    /**
     * Initializes the ZBTree with a set of points.
     *
     * @param points A two-dimensional array of points to initialize the tree.
     */
    public void init(long[][] points) {
        RZLoad load = new RZLoad(d, C, F);
        this.root = load.Loading(points);
    }

    /**
     * Inserts a new point into the ZBTree.
     *
     * @param pointers The pointer array of the point to be inserted.
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     */
    private void insert(long[] pointers, long[] count) {
        ZBNode current = this.skyline;
        while (current instanceof ZBDirNode) {
            current = ((ZBDirNode) current).getChildren(current.getUsedSpace() - 1);
            count[1]++;
        }
        ZBDataNode currentRegion = (ZBDataNode) current;
        if (currentRegion.getUsedSpace() == 0) {
            currentRegion.addData(pointers);
            return;
        }

        // Check if the current region needs to be split due to exceeding capacity
        if (currentRegion.getUsedSpace() >= C) {
            BitSet area = currentRegion.getMinzt();
            int position = currentRegion.getUsedSpace() - 1;
            int M = (int) Math.floor(0.5 * C);
            for (int i = M; i < currentRegion.getUsedSpace() - 1; i++) {
                BitSet area1 = currentRegion.getDatas(i).getMaxzt();
                if (area.length() < area1.length()) {
                    position = i;
                    break;
                }
            }

            ZBDirNode parent = (ZBDirNode) currentRegion.getParent();
            count[1]++;

            ZBDataNode rz = new ZBDataNode(parent, d, C);

            rz.addAndDelete(currentRegion, position + 1);
            rz.addData(pointers);

            if (parent == null) {
                parent = new ZBDirNode(null, d, F);

                parent.addChildren(currentRegion, currentRegion.getCurRzRegion());
                parent.addChildren(rz, rz.getCurRzRegion());

                this.skyline = parent;
            } else {
                parent.setDatas(parent.getUsedSpace() - 1, currentRegion.getCurRzRegion());

                parent.addChildren(rz, rz.getCurRzRegion());

                ZBDirNode curNode = parent;
                // Update regions as necessary
                while (curNode.getUsedSpace() > F) {
                    area = curNode.getMinzt();
                    position = curNode.getUsedSpace() - 2;
                    M = (int) Math.floor(0.5 * F);
                    for (int i = M; i < curNode.getUsedSpace() - 2; i++) {
                        BitSet area1 = curNode.getDatas(i).getMaxzt();
                        if (area.length() < area1.length()) {
                            position = i;
                            break;
                        }
                    }

                    count[1]++;
                    ZBDirNode region = (ZBDirNode) curNode.getParent();

                    ZBDirNode rzx = new ZBDirNode(region, d, F);

                    rzx.addAndDelete(curNode, position + 1);

                    if (region == null) {

                        region = new ZBDirNode(null, d, F);

                        region.addChildren(curNode, curNode.getCurRzRegion());
                        region.addChildren(rzx, rzx.getCurRzRegion());

                        this.skyline = region;

                        break;
                    } else {
                        region.setDatas(region.getUsedSpace() - 1, curNode.getCurRzRegion());
                        region.addChildren(rzx, rzx.getCurRzRegion());
                        curNode = region;
                    }
                }
                while (curNode != null) {
                    int len = curNode.getUsedSpace() - 1;
                    curNode.setDatas(len, curNode.getChildren(len).getCurRzRegion());
                    curNode = (ZBDirNode) curNode.getParent();
                    count[1]++;
                }
            }
        } else {
            currentRegion.addData(pointers);

            ZBDirNode curNode = (ZBDirNode) currentRegion.getParent();
            count[1]++;
            while (curNode != null) {
                curNode.setDatas(curNode.getUsedSpace() - 1, curNode.getChildren(curNode.getUsedSpace() - 1).getCurRzRegion());
                curNode = (ZBDirNode) curNode.getParent();
                count[1]++;
            }
        }
    }

    /**
     * Computes the skyline of the ZBTree.
     *
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return A list of points that form the skyline.
     */
    public List<long[]> skyline(long[] count) {
        ZBNode SRC = this.root;
        List<long[]> skyline = new ArrayList<>();
        if (SRC instanceof ZBDataNode) { // Only one node in the tree
            for (int i = 0; i < SRC.getUsedSpace(); i++) {
                long[] p = ((ZBDataNode) SRC).getData(i);
                if (!Dominate(p, p, count)) {
                    insert(p, count);
                    skyline.add(p);
                }
            }
            return skyline;
        }
        Stack<RZRegion> deque = new Stack<>();
        Map<RZRegion, ZBNode> record = new HashMap<>();

        ZBDirNode r = (ZBDirNode) SRC;
        for (int i = r.getUsedSpace() - 1; i >= 0; i--) { // Include all data from root in the heap
            count[1]++;
            deque.add(r.getDatas(i));
            record.put(r.getDatas(i), r.getChildren(i));
        }

        while (!deque.isEmpty()) {
            RZRegion pop = deque.pop();
            count[1]++;
            if (!Dominate(pop.getMinpt(), pop.getMaxpt(), count)) {
                ZBNode cur = record.get(pop);
                record.remove(pop);
                if (cur instanceof ZBDirNode) {
                    for (int i = cur.getUsedSpace() - 1; i >= 0; i--) {
                        count[1]++;
                        deque.add(cur.getDatas(i));
                        record.put(cur.getDatas(i), ((ZBDirNode) cur).getChildren(i));
                    }
                } else {
                    for (int i = 0; i < cur.getUsedSpace(); i++) {
                        long[] p = ((ZBDataNode) cur).getData(i);
                        if (!Dominate(p, p, count)) {
                            insert(p, count);
                            skyline.add(p);
                        }
                    }
                }
            }
        }
        return skyline;
    }

    /**
     * Determines if a point is dominated by any point in the skyline
     *
     * @param minpt The minimum bounding point of the query region
     * @param maxpt The maximum bounding point of the query region
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return true if there is at least one point in the skyline that dominates the query point; otherwise, returns false
     */
    private Boolean Dominate(long[] minpt, long[] maxpt, long[] count) {
        // Check if the skyline consists of only one node
        if (this.skyline instanceof ZBDataNode) {
            for (int i = 0; i < this.skyline.getUsedSpace(); i++) {
                long[] p = ((ZBDataNode) this.skyline).getData(i);
                // Check if the current point dominates the query point
                if (Utils.isDominatedBy(p, minpt, count))
                    return true;
            }
            return false;
        }

        // Initialize data structures for breadth-first search
        Queue<RZRegion> queue = new ArrayDeque<>();
        Map<RZRegion, ZBNode> record = new HashMap<>();

        // Start from the root node
        ZBDirNode r = (ZBDirNode) this.skyline;
        for (int i = 0; i < r.getUsedSpace(); i++) {
            count[1]++;
            queue.add(r.getDatas(i));
            record.put(r.getDatas(i), r.getChildren(i));
        }
        // Breadth-first search to traverse the tree
        while (!queue.isEmpty()) {
            RZRegion region = queue.poll();
            ZBNode poll = record.remove(region);
            count[1]++;
            if (poll instanceof ZBDirNode rz) {
                for (int i = 0; i < poll.getUsedSpace(); i++) {
                    count[1]++;
                    // Check if the maximum point of the current region dominates the query point
                    if (Utils.isDominatedBy(rz.getDatas(i).getMaxpt(), minpt, count)) {
                        return true;
                    } else if (Utils.isDominatedBy(rz.getDatas(i).getMinpt(), maxpt, count)) {
                        queue.add(rz.getDatas(i));
                        record.put(rz.getDatas(i), rz.getChildren(i));
                    }
                }
            } else {
                for (int i = 0; i < poll.getUsedSpace(); i++) {
                    // Check if the current point dominates the query point
                    if (Utils.isDominatedBy(((ZBDataNode) poll).getData(i), minpt, count))
                        return true;
                }
            }
        }
        return false;
    }
}
