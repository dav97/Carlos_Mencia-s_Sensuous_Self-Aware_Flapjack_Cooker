package overworld;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.event.ActionListener;
import java.io.InputStream;

import static main.Globals.DRAW_SCALE_BY_CONTAINER_WIDTH_DIVISOR;
import static org.newdawn.slick.Image.FILTER_NEAREST;
import static overworld.Globals.*;
import static overworld.Globals.PlayerGraphicIndex.*;

/**
 * overworld.Presenter will act as the presenter for the overworld game state, mediating between the model and view.
 * Post updates to the view. Post updates to and request data from the model.
 *
 * @author Scorple
 * @version 1.0
 * @since 2016.08.01
 */
public class Presenter extends BasicGameState
{
    private static int           WINDOW_WIDTH;
    private static int           WINDOW_HEIGHT;
    private static float         WINDOW_CENTER_HORIZONTAL;
    private static float         WINDOW_CENTER_VERTIAL;
    private static String        MAP_HOOK;
    //private final ActionListener changeStateListener; //TODO: needs redoing
    private final  int           id;
    private        Model         model;
    private        PlayerUpdater playerUpdater;
    private        View          view;
    private float scale = 6;

    /**
     * State constructor. Stores reference parameters.
     *
     * @param id                  int: The numeric id of this state.
     * @param changeStateListener ActionListener: Callback for change state requests.
     */
    public Presenter(int id, ActionListener changeStateListener)
    {
        this.id = id;
        //this.changeStateListener = changeStateListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getID()
    {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException
    {
        WINDOW_WIDTH = container.getWidth();
        WINDOW_HEIGHT = container.getHeight();
        WINDOW_CENTER_HORIZONTAL = (((float) WINDOW_WIDTH / 2.0f) / scale);
        WINDOW_CENTER_VERTIAL = (((float) WINDOW_HEIGHT / 2.0f) / scale);

        //component setup
        model = new Model();
        view = new View();
        scale = Math.min((container.getWidth() / DRAW_SCALE_BY_CONTAINER_WIDTH_DIVISOR),
                         (container.getHeight() / main.Globals.DRAW_SCALE_BY_CONTAINER_HEIGHT_DIVISOR));
        view.setScale(scale);
        //end component setup

        //map setup
        String defaultMapName = MAP_HOOK_LIST[DEFAULT_MAP_ID];

        loadMap(defaultMapName);
        //end map setup

        //player setup
        InputStream inputStream = ResourceLoader.getResourceAsStream(DEFAULT_CHARACTER_IMAGE_PATH);
        Image playerBasic =
            new Image(inputStream, DEFAULT_CHARACTER_IMAGE_PATH, false, FILTER_NEAREST);

        long playerWidth = playerBasic.getWidth() *
                           GRAPHIC_TO_LOGIC_CONVERSION; //the logical player width is the raw graphic width
        long playerHeight = playerBasic.getHeight() *
                            GRAPHIC_TO_LOGIC_CONVERSION; //the logical player height is the raw graphic height

        model.setupPlayerModel(playerWidth, playerHeight);
        view.setupPlayerViewModel(playerBasic);

        loadPlayerGraphics();

        model.spawnPlayer(TILED_HOOK_PROPERTY_SPAWN);

        model.setDDXDueToInput(STANDARD_DDX_DUE_TO_INPUT);
        model.setMaxDXDueToInput(STANDARD_MAX_DX_DUE_TO_INPUT);
        model.setInstantaneousJumpDY(STANDARD_INSTANTANEOUS_JUMP_DY);
        model.setInstantaneousWallJumpDY(STANDARD_INSTANTANEOUS_WALL_JUMP_DY);
        model.setInstantaneousWallJumpLeftDX(STANDARD_INSTANTANEOUS_WALL_JUMP_LEFT_DX);
        model.setInstantaneousWallJumpRightDX(STANDARD_INSTANTANEOUS_WALL_JUMP_RIGHT_DX);
        model.setDDYDueToGravity(STANDARD_DDY_DUE_TO_GRAVITY);
        model.setMaxDYDueToGravity(STANDARD_MAX_DY_DUE_TO_GRAVITY);
        model.setMaxDYOnWall(STANDARD_MAX_DY_ON_WALL);
        //end player setup

        playerUpdater = new PlayerUpdater(this);
    }

    /**
     * Load in a TiledMap and map graphic from the map name. Update the model with map logic
     * and pass the new map graphic to the view.
     *
     * @param loadMapName String: The name of the map to load. Should always match one of the
     *                    strings in overworld.Globals.MAP_HOOK_LIST. The Tiled map and graphic
     *                    files should be stored in overworld.Globals.MAP_RESOURCE_PATH. Graphic
     *                    should have extension overworld.Globals.GRAPHIC_EXTENSION.
     *
     * @throws SlickException Slick library exception.
     */
    private void loadMap(String loadMapName) throws SlickException
    {
        System.out.println(MAP_RESOURCE_PATH + loadMapName + TILED_MAP_EXTENSION);
        InputStream inputStream = ResourceLoader.getResourceAsStream(
            MAP_RESOURCE_PATH + loadMapName + TILED_MAP_EXTENSION);
        TiledMap tiledMap               = new TiledMap(inputStream, MAP_TILESET_PATH);
        int      tiledForegroundLayerId = tiledMap.getLayerIndex(TILED_FOREGROUND_LAYER_NAME);
        int      tiledReferenceLayerId  = tiledMap.getLayerIndex(TILED_REFERENCE_LAYER_NAME);

        Boolean mapClip[][] =
            new Boolean[tiledMap.getWidth()][tiledMap.getHeight()]; //true means passable, false means not passable
        String mapHooks[][] = new String[tiledMap.getWidth()][tiledMap.getHeight()];

        int    tileId;
        int    refTileId;
        String refTileHook;
        for (int x = 0; x < tiledMap.getWidth(); ++x)
        {
            for (int y = 0; y < tiledMap.getHeight(); ++y)
            {
                tileId = tiledMap.getTileId(x, y, tiledForegroundLayerId);
                mapClip[x][y] = tiledMap.getTileProperty(tileId,
                                                         TILED_CLIP_PROPERTY_NAME,
                                                         TILED_CLIP_PROPERTY_ENABLED)
                                        .equals(TILED_CLIP_PROPERTY_ENABLED);
                refTileId = tiledMap.getTileId(x, y, tiledReferenceLayerId);
                refTileHook = tiledMap.getTileProperty(refTileId,
                                                       TILED_HOOK_PROPERTY_NAME,
                                                       TILED_HOOK_PROPERTY_DEFAULT);
                mapHooks[x][y] = refTileHook;
                if (!refTileHook.equals(TILED_HOOK_PROPERTY_DEFAULT))
                {
                    System.out.println("Tile at <" + x + ", " + y + "> has reference hook <" + refTileHook + ">");
                }
            }
        }

        model.setupMapModel(tiledMap.getWidth(),
                            tiledMap.getHeight(),
                            tiledMap.getTileWidth() * GRAPHIC_TO_LOGIC_CONVERSION,
                            mapClip,
                            mapHooks);

        inputStream = ResourceLoader.getResourceAsStream(
            MAP_RESOURCE_PATH + loadMapName + GRAPHICS_EXTENSION);
        Image tiledMapImage =
            new Image(inputStream,
                      MAP_RESOURCE_PATH + loadMapName + GRAPHICS_EXTENSION,
                      false,
                      FILTER_NEAREST);

        view.setMapImage(tiledMapImage);

        MAP_HOOK = loadMapName;
        model.setMapHookCurrent(MAP_HOOK);
    }

    void loadPlayerGraphics() throws SlickException
    {
        InputStream inputStream;

        inputStream = ResourceLoader.getResourceAsStream(
            PLAYER_GRAPHICS_WALK_PATH + PLAYER_GRAPHICS_LEFT_PREFIX +
            PLAYER_GRAPHICS_WALK_POSTFIX + "1" + GRAPHICS_EXTENSION);
        Image playerImageFaceLeft = new Image(inputStream,
                                              PLAYER_GRAPHICS_WALK_PATH + PLAYER_GRAPHICS_LEFT_PREFIX +
                                              PLAYER_GRAPHICS_WALK_POSTFIX + "1" +
                                              GRAPHICS_EXTENSION,
                                              false,
                                              FILTER_NEAREST);

        inputStream = ResourceLoader.getResourceAsStream(
            PLAYER_GRAPHICS_WALK_PATH + PLAYER_GRAPHICS_RIGHT_PREFIX +
            PLAYER_GRAPHICS_WALK_POSTFIX + "1" + GRAPHICS_EXTENSION);
        Image playerImageFaceRight = new Image(inputStream,
                                               PLAYER_GRAPHICS_WALK_PATH +
                                               PLAYER_GRAPHICS_RIGHT_PREFIX +
                                               PLAYER_GRAPHICS_WALK_POSTFIX + "1" +
                                               GRAPHICS_EXTENSION,
                                               false,
                                               FILTER_NEAREST);

        Image[] playerFramesWalkLeft = new Image[PLAYER_GRAPHICS_WALK_FRAME_COUNT];
        for (int i = 0; i < PLAYER_GRAPHICS_WALK_FRAME_COUNT; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHICS_WALK_PATH + PLAYER_GRAPHICS_LEFT_PREFIX +
                PLAYER_GRAPHICS_WALK_POSTFIX + (i + 1) + GRAPHICS_EXTENSION);
            playerFramesWalkLeft[i] = new Image(inputStream,
                                                PLAYER_GRAPHICS_WALK_PATH +
                                                PLAYER_GRAPHICS_LEFT_PREFIX +
                                                PLAYER_GRAPHICS_WALK_POSTFIX + (i + 1) +
                                                GRAPHICS_EXTENSION,
                                                false,
                                                FILTER_NEAREST);
        }
        Animation playerAnimationWalkLeft =
            new Animation(playerFramesWalkLeft, PLAYER_GRAPHICS_WALK_FRAME_DURATION, true);
        playerAnimationWalkLeft.setLooping(true);
        playerAnimationWalkLeft.setPingPong(true);

        Image[] playerFramesWalkRight = new Image[PLAYER_GRAPHICS_WALK_FRAME_COUNT];
        for (int i = 0; i < PLAYER_GRAPHICS_WALK_FRAME_COUNT; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHICS_WALK_PATH + PLAYER_GRAPHICS_RIGHT_PREFIX +
                PLAYER_GRAPHICS_WALK_POSTFIX + (i + 1) + GRAPHICS_EXTENSION);
            playerFramesWalkRight[i] = new Image(inputStream,
                                                 PLAYER_GRAPHICS_WALK_PATH +
                                                 PLAYER_GRAPHICS_RIGHT_PREFIX +
                                                 PLAYER_GRAPHICS_WALK_POSTFIX + (i + 1) +
                                                 GRAPHICS_EXTENSION,
                                                 false,
                                                 FILTER_NEAREST);
        }
        Animation playerAnimationWalkRight =
            new Animation(playerFramesWalkRight, PLAYER_GRAPHICS_WALK_FRAME_DURATION, true);
        playerAnimationWalkRight.setLooping(true);
        playerAnimationWalkRight.setPingPong(true);

        Image[] playerFramesRunLeft = new Image[PLAYER_GRAPHICS_RUN_FRAME_COUNT];
        for (int i = 0; i < PLAYER_GRAPHICS_RUN_FRAME_COUNT; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHICS_RUN_PATH + PLAYER_GRAPHICS_LEFT_PREFIX +
                PLAYER_GRAPHICS_RUN_POSTFIX + (i + 1) + GRAPHICS_EXTENSION);
            playerFramesRunLeft[i] = new Image(inputStream,
                                               PLAYER_GRAPHICS_RUN_PATH +
                                               PLAYER_GRAPHICS_LEFT_PREFIX +
                                               PLAYER_GRAPHICS_RUN_POSTFIX + (i + 1) +
                                               GRAPHICS_EXTENSION,
                                               false,
                                               FILTER_NEAREST);
        }
        Animation playerAnimationRunLeft =
            new Animation(playerFramesRunLeft, PLAYER_GRAPHICS_RUN_FRAME_DURATION, true);
        playerAnimationRunLeft.setLooping(true);
        playerAnimationRunLeft.setPingPong(true);

