package myshgs.Others.MBR_Oriented.MBRSKY;

import myshgs.Utils;
import java.util.*;

/**
 * The MBRSky class is designed to perform Skyline queries based on MBR (Minimum Bounding Rectangle).
 * It organizes point data using MBRs and provides efficient methods for computing the Skyline.
 */
public class MBRSky {
    // Data dimension
    private final int d;
    // Capacity of each MBR
    private int C;
    // Points data
    public long[][] points;
    // Array of MBRs
    private MBR[] MBRs;
    // Fanout value
    private int Fanont;
    // Dependency graph map
    private HashMap<MBR, ArrayList<MBR>> dgMap;

    /**
     * Constructor for the MBRSky class.
     *
     * @param C      Capacity of each MBR
     * @param Fanout Fanout value
     * @param d      Dimension of the data
     */
    public MBRSky(int C, int Fanout, int d) {
        this.d = d;
        this.Fanont = Fanout;
        this.C = C;
    }

    /**
     * Initializes the MBRSky object with the given points.
     *
     * @param points Array of points to be processed
     */
    public void init(long[][] points) {
        this.points = points;

        if (Fanont != -1) {
            // Calculate the number of chunks based on fanout and dimensions
            int N = (int) Math.ceil(Math.pow((points.length) / (double) (this.Fanont), 1.0 / d));
            this.C = (int) Math.ceil(Math.pow(N, d));
        }
        // Generate MBRs from the points
        this.MBRs = getSTRMBR(points, C, this.d);
    }

    /**
     * Checks if a point is dominated by any MBR in the list.
     *
     * @param list  List of MBRs to check against
     * @param p     Point to check
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return True if the point is dominated, false otherwise
     */
    private boolean isDominated(ArrayList<MBR> list, long[] p, long[] count) {
        for (MBR mbr : list) {
            count[1]++;
            if (!mbr.isDominate) {
                if (mbr.minValue >= Arrays.stream(p).sum())
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
     * Sorts the entries into chunks based on dimensions and capacity.
     *
     * @param entries Array of entries to sort
     * @param dims    Number of dimensions
     * @param M       Capacity
     * @param comp    Comparator for sorting
     */
    private void sortChunks(long[][] entries, int dims, int M, CenterComp comp) {
        comp.setDim(0);
        Arrays.sort(entries, comp);
        int nToSplit = entries.length;
        for (int d = 1; d < dims; d++) {
            int nodesPerAxis = (int) Math.pow((double) nToSplit / M, 1.0 / (dims - d + 1));
            comp.setDim(d);
            int chunkSize = (int) Math.ceil(Math.pow(nodesPerAxis, dims - d) * M);
            if (chunkSize < M) {
                break;
            }
            int pos = 0;
            while (pos < entries.length) {
                int end = Math.min(pos + chunkSize, entries.length);
                Arrays.sort(entries, pos, end, comp);
                pos += chunkSize;
            }
            nToSplit /= nodesPerAxis;
        }
    }

    /**
     * Performs an MBR-oriented query to identify dependencies between MBRs.
     *
     * @param M     Array of MBRs
     * @param d     Dimension of the data
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return A map of MBRs and their dependent MBRs
     */
    private HashMap<MBR, ArrayList<MBR>> MBRQuery(MBR[] M, int d, long[] count) {
        HashMap<MBR, ArrayList<MBR>> dependentQuery = new HashMap<>();
        for (MBR mbr : M) {
            count[1]++;
            if (mbr.isDominate)
                continue;
            ArrayList<MBR> dependent = new ArrayList<>();
            for (MBR value : M) {
                count[1]++;
                if (value.isDominate || value == mbr)
                    continue;
                if (mbr.isDominate && mbr.maxValue < value.minValue) {
                    break;
                }
                if (utils.DTDominated(value, mbr, d, count)) {
                    mbr.setDominate(true);
                    break;
                }
                if (utils.DTDominated(mbr, value, d, count)) {
                    value.setDominate(true);
                    continue;
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
     * Computes the Skyline of the points.
     *
     * @param count An array used to record the number of dominance test and accessing nodes during the computation process, count[0]: the number of dominance test,count[1]: the number of accessing nodes
     * @return List of points that form the Skyline
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

    /**
     * Generates MBRs from the given points using the STR algorithm.
     *
     * @param points Array of points
     * @param C      Capacity of each MBR
     * @param d      Dimension of the data
     * @return Array of MBRs
     */
    private MBR[] getSTRMBR(long[][] points, int C, int d) {
        int N = points.length;
        CenterComp cmp = new CenterComp();

        // Sort points into chunks
        sortChunks(points, d, C, cmp);

        MBR[] mbrs = new MBR[(int) Math.ceil(N / (double) C)];

        int posNode = 0;
        MBR node = null;
        for (int i = 0; i < points.length; i++) {
            if (i % C == 0) {
                node = new MBR(d, C);
                mbrs[posNode++] = node;
            }
            node.addData(points[i]);
        }
        Arrays.sort(mbrs);
        return mbrs;
    }

}