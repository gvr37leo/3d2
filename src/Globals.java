import processing.core.PApplet;
import processing.event.KeyEvent;

import java.util.HashMap;

public class Globals {
    static HashMap<Character, Boolean> keyMap;
    static Vec2i screenSize;
    static PApplet app;
    static Matrix wstoss;
    static Texture testTexture;

    public static void init(){
        keyMap = new HashMap<>();
        testTexture = new Texture("resources/test.png");
    }

    public static void keyPressed(KeyEvent event) {
        keyMap.put(event.getKey(), true);
    }

    public static void keyReleased(KeyEvent event) {
        keyMap.put(event.getKey(), false);
    }

}
