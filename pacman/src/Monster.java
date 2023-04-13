// Monster.java
// Used for PacMan
package src;

import ch.aplu.jgamegrid.*;
import java.util.*;

public class Monster extends LiveActor {
    private final MonsterType type;
    private final ArrayList<Location> visitedList = new ArrayList<>();
    private boolean stopMoving = false;
    private final Random randomizer = new Random(0);

    public Monster(Game game, MonsterType type) {
        super(game, false, "sprites/" + type.getImageName(), 1);
        this.type = type;
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
        moveNext();
        boolean enable = getDirection() > 150 && getDirection() < 210;
        setHorzMirror(!enable);
    }

    private void moveNext() {
        Location pacLocation = getGame().manager.getPacActor().getLocation();
        double oldDirection = getDirection();
        Location.CompassDirection compassDir = getLocation().get4CompassDirectionTo(pacLocation);
        Location next = getLocation().getNeighbourLocation(compassDir);
        setDirection(compassDir);

        if (type == MonsterType.TX5 && !isVisited(next) && canMove(next))
            setLocation(next);
        else
            moveApproach(oldDirection);
        getGame().getGameCallback().monsterLocationChanged(this);
        addVisitedList(next);
    }

    public MonsterType getType() {
        return type;
    }

    private void addVisitedList(Location location) {
        visitedList.add(location);
        int listLength = 10;
        if (visitedList.size() == listLength)
            visitedList.remove(0);
    }

    private boolean isVisited(Location location) {
        for (Location loc : visitedList)
            if (loc.equals(location))
                return true;
        return false;
    }
}
