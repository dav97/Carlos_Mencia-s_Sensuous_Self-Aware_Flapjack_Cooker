package overworld;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.newdawn.slick.util.ResourceLoader;

import java.io.IOException;
import java.io.InputStream;

import static overworld.Globals.ACTOR_PROPERTIES_RESOURCE_PATH;
import static overworld.Globals.XML_EXTENSION;

/**
 * Each overworld.Actor object will store the data associated with one actor present
 * in the Overworld. This includes the Player, Enemies and other NPCs.
 *
 * @author scorple
 * @version dev04
 * @since 2016_0822
 */
class Actor extends Entity
{
    private long dX;
    private long dY;

    private boolean onWallLeft;
    private boolean onWallRight;

    private long inputDDX;
    private long inputMaxDX;

    private long instantaneousJumpDY;
    private long instantaneousWallJumpDX;
    private long instantaneousWallJumpDY;

    private long gravityDDY;
    private long gravityMaxDY;
    private long gravityMaxWallDY;

    private boolean resetJump;

    private long graphicOffsetX;
    private long graphicOffsetY;

    /**
     * Primary constructor. Requires the initial logical x and y coordinates of this Actor
     * and a reference to load in the remainder of the Actor's properties from an .xml file.
     * Sets some logic defaults and requests property population from .xml file.
     *
     * @param x   long: The initial x coordinate of this Actor.
     * @param y   long: The initial y coordinate of this Actor.
     * @param ref String: A reference tag for loading the rest of this Actor's properties
     *            and referencing it later.
     */
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

    /**
     * Given a reference tag, opens an .xml file to load the rest of the Actor properties from.
     * The reference tag is the name of the .xml file without path or extension, at time of
     * writing the default path is available in the Overworld Globals.
     *
     * @param ref String: Reference tag for this actor, also the name of the .xml file to get
     *            its properties from.
     */
    private void getActorStats(String ref)
    {
        SAXBuilder saxBuilder = new SAXBuilder();
        InputStream inputStream = ResourceLoader.getResourceAsStream(
            ACTOR_PROPERTIES_RESOURCE_PATH + ref + XML_EXTENSION);
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
        System.out.println("inputDDX:<" + inputDDX + ">");
        inputMaxDX = Long.parseLong(movementElement.getChildText("inputMaxDX"));
        System.out.println("inputMaxDX:<" + inputMaxDX + ">");

        instantaneousJumpDY = Long.parseLong(movementElement.getChildText("instantaneousJumpDY"));
        System.out.println("instantaneousJumpDY:<" + instantaneousJumpDY + ">");
        instantaneousWallJumpDX = Long.parseLong(movementElement.getChildText("instantaneousWallJumpDX"));
        System.out.println("instantaneousWallJumpDX:<" + instantaneousWallJumpDX + ">");
        instantaneousWallJumpDY = Long.parseLong(movementElement.getChildText("instantaneousWallJumpDY"));
        System.out.println("instantaneousWallJumpDY:<" + instantaneousWallJumpDY + ">");

        gravityDDY = Long.parseLong(movementElement.getChildText("gravityDDY"));
        System.out.println("gravityDDY:<" + gravityDDY + ">");
        gravityMaxDY = Long.parseLong(movementElement.getChildText("gravityMaxDY"));
        System.out.println("gravityMaxDY:<" + gravityMaxDY + ">");
        gravityMaxWallDY = Long.parseLong(movementElement.getChildText("gravityMaxWallDY"));
        System.out.println("gravityMaxWallDY:<" + gravityMaxWallDY + ">");
    }

    /**
     * Get current actor dX.
     *
     * @return long: Current actor dX.
     */
    long getDX()
    {
        return dX;
    }

    /**
     * Set actor dX.
     *
     * @param dX long: New actor dX.
     */
    void setDX(long dX)
    {
        this.dX = dX;
    }

    /**
     * Get current actor dY.
     *
     * @return long: Current actor dY.
     */
    long getDY()
    {
        return dY;
    }

    /**
     * Set actor dY.
     *
     * @param dY long: New actor dY.
     */
    void setDY(long dY)
    {
        this.dY = dY;
    }

    /**
     * Check whether the actor is on a left wall.
     *
     * @return boolean: True if the actor is on a left wall, otherwise false.
     */
    boolean isOnWallLeft()
    {
        return onWallLeft;
    }

    /**
     * Update whether the actor is on a left wall.
     *
     * @param onWallLeft boolean: True if the actor is on a left wall, otherwise false.
     */
    void setOnWallLeft(boolean onWallLeft)
    {
        this.onWallLeft = onWallLeft;
    }

    /**
     * Check whether the actor is on a right wall.
     *
     * @return boolean: True if the actor is on a right wall, otherwise false
     */
    boolean isOnWallRight()
    {
        return onWallRight;
    }

    /**
     * Update whether the actor is on a right wall.
     *
     * @param onWallRight boolean: True if the actor is on a right wall, otherwise false
     */
    void setOnWallRight(boolean onWallRight)
    {
        this.onWallRight = onWallRight;
    }

    /**
     * Get the standard actor ddX due to input.
     *
     * @return long: The standard actor ddX due to input.
     */
    long getInputDDX()
    {
        return inputDDX;
    }

    /**
     * Set the standard actor ddX due to input.
     *
     * @param inputDDX long: The new standard actor ddX due to input.
     */
    void setInputDDX(long inputDDX)
    {
        this.inputDDX = inputDDX;
    }

