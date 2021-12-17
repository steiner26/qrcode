package error;

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
     *
     * The generator polynomial of degree n is calculated by taking the product
     * of (x - 2^0) * ... * (x - 2^n). As a shortcut, the signs of the powers of 2
     * are taken to be positive, as subtraction and addition are the same in
     * bit-wise modulo 2 arithmetic.
     *
     * A polynomial is represented as a list of coefficients, with the coefficient
     * at index i as the coefficient of the x^i term in the polynomial.
     *
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
     * Main method to test error codeword generation, following the example
     * from https://www.thonky.com/qr-code-tutorial/error-correction-coding
     */
    public static void main(String[] args) {
        Integer[] numbers = { 32, 91, 11, 120, 209, 114, 220, 77, 67, 64, 236, 17, 236, 17, 236, 17 };
        List<Integer> message = Arrays.asList(numbers);
        List<Integer> codewords = getErrorCorrectionCodewords(message, 10);

        System.out.println(codewords);
        System.out.println(Arrays.equals(codewords.toArray(), new Object[]{196, 35, 39, 119, 235, 215, 231, 226, 93, 23}));
    }
}
