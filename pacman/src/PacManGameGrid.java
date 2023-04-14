package src;
import ch.aplu.jgamegrid.*;


/**
 * PacManGameGrid class representing the game grid, which primarily deals with visualizing inanimate objects
 * on the grid via enumerated identification.
 */
public class PacManGameGrid {
    // enumerated block, or inanimate objects, type
    enum BlockType {
        WALL, PILL, SPACE, GOLD, ICE, ERROR
    }

    // number of horizontal cells of the grid
    private final int numHorizontalCells;
    // number of vertical cells of the grid
    private final int numVerticalCells;
    // the grid data structure, represented by a 2-dimensional array of blocks
    private final BlockType[][] mazeArray;


    /**
     * PacManGameGrid constructor.
     * @param numHorizontalCells the number of horizontal cells of the grid
     * @param numVerticalCells   the number of vertical cells of the grid
     */
    public PacManGameGrid(int numHorizontalCells, int numVerticalCells) {
        this.numHorizontalCells = numHorizontalCells;
        this.numVerticalCells = numVerticalCells;
        mazeArray = new BlockType[numVerticalCells][numHorizontalCells];
        String maze =
                        "xxxxxxxxxxxxxxxxxxxx" + // 0
                        "x....x....g...x....x" + // 1
                        "xxe.x.xxxxxx.x.xx.x" + // 2
                        "x.x.......i.g....x.x" + // 3
                        "x.x.xx.xx  xx.xx.x.x" + // 4
                        "x......x    x......x" + // 5
                        "x.x.xx.xxxxxx.xx.x.x" + // 6
                        "x.x......gi......x.x" + // 7
                        "site.x.xxxxxx.x.xx.x" + // 8
                        "x...gx....g...x....x" + // 9
                        "xxxxxxxxxxxxxxxxxxxx";  // 10

        // Copy structure into integer array
        for (int i = 0; i < numVerticalCells; i++)
            for (int k = 0; k < numHorizontalCells; k++) {
                BlockType value = toType(maze.charAt(numHorizontalCells * i + k));
                mazeArray[i][k] = value;
            }
    }

    /**
     * Get the block type from a specific cell in a specified location.
     * @param location the specified location
     * @return         the block type in said location
     */
    public BlockType getCell(Location location)
    {
        return mazeArray[location.y][location.x];
    }

    /**
     * Get the number of horizontal cells of the grid.
     * @return the number of horizontal cells
     */
    public int getNumHorizontalCells() {
        return numHorizontalCells;
    }

    /**
     * Get the number of vertical cells of the grid.
     * @return the number of vertical cells
     */
    public int getNumVerticalCells() {
        return numVerticalCells;
    }

    /**
     * Get the grid itself, represented by a 2-dimensional array.
     * @return the grid
     */
    public BlockType[][] getMazeArray() {
        return mazeArray;
    }

    /**
     * Convert a maze string where each cell is represented by a character, to its corresponding block type.
     * @param c cell character
     * @return  its block type
     */
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
}
