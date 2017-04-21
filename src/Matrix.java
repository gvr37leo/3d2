public class Matrix {

    public float[][] val;

    private Matrix(){
        val = new float[4][4];
    }

    public Matrix mult(Matrix rhs){
        Matrix matrix = new Matrix();
        for (int col = 0; col < 4; col++){
            for (int row = 0; row < 4; row++){
                float sum = 0;
                for(int i = 0; i < 4; i++){
                    sum += val[row][i] * rhs.val[i][col];
                }
                matrix.val[row][col]= sum;
            }
        }
        return matrix;
    }

    public Vec3f mult(Vec3f rhs){
        Vec3f v = new Vec3f();
        for(int rows = 0; rows < 3; rows++){
            float sum = 0;
            for (int i = 0; i < 3; i++) {
                sum += val[rows][i] * rhs.get(i);
            }
            sum += val[rows][3];//all vectors are considered points
            v.set(rows,sum);
        }
        rhs.overwrite(v);
        return rhs;
    }

    public Matrix mult(float scalar){
        for (int col = 0; col < 4; col++){
            for (int row = 0; row < 4; row++){
                val[row][col] *= scalar;
            }
        }
        return this;
    }

    public Matrix inverse(){
        mult(1 / determinant());
        return this;
    }

    public float determinant(){
        return 0;
    }

    public Matrix transpose(){
        return null;
    }

    public static Matrix identity(){
        Matrix matrix = new Matrix();
        matrix.val = new float[][]{
                {1,0,0,0},
                {0,1,0,0},
                {0,0,1,0},
                {0,0,0,1},
        };
        return matrix;
    }

    public static Matrix translate(Vec3f translation){
        Matrix matrix = new Matrix();
        matrix.val = new float[][]{
                {1,0,0,translation.x},
                {0,1,0,translation.y},
                {0,0,1,translation.z},
                {0,0,0,1},
        };
        return matrix;
    }

    public static Matrix scale(Vec3f scaling){
        Matrix matrix = new Matrix();
        matrix.val = new float[][]{
                {scaling.x,0,0,0},
                {0,scaling.y,0,0},
                {0,0,scaling.z,0},
                {0,0,0,1},
        };
        return matrix;
    }

    public static Matrix rot(Vec3f angle, float theta){
        Matrix matrix = new Matrix();
        float cosTheta = (float) Math.cos(theta);
        float oneMinCos = 1 - cosTheta;
        float sinTheta = (float) Math.sin(theta);
        matrix.val = new float[][]{
                {cosTheta + (float)Math.pow(angle.x, 2) * oneMinCos, angle.x * angle.y * oneMinCos - angle.z * sinTheta, angle.x * angle.z * oneMinCos + angle.y * sinTheta,0},
                {angle.y * angle.x * oneMinCos + angle.z *sinTheta, cosTheta + (float)Math.pow(angle.y, 2) * oneMinCos,angle.y * angle.z * oneMinCos - angle.x * sinTheta,0},
                {angle.z * angle.x * oneMinCos - angle.y * sinTheta, angle.z * angle.y * oneMinCos + angle.x * sinTheta,cosTheta + (float)Math.pow(angle.z, 2) * oneMinCos,0},
                {0,0,0,1},
        };
        return matrix;
    }

    public static Matrix cameraTransform(){//move the camera around
        Matrix matrix = new Matrix();
        matrix.val = new float[][]{
                {1,0,0,0},
                {0,1,0,0},
                {0,0,-1,-1},
                {0,0,0,0},
        };
        matrix.inverse();
        return matrix;
    }

//    public static Matrix perspectiveMatrix(Vec3f pos){//perspective matrix, divde by z and ratio and stuff
//        Matrix matrix = new Matrix();
//        matrix.val = new float[][]{
//                {1,0,0,0},
//                {0,1,0,0},
//                {0,0,-1,0},
//                {0,0,-1,0},
//        };
//        return matrix;
//    }
//
//    public static Matrix orthoGraphicMatrix(Vec3f pos){
//        Matrix matrix = new Matrix();
//        matrix.val = new float[][]{
//                {1,0,0,0},
//                {0,1,0,0},
//                {0,0,-1,0},
//                {0,0,-1,0},
//        };
//        return matrix;
//    }

    public static Matrix viewTransForm(){//to screen space
        Matrix matrix = new Matrix();
        float halfScreenWidth = Globals.screenSize.x / 2f;
        float halfScreenHeight = Globals.screenSize.y / 2f;
        matrix.val = new float[][]{
                {halfScreenWidth,0,0,halfScreenWidth},
                {0,-halfScreenHeight,0,halfScreenHeight},
                {0,0,1,0},
                {0,0,0,1},
        };
        return matrix;
    }


}
