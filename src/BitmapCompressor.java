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
 *  @author Beckett Porter
 */
public class BitmapCompressor
{
    // Variable which tells the compressor how many bits to allocate for storing the length of a block,
    // 8 seems to work well and only occasionally overflows (which I added a check for so that is handled).
    final static short BLOCK_SIZE = 8;

    /**
     * Reads a sequence of bits from standard input, compresses them,
     * and writes the results to standard output.
     */
    public static void compress()
    {
        boolean bit;
        boolean nextBit;
        short length;
        boolean firstBitCheckCompleted = false;

        nextBit = BinaryStdIn.readBoolean();

        while (!BinaryStdIn.isEmpty())
        {
            // Set bit to the next bit and then get a new next bit to move the search window forward by one.
            bit = nextBit;

            // This is a check to make sure we write out an empty block if the sequence starts with a 1.
            // (this is because we assume in the decompressor that we always start with 0)
            if (!firstBitCheckCompleted && bit)
            {
                writeEmptyBlock();
            }
            firstBitCheckCompleted = true;

            // Get the next bit in the sequence.
            nextBit = BinaryStdIn.readBoolean();


            // Set the sequence length to 1 to show it is a new block.
            length = 1;

            // If this next bit differs from the bit we currently have,
            // end the block and write out the size.
            while (bit == nextBit)
            {
                // If the length is going to overflow the block size, write out the current length and then
                // set length to 0 and break from the while loop; this will write
                // out the empty block in the write after the while loop.
                if (length >= Math.pow(2, BLOCK_SIZE) - 1)
                {
                    BinaryStdOut.write(length, BLOCK_SIZE);
                    length = 0;
                    break;
                }

                // Increment length as a match between bit and the next bit was found.
                length++;

                // Make sure there is still something in the input to read before reading the next boolean.
                if (!BinaryStdIn.isEmpty())
                {
                    nextBit = BinaryStdIn.readBoolean();
                }
                // If there isn't, break and exit the while loop and finish up this final block by writing the length.
                else
                {
                    break;
                }
            }
            // Write out the length of the found sequence for BLOCK_SIZE bits.
            BinaryStdOut.write(length, BLOCK_SIZE);
        }

        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand()
    {
        // Instance variables for the bit indicating what the block type is and the number of these bits to print.
        boolean bitToPrint = true;
        int numBitsToPrint;

        // Go until there is no more input to be read.
        while (!BinaryStdIn.isEmpty())
        {
            // The initial bit in the block is what type of bit the block represents.
            bitToPrint = !bitToPrint;

            // Then read the next BLOCK_SIZE - 1 bits which indicates the number of these bits to write out.
            numBitsToPrint = BinaryStdIn.readInt(BLOCK_SIZE);

            // Then write out that number of those bits.
            for (int i = 0; i < numBitsToPrint; i++)
            {
                BinaryStdOut.write(bitToPrint);
            }
        }

        BinaryStdOut.close();
    }

    private static void writeEmptyBlock()
    {
        BinaryStdOut.write(0, BLOCK_SIZE);
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