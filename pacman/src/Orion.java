package src;

import ch.aplu.jgamegrid.Location;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Orion class extended from abstract parent Monster.
 * Enemies in the game whose movement is based solely on protecting gold pieces.
 * @see Monster
 * @see Gold
 */
public class Orion extends Monster {
    // Name of class required for GameCallback
    private static final MonsterType TYPE = MonsterType.Orion;
    // Constructor arguments
    public static final int numOrionImages = 1;
    public static final String directory = "sprites/m_orion.gif";

    // Variables to keep track of positions of gold pieces for Orion's movement logic
    private HashableLocation currDestination = null;
    private boolean hasDestination = false;
    private final HashMap<HashableLocation, Boolean> goldVisited = new HashMap<>();
    protected HashMap<HashableLocation, Boolean> goldPacmanAte = new HashMap<>();

    // Constants used to check for non-diagonal directions
    private static final int CHECK_NON_DIAGONAL = 10;
    private static final int NON_DIAGONAL = 0;
    private static final int LIST_START = 0;

    /**
     * Orion constructor
     * @param manager stores locations of all game objects
     */
    public Orion(ObjectManager manager) {
        super(manager, false, directory, numOrionImages);
        assert manager != null;
        setType(TYPE);
        // Assert there are actually items for Orion to store
        assert ! this.getManager().getItems().isEmpty();
        this.makeGoldMaps();
    }

    /**
     * <ul>
     * <li>Moves Orion to its next location, based on walking through every gold location randomly;
     * prioritizing golds that Pacman has yet to eat.</li>
     * <li>Orion has walk cycles; a walk cycle starts when Orion determines the first gold location to walk to,
     * and ends when it arrives at its last unvisited gold location.</li>
     * </ul>
     * Overridden from Monster.
     */
    @Override
    protected void moveApproach() {
        // If already are at destination or destination is null, find a new destination to walk to
        if (this.currDestination != null &&
                this.currDestination.location().getX() == this.getLocation().getX() &&
                this.currDestination.location().getY() == this.getLocation().getY()) {
            this.hasDestination = false;
            this.goldVisited.put(this.currDestination, true);
            // After Orion finishes walk cycle, reset
            // its cycle by setting all goldLocations values to false
            if (this.checkIfAllVisited(new ArrayList<>(this.goldVisited.keySet())))
                goldVisited.replaceAll((l, v) -> false);
        }

        if (!hasDestination) this.findNewGold();

        // Now we go towards the direction of this new location
        Location orionLocation = this.getLocation();

        // Orion monster can only go vertically and horizontally (it doesn't fly)
        // Want to go towards direction where distance to gold is minimized
        int minDistance = Integer.MAX_VALUE;
        ArrayList<Location> possibleLocations = new ArrayList<>();
        Location toMove;
        for (Location.CompassDirection dir : Location.CompassDirection.values()) {
            if (dir.getDirection()%CHECK_NON_DIAGONAL == NON_DIAGONAL) {
                Location currLocation = orionLocation.getNeighbourLocation(dir);
                int distanceToGold = currLocation.getDistanceTo(this.currDestination.location());
                // To prevent Orion from just going to the same 2 locations repeatedly,
                // need to track visited locations with visited list
                if (this.canMove(currLocation) && this.notVisited(currLocation) && distanceToGold <= minDistance) {
                    // Keep track of all possible tying directions
                    if (distanceToGold < minDistance) {
                        minDistance = distanceToGold;
                        possibleLocations = new ArrayList<>();
                    }
                    possibleLocations.add(currLocation);
                }
            }
        }

        // In case every move has been visited already, just
        // find the immediate place you can move to
        if (possibleLocations.isEmpty()) {
            Location.CompassDirection[] directions = Location.CompassDirection.values();
            while(true) {
                int currIndex = this.RANDOMIZER.nextInt(LIST_START, directions.length);
                Location.CompassDirection dir = directions[currIndex];
                Location newLocation = this.getLocation().getNeighbourLocation(dir);
                if (this.canMove(newLocation) &&
                        dir.getDirection()%CHECK_NON_DIAGONAL == NON_DIAGONAL) {
                    toMove = newLocation;
                    break;
                }
            }
        }

        // There may be more than one unvisited location that minimizes distance
        // to a gold, randomly select from these options
        else {
            int randomIndex = this.RANDOMIZER.nextInt(LIST_START, possibleLocations.size());
            toMove = possibleLocations.get(randomIndex);
        }

        // Now when the move has been decided, can move Orion to the desired piece
        this.addVisitedList(toMove);
        this.setLocation(toMove);
    }

