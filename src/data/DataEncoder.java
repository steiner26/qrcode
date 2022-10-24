package data;

import model.Mode;
import model.Version;

import java.util.List;

/**
 * Interface representing an encoder that can encode data for a certain Mode.
 */
public interface DataEncoder {

    /**
     * Get the mode that this encoder can encode data for
     * @return The mode that this encoder encodes data for
     */
    Mode getMode();

    /**
     * Encode a list of bytes for a given version of QR code in the mode of this encoder
     * @param data The bytes of data to encode
     * @param version The version of the QR code to encode data for
     * @return A list of bytes representing the encoded data with no error correction
     */
    List<Integer> encode(List<Integer> data, Version version);

    /**
     * Get the length in bits of the encoded data for a given number of characters and version
     * @param numCharacters The number of characters to be encoded
     * @param version The version of the QR code to encode data for
     * @return The number of bits in the encoded data
     */
    int getEncodedBitStreamLength(int numCharacters, Version version);
}
