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
            EdgeWalkerVert edgeWalkerL = new EdgeWalkerVert(topLeft, bottomLeft, null, null);
            EdgeWalkerVert edgeWalkerR = new EdgeWalkerVert(topright, bottomRight, null, null);
            while (edgeWalkerL.pos.y < bottomLeft.y){
                EdgeWalkerHor horizontalWalker = new EdgeWalkerHor(edgeWalkerL.pos.c(), edgeWalkerR.pos.c(), null, null);
                while (horizontalWalker.pos.x < edgeWalkerR.pos.x){
                    Vec2i pos = new Vec2i((int)horizontalWalker.pos.x, (int) horizontalWalker.pos.y);
                    if(horizontalWalker.pos.z < zbuffer[pos.x][pos.y]){
                        locationGiver.giveLocation(pos);
                        zbuffer[pos.x][pos.y] = horizontalWalker.pos.z;
                    }
                    horizontalWalker.stepHorizontal();
                }
                edgeWalkerL.stepVertical();
                edgeWalkerR.stepVertical();
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

    class EdgeWalkerVert {
        public Vec3f pos;
        public Vec3f posInc;

        public Vec2f uv;
        public Vec2f uvInc;

        EdgeWalkerVert(Vec3f from, Vec3f to, Vec2f uvfrom, Vec2f uvto){
            pos = from;
            float ydiff = to.y - from.y;
            posInc = to.c().sub(from).scale(1 / ydiff);
        }

        public void stepVertical(){
            pos.add(posInc);
//            uv.add(uvInc);
        }
    }

    class EdgeWalkerHor {
        public Vec3f pos;
        public Vec3f posInc;

        public Vec2f uv;
        public Vec2f uvInc;

        EdgeWalkerHor(Vec3f from, Vec3f to, Vec2f uvfrom, Vec2f uvto){
            pos = from;
            float xdiff = to.x - from.x;
            posInc = to.c().sub(from).scale(1 / xdiff);
        }

        public void stepHorizontal(){
            pos.add(posInc);
//            uv.add(uvInc);
        }
    }
}
