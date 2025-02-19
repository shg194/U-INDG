package myshgs.MyApproaches.IQuadTree.QuadTree;

public abstract class Node {
    public ZIMBRA zmbr;
    private DirNode parent = null;
    private Node next;
    private int from;
    // skyline pointers skyline[0] = pointers.starting skyline[1] = pointers.ending
    public int[] skyline;
    public int pos;

    public Node(int d, int pos) {
        zmbr = new ZIMBRA(d);
        this.from = -1;
        this.skyline = new int[2];
        this.pos = pos;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }



    public DirNode getParent() {
        return parent;
    }

    public void setParent(DirNode parent) {
        this.parent = parent;
    }

    public Node getNext() {
        return this.next;
    }

    public void setNext(Node next) {
        this.next = next;
    }
}
