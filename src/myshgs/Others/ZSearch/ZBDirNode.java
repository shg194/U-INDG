package myshgs.Others.ZSearch;

/**
 * Represents a directory node in the ZB tree structure, extending the functionality of a basic ZBNode.
 */
public class ZBDirNode extends ZBNode {
    protected ZBNode[] children; // Array to store child nodes

    /**
     * Constructs a ZBDirNode instance with the specified parent, dimension, and capacity.
     *
     * @param parent The parent node of this node
     * @param d The dimension of the space in which this node exists
     * @param N The maximum number of children this node can have
     */
    public ZBDirNode(ZBNode parent, int d, int N) {
        super(parent, d, N);
        this.children = new ZBNode[N+1]; // Initialize the children array with the specified capacity
    }

    /**
     * Returns the array of child nodes.
     *
     * @return An array of ZBNode objects representing the children of this node
     */
    public ZBNode[] getChildren() {
        return children;
    }

    /**
     * Returns the child node at the specified index.
     *
     * @param index The index of the child node to retrieve
     * @return The ZBNode at the specified index
     */
    public ZBNode getChildren(int index) {
        return children[index];
    }

    /**
     * Adds a child node to this node at the next available position and updates relevant data.
     *
     * @param rtNode The ZBNode to be added as a child
     * @param region The region associated with the new child node
     */
    public void addChildren(ZBNode rtNode, RZRegion region) {
        int space = this.getUsedSpace(); // Get the current used space to determine the insertion position
        this.setDatas(space, region); // Update the data at the insertion position
        this.children[space] = rtNode; // Add the new child node
        this.setUsedSpace(++space); // Update the used space
        rtNode.setParent(this); // Set the parent of the new child node to this node
    }

    /**
     * Deletes a child node at the specified index and shifts all subsequent nodes to fill the gap.
     *
     * @param i The index of the child node to be deleted
     */
    public void deletes(int i) {
        System.arraycopy(children, i + 1, children, i, this.getUsedSpace() - i - 1); // Shift nodes to overwrite the deleted node
        delete(i); // Perform additional deletion tasks
    }

    /**
     * Merges data from another node into this node and performs necessary deletions and additions.
     *
     * @param cur The current node from which data is to be merged
     * @param position The position in the current node from which to start merging
     */
    public void addAndDelete(ZBNode cur, int position) {
        // Copy children and regions from the current node to this node, starting from the specified position
        System.arraycopy(((ZBDirNode)cur).getChildren(), position, this.children, 0, cur.getUsedSpace() - position);
        System.arraycopy(cur.getRZRegion(), position, this.getRZRegion(), 0, cur.getUsedSpace() - position);
        this.setUsedSpace(cur.getUsedSpace() - position); // Update the used space of this node
        cur.setUsedSpace(position); // Update the used space of the current node
        for (int i = 0; i < this.getUsedSpace(); i++) {
            children[i].setParent(this); // Update the parent reference of each child node
        }
    }
}
