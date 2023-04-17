package src;
import ch.aplu.jgamegrid.*;
import java.awt.event.KeyEvent;
import java.util.*;


/**
 * Based on skeleton code for SWEN20003 Project, Semester 2, 2022, The University of Melbourne.
 * PacActor class extended from abstract LiveActor class, implementing a key repeat listener interface.
 * The latter is so that the game responds to player's input.
 * (WIP) - too messy and poorly coded for not. Need some work done.
 * @see LiveActor
 * @see GGKeyRepeatListener
 * @see ObjectManager
 */
public class PacActor extends LiveActor implements GGKeyRepeatListener {
    private static final int INF = 1000;
    private static final int NB_SPRITES = 4;
    private static final String DIRECTORY = "sprites/pacpix.gif";
    private int idSprite = 0;
    private int nbPills = 0;
    private int score = 0;
    private Location initLocation;
    private List<String> propertyMoves = new ArrayList<>();
    private int propertyMoveIndex = 0;
    private boolean isAuto = false;


    /**
     * PacMan constructor.
     * @param manager the object manager
     */
    public PacActor(ObjectManager manager) {
        super(manager, true, DIRECTORY, NB_SPRITES);
        assert manager != null;
    }

    /**
     * Get PacMan's initial location to add to game.
     * @return the initial location
     * @see    Location
     */
    public Location getInitLocation() {
        return initLocation;
    }

    /**
     * Set whether PacMan runs in auto mode or player mode.
     * @param auto true if PacMan runs in auto mode, false if otherwise
     */
    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    /**
     * Set initial location for PacMan.
     * @param initLocation PacMan's initial location
     * @see   Location
     */
    public void setInitLocation(Location initLocation) {
        this.initLocation = initLocation;
    }

    /**
     * Set the random seed for PacMan.
     * @param seed specified seed
     */
    @Override
    protected void setSeed(int seed) {
        RANDOMIZER.setSeed(seed);
    }

    /**
     * Setting player's sequence of moves in auto-movement mode.
     * @param propertyMoveString the string, separated by ',' where each other character represents a
     *                           particular move
     */
    public void setPropertyMoves(String propertyMoveString) {
        if (propertyMoveString != null)
            this.propertyMoves = Arrays.asList(propertyMoveString.split(","));
    }

    /**
     * Method in key listener.
     * @param keyCode key code represents which key was pressed by player.
     */
    @Override
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
            eatItem(getManager());
        }
    }

    /**
     * Overridden act method from Actor class to act within the game.
     */
    @Override
    public void act() {
        show(idSprite);
        idSprite++;
        if (idSprite == NB_SPRITES)
            idSprite = 0;
        if (isAuto)
            moveApproach();
        getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
    }

    /**
     * Overridden move approach method for PacMan which is only used when in auto movement mode.
     */
    @Override
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
            int sign = RANDOMIZER.nextDouble() < 0.5 ? 1 : -1;
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
        eatItem(getManager());
    }

    /**
     * Method for handling PacMan eating an item. Each item will have a different effect upon acquired, and
     * this method will handle that as well.
     * @param manager object manager
     */
    private void eatItem(ObjectManager manager) {
        Location location = getLocation();
        HashableLocation hashLocation = new HashableLocation(location);

        // item exists
        if (manager.getItems().containsKey(hashLocation)) {
            Item item = manager.getItems().get(hashLocation);

            // check of which type
            String itemType = (item instanceof Pill) ? "pills" :
                              (item instanceof Gold) ? "gold"  : "ice";
            if (! (item instanceof Ice)) nbPills++;
            score += item.getScore();
            getManager().decrementNumPillAndGold(item);

            // signals the manager and removes itself
            item.signalManager(manager);
            getBackground().fillCell(location, Game.COLOR_SPACE);
            getGameCallback().pacManEatPillsAndItems(location, itemType);
            item.removeItem(manager);
        }
        String title = "[PacMan in the Multiverse] Current score: " + score;
        getGameGrid().setTitle(title);
    }

    /**
     * Game over checking - whether PacMan has collided with a monster or not.
     * @return true if collided, false if otherwise.
     */
    public boolean collideMonster() {
        for (Monster monster : getManager().getMonsters())
            if (checkCollision(monster))
                return true;
        return false;
    }

    /**
     * Get the closest location of an item that is either a pill or gold. Used only when in auto mode.
     * @return said closest location
     * @see    Location
     */
    private Location closestPillLocation() {
        int currentDistance = INF;
        Location currentLocation = null;
        for (Map.Entry<HashableLocation, Item> entry : getManager().getItems().entrySet()) {
            Item item = entry.getValue();
            if (item instanceof Pill || item instanceof Gold) {
                Location location = entry.getKey().location();
                int distanceToPill = location.getDistanceTo(getLocation());
                if (distanceToPill < currentDistance) {
                    currentLocation = location;
                    currentDistance = distanceToPill;
                }
            }
        }
        return currentLocation;
    }

    /**
     * Let pacman in auto mode follow the moves parsed from properties file. It will read which direction
     * pacman will be heading to, and move accordingly.
     */
    private void followPropertyMoves() {
        String currentMove = propertyMoves.get(propertyMoveIndex);
        switch (currentMove) {
            case "R" -> turn(90);
            case "L" -> turn(-90);
            case "M" -> {
                Location next = getNextMoveLocation();
                if (canMove(next)) {
                    setLocation(next);
                    eatItem(getManager());
                }
            }
        }
        propertyMoveIndex++;
    }
}