package src;
import ch.aplu.jgamegrid.*;


/**
 * PacManGameGrid class representing the game grid, which primarily deals with visualizing inanimate objects
 * on the grid via enumerated identification.
 */
public class PacManGameGrid {
    // grid constants
    private final int X_LEFT;
    private final int Y_TOP;
    private final int X_RIGHT;
    private final int Y_BOTTOM;
    // overestimated maximal distance between any 2 given locations
    public final int INF;

    // number of horizontal cells of the grid
    private final int numHorizontalCells;
    // number of vertical cells of the grid
    private final int numVerticalCells;
    // the grid data structure, represented by a 2-dimensional array of blocks
    private final BlockType[][] mazeArray;

    // character representing specific block
    private static final char WALL_CHAR = 'x';
    private static final char SPACE_CHAR = ' ';
    private static final char PILL_CHAR = '.';
    private static final char GOLD_CHAR = 'g';
    private static final char ICE_CHAR = 'i';


    /**
     * Enumerated block, or inanimate objects, type. This includes
     * <ul>
     *     <li>WALL  - obstructed block which cannot be bypassed
     *     <li>PILL  - the pill item required to be eaten by pacman to win
     *     <li>SPACE - the empty space
     *     <li>GOLD  - the gold piece required to be eaten, but also aggravates monsters
     *     <li>ICE   - the ice piece not required to be eaten, but freezes monsters
     *     <li>ERROR - error block (nonexistent)
     * </ul>
     */
    public enum BlockType {
        WALL, PILL, SPACE, GOLD, ICE, ERROR
    }


    /**
     * PacManGameGrid constructor.
     * @param numHorizontalCells the number of horizontal cells of the grid
     * @param numVerticalCells   the number of vertical cells of the grid
     */
    public PacManGameGrid(int numHorizontalCells, int numVerticalCells) {
        this.numHorizontalCells = numHorizontalCells;
        this.numVerticalCells = numVerticalCells;
        this.INF = numHorizontalCells + numVerticalCells;

        // Setup grid border
        X_LEFT   = 0;
        Y_TOP    = 0;
        X_RIGHT  = numHorizontalCells;
        Y_BOTTOM = numVerticalCells;
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

    /**
     * Get the block type from a specific cell in a specified location.
     * @param location the specified location
     * @return         the block type in said location
     * @see            Location
     */
    public BlockType getCell(Location location)
    {
        return mazeArray[location.y][location.x];
    }

    /**
     * Override existing mazeArray cell with value given in .properties
     * @param location the specified location
     * @param value    the value to be replaced with
     * @see            Location
     * @see            BlockType
     */
    protected void setCell(Location location, BlockType value) {
        mazeArray[location.x][location.y] = value;
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
     * @see    BlockType
     */
    public BlockType[][] getMazeArray() {
        return mazeArray;
    }

    /**
     * Get leftmost x-coordinate of the grid; used for border checking when initiating movement.
     * @return leftmost x-coordinate
     */
    public int getXLeft() {
        return X_LEFT;
    }

    /**
     * Get topmost y-coordinate of the grid; used for border checking when initiating movement.
     * @return leftmost y-coordinate
     */
    public int getYTop() {
        return Y_TOP;
    }

    /**
     * Get rightmost x-coordinate of the grid; used for border checking when initiating movement.
     * @return rightmost x-coordinate
     */
    public int getXRight() {
        return X_RIGHT;
    }

    /**
     * Get bottommost y-coordinate of the grid; used for border checking when initiating movement.
     * @return bottommost y-coordinate
     */
    public int getYBottom() {
        return Y_BOTTOM;
    }

    /**
     * Convert a maze string where each cell is represented by a character, to its corresponding block type.
     * @param c cell character
     * @return  its block type
     * @see     BlockType
     */
    private BlockType toType(char c) {
        return switch (c) {
            case WALL_CHAR  -> BlockType.WALL;
            case PILL_CHAR  -> BlockType.PILL;
            case GOLD_CHAR  -> BlockType.GOLD;
            case ICE_CHAR   -> BlockType.ICE;
            case SPACE_CHAR -> BlockType.SPACE;
            default         -> BlockType.ERROR;
        };
    }
}
