package overworld;

import org.lwjgl.opengl.Display;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.event.ActionListener;
import java.io.InputStream;

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
    private static int WINDOW_WIDTH;
    private static int WINDOW_HEIGHT;
    private static float WINDOW_CENTER_HORIZONTAL;
    private static float WINDOW_CENTER_VERTIAL;
    private static String MAP_HOOK;
    //private final ActionListener changeStateListener; //TODO: needs redoing
    private final int id;
    private Model model;
    private PlayerUpdater playerUpdater;
    private View view;
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
        scale = Math.min((container.getWidth() / main.Globals.DRAW_SCALE_BY_CONTAINER_WIDTH_DIVISOR),
                         (container.getHeight() / main.Globals.DRAW_SCALE_BY_CONTAINER_HEIGHT_DIVISOR));
        view.setScale(scale);
        //end component setup

        //map setup
        String defaultMapName = Globals.MAP_HOOK_LIST[Globals.DEFAULT_MAP_ID];

        loadMap(defaultMapName);
        //end map setup

        //player setup
        InputStream inputStream = ResourceLoader.getResourceAsStream(Globals.DEFAULT_CHARACTER_IMAGE_PATH);
        Image playerBasic =
            new Image(inputStream, Globals.DEFAULT_CHARACTER_IMAGE_PATH, false, Image.FILTER_NEAREST);

        long playerWidth = playerBasic.getWidth() *
            Globals.GRAPHIC_TO_LOGIC_CONVERSION; //the logical player width is the raw graphic width
        long playerHeight = playerBasic.getHeight() *
            Globals.GRAPHIC_TO_LOGIC_CONVERSION; //the logical player height is the raw graphic height

        model.setupPlayerModel(playerWidth, playerHeight);
        view.setupPlayerViewModel(playerBasic);

        model.spawnPlayer(Globals.TILED_HOOK_PROPERTY_SPAWN);

        model.setDDXDueToInput(Globals.STANDARD_DDX_DUE_TO_INPUT);
        model.setMaxDXDueToInput(Globals.STANDARD_MAX_DX_DUE_TO_INPUT);
        model.setInstantaneousJumpDY(Globals.STANDARD_INSTANTANEOUS_JUMP_DY);
        model.setInstantaneousWallJumpDY(Globals.STANDARD_INSTANTANEOUS_WALL_JUMP_DY);
        model.setInstantaneousWallJumpLeftDX(Globals.STANDARD_INSTANTANEOUS_WALL_JUMP_LEFT_DX);
        model.setInstantaneousWallJumpRightDX(Globals.STANDARD_INSTANTANEOUS_WALL_JUMP_RIGHT_DX);
        model.setDDYDueToGravity(Globals.STANDARD_DDY_DUE_TO_GRAVITY);
        model.setMaxDYDueToGravity(Globals.STANDARD_MAX_DY_DUE_TO_GRAVITY);
        model.setMaxDYOnWall(Globals.STANDARD_MAX_DY_ON_WALL);
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
        System.out.println(Globals.MAP_RESOURCE_PATH + loadMapName + Globals.TILED_MAP_EXTENSION);
        InputStream inputStream = ResourceLoader.getResourceAsStream(
            Globals.MAP_RESOURCE_PATH + loadMapName + Globals.TILED_MAP_EXTENSION);
        TiledMap tiledMap = new TiledMap(inputStream, Globals.MAP_TILESET_PATH);
        int tiledForegroundLayerId = tiledMap.getLayerIndex(Globals.TILED_FOREGROUND_LAYER_NAME);
        int tiledReferenceLayerId = tiledMap.getLayerIndex(Globals.TILED_REFERENCE_LAYER_NAME);

        Boolean mapClip[][] =
            new Boolean[tiledMap.getWidth()][tiledMap.getHeight()]; //true means passable, false means not passable
        String mapHooks[][] = new String[tiledMap.getWidth()][tiledMap.getHeight()];

        int tileId;
        int refTileId;
        String refTileHook;
        for (int x = 0; x < tiledMap.getWidth(); ++x)
        {
            for (int y = 0; y < tiledMap.getHeight(); ++y)
            {
                tileId = tiledMap.getTileId(x, y, tiledForegroundLayerId);
                mapClip[x][y] = tiledMap.getTileProperty(tileId,
                                                         Globals.TILED_CLIP_PROPERTY_NAME,
                                                         Globals.TILED_CLIP_PROPERTY_ENABLED)
                                        .equals(Globals.TILED_CLIP_PROPERTY_ENABLED);
                refTileId = tiledMap.getTileId(x, y, tiledReferenceLayerId);
                refTileHook = tiledMap.getTileProperty(refTileId,
                                                       Globals.TILED_HOOK_PROPERTY_NAME,
                                                       Globals.TILED_HOOK_PROPERTY_DEFAULT);
                mapHooks[x][y] = refTileHook;
                if (!refTileHook.equals(Globals.TILED_HOOK_PROPERTY_DEFAULT))
                {
                    System.out.println("Tile at <" + x + ", " + y + "> has reference hook <" + refTileHook + ">");
                }
            }
        }

        model.setupMapModel(tiledMap.getWidth(),
                            tiledMap.getHeight(),
                            tiledMap.getTileWidth() * Globals.GRAPHIC_TO_LOGIC_CONVERSION,
                            mapClip,
                            mapHooks);

        inputStream = ResourceLoader.getResourceAsStream(
            Globals.MAP_RESOURCE_PATH + loadMapName + Globals.GRAPHICS_EXTENSION);
        Image tiledMapImage =
            new Image(inputStream,
                      Globals.MAP_RESOURCE_PATH + loadMapName + Globals.GRAPHICS_EXTENSION,
                      false,
                      Image.FILTER_NEAREST);

        view.setMapImage(tiledMapImage);

        MAP_HOOK = loadMapName;
        model.setMapHookCurrent(MAP_HOOK);
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
            scale = Math.min((WINDOW_WIDTH / main.Globals.DRAW_SCALE_BY_CONTAINER_WIDTH_DIVISOR),
                             (WINDOW_HEIGHT / main.Globals.DRAW_SCALE_BY_CONTAINER_HEIGHT_DIVISOR));
            view.setScale(scale);
            WINDOW_CENTER_HORIZONTAL = (((float) WINDOW_WIDTH / 2.0f) / scale);
            WINDOW_CENTER_VERTIAL = (((float) WINDOW_HEIGHT / 2.0f) / scale);
        }
        //end check window size change and update logic

        //view updating
        long playerX = model.getPlayerX();
        long playerY = model.getPlayerY();
        long playerWidth = model.getPlayerWidth();
        long playerHeight = model.getPlayerHeight();

        float mapX = -(((float) playerX / Globals.GRAPHIC_TO_LOGIC_CONVERSION) +
            (((float) playerWidth / Globals.GRAPHIC_TO_LOGIC_CONVERSION) / 2.0f) - WINDOW_CENTER_HORIZONTAL);
        float mapY = -(((float) playerY / Globals.GRAPHIC_TO_LOGIC_CONVERSION) +
            (((float) playerHeight / Globals.GRAPHIC_TO_LOGIC_CONVERSION) / 2.0f) - WINDOW_CENTER_VERTIAL);

        view.setMapLocation(mapX, mapY);

        float centeredPlayerX =
            WINDOW_CENTER_HORIZONTAL - (((float) playerWidth / Globals.GRAPHIC_TO_LOGIC_CONVERSION) / 2.0f);
        float centeredPlayerY =
            WINDOW_CENTER_VERTIAL - (((float) playerHeight / Globals.GRAPHIC_TO_LOGIC_CONVERSION) / 2.0f);

        view.setPlayerLocation(centeredPlayerX, centeredPlayerY);
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
