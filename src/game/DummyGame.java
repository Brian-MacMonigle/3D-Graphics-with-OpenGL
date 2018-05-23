package game;

import engine.*;
import engine.graph.Camera;
import engine.graph.Renderer;
import engine.graph.lights.DirectionalLight;
import engine.graph.weather.Fog;
import engine.items.SkyBox;
import engine.items.Terrain;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class DummyGame implements IGameLogic {
    
    private static final float MOUSE_SENSITIVITY = 0.2f;
    
    private static final float CAMERA_POS_STEP = 0.05f;
    
    private final Vector3f cameraInc;
    
    private final Renderer renderer;
    
    private final Camera camera;
    
    private Scene scene;
    
    private Hud hud;
    
    private float lightAngle;
    
    private Terrain terrain;
    
    private boolean polygonMode = false;
    
    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
    }
    
    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        
        scene = new Scene();
        
        float skyBoxScale = 100.0f;
        float terrainScale = 10;
        int terrainSize = 3;
        float minY = -0.1f;
        float maxY = 0.1f;
        int textInc = 40;
        terrain = new Terrain(terrainSize, terrainScale, minY, maxY, "/textures/heightmap.png",
                              "/textures/terrain.png", textInc);
        scene.setGameItems(terrain.getGameItems());
        
        // Setup  SkyBox
        SkyBox skyBox = new SkyBox("/models/skybox.obj", "/textures/skybox.png");
        skyBox.setScale(skyBoxScale);
        // scene.setSkyBox(skyBox);
        
        // Setup Lights
        setupLights();
        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.15f));
        scene.setClearColor(new Vector4f(0.5f, 0.5f, 0.5f, 1));
        
        // Create HUD
        hud = new Hud("DEMO");
        
        camera.getPosition().x = 0.0f;
        camera.getPosition().z = 0.0f;
        camera.getPosition().y = -0.2f;
        camera.getRotation().x = 10.f;
    }
    
    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);
        
        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));
        
        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(0.5f, 1, 0.5f);
        sceneLight.setDirectionalLight(new DirectionalLight(new Vector3f(1, 1, 1), lightPosition, lightIntensity));
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
        if(window.isKeyPressed(GLFW_KEY_LEFT_SHIFT) || window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)) {
            cameraInc.x *= 10;
            cameraInc.y *= 10;
            cameraInc.z *= 10;
        }
        
        if(window.isKeyPressed(GLFW_KEY_TAB)) {
            polygonMode = true;
        } else {
            polygonMode = false;
        }
    }
    
    @Override
    public void update(float interval, MouseInput mouseInput) {
        // Update camera based on mouse
        if(mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
            
            // Update HUD compass
            hud.rotateCompass(camera.getRotation().y);
        }
        
        // Update camera position
        Vector3f prevPos = new Vector3f(camera.getPosition());
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP,
                            cameraInc.z * CAMERA_POS_STEP);
        // Check if there has been a collision. If true, set the y position to
        // the maximum height
        float height = terrain.getHeight(camera.getPosition()) + 0.075f;
        if(camera.getPosition().y <= height) {
            //camera.setPosition(prevPos.x, prevPos.y, prevPos.z);
            camera.getPosition().y = height;
        }
    
        // Update directional light direction, intensity and colour
        SceneLight sceneLight = scene.getSceneLight();
        DirectionalLight directionalLight = sceneLight.getDirectionalLight();
        lightAngle += 1f;
        if (lightAngle > 90) {
            directionalLight.setIntensity(0);
            if (lightAngle >= 100) {
                lightAngle = -90;
            }
            sceneLight.getSkyBoxLight().set(0.3f, 0.3f, 0.3f);
        } else if (lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (float) (Math.abs(lightAngle) - 80) / 10.0f;
            sceneLight.getSkyBoxLight().set(factor, factor, factor);
            directionalLight.setIntensity(factor);
            directionalLight.getColor().y = Math.max(factor, 0.9f);
            directionalLight.getColor().z = Math.max(factor, 0.5f);
        } else {
            sceneLight.getSkyBoxLight().set(1.0f, 1.0f, 1.0f);
            directionalLight.setIntensity(1);
            directionalLight.getColor().x = 1;
            directionalLight.getColor().y = 1;
            directionalLight.getColor().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
    }
    
    @Override
    public void render(Window window) {
        glPolygonMode(GL_FRONT_AND_BACK, (polygonMode) ? GL_LINE : GL_FILL);
        hud.updateSize(window);
        renderer.render(window, camera, scene, hud);
    }
    
    @Override
    public void cleanup() {
        if(renderer != null) {
            renderer.cleanup();
        }
        if(scene != null) {
            scene.cleanup();
        }
        if(hud != null) {
            hud.cleanup();
        }
    }
}