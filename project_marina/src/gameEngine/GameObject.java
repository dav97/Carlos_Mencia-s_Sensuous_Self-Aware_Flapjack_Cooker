package gameEngine;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import graphicsEngine.VertexArrayObject;

public class GameObject {

    public int vaoID;
    public int count;
    public float SIZE = 1.0f;

    public GameObject() {

    }

    public void draw() {
        glBindVertexArray(this.vaoID);
        glEnableVertexAttribArray(0);
        glDrawElements(GL_TRIANGLES, count, GL_UNSIGNED_BYTE, 0);
        glDisableVertexAttribArray(0);
        glBindVertexArray(0);
    }

}