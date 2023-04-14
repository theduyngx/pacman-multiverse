package src;

import ch.aplu.jgamegrid.*;
import java.util.Random;

public abstract class LiveActor extends Actor {
    private final Game game;
    private ObjectManager manager;
    protected final Random randomizer = new Random(0);

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

    // Abstract move approach that changes depending on the live actor
    protected abstract void moveApproach();

    protected void setManager(ObjectManager manager) {
        this.manager = manager;
    }


    /**
     * Check whether a live actor can move to a specified location.
     * @param location specified location
     * @return         boolean indicating whether actor can move there.
     */
    protected boolean canMove(Location location) {
        int x = location.getX();
        int y = location.getY();
        return (! HashableLocation.containLocationHash(getManager().getWalls(), location)) &&
                x < getGame().getXRight() && x >= getGame().getXLeft() &&
                y < getGame().getYBottom() && y >= getGame().getYTop();
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
