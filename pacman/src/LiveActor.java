package src;
import ch.aplu.jgamegrid.*;
import java.util.Random;

/**
 * Abstract LiveActor class for any actors in the game that are animate objects.
 */
public abstract class LiveActor extends Actor {
    // properties
    private final Game game;
    private ObjectManager manager;
    protected final Random randomizer = new Random(0);

    /**
     * Constructor for LiveActor.
     * @param game          the game object
     * @param isRotatable   whether the actor is rotatable or not - important if actor's direction changes
     * @param directory     directory that contains sprite image for the actor
     * @param numSprites    number of sprites the actor has
     */
    public LiveActor(Game game, boolean isRotatable, String directory, int numSprites) {
        super(isRotatable, directory, numSprites);
        this.game = game;
    }

    /**
     * Get the game object; used when actor performs an action that requires the game to signal its
     * GameCallBack.
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * Get the object manager object; used frequently since the object manager is responsible for
     * updating all objects' locations.
     * @return the object manager
     */
    public ObjectManager getManager() {
        return manager;
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
     * Set the live actor's object manager.
     * @param manager the object manager
     */
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
