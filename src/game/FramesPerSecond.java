package game;

import engine.Timer;
import engine.graph.FontTexture;
import engine.items.TextItem;

import java.util.Arrays;

public class FramesPerSecond extends TextItem {
    // TODO: Change FPS so we only update TEXTURE and not MESH
    private float[] buff;
    
    private final int buffSize = 15;
    
    private int index = 0;
    
    private Timer timer;
    
    private float refreshRate = 0.5f;
    
    private float accumulator = 0;
    
    public FramesPerSecond(FontTexture fontTexture) throws Exception {
        super("00fps", fontTexture);
        timer = new Timer();
        buff = new float[buffSize];
    }
    
    public void update() {
        buff[index] = timer.getElapsedTime();
        accumulator += buff[index];
        System.out.println("Elapsed Time: " + buff[index]);
        index = ++index % buffSize;
        
        if(accumulator >= refreshRate) {
            accumulator = 0;
            
            float avg = 0;
            for(float frame : buff) {
                avg += frame;
            }
            avg /= buffSize;
            int fps = (int) (1 / avg + 0.5f);
            System.out.println("Avg: " + avg + "\tfps: " + fps);
            this.setText(fps + "fps");
        }
    }
}
