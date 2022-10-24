package data;

import model.BinaryData;
import model.Mode;
import model.Version;

import java.util.List;

/**
 * Data encoder for the Numeric mode
 */
public class NumericDataEncoder implements DataEncoder {
    @Override
    public Mode getMode() {
        return Mode.NUMERIC;
    }

    @Override
    public List<Integer> encode(List<Integer> data, Version version) {
        BinaryData binaryData = new BinaryData();
        binaryData.appendInt(getMode().getIndicator(), 4);
        binaryData.appendInt(data.size(), version.getCharacterCountIndicatorLength(getMode()));

        int i = 0;
        while (i < data.size()) {
            int temp = 0;
            int numBits = 4;
            temp += data.get(i);
            i++;
            for (int j = 0; j < 2; j++) {
                if (i < data.size()) {
                    temp *= 10;
                    temp += data.get(i);
                    numBits += 3;
                    i++;
                }
            }
            binaryData.appendInt(temp, numBits);
        }

        return binaryData.toIntegerList();
    }

    @Override
    public int getEncodedBitStreamLength(int numCharacters, Version version) {
        int C = version.getCharacterCountIndicatorLength(getMode());
        int R = (numCharacters % 3 == 0 ? 0 : numCharacters % 3 == 1 ? 4 : 7);
        return 4 + C + (10 * (numCharacters / 3)) + R;
    }
}
