package main;


import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import utils.Globals;

/**
 * Created by Rick on 10/29/2015.
 */
public class Main extends StateBasedGame {

    public static final int WIDTH = Globals.WINDOW_WIDTH;
    public static final int HEIGHT = Globals.WINDOW_HEIGHT;

    public static final int menu = 0;
    public static final int overWorld = 1;
    public static final int battle = 2;

    public static void main(String[] args) throws SlickException {
        AppGameContainer app = new AppGameContainer(new Main("Project Marina"));

        app.setDisplayMode(WIDTH, HEIGHT, false);
        app.start();
    }

    /**
     * Create a new state based game
     *
     * @param name The name of the game
     */
    public Main(String name) {
        super(name);
    }

    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        addState(new MainMenu());
        addState(new Overworld());
        addState(new Battle());

        enterState(overWorld);
    }
}
