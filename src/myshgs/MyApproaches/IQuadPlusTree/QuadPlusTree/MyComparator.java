/**
 * This class is used to compare arrays of long type.
 * It is mainly used in the QuadPlusTree data structure for node comparison.
 */
package myshgs.MyApproaches.IQuadPlusTree.QuadPlusTree;

import java.util.Comparator;

/**
 * Implements a comparator for arrays of long type.
 * Compares two long arrays element by element from front to back until a different value is found.
 * If all elements are equal, then these two arrays are considered equal.
 */
public class MyComparator implements Comparator<long[]> {
    /**
     * Compares the two given long arrays.
     *
     * @param o1 The first long array to be compared.
     * @param o2 The second long array to be compared.
     * @return Returns an integer value representing the comparison result:
     *         less than 0 if o1 is less than o2,
     *         greater than 0 if o1 is greater than o2,
     *         and 0 if they are equal.
     */
    @Override
    public int compare(long[] o1, long[] o2) {
        int compare ;
        for (int i = 0; i < o1.length; i++) {
            compare = Long.compare(o1[i], o2[i]);
            if (compare != 0)
                return compare;
        }
        return 0;
    }
}
