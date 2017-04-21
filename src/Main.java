import processing.core.PApplet;
import processing.event.KeyEvent;

import java.util.HashMap;

public class Main extends PApplet {


    public void settings(){
        size(600,600);
    }

    Camera camera;
    float dt = 1.0f / 60;
    float rotationSpeed = 4;
    float theta;

    public void setup(){
        Globals.init();
        Globals.screenSize = new Vec2i(width/2, height/2);
        Globals.app = this;

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
            theta += rotationSpeed * dt;
        }
        if(keyMap.get('d') != null && keyMap.get('d')){
            theta -= rotationSpeed * dt;
        }

        Mesh cube = Mesh.generateCube();
        Matrix rot = Matrix.rot(new Vec3f(0,1,0),theta);
        Matrix trans = Matrix.translate(new Vec3f(0,0,2));
        Matrix scale = Matrix.scale(new Vec3f(1,1,1));

        Matrix objToWs = trans.mult(scale).mult(rot);
        cube.applyTransformation(objToWs);
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
