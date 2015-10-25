package utils;

/**
 * Created by Rick on 10/18/2015.
 */
public class Vector3f {

    public float x, y, z;

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3f() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

    public void translate(Vector3f vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
    }

    public Vector3i toVector3i() {
        return new Vector3i(
                (int)((x + 1) * (Globals.WINDOW_WIDTH / 2)),
                (int)((y + 1) * (Globals.WINDOW_HEIGHT / 2)),
                0
        );
    }

}
