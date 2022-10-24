package model;

/**
 * Enum representing an error correction level, as described by
 * section 8.5 of the QR Code specification ISO/IEC 18004.
 */
public enum ErrorCorrectionLevel {
    L(0b01),
    M(0b00),
    Q(0b11),
    H(0b10);

    public static final int INDICATOR_LENGTH_IN_BITS = 2;

    /** The 2-bit indicator for this error correction level */
    private int indicator;

    /**
     * Create an ErrorCorrectionLevel with a given indicator
     *
     * @param indicator The 2-bit indicator for this error correction level
     */
    ErrorCorrectionLevel(int indicator) {
        this.indicator = indicator;
    }

    /**
     * Get the 2-bit indicator for this error correction level
     *
     * @return The 2-bit indicator for this error correction level
     */
    public int getIndicator() {
        return this.indicator;
    }

    /**
     * Get the 0-indexed ordered index of this error correction level when ordered from least recovery capacity to most
     *
     * @return The ordered index of this error correction level
     */
    public int getIndex() {
        return this.ordinal();
    }
}
