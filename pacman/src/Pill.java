package src;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;

public class Pill extends Item {

    @Override
    public void putItem(GGBackground bg, Game game, Location location) {
        bg.fillCircle(game.toPoint(location), radius);
    }

    @Override
    public void signalManager(ObjectManager manager) {
        // do nothing
    }
}
