package javacompare;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays; 



public class Perturbations {
    private Body[] bodies;
    private int N;
    private double dt;
    private int halfGridSize;
    private int gridSize;
    private double delta;
    private double energyDelta = 0.01;
    private double angularMomentumDelta = 0.01;
    private int halfGridSizeAngularMomentum = 0;
    private int angularMomentumIndex = 0;
    private int halfGridSizeEnergy = 1;

    private HashMap<String, Boolean> options = new HashMap<String, Boolean>();
    private IntegratorFunction simulationIntegrator;

    private double originalEnergy;
    private final Vector originalCentreOfMass;
    private final Vector originalAngularMomentum;

    private double[][] timeMatrix;
    private char[][] stopCodeMatrix;
    private int[][] stabilityMatrix;

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
        stabilityMatrix = new int[2 * halfGridSize + 1][2 * halfGridSize + 1];
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
    public void setHalfGridSizeEnergy(int halfGridSizeEnergy) {
        this.halfGridSizeEnergy = halfGridSizeEnergy;
    }

    public void setIntegratorFunction(IntegratorFunction integratorFunction) {
        this.simulationIntegrator = integratorFunction;
    }
    public void setEnergyDelta(double energyDelta) {
        this.energyDelta = energyDelta;
    }

    public void setAngularMomentumShift(int halfGridSizeAngularMomentum, double angularMomentumDelta) {
        this.angularMomentumDelta = angularMomentumDelta;
        this.halfGridSizeAngularMomentum = halfGridSizeAngularMomentum;
    }

    public Body[] perturbAngularMomentum(int k, double shiftAngularMomentumDelta, int i, int j, double delta){
        // Get the angle between the position vector of body 1 and the difference between the velocity vectors of bodies 1 and 3
        double theta = bodies[0].getPosition().angle(bodies[0].getVelocity());

        Body[] perturbedBodies = Calculations.copyBodies(bodies);
        
        // Perturb Body 1 and adjust Body 3 accordingly, Body 2 remains in position
        Vector perturbedPosition = Vector.add(bodies[0].getPosition(), new Vector(i * delta, j * delta, 0));
        perturbedBodies[0].setPosition(perturbedPosition);
        perturbedBodies[2].setPosition(perturbedPosition.negate());

        // Magnitude shift of Angular Momentum
        double L = k*shiftAngularMomentumDelta;

        // Find the magnitude of the new velocity for Body 1
        double temp1 = perturbedBodies[0].getPosition().norm();
        double temp2 = Math.sin(theta);
        double temp3 = (-3.0 * Math.pow(L,2.0)) + (30.0 * temp1 * Math.pow(temp2, 2.0)) + (12.0 * Math.pow(temp1, 2.0) * Math.pow(temp2, 2.0) * originalEnergy);
        if (temp3 < 0) {
            return null;
        }
        double velMagnitudeOne = ((3.0 * L) + Math.sqrt(temp3)) / (6.0 * temp1 * temp2);

        // Magnitude of the new velocity for Body 3
        double velMagnitudeThree = velMagnitudeOne - (L / (temp1 * temp2));

        // Update Velocities
        perturbedBodies[0].setVelocity(bodies[0].getVelocity().normalise().multiply(velMagnitudeOne));
        perturbedBodies[2].setVelocity(bodies[2].getVelocity().normalise().multiply(velMagnitudeThree));
        perturbedBodies[1].setVelocity(perturbedBodies[0].getVelocity().negate().subtract(perturbedBodies[2].getVelocity()));

        return perturbedBodies;
    }

    public void shiftEnergy(double shiftEnergy){
        //originalEnergy =(1+shiftEnergy)*originalEnergy;
        double newVelocityMagnitude = Math.sqrt((1.0/3.0) * ((1+shiftEnergy)*originalEnergy + 5.0/(2.0 * bodies[0].getPosition().norm())));
        // Finally, preserve the angular momentum by setting the velocity of body 1 to (-2) times that of bodies 0 and 2
        bodies[0].setVelocity(bodies[0].getVelocity().normalise().multiply(newVelocityMagnitude));
        bodies[2].setVelocity(bodies[0].getVelocity());
        bodies[1].setVelocity(bodies[0].getVelocity().multiply(-2.0));
    }

