package utils;

/**
 * Created by Rick on 10/19/2015.
 */
public class Vector3i {

    public int x, y, z;

    public Vector3i(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3i() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public void translate(Vector3i vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
    }

    public Vector3f toVector3f() {
        return new Vector3f(
                ((float)x / (Globals.WINDOW_WIDTH / 2)) - 1,
                ((float)y / (Globals.WINDOW_HEIGHT / 2)) - 1,
                0.0f
        );
    }

}
