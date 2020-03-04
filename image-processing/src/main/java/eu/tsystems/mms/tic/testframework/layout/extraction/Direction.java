
package eu.tsystems.mms.tic.testframework.layout.extraction;

/**
 * User: rnhb
 * Date: 16.05.14
 */
public enum Direction {
    RIGHT(1, 0),
    DOWN(0, 1),
    LEFT(-1, 0),
    UP(0, -1),
    NONE(0, 0);

    Direction(int x, int y) {
        dX = x;
        dY = y;
    }

    public int dX;
    public int dY;

    public Direction next() {
        if (this == RIGHT) {
            return DOWN;
        }
        if (this == DOWN) {
            return LEFT;
        }
        if (this == LEFT) {
            return UP;
        }
        if (this == UP) {
            return RIGHT;
        }
        return NONE;
    }

    public Direction previous() {
        if (this == RIGHT) {
            return UP;
        }
        if (this == DOWN) {
            return RIGHT;
        }
        if (this == LEFT) {
            return DOWN;
        }
        if (this == UP) {
            return LEFT;
        }
        return NONE;
    }
}
