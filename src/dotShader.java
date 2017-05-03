import java.awt.*;

public class DotShader extends Shader{

    Vec3f lightDir;

    public DotShader(Mesh mesh, Vec3f lightDir){
        super(mesh);
        this.lightDir = lightDir;
    }

    Vec3f vert(Vec3f v) {
        return v;
    }

    Color frag(int vertexIndex) {
        mesh.faces[vertexIndex];
        return null;
    }
}
