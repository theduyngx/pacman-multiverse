package src;

public class Troll extends Monster
{
    public Troll(Game game, String spriteName)
    {
        super(game, spriteName);
    }

    @Override
    protected void walkApproach()
    {
        double oldDirection = this.getDirection();
        int sign = this.randomiser.nextDouble();
        this.setDirection(oldDirection);
        this.turn(sign*90);

        next = getNextMoveLocation();

        // First get a random direction to go to (left or right)
        if (this.canMove(next))
        {
            this.setLocation(next);
        }

        // Collision occurs going first given driection
        else
        {
            // Check if you can go the opposite turn,
            // either left or right
            this.setDirection(oldDirection);
            this.turn(-sign*90);
            next = this.getNextMoveLocation();

            if (this.canMove(next))
            {
                this.setLocation(next);
            }

            // If nothing really worked, just go
            // backwards
            else
            {
                this.setDirection(oldDirection);
                this.turn(180);
                next = this.getNextMoveLocation();
                this.setLocation(next);
            }
        }

        // Tell game to change monster's location and store
        // this as visited
        game.getGameCallback().monsterLocationChanged(this);
        this.addVisitedList(next);
    }
}
