package myshgs.Others.ZSearch;

/**
 * Represents a data node in the ZB tree structure, extending the functionality of a basic ZB node.
 * This class is primarily used for storing and managing multi-dimensional data in the ZB tree.
 */
public class ZBDataNode extends ZBNode {
    private long[][] data; // Multi-dimensional data array stored in this node
    private int N;         // Dimension of the data

    /**
     * Constructs a ZBDataNode instance with the specified parent node, data dimension, and size.
     *
     * @param parent The parent node of this node.
     * @param d      The dimension of the data.
     * @param N      The maximum number of data items that this node can store.
     */
    public ZBDataNode(ZBNode parent, int d, int N) {
        super(parent, d, N);
        this.N = N;
        this.data = new long[N + 1][d];
    }

    /**
     * Returns the multi-dimensional data stored in this node.
     *
     * @return A two-dimensional array containing all the data.
     */
    public long[][] getData() {
        return data;
    }

    /**
     * Returns the data at the specified index.
     *
     * @param index The index of the data to retrieve.
     * @return The data at the specified index.
     */
    public long[] getData(int index) {
        return data[index];
    }

    /**
     * Adds a new piece of data to this node.
     * This method first finds the first available position, then stores the data at that position,
     * and updates the usage space and data region information.
     *
     * @param data The data to add, represented as an array.
     */
    public void addData(long[] data) {
        int space = this.getUsedSpace();
        this.data[space] = data;
        this.setDatas(space++, new RZRegion(data.length, data));
        this.setUsedSpace(space);
    }

    /**
     * Deletes the data at the specified index.
     * This method moves subsequent data forward to fill the gap left by the deleted data,
     * and updates the usage space information.
     *
     * @param i The index of the data to delete.
     */
    public void deletes(int i) {
        System.arraycopy(data, i + 1, data, i, this.getUsedSpace() - i - 1);
        delete(i);
    }

    /**
     * Adds data from another node to the current node and deletes the corresponding data from the source node.
     * This method is used for data transfer and deletion operations between nodes during ZB tree rotations or splits.
     *
     * @param cur      The source node, from which data is copied.
     * @param position The starting index in the source node from which to begin copying data.
     */
    public void addAndDelete(ZBNode cur,int position){
        System.arraycopy(((ZBDataNode)cur).getData(),position,this.data,0,cur.getUsedSpace()-position);
        System.arraycopy(cur.getRZRegion(),position,this.getRZRegion(),0,cur.getUsedSpace()-position);
        this.setUsedSpace(cur.getUsedSpace()-position);
        cur.setUsedSpace(position);
    }
}
