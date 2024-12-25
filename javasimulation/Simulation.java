package javasimulation;
import java.util.HashMap;
import java.util.List;

public class Simulation implements Runnable {

    private final Body[] bodies;
    private final int n;
    private final int N;
    private final double dt;
    private final double G;
    private double elapsedTime = 0;
    private IntegratorFunction integratorFunction;
    private double[][][] simulation;
    private static final int shapeSpaceSize = 1000;
    private int[][] shapeSpace = new int[shapeSpaceSize][shapeSpaceSize];

    private HashMap<String, Boolean> options = new HashMap<String, Boolean>();

    private double energyErrorBound = 1e-3;
    private double distanceBound = 15;
    private double timestepSizeBound = 1e-5;
    private double MaxNumberOfTimesteps = 1e8;

    private Vector[] centreOfMass;
    private double[] potentialEnergy;
    private double[] kineticEnergy;
    private Vector[] angularMomentum;
    private Vector[] linearMomentum;
    private double orbitLength;
    private int currentTimestep = 0;
    private char stopCode = 'X';
    private Vector initialCentreOfMass;
    private double initialEnergy;

    private boolean isPerturbed = false;

    public Simulation(Body[] bodies, int N, double dt) {
        this.bodies = bodies;
        this.n = bodies.length;
        this.N = N;
        this.dt = dt;
        this.G = 1;

        // Setting initial system properties
        this.initialCentreOfMass = Calculations.centreOfMass(bodies);
        this.initialEnergy = Calculations.totalEnergy(bodies, 1);

        
        SimulationIO.setDefaultSimulationOptions(options);

        centreOfMass = new Vector[N];
        potentialEnergy = new double[N];
        kineticEnergy = new double[N];
        angularMomentum = new Vector[N];
        linearMomentum = new Vector[N];

        this.integratorFunction = Integrators::symplecticEuler;
    }

    public Simulation(Body[] bodies, int N, double dt, List<String> customOptions) {
        this(bodies, N, dt);
        SimulationIO.setSimulationOptions(this, customOptions);
    }

    public Simulation(Body[] bodies, int N, double dt, HashMap<String, Boolean> customOptions) {
        this(bodies, N, dt);
        this.options.putAll(customOptions);
        isPerturbed = options.get("perturbPositions") || options.get("perturbVelocities");
    }

    // #region Getters and Setters

    public int getShapeSpaceStabilityNumber() {
        int stabilityNumber = 0;
        for (int[] row : shapeSpace) {
            for (int value : row) {
                if (value >= 1) {
                    stabilityNumber += 1;
                }
            }
        }
        return stabilityNumber;
    }

    public static int getShapeSpaceSize() {
        return shapeSpaceSize;
    }

    public char getStopCode() {
        return stopCode;
    }

    public int getCurrentTimestep() {
        return currentTimestep;
    }

    public HashMap<String, Boolean> getOptions() {
        return options;
    }

    public void setOptions(HashMap<String, Boolean> options) {
        options.putAll(options);
    }

    public void setEnergyErrorBound(double energyErrorBound) {
        this.energyErrorBound = energyErrorBound;
    }

    public void setDistanceBound(double distanceBound) {
        this.distanceBound = distanceBound;
    }

    public void setTimestepSizeBound(double timestepSizeBound) {
        this.timestepSizeBound = timestepSizeBound;
    }

    public double getElapsedTime() {
        return elapsedTime;
    }

    public void setIntegratorFunction(IntegratorFunction integratorFunction) {
        this.integratorFunction = integratorFunction;
    }

    // #endregion

    private void doOptionalCalculations(int timestep) {
        if (options.get("calculateCentreOfMass")) {
            centreOfMass[timestep] = Calculations.centreOfMass(bodies);
        }
        if (options.get("calculateEnergies")) {
            potentialEnergy[timestep] = Calculations.potentialEnergy(bodies, G);
            kineticEnergy[timestep] = Calculations.kineticEnergy(bodies);
        }
        if (options.get("calculateAngularMomentum")) {
            angularMomentum[timestep] = Calculations.angularMomentum(bodies);
        }
        if (options.get("calculateLinearMomentum")) {
            linearMomentum[timestep] = Calculations.linearMomentum(bodies);
        }
    }

    public void run(){

        if (!this.isPerturbed){
            simulation = new double[N][6][n];
        }


        // ----- Main Time Loop ----- \\
        while (true) {

            // If we are not running memoryless for pertubation 
            if (!this.isPerturbed){
                
                // Record all optional calculations
                doOptionalCalculations(this.currentTimestep);

                // Record the current timestep of the simulation
                for (int p = 0; p < n; p++) {
                    Vector position = bodies[p].getPosition();
                    Vector velocity = bodies[p].getVelocity();
                    simulation[this.currentTimestep][0][p] = position.getX();
                    simulation[this.currentTimestep][1][p] = position.getY();
                    simulation[this.currentTimestep][2][p] = position.getZ();
                    simulation[this.currentTimestep][3][p] = velocity.getX();
                    simulation[this.currentTimestep][4][p] = velocity.getY();
                    simulation[this.currentTimestep][5][p] = velocity.getZ();
                }
            }

            if (options.get("calculateShapeSpace")) {
                int[] shapeSpaceCoords = getShapeSpace(bodies);
                shapeSpace[shapeSpaceCoords[0]][shapeSpaceCoords[1]] = 1;
            }


            // Then update the states of all bodies
            boolean useVariableTimestep = options.get("useVariableTimestep");
            double usedTimestepLength = integratorFunction.Integrate(bodies, dt, useVariableTimestep);
            elapsedTime += usedTimestepLength;

            double currentEnergy = Calculations.totalEnergy(bodies, 1);

            // Check if the simulation should stop
            if (options.get("checkStopConditions") && checkStopConditions(currentEnergy, usedTimestepLength, elapsedTime)) {
                break;
            }

            if (options.get("findOrbitLength") && (this.currentTimestep > 10) && (orbitLength != 0.0)) {
                findOrbitLength();
            }

            // Increase step count
            this.currentTimestep++;

            // Check if at the end of the simulaion
            if((this.isPerturbed && elapsedTime >= this.N)){
                break;
            } else if(!this.isPerturbed && this.currentTimestep >= this.N) {
                break;
            } else if (this.currentTimestep > this.MaxNumberOfTimesteps) {
                stopCode = 'L';
                break;
            }

        }

        if (!options.get("skipSaveToCSV") || !this.isPerturbed)  {
            writeSimulationToFiles();       
        }
    }

