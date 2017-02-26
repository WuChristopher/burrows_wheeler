# burrows_wheeler

## [Programming Assignment 5: Burrows-Wheeler Data Compression](http://coursera.cs.princeton.edu/algs4/assignments/burrows.html)

Implement the Burrows-Wheeler data compression algorithm. This revolutionary algorithm outcompresses gzip and PKZIP, is relatively easy to implement, and is not protected by any patents. It forms the basis of the Unix compression utility bzip2.

The Burrows-Wheeler compression algorithm consists of three algorithmic components, which are applied in succession:

Burrows-Wheeler transform. Given a typical English text file, transform it into a text file in which sequences of the same character occur near each other many times.
Move-to-front encoding. Given a text file in which sequences of the same character occur near each other many times, convert it into a text file in which certain characters appear more frequently than others.
Huffman compression. Given a text file in which certain characters appear more frequently than others, compress it by encoding frequently occurring characters with short codewords and rare ones with long codewords.
Step 3 is the one that compresses the message: it is particularly effective because Steps 1 and 2 steps result in a text file in which certain characters appear much more frequently than others. To expand a message, apply the inverse operations in reverse order: first apply the Huffman expansion, then the move-to-front decoding, and finally the inverse Burrows-Wheeler transform. Your task is to implement Burrows-Wheeler and move-to-front components efficiently.
Binary input and binary output. To enable your programs to work with binary data, you will use BinaryStdIn and BinaryStdOut, which are described in Algorithms, 4th edition and part of algs4.jar. To display the binary output when debugging, you can use HexDump, which takes a command-line argument n, reads bytes from standard input and writes them to standard output in hexadecimal, n per line.

% more abra.txt
ABRACADABRA!

% java  edu.princeton.cs.algs4.HexDump 16 < abra.txt
41 42 52 41 43 41 44 41 42 52 41 21
96 bits
Note that in ASCII, 'A' is 41 (hex) and '!' is 21 (hex).
Huffman encoding and decoding. Huffman (Program 5.10 in Algorithms, 4th edition) implements the classic Huffman compression and expansion algorithms.

% java edu.princeton.cs.algs4.Huffman - < abra.txt | java edu.princeton.cs.algs4.HexDump 16
50 4a 22 43 43 54 a8 40 00 00 01 8f 96 8f 94
120 bits
% java  edu.princeton.cs.algs4.Huffman - < abra.txt | java edu.princeton.cs.algs4.Huffman +
ABRACADABRA!
You will not write any code for this step.
Move-to-front encoding and decoding. The main idea of move-to-front encoding is to maintain an ordered sequence of the characters in the alphabet, and repeatedly read in a character from the input message, print out the position in which that character appears, and move that character to the front of the sequence. As a simple example, if the initial ordering over a 6-character alphabet is A B C D E F, and we want to encode the input CAAABCCCACCF, then we would update the move-to-front sequences as follows:

move-to-front    in   out
-------------    ---  ---
 A B C D E F      C    2 
 C A B D E F      A    1
 A C B D E F      A    0
 A C B D E F      A    0
 A C B D E F      B    2
 B A C D E F      C    2
 C B A D E F      C    0
 C B A D E F      C    0
 C B A D E F      A    2
 A C B D E F      C    1
 C A B D E F      C    0
 C A B D E F      F    5
 F C A B D E  
If the same character occurs next to each other many times in the input, then many of the output values will be small integers, such as 0, 1, and 2. The extremely high frequency of certain characters makes an ideal scenario for Huffman coding.
Move-to-front encoding. Your task is to maintain an ordered sequence of the 256 extended ASCII characters. Initialize the sequence by making the ith character in the sequence equal to the ith extended ASCII character. Now, read in each 8-bit character c from standard input one at a time, output the 8-bit index in the sequence where c appears, and move c to the front.
% java MoveToFront - < abra.txt | java  edu.princeton.cs.algs4.HexDump 16
41 42 52 02 44 01 45 01 04 04 02 26
96 bits
Move-to-front decoding. Initialize an ordered sequence of 256 characters, where extended ASCII character i appears ith in the sequence. Now, read in each 8-bit character i (but treat it as an integer between 0 and 255) from standard input one at a time, write the ith character in the sequence, and move that character to the front. Check that the decoder recovers any encoded message.
% java MoveToFront - < abra.txt | java MoveToFront +
ABRACADABRA!
Name your program MoveToFront.java and organize it using the following API:
public class MoveToFront {
    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode()

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode()

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args)
}
Performance requirements. The running time of move-to-front encoding and decoding should be proportional to R n (or better) in the worst case and proportional to n + R (or better) in practice on inputs that arise when compressing typical English text, where n is the number of characters in the input and R is the alphabet size.
Circular suffix array. To efficiently implement the key component in the Burrows-Wheeler transform, you will use a fundamental data structure known as the circular suffix array, which describes the abstraction of a sorted array of the n circular suffixes of a string of length n. As an example, consider the string "ABRACADABRA!" of length 12. The table below shows its 12 circular suffixes and the result of sorting them.

 i       Original Suffixes           Sorted Suffixes         index[i]
