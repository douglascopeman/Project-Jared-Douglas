package nbodysimulation;

public class Integrators{

    public static Body[] SymplecticEuler(Body[] bodies, double dt) {
        for (Body body : bodies) {
            body.calculateAcceleration(bodies);
            body.setVelocity(Vector.add(body.getVelocity(), Vector.multiply(body.getAcceleration(), dt)));
            body.setPosition(Vector.add(body.getPosition(), Vector.multiply(body.getVelocity(), dt)));
        }
        return bodies;
    }

    public static Body[] Euler(Body[] bodies, double dt) {
        for (Body body : bodies) {
            body.calculateAcceleration(bodies);
            body.setPosition(Vector.add(body.getPosition(), Vector.multiply(body.getVelocity(), dt)));
            body.setVelocity(Vector.add(body.getVelocity(), Vector.multiply(body.getAcceleration(), dt)));
        }
        return bodies;
    }

}