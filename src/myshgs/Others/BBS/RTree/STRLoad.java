package myshgs.Others.BBS.RTree;

import java.util.Arrays;

/**
 * STRLoad class provides functionality to load data into an R-tree.
 * It uses the STR (Sort-Tile-Recursive) algorithm to construct the R-tree.
 */
public class STRLoad {
    /**
     * Loads data into the R-tree.
     *
     * @param rtree R-tree instance to store the data
     * @param points Array of points to be inserted into the R-tree
     * @param C Maximum number of entries in a data node
     * @param F Maximum number of entries in a directory node
     * @return Root node of the constructed R-tree
     */
    public RTNode Load(RTree rtree, long[][] points, int C, int F) {
        int depth = 0;
        int N = points.length;
        int d = points[0].length;
        CenterComp cmp = new CenterComp();

        // Create MBRs for each point
        MBR[] list = new MBR[N];
        for (int i = 0; i < N; i++) {
            list[i] = new MBR(points[i]);
        }

        // Sort the MBRs
        sortChunks(list, d, C, cmp);

        // Create initial data nodes
        RTNode[] nodes = new RTNode[(int) Math.ceil(N / (double) C)];
        int posNode = 0;
        RTNode node = null;
        int end = (list.length % C) == 0 || (list.length / C) == 0 ? list.length : ((list.length / C) * C - C);
        for (int i = 0; i < end; i++) {
            if (i % C == 0) {
                node = new RTDataNode(rtree, null);
                nodes[posNode++] = node;
            }
            node.addData(list[i]);
        }
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

        // If only one node, return it as the root
        if (nodes.length == 1) {
            return nodes[0];
        }

        // Create parent directory nodes
        RTDirNode[] parentNodes = null;
        do {
            depth++;
            parentNodes = new RTDirNode[(int) Math.ceil(nodes.length / (double) F)];

            // Sort the nodes
            sortChunks(nodes, d, F, cmp);
            RTDirNode p = null;
            int posParent = 0;
            end = (nodes.length % F) == 0 || (nodes.length / F) == 0 ? nodes.length : ((nodes.length / F) * F - F);
            for (int i = 0; i < end; i++) {
                if (i % F == 0) {
                    p = new RTDirNode(rtree, null, depth);
                    parentNodes[posParent++] = p;
                }
                p.insert(nodes[i]);
            }
            if (end != nodes.length) {
                p = new RTDirNode(rtree, null, depth);
                parentNodes[posParent++] = p;
                int start = end + (nodes.length - end) / 2;
                for (int i = end; i < start; i++) {
                    p.insert(nodes[i]);
                }
                p = new RTDirNode(rtree, null, depth);
                parentNodes[posParent] = p;
                for (int i = start; i < nodes.length; i++) {
                    p.insert(nodes[i]);
                }
            }
            nodes = parentNodes;
        } while (parentNodes.length > 1);
        return parentNodes[0];
    }

    /**
     * Sorts the chunks of entries based on the STR algorithm.
     *
     * @param entries Array of entries to be sorted
     * @param dims Number of dimensions
     * @param F Maximum number of entries in a directory node
     * @param comp Comparator used for sorting
     */
    private void sortChunks(Object[] entries, int dims, int F, CenterComp comp) {
        comp.setDim(0);
        Arrays.sort(entries, comp);
        int nToSplit = entries.length;
        for (int d = 1; d < dims; d++) {
            int nodesPerAxis = (int) Math.pow((double) nToSplit / F, 1.0 / (double) (dims - d + 1));
            comp.setDim(d);
            int chunkSize = (int) Math.ceil(Math.pow(nodesPerAxis, dims - d) * F);
            if (chunkSize < F) {
                break;
            }
            int pos = 0;
            while (pos < entries.length) {
                int end = Math.min(pos + chunkSize, entries.length);
                Arrays.sort(entries, pos, end, comp);
                pos += chunkSize;
            }
            nToSplit /= nodesPerAxis;
        }
    }
}
