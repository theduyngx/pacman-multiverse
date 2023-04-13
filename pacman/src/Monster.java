// Monster.java
// Used for PacMan
package src;

import ch.aplu.jgamegrid.*;
import java.util.*;


public abstract class Monster extends LiveActor {
    private String name;
    private final ArrayList<Location> visitedList = new ArrayList<>();
    private boolean stopMoving = false;
    protected final Random randomizer = new Random(0);

    public Monster(Game game, boolean isRotatable, String directory, int numSprites) {
        super(game, isRotatable, directory, numSprites);
    }

    public String getName() {
        return name;
    }

    protected void setName(String name) {
        this.name = name;
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

    @Override
    public void setSeed(int seed) {
        randomizer.setSeed(seed);
    }

    public void setStopMoving(boolean stopMoving) {
        this.stopMoving = stopMoving;
    }

    @Override
    public void act() {
        if (stopMoving) return;
        walkApproach();
        boolean enable = getDirection() > 150 && getDirection() < 210;
        setHorzMirror(!enable);
    }

    // Abstract move approach that changes depending on the monster
    public abstract void walkApproach();

    protected void addVisitedList(Location location) {
        visitedList.add(location);
        int listLength = 10;
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
