import java.awt.*;

public class unlitColorShader extends Shader {
    Color c;

    unlitColorShader(Mesh mesh, Color c) {
        super(mesh);
        this.c = c;
    }

    @Override
    Vec3f vert(Vec3f v) {
        return v;
    }

    @Override
    Color frag(int vertexIndex) {
        return c;
    }
}
