// PacMan.java
// Simple PacMan implementation
package src;

import ch.aplu.jgamegrid.*;
import src.utility.GameCallback;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
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

    private int getNumItems() {
        return manager.getItems().size();
    }

    public void run() {
        setSimulationPeriod(100);
        setTitle("[PacMan in the Multiverse]");

        GGBackground bg = getBg();
        drawGrid(bg);

        //Setup Random seeds
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


        //Run the game
        doRun();
        show();
        // Loop to look for collision in the application thread
        // This makes it improbable that we miss a hit
        boolean hasPacmanBeenHit;
        boolean hasPacmanEatAllPills;
        putItems(bg);
        int maxPillsAndItems = getNumItems();

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

    public ArrayList<Location> getPillAndItemLocations() {
        return new ArrayList<>(manager.getItems().keySet());
    }

    private void drawGrid(GGBackground bg) {
        // set the background
        bg.clear(Color.gray);
        bg.setPaintColor(Color.white);

        // draw the maze (its border and items)
        for (int y = 0; y < numVerticalCells; y++)
            for (int x = 0; x < numHorizontalCells; x++) {
                bg.setPaintColor(Color.white);
                Location location = new Location(x, y);
                if (grid.getCell(location) != PacManGameGrid.BlockType.ERROR)
                    bg.fillCell(location, Color.WHITE);
                if (grid.getCell(location) == PacManGameGrid.BlockType.WALL)
                    bg.fillCell(location, Color.lightGray);
            }
    }

    public void putItems(GGBackground bg) {
        // putting all items
        for (Map.Entry<Location, Item> entry : manager.getItems().entrySet()) {
            Location location = entry.getKey();
            Item item = entry.getValue();
            item.putItem(bg, this, location);
        }
    }

    // putting all monsters to grid
    public void putMonsters() {
        for (Map.Entry<Location, Monster> entry : manager.getMonsters().entrySet()) {
            Location location = entry.getKey();
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
