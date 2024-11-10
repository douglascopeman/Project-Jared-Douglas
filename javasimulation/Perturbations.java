package javasimulation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.List;


public class Perturbations {
    private final Body[] bodies;
    private int N;
    private double dt;
    private int halfGridSize;
    private double delta;

    private HashMap<String, Boolean> options = new HashMap<String, Boolean>();
    private IntegratorFunction simulationIntegrator;

    private final double originalEnergy;
    private final Vector originalCentreOfMass;
    private final Vector originalAngularMomentum;

    public Perturbations(Body[] bodies, int N, double dt) {
        this.bodies = Calculations.copyBodies(bodies);
        this.N = N;
        this.dt = dt;

        SimulationIO.setDefaultPerturbationsOptions(options);

        this.originalEnergy = Calculations.totalEnergy(bodies, 1);
        this.originalCentreOfMass = Calculations.centreOfMass(bodies);
        this.originalAngularMomentum = Calculations.angularMomentum(bodies);
    }

    public Perturbations(Body[] bodies, int N, double dt, int halfGridSize, double delta) {
        this(bodies, N, dt);

        this.halfGridSize = halfGridSize;
        this.delta = delta;
    }

    public Perturbations(Body[] bodies, int N, double dt, List<String> clOptions) {
        this(bodies, N, dt);
        SimulationIO.setPerturbationsSettings(this, clOptions);
    }

    public HashMap<String, Boolean> getOptions() {
        return options;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public void setHalfGridSize(int halfGridSize) {
        this.halfGridSize = halfGridSize;
    }

    public void setIntegratorFunction(IntegratorFunction integratorFunction) {
        this.simulationIntegrator = integratorFunction;
    }

    private Body[] perturbPositions(int i, int j, double delta) {
        Body[] perturbedBodies = Calculations.copyBodies(bodies);
        
        // Perturn body 0 and adjust body 2 accordingly
        Vector perturbedPosition = Vector.add(bodies[0].getPosition(), new Vector(i * delta, j * delta, 0));

        perturbedBodies[0].setPosition(perturbedPosition);
        perturbedBodies[2].setPosition(perturbedPosition.negate());

        // Find the new magnitude of velocity required to preserve the energy
        double newVelocity = Math.sqrt((1.0/3.0) * (originalEnergy + 5.0/(2.0 * perturbedBodies[0].getPosition().norm())));

        // Finally, preserve the angular momentum by setting the velocity of body 1 to (-2) times that of bodies 0 and 2

        perturbedBodies[0].setVelocity(bodies[0].getVelocity().normalise().multiply(newVelocity));
        perturbedBodies[2].setVelocity(perturbedBodies[0].getVelocity());
        perturbedBodies[1].setVelocity(perturbedBodies[0].getVelocity().multiply(-2.0));

        return perturbedBodies;
    }

    private Body[] perturbVelocities(int i, int j, double detla) {
        Body[] perturbedBodies = Calculations.copyBodies(bodies);

        // Perturb body 0 and then preserve angular momentum by setting the velocities of bodies 1 and 2 accordingly
        Vector perturbedVelocity = Vector.add(bodies[0].getVelocity(), new Vector(i * delta, j * delta, 0));
        perturbedBodies[0].setVelocity(perturbedVelocity);
        perturbedBodies[2].setVelocity(perturbedVelocity);
        perturbedBodies[1].setVelocity(perturbedVelocity.multiply(-2.0));

        // finally, preserve the energy by ensuring the magnitude of body 0's position is correct
        double newMagnitude = -5.0/(2.0 * (originalEnergy - 3.0*Math.pow(bodies[0].getVelocity().norm(),2)));

        perturbedBodies[0].setPosition(bodies[0].getPosition().normalise().multiply(newMagnitude));
        perturbedBodies[2].setPosition(perturbedBodies[0].getPosition().negate());

        return perturbedBodies;
    }


    public double[][] run() {
        // Initialise the stop matrix populated with the time at which the simulation stops
        double[][] stopMatrix = new double[2 * halfGridSize + 1][2 * halfGridSize + 1];
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Save the perturbation settings
        SimulationIO.writePerturbationSettingsToFile(N, dt, N, delta, halfGridSize);

        // Iterate over the grid of perturbations
        for (int i = -halfGridSize; i <= halfGridSize; i++) {
            for (int j = -halfGridSize; j <= halfGridSize; j++) {
                final int rowIndex = i;
                final int columnIndex = j;
                executor.submit(() -> simulationThread(rowIndex, columnIndex, stopMatrix));
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        } catch (RuntimeException e) {
            System.err.println("Error in executor.awaitTermination");
        }

        return stopMatrix;
    }

    private void simulationThread(int rowIndex, int columnIndex, double[][] stopMatrix) {
        // Perturb the bodies
        Body[] perturbedBodies = bodies;
        if (options.get("perturbPositions")) {
            perturbedBodies = perturbPositions(rowIndex, columnIndex, delta);
        } else if (options.get("perturbVelocities")) {
            perturbedBodies = perturbVelocities(rowIndex, columnIndex, delta);
        }


        // #region Sanity checks to be removed later
        double perturbedEnergy = Calculations.totalEnergy(perturbedBodies, 1);
        Vector perturbedCentreOfMass = Calculations.centreOfMass(perturbedBodies);
        Vector perturbedAngularMomentum = Calculations.angularMomentum(perturbedBodies);

        assert Math.abs(perturbedEnergy - originalEnergy) < 1e-10;
        assert perturbedCentreOfMass.subtract(originalCentreOfMass).norm() < 1e-10;
        assert perturbedAngularMomentum.subtract(originalAngularMomentum).norm() < 1e-10;
        // #endregion

        Simulation simulation = new Simulation(perturbedBodies, N, dt, options);
        simulation.setIntegratorFunction(simulationIntegrator);
        Thread simulationThread = new Thread(simulation);
        simulationThread.setName("(" + rowIndex + ", " + columnIndex + ")");

        try {
            simulationThread.start();
            simulationThread.join();
        } catch (Exception e) {
        } finally {
            int gridSize = 2 * halfGridSize + 1;
            stopMatrix[rowIndex + halfGridSize][gridSize - (columnIndex + halfGridSize)] = simulation.getElapsedTime();
            char stopCode = simulation.getStopCode();
            String ThreadName = "Thread " + simulationThread.getName();
            System.out.println(String.format("%-" + 25 + "s", ThreadName) + stopCode);
        }

    }

}
