package src;

import ch.aplu.jgamegrid.*;
import java.awt.*;

public class Ice extends Item {
    private static final int FREEZE_TIME = 3;
    private static final String directory = "sprites/ice.png";

    public Ice() {
        super(directory);
    }

    @Override
    public void putItem(GGBackground bg, Game game, Location location) {
        bg.setPaintColor(Color.blue);
        bg.fillCircle(game.toPoint(location), radius);
        game.addActor(this, location);
    }

    @Override
    public void signalManager(ObjectManager manager) {
        // assert that player is in fact at the location of item
        if (matchPacmanLocation(manager))
            // trigger signal
            for (Monster monster : manager.getMonsters().values())
                monster.stopMoving(FREEZE_TIME);
    }
}
