package engine;

import engine.graph.Mesh;
import engine.graph.weather.Fog;
import engine.items.GameItem;
import engine.items.SkyBox;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
    
    private Map<Mesh, List<GameItem>> meshMap;
    
    private SkyBox skyBox;
    
    private SceneLight sceneLight;
    
    private Fog fog;
    
    private Vector4f clearColor;
    
    public Scene() {
        meshMap = new HashMap();
        fog = Fog.NOFOG;
        clearColor = new Vector4f(0, 0, 0, 0);
    }
    
    public Map<Mesh, List<GameItem>> getGameMeshes() {
        return meshMap;
    }
    
    public void setGameItems(GameItem[] gameItems) {
        int numGameItems = gameItems != null ? gameItems.length : 0;
        for(int i = 0; i < numGameItems; i++) {
            GameItem gameItem = gameItems[i];
            Mesh mesh = gameItem.getMesh();
            List<GameItem> list = meshMap.get(mesh);
            if(list == null) {
                list = new ArrayList<>();
                meshMap.put(mesh, list);
            }
            list.add(gameItem);
        }
    }
    
    public void cleanup() {
        for(Mesh mesh : meshMap.keySet()) {
            mesh.cleanUp();
        }
    }
    
    public SkyBox getSkyBox() {
        return skyBox;
    }
    
    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }
    
    public SceneLight getSceneLight() {
        return sceneLight;
    }
    
    public void setSceneLight(SceneLight sceneLight) {
        this.sceneLight = sceneLight;
    }
    
    public Fog getFog() {
        return fog;
    }
    
    public void setFog(Fog fog) {
        this.fog = fog;
    }
    
    public Vector4f getClearColor() {
        return clearColor;
    }
    
    public void setClearColor(Vector4f clearColor) {
        this.clearColor = clearColor;
    }
}