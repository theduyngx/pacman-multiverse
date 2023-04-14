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
    private final HashMap<HashableLocation, Monster> monsters;
    // hashmap of all items with their location as key
    private final HashMap<HashableLocation, Item> items;
    // hashmap of all walls with their location as key
    private final HashMap<HashableLocation, Integer> walls;

    // the game
    private final Game game;
    // game callback
    private final GameCallback gameCallback;
    // random seed
    private int seed = INIT_SEED;
    // current number of pills and gold pieces, which indicate whether player has won or not
    private int numPillsAndGold = 0;

    /**
     * Constructor for ObjectManager.
     */
    public ObjectManager(Game game) {
        assert game != null;
        this.game = game;
        this.gameCallback = new GameCallback();
        this.monsters = new HashMap<>();
        this.items = new HashMap<>();
        this.walls = new HashMap<>();
    }

    public Game getGame() {
        return game;
    }

    public GameCallback getGameCallback() {
        return gameCallback;
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
     */
    public HashMap<HashableLocation, Monster> getMonsters() {
        return monsters;
    }

    /**
     * Get all items currently still in the game.
     * @return a hashmap where the key is the items' locations, and value being the items
     */
    public HashMap<HashableLocation, Item> getItems() {
        return items;
    }

    /**
     * Get all walls.
     * @return a hashmap where the key is the walls' locations, and value being the walls
     */
    public HashMap<HashableLocation, Integer> getWalls() {
        return walls;
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
     * @param properties properties to parse for monsters
     */
    public void instantiateMonsters(Properties properties) {
        if (properties.containsKey("TX5.location")) {
            String[] TX5Locations = properties.getProperty("TX5.location").split(";");
            for (String loc : TX5Locations) {
                String[] pos = loc.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                TX5 tx5 = new TX5(this);
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
                Troll troll = new Troll(this);
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
                Orion orion = new Orion(this);
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
                Alien alien = new Alien(this);
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
                Wizard wizard = new Wizard(this);
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
     * Set all monsters to stop moving; used when game is over (win/lose condition is met).
     */
    protected void setMonstersStopMoving() {
        for (Monster monster : getMonsters().values())
            monster.setStopMoving(true);
    }
}
