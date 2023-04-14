// PacActor.java
// Used for PacMan
package src;

import ch.aplu.jgamegrid.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PacActor extends LiveActor implements GGKeyRepeatListener {
    private static final int INF = 1000;
    private static final int nbSprites = 4;
    private static final String directory = "sprites/pacpix.gif";
    private int idSprite = 0;
    private int nbPills = 0;
    private int score = 0;
    private Location initLocation;
    private List<String> propertyMoves = new ArrayList<>();
    private int propertyMoveIndex = 0;
    private final Random randomizer = new Random();
    private boolean isAuto = false;


    public PacActor(Game game) {
        super(game, true, directory, nbSprites);
    }

    public Location getInitLocation() {
        return initLocation;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    public void setInitLocation(Location initLocation) {
        this.initLocation = initLocation;
    }

    @Override
    protected void setSeed(int seed) {
        randomizer.setSeed(seed);
    }

    public void setPropertyMoves(String propertyMoveString) {
        if (propertyMoveString != null) {
            this.propertyMoves = Arrays.asList(propertyMoveString.split(","));
        }
    }

    public void keyRepeated(int keyCode) {
        if (isAuto) return;
        if (isRemoved())  // Already removed
            return;
        Location next = null;
        switch (keyCode) {
            case KeyEvent.VK_LEFT -> {
                next = getLocation().getNeighbourLocation(Location.WEST);
                setDirection(Location.WEST);
            }
            case KeyEvent.VK_UP -> {
                next = getLocation().getNeighbourLocation(Location.NORTH);
                setDirection(Location.NORTH);
            }
            case KeyEvent.VK_RIGHT -> {
                next = getLocation().getNeighbourLocation(Location.EAST);
                setDirection(Location.EAST);
            }
            case KeyEvent.VK_DOWN -> {
                next = getLocation().getNeighbourLocation(Location.SOUTH);
                setDirection(Location.SOUTH);
            }
        }
        if (next != null && canMove(next)) {
            setLocation(next);
            eatPill(getManager(), next);
        }
    }

    @Override
    public void act() {
        show(idSprite);
        idSprite++;
        if (idSprite == nbSprites)
            idSprite = 0;
        if (isAuto)
            moveApproach();
        getGame().getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
    }

    private Location closestPillLocation() {
        int currentDistance = INF;
        Location currentLocation = null;
        List<Location> pillAndItemLocations = getManager().getItemLocations();
        for (Location location: pillAndItemLocations) {
            int distanceToPill = location.getDistanceTo(getLocation());
            if (distanceToPill < currentDistance) {
                currentLocation = location;
                currentDistance = distanceToPill;
            }
        }
        return currentLocation;
    }

    private void followPropertyMoves() {
        String currentMove = propertyMoves.get(propertyMoveIndex);
        switch (currentMove) {
            case "R" -> turn(90);
            case "L" -> turn(-90);
            case "M" -> {
                Location next = getNextMoveLocation();
                if (canMove(next)) {
                    setLocation(next);
                    eatPill(getManager(), next);
                }
            }
        }
        propertyMoveIndex++;
    }

    protected void moveApproach() {
        if (propertyMoves.size() > propertyMoveIndex) {
            followPropertyMoves();
            return;
        }
        Location closestPill = closestPillLocation();
        double oldDirection = getDirection();
        Location.CompassDirection compassDir = getLocation().get4CompassDirectionTo(closestPill);
        Location next = getLocation().getNeighbourLocation(compassDir);
        setDirection(compassDir);
        if (canMove(next))
            setLocation(next);
        else {
            int sign = randomizer.nextDouble() < 0.5 ? 1 : -1;
            setDirection(oldDirection);
            turn(sign * 90);                // Try to turn left/right
            next = getNextMoveLocation();
            if (! canMove(next)) {
                setDirection(oldDirection);
                next = getNextMoveLocation();     // Try to move forward
                if (! canMove(next)) {
                    setDirection(oldDirection);
                    turn(-sign * 90);       // Try to turn right/left
                    next = getNextMoveLocation();
                    if (! canMove(next)) {
                        setDirection(oldDirection);
                        turn(180);          // Turn backward
                        next = getNextMoveLocation();
                    }
                }
            }
            setLocation(next);
        }
        eatPill(getManager(), getLocation());
    }

    private void eatPill(ObjectManager manager, Location location) {
        HashableLocation hashLocation = new HashableLocation(location);

        // item exists
        if (manager.getItems().containsKey(hashLocation)) {
            Item item = manager.getItems().get(hashLocation);

            // check of which type
            String itemType = (item instanceof Pill) ? "pills" :
                              (item instanceof Gold) ? "gold"  :
                              "ice";
            if (! (item instanceof Ice)) nbPills++;
            score += item.getScore();

            // signals the manager and removes itself
            item.signalManager(manager);
            getBackground().fillCell(location, Game.COLOR_SPACE);
            getGame().getGameCallback().pacManEatPillsAndItems(location, itemType);
            item.removeItem(manager);
        }
        String title = "[PacMan in the Multiverse] Current score: " + score;
        gameGrid.setTitle(title);
    }

    /**
     * Game over checking - whether PacMan has collided with a monster or not.
     * @return true if collided, false if otherwise.
     */
    public boolean collideMonster() {
        for (Monster monster : getManager().getMonsters().values())
            if (checkCollision(monster))
                return true;
        return false;
    }
}