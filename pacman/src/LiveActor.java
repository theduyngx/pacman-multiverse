package src;
import ch.aplu.jgamegrid.*;
import src.utility.GameCallback;

import java.util.ArrayList;
import java.util.Random;


/**
 * Abstract LiveActor class for any actors in the game that are animate objects. Like Item class, it will
 * frequently interact with the object manager to get the state of the game, which includes where every
 * other actor is located at, and whether there is a location collision or not.
 * @see Actor
 * @see ObjectManager
 */
public abstract class LiveActor extends Actor {
    // manager and randomizer
    private final ObjectManager manager;
    private final Random randomizer = new Random(0);

    // initial location for actor instantiation
    private Location initLocation;

    // visited locations
    private final ArrayList<Location> visitedList = new ArrayList<>();

    // direction-related - representing which angle to turn to for a move
    public static final int RIGHT_TURN_ANGLE = 90;
    public static final int LEFT_TURN_ANGLE = -RIGHT_TURN_ANGLE;
    public static final int BACK_TURN_ANGLE = 2 * RIGHT_TURN_ANGLE;

    // other properties
    private String name;
    public static final int SLOW_DOWN = 3;

    /**
     * Constructor for LiveActor.
     * @param manager       the object manager
     * @param isRotatable   whether the actor is rotatable or not - important if actor's direction changes
     * @param directory     directory that contains sprite image for the actor
     * @param numSprites    number of sprites the actor has
     */
    public LiveActor(ObjectManager manager, boolean isRotatable, String directory, int numSprites) {
        super(isRotatable, directory, numSprites);
        assert manager != null;
        this.manager = manager;
    }

    /**
     * Get the game grid.
     * @return the game grid
     * @see    GameGrid
     */
    public GameGrid getGameGrid() {
        assert this.gameGrid != null;
        return gameGrid;
    }

    /**
     * Get the object manager object; used frequently since the object manager is responsible for
     * updating all objects' locations.
     * @return the object manager
     */
    public ObjectManager getManager() {
        assert this.manager != null;
        return manager;
    }

    /**
     * Get the GameCallBack to update log from object manager.
     * @return the game callback object
     * @see    GameCallback
     */
    public GameCallback getGameCallback() {
        GameCallback gameCallback = getManager().getGameCallback();
        assert gameCallback != null;
        return gameCallback;
    }

    /**
     * Get the randomizer which, depending on the context and type of actor, will dictate the next move that
     * the actor will make.
     * @return the randomizer
     */
    public Random getRandomizer() {
        return randomizer;
    }

    /**
     * Get PacMan's initial location to add to game.
     * @return the initial location
     * @see    Location
     */
    public Location getInitLocation() {
        return initLocation;
    }

    /**
     * Get actor's name; used for GameCallBack to write actor's names to log.
     * @return monster's name
     */
    public String getName() {
        return name;
    }

    /**
     * Set live actor's name.
     * @param name live actor's name
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Set initial location for PacMan.
     * @param initLocation PacMan's initial location
     * @see   Location
     */
    protected void setInitLocation(Location initLocation) {
        this.initLocation = initLocation;
    }

    /**
     * Abstract method setting seed for the live actor.
     * @param seed specified seed
     */
    protected abstract void setSeed(int seed);

    /**
     * Abstract move approach that changes depending on the live actor: PacActor in auto mode, as well as
     * different enemies may have each distinctive move approach.
     */
    protected abstract void moveApproach();


    /**
     * Check whether a live actor can move to a specified location.
     * @param location specified location
     * @return         boolean indicating whether actor can move there.
     * @see            Location
     */
    protected boolean canMove(Location location) {
        int x = location.getX(), y = location.getY();
        PacManGameGrid grid = getManager().getGame().getGrid();
        assert grid != null;
        return (! HashableLocation.containLocationHash(getManager().getWalls(), location)) &&
                x < grid.getXRight() && x >= grid.getXLeft() && y < grid.getYBottom() && y >= grid.getYTop();
    }

    /**
     * (WIP: should be HashMap<HashableLocation, Monster>) Add location to visited list.
     * @param location current location of monster
     */
    protected void addVisitedList(Location location) {
        visitedList.add(location);
        int LIST_LENGTH = 10;
        if (visitedList.size() == LIST_LENGTH)
            visitedList.remove(0);
    }

    /**
     * Check if monster has not visited a specific location.
     * @param location specified location to check if monster has visited
     * @return         true if monster has yet, false if otherwise
     */
    protected boolean notVisited(Location location) {
        for (Location loc : visitedList)
            if (loc.equals(location))
                return false;
        return true;
    }

    /**
     * Check if 'this' live actor collides with a specified other or not.
     * @param other specified other live actor
     * @return      if live actor collides with the other or not
     */
    public boolean checkCollision(LiveActor other) {
        return (this.getLocation().equals(other.getLocation()));
    }
}
