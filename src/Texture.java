import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Texture {
    BufferedImage image;

    Texture(String s){
        try {
            image = ImageIO.read(new File(s));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Color getPixel(Vec2f uv){
        int x = (int) ((image.getWidth() - 1 ) * uv.x);
        int y = (int) ((image.getHeight() - 1 ) * uv.y);
        return new Color(image.getRGB(x, y));
    }
}
