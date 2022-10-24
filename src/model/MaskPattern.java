package model;

import java.util.function.BiPredicate;

/**
 * Enum representing the eight mask patterns as described by section 8.8 of the QR Code specification ISO/IEC 18004.
 */
public enum MaskPattern {
    ZERO(0b000, MaskPattern::zeroCondition),
    ONE(0b001, MaskPattern::oneCondition),
    TWO(0b010, MaskPattern::twoCondition),
    THREE(0b011, MaskPattern::threeCondition),
    FOUR(0b100, MaskPattern::fourCondition),
    FIVE(0b101, MaskPattern::fiveCondition),
    SIX(0b110, MaskPattern::sixCondition),
    SEVEN(0b111, MaskPattern::sevenCondition);

    /** The length of the mask pattern reference indicator in bits, as it is used in the format information */
    public static final int INDICATOR_LENGTH_IN_BITS = 3;

    /** The 3 bit indicator for this mask pattern */
    private final int indicator;

    /** The condition for this mask pattern */
    private final BiPredicate<Integer, Integer> condition;

    /**
     * Create a MaskPattern with a given indicator and condition
     *
     * @param indicator The 3 bit indicator for this mask pattern
     * @param condition The condition for this mask pattern
     */
    MaskPattern(int indicator, BiPredicate<Integer, Integer> condition) {
        this.indicator = indicator;
        this.condition = condition;
    }

    /**
     * Get the indicator for this mask pattern
     *
     * @return the indicator for this mask pattern
     */
    public int getIndicator() {
        return indicator;
    }

    /**
     * Get the condition for this mask pattern
     *
     * @return The condition for this mask pattern
     */
    public BiPredicate<Integer, Integer> getCondition() {
        return condition;
    }

    private static boolean zeroCondition(int i, int j) {
        return (i + j) % 2 == 0;
    }

    private static boolean oneCondition(int i, int j) {
        return i % 2 == 0;
    }

    private static boolean twoCondition(int i, int j) {
        return j % 3 == 0;
    }

    private static boolean threeCondition(int i, int j) {
        return (i + j) % 3 == 0;
    }

    private static boolean fourCondition(int i, int j) {
        return ((i / 2) + (j / 3)) % 2 == 0;
    }

    private static boolean fiveCondition(int i, int j) {
        return ((i * j) % 2) + ((i * j) % 3) == 0;
    }

    private static boolean sixCondition(int i, int j) {
        return (((i * j) % 2) + ((i * j) % 3)) % 2 == 0;
    }

    private static boolean sevenCondition(int i, int j) {
        return (((i * j) % 3) + ((i + j) % 2)) % 2 == 0;
    }
}
