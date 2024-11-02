package javasimulation;

public class Run{

    public static void main(String[] args) {

        Body[] bodies = OrbitExamples.orbitExampleMap.get(args[0]);
        int N = Integer.parseInt(args[1]);
        double dt = Double.parseDouble(args[2]);
        String[] customOptions = java.util.Arrays.copyOfRange(args, 3, args.length);

        RunSingleOrbit runSingleOrbit = new RunSingleOrbit(bodies, N, dt, customOptions);
        runSingleOrbit.run();
    }
}