package overworld;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.tiled.TiledMap;

/**
 * overworld.View acts as the view for the overworld game state.
 *
 * @author Scorple
 * @version 1.0
 * @since 2016.08.01
 */
class View
{
    private float scale;

    //TODO: move these to a separate class
    private TiledMap tiledMap;
    private Image mapImage;
    private float mapX;
    private float mapY;

    //TODO: move these to a separate class
    private Image playerImage;
    private float playerX;
    private float playerY;

    /**
     * Default constructor for this view.
     */
    View()
    {

    }

    /**
     * Render the overworld view.
     *
     * @param g Graphics: The graphics context.
     */
    void draw(Graphics g)
    {
        g.setAntiAlias(false); //this is a pixel-art game, no anti-aliasing here

        g.scale(scale, scale); //TODO: this should be based on window size

        //because of the way scaling is handled the tiledmap needs to be drawn by location
        //and then the pixels are scaled up, this causes the map to move 1 * scale pixels at a time
        //this looks really bad at slow movement speeds and I don't know how to fix it
        //tiledMap.render(Math.round(mapX), Math.round(mapY), 1); //TODO: 1 is the foreground layer, should be a global

        //TODO: performance, only draw the part of the map on screen
        mapImage.draw(mapX, mapY);

        playerImage.draw(playerX, playerY);
    }

    /**
     * Set the scale to use for graphic drawing.
     *
     * @param scale float: The scale to use for drawing.
     */
    void setScale(float scale)
    {
        this.scale = scale;
    }

    /**
     * Set a reference to the current overworld map for drawing.
     *
     * @param tiledMap TiledMap: The source of the map to draw.
     *
     * @deprecated
     */
    void setupMapViewModel(TiledMap tiledMap)
    {
        this.tiledMap = tiledMap;
        this.mapX = 0;
        this.mapY = 0;
    }

    /**
     * Set the overworld map image to use for drawing.
     *
     * @param mapImage Image: The map to draw.
     */
    void setMapImage(Image mapImage)
    {
        this.mapImage = mapImage;
    }

    /**
     * Sets the coordinate to use for drawing the map.
     * This pair is the upper left corner of the map,
     * it is drawn right and down from this pair.
     *
     * @param mapX int: The X coordinate to draw the map.
     * @param mapY int: The Y coordinate to draw the map.
     */
    void setMapLocation(float mapX, float mapY)
    {
        this.mapX = mapX;
        this.mapY = mapY;
    }

    /**
     * Sets the player image to use for drawing,
     * and default draw location.
     *
     * @param playerImage Image: The still player character graphic.
     */
    void setupPlayerViewModel(Image playerImage)
    {
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
    void setPlayerLocation(float playerX, float playerY)
    {
        this.playerX = playerX;
        this.playerY = playerY;
    }
}
