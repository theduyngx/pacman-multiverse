package src;
import src.utility.GameCallback;
import ch.aplu.jgamegrid.Location;
import java.util.*;


/**
 * ObjectManager class to manage all objects, animate or inanimate (viz. item or live actor), especially
 * their instantiations and locations. Anything that has to do with checking every actor for a specific
 * task uniformly will be a responsibility of ObjectManager, since it has access to all Actors, as well as
 * other grid-related objects.
 * As such, ObjectManager can be frequently used to deal with a specific Actor checking the 'state' of
 * every other actor.
 * @see Game
 * @see Item
 * @see LiveActor
 */
public class ObjectManager {
    // constant initial seed
    private final static int INIT_SEED = 30006;

    // PacMan
    private PacActor pacActor;
    // hashmap of monsters with their initial location as key
    // private final HashMap<HashableLocation, Monster> monsters;
    private final ArrayList<Monster> MONSTERS;
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
    private boolean isMultiverse = false;

    /**
     * Constructor for ObjectManager.
     * @see Game
     */
    public ObjectManager(Game game) {
        assert game != null;
        this.GAME = game;
        this.GAME_CALLBACK = new GameCallback();
        // this.monsters = new HashMap<>();
        this.MONSTERS = new ArrayList<>();
        this.ITEMS = new HashMap<>();
        this.WALLS = new HashMap<>();
    }

    /**
     * Get the game object; used to retrieve game's grid, which either to update grid's cell or to
     * get the information about the grid's border to disallow actors moving out of bound.
     * @return the game
     */
    protected Game getGame() {
        return GAME;
    }

    /**
     * Get the game callback; used by live actors to update their activities to log.
     * @return the game callback
     * @see    GameCallback
     */
    protected GameCallback getGameCallback() {
        return GAME_CALLBACK;
    }

    /**
     * Get the player PacMan. This is primarily used for checking collisions between PacMan and monsters.
     * @return player PacMan
     * @see    PacActor
     */
    protected PacActor getPacActor() {
        return pacActor;
    }

    /**
     * Get all monsters.
     * @return a list of all the monsters in the game
     * @see    Monster
     */
    protected ArrayList<Monster> getMonsters() {
        return MONSTERS;
    }

    /**
     * Get all items currently still in the game.
     * @return a hashmap where the key is the items' locations, and value being the items
     * @see    HashableLocation
     * @see    Item
     */
    protected HashMap<HashableLocation, Item> getItems() {
        return ITEMS;
    }

    /**
     * Get all walls.
     * @return a hashmap where the key is the walls' locations, and value being the walls
     * @see    HashableLocation
     */
    protected HashMap<HashableLocation, Integer> getWalls() {
        return WALLS;
    }

    /**
     * Get the number of pills and gold pieces left in the game. Hence, used to detect winning condition.
     * @return the number of pills and gold pieces left in the game
     */
    protected int getNumPillsAndGold() {
        return numPillsAndGold;
    }

    /**
     * Get the version of the game
     * @return a boolean representing if the game is simple or a multiverse
     */
    protected boolean isMultiverse() {
        return isMultiverse;
    }

    /**
     * Decrementing the number of pill and gold pieces when PacMan eats one of the pieces.
     * Hence, used in eatItem of PacActor. It will also check if a specified item is of instance
     * Gold or Pill, and if not then it will not decrement.
     * @param item specified eaten item
     * @see        Item
     */
    protected void decrementNumPillAndGold(Item item) {
        if (item instanceof Gold || item instanceof Pill)
            numPillsAndGold--;
    }

