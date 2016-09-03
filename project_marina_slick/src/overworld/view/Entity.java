package overworld.view;

import org.newdawn.slick.Animation;

/**
 * Created by Scorple on 8/30/2016.
 */
public class Entity
{
    private boolean draw;

    private long x;
    private long y;

    private Animation defaultGraphic;

    public Entity()
    {
        draw = true;

        x = 0;
        y = 0;

        defaultGraphic = null;
    }

    public Entity(Animation defaultGraphic)
    {
        draw = true;

        x = 0;
        y = 0;

        this.defaultGraphic = defaultGraphic;
    }

    public boolean shouldDraw()
    {
        return draw;
    }

    public void setDraw(boolean draw)
    {
        this.draw = draw;
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

    public Animation getDefaultGraphic()
    {
        return defaultGraphic;
    }

    public void setDefaultGraphic(Animation defaultGraphic)
    {
        this.defaultGraphic = defaultGraphic;
    }
}
