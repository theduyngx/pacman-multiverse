package src;

import ch.aplu.jgamegrid.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Orion extends Monster {
    private static final String ORION_NAME = "Orion";
    public static final int numOrionImages = 1;
    public static final String directory = "sprites/m_orion.gif";

    private Location currDestination = null;
    private boolean hasDestination = false;
    private final boolean allEaten = false;

    // Dictionary to store all gold locations on the map
    private final HashMap<HashableLocation, Boolean> goldLocations = new HashMap<>();


    public Orion(ObjectManager manager) {
        super(manager, false, directory, numOrionImages);
        assert manager != null;
        setName(ORION_NAME);
        // Just make sure there are actually items to read through
        if (!this.getManager().getItems().isEmpty())
            this.makeGoldLocations();
    }

    @Override
    public void moveApproach() {
        // Always check first if we are already at the current destination
        // If we already are, or a destination has not been defined, we
        // want to ensure we find a new destination to walk towards
        // NOTE: This assumes that the .equals() function of Location class is correct
        if (this.currDestination != null && (this.getLocation().equals(this.currDestination))) {
            this.hasDestination = false;
            HashableLocation.putLocationHash(goldLocations, currDestination, true);
            // Need to also check if this location was the last gold
            // location in the cycle; if so we need to reset the goldLocations dictionary
            if (this.checkIfAllVisited(new ArrayList<>(this.goldLocations.keySet())))
                goldLocations.replaceAll((l, v) -> false);
        }

        // Here we just keep walking towards the current destination we have, find one if there is none
        if (!hasDestination) this.findNewGold();

        // Now we go towards the direction of this new location
        Location orionLocation = this.getLocation();
        Location.CompassDirection compassDir = this.getLocation().get4CompassDirectionTo(this.currDestination);
        this.setDirection(compassDir);

        // Orion monster can only go vertically and horizontally (it doesn't fly)
        // Want to go towards direction where distance to gold is minimized

        // NOTE: Unlike TX5, Orion does not automatically resort to randomly
        // picking a direction to walk in if the closest spot to a gold is a wall.
        // Once Orion picks a gold to walk towards, there is no randomness in
        // the direction, it only needs to know if it's the spot closest to a gold
        // that is not blocked off by a wall
        int minDistance = Integer.MAX_VALUE;
        Location toMove = null;
        for (Location.CompassDirection dir : Location.CompassDirection.values()) {
            if (dir.getDirection()%10 == 0) {
                Location currLocation = orionLocation.getNeighbourLocation(dir);
                int distanceToGold = currLocation.getDistanceTo(this.currDestination);
                if (this.canMove(currLocation) && distanceToGold < minDistance) {
                    minDistance = distanceToGold;
                    toMove = currLocation;
                }
            }
        }

        // Now when the move has been decided, can move Orion to the desired piece
        this.setLocation(toMove);
    }

    public void findNewGold() {
        // Idea is that we need to somehow keep track of the gold coins that have and have not been visited
        HashMap<HashableLocation, Integer> notTaken = new HashMap<>();
        int minDistance = Integer.MAX_VALUE;
        int numGolds = 0;

        // Loop through all the possible gold coins
        // if there are some that aren't visited yet
        for (Item item : getManager().getItems().values()) {
            // Any item that is still in the items hashmap
            // is considered not visited yet
            if (item instanceof Gold) {
                // Check the distance of each coin
                int currDistanceToOrion = item.getLocation().getDistanceTo(this.getLocation());

                // Now map each gold location onto a hashmap depending
                // on whether that gold piece was taken already
                HashableLocation.putLocationHash(notTaken, item.getLocation(), currDistanceToOrion);
            }
        }

        // Need to first decide which golds we can iterate through:
        // either the golds pacman hasn't eaten yet, or just
        // to consider every location
        this.checkIfAllVisited(new ArrayList<>(notTaken.keySet()));
        ArrayList<HashableLocation> goldsToIterate = new ArrayList<>(this.goldLocations.keySet());


        // Now randomly pick which gold you want to go to
        Location newLocation = this.getRandomLocation(goldsToIterate);

        // Set this new location for Orion and set hasDestination to true
        this.hasDestination = true;
        this.currDestination = newLocation;
    }


    // This function initializes all the possible gold locations,
    // is a hashmap that tracks which gold pieces in Orion's walk cycle have not been visited yet
    private void makeGoldLocations() {
        for (Map.Entry<HashableLocation, Item> entry : this.getManager().getItems().entrySet()) {
            Item item = entry.getValue();
            if (item instanceof Gold) {
                Location location = entry.getKey().location();
                HashableLocation.putLocationHash(goldLocations, location, true);
            }
        }
    }

    // Helper function that checks if all the golds found from the current items hashmap are visited
    private boolean checkIfAllVisited(ArrayList<HashableLocation> golds) {
        for (HashableLocation hashed : golds)
            if (! goldLocations.get(hashed))
                return false;
        return true;
    }

    // Helper function to generate a random location from a given list
    private Location getRandomLocation(ArrayList<HashableLocation> golds) {
        while (true) {
            int randomIndex = this.randomizer.nextInt(0, golds.size());
            Location currentLocation = golds.get(randomIndex).location();
            if (! HashableLocation.getLocationHash(goldLocations, currentLocation))
                return currentLocation;
        }
    }
}
