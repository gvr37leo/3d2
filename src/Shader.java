

import java.awt.*;

public abstract class Shader {

    Mesh mesh;

    Shader(Mesh mesh){
        this.mesh = mesh;
    }

    abstract Vec3f vert(Vec3f v);

    abstract Color frag(int faceIndex);

}
