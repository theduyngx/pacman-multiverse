package src;
import ch.aplu.jgamegrid.*;


/**
 * Abstract Item class extended from Actor for any actors in the game that are inanimate objects that
 * have an effect to the game. It will make use of the object manager to handle its locations, which
 * extends to whether or not a live actor has 'collided' with its location or not. In the case of pacman,
 * that should imply the item, if not obstacles, have been acquired.
 * @see Actor
 * @see ObjectManager
 */
public abstract class Item extends Actor {
    // constant radius value when drawn
    public static final int RADIUS = 5 * Game.STRETCH_RATE;
    // the score that would be acquired if eaten by PacMan
    private int score;
    // item's name
    private String name;

    /**
     * Item constructor.
     * @param src the sprite image directory
     */
    public Item(String src) {
        super(src);
    }

    /**
     * Check if an item is at PacMan's position, meaning PacMan has obtained item in question.
     * It should be noted that this method is used purely for assertion before executing the signal
     * to manager method.
     * @param manager object manager
     * @return        whether PacMan has eaten the item
     */
    public boolean matchPacmanLocation(ObjectManager manager) {
        // assert that player is in fact at the location of item
        int xItem = this.getX();
        int yItem = this.getY();
        int xPac  = manager.getPacActor().getX();
        int yPac  = manager.getPacActor().getY();
        return (xItem == xPac && yItem == yPac);
    }

    /**
     * Get the item's name. Used for printing to log in game callback.
     * @return the item's name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the score acquired by eating the item.
     * @return acquired score
     */
    public int getScore() {
        return score;
    }

    /**
     * Set the score that would be acquired if item were eaten.
     * @param score set score
     */
    protected void setScore(int score) {
        this.score = score;
    }

    /**
     * Set the item's name.
     * @param name item's name
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Remove item; used when item is eaten by PacMan.
     * @param manager object manager
     */
    protected void removeItem(ObjectManager manager) {
        HashableLocation hashLocation = new HashableLocation(getLocation());
        manager.getItems().remove(hashLocation);
        removeSelf();
    }

    /**
     * Abstract method to put itself to the game.
     * @param bg        background of game grid
     * @param game      the game
     * @param location  item's location
     * @see             GGBackground
     * @see             Game
     * @see             Location
     */
    protected abstract void putItem(GGBackground bg, Game game, Location location);

    /**
     * Abstract method to signal the object manager for changes that acquiring the item makes.
     * @param manager the object manager
     */
    protected abstract void signalManager(ObjectManager manager);
}