package javasimulation;

import java.util.HashMap;

public class Integrators{

    public static HashMap<String, IntegratorFunction> integratorMap = new HashMap<>() {{
        put("euler", Integrators::euler);
        put("symplecticEuler", Integrators::symplecticEuler);
        put("threeStepLeapfrog", Integrators::threeStepLeapfrog);
        put("yoshida", Integrators::yoshida);
        put("forestRuth", Integrators::forestRuth);
    }};

    public static double euler(Body[] bodies, double dt, boolean useVariableTimestep) {
        if (useVariableTimestep) {
            System.err.println("Euler integrator does not support variable timestep");
        }

        for (Body body : bodies) {
            body.calculateAcceleration(bodies);
        }
        for (Body body : bodies) {
            body.setPosition(Vector.add(body.getPosition(), Vector.multiply(body.getVelocity(), dt)));
            body.setVelocity(Vector.add(body.getVelocity(), Vector.multiply(body.getAcceleration(), dt)));
        }
        return dt;
    }

    public static double symplecticEuler(Body[] bodies, double dt, boolean useVariableTimestep) {
        if (useVariableTimestep) {
            dt = getVariableTimestep(bodies, dt, integratorMap.get("symplecticEuler"));
        }

        for (Body body : bodies){
            body.calculateAcceleration(bodies);
        }
        for (Body body : bodies) {
            body.setVelocity(Vector.add(body.getVelocity(), Vector.multiply(body.getAcceleration(), dt)));
            body.setPosition(Vector.add(body.getPosition(), Vector.multiply(body.getVelocity(), dt)));
        }
        return dt;
    }

    public static double threeStepLeapfrog(Body[] bodies, double dt, boolean useVariableTimestep) {
        for (Body body : bodies) {
            body.calculateAcceleration(bodies);
        }

        if (useVariableTimestep) {
            dt = getVariableTimestep(bodies, dt, integratorMap.get("threeStepLeapfrog"));
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

        return dt;
    }

    private static void higherOrderHelper(Body[] bodies, double dt, double[] Cs, double[] Ds) {
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
    }

    public static double yoshida(Body[] bodies, double dt, boolean useVariableTimestep) {
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
            dt = getVariableTimestep(bodies, dt, integratorMap.get("yoshida"));
        }

        higherOrderHelper(bodies, dt, Cs, Ds);

        return dt;
    }

    public static double forestRuth(Body[] bodies, double dt, boolean useVariableTimestep) {
        double x = 1.0/6 * (Math.pow(2, 1.0/3) + Math.pow(2, -1.0/3) - 1);
        double[] Cs = {x + 1.0/2, -x, -x, x + 1.0/2};
        double[] Ds = {2*x+1, -4*x-1, 2*x+1, 0};

        for(int i = 0; i < 4; i++){
            if (useVariableTimestep) {
                dt = getVariableTimestep(bodies, dt, integratorMap.get("forestRuth"));
            }
            higherOrderHelper(bodies, dt, Cs, Ds);
        }

        return dt;
    }

    private static double getVariableTimestep(Body[] bodies, double variableTimestepConstant, IntegratorFunction integrator) {
        Body[] bodiesClone = Calculations.copyBodies(bodies);
        double tempTimestep = getVariableTimestepHelper(bodies, variableTimestepConstant);

        integrator.Integrate(bodiesClone, tempTimestep, false);

        double tempTimestepBackwards = getVariableTimestepHelper(bodiesClone, variableTimestepConstant);

        double averageTimestep = (tempTimestep + tempTimestepBackwards) / 2;

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