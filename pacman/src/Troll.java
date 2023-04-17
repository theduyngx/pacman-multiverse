package src;
import ch.aplu.jgamegrid.*;


/**
 * Troll class extended from abstract parent Monster.
 * Enemies in the game whose movement is completely random.
 * @see Monster
 */
public class Troll extends Monster {
    // Type of monster needed for GameCallback
    private static final MonsterType TYPE = MonsterType.Troll;

    // Variables used for super's constructor
    public static final int NUM_TROLL_IMAGES = 1;
    public static final String DIRECTORY = "sprites/m_troll.gif";

    /**
     * Troll constructor
     * @param manager stores locations of all game objects
     */
    public Troll(ObjectManager manager) {
        super(manager, false, DIRECTORY, NUM_TROLL_IMAGES);
        assert manager != null;
        setType(TYPE);
    }

    /**
     * Moves troll to its next location, determination of movement is completely random
     */
    @Override
    protected void moveApproach() {
        double oldDirection = this.getDirection();
        // Should be int but I don't know what happened
        double sign = this.RANDOMIZER.nextDouble();
        this.setDirection(oldDirection);
        this.turn(sign*90);

        Location next = getNextMoveLocation();

        // First get a random direction to go to (left or right)
        if (this.canMove(next))
            this.setLocation(next);

        // Collision occurs going first given direction
        else {
            // Check if you can go the opposite turn, either left or right
            this.setDirection(oldDirection);
            this.turn(-sign*90);
            next = this.getNextMoveLocation();

            if (this.canMove(next))
                this.setLocation(next);

            // If nothing really worked, just go backwards
            else {
                this.setDirection(oldDirection);
                this.turn(180);
                next = this.getNextMoveLocation();
                this.setLocation(next);
            }
        }

        // Tell game to change monster's location and store this as visited
        this.addVisitedList(next);
    }
}
