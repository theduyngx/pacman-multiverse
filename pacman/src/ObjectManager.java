package src;

import ch.aplu.jgamegrid.Location;

import java.util.ArrayList;
import java.util.Properties;

public class ObjectManager {
    private final ArrayList<Monster> monsters;
    private final PacActor pacActor;
    private final ArrayList<Pill> pills;
    private final ArrayList<Gold> golds;
    private final ArrayList<Ice> ices;

    public PositionManager(PacActor pacActor) {
        this.monsters = new ArrayList<>();
        this.pacActor = pacActor;
        this.pills = new ArrayList<>();
        this.golds = new ArrayList<>();
        this.ices = new ArrayList<>();
    }

    public void parseProperties(Properties properties) {
        pacActor.setPropertyMoves(properties.getProperty("PacMan.move"));
        pacActor.setAuto(Boolean.parseBoolean(properties.getProperty("PacMan.isAuto")));
        String[] pillLocations = properties.getProperty("Pills.location").split(";");

        for (String pL : pillLocations) {
            String[] pos = pL.split(",");
            int posX = Integer.parseInt(pos[0]);
            int posY = Integer.parseInt(pos[1]);

            Pill pill = Pill(new Location(posX, posY));
            pillLocations.add(pill);
        }
        for (String pL : pillLocations) {
            String[] pos = pL.split(",");
            int posX = Integer.parseInt(pos[0]);
            int posY = Integer.parseInt(pos[1]);

            Pill pill = Pill(new Location(posX, posY));
        }
    }
}
