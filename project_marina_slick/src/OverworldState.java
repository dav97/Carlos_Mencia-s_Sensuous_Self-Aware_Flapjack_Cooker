import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.tiled.TiledMap;

import java.awt.event.ActionListener;

/**
 * OverworldState will act as the presenter for the overworld game state, mediating between the model and view.
 * Post updates to the view. Post updates to and request data from the model.
 *
 * @author Scorple
 * @version 1.0
 * @since 2016.08.01
 */
class OverworldState extends BasicGameState {
    private static int WINDOW_WIDTH;
    private static int WINDOW_HEIGHT;
    private static float WINDOW_CENTER_HORIZONTAL;
    private static float WINDOW_CENTER_VERTIAL;
    private static String MAP_HOOK;
    //private final ActionListener changeStateListener; //TODO: needs redoing
    private final int id;
    private OverworldModel overworldModel;
    private OverworldView overworldView;
    private float scale = 6;
    //private boolean staleJumpInput = false; //not sure about this one
    private boolean staleUseInput = false; //TODO: move this, and player input update handling, to a class

    /**
     * State constructor. Stores reference parameters.
     *
     * @param id int: The numeric id of this state.
     * @param changeStateListener ActionListener: Callback for change state requests.
     */
    OverworldState(int id, ActionListener changeStateListener) {
        this.id = id;
        //this.changeStateListener = changeStateListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getID() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        WINDOW_WIDTH = container.getWidth();
        WINDOW_HEIGHT = container.getHeight();
        WINDOW_CENTER_HORIZONTAL = ((WINDOW_WIDTH / 2) / scale);
        WINDOW_CENTER_VERTIAL = ((WINDOW_HEIGHT / 2) / scale);

        //component setup
        overworldModel = new OverworldModel();
        overworldView = new OverworldView();
        scale = Math.min((container.getWidth() / Globals.DRAW_SCALE_BY_CONTAINER_WIDTH_DIVISOR), (container.getHeight() / Globals.DRAW_SCALE_BY_CONTAINER_HEIGHT_DIVISOR));
        overworldView.setScale(scale);
        //end component setup

        //map setup
        String defaultMapName = OverworldGlobals.MAP_HOOK_LIST[OverworldGlobals.DEFAULT_MAP_ID];

        loadMap(defaultMapName);
        //end map setup

        //player setup
        Image playerBasic = new Image(OverworldGlobals.DEFAULT_CHARACTER_IMAGE_PATH, false, Image.FILTER_NEAREST);

        int playerWidth = playerBasic.getWidth(); //the logical player width is the raw graphic width
        int playerHeight = playerBasic.getHeight(); //the logical player height is the raw graphic height

        overworldModel.setupPlayerModel(playerWidth, playerHeight);
        overworldView.setupPlayerViewModel(playerBasic);

        overworldModel.spawnPlayer(OverworldGlobals.TILED_HOOK_PROPERTY_SPAWN);

        overworldModel.setDDXDueToInput(OverworldGlobals.STANDARD_DDX_DUE_TO_INPUT);
        overworldModel.setMaxDXDueToInput(OverworldGlobals.STANDARD_MAX_DX_DUE_TO_INPUT);
        overworldModel.setInstantaneousJumpDY(OverworldGlobals.STANDARD_INSTANTANEOUS_JUMP_DY);
        overworldModel.setDDYDueToGravity(OverworldGlobals.STANDARD_DDY_DUE_TO_GRAVITY);
        overworldModel.setMaxDYDueToGravity(OverworldGlobals.STANDARD_MAX_DY_DUE_TO_GRAVITY);
        //end player setup
    }

