package overworld;

import java.util.HashMap;

import static overworld.Globals.*;

/**
 * overworld.Model holds all the logical data for the overworld game state and acts as
 * an interface to that data, regularly queried and updated by the overworld presenter
 * and its members.
 *
 * @author Scorple
 * @version dev01
 * @since 2016_0801
 */
class Model
{
    private Map map;

    private HashMap<String, Entity> entityMap;

    /**
     * Default constructor for this model.
     */
    Model()
    {

    }

    /**
     * Setup the model.
     *
     * @param mapWidth  int: The width in tiles of the map.
     * @param mapHeight int: The height in tiles of the map.
     * @param tileWidth long: The raw width of a tile graphic
     *                  (should never differ from its height).
     * @param mapClip   Boolean[][]: A grid used for collision
     *                  detection, maps one-to-one with the map
     *                  tiles, true if the player may pass through
     *                  a tile, otherwise false.
     * @param mapHooks  String[][]: A grid used for getting
     *                  reference points ("hooks") on the map,
     *                  such as spawn and transition points.
     *                  0
     */
    void setupModel(int mapWidth, int mapHeight, long tileWidth, Boolean[][] mapClip, String[][] mapHooks)
    {
        map = new Map(mapWidth, mapHeight, tileWidth, mapClip, mapHooks);

        entityMap = new HashMap<>();
    }

    /**
     * Get the Map object associated with the currently loaded map.
     *
     * @return Map: The Map object associated with the currently loaded map.
     */
    Map getMap()
    {
        return map;
    }

    /**
     * Set the player location and default movement values
     * based on the location of the given hook in the map.
     *
     * @param hook String: A reference point in the map.
     */
    void spawnActor(String actorRef, String hook)
    {
        System.out.println("Spawning player at hook <" + hook + ">");

        for (int x = 0; x < map.getWidth(); ++x)
        {
            for (int y = 0; y < map.getHeight(); ++y)
            {
                if (map.getHooks()[x][y].equals(hook))
                {
                    System.out.println("Hook <" + hook + "> found at <" + x + ", " + y + ">");
                    Actor actor = new Actor(x * map.getTileWidth(), y * map.getTileWidth(), actorRef);
                    entityMap.put(actorRef, actor);
                    map.setHookSpawn(hook);
                    return;
                }
            }
        }
    }

    //TODO: consider splitting, generalizing

    /**
     * Collision checking method, given a proposed amount of player movement,
     * check for map tiles within that distance of the player, if there are any,
     * return the distance the player may travel until colliding with the tile(s),
     * otherwise return the proposed amount of movement. May return zero, will treat
     * movement which would cause the player to leave me map as colliding with the
     * edge of the map.
     * Only applies to movement left or right.
     *
     * @param dX long: The proposed amount of player movement.
     *
     * @return long: The player distance from the tile she will
     * collide with if she moved the proposed amount.
     */
    long getActorHorizontalCollisionDistanceByDX(Actor actor, long dX)
    {
        long playerXTest = actor.getX();

        if (dX < 0)
        {
            playerXTest = actor.getX() + dX;
        }
        else if (dX > 0)
        {
            playerXTest = actor.getX() + actor.getWidth() + dX;
        }

        //map bounds checking
        if (playerXTest < 0)
        {
            return -(actor.getX());
        }
        if (playerXTest >= (((map.getWidth()) * map.getTileWidth())))
        {
            return (((map.getWidth()) * map.getTileWidth()) - (actor.getX() + actor.getWidth()));
        }
        //end map bounds checking

        int maxMapXCollisionTiles =
            (int) (1 + ((actor.getHeight() > map.getTileWidth()) ? (actor.getHeight() / map.getTileWidth()) : 0) +
                   1); //check the mapY, at minimum, level with the top and bottom of the player

        int   mapXTest = (int) (playerXTest / map.getTileWidth());
        int[] mapYTest = new int[maxMapXCollisionTiles];

        mapYTest[0] = (int) (actor.getY() /
                             map.getTileWidth()); //the first mapY to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxMapXCollisionTiles - 1); ++i)
        {
            mapYTest[i] = mapYTest[i - 1] +
                          1; //for every mapY we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapYTest[maxMapXCollisionTiles - 1] = (int) (((actor.getY() + actor.getHeight()) - 1) /
                                                     map.getTileWidth()); //the last mapY to test is the grid coordinate level with the bottom of the player

        for (int i = 0; i < maxMapXCollisionTiles; ++i)
        { //check collision on every tile to the left or right of the player within a distance of dX
            if (!map.getClip()[mapXTest][mapYTest[i]])
            {
                long collisionDX = 0;
                if (dX < 0)
                {
                    collisionDX = (((mapXTest + 1) * map.getTileWidth())) -
                                  actor.getX(); //subtract the X location of the left side of the player from the X location of the right side of the tile
                }
                else if (dX > 0)
                {
                    collisionDX = ((mapXTest) * map.getTileWidth()) - ((actor.getX() +
                                                                        actor.getWidth())); //subtract the X location of the right side of the player from the X location of the left side of the tile
                }
                return collisionDX;
            }
        }

        return dX;
    }

