package overworld;

/**
 * Each overworld.Map object will store the data associated with the currently loaded map,
 * including its collision and reference matrix.
 *
 * @author scorple
 * @version dev04
 * @since 2016_0822
 */
public class Map
{
    int         width; //the map width in tiles
    int         height; //the map height in tiles
    long        tileWidth; //the logical width/height of a tile
    Boolean[][] clip; //the grid of passable (clip true) and not passable (clip false)
    String[][]  hooks; //the grid of reference hooks in the current map
    String      hookCurrent; //the reference hook of the current map
    String      hookSpawn; //the reference hook of the spawn point (usually previous map)

    /**
     * Primary constructor. Requires the width in tiles of the map, the height in tiles of the map,
     * the logical width/height of map tiles, a Boolean grid of passable/impassable tiles, and a
     * String grid of reference hooks attached to logical map tiles.
     *
     * @param width     int: The width of the map in tiles.
     * @param height    int: The height of the map in tiles.
     * @param tileWidth long: The logical width/height of a map tile.
     * @param clip      Boolean[][]: The grid of passable (clip true) and impassable (clip false) tiles.
     * @param hooks     String[][]: The grid of reference hooks in the map.
     */
    Map(int width, int height, long tileWidth, Boolean[][] clip, String[][] hooks)
    {
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.clip = clip;
        this.hooks = hooks;

        hookCurrent = "default_map";
        hookSpawn = "spawn";
    }

    /**
     * Get the width of the map in tiles.
     *
     * @return int: The width of the map in tiles.
     */
    int getWidth()
    {
        return width;
    }

    /**
     * Get the height of the map in tiles.
     *
     * @return int: The height of the map in tiles.
     */
    int getHeight()
    {
        return height;
    }

    /**
     * Get the logical width/height of a map tile.
     *
     * @return long: The logical width/height of a map tile.
     */
    long getTileWidth()
    {
        return tileWidth;
    }

    /**
     * Get the grid of passable (clip true) and impassable (clip false) tiles.
     *
     * @return Boolean[][]: The grid of passable (clip true) and impassable (clip false) tiles.
     */
    public Boolean[][] getClip()
    {
        return clip;
    }

    /**
     * Update the grid of passable (clip true) and impassable (clip false) tiles.
     *
     * @param clip Boolean[][]: The new grid of passable (clip true) and impassable (clip false) tiles.
     */
    public void setClip(Boolean[][] clip)
    {
        this.clip = clip;
    }

    /**
     * Get the grid of reference hooks in the map.
     *
     * @return String[][]: The grid of reference hooks in the map.
     */
    public String[][] getHooks()
    {
        return hooks;
    }

    /**
     * Update the grid of reference hooks in the map.
     *
     * @param hooks String[][]: The grid of reference hooks in the map.
     */
    public void setHooks(String[][] hooks)
    {
        this.hooks = hooks;
    }

    /**
     * Get the reference hook for the currently loaded map.
     *
     * @return String: The reference hook for the currently loaded map.
     */
    String getHookCurrent()
    {
        return hookCurrent;
    }

    /**
     * Set the reference hook for the currently loaded map.
     *
     * @param hookCurrent String: The reference hook for the currently loaded map.
     */
    void setHookCurrent(String hookCurrent)
    {
        this.hookCurrent = hookCurrent;
    }

    /**
     * Get the reference hook for the player spawn location.
     *
     * @return String: The reference hook for the player spawn location.
     */
    String getHookSpawn()
    {
        return hookSpawn;
    }

    /**
     * Set the reference hook for the player spawn location.
     *
     * @param hookSpawn String: The reference hook for the player spawn location.
     */
    void setHookSpawn(String hookSpawn)
    {
        this.hookSpawn = hookSpawn;
    }
}
