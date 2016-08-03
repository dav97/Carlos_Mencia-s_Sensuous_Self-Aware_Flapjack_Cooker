import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
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
        overworldModel = new OverworldModel();
        overworldView = new OverworldView();

        TiledMap tiledMap = new TiledMap("res/debug/debug.tmx");
        Boolean mapClip[][] = new Boolean[tiledMap.getWidth()][tiledMap.getHeight()]; //true means passable, false means not passable

        int tileId;
        for (int x = 0; x < tiledMap.getWidth(); ++x) {
            for (int y = 0; y < tiledMap.getHeight(); ++y) {
                tileId = tiledMap.getTileId(x, y, 0);
                mapClip[x][y] = tiledMap.getTileProperty(tileId, "clip", "1").equals("1");
            }
        }

        overworldModel.setMapModel(tiledMap.getWidth(), tiledMap.getHeight(), mapClip);
        overworldView.setMap(tiledMap);
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
