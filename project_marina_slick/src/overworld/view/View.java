package overworld.view;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.tiled.TiledMap;

import java.util.HashMap;
import java.util.Map.Entry;

import static overworld.Globals.ActorGraphicIndex;
import static overworld.Globals.ActorGraphicIndex.faceFront;

/**
 * overworld.view.View holds and renders all the graphical elements for the overworld game state,
 * updated regularly by the overworld presenter.
 *
 * @author Scorple
 * @version dev01
 * @since 2016_0801
 */
public class View
{
    private float scale;

    //TODO: move these to a separate class
    private TiledMap tiledMap;
    private Image    mapImage;
    private Image    mapForegroundImage;
    private Image    mapMidgroundImage;
    private Image    mapBackgroundImage;
    private Image    mapSkyboxImage;
    private float    mapX;
    private float    mapY;

    private Map map;

    private HashMap<String, Entity> entityMap;

    /**
     * Default constructor for this view.
     */
    public View()
    {
        entityMap = new HashMap<>();
    }

    /**
     * Render the overworld view.
     *
     * @param g Graphics: The graphics context.
     */
    public void draw(Graphics g)
    {
        g.setAntiAlias(false); //this is a pixel-art game, no anti-aliasing here

        g.scale(scale, scale);

        //TODO: performance, only draw the part of the map on screen
        mapSkyboxImage.draw(mapX, mapY);

        mapBackgroundImage.draw(mapX, mapY);

        mapMidgroundImage.draw(mapX, mapY);

        for (Entry<String, Entity> entry : entityMap.entrySet())
        {
            Entity entity = entry.getValue();
            if (entity.shouldDraw())
            {
                if (entity instanceof Actor)
                {
                    if (((Actor) entity).getGraphic() != null)
                    {
                        ((Actor) entity).getGraphic().draw(entity.getX(), entity.getY());
                    }
                    else
                    {
                        System.out.println("Warning:");
                    }
                }
                else
                {
                    if (entity.getDefaultGraphic() != null)
                    {
                        entity.getDefaultGraphic().draw(entity.getX(), entity.getY());
                    }
                    else
                    {
                        System.out.println("Warning:");
                    }
                }
            }
        }

        mapForegroundImage.draw(mapX, mapY);
    }

    /**
     * Set the scale to use for graphic drawing.
     *
     * @param scale float: The scale to use for drawing.
     */
    public void setScale(float scale)
    {
        this.scale = scale;
    }

    /**
     * Set the overworld map image to use for drawing.
     *
     * @param mapImage Image: The map to draw.
     */
    public void setMapImage(Image mapImage)
    {
        this.mapImage = mapImage;
    }

    /**
     * Set the overworld map foreground image to use for drawing.
     *
     * @param mapForegroundImage Image: The foreground of the map to draw.
     */
    public void setMapForegroundImage(Image mapForegroundImage)
    {
        this.mapForegroundImage = mapForegroundImage;
    }

    /**
     * Set the overworld map midground image to use for drawing.
     *
     * @param mapMidgroundImage Image: The midground of the map to draw.
     */
    public void setMapMidgroundImage(Image mapMidgroundImage)
    {
        this.mapMidgroundImage = mapMidgroundImage;
    }

    /**
     * Set the overworld map background image to use for drawing.
     *
     * @param mapBackgroundImage Image: The background of the map to draw.
     */
    public void setMapBackgroundImage(Image mapBackgroundImage)
    {
        this.mapBackgroundImage = mapBackgroundImage;
    }

    public void setMapSkyboxImage(Image mapSkyboxImage)
    {
        this.mapSkyboxImage = mapSkyboxImage;
    }

    /**
     * Sets the coordinate to use for drawing the map.
     * This pair is the upper left corner of the map,
     * it is drawn right and down from this pair.
     *
     * @param mapX int: The X coordinate to draw the map.
     * @param mapY int: The Y coordinate to draw the map.
     */
    public void setMapLocation(float mapX, float mapY)
    {
        this.mapX = mapX;
        this.mapY = mapY;
    }

    public void addEntity(String ref, Entity entity)
    {
        entityMap.put(ref, entity);
    }

    public void setEntityLocation(String ref, float x, float y)
    {
        if (entityMap.get(ref) != null)
        {
            entityMap.get(ref).setLocation(x, y);
        }
    }

    public ActorGraphicIndex getActorGraphicIndex(String ref)
    {
        if (entityMap.get(ref) instanceof Actor)
        {
            return ((Actor) entityMap.get(ref)).getGraphicIndex();
        }
        else
        {
            System.out.println("Error: attempt to get graphic index of non-actor entity, ref:<" + ref + ">");
        }

        return faceFront;
    }

    public void setActorGraphicIndex(String ref, ActorGraphicIndex graphicIndex)
    {
        if (entityMap.get(ref) instanceof Actor)
        {
            ((Actor) entityMap.get(ref)).setGraphicIndex(graphicIndex);
        }
        else
        {
            System.out.println("Error: attempt to update graphic index of non-actor entity, ref:<" + ref + ">");
        }
    }

    public void resetActorAnimation(String ref)
    {
        if (entityMap.get(ref) instanceof Actor)
        {
            ((Actor) entityMap.get(ref)).resetAnimation();
        }
        else
        {
            System.out.println("Error: attempt to restart animation of non-actor entity, ref:<" + ref + ">");
        }
    }

    public void setActorFall(String ref)
    {
        if (entityMap.get(ref) instanceof Actor)
        {
            ((Actor) entityMap.get(ref)).setFall();
        }
        else
        {
            System.out.println("Error, attempt to set fall frame of non-actor entity, ref:<" + ref + ">");
        }
    }
}
