package overworld;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.tiled.TiledMap;

import static overworld.Globals.PlayerGraphicIndex;
import static overworld.Globals.PlayerGraphicIndex.faceFront;

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
    private Image    mapImage;
    private float    mapX;
    private float    mapY;

    private PlayerGraphicIndex playerGraphicIndex;

    //TODO: move these to a separate class
    //private int playerGraphicIndex;
    private Image     playerImage;
    private Image     playerImageFaceLeft;
    private Image     playerImageFaceRight;
    private Animation playerAnimationWalkLeft;
    private Animation playerAnimationWalkRight;
    private Animation playerAnimationRunLeft;
    private Animation playerAnimationRunRight;
    private Animation playerAnimationJumpLeft;
    private Animation playerAnimationJumpRight;
    private Image     playerImageWallLeft;
    private Image     playerImageWallRight;
    private float     playerX;
    private float     playerY;

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

        //playerImage.draw(playerX, playerY);
        switch (playerGraphicIndex)
        {
            case faceFront:
                playerImage.draw(playerX, playerY);
                break;
            case faceLeft:
                playerImageFaceLeft.draw(playerX, playerY);
                break;
            case faceRight:
                playerImageFaceRight.draw(playerX, playerY);
                break;
            case walkLeft:
                playerAnimationWalkLeft.draw(playerX, playerY);
                break;
            case walkRight:
                playerAnimationWalkRight.draw(playerX, playerY);
                break;
            case runLeft:
                playerAnimationRunLeft.draw(playerX, playerY);
                break;
            case runRight:
                playerAnimationRunRight.draw(playerX, playerY);
                break;
            case jumpLeft:
                playerAnimationJumpLeft.draw(playerX, playerY);
                break;
            case jumpRight:
                playerAnimationJumpRight.draw(playerX, playerY);
                break;
            case wallLeft:
                playerImageWallLeft.draw(playerX, playerY);
                break;
            case wallRight:
                playerImageWallRight.draw(playerX, playerY);
                break;
        }
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
        this.playerGraphicIndex = faceFront;
    }

    void setPlayerImageFaceLeft(Image playerImageFaceLeft)
    {
        this.playerImageFaceLeft = playerImageFaceLeft;
    }

    void setPlayerImageFaceRight(Image playerImageFaceRight)
    {
        this.playerImageFaceRight = playerImageFaceRight;
    }

    void setPlayerAnimationWalkLeft(Animation playerAnimationWalkLeft)
    {
        this.playerAnimationWalkLeft = playerAnimationWalkLeft;
    }

    void setPlayerAnimationWalkRight(Animation playerAnimationWalkRight)
    {
        this.playerAnimationWalkRight = playerAnimationWalkRight;
    }

    void setPlayerAnimationRunLeft(Animation playerAnimationRunLeft)
    {
        this.playerAnimationRunLeft = playerAnimationRunLeft;
    }

    void setPlayerAnimationRunRight(Animation playerAnimationRunRight)
    {
        this.playerAnimationRunRight = playerAnimationRunRight;
    }

    void setPlayerAnimationJumpLeft(Animation playerAnimationJumpLeft)
    {
        this.playerAnimationJumpLeft = playerAnimationJumpLeft;
    }

    void resetJump()
    {
        playerAnimationJumpLeft.restart();
        playerAnimationJumpRight.restart();
    }

    void setFall()
    {
        playerAnimationJumpLeft.setCurrentFrame(playerAnimationJumpLeft.getFrameCount() - 1);
        playerAnimationJumpRight.setCurrentFrame(playerAnimationJumpLeft.getFrameCount() - 1);
    }

    void setPlayerAnimationJumpRight(Animation playerAnimationJumpRight)
    {
        this.playerAnimationJumpRight = playerAnimationJumpRight;
    }

    void setPlayerImageWallLeft(Image playerImageWallLeft)
    {
        this.playerImageWallLeft = playerImageWallLeft;
    }

    void setPlayerImageWallRight(Image playerImageWallRight)
    {
        this.playerImageWallRight = playerImageWallRight;
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

    PlayerGraphicIndex getPlayerGraphicIndex()
    {
        return playerGraphicIndex;
    }

    void setPlayerGraphicIndex(PlayerGraphicIndex playerGraphicIndex)
    {
        this.playerGraphicIndex = playerGraphicIndex;
    }
}
