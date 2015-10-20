package utils;

/**
 * Created by Rick on 10/18/2015.
 */
public class Globals {

    public static int WINDOW_WIDTH = 960;
    public static int WINDOW_HEIGHT = 640;
    public static int MAP_WIDTH = 30;
    public static int MAP_HEIGHT = 20;
    public static float TILE_WIDTH =
    //(WINDOW_WIDTH / MAP_WIDTH) / (WINDOW_WIDTH / 2);
    32.0f / (WINDOW_WIDTH / 2);
    public static float TILE_HEIGHT =
    //(WINDOW_HEIGHT / MAP_HEIGHT) / (WINDOW_HEIGHT / 2);
    32.0f / (WINDOW_HEIGHT / 2);
    public static float ACTOR_WIDTH = 32.0f / (WINDOW_WIDTH / 2);
    public static float ACTOR_HEIGHT = 64.0f / (WINDOW_HEIGHT / 2);
    public static float PIXEL_WIDTH = 1.0f / (WINDOW_WIDTH / 2);
    public static float PIXEL_HEIGHT = 1.0f / (WINDOW_HEIGHT / 2);
    public static int TILE_WIDTH_P = 32;
    public static int TILE_HEIGHT_P = 32;
    public static int ACTOR_WIDTH_P = 32;
    public static int ACTOR_HEIGHT_P = 64;

}
