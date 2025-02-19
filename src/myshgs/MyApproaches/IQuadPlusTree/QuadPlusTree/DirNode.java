package myshgs.MyApproaches.IQuadPlusTree.QuadPlusTree;

import myshgs.MyApproaches.IQuadPlusTree.IQuadPlusTree;
import myshgs.Utils;

import java.util.Arrays;
import java.util.BitSet;

/**
 * Represents a directory node in the QuadPlusTree structure.
 * Inherits from Node and extends its functionality for directory-specific operations.
 */
public class DirNode extends Node {
    public BitSet[] key;
    public Node[] child;

    /**
     * Constructs a DirNode instance.
     *
     * @param tree The IQuadPlusTree instance this node belongs to.
     * @param from The starting dimension for the node.
     */
    public DirNode(IQuadPlusTree tree, int from) {
        super(tree, from);
        key = new BitSet[tree.F + 1];
        child = new Node[tree.F + 1];
    }

    /**
     * Adds two child nodes to this directory node.
     * This method is used to initialize the first two children with their respective keys.
     *
     * @param a The first child node to add.
     * @param b The second child node to add.
     */
    public void add(Node a, Node b) {
        key[0] = new BitSet();
        key[0].set(1);
        a.pos = 0;
        a.parents = this;
        child[0] = a;

        key[1] = new BitSet();
        key[1].set(0, 2);
        b.pos = 1;
        b.parents = this;
        child[1] = b;

        usedSpace = 2;
    }

    /**
     * Inserts a new child node at the specified index.
     * This method adjusts the keys and positions of existing child nodes to accommodate the new node.
     *
     * @param index The index at which to insert the new child node.
     * @param node The new child node to insert.
     */
    public void insert(int index, Node node) {
        BitSet kset = key[index - 1];
        int len = kset.length();

        BitSet tkey = (BitSet) kset.clone();
        tkey.set(len, true);
        tkey.set(len - 1, true);

        kset.set(len - 1, false);
        kset.set(len, true);

        System.arraycopy(key, index, key, index + 1, usedSpace - index);
        key[index] = tkey;

        for (int i = usedSpace - 1; i >= index; i--) {
            child[i + 1] = child[i];
            child[i + 1].pos = i + 1;
        }

        node.parents = this;
        node.pos = index;
        child[index] = node;
        usedSpace++;
    }

    /**
     * Alters the child node at the specified index.
     * This method updates the reference to the child node but does not change the structure of this directory node.
     *
     * @param index The index of the child node to alter.
     * @param node The new child node to replace the old one.
     */
    public void alter(int index, Node node) {
        node.parents = this;
        node.pos = index;
        child[index] = node;
    }

    /**
     * Updates the minimum point of this directory node.
     * This method adjusts the minpt attribute based on the provided minimum point array.
     *
     * @param minpt The new minimum point array.
     */
    public void setMinpt(long[] minpt) {
        for (int i = 0; i < tree.d; i++) {
            this.minpt[i] = Math.min(minpt[i], this.minpt[i]);
        }
    }

    /**
     * Returns a string representation of this directory node.
     * This method provides a detailed string representation of the node for debugging and logging purposes.
     *
     * @return A string representation of this directory node.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        for (int i = 0; i < usedSpace; i++) {
            sb.append(key[i]).append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        sb.append(" } ");

        return "DirNode{ " +
                ", key = " + sb +
                ", usedSpace = " + usedSpace +
                ", minpt = " + Arrays.toString(minpt) +
                ", pos=" + pos +
                " }";
    }
}
