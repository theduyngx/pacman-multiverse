// Monster.java
// Used for PacMan
package src;

import ch.aplu.jgamegrid.*;
import java.awt.Color;
import java.util.*;

public abstract class Monster extends LiveActor {

    private final ArrayList<Location> visitedList = new ArrayList<>();
    private final int listLength = 10;
    private boolean stopMoving = false;
    private int seed = 0;
    protected final Random randomizer = new Random(0);

    public Monster(Game game, boolean isRotatable, String directory, int numSprites) {
        super(game, isRotatable, directory, numSprites);
    }

    public void stopMoving(int seconds) {
        this.stopMoving = true;
        Timer timer = new Timer(); // Instantiate Timer Object
        int SECOND_TO_MILLISECONDS = 1000;
        final Monster monster = this;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                monster.stopMoving = false;
            }
        }, (long) seconds * SECOND_TO_MILLISECONDS);
    }

    public void setSeed(int seed) {
        this.seed = seed;
        randomizer.setSeed(seed);
    }

    public void setStopMoving(boolean stopMoving) {
        this.stopMoving = stopMoving;
    }

    public void act() {
        if (stopMoving) return;
        walkApproach();
        boolean enable = getDirection() > 150 && getDirection() < 210;
        setHorzMirror(!enable);
    }

    // One abstract method that changes depending on the monster
    public abstract void walkApproach();

    protected void addVisitedList(Location location) {
        visitedList.add(location);
        if (visitedList.size() == listLength)
            visitedList.remove(0);
    }

    protected boolean isVisited(Location location) {
        for (Location loc : visitedList)
            if (loc.equals(location))
                return true;
        return false;
    }
}