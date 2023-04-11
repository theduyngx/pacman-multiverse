package src;
import ch.aplu.jgamegrid.*;

public class TX5 extends Monster
{
    public TX5(Game game, String spriteName)
    {
        super(game, spriteName);
    }

    @Override
    protected void walkApproach()
    {
        // With TX5, need to base direction to move on the relative position
        // of pacman
        Location pacLocation = this.getGame().pacActor.getLocation(); // Possibly make game attributes private
        double oldDirection = this.getDirection();
        Location.CompassDirection compassDir = getLocation().get4CompassDirectionTo(pacLocation);
        Location next = getLocation().getNeighbourLocation(compassDir);
        this.setDirection(compassDir);

        // This marks the direction nearest to pacman
        next = this.getLocation().getNeighbourLocation(compassDir);
        if (this.canMove(next))
        {
            this.setLocation(next);
        }

        // If can't move here, have to move to a random spot
        else
        {
            double sign = this.getRandomiser().nextDouble();
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
