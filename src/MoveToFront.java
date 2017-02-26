import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

/**
 * Created by Christopher on 16/02/2017.
 */
public class MoveToFront {
    private static final int R = 256;

    private static class Node {
        char c;
        Node next;
        public Node(char c) {
            this.c = c;
        }
    }

    private static class SinglyLinkedList {
        private Node first = null;
        private boolean removed = false;

        private void add(char c) {
            Node oldFirst = first;
            first = new Node(c);
            first.next = oldFirst;
        }

        private int remove(char c) {
            if (first == null) throw new IllegalArgumentException("empty list");
            if (first.c == c) return 0;
            int idx = -1;
            Node prev = first;
            Node n = first.next;
            int cnt = 1;
            while (n != null) {
                if (n.c == c) {
                    idx = cnt;
                    prev.next = n.next;
                    break;
                }
                prev = n;
                n = n.next;
                cnt++;
            }
            return idx;
        }

        private char remove(int i) {
            removed = true;
            if (first == null) throw new IllegalArgumentException("empty list");
            if (i == 0) { removed = false; return first.c; }
            Node prev = first;
            Node n = first.next;
            i--;
            char c = 0;
            while (n != null) {
                if (i == 0) {
                    c = n.c;
                    prev.next = n.next;
                    break;
                }
                prev = n;
                n = n.next;
                i--;
            }
            return c;
        }
    }
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        SinglyLinkedList ascii = new SinglyLinkedList();
        for (int i = R - 1; i >= 0; i--) {
            ascii.add((char) i);
        }
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int idx = ascii.remove(c);
            BinaryStdOut.write((char) idx);
            if (idx > 0) ascii.add(c);
        }
        BinaryStdIn.close();
        BinaryStdOut.flush();
        BinaryStdOut.close();
    }
    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        SinglyLinkedList ascii = new SinglyLinkedList();
        for (int i = R - 1; i >= 0 ; i--) {
            ascii.add((char) i);
        }
        while (!BinaryStdIn.isEmpty()) {
            int idx = (int) BinaryStdIn.readChar(8);
            char c = ascii.remove(idx);
            BinaryStdOut.write(c);
            if (ascii.removed) ascii.add(c);
        }
        BinaryStdIn.close();
        BinaryStdOut.flush();
        BinaryStdOut.close();
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) encode();
        if (args[0].equals("+")) decode();
        //StdOut.print();
    }
}
