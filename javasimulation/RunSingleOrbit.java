package javasimulation;
import java.util.HashMap;

public class RunSingleOrbit {
    private Body[] bodies;
    private int N;
    private double dt;
    private IntegratorFunction integrator;
    private boolean checkStopConditions = false;
    private boolean useVariableTimestep = false;
    private boolean calculateCentreOfMass = false;
    private boolean calculateEnergies = false;
    private boolean calculateAngularMomentum = false;
    private boolean calculateLinearMomentum = false;
    private boolean findOrbitLength = false;

    private void setOptions(String[] customOptions) {
        for (String option : customOptions) {
            switch (option) {
                case "-useVariableTimestep":
                    System.out.println("using variable timestep");
                    useVariableTimestep = true;
                    break;
                case "-checkStopConditions":
                    System.out.println("checking stop conditions");
                    checkStopConditions = true;
                    break;
                case "-calculateCentreOfMass":
                    System.out.println("calculating centre of mass");
                    calculateCentreOfMass = true;
                    break;
                case "-calculateEnergies":
                    System.out.println("calculating energies");
                    calculateEnergies = true;
                    break;
                case "-calculateAngularMomentum":
                    System.out.println("calculating angular momentum");
                    calculateAngularMomentum = true;
                    break;
                case "-calculateLinearMomentum":
                    System.out.println("calculating linear momentum");
                    calculateLinearMomentum = true;
                    break;
                case "-findOrbitLength":
                    System.out.println("finding orbit length");
                    findOrbitLength = true;
                    break;
                default:
                    String possibleIntegrator = option.substring(12, option.length());
                    if (Integrators.integratorMap.containsKey(possibleIntegrator)) {
                        System.out.println("integrator: " + possibleIntegrator);
                        integrator = Integrators.integratorMap.get(possibleIntegrator);
                    } else {
                        System.err.println("Invalid option: " + option);
                    }
                    break;
            }
        }

    }
    
    public RunSingleOrbit(Body[] bodies, int N, double dt, String[] customOptions) {

        this.bodies = bodies;
        this.N = N;
        this.dt = dt;

        setOptions(customOptions);
    }

    public void run() {
        Simulation simulation = new Simulation(bodies, N, dt, 1, integrator);

        HashMap<String, Boolean> options = simulation.getOptions();
        options.replace("useVariableTimestep", useVariableTimestep);
        options.replace("checkStopConditions", checkStopConditions);
        options.replace("calculateEnergies", calculateEnergies);
        options.replace("calculateCentreOfMass", calculateCentreOfMass);
        options.replace("calculateAngularMomentum", calculateAngularMomentum);
        options.replace("calculateLinearMomentum", calculateLinearMomentum);
        options.replace("findOrbitLength", findOrbitLength);
        simulation.setOptions(options);

        long statTime = System.currentTimeMillis();
        simulation.run();
        long endTime = System.currentTimeMillis();

        System.out.println("\tTime: " + (endTime - statTime) + "ms");
    }
}
