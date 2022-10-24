package model;

/**
 * Model class representing the information about a block of data.
 */
public class BlockInfo {

    /** The number of data codewords in this block */
    private int dataCodewords;

    /** The number of error correction codewords in this block */
    private int errorCorrectionCodewords;

    /**
     * Get the total number of codewords in this block
     *
     * @return the total number of codewords in this block
     */
    public int getTotalCodewords() {
        return this.dataCodewords + this.errorCorrectionCodewords;
    }

}