    /**
     * Check for collision immediately to the left of the player.
     * (i.e. she is touching a wall to the left)
     *
     * @return boolean: True if there is collision immediately left of the player.
     */
    boolean isActorCollisionLeft(Actor actor)
    {
        return (getActorHorizontalCollisionDistanceByDX(actor, STANDARD_COLLISION_CHECK_DISTANCE_LEFT) == 0.0f);
    }

    /**
     * Check for collision immediately to the right of the player.
     * (i.e. she is touching a wall to the right)
     *
     * @return boolean: True if there is collision immediately right of the player.
     */
    boolean isActorCollisionRight(Actor actor)
    {
        return (getActorHorizontalCollisionDistanceByDX(actor, STANDARD_COLLISION_CHECK_DISTANCE_RIGHT) == 0.0f);
    }

    /**
     * Collision checking method, given a proposed amount of player movement,
     * check for map tiles within that distance of the player, if there are any,
     * return the distance the player may travel until colliding with the tile(s),
     * otherwise return the proposed amount of movement. May return zero, will treat
     * movement which would cause the player to leave me map as colliding with the
     * edge of the map.
     * Only applies to movement up or down.
     *
     * @param dY long: The proposed amount of player movement.
     *
     * @return long: The player distance from the tile she will
     * collide with if she moved the proposed amount.
     */
    long getActorVerticalCollisionDistanceByDY(Actor actor, long dY)
    {
        long playerYTest = actor.getY();

        if (dY < 0)
        {
            playerYTest = actor.getY() + dY;
        }
        else if (dY > 0)
        {
            playerYTest = actor.getY() + actor.getHeight() + dY;
        }

        //map bounds checking
        if (playerYTest < 0)
        {
            return -(actor.getY());
        }
        if (playerYTest >= (((map.getHeight()) * map.getTileWidth())))
        {
            return (((map.getHeight()) * map.getTileWidth()) - (actor.getY() + actor.getHeight()));
        }
        //end map bounds checking

        int maxMapYCollisionTiles =
            (int) (1 + ((actor.getWidth() > map.getTileWidth()) ? (actor.getWidth() / map.getTileWidth()) : 0) +
                   1); //check the mapX, at minimum, level with the left and right side of the player

        int[] mapXTest = new int[maxMapYCollisionTiles];
        int   mapYTest = (int) (playerYTest / map.getTileWidth());

        mapXTest[0] = (int) (actor.getX() /
                             map.getTileWidth()); //the first mapX to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxMapYCollisionTiles - 1); ++i)
        {
            mapXTest[i] = mapXTest[i - 1] +
                          1; //for every mapX we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapXTest[maxMapYCollisionTiles - 1] = (int) (((actor.getX() + actor.getWidth()) - 1) /
                                                     map.getTileWidth()); //the last mapX to test is the grid coordinate level with the bottom of the player

        for (int i = 0; i < maxMapYCollisionTiles; ++i)
        { //check collision on every tile to the left or right of the player within a distance of dX
            if (!map.getClip()[mapXTest[i]][mapYTest])
            {
                long collisionDY = 0;
                if (dY < 0)
                {
                    collisionDY = (((mapYTest + 1) * map.getTileWidth())) -
                                  actor.getY(); //subtract the Y location of the top of the player from the Y location of the bottom of the tile
                }
                else if (dY > 0)
                {
                    collisionDY = ((mapYTest) * map.getTileWidth()) - ((actor.getY() +
                                                                        actor.getHeight())); //subtract the Y location of the bottom of the player from the Y location of the top of the tile
                }
                return collisionDY;
            }
        }

        return dY;
    }

