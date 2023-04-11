package src;

import ch.aplu.jgamegrid.Location;

public class Pill extends Item {
    public Pill(Location location) {
        super(location);
    }

    @Override
    public void signalManager(ObjectManager manager) {
        // do nothing
    }
}
