package error;

import model.BinaryData;
import model.Version;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Util class for error correction, which is described in section 8.5 of
 * the QR Code specification ISO/IEC 18004.
 */
public class ErrorCorrectionUtils {

    /**
     * Generate a specified number of error correction codewords for a given message.
     * This implements error correction from section 8.5 of the QR Code specification
     * ISO/IEC 18004.
     *
     * @param message The message to generate error correction codewords for. Each byte
     *                of the message should be its own entry in the list
     * @param numCodewords The number of error correction codewords to generate
     * @return The requested number of error correction codewords for the message
     */
    public static List<Integer> getErrorCorrectionCodewords(List<Integer> message, int numCodewords) {
        List<GF256Number> messageCoefficients = message.stream()
                .map(GF256Number::fromValue)
                .collect(Collectors.toList());
        List<GF256Number> generatorCoefficients = getGeneratorPolynomialCoefficients(numCodewords);
        List<Register> registers = Register.createRegisterList(numCodewords);

        // Cache variable to be reused in calculations
        GF256Number cache;

        for (GF256Number m : messageCoefficients) {
            GF256Number input = m.add(registers.get(numCodewords-1).getValue());
            cache = input.multiply(generatorCoefficients.get(0));
            registers.get(0).setNextValue(cache);
            for (int i = 1; i < numCodewords; i++) {
                cache = input.multiply((generatorCoefficients.get(i)));
                cache = cache.add(registers.get(i-1).getValue());
                registers.get(i).setNextValue(cache);
            }
            for (Register r : registers) {
                r.pulse();
            }
        }

        List<Integer> result = registers.stream()
                .map(register -> register.getValue().asInt())
                .collect(Collectors.toList());
        Collections.reverse(result);

        return result;
    }

    /**
     * Get the generator polynomial coefficients for a given degree of the polynomial.
     * <br />
     * The generator polynomial of degree n is calculated by taking the product
     * of (x - 2^0) * ... * (x - 2^n). As a shortcut, the signs of the powers of 2
     * are taken to be positive, as subtraction and addition are the same in
     * bit-wise modulo 2 arithmetic.
     * <br />
     * A polynomial is represented as a list of coefficients, with the coefficient
     * at index i as the coefficient of the x^i term in the polynomial.
     * To generate the next polynomial of degree n, the previous polynomial of
     * degree n G_{n-1} needs to be multiplied by (x + 2^n), which can be
     * represented as (G_{n-1} * x) + (G_{n-1} * 2^n). To multiply by x,
     * the polynomial is shifted up a degree and a coefficient of 0 is inserted
     * for the term x^0. Then to add (G_{n-1} * 2^n), the coefficient that was
     * previously at each degree is multiplied by 2^n and added to the new
     * coefficient at that degree. This gives the new generator polynomial G_n.
     * */
    private static List<GF256Number> getGeneratorPolynomialCoefficients(int degree) {
        // start with a polynomial of (x + 2^0) which is equivalent to (x + 1)
        List<GF256Number> result = new ArrayList<>(degree);
        result.add(GF256Number.fromPower(0));
        result.add(GF256Number.fromPower(0));

        // iteratively generate each polynomial
        for (int n = 1; n < degree; n++) {
            result.add(0, GF256Number.fromValue(0));
            GF256Number next = GF256Number.fromPower(n);
            for (int i = 0; i < result.size() - 1; i++) {
                result.set(i, result.get(i).add(result.get(i+1).multiply(next)));
            }
        }

        // remove the highest degree coefficient of 1 as it is not used in further calculations
        result.remove(result.size() - 1);
        return result;
    }

    /**
     * Append the 10 error correction bits (without masking) to binary data for format information
     * according to appendix C.1 of the QR Code specification ISO/IEC 18004.
     *
     * @param formatInfo Binary data representing format information, with length of 5 bits
     */
    public static void appendErrorCorrectionBitsToFormatInfo(BinaryData formatInfo) {
        List<Integer> generator = Arrays.asList(1, 1, 1, 0, 1, 1, 0, 0, 1, 0, 1);
        appendBCHCode(formatInfo, generator, 15, 5);
    }

    /**
     * Append the 10 error correction bits (without masking) to binary data for format information
     * according to appendix C.1 of the QR Code specification ISO/IEC 18004.
     *
     * @param versionInfo Binary data representing version information, with length of 6 bits
     */
    public static void appendErrorCorrectionBitsToVersionInfo(BinaryData versionInfo) {
        List<Integer> generator = Arrays.asList(1, 0, 1, 0, 0, 1, 0, 0, 1, 1, 1, 1, 1);
        appendBCHCode(versionInfo, generator, 18, 6);
    }

    /**
     * Appends the BCH error correction bits to the given binary data. The error correction bits
     * will have a length of (n-k), and the resulting data will have a total length of n bits
     *
     * @param data The data to append the error correction bits to, with a length of k bits
     * @param generator The generator polynomial to generate the BCH bits from
     * @param n The target length of the message + error correction bits
     * @param k The length of the message
     */
    private static void appendBCHCode(BinaryData data, List<Integer> generator, int n, int k) {
        List<Integer> message = data.toBitList();
        Collections.reverse(message);
        for (int i = k; i < n; i++) {
            message.add(0, 0);
        }

        int messageIndex = findLastSetBit(message);
        int generatorIndex = findLastSetBit(generator);
        while (messageIndex >= n - k) {
            int offset = messageIndex - generatorIndex;
            for (int i = 0; i <= messageIndex; i++) {
                int generatorAccessIndex = i - offset;
                message.set(i, message.get(i) ^ (generatorAccessIndex < 0 ? 0 : generator.get(generatorAccessIndex)));
            }
            messageIndex = findLastSetBit(message);
        }

        for (int i = n - k - 1; i >= 0; i--) {
            data.appendInt(message.get(i), 1);
        }
    }

    /**
     * Find the last entry in the list that is a 1
     *
     * @param bits The list of bits as 0s and 1s, with each bit having an entry in the list
     * @return The last index of the bits where the bit is set to 1
     */
    private static int findLastSetBit(List<Integer> bits) {
        for (int i = bits.size() - 1; i >= 0; i--) {
            if (bits.get(i) == 1) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Main method to test error codeword generation, following the example
     * from <a href="https://www.thonky.com/qr-code-tutorial/error-correction-coding">this website</a>
     */
    public static void main(String[] args) {
        Integer[] numbers = { 32, 91, 11, 120, 209, 114, 220, 77, 67, 64, 236, 17, 236, 17, 236, 17 };
        List<Integer> message = Arrays.asList(numbers);
        List<Integer> codewords = getErrorCorrectionCodewords(message, 10);

        System.out.println(codewords);
        System.out.println(Arrays.equals(codewords.toArray(), new Object[]{196, 35, 39, 119, 235, 215, 231, 226, 93, 23}));

        // Print out version information for each version, with BCH bits appended
        for (int i = 7; i <= 40; i++) {
            Version version = new Version(i);
            BinaryData data = new BinaryData();
            data.appendInt(version.getVersion(), Version.INDICATOR_LENGTH_IN_BITS);
            appendErrorCorrectionBitsToVersionInfo(data);
            System.out.println(i + " : " + data.toBitList());
        }
    }
}
