package nbodysimulation;

public class run{

    public static void main(String[] args) {
        Body[] bodies = new Body[2];
        bodies[0] = new Body(new Vector(1, 0, 0), new Vector(0, 0.5, 0), 1);
        bodies[1] = new Body(new Vector(-1, 0, 0), new Vector(0, 0.5, 0), 1);
        Simulation simulation = new Simulation(bodies, 1000, 0.01);
        simulation.run();
    }
}