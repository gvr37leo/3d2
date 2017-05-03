import java.awt.*;

public class NormalShader extends Shader{

    Vec3f lightDir;

    public NormalShader(Mesh mesh, Vec3f lightDir){
        super(mesh);
        this.lightDir = lightDir;
    }

    Vec3f vert(Vec3f v) {
        return v;
    }

    Color frag(int faceIndex) {
        Vec3f p1 = mesh.vertices[mesh.faces[faceIndex]];
        Vec3f p2 = mesh.vertices[mesh.faces[faceIndex + 1]];
        Vec3f p3 = mesh.vertices[mesh.faces[faceIndex + 2]];
        Vec3f normal = (p2.c().sub(p1)).cross(p3.c().sub(p1)).normalize();
        float lightIntensity = normal.dot(lightDir);
        return new Color(
                Math.abs(lightIntensity),
                Math.abs(lightIntensity),
                Math.abs(lightIntensity));
    }
}
