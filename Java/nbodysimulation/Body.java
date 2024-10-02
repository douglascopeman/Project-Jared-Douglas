package nbodysimulation;

public class Body {
    private double mass;
    private Vector position;
    private Vector velocity;
    private Vector acceleration;

    public Body(Vector position, Vector velocity, double mass) {
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
    }

    // Getters & Setters
    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public Vector getPosition() {
        return position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void calculateAcceleration(Body[] bodies, double G) {
        this.acceleration = new Vector(0, 0, 0);
        for (Body other_body : bodies) {
            if (other_body != this) {
                this.acceleration = Vector.add(this.acceleration, Vector.vectorMultiply(Vector.divide(Vector.pow(Vector.abs(this.getPosition().add(other_body.getPosition())), 3), G * other_body.getMass()), other_body.getPosition().add(this.getPosition())));
            }
        }
    }

    public void calculateAcceleration(Body[] bodies) {
        this.calculateAcceleration(bodies, 1);
    }

    public Vector getAcceleration() {
        return acceleration;
    }


}