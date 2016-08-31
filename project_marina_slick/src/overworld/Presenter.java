package overworld;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.ResourceLoader;
import overworld.model.Actor;
import overworld.model.Model;
import overworld.view.View;

import java.awt.event.ActionListener;
import java.io.InputStream;

import static main.Globals.DRAW_SCALE_BY_CONTAINER_WIDTH_DIVISOR;
import static org.newdawn.slick.Image.FILTER_NEAREST;
import static overworld.Globals.*;
import static overworld.Globals.PlayerGraphicIndex.*;

/**
 * overworld.Presenter will act as the presenter for the overworld game state, mediating between the MODEL and VIEW.
 * Post updates to the VIEW. Post updates to and request data from the MODEL.
 *
 * @author scorple
 * @version dev01
 * @since 2016_0801
 */
public class Presenter extends BasicGameState
{
    private static int           WINDOW_WIDTH;
    private static int           WINDOW_HEIGHT;
    private static float         WINDOW_CENTER_HORIZONTAL;
    private static float         WINDOW_CENTER_VERTICAL;
    private static String        MAP_HOOK;
    private static String        PLAYER_REF;
    //private final ActionListener changeStateListener; //TODO: needs redoing
    private final  int           id;
    private        Model         MODEL;
    private        PlayerUpdater PLAYER_UPDATER;
    private        View          VIEW;
    private float GRAPHIC_SCALE = 6;

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
        WINDOW_CENTER_HORIZONTAL = (((float) WINDOW_WIDTH / 2.0f) / GRAPHIC_SCALE);
        WINDOW_CENTER_VERTICAL = (((float) WINDOW_HEIGHT / 2.0f) / GRAPHIC_SCALE);

        //component setup
        MODEL = new Model();
        VIEW = new View();
        GRAPHIC_SCALE = Math.min((container.getWidth() / DRAW_SCALE_BY_CONTAINER_WIDTH_DIVISOR),
                                 (container.getHeight() / main.Globals.DRAW_SCALE_BY_CONTAINER_HEIGHT_DIVISOR));
        VIEW.setScale(GRAPHIC_SCALE);
        //end component setup

        //map setup
        String defaultMapName = MAP_HOOK_LIST[DEFAULT_MAP_ID];

        loadMap(defaultMapName);
        //end map setup

        //player setup
        InputStream inputStream = ResourceLoader.getResourceAsStream(DEFAULT_CHARACTER_IMAGE_PATH);
        Image playerBasic =
            new Image(inputStream, DEFAULT_CHARACTER_IMAGE_PATH, false, FILTER_NEAREST);

        VIEW.setupPlayerViewModel(playerBasic);

        loadPlayerGraphics();

        PLAYER_REF = DEFAULT_PLAYER_REF;
        MODEL.spawnActor(DEFAULT_PLAYER_REF, MAP_HOOK, true);
        MODEL.getMap().setHookSpawn(MAP_HOOK);
        //end player setup

