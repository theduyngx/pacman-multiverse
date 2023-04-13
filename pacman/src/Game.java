// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import src.utility.GameCallback;

import java.awt.*;
import java.util.Map;
import java.util.Properties;

public class Game extends GameGrid {
    public final static Color COLOR_LOSE = Color.red;
    public final static Color COLOR_WIN = Color.yellow;
    public final static Color COLOR_BACKGROUND = Color.white;
    public final static String LOSE_MESSAGE = "GAME OVER";
    public final static String WIN_MESSAGE = "YOU WIN";
    private final static int numHorizontalCells = 20;
    private final static int numVerticalCells = 11;
    protected PacManGameGrid grid;
    protected PacActor pacActor;
    protected ObjectManager manager;
    private final GameCallback gameCallback;
    private final Monster troll = new Troll(this);
    private final Monster tx5 = new TX5(this);

    public Game(GameCallback gameCallback, Properties properties) {
        //Setup game
        super(numHorizontalCells, numVerticalCells, 50, false);
        this.gameCallback = gameCallback;
        this.grid = new PacManGameGrid(numHorizontalCells, numVerticalCells);
        this.pacActor = new PacActor(this);
        this.manager = new ObjectManager(pacActor);
        manager.parseProperties(properties);
        manager.instantiateObjects(grid);
        pacActor.setManager(manager);


        /////////////
        // temporarily initialize troll and tx5
        String[] trollLocations = properties.getProperty("Troll.location").split(",");
        String[] tx5Locations = properties.getProperty("TX5.location").split(",");
        int trollX = Integer.parseInt(trollLocations[0]);
        int trollY = Integer.parseInt(trollLocations[1]);

        int tx5X = Integer.parseInt(tx5Locations[0]);
        int tx5Y = Integer.parseInt(tx5Locations[1]);

        addActor(troll, new Location(trollX, trollY), Location.NORTH);
        addActor(tx5, new Location(tx5X, tx5Y), Location.NORTH);
        /////////////
    }

    public GameCallback getGameCallback() {
        return gameCallback;
    }

    public void run() {
        setSimulationPeriod(100);
        setTitle("[PacMan in the Multiverse]");

        GGBackground bg = getBg();
        drawGrid(bg);

        // Setup Random seeds
        int seed = manager.getSeed();
        pacActor.setSeed(seed);
        troll.setSeed(seed);
        tx5.setSeed(seed);
        addKeyRepeatListener(pacActor);
        setKeyRepeatPeriod(150);
        troll.setSlowDown(3);
        tx5.setSlowDown(3);
        pacActor.setSlowDown(3);
        tx5.stopMoving(5);
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
            hasPacmanBeenHit = troll.getLocation().equals(pacActor.getLocation()) ||
                    tx5.getLocation().equals(pacActor.getLocation());

            /// NOTE: not yet correct since pacman doesn't have to eat all Ice cubes
            hasPacmanEatAllPills = manager.getItems().size() == 0;

            delay(10);
        } while(!hasPacmanBeenHit && !hasPacmanEatAllPills);
        delay(120);

        Location loc = pacActor.getLocation();
        troll.setStopMoving(true);
        tx5.setStopMoving(true);
        pacActor.removeSelf();

        String title = "";
        if (hasPacmanBeenHit) {
            bg.setPaintColor(COLOR_LOSE);
            title = LOSE_MESSAGE;
            addActor(new Actor("sprites/explosion3.gif"), loc);
        }
        else if (hasPacmanEatAllPills) {
            bg.setPaintColor(COLOR_WIN);
            title = WIN_MESSAGE;
        }
        setTitle(title);
        gameCallback.endOfGame(title);
        doPause();
    }

    private void drawGrid(GGBackground bg) {
        // set the background
        bg.clear(Color.gray);
        bg.setPaintColor(COLOR_BACKGROUND);

        // draw the maze (its border and items)
        for (int y = 0; y < numVerticalCells; y++) {
            for (int x = 0; x < numHorizontalCells; x++) {
                bg.setPaintColor(COLOR_BACKGROUND);
                Location location = new Location(x, y);
                if (grid.getCell(location) != PacManGameGrid.BlockType.ERROR)
                    bg.fillCell(location, Color.lightGray);
                if (grid.getCell(location) == PacManGameGrid.BlockType.WALL)
                    bg.fillCell(location, ObjectManager.COLOR_WALL);
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

    public int getNumHorizontalCells() {
        return numHorizontalCells;
    }
    public int getNumVerticalCells() {
        return numVerticalCells;
    }
}