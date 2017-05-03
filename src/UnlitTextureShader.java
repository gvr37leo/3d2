import java.awt.*;

public class UnlitTextureShader extends Shader {
    UnlitTextureShader(Mesh mesh, Texture texture) {
        super(mesh);
    }

    @Override
    Vec3f vert(Vec3f v) {
        return null;
    }

    @Override
    Color frag(int vertexIndex) {
        return null;
    }
}
