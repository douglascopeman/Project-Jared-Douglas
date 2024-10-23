package javasimulation;

public class Run{

    public static void main(String[] args) {
        Body[] bodies = new Body[2];
        bodies[0] = new Body(new Vector(1, 0, 0), new Vector(0, 0.6, 0), 1);
        bodies[1] = new Body(new Vector(-1, 0, 0), new Vector(0, -0.6, 0), 1);
        int N = Integer.parseInt(args[0]);
        double dt = Double.parseDouble(args[1]);
        Simulation simulation = new Simulation(bodies, N, dt);

        long statTime = System.currentTimeMillis();
        simulation.run();
        long endTime = System.currentTimeMillis();

        System.out.println("\tTime: " + (endTime - statTime) + "ms");
    }
}