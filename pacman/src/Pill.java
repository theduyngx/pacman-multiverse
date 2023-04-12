package src;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;

public class Pill extends Item {
    public Pill(Game game, Location location) {
        super(game, location);
    }

    @Override
    public void putItem(GGBackground bg, Game game) {
        bg.fillCircle(game.toPoint(getLocation()), radius);
    }

    @Override
    public void signalManager(ObjectManager manager) {
        // do nothing
    }
}
