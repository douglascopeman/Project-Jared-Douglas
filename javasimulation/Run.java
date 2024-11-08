package javasimulation;
import java.util.List;

public class Run{

    public static void main(String[] args) {

        Body[] bodies = OrbitExamples.orbitExampleMap.get(args[0]);
        int N = Integer.parseInt(args[1]);
        double dt = Double.parseDouble(args[2]);
        String[] commandlineOptions = java.util.Arrays.copyOfRange(args, 3, args.length);
        
        List<String> clOptionsList = java.util.Arrays.asList(commandlineOptions);

        if (clOptionsList.contains("-perturbate")) {
            // This definitely needs to be refactored
            int halfGridSize = clOptionsList.stream()
                                            .filter(s -> s.strip().equals("-halfGridSize"))
                                            .map(s -> clOptionsList.get(clOptionsList.indexOf(s) + 1))
                                            .mapToInt(Integer::parseInt)
                                            .findFirst()
                                            .orElseThrow(() -> new IllegalArgumentException("Missing -halfGridSize option"));
            double delta = clOptionsList.stream()
                                        .filter(s -> s.strip().equals("-delta"))
                                        .map(s -> clOptionsList.get(clOptionsList.indexOf(s) + 1))
                                        .mapToDouble(Double::parseDouble)
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalArgumentException("Missing -delta option"));

            System.out.println("Perturbating with halfGridSize: " + halfGridSize + " and delta: " + delta);
            SimulationIO.setupDirectories();
            Perturbations runPerturbations = new Perturbations(bodies, N, dt, halfGridSize, delta);
            double[][] stopMatrix = runPerturbations.run();
            SimulationIO.saveMatrix(stopMatrix);
        } else {

            Simulation simulation = new Simulation(bodies, N, dt, commandlineOptions);
            simulation.run();

        }

    }
}