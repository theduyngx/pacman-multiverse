package src;
import ch.aplu.jgamegrid.*;

public class TX5 extends Monster
{
    public static final int numTX5Images = 1;
    public static final MonsterType type = MonsterType.TX5;
    public static final String directory = "sprites/m_tx5.gif";

    public TX5(Game game)
    {
        super(game, false, directory, numTX5Images);
    }

    @Override
    public void walkApproach()
    {
        // With TX5, need to base direction to move on the relative position
        // of pacman
        Location pacLocation = this.getGame().manager.getPacActor().getLocation(); // Possibly make game attributes private
        double oldDirection = this.getDirection();
        Location.CompassDirection compassDir = getLocation().get4CompassDirectionTo(pacLocation);
        Location next = getLocation().getNeighbourLocation(compassDir);
        this.setDirection(compassDir);

        // This marks the direction nearest to pacman
        next = this.getLocation().getNeighbourLocation(compassDir);
        // Only go to this direction if you can move here
        // and it was not visited yet
        if (this.canMove(next) && !this.isVisited(next))
        {
            this.setLocation(next);
        }

        // If can't move here, have to move to a random spot
        else
        {
            double sign = this.randomizer.nextDouble();
            this.setDirection(oldDirection);
            this.turn(sign*90);
            next = this.getNextMoveLocation();

            // Check if we can turn this direction
            if (this.canMove(next))
            {
                this.setLocation(next);
            }

            // Otherwise just turn backwards
            else
            {
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
