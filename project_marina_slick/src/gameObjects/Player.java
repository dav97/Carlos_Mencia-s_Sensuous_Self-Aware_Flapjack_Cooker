package gameObjects;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.tiled.TiledMap;
import utils.Globals;
import utils.Vector2f;
import utils.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by Rick on 10/29/2015.
 */
public class Player {

    private int WIDTH = Globals.ACTOR_WIDTH;
    private int HEIGHT = Globals.ACTOR_HEIGHT;

    private int SCALE = Globals.SCALE;

    private Image player;

    private Vector2f position;
    private Vector2f movement;

    private BoundingBox boundingBox;

    private boolean floor;
    private boolean ceiling;
    private boolean leftWall;
    private boolean rightWall;

    public Player() throws SlickException {
        player = new Image("res/Overworld Characters/PCs/Marina/mf.png", false, Image.FILTER_NEAREST);

        position = new Vector2f();
        movement = new Vector2f();

        boundingBox = new BoundingBox(position, WIDTH, HEIGHT);

        floor = false;
        ceiling = false;
        leftWall = false;
        rightWall = false;
    }

    public Player(int x, int y) throws SlickException {
        player = new Image("res/Overworld Characters/PCs/Marina/mf.png", false, Image.FILTER_NEAREST);

        position = new Vector2f(x * Globals.TILE_WIDTH, y * Globals.TILE_HEIGHT);
        movement = new Vector2f();

        boundingBox = new BoundingBox(position, WIDTH, HEIGHT);

        floor = false;
        ceiling = false;
        leftWall = false;
        rightWall = false;
    }

    public void draw(Graphics g) {
        player.draw(position.getX(), position.getY(), WIDTH, HEIGHT);
        //g.drawImage(player, position.getX(), position.getY());
        g.scale(1f / SCALE, 1f / SCALE);
        g.drawString("floor: " + floor, 10, 70);
        g.drawString("ceiling: " + ceiling, 10, 90);
        g.drawString("leftWall: " + leftWall, 10, 110);
        g.drawString("rightWall: " + rightWall, 10, 130);
        g.scale(SCALE, SCALE);
    }

    public void update(Input input, Map map) {
        checkCollisions(map);

        position.translate(movement);
        boundingBox.setPosition(position);

        if (input.isKeyDown(Input.KEY_W)) {
            if (!ceiling && floor) {
                movement.setY(-50);
            }
            else {
                movement.setY(0);
            }
        }
        else if (input.isKeyDown(Input.KEY_S)) {
            if (!floor) {
                movement.setY(1);
            }
            else {
                movement.setY(0);
            }
        }
        else {
            movement.setY(0);
        }
        if (input.isKeyDown(Input.KEY_A)) {
            if (!leftWall) {
                movement.setX(-1);
            }
            else {
                movement.setX(0);
            }
        }
        else if (input.isKeyDown(Input.KEY_D)) {
            if (!rightWall) {
                movement.setX(1);
            }
            else {
                movement.setX(0);
            }
        }
        else {
            movement.setX(0);
        }
        if (!floor && movement.getY() < 1) {
            movement.setY(movement.getY() + 1);
        }
    }

    public void update(Input input, TiledMap tiledMap) {
        checkCollisions(tiledMap);

        position.translate(movement);
        boundingBox.setPosition(position);

        if (input.isKeyDown(Input.KEY_W)) {
            if (!ceiling && floor) {
                movement.setY(-1);
            }
            else {
                movement.setY(0);
            }
        }
        else if (input.isKeyDown(Input.KEY_S)) {
            if (!floor) {
                movement.setY(1);
            }
            else {
                movement.setY(0);
            }
        }
        else {
            movement.setY(0);
        }
        if (input.isKeyDown(Input.KEY_A)) {
            if (!leftWall) {
                movement.setX(-1);
            }
            else {
                movement.setX(0);
            }
        }
        else if (input.isKeyDown(Input.KEY_D)) {
            if (!rightWall) {
                movement.setX(1);
            }
            else {
                movement.setX(0);
            }
        }
        else {
            movement.setX(0);
        }
        if (!floor && movement.getY() < 1) {
            movement.setY(movement.getY() + 1);
        }
    }

    public void checkCollisions(Map map) {
        floor = false;
        ceiling = false;
        leftWall = false;
        rightWall = false;

        for (Tile[] row : map.getGrid()) {
            for (Tile t : row) {
                if (t != null) {
                    //if (boundingBox.below(32).intersects(t.getBoundingBox())) {
                        //if (boundingBox.distanceBelow(t.getBoundingBox()) > movement.y) {
                            //movement.y = boundingBox.distanceBelow(t.getBoundingBox());
                        //}
                        if (boundingBox.below(2).intersects(t.getBoundingBox())) {
                            floor = true;
                        }
                    //}
                    //if (boundingBox.above(32).intersects(t.getBoundingBox())) {
                        //if (boundingBox.distanceAbove(t.getBoundingBox()) < movement.y) {
                            //movement.y = boundingBox.distanceAbove(t.getBoundingBox());
                        //}
                        if (boundingBox.above(2).intersects(t.getBoundingBox())) {
                            ceiling = true;
                        }
                    //}
                    //if (boundingBox.left(32).intersects(t.getBoundingBox())) {
                        //if (boundingBox.distanceLeft(t.getBoundingBox()) > movement.x) {
                            //movement.x = boundingBox.distanceLeft(t.getBoundingBox());
                        //}
                        if (boundingBox.left(2).intersects(t.getBoundingBox())) {
                            leftWall = true;
                        }
                    //}
                    //if (boundingBox.right(32).intersects(t.getBoundingBox())) {
                        //if (boundingBox.distanceRight(t.getBoundingBox()) < movement.x) {
                            //movement.x = boundingBox.distanceRight(t.getBoundingBox());
                        //}
                        if (boundingBox.right(2).intersects(t.getBoundingBox())) {
                            rightWall = true;
                        }
                    //}
                }
            }
        }
    }

