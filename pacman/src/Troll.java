package src;

import ch.aplu.jgamegrid.*;

public class Troll extends Monster {
    private static final String TROLL_NAME = "Troll";
    public static final int numTrollImages = 1;
    public static final String directory = "sprites/m_troll.gif";

    public Troll(Game game) {
        super(game, false, directory, numTrollImages);
        setName(TROLL_NAME);
    }

    @Override
    public void moveApproach() {
        double oldDirection = this.getDirection();
        // Should be int but I don't know what happened
        double sign = this.randomizer.nextDouble();
        this.setDirection(oldDirection);
        this.turn(sign*90);

        Location next = getNextMoveLocation();

        // First get a random direction to go to (left or right)
        if (this.canMove(next))
            this.setLocation(next);

        // Collision occurs going first given direction
        else {
            // Check if you can go the opposite turn, either left or right
            this.setDirection(oldDirection);
            this.turn(-sign*90);
            next = this.getNextMoveLocation();

            if (this.canMove(next))
                this.setLocation(next);

            // If nothing really worked, just go backwards
            else {
                this.setDirection(oldDirection);
                this.turn(180);
                next = this.getNextMoveLocation();
                this.setLocation(next);
            }
        }

        // Tell game to change monster's location and store this as visited
        this.getGame().getGameCallback().monsterLocationChanged(this);
        this.addVisitedList(next);
    }
}
