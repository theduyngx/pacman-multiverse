package src;

import ch.aplu.jgamegrid.*;
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
    private final PacManGameGrid grid;

    // object manager
    protected ObjectManager manager;

    /**
     * Game class constructor.
     * @param properties    properties object read from properties file for instantiating actors and items
     */
    public Game(Properties properties) {

        // Setup game
        super(numHorizontalCells, numVerticalCells, CELL_SIZE, false);
        this.grid = new PacManGameGrid(numHorizontalCells, numVerticalCells);
        this.manager = new ObjectManager(this);

        // parse properties and instantiate objects
        manager.instantiatePacActor();
        manager.parseProperties(properties);
        manager.instantiateObjects(grid);

        // instantiate actors
        manager.instantiateMonsters(properties);
    }

    /**
     * Get the game grid.
     * @return the game grid
     */
    public PacManGameGrid getGrid() {
        return grid;
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
        addKeyRepeatListener(manager.getPacActor());
        setKeyRepeatPeriod(150);
        putPacActor();
        putMonsters();

        // Run the game
        PacActor pacActor = manager.getPacActor();
        doRun();
        show();

        // check win / lose conditions
        boolean hasPacmanEatAllPills, hasPacmanBeenHit;
        putItems(bg);
        do {
            hasPacmanBeenHit = pacActor.collideMonster();
            hasPacmanEatAllPills = manager.getNumPillsAndGold() <= 0;
            delay(10);
        } while (! hasPacmanBeenHit && ! hasPacmanEatAllPills);
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
        manager.getGameCallback().endOfGame(title);
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
        PacActor pacActor = manager.getPacActor();
        addActor(pacActor, pacActor.getInitLocation());
    }
}