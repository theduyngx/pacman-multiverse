package src;

import ch.aplu.jgamegrid.GGBackground;
import ch.aplu.jgamegrid.Location;

import java.awt.*;

public class Pill extends Item {
    private static final String directory = "sprites/ice.png";
    private static final int PILL_SCORE = 1;
    public Pill() {
        super(directory);
        setScore(PILL_SCORE);
    }

    @Override
    public void putItem(GGBackground bg, Game game, Location location) {
        bg.setPaintColor(Color.white);
        bg.fillCircle(game.toPoint(location), radius);
        game.addActor(this, location);
        hide();
    }

    @Override
    public void signalManager(ObjectManager manager) {
        // do nothing
    }
}
