package overworld;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

import static overworld.Globals.ACTOR_PROPERTIES_RESOURCE_PATH;
import static overworld.Globals.XML_EXTENTION;

/**
 * Created by Scorple on 8/22/2016.
 */
public class Actor extends Entity
{
    long dX;
    long dY;

    boolean onWallLeft;
    boolean onWallRight;

    long inputDDX;
    long inputMaxDX;

    long instantaneousJumpDY;
    long instantaneousWallJumpDX;
    long instantaneousWallJumpDY;

    long gravityDDY;
    long gravityMaxDY;
    long gravityMaxWallDY;

    boolean resetJump;

    long graphicOffsetX;
    long graphicOffsetY;

    Actor(long x, long y, String ref)
    {
        super(x, y);
        dX = 0;
        dY = 0;
        onWallLeft = false;
        onWallRight = false;
        resetJump = false;

        getActorStats(ref);
    }

    private void getActorStats(String ref)
    {
        SAXBuilder saxBuilder = new SAXBuilder();
        InputStream inputStream = ResourceLoader.getResourceAsStream(
            ACTOR_PROPERTIES_RESOURCE_PATH + ref + XML_EXTENTION);
        Document document = null;

        try
        {
            document = saxBuilder.build(inputStream);
        }
        catch (JDOMException | IOException e)
        {
            e.printStackTrace();
        }

        Element actorElement = document.getRootElement();

        Element generalElement = actorElement.getChild("general");

        setWidth(Long.parseLong(generalElement.getChildText("width")));
        setHeight(Long.parseLong(generalElement.getChildText("height")));

        graphicOffsetX = Long.parseLong(generalElement.getChildText("graphicOffsetX"));
        graphicOffsetY = Long.parseLong(generalElement.getChildText("graphicOffsetY"));

        Element movementElement = actorElement.getChild("movement");

        inputDDX = Long.parseLong(movementElement.getChildText("inputDDX"));
        inputMaxDX = Long.parseLong(movementElement.getChildText("inputMaxDX"));

        instantaneousJumpDY = Long.parseLong(movementElement.getChildText("instantaneousJumpDY"));
        instantaneousWallJumpDX = Long.parseLong(movementElement.getChildText("instantaneousWallJumpDX"));
        instantaneousWallJumpDY = Long.parseLong(movementElement.getChildText("instantaneousWallJumpDY"));

        gravityDDY = Long.parseLong(movementElement.getChildText("gravityDDY"));
        gravityMaxDY = Long.parseLong(movementElement.getChildText("gravityMaxDY"));
        gravityMaxWallDY = Long.parseLong(movementElement.getChildText("gravityMaxWallDY"));
    }

    public long getDX()
    {
        return dX;
    }

    public void setDX(long dX)
    {
        this.dX = dX;
    }

    public long getDY()
    {
        return dY;
    }

    public void setDY(long dY)
    {
        this.dY = dY;
    }

    public boolean isOnWallLeft()
    {
        return onWallLeft;
    }

    public void setOnWallLeft(boolean onWallLeft)
    {
        this.onWallLeft = onWallLeft;
    }

    public boolean isOnWallRight()
    {
        return onWallRight;
    }

    public void setOnWallRight(boolean onWallRight)
    {
        this.onWallRight = onWallRight;
    }

    public long getInputDDX()
    {
        return inputDDX;
    }

    public void setInputDDX(long inputDDX)
    {
        this.inputDDX = inputDDX;
    }

    public long getInputMaxDX()
    {
        return inputMaxDX;
    }

    public void setInputMaxDX(long inputMaxDX)
    {
        this.inputMaxDX = inputMaxDX;
    }

    public long getInstantaneousJumpDY()
    {
        return instantaneousJumpDY;
    }

    public void setInstantaneousJumpDY(long instantaneousJumpDY)
    {
        this.instantaneousJumpDY = instantaneousJumpDY;
    }

    public long getInstantaneousWallJumpDX()
    {
        return instantaneousWallJumpDX;
    }

    public void setInstantaneousWallJumpDX(long instantaneousWallJumpDX)
    {
        this.instantaneousWallJumpDX = instantaneousWallJumpDX;
    }

    public long getInstantaneousWallJumpDY()
    {
        return instantaneousWallJumpDY;
    }

    public void setInstantaneousWallJumpDY(long instantaneousWallJumpDY)
    {
        this.instantaneousWallJumpDY = instantaneousWallJumpDY;
    }

    public long getGravityDDY()
    {
        return gravityDDY;
    }

    public void setGravityDDY(long gravityDDY)
    {
        this.gravityDDY = gravityDDY;
    }

    public long getGravityMaxDY()
    {
        return gravityMaxDY;
    }

    public void setGravityMaxDY(long gravityMaxDY)
    {
        this.gravityMaxDY = gravityMaxDY;
    }

    public long getGravityMaxWallDY()
    {
        return gravityMaxWallDY;
    }

    public void setGravityMaxWallDY(long gravityMaxWallDY)
    {
        this.gravityMaxWallDY = gravityMaxWallDY;
    }

    public boolean isResetJump()
    {
        return resetJump;
    }

    public void setResetJump(boolean resetJump)
    {
        this.resetJump = resetJump;
    }

    public long getGraphicOffsetX()
    {
        return graphicOffsetX;
    }

    public void setGraphicOffsetX(long graphicOffsetX)
    {
        this.graphicOffsetX = graphicOffsetX;
    }

    public long getGraphicOffsetY()
    {
        return graphicOffsetY;
    }

    public void setGraphicOffsetY(long graphicOffsetY)
    {
        this.graphicOffsetY = graphicOffsetY;
    }
}
