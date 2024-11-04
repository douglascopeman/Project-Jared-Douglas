package javasimulation;
import java.util.Scanner;

public class Run{

    public static void main(String[] args) {

        //#region testing for perturbations

        System.out.println("Temp override for testing perturbations (true/false): ");
        Scanner scanner = new Scanner(System.in);
        boolean tempOverride = scanner.nextBoolean();

        if (tempOverride) {
            OutputWriter.setupDirectories();
            Perturbations runPerturbations = new Perturbations(OrbitExamples.orbitExampleMap.get("figureEight"), 10000, 0.01, 201, 0.001);
            double[][] stopMatrix = runPerturbations.run();
            OutputWriter.saveMatrix(stopMatrix);
            scanner.close();
            return;
        }
        scanner.close();

        //#endregion

        Body[] bodies = OrbitExamples.orbitExampleMap.get(args[0]);
        int N = Integer.parseInt(args[1]);
        double dt = Double.parseDouble(args[2]);
        String[] commandlineOptions = java.util.Arrays.copyOfRange(args, 3, args.length);

        Simulation simulation = new Simulation(bodies, N, dt, commandlineOptions);
        simulation.run();

    }
}