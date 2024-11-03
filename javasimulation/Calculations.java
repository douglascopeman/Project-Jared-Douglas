package javasimulation;

public class Calculations {
    public static double potentialEnergy(Body[] bodies, double G) {
        double potentialEnergy = 0;
        // Weird indexing is to prevent overcounting
        for (int i = 0; i < bodies.length; i++) {
            for (int j = i + 1; j < bodies.length; j++) {
                Body body = bodies[i];
                Body other_body = bodies[j];
                if (body != other_body) {
                    Vector positionDifference = Vector.subtract(body.getPosition(), other_body.getPosition());
                    potentialEnergy += -G * body.getMass() * other_body.getMass() / positionDifference.norm();
                }
            }
        }
        return potentialEnergy;
    }

    public static double kineticEnergy(Body[] bodies) {
        double kineticEnergy = 0.0;
        for (int p = 0; p < bodies.length; p++) {
            kineticEnergy += bodies[p].getKineticEnergy();
        }
            
        return kineticEnergy;
    }

    public static double totalEnergy(Body[] bodies, double G) {
        return kineticEnergy(bodies) + potentialEnergy(bodies, G);
    }

    public static double totalMass(Body[] bodies) {
        double totalMass = 0;
        for (int i = 0; i < bodies.length; i++) {
            totalMass += bodies[i].getMass();
        }
        return totalMass;

    }

    public static Vector centreOfMass(Body[] bodies) {
        Vector centreOfMass = new Vector();
        for (int p = 0; p < bodies.length; p++) {
            centreOfMass = centreOfMass.add(Vector.multiply(bodies[p].getPosition(), bodies[p].getMass()));
        }
        centreOfMass.divide(totalMass(bodies));
        
        return centreOfMass;
    }

    public static Vector angularMomentum(Body[] bodies) {
        Vector L = new Vector();
        for (int p = 0; p < bodies.length; p++) {
            L.add(bodies[p].calculateAngularMomentum());
        }
        return L;
    }

    public static Vector linearMomentum(Body[] bodies) {
        Vector P = new Vector();
        for (int p = 0; p < bodies.length; p++) {
            P.add(bodies[p].calculateLinearMomentum());
        }
        
        return P;
    }

    public static Body[] copyBodies(Body[] bodies) {
        Body[] copiedBodies = new Body[bodies.length];
        for (int i = 0; i < bodies.length; i++) {
            copiedBodies[i] = bodies[i].copy();
        }
        return copiedBodies;
    }
}
