package javasimulation;

@Deprecated
public class Integrator {
    private final IntegratorType type;
    private boolean useVariableTimestep;
    
    public Integrator(IntegratorType type, boolean useVariableTimestep) {
        this.type = type;
        this.useVariableTimestep = useVariableTimestep;
        if (type == IntegratorType.EULER && useVariableTimestep) {
            throw new IllegalArgumentException("Euler integrator does not support variable timestep");
        }
    }

    public void setUseVariableTimestep(boolean useVariableTimestep) {
        if (type == IntegratorType.EULER && useVariableTimestep) {
            throw new IllegalArgumentException("Euler integrator does not support variable timestep");
        }
        this.useVariableTimestep = useVariableTimestep;
    }
    
    public double Integrate(Body[] bodies, double dt) {
        switch (type) {
            case EULER -> {
                Integrators.euler(bodies, dt, useVariableTimestep);
                return dt;
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