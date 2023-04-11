package src;
import ch.aplu.jgamegrid.*;
import java.awt.*;

public class Gold extends Item {
    private static final int AGGRAVATE_TIME = 3;
    private static final String directory = "sprites/gold.png";

    public Gold(Location location) {
        super(location);
    }

    @Override
    public void putItem(GGBackground bg, Game game) {
        bg.setPaintColor(Color.yellow);
        bg.fillCircle(game.toPoint(getLocation()), radius);
        setActor(new Actor(directory));
        game.addActor(getActor(), getLocation());
    }

    @Override
    public void signalManager(ObjectManager manager) {
        // assert that player is in fact at the location of item
        if (! matchPacmanLocation(manager)) {

            // trigger signal
            for (Monster monster : manager.getMonsters().values()) {
                // NOTE: gold is supposed to aggravate
                monster.stopMoving(AGGRAVATE_TIME);
            }
        }
    }
}