    public void writeSimulationToFiles() {

        // Create the output directory if it doesn't exist, or clear it if it does
        SimulationIO.setupDirectories();

        // Start by writing the simulation settings to a file
        SimulationIO.writeSettingsToFile(N, dt, n, G, options.get("findOrbitLength"), orbitLength);

        // Then write each body to its own file
        SimulationIO.writeBodiesToFiles(simulation, N, n);

        // Finally, write all optional calculations to files
        SimulationIO.writeOptionsToFiles(options, centreOfMass, potentialEnergy, kineticEnergy, angularMomentum, linearMomentum);

        if (options.get("calculateShapeSpace")) {SimulationIO.saveMatrix("shapeSpaceMatrix", shapeSpace); }

    }

    private void findOrbitLength() {
        boolean hasMadeFullOrbit = true;
        for (int p = 0; p < n; p++) {
            Vector diff = bodies[p].getPosition().subtract(bodies[p].getInitialPosition());
            double distance = diff.norm();
            if (distance > 0.1) {
                hasMadeFullOrbit = false;
                break;
            }
        }
        if (hasMadeFullOrbit) {
            orbitLength = elapsedTime;
        }
    }

    private double findMaxDistance() {
        double maxDistance = 0;
        for (Body body : bodies) {
            for (Body otherBody : bodies) {
                double distance = body.getPosition().subtract(otherBody.getPosition()).norm();
                if (distance > maxDistance) {
                    maxDistance = distance;
                }
            }
        }
        return maxDistance;
    }

    private boolean checkStopConditions(double currentEnergy, double usedTimestepLength, double elapsedTime) {
        if (options.get("calculateEnergies")) {
            // Check if the energy error is within the bound
            double energyDiff = currentEnergy - this.initialEnergy;
            double energyError = Math.abs(energyDiff / (this.initialEnergy));
            if (energyError > energyErrorBound) {
                // System.out.println("Simulation terminated after exceeding energy error bound");
                // System.out.println("Energy error bound: \t" + energyErrorBound);
                // System.out.println("Energy error: \t" + energyError);
                // System.out.println("Time reached: \t" + elapsedTime);
                // System.out.println("Timestep reached: \t" + timestep);
                // throw new RuntimeException("Energy error bound exceeded");
                stopCode = 'E';
                return true;
            }
        }

        if (options.get("calculateCentreOfMass")) {
            // Check if the distance between the centre of mass and the origin is within the bound
            double distance = this.initialCentreOfMass.norm();
            if (distance > distanceBound) {
                // System.out.println("Simulation terminated after exceeding distance bound");
                // System.out.println("Distance bound: \t" + distanceBound);
                // System.out.println("Distance: \t" + distance);
                // System.out.println("Time reached: \t" + elapsedTime);
                // throw new RuntimeException("Distance bound exceeded");
                stopCode = 'C';
                return true;
            }
        }

        // Check if the maximum distance between any two bodies is within the bound
        double maxDistance = findMaxDistance();
        if (maxDistance > distanceBound) {
            // System.out.println("Simulation terminated after exceeding distance bound");
            // System.out.println("Distance bound: \t" + distanceBound);
            // System.out.println("Distance: \t" + maxDistance);
            // System.out.println("Time reached: \t" + elapsedTime);
            // throw new RuntimeException("Distance bound exceeded");
            stopCode = 'D';
            return true;
        }


        // Check if the timestep size is within the bound
        if (options.get("useVariableTimestep")) {
            if (usedTimestepLength < timestepSizeBound) {
                // System.out.println("Simulation terminated after exceeding timestep size bound");
                // System.out.println("Timestep size bound: \t" + timestepSizeBound);
                // System.out.println("Timestep size: \t" + usedTimestepLength);
                // System.out.println("Time reached: \t" + elapsedTime);
                // throw new RuntimeException("Timestep size bound exceeded");
                stopCode = 'V';
                return true;
            }
        }

        return false;
    } 

    // The following method takes a body and returns an integer array containing the two shape space coordinates
    private int[] getShapeSpace(Body[] bodies) {
        double r_12 = bodies[0].getPosition().subtract(bodies[1].getPosition()).norm();
        double r_13 = bodies[0].getPosition().subtract(bodies[2].getPosition()).norm();
        double r_23 = bodies[1].getPosition().subtract(bodies[2].getPosition()).norm();
        
        double q_1 = r_12 / (r_12 + r_13 + r_23);
        double q_2 = r_23 / (r_12 + r_13 + r_23);

        int x = (int) Math.round(q_1 * shapeSpace.length);
        int y = (int) Math.round(q_2 * shapeSpace[0].length);
        return new int[] {x, y};
    }

}