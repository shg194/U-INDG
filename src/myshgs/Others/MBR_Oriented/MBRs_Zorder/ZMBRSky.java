package myshgs.Others.MBR_Oriented.MBRs_Zorder;

import myshgs.Utils;

import java.util.*;

public class ZMBRSky {
    // Dimensionality
    private final int d;
    // Maximum number of data points in each MBR
    private int C;
    // Collection of all points
    public long[][] points;
    // Array of Minimum Bounding Rectangles (MBRs)
    private MBR[] MBRs;
    // Fanout, the maximum number of child nodes an internal node can have
    private int Fanont;
    // Map storing MBRs and their corresponding dominance sets
    private HashMap<MBR, ArrayList<MBR>> dgMap;

    /**
     * Constructor for initializing the ZMBRSky object.
     *
     * @param C      Maximum number of data points in each MBR
     * @param Fanout Fanout, the maximum number of child nodes an internal node can have
     * @param d      Dimensionality
     */
    public ZMBRSky(int C, int Fanout, int d) {
        this.d = d;
        this.Fanont = Fanout;
        this.C = C;
    }

    /**
     * Initializes the MBRs based on the provided points.
     *
     * @param points Collection of all points
     */
    public void init(long[][] points) {
        this.points = points;
        if (Fanont != -1) {
            // Calculate C based on fanout and dimensionality
            int N = (int) Math.ceil(Math.pow((points.length) / (double) (this.Fanont), 1.0 / d));
            this.C = (int) Math.ceil(Math.pow(N, d));
        }

        // Group points by Z-order and construct MBRs
        this.MBRs = getZOrederMBR(points, C);
    }

    /**
     * Creates a new MBR node.
     *
     * @param window List of points in the current window
     * @param deque  Deque containing unprocessed points
     * @return The newly created MBR node
     */
    private MBR createNode(List<BitSet> window, Deque<BitSet> deque) {
        int M = (int) Math.floor(0.5 * C);
        MBR node = new MBR(d, C);
        BitSet bitSet = window.get(0);
        BitSet cur = utils.getArea(bitSet, window.get(window.size() - 1), d);
        int position = window.size() - 1;
        boolean flag = false;
        while (position >= M) {
            position--;
            BitSet area = utils.getArea(bitSet, window.get(position), d);
            if (Utils.compare(area, cur) > 0) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            position = window.size() - 1;
        }
        for (int i = 0; i <= position; i++) {
            node.addData(Utils.fromZtoP(window.get(i), d));
        }
        for (int i = window.size() - 1; i >= position + 1; i--) {
            deque.addFirst(window.get(i));
        }
        return node;
    }

    /**
     * MBRs by Z-order and constructs an array of MBRs.
     *
     * @param points Collection of all points
     * @param C      Maximum number of data points in each MBR
     * @return Array of constructed MBRs
     */
    private MBR[] getZOrederMBR(long[][] points, int C) {
        List<MBR> target = new ArrayList<>();
        List<BitSet> data = new ArrayList<>();
        for (long[] p : points) {
            data.add(Utils.fromPtoZ(p));
        }
        data.sort(Utils::compare);

        Deque<BitSet> deque = new ArrayDeque<>(data);

        while (!deque.isEmpty()) {
            List<BitSet> window = new ArrayList<>();
            int len = Math.min(deque.size(), C);
            for (int i = 0; i < len; i++) {
                window.add(deque.pop());
            }

            MBR node = createNode(window, deque);
            target.add(node);
        }
        return target.toArray(new MBR[0]);
    }

    /**
     * Checks if an object is dominated by any MBR.
     *
     * @param list   List of MBRs
     * @param p      Point to check
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return True if the point is dominated, otherwise false
     */
    private boolean isDominated(ArrayList<MBR> list, long[] p, long[] count) {
        for (MBR mbr : list) {
            count[1]++;
            if (!mbr.isDominate) {
                if (Utils.compare(mbr.minpt, Utils.fromPtoZ(p)) >= 0)
                    break;

                for (int i = 0; i < mbr.usedSpace; i++) {
                    int dtDev = Utils.DtDev(mbr.datas[i], p, count);
                    if (dtDev == -1) {
                        return true;
                    } else if (dtDev == 1) {
                        mbr.delete(i--);
                    }
                }
            }
        }

        return false;
    }

    /**
     * Performs MBR queries to find dependency group for each MBR.
     *
     * @param M      Array of MBRs
     * @param d      Dimensionality
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return Map of MBRs and their dependency group.
     */
    private HashMap<MBR, ArrayList<MBR>> MBRQuery(MBR[] M, int d, long[] count) {
        HashMap<MBR, ArrayList<MBR>> dependentQuery = new HashMap<>();
        for (int i = 0; i < M.length; i++) {
            MBR mbr = M[i];
            count[1]++;
            if (mbr.isDominate)
                continue;
            ArrayList<MBR> dependent = new ArrayList<>();
            for (int j = 0; j < i; j++) {
                MBR value = M[j];
                count[1]++;
                if (value.isDominate)
                    continue;
                if (utils.DTDominated(value, mbr, d, count)) {
                    mbr.setDominate(true);
                    break;
                }
                if (Utils.isDominatedBy(value.getMin(), mbr.getMax(), count)) {
                    dependent.add(value);
                }
            }
            dependentQuery.put(mbr, dependent);
        }
        return dependentQuery;
    }

    /**
     * Computes the skyline set of points.
     *
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return List of objects points
     */
    public List<long[]> skyline(long[] count) {
        List<long[]> skyline = new ArrayList<>();
        this.dgMap = MBRQuery(MBRs, d, count);

        for (MBR mbr : this.MBRs) {
            count[1]++;
            for (int i = 0; i < mbr.usedSpace; i++) {
                long[] data = mbr.datas[i];
                for (int j = i + 1; j < mbr.usedSpace; j++) {
                    int dtDev = Utils.DtDev(data, mbr.datas[j], count);
                    if (dtDev == 1) {
                        mbr.delete(i);
                        i--;
                        break;
                    } else if (dtDev == -1) {
                        mbr.delete(j--);
                    }
                }
            }

            if (!mbr.isDominate) {
                for (int i = 0; i < mbr.usedSpace; i++) {
                    long[] p = mbr.datas[i];
                    if (isDominated(dgMap.get(mbr), p, count)) {
                        mbr.delete(i--);
                    } else {
                        skyline.add(p);
                    }
                }
            }
        }
        return skyline;
    }
}
