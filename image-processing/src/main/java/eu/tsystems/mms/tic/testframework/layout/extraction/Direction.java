/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Peter Lehmann
 */
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
