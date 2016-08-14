package main;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import overworld.Presenter;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * main.Game will act as the parent for the rest of the app.
 *
 * @author Scorple
 * @version dev01
 * @since 2016_0801
 */
class Game extends StateBasedGame
{
    private Presenter presenter;

    private ChangeStateListener changeStateListener;

    /**
     * Create a new state based game.
     *
     * @param name String: The name of the game.
     */
    public Game(String name)
    {
        super(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initStatesList(GameContainer container) throws SlickException
    {
        int stateId = 0; //id should be passed to each game state and post-fix iterated
        changeStateListener = new ChangeStateListener(); //passed into each game state

        presenter = new Presenter(stateId++, changeStateListener);
        addState(presenter);

        //we will start in the "overworld" state for testing and development
        //TODO: add and start in main menu or splash screen state
        enterState(presenter.getID());
    }

    /**
     * Private listener class for a state requests to change state.
     */
    private class ChangeStateListener implements ActionListener
    {
        /**
         * {@inheritDoc}
         */
        @Override
        public void actionPerformed(ActionEvent changeStateRequest)
        {
            int newStateId = Integer.parseInt(changeStateRequest.getActionCommand());

            enterState(newStateId);
        }
    }
}
