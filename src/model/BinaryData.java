package model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Model class representing a string of binary data with an arbitrary length.
 */
public class BinaryData {

    /** The bits of this binary data */
    private BitSet bits;

    /** The size of this binary data */
    private int size;

    /**
     * Create a new BinaryData with no data
     */
    public BinaryData() {
        this.bits = new BitSet(0);
        this.size = 0;
    }

    /**
     * Append an int to the end of this BinaryData
     *
     * @param value The value of the int to be appended
     * @param numBits The number of bits the value should take up
     */
    public void appendInt(int value, int numBits) {
        for (int i = 0; i < numBits; i++) {
            if ((value & (1 << i)) > 0) {
                bits.set((numBits - i - 1) + size);
            }
        }
        size += numBits;
    }

    /**
     * Get an array of bytes that represents this binary data. The array can be modified
     *
     * @return An array of bytes representing the binary data
     */
    public byte[] toByteArray() {
        // TODO make sure 0 bits at the end are included
        byte[] bytes = bits.toByteArray();
        // Bytes need to have their bits reversed
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            byte newByte = 0;
            for (int j = 0; j < 8; j++) {
                int bitValue = b & (1 << j);
                int newLocation = 7 - j;
                if (newLocation > j) {
                    newByte |= bitValue << (newLocation - j);
                } else {
                    newByte |= bitValue >> (j - newLocation);

                }
            }
            bytes[i] = newByte;
        }
        return bytes;
    }

    /**
     * Get a list of integers that represents this binary data. This list can be modified
     *
     * @return A list of integers representing the binary data
     */
    public List<Integer> toIntegerList() {
        ArrayList<Integer> result = new ArrayList<>();
        for (byte b : this.toByteArray()) {
            result.add((int) b);
        }
        return result;
    }
}
