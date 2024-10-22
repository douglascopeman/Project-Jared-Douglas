package javasimulation;

public class Integrators{

    public static Body[] SymplecticEuler(Body[] bodies, double dt) {
        for (Body body : bodies){
            body.calculateAcceleration(bodies);
        }
        for (Body body : bodies) {
            body.setVelocity(Vector.add(body.getVelocity(), Vector.multiply(body.getAcceleration(), dt)));
            body.setPosition(Vector.add(body.getPosition(), Vector.multiply(body.getVelocity(), dt)));
        }
        return bodies;
    }

    public static Body[] Yoshida(Body[] bodies, double dt) {
        double w0 = -(Math.pow(2, 1/3))/(2-(Math.pow(2, 1/3)));
        double w1 = 1/(2-(Math.pow(2,1/3)));
        double[] Cs = new double[4];
        double[] Ds = new double[4];
        Cs[0] = w1/2;
        Cs[3] = w1/2;
        Cs[1] = (w0+w1)/2;
        Cs[2] = (w0+w1)/2;
        Ds[0] = w1;
        Ds[2] = w1;
        Ds[1] = w0;

        for (int i = 0; i < 5; i++){
            for (Body body : bodies){
                body.setPosition(Vector.add(body.getVelocity(), Vector.multiply(body.getVelocity(), dt*Cs[i])));
            }
            for (Body body : bodies) {
                body.calculateAcceleration(bodies);
            }
            for (Body body : bodies) {
            body.setVelocity(Vector.add(body.getVelocity(), Vector.multiply(body.getAcceleration(), dt*Ds[i])));
            }
        }

        return bodies;
    }

    //Yet to be tested for functionality
    public static Body[] Euler(Body[] bodies, double dt) {
        for (Body body : bodies) {
            body.calculateAcceleration(bodies);
        }
        for (Body body : bodies) {
            body.setPosition(Vector.add(body.getPosition(), Vector.multiply(body.getVelocity(), dt)));
            body.setVelocity(Vector.add(body.getVelocity(), Vector.multiply(body.getAcceleration(), dt)));
        }
        return bodies;
    }

}