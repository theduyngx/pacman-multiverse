package src;
import src.utility.GameCallback;
import ch.aplu.jgamegrid.Location;
import java.util.*;


/**
 * ObjectManager class to manage all objects, animate or inanimate, especially their instantiations and
 * locations. Anything that has to do with checking every actor for a specific task uniformly will be a
 * responsibility of ObjectManager, since it has access to all Actors, as well as other grid-related
 * objects.
 * As such, ObjectManager can be frequently used to deal with a specific Actor checking the 'state' of
 * every other actor.
 */
public class ObjectManager {
    // constant initial seed
    private final static int INIT_SEED = 30006;

    // PacMan
    private PacActor pacActor;
    // hashmap of monsters with their initial location as key
    // private final HashMap<HashableLocation, Monster> monsters;
    private final ArrayList<Monster> MONSTERS;
    // Stores the locations of monsters for initialization
    private final ArrayList<Location> MONSTERS_LOCATIONS;
    // hashmap of all items with their location as key
    private final HashMap<HashableLocation, Item> ITEMS;
    // hashmap of all walls with their location as key
    private final HashMap<HashableLocation, Integer> WALLS;

    // the game
    private final Game GAME;
    // game callback
    private final GameCallback GAME_CALLBACK;
    // random seed
    private int seed = INIT_SEED;
    // current number of pills and gold pieces, which indicate whether player has won or not
    private int numPillsAndGold = 0;

    /**
     * Constructor for ObjectManager.
     */
    public ObjectManager(Game game) {
        assert game != null;
        this.GAME = game;
        this.GAME_CALLBACK = new GameCallback();
        // this.monsters = new HashMap<>();
        this.MONSTERS = new ArrayList<>();
        // Need this only for monsters initialization
        this.MONSTERS_LOCATIONS = new ArrayList<>();
        this.ITEMS = new HashMap<>();
        this.WALLS = new HashMap<>();
    }

    public Game getGame() {
        return GAME;
    }

    public GameCallback getGameCallback() {
        return GAME_CALLBACK;
    }

    /**
     * Get the player PacMan.
     * @return player PacMan
     */
    public PacActor getPacActor() {
        return pacActor;
    }

    /**
     * Get all monsters.
     * @return a hashmap where the key is monsters' initial location, and value being the monsters
     * @return a list of all the monsters in the game
     */
    public ArrayList<Monster> getMonsters() {
    // public HashMap<HashableLocation, Monster> getMonsters() {
        return MONSTERS;
    }

    /**
     * Get all monster locations (used only in initialization)
     * @return a list of the locations of all the monsters,
     *         in the same order monsters were entered
     */
    public ArrayList<Location> getMonsterLocations() {
        return MONSTERS_LOCATIONS;
    }

    /**
     * Get all items currently still in the game.
     * @return a hashmap where the key is the items' locations, and value being the items
     */
    public HashMap<HashableLocation, Item> getItems() {
        return ITEMS;
    }

    /**
     * Get all walls.
     * @return a hashmap where the key is the walls' locations, and value being the walls
     */
    public HashMap<HashableLocation, Integer> getWalls() {
        return WALLS;
    }

    /**
     * Get the number of pills and gold pieces left in the game. Hence, used to detect winning condition.
     * @return the number of pills and gold pieces left in the game
     */
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