    public Body[] perturbPositions(int i, int j, double delta) {
        Body[] perturbedBodies = Calculations.copyBodies(bodies);
        
        // Perturb body 0 and adjust body 2 accordingly
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

    private Body[] perturbVelocities(int i, int j, double delta) {
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
        ExecutorService executor = Executors.newFixedThreadPool(10);

        // Save the perturbation settings
        SimulationIO.writePerturbationSettingsToFile(N, delta, halfGridSize);

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
            System.err.println("Error in executor.awaitTermination");
        }

        SimulationIO.saveMatrix("timeMatrix", timeMatrix);
        SimulationIO.saveMatrix("stopCodeMatrix", stopCodeMatrix);
        if (options.get("calculateShapeSpace")) {
            SimulationIO.saveMatrix("stabilityMatrix", stabilityMatrix);
        }
    }

    private void simulationThread(int rowIndex, int columnIndex) {
        // matrix coordinates for convenience
        int saveRowIndex = rowIndex + halfGridSize;
        int saveColumnIndex = gridSize - (columnIndex + halfGridSize + 1);

        // Perturb the bodies
        Body[] perturbedBodies = bodies;
        if (options.get("perturbPositions")) {
            perturbedBodies = perturbPositions(rowIndex, columnIndex, delta);
            // If the perturbation results in a negative sqrt, set the results to a failed state
            if (perturbedBodies == null) {
                timeMatrix[saveRowIndex][saveColumnIndex] = 0;
                stopCodeMatrix[saveRowIndex][saveColumnIndex] = 'F';
                stabilityMatrix[saveRowIndex][saveColumnIndex] = (int) Math.pow(Simulation.getShapeSpaceSize(), 2);
                return;
            }
        } else if (options.get("perturbVelocities")) {
            perturbedBodies = perturbVelocities(rowIndex, columnIndex, delta);
        } else if (options.get("perturbAngularMomentum")) {
            perturbedBodies = perturbAngularMomentum(this.angularMomentumIndex, angularMomentumDelta, rowIndex, columnIndex, delta);
            if (perturbedBodies == null) {
                timeMatrix[saveRowIndex][saveColumnIndex] = 0;
                stopCodeMatrix[saveRowIndex][saveColumnIndex] = 'F';
                stabilityMatrix[saveRowIndex][saveColumnIndex] = (int) Math.pow(Simulation.getShapeSpaceSize(), 2);
                return;
            }
        }

        
        // #region Sanity checks to be removed later
        double perturbedEnergy = Calculations.totalEnergy(perturbedBodies, 1);
        Vector perturbedCentreOfMass = Calculations.centreOfMass(perturbedBodies);
        Vector perturbedAngularMomentum = Calculations.angularMomentum(perturbedBodies);

        assert Math.abs(perturbedEnergy - originalEnergy) < 1e-10 : "Energy assertion failed";
        assert perturbedCentreOfMass.subtract(originalCentreOfMass).norm() < 1e-10 : "CoM assertion failed";
        assert perturbedAngularMomentum.subtract(originalAngularMomentum).norm() < 1e-10 : "Angular Momentum assertion failed";
        // #endregion

        
        Simulation simulation = new Simulation(perturbedBodies, N, dt, options);
        simulation.setIntegratorFunction(simulationIntegrator);
        Thread simulationThread = new Thread(simulation);
        try {
            simulationThread.start();
            simulationThread.join();
        } catch (Exception e) {
        } finally {
            // save the elapsed time to the time matrix
            double elapsedTime = simulation.getElapsedTime();
            timeMatrix[saveRowIndex][saveColumnIndex] = elapsedTime;

            // save the stop code to the stop code matrix
            char stopCode = simulation.getStopCode();
            stopCodeMatrix[saveRowIndex][saveColumnIndex] = stopCode;
            // save the stability number to the stability matrix if the simulation completed successfully
            if (options.get("calculateShapeSpace") && stopCode == 'X') {
                int stabilityNumber = simulation.getShapeSpaceStabilityNumber();
                stabilityMatrix[saveRowIndex][saveColumnIndex] = stabilityNumber;
            } else {
                stabilityMatrix[saveRowIndex][saveColumnIndex] = 0;
            }

            //printing the thread name and stop code every row
            if (columnIndex == 0){
                System.out.print("\rRow " + rowIndex + " in [-" + halfGridSize + "," + halfGridSize + "]          ");
            }
        }
    }

