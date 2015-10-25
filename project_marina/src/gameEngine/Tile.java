package gameEngine;

import graphicsEngine.VertexArrayObject;
import utils.Globals;
import utils.Vector3f;
import utils.Vector3i;

/**
 * Created by Rick on 10/18/2015.
 */
public class Tile extends GameObject {

    private VertexArrayObject vao;

    public static float WIDTH = Globals.TILE_WIDTH;
    public static float HEIGHT = Globals.TILE_HEIGHT;

    public Vector3f position;
    public Vector3i iposition;

    float[] verticies = {
            0.0f, HEIGHT, 0.0f,
            0.0f, 0.0f, 0.0f,
            WIDTH, 0.0f, 0.0f,
            WIDTH, HEIGHT, 0.0f
    };

    public byte[] indicies = new byte[] {
            0, 1, 2,
            2, 3, 0
    };

    public Tile() {
        this.count = indicies.length;
        this.position = new Vector3f();
        this.iposition = new Vector3i();
        vao = new VertexArrayObject(this.verticies, this.indicies);
        this.vaoID = vao.getVaoID();
    }

}
