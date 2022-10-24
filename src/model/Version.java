package model;

/**
 * Model class representing the version of a QR symbol, as specified by section
 * 7.3 of the QR Code specification ISO/IEC 18004.
 */
public class Version {

    public static final int INDICATOR_LENGTH_IN_BITS = 6;

    private static final int MINIMUM_VERSION = 1;
    private static final int MAXIMUM_VERSION = 40;

    private static final int MAXIMUM_VERSION_SHORT_CHARACTER_CODE_INDICATOR = 9;
    private static final int MAXIMUM_VERSION_MEDIUM_CHARACTER_CODE_INDICATOR = 26;

    private static final int MAXIMUM_VERSION_NO_VERSION_INFO = 6;

    /** The numerical value of the version, between 1 and 40 (inclusive) */
    private int version;

    /**
     * Create a Version with a given version value.
     *
     * @param version The numerical value of the version
     */
    public Version(int version) {
        if (version < MINIMUM_VERSION || version > MAXIMUM_VERSION) {
            throw new IllegalArgumentException(String.format("Version %s is not valid", version));
        }
        this.version = version;
    }

    /**
     * Get the side length of a symbol of this Version
     *
     * @return The side length of a symbol of this Version
     */
    public int getSymbolSideLength() {
        return 21 + (4 * (version -1));
    }

    /**
     * Get the version as an int value
     *
     * @return The integer value of the version number
     */
    public int getVersion() {
        return version;
    }

    /**
     * Get whether a symbol of this version has version info
     *
     * @return Whether a symbol of this version has version info
     */
    public boolean hasVersionInfo() {
        return version > MAXIMUM_VERSION_NO_VERSION_INFO;
    }

    /**
     * Get the length of the character count indicator for this version and a given mode.
     *
     * @param mode The mode to get the length of the character count indicator for.
     * @return The length of the character count indicator.
     */
    public int getCharacterCountIndicatorLength(Mode mode) {
        if (version <= MAXIMUM_VERSION_SHORT_CHARACTER_CODE_INDICATOR) {
            return mode.getShortIndicatorLength();
        } else if (version <= MAXIMUM_VERSION_MEDIUM_CHARACTER_CODE_INDICATOR) {
            return mode.getMediumIndicatorLength();
        } else {
            return mode.getLongIndicatorLength();
        }
    }
}
