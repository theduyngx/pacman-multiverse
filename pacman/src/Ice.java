package src;

import ch.aplu.jgamegrid.Location;

public class Ice extends Item {
    private static final int FREEZE_TIME = 3;

    public Ice(Location location) {
        super(location);
    }

    @Override
    public void signalManager(ObjectManager manager) {
        // assert that player is in fact at the location of item
        if (matchPacmanLocation(manager)) {

            // trigger signal
            for (Monster monster : manager.getMonsters()) {
                monster.stopMoving(FREEZE_TIME);
            }
        }
    }
}
