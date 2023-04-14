package src;

import ch.aplu.jgamegrid.Location;
import java.util.*;

public class ObjectManager {
    private final static int INIT_SEED = 30006;

    private final PacActor pacActor;
    private final HashMap<HashableLocation, Monster> monsters;
    private final HashMap<HashableLocation, Item> items;
    private final HashMap<HashableLocation, Integer> walls;
    private int seed = INIT_SEED;
    private int numPillsAndGold = 0;

    // constructor
    public ObjectManager(PacActor pacActor) {
        if (pacActor == null) {
            System.exit(1);
        }
        this.pacActor = pacActor;
        this.monsters = new HashMap<>();
        this.items = new HashMap<>();
        this.walls = new HashMap<>();
    }

    // getters
    public PacActor getPacActor() {
        return pacActor;
    }

    public HashMap<HashableLocation, Monster> getMonsters() {
        return monsters;
    }

    public HashMap<HashableLocation, Item> getItems() {
        return items;
    }

    public HashMap<HashableLocation, Integer> getWalls() {
        return walls;
    }

    public int getSeed() {
        return seed;
    }

    // get locations of all items
    public ArrayList<Location> getItemLocations() {
        return new ArrayList<>(items.keySet().stream().map(HashableLocation::location).toList());
    }

    public int getNumPillsAndGold() {
        return numPillsAndGold;
    }

    /**
     * Decrementing the number of pill and gold pieces when PacMan eats one of the pieces.
     * Hence, used in eatItem of PacActor. It will also check if a specified item is of instance
     * Gold or Pill, and if not then it will not decrement.
     * @param item specified eaten item
     */
    protected void decrementNumPillAndGold(Item item) {
        if (item instanceof Gold || item instanceof Pill)
            numPillsAndGold--;
    }

    // parsing properties that do not require an Actor instantiation
    public void parseProperties(Properties properties) {
        seed = Integer.parseInt(properties.getProperty("seed"));

        // parse pacman
        pacActor.setPropertyMoves(properties.getProperty("PacMan.move"));
        pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
        String[] pacManLocations = properties.getProperty("PacMan.location").split(",");
        int pacManX = Integer.parseInt(pacManLocations[0]);
        int pacManY = Integer.parseInt(pacManLocations[1]);
        pacActor.setInitLocation(new Location(pacManX, pacManY));

        // parse the pill locations if there is pill location
        if (properties.containsKey("Pills.location")) {
            String[] pillLocations = properties.getProperty("Pills.location").split(";");
            for (String pL : pillLocations) {
                String[] pos = pL.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                if (! HashableLocation.containLocationHash(items, location)) {
                    Pill pill = new Pill();
                    HashableLocation.putLocationHash(items, location, pill);
                    numPillsAndGold++;
                }
            }
        }

        // parse the gold locations if there is gold location
        if (properties.containsKey("Golds.location")) {
            String[] goldLocations = properties.getProperty("Gold.location").split(";");
            for (String gL : goldLocations) {
                String[] pos = gL.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                if (! HashableLocation.containLocationHash(items, location)) {
                    Gold gold = new Gold();
                    HashableLocation.putLocationHash(items, location, gold);
                    numPillsAndGold++;
                }
            }
        }
    }


    // instantiate the items in the grid and put them in their respective hashmaps
    public void instantiateObjects(PacManGameGrid grid) {
        for (int col = 0; col < grid.getNumVerticalCells(); col++)
            for (int row = 0; row < grid.getNumHorizontalCells(); row++) {
                PacManGameGrid.BlockType itemType = grid.getMazeArray()[col][row];

                // ignore if location is already occupied
                Location location = new Location(row, col);
                if (HashableLocation.containLocationHash(items, location)) continue;

                // otherwise add
                switch (itemType) {
                    case PILL -> {
                        Pill pill = new Pill();
                        HashableLocation.putLocationHash(items, location, pill);
                        numPillsAndGold++;
                    }
                    case GOLD -> {
                        Gold gold = new Gold();
                        HashableLocation.putLocationHash(items, location, gold);
                        numPillsAndGold++;
                    }
                    case ICE -> {
                        Ice ice = new Ice();
                        HashableLocation.putLocationHash(items, location, ice);
                    }
                }
            }
    }

    /**
     * (WIP) Instantiating monsters, for now this is clunky and needs plenty of work done
     * @param game       the game
     * @param properties properties to parse for monsters
     */
    public void instantiateMonsters(Game game, Properties properties) {
        if (properties.containsKey("TX5.location")) {
            String[] TX5Locations = properties.getProperty("TX5.location").split(";");
            for (String loc : TX5Locations) {
                String[] pos = loc.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                TX5 tx5 = new TX5(game);
                HashableLocation.putLocationHash(monsters, location, tx5);
            }
        }
        if (properties.containsKey("Troll.location")) {
            String[] trollLocations = properties.getProperty("Troll.location").split(";");
            for (String loc : trollLocations) {
                String[] pos = loc.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                Troll troll = new Troll(game);
                HashableLocation.putLocationHash(monsters, location, troll);
            }
        }
        if (properties.containsKey("Orion.location")) {
            String[] orionLocations = properties.getProperty("Orion.location").split(";");
            for (String loc : orionLocations) {
                String[] pos = loc.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                Orion orion = new Orion(game);
                HashableLocation.putLocationHash(monsters, location, orion);
            }
        }
        if (properties.containsKey("Alien.location")) {
            String[] alienLocations = properties.getProperty("Alien.location").split(";");
            for (String loc : alienLocations) {
                String[] pos = loc.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                Alien alien = new Alien(game);
                HashableLocation.putLocationHash(monsters, location, alien);
            }
        }
        if (properties.containsKey("Wizard.location")) {
            String[] wizardLocations = properties.getProperty("Wizard.location").split(";");
            for (String loc : wizardLocations) {
                String[] pos = loc.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                Wizard wizard = new Wizard(game);
                HashableLocation.putLocationHash(monsters, location, wizard);
            }
        }

        /// TEMPORARY SET SEED AND SLOW DOWN
        for (Monster monster : monsters.values()) {
            monster.setSeed(seed);
            monster.setSlowDown(3);
            if (monster instanceof TX5)
                monster.stopMoving(5);
        }
    }

    /**
     * Set all monsters to stop moving.
     */
    protected void setMonstersStopMoving() {
        for (Monster monster : getMonsters().values())
            monster.setStopMoving(true);
    }
}
