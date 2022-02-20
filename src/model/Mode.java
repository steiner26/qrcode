package model;

/**
 * Enum representing a data encoding mode and its 4-bit indicator, as described by
 * section 8.3 of the QR Code specification ISO/IEC 18004.
 */
public enum Mode {
    ECI(0b0111),
    NUMERIC(0b0001, 10, 12, 14),
    ALPHANUMERIC(0b0010, 9, 11, 13),
    BYTE(0b0100, 8, 16, 16),
    KANJI(0b1000, 8, 10, 12),
    STRUCTURED_APPEND(0b0011),
    FNC1_FIRST_POSITION(0b0101),
    FNC1_SECOND_POSITION(0b1001);

    /** The 4-bit indicator for this mode */
    private int indicator;

    /** Character count indicator lengths for short, medium and long */
    private int shortLength;
    private int mediumLength;
    private int longLength;

    /**
     * Create a Mode with a given indicator
     *
     * @param indicator The 4-bit indicator for this mode
     */
    Mode(int indicator) {
        this.indicator = indicator;
    }

    /**
     * Create a Mode with a given indicator and character count indicator lengths
     *
     * @param indicator The 4-bit indicator for this mode
     */
    Mode(int indicator, int shortLength, int mediumLength, int longLength) {
        this.indicator = indicator;
        this.shortLength = shortLength;
        this.mediumLength = mediumLength;
        this.longLength = longLength;
    }

    /**
     * Get the 4-bit indicator for this mode
     *
     * @return The indicator for this mode
     */
    public int getIndicator() {
        return indicator;
    }

    /**
     * Get the short character count indicator length for this mode
     *
     * @return The short character count indicator length for this mode
     */
    public int getShortIndicatorLength() {
        return shortLength;
    }

    /**
     * Get the medium character count indicator length for this mode
     *
     * @return The medium character count indicator length for this mode
     */
    public int getMediumIndicatorLength() {
        return mediumLength;
    }

    /**
     * Get the long character count indicator length for this mode
     *
     * @return The long character count indicator length for this mode
     */
    public int getLongIndicatorLength() {
        return longLength;
    }
}