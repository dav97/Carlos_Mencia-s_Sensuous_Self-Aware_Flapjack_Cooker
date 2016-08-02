import org.newdawn.slick.Graphics;

/**
 * OverworldView acts as the view for the overworld game state.
 *
 * @author Scorple
 * @version 1.0
 * @since 2016.08.01
 */
class OverworldView {
    OverworldView() {

    }
    /**
     * Render the overworld view.
     *
     * @param g Graphics: The graphics context.
     */
    void draw(Graphics g) {
        g.setAntiAlias(false); //this is a pixel-art game, no anti-aliasing here

        //TODO: draw the map, player, enemies, etc.
    }
}
