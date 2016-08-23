package overworld;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.tiled.TiledMap;

import static overworld.Globals.PlayerGraphicIndex;
import static overworld.Globals.PlayerGraphicIndex.faceFront;

/**
 * overworld.View holds and renders all the graphical elements for the overworld game state,
 * updated regularly by the overworld presenter.
 *
 * @author Scorple
 * @version dev01
 * @since 2016_0801
 */
class View
{
    private float scale;

    //TODO: move these to a separate class
    private TiledMap tiledMap;
    private Image    mapImage;
    private Image    mapForegroundImage;
    private Image    mapMidgroundImage;
    private Image    mapBackgroundImage;
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

        g.scale(scale, scale);

        //TODO: performance, only draw the part of the map on screen
        mapBackgroundImage.draw(mapX, mapY);

        mapMidgroundImage.draw(mapX, mapY);

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

        mapForegroundImage.draw(mapX, mapY);
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
     * Set the overworld map image to use for drawing.
     *
     * @param mapImage Image: The map to draw.
     */
    void setMapImage(Image mapImage)
    {
        this.mapImage = mapImage;
    }

    void setMapForegroundImage(Image mapForegroundImage)
    {
        this.mapForegroundImage = mapForegroundImage;
    }

    void setMapMidgroundImage(Image mapMidgroundImage)
    {
        this.mapMidgroundImage = mapMidgroundImage;
    }

    void setMapBackgroundImage(Image mapBackgroundImage)
    {
        this.mapBackgroundImage = mapBackgroundImage;
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
     * Set the default player image to use for drawing,
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

    /**
     * Set the image to use when the player is standing still and facing left.
     * (i.e. the faceLeft case)
     *
     * @param playerImageFaceLeft Image: The image to use when the player is standing still and facing left.
     */
    void setPlayerImageFaceLeft(Image playerImageFaceLeft)
    {
        this.playerImageFaceLeft = playerImageFaceLeft;
    }

    /**
     * Set the image to use when the player is standing still and facing right.
     * (i.e. the faceRight case)
     *
     * @param playerImageFaceRight Image: The image to use when the player is standing still and facing right.
     */
    void setPlayerImageFaceRight(Image playerImageFaceRight)
    {
        this.playerImageFaceRight = playerImageFaceRight;
    }

    /**
     * Set the animation to use when the player is walking left.
     * (i.e. the walkLeft case)
     * Not currently used.
     *
     * @param playerAnimationWalkLeft Animation: The animation to use when the player is walking left.
     */
    void setPlayerAnimationWalkLeft(Animation playerAnimationWalkLeft)
    {
        this.playerAnimationWalkLeft = playerAnimationWalkLeft;
    }

    /**
     * Set the animation to use when the player is walking right.
     * (i.e. the walkRight case)
     * Not currently used.
     *
     * @param playerAnimationWalkRight Animation: The animation to use when the player is walking right.
     */
    void setPlayerAnimationWalkRight(Animation playerAnimationWalkRight)
    {
        this.playerAnimationWalkRight = playerAnimationWalkRight;
    }

    /**
     * Set the animation to use when the player is running left.
     * (i.e. the runLeft case)
     *
     * @param playerAnimationRunLeft Animation: The animation to use when the player is running left.
     */
    void setPlayerAnimationRunLeft(Animation playerAnimationRunLeft)
    {
        this.playerAnimationRunLeft = playerAnimationRunLeft;
    }

    /**
     * Set the animation to use when the player is running right.
     * (i.e. the runRight case)
     *
     * @param playerAnimationRunRight Animation: The animation to use when the player is running right.
     */
    void setPlayerAnimationRunRight(Animation playerAnimationRunRight)
    {
        this.playerAnimationRunRight = playerAnimationRunRight;
    }

    /**
     * Set the animation to use when the player is jumping (or falling) left.
     * (i.e. the jumpLeft case)
     *
     * @param playerAnimationJumpLeft Animation: The animation to use when the player is jumping (or falling) left.
     */
    void setPlayerAnimationJumpLeft(Animation playerAnimationJumpLeft)
    {
        this.playerAnimationJumpLeft = playerAnimationJumpLeft;
    }

    /**
     * Set the animation to use when the player is jumping (or falling) right.
     * (i.e. the jumpRight case)
     *
     * @param playerAnimationJumpRight Animation: The animation to use when the player is jumping (or falling) right.
     */
    void setPlayerAnimationJumpRight(Animation playerAnimationJumpRight)
    {
        this.playerAnimationJumpRight = playerAnimationJumpRight;
    }

    /**
     * Restart the player jump animation since that animation does not loop.
     */
    void resetJump()
    {
        playerAnimationJumpLeft.restart();
        playerAnimationJumpRight.restart();
    }

    /**
     * Set the jump animation to its final frame since that frame is used for falls.
     */
    void setFall()
    {
        playerAnimationJumpLeft.setCurrentFrame(playerAnimationJumpLeft.getFrameCount() - 1);
        playerAnimationJumpRight.setCurrentFrame(playerAnimationJumpLeft.getFrameCount() - 1);
    }

    /**
     * Set the image to use when the player is on the left wall.
     * (i.e. the wallLeft case)
     *
     * @param playerImageWallLeft Image: The image to use when the player is on the left wall.
     */
    void setPlayerImageWallLeft(Image playerImageWallLeft)
    {
        this.playerImageWallLeft = playerImageWallLeft;
    }

    /**
     * Set the image to use when the player is on the right wall.
     * (i.e. the wallRight case)
     *
     * @param playerImageWallRight Image: The image to use when the player is on the right wall.
     */
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

    /**
     * Get the current player graphic identifier, sometimes needed to decide which animation
     * to use next.
     *
     * @return PlayerGraphicIndex: The current player graphic identifier.
     */
    PlayerGraphicIndex getPlayerGraphicIndex()
    {
        return playerGraphicIndex;
    }

    /**
     * Set the current player graphic identifier.
     *
     * @param playerGraphicIndex PlayerGraphicIndex: The new current player graphic identifier.
     */
    void setPlayerGraphicIndex(PlayerGraphicIndex playerGraphicIndex)
    {
        this.playerGraphicIndex = playerGraphicIndex;
    }
}
