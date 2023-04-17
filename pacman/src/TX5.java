package src;
import ch.aplu.jgamegrid.*;


/**
 * TX5 class extended from Monster class. These are enemies that always move towards Pacman.
 * If unable to do so, they move randomly.
 * @see Monster
 */
public class TX5 extends Monster {
    // Name of class needed for GameCallback
    private static final String TX5_NAME = "TX5";

    // Required variables for super constructor
    public static final int NUM_TX5_IMAGES = 1;
    public static final String DIRECTORY = "sprites/m_tx5.gif";

    /**
     * TX5 Constructor
     * @param manager stores locations of all game objects
     */
    public TX5(ObjectManager manager) {
        super(manager, false, DIRECTORY, NUM_TX5_IMAGES);
        assert manager != null;
        setName(TX5_NAME);
    }

    /**
     * Moves TX5 to its next location, determination of movement is purely to get closer to Pacman;
     * otherwise resorts to random movement. Overridden from Monster.
     */
    @Override
    public void moveApproach() {
        // With TX5, need to base direction to move on the position of pacman
        Location pacLocation = getManager().getPacActor().getLocation();
        double oldDirection = this.getDirection();
        Location.CompassDirection compassDir = getLocation().get4CompassDirectionTo(pacLocation);
        this.setDirection(compassDir);

        // This marks the direction nearest to pacman
        Location next = this.getLocation().getNeighbourLocation(compassDir);
        // Only go to this direction if you can move here, and if it wasn't visited yet
        if (this.canMove(next) && this.notVisited(next))
            this.setLocation(next);

        // If it can't move here, has to move to a random spot,
        // means either turn left, turn right, or move backwards
        else {
            double sign = this.RANDOMIZER.nextDouble();
            this.setDirection(oldDirection);
            this.turn(sign*90);
            next = this.getNextMoveLocation();

            // Check if we can turn this direction
            if (this.canMove(next))
                this.setLocation(next);

            // Otherwise just turn backwards
            else {
                this.setDirection(oldDirection);
                this.turn(180);
                next = this.getNextMoveLocation();
                this.setLocation(next);
            }
        }

        // Record changes in position to game
        this.addVisitedList(next);
    }
}
