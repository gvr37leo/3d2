import processing.core.PApplet;
import processing.event.KeyEvent;

public class Main  extends PApplet {


    public void settings(){
        size(600,600);
    }

    Camera camera = new Camera();

    public void setup(){
        Globals.init();
        Globals.screenSize = new Vec2i(width, height);
        Globals.app = this;

        Matrix rot = Matrix.rot(new Vec3f(0,0,1), (float) Math.PI / 2);
        Matrix trans = Matrix.translate(new Vec3f(5,0,0));
        Matrix rotTrans = trans.mult(rot);
        Vec3f v = new Vec3f(0,1,0);
        rotTrans.mult(v);
    }

    public void draw(){
        clear();
        loadPixels();
        Mesh triangle = Mesh.generateTriangle();
        camera.draw(triangle);
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
