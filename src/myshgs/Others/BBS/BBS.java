package myshgs.Others.BBS;

import myshgs.Others.BBS.RTree.*;
import myshgs.Utils;

import java.util.*;

public class BBS {
    private final int C;
    private final int F;
    private final int d;
    private RTNode root;

    /**
     * Constructor for the BBS class
     * Initializes the BBS object with given parameters
     *
     * @param C The capacity of the R-tree node
     * @param F The fanout of the R-tree node
     * @param dim The dimensionality of the data
     */
    public BBS(int C, int F, int dim) {
        this.C = C;
        this.F = F;
        this.d = dim;
    }

    /**
     * Initializes the R-tree with a set of points
     *
     * @param points A dataset of points used to initialize the R-tree
     */
    public void init(long[][] points) {
        RTree tree = new RTree(C, F, d);
        tree.STRLoad(points);
        root = tree.root;
    }

    /**
     * Computes the skyline of the R-Tree with BBS
     *
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return Returns the list of skyline points
     */
    public List<long[]> skyline(long[] count) {
        List<long[]> res = new ArrayList<>();
        PriorityQueue<MBR> deque = new PriorityQueue<>();
        HashMap<MBR, RTNode> record = new HashMap<>();

        // If the root is a data node, indicating there's only one node in the tree
        if (root instanceof RTDataNode) {
            deque.addAll(Arrays.asList(root.datas).subList(0, root.getUsedSpace()));
            while (!deque.isEmpty()) {
                MBR poll = deque.poll();
                if (isDominate(res, poll, count)) {
                    res.add(poll.getMax());
                }
            }
            return res;
        }

        RTDirNode r = (RTDirNode) root;
        for (int i = 0; i < r.getUsedSpace(); i++) {
            count[1]++;
            deque.add(r.datas[i]);
            record.put(r.datas[i], r.getChild(i));
        }

        while (!deque.isEmpty()) {
            MBR rec = deque.poll();
            count[1]++;
            if (isDominate(res, rec, count)) {
                if (record.get(rec) != null) {
                    RTNode r1 = record.get(rec);
                    for (int i = 0; i < r1.getUsedSpace(); i++) {
                        count[1]++;
                        if (isDominate(res, r1.datas[i], count)) {
                            deque.add(r1.datas[i]);
                            if (r1 instanceof RTDirNode r2) {
                                record.put(r2.datas[i], r2.getChild(i));
                            } else {
                                record.put(r1.datas[i], null);
                            }
                        }
                    }
                } else {
                    res.add(rec.getMax());
                }
            }
        }
        return res;
    }

    /**
     * Checks if the rectangle is dominated by the given skyline points
     *
     * @param list The list of skyline points
     * @param rec The MBR (Minimum Bounding Rectangle) of the node to check
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return Returns true if the rectangle is not dominated; otherwise returns false
     */
    public boolean isDominate(List<long[]> list, MBR rec, long[] count) {
        for (long[] rectangle : list) {
            if (Utils.isDominatedBy(rectangle, rec.getMin(), count))
                return false;
        }
        return true;
    }
}