    /**
     * Parse properties that do not require an Actor instantiation.
     * @param properties the specified properties
     */
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
                if (! HashableLocation.containLocationHash(ITEMS, location)) {
                    Pill pill = new Pill();
                    HashableLocation.putLocationHash(ITEMS, location, pill);
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
                if (! HashableLocation.containLocationHash(ITEMS, location)) {
                    Gold gold = new Gold();
                    HashableLocation.putLocationHash(ITEMS, location, gold);
                    numPillsAndGold++;
                }
            }
        }
    }

    public void instantiatePacActor() {
        this.pacActor = new PacActor(this);
        pacActor.setSeed(seed);
        pacActor.setSlowDown(3);
    }


    /**
     * Instantiate the items in the grid and put them in their respective hashmaps.
     * @param grid the game grid so that the items can be drawn onto
     */
    public void instantiateObjects(PacManGameGrid grid) {
        for (int col = 0; col < grid.getNumVerticalCells(); col++)
            for (int row = 0; row < grid.getNumHorizontalCells(); row++) {
                PacManGameGrid.BlockType itemType = grid.getMazeArray()[col][row];

                // ignore if location is already occupied
                Location location = new Location(row, col);
                if (HashableLocation.containLocationHash(ITEMS, location)) continue;

                // otherwise add
                switch (itemType) {
                    case PILL -> {
                        Pill pill = new Pill();
                        HashableLocation.putLocationHash(ITEMS, location, pill);
                        numPillsAndGold++;
                    }
                    case GOLD -> {
                        Gold gold = new Gold();
                        HashableLocation.putLocationHash(ITEMS, location, gold);
                        numPillsAndGold++;
                    }
                    case ICE -> {
                        Ice ice = new Ice();
                        HashableLocation.putLocationHash(ITEMS, location, ice);
                    }
                }
            }
    }

    /**
     * (WIP) Instantiating monsters, for now this is clunky and needs plenty of work done
     * @param properties properties to parse for monsters
     */
    public void instantiateMonsters(Properties properties) {
        if (properties.containsKey("TX5.location") && !properties.getProperty("TX5.location").equals("")) {
            String[] TX5Locations = properties.getProperty("TX5.location").split(";");
            for (String loc : TX5Locations) {
                String[] pos = loc.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                TX5 tx5 = new TX5(this);
                this.MONSTERS_LOCATIONS.add(location);
                // HashableLocation.putLocationHash(monsters, location, tx5);
                this.MONSTERS.add(tx5);
            }
        }
        if (properties.containsKey("Troll.location") && !properties.getProperty("Troll.location").equals("")) {
            String[] trollLocations = properties.getProperty("Troll.location").split(";");
            for (String loc : trollLocations) {
                String[] pos = loc.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                Troll troll = new Troll(this);
                this.MONSTERS_LOCATIONS.add(location);
                // HashableLocation.putLocationHash(monsters, location, troll);
                this.MONSTERS.add(troll);
            }
        }
        if (properties.containsKey("Orion.location") && !properties.getProperty("Orion.location").equals("")) {
            String[] orionLocations = properties.getProperty("Orion.location").split(";");
            for (String loc : orionLocations) {
                String[] pos = loc.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                Orion orion = new Orion(this);
                this.MONSTERS_LOCATIONS.add(location);
                // HashableLocation.putLocationHash(monsters, location, orion);
                this.MONSTERS.add(orion);
            }
        }
        if (properties.containsKey("Alien.location") && !properties.getProperty("Alien.location").equals("")) {
            String[] alienLocations = properties.getProperty("Alien.location").split(";");
            for (String loc : alienLocations) {
                String[] pos = loc.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                Alien alien = new Alien(this);
                this.MONSTERS_LOCATIONS.add(location);
                // HashableLocation.putLocationHash(monsters, location, alien);
                this.MONSTERS.add(alien);
            }
        }
        if (properties.containsKey("Wizard.location") && !properties.getProperty("Wizard.location").equals("")) {
            String[] wizardLocations = properties.getProperty("Wizard.location").split(";");
            for (String loc : wizardLocations) {
                String[] pos = loc.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                Wizard wizard = new Wizard(this);
                this.MONSTERS_LOCATIONS.add(location);
                // HashableLocation.putLocationHash(monsters, location, wizard);
                this.MONSTERS.add(wizard);
            }
        }

        /// TEMPORARY SET SEED AND SLOW DOWN
        // for (Monster monster : monsters.values()) {
        for (Monster monster : MONSTERS) {
            monster.setSeed(seed);
            monster.setSlowDown(3);
            if (monster instanceof TX5)
                monster.stopMoving(5);
        }
    }

    /**
     * Set all monsters to stop moving; used when game is over (win/lose condition is met).
     */
    protected void setMonstersStopMoving() {
        // for (Monster monster : getMonsters().values())
        for (Monster monster: MONSTERS) {
            monster.setStopMoving(true);
        }
    }
}
