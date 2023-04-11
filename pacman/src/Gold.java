package src;

import ch.aplu.jgamegrid.Location;

public class Gold extends Item {
    public Gold(Location location) {
        super(location);
    }

    @Override
    public void signalManager(ObjectManager manager) {
        // assert that player is in fact at the location of item
        int xItem = this.getX();
        int yItem = this.getY();
        int xPac  = manager.getPacActor().getX();
        int yPac  = manager.getPacActor().getY();
        if (xItem != xPac || yItem != yPac) return;

        // trigger signal

    }
}
