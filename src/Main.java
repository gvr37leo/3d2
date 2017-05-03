import processing.core.PApplet;
import processing.event.KeyEvent;

import java.awt.*;
import java.util.HashMap;

public class Main extends PApplet {


    public void settings(){
        size(600,600);
    }

    Camera camera;
    float dt = 1.0f / 60;
    float rotationSpeed = 4;
    Vec3f rotation = new Vec3f(0,0,0);

    public void setup(){
        Globals.init();
        Globals.screenSize = new Vec2i(width/2, height/2);
        Globals.app = this;
        Globals.wstoss = Matrix.viewTransForm();

        camera = new Camera();
        Matrix rot = Matrix.rot(new Vec3f(0,0,1), (float) Math.PI / 2);
        Matrix trans = Matrix.translate(new Vec3f(5,0,0));
        Matrix rotTrans = trans.mult(rot);
        Vec3f v = new Vec3f(0,1,0);
        rotTrans.mult(v);
    }

    public void draw(){
        clear();
        camera.zbuffer = new float[Globals.screenSize.x][Globals.screenSize.y];
        Utils.init2dArray(camera.zbuffer,camera.far);
        loadPixels();

        HashMap<Character, Boolean> keyMap = Globals.keyMap;
        if(keyMap.get('a') != null && keyMap.get('a')){
            rotation.y += rotationSpeed * dt;
        }
        if(keyMap.get('d') != null && keyMap.get('d')){
            rotation.y -= rotationSpeed * dt;
        }
        if(keyMap.get('w') != null && keyMap.get('w')){
            rotation.x += rotationSpeed * dt;
        }
        if(keyMap.get('s') != null && keyMap.get('s')){
            rotation.x -= rotationSpeed * dt;
        }
        if(keyMap.get('q') != null && keyMap.get('q')){
            rotation.z += rotationSpeed * dt;
        }
        if(keyMap.get('e') != null && keyMap.get('e')){
            rotation.z -= rotationSpeed * dt;
        }

        Mesh cube = Mesh.generateCube();

        Matrix TRS = Matrix.TRS(new Vec3f(0,0,2), rotation, new Vec3f(1,1,1));
        cube.applyTransformation(TRS);
        camera.draw(cube);
        Camera.draw2dArray(new Vec2i(Globals.screenSize.x,0), camera.zbuffer);
        updatePixels();
    }

    public void keyPressed(KeyEvent event) {
        Globals.keyPressed(event);
    }

    public void keyReleased(KeyEvent event) {
        Globals.keyReleased(event);
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.runSketch();
    }
}
