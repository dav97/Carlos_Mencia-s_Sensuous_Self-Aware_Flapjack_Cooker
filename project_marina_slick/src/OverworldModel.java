/**
 * OverworldModel acts as a general model for the overworld game state.
 *
 * @author Scorple
 * @version 1.0
 * @since 2016.08.01
 */
class OverworldModel {
    //TODO: move these to a separate class
    int mapWidth; //the map width in tiles
    int mapHeight; //the map height in tiles
    int tileWidth;
    Boolean[][] mapClip; //the grid of passable (clip true) and not passable (clip false)
    String mapHookCurrent;
    String mapHookSpawn;
    String[][] mapHooks;

    //TODO: move these to a separate class
    int playerWidth;
    int playerHeight;
    //storing floats for this makes movement and drawing easier,
    //may make collision handling much harder...
    float playerX;
    float playerY;
    float playerDX;
    float playerDY;

    boolean playerOnWallLeft;
    boolean playerOnWallRight;

    float dDXDueToInput;
    float maxDXDueToInput;

    float instantaneousJumpDY;
    float instantaneousWallJumpDY;
    float instantaneousWallJumpLeftDX;
    float instantaneousWallJumpRightDX;

    float dDYDueToGravity;
    float maxDYDueToGravity;
    float maxDYOnWall;

    /**
     * Default constructor for this model.
     */
    OverworldModel() {

    }

