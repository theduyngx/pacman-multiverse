package src;

import ch.aplu.jgamegrid.*;
import java.awt.*;

public abstract class LiveActor extends Actor {
    private final Game game;
    private ObjectManager manager;

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

    protected boolean canMove(Location location) {
        Color c = getBackground().getColor(location);
        return !c.equals(ObjectManager.COLOR_WALL) && location.getX() < game.getNumHorizontalCells()
                && location.getX() >= 0 && location.getY() < game.getNumVerticalCells() && location.getY() >= 0;
    }
}
