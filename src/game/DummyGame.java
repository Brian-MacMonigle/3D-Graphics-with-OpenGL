package game;

import engine.*;
import engine.graph.*;
import engine.graph.lights.DirectionalLight;
import engine.graph.weather.Fog;
import engine.items.GameItem;
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
    
    private float quadAngle;
    
    private GameItem[] quads;
    
    private boolean polygonMode = false;
    
    public DummyGame() {
        renderer = new Renderer();
        camera = new Camera();
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
        quadAngle = 45;
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
        scene.setFog(new Fog(true, new Vector3f(0.5f, 0.5f, 0.5f), 0.1f));
        scene.setClearColor(new Vector4f(0.5f, 0.5f, 0.5f, 1));
        
        // Create HUD
        hud = new Hud("DEMO");
        
        camera.getPosition().x = 0.0f;
        camera.getPosition().z = 0.0f;
        camera.getPosition().y = -0.2f;
        camera.getRotation().x = 10.f;
        
        // Setup floading quads for Normal Map demo
        float reflectance = 0.65f;
        Texture texture = new Texture("/textures/rock.png");
        Texture normalMap = new Texture("/textures/rock_normals.png");
        
        Material noNormal = new Material(texture, reflectance);
        GameItem item1 = new GameItem(OBJLoader.loadMesh("/models/quad.obj"));
        item1.getMesh().setMaterial(noNormal);
        item1.setPosition(-3f, 2f, -1f);
        item1.setScale(2.0f);
        item1.setRotation(45, 0, 45);
        
        // texture = new Texture("/textures/rock_normals.png");
        Material normal = new Material(texture, reflectance);
        normal.setNormalMap(normalMap);
        GameItem item2 = new GameItem(OBJLoader.loadMesh("/models/quad.obj"));
        item2.getMesh().setMaterial(normal);
        item2.setPosition(3f, 2f, -1f);
        item2.setScale(2.0f);
        item2.setRotation(90, 0, 0);
        
        quads = new GameItem[] {item1, item2};
        
        scene.setGameItems(quads);
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
        
        if(window.isKeyPressed(GLFW_KEY_LEFT)) {
            lightAngle -= 2.5f;
        } else if(window.isKeyPressed(GLFW_KEY_RIGHT)) {
            lightAngle += 2.5f;
        }
        if(lightAngle > 180) {
            lightAngle = -180;
        } else if(lightAngle < -180) {
            lightAngle = 180;
        }
        
        if(window.isKeyPressed(GLFW_KEY_UP)) {
            quadAngle += 1.0f;
        } else if(window.isKeyPressed(GLFW_KEY_DOWN)) {
            quadAngle -= 1.0f;
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
        DirectionalLight directionalLight = sceneLight.getDirectionalLight();// A comment
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float) Math.sin(angRad);
        directionalLight.getDirection().y = (float) Math.cos(angRad);
        
        // Update quad angle
        for(GameItem item : quads) {
            item.getRotation().y = quadAngle;
        }
        
        System.out.println(lightAngle);
    }
    
    @Override
    public void render(Window window) {
        glPolygonMode(GL_FRONT_AND_BACK, (polygonMode) ? GL_LINE : GL_FILL);
        if(hud != null) {
            hud.updateSize(window);
        }
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