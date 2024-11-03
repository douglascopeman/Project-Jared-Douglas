package javasimulation;

public class Perturbations {
    private final Body[] bodies;
    private int N;
    private double dt;
    private int halfGridSize;
    private double delta;
    private final double originalEnergy;
    private final Vector originalCentreOfMass;
    private final Vector originalAngularMomentum;

    public Perturbations(Body[] bodies, int N, double dt, int halfGridSize, double delta) {
        this.bodies = Calculations.copyBodies(bodies);
        this.N = N;
        this.dt = dt;
        if (halfGridSize % 2 == 0) {
            throw new IllegalArgumentException("halfGridSize must be an odd number");
        }
        this.halfGridSize = halfGridSize;
        this.delta = delta;
        this.originalEnergy = Calculations.totalEnergy(bodies, 1);
        this.originalCentreOfMass = Calculations.centreOfMass(bodies);
        this.originalAngularMomentum = Calculations.angularMomentum(bodies);
    }

    private Body[] perturbBodies(int i, int j, double delta) {
        Body[] perturbedBodies = Calculations.copyBodies(bodies);
        
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

        // Save the perturbation settings
        OutputWriter.writePerturbationSettingsToFile(N, dt, N, delta, halfGridSize);

        // Iterate over the grid of perturbations
        for (int i = -halfGridSize; i <= halfGridSize; i++) {
            for (int j = -halfGridSize; j <= halfGridSize; j++) {
                // Perturb the bodies
                System.out.println("Perturbing bodies (" + i + ", " + j + ")");
                Body[] perturbedBodies = perturbBodies(i, j, delta);


                // #region Sanity checks to be removed later
                double perturbedEnergy = Calculations.totalEnergy(perturbedBodies, 1);
                Vector perturbedCentreOfMass = Calculations.centreOfMass(perturbedBodies);
                Vector perturbedAngularMomentum = Calculations.angularMomentum(perturbedBodies);

                assert Math.abs(perturbedEnergy - originalEnergy) < 1e-10;
                assert perturbedCentreOfMass.subtract(originalCentreOfMass).norm() < 1e-10;
                assert perturbedAngularMomentum.subtract(originalAngularMomentum).norm() < 1e-10;
                // #endregion
                
                // Run the simulation
                String[] options = new String[] {"-integrator yoshida", 
                                                "-checkStopConditions", 
                                                "-calculateEnergies", 
                                                "-calculateCentreOfMass", 
                                                "-useVariableTimestep", 
                                                "-skipSaveToCSV"};
                Simulation simulation = new Simulation(perturbedBodies, N, dt, options);

                try {
                    simulation.run();
                } catch (Exception e) {
                } finally {
                    stopMatrix[i + halfGridSize][j + halfGridSize] = simulation.getCurrentTimestep();
                    System.out.println();
                }
            }
        }

        return stopMatrix;
    }

}
