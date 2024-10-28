package javasimulation;

// Create a class that implements the existing Integrator interface
public class Integrator implements IntegratorInterface {
    private final IntegratorType type;
    private final boolean useVariableTimestep;
    
    public Integrator(IntegratorType type, boolean useVariableTimestep) {
        this.type = type;
        this.useVariableTimestep = useVariableTimestep;
    }
    
    @Override
    public Body[] Integrate(Body[] bodies, double dt) {
        switch (type) {
            case EULER -> {
                return Integrators.euler(bodies, dt);
            }
            case SYMPLECTIC_EULER -> {
                return Integrators.symplecticEuler(bodies, dt, useVariableTimestep);
            }
            case THREE_STEP_LEAPFROG -> {
                return Integrators.threeStepLeapfrog(bodies, dt, useVariableTimestep);
            }
            case YOSHIDA -> {
                return Integrators.yoshida(bodies, dt, useVariableTimestep);
            }
            case FOREST_RUTH -> {
                return Integrators.forestRuth(bodies, dt, useVariableTimestep);
            }
            default -> throw new IllegalArgumentException("Unknown integrator type");
        }
    }
}