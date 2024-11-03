package javasimulation;
import java.util.Scanner;

public class Run{

    public static void main(String[] args) {

        System.out.println("Temp override for testing perturbations (true/false): ");
        Scanner scanner = new Scanner(System.in);
        boolean tempOverride = scanner.nextBoolean();

        if (tempOverride) {
            RunPerturbations runPerturbations = new RunPerturbations(OrbitExamples.orbitExampleMap.get("figureEight"), 1000, 0.01, 1, 0.01);
            double[][] stopMatrix = runPerturbations.run();
            runPerturbations.saveStopMatrix(stopMatrix);
            scanner.close();
            return;
        }
        scanner.close();



        Body[] bodies = OrbitExamples.orbitExampleMap.get(args[0]);
        int N = Integer.parseInt(args[1]);
        double dt = Double.parseDouble(args[2]);
        String[] customOptions = java.util.Arrays.copyOfRange(args, 3, args.length);

        RunSingleOrbit runSingleOrbit = new RunSingleOrbit(bodies, N, dt, customOptions);
        runSingleOrbit.run();
    }
}