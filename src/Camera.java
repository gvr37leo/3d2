import javafx.util.Pair;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;

public class Camera {
    public Vec3f pos;
    public Vec3f forward;
    public float ratio;
    public float fov;
    public float far = 5;
    public float near = 1;
    public float[][] zbuffer;
    Color[] colors = new Color[]{
            Color.yellow,
            Color.blue,
            Color.cyan,
            Color.magenta,
            Color.red,
            Color.pink,
            Color.green,
            Color.gray,
            new Color(0x6E2B02)
    };

    Camera(){

    }

    void draw(Mesh mesh){
        for (int i = 0; i < mesh.faces.length; i += 3) {
            int _i = i;
            triangle(mesh, mesh.faces[i], mesh.faces[i + 1], mesh.faces[i + 2], (Vec2i pos, EdgeWalker edgeWalker) -> {
                Utils.putPixel(pos.x, pos.y, clownColor(_i));
            });
        }
    }

    Vec3f wstoss(Vec3f ws){
        Vec3f v = ws.c();
        v.x /= v.z;
        v.y /= v.z;
        Globals.wstoss.mult(v);
        return v;
    }

    public void triangle(Mesh mesh, int t0, int t1, int t2, ILocationGiver locationGiver){
        ArrayList<Pair<Integer, Vec3f>> tuples = new ArrayList<>();
        tuples.add(new Pair<>(t0, wstoss(mesh.vertices[t0].c())));
        tuples.add(new Pair<>(t1, wstoss(mesh.vertices[t1].c())));
        tuples.add(new Pair<>(t2, wstoss(mesh.vertices[t2].c())));
        tuples.sort(Comparator.comparingDouble(value -> value.getValue().y));

        Vec3f middleRight;
        Vec2f middleRightUv;
        Vec3f top = tuples.get(0).getValue();
        Vec3f middle = tuples.get(1).getValue();
        Vec3f bot = tuples.get(2).getValue();

        if(middle.y == top.y) {
            middleRight = top.c();
            middleRightUv = mesh.uvs[tuples.get(0).getKey()].c();
        }
        else {
            float ratio = (middle.y - top.y) / (bot.y - top.y);
            middleRight = top.lerp(bot, ratio);
            middleRightUv = mesh.uvs[tuples.get(0).getKey()].lerp(mesh.uvs[tuples.get(2).getKey()], ratio);
        }


        if(middleRight.x < middle.x){
            Vec3f temp = middleRight;
            middleRight = middle;
            middle = temp;
        }
        Mesh tempMesh = new Mesh();
        tempMesh.vertices = new Vec3f[]{top, middle, middleRight,bot};
        tempMesh.uvs = new Vec2f[]{mesh.uvs[tuples.get(0).getKey()], mesh.uvs[tuples.get(1).getKey()], middleRightUv, mesh.uvs[tuples.get(2).getKey()]};


        IFromToer fromToer = (int itopLeft, int itopright, int ibottomLeft, int ibottomRight) -> {
            Vec3f topLeft = tempMesh.vertices[itopLeft];
            Vec3f topright = tempMesh.vertices[itopright];
            Vec3f bottomLeft = tempMesh.vertices[ibottomLeft];
            Vec3f bottomRight = tempMesh.vertices[ibottomRight];

            float[] fromsLeft = new float[]{topLeft.x, topLeft.z, tempMesh.uvs[itopLeft].x, tempMesh.uvs[itopLeft].y};
            float[] tosLeft = new float[]{bottomLeft.x, bottomLeft.z, tempMesh.uvs[ibottomLeft].x, tempMesh.uvs[ibottomLeft].y};
            EdgeWalker leftWalker = new EdgeWalker(topLeft.y, bottomLeft.y, fromsLeft, tosLeft);

            float[] fromsRight = new float[]{topright.x, topright.z, tempMesh.uvs[itopright].x, tempMesh.uvs[itopright].y};
            float[] tosRight = new float[]{bottomRight.x, bottomRight.z, tempMesh.uvs[ibottomRight].x, tempMesh.uvs[ibottomRight].x};
            EdgeWalker rightWalker = new EdgeWalker(topright.y, bottomRight.y,fromsRight, tosRight);

            while (leftWalker.counter < bottomLeft.y){
                float[] fromH = new float[]{leftWalker.pos[1], leftWalker.pos[2], leftWalker.pos[3]};//y never changes and will always be equal to the verticl walker's counter value
                float[] toH = new float[]{rightWalker.pos[1],rightWalker.pos[2],rightWalker.pos[3]};
                EdgeWalker horizontalWalker = new EdgeWalker(leftWalker.pos[0], rightWalker.pos[0], fromH, toH);

                while (horizontalWalker.counter < rightWalker.pos[0]){//pos[0] contains the x coordinate
                    Vec2i pos = new Vec2i((int)horizontalWalker.counter, (int)leftWalker.counter);
                    if(horizontalWalker.pos[0] < zbuffer[pos.x][pos.y]){
                        locationGiver.giveLocation(pos, horizontalWalker);
                        zbuffer[pos.x][pos.y] = horizontalWalker.pos[0];
                    }
                    horizontalWalker.step();
                }
                leftWalker.step();
                rightWalker.step();
            }
        };

        fromToer.fromTo(0, 0, 1, 2);
        fromToer.fromTo(1, 2, 3, 3);

    }
    interface IFromToer{
        void fromTo(int topLeft, int topright, int bottomLeft, int bottomRight);
    }

    interface ILocationGiver{
        void giveLocation(Vec2i pos, EdgeWalker edgeWalker);
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

    public Color clownColor(int i){
        return colors[i % colors.length];
    }
}
