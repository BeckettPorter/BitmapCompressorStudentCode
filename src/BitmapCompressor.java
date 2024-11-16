/******************************************************************************
 *  Compilation:  javac BitmapCompressor.java
 *  Execution:    java BitmapCompressor - < input.bin   (compress)
 *  Execution:    java BitmapCompressor + < input.bin   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   q32x48.bin
 *                q64x96.bin
 *                mystery.bin
 *
 *  Compress or expand binary input from standard input.
 *
 *  % java DumpBinary 0 < mystery.bin
 *  8000 bits
 *
 *  % java BitmapCompressor - < mystery.bin | java DumpBinary 0
 *  1240 bits
 ******************************************************************************/

/**
 *  The {@code BitmapCompressor} class provides static methods for compressing
 *  and expanding a binary bitmap input.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Zach Blick
 *  @author YOUR NAME HERE
 */
public class BitmapCompressor {

    final static short BLOCK_SIZE = 16;

    /**
     * Reads a sequence of bits from standard input, compresses them,
     * and writes the results to standard output.
     */
    public static void compress()
    {
        // First bit is what the block is (either 1 or 0) and the next 5 are the length of the block.
        boolean bit = BinaryStdIn.readBoolean();

        // Write the initial bit to indicate what this block represents (Either a 0 or 1).
        BinaryStdOut.write(bit);

        short blockLength = 1;

        while (!BinaryStdIn.isEmpty())
        {
            // Get the next bit in the sequence.
            boolean nextBit = BinaryStdIn.readBoolean();

            // If this next bit differs from the first bit we got, end the block and write out the size.
            if (bit != nextBit)
            {
                // Write out the blockLength for BLOCK_SIZE - 1 bits.
                BinaryStdOut.write(blockLength, BLOCK_SIZE - 1);

                bit = nextBit;
                blockLength = 1;

                BinaryStdOut.write(bit);
            }
            else
            {
                blockLength++;
            }
        }

        // Write the final block length for the last block
        BinaryStdOut.write(blockLength, BLOCK_SIZE - 1);

        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand()
    {

        boolean bitToPrint;
        int numBitsToPrint;

        while (!BinaryStdIn.isEmpty())
        {
            bitToPrint = BinaryStdIn.readBoolean();

            numBitsToPrint = BinaryStdIn.readInt(BLOCK_SIZE - 1);

            for (int i = 0; i < numBitsToPrint; i++)
            {
                BinaryStdOut.write(bitToPrint);
            }
        }

        BinaryStdOut.close();
    }

    /**
     * When executed at the command-line, run {@code compress()} if the command-line
     * argument is "-" and {@code expand()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}