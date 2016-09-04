package overworld.view;

import org.newdawn.slick.Animation;
import overworld.Globals.NPCGraphicIndex;

import static overworld.Globals.NPCGraphicIndex.faceLeft;

/**
 * Created by Scorple on 9/4/2016.
 */
public class NPC extends Entity
{
    private NPCGraphicIndex graphicIndex;

    private Animation animationFaceLeft;
    private Animation animationFaceRight;
    private Animation animationWalkLeft;
    private Animation animationWalkRight;

    public NPC()
    {
        graphicIndex = faceLeft;

        animationFaceLeft = null;
        animationFaceRight = null;
        animationWalkLeft = null;
        animationWalkRight = null;
    }

    public NPCGraphicIndex getGraphicIndex()
    {
        return graphicIndex;
    }

    public void setGraphicIndex(NPCGraphicIndex graphicIndex)
    {
        this.graphicIndex = graphicIndex;
    }

    public Animation getGraphic()
    {
        switch (graphicIndex)
        {
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
}
