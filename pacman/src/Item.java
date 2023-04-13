package src;
import ch.aplu.jgamegrid.*;

public abstract class Item extends Actor {
    private int score;
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
        return (xItem == xPac && yItem == yPac);
    }

    // get and set score
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }

    // remove item
    public void removeItem(ObjectManager manager) {
        HashableLocation hashLocation = new HashableLocation(getLocation());
        manager.getItems().remove(hashLocation);
        removeSelf();
    }

    public abstract void putItem(GGBackground bg, Game game, Location location);

    public abstract void signalManager(ObjectManager manager);
}
