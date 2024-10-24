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
                return Integrators.Euler(bodies, dt);
            }
            case SYMPLECTIC_EULER -> {
                return Integrators.SymplecticEuler(bodies, dt, useVariableTimestep);
            }
            case YOSHIDA -> {
                return Integrators.Yoshida(bodies, dt, useVariableTimestep);
            }
            default -> throw new IllegalArgumentException("Unknown integrator type");
        }
    }
}