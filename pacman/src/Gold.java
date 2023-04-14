package src;
import ch.aplu.jgamegrid.*;
import java.awt.*;

/**
 * Gold class extended from abstract Item class.
 */
public class Gold extends Item {
    // properties
    private static final int AGGRAVATE_TIME = 3;
    private static final String directory = "sprites/gold.png";
    private static final int GOLD_SCORE = 5;

    /**
     * Constructor for Gold. It will set its own score, and call Item's constructor with its own
     * sprite image directory.
     */
    public Gold() {
        super(directory);
        setScore(GOLD_SCORE);
    }

    /**
     * Overridden putItem method, where gold puts itself to the game.
     * @param bg        background of game grid
     * @param game      the game
     * @param location  the current gold item's location
     */
    @Override
    public void putItem(GGBackground bg, Game game, Location location) {
        bg.setPaintColor(Color.yellow);
        bg.fillCircle(game.toPoint(location), radius);
        game.addActor(this, location);
    }

    /**
     * (WIP) Overridden method signalling object manager to aggravate monsters.
     * @param manager object manager
     */
    @Override
    public void signalManager(ObjectManager manager) {
        // assert that player is in fact at the location of item
        if (! matchPacmanLocation(manager))
            // trigger signal
            for (Monster monster : manager.getMonsters().values())
                // NOTE: gold is supposed to aggravate
                monster.stopMoving(AGGRAVATE_TIME);
    }
}