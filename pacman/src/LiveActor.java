package src;
import src.utility.GameCallback;

import ch.aplu.jgamegrid.*;
import java.util.LinkedList;
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

    // Visited locations list - after a cycle, the earliest location will be removed from
    // visited list. We use LinkedList instead of ArrayList for fast removal and adding.
    private final LinkedList<Location> visitedList = new LinkedList<>();

    // direction-related - representing which angle to turn to for a move
    public static final int RIGHT_TURN_ANGLE = 90;
    public static final int LEFT_TURN_ANGLE = -RIGHT_TURN_ANGLE;
    public static final int BACK_TURN_ANGLE = 2 * RIGHT_TURN_ANGLE;
    public static final int SLOW_DOWN = 3;

    // step sizes
    public static final int NORMAL_STEP_SIZE = 1;

    // other properties
    private String name;
    private int stepSize;

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
     * Get the step size of the live actor. The step size refers to the number of cells per move of
     * actor in question.
     * @return the step size
     */
    public int getStepSize() {
        return stepSize;
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
     * Get Live Actor's initial location to add to game. As such, the sole purpose of this method is to
     * add the actor to the grid.
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
     * Set the step size for the live actor.
     * @param stepSize the step size
     */
    public void setStepSize(int stepSize) {
        this.stepSize = stepSize;
    }

    /**
     * Set live actor's name.
     * @param name live actor's name
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Set initial location for live actor. This method goes hand-in-hand with
     * <code> Location getInitLocation() </code>, as in its sole purpose is only to add the actor
     * to the game and its grid. For this reason, it should be expected to only be used once for
     * each actor.
     * @param initLocation PacMan's initial location
     * @see   Location
     */
    protected void setInitLocation(Location initLocation) {
        this.initLocation = initLocation;
    }

    /**
     * Abstract method setting seed for the live actor. Seed is used for determining the randomizer
     * which dictates movements of actors.
     * @param seed specified seed
     */
    protected abstract void setSeed(int seed);

    /**
     * Abstract move approach that changes depending on the live actor: PacActor in auto mode, as well as
     * different enemies may have each distinctive move approach.
     */
    protected abstract void moveApproach();


    /**
     * Check whether a live actor can move to a specified location. This is to make sure that the location that
     * the live actor queries should be a space with no obstacles (like wall blocks), or that it is not out of
     * bound from the game grid.
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
     * Add location to the hashmap of visited locations.
     * <ul>
     *     <li>For monsters, this is used for those that need to remember its visited location within a
     *         specific cycle so that it avoids returning to a location that it's already visited before.
     *     <li>For PacMan, this is used specifically for auto movement mode. This is for the same reason
     *         as monsters with behaviors specified above.
     * </ul>
     * @param location current location of monster
     * @see   Orion
     * @see   TX5
     * @see   Troll
     * @see   PacActor
     */
    protected void addVisitedList(Location location) {
        int CYCLE_LENGTH = 10;
        visitedList.add(location);
        if (visitedList.size() == CYCLE_LENGTH)
            visitedList.removeFirst();
    }

    /**
     * Check if monster has not visited a specific location. This method is used alongside with
     * <code> void addVisitedList(Location location) </code> method, meaning it is only used
     * when the behavior of the live actor calls for a use of the visited locations list.
     * @param  location specified location to check if monster has visited
     * @return          true if monster has yet, false if otherwise
     * @see    Orion
     * @see    TX5
     * @see    Troll
     * @see    PacActor
     */
    protected boolean notVisited(Location location) {
        for (Location loc : visitedList)
            if (loc.equals(location)) return false;
        return true;
    }

    /**
     * Check if 'this' live actor collides with a specified other or not. This is used to specifically
     * determine whether PacMan has collided with a monster. Meaning its main purpose is to check for
     * Game Over condition.
     * @param other specified other live actor
     * @return      if live actor collides with the other or not
     * @see         PacActor
     */
    public boolean checkCollision(LiveActor other) {
        return (this.getLocation().equals(other.getLocation()));
    }
}
