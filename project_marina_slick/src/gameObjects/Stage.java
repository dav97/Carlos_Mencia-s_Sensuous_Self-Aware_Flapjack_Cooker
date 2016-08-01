package gameObjects;

import com.sun.org.apache.bcel.internal.generic.GOTO;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import utils.Globals;
import utils.Vector2i;

/**
 * Created by Rick on 10/29/2015.
 */
public class Stage {

    private static Image background;
    private static TiledMap tiledMap;
    private static Map map;
    private static Player player;

    private static int BACKGROUND_HEIGHT, BACKGROUND_WIDTH, CEILING_GAP;
    private static Vector2i backgroundPos;

    public Stage() throws SlickException {
        //background = new Image("res/debug/debug.png", false, Image.FILTER_NEAREST);
        tiledMap = new TiledMap("res/debug/debug.tmx");

        BACKGROUND_HEIGHT = tiledMap.getHeight() * 2;
        //background.getHeight() * 2;
        BACKGROUND_WIDTH = tiledMap.getWidth() * 2;
        // background.getWidth() * 2;

        map = new Map();
        player = new Player(3, 3);

        CEILING_GAP = Globals.MAP_HEIGHT - BACKGROUND_HEIGHT;

        //System.out.println("Background Width: " + BACKGROUND_WIDTH);
        //System.out.println("Background Height: " + BACKGROUND_HEIGHT);
        //System.out.println("Ceiling Gap: " + CEILING_GAP);

        backgroundPos = new Vector2i();
        backgroundPos.setY(CEILING_GAP);
    }

    public void draw(Graphics g) {
        tiledMap.render(0, 0);//7 * Globals.TILE_HEIGHT);
        //background.draw(backgroundPos.getX(), backgroundPos.getY(), BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        //map.draw(g);
        player.draw(g);
    }

    public void update(Input input) {
        map.update();
        //player.update(input, map);
        player.update(input, tiledMap);
    }

}
