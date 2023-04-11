package src;
import ch.aplu.jgamegrid.*;

public abstract class Item {
    public static final int radius = 5;
    private Actor actor;
    private final Location location;
    public Item(Location location) {
        this.location = location;
    }

    // getters
    public Location getLocation() {
        return location;
    }
    public Actor getActor() {
        return actor;
    }

    // setters
    public void setActor(Actor actor) {
        this.actor = actor;
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

    public abstract void putItem(GGBackground bg, Game game);

    public abstract void signalManager(ObjectManager manager);
}
