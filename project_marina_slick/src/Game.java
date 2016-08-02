import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Game will act as the parent for the rest of the app.
 *
 * @author Scorple
 * @version 1.0
 * @since 2016.08.01
 */
class Game extends StateBasedGame {
    private OverworldState overworldState;

    private ChangeStateListener changeStateListener;

    /**
     * Create a new state based game.
     *
     * @param name String: The name of the game.
     */
    public Game(String name) {
        super(name);
    }

    @Override
    public void initStatesList(GameContainer container) throws SlickException {
        int stateId = 0; //id should be passed to each game state and post-fix iterated
        changeStateListener = new ChangeStateListener(); //passed into each game state

        overworldState = new OverworldState(stateId++, changeStateListener);
        addState(overworldState);

        //we will start in the "overworld" state for testing and development
        //TODO: add and start in main menu or splash screen state
        enterState(overworldState.getID());
    }

    /**
     * Private listener class for a state requests to change state.
     */
    private class ChangeStateListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent changeStateRequest) {
            int newStateId = Integer.parseInt(changeStateRequest.getActionCommand());

            enterState(newStateId);
        }
    }
}
