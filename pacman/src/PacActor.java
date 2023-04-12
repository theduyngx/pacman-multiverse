// PacActor.java
// Used for PacMan
package src;

import ch.aplu.jgamegrid.*;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PacActor extends Actor implements GGKeyRepeatListener {
    private static final int nbSprites = 4;
    private int idSprite = 0;
    private int nbPills = 0;
    private int score = 0;
    private final Game game;
    private List<String> propertyMoves = new ArrayList<>();
    private int propertyMoveIndex = 0;
    private final Random randomizer = new Random();
    private final ObjectManager manager;
    private boolean isAuto = false;


    public PacActor(Game game, ObjectManager manager) {
        super(true, "sprites/pacpix.gif", nbSprites);  // Rotatable
        this.game = game;
        this.manager = manager;
    }


    public void setAuto(boolean auto) {
        isAuto = auto;
    }


    public void setSeed(int seed) {
        randomizer.setSeed(seed);
    }

    public void setPropertyMoves(String propertyMoveString) {
        if (propertyMoveString != null) {
            this.propertyMoves = Arrays.asList(propertyMoveString.split(","));
        }
    }

    public void keyRepeated(int keyCode) {
        if (isAuto) {
            return;
        }
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
            eatPill(manager, next);
        }
    }

    public void act() {
        show(idSprite);
        idSprite++;
        if (idSprite == nbSprites)
            idSprite = 0;

        if (isAuto) {
            moveInAutoMode();
        }
        this.game.getGameCallback().pacManLocationChanged(getLocation(), score, nbPills);
    }

    private Location closestPillLocation() {
        int currentDistance = 1000;
        Location currentLocation = null;
        List<Location> pillAndItemLocations = game.getPillAndItemLocations();
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
                    eatPill(manager, next);
                }
            }
        }
        propertyMoveIndex++;
    }

    private void moveInAutoMode() {
        if (propertyMoves.size() > propertyMoveIndex) {
            followPropertyMoves();
            return;
        }
        Location closestPill = closestPillLocation();
        double oldDirection = getDirection();

        Location.CompassDirection compassDir =
                getLocation().get4CompassDirectionTo(closestPill);
        Location next = getLocation().getNeighbourLocation(compassDir);
        setDirection(compassDir);
        if (canMove(next)) {
            setLocation(next);
        }
        else {
            // normal movement
            int sign = randomizer.nextDouble() < 0.5 ? 1 : -1;
            setDirection(oldDirection);
            turn(sign * 90);  // Try to turn left/right
            next = getNextMoveLocation();
            if (canMove(next)) {
                setLocation(next);
            }
            else {
                setDirection(oldDirection);
                next = getNextMoveLocation();
                if (canMove(next)) { // Try to move forward
                    setLocation(next);
                }
                else {
                    setDirection(oldDirection);
                    turn(-sign * 90);  // Try to turn right/left
                    next = getNextMoveLocation();
                    if (canMove(next)) {
                        setLocation(next);
                    }
                    else {
                        setDirection(oldDirection);
                        turn(180);  // Turn backward
                        next = getNextMoveLocation();
                        setLocation(next);
                    }
                }
            }
        }
        eatPill(manager, next);
    }

    private boolean canMove(Location location) {
        Color c = getBackground().getColor(location);
        return !c.equals(Color.gray) && location.getX() < game.getNumHorizontalCells()
                && location.getX() >= 0 && location.getY() < game.getNumVerticalCells() && location.getY() >= 0;
    }

    public int getNbPills() {
        return nbPills;
    }

    private void eatPill(ObjectManager manager, Location location) {
        if (manager.getPills().containsKey(location)) {
            nbPills++;
            score++;
            getBackground().fillCell(location, Color.lightGray);
            game.getGameCallback().pacManEatPillsAndItems(location, "pills");
            Pill pill = manager.getPills().get(location);
            pill.removeItem(manager);
        }
        else if (manager.getGolds().containsKey(location)) {
            nbPills++;
            score+= 5;
            getBackground().fillCell(location, Color.lightGray);
            game.getGameCallback().pacManEatPillsAndItems(location, "gold");
            Gold gold = manager.getGolds().get(location);
            gold.removeItem(manager);
        }
        else if (manager.getIces().containsKey(location)) {
            getBackground().fillCell(location, Color.lightGray);
            game.getGameCallback().pacManEatPillsAndItems(location, "ice");
            Ice ice = manager.getIces().get(location);
            ice.removeItem(manager);
        }
        String title = "[PacMan in the Multiverse] Current score: " + score;
        gameGrid.setTitle(title);
    }
}