--    -----------------------     -----------------------    --------
 0    A B R A C A D A B R A !     ! A B R A C A D A B R A    11
 1    B R A C A D A B R A ! A     A ! A B R A C A D A B R    10
 2    R A C A D A B R A ! A B     A B R A ! A B R A C A D    7
 3    A C A D A B R A ! A B R     A B R A C A D A B R A !    0
 4    C A D A B R A ! A B R A     A C A D A B R A ! A B R    3
 5    A D A B R A ! A B R A C     A D A B R A ! A B R A C    5
 6    D A B R A ! A B R A C A     B R A ! A B R A C A D A    8
 7    A B R A ! A B R A C A D     B R A C A D A B R A ! A    1
 8    B R A ! A B R A C A D A     C A D A B R A ! A B R A    4
 9    R A ! A B R A C A D A B     D A B R A ! A B R A C A    6
10    A ! A B R A C A D A B R     R A ! A B R A C A D A B    9
11    ! A B R A C A D A B R A     R A C A D A B R A ! A B    2
We define index[i] to be the index of the original suffix that appears ith in the sorted array. For example, index[11] = 2 means that the 2nd original suffix appears 11th in the sorted order (i.e., last alphabetically).
Your job is to implement the following circular suffix array API, which provides the client access to the index[] values:

public class CircularSuffixArray {
    public CircularSuffixArray(String s)  // circular suffix array of s
    public int length()                   // length of s
    public int index(int i)               // returns index of ith sorted suffix
    public static void main(String[] args)// unit testing of the methods (optional)
}
Corner cases. The constructor should throw a java.lang.NullPointerException if the argument is null; the method index() should throw a java.lang.IndexOutOfBoundsException if i is outside its prescribed range (between 0 and n − 1).
Performance requirements. Your data type should use space proportional to N. The constructor should take time proportional to N log N (or better) on typical English text; the methods length() and index() should take constant time in the worst case. Warning: beginning with Java 7, Update 6, the substring() method takes time and space proportional to the length of the substring—in other words, you cannot form the n circular suffixes explicitly because that would take both quadratic time and space.

Burrows-Wheeler transform. The goal of the Burrows-Wheeler transform is not to compress a message, but rather to transform it into a form that is more amenable to compression. The transform rearranges the characters in the input so that there are lots of clusters with repeated characters, but in such a way that it is still possible to recover the original input. It relies on the following intuition: if you see the letters hen in English text, then most of the time the letter preceding it is t or w. If you could somehow group all such preceding letters together (mostly t's and some w's), then you would have an easy opportunity for data compression.

Burrows-Wheeler encoding. The Burrows-Wheeler transform of a string s of length N is defined as follows: Consider the result of sorting the n circular suffixes of s. The Burrows-Wheeler transform is the last column in the sorted suffixes array t[], preceded by the row number first in which the original string ends up. Continuing with the "ABRACADABRA!" example above, we highlight the two components of the Burrows-Wheeler transform in the table below.
 i     Original Suffixes          Sorted Suffixes       t    index[i]
--    -----------------------     -----------------------    --------
 0    A B R A C A D A B R A !     ! A B R A C A D A B R A    11
 1    B R A C A D A B R A ! A     A ! A B R A C A D A B R    10
 2    R A C A D A B R A ! A B     A B R A ! A B R A C A D    7
*3    A C A D A B R A ! A B R     A B R A C A D A B R A !   *0
 4    C A D A B R A ! A B R A     A C A D A B R A ! A B R    3
 5    A D A B R A ! A B R A C     A D A B R A ! A B R A C    5
 6    D A B R A ! A B R A C A     B R A ! A B R A C A D A    8
 7    A B R A ! A B R A C A D     B R A C A D A B R A ! A    1
 8    B R A ! A B R A C A D A     C A D A B R A ! A B R A    4
 9    R A ! A B R A C A D A B     D A B R A ! A B R A C A    6
