package src;

import ch.aplu.jgamegrid.*;
import java.awt.*;
import java.util.Random;

public abstract class LiveActor extends Actor {
    private final Game game;
    private ObjectManager manager;
    private final Random randomizer = new Random();

    public LiveActor(Game game, boolean isRotatable, String directory, int numSprites) {
        super(isRotatable, directory, numSprites);
        this.game = game;
    }

    public Game getGame() {
        return game;
    }

    public ObjectManager getManager() {
        return manager;
    }

    protected abstract void setSeed(int seed);

    protected void setManager(ObjectManager manager) {
        this.manager = manager;
    }

    protected void moveApproach(double oldDirection) {
        int sign = randomizer.nextDouble() < 0.5 ? 1 : -1;
        setDirection(oldDirection);
        turn(sign * 90);                // Try to turn left/right
        Location next = getNextMoveLocation();
        if (! canMove(next)) {
            setDirection(oldDirection);
            next = getNextMoveLocation();     // Try to move forward
            if (! canMove(next)) {
                setDirection(oldDirection);
                turn(-sign * 90);       // Try to turn right/left
                next = getNextMoveLocation();
                if (! canMove(next)) {
                    setDirection(oldDirection);
                    turn(180);          // Turn backward
                    next = getNextMoveLocation();
                }
            }
        }
        setLocation(next);
    }

    protected boolean canMove(Location location) {
        Color c = getBackground().getColor(location);
        return !c.equals(ObjectManager.COLOR_WALL) && location.getX() < game.getNumHorizontalCells()
                && location.getX() >= 0 && location.getY() < game.getNumVerticalCells() && location.getY() >= 0;
    }
}
