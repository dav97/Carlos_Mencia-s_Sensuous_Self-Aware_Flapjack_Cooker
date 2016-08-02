import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;
import utils.Globals;

/**
 * Launcher will create and start the game.
 *
 * @author Scorple
 * @version 1.0
 * @since 2016.08.01
 */
class Launcher {
    public static final int WIDTH = Globals.WINDOW_WIDTH;
    public static final int HEIGHT = Globals.WINDOW_HEIGHT;

    /**
     * Required standard method to make executable.
     *
     * @param args String[]: Not used.
     *
     * @throws SlickException
     */
    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new Game("Project Marina"));

        app.setDisplayMode(WIDTH, HEIGHT, false);
        app.start();
    }
}
