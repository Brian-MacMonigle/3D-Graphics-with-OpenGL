package game;

import engine.GameItem;
import engine.IHud;
import engine.TextItem;
import engine.Window;
import engine.graph.FontTexture;
import engine.graph.Material;
import engine.graph.Mesh;
import engine.graph.OBJLoader;
import org.joml.Vector4f;

import java.awt.Font;

public class Hud implements IHud {
    
    private static final Font FONT = new Font("Arial", Font.PLAIN, 20);
    
    private static final String CHARSET = "ISO-8859-1";
    
    private final GameItem[] gameItems;
    
    private final TextItem statusTextItem;
    
    private final GameItem compassItem;
    
    public Hud(String statusText) throws Exception {
        FontTexture fontTexture = new FontTexture(FONT, CHARSET);
        this.statusTextItem = new TextItem(statusText, fontTexture);
        this.statusTextItem.getMesh().getMaterial().setAmbientColour(new Vector4f(1, 1, 1, 1));
        
        // Create compass
        Mesh mesh = OBJLoader.loadMesh("/models/compass.obj");
        Material material = new Material();
        material.setAmbientColour(new Vector4f(1, 0, 0, 1));
        mesh.setMaterial(material);
        compassItem = new GameItem(mesh);
        compassItem.setScale(40.0f);
        // Rotate to transform it to screen coordinates
        rotateCompass(0);
        
        // Create list that holds the items that compose the HUD
        gameItems = new GameItem[] {statusTextItem, compassItem};
    }
    
    public void setStatusText(String statusText) {
        this.statusTextItem.setText(statusText);
    }
    
    public void rotateCompass(float angle) {
        this.compassItem.setRotation(180f, 0f, -angle);
    }
    
    @Override
    public GameItem[] getGameItems() {
        return gameItems;
    }
    
    public void updateSize(Window window) {
        this.statusTextItem.setPosition(10f, window.getHeight() - 50f, 0);
        this.compassItem.setPosition(window.getWidth() - 40f, 50f, 0);
    }
}