package main;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.*;

import gameEngine.Stage;
import input.Keyboard;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.opengl.GLContext;
import utils.Globals;

/**
 * Created by Rick on 10/18/2015.
 */
public class Driver {

    public boolean running = false;
    public static int WIDTH = Globals.WINDOW_WIDTH;
    public static int HEIGHT = Globals.WINDOW_HEIGHT;
    public long window;

    private GLFWKeyCallback keyCallback;

    Stage stage;

    public void init() {
        this.running = true;

        //start glfw start

        if (glfwInit() != GL_TRUE) {
            System.err.println("GLFW init failed");
        }

        window = glfwCreateWindow(WIDTH, HEIGHT, "Project Marina", NULL, NULL);

        if (window == NULL) {
            System.err.println("Could not create window");
        }

        glfwSetKeyCallback(window, keyCallback = new Keyboard());

        glfwMakeContextCurrent(window);
        glfwShowWindow(window);

        GLContext.createFromCurrent();
        System.out.println("OpenGL: " + glGetString(GL_VERSION));

        //finish glfw setup

        stage = new Stage();
    }

    public void render() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        stage.draw();

        glfwSwapBuffers(window);
    }

    public void update() {
        glfwPollEvents();

        stage.update();
    }

    public void run() {
        init();

        long lastTime = System.nanoTime();
        double delta = 0.0;
        double ns = 1000000000.0 / 60.0;
        long timer = System.currentTimeMillis();
        int updates = 0;
        int frames = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            if (delta >= 1.0) {
                update();
                updates++;
                delta--;
            }

            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println(updates + " UPS, " + frames + " FPS");
                updates = 0;
                frames = 0;
            }

            if (glfwWindowShouldClose(window) == GL_TRUE) {
                running = false;
            }

            if (Keyboard.isKeyDown(GLFW_KEY_ESCAPE)) {
                running = false;
                System.exit(0);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Running...");
        Driver driver = new Driver();
        driver.run();
    }

}
