import org.newdawn.slick.Graphics;
import org.newdawn.slick.tiled.TiledMap;

/**
 * OverworldView acts as the view for the overworld game state.
 *
 * @author Scorple
 * @version 1.0
 * @since 2016.08.01
 */
class OverworldView {
    //TODO: move these to a separate class
    private TiledMap tiledMap;
    private int mapX, mapY;

    /**
     * Default constructor for this view.
     */
    OverworldView() {
        mapX = 0;
        mapY = 0;
    }
    /**
     * Render the overworld view.
     *
     * @param g Graphics: The graphics context.
     */
    void draw(Graphics g) {
        g.setAntiAlias(false); //this is a pixel-art game, no anti-aliasing here
        g.scale(6.0f, 6.0f); //TODO: this should be based on window size

        //TODO: draw the map, player, enemies, etc.

        tiledMap.render(mapX, mapY);
    }

    /**
     * Sets a reference to the current overworld map for drawing.
     *
     * @param tiledMap TiledMap: The source of the map to draw.
     */
    void setMap(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
    }

    /**
     * Sets the coordinate to use for drawing the map.
     * This pair is the upper left corner of the map,
     * it is drawn right and down from this pair.
     *
     * @param mapX int: The X coordinate to draw the map at.
     * @param mapY int: The Y coordinate to draw the map at.
     */
    void setMapLocation(int mapX, int mapY) {
        this.mapX = mapX;
        this.mapY = mapY;
    }
}
