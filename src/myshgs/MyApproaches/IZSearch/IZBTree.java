package myshgs.MyApproaches.IZSearch;

import myshgs.MyApproaches.IZSearch.ZBTree.*;
import myshgs.Utils;

import java.util.*;

public class IZBTree {
    public ZBNode root;
    private int C = 0;
    private int F = 0;
    private int Q = 0;
    private int d = 0;

    /**
     * Constructs an ZBTree instance with the specified parameters.
     *
     * @param C The C parameter, representing the capacity of a node.
     * @param F The F parameter, fanout of a non-root node.
     * @param Q The Q parameter.
     * @param d The dimension of the data.
     *
     * Note: The condition F >= 4 is required because the algorithm relies on this ratio to function correctly.
     */
    public IZBTree(int C, int F, int Q, int d) {
        if (F >= 4) {
            this.C = C;
            this.F = F;
            this.Q = Q;
            this.d = d;
            this.root = new ZBDataNode(null, d, C);
        } else {
            System.out.println("F should >=4 because 3/2=1");
            System.exit(0);
        }
    }

    /**
     * Initializes the ZBTree with a set of points.
     *
     * @param points A dataset.
     */
    public void init(long[][] points) {
        RZLoad load = new RZLoad(d, C, F);
        this.root = load.Loading(points);
    }

    /**
     * Determines if a point p is dominated by any point in the skyline within the specified range.
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
     * Determines if a point p is dominated by any point .
     *
     * @param node The node of the subtree.
     * @param p The point to check.
     * @param curNum The current number of points processed.
     * @param skyline The list of skyline points.
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return true if p is dominated by any point in the subtree, otherwise false.
     */
    public boolean isDominate(ZBNode node, long[] p, int curNum, List<long[]> skyline, long[] count) {
        if (curNum < 1000) {
            return !SDominate(0, skyline.size(), skyline, p, count);
        }

        // Identification NDG

        ZBNode cur = node;
        Stack<RZRegion> stack = new Stack<>();
        HashMap<RZRegion, ZBNode> record = new HashMap<>();

        boolean Threshold = false;
        while (cur != null) {
            ZBDirNode parent = (ZBDirNode) cur.getParent();
            if (parent != null) {
                int T = parent.skyline[1] - parent.skyline[0];
                if (T > Q || Threshold) {
                    if (!Threshold) {
                        Threshold = true;

                        ZBNode block = parent.getChildren(cur.pos);
                        RZRegion bmbr = parent.getDatas(cur.pos);

                        count[1]++;
                        int pre = block.skyline[0];
                        int last = block.skyline[1];

                        if (last - pre > 0) {
                            if (Utils.isDominatedBy(block.minpt, p, count)) {
                                stack.add(bmbr);
                                record.put(bmbr, block);
                            }
                        }
                    }

                    for (int i = cur.pos - 1; i >= 0; i--) {
                        ZBNode block = parent.getChildren(i);
                        RZRegion bmbr = parent.getDatas(i);

                        count[1]++;
                        int pre = block.skyline[0];
                        int last = block.skyline[1];
                        if (last - pre > 0) {
                            if (Utils.isDominatedBy(block.minpt, p, count)) {
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
            RZRegion pop = stack.pop();
            ZBNode poll = record.remove(pop);
            count[1]++;

            if (poll instanceof ZBDirNode zds) {
                int[] its = zds.skyline;

                if (its[1] - its[0] <= Q) {
                    if (SDominate(its[0], its[1], skyline, p, count)) {
                        return false;
                    }
                } else {
                    for (int i = poll.getUsedSpace() - 1; i >= 0; i--) {
                        ZBNode block = zds.getChildren(i);
                        RZRegion number = zds.getDatas(i);

                        count[1]++;
                        int pre = block.skyline[0];
                        int last = block.skyline[1];
                        if (last - pre > 0) {
                            if (Utils.isDominatedBy(block.minpt, p, count)) {
                                stack.add(number);
                                record.put(number, block);
                            }
                        }
                    }
                }
            } else {
                ZBDataNode n = (ZBDataNode) poll;
                if (SDominate(n.skyline[0], n.skyline[1], skyline, p, count)) {
                    return false;
                }
            }
        }
        return !SDominate(curNum, skyline.size(), skyline, p, count);
    }

    /**
     * Computes the skyline .
     *
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return A list of points that form the skyline.
     */

    public List<long[]> skyline(long[] count) {
        List<long[]> skyline = new ArrayList<>();
        Stack<RZRegion> deque = new Stack<>();
        HashMap<RZRegion, ZBNode> record = new HashMap<>();

        Set<ZBNode> updatePointer = new HashSet<>();
        int curNum = 0;

        if (root instanceof ZBDataNode node) {
            int[] its = node.skyline;
            for (int i = 0; i < node.getUsedSpace(); i++) {
                RZRegion p = node.getDatas(i);
                if (isDominate(node, p.getMinpt(), curNum, skyline, count)) {
                    skyline.add(p.getMinpt());
                    its[1]++;
                }
            }
            return skyline;
        }

        ZBDirNode r = (ZBDirNode) root;

        for (int i = r.getUsedSpace() - 1; i >= 0; i--) {
            count[1]++;
            deque.add(r.getDatas(i));
            record.put(r.getDatas(i), r.getChildren(i));
        }

        while (!deque.isEmpty()) {
            RZRegion pop = deque.pop();
            ZBNode node = record.remove(pop);
            int[] its = node.skyline;
            its[0] = its[1] = skyline.size();
            count[1]++;

            if (isDominate(node, node.minpt, curNum, skyline, count)) {
                if (node instanceof ZBDirNode r2) {
                    for (int i = node.getUsedSpace() - 1; i >= 0; i--) {
                        count[1]++;
                        deque.add(node.getDatas(i));
                        record.put(r2.getDatas(i), r2.getChildren(i));
                    }
                } else {
                    for (int i = 0; i < node.getUsedSpace(); i++) {
                        RZRegion p = node.getDatas(i);
                        if (isDominate(node, p.getMinpt(), curNum, skyline, count)) {
                            skyline.add(p.getMinpt());
                            its[1]++;
                        }
                    }
                    int num = its[1] - curNum;
                    if (its[1] != its[0]) {
                        ZBNode parent = node.getParent();
                        count[1]++;
                        if (parent != null) {
                            parent.skyline[1] = its[1];
                            updatePointer.add(parent);
                        }

                        if (num > Q) {
                            updatePP(updatePointer, count);
                            curNum = its[1];
                        }
                    }
                }
            }
        }
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
    public void updatePP(Set<ZBNode> set, long[] count) {
        // 当节点集合不为空时，继续处理
        while (!set.isEmpty()) {
            // 创建一个新的集合，用于存储当前处理轮次中所有节点的父节点
            Set<ZBNode> cur = new HashSet<>();
            // 遍历当前节点集合
            for (ZBNode p : set) {
                // 获取当前节点的父节点
                ZBNode parent = p.getParent();
                // 增加处理计数
                count[1]++;
                // 如果父节点存在，则更新父节点的属性，并将其添加到新的集合中
                if (parent != null) {
                    // 更新父节点的属性为当前节点和父节点属性的较大值
                    parent.skyline[1] = Math.max(p.skyline[1], parent.skyline[1]);
                    // 将父节点添加到新的集合中，以便在下一轮次中处理
                    cur.add(parent);
                }
            }
            // 更新当前节点集为新集合，准备下一轮次的处理
            set = cur;
        }
    }

}
