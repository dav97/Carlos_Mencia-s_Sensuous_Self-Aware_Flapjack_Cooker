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
        player.position.x = -1.0f + Globals.TILE_WIDTH;
        player.position.y = -1.0f + Globals.TILE_HEIGHT;
    }

    public void checkFloor(Player player) {
        boolean floor = false;
        boolean ceiling = false;
        boolean leftWall = false;
        boolean rightWall= false;
        for (Tile[] row : map.grid) {
            for (Tile tile : row) {
                if (tile != null) {
                    if ((player.position.x >= tile.position.x &&
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
        shaderManager.shader1.setUniform3f("pos", player.position);
        player.draw();
        shaderManager.shader1.stop();

        shaderManager.shader1.start();
        for (Tile[] row : map.grid) {
            for (Tile tile : row) {
                if (tile != null) {
                    shaderManager.shader1.setUniform3f("pos", tile.position);
                    tile.draw();
                }
            }
        }
        shaderManager.shader1.stop();
    }

}
