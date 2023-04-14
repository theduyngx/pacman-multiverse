package src;
import ch.aplu.jgamegrid.*;
import src.utility.GameCallback;

import java.util.Random;

/**
 * Abstract LiveActor class for any actors in the game that are animate objects.
 */
public abstract class LiveActor extends Actor {
    // properties
    private final ObjectManager manager;
    protected final Random randomizer = new Random(0);

    /**
     * Constructor for LiveActor.
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
     */
    public GameCallback getGameCallback() {
        GameCallback gameCallback = getManager().getGameCallback();
        assert gameCallback != null;
        return gameCallback;
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
     */
    protected boolean canMove(Location location) {
        int x = location.getX(), y = location.getY();
        Game game = getManager().getGame();
        assert game != null;
        return (! HashableLocation.containLocationHash(getManager().getWalls(), location)) &&
                x < game.getXRight() && x >= game.getXLeft() && y < game.getYBottom() && y >= game.getYTop();
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
