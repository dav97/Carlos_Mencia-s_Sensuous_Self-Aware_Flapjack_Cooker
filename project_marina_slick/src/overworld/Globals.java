package overworld;

/**
 * overworld.Globals contains constants global to the overworld package.
 *
 * @author scorple
 * @version dev01
 * @since 2016_0805
 */
class Globals
{

    public static final String   TILED_CLIP_PROPERTY_DISABLED              = "0";
    //public static final String   DEFAULT_TILED_MAP_PATH                    = "res/overworld/map/debug/debug.tmx";
    //public static final String   DEFAULT_MAP_IMAGE_PATH                    = "res/overworld/debug/debug.png";
    //public static final String   PLAYER_GRAPHICS_PATH                      = "res/overworld/actor/marina/graphic/";
    static final String[] MAP_HOOK_LIST              = new String[]{"debug2", "debug_house2"};
    static final int      DEFAULT_MAP_ID             = 0;
    //this is annoying but currently necessary because the TiledMap constructor
    //adds a "/" to the tileset path... TODO: revisit
    static final String   MAP_RESOURCE_PATH          = "res/overworld/map/";
    static final String   MAP_GRAPHIC_DIRECTORY      = "/";
    static final String   MAP_TILESET_PATH           = "res/overworld/map";
    static final String   TILED_MAP_EXTENSION        = ".tmx";
    static final String   GRAPHICS_EXTENSION         = ".png";
    static final String   TILED_REFERENCE_LAYER_NAME = "reference";
    static final String   TILED_COLLISION_LAYER_NAME = "collision";
    //static final        String   TILED_FOREGROUND_LAYER_NAME               = "foreground";
    //static final        String   TILED_MIDGROUND_LAYER_NAME                = "midground";
    //static final        String   TILED_BACKGROUND_LAYER_NAME               = "background";
    //static final        String   TILED_SKYBOX_LAYER_NAME                   = "skybox";
    static final String   TILED_CLIP_PROPERTY_NAME   = "clip";
    static final        String   TILED_CLIP_PROPERTY_ENABLED               = "1";
    static final        String   TILED_HOOK_PROPERTY_NAME                  = "hook";
    static final        String   TILED_HOOK_PROPERTY_DEFAULT               = "";
    static final        String   TILED_HOOK_PROPERTY_SPAWN                 = "spawn";
    static final        String   MAP_GRAPHIC_FOREGROUND_POSTFIX            = "_foreground";
    static final        String   MAP_GRAPHIC_MIDGROUND_POSTFIX             = "_midground";
    static final        String   MAP_GRAPHIC_BACKGROUND_POSTFIX            = "_background";
    static final        String   MAP_GRAPHIC_SKYBOX_POSTFIX                = "_skybox";
    static final        String   DEFAULT_CHARACTER_IMAGE_PATH              =
        "res/overworld/actor/marina/graphic/mf.png";
    static final        String   PLAYER_GRAPHICS_LEFT_PREFIX               = "l";
    static final        String   PLAYER_GRAPHICS_RIGHT_PREFIX              = "r";
    static final        String   PLAYER_GRAPHICS_WALK_PATH                 =
        "res/overworld/actor/marina/graphic/walk/";
    static final        int      PLAYER_GRAPHICS_WALK_FRAME_COUNT          = 3;
    static final        int      PLAYER_GRAPHICS_WALK_FRAME_DURATION       = 200;
    static final        String   PLAYER_GRAPHICS_WALK_POSTFIX              = "w";
    static final        String   PLAYER_GRAPHICS_RUN_PATH                  = "res/overworld/actor/marina/graphic/run/";
    static final        int      PLAYER_GRAPHICS_RUN_FRAME_COUNT           = 4;
    static final        int      PLAYER_GRAPHICS_RUN_FRAME_DURATION        = 125;
    static final        String   PLAYER_GRAPHICS_RUN_POSTFIX               = "run";
    static final        String   PLAYER_GRAPHICS_JUMP_PATH                 =
        "res/overworld/actor/marina/graphic/jump/";
    static final        int      PLAYER_GRAPHICS_JUMP_FRAME_COUNT          = 2;
    static final        int      PLAYER_GRAPHICS_JUMP_FRAME_DURATION       = 75;
    static final        String   PLAYER_GRAPHICS_JUMP_POSTFIX              = "j";
    static final        String   PLAYER_GRAPHICS_WALL_PATH                 =
        "res/overworld/actor/marina/graphic/action/";
    static final        String   PLAYER_GRAPHICS_WALL_PRE_PREFIX           = "walljump_";
    static final        int      GRAPHIC_TO_LOGIC_CONVERSION               = 10000;
    static final        long     STANDARD_COLLISION_CHECK_DISTANCE_UP      = -1000;
    static final        long     STANDARD_COLLISION_CHECK_DISTANCE_DOWN    = 1000;
    static final        long     STANDARD_COLLISION_CHECK_DISTANCE_LEFT    = -1000;
    static final        long     STANDARD_COLLISION_CHECK_DISTANCE_RIGHT   = 1000;
    static final        long     STANDARD_DDX_DUE_TO_INPUT                 = 8;
    static final        long     STANDARD_MAX_DX_DUE_TO_INPUT              = 1000;
    static final        long     STANDARD_INSTANTANEOUS_JUMP_DY            = -3500;
    static final        long     STANDARD_INSTANTANEOUS_WALL_JUMP_DY       = -2000;
    static final        long     STANDARD_INSTANTANEOUS_WALL_JUMP_LEFT_DX  = -2000;
    static final        long     STANDARD_INSTANTANEOUS_WALL_JUMP_RIGHT_DX = 2000;
    static final        long     STANDARD_DDY_DUE_TO_GRAVITY               = 10;
    static final        long     STANDARD_MAX_DY_DUE_TO_GRAVITY            = 2000;
    static final        long     STANDARD_MAX_DY_ON_WALL                   = 200;

    static final String ACTOR_PROPERTIES_RESOURCE_PATH = "res/overworld/actor/";
    static final String DEFAULT_PLAYER_REF             = "marina";
    static final String XML_EXTENSION                  = ".xml";

    static final String NPC_HOOK_PREFIX = "npc_";

    enum PlayerGraphicIndex
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