    /**
     * Get the maximum actor dX due to horizontal input.
     *
     * @return long: The maximum actor dX due to horizontal input.
     */
    long getInputMaxDX()
    {
        return inputMaxDX;
    }

    /**
     * Set the maximum actor dX due to horizontal input.
     *
     * @param inputMaxDX long: The new maximum actor dX due to horizontal input.
     */
    void setInputMaxDX(long inputMaxDX)
    {
        this.inputMaxDX = inputMaxDX;
    }

    /**
     * Get the dY to assert at the instant the actor jumps (not wall jumps).
     *
     * @return long: The dY to assert at the instant the actor jumps.
     */
    long getInstantaneousJumpDY()
    {
        return instantaneousJumpDY;
    }

    /**
     * Set the dY to assert at the instant the actor jumps (not wall jumps).
     *
     * @param instantaneousJumpDY long: The new dY to assert at the instant the actor jumps.
     */
    void setInstantaneousJumpDY(long instantaneousJumpDY)
    {
        this.instantaneousJumpDY = instantaneousJumpDY;
    }

    /**
     * Get the dX to assert at the instant the actor jumps from a wall.
     *
     * @return long: The dX to assert at the instant the actor jumps from a wall.
     */
    long getInstantaneousWallJumpDX()
    {
        return instantaneousWallJumpDX;
    }

    /**
     * Set the dX to assert at the instant the actor jumps from a wall.
     *
     * @param instantaneousWallJumpDX long: The new dX to assert at the instant the actor jumps from a wall.
     */
    void setInstantaneousWallJumpDX(long instantaneousWallJumpDX)
    {
        this.instantaneousWallJumpDX = instantaneousWallJumpDX;
    }

    /**
     * Get the dY to assert at the instant the actor jumps from a wall.
     *
     * @return long: The dY to assert at the instant the actor jumps from a wall.
     */
    long getInstantaneousWallJumpDY()
    {
        return instantaneousWallJumpDY;
    }

    /**
     * Set the dY to assert at the instant the actor jumps from a wall.
     *
     * @param instantaneousWallJumpDY long: The new dY to assert at the instant the actor jumps from a wall.
     */
    void setInstantaneousWallJumpDY(long instantaneousWallJumpDY)
    {
        this.instantaneousWallJumpDY = instantaneousWallJumpDY;
    }

    /**
     * Get the standard actor ddY due to gravity.
     *
     * @return long: The standard actor ddY due to gravity.
     */
    long getGravityDDY()
    {
        return gravityDDY;
    }

    /**
     * Set the standard actor ddY due to gravity.
     *
     * @param gravityDDY long: The new standard actor ddY due to gravity.
     */
    void setGravityDDY(long gravityDDY)
    {
        this.gravityDDY = gravityDDY;
    }

    /**
     * Get the maximum actor dY due to gravity.
     *
     * @return long: The maximum actor dY due to gravity.
     */
    long getGravityMaxDY()
    {
        return gravityMaxDY;
    }

    /**
     * Set the maximum actor dY due to gravity.
     *
     * @param gravityMaxDY long: The new maximum actor dY due to gravity.
     */
    void setGravityMaxDY(long gravityMaxDY)
    {
        this.gravityMaxDY = gravityMaxDY;
    }

    /**
     * Get the maximum actor dY due to gravity while on a wall.
     *
     * @return long: The maximum actor dY due to gravity while on a wall.
     */
    long getGravityMaxWallDY()
    {
        return gravityMaxWallDY;
    }

    /**
     * Set the maximum actor dY due to gravity while on a wall.
     *
     * @param gravityMaxWallDY long: The new maximum actor dY due to gravity while on a wall.
     */
    void setGravityMaxWallDY(long gravityMaxWallDY)
    {
        this.gravityMaxWallDY = gravityMaxWallDY;
    }

    /**
     * Check whether or not we should reset the jump animation.
     *
     * @return boolean: True if we should reset the jump animation, false otherwise.
     */
    boolean isResetJump()
    {
        return resetJump;
    }

    /**
     * Update whether or not we should reset the jump animation.
     *
     * @param resetJump boolean: True if we should reset the jump animation, false otherwise.
     */
    void setResetJump(boolean resetJump)
    {
        this.resetJump = resetJump;
    }

    /**
     * Get the logical units to horizontally offset the actor graphic.
     *
     * @return long: Logical units to horizontally offset the actor graphic.
     */
    long getGraphicOffsetX()
    {
        return graphicOffsetX;
    }

    /**
     * Update the logical units to horizontally offset the actor graphic.
     *
     * @param graphicOffsetX long: The new logical units to horizontally offset the actor graphic.
     */
    void setGraphicOffsetX(long graphicOffsetX)
    {
        this.graphicOffsetX = graphicOffsetX;
    }

    /**
     * Get the logical units to vertically offset the actor graphic.
     *
     * @return long: Logical units to vertically offset the actor graphic.
     */
    long getGraphicOffsetY()
    {
        return graphicOffsetY;
    }

    /**
     * Update the logical units to vertically offset the actor graphic.
     *
     * @param graphicOffsetY long: The new logical units to vertically offset the actor graphic.
     */
    void setGraphicOffsetY(long graphicOffsetY)
    {
        this.graphicOffsetY = graphicOffsetY;
    }
}
