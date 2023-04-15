package src;

import ch.aplu.jgamegrid.Location;
import java.util.ArrayList;

/**
 * Monster Child Class specific for Aliens
 * Enemies in the game who move ONLY to get
 * closer to Pacman, and can also move diagonally
 */
public class Alien extends Monster {
    // Name of class needed for GameCallback
    private static final String ALIEN_NAME = "Alien";
    // Need these variables for implementation with
    // super constructor
    public static final int numAlienImages = 1;
    public static final String directory = "sprites/m_alien.gif";

    /**
     * Alien constructor
     * @param manager    stores locations of all game objects
     */
    public Alien(ObjectManager manager) {
        super(manager, false, directory, numAlienImages);
        assert manager != null;
        setName(ALIEN_NAME);
    }

    /**
     * Moves Alien to its next location, purely
     * determined by which 8 neighboring locations
     * it can move to and are closest to Pacman
     */
    @Override
    public void moveApproach() {
        // Aliens pick from 8 of the different directions it can walk towards,
        // need to find the direction that is closest to pacman
        ArrayList<Location> possibleMoves = new ArrayList<>();
        int minDistance = Integer.MAX_VALUE;
        Location pacmanLocation = getManager().getPacActor().getLocation();

        for (Location.CompassDirection dir: Location.CompassDirection.values()) {
            Location currLocation = this.getLocation().getNeighbourLocation(dir);
            int distanceToPacman = currLocation.getDistanceTo(pacmanLocation);

            // Make sure to account for ties, since this means we need
            // to randomly pick from all tying directions
            if (this.canMove(currLocation) && distanceToPacman <= minDistance) {
                if (distanceToPacman < minDistance) {
                    minDistance = distanceToPacman;
                    possibleMoves = new ArrayList<>();
                }
                possibleMoves.add(currLocation);
            }
        }

        // Randomly pick a direction from all possible minimum
        // distance directions
        int listIndex = this.randomizer.nextInt(0, possibleMoves.size());
        this.setLocation(possibleMoves.get(listIndex));
    }
}
