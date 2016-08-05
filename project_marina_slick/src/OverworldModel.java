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
    }

    /**
     * Set the player location and default movement values
     * based on the location of the given hook in the map.
     *
     * @param hook String: A reference point in the map.
     */
    void spawnPlayer(String hook) {
        for (int x = 0; x < mapWidth; ++x) {
            for (int y = 0; y < mapHeight; ++y) {
                if (mapHooks[x][y].equals(hook)) {
                    playerX = x * tileWidth;
                    playerY = y * tileWidth;
                    playerDX = 0;
                    playerDY = 0;
                    break;
                }
            }
        }
    }

    float getHorizontalCollisionByDX(float dX) {
        //TODO: need to check for collision at middle and bottom of player too, currently just top
        float playerXTest = playerX;

        if (dX < 0) {
            playerXTest = playerX + dX;
        } else if (dX > 0) {
            playerXTest = playerX + playerWidth + dX;
        }

        int mapXTest = (int) (playerXTest / tileWidth);
        int mapYTest = (int) (playerY / tileWidth);

        if (!mapClip[mapXTest][mapYTest]) {
            float collisionDX = 0;
            if (dX < 0) {
                //I do not understand why this works TODO: figure it out
                collisionDX = ((mapXTest + 1) * tileWidth) - playerX;
            } else if (dX > 0) {
                //I do not understand why this works TODO: figure it out
                collisionDX = ((mapXTest - 1) * tileWidth) - playerX;
            }
            System.out.println("Collision detected, distance (dx) to tile is: " + collisionDX);
            System.out.println("playerX is " + playerX + ", playerY " + playerY);
            System.out.println("mapXTest is " + mapXTest + ", " + mapXTest * tileWidth + ", mapYTest " + mapYTest + ", " + mapYTest * tileWidth);
            return collisionDX;
        }

        return dX;
    }

    float getVerticalCollisionByDY() {
        return playerDY;
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

    float getPlayerDX() {
        return playerDX;
    }

    void setPlayerDX(float playerDX) {
        this.playerDX = playerDX;
    }
}
