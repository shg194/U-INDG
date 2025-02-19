package myshgs.MyApproaches.IZOrderRTree.RTree;

import java.util.Arrays;

/**
 * This class is responsible for loading data points into an RTree data structure using Z-order curve.
 * Z-order curve is a space-filling curve that improves the efficiency of multi-dimensional data storage and retrieval.
 */
public class ZOrderLoad {
    /**
     * Loads data points into the RTree using Z-order.
     *
     * @param rtree The RTree instance to load data into.
     * @param points A two-dimensional array of data points, each sub-array represents the coordinates of a point.
     * @param C The capacity of data nodes, determining the maximum number of data points a data node can contain.
     * @param F The capacity of directory nodes, determining the maximum number of child nodes a directory node can contain.
     * @return Returns the root node of the constructed RTree.
     */
    public Node Load(RTree rtree, long[][] points, int C, int F) {
        // Initialize the current level depth and the total number of data points
        int depth = 0;
        int N = points.length;

        // Create an MBR array to store the minimum bounding rectangles of all data points
        MBR[] list = new MBR[N];
        for (int i = 0; i < N; i++) {
            list[i] = new MBR(points[i]);
        }

        // Sort the MBR array to ensure that data points are processed in Z-order
        Arrays.sort(list);

        // Calculate the number of data nodes needed and initialize the data node array
        Node[] nodes = new Node[(int) Math.ceil(N / (double) C)];
        int posNode = 0;
        Node node = null;
        // Determine the end position for evenly distributing data points into data nodes
        int end = (list.length % C) == 0 || (list.length / C) == 0 ? list.length : ((list.length / C) * C - C);
        for (int i = 0; i < end; i++) {
            if (i % C == 0) {
                // Create a new data node and add it to the data node array
                node = new RTDataNode(rtree, null);
                nodes[posNode++] = node;
            }
            // Add the data point to the current data node
            node.addData(list[i]);
        }
        // Handle the remaining data points that do not reach the capacity of a full data node
        if (end != list.length) {
            node = new RTDataNode(rtree, null);
            nodes[posNode++] = node;
            int start = end + (list.length - end) / 2;
            for (int i = end; i < start; i++) {
                node.addData(list[i]);
            }
            node = new RTDataNode(rtree, null);
            nodes[posNode] = node;
            for (int i = start; i < list.length; i++) {
                node.addData(list[i]);
            }
        }

        // If there is only one data node, it is the root node of the RTree
        if (nodes.length == 1) {
            return nodes[0];
        }

        // Directory node array, used to store the parent nodes of the current level of nodes
        RTDirNode[] parentNodes = null;
        do {
            depth++;
            parentNodes = new RTDirNode[(int) Math.ceil(nodes.length / (double) F)];

            RTDirNode p = null;
            int posParent = 0;
            end = (nodes.length % F) == 0 || (nodes.length / F) == 0 ? nodes.length : ((nodes.length / F) * F - F);
            for (int i = 0; i < end; i++) {
                if (i % F == 0) {
                    // Create a new directory node and add it to the directory node array
                    p = new RTDirNode(rtree, null, depth);
                    parentNodes[posParent++] = p;
                }
                // Insert the current node into the directory node
                p.insert(nodes[i]);
            }
            // Handle directory nodes that do not reach full capacity
            if (end != nodes.length) {
                p = new RTDirNode(rtree, null, depth);
                parentNodes[posParent] = p;
                p.pos = posParent++;
                int start = end + (nodes.length - end) / 2;
                for (int i = end; i < start; i++) {
                    p.insert(nodes[i]);
                }
                p = new RTDirNode(rtree, null, depth);
                parentNodes[posParent] = p;
                p.pos = posParent;
                for (int i = start; i < nodes.length; i++) {
                    p.insert(nodes[i]);
                }
            }
            // Update the node array to the directory node array of the current level, preparing for the next level of construction
            nodes = parentNodes;
        } while (parentNodes.length > 1);
        // Return the root node of the constructed RTree
        return parentNodes[0];
    }
}
