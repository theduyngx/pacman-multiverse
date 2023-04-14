// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import src.utility.GameCallback;

import java.awt.*;
import java.util.Map;
import java.util.Properties;

public class Game extends GameGrid {
    // draw grid colors
    public final static Color COLOR_LOSE = Color.red;
    public final static Color COLOR_WIN = Color.yellow;
    public final static Color COLOR_BACKGROUND = Color.white;
    public final static Color COLOR_WALL = Color.gray;
    public final static Color COLOR_SPACE = Color.lightGray;

    // win/lose messages
    public final static String LOSE_MESSAGE = "GAME OVER";
    public final static String WIN_MESSAGE = "YOU WIN";

    // game grid
    public final static int CELL_SIZE = 50;
    private final static int numHorizontalCells = 20;
    private final static int numVerticalCells = 11;
    private final int xLeft;
    private final int yTop;
    private final int xRight;
    private final int yBottom;
    private final PacManGameGrid grid;

    // actors and callback
    protected PacActor pacActor;
    protected ObjectManager manager;
    private final GameCallback gameCallback;

    public Game(GameCallback gameCallback, Properties properties) {
        // Setup game
        super(numHorizontalCells, numVerticalCells, CELL_SIZE, false);
        this.gameCallback = gameCallback;
        this.grid = new PacManGameGrid(numHorizontalCells, numVerticalCells);
        this.pacActor = new PacActor(this);
        this.manager = new ObjectManager(pacActor);
        manager.parseProperties(properties);
        manager.instantiateObjects(grid);
        pacActor.setManager(manager);

        // Setup grid border
        xLeft   = 0;
        yTop    = 0;
        xRight  = numHorizontalCells;
        yBottom = numVerticalCells;


        /////////////
        manager.instantiateMonsters(this, properties);
        /////////////
    }

    public GameCallback getGameCallback() {
        return gameCallback;
    }

    public int getXLeft() {
        return xLeft;
    }

    public int getYTop() {
        return yTop;
    }

    public int getXRight() {
        return xRight;
    }

    public int getYBottom() {
        return yBottom;
    }

    public void run() {
        setSimulationPeriod(100);
        setTitle("[PacMan in the Multiverse]");

        GGBackground bg = getBg();
        drawGrid(bg);

        // Setup Random seeds
        int seed = manager.getSeed();
        pacActor.setSeed(seed);
        addKeyRepeatListener(pacActor);
        setKeyRepeatPeriod(150);
        pacActor.setSlowDown(3);
        putPacActor();
        putMonsters();


        // Run the game
        doRun();
        show();
        // Loop to look for collision in the application thread
        // This makes it improbable that we miss a hit
        boolean hasPacmanBeenHit;
        boolean hasPacmanEatAllPills;
        putItems(bg);

        do {
            hasPacmanBeenHit = pacActor.collideMonster();
            hasPacmanEatAllPills = manager.getNumPillsAndGold() <= 0;
            delay(10);
        } while(!hasPacmanBeenHit && !hasPacmanEatAllPills);
        delay(120);

        Location loc = pacActor.getLocation();
        manager.setMonstersStopMoving();
        pacActor.removeSelf();

        String title;
        if (hasPacmanBeenHit) {
            bg.setPaintColor(COLOR_LOSE);
            title = LOSE_MESSAGE;
            addActor(new Actor("sprites/explosion3.gif"), loc);
        }
        else {
            bg.setPaintColor(COLOR_WIN);
            title = WIN_MESSAGE;
        }
        setTitle(title);
        gameCallback.endOfGame(title);
        doPause();
    }

    private void drawGrid(GGBackground bg) {
        // set the background
        bg.clear(COLOR_WALL);
        bg.setPaintColor(COLOR_BACKGROUND);

        // draw the maze (its border and items)
        for (int y = 0; y < numVerticalCells; y++) {
            for (int x = 0; x < numHorizontalCells; x++) {
                bg.setPaintColor(COLOR_BACKGROUND);
                Location location = new Location(x, y);
                if (grid.getCell(location) != PacManGameGrid.BlockType.ERROR)
                    bg.fillCell(location, COLOR_SPACE);
                if (grid.getCell(location) == PacManGameGrid.BlockType.WALL) {
                    HashableLocation.putLocationHash(manager.getWalls(), location, 1);
                    bg.fillCell(location, COLOR_WALL);
                }
            }
        }
    }

    public void putItems(GGBackground bg) {
        // putting all items
        for (Map.Entry<HashableLocation, Item> entry : manager.getItems().entrySet()) {
            Location location = entry.getKey().location();
            Item item = entry.getValue();
            item.putItem(bg, this, location);
        }
    }

    // putting all monsters to grid
    public void putMonsters() {
        for (Map.Entry<HashableLocation, Monster> entry : manager.getMonsters().entrySet()) {
            Location location = entry.getKey().location();
            Monster monster = entry.getValue();
            addActor(monster, location, Location.NORTH);
        }
    }

    // putting PacMan
    public void putPacActor() {
        addActor(pacActor, pacActor.getInitLocation());
    }
}