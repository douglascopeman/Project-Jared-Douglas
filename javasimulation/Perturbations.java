package javasimulation;
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
    private int energyIndex = 0;
    private int halfGridSizeEnergy = 1;
    private int perturbSingularSubdivide = 3; //must be odd!

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

    public Body[] perturbEnergy(int k, double energyDelta, int i, int j, double delta){
        Body[] perturbedBodies = Calculations.copyBodies(bodies);
        
        // Perturbs body 0 and adjust body 2 accordingly
        Vector perturbedPosition = Vector.add(bodies[0].getPosition(), new Vector(i * delta, j * delta, 0));

        perturbedBodies[0].setPosition(perturbedPosition);
        perturbedBodies[2].setPosition(perturbedPosition.negate());

        // Find the new magnitude of velocity required to preserve the energy
        // First check that the root of the energy term is real
        double energyTerm = (1+(k*energyDelta))*(originalEnergy) + 5.0/(2.0 * perturbedBodies[0].getPosition().norm());
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
        
        // Perturbs body 0 and adjust body 2 accordingly
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
        } else if (options.get("perturbVelocities")) {
            perturbedBodies = perturbVelocities(rowIndex, columnIndex, delta);
        } else if (options.get("perturbAngularMomentum")) {
            perturbedBodies = perturbAngularMomentum(this.angularMomentumIndex, angularMomentumDelta, rowIndex, columnIndex, delta);
        } else if (options.get("perturbEnergy")) {
            perturbedBodies = perturbEnergy(this.energyIndex, energyDelta, rowIndex, columnIndex, delta);
        }

        // If the perturbation results in a negative sqrt, set the results to a failed state
        if (perturbedBodies == null) {
            timeMatrix[saveRowIndex][saveColumnIndex] = 0;
            stopCodeMatrix[saveRowIndex][saveColumnIndex] = 'F';
            stabilityMatrix[saveRowIndex][saveColumnIndex] = (int) Math.pow(Simulation.getShapeSpaceSize(), 2);
            return;
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

    public void runEnergy(){

        // Save the perturbation settings
        SimulationIO.write3dPerturbationSettingsToFile(N, delta, energyDelta, halfGridSize, halfGridSizeEnergy);

        // The loop for the parent perturbation
        for(int k = -halfGridSizeEnergy; k <= halfGridSizeEnergy; k++){
            ExecutorService executor = Executors.newFixedThreadPool(10);

            this.energyIndex = k;
            
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
            SimulationIO.saveMatrix("timeMatrix"+(k*energyDelta), timeMatrix);
            SimulationIO.saveMatrix("stopCodeMatrix"+(k*energyDelta), stopCodeMatrix);
            if (options.get("calculateShapeSpace")) {
                SimulationIO.saveMatrix("stabilityMatrix"+(k*energyDelta), stabilityMatrix);
            }
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
        final int p = this.perturbSingularSubdivide; // This is the grid size of this more granular perturbation plot

        // The variables which will save the co ordinates of the best simulation
        int optimalX = 0;
        int optimalY = 0;
        int optimalOrbitLength = 1;
        int optimalTotalTimesteps = 1;

        //find the new simulation conditions
        int newHalfGridSize = (p-1)/2;
        int iShiftStart = (i*p)-newHalfGridSize;
        int iShiftEnd = (i*p)+newHalfGridSize;
        int jShiftStart = (j*p)-newHalfGridSize;
        int jShiftEnd = (j*p)+newHalfGridSize;
        double newDelta = currentDelta / p;

        int maxThreads = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);
        // instantiate matrices to keep track of the stability numbers and the start/stop of threads (printed out for progress visualisation)
        int[][] stabilityMatrix = new int[p][p];
        char[][] startStopMatrix = new char[p][p];
        int[][] totalTimestepMatrix = new int[p][p];
        int[][] orbitLengthMatrix = new int[p][p];
        //set default 'waiting' state for all threads
        Arrays.stream(startStopMatrix).forEach(row -> Arrays.fill(row, '-'));

        options.put("findOrbitLength", true);
        
        // Performing mini perturbation of singular pixel using threads
        for (int k = iShiftStart; k <= iShiftEnd; k++) {
            for (int l = jShiftStart; l <= jShiftEnd; l++) {
                final int rowIndex = k;
                final int columnIndex = l;
                // submit a job to the executor with the perturbation of the simulation
                executor.submit(() -> {
                    Body[] perturbedBodies = perturbPositions(rowIndex, columnIndex, newDelta);
                    Simulation simulation = new Simulation(perturbedBodies, N, dt, options);
                    simulation.setIntegratorFunction(simulationIntegrator);
                    
                    Thread simThread = new Thread(simulation);
                    try {
                        // set the current thread running and wait for it to finish
                        startStopMatrix[rowIndex - iShiftStart][columnIndex - jShiftStart] = '#';
                        simThread.start();
                        simThread.join();
                    } catch (Exception e) {
                    } finally{
                        // set the thread to stopped
                        int firstIndex = rowIndex - iShiftStart;
                        int secondIndex = columnIndex - jShiftStart;

                        startStopMatrix[firstIndex][secondIndex] = '*';
                        totalTimestepMatrix[firstIndex][secondIndex] = simulation.getCurrentTimestep();
                        orbitLengthMatrix[firstIndex][secondIndex] = simulation.getOrbitLength();
                    }
                    // save the stability number to the stability matrix
                    int localStabilityNumber = simulation.getShapeSpaceStabilityNumber();
                    synchronized (stabilityMatrix) {
                    stabilityMatrix[rowIndex - iShiftStart][columnIndex - jShiftStart] = localStabilityNumber;
                    }
                });
            }
        }
        threadMatrixPrintout(startStopMatrix, p);

        // shutdown the executor and wait for all threads to finish
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            System.err.println("Error in executor.awaitTermination");
        }

        // Print the stability matrix
        System.out.println("Stability Matrix:");
        for (int m = 0; m < p; m++) {
            for (int n = 0; n < p; n++) {
                System.out.print(stabilityMatrix[m][n] + " ");
            }
            System.out.println();
        }


        // find the optimal simulation
        int minStabilityNumber = Integer.MAX_VALUE;
        int minRowIndex = 0;
        int minColumnIndex = 0;

        for (int m = 0; m < p; m++) {
            for (int n = 0; n < p; n++) {
                if (stabilityMatrix[m][n] < minStabilityNumber && stabilityMatrix[m][n] > 0) {
                    minStabilityNumber = stabilityMatrix[m][n];
                    minRowIndex = m;
                    minColumnIndex = n;
                }
            }
        }

        // from the minimum stability number, find the optimal simulation
        optimalX = iShiftStart + minRowIndex;
        optimalY = jShiftStart + minColumnIndex;
        double newDeltaScale = Math.ceil(Math.abs(Math.log10(newDelta)));

        // print the optimal perturbation
        System.out.println("Optimal delta X: " + String.format("%." + ((int)newDeltaScale + 1) + "f", optimalX * newDelta) + " Optimal delta Y: " + String.format("%." + ((int)newDeltaScale + 1 ) + "f", optimalY * newDelta));

        // save the optimal simulation conditions for the final display simulation
        optimalOrbitLength = orbitLengthMatrix[minRowIndex][minColumnIndex];
        optimalTotalTimesteps = totalTimestepMatrix[minRowIndex][minColumnIndex];

        Body[] perturbedBodies = perturbPositions(optimalX, optimalY, newDelta);

        options.replace("perturbPositions", false);

        // If an orbit is found, run the simulation for the optimal orbit length,
        // otherwise run for a 10th of the total steps
        if (optimalOrbitLength != 1) {
            System.out.println("An orbit is " + optimalOrbitLength + " steps");
            System.out.println("Total number of orbits is " + (optimalTotalTimesteps / (double) optimalOrbitLength) );

            Simulation simulation = new Simulation(perturbedBodies, optimalOrbitLength, dt, options);
            simulation.setIntegratorFunction(simulationIntegrator);
            simulation.run();            
        } else {
            double fractionalRunDenom = 100.0;    // The fraction of the orbit that the location data will be saved
            boolean isSaveEndFraction = true;   // When true the end fraction of the simulation is displayed not the start fraction, this is particularly useful for terminated simulations

            System.out.println("No orbit found, running for 1/" + (int) fractionalRunDenom + " of total steps (Total steps = " + optimalTotalTimesteps + ")");

            int fractionalRunTimesteps = optimalTotalTimesteps / (int) fractionalRunDenom;

            // See the last fraction of the orbit instead of the first fraction
            if (fractionalRunDenom != 1.0 && isSaveEndFraction){
                Simulation simulationFirstFraction = new Simulation(perturbedBodies, optimalTotalTimesteps - fractionalRunTimesteps-1, dt, options);
                simulationFirstFraction.setMemorylessRun();
                simulationFirstFraction.setIntegratorFunction(simulationIntegrator);
                simulationFirstFraction.run();
            }
            Simulation simulation = new Simulation(perturbedBodies, fractionalRunTimesteps, dt, options);
            simulation.setIntegratorFunction(simulationIntegrator);
            simulation.run();   
        }
    }

    private void threadMatrixPrintout(char[][] startStopMatrix, int p){
        // print out the start/stop matrix to show the progress of the threads
        // when all threads are marked as stopped, the simulation is complete
        System.out.println("Threads started (#) / stopped (*) / waiting (-): ");

        boolean allThreadsCompleted = false;
        while(!allThreadsCompleted) {
            allThreadsCompleted = true;
            for (int m = 0; m < p; m++) {
                for (int n = 0; n < p; n++) {
                    System.out.print(startStopMatrix[m][n] + " ");
                    if (startStopMatrix[m][n] != '*') {
                        allThreadsCompleted = false;
                    }
                }
                System.out.println();
            }
            // wait for a second before updating the progress
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            //magic string to move the cursor up p lines
            System.out.print("\033[" + p + "A");
        }
        // Move the cursor down p lines
        System.out.print("\033[" + p + "B");
        System.out.println("All threads completed");
    }

    public void perturbSingularThreadless(int i, int j, double currentDelta){
        //Ideally, this function would take in perturbation coordinates, subdivides the perturbation by a smaller delta that the original used. Then determines the best simulation stability number within the smaller grid and counts the orbits within the total long time as well as the number of timestep per obit. using the latter, we run another simulation with that length to produce the plot.

        // This method is used to increase the accuracy when a double click action is made on a plot, 
        // it breaks the pixel into a small perturbation plot
        final int p = 3; // This is the grid size of this perturbation plot

        options.put("findOrbitLength", true);

        double previousStabilityNumber = Integer.MAX_VALUE;

        // The variables which will save the co ordinates of the best simulation
        int optimalX = 0;
        int optimalY = 0;
        int optimalOrbitLength = 1;
        int optimalTotalTimesteps = 1;

        //find the new simulation conditions
        int newHalfGridSize = (p-1)/2;
        int iShiftStart = (i*p)-newHalfGridSize;
        int iShiftEnd = (i*p)+newHalfGridSize;
        int jShiftStart = (j*p)-newHalfGridSize;
        int jShiftEnd = (j*p)+newHalfGridSize;
        double newDelta = currentDelta / p;

        
        // Performing mini perturbation of singular pixel
        for (int k = iShiftStart; k <= iShiftEnd; k++){
            for (int l = jShiftStart; l <= jShiftEnd; l++){
                System.out.println(k + " , " + l);
                Body [] perturbedBodies = perturbPositions(k,l, newDelta);
                Simulation simulation = new Simulation(perturbedBodies, N, dt, options);
                simulation.setIntegratorFunction(simulationIntegrator);
                simulation.run();

                System.out.println(simulation.getStopCode());

                if (simulation.getShapeSpaceStabilityNumber() < previousStabilityNumber){
                    optimalX = k;
                    optimalY = l;
                    optimalOrbitLength = simulation.getOrbitLength();
                    optimalTotalTimesteps = simulation.getCurrentTimestep();
                    previousStabilityNumber = simulation.getShapeSpaceStabilityNumber();
                }
            }
        }

        double newDeltaScale = Math.ceil(Math.abs(Math.log10(newDelta)));

        System.out.println("Optimal delta X: " + String.format("%." + ((int)newDeltaScale + 1) + "f", optimalX * newDelta) + " Optimal delta Y: " + String.format("%." + ((int)newDeltaScale + 1 ) + "f", optimalY * newDelta));

        Body [] perturbedBodies = perturbPositions(optimalX,optimalY, newDelta);
        options.replace("perturbPositions", false);

        if (optimalOrbitLength != 1) {
            System.out.println("An orbit is " + optimalOrbitLength + " steps");
            System.out.println("Total number of orbits is " + (optimalOrbitLength / optimalTotalTimesteps) );

            Simulation simulation = new Simulation(perturbedBodies, optimalOrbitLength, dt, options);
            simulation.setIntegratorFunction(simulationIntegrator);
            simulation.run();            
        } else {
            System.out.println("No orbit found, running for a 10th of total steps (Total steps = " + optimalTotalTimesteps + ")");

            Simulation simulation = new Simulation(perturbedBodies, optimalTotalTimesteps/100, dt, options);
            simulation.setIntegratorFunction(simulationIntegrator);
            simulation.run();   
        }
    }
    
}


