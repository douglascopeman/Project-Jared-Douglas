package javasimulation;
import java.io.FileWriter;

public class RunPerturbations {
    private final Body[] bodies;
    private int N;
    private double dt;
    private int halfGridSize;
    private double delta;
    private final double originalEnergy;
    private final Vector originalCentreOfMass;
    private final Vector originalAngularMomentum;

    public RunPerturbations(Body[] bodies, int N, double dt, int halfGridSize, double delta) {
        this.bodies = SimulationCalculations.copyBodies(bodies);
        this.N = N;
        this.dt = dt;
        if (halfGridSize % 2 == 0) {
            throw new IllegalArgumentException("halfGridSize must be an odd number");
        }
        this.halfGridSize = halfGridSize;
        this.delta = delta;
        this.originalEnergy = SimulationCalculations.calculateTotalEnergy(bodies, 1);
        this.originalCentreOfMass = SimulationCalculations.calculateCentreOfMass(bodies);
        this.originalAngularMomentum = SimulationCalculations.calculateAngularMomentum(bodies);
    }

    private Body[] perturbBodies(int i, int j, double delta) {
        Body[] perturbedBodies = SimulationCalculations.copyBodies(bodies);
        
        // Perturn body 0 and adjust body 2 accordingly
        Vector perturbedPosition = Vector.add(bodies[0].getPosition(), new Vector(i * delta, j * delta, 0));

        perturbedBodies[0].setPosition(perturbedPosition);
        perturbedBodies[2].setPosition(perturbedPosition.negate());

        // Find the new magnitude of velocity required to preserve the energy
        double newVelocity = Math.sqrt((1.0/3.0) * (originalEnergy + 5.0/(2.0 * perturbedBodies[0].getPosition().norm())));

        // Finally, preserve the angular momentum by setting the velocity of body 1 to (-2) times that of bodies 0 and 2

        perturbedBodies[0].setVelocity(bodies[0].getVelocity().normalise().multiply(newVelocity));
        perturbedBodies[2].setVelocity(perturbedBodies[0].getVelocity().negate());
        perturbedBodies[1].setVelocity(perturbedBodies[0].getVelocity().multiply(-2.0));

        return perturbedBodies;
    }

    public double[][] run() {
        // Initialise the stop matrix populated with the time at which the simulation stops
        double[][] stopMatrix = new double[2 * halfGridSize + 1][2 * halfGridSize + 1];

        // Iterate over the grid of perturbations
        for (int i = -halfGridSize; i <= halfGridSize; i++) {
            for (int j = -halfGridSize; j <= halfGridSize; j++) {
                // Perturb the bodies
                System.out.println("Perturbing bodies (" + i + ", " + j + ")");
                Body[] perturbedBodies = perturbBodies(i, j, delta);

                // double perturbedEnergy = SimulationCalculations.calculateTotalEnergy(perturbedBodies, 1);
                // Vector perturbedCentreOfMass = SimulationCalculations.calculateCentreOfMass(perturbedBodies);
                // Vector perturbedAngularMomentum = SimulationCalculations.calculateAngularMomentum(perturbedBodies);

                // assert Math.abs(perturbedEnergy - originalEnergy) < 1e-10;
                // assert perturbedCentreOfMass.subtract(originalCentreOfMass).norm() < 1e-10;
                // assert perturbedAngularMomentum.subtract(originalAngularMomentum).norm() < 1e-10;

                // Run the simulation
                // Simulation simulation = new Simulation(perturbedBodies, N, dt, 1, Integrators::yoshida);
                RunSingleOrbit simulation = new RunSingleOrbit(perturbedBodies, 10000, 0.01, new String[] {"-integrator yoshida", "-checkStopConditions", "-calculateEnergies", "-calculateCentreOfMass", "-useVariableTimestep"});

                try {
                    simulation.run();
                } catch (Exception e) {
                    System.err.println("Error running simulation for perturbation (" + i + ", " + j + ")");
                    System.out.println("Time elapsed: " + simulation.getElapsedTime());
                    e.printStackTrace();
                } finally {
                    stopMatrix[i + halfGridSize][j + halfGridSize] = simulation.getElapsedTime();
                }
            }
        }

        return stopMatrix;
    }

    public void saveStopMatrix(double[][] stopMatrix) {
        try (FileWriter writer = new FileWriter("Outputs\\stopMatrix.csv")) {
            for (int i = 0; i < stopMatrix.length; i++) {
                for (int j = 0; j < stopMatrix[i].length; j++) {
                    writer.write(stopMatrix[i][j] + ",");
                }
                writer.write("\n");
            }
            
        } catch (Exception e) {
            System.out.println("Error writing stop matrix to file");
        }
    }

}
