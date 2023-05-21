package render;

import lwjglutils.OGLTextRenderer;
import lwjglutils.OGLTextureCube;
import lwjglutils.ShaderUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import solids.Cube;
import solids.Grid;
import transforms.*;

import java.io.IOException;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

public class Renderer extends AbstractRenderer {
    private Camera camera;
    double ox, oy;
    boolean mouseButton1 = false;
    private Mat4 projection;
    private Mat4 model = new Mat4Identity(), skyModel = new Mat4Identity();
    private int shaderProgram, shaderProgram2;
    private int loc_uModel, loc_uView, loc_uProj, loc_uSkyModel;
    private int loc2_uModel, loc2_uView, loc2_uProj, loc2_uSkyModel;
    private Grid grid;
    private Cube cube;
    private int modeObject = 0;
    private int m = 500;
    OGLTextureCube texture;

    public Renderer(int width, int height) {
        super(width, height);
    }

    @Override
    public void init() {
        camera = new Camera()
                .withPosition(new Vec3D(10.f, 10f, 5f))
                .withAzimuth(Math.PI * 1.25)
                .withZenith(Math.PI * -0.125)
                .withFirstPerson(true)
                .withRadius(3);

        projection = new Mat4PerspRH(Math.PI / 3, 600 / (float) 800, 0.1f, 50.f);

        glShadeModel(GL_SMOOTH);

        glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

        initTexture();

        initSkyBox();
        initGrid();

        textRenderer = new OGLTextRenderer(width, height);
    }

    @Override
    public void display() {
        glViewport(0, 0, width, height);

        glDisable(GL_CULL_FACE);
        glEnable(GL_DEPTH_TEST);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        drawSkybox();
        drawGrid();

        String text = "Some text";

        textRenderer.addStr2D(10, 20, text);
    }

    private GLFWKeyCallback keyCallback = new GLFWKeyCallback() {
        @Override
        public void invoke(long window, int key, int scancode, int action, int mods) {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true);

            if (action == GLFW_PRESS || action == GLFW_REPEAT) {
                switch (key) {
                    // Movement
                    case GLFW_KEY_W:
                        camera = camera.forward(0.5);
                        break;
                    case GLFW_KEY_S:
                        camera = camera.backward(0.5);
                        break;
                    case GLFW_KEY_A:
                        camera = camera.left(0.5);
                        break;
                    case GLFW_KEY_D:
                        camera = camera.right(0.5);
                        break;
                    case GLFW_KEY_Q:
                        camera = camera.up(0.5);
                        break;
                    case GLFW_KEY_E:
                        camera = camera.down(0.5);
                        break;
                    // Change object
                    case GLFW_KEY_O:
                        modeObject = (++modeObject) % 2;
                        break;
                }
            }
        }
    };

    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
        @Override
        public void invoke(long window, double x, double y) {
            if (mouseButton1) {
                camera = camera.addAzimuth(Math.PI * (ox - x) / width)
                        .addZenith(Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback() {
        @Override
        public void invoke(long window, int button, int action, int mods) {
            mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                mouseButton1 = true;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                ox = xBuffer.get(0);
                oy = yBuffer.get(0);
            }

            if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE) {
                mouseButton1 = false;
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);
                camera = camera.addAzimuth(Math.PI * (ox - x) / width)
                        .addZenith(Math.PI * (oy - y) / width);
                ox = x;
                oy = y;
            }
        }
    };

    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override
        public void invoke(long window, double dx, double dy) {
        }
    };

    @Override
    public GLFWKeyCallback getKeyCallback() {
        return keyCallback;
    }

    @Override
    public GLFWScrollCallback getScrollCallback() {
        return scrollCallback;
    }

    @Override
    public GLFWMouseButtonCallback getMouseCallback() {
        return mbCallback;
    }

    @Override
    public GLFWCursorPosCallback getCursorCallback() {
        return cpCallbacknew;
    }

    public void initTexture() {
        String[] names = {
                "textures/snow_positive_x.jpg",
                "textures/snow_negative_x.jpg",
                "textures/snow_positive_y.jpg",
                "textures/snow_negative_y.jpg",
                "textures/snow_positive_z.jpg",
                "textures/snow_negative_z.jpg"
        };

        try {
            texture = new OGLTextureCube(names);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initGrid() {
        shaderProgram = ShaderUtils.loadProgram("/shaders/Main/Main");

        loc_uModel = glGetUniformLocation(shaderProgram, "uModel");
        loc_uView = glGetUniformLocation(shaderProgram, "uView");
        loc_uProj = glGetUniformLocation(shaderProgram, "uProj");

        loc_uSkyModel = glGetUniformLocation(shaderProgram, "uSkyModel");

        texture.bind(shaderProgram, "uTextureID", 0);

        model = model.mul(new Mat4Transl(-0.5, -0.5, -0.5)).mul(new Mat4RotX(Math.PI / 2));

        grid = new Grid(m, m);
    }

    public void initSkyBox() {
        shaderProgram2 = ShaderUtils.loadProgram("/shaders/Skybox/Main");

        loc2_uModel = glGetUniformLocation(shaderProgram2, "uModel");
        loc2_uView = glGetUniformLocation(shaderProgram2, "uView");
        loc2_uProj = glGetUniformLocation(shaderProgram2, "uProj");

        loc2_uSkyModel = glGetUniformLocation(shaderProgram2, "uSkyModel");

        texture.bind(shaderProgram2, "uTextureID", 0);

        skyModel = skyModel.mul(new Mat4Transl(-0.5, -0.5, -0.5)).mul(new Mat4Scale(40)).mul(new Mat4RotX(Math.PI / 2));

        cube = new Cube();
    }

    void drawGrid() {
        glUseProgram(shaderProgram);

        glUniformMatrix4fv(loc_uModel, false, model.floatArray());
        glUniformMatrix4fv(loc_uView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(loc_uProj, false, projection.floatArray());

        glUniformMatrix4fv(loc_uSkyModel, false, skyModel.floatArray());

        grid.getBuffers().draw(GL_TRIANGLES, shaderProgram);
    }

    void drawSkybox() {
        glUseProgram(shaderProgram2);

        glUniformMatrix4fv(loc2_uModel, false, model.floatArray());
        glUniformMatrix4fv(loc2_uView, false, camera.getViewMatrix().floatArray());
        glUniformMatrix4fv(loc2_uProj, false, projection.floatArray());

        glUniformMatrix4fv(loc2_uSkyModel, false, skyModel.floatArray());

        cube.getBuffers().draw(GL_TRIANGLES, shaderProgram2);
    }
}
