import javafx.util.Pair;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;



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
            int iLocal = i;
            triangle(mesh, mesh.faces[i], mesh.faces[i + 1], mesh.faces[i + 2], (Vec2i pos, EdgeWalker edgeWalker) -> {
//                mesh.texture.getPixel(new Vec2f(edgeWalker.get(EdgeWalkerCode.uvx), edgeWalker.get(EdgeWalkerCode.uvy)));

                Color c = mesh.shader.frag(iLocal);
                Utils.putPixel(pos.x, pos.y, c);
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

            Vec2f tempuv = middleRightUv;
            middleRightUv = mesh.uvs[tuples.get(1).getKey()];
            mesh.uvs[tuples.get(1).getKey()] = tempuv;
            //instead of swapping everything should probably swap tuples
        }
        Mesh tempMesh = new Mesh();
        tempMesh.vertices = new Vec3f[]{top, middle, middleRight,bot};
        tempMesh.uvs = new Vec2f[]{mesh.uvs[tuples.get(0).getKey()], mesh.uvs[tuples.get(1).getKey()], middleRightUv, mesh.uvs[tuples.get(2).getKey()]};


        IFromToer fromToer = (int itopLeft, int itopright, int ibottomLeft, int ibottomRight) -> {
            Vec3f topLeft = tempMesh.vertices[itopLeft];
            Vec3f topright = tempMesh.vertices[itopright];
            Vec3f bottomLeft = tempMesh.vertices[ibottomLeft];
            Vec3f bottomRight = tempMesh.vertices[ibottomRight];

            ArrayList<Triplet<EdgeWalkerCode, Float, Float>> fromtosLeft = new ArrayList<>();
            fromtosLeft.add(new Triplet<>(EdgeWalkerCode.x ,topLeft.x,bottomLeft.x));
            fromtosLeft.add(new Triplet<>(EdgeWalkerCode.z ,topLeft.z,bottomLeft.z));
            fromtosLeft.add(new Triplet<>(EdgeWalkerCode.uvx ,tempMesh.uvs[itopLeft].x,tempMesh.uvs[ibottomLeft].x));
            fromtosLeft.add(new Triplet<>(EdgeWalkerCode.uvy ,tempMesh.uvs[itopLeft].y,tempMesh.uvs[ibottomLeft].y));
            EdgeWalker leftWalker = new EdgeWalker(topLeft.y, bottomLeft.y, fromtosLeft);

            ArrayList<Triplet<EdgeWalkerCode, Float, Float>> fromtosRight = new ArrayList<>();
            fromtosRight.add(new Triplet<>(EdgeWalkerCode.x ,topright.x,bottomRight.x));
            fromtosRight.add(new Triplet<>(EdgeWalkerCode.z ,topright.z,bottomRight.z));
            fromtosRight.add(new Triplet<>(EdgeWalkerCode.uvx ,tempMesh.uvs[itopright].x,tempMesh.uvs[ibottomRight].x));
            fromtosRight.add(new Triplet<>(EdgeWalkerCode.uvy ,tempMesh.uvs[itopright].y,tempMesh.uvs[ibottomRight].y));
            EdgeWalker rightWalker = new EdgeWalker(topLeft.y, bottomLeft.y, fromtosRight);

            while (leftWalker.counter < bottomLeft.y){
                ArrayList<Triplet<EdgeWalkerCode, Float, Float>> fromToHor = new ArrayList<>();
                fromToHor.add(new Triplet<>(EdgeWalkerCode.z ,leftWalker.get(EdgeWalkerCode.z), rightWalker.get(EdgeWalkerCode.z)));
                fromToHor.add(new Triplet<>(EdgeWalkerCode.uvx ,leftWalker.get(EdgeWalkerCode.uvx), rightWalker.get(EdgeWalkerCode.uvx)));
                fromToHor.add(new Triplet<>(EdgeWalkerCode.uvy ,leftWalker.get(EdgeWalkerCode.uvy), rightWalker.get(EdgeWalkerCode.uvy)));
                EdgeWalker horizontalWalker = new EdgeWalker(leftWalker.get(EdgeWalkerCode.x), rightWalker.get(EdgeWalkerCode.x), fromToHor);

                while (horizontalWalker.counter < rightWalker.get(EdgeWalkerCode.x)){
                    Vec2i pos = new Vec2i((int)horizontalWalker.counter, (int)leftWalker.counter);
                    if(horizontalWalker.get(EdgeWalkerCode.z) < zbuffer[pos.x][pos.y]){
                        locationGiver.giveLocation(pos, horizontalWalker);
                        zbuffer[pos.x][pos.y] = horizontalWalker.get(EdgeWalkerCode.z);
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

    enum EdgeWalkerCode{
        x(0),y(1),z(2),uvx(3),uvy(4);

        public final int val;
        private EdgeWalkerCode(int val){
            this.val = val;
        }
    }
    class EdgeWalker{
        HashMap<EdgeWalkerCode, Float> posMap = new HashMap<>();
        HashMap<EdgeWalkerCode, Float> incsMap = new HashMap<>();
        float counter;
//        Pair<EdgeWalkerCode, Pair<Float, Float>>
        EdgeWalker(float from, float to,ArrayList<Triplet<EdgeWalkerCode, Float, Float>> fromtos){
            this.counter = from;
            float diff = to - from;
            for (int i = 0; i < fromtos.size(); i++) {
                posMap.put(fromtos.get(i).left, fromtos.get(i).middle);
                incsMap.put(fromtos.get(i).left, (fromtos.get(i).right - fromtos.get(i).middle) / diff);
            }
        }

        void step(){
            for(EdgeWalkerCode key:posMap.keySet()){
                posMap.put(key, posMap.get(key) + incsMap.get(key));
            }
            counter++;
        }

        float get(EdgeWalkerCode code){
            return posMap.get(code);
        }

    }

    public Color clownColor(int i){
        return colors[i % colors.length];
    }
}
