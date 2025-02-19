package myshgs.MyApproaches.IZSearch.ZBTree;

/**
 * Represents a directory node in the ZBTree structure, extending the functionality of a basic ZBNode.
 * This class is responsible for managing child nodes and handling operations related to directory nodes.
 */
public class ZBDirNode extends ZBNode {
    protected ZBNode[] children;

    /**
     * Constructs a ZBDirNode instance with the specified parent node, dimension, and capacity.
     *
     * @param parent The parent node of this directory node.
     * @param d The dimension of the data space.
     * @param N The maximum number of children this node can have.
     */
    public ZBDirNode(ZBNode parent, int d, int N) {
        super(parent, d, N);
        this.children = new ZBNode[N + 1];
    }

    /**
     * Returns all child nodes of this directory node.
     *
     * @return An array of child nodes.
     */
    public ZBNode[] getChildren() {
        return children;
    }

    /**
     * Returns the child node at the specified index.
     *
     * @param index The index of the child node to return.
     * @return The child node at the specified index.
     */
    public ZBNode getChildren(int index) {
        return children[index];
    }

    /**
     * Adds a child node to this directory node and updates the necessary information.
     * This method adjusts the minimum bounding rectangle (MBR) of this node based on the added child node.
     *
     * @param rtNode The child node to be added.
     * @param region The region information of the child node.
     */
    public void addChildren(ZBNode rtNode, RZRegion region) {
        long[] data = rtNode.minpt;
        for (int i = 0; i < data.length; i++) {
            this.minpt[i] = Math.min(this.minpt[i], data[i]);
        }
        int space = this.getUsedSpace();
        this.setDatas(space, region);
        this.children[space] = rtNode;
        rtNode.pos = space;
        this.setUsedSpace(++space);
        rtNode.setParent(this);
    }

    /**
     * Deletes the child node at the specified index and shifts the subsequent nodes to fill the gap.
     *
     * @param i The index of the child node to be deleted.
     */
    public void deletes(int i) {
        System.arraycopy(children, i + 1, children, i, this.getUsedSpace() - i - 1);
        delete(i);
    }

    /**
     * Adds nodes from another directory node and deletes a portion of the nodes from that directory.
     * This method is used to handle node splitting and merging operations.
     *
     * @param cur The current directory node from which nodes are to be added and deleted.
     * @param position The starting index for adding and deleting nodes.
     */
    public void addAndDelete(ZBNode cur, int position) {
        System.arraycopy(((ZBDirNode) cur).getChildren(), position, this.children, 0, cur.getUsedSpace() - position);
        System.arraycopy(cur.getRZRegion(), position, this.getRZRegion(), 0, cur.getUsedSpace() - position);
        this.setUsedSpace(cur.getUsedSpace() - position);
        cur.setUsedSpace(position);
        for (int i = 0; i < this.getUsedSpace(); i++) {
            children[i].setParent(this);
        }
    }
}
