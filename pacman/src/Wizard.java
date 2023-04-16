package src;

import ch.aplu.jgamegrid.Location;

public class Wizard extends Monster {
    // Name of class needed for GameCallback
    private static final String WIZARD_NAME = "Wizard";
    // Need these variables for implementation with
    // super constructor
    public static final int NUM_WIZARD_IMAGES = 1;
    public static final String DIRECTORY = "sprites/m_wizard.gif";

    /**
     * Wizard constructor
     * @param manager    stores locations of all game objects
     */
    public Wizard(ObjectManager manager) {
        super(manager, false, DIRECTORY, NUM_WIZARD_IMAGES);
        assert manager != null;
        setName(WIZARD_NAME);
    }

    /**
     * Moves Wizard to its next location, movement is
     * randomly selected from its 8 neighboring locations
     * But also has the ability to walk through walls
     */
    @Override
    public void moveApproach() {
        Location.CompassDirection[] possibleLocations = Location.CompassDirection.values();

        // This loop will keep on going until a location is set for the wizard
        // Randomly picks from the 8 possible directions
        while (true) {
            int currIndex = this.RANDOMIZER.nextInt(0, possibleLocations.length);
            int currDirection = possibleLocations[currIndex].getDirection();
            Location currentLocation = this.getLocation().getNeighbourLocation(currDirection);

            // best case scenario: the wizard found a location to move to instantly
            if (this.canMove(currentLocation)) {
                this.setLocation((currentLocation));
                break;
            }

            // Even if it can't move to that block, it might be able to go to
            // the adjacent block if the space beyond the wall is valid
            else {
                Location beyondWallLocation = currentLocation.getNeighbourLocation(currDirection);
                if (this.canMove(beyondWallLocation)) {
                    this.setLocation(beyondWallLocation);
                    break;
                }
            }

        }
    }
}
