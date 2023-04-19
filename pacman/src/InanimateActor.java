package src;

public class InanimateActor {
    // character representing specific block
    private static final char WALL_CHAR = 'x';
    private static final char SPACE_CHAR = ' ';
    private static final char PILL_CHAR = '.';
    private static final char GOLD_CHAR = 'g';
    private static final char ICE_CHAR = 'i';
    private static final char ERROR_CHAR = '\0';


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
        WALL(WALL_CHAR),
        PILL(PILL_CHAR),
        SPACE(SPACE_CHAR),
        GOLD(GOLD_CHAR),
        ICE(ICE_CHAR),
        ERROR(ERROR_CHAR);
        public final char BLOCK_CHAR;
        BlockType(char BLOCK_CHAR) {
            this.BLOCK_CHAR = BLOCK_CHAR;
        }
    }
}
