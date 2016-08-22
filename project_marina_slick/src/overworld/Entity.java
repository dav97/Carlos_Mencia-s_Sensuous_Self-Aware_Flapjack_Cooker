package overworld;

/**
 * Created by Scorple on 8/16/2016.
 */
public class Entity
{
    long x;
    long y;
    long width;
    long height;

    public Entity()
    {
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
    }

    public Entity(long x, long y)
    {
        this.x = x;
        this.y = y;
        this.width = 0;
        this.height = 0;
    }

    public Entity(long x, long y, long width, long height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public long getX()
    {
        return x;
    }

    public void setX(long x)
    {
        this.x = x;
    }

    public long getY()
    {
        return y;
    }

    public void setY(long y)
    {
        this.y = y;
    }

    public long getWidth()
    {
        return width;
    }

    public void setWidth(long width)
    {
        this.width = width;
    }

    public long getHeight()
    {
        return height;
    }

    public void setHeight(long height)
    {
        this.height = height;
    }
}
