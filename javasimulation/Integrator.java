package javasimulation;
import java.io.FileWriter;
import java.io.IOException;

// Create a class that implements the existing Integrator interface
public class Integrator implements IntegratorInterface {
    private final IntegratorType type;
    private FileWriter timestepWriter;
    
    public Integrator(IntegratorType type, boolean useVariableTimestep) {
        this.type = type;
        if (useVariableTimestep) {
            try {
                FileWriter writer = new FileWriter("Outputs/timesteps.csv", true);
                this.timestepWriter = writer;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            this.timestepWriter = null;
        }
    }
    
    @Override
    public Body[] Integrate(Body[] bodies, double dt) {
        try {
            switch (type) {
                case EULER -> {
                    return Integrators.euler(bodies, dt);
                }
                case SYMPLECTIC_EULER -> {
                    return Integrators.symplecticEuler(bodies, dt, this.timestepWriter);
                }
                case THREE_STEP_LEAPFROG -> {
                    return Integrators.threeStepLeapfrog(bodies, dt, this.timestepWriter);
                }
                case YOSHIDA -> {
                    return Integrators.yoshida(bodies, dt, this.timestepWriter);
                }
                case FOREST_RUTH -> {
                    return Integrators.forestRuth(bodies, dt, this.timestepWriter);
                }
                default -> throw new IllegalArgumentException("Unknown integrator type");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}