        Image[] playerFramesRunRight = new Image[PLAYER_GRAPHICS_RUN_FRAME_COUNT];
        for (int i = 0; i < PLAYER_GRAPHICS_RUN_FRAME_COUNT; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHICS_RUN_PATH + PLAYER_GRAPHICS_RIGHT_PREFIX +
                PLAYER_GRAPHICS_RUN_POSTFIX + (i + 1) + GRAPHICS_EXTENSION);
            playerFramesRunRight[i] = new Image(inputStream,
                                                PLAYER_GRAPHICS_RUN_PATH +
                                                PLAYER_GRAPHICS_RIGHT_PREFIX +
                                                PLAYER_GRAPHICS_RUN_POSTFIX + (i + 1) +
                                                GRAPHICS_EXTENSION,
                                                false,
                                                FILTER_NEAREST);
        }
        Animation playerAnimationRunRight =
            new Animation(playerFramesRunRight, PLAYER_GRAPHICS_RUN_FRAME_DURATION, true);
        playerAnimationRunRight.setLooping(true);
        playerAnimationRunRight.setPingPong(true);

        Image[] playerFramesJumpLeft = new Image[PLAYER_GRAPHICS_JUMP_FRAME_COUNT];
        for (int i = 0; i < PLAYER_GRAPHICS_JUMP_FRAME_COUNT; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHICS_JUMP_PATH + PLAYER_GRAPHICS_LEFT_PREFIX + PLAYER_GRAPHICS_JUMP_POSTFIX + (i + 1) +
                GRAPHICS_EXTENSION);
            playerFramesJumpLeft[i] = new Image(inputStream,
                                                PLAYER_GRAPHICS_JUMP_PATH + PLAYER_GRAPHICS_LEFT_PREFIX +
                                                PLAYER_GRAPHICS_JUMP_POSTFIX + (i + 1) +
                                                GRAPHICS_EXTENSION,
                                                false,
                                                FILTER_NEAREST);
        }
        Animation playerAnimationJumpLeft =
            new Animation(playerFramesJumpLeft, PLAYER_GRAPHICS_JUMP_FRAME_DURATION, true);
        playerAnimationJumpLeft.setLooping(false);

        Image[] playerFramesJumpRight = new Image[PLAYER_GRAPHICS_JUMP_FRAME_COUNT];
        for (int i = 0; i < PLAYER_GRAPHICS_JUMP_FRAME_COUNT; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHICS_JUMP_PATH + PLAYER_GRAPHICS_RIGHT_PREFIX + PLAYER_GRAPHICS_JUMP_POSTFIX + (i + 1) +
                GRAPHICS_EXTENSION);
            playerFramesJumpRight[i] = new Image(inputStream,
                                                 PLAYER_GRAPHICS_JUMP_PATH + PLAYER_GRAPHICS_RIGHT_PREFIX +
                                                 PLAYER_GRAPHICS_JUMP_POSTFIX + (i + 1) +
                                                 GRAPHICS_EXTENSION,
                                                 false,
                                                 FILTER_NEAREST);
        }
        Animation playerAnimationJumpRight =
            new Animation(playerFramesJumpRight, PLAYER_GRAPHICS_JUMP_FRAME_DURATION, true);
        playerAnimationJumpRight.setLooping(false);

        inputStream = ResourceLoader.getResourceAsStream(
            PLAYER_GRAPHICS_WALL_PATH + PLAYER_GRAPHICS_WALL_PRE_PREFIX +
            PLAYER_GRAPHICS_LEFT_PREFIX + GRAPHICS_EXTENSION);
        Image playerImageWallLeft = new Image(inputStream,
                                              PLAYER_GRAPHICS_WALL_PATH +
                                              PLAYER_GRAPHICS_WALL_PRE_PREFIX +
                                              PLAYER_GRAPHICS_LEFT_PREFIX +
                                              GRAPHICS_EXTENSION,
                                              false,
                                              FILTER_NEAREST);

        inputStream = ResourceLoader.getResourceAsStream(
            PLAYER_GRAPHICS_WALL_PATH + PLAYER_GRAPHICS_WALL_PRE_PREFIX +
            PLAYER_GRAPHICS_RIGHT_PREFIX + GRAPHICS_EXTENSION);
        Image playerImageWallRight = new Image(inputStream,
                                               PLAYER_GRAPHICS_WALL_PATH +
                                               PLAYER_GRAPHICS_WALL_PRE_PREFIX +
                                               PLAYER_GRAPHICS_RIGHT_PREFIX +
                                               GRAPHICS_EXTENSION,
                                               false,
                                               FILTER_NEAREST);

        view.setPlayerImageFaceLeft(playerImageFaceLeft);
        view.setPlayerImageFaceRight(playerImageFaceRight);
        view.setPlayerAnimationWalkLeft(playerAnimationWalkLeft);
        view.setPlayerAnimationWalkRight(playerAnimationWalkRight);
        view.setPlayerAnimationRunLeft(playerAnimationRunLeft);
        view.setPlayerAnimationRunRight(playerAnimationRunRight);
        view.setPlayerAnimationJumpLeft(playerAnimationJumpLeft);
        view.setPlayerAnimationJumpRight(playerAnimationJumpRight);
        view.setPlayerImageWallLeft(playerImageWallLeft);
        view.setPlayerImageWallRight(playerImageWallRight);
    }

    /**
     * Transition to a new map. Load the new map and spawn the player at the hook associated
     * with the map transitioning from.
     *
     * @param newMapHook String: The name of the map to load.
     * @param spawnHook  String: The hook to use for spawning the player in the new map.
     *
     * @throws SlickException Slick library exception.
     */
    void transitionMap(String newMapHook, String spawnHook) throws SlickException
    {
        loadMap(newMapHook);

        model.spawnPlayer(spawnHook);
        model.setMapHookSpawn(spawnHook);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException
    {
        //check window size change and update logic
        if (WINDOW_WIDTH != Display.getWidth() || WINDOW_HEIGHT != Display.getHeight())
        {
            WINDOW_WIDTH = Display.getWidth();
            WINDOW_HEIGHT = Display.getHeight();
            scale = Math.min((WINDOW_WIDTH / DRAW_SCALE_BY_CONTAINER_WIDTH_DIVISOR),
                             (WINDOW_HEIGHT / main.Globals.DRAW_SCALE_BY_CONTAINER_HEIGHT_DIVISOR));
            view.setScale(scale);
            WINDOW_CENTER_HORIZONTAL = (((float) WINDOW_WIDTH / 2.0f) / scale);
            WINDOW_CENTER_VERTIAL = (((float) WINDOW_HEIGHT / 2.0f) / scale);
        }
        //end check window size change and update logic

        //view updating
        long playerX      = model.getPlayerX();
        long playerY      = model.getPlayerY();
        long playerDX     = model.getPlayerDX();
        long playerDY     = model.getPlayerDY();
        long playerWidth  = model.getPlayerWidth();
        long playerHeight = model.getPlayerHeight();

        //map draw position updating
        float mapX = -(((float) playerX / GRAPHIC_TO_LOGIC_CONVERSION) +
                       (((float) playerWidth / GRAPHIC_TO_LOGIC_CONVERSION) / 2.0f) - WINDOW_CENTER_HORIZONTAL);
        float mapY = -(((float) playerY / GRAPHIC_TO_LOGIC_CONVERSION) +
                       (((float) playerHeight / GRAPHIC_TO_LOGIC_CONVERSION) / 2.0f) - WINDOW_CENTER_VERTIAL);

        view.setMapLocation(mapX, mapY);
        //end map draw position updating

        //player draw position updating
        float centeredPlayerX =
            WINDOW_CENTER_HORIZONTAL - (((float) playerWidth / GRAPHIC_TO_LOGIC_CONVERSION) / 2.0f);
        float centeredPlayerY =
            WINDOW_CENTER_VERTIAL - (((float) playerHeight / GRAPHIC_TO_LOGIC_CONVERSION) / 2.0f);

        view.setPlayerLocation(centeredPlayerX, centeredPlayerY);
        //end player draw position updating

        //player graphic/animation updating
        if (model.isPlayerOnWallLeft())
        {
            view.setPlayerGraphicIndex(wallLeft);
        }
        else if (model.isPlayerOnWallRight())
        {
            view.setPlayerGraphicIndex(wallRight);
        }
        else if (playerDY < 0)
        {
            if ((view.getPlayerGraphicIndex() != jumpLeft) && (view.getPlayerGraphicIndex() != jumpRight))
            {
                view.resetJump();
            }
            if (playerDX < 0)
            {
                view.setPlayerGraphicIndex(jumpLeft);
            }
            else if (playerDX > 0)
            {
                view.setPlayerGraphicIndex(jumpRight);
            }
            else if ((view.getPlayerGraphicIndex() == runLeft) ||
                     (view.getPlayerGraphicIndex() == faceLeft) || (view.getPlayerGraphicIndex() == jumpLeft) ||
                     (view.getPlayerGraphicIndex() == walkLeft) || (view.getPlayerGraphicIndex() == wallLeft))
            {
                view.setPlayerGraphicIndex(jumpLeft);
            }
            else
            {
                view.setPlayerGraphicIndex(jumpRight);
            }
        }
        else if (playerDY > 0 || !model.isPlayerCollisionDown())
        {
            if ((view.getPlayerGraphicIndex() != jumpLeft) && (view.getPlayerGraphicIndex() != jumpRight))
            {
                view.setFall();
            }
            if (playerDX < 0)
            {
                view.setPlayerGraphicIndex(jumpLeft);
            }
            else if (playerDX > 0)
            {
                view.setPlayerGraphicIndex(jumpRight);
            }
            else if ((view.getPlayerGraphicIndex() == runLeft) ||
                     (view.getPlayerGraphicIndex() == faceLeft) || (view.getPlayerGraphicIndex() == jumpLeft) ||
                     (view.getPlayerGraphicIndex() == walkLeft) || (view.getPlayerGraphicIndex() == wallLeft))
            {
                view.setPlayerGraphicIndex(jumpLeft);
            }
            else
            {
                view.setPlayerGraphicIndex(jumpRight);
            }
        }
        else if (playerDX > 0)
        {
            view.setPlayerGraphicIndex(runRight);
        }
        else if (playerDX < 0)
        {
            view.setPlayerGraphicIndex(runLeft);
        }
        else
        {
            if ((view.getPlayerGraphicIndex() == runLeft) || (view.getPlayerGraphicIndex() == walkLeft) ||
                (view.getPlayerGraphicIndex() == wallLeft) || (view.getPlayerGraphicIndex() == jumpLeft))
            {
                view.setPlayerGraphicIndex(faceLeft);
            }
            else if ((view.getPlayerGraphicIndex() == runRight) || (view.getPlayerGraphicIndex() == walkRight) ||
                     (view.getPlayerGraphicIndex() == wallRight) || (view.getPlayerGraphicIndex() == jumpRight))
            {
                view.setPlayerGraphicIndex(faceRight);
            }
        }
        //end player graphic/animation updating
        //end view updating

        view.draw(g);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException
    {
        playerUpdater.update(model, container.getInput());

        //example of requesting game state change, i.e. to the main menu or fight state
        /*if (false) {
            ActionEvent e = new ActionEvent(this, 0, String.valueOf(id));
            changeStateListener.actionPerformed(e);
        }*/
    }
}