        PLAYER_UPDATER = new PlayerUpdater(this, PLAYER_REF);
    }

    /**
     * Load in a TiledMap and map graphic from the map name. Update the MODEL with map logic
     * and pass the new map graphic to the VIEW.
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
        System.out.println(MAP_RESOURCE_PATH + loadMapName + "/" + loadMapName + TILED_MAP_EXTENSION);
        InputStream inputStream = ResourceLoader.getResourceAsStream(
            MAP_RESOURCE_PATH + loadMapName + "/" + loadMapName + TILED_MAP_EXTENSION);
        TiledMap tiledMap              = new TiledMap(inputStream, MAP_TILESET_PATH);
        int      tiledCollisionLayerId = tiledMap.getLayerIndex(TILED_COLLISION_LAYER_NAME);
        int      tiledReferenceLayerId = tiledMap.getLayerIndex(TILED_REFERENCE_LAYER_NAME);

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
                tileId = tiledMap.getTileId(x, y, tiledCollisionLayerId);
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

        MODEL.setupMapModel(tiledMap.getWidth(),
                            tiledMap.getHeight(),
                            tiledMap.getTileWidth() * GRAPHIC_TO_LOGIC_CONVERSION,
                            mapClip,
                            mapHooks);

        inputStream = ResourceLoader.getResourceAsStream(
            MAP_RESOURCE_PATH + loadMapName + MAP_GRAPHIC_DIRECTORY + loadMapName + MAP_GRAPHIC_FOREGROUND_POSTFIX +
            GRAPHICS_EXTENSION);
        Image mapForegroundImage =
            new Image(inputStream,
                      MAP_RESOURCE_PATH + loadMapName + MAP_GRAPHIC_DIRECTORY + loadMapName +
                      MAP_GRAPHIC_FOREGROUND_POSTFIX + GRAPHICS_EXTENSION,
                      false,
                      FILTER_NEAREST);

        VIEW.setMapForegroundImage(mapForegroundImage);

        inputStream = ResourceLoader.getResourceAsStream(
            MAP_RESOURCE_PATH + loadMapName + MAP_GRAPHIC_DIRECTORY + loadMapName + MAP_GRAPHIC_MIDGROUND_POSTFIX +
            GRAPHICS_EXTENSION);
        Image mapMidgroundImage =
            new Image(inputStream,
                      MAP_RESOURCE_PATH + loadMapName + MAP_GRAPHIC_DIRECTORY + loadMapName +
                      MAP_GRAPHIC_MIDGROUND_POSTFIX + GRAPHICS_EXTENSION,
                      false,
                      FILTER_NEAREST);

        VIEW.setMapMidgroundImage(mapMidgroundImage);

        inputStream = ResourceLoader.getResourceAsStream(
            MAP_RESOURCE_PATH + loadMapName + MAP_GRAPHIC_DIRECTORY + loadMapName + MAP_GRAPHIC_BACKGROUND_POSTFIX +
            GRAPHICS_EXTENSION);
        Image mapBackgroundImage =
            new Image(inputStream,
                      MAP_RESOURCE_PATH + loadMapName + MAP_GRAPHIC_DIRECTORY + loadMapName +
                      MAP_GRAPHIC_BACKGROUND_POSTFIX + GRAPHICS_EXTENSION,
                      false,
                      FILTER_NEAREST);

        VIEW.setMapBackgroundImage(mapBackgroundImage);

        inputStream = ResourceLoader.getResourceAsStream(
            MAP_RESOURCE_PATH + loadMapName + MAP_GRAPHIC_DIRECTORY + loadMapName + MAP_GRAPHIC_SKYBOX_POSTFIX +
            GRAPHICS_EXTENSION);
        Image mapSkyboxImage = new Image(inputStream,
                                         MAP_RESOURCE_PATH + loadMapName + MAP_GRAPHIC_DIRECTORY + loadMapName +
                                         MAP_GRAPHIC_SKYBOX_POSTFIX + GRAPHICS_EXTENSION,
                                         false,
                                         FILTER_NEAREST);

        VIEW.setMapSkyboxImage(mapSkyboxImage);

        MAP_HOOK = loadMapName;
        MODEL.getMap().setHookCurrent(MAP_HOOK);
        MODEL.spawnEntities();
    }

    /**
     * Load in all the raw graphics and construct image and animation objects to be used in
     * rendering the player, and pass them all to the VIEW which will actually use them.
     *
     * @throws SlickException Slick library exception.
     */
    private void loadPlayerGraphics() throws SlickException
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

        VIEW.setPlayerImageFaceLeft(playerImageFaceLeft);
        VIEW.setPlayerImageFaceRight(playerImageFaceRight);
        VIEW.setPlayerAnimationWalkLeft(playerAnimationWalkLeft);
        VIEW.setPlayerAnimationWalkRight(playerAnimationWalkRight);
        VIEW.setPlayerAnimationRunLeft(playerAnimationRunLeft);
        VIEW.setPlayerAnimationRunRight(playerAnimationRunRight);
        VIEW.setPlayerAnimationJumpLeft(playerAnimationJumpLeft);
        VIEW.setPlayerAnimationJumpRight(playerAnimationJumpRight);
        VIEW.setPlayerImageWallLeft(playerImageWallLeft);
        VIEW.setPlayerImageWallRight(playerImageWallRight);
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

        MODEL.spawnActor(PLAYER_REF, spawnHook, true);
        MODEL.getMap().setHookSpawn(spawnHook);
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
            GRAPHIC_SCALE = Math.min((WINDOW_WIDTH / DRAW_SCALE_BY_CONTAINER_WIDTH_DIVISOR),
                                     (WINDOW_HEIGHT / main.Globals.DRAW_SCALE_BY_CONTAINER_HEIGHT_DIVISOR));
            VIEW.setScale(GRAPHIC_SCALE);
            WINDOW_CENTER_HORIZONTAL = (((float) WINDOW_WIDTH / 2.0f) / GRAPHIC_SCALE);
            WINDOW_CENTER_VERTICAL = (((float) WINDOW_HEIGHT / 2.0f) / GRAPHIC_SCALE);
        }
        //end check window size change and update logic

        //VIEW updating
        //player
        Actor player                 = (Actor) MODEL.getEntityByRef(PLAYER_REF);
        long  playerX                = player.getX() + player.getGraphicOffsetX();
        long  playerY                = player.getY() + player.getGraphicOffsetY();
        long  playerDX               = player.getDX();
        long  playerDY               = player.getDY();
        long  playerWidth            = player.getWidth();
        long  playerHeight           = player.getHeight();
        long  playerMiddleHorizontal = playerX + (playerWidth / 2);
        long  playerMiddleVertical   = playerY + (playerHeight / 2);

        float playerRenderX =
            WINDOW_CENTER_HORIZONTAL - (((float) playerWidth / GRAPHIC_TO_LOGIC_CONVERSION) / 2.0f);
        float playerRenderY =
            WINDOW_CENTER_VERTICAL - (((float) playerHeight / GRAPHIC_TO_LOGIC_CONVERSION) / 2.0f);
        //end player

        //map draw position updating
        float mapRenderX = -(((float) playerX / GRAPHIC_TO_LOGIC_CONVERSION) +
                             (((float) playerWidth / GRAPHIC_TO_LOGIC_CONVERSION) / 2.0f) - WINDOW_CENTER_HORIZONTAL);
        float mapRenderY = -(((float) playerY / GRAPHIC_TO_LOGIC_CONVERSION) +
                             (((float) playerHeight / GRAPHIC_TO_LOGIC_CONVERSION) / 2.0f) - WINDOW_CENTER_VERTICAL);

        //check player too far left and correct for overscroll
        if ((playerMiddleHorizontal / GRAPHIC_TO_LOGIC_CONVERSION) < (WINDOW_CENTER_HORIZONTAL))
        {
            //System.out.println("Player too far left.");
            mapRenderX = 0;
            playerRenderX = ((float) playerX / (float) GRAPHIC_TO_LOGIC_CONVERSION);
        }

        //check player too far right and correct for overscroll
        if ((playerMiddleHorizontal / GRAPHIC_TO_LOGIC_CONVERSION) >
            ((MODEL.getMap().getWidth() * (MODEL.getMap().getTileWidth() / GRAPHIC_TO_LOGIC_CONVERSION)) -
             WINDOW_CENTER_HORIZONTAL))
        {
            //System.out.println("Player too far right.");
            mapRenderX = ((WINDOW_WIDTH / GRAPHIC_SCALE) -
                          (MODEL.getMap().getWidth() * (MODEL.getMap().getTileWidth() / GRAPHIC_TO_LOGIC_CONVERSION)));
            playerRenderX = ((WINDOW_WIDTH / GRAPHIC_SCALE) - (MODEL.getMap().getWidth() *
                                                               (MODEL.getMap().getTileWidth() /
                                                                GRAPHIC_TO_LOGIC_CONVERSION))) +
                            ((float) playerX / (float) GRAPHIC_TO_LOGIC_CONVERSION);
        }

        //check player too far up and correct for overscroll
        if ((playerMiddleVertical / GRAPHIC_TO_LOGIC_CONVERSION) < (WINDOW_CENTER_VERTICAL))
        {
            //System.out.println("Player too far up.");
            mapRenderY = 0;
            playerRenderY = ((float) playerY / (float) GRAPHIC_TO_LOGIC_CONVERSION);
        }

        //check player too far down and correct for overscroll
        if (((playerMiddleVertical / GRAPHIC_TO_LOGIC_CONVERSION) + 1) > //+1 to force round up
            ((MODEL.getMap().getHeight() * (MODEL.getMap().getTileWidth() / GRAPHIC_TO_LOGIC_CONVERSION)) -
             WINDOW_CENTER_VERTICAL))
        {
            //System.out.println("Player too far down.");
            mapRenderY = ((WINDOW_HEIGHT / GRAPHIC_SCALE) -
                          (MODEL.getMap().getHeight() * (MODEL.getMap().getTileWidth() / GRAPHIC_TO_LOGIC_CONVERSION)));
            playerRenderY = (((WINDOW_HEIGHT / GRAPHIC_SCALE) - (MODEL.getMap().getHeight() * (
                MODEL.getMap().getTileWidth() /
                GRAPHIC_TO_LOGIC_CONVERSION))) +
                             ((float) playerY / (float) GRAPHIC_TO_LOGIC_CONVERSION));
        }

        VIEW.setMapLocation(mapRenderX, mapRenderY);
        //end map draw position updating

        //player draw position updating
        VIEW.setPlayerLocation(playerRenderX, playerRenderY);
        //end player draw position updating

        //player graphic/animation updating
        if (player.isOnWallLeft() && playerDY >= 0)
        {
            VIEW.setPlayerGraphicIndex(wallLeft);
        }
        else if (player.isOnWallRight() && playerDY >= 0)
        {
            VIEW.setPlayerGraphicIndex(wallRight);
        }
        else if (playerDY < 0)
        {
            if ((player.isResetJump()) ||
                ((VIEW.getPlayerGraphicIndex() != jumpLeft) && (VIEW.getPlayerGraphicIndex() != jumpRight)))
            {
                player.setResetJump(false);
                VIEW.resetJump();
            }
            if (playerDX < 0)
            {
                VIEW.setPlayerGraphicIndex(jumpLeft);
            }
            else if (playerDX > 0)
            {
                VIEW.setPlayerGraphicIndex(jumpRight);
            }
            else if ((VIEW.getPlayerGraphicIndex() == runLeft) ||
                     (VIEW.getPlayerGraphicIndex() == faceLeft) || (VIEW.getPlayerGraphicIndex() == jumpLeft) ||
                     (VIEW.getPlayerGraphicIndex() == walkLeft) || (VIEW.getPlayerGraphicIndex() == wallLeft))
            {
                VIEW.setPlayerGraphicIndex(jumpLeft);
            }
            else
            {
                VIEW.setPlayerGraphicIndex(jumpRight);
            }
        }
        else if (playerDY > 0 || !MODEL.isActorCollisionDown(player))
        {
            if ((VIEW.getPlayerGraphicIndex() != jumpLeft) && (VIEW.getPlayerGraphicIndex() != jumpRight))
            {
                VIEW.setFall();
            }
            if (playerDX < 0)
            {
                VIEW.setPlayerGraphicIndex(jumpLeft);
            }
            else if (playerDX > 0)
            {
                VIEW.setPlayerGraphicIndex(jumpRight);
            }
            else if ((VIEW.getPlayerGraphicIndex() == runLeft) ||
                     (VIEW.getPlayerGraphicIndex() == faceLeft) || (VIEW.getPlayerGraphicIndex() == jumpLeft) ||
                     (VIEW.getPlayerGraphicIndex() == walkLeft) || (VIEW.getPlayerGraphicIndex() == wallLeft))
            {
                VIEW.setPlayerGraphicIndex(jumpLeft);
            }
            else
            {
                VIEW.setPlayerGraphicIndex(jumpRight);
            }
        }
        else if (playerDX > 0)
        {
            VIEW.setPlayerGraphicIndex(runRight);
        }
        else if (playerDX < 0)
        {
            VIEW.setPlayerGraphicIndex(runLeft);
        }
        else
        {
            if ((VIEW.getPlayerGraphicIndex() == runLeft) || (VIEW.getPlayerGraphicIndex() == walkLeft) ||
                (VIEW.getPlayerGraphicIndex() == wallLeft) || (VIEW.getPlayerGraphicIndex() == jumpLeft))
            {
                VIEW.setPlayerGraphicIndex(faceLeft);
            }
            else if ((VIEW.getPlayerGraphicIndex() == runRight) || (VIEW.getPlayerGraphicIndex() == walkRight) ||
                     (VIEW.getPlayerGraphicIndex() == wallRight) || (VIEW.getPlayerGraphicIndex() == jumpRight))
            {
                VIEW.setPlayerGraphicIndex(faceRight);
            }
        }
        //end player graphic/animation updating
        //end VIEW updating

        VIEW.draw(g);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException
    {
        //System.out.println(delta);

        PLAYER_UPDATER.update(MODEL, container.getInput());

        //example of requesting game state change, i.e. to the main menu or fight state
        /*if (false) {
            ActionEvent e = new ActionEvent(this, 0, String.valueOf(id));
            changeStateListener.actionPerformed(e);
        }*/
    }
}
