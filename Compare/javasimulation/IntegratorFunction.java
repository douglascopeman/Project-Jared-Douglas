package javasimulation;

@FunctionalInterface
public interface IntegratorFunction {
    double Integrate(Body[] bodies, double dt, boolean useVariableTimestep);
}