10    A ! A B R A C A D A B R     R A ! A B R A C A D A B    9
11    ! A B R A C A D A B R A     R A C A D A B R A ! A B    2
Since the original string ABRACADABRA! ends up in row 3, we have first = 3. Thus, the Burrows-Wheeler transform is
3
ARD!RCAAAABB
Notice how there are 4 consecutive As and 2 consecutive Bs—these clusters make the message easier to compress.
% java BurrowsWheeler - < abra.txt | java edu.princeton.cs.algs4.HexDump 16
00 00 00 03 41 52 44 21 52 43 41 41 41 41 42 42
128 bits
Also, note that the integer 3 is represented using 4 bytes (00 00 00 03). The character 'A' is represented by hex 41, the character 'R' by 52, and so forth.
Burrows-Wheeler decoder. Now, we describe how to invert the Burrows-Wheeler transform and recover the original input string. If the jth original suffix (original string, shifted j characters to the left) is the ith row in the sorted order, we define next[i] to be the row in the sorted order where the (j + 1)st original suffix appears. For example, if first is the row in which the original input string appears, then next[first] is the row in the sorted order where the 1st original suffix (the original string left-shifted by 1) appears; next[next[first]] is the row in the sorted order where the 2nd original suffix appears; next[next[next[first]]] is the row where the 3rd original suffix appears; and so forth.
Decoding the message given t[], first, and the next[] array. The input to the Burrows-Wheeler decoder is the last column t[] of the sorted suffixes along with first. From t[], we can deduce the first column of the sorted suffixes because it consists of precisely the same characters, but in sorted order.
 i      Sorted Suffixes     t      next
--    -----------------------      ----
 0    ! ? ? ? ? ? ? ? ? ? ? A        3
 1    A ? ? ? ? ? ? ? ? ? ? R        0
 2    A ? ? ? ? ? ? ? ? ? ? D        6
*3    A ? ? ? ? ? ? ? ? ? ? !        7
 4    A ? ? ? ? ? ? ? ? ? ? R        8
 5    A ? ? ? ? ? ? ? ? ? ? C        9
 6    B ? ? ? ? ? ? ? ? ? ? A       10
 7    B ? ? ? ? ? ? ? ? ? ? A       11
 8    C ? ? ? ? ? ? ? ? ? ? A        5
 9    D ? ? ? ? ? ? ? ? ? ? A        2
10    R ? ? ? ? ? ? ? ? ? ? B        1
11    R ? ? ? ? ? ? ? ? ? ? B        4
Now, given the next[] array and first, we can reconstruct the original input string because the first character of the ith original suffix is the ith character in the input string. In the example above, since first = 3, we know that the original input string appears in row 3; thus, the original input string starts with 'A' (and ends with '!'). Since next[first] = 7, the next original suffix appears in row 7; thus, the next character in the original input string is 'B'. Since next[next[first]] = 11, the next original suffix appears in row 11; thus, the next character in the original input string is 'R'.
Constructing the next[] array from t[] and first. Amazingly, the information contained in the Burrows-Wheeler transform suffices to reconstruct the next[] array, and, hence, the original message! Here's how. It is easy to deduce a next[] value for a character that appears exactly once in the input string. For example, consider the suffix that starts with 'C'. By inspecting the first column, it appears 8th in the sorted order. The next original suffix after this one will have the character 'C' as its last character. By inspecting the last column, the next original appears 5th in the sorted order. Thus, next[8] = 5. Similarly, 'D' and '!' each occur only once, so we can deduce that next[9] = 2 and next[0] = 3.
 i      Sorted Suffixes     t      next
--    -----------------------      ----
 0    ! ? ? ? ? ? ? ? ? ? ? A        3
 1    A ? ? ? ? ? ? ? ? ? ? R
 2    A ? ? ? ? ? ? ? ? ? ? D
*3    A ? ? ? ? ? ? ? ? ? ? !
 4    A ? ? ? ? ? ? ? ? ? ? R 
 5    A ? ? ? ? ? ? ? ? ? ? C
 6    B ? ? ? ? ? ? ? ? ? ? A
 7    B ? ? ? ? ? ? ? ? ? ? A
 8    C ? ? ? ? ? ? ? ? ? ? A        5
 9    D ? ? ? ? ? ? ? ? ? ? A        2
