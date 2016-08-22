package main;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import java.io.File;

/**
 * main.Launcher will create and start the game.
 *
 * @author Scorple
 * @version dev01
 * @since 2016_0801
 */
class Launcher
{
    private static final int WIDTH = Globals.DEFAUT_WINDOW_WIDTH;
    private static final int HEIGHT = Globals.DEFAULT_WINDOW_HEIGHT;

    /**
     * Required standard method to make executable.
     *
     * @param args String[]: Not used.
     *
     * @throws SlickException
     */
    public static void main(String[] args) throws SlickException
    {
        File devLibDir = new File("lib/slick");

        if (devLibDir.exists())
        {
            System.setProperty("org.lwjgl.librarypath", devLibDir.getAbsolutePath());
        }

        AppGameContainer app = new AppGameContainer(new Game("Project Marina"));

        app.setDisplayMode(WIDTH, HEIGHT, false);
        app.start();
    }
}
