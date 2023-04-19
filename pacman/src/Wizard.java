package src;
import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Wizard class extended from Monster class.
 * @see Monster
 */
public class Wizard extends Monster {
    // Name of class needed for GameCallback
    private static final MonsterType TYPE = MonsterType.Wizard;

    // Required variables for super constructor
    public static final int NUM_WIZARD_IMAGES = 1;
    public static final String DIRECTORY = "sprites/m_wizard.gif";

    // Constants needed for wizard class
    public static final int LIST_START = 0;
    public static final int BEYOND_WALL = 1;

    /**
     * Wizard constructor
     * @param manager stores locations of all game objects
     */
    public Wizard(ObjectManager manager) {
        super(manager, false, DIRECTORY, NUM_WIZARD_IMAGES);
        assert manager != null;
        setType(TYPE);
    }

    /**
     * Moves Wizard to its next location, movement is randomly selected from its 8 neighboring locations,
     * but also has the ability to walk through walls. Overridden from Monster.
     */
    @Override
    protected Location nextMonsterLocation(int stepSize) {
        // Get the possibleDirections then add each direction int value to the
        // directionValues
        Location.CompassDirection[] possibleDirections = Location.CompassDirection.values();
        ArrayList<Integer> directionValues = new ArrayList<>();
        for (Location.CompassDirection dir : possibleDirections) {
            directionValues.add(dir.getDirection());
        }

        // This checks if we even can return a direction
        Location finalLoc = null;

        // This loop will keep on going until a location is set for the wizard
        // Randomly picks from the 8 possible directions or if it exhausted
        // all possible directions
        while (!directionValues.isEmpty()) {
            int currIndex = this.getRandomizer().nextInt(LIST_START, directionValues.size());
            int currDirection = directionValues.get(currIndex);

            if (this.canMove(currDirection, stepSize)) {
                Location currentLocation = this.getLocation().getAdjacentLocation(currDirection, stepSize);
                finalLoc = currentLocation;
                break;
            }

            // Even if it can't move to that block, it might be able to go to
            // the adjacent block if the space beyond the wall is valid
            else {
                // Whether furious or normal, wizard only looks one step after
                // its chosen location to see if it's a wall or not
                if (this.canMove(currDirection, stepSize+BEYOND_WALL)) {
                    Location beyondWallLocation = this.getLocation().getAdjacentLocation(currDirection,
                            stepSize+BEYOND_WALL);
                    finalLoc = beyondWallLocation;
                    break;
                }
            }
            directionValues.remove(currIndex);
        }

        return finalLoc;
    }
}