package overworld;

/**
 * Created by Scorple on 8/5/2016.
 */
class Globals
{

    public static final String[] MAP_HOOK_LIST = new String[]{"debug", "debug_house"};

    public static final int DEFAULT_MAP_ID = 0;

    //this is annoying but currently necessary because the TiledMap constructor
    //adds a "/" to the tileset path... TODO: revisit
    public static final String MAP_RESOURCE_PATH = "res/debug/";
    public static final String MAP_TILESET_PATH = "res/debug";

    public static final String TILED_MAP_EXTENSION = ".tmx";
    public static final String GRAPHICS_EXTENSION = ".png";

    public static final String DEFAULT_TILED_MAP_PATH = "res/debug/debug.tmx";

    public static final String TILED_FOREGROUND_LAYER_NAME = "foreground";
    public static final String TILED_REFERENCE_LAYER_NAME = "reflayer";

    public static final String TILED_CLIP_PROPERTY_NAME = "clip";
    public static final String TILED_CLIP_PROPERTY_ENABLED = "1";
    public static final String TILED_CLIP_PROPERTY_DISABLED = "0";

    public static final String TILED_HOOK_PROPERTY_NAME = "hook";
    public static final String TILED_HOOK_PROPERTY_DEFAULT = "";
    public static final String TILED_HOOK_PROPERTY_SPAWN = "spawn";

    public static final String DEFAULT_MAP_IMAGE_PATH                    = "res/debug/debug.png";
    public static final String PLAYER_GRAPHICS_PATH                      = "res/Overworld Characters/PCs/Marina/";
    public static final String DEFAULT_CHARACTER_IMAGE_PATH              = "res/Overworld Characters/PCs/Marina/mf.png";
    public static final String PLAYER_GRAPHICS_LEFT_PREFIX               = "l";
    public static final String PLAYER_GRAPHICS_RIGHT_PREFIX              = "r";
    public static final String PLAYER_GRAPHICS_WALK_PATH                 = "res/Overworld Characters/PCs/Marina/walk/";
    public static final int    PLAYER_GRAPHICS_WALK_FRAME_COUNT          = 3;
    public static final int    PLAYER_GRAPHICS_WALK_FRAME_DURATION       = 200;
    public static final String PLAYER_GRAPHICS_WALK_POSTFIX              = "w";
    public static final String PLAYER_GRAPHICS_RUN_PATH                  = "res/Overworld Characters/PCs/Marina/run/";
    public static final int    PLAYER_GRAPHICS_RUN_FRAME_COUNT           = 4;
    public static final int    PLAYER_GRAPHICS_RUN_FRAME_DURATION        = 125;
    public static final String PLAYER_GRAPHICS_RUN_POSTFIX               = "run";
    public static final String PLAYER_GRAPHICS_JUMP_PATH                 = "res/Overworld Characters/PCs/Marina/jump/";
    public static final int    PLAYER_GRAPHICS_JUMP_FRAME_COUNT          = 2;
    public static final int    PLAYER_GRAPHICS_JUMP_FRAME_DURATION       = 75;
    public static final String PLAYER_GRAPHICS_JUMP_POSTFIX              = "j";
    public static final String PLAYER_GRAPHICS_WALL_PATH                 =
        "res/Overworld Characters/PCs/Marina/action/";
    public static final String PLAYER_GRAPHICS_WALL_PRE_PREFIX           = "walljump_";
    public static final int    GRAPHIC_TO_LOGIC_CONVERSION               = 10000;
    public static final long   STANDARD_COLLISION_CHECK_DISTANCE_UP      = -1000;
    public static final long   STANDARD_COLLISION_CHECK_DISTANCE_DOWN    = 1000;
    public static final long   STANDARD_COLLISION_CHECK_DISTANCE_LEFT    = -1000;
    public static final long   STANDARD_COLLISION_CHECK_DISTANCE_RIGHT   = 1000;
    public static final long   STANDARD_DDX_DUE_TO_INPUT                 = 8;
    public static final long   STANDARD_MAX_DX_DUE_TO_INPUT              = 1000;
    public static final long   STANDARD_INSTANTANEOUS_JUMP_DY            = -3500;
    public static final long   STANDARD_INSTANTANEOUS_WALL_JUMP_DY       = -2000;
    public static final long   STANDARD_INSTANTANEOUS_WALL_JUMP_LEFT_DX  = -2000;
    public static final long   STANDARD_INSTANTANEOUS_WALL_JUMP_RIGHT_DX = 2000;
    public static final long   STANDARD_DDY_DUE_TO_GRAVITY               = 10;
    public static final long   STANDARD_MAX_DY_DUE_TO_GRAVITY            = 2000;
    public static final long   STANDARD_MAX_DY_ON_WALL                   = 200;

    public enum PlayerGraphicIndex
    {
        faceFront,
        faceLeft,
        faceRight,
        walkLeft,
        walkRight,
        runLeft,
        runRight,
        jumpLeft,
        jumpRight,
        wallLeft,
        wallRight
    }

}
