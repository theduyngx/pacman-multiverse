package src;
import ch.aplu.jgamegrid.*;
import java.awt.*;

/**
 * Ice class extended from abstract Item class.
 * @see Item
 */
public class Ice extends Item {
    // properties
    private static final int FREEZE_TIME = 3;
    private static final String DIRECTORY = "sprites/ice.png";
    private static final int ICE_SCORE = 0;

    /**
     * Constructor for Ice. It will set its own score, and call Item's constructor with its own
     * sprite image directory.
     */
    public Ice() {
        super(DIRECTORY);
        setScore(ICE_SCORE);
    }

    /**
     * Overridden putItem method, where ice puts itself to the game.
     * @param bg        background of game grid
     * @param game      the game
     * @param location  the current gold item's location
     * @see             GGBackground
     * @see             Game
     * @see             Location
     */
    @Override
    public void putItem(GGBackground bg, Game game, Location location) {
        bg.setPaintColor(Color.blue);
        game.addActor(this, location);
    }

    /**
     * Overridden method signalling object manager to freeze monsters.
     * @param manager object manager
     * @see           ObjectManager
     */
    @Override
    public void signalManager(ObjectManager manager) {
        // assert that player is in fact at the location of item
        if (matchPacmanLocation(manager))
            // trigger signal
            if (manager.isMultiverse()) {
                for (Monster monster : manager.getMonsters())
                    monster.stopMoving(FREEZE_TIME);
            }
    }
}