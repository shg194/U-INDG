package myshgs.MyApproaches.IZOrderRTree.RTree;

import myshgs.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * RTree class represents the R-Tree data structure for spatial indexing.
 * It uses the R*-Tree algorithm for node splitting and tree balancing.
 */
public class RTree {
    public Node root;
    private int C = 0; // Capacity of a node
    private int F = 0; // Fanout of a node
    private final int dims; // Dimensionality of the data

    /**
     * Constructs an RTree with specified parameters.
     *
     * @param C     the capacity of nodes in the tree
     * @param F     the fanout of nodes in the tree
     * @param dim   the dimensionality of the data in the tree
     */
    public RTree(int C, int F, int dim) {
        this.C = C;
        this.F = F;
        this.dims = dim;
        root = new RTDataNode(this, null);
    }

    /**
     * Returns the dimensionality of the data in the tree.
     *
     * @return the dimensionality of the data
     */
    public int getDims() {
        return dims;
    }

    /**
     * Sets the root node of the tree.
     *
     * @param root the new root node of the tree
     */
    public void setRoot(Node root) {
        this.root = root;
    }

    /**
     * Returns the capacity of nodes in the tree.
     *
     * @return the capacity of nodes
     */
    public int getCap() {
        return C;
    }

    /**
     * Returns the fanout of nodes in the tree.
     *
     * @return the fanout of nodes
     */
    public int getFanout() {
        return F;
    }

    /**
     * Inserts a new MBR (Minimum Bounding Rectangle) into the R-Tree.
     *
     * @param rec the MBR to be inserted
     * @throws IllegalArgumentException if the MBR is null or its dimensionality does not match the tree's dimensionality
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
     * Loads data into the R-Tree using Z-Order curve.
     *
     * @param points the array of points to be loaded into the tree
     */
    public void ZOrderLoad(long[][] points) {
        ZOrderLoad ZOrderLoad = new ZOrderLoad();
        this.root = ZOrderLoad.Load(this, points, this.C, this.F);
    }

    /**
     * Performs a post-order traversal on the R-Tree and returns all nodes.
     *
     * @param root the root node of the tree to traverse
     * @return a list containing all nodes in the tree
     * @throws IllegalArgumentException if the provided node is null
     */
    public List<Node> traversePost(Node root) { // Acquire all nodes from this tree
        if (root == null) {
            throw new IllegalArgumentException("Node cannot be null.");
        }
        List<Node> list = new ArrayList<>();
        list.add(root);
        if (!root.isLeaf()) {
            for (int i = 0; i < root.usedSpace; i++) {
                list.addAll(traversePost(((RTDirNode) root).getChild(i)));
            }
        }
        return list;
    }

    /**
     * Performs a level-order traversal on the R-Tree.
     *
     * @param root the root node of the tree to traverse
     */
    public void levelOrderTraversal(Node root) {
        if (root == null) {
            return;
        }

        // Use a queue for level-order traversal
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();

            // Process the current node (e.g., print node information or perform other operations)
            System.out.println(currentNode);

            // If it's not a leaf node, add its child nodes to the queue
            if (currentNode instanceof RTDirNode dir) {
                for (int i = 0; i < dir.usedSpace; i++) {
                    Node child = dir.getChild(i);
                    queue.offer(child);
                }
            }
        }
    }
}
