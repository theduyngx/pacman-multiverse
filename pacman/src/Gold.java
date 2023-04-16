package src;
import ch.aplu.jgamegrid.*;
import java.awt.*;

/**
 * Gold class extended from abstract Item class.
 */
public class Gold extends Item {
    // properties
    private static final String DIRECTORY = "sprites/gold.png";
    private static final int GOLD_SCORE = 5;

    /**
     * Constructor for Gold. It will set its own score, and call Item's constructor with its own
     * sprite image directory.
     */
    public Gold() {
        super(DIRECTORY);
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
        game.addActor(this, location);
    }

    /**
     * Overridden method signalling object manager to aggravate monsters.
     * @param manager object manager
     */
    @Override
    public void signalManager(ObjectManager manager) {
        // assert that player is in fact at the location of item
        if (matchPacmanLocation(manager))
            // trigger signal
            for (Monster monster : manager.getMonsters()) {
                if (manager.isMultiverse()) {
                monster.speedUp(Monster.AGGRAVATE_TIME);
                // When the monster is Orion, we want Orion to
                // know that this gold piece is already eaten
                if (monster instanceof Orion) {
                    Orion orion = (Orion) monster;
                    HashableLocation.putLocationHash(
                            orion.goldPacmanAte,
                            this.getLocation(),
                            true
                    );
                }
            }
        }
    }
}