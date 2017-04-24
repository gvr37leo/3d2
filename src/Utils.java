import processing.core.PApplet;

import java.awt.*;

public class Utils {



    public static void init2dArray(float[][] array, float def){
        for(int x = 0; x < array.length; x++){
            for(int y = 0; y < array[0].length; y++){
                array[x][y] = def;
            }
        }
    }


    public static void putPixel(int x, int y, Color color){
        PApplet app = Globals.app;
        if(x >= app.width || x < 0 || y >= app.height || y < 0)return;
        int index = y * (app.width) + x;
        app.pixels[index] = app.color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }

    public static float map(float val1,float  start1,float  stop1,float  start2,float  stop2){
        return start2 + (stop2 - start2) * ((val1 - start1) / (stop1 - start1));
    }


}

class Triplet<L,M,R>{
    L left;
    M middle;
    R right;

    Triplet(L left, M middle, R right){
        this.left = left;
        this.middle = middle;
        this.right = right;
    }
}