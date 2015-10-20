package gameEngine;

import utils.Globals;

/**
 * Created by Rick on 10/18/2015.
 */
public class Map {

    public static int WIDTH = Globals.MAP_WIDTH;
    public static int HEIGHT = Globals.MAP_HEIGHT;
    public static Tile[][] grid;

    public Map() {
        grid = new Tile[WIDTH][HEIGHT];

        for (int i = 0; i < WIDTH; i++) {
            grid[i][0] = new Tile();
            //grid[i][0].position.x = -1.0f + (Globals.TILE_WIDTH * i);
            grid[i][0].iposition.x = Globals.TILE_WIDTH_P * i;
            //grid[i][0].position.y = -1.0f;
            grid[i][0].iposition.y = 0;
        }

        for (int i = 4; i < 8; i++) {
            //grid[i][5] = new Tile();
            //grid[i][5].position.x = -1.0f + (Globals.TILE_WIDTH * i);
            //grid[i][5].position.y = -1.0f + (Globals.TILE_HEIGHT * 5);
        }

        for (int i = 12; i < 18; i++) {
            //grid[i][5] = new Tile();
            //grid[i][5].position.x = -1.0f + (Globals.TILE_WIDTH * i);
            //grid[i][5].position.y = -1.0f + (Globals.TILE_HEIGHT * 5);
        }

        for (int i = 22; i < 26; i++) {
            //grid[i][5] = new Tile();
            //grid[i][5].position.x = -1.0f + (Globals.TILE_WIDTH * i);
            //grid[i][5].position.y = -1.0f + (Globals.TILE_HEIGHT * 5);
        }

        for (int i = 8; i < 12; i++) {
            //grid[i][10] = new Tile();
            //grid[i][10].position.x = -1.0f + (Globals.TILE_WIDTH * i);
            //grid[i][10].position.y = -1.0f + (Globals.TILE_HEIGHT * 10);
        }

        for (int i = 18; i < 22; i++) {
            //grid[i][10] = new Tile();
            //grid[i][10].position.x = -1.0f + (Globals.TILE_WIDTH * i);
            //grid[i][10].position.y = -1.0f + (Globals.TILE_HEIGHT * 10);
        }

        for (int i = 12; i < 18; i++) {
            grid[i][15] = new Tile();
            //grid[i][15].position.x = -1.0f + (Globals.TILE_WIDTH * i);
            grid[i][15].iposition.x = Globals.TILE_WIDTH_P * i;
            //grid[i][15].position.y = -1.0f + (Globals.TILE_HEIGHT * 15);
            grid[i][15].iposition.y = Globals.TILE_HEIGHT_P * 15;
        }

        for (int i = 1; i < 15; i++) {
            grid[12][i] = new Tile();
            //grid[12][i].position.x = -1.0f + (Globals.TILE_WIDTH * 12);
            grid[12][i].iposition.x = Globals.TILE_WIDTH_P * 12;
            //grid[12][i].position.y = -1.0f + (Globals.TILE_HEIGHT * i);
            grid[12][i].iposition.y = Globals.TILE_HEIGHT_P * i;
            grid[17][i] = new Tile();
            //grid[17][i].position.x = -1.0f + (Globals.TILE_WIDTH * 17);
            grid[17][i].iposition.x = Globals.TILE_WIDTH_P * 17;
            //grid[17][i].position.y = -1.0f + (Globals.TILE_HEIGHT * i);
            grid[17][i].iposition.y = Globals.TILE_HEIGHT_P * i;
        }
    }

}
