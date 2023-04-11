package src;

import ch.aplu.jgamegrid.Location;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class ObjectManager {
    private final PacActor pacActor;
    private final HashMap<Location, Monster> monsters;
    private final HashMap<Location, Pill> pills;
    private final HashMap<Location, Gold> golds;
    private final HashMap<Location, Ice> ices;

    // constructor
    public ObjectManager(PacActor pacActor) {
        if (pacActor == null) {
            System.exit(1);
        }
        this.monsters = new HashMap<>();
        this.pacActor = pacActor;
        this.pills = new HashMap<>();
        this.golds = new HashMap<>();
        this.ices = new HashMap<>();
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

    // other methods
    public void parseProperties(Properties properties) {
        pacActor.setPropertyMoves(properties.getProperty("PacMan.move"));
        pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));

        String[] pillLocations = properties.getProperty("Pills.location").split(";");
        for (String pL : pillLocations) {
            String[] pos = pL.split(",");
            int posX = Integer.parseInt(pos[0]);
            int posY = Integer.parseInt(pos[1]);
            Location location = new Location(posX, posY);
            Pill pill = new Pill(location);
            pills.put(location, pill);
        }

        String[] goldLocations = properties.getProperty("Gold.location").split(";");
        for (String gL : goldLocations) {
            String[] pos = gL.split(",");
            int posX = Integer.parseInt(pos[0]);
            int posY = Integer.parseInt(pos[1]);
            Location location = new Location(posX, posY);
            Gold gold = new Gold(location);
            golds.put(location, gold);
        }

        String[] iceLocations = properties.getProperty("Gold.location").split(";");
        for (String iL : iceLocations) {
            String[] pos = iL.split(",");
            int posX = Integer.parseInt(pos[0]);
            int posY = Integer.parseInt(pos[1]);
            Location location = new Location(posX, posY);
            Ice ice = new Ice(location);
            ices.put(location, ice);
        }
    }
}
