package model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Coordinate {

    private final int i;
    private final int j;

    /**
     * Create a new coordinate with given coordinates.
     *
     * @param i The horizontal coordinate starting from the top left
     * @param j The vertical coordinate starting from the top left
     */
    public Coordinate(int i, int j) {
        this.i = i;
        this.j = j;
    }

    /**
     * Create a list of coordinates for all coordinates within a rectangular region, inclusive of the coordinates provided
     *
     * @param topLeft     The top left coordinate of the rectangle
     * @param bottomRight The bottom right coordinate of the rectangle
     * @return A list of coordinates, going down columns from the top right coordinate
     */
    public static List<Coordinate> withinRectangle(Coordinate topLeft, Coordinate bottomRight) {
        return IntStream.rangeClosed(topLeft.getI(), bottomRight.getI()).boxed().flatMap(
                (Integer i) -> IntStream.rangeClosed(topLeft.getJ(), bottomRight.getJ()).boxed().map(
                        (Integer j) -> new Coordinate(i, j)
                )
        ).collect(Collectors.toList());
    }

    /**
     * The i coordinate of this coordinate pair
     *
     * @return The i coordinate
     */
    public int getI() {
        return i;
    }

    /**
     * The j coordinate of this coordinate pair
     *
     * @return The j coordinate
     */
    public int getJ() {
        return j;
    }

    /**
     * The radial distance to another coordinate, defined as the largest of the difference between both i and j coordinates.
     *
     * @param other The coordinate to calculate distance to
     * @return The radial distance to the other coordinate
     */
    public int radialDistanceTo(Coordinate other) {
        return Math.max(Math.abs(i - other.getI()), Math.abs(j - other.getJ()));
    }

    @Override
    public String toString() {
        return "Coordinate{" +
                "i=" + i +
                ", j=" + j +
                '}';
    }

}
