package game;

import engine.GameItem;
import engine.IGameLogic;
import engine.MouseInput;
import engine.Window;
import engine.graph.Camera;
import engine.graph.Mesh;
import engine.graph.OBJLoader;
import engine.graph.Texture;
import org.joml.Vector2f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class DummyGame implements IGameLogic {
    
    private static final float MOUSE_SENSITIVITY = 0.2f;
    
    private final Vector3f cameraInc;
    
    private final Renderer renderer;
    
    private final Camera camera;
    
    private GameItem[] gameItems;
    
    private static final float CAMERA_POS_STEP = 0.05f;
    
    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }
    
    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
    
        
        Mesh mesh = OBJLoader.loadMesh("/models/bunny.obj");
        //Mesh mesh = OBJLoader.loadMesh("/models/cube.obj");
        Texture texture = new Texture("/textures/grassblock.png");
        //mesh.setTexture(texture);
        GameItem gameItem = new GameItem(mesh);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        gameItem.setScale(0.5f);
        gameItem.setPosition(0, 0, -2);
        gameItems = new GameItem[]{gameItem};
    
    }
    
    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if(window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -1;
        } else if(window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if(window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -1;
        } else if(window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if(window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -1;
        } else if(window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
    }
    
    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP,
                            cameraInc.z * CAMERA_POS_STEP);
        
        // Update camera based on mouse
        if(mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }
    }
    
    @Override
    public void render(Window window) {
        renderer.render(window, camera, gameItems);
    }
    
    @Override
    public void cleanup() {
        renderer.cleanup();
        for(GameItem gameItem : gameItems) {
            gameItem.getMesh().cleanUp();
        }
    }
}