    /**
     * Helper function for moveApproach that decides the next gold piece location Orion moves to.
     */
    private void findNewGold() {
        // keep track of the gold pieces that have and have not been visited
        HashMap<HashableLocation, Boolean> notTaken = new HashMap<>();

        // Loop through all the possible gold coins in the game
        for (HashableLocation loc : this.goldPacmanAte.keySet()) {
            // Prioritize any gold coin that pacman hasn't eaten yet
            if (!this.goldVisited.get(loc)) {
                // map each gold location onto a hashmap depending
                // on whether said gold was taken already
                HashableLocation.putLocationHash(notTaken, loc.location(), true);
            }
        }
        // If there are still golds pacman hasn't eaten yet and Orion hasn't visited
        ArrayList<HashableLocation> goldsToIterate = new ArrayList<>(notTaken.keySet());

        // Otherwise randomly check from all possible gold locations
        if (goldsToIterate.isEmpty() || this.checkIfAllVisited(goldsToIterate)) {
            goldsToIterate = new ArrayList<>(this.goldVisited.keySet());
        }

        // randomly pick which gold to go to, and set new location
        HashableLocation newLocation = this.getRandomLocation(goldsToIterate);
        this.hasDestination = true;
        this.currDestination = newLocation;
    }

    /**
     * This function initializes 2 key maps needed for Orion:
     * <ul>
     * <li>goldLocations: gold piece locations visited for each walking cycle;
     * <li>goldPacmanAte: gold pieces Pacman ate already
     * </ul>
     */
    private void makeGoldMaps() {
        for (HashableLocation loc: this.getManager().getItems().keySet()) {
            if (this.getManager().getItems().get(loc) instanceof Gold) {
                this.goldVisited.put(loc, false);
                this.goldPacmanAte.put(loc, false);
            }
        }
    }

    /**
     * Check if a given list of gold piece locations have been visited by Orion already for a given walk cycle
     * @param golds: List of a number (not necessarily all) gold piece locations
     * @return       boolean indicating if all golds in list were visited already
     * @see          HashableLocation
     */
    private boolean checkIfAllVisited(ArrayList<HashableLocation> golds) {
        for (HashableLocation loc : golds)
            if (!this.goldVisited.get(loc))
                return false;
        return true;
    }

    /**
     * Randomly pick a gold location from a given list
     * of gold locations that IS NOT YET VISITED in Orion's
     * walk cycle
     * @param golds: List of a number (not necessarily all) gold
     *               piece locations
     * @return Random location from list of gold locations
     * @see    HashableLocation
     */
    private HashableLocation getRandomLocation(ArrayList<HashableLocation> golds) {
        // Make a new arraylist where from the list of golds, none are either
        // visited or the exact same location Orion is in
        ArrayList<HashableLocation> goldsToCheck = new ArrayList<>();
        for (HashableLocation loc : golds) {
            if (!this.goldVisited.get(loc) &&
                    (loc.getX() != this.getLocation().getX() ||
                    loc.getY() != this.getLocation().getY())) {
                goldsToCheck.add(loc);
            }
        }

        // Now return a random location from this new list
        int randomIndex = this.RANDOMIZER.nextInt(LIST_START, goldsToCheck.size());
        return goldsToCheck.get(randomIndex);
    }
}


