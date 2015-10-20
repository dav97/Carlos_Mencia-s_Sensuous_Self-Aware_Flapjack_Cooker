package gameEngine;

import graphicsEngine.ShaderManager;
import utils.Globals;

/**
 * Created by Rick on 10/18/2015.
 */
public class Stage {

    public static ShaderManager shaderManager;

    public static Player player;
    public static Map map;

    public Stage() {
        shaderManager = new ShaderManager();
        shaderManager.loadAll();

        map = new Map();
        player = new Player();
        //player.position.x = -1.0f + Globals.TILE_WIDTH;
        player.iposition.x = Globals.TILE_WIDTH_P;
        //player.position.y = -1.0f + Globals.TILE_HEIGHT;
        player.iposition.y = Globals.TILE_HEIGHT_P;
    }

    public void checkFloor(Player player) {
        boolean floor = false;
        boolean ceiling = false;
        boolean leftWall = false;
        boolean rightWall= false;
        for (Tile[] row : map.grid) {
            for (Tile tile : row) {
                if (tile != null) {
                    /*if ((player.position.x >= tile.position.x &&
                            player.position.x < tile.position.x + Globals.TILE_WIDTH) ||
                            (player.position.x + Globals.ACTOR_WIDTH >= tile.position.x &&
                                    player.position.x + Globals.ACTOR_WIDTH < tile.position.x + Globals.TILE_WIDTH)) {
                        if (player.position.y <= tile.position.y + Globals.TILE_HEIGHT &&
                                player.position.y + Globals.ACTOR_HEIGHT > tile.position.y + Globals.TILE_HEIGHT) {
                            if (player.position.y < tile.position.y + Globals.TILE_HEIGHT) {
                                player.position.y = tile.position.y + Globals.TILE_HEIGHT;
                            }
                            floor = true;
                        }
                        if (player.position.y + Globals.ACTOR_HEIGHT >= tile.position.y &&
                                player.position.y < tile.position.y) {
                            player.position.y = tile.position.y - Globals.ACTOR_HEIGHT;
                            ceiling = true;
                        }
                    }
                    if ((player.position.y >= tile.position.y &&
                            player.position.y < tile.position.y + Globals.TILE_HEIGHT) ||
                            (player.position.y + Globals.ACTOR_HEIGHT >= tile.position.y &&
                                    player.position.y + Globals.ACTOR_HEIGHT < tile.position.y + Globals.TILE_HEIGHT) ||
                            (player.position.y <= tile.position.y &&
                            player.position.y + Globals.ACTOR_HEIGHT >= tile.position.y + Globals.TILE_HEIGHT)) {
                        if (player.position.x - (Globals.PIXEL_WIDTH * 4) <= tile.position.x + Globals.TILE_WIDTH &&
                                player.position.x > tile.position.x) {
                            leftWall = true;
                        }
                        if (player.position.x + Globals.ACTOR_WIDTH + (Globals.PIXEL_WIDTH * 4) >= tile.position.x &&
                                player.position.x < tile.position.x) {
                            rightWall = true;
                        }
                    }*/
                    if ((player.iposition.x >= tile.iposition.x &&
                            player.iposition.x < tile.iposition.x + Globals.TILE_WIDTH_P) ||
                            (player.iposition.x + Globals.ACTOR_WIDTH_P >= tile.iposition.x &&
                                    player.iposition.x + Globals.ACTOR_WIDTH_P < tile.iposition.x + Globals.TILE_WIDTH_P)) {
                        if (player.iposition.y <= tile.iposition.y + Globals.TILE_HEIGHT_P &&
                                player.iposition.y + Globals.ACTOR_HEIGHT_P > tile.iposition.y + Globals.TILE_HEIGHT_P) {
                            if (player.iposition.y < tile.iposition.y + Globals.TILE_HEIGHT_P) {
                                player.iposition.y = tile.iposition.y + Globals.TILE_HEIGHT_P;
                            }
                            floor = true;
                        }
                        if (player.iposition.y + Globals.ACTOR_HEIGHT_P >= tile.iposition.y &&
                                player.iposition.y < tile.iposition.y) {
                            player.iposition.y = tile.iposition.y - Globals.ACTOR_HEIGHT_P;
                            ceiling = true;
                        }
                    }
                    if ((player.iposition.y >= tile.iposition.y &&
                            player.iposition.y < tile.iposition.y + Globals.TILE_HEIGHT_P) ||
                            (player.iposition.y + Globals.ACTOR_HEIGHT_P >= tile.iposition.y &&
                                    player.iposition.y + Globals.ACTOR_HEIGHT_P < tile.iposition.y + Globals.TILE_HEIGHT_P) ||
                            (player.iposition.y <= tile.iposition.y &&
                                    player.iposition.y + Globals.ACTOR_HEIGHT_P >= tile.iposition.y + Globals.TILE_HEIGHT_P)) {
                        if (player.iposition.x - 4 <= tile.iposition.x + Globals.TILE_WIDTH_P &&
                                player.iposition.x > tile.iposition.x) {
                            leftWall = true;
                        }
                        if (player.iposition.x + Globals.ACTOR_WIDTH_P + 4 >= tile.iposition.x &&
                                player.iposition.x < tile.iposition.x) {
                            rightWall = true;
                        }
                    }
                }
            }
        }
        player.floor = floor;
        player.ceiling = ceiling;
        player.leftWall = leftWall;
        player.rightWall = rightWall;
    }

    public void update() {
        checkFloor(player);
        player.update();
    }

    public void draw() {
        shaderManager.shader1.start();
        shaderManager.shader1.setUniform3f("pos", player.iposition.toVector3f());
        player.draw();
        shaderManager.shader1.stop();

        shaderManager.shader1.start();
        for (Tile[] row : map.grid) {
            for (Tile tile : row) {
                if (tile != null) {
                    shaderManager.shader1.setUniform3f("pos", tile.iposition.toVector3f());
                    tile.draw();
                }
            }
        }
        shaderManager.shader1.stop();
    }

}
