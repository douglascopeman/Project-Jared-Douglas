package javasimulation;

public class Vector{

    private final double x, y, z;

    public Vector(){
        this.x = 0;
        this.y = 0;
        this.z = 0;
    }

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

    public static double dot(Vector a, Vector b){
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }

    public static Vector cross(Vector a, Vector b){
        return new Vector(a.y * b.z - a.z * b.y, a.z * b.x - a.x * b.z, a.x * b.y - a.y * b.x);
    }

    public static double norm(Vector a){
        return Math.sqrt(dot(a, a));
    }

    public double norm(){
        return Math.sqrt(dot(this, this));
    }

    public Vector multiply(double b){
        return new Vector(this.x * b, this.y * b, this.z * b);
    }

    public static Vector multiply(Vector a, double b){
        return new Vector(a.x * b, a.y * b, a.z * b);
    }

    public static Vector multiply(Vector a, Vector b){
        return new Vector(a.x * b.x, a.y * b.y, a.z * b.z);
    }

    public static Vector pow(Vector a, double b){
        return new Vector(Math.pow(a.x, b), Math.pow(a.y, b), Math.pow(a.z, b));
    }

    public static Vector divide(Vector a, double b){
        return new Vector(a.x / b, a.y / b, a.z / b);
    }

    public Vector divide(double b){
        return new Vector(this.x / b, this.y / b, this.z / b);
    }

    public static Vector divide(double a, Vector b){
        return new Vector(a / b.x, a / b.y, a / b.z);
    }

    @Override
    public String toString(){     
        return "(" + this.x + ", " + this.y + ", " + this.z + ")";
    }

}