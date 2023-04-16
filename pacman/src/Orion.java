package src;

import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Monster Child Class specific for Orion
 * Enemies in the game whose movement is
 * based solely on protecting gold pieces
 */
public class Orion extends Monster {
    // Name of class needed for GameCallback
    private static final String ORION_NAME = "Orion";
    // Need these variables for implementation with
    // super constructor
    public static final int numOrionImages = 1;
    public static final String directory = "sprites/m_orion.gif";

    // These are additional variables to implement Orion movement
    // logic as it needs to keep track of positions of gold pieces
    private HashableLocation currDestination = null;
    private boolean hasDestination = false;
    private final HashMap<HashableLocation, Boolean> goldVisited = new HashMap<>();
    protected HashMap<HashableLocation, Boolean> goldPacmanAte = new HashMap<>();

    /**
     * Orion constructor
     * @param manager    stores locations of all game objects
     */
    public Orion(ObjectManager manager) {
        super(manager, false, directory, numOrionImages);
        assert manager != null;
        setName(ORION_NAME);
        // Just make sure there are actually items
        // for Orion to store
        if (!this.getManager().getItems().isEmpty())
        {
            this.makeGoldMaps();
        }
    }

    /**
     * Moves Orion to its next location, based on walking through every
     * gold location randomly, prioritizing golds Pacman has not eaten
     * Orion has walk cycles; a walk cycle starts when Orion determines the
     * first gold location to walk to, and ends when it arrives at
     * its last unvisited gold location.
     */
    @Override
    public void moveApproach() {
        // If we already are at destination or destination is null,
        // we want to ensure we find a new destination to walk towards
        if (this.currDestination != null && this.currDestination.location().equals(
                this.getLocation())) {
            this.hasDestination = false;
            this.goldVisited.put(this.currDestination, true);
            // After Orion finishes walk cycle, reset its cycle
            // by setting all goldLocations values to false
            if (this.checkIfAllVisited(new ArrayList<>(
                    this.goldVisited.keySet()))) {
                goldVisited.replaceAll((l, v) -> false);
            }
        }

        if (!hasDestination) this.findNewGold();

        // Now we go towards the direction of this new location
        Location orionLocation = this.getLocation();
        Location.CompassDirection compassDir = this.getLocation().get4CompassDirectionTo(
                this.currDestination.location());
        this.setDirection(compassDir);

        // Orion monster can only go vertically and horizontally (it doesn't fly)
        // Want to go towards direction where distance to gold is minimized
        int minDistance = Integer.MAX_VALUE;
        ArrayList<Location> possibleLocations = new ArrayList<>();
        Location toMove;
        for (Location.CompassDirection dir : Location.CompassDirection.values()) {
            if (dir.getDirection()%10 == 0) {
                Location currLocation = orionLocation.getNeighbourLocation(dir);
                int distanceToGold = currLocation.getDistanceTo(this.currDestination.location());
                // To prevent Orion from just going to the same 2 places constantly,
                // need to track visited locations with visited list
                if (this.canMove(currLocation) && !this.isVisited(currLocation) &&
                        distanceToGold <= minDistance) {
                    // Keep track of all possible tying directions
                    if (distanceToGold < minDistance) {
                        minDistance = distanceToGold;
                        possibleLocations = new ArrayList<>();
                    }
                    possibleLocations.add(currLocation);
                }
            }
        }

        // In case every move has been visited already,
        // just find the immediate place you can move to
        if (possibleLocations.isEmpty()) {
            Location.CompassDirection[] directions = Location.CompassDirection.values();
            while(true) {
                int currIndex = this.RANDOMIZER.nextInt(0, directions.length);
                Location.CompassDirection dir = directions[currIndex];
                Location newLocation = this.getLocation().getNeighbourLocation(dir);
                if (this.canMove(newLocation) && dir.getDirection()%10 == 0) {
                    toMove = newLocation;
                    break;
                }
            }
        }

        // There may be more than one unvisited location that minimizes distance
        // to a gold, randomly select from these options
        else {
            int randomIndex = this.RANDOMIZER.nextInt(0, possibleLocations.size());
            toMove = possibleLocations.get(randomIndex);
        }

        // Now when the move has been decided, can move Orion to the desired piece
        this.addVisitedList(toMove);
        this.setLocation(toMove);
    }

    /**
     * Helper function for moveApproach that decides the next
     * gold piece location Orion moves towards
     */
    private void findNewGold() {
        // Idea is that we need to somehow keep track of the gold coins that have and have not been visited
        HashMap<HashableLocation, Boolean> notTaken = new HashMap<>();
        int numGolds = 0;

        // Loop through all the possible gold coins
        // in the game
        for (HashableLocation loc : this.goldPacmanAte.keySet()) {
            // Want to prioritize any gold coin that
            // pacman hasn't eaten yet
            if (!this.goldVisited.get(loc)) {
                // Now map each gold location onto a hashmap depending
                // on whether that gold piece was taken already
                HashableLocation.putLocationHash(notTaken, loc.location(), true);
                numGolds++;
            }
        }

        ArrayList<HashableLocation> goldsToIterate;

        // Need to first decide which golds we can iterate through:
        // If there are still golds pacman hasn't eaten yet and Orion hasn't visited
        if (!this.checkIfAllVisited(new ArrayList<>(notTaken.keySet())) && numGolds > 0)
        {
            goldsToIterate = new ArrayList<>(notTaken.keySet());
        }

        // Otherwise randomly check from all possible gold locations
        else
        {
            goldsToIterate = new ArrayList<>(this.goldVisited.keySet());
        }


        // Now randomly pick which gold you want to go to
        HashableLocation newLocation = this.getRandomLocation(goldsToIterate);

        // Set this new location for Orion and set hasDestination to true
        this.hasDestination = true;
        this.currDestination = newLocation;
    }

    /**
     * This function initializes 2 key maps needed for Orion:
     * - goldLocations: gold piece locations visited for each walking cycle
     * - goldPacmanAte: gold pieces Pacman ate already
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
     * Check if a given list of gold piece locations have been
     * visited by Orion already for a given walk cycle
     * @param golds: List of a number (not necessarily all) gold
     *               piece locations
     * @return boolean indicating if all golds in list were visited
     *         already
     */
    private boolean checkIfAllVisited(ArrayList<HashableLocation> golds)
    {
        for (HashableLocation loc : golds) {
            if (!this.goldVisited.get(loc)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Randomly pick a gold location from a given list
     * of gold locations that IS NOT YET VISITED in Orion's
     * walk cycle
     * @param golds: List of a number (not necessarily all) gold
     *               piece locations
     * @return Random location from list of gold locations
     */
    private HashableLocation getRandomLocation(ArrayList<HashableLocation> golds)
    {
        while (true)
        {
            int randomIndex = this.RANDOMIZER.nextInt(0, golds.size());

            HashableLocation currentLocation = golds.get(randomIndex);

            if (this.currDestination != null && this.currDestination.getX() == 4 && this.currDestination.getY() == 9
                && this.getLocation().getY() == 7 && this.getLocation().getX() == 6) {
                System.out.printf("Chosen Coordinate : %d %d\n", currentLocation.getX(), currentLocation.getY());
            }

            if (!this.goldVisited.get(currentLocation) &&
                  // Need to check if the gold is NOT the one
                  // Orion is already in
                  currentLocation.location().getX() != this.getLocation().getX() &&
                  currentLocation.location().getY() != this.getLocation().getY()
            ) {
                return currentLocation;
            }
        }
    }
}
