package utils;

/**
 * Created by Rick on 10/29/2015.
 */
public class Vector2i {

    public int x;
    public int y;

    public Vector2i() {
        x = 0;
        y = 0;
    }

    public Vector2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2i(Vector2i vec) {
        x = vec.x;
        y = vec.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void translate(Vector2i vec) {
        x += vec.getX();
        y += vec.getY();
    }

}
