package model;

import error.ErrorCorrectionUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SymbolInfo {

    public static final int DETECTION_PATTERN_DIAMETER = 7;
    public static final int DETECTION_PATTERN_RADIUS = 3;

    public static final int ALIGNMENT_PATTERN_DIAMETER = 5;
    public static final int ALIGNMENT_PATTERN_RADIUS = 2;

    public static final int TIMING_PATTERN_COORDINATE = 6;
    public static final int TIMING_PATTERN_OFFSET = 8;

    public static final List<Boolean> FORMAT_INFO_MASK_PATTERN =
            Arrays.asList(true, false, true, false, true, false, false, false, false, false, true, false, false, true, false);

    private Version version;

    private ErrorCorrectionLevel errorCorrectionLevel;

    private MaskPattern maskPattern;


    private BlockInfo firstBlockInfo;

    private int firstBlockInfoQuantity;

    private BlockInfo secondBlockInfo;

    private int secondBlockInfoQuantity;


    private Coordinate topLeftDetectionPatternCenter;

    private Coordinate topRightDetectionPatternCenter;

    private Coordinate bottomLeftDetectionPatternCenter;

    private List<Coordinate> detectionPatternCenters;

    private List<Coordinate> alignmentPatternCenters;

    private List<Coordinate> topLeftFormatInfoCoordinates;

    private List<Coordinate> splitFormatInfoCoordinates;

    private List<Coordinate> topRightVersionInfoCoordinates;

    private List<Coordinate> bottomLeftVersionInfoCoordinates;

    public static List<SymbolInfo> forEachMaskPattern(Version version, ErrorCorrectionLevel errorCorrectionLevel) {
        return Arrays.stream(MaskPattern.values())
                .map((MaskPattern pattern) -> new SymbolInfo(version, errorCorrectionLevel, pattern))
                .collect(Collectors.toList());
    }

    public SymbolInfo(Version version, ErrorCorrectionLevel errorCorrectionLevel, MaskPattern maskPattern) {
        this.version = version;
        this.errorCorrectionLevel = errorCorrectionLevel;
        this.maskPattern = maskPattern;

        this.topLeftDetectionPatternCenter = new Coordinate(DETECTION_PATTERN_RADIUS, DETECTION_PATTERN_RADIUS);
        this.topRightDetectionPatternCenter = new Coordinate(DETECTION_PATTERN_RADIUS, version.getSymbolSideLength() - DETECTION_PATTERN_RADIUS - 1);
        this.bottomLeftDetectionPatternCenter = new Coordinate(version.getSymbolSideLength() - DETECTION_PATTERN_RADIUS - 1, DETECTION_PATTERN_RADIUS);
        this.detectionPatternCenters = Arrays.asList(topLeftDetectionPatternCenter, topRightDetectionPatternCenter, bottomLeftDetectionPatternCenter);

        this.alignmentPatternCenters = generateAlignmentPatternCenters();
        this.topLeftFormatInfoCoordinates = generateTopLeftFormatInfoCoordinates();
        this.splitFormatInfoCoordinates = generateSplitFormatInfoCoordinates();
        this.topRightVersionInfoCoordinates = generateTopRightVersionInfoCoordinates();
        this.bottomLeftVersionInfoCoordinates = generateBottomLeftVersionInfoCoordinates();
    }

    public int getSymbolSideLength() {
        return version.getSymbolSideLength();
    }

    public boolean isCoordinateOutOfBounds(Coordinate coordinate) {
        int sideLength = getSymbolSideLength();
        return coordinate.getI() < 0 || coordinate.getI() >= sideLength || coordinate.getJ() < 0 || coordinate.getJ() >= sideLength;
    }

    public boolean isCoordinateAvailableForData(int i, int j) {
        Coordinate coordinate = new Coordinate(i, j);

        if (isCoordinateOutOfBounds(coordinate)) {
            return false;
        }
        if (coordinate.getI() == TIMING_PATTERN_COORDINATE || coordinate.getJ() == TIMING_PATTERN_COORDINATE) {
            return false;
        }
        for (Coordinate detectionPatternCenter : detectionPatternCenters) {
            if (coordinate.radialDistanceTo(detectionPatternCenter) <= DETECTION_PATTERN_RADIUS + 1) {
                return false;
            }
        }
        for (Coordinate alignmentPatternCenter : alignmentPatternCenters) {
            if (coordinate.radialDistanceTo(alignmentPatternCenter) <= ALIGNMENT_PATTERN_RADIUS) {
                return false;
            }
        }
        for (Coordinate other : topLeftFormatInfoCoordinates) {
            if (other.equals(coordinate)) {
                return false;
            }
        }
        for (Coordinate other : splitFormatInfoCoordinates) {
            if (other.equals(coordinate)) {
                return false;
            }
        }
        for (Coordinate other : topRightVersionInfoCoordinates) {
            if (other.equals(coordinate)) {
                return false;
            }
        }
        for (Coordinate other : bottomLeftVersionInfoCoordinates) {
            if (other.equals(coordinate)) {
                return false;
            }
        }
        return true;
    }

    public List<Coordinate> getDetectionPatternCenters() {
        return detectionPatternCenters;
    }

    public List<Coordinate> getAlignmentPatternCenters() {
        return alignmentPatternCenters;
    }

    public List<Coordinate> getTopLeftFormatInfoCoordinates() {
        return topLeftFormatInfoCoordinates;
    }

    public List<Coordinate> getSplitFormatInfoCoordinates() {
        return splitFormatInfoCoordinates;
    }

    public List<Coordinate> getTopRightVersionInfoCoordinates() {
        return topRightVersionInfoCoordinates;
    }

    public List<Coordinate> getBottomLeftVersionInfoCoordinates() {
        return bottomLeftVersionInfoCoordinates;
    }

    public BinaryData getFormatInfo() {
        BinaryData data = new BinaryData();
        data.appendInt(errorCorrectionLevel.getIndicator(), ErrorCorrectionLevel.INDICATOR_LENGTH_IN_BITS);
        data.appendInt(maskPattern.getIndicator(), MaskPattern.INDICATOR_LENGTH_IN_BITS);
        ErrorCorrectionUtils.appendErrorCorrectionBitsToFormatInfo(data);
        for (int i = 0; i < data.size(); i++) {
            data.xorBit(i, SymbolInfo.FORMAT_INFO_MASK_PATTERN.get(i));
        }
        return data;
    }

    public boolean hasVersionInfo() {
        return version.hasVersionInfo();
    }

    public BinaryData getVersionInfo() {
        BinaryData data = new BinaryData();
        data.appendInt(version.getVersion(), Version.INDICATOR_LENGTH_IN_BITS);
        return data;
    }

    public BlockInfo getBlockInfo(int blockNumber) {
        return blockNumber <= firstBlockInfoQuantity ? firstBlockInfo : secondBlockInfo;
    }

    private List<Coordinate> generateDetectionPatternCenters() {
        return null;
    }

    private List<Coordinate> generateAlignmentPatternCenters() {
        try (BufferedReader in = new BufferedReader(new FileReader("data/AlignmentPatterns.csv"))) {
            for (int i = 1; i < version.getVersion(); i++) {
                in.readLine();
            }
            String line = in.readLine();
            Pattern pattern = Pattern.compile(", ");
            List<Integer> centerCoordinates = pattern.splitAsStream(line)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
            List<Coordinate> alignmentPatternCenters = new ArrayList<>();
            for (int x = 0; x < centerCoordinates.size(); x++) {
                int i = centerCoordinates.get(x);
                for (int y = 0; y < centerCoordinates.size(); y++) {
                    // given a list of coordinate locations [a, ..., z], the centers (a, a), (a, z) and (z, a) are not used
                    if ((x == 0 && y == 0) || (x == 0 && y == centerCoordinates.size() - 1) || (x == centerCoordinates.size() - 1 && y == 0)) {
                        continue;
                    }
                    int j = centerCoordinates.get(y);
                    alignmentPatternCenters.add(new Coordinate(i, j));
                }
            }
            return alignmentPatternCenters;
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<Coordinate> generateTopLeftFormatInfoCoordinates() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        int iCoordinate = 0;
        int jCoordinate = DETECTION_PATTERN_DIAMETER + 1;

        for (int i = 0; i < 15; i++) {
            coordinates.add(new Coordinate(iCoordinate, jCoordinate));
            if (i <= 6) {
                iCoordinate++;
                if (i == 5) {
                    iCoordinate++;
                }
            } else {
                jCoordinate--;
                if (i == 8) {
                    jCoordinate--;
                }
            }
        }

        return coordinates;
    }

    private List<Coordinate> generateSplitFormatInfoCoordinates() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        int iCoordinate = DETECTION_PATTERN_DIAMETER + 1;
        int jCoordinate = getSymbolSideLength() - 1;

        for (int i = 0; i < 15; i++) {
            coordinates.add(new Coordinate(iCoordinate, jCoordinate));
            if (i <= 6) {
                jCoordinate--;
            } else if (i == 7) {
                iCoordinate = getSymbolSideLength() - DETECTION_PATTERN_DIAMETER;
                jCoordinate = DETECTION_PATTERN_DIAMETER + 1;
            } else {
                iCoordinate++;
            }
        }

        return coordinates;
    }

    private List<Coordinate> generateTopRightVersionInfoCoordinates() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            int column = i % 3;
            int row = i / 3;
            coordinates.add(new Coordinate(row, topRightDetectionPatternCenter.getJ() - 7 + column));
        }
        return coordinates;
    }

    private List<Coordinate> generateBottomLeftVersionInfoCoordinates() {
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            int column = i % 3;
            int row = i / 3;
            coordinates.add(new Coordinate(bottomLeftDetectionPatternCenter.getI() - 7 + column, row));
        }
        return coordinates;
    }


}
