import edu.princeton.cs.algs4.*;
import edu.princeton.cs.algs4.Queue;

import java.util.*;

/**
 * Created by Christopher on 18/02/2017.
 */
public class BurrowsWheeler {

    private static final int R = 256; // extended ASCII

    // apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
    public static void encode() {
        StringBuilder sb = new StringBuilder();
        while (!BinaryStdIn.isEmpty()) sb.append(BinaryStdIn.readChar());
        String s = sb.toString();
        CircularSuffixArray csa = new CircularSuffixArray(s);
        for (int i = 0; i < csa.length(); i++) {
            if (csa.index(i) == 0) {
                BinaryStdOut.write(i);
                break;
            }
        }

        for (int i = 0; i < csa.length(); i++) {
            BinaryStdOut.write(s.charAt((csa.index(i) + s.length() - 1) % s.length()));
        }
        BinaryStdOut.flush();
        BinaryStdOut.close();
        BinaryStdIn.close();
        //StdOut.print();
    }

    // apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
    public static void decode() {
        int first = BinaryStdIn.readInt();
        ArrayList<Character> tail = new ArrayList<>();
        int[] count = new int[R+1];
        Object[] charIdx = new Object[R];
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            tail.add(c);
            if (charIdx[c] != null) ((Queue<Integer>) charIdx[c]).enqueue(tail.size()-1);
            else {
                Queue<Integer> q = new Queue<>();
                q.enqueue(tail.size() - 1);
                charIdx[c] = q;
            }
            count[c+1]++;
        }
        // use key-index counting to sort the input chars
        for (int r = 0; r < R; r++) {
            count[r+1] += count[r];
        }
        char[] head = new char[tail.size()];
        for (int i = 0; i < tail.size(); i++) {
            head[count[tail.get(i)]++] = tail.get(i);
        }
        // initialize the next[] per the algorithm
        int[] next = new int[tail.size()];
        for (int i = 0; i < head.length; i++) {
            //int idx = charIdx.get(head[i]).dequeue();
            int idx = ((Queue<Integer>) charIdx[head[i]]).dequeue();
            next[i] = idx;
        }
        // use 'first' and 'next[]' to reconstruct the String
        int cnt = 0;
        while (cnt < tail.size()) {
            BinaryStdOut.write(head[first]);
            first = next[first];
            cnt++;
        }
        BinaryStdOut.flush();
        BinaryStdOut.close();
        BinaryStdIn.close();
    }

    /**
     * Burrows-Wheeler transform for debugging.
     */
    private static void transform(String s) {
        CircularSuffixArray csa = new CircularSuffixArray(s);
        for (int i = 0; i < csa.length(); i++) {
            if (csa.index(i) == 0) {
                StdOut.print(i);
                StdOut.print(" ");
                break;
            }
        }

        for (int i = 0; i < csa.length(); i++) {
            StdOut.printf("%c ", s.charAt((csa.index(i) + s.length() - 1) % s.length()));
        }
    }

    /**
     * Burrows-Wheeler inverse transform for debugging.
     */
    private static void inverse(int first, String s) {
        ArrayList<Character> tail = new ArrayList<>();
        int[] count = new int[R+1];
        //HashMap<Character, Queue<Integer>> charIdx = new HashMap<>();
        Object[] charIdx = new Object[R];
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            tail.add(c);
            if (charIdx[c] != null) ((Queue<Integer>) charIdx[c]).enqueue(tail.size()-1);
            else {
                Queue<Integer> q = new Queue<>();
                q.enqueue(tail.size() - 1);
                charIdx[c] = q;
            }
            count[c+1]++;
        }
        // use key-index counting to sort the input chars
        for (int r = 0; r < R; r++) {
            count[r+1] += count[r];
        }
        char[] head = new char[tail.size()];
        for (int i = 0; i < tail.size(); i++) {
            head[count[tail.get(i)]++] = tail.get(i);
        }
        // initialize the next[] per the algorithm
        int[] next = new int[tail.size()];
        for (int i = 0; i < head.length; i++) {
            //int idx = charIdx.get(head[i]).dequeue();
            int idx = ((Queue<Integer>) charIdx[head[i]]).dequeue();
            next[i] = idx;
        }
        // use 'first' and 'next[]' to reconstruct the String
        int cnt = 0;
        while (cnt < tail.size()) {
            StdOut.printf("%c ", head[first]);
            first = next[first];
            cnt++;
        }
    }

    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    // if args[0] is '--', debug Burrows-Wheeler transform(encoding)
    // if args[0] is '++', debug Burrows-Wheeler inverse transform(decoding)
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        if (args[0].equals("+")) decode();
        if (args[0].equals("--")) transform("CDABCCCC");
        if (args[0].equals("++")) inverse(6, "DDBCDBBA");
    }
}
