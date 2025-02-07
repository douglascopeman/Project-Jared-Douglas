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

        // We consider the cases where we perturb the positions, velocities, energy, angular momentum across an x-y delta, or if we consider a single orbit perturbation or given conditions, in that order
        if (clOptionsList.contains("--perturbPositions") || clOptionsList.contains("--perturbVelocities")) {
            SimulationIO.setupDirectories();
            Perturbations perturbations = new Perturbations(bodies, N, dt, clOptionsList);
            perturbations.run();
        } else if (clOptionsList.contains("--perturbEnergy")) {
            SimulationIO.setupDirectories();
            Perturbations perturbations = new Perturbations(bodies, N, dt, clOptionsList);
            perturbations.runEnergy();
        } else if (clOptionsList.contains("--perturbAngularMomentum")) {
            SimulationIO.setupDirectories();
            Perturbations perturbations = new Perturbations(bodies, N, dt, clOptionsList);
            perturbations.runAngularMomentum();
        } else if (clOptionsList.contains("--perturbSingular")){
            SimulationIO.setupDirectories();
            int perturbSingularIndex = clOptionsList.indexOf("--perturbSingular");
            Perturbations perturbations = new Perturbations(bodies, N, dt, clOptionsList);
            int i_shift = Integer.parseInt(clOptionsList.get(perturbSingularIndex + 1));
            int j_shift = Integer.parseInt(clOptionsList.get(perturbSingularIndex + 2));
            float delta = Float.parseFloat(clOptionsList.get(perturbSingularIndex + 3));
            // bodies = perturbations.perturbPositions(i_shift, j_shift, delta);
            //Simulation simulation = new Simulation(bodies, N, dt, clOptionsList);
            //simulation.run();

            perturbations.perturbSingular(i_shift, j_shift, delta);
        } else {
            SimulationIO.setupDirectories();
            Simulation simulation = new Simulation(bodies, N, dt, clOptionsList);
            simulation.run();
        }
    }
}