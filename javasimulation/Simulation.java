package javasimulation;
import java.util.HashMap;

public class Simulation {

    private final Body[] bodies;
    private final int n;
    private final int N;
    private final double dt;
    private final double G;
    private double elapsedTime = 0;
    private IntegratorFunction integratorFunction;
    private double[][][] simulation;

    private HashMap<String, Boolean> options = new HashMap<String, Boolean>();

    private double energyErrorBound = 1e-2;
    private double distanceBound = 20;
    private double timestepSizeBound = 1e-6;

    private Vector[] centreOfMass;
    private double[] potentialEnergy;
    private double[] kineticEnergy;
    private Vector[] angularMomentum;
    private Vector[] linearMomentum;
    private double orbitLength;
    private int currentTimestep;

    public Simulation(Body[] bodies, int N, double dt) {
        this.bodies = bodies;
        this.n = bodies.length;
        this.N = N;
        this.dt = dt;
        this.G = 1;
        
        options.put("useVariableTimestep", false);
        options.put("checkStopConditions", false);
        options.put("calculateCentreOfMass", false);
        options.put("calculateEnergies", false);
        options.put("calculateAngularMomentum", false);
        options.put("calculateLinearMomentum", false);
        options.put("findOrbitLength", false);

        centreOfMass = new Vector[N];
        potentialEnergy = new double[N];
        kineticEnergy = new double[N];
        angularMomentum = new Vector[N];
        linearMomentum = new Vector[N];

        this.integratorFunction = Integrators::symplecticEuler;
    }

    public Simulation(Body[] bodies, int N, double dt, String[] customOptions)
    {
        this(bodies, N, dt);

        setCommandlineOptions(customOptions);
    }


    // #region Getters and Setters

    private void setCommandlineOptions(String[] options) {
        for (String option: options) {
            option = option.substring(1);
            if (this.options.containsKey(option)) {
                this.options.put(option, true);
            } else if (option.substring(0, 10).equals("integrator")) {
                this.integratorFunction = Integrators.integratorMap.get(option.substring(11));
            } else {
                System.out.println(option.substring(12));
                throw new IllegalArgumentException("Invalid option: " + option);
            }
        }

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
        long startTime = System.currentTimeMillis();

        simulation = new double[N][6][n];

        // ----- Main Time Loop ----- \\
        for (int i = 0; i < N; i++) {
            this.currentTimestep = i;

            // Record all optional calculations
            doOptionalCalculations(i);

            // Record the current timestep of the simulation
            for (int p = 0; p < n; p++) {
                Vector position = bodies[p].getPosition();
                Vector velocity = bodies[p].getVelocity();
                simulation[i][0][p] = position.getX();
                simulation[i][1][p] = position.getY();
                simulation[i][2][p] = position.getZ();
                simulation[i][3][p] = velocity.getX();
                simulation[i][4][p] = velocity.getY();
                simulation[i][5][p] = velocity.getZ();
            }

            // Then update the states of all bodies
            boolean useVariableTimestep = options.get("useVariableTimestep");
            double usedTimestepLength = integratorFunction.Integrate(bodies, dt, useVariableTimestep);
            elapsedTime += usedTimestepLength;

            // Check if the simulation should stop
            if (options.get("checkStopConditions")) {
                checkStopConditions(i, usedTimestepLength, elapsedTime);
            }

            if (options.get("findOrbitLength") && (i > 10) && (orbitLength != 0.0)) {
                findOrbitLength();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Time taken: " + (endTime - startTime) + "ms");

        writeSimulationToFiles();
    }

    public void writeSimulationToFiles() {

        // Create the output directory if it doesn't exist, or clear it if it does
        OutputWriter.setupDirectories();

        // Start by writing the simulation settings to a file
        OutputWriter.writeSettingsToFile(N, dt, n, G, options.get("findOrbitLength"), orbitLength);

        // Then write each body to its own file
        OutputWriter.writeBodiesToFiles(simulation, N, n);

        // Finally, write all optional calculations to files
        OutputWriter.writeOptionsToFiles(options, centreOfMass, potentialEnergy, kineticEnergy, angularMomentum, linearMomentum);

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

    private void checkStopConditions(int timestep, double usedTimestepLength, double elapsedTime) {
        if (options.get("calculateEnergies")) {
            // Check if the energy error is within the bound
            double energyDiff = kineticEnergy[timestep] - kineticEnergy[0] + potentialEnergy[timestep] - potentialEnergy[0];
            double energyError = Math.abs(energyDiff / (kineticEnergy[0] + potentialEnergy[0]));
            if (energyError > energyErrorBound) {
                System.out.println("Simulation terminated after exceeding energy error bound");
                System.out.println("Energy error bound: \t" + energyErrorBound);
                System.out.println("Energy error: \t" + energyError);
                System.out.println("Time reached: \t" + elapsedTime);
                throw new RuntimeException("Energy error bound exceeded");
            }
        }

        if (options.get("calculateCentreOfMass")) {
            // Check if the distance between the centre of mass and the origin is within the bound
            double distance = centreOfMass[timestep].norm();
            if (distance > distanceBound) {
                System.out.println("Simulation terminated after exceeding distance bound");
                System.out.println("Distance bound: \t" + distanceBound);
                System.out.println("Distance: \t" + distance);
                System.out.println("Time reached: \t" + elapsedTime);
                throw new RuntimeException("Distance bound exceeded");
            }
        }

        // Check if the timestep size is within the bound
        if (options.get("useVariableTimestep")) {
            if (usedTimestepLength < timestepSizeBound) {
                System.out.println("Simulation terminated after exceeding timestep size bound");
                System.out.println("Timestep size bound: \t" + timestepSizeBound);
                System.out.println("Timestep size: \t" + usedTimestepLength);
                System.out.println("Time reached: \t" + elapsedTime);
                throw new RuntimeException("Timestep size bound exceeded");
            }
        }
    } 

}