// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import src.utility.GameCallback;

import java.awt.*;
import java.util.ArrayList;
import java.util.Properties;

public class Game extends GameGrid {
    private final static int numHorizontalCells = 20;
    private final static int numVerticalCells = 11;
    protected PacManGameGrid grid;

    protected PacActor pacActor;
    protected ObjectManager manager;
    private final Monster troll = new Monster(this, MonsterType.Troll);
    private final Monster tx5 = new Monster(this, MonsterType.TX5);
    private final GameCallback gameCallback;
    private int seed = 30006;

    public Game(GameCallback gameCallback, Properties properties) {
        //Setup game
        super(numHorizontalCells, numVerticalCells, 50, false);
        this.gameCallback = gameCallback;
        this.grid = new PacManGameGrid(numHorizontalCells, numVerticalCells);
        this.pacActor = new PacActor(this, manager);
        this.manager = new ObjectManager(pacActor);
        manager.parseProperties(properties);
        manager.instantiateItems(grid);
    }

    public void run() {
        setSimulationPeriod(100);
        setTitle("[PacMan in the Multiverse]");

        GGBackground bg = getBg();
        drawGrid(bg);

        //Setup Random seeds
        pacActor.setSeed(seed);
        troll.setSeed(seed);
        tx5.setSeed(seed);
        addKeyRepeatListener(pacActor);
        setKeyRepeatPeriod(150);
        troll.setSlowDown(3);
        tx5.setSlowDown(3);
        pacActor.setSlowDown(3);
        tx5.stopMoving(5);
        setupActorLocations();



        //Run the game
        doRun();
        show();
        // Loop to look for collision in the application thread
        // This makes it improbable that we miss a hit
        boolean hasPacmanBeenHit;
        boolean hasPacmanEatAllPills;
        manager.putItems(bg, this);
        int maxPillsAndItems = countPillsAndItems();

        do {
            hasPacmanBeenHit = troll.getLocation().equals(pacActor.getLocation()) ||
                    tx5.getLocation().equals(pacActor.getLocation());
            hasPacmanEatAllPills = pacActor.getNbPills() >= maxPillsAndItems;
            delay(10);
        } while(!hasPacmanBeenHit && !hasPacmanEatAllPills);
        delay(120);

        Location loc = pacActor.getLocation();
        troll.setStopMoving(true);
        tx5.setStopMoving(true);
        pacActor.removeSelf();

        String title = "";
        if (hasPacmanBeenHit) {
            bg.setPaintColor(Color.red);
            title = "GAME OVER";
            addActor(new Actor("sprites/explosion3.gif"), loc);
        }
        else if (hasPacmanEatAllPills) {
            bg.setPaintColor(Color.yellow);
            title = "YOU WIN";
        }
        setTitle(title);
        gameCallback.endOfGame(title);
        doPause();
    }

    public GameCallback getGameCallback() {
        return gameCallback;
    }

    private void setupActorLocations() {
        manager.putActors(this);
    }

    private int countPillsAndItems() {
        return manager.getPills().size() + manager.getIces().size() + manager.getGolds().size();
    }

    public ArrayList<Location> getPillAndItemLocations() {
        ArrayList<Location> pillLocations = new ArrayList<>(manager.getPills().keySet());
        ArrayList<Location> iceLocations  = new ArrayList<>(manager.getIces ().keySet());
        ArrayList<Location> collectibles  = new ArrayList<>(manager.getGolds().keySet());
        collectibles.addAll(iceLocations);
        collectibles.addAll(pillLocations);
        return collectibles;
    }

    private void drawGrid(GGBackground bg) {
        // set the background
        bg.clear(Color.gray);
        bg.setPaintColor(Color.white);

        // draw the maze (its border and items)
        for (int y = 0; y < numVerticalCells; y++) {
            for (int x = 0; x < numHorizontalCells; x++) {
                bg.setPaintColor(Color.white);
                Location location = new Location(x, y);
                int a = grid.getCell(location);
                if (a > 0)
                    bg.fillCell(location, Color.lightGray);
                else {
                    manager.putItems(bg, this);
                }
//                if (a == 1 && manager.getPills().size() == 0) {
//
////                    putPill(bg, location);
//                }
//                else if (a == 3 && manager.getGolds().size() == 0) {
//
////                    putGold(bg, location);
//                }
//                else if (a == 4) {
//
////                    putIce(bg, location);
//                }
//            }
//        }
//
//        for (Location location : manager.getPills().keySet()) {
////            putPill(bg, location);
//        }
//
//        for (Location location : manager.getGolds().keySet()) {
////            putGold(bg, location);
//        }
            }
        }
    }

    public int getNumHorizontalCells() {
        return numHorizontalCells;
    }
    public int getNumVerticalCells() {
        return numVerticalCells;
    }
}