    /**
     * Parse properties that do not require an Actor instantiation.
     * @param properties the specified properties
     * @see   Properties
     */
    public void parseProperties(Properties properties) {
        seed = Integer.parseInt(properties.getProperty("seed"));
        isMultiverse = properties.getProperty("version").contains("multiverse");

        // parse pacman
        pacActor.setPropertyMoves(properties.getProperty("PacMan.move"));
        pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
        String[] pacManLocations = properties.getProperty("PacMan.location").split(",");
        int pacManX = Integer.parseInt(pacManLocations[0]);
        int pacManY = Integer.parseInt(pacManLocations[1]);
        pacActor.setInitLocation(new Location(pacManX, pacManY));

        // parse the pill locations if there is pill location
        if (properties.containsKey("Pill.location")) {
            String[] pillLocations = properties.getProperty("Pills.location").split(";");
            for (String pL : pillLocations) {
                String[] pos = pL.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                Pill pill = new Pill();
                HashableLocation.putLocationHash(ITEMS, location, pill);
                getGame().getGrid().setCell(location, PacManGameGrid.BlockType.PILL);
                numPillsAndGold++;
            }
        }

        // parse the gold locations if there is gold location
        if (properties.containsKey("Gold.location")) {
            String[] goldLocations = properties.getProperty("Gold.location").split(";");
            for (String gL : goldLocations) {
                String[] pos = gL.split(",");
                int posX = Integer.parseInt(pos[0]);
                int posY = Integer.parseInt(pos[1]);
                Location location = new Location(posX, posY);
                Gold gold = new Gold();
                HashableLocation.putLocationHash(ITEMS, location, gold);
                getGame().getGrid().setCell(location, PacManGameGrid.BlockType.GOLD);
                numPillsAndGold++;
            }
        }
    }

    /**
     * Instantiate the pacman actor. Called in Game constructor.
     * @see PacActor
     */
    protected void instantiatePacActor() {
        this.pacActor = new PacActor(this);
        pacActor.setSeed(seed);
        pacActor.setSlowDown(LiveActor.SLOW_DOWN);
    }


    /**
     * Instantiate the items in the grid and put them in their respective hashmaps. Called in Game constructor.
     * @param grid the game grid so that the items can be drawn onto
     * @see   PacManGameGrid
     */
    protected void instantiateObjects(PacManGameGrid grid) {
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
     * Instantiating monsters. Called in Game constructor.
     * @param properties properties to parse for monsters
     * @see   Properties
     */
    protected void instantiateMonsters(Properties properties) {
        // for each monster type
        ArrayList<Monster.MonsterType> types = new ArrayList<>(Arrays.asList(Monster.MonsterType.values()));
        for (Monster.MonsterType type : types) {
            // check if monster type is valid (as in, if type only exists in multiverse but property
            // states otherwise, then we ignore)
            if (type.inMultiverse && !isMultiverse) continue;
            String name = type.toString();
            String property_name = name + ".location";

            // valid entry
            if (properties.containsKey(property_name) && !properties.getProperty(property_name).equals("")) {
                String[] locations = properties.getProperty(property_name).split(";");

                // get all locations of monster
                for (String loc : locations) {
                    String[] pos = loc.split(",");
                    int posX = Integer.parseInt(pos[0]);
                    int posY = Integer.parseInt(pos[1]);
                    Location location = new Location(posX, posY);
                    Monster monster = switch(type) {
                        case TX5    -> new TX5(this);
                        case Troll  -> new Troll(this);
                        case Orion  -> new Orion(this);
                        case Alien  -> new Alien(this);
                        case Wizard -> new Wizard(this);
                    };

                    // set location and add itself to monster list
                    monster.setInitLocation(location);
                    this.MONSTERS.add(monster);

                    /// SET SEED AND SLOW DOWN TO REDUCE GAME DIFFICULTY
                    monster.setSeed(seed);
                    monster.setSlowDown(LiveActor.SLOW_DOWN);
                    if (type == Monster.MonsterType.TX5)
                        monster.stopMoving(5);
                }
            }
        }
    }

    /**
     * Set all monsters to stop moving; used when game is over (win/lose condition is met).
     * @see Monster
     */
    protected void setMonstersStopMoving() {
        // for (Monster monster : getMonsters().values())
        for (Monster monster: MONSTERS)
            monster.setStopMoving(true);
    }
}
