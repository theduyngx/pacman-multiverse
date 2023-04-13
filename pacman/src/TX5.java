package src;
import ch.aplu.jgamegrid.*;

public class TX5 extends Monster {
    private static final String TX5_NAME = "TX5";
    public static final int numTX5Images = 1;
    public static final String directory = "sprites/m_tx5.gif";

    public TX5(Game game) {
        super(game, false, directory, numTX5Images);
        setName(TX5_NAME);
    }

    @Override
    public void moveApproach() {
        // With TX5, need to base direction to move on the relative position of pacman
        Location pacLocation = getManager().getPacActor().getLocation();
        double oldDirection = this.getDirection();
        Location.CompassDirection compassDir = getLocation().get4CompassDirectionTo(pacLocation);
        this.setDirection(compassDir);

        // This marks the direction nearest to pacman
        Location next = this.getLocation().getNeighbourLocation(compassDir);
        // Only go to this direction if you can move here, and it was not visited yet
        if (this.canMove(next) && !this.isVisited(next))
            this.setLocation(next);

        // If it can't move here, has to move to a random spot
        else {
            double sign = this.randomizer.nextDouble();
            this.setDirection(oldDirection);
            this.turn(sign*90);
            next = this.getNextMoveLocation();

            // Check if we can turn this direction
            if (this.canMove(next))
                this.setLocation(next);

            // Otherwise just turn backwards
            else {
                this.setDirection(oldDirection);
                this.turn(180);
                next = this.getNextMoveLocation();
                this.setLocation(next);
            }
        }

        // Record changes in position to game
        this.getGame().getGameCallback().monsterLocationChanged(this);
        this.addVisitedList(next);
    }
}
