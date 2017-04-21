public abstract class Veci<T extends Veci<T>>{
    int dimensions;

    public static Veci construct(int dimensions){
        switch(dimensions){
            case 3:return new Veci3(0,0,0);
            default:return new Vec2i(0,0);
        }
    }

    public T add(T v){
        return iterate((i) -> set(i, get(i) + v.get(i)));
    }

    public T sub(T v){
        return iterate((i) -> set(i, get(i) - v.get(i)));
    }

    public T scale(float s){
        return iterate((i) -> set(i, (int)(get(i) * s)));
    }

    public T lerp(T v, float weight){
        return c().add(v.c().sub(This()).scale(weight));
    }

    public int dot(T v){
        int sum = 0;
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
        T c = (T) Veci.construct(dimensions);
        iterate((i) -> c.set(i, get(i)));
        return c;
    }

    public Veci overwrite(Veci v){
        return iterate((i) -> set(i, v.get(i)));
    }

    public T iterate(Iterator iterator){
        for(int i = 0; i < dimensions; i++)iterator.iterate(i);
        return This();
    }

    interface Iterator{
        void iterate(int i);
    }

    public abstract int get(int i);
    public abstract void set(int i, int val);
    public abstract T This();

    public boolean equals(T v){
        for(int i = 0; i < dimensions; i++)if(get(i) != v.get(i)) return false;
        return true;
    }

    public Vecf toFloat(){
        Vecf f = Vecf.construct(dimensions);
        for(int i = 0; i < dimensions; i++) f.set(i, get(i));
        return f;
    }
}

class Veci3 extends Veci<Veci3> {
    int x;
    int y;
    int z;

    Veci3(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
        this.dimensions = 3;
    }

    Veci3(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.dimensions = 3;
    }

    public int get(int i){
        switch (i){
            case 1:return y;
            case 2:return z;
            default: return x;
        }
    }

    public void set(int i, int val){
        switch (i){
            case 1:y = val;
                break;
            case 2:z = val;
                break;
            default: x = val;
        }
    }

    public Veci3 This() {
        return this;
    }

    public Vec3f cross(Vec3f v){
        return new Vec3f(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
        );
    }
}

class Vec2i extends Veci<Vec2i> {
    int x;
    int y;

    Vec2i(int x, int y){
        this.x = x;
        this.y = y;
        this.dimensions = 2;
    }

    public int get(int i){
        switch (i){
            case 1:return y;
            default: return x;
        }
    }

    public void set(int i, int val){
        switch (i){
            case 1:y = val;
                break;
            default: x = val;
        }
    }

    public Vec2i This() {
        return this;
    }
}
