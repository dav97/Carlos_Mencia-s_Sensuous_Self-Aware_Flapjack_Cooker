package gameObjects;

import utils.Vector2f;
import utils.Vector2i;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Created by Rick on 10/30/2015.
 */
public class BoundingBox extends Rectangle2D.Float {

    public BoundingBox(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public BoundingBox(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public BoundingBox(Vector2i pos, int width, int height) {
        x = pos.getX();
        y = pos.getY();
        this.width = width;
        this.height = height;
    }

    public BoundingBox(Vector2f pos, float width, float height) {
        x = pos.getX();
        y = pos.getY();
        this.width = width;
        this.height = height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setPosition(Vector2i pos) {
        x = pos.getX();
        y = pos.getY();
    }

    public void setPosition(Vector2f pos) {
        x = pos.getX();
        y = pos.getY();
    }

    public BoundingBox below(int buffer) {
        return new BoundingBox(x, y + buffer, width, height);
    }

    public BoundingBox above(int buffer) {
        return new BoundingBox(x, y - buffer, width, height);
    }

    public BoundingBox left(int buffer) {
        return new BoundingBox(x - buffer, y, width, height);
    }

    public BoundingBox right(int buffer) {
        return new BoundingBox(x + buffer, y, width, height);
    }

    public int distanceBelow(BoundingBox r) {
        return (int) (r.y - (y + height));
    }

    public int distanceAbove(BoundingBox r) {
        return (int) (y - (r.y + r.height));
    }

    public int distanceLeft(BoundingBox r) {
        return (int) (x - (r.x + r.width));
    }

    public int distanceRight(BoundingBox r) {
        return (int) (r.x - (x + width));
    }

}
