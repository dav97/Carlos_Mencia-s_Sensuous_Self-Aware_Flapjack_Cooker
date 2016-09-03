package overworld.view;

import org.newdawn.slick.Animation;
import overworld.Globals.ActorGraphicIndex;

import static overworld.Globals.ActorGraphicIndex.faceFront;

/**
 * Created by Scorple on 8/30/2016.
 */
public class Actor extends Entity
{
    private ActorGraphicIndex graphicIndex;

    private Animation animationFaceLeft;
    private Animation animationFaceRight;
    private Animation animationWalkLeft;
    private Animation animationWalkRight;
    private Animation animationRunLeft;
    private Animation animationRunRight;
    private Animation animationJumpLeft;
    private Animation animationJumpRight;
    private Animation animationWallLeft;
    private Animation animationWallRight;

    public Actor()
    {
        graphicIndex = faceFront;

        animationFaceLeft = null;
        animationFaceRight = null;
        animationWalkLeft = null;
        animationWalkRight = null;
        animationRunLeft = null;
        animationRunRight = null;
        animationJumpLeft = null;
        animationJumpRight = null;
        animationWallLeft = null;
        animationWallRight = null;
    }

    public Animation getGraphic()
    {
        switch (graphicIndex)
        {
            case faceFront:
                return getDefaultGraphic();
            case faceLeft:
                if (animationFaceLeft != null)
                {
                    return animationFaceLeft;
                }
                break;
            case faceRight:
                if (animationFaceRight != null)
                {
                    return animationFaceRight;
                }
                break;
            case walkLeft:
                if (animationWalkLeft != null)
                {
                    return animationWalkLeft;
                }
                break;
            case walkRight:
                if (animationWalkRight != null)
                {
                    return animationWalkRight;
                }
                break;
            case runLeft:
                if (animationRunLeft != null)
                {
                    return animationRunLeft;
                }
                break;
            case runRight:
                if (animationRunRight != null)
                {
                    return animationRunRight;
                }
                break;
            case jumpLeft:
                if (animationJumpLeft != null)
                {
                    return animationJumpLeft;
                }
                break;
            case jumpRight:
                if (animationJumpRight != null)
                {
                    return animationJumpRight;
                }
                break;
            case wallLeft:
                if (animationWallLeft != null)
                {
                    return animationWallLeft;
                }
                break;
            case wallRight:
                if (animationWallRight != null)
                {
                    return animationWallRight;
                }
                break;
            default:
                return getDefaultGraphic();
        }

        return getDefaultGraphic();
    }

    public void setAnimationFaceLeft(Animation animationFaceLeft)
    {
        this.animationFaceLeft = animationFaceLeft;
    }

    public void setAnimationFaceRight(Animation animationFaceRight)
    {
        this.animationFaceRight = animationFaceRight;
    }

    public void setAnimationWalkLeft(Animation animationWalkLeft)
    {
        this.animationWalkLeft = animationWalkLeft;
    }

    public void setAnimationWalkRight(Animation animationWalkRight)
    {
        this.animationWalkRight = animationWalkRight;
    }

    public void setAnimationRunLeft(Animation animationRunLeft)
    {
        this.animationRunLeft = animationRunLeft;
    }

    public void setAnimationRunRight(Animation animationRunRight)
    {
        this.animationRunRight = animationRunRight;
    }

    public void setAnimationJumpLeft(Animation animationJumpLeft)
    {
        this.animationJumpLeft = animationJumpLeft;
    }

    public void setAnimationJumpRight(Animation animationJumpRight)
    {
        this.animationJumpRight = animationJumpRight;
    }

    public void setAnimationWallLeft(Animation animationWallLeft)
    {
        this.animationWallLeft = animationWallLeft;
    }

    public void setAnimationWallRight(Animation animationWallRight)
    {
        this.animationWallRight = animationWallRight;
    }
}
