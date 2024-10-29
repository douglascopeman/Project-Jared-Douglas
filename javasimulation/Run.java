package javasimulation;
import java.util.HashMap;

public class Run{

    public static void main(String[] args) {

        Body[] bodies                   = OrbitExamples.figureEight;
        int N                           = Integer.parseInt(args[0]);
        double dt                       = Double.parseDouble(args[1]);
        IntegratorType integratorType   = args.length == 3  ? IntegratorType.valueOf(args[2]) 
                                                            : IntegratorType.SYMPLECTIC_EULER;
        boolean useVariableTimestep     = args.length == 4  ? Boolean.parseBoolean(args[3]) 
                                                            : false;
        boolean checkStopConditions     = args.length == 5  ? Boolean.parseBoolean(args[4]) 
                                                            : false;
        Simulation simulation; 

        simulation = new Simulation(bodies, N, dt, 1, integratorType);
        
        HashMap<String, Boolean> options = simulation.getOptions();
        options.replace("useVariableTimestep", useVariableTimestep);
        options.replace("checkStopConditions", checkStopConditions);
        simulation.setOptions(options);
       

        long statTime = System.currentTimeMillis();
        simulation.run();
        long endTime = System.currentTimeMillis();

        System.out.println("\tTime: " + (endTime - statTime) + "ms");

    }
}