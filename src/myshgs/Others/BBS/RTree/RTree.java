package myshgs.Others.BBS.RTree;

import myshgs.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * RTree class to implement the R-tree data structure for spatial indexing
 */
public class RTree {
    public RTNode root;
    private int C = 0; // Node capacity
    private int F = 0; // Fanout degree
    private final int dims; // Data dimension

    /**
     * Constructor to initialize the RTree object
     *
     * @param C   Node capacity
     * @param F   Fanout degree
     * @param dim Data dimension
     */
    public RTree(int C, int F, int dim) {
        this.C = C;
        this.F = F;
        this.dims = dim;
        root = new RTDataNode(this, null);
    }

    /**
     * Get the data dimension of the RTree
     *
     * @return Data dimension
     */
    public int getDims() {
        return dims;
    }

    /**
     * Set the root node of the RTree
     *
     * @param root Root node
     */
    public void setRoot(RTNode root) {
        this.root = root;
    }

    /**
     * Get the node capacity of the RTree
     *
     * @return Node capacity
     */
    public int getCap() {
        return C;
    }

    /**
     * Get the fanout degree of the RTree
     *
     * @return Fanout degree
     */
    public int getFanout() {
        return F;
    }

    /**
     * Insert an MBR object into the RTree
     *
     * @param rec MBR object to be inserted
     * @throws IllegalArgumentException If the rectangle is null or dimensions do not match, an exception is thrown
     */
    public void insert(MBR rec) {
        if (rec == null) {
            throw new IllegalArgumentException("Rectangle cannot be null.");
        }
        if (rec.getMin().length != getDims()) {
            throw new IllegalArgumentException("Rectangle dimension different than RTree dimension.");
        }
        RTDataNode leaf = root.chooseLeaf(rec);
        leaf.insert(rec);
    }

    /**
     * Load data into the RTree using the STR algorithm
     *
     * @param points Array of points to be loaded
     */
    public void STRLoad(long[][] points) {
        STRLoad strLoad = new STRLoad();
        this.root = strLoad.Load(this, points, this.C, this.F);
    }

    /**
     * Post-order traversal of the RTree to collect all nodes
     *
     * @param root Root node
     * @return List containing all nodes
     * @throws IllegalArgumentException If the root node is null, an exception is thrown
     */
    public List<RTNode> traversePost(RTNode root) {
        if (root == null) {
            throw new IllegalArgumentException("Node cannot be null.");
        }
        List<RTNode> list = new ArrayList<>();
        list.add(root);
        if (!root.isLeaf()) {
            for (int i = 0; i < root.usedSpace; i++) {
                list.addAll(traversePost(((RTDirNode) root).getChild(i)));
            }
        }
        return list;
    }

    /**
     * Level order traversal of the RTree and process each node
     *
     * @param root Root node
     */
    public void levelOrderTraversal(RTNode root) {
        if (root == null) {
            return;
        }

        // Use a queue for level order traversal
        Queue<RTNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            RTNode currentNode = queue.poll();

            // Process the current node (can print node information, or perform other operations)
            System.out.println(currentNode);

            // If it is not a leaf node, add its children to the queue
            if (currentNode instanceof RTDirNode dir) {
                for (int i = 0; i < dir.usedSpace; i++) {
                    RTNode child = dir.getChild(i);
                    queue.offer(child);
                }
            }
        }
    }
}
