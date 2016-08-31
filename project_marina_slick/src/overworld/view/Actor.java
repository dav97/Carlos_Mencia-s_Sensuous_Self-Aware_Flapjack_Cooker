package overworld.view;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import overworld.Globals;

/**
 * Created by Scorple on 8/30/2016.
 */
public class Actor extends Entity
{
    private Globals.PlayerGraphicIndex playerGraphicIndex;

    private Image     imageFaceLeft;
    private Image     imageFaceRight;
    private Animation animationWalkLeft;
    private Animation animationWalkRight;
    private Animation animationRunLeft;
    private Animation animationRunRight;
    private Animation animationJumpLeft;
    private Animation animationJumpRight;
    private Image     imageWallLeft;
    private Image     imageWallRight;
}