    /**
     * Load in a TiledMap and map graphic from the map name. Update the model with map logic
     * and pass the new map graphic to the view.
     *
     * @param loadMapName String: The name of the map to load. Should always match one of the
     *                    strings in OverworldGlobals.MAP_HOOK_LIST. The Tiled map and graphic
     *                    files should be stored in OverworldGlobals.MAP_RESOURCE_PATH. Graphic
     *                    should have extension OverworldGlobals.GRAPHIC_EXTENSION.
     *
     * @throws SlickException Slick library exception.
     */
    private void loadMap(String loadMapName) throws SlickException {
        TiledMap tiledMap = new TiledMap(OverworldGlobals.MAP_RESOURCE_PATH +
                loadMapName + OverworldGlobals.TILED_MAP_EXTENSION);
        int tiledForegroundLayerId = tiledMap.getLayerIndex(OverworldGlobals.TILED_FOREGROUND_LAYER_NAME);
        int tiledReferenceLayerId = tiledMap.getLayerIndex(OverworldGlobals.TILED_REFERENCE_LAYER_NAME);

        Boolean mapClip[][] = new Boolean[tiledMap.getWidth()][tiledMap.getHeight()]; //true means passable, false means not passable
        String mapHooks[][] = new String[tiledMap.getWidth()][tiledMap.getHeight()];

        int tileId;
        int refTileId;
        String refTileHook;
        for (int x = 0; x < tiledMap.getWidth(); ++x) {
            for (int y = 0; y < tiledMap.getHeight(); ++y) {
                tileId = tiledMap.getTileId(x, y, tiledForegroundLayerId);
                mapClip[x][y] = tiledMap.getTileProperty(tileId, OverworldGlobals.TILED_CLIP_PROPERTY_NAME, OverworldGlobals.TILED_CLIP_PROPERTY_ENABLED).equals(OverworldGlobals.TILED_CLIP_PROPERTY_ENABLED);
                refTileId = tiledMap.getTileId(x, y, tiledReferenceLayerId);
                refTileHook = tiledMap.getTileProperty(refTileId, OverworldGlobals.TILED_HOOK_PROPERTY_NAME, OverworldGlobals.TILED_HOOK_PROPERTY_DEFAULT);
                mapHooks[x][y] = refTileHook;
                if (!refTileHook.equals(OverworldGlobals.TILED_HOOK_PROPERTY_DEFAULT)) {
                    System.out.println("Tile at <" + x + ", " + y + "> has reference hook <" + refTileHook + ">");
                }
            }
        }

        overworldModel.setupMapModel(tiledMap.getWidth(), tiledMap.getHeight(), tiledMap.getTileWidth(), mapClip, mapHooks);

        Image tiledMapImage = new Image(OverworldGlobals.MAP_RESOURCE_PATH +
                loadMapName + OverworldGlobals.GRAPHICS_EXTENSION,
                false, Image.FILTER_NEAREST);

        overworldView.setMapImage(tiledMapImage);

        MAP_HOOK = loadMapName;
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
    private void transitionMap(String newMapHook, String spawnHook) throws SlickException {
        loadMap(newMapHook);

        overworldModel.spawnPlayer(spawnHook);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        overworldView.draw(g);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        //TODO: split this method up

        if (container.getWidth() != WINDOW_WIDTH || container.getHeight() != WINDOW_HEIGHT) {
            WINDOW_WIDTH = container.getWidth();
            WINDOW_HEIGHT = container.getHeight();
            WINDOW_CENTER_HORIZONTAL = ((WINDOW_WIDTH / 2) / scale);
            WINDOW_CENTER_VERTIAL = ((WINDOW_HEIGHT / 2) / scale);
            scale = Math.min((container.getWidth() / Globals.DRAW_SCALE_BY_CONTAINER_WIDTH_DIVISOR), (container.getHeight() / Globals.DRAW_SCALE_BY_CONTAINER_HEIGHT_DIVISOR));
            overworldView.setScale(scale);
        }

        float playerX = overworldModel.getPlayerX();
        float playerY = overworldModel.getPlayerY();
        float playerDX = overworldModel.getPlayerDX();

        //example of updating player location/
        //crummy but functional player controls for testing purposes
        float proposedPlayerDX = 0;
        float proposedPlayerDY;

        //gravity
        //if the player dy is less than the max dy due to gravity, add to it
        if (overworldModel.getPlayerDY() < overworldModel.getMaxDYDueToGravity()) {
            proposedPlayerDY = overworldModel.getPlayerDY() + overworldModel.getDDYDueToGravity();
        } else {
            proposedPlayerDY = overworldModel.getPlayerDY();
        }
        //end gravity

        //horizontal player movement fade
        //if it's close enough to 0, just make it 0 - was experiencing a floating point error otherwise
        if ((playerDX < OverworldGlobals.STANDARD_DX_FADE_SANITY_BOUND) &&
                (playerDX > -(OverworldGlobals.STANDARD_DX_FADE_SANITY_BOUND))) {
            proposedPlayerDX = 0;
        } else if (playerDX > 0) {
            proposedPlayerDX = playerDX - overworldModel.getDDXDueToInput();
        } else if (playerDX < 0) {
            proposedPlayerDX = playerDX + overworldModel.getDDXDueToInput();
        }
        //end horizontal player movement fade

        //player input
        //jump
        if (container.getInput().isKeyDown(Input.KEY_W)
            //&& !staleJumpInput
                ) {
            if (overworldModel.getVerticalCollisionDistanceByDY(overworldModel.getMaxDXDueToInput()) == 0) { //if the player has solid ground beneath her
                //staleJumpInput = true;
                proposedPlayerDY = overworldModel.getInstantaneousJumpDY();
            }
        }
        /*if (!container.getInput().isKeyDown(Input.KEY_W)) {
            staleJumpInput = false;
        }*/
        //move right
        if (container.getInput().isKeyDown(Input.KEY_A)) {
            if (playerDX > -(overworldModel.getMaxDXDueToInput())) {
                proposedPlayerDX = playerDX - (overworldModel.getDDXDueToInput());
            } else {
                proposedPlayerDX = playerDX;
            }
        }
        /*if (container.getInput().isKeyDown(Input.KEY_S)) {
            //not yet implemented
        }*/
        //move left
        if (container.getInput().isKeyDown(Input.KEY_D)) {
            if (playerDX < overworldModel.getMaxDXDueToInput()) {
                proposedPlayerDX = playerDX + overworldModel.getDDXDueToInput();
            } else {
                proposedPlayerDX = playerDX;
            }
        }
        //"use", currently disallowed while jumping or falling
        if ((container.getInput().isKeyDown(Input.KEY_E)) &&
                (overworldModel.getPlayerDY() == 0) &&
                !staleUseInput) {
            staleUseInput = true;
            String hooks[] = overworldModel.getIntersectingTileHooks();
            //String feedback = "Intersecting tile hooks:";

            outerLoop:
            //TODO: naming loops to break multiple layers is discouraged, revisit
            for (String hook : hooks) {
                if (!hook.equals("")) {
                    for (int i_map_id = 0; i_map_id < OverworldGlobals.MAP_HOOK_LIST.length; ++i_map_id) {
                        if (hook.equals(OverworldGlobals.MAP_HOOK_LIST[i_map_id])) {
                            System.out.println("Valid map hook found: <" + hook + ">");
                            System.out.println("Transitioning to <" + hook + "> from <" + MAP_HOOK + ">");
                            transitionMap(hook, MAP_HOOK);
                            break outerLoop;
                        }
                    }
                    //feedback += " " + hooks[i_hook];
                }
            }
            //System.out.println(feedback);
        }
        if (!container.getInput().isKeyDown(Input.KEY_E)) {
            staleUseInput = false;
        }
        //end player input

        //update player movement and location
        //if the proposed player dx is non-zero, check for collisions and use the collision distance for setting dx
        if (proposedPlayerDX != 0) {
            float adjustedDX = overworldModel.getHorizontalCollisionDistanceByDX(proposedPlayerDX);

            overworldModel.setPlayerDX(adjustedDX);
        } else {
            overworldModel.setPlayerDX(proposedPlayerDX);
        }

        //if the proposed player dy is non-zero, check for collisions and use the collision distance for setting dy
        if (proposedPlayerDY != 0) {
            float adjustedDY = overworldModel.getVerticalCollisionDistanceByDY(proposedPlayerDY);

            overworldModel.setPlayerDY(adjustedDY);
        } else {
            overworldModel.setPlayerDY(proposedPlayerDY);
        }

        overworldModel.setPlayerX(overworldModel.getPlayerX() + overworldModel.getPlayerDX());
        overworldModel.setPlayerY(overworldModel.getPlayerY() + overworldModel.getPlayerDY());
        //end update player movement and location

        //view updating
        float playerWidth = overworldModel.getPlayerWidth();
        float playerHeight = overworldModel.getPlayerHeight();

        float mapX = -(playerX + (playerWidth / 2) - WINDOW_CENTER_HORIZONTAL);
        float mapY = -(playerY + (playerHeight / 2) - WINDOW_CENTER_VERTIAL);

        overworldView.setMapLocation(mapX, mapY);

        float centeredPlayerX = WINDOW_CENTER_HORIZONTAL - (playerWidth / 2);
        float centeredPlayerY = WINDOW_CENTER_VERTIAL - (playerHeight / 2);

        overworldView.setPlayerLocation(centeredPlayerX, centeredPlayerY);
        //end view updating

        //example of requesting game state change, i.e. to the main menu or fight state
        /*if (false) {
            ActionEvent e = new ActionEvent(this, 0, String.valueOf(id));
            changeStateListener.actionPerformed(e);
        }*/
    }
}
