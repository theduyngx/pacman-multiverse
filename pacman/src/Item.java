package src;
import ch.aplu.jgamegrid.*;

public abstract class Item extends Actor {
    public static final int radius = 5;

    public Item(String src) {
        super(src);
    }


    public boolean matchPacmanLocation(ObjectManager manager) {
        // assert that player is in fact at the location of item
        int xItem = this.getX();
        int yItem = this.getY();
        int xPac  = manager.getPacActor().getX();
        int yPac  = manager.getPacActor().getY();
        return (xItem == xPac || yItem == yPac);
    }

    // remove item
    public void removeItem(ObjectManager manager) {
        if (this instanceof Pill)
            manager.getPills().remove(getLocation());
        else if (this instanceof Gold)
            manager.getGolds().remove(getLocation());
        else if (this instanceof Ice)
            manager.getIces().remove(getLocation());
//        this.removeSelf();
    }

    public abstract void putItem(GGBackground bg, Game game, Location location);

    public abstract void signalManager(ObjectManager manager);
}
