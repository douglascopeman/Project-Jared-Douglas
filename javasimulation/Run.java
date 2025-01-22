package javasimulation;
import java.util.List;
import java.util.Arrays;

public class Run{

    public static void main(String[] args) {
        Body[] bodies = OrbitExamples.orbitExampleMap.get(args[0]);
        int N = Integer.parseInt(args[1]);
        double dt = Double.parseDouble(args[2]);
        String[] commandlineOptions = Arrays.copyOfRange(args, 3, args.length);
        List<String> clOptionsList = Arrays.asList(commandlineOptions);

        if (clOptionsList.contains("--perturbPositions") || clOptionsList.contains("--perturbVelocities")) {
            SimulationIO.setupDirectories();
            Perturbations perturbations = new Perturbations(bodies, N, dt, clOptionsList);
            if(clOptionsList.contains("peturbEnergies")){
                perturbations.runEnergyLayers();
            } else {
                perturbations.run();
            }
        } else if (clOptionsList.contains("--perturbAngularMomentum")) {
            SimulationIO.setupDirectories();
            Perturbations perturbations = new Perturbations(bodies, N, dt, clOptionsList);
            perturbations.runAngularMomentum();
        } else if (clOptionsList.contains("--perturbSingular")){
            int perturbSingularIndex = clOptionsList.indexOf("--perturbSingular");
            Perturbations perturbations = new Perturbations(bodies, N, dt);
            // bodies = perturbations.perturbPositions(Integer.parseInt(clOptionsList.get(perturbSingularIndex + 1)), Integer.parseInt(clOptionsList.get(perturbSingularIndex + 2)), Float.parseFloat(clOptionsList.get(perturbSingularIndex + 3)));
            // Simulation simulation = new Simulation(bodies, N, dt, clOptionsList);
            // simulation.run();
            perturbations.perturbSingular(Integer.parseInt(clOptionsList.get(perturbSingularIndex + 1)), Integer.parseInt(clOptionsList.get(perturbSingularIndex + 2)), Float.parseFloat(clOptionsList.get(perturbSingularIndex + 3)));
        }else {
            Simulation simulation = new Simulation(bodies, N, dt, clOptionsList);
            simulation.run();
        }

        //java.awt.Toolkit.getDefaultToolkit().beep();
    }
}