package javasimulation;

public class SimulationCalculations {
    public static double calculatePotentialEnergy(Body[] bodies, double G) {
        double potentialEnergy = 0.0;
        for (int p = 0; p < bodies.length; p++) {
            potentialEnergy += bodies[p].getPotentialEnergy(bodies, G);
        }
        return potentialEnergy;
    }

    public static double calculateKineticEnergy(Body[] bodies) {
        double kineticEnergy = 0.0;
        for (int p = 0; p < bodies.length; p++) {
            kineticEnergy += bodies[p].getKineticEnergy();
        }
            
        return kineticEnergy;
    }

    public static double calculateTotalEnergy(Body[] bodies, double G) {
        return calculateKineticEnergy(bodies) + calculatePotentialEnergy(bodies, G);
    }

    public static double calculateTotalMass(Body[] bodies) {
        double totalMass = 0;
        for (int i = 0; i < bodies.length; i++) {
            totalMass += bodies[i].getMass();
        }
        return totalMass;

    }

    public static Vector calculateCentreOfMass(Body[] bodies) {
        Vector centreOfMass = new Vector();
        for (int p = 0; p < bodies.length; p++) {
            centreOfMass = centreOfMass.add(Vector.multiply(bodies[p].getPosition(), bodies[p].getMass()));
        }
        centreOfMass.divide(calculateTotalMass(bodies));
        
        return centreOfMass;
    }

    public static Vector calculateAngularMomentum(Body[] bodies) {
        Vector L = new Vector();
        for (int p = 0; p < bodies.length; p++) {
            L.add(bodies[p].calculateAngularMomentum());
        }
        return L;
    }

    public static Vector calclateLinearMomentum(Body[] bodies) {
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
