package nbodysimulation;

public class Vector{

    double x, y, z;

    public Vector(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector add(Vector other){
        return new Vector(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public static Vector add(Vector a, Vector b){
        return new Vector(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public static Vector abs(Vector a){
        return new Vector(Math.abs(a.x), Math.abs(a.y), Math.abs(a.z));
    }

    public static Vector multiply(Vector a, double b){
        return new Vector(a.x * b, a.y * b, a.z * b);
    }

    public static Vector vectorMultiply(Vector a, Vector b){
        return new Vector(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    public static Vector pow(Vector a, double b){
        return new Vector(Math.pow(a.x, b), Math.pow(a.y, b), Math.pow(a.z, b));
    }

    public static Vector divide(Vector a, double b){
        return new Vector(a.x / b, a.y / b, a.z / b);
    }

}