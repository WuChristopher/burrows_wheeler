import edu.princeton.cs.algs4.StdOut;

/**
 * Created by Christopher on 16/02/2017.
 */
public class CircularSuffixArray {

    /**
     * After Java 7, Update 6 the substring() method of String takes linear time and space,
     * which means we cannot build suffixes arrays(not to mention the circular suffixes) using
     * the substring() method, which now takes quadratic time and space for all the suffixes!
     */
    private int[] suffixes;
    private int len;

    public CircularSuffixArray(String s) {
        if (s == null) throw new NullPointerException();
        len = s.length();
        suffixes = new int[s.length()];
        for (int i = 0; i < len; i++) {
            suffixes[i] = i;
        }
        sort(s, suffixes);
    }

    public int length() { return len; }

    public int index(int i) {
        if (i < 0 || i >= suffixes.length) throw new IndexOutOfBoundsException("i: " + i);
        return suffixes[i];
    }

    private static void sort(String s, int[] a) {
        sort(s, a, 0, a.length - 1, 0);
    }

    /**
     * Use 3-way String quick-sort similar algorithm to sort the suffix array, because the
     * suffix array only contains the index of the starting char of each suffix string,
     * the original string should be passed in for addition information.
     */
    private static void sort(String s, int[] a, int lo, int hi, int d) {
        if (hi <= lo) return;
        int lt = lo, gt = hi;
        if (d >= s.length()) return;
        char v = s.charAt((a[lo] + d) % s.length());
        int i = lo + 1;
        while (i <= gt) {
            char t = s.charAt((a[i] + d) % s.length());
            if (t < v) exch(a, lt++, i++);
            else if (t > v) exch(a, i, gt--);
            else i++;
        }

        sort(s, a, lo, lt - 1, d);
        sort(s, a, lt, gt, d + 1);
        sort(s, a, gt + 1, hi, d);
    }

    private static void exch(int[] a, int i, int j) {
        int tmp = a[i];
        a[i] = a[j];
        a[j] = tmp;
    }

    public static void main(String[] args) {
        String s = "couscous";
        CircularSuffixArray csa = new CircularSuffixArray(s);
        for (int i = 0; i < csa.length(); i++) {
            StdOut.printf("%d ", csa.index(i));
        }
        StdOut.print();
    }
}
