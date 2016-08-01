package utils;

/**
 * Created by Rick on 10/29/2015.
 */
public class Vector2f {

    public float x;
    public float y;

    public Vector2f() {
        x = 0;
        y = 0;
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(Vector2f vec) {
        x = vec.x;
        y = vec.y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void translate(Vector2f vec) {
        x += vec.getX();
        y += vec.getY();
    }

}
