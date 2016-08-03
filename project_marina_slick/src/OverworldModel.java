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
    Boolean[][] mapClip; //the grid of passable (clip true) and not passable (clip false)

    /**
     * Default constructor for this model.
     */
    OverworldModel() {

    }

    void setMapModel(int mapWidth, int mapHeight, Boolean[][] mapClip) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.mapClip = mapClip;
    }
}
