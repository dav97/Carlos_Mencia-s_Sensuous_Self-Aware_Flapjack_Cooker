import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
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

    private Image playerImage;
    private int playerX, playerY;

    /**
     * Default constructor for this view.
     */
    OverworldView() {

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

        tiledMap.render(mapX, mapY, 1); //TODO: 0 is the foreground layer, should be a global
        playerImage.draw(playerX, playerY);
    }

    /**
     * Sets a reference to the current overworld map for drawing.
     *
     * @param tiledMap TiledMap: The source of the map to draw.
     */
    void setupMapViewModel(TiledMap tiledMap) {
        this.tiledMap = tiledMap;
        this.mapX = 0;
        this.mapY = 0;
    }

    /**
     * Sets the coordinate to use for drawing the map.
     * This pair is the upper left corner of the map,
     * it is drawn right and down from this pair.
     *
     * @param mapX int: The X coordinate to draw the map.
     * @param mapY int: The Y coordinate to draw the map.
     */
    void setMapLocation(int mapX, int mapY) {
        this.mapX = mapX;
        this.mapY = mapY;
    }

    /**
     * Sets the player image to use for drawing,
     * and default draw location.
     *
     * @param playerImage Image: The still player character graphic.
     */
    void setupPlayerViewModel(Image playerImage) {
        this.playerImage = playerImage;
        this.playerX = 0;
        this.playerY = 0;
    }

    /**
     * Updates the location to draw the player.
     *
     * @param playerX int: The X coordinate to draw the player.
     * @param playerY int: The Y coordinate to draw the player.
     */
    void setPlayerLocation(int playerX, int playerY) {
        this.playerX = playerX;
        this.playerY = playerY;
    }
}
