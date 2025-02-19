package myshgs.Others.ZSearch;

import myshgs.Utils;

import java.util.*;

public class RZLoad {
    // Dimensionality of the data
    private final int d;
    // Capacity of leaf nodes
    private final int C;
    // Fanout of directory nodes
    private final int F;

    /**
     * Constructor for RZLoad
     * @param d Dimensionality of the data
     * @param C Capacity of leaf nodes
     * @param F fanout of directory nodes
     */
    public RZLoad(int d, int C, int F) {
        this.d = d;
        this.C = C;
        this.F = F;
    }

    /**
     * Creates a leaf node
     * @param window List of data bitsets
     * @param deque Deque of data bitsets
     * @return Returns a created ZBDataNode object
     */
    private ZBNode createNode(List<BitSet> window, Deque<BitSet> deque) {
        int M = (int) Math.floor(0.5*C);
        ZBDataNode leafNode = new ZBDataNode(null, d, C);
        BitSet bitSet = window.get(0);
        BitSet cur = utils.getArea(bitSet, window.get(window.size() - 1),d);
        int position = window.size()-1;
        boolean flag = false;
        while (position >= M) {
            position--;
            BitSet area = utils.getArea(bitSet, window.get(position),d);
            if (Utils.compare(area,cur) > 0) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            position = window.size() - 1;
        }
        for (int i = 0; i <= position; i++) {
            leafNode.addData(Utils.fromZtoP(window.get(i), d));
        }
        for (int i = window.size() - 1; i >= position + 1; i--) {
            deque.addFirst(window.get(i));
        }
        return leafNode;
    }

    /**
     * Merges nodes into a directory node
     * @param window List of nodes to be merged
     * @param deque Deque of nodes
     * @return Returns a created ZBDirNode object
     */
    private ZBNode merge(List<ZBNode> window, Deque<ZBNode> deque) {
        int M = (int) Math.floor(0.5*F);
        ZBDirNode Node = new ZBDirNode(null, d, F);
        BitSet minzt = window.get(0).getMinzt();
        BitSet cur = utils.getArea(minzt, window.get(window.size() - 1).getMaxzt(),d);

        int position = window.size() - 1;
        boolean flag = false;
        while (position >= M) {
            position--;
            BitSet area = utils.getArea(minzt, window.get(position).getMaxzt(),d);
            if (Utils.compare(cur,area) > 0) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            position = window.size() - 1;
        }
        for (int i = 0; i <= position; i++) {
            Node.addChildren(window.get(i), window.get(i).getCurRzRegion());
        }
        for (int i = window.size() - 1; i >= position + 1; i--) {
            deque.addFirst(window.get(i));
        }
        return Node;
    }

    /**
     * Main function for loading points to build the ZB-tree
     * @param points Array of points
     * @return Returns the root node of the built ZB-tree
     */
    public ZBNode Loading(long[][] points) {
        List<ZBNode> target = new ArrayList<>();
        List<BitSet> data = new ArrayList<>();
        for (long[] p : points) {
            data.add(Utils.fromPtoZ(p));
        }
        data.sort(Utils::compare);

        Deque<BitSet> deque = new ArrayDeque<>(data);

        while (!deque.isEmpty()) {
            List<BitSet> window = new ArrayList<>();
            int len = Math.min(deque.size(), C);
            for (int i = 0; i < len; i++) {
                window.add(deque.pop());
            }

            ZBNode node = createNode(window, deque);
            target.add(node);
        }

        do {
            Deque<ZBNode> deq = new ArrayDeque<>(target);
            target.clear();
            while (!deq.isEmpty()) {
                List<ZBNode> window = new ArrayList<>();
                int len = Math.min(deq.size(), F);
                for (int i = 0; i < len; i++) {
                    window.add(deq.pop());
                }
                ZBNode merge = merge(window, deq);
                target.add(merge);
            }
        } while (target.size() > 1);
        return target.get(0);
    }
}
