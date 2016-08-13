package overworld;

/**
 * overworld.Model acts as a general model for the overworld game state.
 *
 * @author Scorple
 * @version 1.0
 * @since 2016.08.01
 */
class Model
{
    //TODO: move these to a separate class
    int mapWidth; //the map width in tiles
    int mapHeight; //the map height in tiles
    long tileWidth;
    Boolean[][] mapClip; //the grid of passable (clip true) and not passable (clip false)
    String mapHookCurrent;
    String mapHookSpawn;
    String[][] mapHooks;

    //TODO: move these to a separate class
    long playerWidth;
    long playerHeight;
    //storing floats for this makes movement and drawing easier,
    //may make collision handling much harder...
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
    }

    String getMapHookCurrent()
    {
        return mapHookCurrent;
    }

    void setMapHookCurrent(String mapHookCurrent)
    {
        this.mapHookCurrent = mapHookCurrent;
    }

    String getMapHookSpawn()
    {
        return mapHookSpawn;
    }

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
    void setupPlayerModel(long playerWidth, long playerHeight)
    {
        this.playerWidth = playerWidth;
        this.playerHeight = playerHeight;
        playerX = 0;
        playerY = 0;
        playerDX = 0;
        playerDY = 0;
        System.out.println("playerWidth is " + playerWidth + ", playerHeight is " + playerHeight);
    }

    /**
     * Set the player location and default movement values
     * based on the location of the given hook in the map.
     *
     * @param hook String: A reference point in the map.
     */
    void spawnPlayer(String hook)
    {
        System.out.println("Spawning player at hook <" + hook + ">");

        for (int x = 0; x < mapWidth; ++x)
        {
            for (int y = 0; y < mapHeight; ++y)
            {
                if (mapHooks[x][y].equals(hook))
                {
                    System.out.println("Hook <" + hook + "> found at <" + x + ", " + y + ">");
                    playerX = x * tileWidth;
                    playerY = y * tileWidth;
                    playerDX = 0;
                    playerDY = 0;
                    playerOnWallLeft = false;
                    playerOnWallRight = false;
                    mapHookSpawn = hook;
                    return;
                }
            }
        }
    }

    //TODO: consider splitting, generalizing

    /**
     * Collision checking method. Given a proposed amount of
     * player movement, check for map tiles within that distance
     * of the player. If there are any, return that distance.
     * Applies to movement left or right.
     *
     * @param dX long: The proposed amount of player movement.
     *
     * @return long: The player distance from the tile she will
     * collide with if she moved the proposed amount.
     */
    long getHorizontalCollisionDistanceByDX(long dX)
    {
        long playerXTest = playerX;

        if (dX < 0)
        {
            playerXTest = playerX + dX;
        }
        else if (dX > 0)
        {
            playerXTest = playerX + playerWidth + dX;
        }

        //map bounds checking
        if (playerXTest < 0)
        {
            return -(playerX);
        }
        if (playerXTest > (((mapWidth) * tileWidth)))
        {
            return (((mapWidth) * tileWidth) - (playerX + playerWidth));
        }
        //end map bounds checking

        int maxMapXCollisionTiles = (int) (1 + ((playerHeight > tileWidth) ? (playerHeight / tileWidth) : 0) +
            1); //check the mapY, at minimum, level with the top and bottom of the player

        int mapXTest = (int) (playerXTest / tileWidth);
        int[] mapYTest = new int[maxMapXCollisionTiles];

        mapYTest[0] = (int) (playerY /
            tileWidth); //the first mapY to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxMapXCollisionTiles - 1); ++i)
        {
            mapYTest[i] = mapYTest[i - 1] +
                1; //for every mapY we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapYTest[maxMapXCollisionTiles - 1] = (int) (((playerY + playerHeight) - 1) /
            tileWidth); //the last mapY to test is the grid coordinate level with the bottom of the player

        for (int i = 0; i < maxMapXCollisionTiles; ++i)
        { //check collision on every tile to the left or right of the player within a distance of dX
            if (!mapClip[mapXTest][mapYTest[i]])
            {
                long collisionDX = 0;
                if (dX < 0)
                {
                    collisionDX = (((mapXTest + 1) * tileWidth)) -
                        playerX; //subtract the X location of the left side of the player from the X location of the right side of the tile
                }
                else if (dX > 0)
                {
                    collisionDX = ((mapXTest) * tileWidth) - ((playerX +
                        playerWidth)); //subtract the X location of the right side of the player from the X location of the left side of the tile
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
    boolean isPlayerCollisionLeft()
    {
        return (getHorizontalCollisionDistanceByDX(Globals.STANDARD_COLLISION_CHECK_DISTANCE_LEFT) == 0.0f);
    }

    /**
     * Check for collision immediately to the right of the player.
     * (i.e. she is touching a wall to the right)
     *
     * @return boolean: True if there is collision immediately right of the player.
     */
    boolean isPlayerCollisionRight()
    {
        return (getHorizontalCollisionDistanceByDX(Globals.STANDARD_COLLISION_CHECK_DISTANCE_RIGHT) == 0.0f);
    }

    /**
     * Collision checking method. Given a proposed amount of
     * player movement, check for map tiles within that distance
     * of the player. If there are any, return that distance.
     * Applies to movement up or down.
     *
     * @param dY long: The proposed amount of player movement.
     *
     * @return long: The player distance from the tile she will
     * collide with if she moved the proposed amount.
     */
    long getVerticalCollisionDistanceByDY(long dY)
    {
        long playerYTest = playerY;

        if (dY < 0)
        {
            playerYTest = playerY + dY;
        }
        else if (dY > 0)
        {
            playerYTest = playerY + playerHeight + dY;
        }

        //map bounds checking
        if (playerYTest < 0)
        {
            return -(playerY);
        }
        if (playerYTest > (((mapHeight) * tileWidth)))
        {
            return (((mapHeight) * tileWidth) - (playerY + playerHeight));
        }
        //end map bounds checking

        int maxMapYCollisionTiles = (int) (1 + ((playerWidth > tileWidth) ? (playerWidth / tileWidth) : 0) +
            1); //check the mapX, at minimum, level with the left and right side of the player

        int[] mapXTest = new int[maxMapYCollisionTiles];
        int mapYTest = (int) (playerYTest / tileWidth);

        mapXTest[0] = (int) (playerX /
            tileWidth); //the first mapX to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxMapYCollisionTiles - 1); ++i)
        {
            mapXTest[i] = mapXTest[i - 1] +
                1; //for every mapX we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapXTest[maxMapYCollisionTiles - 1] = (int) (((playerX + playerWidth) - 1) /
            tileWidth); //the last mapX to test is the grid coordinate level with the bottom of the player

        for (int i = 0; i < maxMapYCollisionTiles; ++i)
        { //check collision on every tile to the left or right of the player within a distance of dX
            if (!mapClip[mapXTest[i]][mapYTest])
            {
                long collisionDY = 0;
                if (dY < 0)
                {
                    collisionDY = (((mapYTest + 1) * tileWidth)) -
                        playerY; //subtract the Y location of the top of the player from the Y location of the bottom of the tile
                }
                else if (dY > 0)
                {
                    collisionDY = ((mapYTest) * tileWidth) - ((playerY +
                        playerHeight)); //subtract the Y location of the bottom of the player from the Y location of the top of the tile
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
    boolean isPlayerCollisionUp()
    {
        return (getVerticalCollisionDistanceByDY(Globals.STANDARD_COLLISION_CHECK_DISTANCE_UP) == 0.0f);
    }

    /**
     * Check for collision immediately below the player.
     * (i.e. she is standing on solid ground)
     *
     * @return boolean: True if there is collision immediately below the player.
     */
    boolean isPlayerCollisionDown()
    {
        return (getVerticalCollisionDistanceByDY(Globals.STANDARD_COLLISION_CHECK_DISTANCE_DOWN) == 0.0f);
    }

    /**
     * Get a list of the hooks attached to all tiles with player is intersecting with.
     *
     * @return String[]: A list containing the hook property of every tile the player
     * is intersecting with. WARNING: MAY CONTAIN DUPLICATES, ESPECIALLY IF CHECKED
     * DURING JUMP.
     */
    String[] getIntersectingTileHooks()
    {
        int maxPlayerIntersectionTilesTopToBottom =
            (int) (1 + ((playerHeight > tileWidth) ? (playerHeight / tileWidth) : 0) +
                1); //check the mapY, at minimum, level with the top and bottom of the player
        int maxPlayerIntersectionTilesLeftToRight =
            (int) (1 + ((playerWidth > tileWidth) ? (playerWidth / tileWidth) : 0) +
                1); //check the mapX, at minimum, level with the left and right side of the player
        int maxIntersectingTiles = maxPlayerIntersectionTilesTopToBottom * maxPlayerIntersectionTilesLeftToRight;

        int[] mapXTest = new int[maxPlayerIntersectionTilesLeftToRight];

        //get every mapX grid coordinate we will need to check for hooks
        mapXTest[0] = (int) (playerX /
            tileWidth); //the first mapX to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxPlayerIntersectionTilesLeftToRight - 1); ++i)
        {
            mapXTest[i] = mapXTest[i - 1] +
                1; //for every mapX we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapXTest[maxPlayerIntersectionTilesLeftToRight - 1] = (int) (((playerX + playerWidth) - 1) /
            tileWidth); //the last mapX to test is the grid coordinate level with the bottom of the player

        int[] mapYTest = new int[maxPlayerIntersectionTilesTopToBottom];

        //get every mapY grid coordinate we will need to check for hooks
        mapYTest[0] = (int) (playerY /
            tileWidth); //the first mapY to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxPlayerIntersectionTilesTopToBottom - 1); ++i)
        {
            mapYTest[i] = mapYTest[i - 1] +
                1; //for every mapY we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapYTest[maxPlayerIntersectionTilesTopToBottom - 1] = (int) (((playerY + playerHeight) - 1) /
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

    /**
     * Get the raw player width in pixels.
     *
     * @return int: The raw player width in pixels.
     */
    long getPlayerWidth()
    {
        return playerWidth;
    }

    /**
     * Get the raw player height in pixels.
     *
     * @return int: The raw player width in pixels.
     */
    long getPlayerHeight()
    {
        return playerHeight;
    }

    /**
     * Get the logical X coordinate of the player.
     *
     * @return long: The logical X coordinate of the player.
     */
    long getPlayerX()
    {
        return playerX;
    }

    /**
     * Set the logical player X coordinate.
     *
     * @param playerX long: The logical X coordinate of the player.
     */
    void setPlayerX(long playerX)
    {
        this.playerX = playerX;
    }

    /**
     * Get the logical Y coordinate of the player.
     *
     * @return long: The logical Y coordinate of the player.
     */
    long getPlayerY()
    {
        return playerY;
    }

    /**
     * Set the logical player Y coordinate.
     *
     * @param playerY long: The logical Y coordinate of the player.
     */
    void setPlayerY(long playerY)
    {
        this.playerY = playerY;
    }

    /**
     * Set the logical player X and Y coordinate.
     *
     * @param playerX long: The logical X coordinate of the player.
     * @param playerY long: The logical Y coordinate of the player.
     */
    void setPlayerLocation(long playerX, long playerY)
    {
        this.playerX = playerX;
        this.playerY = playerY;
    }

    /**
     * Get current player dx.
     *
     * @return long: Current player dx.
     */
    long getPlayerDX()
    {
        return playerDX;
    }

    /**
     * Set player dx.
     *
     * @param playerDX long: New player dx.
     */
    void setPlayerDX(long playerDX)
    {
        this.playerDX = playerDX;
    }

    /**
     * Get current player dy.
     *
     * @return long: Current player dy.
     */
    long getPlayerDY()
    {
        return playerDY;
    }

    /**
     * Set player dy.
     *
     * @param playerDY long: New player dy.
     */
    void setPlayerDY(long playerDY)
    {
        this.playerDY = playerDY;
    }

    boolean isPlayerOnWallLeft()
    {
        return playerOnWallLeft;
    }

    void setPlayerOnWallLeft(boolean playerOnWallLeft)
    {
        this.playerOnWallLeft = playerOnWallLeft;
    }

    boolean isPlayerOnWallRight()
    {
        return playerOnWallRight;
    }

    void setPlayerOnWallRight(boolean playerOnWallRight)
    {
        this.playerOnWallRight = playerOnWallRight;
    }

    long getDDXDueToInput()
    {
        return dDXDueToInput;
    }

    void setDDXDueToInput(long dDXDueToInput)
    {
        this.dDXDueToInput = dDXDueToInput;
    }

    long getMaxDXDueToInput()
    {
        return maxDXDueToInput;
    }

    void setMaxDXDueToInput(long maxDXDueToInput)
    {
        this.maxDXDueToInput = maxDXDueToInput;
    }

    long getInstantaneousJumpDY()
    {
        return instantaneousJumpDY;
    }

    void setInstantaneousJumpDY(long instantaneousJumpDY)
    {
        this.instantaneousJumpDY = instantaneousJumpDY;
    }

    long getInstantaneousWallJumpDY()
    {
        return instantaneousWallJumpDY;
    }

    void setInstantaneousWallJumpDY(long instantaneousWallJumpDY)
    {
        this.instantaneousWallJumpDY = instantaneousWallJumpDY;
    }

    long getInstantaneousWallJumpLeftDX()
    {
        return instantaneousWallJumpLeftDX;
    }

    void setInstantaneousWallJumpLeftDX(long instantaneousWallJumpLeftDX)
    {
        this.instantaneousWallJumpLeftDX = instantaneousWallJumpLeftDX;
    }

    long getInstantaneousWallJumpRightDX()
    {
        return instantaneousWallJumpRightDX;
    }

    void setInstantaneousWallJumpRightDX(long instantaneousWallJumpRightDX)
    {
        this.instantaneousWallJumpRightDX = instantaneousWallJumpRightDX;
    }

    long getDDYDueToGravity()
    {
        return dDYDueToGravity;
    }

    void setDDYDueToGravity(long dDYDueToGravity)
    {
        this.dDYDueToGravity = dDYDueToGravity;
    }

    long getMaxDYDueToGravity()
    {
        return maxDYDueToGravity;
    }

    void setMaxDYDueToGravity(long maxDYDueToGravity)
    {
        this.maxDYDueToGravity = maxDYDueToGravity;
    }

    long getMaxDYOnWall()
    {
        return maxDYOnWall;
    }

    void setMaxDYOnWall(long maxDYOnWall)
    {
        this.maxDYOnWall = maxDYOnWall;
    }
}