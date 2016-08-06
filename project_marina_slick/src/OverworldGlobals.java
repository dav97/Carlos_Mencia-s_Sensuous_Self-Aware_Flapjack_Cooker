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

    public static final float STANDARD_DDX_DUE_TO_INPUT = 0.001f;
    public static final float STANDARD_MAX_DX_DUE_TO_INPUT = 0.1f;

    public static final float STANDARD_DX_FADE_SANITY_BOUND = 0.01f;

    public static final float STANDARD_INSTANTANEOUS_JUMP_DY = -0.35f;

    public static final float STANDARD_DDY_DUE_TO_GRAVITY = 0.001f;
    public static final float STANDARD_MAX_DY_DUE_TO_GRAVITY = 0.25f;

}
