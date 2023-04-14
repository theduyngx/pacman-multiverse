package src;

import ch.aplu.jgamegrid.*;
import src.utility.GameCallback;

import java.awt.*;
import java.util.Map;
import java.util.Properties;

/**
 * Based on skeleton code for SWEN20003 Project, Semester 2, 2022, The University of Melbourne.
 * The Game class is responsible for putting items and actors onto its own grid, as well as running
 * the game.
 *
 * @author The Duy Nguyen            - 1100548 (theduyn@student.unimelb.edu.au)
 * @author Ramon Javier L. Felipe VI - 1233281 (...)
 * @author Jonathan - ... (...)
 */
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
    public final static int STRETCH_RATE = 2;
    public final static int CELL_SIZE = 20 * STRETCH_RATE;
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

    /**
     * Game class constructor.
     * @param gameCallback  GameCallBack object
     * @param properties    properties object read from properties file for instantiating actors and items
     */
    public Game(GameCallback gameCallback, Properties properties) {

        // Setup game
        super(numHorizontalCells, numVerticalCells, CELL_SIZE, false);
        this.gameCallback = gameCallback;
        this.grid = new PacManGameGrid(numHorizontalCells, numVerticalCells);
        this.pacActor = new PacActor(this);
        this.manager = new ObjectManager(pacActor);

        // parse properties and instantiate objects
        manager.parseProperties(properties);
        manager.instantiateObjects(grid);
        pacActor.setManager(manager);

        // Setup grid border
        xLeft   = 0;
        yTop    = 0;
        xRight  = numHorizontalCells;
        yBottom = numVerticalCells;

        // instantiate monsters
        manager.instantiateMonsters(this, properties);
    }

    /**
     * Get GameCallBack object; used when actors act and require game to signal the callback.
     * @return the GameCallBack object
     */
    public GameCallback getGameCallback() {
        return gameCallback;
    }

    /**
     * Get leftmost x-coordinate of the grid; used for border checking when initiating movement.
     * @return leftmost x-coordinate
     */
    public int getXLeft() {
        return xLeft;
    }

    /**
     * Get topmost y-coordinate of the grid; used for border checking when initiating movement.
     * @return leftmost y-coordinate
     */
    public int getYTop() {
        return yTop;
    }

    /**
     * Get rightmost x-coordinate of the grid; used for border checking when initiating movement.
     * @return rightmost x-coordinate
     */
    public int getXRight() {
        return xRight;
    }

    /**
     * Get bottommost y-coordinate of the grid; used for border checking when initiating movement.
     * @return bottommost y-coordinate
     */
    public int getYBottom() {
        return yBottom;
    }


    /**
     * Run the game. Upon running, all actors and items will be put to the game, and it will continually
     * check for a winning / losing condition until either one is met.
     */
    public void run() {
        // set up game window
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

        // check win / lose conditions
        boolean hasPacmanEatAllPills, hasPacmanBeenHit;
        putItems(bg);
        do {
            hasPacmanBeenHit = pacActor.collideMonster();
            hasPacmanEatAllPills = manager.getNumPillsAndGold() <= 0;
            delay(10);
        } while(!hasPacmanBeenHit && !hasPacmanEatAllPills);
        delay(120);

        // upon winning / losing
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


    /**
     * Draw the game's grid. The grid includes empty space and walls.
     * @param bg background object for grid
     */
    private void drawGrid(GGBackground bg) {
        // set the background
        bg.clear(COLOR_WALL);
        bg.setPaintColor(COLOR_BACKGROUND);

        // draw the maze (its border and items)
        for (int y = 0; y < numVerticalCells; y++) {
            for (int x = 0; x < numHorizontalCells; x++) {
                bg.setPaintColor(COLOR_BACKGROUND);
                Location location = new Location(x, y);
                // space
                if (grid.getCell(location) != PacManGameGrid.BlockType.ERROR)
                    bg.fillCell(location, COLOR_SPACE);
                // wall -> added to wall map in manager
                if (grid.getCell(location) == PacManGameGrid.BlockType.WALL) {
                    HashableLocation.putLocationHash(manager.getWalls(), location, 1);
                    bg.fillCell(location, COLOR_WALL);
                }
            }
        }
    }


    /**
     * Putting all items to game.
     */
    public void putItems(GGBackground bg) {
        for (Map.Entry<HashableLocation, Item> entry : manager.getItems().entrySet()) {
            Location location = entry.getKey().location();
            Item item = entry.getValue();
            item.putItem(bg, this, location);
        }
    }

    /**
     * Putting all monsters to game.
     */
    public void putMonsters() {
        for (Map.Entry<HashableLocation, Monster> entry : manager.getMonsters().entrySet()) {
            Location location = entry.getKey().location();
            Monster monster = entry.getValue();
            addActor(monster, location, Location.NORTH);
        }
    }

    /**
     * Putting PacMan to game.
     */
    public void putPacActor() {
        addActor(pacActor, pacActor.getInitLocation());
    }
}