    private ArrayList<Integer> tilesLow = new ArrayList<>();
    private ArrayList<Integer> tilesHigh = new ArrayList<>();
    private ArrayList<Integer> tilesLeft = new ArrayList<>();
    private ArrayList<Integer> tilesRight = new ArrayList<>();

    public void checkCollisions(TiledMap tiledMap) {
        floor = false;
        ceiling = false;
        leftWall = false;
        rightWall = false;

        getNearbyTiles(tiledMap);

        for(int tile : tilesLow) {
            if (tiledMap.getTileProperty(tile, "clip", "1").equals("0")) {
                floor = true;
            }
        }
        for(int tile : tilesHigh) {
            if (tiledMap.getTileProperty(tile, "clip", "1").equals("0")) {
                ceiling = true;
            }
        }
        for(int tile: tilesLeft) {
            if (tiledMap.getTileProperty(tile, "clip", "1").equals("0")) {
                leftWall = true;
            }
        }
        for(int tile : tilesRight) {
            if (tiledMap.getTileProperty(tile, "clip", "1").equals("0")) {
                rightWall = true;
            }
        }
    }

    public void getNearbyTiles(TiledMap tiledMap) {
        Vector2f topLeft = position;
        Vector2f topRight = new Vector2f(position.getX() + Globals.ACTOR_WIDTH, position.getY());
        Vector2f botLeft = new Vector2f(position.getX(), position.getY() + Globals.ACTOR_HEIGHT);
        Vector2f botRight = new Vector2f(position.getX() + Globals.ACTOR_WIDTH, position.getY() + Globals.ACTOR_HEIGHT);
        Vector2f midLeft = new Vector2f(position.getX(), position.getY() + Globals.ACTOR_HEIGHT / 2);
        Vector2f midRight = new Vector2f(position.getX() + Globals.ACTOR_WIDTH, position.getY() + Globals.ACTOR_HEIGHT / 2);

        int buffer = 1;
        int layerIndex = tiledMap.getLayerIndex("Tile Layer 1");

        int idBelowOnLeft = tiledMap.getTileId((int)(botLeft.getX() / (float)Globals.TILE_WIDTH), (int)((botLeft.getY() + buffer) / (float)Globals.TILE_HEIGHT), layerIndex);
        int idBelowOnRight = tiledMap.getTileId((int)(botRight.getX() / (float)Globals.TILE_WIDTH), (int)((botRight.getY() + buffer) / (float)Globals.TILE_HEIGHT), layerIndex);
        int idAboveOnLeft = tiledMap.getTileId((int)(topLeft.getX() / (float)Globals.TILE_WIDTH), (int)((topLeft.getY() - buffer) / (float)Globals.TILE_HEIGHT), layerIndex);
        int idAboveOnRight = tiledMap.getTileId((int)(topRight.getX() / (float)Globals.TILE_WIDTH), (int)((topRight.getY() - buffer) / (float)Globals.TILE_HEIGHT), layerIndex);
        int idLeftOnTop = tiledMap.getTileId((int)((topLeft.getX() - buffer) / (float)Globals.TILE_WIDTH), (int)(topLeft.getY() / (float)Globals.TILE_HEIGHT), layerIndex);
        int idLeftOnMid = tiledMap.getTileId((int)((midLeft.getX() - buffer) / (float)Globals.TILE_WIDTH), (int)(midLeft.getY() / (float)Globals.TILE_HEIGHT), layerIndex);
        int idLeftOnBot = tiledMap.getTileId((int)((botLeft.getX() - buffer) / (float)Globals.TILE_WIDTH), (int)(botLeft.getY() / (float)Globals.TILE_HEIGHT), layerIndex);
        int idRightOnTop = tiledMap.getTileId((int)((topRight.getX() + buffer) / (float)Globals.TILE_WIDTH), (int)(topRight.getY() / (float)Globals.TILE_HEIGHT), layerIndex);
        int idRightOnMid = tiledMap.getTileId((int)((midRight.getX() + buffer) / (float)Globals.TILE_WIDTH), (int)(midRight.getY() / (float)Globals.TILE_HEIGHT), layerIndex);
        int idRightOnBot = tiledMap.getTileId((int)((botRight.getX() + buffer) / (float)Globals.TILE_WIDTH), (int)(botRight.getY() / (float)Globals.TILE_HEIGHT), layerIndex);

        tilesLow.add(idBelowOnLeft);
        tilesLow.add(idBelowOnRight);
        tilesHigh.add(idAboveOnLeft);
        tilesHigh.add(idAboveOnRight);
        tilesLeft.add(idLeftOnBot);
        tilesLeft.add(idLeftOnMid);
        tilesLeft.add(idLeftOnTop);
        tilesRight.add(idRightOnBot);
        tilesRight.add(idRightOnMid);
        tilesRight.add(idRightOnTop);
    }
}