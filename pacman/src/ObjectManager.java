package src;

import ch.aplu.jgamegrid.Location;
import java.util.HashMap;
import java.util.Properties;

public class ObjectManager {
    private final PacActor pacActor;
    private final HashMap<Location, Monster> monsters;
    private final HashMap<Location, Pill> pills;
    private final HashMap<Location, Gold> golds;
    private final HashMap<Location, Ice> ices;
    private int seed = 30006;

    // constructor
    public ObjectManager(PacActor pacActor) {
        if (pacActor == null) {
            System.exit(1);
        }
        this.pacActor = pacActor;
        this.monsters = new HashMap<>();
        this.pills    = new HashMap<>();
        this.golds    = new HashMap<>();
        this.ices     = new HashMap<>();
    }

    // getters
    public PacActor getPacActor() {
        return pacActor;
    }
    public HashMap<Location, Monster> getMonsters() {
        return monsters;
    }
    public HashMap<Location, Pill> getPills() {
        return pills;
    }
    public HashMap<Location, Gold> getGolds() {
        return golds;
    }
    public HashMap<Location, Ice> getIces() {
        return ices;
    }

    // parsing properties
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
                Pill pill = new Pill();
                pills.put(location, pill);
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
                Gold gold = new Gold();
                golds.put(location, gold);
            }
        }
    }


    // instantiate the items in the grid and put them in their respective hashmaps
    public void instantiateObjects(PacManGameGrid grid) {

        for (int col = 0; col < grid.getNumVerticalCells(); col++)
            for (int row = 0; row < grid.getNumHorizontalCells(); row++) {
                PacManGameGrid.BlockType itemType = grid.getMazeArray()[col][row];
                Location location = new Location(row, col);
                if (itemType == PacManGameGrid.BlockType.PILL) {
                    Pill pill = new Pill();
                    pills.put(location, pill);
                }
                else if (itemType == PacManGameGrid.BlockType.GOLD) {
                    Gold gold = new Gold();
                    golds.put(location, gold);
                }
                else if (itemType == PacManGameGrid.BlockType.ICE) {
                    Ice ice = new Ice();
                    ices.put(location, ice);
                }
            }
    }
}
