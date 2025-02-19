package myshgs.MyApproaches.IQuadTree.QuadTree;

import java.util.Arrays;

public class DirNode extends Node {
    public Node[] child;

    public DirNode(int d, int pos) {
        super(d,pos);
        zmbr = new ZIMBRA(d);
        child = new Node[1 << d];
    }

    public void setChild(int index, LeafNode zBlock) {
        this.zmbr.bit.add(index);
        zBlock.setParent(this);

        child[index] = zBlock;
    }

    public void alterChild(int index, DirNode zBlock) {
        Node block = child[index];
        zBlock.pos = index;
        zBlock.setNext(block.getNext());
        zBlock.setParent(this);


        child[index] = zBlock;
    }

    public void alterMin(long[] p) {
        long[] min = zmbr.min;
        for (int i = 0; i < min.length; i++) {
            min[i] = Math.min(p[i], min[i]);
        }
    }

    @Override
    public String toString() {
        return "ZBlock{" +
                ", pos =" + pos +
                ", min =" + Arrays.toString(zmbr.min) +
                ", arr :" + zmbr.bit +
                ", skyline = " + Arrays.toString(skyline) +
                '}';
    }


}
