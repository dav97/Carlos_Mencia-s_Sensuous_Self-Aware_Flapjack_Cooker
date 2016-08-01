package gameObjects;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import utils.Globals;
import utils.Vector2i;

/**
 * Created by Rick on 10/29/2015.
 */
public class Tile {

    private int WIDTH = Globals.TILE_WIDTH;
    private int HEIGHT = Globals.TILE_HEIGHT;

    private Image tile;

    private Vector2i position;

    private BoundingBox boundingBox;

    public Tile() throws SlickException {
        tile = new Image("res/Map Elements/Map Tiles/tile.png", false, Image.FILTER_NEAREST);

        position = new Vector2i();

        boundingBox = new BoundingBox(position, WIDTH, HEIGHT);
    }

    public Tile(int x, int y) {
        position = new Vector2i(x, y);
    }

    public Tile(Vector2i pos) {
        position = new Vector2i(pos);
    }

    public Vector2i getPosition() {
        return position;
    }

    public void setPosition(int x, int y) {
        position.x = x;
        position.y = y;
        boundingBox.setPosition(position);
    }

    public void setPosition(Vector2i pos) {
        position = new Vector2i(pos);
        boundingBox.setPosition(position);
    }

    public void setX(int x) {
        position.x = x;
        boundingBox.setPosition(position);
    }

    public void setY(int y) {
        position.y = y;
        boundingBox.setPosition(position);
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public void draw() {
        tile.draw(position.getX(), position.getY(), Globals.TILE_WIDTH, Globals.TILE_HEIGHT);
    }

}
