package javasimulation;
import java.io.FileWriter;
import java.io.IOException;

public class Integrators{

    public static Body[] euler(Body[] bodies, double dt) {
        for (Body body : bodies) {
            body.calculateAcceleration(bodies);
        }
        for (Body body : bodies) {
            body.setPosition(Vector.add(body.getPosition(), Vector.multiply(body.getVelocity(), dt)));
            body.setVelocity(Vector.add(body.getVelocity(), Vector.multiply(body.getAcceleration(), dt)));
        }
        return bodies;
    }

    public static Body[] symplecticEuler(Body[] bodies, double dt, boolean useVariableTimestep) {
        if (useVariableTimestep) {
            dt = getVariableTimestep(bodies, 0.1, new Integrator(IntegratorType.SYMPLECTIC_EULER, false));
        }

        for (Body body : bodies){
            body.calculateAcceleration(bodies);
        }
        for (Body body : bodies) {
            body.setVelocity(Vector.add(body.getVelocity(), Vector.multiply(body.getAcceleration(), dt)));
            body.setPosition(Vector.add(body.getPosition(), Vector.multiply(body.getVelocity(), dt)));
        }
        return bodies;
    }

    public static Body[] threeStepLeapfrog(Body[] bodies, double dt, boolean useVariableTimestep) {
        for (Body body : bodies) {
            body.calculateAcceleration(bodies);
        }

        if (useVariableTimestep) {
            dt = getVariableTimestep(bodies, 0.1, new Integrator(IntegratorType.THREE_STEP_LEAPFROG, false));
        }

        Vector[] half_velocity = new Vector[bodies.length];
        for (int i = 0; i < bodies.length; i++) {
            half_velocity[i] = Vector.add(bodies[i].getVelocity(), Vector.multiply(bodies[i].getAcceleration(), dt/2));
        }

        for (int i = 0; i < bodies.length; i++) {
            bodies[i].setPosition(Vector.add(bodies[i].getPosition(), Vector.multiply(half_velocity[i], dt)));
        }

        for (Body body: bodies) {
            body.calculateAcceleration(bodies);
        }

        for (int i = 0; i < bodies.length; i++) {
            bodies[i].setVelocity(Vector.add(half_velocity[i], Vector.multiply(bodies[i].getAcceleration(), dt/2)));
        }

        return bodies;
    }

    private static Body[] higherOrderHelper(Body[] bodies, double dt, double[] Cs, double[] Ds) {
        for (int i = 0; i < 4; i++){
            for (Body body : bodies){
                body.setPosition(Vector.add(body.getPosition(), Vector.multiply(body.getVelocity(), dt*Cs[i])));
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

    public static Body[] yoshida(Body[] bodies, double dt, boolean useVariableTimestep) {
        double w0 = -(Math.pow(2, 1.0/3))/(2-(Math.pow(2, 1.0/3)));
        double w1 = 1/(2-(Math.pow(2,1.0/3)));
        double[] Cs = new double[4];
        double[] Ds = new double[4];
        Cs[0] = w1/2;
        Cs[3] = w1/2;
        Cs[1] = (w0+w1)/2;
        Cs[2] = (w0+w1)/2;
        Ds[0] = w1;
        Ds[2] = w1;
        Ds[1] = w0;

        if (useVariableTimestep) {
            dt = getVariableTimestep(bodies, 0.1, new Integrator(IntegratorType.YOSHIDA, false));
        }

        higherOrderHelper(bodies, dt, Cs, Ds);

        return bodies;
    }

    public static Body[] forestRuth(Body[] bodies, double dt, boolean useVariableTimestep){
        double x = 1.0/6 * (Math.pow(2, 1.0/3) + Math.pow(2, -1.0/3) - 1);
        double[] Cs = {x + 1.0/2, -x, -x, x + 1.0/2};
        double[] Ds = {2*x+1, -4*x-1, 2*x+1, 0};

        for(int i = 0; i < 4; i++){
            if (useVariableTimestep) {
                dt = getVariableTimestep(bodies, 0.1, new Integrator(IntegratorType.FOREST_RUTH, false));
            }
            higherOrderHelper(bodies, dt, Cs, Ds);
        }

        return bodies;
    }

    private static double getVariableTimestep(Body[] bodies, double variableTimestepConstant, Integrator integrator) {
        Body[] bodiesClone = bodies.clone();
        double tempTimestep = getVariableTimestepHelper(bodies, variableTimestepConstant);

        Body[] futureBodies = integrator.Integrate(bodiesClone, tempTimestep);

        double tempTimestepBackwards = getVariableTimestepHelper(futureBodies, variableTimestepConstant);

        double averageTimestep = (tempTimestep + tempTimestepBackwards) / 2;

        try (FileWriter writer = new FileWriter("Outputs/timesteps.csv", true)) {
            writer.append(String.valueOf(averageTimestep)).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return averageTimestep;
    }

    private static double getVariableTimestepHelper(Body[] bodies, double variableTimestepConstant) {
        double maxVelocityDifferenceNorm = 0;
        double minPositionDifferenceNorm = Double.MAX_VALUE;
        for (Body body : bodies) {
            for (Body otherBody : bodies) {
                if (body != otherBody) {
                    Vector velocityDifference = body.getVelocity().subtract(otherBody.getVelocity());
                    double velocityNorm = Vector.norm(velocityDifference);
                    if (velocityNorm > maxVelocityDifferenceNorm) {
                        maxVelocityDifferenceNorm = velocityNorm;
                    }

                    Vector positionDifference = body.getPosition().subtract(otherBody.getPosition());
                    double positionNorm = Vector.norm(positionDifference);
                    if (positionNorm < minPositionDifferenceNorm) {
                        minPositionDifferenceNorm = positionNorm;
                    }
                }
            }
        }
        return variableTimestepConstant * minPositionDifferenceNorm / maxVelocityDifferenceNorm;
    }

}