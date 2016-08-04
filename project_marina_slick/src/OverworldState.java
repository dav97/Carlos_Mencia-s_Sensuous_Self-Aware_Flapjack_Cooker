import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
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
    OverworldModel overworldModel;
    OverworldView overworldView;
    private int id;
    private ActionListener changeStateListener;

    /**
     * State constructor. Stores reference parameters.
     *
     * @param id int: The numeric id of this state.
     * @param changeStateListener ActionListener: Callback for change state requests.
     */
    OverworldState(int id, ActionListener changeStateListener) {
        this.id = id;
        this.changeStateListener = changeStateListener;
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
        //component setup
        overworldModel = new OverworldModel();
        overworldView = new OverworldView();
        //end component setup

        //map setup
        TiledMap tiledMap = new TiledMap("res/debug/debug.tmx");

        System.out.println("Foreground layer id is <" + tiledMap.getLayerIndex("foreground") + ">");
        System.out.println("Reference layer id is <" + tiledMap.getLayerIndex("reflayer") + ">");

        Boolean mapClip[][] = new Boolean[tiledMap.getWidth()][tiledMap.getHeight()]; //true means passable, false means not passable
        String mapHooks[][] = new String[tiledMap.getWidth()][tiledMap.getHeight()];

        int tileId;
        int refTileId;
        String refTileHook;
        for (int x = 0; x < tiledMap.getWidth(); ++x) {
            for (int y = 0; y < tiledMap.getHeight(); ++y) {
                tileId = tiledMap.getTileId(x, y, 1); //TODO: 0 is the foreground layer, should be a global
                mapClip[x][y] = tiledMap.getTileProperty(tileId, "clip", "1").equals("1");
                //System.out.println("Tile at <" + x + ", " + y + "> is " + ((mapClip[x][y]) ? "CLIPPING" : "NO CLIPPING"));
                refTileId = tiledMap.getTileId(x, y, 0); //TODO: 1 is the reference layer, should be a global
                refTileHook = tiledMap.getTileProperty(refTileId, "hook", "");
                mapHooks[x][y] = refTileHook;
                if (!refTileHook.equals("")) {
                    System.out.println("Tile at <" + x + ", " + y + "> has reference hook " + refTileHook);
                }
            }
        }

        overworldModel.setupMapModel(tiledMap.getWidth(), tiledMap.getHeight(), tiledMap.getTileWidth(), mapClip, mapHooks);
        overworldView.setupMapViewModel(tiledMap);
        //end map setup

        //player setup
        //TODO: this should not be hard coded... or a still...
        Image playerBasic = new Image("res/Overworld Characters/PCs/Marina/mf.png", false, Image.FILTER_NEAREST);

        int playerWidth = playerBasic.getWidth(); //the logical player width is the raw graphic width
        int playerHeight = playerBasic.getHeight(); //the logical player height is the raw graphic height

        overworldModel.setupPlayerModel(playerWidth, playerHeight);
        overworldView.setupPlayerViewModel(playerBasic);

        overworldModel.spawnPlayer("spawn");
        overworldView.setPlayerLocation(overworldModel.getPlayerX(), overworldModel.getPlayerY());
        //end player setup
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


        //example of requesting game state change, i.e. to the main menu or fight state
        /*if (false) {
            ActionEvent e = new ActionEvent(this, 0, String.valueOf(id));
            changeStateListener.actionPerformed(e);
        }*/
    }
}