10    R ? ? ? ? ? ? ? ? ? ? B
11    R ? ? ? ? ? ? ? ? ? ? B
However, since 'R' appears twice, it may seem ambiguous whether next[10] = 1 and next[11] = 4, or whether next[10] = 4 and next[11] = 1. Here's the key rule that resolves the apparent ambiguity:
If sorted row i and j both start with the same character and i < j, then next[i] < next[j].
This rule implies next[10] = 1 and next[11] = 4. Why is this rule valid? The rows are sorted so row 10 is lexicographically less than row 11. Thus the ten unknown characters in row 10 must be less than the ten unknown characters in row 11 (since both start with 'R'). We also know that between the two rows that end with 'R', row 1 is less than row 4. But, the ten unknown characters in row 10 and 11 are precisely the first ten characters in rows 1 and 4. Thus, next[10] = 1 and next[11] = 4 or this would contradict the fact that the suffixes are sorted.
Check that the decoder recovers any encoded message.

% java BurrowsWheeler - < abra.txt | java BurrowsWheeler +
ABRACADABRA!
Name your program BurrowsWheeler.java and organize it using the following API:
public class BurrowsWheeler {
    // apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
    public static void encode()

    // apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
    public static void decode()

    // if args[0] is '-', apply Burrows-Wheeler encoding
    // if args[0] is '+', apply Burrows-Wheeler decoding
    public static void main(String[] args)
}
Performance requirements. The running time of your Burrows-Wheeler encoder should be proportional to n + R (or better) in the worst case, excluding the time to construct the circular suffix array. The running time of your Burrows-Wheeler decoder should be proportional to n + R (or better) in the worst case.
Analysis (optional). Once you have MoveToFront.java and BurrowsWheeler.java working, compress some of these text files; then, test it on some binary files. Calculate the compression ratio achieved for each file and report the time to compress and expand each file. (Here, compression and expansion consists of applying BurrowsWheeler, MoveToFront, and Huffman in succession.) Finally, determine the order of growth of the running time of each of your encoders and decoders, both in the worst case and on typical English text inputs.

Deliverables. Submit MoveToFront.java, BurrowsWheeler.java, CircularSuffixArray.java, and any other supporting files (excluding algs4.jar). You may not call any library functions other those in java.lang, java.util, and algs4.jar. 

This assignment was developed by Kevin Wayne. 
Copyright © 2004.

## [Programming Assignment Checklist: Burrows-Wheeler Data Compression](http://coursera.cs.princeton.edu/algs4/checklists/burrows.html)

Frequently Asked Questions
What program should I use for reading and writing the data? You must use BinaryStdIn.java and BinaryStdOut.java. These read and write sequences of bytes, whereas StdIn.java and StdOut.java read and write sequences of Unicode characters. These are in stdlib.jar.

My programs work from the command line but the autograder complains that that it can't read in the input. Any ideas? The autograder calls main() more than once. So, be sure that any static variables get reset appropriately (or, better yet, don't use static variables).

My programs don't work properly with binary data. Why not? Be absolutely sure that you use only BinaryStdIn.java and BinaryStdOut.java for input and output. Also, be sure that you call BinaryStdOut.flush() or BinaryStdOut.close() after you are done writing—see Huffman.expand() for an example.

Why does BinaryStdIn return the 8-bits as a (16-bit unsigned) char instead of as an (8-bit signed) byte? The primitive type byte is annoying to use in Java. When you operate on a byte, it is typically promoted to an int and you must be careful because the byte is signed. For example, to convert a byte b to a char c, you must write c = (char) (b & 0xff) instead of c = (char) b. By using char, we avoid the hassle.

For the Burrows-Wheeler encoder, in which order do I sort the suffixes? The input is a sequence of extended ASCII characters (00 to FF), which you can read in with BinaryStdIn.readString(). You should sort the suffixes according to extended ASCII order, which is the natural order of the String data type.

For the Burrows-Wheeler decoder, does next[0] always equal first? And wouldn't this mean that the index first is redundant? No, this is just a coincidence with the input string "ABRACADABRA!". Consider any two input strings that are cyclic rotations of one another, e.g., "ABRACADABRA!" and "CADABRA!ABRA". They will have the same sorted suffixes and t[] array—their only difference will be in the index first.

Can I assume that the decode() method in BurrowsWheeler receives only valid inputs (that were created by a call to the encode() method)? Yes.

How can I view the contents of a binary file and determine its size? Use HexDump.java, as in the assignment. The command-line argument specifies the number of bytes per line to print; if the argument is 0, all output except for the number of bits will be suppressed.