    /**
     * Set the map model.
     *
     * @param mapWidth  int: The width in tiles of the map.
     * @param mapHeight int: The height in tiles of the map.
     * @param tileWidth int: The raw width of a tile graphic
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
    void setupMapModel(int mapWidth, int mapHeight, int tileWidth, Boolean[][] mapClip, String[][] mapHooks) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.tileWidth = tileWidth;
        this.mapClip = mapClip;
        this.mapHooks = mapHooks;
    }

    String getMapHookCurrent() {
        return mapHookCurrent;
    }

    void setMapHookCurrent(String mapHookCurrent) {
        this.mapHookCurrent = mapHookCurrent;
    }

    String getMapHookSpawn() {
        return mapHookSpawn;
    }

    void setMapHookSpawn(String mapHookPrevious) {
        this.mapHookSpawn = mapHookPrevious;
    }

    /**
     * Create the player model and set default location and
     * movement values.
     *
     * @param playerWidth  int: The raw width of the player graphic.
     * @param playerHeight int: The raw height of the player graphic.
     */
    void setupPlayerModel(int playerWidth, int playerHeight) {
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
    void spawnPlayer(String hook) {
        System.out.println("Spawning player at hook <" + hook + ">");

        for (int x = 0; x < mapWidth; ++x) {
            for (int y = 0; y < mapHeight; ++y) {
                if (mapHooks[x][y].equals(hook)) {
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
     * @param dX float: The proposed amount of player movement.
     *
     * @return float: The player distance from the tile she will
     * collide with if she moved the proposed amount.
     */
    float getHorizontalCollisionDistanceByDX(float dX) {
        float playerXTest = playerX;

        if (dX < 0) {
            playerXTest = playerX + dX;
        } else if (dX > 0) {
            playerXTest = playerX + playerWidth + dX;
        }

        //map bounds checking
        if (playerXTest < 0) {
            return -(playerX);
        }
        if (playerXTest > (((mapWidth) * tileWidth))) {
            return (((mapWidth) * tileWidth) - (playerX + playerWidth));
        }
        //end map bounds checking

        int maxMapXCollisionTiles = 1 + ((playerHeight > tileWidth) ? (playerHeight / tileWidth) : 0) + 1; //check the mapY, at minimum, level with the top and bottom of the player

        int mapXTest = (int) (playerXTest / tileWidth);
        int[] mapYTest = new int[maxMapXCollisionTiles];

        mapYTest[0] = (int) (playerY / tileWidth); //the first mapY to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxMapXCollisionTiles - 1); ++i) {
            mapYTest[i] = mapYTest[i - 1] + 1; //for every mapY we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapYTest[maxMapXCollisionTiles - 1] = (int) (((playerY + playerHeight) - 1) / tileWidth); //the last mapY to test is the grid coordinate level with the bottom of the player

        for (int i = 0; i < maxMapXCollisionTiles; ++i) { //check collision on every tile to the left or right of the player within a distance of dX
            if (!mapClip[mapXTest][mapYTest[i]]) {
                float collisionDX = 0;
                if (dX < 0) {
                    collisionDX = (((mapXTest + 1) * tileWidth)) - playerX; //subtract the X location of the left side of the player from the X location of the right side of the tile
                    //System.out.println("Collision detected left, distance (dx) to left tile is: " + collisionDX);
                    //System.out.println("playerX is " + playerX + ", playerY " + playerY);
                    //System.out.println("mapXTest is " + mapXTest + ", " + mapXTest * tileWidth + ", mapYTest " + mapYTest[i] + ", " + mapYTest[i] * tileWidth);
                } else if (dX > 0) {
                    collisionDX = ((mapXTest) * tileWidth) - ((playerX + playerWidth)); //subtract the X location of the right side of the player from the X location of the left side of the tile
                    //System.out.println("Collision detected right, distance (dx) to right tile is: " + collisionDX);
                    //System.out.println("playerX is " + playerX + ", playerY " + playerY);
                    //System.out.println("mapXTest is " + mapXTest + ", " + mapXTest * tileWidth + ", mapYTest " + mapYTest[i] + ", " + mapYTest[i] * tileWidth);
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
    boolean isPlayerCollisionLeft() {
        return (getHorizontalCollisionDistanceByDX(OverworldGlobals.STANDARD_COLLISION_CHECK_DISTANCE_LEFT) == 0.0f);
    }

    /**
     * Check for collision immediately to the right of the player.
     * (i.e. she is touching a wall to the right)
     *
     * @return boolean: True if there is collision immediately right of the player.
     */
    boolean isPlayerCollisionRight() {
        return (getHorizontalCollisionDistanceByDX(OverworldGlobals.STANDARD_COLLISION_CHECK_DISTANCE_RIGHT) == 0.0f);
    }

    /**
     * Collision checking method. Given a proposed amount of
     * player movement, check for map tiles within that distance
     * of the player. If there are any, return that distance.
     * Applies to movement up or down.
     *
     * @param dY float: The proposed amount of player movement.
     *
     * @return float: The player distance from the tile she will
     *         collide with if she moved the proposed amount.
     */
    float getVerticalCollisionDistanceByDY(float dY) {
        float playerYTest = playerY;

        if (dY < 0) {
            playerYTest = playerY + dY;
        } else if (dY > 0) {
            playerYTest = playerY + playerHeight + dY;
        }

        //map bounds checking
        if (playerYTest < 0) {
            return -(playerY);
        }
        if (playerYTest > (((mapHeight) * tileWidth))) {
            return (((mapHeight) * tileWidth) - (playerY + playerHeight));
        }
        //end map bounds checking

        int maxMapYCollisionTiles = 1 + ((playerWidth > tileWidth) ? (playerWidth / tileWidth) : 0) + 1; //check the mapX, at minimum, level with the left and right side of the player

        int[] mapXTest = new int[maxMapYCollisionTiles];
        int mapYTest = (int) (playerYTest / tileWidth);

        mapXTest[0] = (int) (playerX / tileWidth); //the first mapX to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxMapYCollisionTiles - 1); ++i) {
            mapXTest[i] = mapXTest[i - 1] + 1; //for every mapX we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapXTest[maxMapYCollisionTiles - 1] = (int) (((playerX + playerWidth) - 1) / tileWidth); //the last mapX to test is the grid coordinate level with the bottom of the player

        for (int i = 0; i < maxMapYCollisionTiles; ++i) { //check collision on every tile to the left or right of the player within a distance of dX
            if (!mapClip[mapXTest[i]][mapYTest]) {
                float collisionDY = 0;
                if (dY < 0) {
                    collisionDY = (((mapYTest + 1) * tileWidth)) - playerY; //subtract the Y location of the top of the player from the Y location of the bottom of the tile
                } else if (dY > 0) {
                    collisionDY = ((mapYTest) * tileWidth) - ((playerY + playerHeight)); //subtract the Y location of the bottom of the player from the Y location of the top of the tile
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
    boolean isPlayerCollisionUp() {
        return (getVerticalCollisionDistanceByDY(OverworldGlobals.STANDARD_COLLISION_CHECK_DISTANCE_UP) == 0.0f);
    }

    /**
     * Check for collision immediately below the player.
     * (i.e. she is standing on solid ground)
     *
     * @return boolean: True if there is collision immediately below the player.
     */
    boolean isPlayerCollisionDown() {
        return (getVerticalCollisionDistanceByDY(OverworldGlobals.STANDARD_COLLISION_CHECK_DISTANCE_DOWN) == 0.0f);
    }
    
    float[] getDiagonalCollisionDistanceByDXAndDY(float dX, float dY) {
		float[] dXdY = new float[2];
        float playerXTest = 0.0f;
        float playerYTest = 0.0f;
        int mapXTest = -1;
		int mapYTest = -1;
        float collisionDX = 0.0f;
        float collisionDY = 0.0f;

        if (dX < 0.0f) {
            playerXTest = playerX + dX;
        } else if (dX > 0.0f) {
            playerXTest = playerX + playerWidth + dX;
        }
        
        if (dY < 0.0f) {
            playerYTest = playerY + dY;
        } else if (dY > 0.0f) {
            playerYTest = playerY + playerHeight + dY;
        }
		
		if (dX < 0.0f || dX > 0.0f) {
			mapXTest = (int) (playerXTest / tileWidth);
		}
		
		if (dY < 0.0f || dY > 0.0f) {
			mapYTest = (int) (playerYTest / tileWidth);
		}
		
		if (mapXTest != -1 && mapYTest != -1) {
			if (!mapClip[mapXTest][mapYTest]) {
                System.out.println("Corner collision detected");
                System.out.println("proposedDX:<" + dX + "> proposedDY:<" + dY + ">");
                System.out.println("playerX:<" + playerX + "> playerY:<" + playerY + ">");
                System.out.println("playerWidth:<" + playerWidth + "> playerHeight:<" + playerHeight + ">");
                System.out.println("playerXTest:<" + playerXTest + "> playerYTest:<" + playerYTest + ">");
                System.out.println("mapXTest:<" + mapXTest + "> mapYTest:<" + mapYTest + ">");
                if (Math.abs(dX) >= Math.abs(dY)) {
					//if the scalar change in X is greater or equal to the scalar change in Y,
					//prioritize moving in the X direction - below or above the tile
					//we are hitting the corner of
                    System.out.println("Favoring dX");
                    dXdY[0] = dX;
                    if (dY < 0.0f) {
						collisionDY = (((mapYTest + 1) * tileWidth)) - playerY; //subtract the Y location of the top of the player from the Y location of the bottom of the tile
					} else if (dY > 0.0f) {
						collisionDY = ((mapYTest) * tileWidth) - ((playerY + playerHeight)); //subtract the Y location of the bottom of the player from the Y location of the top of the tile
					}
                    //dXdY[1] = collisionDY;
                    dXdY[1] = 0.0f;
                }
				else {
					//if the scalar change in X is less than the scalar change in Y,
					//prioritize moving in the Y direction - left or right of the tile
					//we are hitting the corner of
                    System.out.println("Favoring dY");
                    if (dX < 0.0f) {
                        System.out.println((mapXTest + 1) * tileWidth + " - " + (playerX));
                        collisionDX = (((mapXTest + 1) * tileWidth)) - playerX; //subtract the X location of the left side of the player from the X location of the right side of the tile
					} else if (dX > 0.0f) {
                        System.out.println(mapXTest * tileWidth + " - " + (playerX + playerWidth - 1));
                        collisionDX = ((mapXTest) * tileWidth) - ((playerX + playerWidth -
                            1)); //subtract the X location of the right side of the player from the X location of the left side of the tile
                    }
                    //dXdY[0] = collisionDX;
                    dXdY[0] = 0.0f;
                    dXdY[1] = dY;
                }
                System.out.println("dXdY[0]:<" + dXdY[0] + "> dXdY[1]:<" + dXdY[1] + ">");
            }
			else {
                dXdY[0] = dX;
                dXdY[1] = dY;
            }
		}
		else {
            dXdY[0] = dX;
            dXdY[1] = dY;
        }
		
		return dXdY;
	}

    /**
     * Get a list of the hooks attached to all tiles with player is intersecting with.
     *
     * @return String[]: A list containing the hook property of every tile the player
     * is intersecting with. WARNING: MAY CONTAIN DUPLICATES, ESPECIALLY IF CHECKED
     * DURING JUMP.
     */
    String[] getIntersectingTileHooks() {
        int maxPlayerIntersectionTilesTopToBottom = 1 + ((playerHeight > tileWidth) ? (playerHeight / tileWidth) : 0) + 1; //check the mapY, at minimum, level with the top and bottom of the player
        int maxPlayerIntersectionTilesLeftToRight = 1 + ((playerWidth > tileWidth) ? (playerWidth / tileWidth) : 0) + 1; //check the mapX, at minimum, level with the left and right side of the player
        int maxIntersectingTiles = maxPlayerIntersectionTilesTopToBottom * maxPlayerIntersectionTilesLeftToRight;

        int[] mapXTest = new int[maxPlayerIntersectionTilesLeftToRight];

        //get every mapX grid coordinate we will need to check for hooks
        mapXTest[0] = (int) (playerX / tileWidth); //the first mapX to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxPlayerIntersectionTilesLeftToRight - 1); ++i) {
            mapXTest[i] = mapXTest[i - 1] + 1; //for every mapX we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapXTest[maxPlayerIntersectionTilesLeftToRight - 1] = (int) (((playerX + playerWidth) - 1) / tileWidth); //the last mapX to test is the grid coordinate level with the bottom of the player

        int[] mapYTest = new int[maxPlayerIntersectionTilesTopToBottom];

        //get every mapY grid coordinate we will need to check for hooks
        mapYTest[0] = (int) (playerY / tileWidth); //the first mapY to test is the grid coordinate level with the top of the player
        for (int i = 1; i < (maxPlayerIntersectionTilesTopToBottom - 1); ++i) {
            mapYTest[i] = mapYTest[i - 1] + 1; //for every mapY we need to test in between the top and bottom of the player, add one to the previous grid Y coordinate
        }
        mapYTest[maxPlayerIntersectionTilesTopToBottom - 1] = (int) (((playerY + playerHeight) - 1) / tileWidth); //the last mapX to test is the grid coordinate level with the bottom of the player

        String[] hooks = new String[maxIntersectingTiles];

        //fill the String array with the hooks of every tile intersecting with the player
        for (int x = 0; x < maxPlayerIntersectionTilesLeftToRight; ++x) {
            for (int y = 0; y < maxPlayerIntersectionTilesTopToBottom; ++y) {
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
    int getPlayerWidth() {
        return playerWidth;
    }

    /**
     * Get the raw player height in pixels.
     *
     * @return int: The raw player width in pixels.
     */
    int getPlayerHeight() {
        return playerHeight;
    }

    /**
     * Get the logical X coordinate of the player.
     *
     * @return float: The logical X coordinate of the player.
     */
    float getPlayerX() {
        return playerX;
    }

    /**
     * Set the logical player X coordinate.
     *
     * @param playerX float: The logical X coordinate of the player.
     */
    void setPlayerX(float playerX) {
        this.playerX = playerX;
    }

    /**
     * Get the logical Y coordinate of the player.
     *
     * @return float: The logical Y coordinate of the player.
     */
    float getPlayerY() {
        return playerY;
    }

    /**
     * Set the logical player Y coordinate.
     *
     * @param playerY float: The logical Y coordinate of the player.
     */
    void setPlayerY(float playerY) {
        this.playerY = playerY;
    }

    /**
     * Set the logical player X and Y coordinate.
     *
     * @param playerX float: The logical X coordinate of the player.
     * @param playerY float: The logical Y coordinate of the player.
     */
    void setPlayerLocation(float playerX, float playerY) {
        this.playerX = playerX;
        this.playerY = playerY;
    }

    /**
     * Get current player dx.
     *
     * @return float: Current player dx.
     */
    float getPlayerDX() {
        return playerDX;
    }

    /**
     * Set player dx.
     *
     * @param playerDX float: New player dx.
     */
    void setPlayerDX(float playerDX) {
        this.playerDX = playerDX;
    }

    /**
     * Get current player dy.
     *
     * @return float: Current player dy.
     */
    float getPlayerDY() {
        return playerDY;
    }

    /**
     * Set player dy.
     *
     * @param playerDY float: New player dy.
     */
    void setPlayerDY(float playerDY) {
        this.playerDY = playerDY;
    }

    boolean isPlayerOnWallLeft() {
        return playerOnWallLeft;
    }

    void setPlayerOnWallLeft(boolean playerOnWallLeft) {
        this.playerOnWallLeft = playerOnWallLeft;
    }

    boolean isPlayerOnWallRight() {
        return playerOnWallRight;
    }

    void setPlayerOnWallRight(boolean playerOnWallRight) {
        this.playerOnWallRight = playerOnWallRight;
    }

    float getDDXDueToInput() {
        return dDXDueToInput;
    }

    void setDDXDueToInput(float dDXDueToInput) {
        this.dDXDueToInput = dDXDueToInput;
    }

    float getMaxDXDueToInput() {
        return maxDXDueToInput;
    }

    void setMaxDXDueToInput(float maxDXDueToInput) {
        this.maxDXDueToInput = maxDXDueToInput;
    }

    float getInstantaneousJumpDY() {
        return instantaneousJumpDY;
    }

    void setInstantaneousJumpDY(float instantaneousJumpDY) {
        this.instantaneousJumpDY = instantaneousJumpDY;
    }

    float getInstantaneousWallJumpDY() {
        return instantaneousWallJumpDY;
    }

    void setInstantaneousWallJumpDY(float instantaneousWallJumpDY) {
        this.instantaneousWallJumpDY = instantaneousWallJumpDY;
    }

    float getInstantaneousWallJumpLeftDX() {
        return instantaneousWallJumpLeftDX;
    }

    void setInstantaneousWallJumpLeftDX(float instantaneousWallJumpLeftDX) {
        this.instantaneousWallJumpLeftDX = instantaneousWallJumpLeftDX;
    }

    float getInstantaneousWallJumpRightDX() {
        return instantaneousWallJumpRightDX;
    }

    void setInstantaneousWallJumpRightDX(float instantaneousWallJumpRightDX) {
        this.instantaneousWallJumpRightDX = instantaneousWallJumpRightDX;
    }

    float getDDYDueToGravity() {
        return dDYDueToGravity;
    }

    void setDDYDueToGravity(float dDYDueToGravity) {
        this.dDYDueToGravity = dDYDueToGravity;
    }

    float getMaxDYDueToGravity() {
        return maxDYDueToGravity;
    }

    void setMaxDYDueToGravity(float maxDYDueToGravity) {
        this.maxDYDueToGravity = maxDYDueToGravity;
    }

    float getMaxDYOnWall() {
        return maxDYOnWall;
    }

    void setMaxDYOnWall(float maxDYOnWall) {
        this.maxDYOnWall = maxDYOnWall;
    }
}
