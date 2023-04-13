package src;

public class Alien extends Monster
{
    public static final int numAlienImages = 1;
    public static final String directory = "sprites/m_alien.gif";

    public Alien(Game game)
    {
        super(game, false, directory, numAlienImages);
    }

    @Override
    public void walkApproach()
    {

    }
}
