public class Mesh {
    Vec3f[] vertices;
    int[] edges;
    int[] faces;
    Vec2f[] uvs;

    static Mesh generateTriangle(){
        Mesh triangle = new Mesh();
        float f = 0.5f;
        triangle.vertices = new Vec3f[]{
                new Vec3f(0,1,0),
                new Vec3f(1,-1,0),
                new Vec3f(-1,-1,0),
        };
        triangle.uvs = new Vec2f[]{
                new Vec2f(0.5f,0),
                new Vec2f(1,1),
                new Vec2f(0,1),
        };
        triangle.edges = new int[]{
                0,1, 1,2, 2,0
        };
        triangle.faces = new int[]{
                0,1,2
        };
        return triangle;
    }

    static Mesh generateCube(){
        float f = 0.5f;
        Mesh cube = new Mesh();
        cube.vertices = new Vec3f[]{
                new Vec3f(-f,f,f),
                new Vec3f(f,f,f),
                new Vec3f(f,f,-f),
                new Vec3f(-f,f,-f),

                new Vec3f(-f,-f,f),
                new Vec3f(f,-f,f),
                new Vec3f(f,-f,-f),
                new Vec3f(-f,-f,-f),
        };
        cube.uvs = new Vec2f[]{
                new Vec2f(0,0),
                new Vec2f(1,0),
                new Vec2f(1,1),
                new Vec2f(0,1),

                new Vec2f(0,1),
                new Vec2f(1,1),
                new Vec2f(1,0),
                new Vec2f(0,0),
        };
        cube.edges = new int[]{
                0,1,  1,2,  2,3,  3,0,
                0,4,  1,5,  2,6,  3,7,
                4,5,  5,6,  6,7,  7,4,
        };
        cube.faces = new int[]{
                0,1,2, 3,0,2,

                7,3,6, 6,3,2,
                6,2,5, 5,2,1,
                1,0,4, 1,4,5,
                0,3,4, 4,3,7,

                4,7,6, 4,6,5
        };
        return cube;
    }

    public Mesh applyTransformation(Matrix m){
        for (Vec3f vertex : vertices)m.mult(vertex);
        return this;
    }
}
