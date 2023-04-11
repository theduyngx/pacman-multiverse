package src;

import ch.aplu.jgamegrid.Location;

public class Gold extends Item {
    private static final int AGGRAVATE_TIME = 3;

    public Gold(Location location) {
        super(location);
    }

    @Override
    public void signalManager(ObjectManager manager) {
        // assert that player is in fact at the location of item
        if (! matchPacmanLocation(manager)) {

            // trigger signal
            for (Monster monster : manager.getMonsters()) {
                // NOTE: gold is supposed to aggravate
                monster.stopMoving(AGGRAVATE_TIME);
            }
        }
    }
}
