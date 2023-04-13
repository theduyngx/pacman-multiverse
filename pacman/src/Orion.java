package src;

import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.HashMap;

public class Orion extends Monster
{
    public static final int numOrionImages = 1;
    public static final String directory = "sprites/m_orion.gif";

    private Location currDestination = null;
    private boolean hasDestination = false;
    private ArrayList<Location> visited = new ArrayList<>();
    private boolean allEaten = false;


    public Orion(Game game)
    {
        super(game, false, directory, numOrionImages);
    }

    @Override
    public void walkApproach()
    {
        // Always check first if we are already at the current destination
        // If we already are, or a destination has not been defined, we
        // want to ensure we find a new destination to walk towards
        // NOTE: This assumes that the .equals() function of Location class is correct
        if (this.currDestination != null && (this.getLocation().equals(this.currDestination)))
        {
            this.hasDestination = false;
            if (!this.allEaten)
            {
                visited.add(this.currDestination);
            }
        }

        // Here we just keep walking towards the current
        // destination we have, find one if there is none
        if (!hasDestination) { this.findNewGold(); }

        // Now we go towards the direction of this new location
        Location orionLocation = this.getLocation();
        Location.CompassDirection compassDir = this.getLocation().get4CompassDirectionTo(this.currDestination);
        Location next = this.getLocation().getNeighbourLocation(compassDir);
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
        for (Location.CompassDirection dir : Location.CompassDirection.values())
        {
            if (dir.getDirection()%10 == 0)
            {
                Location currLocation = this.getLocation().getNeighbourLocation(dir);
                int distanceToGold = currLocation.getDistanceTo(this.currDestination);
                if (this.canMove(currLocation) && distanceToGold < minDistance)
                {
                    minDistance = distanceToGold;
                    toMove = currLocation;
                }
            }
        }

        // Now when the move has been decided, can move Orion to the desired
        // piece
        this.setLocation(toMove);
    }

    public void findNewGold()
    {
        // Idea is that we need to somehow keep track of the
        // gold coins that have and have not been visited
        HashMap<Item, Integer> notTaken = new HashMap<>();
        int minDistance = Integer.MAX_VALUE;
        int numGolds = 0;

        // Loop through all the possible gold coins
        // if there are some that aren't visited yet
        for (Item item: this.getGame().manager.getItems().values())
        {
            // Any item that is still in the items hashmap
            // is considered not visited yet
            if (item.getClass().equals("class Gold"))
            {
                // Check the distance of each coin
                int currDistanceToOrion = item.getLocation().getDistanceTo(this.getLocation());

                // Now map each gold location onto a hashmap depending
                // on whether that gold piece was taken already
                notTaken.put(item, currDistanceToOrion);
            }
        }

        ArrayList<Location> goldDistances = new ArrayList<>();

        if (numGolds > 0)
        {
            for (Item item: notTaken.keySet())
            {
                if (notTaken.get(item) <= minDistance)
                {
                    if (notTaken.get(item) < minDistance)
                    {
                        minDistance = notTaken.get(item);
                        goldDistances = new ArrayList<>();
                    }
                    goldDistances.add(item.getLocation());
                }
            }
        }

        else
        {
            this.allEaten = true;
            goldDistances = this.visited;
        }

        // Now randomly pick which gold you want to go
        // to
        int listIndex = this.randomizer.nextInt(0, goldDistances.size());
        Location newLocation = goldDistances.get(listIndex);

        // Set this new location for Orion and set hasDestination to true
        this.hasDestination = true;
        this.currDestination = newLocation;
    }
}
