package gameObjects;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;
import utils.Globals;

/**
 * Created by Rick on 10/29/2015.
 */
public class Map {

    public SpriteSheet mapTiles;

    public static int WIDTH = Globals.MAP_WIDTH;
    public static int HEIGHT = Globals.MAP_HEIGHT;
    public static int TILE_WIDTH = Globals.TILE_WIDTH;
    public static int TILE_HEIGHT = Globals.TILE_HEIGHT;
    public static Tile[][] grid;

    public Map() throws SlickException {
        grid = new Tile[WIDTH][HEIGHT];

        for (int i = 0; i < WIDTH; i++) {
            grid[i][0] = new Tile();
            grid[i][0].setX(TILE_WIDTH * i);
            grid[i][0].setY(TILE_HEIGHT * (HEIGHT - 1));
        }
        /*
        for (int i = 12; i < 18; i++) {
            grid[i][15] = new Tile();
            grid[i][15].setX(TILE_WIDTH * i);
            grid[i][15].setY(TILE_HEIGHT * (HEIGHT - 15));
        }

        for (int i = 1; i < 15; i++) {
            grid[12][i] = new Tile();
            grid[12][i].setX(TILE_WIDTH * 12);
            grid[12][i].setY(TILE_HEIGHT * (HEIGHT - i));
            grid[17][i] = new Tile();
            grid[17][i].setX(TILE_WIDTH * 17);
            grid[17][i].setY(TILE_HEIGHT * (HEIGHT - i));
        }
        */
    }

    public Tile[][] getGrid() {
        return grid;
    }

    public void draw(Graphics g) {
        for (Tile[] row : grid) {
            for (Tile t : row) {
                if (t != null) {
                    t.draw();
                }
            }
        }
    }

    public void update() {

    }

}
