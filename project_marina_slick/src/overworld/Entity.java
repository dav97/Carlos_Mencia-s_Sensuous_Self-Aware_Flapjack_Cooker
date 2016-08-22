package overworld;

/**
 * overworld.Entity is a generic class for storing the location and size of any
 * applicable object in the Overworld, and extending for more specialized objects.
 *
 * @author scorple
 * @version dev04
 * @since 2016_0822
 */
class Entity
{
    private long x;
    private long y;
    private long width;
    private long height;

    /**
     * Default constructor, initializes logical coordinates, width and height to 0.
     */
    public Entity()
    {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
    }

    /**
     * Primary constructor, accepts initial logical x and y coordinates and defaults
     * width and height to 0.
     *
     * @param x long: The initial x coordinate of the Entity.
     * @param y long: The initial y coordinate of the Entity.
     */
    public Entity(long x, long y)
    {
        this.x = x;
        this.y = y;
        this.width = 0;
        this.height = 0;
    }

    /**
     * Secondary constructor, accepts initial local x and y coordinates as well as
     * width and height.
     *
     * @param x      long: The initial x coordinate of the Entity.
     * @param y      long: The initial y coordinate of the Entity.
     * @param width  long: The width of the Entity.
     * @param height long: The height of the Entity.
     */
    public Entity(long x, long y, long width, long height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Get the Entity's logical x coordinate.
     *
     * @return long: The Entity's logical x coordinate.
     */
    public long getX()
    {
        return x;
    }

    /**
     * Update the Entity's logical x coordinate.
     *
     * @param x long: The Entity's new logical x coordinate.
     */
    public void setX(long x)
    {
        this.x = x;
    }

    /**
     * Get the Entity's logical y coordinate.
     *
     * @return long: The Entity's logical y coordinate.
     */
    public long getY()
    {
        return y;
    }

    /**
     * Update the Entity's logical y coordinate.
     *
     * @param y long: The Entity's new logical y coordinate.
     */
    public void setY(long y)
    {
        this.y = y;
    }

    /**
     * Get the Entity's logical width.
     *
     * @return long: The Entity's logical width.
     */
    public long getWidth()
    {
        return width;
    }

    /**
     * Update the Entity's logical width.
     *
     * @param width long: The Entity's new logical width.
     */
    public void setWidth(long width)
    {
        this.width = width;
    }

    /**
     * Get the Entity's logical height.
     *
     * @return long: The Entity's logical height.
     */
    public long getHeight()
    {
        return height;
    }

    /**
     * Update the Entity's logical height.
     *
     * @param height long: The Entity's new logical height.
     */
    public void setHeight(long height)
    {
        this.height = height;
    }
}
