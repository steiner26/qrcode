package error;

/**
 * Model class representing a number to be used in bit-wise modulo 2 and byte-wise
 * modulo 100011101 arithmetic, as specified by section 8.5.2 of the QR Code specification
 * ISO/IEC 18004.
 */
public class GF256Number {

    /** Modulus used in modulo 100011101 arithmetic, equal to decimal 285 */
    private static final int MODULUS = 285;

    /** Galois Field value of 256 */
    private static final int GALOIS_FIELD_VALUE = 256;

    /** Lookup array by power. Note that the powers of 0 and 255 both have a value of 1 */
    private static final GF256Number[] numbersByPower = generateNumbersByPower();

    /** Lookup array by value. Note that the value of 0 has an undefined power */
    private static final GF256Number[] numbersByValue = generateNumbersByValue();


    /** The value of this number, between 0 and 255 inclusive */
    private int value;

    /** The positive power of 2 such that 2^power = value in GF(256) modulo 100011101 arithmetic */
    private int power;

    /**
     * Create a GF256Number with a given power of two and value.
     * It must be true that 2^power = value in GF(256) modulo 100011101 arithmetic,
     * except for when the value is 0, in which case the power is invalid
     * and should not be referenced.
     *
     * @param power The power of two for this GF256Number
     * @param value The value of this GF256Number
     */
    private GF256Number(int power, int value) {
        this.power = power;
        this.value = value;
    }

    /**
     * Generate a mapping array from powers of two to GF256Numbers.
     *
     * @return An array of GF256Numbers with the GF256Number of value
     * 2^i located at index i
     */
    private static GF256Number[] generateNumbersByPower() {
        GF256Number[] result = new GF256Number[GALOIS_FIELD_VALUE];
        GF256Number one = new GF256Number(0, 1);
        result[0] = one;

        for (int power = 1; power < GALOIS_FIELD_VALUE; power++) {
            int prevValue = result[power - 1].value;
            int value = prevValue * 2;
            if (value >= GALOIS_FIELD_VALUE) {
                value = value ^ MODULUS;
            }
            GF256Number number = new GF256Number(power, value);
            result[power] = number;
        }
        return result;
    }

    /**
     * Generate a mapping array from integer values to GF256Numbers.
     * This method requires the static array numbersByPower to be initialized.
     *
     * @return An array of GF256Numbers with the GF256Number of value
     * i located at index i
     */
    private static GF256Number[] generateNumbersByValue() {
        GF256Number[] result = new GF256Number[GALOIS_FIELD_VALUE];
        result[0] = new GF256Number(-1, 0);

        for (int power = 0; power < GALOIS_FIELD_VALUE - 1; power++) {
            GF256Number number = numbersByPower[power];
            result[number.value] = number;
        }
        return result;
    }

    /**
     * Get the GF256Number for a given value.
     *
     * @param value The value of the GF256Number
     */
    public static GF256Number fromValue(int value) {
        return numbersByValue[value];
    }

    /**
     * Get the GF256Number for a given power of two.
     *
     * @param power The power of two of the GF256Number
     */
    public static GF256Number fromPower(int power) {
        return numbersByPower[power];
    }

    /**
     * Get the integer value of this GF256Number.
     *
     * @return The integer value of this GF256Number, between 0 and 255 inclusive
     */
    public int asInt() {
        return value;
    }

    /**
     * Multiply this GF256Number by another GF256Number using GF(256) modulo
     * 100011101 arithmetic.
     *
     * @param other The GF256Number to multiply this by
     * @return A new GF256Number whose value is the product of the two GF256Numbers
     */
    public GF256Number multiply(GF256Number other) {
        if (this.value == 0 || other.value == 0) {
            return numbersByValue[0];
        }
        int power = (this.power + other.power) % (GALOIS_FIELD_VALUE - 1);
        return numbersByPower[power];
    }

    /**
     * Add this GF256Number to another GF256Number using GF(256) modulo
     * 100011101 arithmetic.
     *
     * @param other The GF256Number to add this to
     * @return A new GF256Number whose value is the sum of the two GF256Numbers
     */
    public GF256Number add(GF256Number other) {
        int value = (this.value ^ other.value);
        if (value >= GALOIS_FIELD_VALUE) {
            value ^= MODULUS;
        }
        return numbersByValue[value];
    }

    @Override
    public String toString() {
        return "(2^" + power + " = " + value + ")";
    }
}
