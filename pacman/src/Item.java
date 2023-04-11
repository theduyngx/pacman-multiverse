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

    public abstract void signalManager(ObjectManager manager);
}
