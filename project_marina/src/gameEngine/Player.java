package gameEngine;

import graphicsEngine.VertexArrayObject;
import input.Keyboard;
import utils.Globals;
import utils.Vector3f;
import utils.Vector3i;

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
    public Vector3i iposition;
    public Vector3i imovement;

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
        this.iposition = new Vector3i();
        vao = new VertexArrayObject(this.vertices, this.indices);
        this.vaoID = vao.getVaoID();
        this.movement = new Vector3f();
        this.imovement = new Vector3i();
    }

    public void update() {
        if (Keyboard.isKeyDown(GLFW_KEY_W)) {
            //position.y += 0.01f;
        }
        if (Keyboard.isKeyDown(GLFW_KEY_S)) {
            //position.y -= 0.01f;
        }
        if (Keyboard.isKeyDown(GLFW_KEY_A)) {
            if (!leftWall && floor) {
                //position.x -= (Globals.PIXEL_WIDTH * 4); //0.01f;
                //iposition.x -= 4;
                imovement.x = -4;
            }
            if (!floor && imovement.x > -4) {
                imovement.x -= 1;
            }
        }
        if (Keyboard.isKeyDown(GLFW_KEY_D)) {
            if (!rightWall && floor) {
                //position.x += (Globals.PIXEL_WIDTH * 4); //0.01f;
                //iposition.x += 4;
                imovement.x = 4;
            }
            if (!floor && imovement.x < 4) {
                imovement.x += 1;
            }
        }
        if (Keyboard.isKeyDown(GLFW_KEY_SPACE)) {
            if (floor && !ceiling) {
                //movement.y = 0.035f;
                imovement.y = 18;
            }
            if (!floor && leftWall) {
                //movement.y = 0.02f;
                imovement.y = 18;
                //movement.x = 0.02f;
                imovement.x = 12;
            }
            if (!floor && rightWall) {
                //movement.y = 0.02f;
                imovement.y = 18;
                //movement.x = -0.02f;
                imovement.x = -12;
            }
        }
        //if (floor && movement.y < 0.0f) {
        //    movement.y = 0.0f;
        //}
        //if (ceiling && movement.y > 0.0f) {
        //    movement.y = 0.0f;
        //}
        if ((floor && imovement.y < 0) ||
                (ceiling && imovement.y > 0)) {
            imovement.y = 0;
        }
        if ((leftWall && imovement.x < 0) ||
                (rightWall && imovement.x > 0)) {
            imovement.x = 0;
        }
        //if (!floor && movement.y > -0.02f) {
        //    movement.y -= 0.001f;
        //}
        if (!floor && imovement.y > -12) {
            imovement.y -= 1;
        }
        //if (movement.x > 0.0f) {
        //    movement.x -= 0.001;
        //}
        if (!Keyboard.isKeyDown(GLFW_KEY_D) && imovement.x > 0) {
            if (floor) {
                imovement.x -= 2;
            }
            else {
                imovement.x -= 1;
            }
        }
        //if (movement.x < 0.0f) {
        //    movement.x += 0.001;
        //}
        if (!Keyboard.isKeyDown(GLFW_KEY_A) && imovement.x < 0) {
            if (floor) {
                imovement.x += 2;
            }
            else {
                imovement.x += 1;
            }
        }
        //position.translate(movement);
        iposition.translate(imovement);
    }

}
