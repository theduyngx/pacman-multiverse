package src;

import ch.aplu.jgamegrid.Location;
import java.util.Arrays;
import java.util.HashMap;

public record HashableLocation(Location location) {

    public int getX() {
        return location.getX();
    }

    public int getY() {
        return location.getY();
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getArrayLocation(location));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass())
            return false;
        HashableLocation other = (HashableLocation) obj;
        return other.getX() == this.getX() && other.getY() == this.getY();
    }

    // get hashable location
    public static int[] getArrayLocation(Location location) {
        return new int[]{location.getX(), location.getY()};
    }

    // put entry to hashmap with HashableLocation as key
    public static <T> void putLocationHash(HashMap<HashableLocation, T> map, Location location, T object) {
        HashableLocation hashLocation = new HashableLocation(location);
        map.put(hashLocation, object);
    }
}
