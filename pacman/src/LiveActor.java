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

    /// FOR NOW: it still checks based on color, we'd prefer to check based on Location instead
    protected boolean canMove(Location location) {
        HashableLocation hashLocation = new HashableLocation(location);
        int x = location.getX();
        int y = location.getY();
        return (! getManager().getWalls().containsKey(hashLocation)) && x < getGame().getXRight() &&
                x >= getGame().getXLeft() && y < getGame().getYBottom() && y >= getGame().getYTop();
    }
}
