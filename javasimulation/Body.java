package javasimulation;

public class Body {
    private double mass;
    private Vector position;
    private final Vector initialPosition;
    private Vector velocity;
    private Vector acceleration;

    public Body(Vector position, Vector velocity, double mass) {
        this.position = position;
        this.initialPosition = position;
        this.velocity = velocity;
        this.mass = mass;
    }

    public Body(Vector position, Vector velocity) {
        this(position, velocity, 1);
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

    public Vector getInitialPosition() {
        return initialPosition;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }

    public void calculateAcceleration(Body[] bodies, double G) {
        this.acceleration = new Vector();
        for (Body other_body : bodies) {
            if (other_body != this) {
                Vector positionDifference = Vector.subtract(getPosition(), other_body.getPosition());
                double scalingFactor = -G * other_body.getMass() / Math.pow(positionDifference.norm(), 3);
                Vector accelerationContribution = Vector.multiply(positionDifference, scalingFactor);

                this.acceleration = Vector.add(acceleration, accelerationContribution);
            }
        }
    }

    public void calculateAcceleration(Body[] bodies) {
        this.calculateAcceleration(bodies, 1);
    }

    public Vector getAcceleration() {
        return this.acceleration;
    }

    public double getPotentialEnergy(Body[] bodies, double G) {
        double kineticEnergy = 0;
        for (Body body : bodies) {
            if (body != this) {
                kineticEnergy += -G * mass * body.getMass() / Vector.subtract(getPosition(), body.getPosition()).norm();
            }
        }
        return kineticEnergy;
    }

    public double getKineticEnergy() {
        return 0.5 * mass * Vector.dot(velocity, velocity);
    }

    public Vector calculateAngularMomentum() {
        Vector pos_vel_cross = Vector.cross(position, velocity);
        return Vector.multiply(pos_vel_cross, mass);
    }

    public Vector calculateLinearMomentum() {
        return Vector.multiply(velocity, mass);
    }

    public Body copy() {
        return new Body(position.copy(), velocity.copy(), mass);
    }

}