    /**
     * Check for collision immediately above the player.
     * (i.e. her head is touching the ceiling)
     *
     * @return boolean: True if there is collision immediately above the player.
     */
    boolean isActorCollisionUp(Actor actor)
    {
        return (getActorVerticalCollisionDistanceByDY(actor, STANDARD_COLLISION_CHECK_DISTANCE_UP) == 0.0f);
    }

    /**
     * Check for collision immediately below the player.
     * (i.e. she is standing on solid ground)
     *
     * @return boolean: True if there is collision immediately below the player.
     */
    boolean isActorCollisionDown(Actor actor)
    {
        return (getActorVerticalCollisionDistanceByDY(actor, STANDARD_COLLISION_CHECK_DISTANCE_DOWN) == 0.0f);
    }

    /**
     * Get a list of reference hooks attached to all tiles with player is intersecting with.
     *
     * @return String[]: A list containing the hook property of every tile the player
     * is intersecting with. WARNING: MAY CONTAIN DUPLICATES, ESPECIALLY IF CHECKED
     * DURING JUMP.
     */
    String[] getActorIntersectingTileHooks(Actor actor)
    {
        int maxPlayerIntersectionTilesTopToBottom =
            (int) (1 + ((actor.getHeight() > map.getTileWidth()) ? (actor.getHeight() / map.getTileWidth()) : 0) +
                   1); //check the mapY, at minimum, level with the top and bottom of the player
        int maxPlayerIntersectionTilesLeftToRight =
            (int) (1 + ((actor.getWidth() > map.getTileWidth()) ? (actor.getWidth() / map.getTileWidth()) : 0) +
                   1); //check the mapX, at minimum, level with the left and right side of the player
        int maxIntersectingTiles = maxPlayerIntersectionTilesTopToBottom * maxPlayerIntersectionTilesLeftToRight;

        int[] mapXTest = new int[maxPlayerIntersectionTilesLeftToRight];

        //get every mapX grid coordinate we will need to check for hooks
        mapXTest[0] = (int) (actor.getX() /
                             map.getTileWidth()); //the first mapX to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxPlayerIntersectionTilesLeftToRight - 1); ++i)
        {
            mapXTest[i] = mapXTest[i - 1] +
                          1; //for every mapX we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapXTest[maxPlayerIntersectionTilesLeftToRight - 1] = (int) (((actor.getX() + actor.getWidth()) - 1) /
                                                                     map.getTileWidth()); //the last mapX to test is the grid coordinate level with the bottom of the player

        int[] mapYTest = new int[maxPlayerIntersectionTilesTopToBottom];

        //get every mapY grid coordinate we will need to check for hooks
        mapYTest[0] = (int) (actor.getY() /
                             map.getTileWidth()); //the first mapY to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxPlayerIntersectionTilesTopToBottom - 1); ++i)
        {
            mapYTest[i] = mapYTest[i - 1] +
                          1; //for every mapY we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapYTest[maxPlayerIntersectionTilesTopToBottom - 1] = (int) (((actor.getY() + actor.getHeight()) - 1) /
                                                                     map.getTileWidth()); //the last mapX to test is the grid coordinate level with the bottom of the player

        String[] hooks = new String[maxIntersectingTiles];

        //fill the String array with the hooks of every tile intersecting with the player
        for (int x = 0; x < maxPlayerIntersectionTilesLeftToRight; ++x)
        {
            for (int y = 0; y < maxPlayerIntersectionTilesTopToBottom; ++y)
            {
                hooks[y + x * maxPlayerIntersectionTilesTopToBottom] = map.getHooks()[mapXTest[x]][mapYTest[y]];
            }
        }

        return hooks;
    }

    /**
     * Given a reference tag, return the Entity mapped to that reference tag
     * in this model.
     *
     * @param ref String: The reference tag to lookup an Entity.
     *
     * @return Entity: The Entity mapped to the given reference tag.
     */
    Entity getEntityByRef(String ref)
    {
        return entityMap.get(ref);
    }
}