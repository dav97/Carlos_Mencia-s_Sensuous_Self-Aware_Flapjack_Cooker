package gameEngine;

import graphicsEngine.VertexArrayObject;
import input.Keyboard;
import utils.Globals;
import utils.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Rick on 10/18/2015.
 */
public class Player extends GameObject {
    public static float WIDTH = Globals.ACTOR_WIDTH;
    public static float HEIGHT = Globals.ACTOR_HEIGHT;

    private VertexArrayObject vao;

    public Vector3f position;
    public Vector3f movement;

    public boolean floor;
    public boolean ceiling;
    public boolean leftWall;
    public boolean rightWall;

    float[] vertices = {
            0.0f, HEIGHT, 0.0f,
            0.0f, 0.0f, 0.0f,
            WIDTH, 0.0f, 0.0f,
            WIDTH, HEIGHT, 0.0f
    };

    public byte[] indices = new byte[] {
            0, 1, 2,
            2, 3, 0
    };

    public Player() {
        this.count = indices.length;
        this.position = new Vector3f();
        vao = new VertexArrayObject(this.vertices, this.indices);
        this.vaoID = vao.getVaoID();
        this.movement = new Vector3f();
    }

    public void update() {
        if (Keyboard.isKeyDown(GLFW_KEY_W)) {
            //position.y += 0.01f;
        }
        if (Keyboard.isKeyDown(GLFW_KEY_S)) {
            //position.y -= 0.01f;
        }
        if (Keyboard.isKeyDown(GLFW_KEY_A)) {
            if (!leftWall) {
                position.x -= (Globals.PIXEL_WIDTH * 4); //0.01f;
            }
        }
        if (Keyboard.isKeyDown(GLFW_KEY_D)) {
            if (!rightWall) {
                position.x += (Globals.PIXEL_WIDTH * 4); //0.01f;
            }
        }
        if (Keyboard.isKeyDown(GLFW_KEY_SPACE)) {
            if (floor && !ceiling) {
                movement.y = 0.035f;
            }
            if (!floor && leftWall) {
                movement.y = 0.02f;
                movement.x = 0.02f;
            }
            if (!floor && rightWall) {
                movement.y = 0.02f;
                movement.x = -0.02f;
            }
        }
        if (floor && movement.y < 0.0f) {
            movement.y = 0.0f;
        }
        if (!floor && movement.y > -0.02f) {
            movement.y -= 0.001f;
        }
        if (ceiling && movement.y > 0.0f) {
            movement.y = 0.0f;
        }
        if (movement.x > 0.0f) {
            movement.x -= 0.001;
        }
        if (movement.x < 0.0f) {
            movement.x += 0.001;
        }
        position.translate(movement);
    }

}
