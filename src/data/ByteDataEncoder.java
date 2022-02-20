package data;

import model.BinaryData;
import model.Mode;
import model.Version;

import java.util.List;

/**
 * Data encoder for the Byte mode
 */
public class ByteDataEncoder implements DataEncoder {
    @Override
    public Mode getMode() {
        return Mode.BYTE;
    }

    @Override
    public List<Integer> encode(List<Integer> data, Version version) {
        BinaryData binaryData = new BinaryData();
        binaryData.appendInt(getMode().getIndicator(), 4);
        binaryData.appendInt(data.size(), version.getCharacterCountIndicatorLength(getMode()));

        for (int i : data) {
            binaryData.appendInt(i, 8);
        }

        return binaryData.toIntegerList();
    }

    @Override
    public int getEncodedBitStreamLength(int numCharacters, Version version) {
        int C =  version.getCharacterCountIndicatorLength(getMode());
        return 4 + C + 8 * numCharacters;
    }
}