    public void runEnergyLayers(){

        // Save the perturbation settings
        SimulationIO.write3dPerturbationSettingsToFile(N, delta, energyDelta, halfGridSize, halfGridSizeEnergy);

        // Keeping the original bodies and energy to revert back to at the start of each child perturbation
        double energyCopy = originalEnergy;
        Body[] originalBodies = Calculations.copyBodies(bodies);

        // The loop for the parent perturbation
        for(int k = -halfGridSizeEnergy; k <= halfGridSizeEnergy; k++){
            ExecutorService executor = Executors.newFixedThreadPool(10);

            // Ensuring the bodies and energy is set back to the original before proceeding
            System.out.println(k);
            this.bodies = Calculations.copyBodies(originalBodies);
            this.originalEnergy = energyCopy;
            
            // We return matrices filled with "F" for failed to initialize for choices of energy that will result in negative sqrt
            if((1+(k*energyDelta))*originalEnergy + 5.0/(2.0 * bodies[0].getPosition().norm()) < 0){
                Arrays.fill(timeMatrix, 0);
                Arrays.fill(stopCodeMatrix, "F");
                return;
            } else{
                // Performing the energy shift
                shiftEnergy(k*energyDelta);
            }
            
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
                System.err.println("Error in executor.awaitTermination");
            }

            // Saving the child perturbation csv's with the relevant energy stamp
            SimulationIO.saveMatrix("timeMatrix"+k, timeMatrix);
            SimulationIO.saveMatrix("stopCodeMatrix"+k, stopCodeMatrix);
        }
    }

    public void runAngularMomentum(){
        // Save the perturbation settings
        SimulationIO.write3dPerturbationSettingsToFile(N, delta, angularMomentumDelta, halfGridSize, halfGridSizeAngularMomentum);

        for(int k = 0; k <= halfGridSizeAngularMomentum; k++){
            ExecutorService executor = Executors.newFixedThreadPool(10);

            this.angularMomentumIndex = k;

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
                System.err.println("Error in executor.awaitTermination");
            }

            // Saving the child perturbation csv's with the relevant energy stamp
            SimulationIO.saveMatrix("timeMatrix"+ (k*angularMomentumDelta), timeMatrix);
            SimulationIO.saveMatrix("stopCodeMatrix"+(k*angularMomentumDelta), stopCodeMatrix);
            if (options.get("calculateShapeSpace")) {
                SimulationIO.saveMatrix("stabilityMatrix"+(k*angularMomentumDelta), stabilityMatrix);
            }
            
        }
        }

    public void perturbSingular(int i, int j, double currentDelta){
        // This method is used to increase the accuracy when a double click action is made on a plot, 
        // it breaks the pixel into a small perturbation plot
        int p = 4; // This is the grid size of this perturbation plot
        double previousStabilityNumber = 0;
        // Simulation bestSimulation = new Simulation(bodies, N, dt);
        
        for (int k = (i*p)-(p/2); k <= (i*p)+(p/2); k++){
            for (int l = (j*p)-(p/2); l <= (j*p)+(p/2); l++){
                System.out.println(k);
                System.out.println(l);
                Body [] perturbedBodies = perturbPositions(k,l, currentDelta / p);
                Simulation simulation = new Simulation(perturbedBodies, N, dt, options);
                simulation.run();
                if (simulation.getShapeSpaceStabilityNumber() > previousStabilityNumber){
                    simulation.writeSimulationToFiles();     
                }
            }
        }
        

    }
    
}


