package src;
import ch.aplu.jgamegrid.*;
import java.util.*;

/**
 * Based on skeleton code for SWEN20003 Project, Semester 2, 2022, The University of Melbourne.
 * Monster abstract class extended from abstract LiveActor class.
 */
public abstract class Monster extends LiveActor {
    // time-related constants
    public static final int SECOND_TO_MILLISECONDS = 1000;
    public static final int AGGRAVATE_TIME = 3;
    private static final int AGGRAVATE_SPEED_FACTOR = 2;

    // monster's name
    private String name;
    // visited locations
    private final ArrayList<Location> visitedList = new ArrayList<>();
    // if it has stopped moving or not
    private boolean stopMoving = false;

    /**
     * Monster constructor.
     * @param isRotatable   if monster is rotatable
     * @param directory     sprite image directory
     * @param numSprites    number of sprites
     */
    public Monster(ObjectManager manager, boolean isRotatable, String directory, int numSprites) {
        super(manager, isRotatable, directory, numSprites);
        assert manager != null;
    }

    /**
     * Get the object manager.
     * @return the object manager
     */
    @Override
    public ObjectManager getManager() {
        assert super.getManager() != null;
        return super.getManager();
    }

    /**
     * Get monster's name; used for GameCallBack to write monster's names to log.
     * @return monster's name
     */
    public String getName() {
        return name;
    }

    /**
     * Set monster's name.
     * @param name monster's name
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Stops monster's movement for a specified number of seconds.
     * @param seconds number of seconds monster stops moving
     */
    public void stopMoving(int seconds) {
        setStopMoving(true);
        Timer timer = new Timer(); // Instantiate Timer Object
        final Monster monster = this;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                monster.setStopMoving(false);
            }
        }, (long) seconds * SECOND_TO_MILLISECONDS);
    }

    /**
     * Speed up monster's movement by a constant factor for a specified number of seconds.
     * @param seconds number of seconds monster speeds up
     */
    public void speedUp(int seconds) {
        this.setSlowDown(1/AGGRAVATE_SPEED_FACTOR);
        Timer timer = new Timer();
        final Monster monster = this;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                monster.setSlowDown(AGGRAVATE_SPEED_FACTOR);
            }
        }, (long) seconds * SECOND_TO_MILLISECONDS);
    }

    /**
     * Overridden method for setting monster's seed.
     * @param seed specified seed
     */
    @Override
    public void setSeed(int seed) {
        randomizer.setSeed(seed);
    }

    /**
     * Set monster to either stop or continue/start moving.
     * @param stopMoving boolean indicating if monster stops moving or not
     */
    public void setStopMoving(boolean stopMoving) {
        this.stopMoving = stopMoving;
    }

    /**
     * Overridden act method from Actor class for monster to act within the game.
     */
    @Override
    public void act() {
        if (stopMoving) return;
        moveApproach();
        boolean enable = getDirection() > 150 && getDirection() < 210;
        setHorzMirror(!enable);

        // Record changes in position to game
        getGameCallback().monsterLocationChanged(this);
    }

    /**
     * (WIP: should be HashMap<HashableLocation, Monster>) Add location to visited list.
     * @param location current location of monster
     */
    protected void addVisitedList(Location location) {
        visitedList.add(location);
        int listLength = 10;
        if (visitedList.size() == listLength)
            visitedList.remove(0);
    }

    /**
     * Check if monster has visited a specific location or not.
     * (WIP: should be HashMap<HashableLocation, Monster>)
     * @param location specified location to check if monster has visited
     * @return         true if monster has, false if otherwise
     */
    protected boolean isVisited(Location location) {
        for (Location loc : visitedList)
            if (loc.equals(location))
                return true;
        return false;
    }
}
