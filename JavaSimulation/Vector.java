package javasimulation;

public class Vector{

    private final double x, y, z;

    public Vector(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX(){
        return this.x;
    }

    public double getY(){
        return this.y;
    }

    public double getZ(){
        return this.z;
    }

    public Vector add(Vector other){
        return new Vector(this.x + other.x, this.y + other.y, this.z + other.z);
    }

    public static Vector add(Vector a, Vector b){
        return new Vector(a.x + b.x, a.y + b.y, a.z + b.z);
    }

    public Vector subtract(Vector other){
        return new Vector(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    public static Vector subtract(Vector a, Vector b){
        return new Vector(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static Vector abs(Vector a){
        return new Vector(Math.abs(a.x), Math.abs(a.y), Math.abs(a.z));
    }

    public static double norm(Vector a){
        return Math.sqrt(a.x * a.x + a.y * a.y + a.z * a.z);
    }

    public double norm(){
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
    }

    public Vector multiply(double b){
        return new Vector(this.x * b, this.y * b, this.z * b);
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

    public static Vector divide(double a, Vector b){
        return new Vector(a / b.x, a / b.y, a / b.z);
    }

    @Override
    public String toString(){     
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

}