package src;

import ch.aplu.jgamegrid.Location;

public class Wizard extends Monster {
    private static final String WIZARD_NAME = "Wizard";
    public static final int numWizardImages = 1;
    public static final String directory = "sprites/m_wizard.gif";

    public Wizard(Game game) {
        super(game, false, directory, numWizardImages);
        setName(WIZARD_NAME);
    }

    @Override
    public void walkApproach() {
        int[] movesUsed = new int[8];
        int minDistance = Integer.MAX_VALUE;
        Location pacmanLocation = getManager().getPacActor().getLocation();
        Location.CompassDirection[] possibleLocations = Location.CompassDirection.values();

        // This loop will keep on going until a location is set for the wizard
        // Randomly picks from the 8 possible directions
        while (true) {
            int currIndex = this.randomizer.nextInt(0, possibleLocations.length);
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
