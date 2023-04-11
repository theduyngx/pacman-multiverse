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

    protected PacActor pacActor = new PacActor(this);
    private final ArrayList<Monster> monsters;
    private final Monster troll = new Monster(this, MonsterType.Troll);
    private final Monster tx5 = new Monster(this, MonsterType.TX5);

    ///
    private final Properties properties;
    ///


    private final ArrayList<Location> pillAndItemLocations;
    private final ArrayList<Actor> iceCubes;
    private final ArrayList<Actor> goldPieces;
    private final GameCallback gameCallback;
    private int seed = 30006;
    private final ArrayList<Location> propertyPillLocations;
    private final ArrayList<Location> propertyGoldLocations;

    public Game(GameCallback gameCallback, Properties properties) {
        //Setup game
        super(numHorizontalCells, numVerticalCells, 50, false);
        this.gameCallback = gameCallback;
        this.grid = new PacManGameGrid(numHorizontalCells, numVerticalCells);
        ///
        this.properties = properties;
        ///
        this.monsters = new ArrayList<>();
        this.pillAndItemLocations = new ArrayList<>();
        this.iceCubes = new ArrayList<>();
        this.goldPieces = new ArrayList<>();
        this.propertyPillLocations = new ArrayList<>();
        this.propertyGoldLocations = new ArrayList<>();
        parseProperty(properties);
    }

    public void parseProperty(Properties properties) {
        pacActor.setPropertyMoves(properties.getProperty("PacMan.move"));
        pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
        seed = Integer.parseInt(properties.getProperty("seed"));
    }

    public void run() {
        setSimulationPeriod(100);
        setTitle("[PacMan in the Multiverse]");

        //Setup for auto test
        loadPillAndItemsLocations();

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
        setupPillAndItemsLocations();
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
        String[] trollLocations = this.properties.getProperty("Troll.location").split(",");
        String[] tx5Locations = this.properties.getProperty("TX5.location").split(",");
        String[] pacManLocations = this.properties.getProperty("PacMan.location").split(",");

        int trollX = Integer.parseInt(trollLocations[0]);
        int trollY = Integer.parseInt(trollLocations[1]);

        int tx5X = Integer.parseInt(tx5Locations[0]);
        int tx5Y = Integer.parseInt(tx5Locations[1]);

        int pacManX = Integer.parseInt(pacManLocations[0]);
        int pacManY = Integer.parseInt(pacManLocations[1]);

        addActor(troll, new Location(trollX, trollY), Location.NORTH);
        addActor(pacActor, new Location(pacManX, pacManY));
        addActor(tx5, new Location(tx5X, tx5Y), Location.NORTH);
    }

    private int countPillsAndItems() {
        int pillsAndItemsCount = 0;
        for (int y = 0; y < numVerticalCells; y++) {
            for (int x = 0; x < numHorizontalCells; x++) {
                Location location = new Location(x, y);
                int a = grid.getCell(location);
                if (a == 1 && propertyPillLocations.size() == 0) { // Pill
                    pillsAndItemsCount++;
                }
                else if (a == 3 && propertyGoldLocations.size() == 0) { // Gold
                    pillsAndItemsCount++;
                }
            }
        }
        if (propertyPillLocations.size() != 0) {
            pillsAndItemsCount += propertyPillLocations.size();
        }

        if (propertyGoldLocations.size() != 0) {
            pillsAndItemsCount += propertyGoldLocations.size();
        }

        return pillsAndItemsCount;
    }

    public ArrayList<Location> getPillAndItemLocations() {
        return pillAndItemLocations;
    }


    private void loadPillAndItemsLocations() {
        String pillsLocationString = properties.getProperty("Pills.location");
        if (pillsLocationString != null) {
            String[] singlePillLocationStrings = pillsLocationString.split(";");
            for (String singlePillLocationString: singlePillLocationStrings) {
                String[] locationStrings = singlePillLocationString.split(",");
                propertyPillLocations.add(new Location(Integer.parseInt(locationStrings[0]),
                                          Integer.parseInt(locationStrings[1])));
            }
        }

        String goldLocationString = properties.getProperty("Gold.location");
        if (goldLocationString != null) {
            String[] singleGoldLocationStrings = goldLocationString.split(";");
            for (String singleGoldLocationString: singleGoldLocationStrings) {
                String[] locationStrings = singleGoldLocationString.split(",");
                propertyGoldLocations.add(new Location(Integer.parseInt(locationStrings[0]),
                                          Integer.parseInt(locationStrings[1])));
            }
        }
    }
    private void setupPillAndItemsLocations() {
        for (int y = 0; y < numVerticalCells; y++) {
            for (int x = 0; x < numHorizontalCells; x++) {
                Location location = new Location(x, y);
                int a = grid.getCell(location);
                if (a == 1 && propertyPillLocations.size() == 0) {
                    pillAndItemLocations.add(location);
                }
                if (a == 3 &&  propertyGoldLocations.size() == 0) {
                    pillAndItemLocations.add(location);
                }
                if (a == 4) {
                    pillAndItemLocations.add(location);
                }
            }
        }


        if (propertyPillLocations.size() > 0) {
            pillAndItemLocations.addAll(propertyPillLocations);
        }
        if (propertyGoldLocations.size() > 0) {
            pillAndItemLocations.addAll(propertyGoldLocations);
        }
    }

    private void drawGrid(GGBackground bg) {
        bg.clear(Color.gray);
        bg.setPaintColor(Color.white);
        for (int y = 0; y < numVerticalCells; y++) {
            for (int x = 0; x < numHorizontalCells; x++) {
                bg.setPaintColor(Color.white);
                Location location = new Location(x, y);
                int a = grid.getCell(location);
                if (a > 0)
                    bg.fillCell(location, Color.lightGray);
                if (a == 1 && propertyPillLocations.size() == 0) { // Pill
                    putPill(bg, location);
                }
                else if (a == 3 && propertyGoldLocations.size() == 0) { // Gold
                    putGold(bg, location);
                }
                else if (a == 4) {
                    putIce(bg, location);
                }
            }
        }

        for (Location location : propertyPillLocations) {
            putPill(bg, location);
        }

        for (Location location : propertyGoldLocations) {
            putGold(bg, location);
        }
    }

    public int getNumHorizontalCells(){
        return this.numHorizontalCells;
    }
    public int getNumVerticalCells() {
        return this.numVerticalCells;
    }
}
