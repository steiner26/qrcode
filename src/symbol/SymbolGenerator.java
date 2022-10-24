package symbol;

import error.ErrorCorrectionUtils;
import model.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class SymbolGenerator {

    private static final int BLACK_MODULE = 0x000000;

    private static final int WHITE_MODULE = 0xFFFFFF;

    public SymbolGenerator() {
    }

    public BufferedImage generateSymbol(SymbolInfo symbolInfo) {
        int sideLength = symbolInfo.getSymbolSideLength();
        BufferedImage image = new BufferedImage(sideLength, sideLength, BufferedImage.TYPE_INT_RGB);
        addPatternsAndInfo(symbolInfo, image);

        return image;
    }

    private void addPatternsAndInfo(SymbolInfo symbolInfo, BufferedImage image) {
        addDetectionPatterns(symbolInfo, image);
        addAlignmentPatterns(symbolInfo, image);
        addTimingPatterns(symbolInfo, image);
        addFormatInfo(symbolInfo, image);
        addVersionInfo(symbolInfo, image);
    }

    private void addDetectionPatterns(SymbolInfo symbolInfo, BufferedImage image) {
        // add detection patterns and extra white ring around detection patterns
        for (Coordinate center : symbolInfo.getDetectionPatternCenters()) {
            Coordinate topLeft = new Coordinate(
                    center.getI() - SymbolInfo.DETECTION_PATTERN_RADIUS - 1,
                    center.getJ() - SymbolInfo.DETECTION_PATTERN_RADIUS - 1
            );
            Coordinate bottomRight = new Coordinate(
                    center.getI() + SymbolInfo.DETECTION_PATTERN_RADIUS + 1,
                    center.getJ() + SymbolInfo.DETECTION_PATTERN_RADIUS + 1
            );

            Coordinate.withinRectangle(topLeft, bottomRight).forEach((Coordinate pixel) -> {
                if (symbolInfo.isCoordinateOutOfBounds(pixel)) {
                    return;
                }
                int distance = pixel.radialDistanceTo(center);
                if (distance == 2 || distance == 4) {
                    image.setRGB(pixel.getJ(), pixel.getI(), WHITE_MODULE);
                } else {
                    image.setRGB(pixel.getJ(), pixel.getI(), BLACK_MODULE);
                }
            });
        }
    }

    private void addAlignmentPatterns(SymbolInfo symbolInfo, BufferedImage image) {
        for (Coordinate center : symbolInfo.getAlignmentPatternCenters()) {
            Coordinate topLeft = new Coordinate(
                    center.getI() - SymbolInfo.ALIGNMENT_PATTERN_RADIUS,
                    center.getJ() - SymbolInfo.ALIGNMENT_PATTERN_RADIUS
            );
            Coordinate bottomRight = new Coordinate(
                    center.getI() + SymbolInfo.ALIGNMENT_PATTERN_RADIUS,
                    center.getJ() + SymbolInfo.ALIGNMENT_PATTERN_RADIUS
            );

            Coordinate.withinRectangle(topLeft, bottomRight).forEach((Coordinate pixel) -> {
                if (pixel.radialDistanceTo(center) == 1) {
                    image.setRGB(pixel.getJ(), pixel.getI(), WHITE_MODULE);
                } else {
                    image.setRGB(pixel.getJ(), pixel.getI(), BLACK_MODULE);
                }
            });
        }
    }

    private void addTimingPatterns(SymbolInfo symbolInfo, BufferedImage image) {
        for (int n = SymbolInfo.TIMING_PATTERN_OFFSET; n < symbolInfo.getSymbolSideLength() - SymbolInfo.TIMING_PATTERN_OFFSET; n++) {
            if (n % 2 == 0) {
                image.setRGB(n, SymbolInfo.TIMING_PATTERN_COORDINATE, BLACK_MODULE);
                image.setRGB(SymbolInfo.TIMING_PATTERN_COORDINATE, n, BLACK_MODULE);
            } else {
                image.setRGB(n, SymbolInfo.TIMING_PATTERN_COORDINATE, WHITE_MODULE);
                image.setRGB(SymbolInfo.TIMING_PATTERN_COORDINATE, n, WHITE_MODULE);
            }
        }
    }

    private void addFormatInfo(SymbolInfo symbolInfo, BufferedImage image) {
        BinaryData formatInfo = symbolInfo.getFormatInfo();

        List<Coordinate> topLeftFormatInfoCoordinates = symbolInfo.getTopLeftFormatInfoCoordinates();
        List<Coordinate> splitFormatInfoCoordinates = symbolInfo.getSplitFormatInfoCoordinates();

        for (int i = 0; i < formatInfo.size(); i++) {
            Coordinate topLeftCoordinate = topLeftFormatInfoCoordinates.get(i);
            Coordinate splitCoordinate = splitFormatInfoCoordinates.get(i);
            int rgb = formatInfo.isBitSet(i) ? BLACK_MODULE : WHITE_MODULE;

            image.setRGB(topLeftCoordinate.getJ(), topLeftCoordinate.getI(), rgb);
            image.setRGB(splitCoordinate.getJ(), splitCoordinate.getI(), rgb);

            if (i == 8) {
                image.setRGB(splitCoordinate.getJ(), splitCoordinate.getI() - 1, BLACK_MODULE);
            }
        }
    }

    private void addVersionInfo(SymbolInfo symbolInfo, BufferedImage image) {
        if (symbolInfo.hasVersionInfo()) {
            BinaryData versionInfo = symbolInfo.getVersionInfo();
            ErrorCorrectionUtils.appendErrorCorrectionBitsToVersionInfo(versionInfo);

            List<Coordinate> topRightVersionInfoCoordinates = symbolInfo.getTopRightVersionInfoCoordinates();
            List<Coordinate> bottomLeftVersionInfoCoordinates = symbolInfo.getBottomLeftVersionInfoCoordinates();

            for (int i = 0; i < versionInfo.size(); i++) {
                Coordinate topRightCoordinate = topRightVersionInfoCoordinates.get(i);
                Coordinate bottomLeftCoordinate = bottomLeftVersionInfoCoordinates.get(i);

                int rgb = versionInfo.isBitSet(i) ? BLACK_MODULE : WHITE_MODULE;
                image.setRGB(topRightCoordinate.getJ(), topRightCoordinate.getI(), rgb);
                image.setRGB(bottomLeftCoordinate.getJ(), bottomLeftCoordinate.getI(), rgb);
            }
        }
    }

    public static void main(String[] args) {
        SymbolInfo info = new SymbolInfo(new Version(14), ErrorCorrectionLevel.M, MaskPattern.FIVE);
        BufferedImage symbol = new SymbolGenerator().generateSymbol(info);
        File outputFile = new File("data/symbol.png");
        try {
            ImageIO.write(symbol, "PNG", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
