/**
 * Created by Scorple on 8/5/2016.
 */
public class OverworldGlobals {

    public static final String[] MAP_HOOK_LIST = new String[]{
            "debug",
            "debug_house"
    };

    public static final int DEFAULT_MAP_ID = 0;

    public static final String MAP_RESOURCE_PATH = "res/debug/";

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

    public static final String DEFAULT_MAP_IMAGE_PATH = "res/debug/debug.png";

    public static final String DEFAULT_CHARACTER_IMAGE_PATH = "res/Overworld Characters/PCs/Marina/mf.png";

    public static final long STANDARD_COLLISION_CHECK_DISTANCE_UP = -10;
    public static final long STANDARD_COLLISION_CHECK_DISTANCE_DOWN = 10;
    public static final long STANDARD_COLLISION_CHECK_DISTANCE_LEFT = -10;
    public static final long STANDARD_COLLISION_CHECK_DISTANCE_RIGHT = 10;

    public static final long STANDARD_DDX_DUE_TO_INPUT = 1;
    public static final long STANDARD_MAX_DX_DUE_TO_INPUT = 10;

    //public static final long STANDARD_DX_FADE_SANITY_BOUND = 0.01f;

    public static final long STANDARD_INSTANTANEOUS_JUMP_DY = -35;
    public static final long STANDARD_INSTANTANEOUS_WALL_JUMP_DY = -20;
    public static final long STANDARD_INSTANTANEOUS_WALL_JUMP_LEFT_DX = -20;
    public static final long STANDARD_INSTANTANEOUS_WALL_JUMP_RIGHT_DX = 20;

    public static final long STANDARD_DDY_DUE_TO_GRAVITY = 1;
    public static final long STANDARD_MAX_DY_DUE_TO_GRAVITY = 20;
    public static final long STANDARD_MAX_DY_ON_WALL = 2;

}
