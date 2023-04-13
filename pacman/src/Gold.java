package src;
import ch.aplu.jgamegrid.*;
import java.awt.*;

public class Gold extends Item {
    private static final int AGGRAVATE_TIME = 3;
    private static final String directory = "sprites/gold.png";
    private static final int GOLD_SCORE = 5;

    public Gold() {
        super(directory);
        setScore(GOLD_SCORE);
    }

    @Override
    public void putItem(GGBackground bg, Game game, Location location) {
        bg.setPaintColor(Color.yellow);
        bg.fillCircle(game.toPoint(location), radius);
        game.addActor(this, location);
    }

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