How much memory can my program consume? The Burrows-Wheeler encoder may use quite a bit, so you may need to use the -Xmx option when executing. You must use space linear in the input size n and alphabet size R. (Industrial strength Burrows-Wheeler compression algorithms typically use a fixed block size, and encode the message in these smaller chunks. This reduces the memory requirements, at the expense of some loss in compression ratio.) Therefore, depending on your operating system and configuration there may be some very large files for which your program will not have enough memory even with the -Xmx option.

I'm running out of memory in the Burrows-Wheeler encoder. Any ideas? Watch out for creating new strings via substring() or string concatenation. Remember, that, as of Java 7u6, substring extraction no longer takes constant time or space!

What is meant by "typical English text inputs"? Inputs such as aesop.txt, moby.txt, or your most recent essay. We do not mean files such as aesop3copies.txt or random sequences of characters.

How do I use gzip and bzip2 on Windows? It's fine to use pkzip or 7-zip instead.

I'm curious. What compression algorithm is used in PKZIP? In gzip? In bzip2? PKZIP uses LZW compression followed by Shannon-Fano (an entropy encoder similar to Huffman). The Unix utility gzip uses a variation of LZ77 (similar to LZW) followed by Huffman coding. The program bzip2 combines the Burrows-Wheeler transform, Huffman coding, and a (fancier) move-to-front style rule.

Testing
Input. Here are some sample input files. For convenience, textfiles-testing.zip contains all of these files bundled together. To fully test your program, you should also try to compress and uncompress binary files (e.g., .class or .jpg files). Be careful to download them as binary files—some browsers will corrupt them if you view the file and use File -> Save. Do not edit them in a text editor—some editors will corrupt them by inserting bogus newline characters.

Reference solutions. For reference, we have provided the output of compressing aesop.txt and us.gif. We have also provided the results of applying each of the three encoding algorithms in isolation. Note that the .gif file is a binary file and is already compressed.

To compare the contents of two files, you can use the following commands:

Mac OS X or Linux: diff file1 file2
Windows: fc file1 file2
Timing your program.

Mac OS X or Linux. Use the following command:
% time java BurrowsWheeler - < mobydick.txt | java MoveToFront - | java Huffman - > mobyDickOutputFileName
You want to record the "real" value.
Windows. We recommend using a simple batch file. The process for creating and running a batch file is as follows:
Create a file called timeTest.bat in the same directory as your Burrows wheeler assignment. This is known as a "batch file".
Inside that file, put the following text (using Notepad or a similar text editor):
    echo %time%
    java BurrowsWheeler - < mobydick.txt | java MoveToFront - | java Huffman - > mobyDickOutputFileName
    echo %time%
    
Execute timeTest.bat by navigating a terminal window so that it's in the same directory as timeTest.bat and your .java files and type timeTest.
Make sure you name the batch file timeTest.bat instead of timeTest.bat.txt. Note that you can test multiple files by adding more lines to the batch file.

Timing using gzip, bzip2, 7zip, etc.

Mac OS X or Linux. Use the time command as above, but with gzip or bzip2.
Windows. There is no built-in data compression program (such as gzip or bzip2). We recommend downloading the free 7-zip program. After installing 7-zip, create a new batch file (any filename ending in .bat) with the following text:
    echo %time%
    7za a -tzip mobyDickOutputFileName.zip mobydick.txt
    echo %time%
This creates a file in .zip format (the same used natively by Windows for compression). To test unzipping time, use the following:

    echo %time%
    7za e mobyDickOutputFileName.zip 
    echo %time%
If you're interested in testing against other compression formats, then see this page.

Possible Progress Steps
These are purely suggestions for how you might make progress. You do not have to follow these steps.

Download the directory burrows to your system. It contains some sample input files and reference solutions. For convenience, burrows-testing.zip contains all of these files bundled together.
Implement the CircularSuffixArray. Be sure not to create explicit copies of the string (e.g., via the substring() method in Java's String data type) when you sort the suffixes. That would take quadratic space. Instead for each suffix, you only need to keep an index that indicates which character is the beginning of the suffix. This way you can build the N suffixes in linear time and space. Then sort this array of indices. It's just like sorting an array of references.
Implement the Burrows-Wheeler transform, using the CircularSuffixArray class.
The Burrows-Wheeler decoding is the trickiest part, but it is very little code once you understand how it works. (Not including declarations and input, our solution is about 10 lines of code.) You may find the key-indexed counting algorithm from the string sorting lecture to be useful.
Implement the move-to-front encoding and decoding algorithms. Not including comments and declarations, our solutions are about 10 lines of code each. If yours is significantly longer, try to simplify it.
