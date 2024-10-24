package javasimulation;

public class Integrators{

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

    public static Body[] SymplecticEuler(Body[] bodies, double dt, boolean useVariableTimestep) {
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

    public static Body[] Yoshida(Body[] bodies, double dt, boolean useVariableTimestep) {
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


    private static double getVariableTimestep(Body[] bodies, double variableTimestepConstant, Integrator integrator) {
        Body[] bodiesClone = bodies.clone();
        double tempTimestep = getVariableTimestepHelper(bodies, variableTimestepConstant);

        Body[] futureBodies = integrator.Integrate(bodiesClone, tempTimestep);

        double tempTimestepBackwards = getVariableTimestepHelper(futureBodies, variableTimestepConstant);

        double averageTimestep = (tempTimestep + tempTimestepBackwards) / 2;

        return averageTimestep;
    }

}