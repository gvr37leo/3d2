import processing.core.PApplet;

import java.awt.*;
import java.util.Arrays;
import java.util.Comparator;

public class Camera {
    public Vec3f pos;
    public Vec3f forward;
    public float ratio;
    public float fov;
    public float far = 5;
    public float near = 1;
    public float[][] zbuffer;

    Camera(){

    }

    void draw(Mesh mesh){
        Vec3f[] screenCoords = new Vec3f[mesh.vertices.length];
        Matrix wstoss = Matrix.viewTransForm();
        for(int i = 0; i < mesh.vertices.length; i++){
            Vec3f v = mesh.vertices[i].c();
            v.x /= v.z;
            v.y /= v.z;
            screenCoords[i] = wstoss.mult(v);
        }
        for (int i = 0; i < mesh.faces.length; i += 3) {
            Vec3f p1 = screenCoords[mesh.faces[i]];
            Vec3f p2 = screenCoords[mesh.faces[i + 1]];
            Vec3f p3 = screenCoords[mesh.faces[i + 2]];

            triangle(p1, p2, p3, (Vec2i pos) -> {
                Utils.putPixel(pos.x, pos.y, Color.white);
            });


        }
    }

    public void triangle(Vec3f t0, Vec3f t1, Vec3f t2, ILocationGiver locationGiver){
        Vec3f[] vs = {t0,t1,t2};
        Arrays.sort(vs, Comparator.comparingDouble(a -> a.y));

        IFromToer fromToer = (Vec3f topLeft, Vec3f topright, Vec3f bottomLeft, Vec3f bottomRight) -> {
            float[] fromsLeft = new float[]{topLeft.x, topLeft.z};
            float[] tosLeft = new float[]{bottomLeft.x, bottomLeft.z};
            EdgeWalker leftWalker = new EdgeWalker(topLeft.y, bottomLeft.y, fromsLeft, tosLeft);

            float[] fromsRight = new float[]{topright.x, topright.z};
            float[] tosRight = new float[]{bottomRight.x, bottomRight.z};
            EdgeWalker rightWalker = new EdgeWalker(topright.y, bottomRight.y,fromsRight, tosRight);

            while (leftWalker.counter < bottomLeft.y){
                float[] fromH = new float[]{leftWalker.pos[1]};//y never changes and will always be equal to the verticl walker's counter value
                float[] toH = new float[]{rightWalker.pos[1]};
                EdgeWalker horizontalWalker = new EdgeWalker(leftWalker.pos[0], rightWalker.pos[0], fromH, toH);

                while (horizontalWalker.counter < rightWalker.pos[0]){//pos[0] contains the x coordinate
                    Vec2i pos = new Vec2i((int)horizontalWalker.counter, (int)leftWalker.counter);
                    if(horizontalWalker.pos[0] < zbuffer[pos.x][pos.y]){
                        locationGiver.giveLocation(pos);
                        zbuffer[pos.x][pos.y] = horizontalWalker.pos[0];
                    }
                    horizontalWalker.step();
                }
                leftWalker.step();
                rightWalker.step();
            }
        };

        Vec3f middleRight;
        if(vs[1].y == vs[0].y)middleRight = vs[0].c();
        else middleRight = vs[0].lerp(vs[2], (vs[1].y - vs[0].y) / (vs[2].y - vs[0].y));

        if(middleRight.x < vs[1].x){
            Vec3f temp = middleRight;
            middleRight = vs[1];
            vs[1] = temp;
        }
        fromToer.fromTo(vs[0].c(), vs[0].c(), vs[1].c(), middleRight.c());
        fromToer.fromTo(vs[1].c(), middleRight.c(),vs[2].c(),vs[2].c());

    }
    interface IFromToer{
        void fromTo(Vec3f topLeft, Vec3f topright, Vec3f bottomLeft, Vec3f bottomRight);
    }

    interface ILocationGiver{
        void giveLocation(Vec2i pos);
    }

    public static void draw2dArray(Vec2i pos, float[][] array){
        for(int x = 0; x < array.length; x++){
            for(int y = 0; y < array[0].length; y++){
                float mapped = Utils.map(array[x][y], 0, 5, 1, 0);
                Utils.putPixel(x + pos.x, y + pos.y, new Color(mapped, mapped, mapped));
            }
        }
    }

    class EdgeWalker{
        float[] pos;
        float[] incs;
        float counter;

        public EdgeWalker(float from, float to,float[] froms, float[] tos){
            this.counter = from;
            float diff = to - from;
            pos = froms;
            incs = new float[tos.length];
            for (int i = 0; i < froms.length; i++) {
                incs[i] = (tos[i] - froms[i]) / diff;
            }
        }

        public void step(){
            for (int i = 0; i < pos.length; i++) {
                pos[i] += incs[i];
            }
            counter++;
        }
    }
}
