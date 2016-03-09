package main;

import gameObjects.Stage;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import utils.Globals;

/**
 * Created by Rick on 10/29/2015.
 */
public class Overworld extends BasicGameState {
    private int delta;
    private int mouseX;
    private int mouseY;
    private int SCALE = Globals.SCALE;

    private static Stage stage;

    @Override
    public int getID() {
        return 1;
    }

    @Override
    public void init(GameContainer container, StateBasedGame game) throws SlickException {
        stage = new Stage();
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
        g.setAntiAlias(false);

        g.drawString(Integer.toString(delta), 10, 30);
        g.drawString(Integer.toString(mouseX) + ", " + Integer.toString(mouseY), 10, 50);

        g.scale(SCALE, SCALE);
        stage.draw(g);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
        this.delta += delta;

        Input input = container.getInput();

        mouseX = input.getMouseX();
        mouseY = input.getMouseY();

        if (input.isKeyDown(Input.KEY_ESCAPE)) {
            container.exit();
        }

        if (this.delta > 0) {
            this.delta = 0;
            stage.update(input);
        }
    }
}
