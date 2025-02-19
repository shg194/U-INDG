package myshgs.MyApproaches.IZSearch.ZBTree;

import myshgs.MyApproaches.IZSearch.utils;
import myshgs.Utils;

import java.util.*;

public class RZLoad {
    // Dimensionality of the data
    private final int d;
    // Capacity of a leaf node
    private final int C;
    // Fanout of a directory node
    private final int F;

    /**
     * Constructor for RZLoad
     * @param d Dimensionality of the data
     * @param C Capacity of a leaf node
     * @param F Fanout of a directory node
     */
    public RZLoad(int d, int C, int F) {
        this.d = d;
        this.C = C;
        this.F = F;
    }

    /**
     * Creates a leaf node based on the current window of data
     * @param window List containing the current window of data
     * @param deque Deque for storing data that has not been used
     * @return Returns the created leaf node
     */
    private ZBNode createNode(List<BitSet> window, Deque<BitSet> deque) {
        int M = (int) Math.round(0.5 * C);
        ZBDataNode leafNode = new ZBDataNode(null, d, C);
        BitSet bitSet = window.get(0);
        BitSet cur = utils.getArea(bitSet, window.get(window.size() - 1), d);
        int position = window.size() - 1;
        boolean flag = false;
        while (position >= M) {
            position--;
            BitSet area = utils.getArea(bitSet, window.get(position), d);
            if (Utils.compare(area, cur) > 0) {
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
     * Merges the current window of nodes into a directory node
     * @param window List containing the current window of nodes
     * @param deque Deque for storing nodes that have not been used
     * @return Returns the created directory node
     */
    private ZBNode merge(List<ZBNode> window, Deque<ZBNode> deque) {
        int M = (int) Math.round(0.4 * F);
        ZBDirNode Node = new ZBDirNode(null, d, F);
        BitSet minzt = window.get(0).getMinzt();
        BitSet cur = utils.getArea(minzt, window.get(window.size() - 1).getMaxzt(), d);

        int position = window.size() - 1;
        boolean flag = false;
        while (position >= M) {
            position--;
            BitSet area = utils.getArea(minzt, window.get(position).getMaxzt(), d);
            if (Utils.compare(cur, area) > 0) {
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
     * Main function for loading data points into a ZBTree
     * @param points Array of data points, each point is a long array
     * @return Returns the root node of the constructed ZBTree
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

        while (target.size() > 1) {
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
        }
        return target.get(0);
    }
}
