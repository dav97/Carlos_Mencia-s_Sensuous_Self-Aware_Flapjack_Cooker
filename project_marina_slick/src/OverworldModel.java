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

    int playerWidth;
    int playerHeight;
    int playerX;
    int playerY;
    int playerDX;
    int playerDY;

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

    /**
     * Get the logical X coordinate of the player.
     *
     * @return int: The logical X coordinate of the player.
     */
    int getPlayerX() {
        return playerX;
    }

    /**
     * Get the logical Y coordinate of the player.
     *
     * @return int: The logical Y coordinate of the player.
     */
    int getPlayerY() {
        return playerY;
    }
}
