package data;

import model.Version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Util class for data encodation, which is described in section 8.4 of
 * the QR Code specification ISO/IEC 18004.
 */
public class DataEncodationUtils {

    // Test that a list of numbers can be encoded in Numeric mode,
    // and that an arbitrary input can be encoded in Byte mode
    public static void main(String[] args) {
        Integer[] numbers = { 0, 1, 2, 3, 4, 5, 6, 7 };
        List<Integer> data = Arrays.asList(numbers);
        List<Integer> result = new NumericDataEncoder().encode(data, new Version(1));
        System.out.println(result);
        // 00010000 00100000 00001100 01010110 01100001 1|0000000 implied
        // 16       32       12       86       97       -128

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            String line = br.readLine();
            List<Integer> data2 = Arrays.asList(line.chars().boxed().toArray(Integer[]::new));
            // Check if input is all numeric
            System.out.println(data2.stream().allMatch(i -> i >= 48 && i <= 57));
            List<Integer> result2 = new ByteDataEncoder().encode(data2, new Version(1));
            System.out.println(result2);
            // For input 'test'
            // 00010000 01000111 01000110 01010111 00110111 0100|0000
            // 16       71       70       87       55       64
        } catch (IOException ignored) {}
    }
}
