package src;
import ch.aplu.jgamegrid.*;

public abstract class Item {
    private final Location location;
    public Item(Location location) {
        this.location = location;
    }

    public int getX() {
        return location.getX();
    }
    public int getY() {
        return location.getY();
    }


    public boolean matchPacmanLocation(ObjectManager manager) {
        // assert that player is in fact at the location of item
        int xItem = this.getX();
        int yItem = this.getY();
        int xPac  = manager.getPacActor().getX();
        int yPac  = manager.getPacActor().getY();
        return (xItem == xPac || yItem == yPac);
    }

    public abstract void signalManager(ObjectManager manager);
}
