package myshgs.MyApproaches.IZSearch.ZBTree;

/**
 * Represents a data node in the ZBTree structure, primarily storing multi-dimensional data.
 * Inherits from ZBNode.
 */
public class ZBDataNode extends ZBNode {
    // Stores the multi-dimensional data, where each row represents a data record.
    private long[][] data;
    // Represents the number of data records this node can hold.
    private int N;

    /**
     * Constructs a ZBDataNode instance.
     * Initializes the node with the parent node, dimension of data, and the number of data records.
     *
     * @param parent The parent node.
     * @param d      The dimension of the data.
     * @param N      The number of data records this node can hold.
     */
    public ZBDataNode(ZBNode parent, int d, int N) {
        super(parent, d, N);
        this.N = N;
        this.data = new long[N + 1][d];
    }

    /**
     * Returns all the data in this node.
     *
     * @return All the data.
     */
    public long[][] getData() {
        return data;
    }

    /**
     * Returns the data at the specified index.
     *
     * @param index The index of the data.
     * @return The data at the specified index.
     */
    public long[] getData(int index) {
        return data[index];
    }

    /**
     * Adds a new data record to this node.
     * It also updates the minimum bounding box (minpt) and RZRegion for this node.
     *
     * @param data The new data record to add.
     */
    public void addData(long[] data) {
        int space = this.getUsedSpace();
        this.data[space] = data;
        for (int i = 0; i < data.length; i++) {
            this.minpt[i] = Math.min(this.minpt[i], data[i]);
        }
        this.setDatas(space++, new RZRegion(data.length, data));
        this.setUsedSpace(space);
    }

    /**
     * Deletes the data record at the specified index.
     * It shifts all data records after the specified index forward by one position.
     *
     * @param i The index of the data record to delete.
     */
    public void deletes(int i) {
        System.arraycopy(data, i + 1, data, i, this.getUsedSpace() - i - 1);
        delete(i);
    }

    /**
     * Adds data from another node and deletes data from that node.
     * Used for balancing operations in the ZBTree.
     *
     * @param cur      The current node to exchange data with.
     * @param position The position in the current node from which to start the operation.
     */
    public void addAndDelete(ZBNode cur, int position) {
        System.arraycopy(((ZBDataNode) cur).getData(), position, this.data, 0, cur.getUsedSpace() - position);
        System.arraycopy(cur.getRZRegion(), position, this.getRZRegion(), 0, cur.getUsedSpace() - position);
        this.setUsedSpace(cur.getUsedSpace() - position);
        cur.setUsedSpace(position);
    }
}
