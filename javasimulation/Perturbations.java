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
    private int gridSize;
    private double delta;

    private HashMap<String, Boolean> options = new HashMap<String, Boolean>();
    private IntegratorFunction simulationIntegrator;

    private double originalEnergy;
    private final Vector originalCentreOfMass;
    private final Vector originalAngularMomentum;

    private double[][] timeMatrix;
    private char[][] stopCodeMatrix;

    public Perturbations(Body[] bodiesOriginal, int N, double dt) {
        this.bodies = Calculations.copyBodies(bodiesOriginal);
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
        this.gridSize = 2 * halfGridSize + 1;
        this.delta = delta;
    }

    public Perturbations(Body[] bodies, int N, double dt, List<String> clOptions) {
        this(bodies, N, dt);
        SimulationIO.setPerturbationsSettings(this, clOptions);
        gridSize = 2 * halfGridSize + 1;
        timeMatrix = new double[2 * halfGridSize + 1][2 * halfGridSize + 1];
        stopCodeMatrix = new char[2 * halfGridSize + 1][2 * halfGridSize + 1];
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

    public void shiftEnergy(double shiftEnergy){
        originalEnergy =(1+shiftEnergy)*originalEnergy;
        double newVelocity = Math.sqrt((1.0/3.0) * (originalEnergy + 5.0/(2.0 * bodies[0].getPosition().norm())));
        // Finally, preserve the angular momentum by setting the velocity of body 1 to (-2) times that of bodies 0 and 2
        bodies[0].setVelocity(bodies[0].getVelocity().normalise().multiply(newVelocity));
        bodies[2].setVelocity(bodies[0].getVelocity());
        bodies[1].setVelocity(bodies[0].getVelocity().multiply(-2.0));
    }

    private Body[] perturbPositions(int i, int j, double delta) {
        Body[] perturbedBodies = Calculations.copyBodies(bodies);
        
        // Perturn body 0 and adjust body 2 accordingly
        Vector perturbedPosition = Vector.add(bodies[0].getPosition(), new Vector(i * delta, j * delta, 0));

        perturbedBodies[0].setPosition(perturbedPosition);
        perturbedBodies[2].setPosition(perturbedPosition.negate());

        // Find the new magnitude of velocity required to preserve the energy
        // First check that the root of the energy term is real
        double energyTerm = originalEnergy + 5.0/(2.0 * perturbedBodies[0].getPosition().norm());
        if (energyTerm < 0) {
            return null;
        }


        double newVelocity = Math.sqrt((1.0/3.0) * energyTerm);

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


    public void run() {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Save the perturbation settings
        SimulationIO.writePerturbationSettingsToFile(N, dt, N, delta, halfGridSize);

        // Iterate over the grid of perturbations
        for (int i = -halfGridSize; i <= halfGridSize; i++) {
            for (int j = -halfGridSize; j <= halfGridSize; j++) {
                final int rowIndex = i;
                final int columnIndex = j;
                executor.submit(() -> simulationThread(rowIndex, columnIndex));
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
        } catch (RuntimeException e) {
            System.err.println("Error in executor.awaitTermination");
        }

        SimulationIO.saveMatrix("timeMatrix", timeMatrix);
        SimulationIO.saveMatrix("stopCodeMatrix", stopCodeMatrix);
    }

    private void simulationThread(int rowIndex, int columnIndex) {
        // Perturb the bodies
        Body[] perturbedBodies = bodies;
        if (options.get("perturbPositions")) {
            // Check if the Figure 8 can be initialised
            // if((originalEnergy + 5.0/(2.0 * Vector.add(bodies[0].getPosition(), new Vector(rowIndex * delta, columnIndex * delta, 0)).norm())) < 0){
            //     int gridSize = 2 * halfGridSize + 1;
            //     timeMatrix[rowIndex + halfGridSize][gridSize - (columnIndex + halfGridSize + 1)] = 0;
            //     stopCodeMatrix[rowIndex + halfGridSize][gridSize - (columnIndex + halfGridSize + 1)] = 'F';
            //     return;
            // }
            perturbedBodies = perturbPositions(rowIndex, columnIndex, delta);
            if (perturbedBodies == null) {
                timeMatrix[rowIndex + halfGridSize][gridSize - (columnIndex + halfGridSize + 1)] = 0;
                stopCodeMatrix[rowIndex + halfGridSize][gridSize - (columnIndex + halfGridSize + 1)] = 'F';
                return;
            }
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
            timeMatrix[rowIndex + halfGridSize][gridSize - (columnIndex + halfGridSize + 1)] = simulation.getElapsedTime();
            char stopCode = simulation.getStopCode();
            stopCodeMatrix[rowIndex + halfGridSize][gridSize - (columnIndex + halfGridSize + 1)] = stopCode;
            if (columnIndex == 0){
                String ThreadName = "Thread " + simulationThread.getName();
                System.out.println(String.format("%-" + 25 + "s", ThreadName) + stopCode);
            }
        }

    }

}
