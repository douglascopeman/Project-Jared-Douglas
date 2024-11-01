package javasimulation;
import java.util.HashMap;

public class Run{

    public static void main(String[] args) {

        Body[] bodies                   = OrbitExamples.circular;
        int N                           = Integer.parseInt(args[0]);
        double dt                       = Double.parseDouble(args[1]);
        boolean checkStopConditions     = false;
        boolean useVariableTimestep     = false;
        IntegratorFunction integrator   = Integrators::symplecticEuler;

        switch (args.length) {
            case 5:
                checkStopConditions = Boolean.parseBoolean(args[4]);
            case 4: 
                useVariableTimestep = Boolean.parseBoolean(args[3]);
            case 3:
                integrator = Integrators.integratorMap.get(args[2]);
                break;
            default:
                break;
        }

        Simulation simulation; 

        simulation = new Simulation(bodies, N, dt, 1, integrator);

        HashMap<String, Boolean> options = simulation.getOptions();
        options.replace("useVariableTimestep", useVariableTimestep);
        options.replace("checkStopConditions", checkStopConditions);
        options.replace("calculateEnergies", true);
        simulation.setOptions(options);
       

        long statTime = System.currentTimeMillis();
        simulation.run();
        long endTime = System.currentTimeMillis();

        System.out.println("\tTime: " + (endTime - statTime) + "ms");

    }
}