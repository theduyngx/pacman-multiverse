package src;
import src.utility.PropertiesLoader;
import java.util.Properties;

public class Driver {
    public static final String DEFAULT_PROPERTIES_PATH = "pacman/properties/test1.properties";

    /**
     * Entry point to program.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String propertiesPath = DEFAULT_PROPERTIES_PATH;
        if (args.length > 0) {
            propertiesPath = args[0];
        }
        final Properties properties = PropertiesLoader.loadPropertiesFile(propertiesPath);
        Game game = new Game(properties);
        game.run();
    }
}
