public abstract class Vecf<T extends Vecf<T>>{
    int dimensions;

    public static Vecf construct(int dimensions){
        switch(dimensions){
            case 3:return new Vec3f(0,0,0);
            default:return new Vec2f(0,0);
        }
    }

    public T add(T v){
        return iterate((i) -> set(i, get(i) + v.get(i)));
    }

    public T sub(T v){
        return iterate((i) -> set(i, get(i) - v.get(i)));
    }

    public T scale(float s){
        return iterate((i) -> set(i, get(i) * s));
    }

    public T lerp(T v, float weight){
        return c().add(v.c().sub(This()).scale(weight));
    }

    public float dot(T v){
        float sum = 0;
        for(int i = 0; i < dimensions; i++)sum += get(i) * v.get(i);
        return sum;
    }

    public T project(T v){
        return this.c().scale(this.dot(v) / this.dot(This()));
    }

    public float length(){
        float sum = 0;
        for(int i = 0; i < dimensions; i++)sum += get(i) * get(i);
        return (float) Math.sqrt(sum);
    }

    public T normalize(){
        return scale(1 / length());
    }

    public T c(){
        T c = (T) Vecf.construct(dimensions);
        iterate((i) -> c.set(i, get(i)));
        return c;
    }

    public Vecf overwrite(Vecf v){
        return iterate((i) -> set(i, v.get(i)));
    }

    public T iterate(Iterator iterator){
        for(int i = 0; i < dimensions; i++)iterator.iterate(i);
        return This();
    }

    interface Iterator{
        void iterate(int i);
    }

    public abstract float get(int i);
    public abstract void set(int i, float val);
    public abstract T This();
    public abstract float det(T v);

    public boolean equals(T v){
        for(int i = 0; i < 3; i++)if(get(i) != v.get(i)) return false;
        return true;
    }
}

class Vec3f extends Vecf<Vec3f> {
    float x;
    float y;
    float z;

    Vec3f(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimensions = 3;
    }

    Vec3f(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.dimensions = 3;
    }

    public float get(int i){
        switch (i){
            case 1:return y;
            case 2:return z;
            default: return x;
        }
    }

    public void set(int i, float val){
        switch (i){
            case 1:y = val;
                break;
            case 2:z = val;
                break;
            default: x = val;
        }
    }

    public Vec3f This() {
        return this;
    }

    public float det(Vec3f v) {
        return cross(v).length();
    }

    public Vec3f cross(Vec3f v){
        return new Vec3f(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
        );
    }

    public static Vec3f left(){
        return new Vec3f(-1,0,0);
    }

    public static Vec3f right(){
        return new Vec3f(1,0,0);
    }

    public static Vec3f up(){
        return new Vec3f(0,1,0);
    }

    public static Vec3f down(){
        return new Vec3f(0,-1,0);
    }

    public static Vec3f front(){
        return new Vec3f(0,0,-1);
    }

    public static Vec3f back(){
        return new Vec3f(0,0,1);
    }
}

class Vec2f extends Vecf<Vec2f> {
    float x;
    float y;

    Vec2f(float x, float y){
        this.x = x;
        this.y = y;
        this.dimensions = 2;
    }

    public float get(int i){
        switch (i){
            case 1:return y;
            default: return x;
        }
    }

    public void set(int i, float val){
        switch (i){
            case 1:y = val;
                break;
            default: x = val;
        }
    }

    public Vec2f This() {
        return this;
    }

    public float det(Vec2f v) {
        return x * v.y - y * v.x;
    }
}
