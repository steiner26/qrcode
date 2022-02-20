package model;

/**
 * Model class representing the version of a QR symbol, as specified by section
 * 7.3 of the QR Code specification ISO/IEC 18004.
 */
public class Version {

    /** The numerical value of the version, between 1 and 40 (inclusive) */
    private int version;

    /**
     * Create a Version with a given version value.
     *
     * @param version The numerical value of the version
     */
    public Version(int version) {
        this.version = version;
    }

    /**
     * Get the length of the character count indicator for this version and a given mode.
     *
     * @param mode The mode to get the length of the character count indicator for.
     * @return The length of the character count indicator.
     */
    public int getCharacterCountIndicatorLength(Mode mode) {
        if (version < 10) {
            return mode.getShortIndicatorLength();
        } else if (version < 27) {
            return mode.getMediumIndicatorLength();
        } else {
            return mode.getLongIndicatorLength();
        }
    }
}
