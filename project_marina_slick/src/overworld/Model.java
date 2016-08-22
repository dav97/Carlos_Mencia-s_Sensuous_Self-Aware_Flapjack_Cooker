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
    //TODO: move these to a separate class
    int         mapWidth; //the map width in tiles
    int         mapHeight; //the map height in tiles
    long        tileWidth;
    Boolean[][] mapClip; //the grid of passable (clip true) and not passable (clip false)
    String      mapHookCurrent;
    String      mapHookSpawn;
    String[][]  mapHooks;

    HashMap<String, Entity> entityMap;

    //TODO: move these to a separate class
    /*long playerWidth;
    long playerHeight;
    long playerX;
    long playerY;
    long playerDX;
    long playerDY;

    boolean playerOnWallLeft;
    boolean playerOnWallRight;

    long dDXDueToInput;
    long maxDXDueToInput;

    long instantaneousJumpDY;
    long instantaneousWallJumpDY;
    long instantaneousWallJumpLeftDX;
    long instantaneousWallJumpRightDX;

    long dDYDueToGravity;
    long maxDYDueToGravity;
    long maxDYOnWall;

    boolean resetJump;*/

    /**
     * Default constructor for this model.
     */
    Model()
    {

    }

    /**
     * Set the map model.
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
    void setupMapModel(int mapWidth, int mapHeight, long tileWidth, Boolean[][] mapClip, String[][] mapHooks)
    {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.tileWidth = tileWidth;
        this.mapClip = mapClip;
        this.mapHooks = mapHooks;

        entityMap = new HashMap<>(); //TODO: move, or make function more generic
    }

    /**
     * Get the reference hook for the currently loaded map.
     *
     * @return String: The reference hook for the currently loaded map.
     */
    String getMapHookCurrent()
    {
        return mapHookCurrent;
    }

    /**
     * Set the reference hook for the currently loaded map.
     *
     * @param mapHookCurrent String: The reference hook for the currently loaded map.
     */
    void setMapHookCurrent(String mapHookCurrent)
    {
        this.mapHookCurrent = mapHookCurrent;
    }

    /**
     * Get the reference hook for the player spawn location.
     *
     * @return String: The reference hook for the player spawn location.
     */
    String getMapHookSpawn()
    {
        return mapHookSpawn;
    }

    /**
     * Set the reference hook for the player spawn location.
     *
     * @param mapHookPrevious String: The reference hook for the player spawn location.
     */
    void setMapHookSpawn(String mapHookPrevious)
    {
        this.mapHookSpawn = mapHookPrevious;
    }

    /**
     * Create the player model and set default location and
     * movement values.
     *
     * @param playerWidth  int: The raw width of the player graphic.
     * @param playerHeight int: The raw height of the player graphic.
     */
    /*
    void setupPlayerModel(long playerWidth, long playerHeight)
    {
        this.playerWidth = playerWidth;
        this.playerHeight = playerHeight;
        playerX = 0;
        playerY = 0;
        playerDX = 0;
        playerDY = 0;
    }
    */

    /**
     * Set the player location and default movement values
     * based on the location of the given hook in the map.
     *
     * @param hook String: A reference point in the map.
     */
    void spawnActor(String actorRef, String hook)
    {
        System.out.println("Spawning player at hook <" + hook + ">");

        for (int x = 0; x < mapWidth; ++x)
        {
            for (int y = 0; y < mapHeight; ++y)
            {
                if (mapHooks[x][y].equals(hook))
                {
                    System.out.println("Hook <" + hook + "> found at <" + x + ", " + y + ">");
                    Actor actor = new Actor(x * tileWidth, y * tileWidth, actorRef);
                    entityMap.put(actorRef, actor);
                    /*playerX = x * tileWidth;
                    playerY = y * tileWidth;
                    playerDX = 0;
                    playerDY = 0;
                    playerOnWallLeft = false;
                    playerOnWallRight = false;*/
                    mapHookSpawn = hook;
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
        long playerXTest = actor.getX();//playerX;

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
        if (playerXTest >= (((mapWidth) * tileWidth)))
        {
            return (((mapWidth) * tileWidth) - (actor.getX() + actor.getWidth()));
        }
        //end map bounds checking

        int maxMapXCollisionTiles = (int) (1 + ((actor.getHeight() > tileWidth) ? (actor.getHeight() / tileWidth) : 0) +
                                           1); //check the mapY, at minimum, level with the top and bottom of the player

        int   mapXTest = (int) (playerXTest / tileWidth);
        int[] mapYTest = new int[maxMapXCollisionTiles];

        mapYTest[0] = (int) (actor.getY() /
                             tileWidth); //the first mapY to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxMapXCollisionTiles - 1); ++i)
        {
            mapYTest[i] = mapYTest[i - 1] +
                          1; //for every mapY we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapYTest[maxMapXCollisionTiles - 1] = (int) (((actor.getY() + actor.getHeight()) - 1) /
                                                     tileWidth); //the last mapY to test is the grid coordinate level with the bottom of the player

        for (int i = 0; i < maxMapXCollisionTiles; ++i)
        { //check collision on every tile to the left or right of the player within a distance of dX
            if (!mapClip[mapXTest][mapYTest[i]])
            {
                long collisionDX = 0;
                if (dX < 0)
                {
                    collisionDX = (((mapXTest + 1) * tileWidth)) -
                                  actor.getX(); //subtract the X location of the left side of the player from the X location of the right side of the tile
                }
                else if (dX > 0)
                {
                    collisionDX = ((mapXTest) * tileWidth) - ((actor.getX() +
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
        if (playerYTest >= (((mapHeight) * tileWidth)))
        {
            return (((mapHeight) * tileWidth) - (actor.getY() + actor.getHeight()));
        }
        //end map bounds checking

        int maxMapYCollisionTiles = (int) (1 + ((actor.getWidth() > tileWidth) ? (actor.getWidth() / tileWidth) : 0) +
                                           1); //check the mapX, at minimum, level with the left and right side of the player

        int[] mapXTest = new int[maxMapYCollisionTiles];
        int   mapYTest = (int) (playerYTest / tileWidth);

        mapXTest[0] = (int) (actor.getX() /
                             tileWidth); //the first mapX to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxMapYCollisionTiles - 1); ++i)
        {
            mapXTest[i] = mapXTest[i - 1] +
                          1; //for every mapX we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapXTest[maxMapYCollisionTiles - 1] = (int) (((actor.getX() + actor.getWidth()) - 1) /
                                                     tileWidth); //the last mapX to test is the grid coordinate level with the bottom of the player

        for (int i = 0; i < maxMapYCollisionTiles; ++i)
        { //check collision on every tile to the left or right of the player within a distance of dX
            if (!mapClip[mapXTest[i]][mapYTest])
            {
                long collisionDY = 0;
                if (dY < 0)
                {
                    collisionDY = (((mapYTest + 1) * tileWidth)) -
                                  actor.getY(); //subtract the Y location of the top of the player from the Y location of the bottom of the tile
                }
                else if (dY > 0)
                {
                    collisionDY = ((mapYTest) * tileWidth) - ((actor.getY() +
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
            (int) (1 + ((actor.getHeight() > tileWidth) ? (actor.getHeight() / tileWidth) : 0) +
                   1); //check the mapY, at minimum, level with the top and bottom of the player
        int maxPlayerIntersectionTilesLeftToRight =
            (int) (1 + ((actor.getWidth() > tileWidth) ? (actor.getWidth() / tileWidth) : 0) +
                   1); //check the mapX, at minimum, level with the left and right side of the player
        int maxIntersectingTiles = maxPlayerIntersectionTilesTopToBottom * maxPlayerIntersectionTilesLeftToRight;

        int[] mapXTest = new int[maxPlayerIntersectionTilesLeftToRight];

        //get every mapX grid coordinate we will need to check for hooks
        mapXTest[0] = (int) (actor.getX() /
                             tileWidth); //the first mapX to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxPlayerIntersectionTilesLeftToRight - 1); ++i)
        {
            mapXTest[i] = mapXTest[i - 1] +
                          1; //for every mapX we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapXTest[maxPlayerIntersectionTilesLeftToRight - 1] = (int) (((actor.getX() + actor.getWidth()) - 1) /
                                                                     tileWidth); //the last mapX to test is the grid coordinate level with the bottom of the player

        int[] mapYTest = new int[maxPlayerIntersectionTilesTopToBottom];

        //get every mapY grid coordinate we will need to check for hooks
        mapYTest[0] = (int) (actor.getY() /
                             tileWidth); //the first mapY to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxPlayerIntersectionTilesTopToBottom - 1); ++i)
        {
            mapYTest[i] = mapYTest[i - 1] +
                          1; //for every mapY we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapYTest[maxPlayerIntersectionTilesTopToBottom - 1] = (int) (((actor.getY() + actor.getHeight()) - 1) /
                                                                     tileWidth); //the last mapX to test is the grid coordinate level with the bottom of the player

        String[] hooks = new String[maxIntersectingTiles];

        //fill the String array with the hooks of every tile intersecting with the player
        for (int x = 0; x < maxPlayerIntersectionTilesLeftToRight; ++x)
        {
            for (int y = 0; y < maxPlayerIntersectionTilesTopToBottom; ++y)
            {
                hooks[y + x * maxPlayerIntersectionTilesTopToBottom] = mapHooks[mapXTest[x]][mapYTest[y]];
            }
        }

        return hooks;
    }

    Entity getEntityByRef(String ref)
    {
        return entityMap.get(ref);
    }
}