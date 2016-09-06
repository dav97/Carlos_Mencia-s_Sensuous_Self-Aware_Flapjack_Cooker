package overworld;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.ResourceLoader;
import overworld.model.Model;
import overworld.view.View;

import java.awt.event.ActionListener;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static main.Globals.DRAW_SCALE_BY_CONTAINER_WIDTH_DIVISOR;
import static org.newdawn.slick.Image.FILTER_NEAREST;
import static overworld.Globals.ActorGraphicIndex.*;
import static overworld.Globals.*;

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
    private static int                         WINDOW_WIDTH;
    private static int                         WINDOW_HEIGHT;
    private static float                       WINDOW_CENTER_HORIZONTAL;
    private static float                       WINDOW_CENTER_VERTICAL;
    private static String                      MAP_HOOK;
    private static String                      PLAYER_REF;
    //private final ActionListener changeStateListener; //TODO: needs redoing
    private final  int                         STATE_ID;
    private        ActorHandler                PLAYER_HANDLER;
    private        HashMap<String, NPCHandler> NPC_HANDLER_MAP;
    private        Model                       MODEL;
    private        View                        VIEW;

    private float GRAPHIC_SCALE = 6f;

    /**
     * State constructor. Stores reference parameters.
     *
     * @param STATE_ID            int: The numeric STATE_ID of this state.
     * @param changeStateListener ActionListener: Callback for change state requests.
     */
    public Presenter(int STATE_ID, ActionListener changeStateListener)
    {
        this.STATE_ID = STATE_ID;
        //this.changeStateListener = changeStateListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getID()
    {
        return STATE_ID;
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
        PLAYER_REF = DEFAULT_PLAYER_REF;
        PLAYER_HANDLER = new ActorHandler(this, PLAYER_REF);
        MODEL.spawnActor(PLAYER_REF, MAP_HOOK, true);
        loadPlayerGraphics();
        MODEL.getMap().setHookSpawn(MAP_HOOK);
        //end player setup
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
        if (NPC_HANDLER_MAP != null)
        {
            NPC_HANDLER_MAP.clear();
        }
        else
        {
            NPC_HANDLER_MAP = new HashMap<>();
        }

        System.out.println(MAP_RESOURCE_PATH + loadMapName + TILED_MAP_EXTENSION);
        InputStream inputStream = ResourceLoader.getResourceAsStream(
            MAP_RESOURCE_PATH + loadMapName + TILED_MAP_EXTENSION);
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

                    String[] splitRef = refTileHook.split("\\.");

                    if (splitRef.length > 1)
                    {
                        if (splitRef[0].equals("npc"))
                        {
                            System.out.println("Creating NPC Handler");

                            NPCHandler npcHandler = new NPCHandler(this,
                                                                   MODEL,
                                                                   VIEW,
                                                                   splitRef[1],
                                                                   x * tiledMap.getTileWidth() *
                                                                   GRAPHIC_TO_LOGIC_CONVERSION,
                                                                   y * tiledMap.getTileWidth() *
                                                                   GRAPHIC_TO_LOGIC_CONVERSION);
                            NPC_HANDLER_MAP.put(splitRef[1], npcHandler);
                        }
                    }
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
        //MODEL.spawnEntities();
    }

    /**
     * Load in all the raw graphics and construct image and animation objects to be used in
     * rendering the player, and pass them all to the VIEW which will actually use them.
     *
     * @throws SlickException Slick library exception.
     */
    private void loadPlayerGraphics() throws SlickException
    {
        InputStream          inputStream;
        overworld.view.Actor actor = new overworld.view.Actor();

        inputStream = ResourceLoader.getResourceAsStream(
            PLAYER_GRAPHIC_PATH_WALK + PLAYER_GRAPHIC_PREFIX_LEFT +
            PLAYER_GRAPHIC_POSTFIX_WALK + "1" + GRAPHICS_EXTENSION);
        Image[] playerFramesFaceLeft = {new Image(inputStream,
                                                  PLAYER_GRAPHIC_PATH_WALK + PLAYER_GRAPHIC_PREFIX_LEFT +
                                                  PLAYER_GRAPHIC_POSTFIX_WALK + "1" +
                                                  GRAPHICS_EXTENSION,
                                                  false,
                                                  FILTER_NEAREST)};
        Animation playerAnimationFaceLeft = new Animation(playerFramesFaceLeft, 1, false);

        inputStream = ResourceLoader.getResourceAsStream(
            PLAYER_GRAPHIC_PATH_WALK + PLAYER_GRAPHIC_PREFIX_RIGHT +
            PLAYER_GRAPHIC_POSTFIX_WALK + "1" + GRAPHICS_EXTENSION);
        Image[] playerFramesFaceRight = {new Image(inputStream,
                                                   PLAYER_GRAPHIC_PATH_WALK +
                                                   PLAYER_GRAPHIC_PREFIX_RIGHT +
                                                   PLAYER_GRAPHIC_POSTFIX_WALK + "1" +
                                                   GRAPHICS_EXTENSION,
                                                   false,
                                                   FILTER_NEAREST)};
        Animation playerAnimationFaceRight = new Animation(playerFramesFaceRight, 1, false);

        Image[] playerFramesWalkLeft = new Image[PLAYER_GRAPHIC_FRAME_COUNT_WALK];
        for (int i = 0; i < PLAYER_GRAPHIC_FRAME_COUNT_WALK; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHIC_PATH_WALK + PLAYER_GRAPHIC_PREFIX_LEFT +
                PLAYER_GRAPHIC_POSTFIX_WALK + (i + 1) + GRAPHICS_EXTENSION);
            playerFramesWalkLeft[i] = new Image(inputStream,
                                                PLAYER_GRAPHIC_PATH_WALK +
                                                PLAYER_GRAPHIC_PREFIX_LEFT +
                                                PLAYER_GRAPHIC_POSTFIX_WALK + (i + 1) +
                                                GRAPHICS_EXTENSION,
                                                false,
                                                FILTER_NEAREST);
        }
        Animation playerAnimationWalkLeft =
            new Animation(playerFramesWalkLeft, PLAYER_GRAPHIC_FRAME_DURATION_WALK, true);
        playerAnimationWalkLeft.setLooping(true);
        playerAnimationWalkLeft.setPingPong(true);

        Image[] playerFramesWalkRight = new Image[PLAYER_GRAPHIC_FRAME_COUNT_WALK];
        for (int i = 0; i < PLAYER_GRAPHIC_FRAME_COUNT_WALK; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHIC_PATH_WALK + PLAYER_GRAPHIC_PREFIX_RIGHT +
                PLAYER_GRAPHIC_POSTFIX_WALK + (i + 1) + GRAPHICS_EXTENSION);
            playerFramesWalkRight[i] = new Image(inputStream,
                                                 PLAYER_GRAPHIC_PATH_WALK +
                                                 PLAYER_GRAPHIC_PREFIX_RIGHT +
                                                 PLAYER_GRAPHIC_POSTFIX_WALK + (i + 1) +
                                                 GRAPHICS_EXTENSION,
                                                 false,
                                                 FILTER_NEAREST);
        }
        Animation playerAnimationWalkRight =
            new Animation(playerFramesWalkRight, PLAYER_GRAPHIC_FRAME_DURATION_WALK, true);
        playerAnimationWalkRight.setLooping(true);
        playerAnimationWalkRight.setPingPong(true);

        Image[] playerFramesRunLeft = new Image[PLAYER_GRAPHIC_FRAME_COUNT_RUN];
        for (int i = 0; i < PLAYER_GRAPHIC_FRAME_COUNT_RUN; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHIC_PATH_RUN + PLAYER_GRAPHIC_PREFIX_LEFT +
                PLAYER_GRAPHIC_POSTFIX_RUN + (i + 1) + GRAPHICS_EXTENSION);
            playerFramesRunLeft[i] = new Image(inputStream,
                                               PLAYER_GRAPHIC_PATH_RUN +
                                               PLAYER_GRAPHIC_PREFIX_LEFT +
                                               PLAYER_GRAPHIC_POSTFIX_RUN + (i + 1) +
                                               GRAPHICS_EXTENSION,
                                               false,
                                               FILTER_NEAREST);
        }
        Animation playerAnimationRunLeft =
            new Animation(playerFramesRunLeft, PLAYER_GRAPHIC_FRAME_DURATION_RUN, true);
        playerAnimationRunLeft.setLooping(true);
        playerAnimationRunLeft.setPingPong(true);

        Image[] playerFramesRunRight = new Image[PLAYER_GRAPHIC_FRAME_COUNT_RUN];
        for (int i = 0; i < PLAYER_GRAPHIC_FRAME_COUNT_RUN; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHIC_PATH_RUN + PLAYER_GRAPHIC_PREFIX_RIGHT +
                PLAYER_GRAPHIC_POSTFIX_RUN + (i + 1) + GRAPHICS_EXTENSION);
            playerFramesRunRight[i] = new Image(inputStream,
                                                PLAYER_GRAPHIC_PATH_RUN +
                                                PLAYER_GRAPHIC_PREFIX_RIGHT +
                                                PLAYER_GRAPHIC_POSTFIX_RUN + (i + 1) +
                                                GRAPHICS_EXTENSION,
                                                false,
                                                FILTER_NEAREST);
        }
        Animation playerAnimationRunRight =
            new Animation(playerFramesRunRight, PLAYER_GRAPHIC_FRAME_DURATION_RUN, true);
        playerAnimationRunRight.setLooping(true);
        playerAnimationRunRight.setPingPong(true);

        Image[] playerFramesJumpLeft = new Image[PLAYER_GRAPHIC_FRAME_COUNT_JUMP];
        for (int i = 0; i < PLAYER_GRAPHIC_FRAME_COUNT_JUMP; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHIC_PATH_JUMP + PLAYER_GRAPHIC_PREFIX_LEFT + PLAYER_GRAPHIC_POSTFIX_JUMP + (i + 1) +
                GRAPHICS_EXTENSION);
            playerFramesJumpLeft[i] = new Image(inputStream,
                                                PLAYER_GRAPHIC_PATH_JUMP + PLAYER_GRAPHIC_PREFIX_LEFT +
                                                PLAYER_GRAPHIC_POSTFIX_JUMP + (i + 1) +
                                                GRAPHICS_EXTENSION,
                                                false,
                                                FILTER_NEAREST);
        }
        Animation playerAnimationJumpLeft =
            new Animation(playerFramesJumpLeft, PLAYER_GRAPHIC_FRAME_DURATION_JUMP, true);
        playerAnimationJumpLeft.setLooping(false);

        Image[] playerFramesJumpRight = new Image[PLAYER_GRAPHIC_FRAME_COUNT_JUMP];
        for (int i = 0; i < PLAYER_GRAPHIC_FRAME_COUNT_JUMP; ++i)
        {
            inputStream = ResourceLoader.getResourceAsStream(
                PLAYER_GRAPHIC_PATH_JUMP + PLAYER_GRAPHIC_PREFIX_RIGHT + PLAYER_GRAPHIC_POSTFIX_JUMP + (i + 1) +
                GRAPHICS_EXTENSION);
            playerFramesJumpRight[i] = new Image(inputStream,
                                                 PLAYER_GRAPHIC_PATH_JUMP + PLAYER_GRAPHIC_PREFIX_RIGHT +
                                                 PLAYER_GRAPHIC_POSTFIX_JUMP + (i + 1) +
                                                 GRAPHICS_EXTENSION,
                                                 false,
                                                 FILTER_NEAREST);
        }
        Animation playerAnimationJumpRight =
            new Animation(playerFramesJumpRight, PLAYER_GRAPHIC_FRAME_DURATION_JUMP, true);
        playerAnimationJumpRight.setLooping(false);

        inputStream = ResourceLoader.getResourceAsStream(
            PLAYER_GRAPHIC_PATH_WALL + PLAYER_GRAPHIC_PRE_PREFIX_WALL +
            PLAYER_GRAPHIC_PREFIX_LEFT + GRAPHICS_EXTENSION);
        Image[] playerFramesWallLeft = {new Image(inputStream,
                                                  PLAYER_GRAPHIC_PATH_WALL +
                                                  PLAYER_GRAPHIC_PRE_PREFIX_WALL +
                                                  PLAYER_GRAPHIC_PREFIX_LEFT +
                                                  GRAPHICS_EXTENSION,
                                                  false,
                                                  FILTER_NEAREST)};
        Animation playerAnimationWallLeft = new Animation(playerFramesWallLeft, 1, false);

        inputStream = ResourceLoader.getResourceAsStream(
            PLAYER_GRAPHIC_PATH_WALL + PLAYER_GRAPHIC_PRE_PREFIX_WALL +
            PLAYER_GRAPHIC_PREFIX_RIGHT + GRAPHICS_EXTENSION);
        Image[] playerFramesWallRight = {new Image(inputStream,
                                                   PLAYER_GRAPHIC_PATH_WALL +
                                                   PLAYER_GRAPHIC_PRE_PREFIX_WALL +
                                                   PLAYER_GRAPHIC_PREFIX_RIGHT +
                                                   GRAPHICS_EXTENSION,
                                                   false,
                                                   FILTER_NEAREST)};
        Animation playerAnimationWallRight = new Animation(playerFramesWallRight, 1, false);

        actor.setAnimationFaceLeft(playerAnimationFaceLeft);
        actor.setAnimationFaceRight(playerAnimationFaceRight);
        actor.setAnimationWalkLeft(playerAnimationWalkLeft);
        actor.setAnimationWalkRight(playerAnimationWalkRight);
        actor.setAnimationRunLeft(playerAnimationRunLeft);
        actor.setAnimationRunRight(playerAnimationRunRight);
        actor.setAnimationJumpLeft(playerAnimationJumpLeft);
        actor.setAnimationJumpRight(playerAnimationJumpRight);
        actor.setAnimationWallLeft(playerAnimationWallLeft);
        actor.setAnimationWallRight(playerAnimationWallRight);

        System.out.println("Adding actor ref:<" + PLAYER_REF + "> to View");

        VIEW.addEntity(PLAYER_REF, actor);
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
        overworld.model.Actor player                 = (overworld.model.Actor) MODEL.getEntityByRef(PLAYER_REF);
        long                  playerX                = player.getX() + player.getGraphicOffsetX();
        long                  playerY                = player.getY() + player.getGraphicOffsetY();
        long                  playerDX               = player.getDX();
        long                  playerDY               = player.getDY();
        long                  playerWidth            = player.getWidth();
        long                  playerHeight           = player.getHeight();
        long                  playerMiddleHorizontal = playerX + (playerWidth / 2);
        long                  playerMiddleVertical   = playerY + (playerHeight / 2);

        //        System.out.println(playerX);
        //        System.out.println(playerY);

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
        VIEW.setEntityLocation(PLAYER_REF, playerRenderX, playerRenderY);
        //end player draw position updating

        //player graphic/animation updating
        if (player.isOnWallLeft() && playerDY >= 0)
        {
            VIEW.setActorGraphicIndex(PLAYER_REF, wallLeft);
        }
        else if (player.isOnWallRight() && playerDY >= 0)
        {
            VIEW.setActorGraphicIndex(PLAYER_REF, wallRight);
        }
        else if (playerDY < 0)
        {
            if ((player.isResetJump()) ||
                ((VIEW.getActorGraphicIndex(PLAYER_REF) != jumpLeft) &&
                 (VIEW.getActorGraphicIndex(PLAYER_REF) != jumpRight)))
            {
                player.setResetJump(false);
                VIEW.resetActorAnimation(PLAYER_REF);
            }
            if (playerDX < 0)
            {
                VIEW.setActorGraphicIndex(PLAYER_REF, jumpLeft);
            }
            else if (playerDX > 0)
            {
                VIEW.setActorGraphicIndex(PLAYER_REF, jumpRight);
            }
            else if ((VIEW.getActorGraphicIndex(PLAYER_REF) == runLeft) ||
                     (VIEW.getActorGraphicIndex(PLAYER_REF) == faceLeft) ||
                     (VIEW.getActorGraphicIndex(PLAYER_REF) == jumpLeft) ||
                     (VIEW.getActorGraphicIndex(PLAYER_REF) == walkLeft) ||
                     (VIEW.getActorGraphicIndex(PLAYER_REF) == wallLeft))
            {
                VIEW.setActorGraphicIndex(PLAYER_REF, jumpLeft);
            }
            else
            {
                VIEW.setActorGraphicIndex(PLAYER_REF, jumpRight);
            }
        }
        else if (playerDY > 0 || !MODEL.isActorCollisionDown(player))
        {
            if ((VIEW.getActorGraphicIndex(PLAYER_REF) != jumpLeft) &&
                (VIEW.getActorGraphicIndex(PLAYER_REF) != jumpRight))
            {
                VIEW.setActorFall(PLAYER_REF);
            }
            if (playerDX < 0)
            {
                VIEW.setActorGraphicIndex(PLAYER_REF, jumpLeft);
            }
            else if (playerDX > 0)
            {
                VIEW.setActorGraphicIndex(PLAYER_REF, jumpRight);
            }
            else if ((VIEW.getActorGraphicIndex(PLAYER_REF) == runLeft) ||
                     (VIEW.getActorGraphicIndex(PLAYER_REF) == faceLeft) ||
                     (VIEW.getActorGraphicIndex(PLAYER_REF) == jumpLeft) ||
                     (VIEW.getActorGraphicIndex(PLAYER_REF) == walkLeft) ||
                     (VIEW.getActorGraphicIndex(PLAYER_REF) == wallLeft))
            {
                VIEW.setActorGraphicIndex(PLAYER_REF, jumpLeft);
            }
            else
            {
                VIEW.setActorGraphicIndex(PLAYER_REF, jumpRight);
            }
        }
        else if (playerDX > 0)
        {
            VIEW.setActorGraphicIndex(PLAYER_REF, runRight);
        }
        else if (playerDX < 0)
        {
            VIEW.setActorGraphicIndex(PLAYER_REF, runLeft);
        }
        else
        {
            if ((VIEW.getActorGraphicIndex(PLAYER_REF) == runLeft) ||
                (VIEW.getActorGraphicIndex(PLAYER_REF) == walkLeft) ||
                (VIEW.getActorGraphicIndex(PLAYER_REF) == wallLeft) ||
                (VIEW.getActorGraphicIndex(PLAYER_REF) == jumpLeft))
            {
                VIEW.setActorGraphicIndex(PLAYER_REF, faceLeft);
            }
            else if ((VIEW.getActorGraphicIndex(PLAYER_REF) == runRight) ||
                     (VIEW.getActorGraphicIndex(PLAYER_REF) == walkRight) ||
                     (VIEW.getActorGraphicIndex(PLAYER_REF) == wallRight) ||
                     (VIEW.getActorGraphicIndex(PLAYER_REF) == jumpRight))
            {
                VIEW.setActorGraphicIndex(PLAYER_REF, faceRight);
            }
        }
        //end player graphic/animation updating
        //end VIEW updating

        for (Map.Entry<String, NPCHandler> entry : NPC_HANDLER_MAP.entrySet())
        {
            NPCHandler npcHandler = entry.getValue();
            npcHandler.updateView(mapRenderX, mapRenderY);
        }

        VIEW.draw(g);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException
    {
        //System.out.println(delta);

        PLAYER_HANDLER.update(MODEL, container.getInput());

        //example of requesting game state change, i.e. to the main menu or fight state
        /*if (false) {
            ActionEvent e = new ActionEvent(this, 0, String.valueOf(STATE_ID));
            changeStateListener.actionPerformed(e);
        }*/
    }
}
