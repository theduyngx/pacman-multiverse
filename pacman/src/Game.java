package src;

import ch.aplu.jgamegrid.*;
import java.awt.*;
import java.util.Map;
import java.util.Properties;


/**
 * Based on skeleton code for SWEN20003 Project, Semester 2, 2022, The University of Melbourne.
 * The Game class is responsible for putting items and actors onto its own grid, as well as running
 * the game.
 * @see GameGrid
 *
 * @author The Duy Nguyen            - 1100548 (theduyn@student.unimelb.edu.au)
 * @author Ramon Javier L. Felipe VI - 1233281 (rfelipe@student.unimelb.edu.au)
 * @author Jonathan Chen Jie Kong    - 1263651 (jonathanchen@student.unimelb.edu.au)
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
    private final static int NUM_HORIZONTAL_CELLS = 20;
    private final static int NUM_VERTICAL_CELLS = 11;
    private final PacManGameGrid GRID;

    // object manager
    protected ObjectManager manager;

    /**
     * Game class constructor.
     * @param properties properties object read from properties file for instantiating actors and items
     * @see              Properties
     */
    public Game(Properties properties) {

        // Setup game
        super(NUM_HORIZONTAL_CELLS, NUM_VERTICAL_CELLS, CELL_SIZE, false);
        this.GRID = new PacManGameGrid(NUM_HORIZONTAL_CELLS, NUM_VERTICAL_CELLS);
        this.manager = new ObjectManager(this);

        // parse properties and instantiate objects
        manager.instantiatePacActor();
        manager.parseProperties(properties);
        manager.instantiateObjects(GRID);

        // instantiate actors
        manager.instantiateMonsters(properties);
    }

    /**
     * Get the game grid.
     * @return the game grid
     * @see    PacManGameGrid
     */
    public PacManGameGrid getGrid() {
        return GRID;
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
     * @see      GGBackground
     */
    private void drawGrid(GGBackground bg) {
        // set the background
        bg.clear(COLOR_WALL);
        bg.setPaintColor(COLOR_BACKGROUND);

        // draw the maze (its border and items)
        for (int y = 0; y < NUM_VERTICAL_CELLS; y++) {
            for (int x = 0; x < NUM_HORIZONTAL_CELLS; x++) {
                bg.setPaintColor(COLOR_BACKGROUND);
                Location location = new Location(x, y);
                // space
                if (GRID.getCell(location) != PacManGameGrid.BlockType.ERROR)
                    bg.fillCell(location, COLOR_SPACE);
                // wall -> added to wall map in manager
                if (GRID.getCell(location) == PacManGameGrid.BlockType.WALL) {
                    HashableLocation.putLocationHash(manager.getWalls(), location, 1);
                    bg.fillCell(location, COLOR_WALL);
                }
            }
        }
    }


    /**
     * Putting all items to game. Items once put to the game will exist within the game as well as the
     * grid, and it will also be visualized to the background.
     * @param background the background
     * @see              GGBackground
     * @see              Item
     */
    public void putItems(GGBackground background) {
        for (Map.Entry<HashableLocation, Item> entry : manager.getItems().entrySet()) {
            Location location = entry.getKey().location();
            Item item = entry.getValue();
            item.putItem(background, this, location);
        }
    }

    /**
     * Putting all monsters to game. Monsters once put to the game will exist within the game as well as the
     * grid, and it will also be visualized to the background.
     * @see Monster
     */
    public void putMonsters() {
        for (int i=0; i<manager.getMonsters().size(); i++) {
            Monster monster = manager.getMonsters().get(i);
            Location location = monster.getInitLocation();
            addActor(monster, location, Location.NORTH);
        }
    }

    /**
     * Putting PacMan to game. Similar to put monsters, PacMan will also be added to the game in the same
     * manner.
     * @see PacActor
     */
    public void putPacActor() {
        PacActor pacActor = manager.getPacActor();
        addActor(pacActor, pacActor.getInitLocation());
    }
}