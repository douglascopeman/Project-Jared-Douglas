package javasimulation;

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
        return this.mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public Vector getPosition() {
        return this.position;
    }

    public void setPosition(Vector position) {
        this.position = position;
    }

    public Vector getVelocity() {
        return this.velocity;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void calculateAcceleration(Body[] bodies, double G) {
        this.acceleration = new Vector(0, 0, 0);
        for (Body other_body : bodies) {
            if (other_body != this) {
                Vector positionDifference = this.getPosition().subtract(other_body.getPosition());

                this.acceleration = this.acceleration.add(
                    positionDifference.multiply(-G * other_body.getMass() / Math.pow(positionDifference.norm(), 3))
                );
            }
        }
    }

    public void calculateAcceleration(Body[] bodies) {
        this.calculateAcceleration(bodies, 1);
    }

    public Vector getAcceleration() {
        return this.acceleration;
    }


}