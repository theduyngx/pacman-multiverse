// PacGrid.java
package src;

import ch.aplu.jgamegrid.*;

public class PacManGameGrid {
    enum BlockType {
        WALL, PILL, SPACE, GOLD, ICE, ERROR
    }
    private final int numHorizontalCells;
    private final int numVerticalCells;
    private final BlockType[][] mazeArray;

    public PacManGameGrid(int numHorizontalCells, int numVerticalCells) {
        this.numHorizontalCells = numHorizontalCells;
        this.numVerticalCells = numVerticalCells;
        mazeArray = new BlockType[numVerticalCells][numHorizontalCells];
        String maze =
                        "xxxxxxxxxxxxxxxxxxxx" + // 0
                        "x....x....g...x....x" + // 1
                        "xgxx.x.xxxxxx.x.xx.x" + // 2
                        "x.x.......i.g....x.x" + // 3
                        "x.x.xx.xx  xx.xx.x.x" + // 4
                        "x......x    x......x" + // 5
                        "x.x.xx.xxxxxx.xx.x.x" + // 6
                        "x.x......gi......x.x" + // 7
                        "xixx.x.xxxxxx.x.xx.x" + // 8
                        "x...gx....g...x....x" + // 9
                        "xxxxxxxxxxxxxxxxxxxx";  // 10

        // Copy structure into integer array
        for (int i = 0; i < numVerticalCells; i++)
            for (int k = 0; k < numHorizontalCells; k++) {
                BlockType value = toType(maze.charAt(numHorizontalCells * i + k));
                mazeArray[i][k] = value;
            }
    }

    public BlockType getCell(Location location)
    {
        return mazeArray[location.y][location.x];
    }
    private BlockType toType(char c) {
        return switch (c) {
            case 'x' -> BlockType.WALL;
            case '.' -> BlockType.PILL;
            case ' ' -> BlockType.SPACE;
            case 'g' -> BlockType.GOLD;
            case 'i' -> BlockType.ICE;
            default  -> BlockType.ERROR;
        };
    }

    public int getNumHorizontalCells() {
        return numHorizontalCells;
    }

    public int getNumVerticalCells() {
        return numVerticalCells;
    }

    public BlockType[][] getMazeArray() {
        return mazeArray;
    }
}
