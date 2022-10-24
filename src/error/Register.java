package error;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a register to be used in error correction codeword
 * generation as described in section 8.5.2 of the QR Code specification ISO/IEC 18004.
 */
class Register {

    /** The current value of this register */
    private GF256Number value;

    /** The input value of this register, to be set at the next clock pulse */
    private GF256Number nextValue;

    /**
     * Create a Register with a current value of 0.
     */
    private Register() {
        value = GF256Number.fromValue(0);
    }

    /**
     * Creates a list of Registers of a given length.
     *
     * @param length The number of registers to be created
     * @return A List with the given number of Registers
     */
    public static List<Register> createRegisterList(int length) {
        List<Register> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            result.add(new Register());
        }
        return result;
    }

    /**
     * Set the next value of this Register to be set after the next clock pulse
     *
     * @param value The next value of this register
     */
    public void setNextValue(GF256Number value) {
        nextValue = value;
    }

    /**
     * Get the current value of this Register
     *
     * @return The current value of this Register
     */
    public GF256Number getValue() {
        return value;
    }

    /**
     * Pulse the clock, setting the value of this Register to its next value
     */
    public void pulse() {
        value = nextValue;
        nextValue = null;
    }
}
