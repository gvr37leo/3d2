import java.awt.*;

public class ClownShader extends Shader{

    Color[] colors = new Color[]{
            Color.gray,
            Color.green,
            Color.white,
            Color.magenta,
            Color.CYAN,
            Color.red,
            Color.orange,
            Color.yellow,
    };

    ClownShader(Mesh mesh) {
        super(mesh);
    }

    Vec3f vert(Vec3f v) {
        return v;
    }

    Color frag(int vertexIndex) {
        return colors[vertexIndex % colors.length];
    }
}
